package ch.eugster.filemaker.fsl.plugin.converter;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Parameters
{
	@JsonProperty("source_xml")
	private String sourceXml;

	@JsonProperty("target_json")
	private String targetJson;

	public String getSourceXml()
	{
		return sourceXml;
	}

	public void setSourceXml(String sourceXml)
	{
		this.sourceXml = sourceXml;
	}

	public String getTargetJson()
	{
		return targetJson;
	}

	public void setTargetJson(String targetJson)
	{
		this.targetJson = targetJson;
	}

	public void merge(Parameters other)
	{
		if (Objects.nonNull(other.getSourceXml()))
		{
			this.setSourceXml(other.getSourceXml());
		}
		if (Objects.nonNull(other.getTargetJson()))
		{
			this.setTargetJson(other.getTargetJson());
		}
	}
}
