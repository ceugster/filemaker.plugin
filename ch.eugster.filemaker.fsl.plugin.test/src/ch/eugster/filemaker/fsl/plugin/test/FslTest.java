package ch.eugster.filemaker.fsl.plugin.test;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ch.eugster.filemaker.fsl.plugin.Fsl;

public class FslTest
{
	private static ObjectMapper mapper;

	@BeforeAll
	public static void beforeAll()
	{
		mapper = new ObjectMapper();
	}

	@Test
	public void testFslWithoutModule() throws JsonMappingException, JsonProcessingException
	{
		String result = Fsl.execute("");
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals("Der Befehl ist ungültig.", resultNode.get("errors").get(0).asText());
	}

	@Test
	public void testFslWithWrongModule() throws JsonMappingException, JsonProcessingException
	{
		String result = Fsl.execute("Schmock");
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals("Der Befehl ist ungültig.", resultNode.get("errors").get(0).asText());
	}

	@Test
	public void testFslWithWrongCommand() throws JsonMappingException, JsonProcessingException
	{
		String result = Fsl.execute("Xls.tschaTscha");
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals("Unbekannter Befehl", resultNode.get("errors").get(0).asText());
	}

	@Test
	public void testFslWithWrongModuleAndCommand() throws JsonMappingException, JsonProcessingException
	{
		String result = Fsl.execute("Schmock.tschaTscha");
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals("Schmock ist kein gültiges Modul.", resultNode.get("errors").get(0).asText());
	}
}
