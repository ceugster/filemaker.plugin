package ch.eugster.filemaker.fsl.plugin.converter;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import ch.eugster.filemaker.fsl.plugin.Executor;
import ch.eugster.filemaker.fsl.plugin.converter.Parameter.ConversionParameter;

public class XmlToJsonConverter implements Executor
{

	@Override
	public String execute(ObjectNode source, ObjectNode target)
	{
		if (Parameter.checkAll(source, target))
		{
			try
			{
				String xml = source.get(ConversionParameter.SOURCE_XML.key()).asText();
				String json = this.convertXmlToJson(xml);
				target.put(ConversionParameter.TARGET_JSON.key(), json);

			}
			catch (IOException e)
			{
				String error = "Konversionsfehler: " + e.getLocalizedMessage();
				this.createErrorMessage(target, error);
			}
		}
		return target.toString();
	}

	private String convertXmlToJson(String xml) throws IOException
	{
		XmlMapper xmlMapper = new XmlMapper();
		JsonNode jsonNode = xmlMapper.readTree(xml.getBytes());
		ObjectMapper jsonMapper = new ObjectMapper();
		return jsonMapper.writeValueAsString(jsonNode);
	}
}
