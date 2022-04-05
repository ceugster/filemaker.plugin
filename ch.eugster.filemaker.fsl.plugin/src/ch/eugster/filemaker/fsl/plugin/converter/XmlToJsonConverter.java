package ch.eugster.filemaker.fsl.plugin.converter;

import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import ch.eugster.filemaker.fsl.plugin.Executor;
import ch.eugster.filemaker.fsl.plugin.converter.Parameter.ConversionParameter;

public class XmlToJsonConverter implements Executor
{

	@Override
	public String execute(ObjectNode source)
	{
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode target = mapper.createObjectNode();
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
				JsonNode errors = target.get("errors");
				if (Objects.isNull(errors))
				{
					errors = target.putArray("errors");
				}
				ArrayNode.class.cast(errors).add("Konversionsfehler: " + e.getLocalizedMessage());
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
