package ch.eugster.filemaker.fsl.plugin.test;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ch.eugster.filemaker.fsl.plugin.Executor;
import ch.eugster.filemaker.fsl.plugin.Fsl;
import ch.eugster.filemaker.fsl.plugin.converter.Converter;

public class ConversionTest
{
	private static ObjectMapper mapper;

	private static String XML_SOURCE_PATH = "resources/xml/camt.054_P_CH0809000000450010065_1111204750_0_2022121623562233.xml";

	private static File XML_SOURCE_FILE = new File(XML_SOURCE_PATH);
	
	private static String XML_TARGET_PATH = "targets/camt.054_P_CH0809000000450010065_1111204750_0_2022121623562233.xml";

	private static File XML_TARGET_FILE = new File(XML_TARGET_PATH);
	
	private static String XML_SOURCE_CONTENT;

	private static String XML_TARGET_CONTENT;

	private static String JSON_SOURCE_PATH = "resources/json/camt.054_P_CH0809000000450010065_1111204750_0_2022121623562233.json";

	private static File JSON_SOURCE_FILE = new File(JSON_SOURCE_PATH);
	
	private static String JSON_TARGET_PATH = "targets/camt.054_P_CH0809000000450010065_1111204750_0_2022121623562233.json";

	private static File JSON_TARGET_FILE = new File(JSON_TARGET_PATH);
	
	private static String JSON_SOURCE_CONTENT;

	private static String JSON_TARGET_CONTENT;

	@BeforeAll
	public static void beforeAll() throws IOException
	{
		mapper = new ObjectMapper();
		XML_SOURCE_CONTENT = FileUtils.readFileToString(XML_SOURCE_FILE, Charset.defaultCharset());
		JSON_SOURCE_CONTENT = FileUtils.readFileToString(JSON_SOURCE_FILE, Charset.defaultCharset());
	}

	@AfterEach
	public void afterEach()
	{
		try
		{
			if (XML_TARGET_FILE.exists())
			{
				XML_TARGET_CONTENT = FileUtils.readFileToString(XML_TARGET_FILE, Charset.defaultCharset());
			}
		}
		catch (Exception e)
		{
			
		}
		try
		{
			if (JSON_TARGET_FILE.exists())
			{
				JSON_TARGET_CONTENT = FileUtils.readFileToString(JSON_TARGET_FILE, Charset.defaultCharset());
			}
		}
		catch (Exception e)
		{
			
		}
	}

	@AfterAll
	public static void afterAll() throws IOException
	{
		try
		{
			FileUtils.cleanDirectory(new File("targets"));
		}
		catch (Exception e)
		{
			
		}
	}

	@Test
	public void testWithoutParameter() throws SQLException, IOException
	{
		String result = Fsl.execute("Converter.convertXmlToJson", null);

		JsonNode resultNode = mapper.readTree(result);
		assertEquals(Executor.ERROR, resultNode.get(Executor.STATUS).asText());
		assertEquals(ArrayNode.class, resultNode.get(Executor.ERRORS).getClass());
		assertEquals(1, resultNode.get(Executor.ERRORS).size());
		assertEquals("missing_argument 'json'", resultNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testWithInvalidParameter() throws SQLException, IOException
	{
		String result = Fsl.execute("Converter.convertXmlToJson", "0");
		JsonNode responseNode = mapper.readTree(result);
		assertEquals("Fehler", responseNode.get(Executor.STATUS).asText());
		assertEquals("invalid_argument 'json'", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testXmlContentToJsonContent() throws SQLException, IOException
	{
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Converter.Key.XML_CONTENT.key(), XML_SOURCE_CONTENT);

		String result = Fsl.execute("Converter.convertXmlToJson", requestNode.toString());

		JsonNode responseNode = mapper.readTree(result);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertNotNull(responseNode.get(Executor.RESULT).asText());
	}

	@Test
	public void testXmlContentToJsonFile() throws SQLException, IOException
	{
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Converter.Key.XML_CONTENT.key(), XML_SOURCE_CONTENT);
		requestNode.put(Converter.Key.JSON_TARGET_FILE.key(), JSON_TARGET_PATH);

		String result = Fsl.execute("Converter.convertXmlToJson", requestNode.toString());

		JsonNode responseNode = mapper.readTree(result);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(JSON_TARGET_PATH, responseNode.get(Executor.RESULT).asText());
		assertTrue(JSON_TARGET_FILE.exists());
	}

	@Test
	public void testXmlFileToJsonFile() throws SQLException, IOException
	{
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Converter.Key.XML_SOURCE_FILE.key(), XML_SOURCE_PATH);
		requestNode.put(Converter.Key.JSON_TARGET_FILE.key(), JSON_TARGET_PATH);

		String result = Fsl.execute("Converter.convertXmlToJson", requestNode.toString());

		JsonNode responseNode = mapper.readTree(result);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(JSON_TARGET_PATH, responseNode.get(Executor.RESULT).asText());
		assertTrue(JSON_TARGET_FILE.exists());
	}

	@Test
	public void testXmlFileToJsonContent() throws SQLException, IOException
	{
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put(Converter.Key.XML_SOURCE_FILE.key(), XML_SOURCE_PATH);

		String result = Fsl.execute("Converter.convertXmlToJson", requestNode.toString());

		JsonNode responseNode = mapper.readTree(result);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertNotNull(responseNode.get(Executor.RESULT).asText());
	}

}
