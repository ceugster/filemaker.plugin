package ch.eugster.filemaker.fsl.plugin.swissqrbill;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Source
{
	@JsonProperty("path")
	private String path;

	@JsonProperty("table")
	private String table;

	@JsonProperty("container_col")
	private String containerCol;

	@JsonProperty("where_col")
	private String whereCol;

	@JsonProperty("where_val")
	private String whereVal;

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	public String getTable()
	{
		return table;
	}

	public void setTable(String table)
	{
		this.table = table;
	}

	public String getContainerCol()
	{
		return containerCol;
	}

	public void setContainerCol(String containerCol)
	{
		this.containerCol = containerCol;
	}

	public String getWhereCol()
	{
		return whereCol;
	}

	public void setWhereCol(String whereCol)
	{
		this.whereCol = whereCol;
	}

	public String getWhereVal()
	{
		return whereVal;
	}

	public void setWhereVal(String whereVal)
	{
		this.whereVal = whereVal;
	}

	public boolean isValid()
	{
		return withFile() || withDatabase();
	}

	public boolean withDatabase()
	{
		return Objects.nonNull(this.containerCol) && Objects.nonNull(this.table) && Objects.nonNull(this.whereCol)
				&& Objects.nonNull(this.whereVal);
	}

	public boolean withFile()
	{
		return Objects.nonNull(this.path) && new File(this.path).exists();
	}

	public Blob getBlob(Database database) throws Exception
	{
		if (this.withDatabase())
		{
			if (Objects.nonNull(database))
			{
				if (database.isValid())
				{
					return select(database);
				}
				else
				{
					throw new Exception("Die Zugangsdaten zur Datenbank sind fehlerhaft.");
				}
			}
			else
			{
				throw new Exception("Die Datenbankkonfiguration fehlt oder ist fehlerhaft.");
			}
		}
		else if (this.withFile())
		{
			return this.read(new File(this.path));
		}
		else
		{
			throw new Exception("Die Konfiguration für das Quellobjekt ist fehlerhaft.");
		}
	}

	private Blob select(Database database) throws Exception
	{
		Blob blob = new Blob();
		Connection connection = database.getConnection();
		String sql = "SELECT CAST(" + this.containerCol + " AS VARCHAR) , GetAs(" + this.containerCol
				+ ", DEFAULT) FROM " + this.table + " WHERE " + this.whereCol + " = ?";
		ResultSet rst = null;
		try
		{
			PreparedStatement pstm = null;
			pstm = connection.prepareStatement(sql);
			pstm.closeOnCompletion();
			pstm.setString(1, this.whereVal);
			rst = pstm.executeQuery();
			if (rst.next())
			{
				blob.setName(rst.getString(1));
				blob.setBlob(rst.getBytes(2));
			}
		}
		catch (Exception e)
		{
			throw new Exception("Fehler in der Datenbankabfrage: " + sql);
		}
		finally
		{
			if (Objects.nonNull(rst))
			{
				try
				{
					rst.close();
				}
				catch (SQLException e)
				{
				}
			}
		}
		return blob;
	}

	private Blob read(File file)
	{
		Blob blob = new Blob();
		if (Objects.nonNull(file) && file.exists())
		{
			byte[] targetArray = null;
			InputStream is = null;
			try
			{
				is = new FileInputStream(file);
				targetArray = new byte[is.available()];
				is.read(targetArray);
			}
			catch (IOException e)
			{

			}
			finally
			{
				if (is != null)
				{
					try
					{
						is.close();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
			blob.setBlob(targetArray);
			blob.setName(file.getName());
		}
		return blob;
	}
}
