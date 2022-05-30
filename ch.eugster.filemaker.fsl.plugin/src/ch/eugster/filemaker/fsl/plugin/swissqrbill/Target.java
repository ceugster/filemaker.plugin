package ch.eugster.filemaker.fsl.plugin.swissqrbill;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Target
{
	@JsonProperty("path")
	private String path;

	@JsonProperty("table")
	private String table;

	@JsonProperty("container_col")
	private String containerCol;

	@JsonProperty("name_col")
	private String nameCol;

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

	public String getNameCol()
	{
		return nameCol;
	}

	public void setNameCol(String nameCol)
	{
		this.nameCol = nameCol;
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

	public boolean withDatabase()
	{
		return Objects.nonNull(this.containerCol) && Objects.nonNull(this.nameCol) && Objects.nonNull(this.table)
				&& Objects.nonNull(this.whereCol) && Objects.nonNull(this.whereVal);
	}

	public boolean withFile()
	{
		return Objects.nonNull(this.path) && new File(this.path).exists();
	}

	public boolean isValid()
	{
		return withFile() || withDatabase();
	}

	public Result update(Database database, Blob blob) throws Exception
	{
		Result result = new Result();
		result.setResult(false);
		if (this.withDatabase())
		{
			if (Objects.nonNull(database))
			{
				if (database.isValid())
				{
					String sql = "UPDATE " + table + " SET " + this.containerCol + " = ? AS '" + blob.getName() + "', "
							+ this.nameCol + " = ? WHERE " + this.whereCol + " = ?";
					PreparedStatement pstm = null;
					pstm = database.getConnection().prepareStatement(sql);
					pstm.closeOnCompletion();
					pstm.setBytes(1, blob.getBlob());
					pstm.setString(2, blob.getName());
					pstm.setString(3, this.whereVal);
					result.setResult(pstm.executeUpdate() == 1);
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
			File file = new File(this.path);
			if (file.exists())
			{
				file.delete();
			}
			OutputStream os = null;
			try
			{
				os = new FileOutputStream(file);
				os.write(blob.getBlob());
				result.setResult(true);
				result.setFile(file);
			}
			finally
			{
				if (os != null)
				{
					try
					{
						os.flush();
					}
					catch (IOException e)
					{
					}
					try
					{
						os.close();
					}
					catch (IOException e)
					{
					}
				}
			}
		}
		else
		{
			throw new Exception("Die Konfiguration für das Zielobjekt ist fehlerhaft.");
		}
		return result;
	}
}
