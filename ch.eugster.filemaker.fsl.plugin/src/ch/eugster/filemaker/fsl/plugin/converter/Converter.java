package ch.eugster.filemaker.fsl.plugin.converter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import ch.eugster.filemaker.fsl.plugin.Executor;

public class Converter extends Executor
{
	/**
	 * Converts xml String to Json String
	 * 
	 * @param requestNode
	 * @param responseNode
	 * 
	 * @returns json or pathname
	 */
	public void convertXmlToJson(ObjectNode requestNode, ObjectNode responseNode)
	{
		String xmlContent = null;
		JsonNode xml = requestNode.findPath(Key.XML_CONTENT.key());
		if (xml.isTextual()) 
		{
			xmlContent = xml.asText();
		}
		else
		{
			xml = requestNode.get(Key.XML_SOURCE_FILE.key());
			if (Objects.nonNull(xml) && xml.isTextual()) 
			{
				try
				{
					Path path = Paths.get(xml.asText());
					StringBuilder builder = new StringBuilder();
					List<String> lines = Files.readAllLines(path);
					lines.forEach(line -> builder.append(line));
					xmlContent = builder.toString();
				}
				catch (Exception e)
				{
					addErrorMessage(responseNode, "unknown source file '" + Key.XML_SOURCE_FILE.key() + "'");
				}
			}
		}
		if (Objects.nonNull(xmlContent)) 
		{
			try
			{
				XmlMapper mapper = new XmlMapper();
				JsonNode jsonContent = mapper.readTree(xmlContent);
				JsonNode json = requestNode.get(Key.JSON_TARGET_FILE.key());
				if (Objects.isNull(json)) 
				{
					responseNode.put(Executor.RESULT, jsonContent.toString());
				}
				else if (json.isTextual())
				{
					String pathname = json.asText();
					Path path = Paths.get(json.asText());
					Files.write(path, jsonContent.toString().getBytes(), StandardOpenOption.CREATE);
					responseNode.put(Executor.RESULT, pathname);
				}
				else
				{
					addErrorMessage(responseNode, "wrong variable type '" + Key.XML_TARGET_FILE.key() + "'");
				}
			}
			catch (Exception e)
			{
				addErrorMessage(responseNode, e.getLocalizedMessage());
			}
		}
		else
		{
			addErrorMessage(responseNode, "invalid xml content");
		}
	}

	public void convertWordToPdf(Object[] parameters)
	{
//		InputStream source = new File(String.valueOf(parameters[0])); 
//		OutputStream target = new File(String.valueOf(parameters[1]));
//		IConverter converter = LocalConverter.builder().build();
//		if (converter
//				.convert(source).as(DocumentType.MS_WORD)
//		        .to(target).as(DocumentType.PDF)
//		        .execute())
//		{
//			
//		}
	}

	public enum Key
	{
		// @formatter:off
		XML_SOURCE_FILE("xml_source_file"),
		XML_TARGET_FILE("xml_target_file"),
		XML_CONTENT("xml_content"),
		JSON_SOURCE_FILE("json_source_file"),
		JSON_TARGET_FILE("json_target_file"),
		JSON_CONTENT("json_content");
		// @formatter:on

		private String key;

		private Key(String key)
		{
			this.key = key;
		}

		public String key()
		{
			return this.key;
		}
	}
}
