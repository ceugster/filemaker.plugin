package ch.eugster.filemaker.fsl.plugin.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ch.eugster.filemaker.fsl.plugin.Executor;
import ch.eugster.filemaker.fsl.plugin.Fsl;

public final class XlsTest extends AbstractXlsTest
{
	@Test
	public void testCallableMethods() throws Exception
	{
		String response = Fsl.execute("Xls.getCallableMethods", "{}");
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		ArrayNode methods = ArrayNode.class.cast(responseNode.get("methods"));
		for (int i = 0; i < methods.size(); i++)
		{
			System.out.println(methods.get(i).asText());
		}
	}

	@Test
	public void testCreateWorkbook() throws Exception
	{
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("workbook", WORKBOOK_1);

		String result = Fsl.execute("Xls.createWorkbook", requestNode.toString());

		JsonNode responseNode = mapper.readTree(result);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testCreateWorkbookWithoutName() throws Exception
	{
		ObjectNode requestNode = mapper.createObjectNode();

		String response = Fsl.execute("Xls.createWorkbook", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals("missing argument 'workbook'", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testCreateWorkbookWhenAlreadyExisting() throws Exception
	{
		xls.workbooks.put(WORKBOOK_1, new XSSFWorkbook());

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("workbook", WORKBOOK_1);

		String response = Fsl.execute("Xls.createWorkbook", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("workbook 'workbook1.xlsx' already exists", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testCreateAndActivateWorkbookWithoutName() throws Exception
	{
		ObjectNode requestNode = mapper.createObjectNode();

		String response = Fsl.execute("Xls.createAndActivateWorkbook", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("missing argument 'workbook'", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testCreateAndActivateWorkbookAlreadyExistingWorkbook() throws Exception
	{
		xls.workbooks.put(WORKBOOK_1, new XSSFWorkbook());

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("workbook", WORKBOOK_1);

		String response = Fsl.execute("Xls.createAndActivateWorkbook", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("workbook 'workbook1.xlsx' already exists", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testCreateSheet() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookIfMissing();
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("sheet", "Arbeitsblatt");

		String response = Fsl.execute("Xls.createSheet", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals("Arbeitsblatt", responseNode.get("sheet").asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testCreateSheetWithoutName() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookIfMissing();

		String response = Fsl.execute("Xls.createSheet", "{}");

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("missing argument 'sheet'", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testCreateSheetAlreadyExisting() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("sheet", SHEET0);

		String response = Fsl.execute("Xls.createSheet", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals("sheet 'Sheet0' already exists", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testActivateSheetByIndex() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("sheet", 0);

		String response = Fsl.execute("Xls.activateSheet", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testActivateSheetByName() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("sheet", SHEET0);

		String response = Fsl.execute("Xls.activateSheet", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}
	
	@Test
	public void testActivateSheetWithoutParameter() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookIfMissing();
		ObjectNode requestNode = mapper.createObjectNode();

		String response = Fsl.execute("Xls.activateSheet", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals("missing_argument 'sheet'", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testActivateNotExistingSheet() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookIfMissing();
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("sheet", "Schmock");

		String response = Fsl.execute("Xls.activateSheet", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals("missing sheet 'Schmock'", responseNode.get(Executor.ERRORS).get(0).asText());

		requestNode = mapper.createObjectNode();
		requestNode.put("sheet", 0);

		response = Fsl.execute("Xls.activateSheet", requestNode.toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals("missing sheet '0'", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testActivateSheetWithoutWorkbook() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("sheet", SHEET0);

		String response = Fsl.execute("Xls.activateSheet", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals("missing argument 'workbook'", responseNode.get(Executor.ERRORS).get(0).asText());

		requestNode = mapper.createObjectNode();
		requestNode.put("index", 0);

		response = Fsl.execute("Xls.activateSheet", requestNode.toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals("missing argument 'workbook'", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testSaveActiveWorkbook() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		ObjectNode requestNode = mapper.createObjectNode();

		String response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testSaveActiveWorkbookNotExisting() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = mapper.createObjectNode();

		String response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("missing argument 'workbook'", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testSaveNamedWorkbook() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("workbook", WORKBOOK_1);
		
		String response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testSaveNamedWorkbookNotExisting() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("workbook", "gigi");
		
		String response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("invalid argument 'workbook'", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testSaveWorkbookToTarget() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("target", WORKBOOK_1);
		
		String response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testSaveNotExistingWorkbookToTarget() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("target", WORKBOOK_1);
		
		String response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("missing argument 'workbook'", responseNode.get(Executor.ERRORS).get(0).asText());
	}
	
	@Test
	public void testSaveNamedWorkbookToTarget() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("workbook", WORKBOOK_1);
		requestNode.put("target", WORKBOOK_1);
		
		String response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testSaveNamedNotExistingWorkbookToTarget() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("workbook", "gigi");
		requestNode.put("target", WORKBOOK_1);
		
		String response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("invalid argument 'workbook'", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testSaveWithParameter() throws JsonMappingException, JsonProcessingException
	{
		String workbook = "./targets/test.xlsx";
		prepareWorkbookIfMissing(workbook);
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("workbook", workbook);
		
		String response = Fsl.execute("Xls.saveWorkbook", requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertTrue(new File(workbook).exists());
	}

	@Test
	public void testSave() throws JsonMappingException, JsonProcessingException
	{
		String workbook = "./targets/test.xlsx";
		prepareWorkbookIfMissing(workbook);
		ObjectNode requestNode = mapper.createObjectNode();
		String result = Fsl.execute("Xls.saveWorkbook", requestNode.toString());
		JsonNode resultNode = mapper.readTree(result);
		assertEquals(Executor.OK, resultNode.get(Executor.STATUS).asText());
		assertTrue(new File(workbook).exists());
	}

	@Test
	public void testSetHeaderFooter() throws JsonMappingException, JsonProcessingException
	{
		String workbook = "./targets/TestSetHeaderFooter.xlsx";
		prepareWorkbookAndSheetIfMissing(workbook, SHEET0);
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("left", "Header links");
		requestNode.put("center", "Header Mitte");
		requestNode.put("right", "Header rechts");

		String response = Fsl.execute("Xls.setHeaders", requestNode.toString());
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());

		Workbook wb = xls.getActiveWorkbook();
		assertEquals("Header links",
				wb.getSheetAt(wb.getActiveSheetIndex()).getHeader().getLeft());
		assertEquals("Header Mitte",
				wb.getSheetAt(wb.getActiveSheetIndex()).getHeader().getCenter());
		assertEquals("Header rechts",
				wb.getSheetAt(wb.getActiveSheetIndex()).getHeader().getRight());

		requestNode = mapper.createObjectNode();
		requestNode.put("left", "Footer links");
		requestNode.put("center", "Footer Mitte");
		requestNode.put("right", "Footer rechts");

		response = Fsl.execute("Xls.setFooters", requestNode.toString());
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals("Footer links",
				wb.getSheetAt(wb.getActiveSheetIndex()).getFooter().getLeft());
		assertEquals("Footer Mitte",
				wb.getSheetAt(wb.getActiveSheetIndex()).getFooter().getCenter());
		assertEquals("Footer rechts",
				wb.getSheetAt(wb.getActiveSheetIndex()).getFooter().getRight());

		requestNode = mapper.createObjectNode();
		requestNode.put("workbook", workbook);
		String result = Fsl.execute("Xls.saveWorkbook", requestNode.toString());
		JsonNode resultNode = mapper.readTree(result);
		assertEquals(Executor.OK, resultNode.get(Executor.STATUS).asText());
		assertTrue(new File(workbook).exists());
	}

	@Test
	public void testJSONFormatting() throws JsonMappingException, JsonProcessingException
	{
		String value = "{\"amount\":287.30,\"currency\":\"CHF\",\"iban\":\"CH4431999123000889012\",\"reference\":\"000000000000000000000000000\",\"message\":\"Rechnungsnr. 10978 / Auftragsnr. 3987\",\"creditor\":{\"name\":\"Schreinerei Habegger & Söhne\",\"address_line_1\":\"Uetlibergstrasse 138\",\"address_line_2\":\"8045 Zürich\",\"country\":\"CH\"},\"debtor\":{\"name\":\"Simon Glarner\",\"address_line_1\":\"Bächliwis 55\",\"address_line_2\":\"8184 Bachenbülach\",\"country\":\"CH\"},\"format\":{\"graphics_format\":\"PDF\",\"output_size\":\"A4_PORTRAIT_SHEET\",\"language\":\"DE\"}}";
		JsonNode json = mapper.readTree(value);
		System.out.println(json.toPrettyString());
	}
}
