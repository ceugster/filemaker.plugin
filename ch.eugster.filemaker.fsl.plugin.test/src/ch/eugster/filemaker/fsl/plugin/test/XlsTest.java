package ch.eugster.filemaker.fsl.plugin.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ch.eugster.filemaker.fsl.plugin.Executor;
import ch.eugster.filemaker.fsl.plugin.Fsl;
import ch.eugster.filemaker.fsl.plugin.xls.Xls;

public class XlsTest extends Xls
{
	protected static ObjectMapper MAPPER = new ObjectMapper();

	protected static final String WORKBOOK_1 = "./targets/workbook1.xlsx";

	@BeforeAll
	public static void beforeAll()
	{
	}

	@BeforeEach
	public void beforeEach()
	{
		releaseAllWorkbooks();
	}

	@AfterAll
	public static void afterAll()
	{
		releaseWorkbooks(MAPPER.createObjectNode(), MAPPER.createObjectNode());
	}

	@AfterEach
	public void afterEach()
	{
		releaseWorkbook(MAPPER.createObjectNode(), MAPPER.createObjectNode());
	}

	@Test
	public void testCallableMethods() throws Exception
	{
		String response = Fsl.execute("Xls.getCallableMethods", "{}");

		JsonNode responseNode = MAPPER.readTree(response);
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
		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.WORKBOOK.key(), WORKBOOK_1);

