package ch.eugster.filemaker.fsl.plugin.swissqrbill;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Test
{
	@JsonProperty("properties")
	private String properties;

	public String getProperties()
	{
		return properties;
	}

	public void setProperties(String properties)
	{
		this.properties = properties;
	}
}
