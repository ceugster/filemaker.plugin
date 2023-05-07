package ch.eugster.filemaker.fsl.plugin.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Objects;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ch.eugster.filemaker.fsl.plugin.Executor;
import ch.eugster.filemaker.fsl.plugin.Fsl;

public class PdfTest
{
	private ObjectMapper mapper = new ObjectMapper();

	private static String sourceFilename = "resources/pdf/document.pdf";

	@BeforeAll
	public static void beforeAll() throws IOException
	{
	}

	@AfterAll
	public static void afterAll() throws IOException
	{
	}

	@Test
	public void testDocumentInfo() throws IOException
	{
		File file = new File(sourceFilename);
		InputStream is = null;
		try
		{
			is = new FileInputStream(file);
			byte[] content = is.readAllBytes();
			ObjectNode requestNode = mapper.createObjectNode();
			requestNode.put("content", Base64.getEncoder().encodeToString(content));

			String response = Fsl.execute("Pdf.getDocumentInfo", requestNode.toString());
			
			JsonNode responseNode = mapper.readTree(response);
			assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
			JsonNode result = mapper.readTree(responseNode.get(Executor.RESULT).asText());
			assertEquals("SwissQRBill", result.get("author").asText());
			assertEquals("SwissQRBill", result.get("creator").asText());
			assertEquals("", result.get("keywords").asText());
			assertEquals("SwissQRBill", result.get("producer").asText());
			assertEquals("", result.get("subject").asText());
			assertEquals("", result.get("title").asText());
			assertEquals("2023/03/29 10:57:18", result.get("creationDate").asText());
			assertEquals("", result.get("modificationDate").asText());
		}
		finally
		{
			if (Objects.nonNull(is))
				is.close();
		}
		
	}
}