		String result = Fsl.execute("Xls.createWorkbook", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(result);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testCreateWorkbookWithoutName() throws Exception
	{
		ObjectNode requestNode = MAPPER.createObjectNode();

		String response = Fsl.execute("Xls.createWorkbook", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals("missing argument 'workbook'", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testCreateWorkbookWhenAlreadyExisting() throws Exception
	{
		Xls.workbooks.put(WORKBOOK_1, new XSSFWorkbook());

		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.WORKBOOK.key(), WORKBOOK_1);

		String response = Fsl.execute("Xls.createWorkbook", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("workbook 'workbook1.xlsx' already exists", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testCreateAndActivateWorkbookWithoutName() throws Exception
	{
		ObjectNode requestNode = MAPPER.createObjectNode();

		String response = Fsl.execute("Xls.createAndActivateWorkbook", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("missing argument 'workbook'", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testCreateAndActivateWorkbookAlreadyExistingWorkbook() throws Exception
	{
		Xls.workbooks.put(WORKBOOK_1, new XSSFWorkbook());

		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.WORKBOOK.key(), WORKBOOK_1);

		String response = Fsl.execute("Xls.createAndActivateWorkbook", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("workbook 'workbook1.xlsx' already exists", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testCreateSheet() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookIfMissing();
		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.SHEET.key(), "Arbeitsblatt");

		String response = Fsl.execute("Xls.createSheet", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals("Arbeitsblatt", responseNode.get(Key.SHEET.key()).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testCreateSheetWithoutName() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookIfMissing();

		String response = Fsl.execute("Xls.createSheet", "{}");

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("missing argument 'sheet'", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testCreateSheetAlreadyExisting() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.SHEET.key(), SHEET0);

		String response = Fsl.execute("Xls.createSheet", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals("sheet 'Sheet0' already exists", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testActivateSheetByIndex() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.SHEET.key(), 0);

		String response = Fsl.execute("Xls.activateSheet", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testActivateSheetByName() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.SHEET.key(), SHEET0);

		String response = Fsl.execute("Xls.activateSheet", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}
	
	@Test
	public void testActivateSheetWithoutParameter() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookIfMissing();
		ObjectNode requestNode = MAPPER.createObjectNode();

		String response = Fsl.execute("Xls.activateSheet", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals("missing_argument 'sheet'", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testActivateNotExistingSheet() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookIfMissing();
		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.SHEET.key(), "Schmock");

		String response = Fsl.execute("Xls.activateSheet", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals("missing sheet 'Schmock'", responseNode.get(Executor.ERRORS).get(0).asText());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.SHEET.key(), 0);

		response = Fsl.execute("Xls.activateSheet", requestNode.toString());

		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals("missing sheet '0'", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testActivateSheetWithoutWorkbook() throws JsonMappingException, JsonProcessingException
	{
		releaseWorkbook(MAPPER.createObjectNode(), MAPPER.createObjectNode());

		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.SHEET.key(), SHEET0);

		String response = Fsl.execute("Xls.activateSheet", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals("missing argument 'workbook'", responseNode.get(Executor.ERRORS).get(0).asText());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.INDEX.key(), 0);

		response = Fsl.execute("Xls.activateSheet", requestNode.toString());

		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals("missing argument 'workbook'", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testSaveActiveWorkbook() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		ObjectNode requestNode = MAPPER.createObjectNode();

		String response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testSaveActiveWorkbookNotExisting() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = MAPPER.createObjectNode();

		String response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("missing argument 'workbook'", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testSaveNamedWorkbook() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.WORKBOOK.key(), WORKBOOK_1);
		
		String response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testSaveNamedWorkbookNotExisting() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.WORKBOOK.key(), "gigi");
		
		String response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("invalid argument 'workbook'", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testSaveWorkbookToTarget() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.TARGET.key(), WORKBOOK_1);
		
		String response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testSaveNotExistingWorkbookToTarget() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.TARGET.key(), WORKBOOK_1);
		
		String response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
		
		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("missing argument 'workbook'", responseNode.get(Executor.ERRORS).get(0).asText());
	}
	
	@Test
	public void testSaveNamedWorkbookToTarget() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.WORKBOOK.key(), WORKBOOK_1);
		requestNode.put(Key.TARGET.key(), WORKBOOK_1);
		
		String response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testSaveNamedNotExistingWorkbookToTarget() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.WORKBOOK.key(), "gigi");
		requestNode.put(Key.TARGET.key(), WORKBOOK_1);
		
		String response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("invalid argument 'workbook'", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testSaveWithParameter() throws JsonMappingException, JsonProcessingException
	{
		String workbook = "./targets/test.xlsx";
		prepareWorkbookIfMissing(workbook);
		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.WORKBOOK.key(), workbook);
		String result = Fsl.execute("Xls.saveWorkbook", requestNode.toString());
		JsonNode resultNode = MAPPER.readTree(result);
		assertEquals(Executor.OK, resultNode.get(Executor.STATUS).asText());
		assertTrue(new File(workbook).exists());
	}

	@Test
	public void testSave() throws JsonMappingException, JsonProcessingException
	{
		String workbook = "./targets/test.xlsx";
		prepareWorkbookIfMissing(workbook);
		ObjectNode requestNode = MAPPER.createObjectNode();
		String result = Fsl.execute("Xls.saveWorkbook", requestNode.toString());
		JsonNode resultNode = MAPPER.readTree(result);
		assertEquals(Executor.OK, resultNode.get(Executor.STATUS).asText());
		assertTrue(new File(workbook).exists());
	}

	@Test
	public void testSetHeaderFooter() throws JsonMappingException, JsonProcessingException
	{
		String workbook = "./targets/TestSetHeaderFooter.xlsx";
		prepareWorkbookAndSheetIfMissing(workbook, SHEET0);
		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.LEFT.key(), "Header links");
		requestNode.put(Key.CENTER.key(), "Header Mitte");
		requestNode.put(Key.RIGHT.key(), "Header rechts");

		String response = Fsl.execute("Xls.setHeaders", requestNode.toString());
		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals("Header links",
				Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getHeader().getLeft());
		assertEquals("Header Mitte",
				Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getHeader().getCenter());
		assertEquals("Header rechts",
				Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getHeader().getRight());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.LEFT.key(), "Footer links");
		requestNode.put(Key.CENTER.key(), "Footer Mitte");
		requestNode.put(Key.RIGHT.key(), "Footer rechts");

		response = Fsl.execute("Xls.setFooters", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals("Footer links",
				Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getFooter().getLeft());
		assertEquals("Footer Mitte",
				Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getFooter().getCenter());
		assertEquals("Footer rechts",
				Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getFooter().getRight());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.WORKBOOK.key(), workbook);
		String result = Fsl.execute("Xls.saveWorkbook", requestNode.toString());
		JsonNode resultNode = MAPPER.readTree(result);
		assertEquals(Executor.OK, resultNode.get(Executor.STATUS).asText());
		assertTrue(new File(workbook).exists());
	}

	protected static void openExistingWorkbook(String path) throws EncryptedDocumentException, IOException
	{
		File file = new File(path);
		Workbook workbook = XSSFWorkbook.class.cast(WorkbookFactory.create(file));
		Xls.workbooks.put(path, workbook);
		Xls.activeWorkbook = workbook;
	}

	protected String prepareWorkbookIfMissing()
	{
		return prepareWorkbookIfMissing(WORKBOOK_1);
	}

	protected String prepareWorkbookIfMissing(String workbook)
	{
		Xls.activeWorkbook = new XSSFWorkbook();
		Xls.workbooks.put(workbook, Xls.activeWorkbook);
		return workbook;
	}

	protected String prepareWorkbookAndSheetIfMissing()
	{
		return prepareWorkbookAndSheetIfMissing(WORKBOOK_1, SHEET0);
	}

	protected String prepareWorkbookAndSheetIfMissing(String workbook, String sheetName)
	{
		workbook = prepareWorkbookIfMissing(workbook);
		Sheet sheet = Xls.activeWorkbook.createSheet(sheetName);
		Xls.activeWorkbook.setActiveSheet(Xls.activeWorkbook.getSheetIndex(sheet));
		return workbook;
	}

	protected void releaseAllWorkbooks()
	{
		Xls.workbooks.clear();
		Xls.activeWorkbook = null;
	}

}
