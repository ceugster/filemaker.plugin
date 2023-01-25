package ch.eugster.filemaker.fsl.plugin.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ch.eugster.filemaker.fsl.plugin.Fsl;

public class UiTest
{
	private ObjectMapper mapper = new ObjectMapper();

	@Test
	public void selectFileTest() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode argument = mapper.createObjectNode();
		argument.put("title", "CAMT-Datei wählen");
		ArrayNode extensions = argument.arrayNode();
		extensions.add("Camt*.xml");
		argument.put("index", Integer.valueOf(0));
		ArrayNode types = argument.arrayNode();
		types.add("CAMT-Dateien");
		File homeDir = new File(System.getProperty("user.home"));
		File downloads = new File(homeDir.getAbsolutePath() + File.separator + "Downloads");
		argument.put("path", downloads.isDirectory() ? downloads.getAbsolutePath() : homeDir.getAbsolutePath());
		String result = Fsl.execute("Ui.selectFile", new Object[] { argument.toString() });
		JsonNode node = mapper.readTree(result);
		assertEquals("OK", node.get("result").asText());
		assertTrue(new File(node.get("target").asText()).isFile());
	}

	@Test
	public void selectDirectoryTest() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode argument = mapper.createObjectNode();
		argument.put("message", "Wählen Sie das gewünschte Verzeichnis");
		argument.put("text", "Text");
		File homeDir = new File(System.getProperty("user.home"));
		File downloads = new File(homeDir.getAbsolutePath() + File.separator + "Downloads");
		argument.put("path", downloads.isDirectory() ? downloads.getAbsolutePath() : homeDir.getAbsolutePath());
		String result = Fsl.execute("Ui.selectDirectory", new Object[] { argument.toString() });
		JsonNode node = mapper.readTree(result);
		assertEquals("OK", node.get("result").asText());
		assertTrue(new File(node.get("target").asText()).isDirectory());
	}
}
