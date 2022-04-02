package ch.eugster.filemaker.fsl.plugin.camt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.filemaker.jdbc.Driver;

import ch.eugster.filemaker.fsl.plugin.Executor;
import ch.eugster.filemaker.fsl.plugin.camt.CamtParameter.CamtDatabase;
import ch.eugster.filemaker.fsl.plugin.camt.CamtParameter.CamtMain;
import ch.eugster.filemaker.fsl.plugin.camt.CamtParameter.CamtReadXml;
import ch.eugster.filemaker.fsl.plugin.camt.CamtParameter.CamtWriteJson;
import ch.eugster.filemaker.fsl.plugin.swissqrbill.QRBillParameter;

public class CamtXmlToJsonConverter implements Executor
{
	public String execute(ObjectNode source)
	{
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode target = mapper.createObjectNode();
		if (CamtParameter.checkAll(source, target))
		{
			Connection connection = null;
			try
			{
				connection = this.createConnection(target);
				String xml = readXml(connection, target);
				String json = convertXmlToJson(target, xml);
				saveJson(connection, target, json);
			}
			finally
			{
				this.closeConnection(connection);
			}
		}
		return target.toPrettyString();
	}

	private void closeConnection(Connection connection)
	{
		try
		{
			if (connection != null)
			{
				connection.close();
			}
		}
		catch (SQLException e)
		{
		}
	}

	private Connection createConnection(ObjectNode target)
	{
		JsonNode db = target.get(CamtParameter.CamtMain.DATABASE.key());
		String url = db.get(CamtParameter.CamtDatabase.URL.key()).asText();
		String username = db.get(CamtParameter.CamtDatabase.USERNAME.key()).asText();
		String password = Objects.isNull(db.get(CamtParameter.CamtDatabase.PASSWORD.key())) ? ""
				: db.get(CamtParameter.CamtDatabase.PASSWORD.key()).asText();

		Driver driver = new Driver();
		Connection connection = null;
		try
		{
			DriverManager.registerDriver(driver);
			connection = DriverManager.getConnection(url, username, password);
		}
		catch (SQLException e)
		{
			QRBillParameter.addErrorNode(target, e.getLocalizedMessage());
		}
		return connection;
	}

	private String readXml(Connection connection, ObjectNode params)
	{
		ByteArrayOutputStream baos = null;
		JsonNode db = params.get(CamtMain.DATABASE.key());
		JsonNode readXml = db.get(CamtDatabase.READ_XML.key());
		String table = readXml.get(CamtReadXml.TABLE.key()).asText();
		String column = readXml.get(CamtReadXml.COLUMN.key()).asText();
		String whereCol = readXml.get(CamtReadXml.WHERE_COL.key()).asText();
		String whereVal = readXml.get(CamtReadXml.WHERE_VAL.key()).asText();
		try
		{
			String sql = "SELECT GetAs(" + column + ", 'FILE') AS " + column + " FROM " + table + " WHERE " + whereCol
					+ " = ?";
			PreparedStatement pstm = connection.prepareStatement(sql);
			pstm.setString(1, whereVal);
			ResultSet rst = pstm.executeQuery();
			while (rst.next())
			{
				Blob blob = rst.getBlob(column);
				InputStream is = blob.getBinaryStream();
				baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int length = is.read(buffer);
				while (length > -1)
				{
					baos.write(buffer, 0, length);
					length = is.read(buffer);
				}
			}
		}
		catch (Exception e)
		{
			CamtParameter.addErrorNode(params, e.getLocalizedMessage());
		}
		return Objects.isNull(baos) ? null : new String(baos.toByteArray());
	}

	private boolean saveJson(Connection connection, ObjectNode params, String jsonValue)
	{
		int result = 0;
		PreparedStatement pstm = null;
		String filename = params.get(CamtMain.FILENAME.key()).asText() + ".json";
		JsonNode db = params.get(CamtMain.DATABASE.key());
		JsonNode writeJson = db.get(CamtDatabase.WRITE_JSON.key());
		String table = writeJson.get(CamtWriteJson.TABLE.key()).asText();
		String json = writeJson.get(CamtWriteJson.JSON_COL.key()).asText();
		String name = writeJson.get(CamtWriteJson.NAME_COL.key()).asText();
		String whereCol = writeJson.get(CamtWriteJson.WHERE_COL.key()).asText();
		String whereVal = writeJson.get(CamtWriteJson.WHERE_VAL.key()).asText();
		try
		{
			String sql = "UPDATE " + table + " SET " + json + " = ?, " + name + " = ? WHERE " + whereCol + " = ?";
			pstm = connection.prepareStatement(sql);
			pstm.setString(1, jsonValue);
			pstm.setString(2, filename);
			pstm.setString(3, whereVal);
			pstm.closeOnCompletion();
			System.out.println(sql);
			result = pstm.executeUpdate();
		}
		catch (SQLException e)
		{
			CamtParameter.addErrorNode(params, e.getLocalizedMessage());
		}
		finally
		{
			if (!Objects.isNull(pstm))
			{
				try
				{
					pstm.close();
				}
				catch (SQLException e)
				{
				}
			}
		}
		return result == 1;
	}

	private String convertXmlToJson(ObjectNode params, String xml)
	{
		JsonNode node = null;
		try
		{
			XmlMapper xmlMapper = new XmlMapper();
			node = xmlMapper.readTree(xml.getBytes());
			ObjectMapper jsonMapper = new ObjectMapper();
			return jsonMapper.writeValueAsString(node);
		}
		catch (IOException e)
		{
			CamtParameter.addErrorNode(params, e.getLocalizedMessage());
		}
		return null;
	}
}
