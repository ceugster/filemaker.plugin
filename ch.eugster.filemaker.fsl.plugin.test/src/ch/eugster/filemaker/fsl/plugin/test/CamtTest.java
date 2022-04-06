package ch.eugster.filemaker.fsl.plugin.test;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.filemaker.jdbc.Driver;

import ch.eugster.filemaker.fsl.plugin.camt.CamtParameter;
import ch.eugster.filemaker.fsl.plugin.camt.CamtParameter.CamtDatabase;
import ch.eugster.filemaker.fsl.plugin.camt.CamtParameter.CamtMain;
import ch.eugster.filemaker.fsl.plugin.camt.CamtParameter.CamtReadXml;
import ch.eugster.filemaker.fsl.plugin.camt.CamtParameter.CamtWriteJson;
import ch.eugster.filemaker.fsl.plugin.camt.CamtXmlToJsonConverter;

public class CamtTest
{
	private ObjectMapper mapper;

	private static Connection con;

	@BeforeAll
	public static void beforeAll() throws IOException, InterruptedException, SQLException
	{
		Driver driver = new Driver();
		DriverManager.registerDriver(driver);
		con = DriverManager.getConnection("jdbc:filemaker://localhost/Test", "Admin", "");
	}

	@BeforeEach
	public void beforeEach()
	{
		this.mapper = new ObjectMapper();
	}

	@AfterEach
	public void afterEach()
	{

	}

	public void afterAll() throws SQLException
	{
		if (con != null)
		{
			con.close();
		}
	}

	@Test
	public void testParametersOK() throws SQLException
	{
		ObjectNode source = this.mapper.createObjectNode();
		source.put(CamtMain.NAME.key(), "Camt.xml");
		source.put(CamtMain.SIZE.key(), new BigDecimal(354));
		source.put(CamtMain.HASH.key(), "3ABA8E1C-B6FE-422F-8442-5F1C58EDAF2B");
		ObjectNode db = source.putObject(CamtMain.DATABASE.key());
		db.put(CamtDatabase.URL.key(), "jdbc:filemaker://localhost/Test");
		db.put(CamtDatabase.USERNAME.key(), "Admin");
		db.put(CamtDatabase.PASSWORD.key(), "");
		ObjectNode readXml = db.putObject(CamtDatabase.READ_XML.key());
		readXml.put(CamtReadXml.TABLE.key(), "Camt");
		readXml.put(CamtReadXml.COLUMN.key(), "xml");
		readXml.put(CamtReadXml.WHERE_COL.key(), "id_text");
		readXml.put(CamtReadXml.WHERE_VAL.key(), "3ABA8E1C-B6FE-422F-8442-5F1C58EDAF2B");
		ObjectNode writeJson = db.putObject(CamtDatabase.WRITE_JSON.key());
		writeJson.put(CamtWriteJson.TABLE.key(), "Camt");
		writeJson.put(CamtWriteJson.JSON_COL.key(), "json");
		writeJson.put(CamtWriteJson.WHERE_COL.key(), "id_text");
		writeJson.put(CamtWriteJson.WHERE_VAL.key(), "3ABA8E1C-B6FE-422F-8442-5F1C58EDAF2B");
		ObjectNode target = this.mapper.createObjectNode();
		assertTrue(CamtParameter.checkAll(source, target));
		assertEquals("Camt.xml", target.get(CamtMain.NAME.key()).asText());
		assertEquals(new BigDecimal(354), target.get(CamtMain.SIZE.key()).decimalValue());
		assertEquals("3ABA8E1C-B6FE-422F-8442-5F1C58EDAF2B", target.get(CamtMain.HASH.key()).asText());
		assertEquals(ObjectNode.class, target.get(CamtMain.DATABASE.key()).getClass());
		db = ObjectNode.class.cast(target.get(CamtMain.DATABASE.key()));
		assertEquals("jdbc:filemaker://localhost/Test", db.get(CamtDatabase.URL.key()).asText());
		assertEquals("Admin", db.get(CamtDatabase.USERNAME.key()).asText());
		assertEquals("", db.get(CamtDatabase.PASSWORD.key()).asText());
		assertEquals(ObjectNode.class, db.get(CamtDatabase.READ_XML.key()).getClass());
		readXml = ObjectNode.class.cast(db.get(CamtDatabase.READ_XML.key()));
		assertEquals("Camt", readXml.get(CamtReadXml.TABLE.key()).asText());
		assertEquals("xml", readXml.get(CamtReadXml.COLUMN.key()).asText());
		assertEquals("id_text", readXml.get(CamtReadXml.WHERE_COL.key()).asText());
		assertEquals("3ABA8E1C-B6FE-422F-8442-5F1C58EDAF2B", readXml.get(CamtReadXml.WHERE_VAL.key()).asText());
		assertEquals(ObjectNode.class, db.get(CamtDatabase.WRITE_JSON.key()).getClass());
		writeJson = ObjectNode.class.cast(db.get(CamtDatabase.WRITE_JSON.key()));
		assertEquals("Camt", writeJson.get(CamtWriteJson.TABLE.key()).asText());
		assertEquals("json", writeJson.get(CamtWriteJson.JSON_COL.key()).asText());
		assertEquals("id_text", writeJson.get(CamtWriteJson.WHERE_COL.key()).asText());
		assertEquals("3ABA8E1C-B6FE-422F-8442-5F1C58EDAF2B", writeJson.get(CamtWriteJson.WHERE_VAL.key()).asText());
		assertEquals("OK", target.get("result").asText());
		assertNull(target.get("errors"));
	}

