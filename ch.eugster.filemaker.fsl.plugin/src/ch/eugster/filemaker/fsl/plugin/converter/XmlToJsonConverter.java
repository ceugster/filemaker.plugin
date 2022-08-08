package ch.eugster.filemaker.fsl.plugin.converter;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import ch.eugster.filemaker.fsl.plugin.Executor;

public class XmlToJsonConverter implements Executor
{
	@Override
	public String execute(String json, ObjectNode result)
	{
		ObjectMapper mapper = new ObjectMapper();
		Parameters parameters = parseParameters(mapper, json, result);
		Parameters properties = parseConfiguration(parameters, mapper, result);
		if (Objects.isNull(result.get("errors")))
		{
			try
			{
				String xml = properties.getSourceXml();
				String jzon = this.convertXmlToJson(xml);
				result.put(Parameter.TARGET_JSON.key(), jzon);

			}
			catch (IOException e)
			{
				String error = "Konversionsfehler: " + e.getLocalizedMessage();
				this.addError(result, error);
			}
		}
		result.put("result",
				(Objects.isNull(result.get("errors")) || result.get("errors").isEmpty()) ? "OK" : "Fehler");
		return result.toString();
	}

	private Parameters parseParameters(ObjectMapper mapper, String json, ObjectNode result)
	{
		Parameters parameters = new Parameters();
		try
		{
			parameters = mapper.readValue(json, Parameters.class);
		}
		catch (JsonMappingException e1)
		{
			this.addError(result, "Die Übergabeparameter enthalten ungültige Elemente.");
		}
		catch (JsonProcessingException e1)
		{
			this.addError(result, "Beim Verarbeiten der Übergabeparameter ist ein Fehler aufgetreten.");
		}
		catch (IllegalArgumentException e)
		{
		}
		return parameters;
	}

	private Parameters parseConfiguration(Parameters parameters, ObjectMapper mapper, ObjectNode result)
	{
		Parameters properties = null;
		Path path = Paths.get(System.getProperty("user.home"), ".fsl", "fsl.json");
		if (path.toFile().exists())
		{
			properties = mapper.readValue(path.toFile(), Parameters.class);
			properties.merge(parameters);

		}
		else
		{

		}
		try
		{
		}
		catch (StreamReadException e)
		{
			this.addError(result, "Die Konfigurationsdatei '" + path.getFileName() + "' ist fehlerhaft.");
		}
		catch (DatabindException e)
		{
			this.addError(result, "Die Konfigurationsdatei '" + path.getFileName() + "' enthält ungültige Elemente.");
		}
		catch (FileNotFoundException e)
		{
			try
			{
				if (path.toFile().createNewFile())
				{
					Writer w = new FileWriter(path.toFile());
					w.write(Parameter.SOURCE_XML.key + "= \n");
					w.write(Parameter.TARGET_JSON.key + "= \n");
					w.close();
				}
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
		catch (IOException e)
		{
			this.addError(result, "Die Konfigurationsdatei '" + path.getFileName() + "' kann ncht gelesen werden.");
		}
		return properties;
	}

	private String convertXmlToJson(String xml) throws IOException
	{
		XmlMapper xmlMapper = new XmlMapper();
		JsonNode jsonNode = xmlMapper.readTree(xml.getBytes());
		ObjectMapper jsonMapper = new ObjectMapper();
		return jsonMapper.writeValueAsString(jsonNode);
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
