package ch.eugster.filemaker.fsl.plugin.swissqrbill;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.filemaker.jdbc.Driver;

public class Database
{
	@JsonProperty("url")
	private String url;

	@JsonProperty("username")
	private String username;

	@JsonProperty("password")
	private String password;

	private Connection connection;

	private Driver driver;

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public boolean isValid()
	{
		this.driver = new Driver();
		try
		{
			DriverManager.registerDriver(driver);
			this.connection = DriverManager.getConnection(this.url, this.username, this.password);
			return true;
		}
		catch (SQLException e)
		{
			return false;
		}
	}

	public Connection getConnection()
	{
		try
		{
			if (Objects.isNull(this.connection) || this.connection.isClosed())
			{
				driver = new Driver();
				DriverManager.registerDriver(driver);
				this.connection = DriverManager.getConnection(this.url, this.username, this.password);
			}
		}
		catch (SQLException e)
		{
			return null;
		}
		return this.connection;
	}

	public void closeConnection()
	{
		if (Objects.nonNull(this.connection))
		{
			try
			{
				this.connection.close();
				this.connection = null;
			}
			catch (SQLException e)
			{
			}
			try
			{
				DriverManager.deregisterDriver(driver);
				this.driver = null;
			}
			catch (SQLException e)
			{
			}
		}
	}
}