	@Test
	public void convertXMLToJson() throws SQLException, IOException
	{
		ObjectNode source = this.mapper.createObjectNode();
		source.put(CamtMain.NAME.key(), "Camt.xml");
		source.put(CamtMain.SIZE.key(), new BigDecimal(354));
		source.put(CamtMain.HASH.key(), "3ABA8E1C-B6FE-422F-8442-5F1C58EDAF2B");
		ObjectNode db = source.putObject(CamtMain.DATABASE.key());
		db.put(CamtDatabase.URL.key(), "jdbc:filemaker://localhost/Test");
		db.put(CamtDatabase.USERNAME.key(), "Admin");
		db.put(CamtDatabase.PASSWORD.key(), "");
		ObjectNode readXml = db.putObject("readXml");
		readXml.put(CamtReadXml.TABLE.key(), "Camt");
		readXml.put(CamtReadXml.COLUMN.key(), "xml");
		readXml.put(CamtReadXml.WHERE_COL.key(), "id_text");
		readXml.put(CamtReadXml.WHERE_VAL.key(), "3ABA8E1C-B6FE-422F-8442-5F1C58EDAF2B");
		ObjectNode writeJson = db.putObject(CamtDatabase.WRITE_JSON.key());
		writeJson.put(CamtWriteJson.TABLE.key(), "Camt");
		writeJson.put(CamtWriteJson.JSON_COL.key(), "json");
		writeJson.put(CamtWriteJson.WHERE_COL.key(), "id_text");
		writeJson.put(CamtWriteJson.WHERE_VAL.key(), "3ABA8E1C-B6FE-422F-8442-5F1C58EDAF2B");
		ObjectNode target = this.mapper.createObjectNode();
		String result = new CamtXmlToJsonConverter().execute(source, target);
		assertEquals("{\n" + "  \"Camt.xml\" : \"Camt.xml\",\n" + "  \"size\" : 354,\n"
				+ "  \"hash\" : \"3ABA8E1C-B6FE-422F-8442-5F1C58EDAF2B\",\n" + "  \"database\" : {\n"
				+ "    \"url\" : \"jdbc:filemaker://localhost/Test\",\n" + "    \"username\" : \"Admin\",\n"
				+ "    \"password\" : \"\",\n" + "    \"readXml\" : {\n" + "      \"table\" : \"Camt\",\n"
				+ "      \"column\" : \"xml\",\n" + "      \"where_col\" : \"id_text\",\n"
				+ "      \"where_val\" : \"3ABA8E1C-B6FE-422F-8442-5F1C58EDAF2B\"\n" + "    },\n"
				+ "    \"writeJson\" : {\n" + "      \"table\" : \"Camt\",\n" + "      \"json\" : \"json\",\n"
				+ "      \"name\" : \"filename\",\n" + "      \"where_col\" : \"id_text\",\n"
				+ "      \"where_val\" : \"3ABA8E1C-B6FE-422F-8442-5F1C58EDAF2B\"\n" + "    }\n" + "  },\n"
				+ "  \"result\" : \"OK\"\n" + "}", result);
	}
}
