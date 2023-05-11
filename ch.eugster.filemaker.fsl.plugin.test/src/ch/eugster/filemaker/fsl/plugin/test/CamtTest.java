package ch.eugster.filemaker.fsl.plugin.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ch.eugster.filemaker.fsl.plugin.Executor;
import ch.eugster.filemaker.fsl.plugin.Fsl;
import ch.eugster.filemaker.fsl.plugin.camt.Camt;

public class CamtTest extends Camt
{
	private ObjectMapper mapper = new ObjectMapper();

	private static String sourceFilename = "resources/xml/camt.054_P_CH0809000000450010065_1111204750_0_2022121623562233.xml";

	private static String sourceContent;

	private static String targetFilename = "resources/json/camt.054_P_CH0809000000450010065_1111204750_0_2022121623562233.json";

	private static String targetContent;

	@BeforeAll
	public static void before() throws IOException
	{
		File sourceFile = new File(sourceFilename);
		sourceContent = FileUtils.readFileToString(sourceFile, Charset.defaultCharset());
		File targetFile = new File(targetFilename);
		targetContent = FileUtils.readFileToString(targetFile, Charset.defaultCharset());
	}

	@Test
	public void readCamtFile() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Camt.Parameter.XML_FILE.key(), sourceFilename);
		String result = Fsl.execute("Camt.parseFile", requestNode.toString());
		JsonNode responseNode = mapper.readTree(result);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(targetContent, responseNode.get(Executor.RESULT).asText());
	}

	@Test
	public void readCamtFromFile() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Camt.Parameter.XML_FILE.key(), sourceFilename);
		String result = Fsl.execute("Camt.parse", requestNode.toString());
		JsonNode responseNode = mapper.readTree(result);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(targetContent, responseNode.get(Executor.RESULT).asText());
	}

	@Test
	public void readCamtFromContent() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Camt.Parameter.XML_CONTENT.key(), sourceContent);
		String result = Fsl.execute("Camt.parse", requestNode.toString());
		JsonNode responseNode = mapper.readTree(result);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(targetContent, responseNode.get(Executor.RESULT).asText());
		assertEquals("MxCamt05400104", responseNode.get("identifier").asText());
	}

	@Test
	public void readCamtContent() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Camt.Parameter.XML_CONTENT.key(), sourceContent);
		String result = Fsl.execute("Camt.parseContent", requestNode.toString());
		JsonNode responseNode = mapper.readTree(result);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(targetContent, responseNode.get(Executor.RESULT).asText());
		assertEquals("MxCamt05400104", responseNode.get("identifier").asText());
		System.out.println(targetContent);
	}

	@Test
	public void readCamtInvalidContent() throws JsonMappingException, JsonProcessingException
	{
		String result = Fsl.execute("Camt.parse", "2345");
		JsonNode responseNode = mapper.readTree(result);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals("invalid json parameter (must be a valid json string)", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void readCamtNullContent() throws JsonMappingException, JsonProcessingException
	{
		String result = Fsl.execute("Camt.parse", null);
		JsonNode responseNode = mapper.readTree(result);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals("missing argument 'xml_file' or 'xml_content'", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testGetNtfctnFromContent() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Camt.Parameter.XML_CONTENT.key(), sourceContent);
		String result = Fsl.execute("Camt.extract", requestNode.toString());
		JsonNode responseNode = mapper.readTree(result);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(targetContent, responseNode.get(Executor.RESULT).asText());
	}

	@Test
	public void testGetNtfctnFromFile() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Camt.Parameter.XML_FILE.key(), sourceFilename);
		String result = Fsl.execute("Camt.extract", requestNode.toString());
		JsonNode responseNode = mapper.readTree(result);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(targetContent, responseNode.get(Executor.RESULT).asText());
	}
	
}
