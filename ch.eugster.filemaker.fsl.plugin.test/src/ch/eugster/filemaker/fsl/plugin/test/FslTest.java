package ch.eugster.filemaker.fsl.plugin.test;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ch.eugster.filemaker.fsl.plugin.Executor;
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
		String response = Fsl.execute("", "{}");
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("missing_command", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testFslWithWrongModule() throws JsonMappingException, JsonProcessingException
	{
		String response = Fsl.execute("Schmock", "{}");
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("missing_argument 'json'", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testFslWithWrongCommand() throws JsonMappingException, JsonProcessingException
	{
		String response = Fsl.execute("Xls.tschaTscha", "{}");
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("invalid_command 'tschaTscha'", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testFslWithWrongModuleAndCommand() throws JsonMappingException, JsonProcessingException
	{
		String result = Fsl.execute("Schmock.tschaTscha", "{}");
		JsonNode resultNode = mapper.readTree(result);
		assertEquals(Executor.ERROR, resultNode.get(Executor.STATUS).asText());
		assertEquals(1, resultNode.get(Executor.ERRORS).size());
		assertEquals("invalid_module 'Schmock'", resultNode.get(Executor.ERRORS).get(0).asText());
	}
}
