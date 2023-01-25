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

import ch.eugster.filemaker.fsl.plugin.Fsl;

public class CamtTest
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
		String result = Fsl.execute("Camt.parse", new Object[] { sourceFilename });
		JsonNode node = mapper.readTree(result);
		assertEquals("OK", node.get("result").asText());
		System.out.println(node.get("target").asText());
		assertEquals(targetContent, node.get("target").asText());
	}

	@Test
	public void readCamtContent() throws JsonMappingException, JsonProcessingException
	{
		String result = Fsl.execute("Camt.parse", new Object[] { sourceFilename });
		JsonNode node = mapper.readTree(result);
		assertEquals("OK", node.get("result").asText());
		assertEquals(targetContent, node.get("target").asText());
	}

	@Test
	public void readCamtInvalidContent() throws JsonMappingException, JsonProcessingException
	{
		String result = Fsl.execute("Camt.parse", new Object[] { "2345" });
		JsonNode node = mapper.readTree(result);
		assertEquals("Fehler", node.get("result").asText());
		assertEquals("Ungültiges Format.", node.get("errors").get(0).asText());
	}

	@Test
	public void readCamtNullContent() throws JsonMappingException, JsonProcessingException
	{
		String result = Fsl.execute("Camt.parse", new Object[0]);
		JsonNode node = mapper.readTree(result);
		assertEquals("Fehler", node.get("result").asText());
		assertEquals("Falsche Anzahl Parameter (Erwartet: 1, übergeben: 0).", node.get("errors").get(0).asText());
	}

	@Test
	public void testGetNtfctnFromContent() throws JsonMappingException, JsonProcessingException
	{
		String result = Fsl.execute("Camt.extract", new Object[] { sourceContent });
		JsonNode node = mapper.readTree(result);
		assertEquals("OK", node.get("result").asText());
		assertEquals(targetContent, node.get("target").asText());
	}

	@Test
	public void testGetNtfctnFromFile() throws JsonMappingException, JsonProcessingException
	{
		String result = Fsl.execute("Camt.extract", new Object[] { sourceFilename });
		JsonNode node = mapper.readTree(result);
		assertEquals("OK", node.get("result").asText());
		assertEquals(targetContent, node.get("target").asText());
	}
}
