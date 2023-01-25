package ch.eugster.filemaker.fsl.plugin.converter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import ch.eugster.filemaker.fsl.plugin.Executor;

public class Converter extends Executor<Converter>
{
	/**
	 * Converts xml String to Json String
	 * 
	 * @param parameters
	 * 
	 * @param parameters[0] xml String
	 */
	public static void convertXmlToJson(Object[] parameters)
	{
		try
		{
			String json = null;
			String source = null;
			XmlMapper xmlMapper = new XmlMapper();
			if (parameters.length < 1)
			{
				throw new IllegalArgumentException("Parameter fehlt");
			}
			if (!parameters[0].getClass().equals(String.class))
			{
				throw new IllegalArgumentException("Ungültiger Parameter (muss vom Typ String sein)");
			}
			try
			{
				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode sourceNode = objectMapper.readTree(parameters[0].toString());
				source = sourceNode.get(Parameter.SOURCE_XML.key()).asText();
				json = xmlMapper.readTree(source).toString();
			}
			catch (Exception e)
			{
				try
				{
					Path path = Paths.get(source);
					if (path.toFile().canRead())
					{
						List<String> lines = Files.readAllLines(path);
						if (!Objects.isNull(lines) && lines.size() > 0)
						{
							StringBuilder builder = new StringBuilder();
							for (String line : lines)
							{
								builder.append(line);
							}
							source = builder.toString();
							json = xmlMapper.readTree(source).toString();
						}
					}
				}
				catch (Exception e2)
				{
					source = parameters[0].toString();
					json = xmlMapper.readTree(source).toString();
				}
			}
			if (Objects.isNull(json))
			{
				throw new IllegalArgumentException("Ungültiger Parameter");
			}
			putValue(Parameter.TARGET_JSON.key(), json);
		}
		catch (Exception e)
		{
			addErrorMessage(e);
		}
	}

	public enum Parameter
	{
		// @formatter:off
		SOURCE_XML("source_xml"),
		TARGET_JSON("target_json");
		// @formatter:on

		private String key;

		private Parameter(String key)
		{
			this.key = key;
		}

		public String key()
		{
			return this.key;
		}
	}
}
