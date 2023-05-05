package ch.eugster.filemaker.fsl.plugin.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.formula.eval.FunctionEval;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
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
import com.fasterxml.jackson.databind.node.TextNode;

import ch.eugster.filemaker.fsl.plugin.Executor;
import ch.eugster.filemaker.fsl.plugin.Fsl;
import ch.eugster.filemaker.fsl.plugin.xls.Xls;

public class XlsTest extends Xls
{
	private static ObjectMapper MAPPER = new ObjectMapper();

	private static final String WORKBOOK_1 = "workbook1";

	@BeforeAll
	public static void beforeAll()
	{
//		File directory = new File("targets");
//		File[] targets = directory.listFiles();
//		for (File target : targets)
//		{
//			target.delete();
//		}
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
		assertEquals(WORKBOOK_1, responseNode.get(Key.WORKBOOK.key()).asText());
		assertNotNull(Xls.workbooks.get(WORKBOOK_1));
	}

	@Test
	public void testCreateWorkbookWithoutName() throws Exception
	{
		ObjectNode requestNode = MAPPER.createObjectNode();

		String response = Fsl.execute("Xls.createWorkbook", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals("missing_argument 'workbook'", responseNode.get(Executor.ERRORS).get(0).asText());
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
		assertEquals("workbook_already_exists", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testCreateAndActivateWorkbookWithoutName() throws Exception
	{
		ObjectNode requestNode = MAPPER.createObjectNode();

		String response = Fsl.execute("Xls.createAndActivateWorkbook", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(3, responseNode.get(Executor.ERRORS).size());
		assertEquals("missing_argument 'workbook'", responseNode.get(Executor.ERRORS).get(0).asText());
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
		assertEquals("workbook_already_exists", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testCreateSheet() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookIfMissing();

		String response = Fsl.execute("Xls.createSheet", "{}");

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(SHEET0, responseNode.get(Key.SHEET.key()).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testCreateSheetAlreadyExisting() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.SHEET.key(), "Sheet0");

		String response = Fsl.execute("Xls.createSheet", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals("sheet_already_exists 'sheet'", responseNode.get(Executor.ERRORS).get(0).asText());
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
		assertEquals(SHEET0, responseNode.get(Key.SHEET.key()).asText());
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
		assertEquals(SHEET0, responseNode.get(Key.SHEET.key()).asText());
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
		assertEquals("missing_sheet", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testActivateNotExistingSheet() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookIfMissing();
		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.SHEET.key(), SHEET0);

		String response = Fsl.execute("Xls.activateSheet", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals("missing_sheet", responseNode.get(Executor.ERRORS).get(0).asText());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.SHEET.key(), 0);

		response = Fsl.execute("Xls.activateSheet", requestNode.toString());

		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals("missing_sheet", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testActivateSheetWithoutWorkbook() throws JsonMappingException, JsonProcessingException
	{
		releaseWorkbook(MAPPER.createObjectNode(), MAPPER.createObjectNode());

		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put("name", SHEET0);

		String response = Fsl.execute("Xls.activateSheet", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals("missing_argument 'workbook'", responseNode.get(Executor.ERRORS).get(0).asText());

		requestNode = MAPPER.createObjectNode();
		requestNode.put("index", 0);

		response = Fsl.execute("Xls.activateSheet", requestNode.toString());

		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals("missing_argument 'workbook'", responseNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testSetCellsRight() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		CellAddress cellAddress = new CellAddress("B2");
		ObjectNode requestNode = MAPPER.createObjectNode();
		TextNode startNode = requestNode.textNode(cellAddress.formatAsString());
		requestNode.set(Key.CELL.key(), startNode);
		TextNode directionNode = requestNode.textNode(Direction.RIGHT.direction());
		requestNode.set("direction", directionNode);

		ArrayNode valuesNode = requestNode.arrayNode();
		valuesNode.add("Title");
		valuesNode.add(1);
		valuesNode.add(2);
		valuesNode.add(3);
		valuesNode.add(4);
		valuesNode.add("SUM(C2:F2)");
		requestNode.set("values", valuesNode);
		requestNode.put("path", "targets/test.xlsx");

		String response = Fsl.execute("Xls.setCells", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(10, Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(1).getCell(6)
				.getNumericCellValue(), 0);
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testSetCellsRightOneCell() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		CellAddress cellAddress = new CellAddress("B2");
		ObjectNode requestNode = MAPPER.createObjectNode();
		TextNode firstNode = requestNode.textNode(cellAddress.formatAsString());
		requestNode.set(Key.CELL.key(), firstNode);

		ArrayNode valuesNode = requestNode.arrayNode();
		valuesNode.add("Title");
		requestNode.set("values", valuesNode);
		requestNode.put("path", "targets/CellsRightOneCell.xlsx");

		String response = Fsl.execute("Xls.setCells", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals("Title", Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(1)
				.getCell(1).getStringCellValue());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testSetCellsLeft() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		CellAddress cellAddress = new CellAddress("G3");
		ObjectNode requestNode = MAPPER.createObjectNode();
		TextNode startNode = requestNode.textNode(cellAddress.formatAsString());
		requestNode.set(Key.CELL.key(), startNode);
		TextNode directionNode = requestNode.textNode("left");
		requestNode.set("direction", directionNode);

		ArrayNode valuesNode = requestNode.arrayNode();
		valuesNode.add("Title");
		valuesNode.add(1);
		valuesNode.add(2);
		valuesNode.add(3);
		valuesNode.add(4);
		valuesNode.add("SUM(C3:F3)");
		requestNode.set("values", valuesNode);
		requestNode.put("path", "targets/test.xlsx");

		String response = Fsl.execute("Xls.setCells", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(10, Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(2).getCell(1)
				.getNumericCellValue(), 0);
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testSetCellsUp() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		CellAddress cellAddress = new CellAddress("I30");
		ObjectNode requestNode = MAPPER.createObjectNode();
		TextNode startNode = requestNode.textNode(cellAddress.formatAsString());
		requestNode.set(Key.CELL.key(), startNode);
		TextNode directionNode = requestNode.textNode(Direction.UP.direction());
		requestNode.set("direction", directionNode);

		ArrayNode valuesNode = requestNode.arrayNode();
		valuesNode.add("Title");
		valuesNode.add(1);
		valuesNode.add(2);
		valuesNode.add(3);
		valuesNode.add(4);
		valuesNode.add("SUM(I26:I29)");
		requestNode.set("values", valuesNode);
		requestNode.put("path", "targets/test.xlsx");

		String response = Fsl.execute("Xls.setCells", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(10, Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(24).getCell(8)
				.getNumericCellValue(), 0);
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testSetCellsDown() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		CellAddress cellAddress = new CellAddress("K3");
		ObjectNode requestNode = MAPPER.createObjectNode();
		TextNode startNode = requestNode.textNode(cellAddress.formatAsString());
		requestNode.set(Key.CELL.key(), startNode);
		TextNode directionNode = requestNode.textNode(Direction.DOWN.direction());
		requestNode.set("direction", directionNode);

		ArrayNode valuesNode = requestNode.arrayNode();
		valuesNode.add("Title");
		valuesNode.add(1);
		valuesNode.add(2);
		valuesNode.add(3);
		valuesNode.add(4);
		valuesNode.add("SUM(K4:K7)");
		requestNode.set("values", valuesNode);
		requestNode.put("path", "targets/test.xlsx");

		String response = Fsl.execute("Xls.setCells", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(10, Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(7).getCell(10)
				.getNumericCellValue(), 0);
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testSetCellsWithAddressValues() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		CellAddress cellAddress = new CellAddress("K3");
		ObjectNode requestNode = MAPPER.createObjectNode();
		ObjectNode cellNode = requestNode.objectNode();
		cellNode.put(Key.ROW.key(), cellAddress.getRow());
		cellNode.put(Key.COL.key(), cellAddress.getColumn());
		requestNode.set(Key.CELL.key(), cellNode);
		TextNode directionNode = requestNode.textNode(Direction.DOWN.direction());
		requestNode.set("direction", directionNode);

		ArrayNode valuesNode = requestNode.arrayNode();
		valuesNode.add("Title");
		valuesNode.add(1);
		valuesNode.add(2);
		valuesNode.add(3);
		valuesNode.add(4);
		valuesNode.add("SUM(K4:K7)");
		requestNode.set("values", valuesNode);
		requestNode.put("path", "targets/test.xlsx");

		String response = Fsl.execute("Xls.setCells", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(10, Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(7).getCell(10)
				.getNumericCellValue(), 0);
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testSetCellsWithoutDirection() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		CellAddress cellAddress = new CellAddress("B2");
		ObjectNode requestNode = MAPPER.createObjectNode();
		TextNode startNode = requestNode.textNode(cellAddress.formatAsString());
		requestNode.set(Key.CELL.key(), startNode);

		ArrayNode valuesNode = requestNode.arrayNode();
		valuesNode.add("Title");
		valuesNode.add(1);
		valuesNode.add(2);
		valuesNode.add(3);
		valuesNode.add(4);
		valuesNode.add("SUM(C2:F2)");
		requestNode.set("values", valuesNode);
		requestNode.put("path", "targets/test.xlsx");

		String response = Fsl.execute("Xls.setCells", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(10, Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(1).getCell(6)
				.getNumericCellValue(), 0);
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testCopyFormula()
	{
		prepareWorkbookAndSheetIfMissing();
		Sheet sheet = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex());
		Row row = sheet.createRow(2);
		Cell cell = row.createCell(2);
		cell.setCellValue(1D);
		cell = row.createCell(3);
		cell.setCellValue(2D);
		cell = row.createCell(4);
		cell.setCellFormula("C3/D3");

		row = sheet.createRow(3);
		cell = row.createCell(2);
		cell.setCellValue(3D);
		cell = row.createCell(3);
		cell.setCellValue(4D);
		cell = row.createCell(4);
		cell.setCellFormula("$C4/D$4");

		row = sheet.createRow(4);
		cell = row.createCell(2);
		cell.setCellValue(5D);
		cell = row.createCell(3);
		cell.setCellValue(6D);
		cell = row.createCell(4);
		cell.setCellFormula("SUM(C3:D5)");

		row = sheet.createRow(5);
		cell = row.createCell(2);
		cell.setCellValue(7D);
		cell = row.createCell(3);
		cell.setCellValue(8D);
		cell = row.createCell(4);
		cell.setCellFormula("SUM(C$3/$D6)");

		row = sheet.createRow(6);
		cell = row.createCell(2);
		cell.setCellValue(9D);
		cell = row.createCell(3);
		cell.setCellValue(10D);
		cell = row.createCell(4);
		cell.setCellFormula("C3+SUM(C3:D7)");

		row = sheet.createRow(7);
		cell = row.createCell(2);
		cell.setCellValue(11D);
		cell = row.createCell(3);
		cell.setCellValue(12D);
		cell = row.createCell(4);
		cell.setCellFormula("C$3+SUM($C3:D$8)");

		for (Row r : sheet)
		{
			for (Cell c : r)
			{
				if (c.getCellType() == CellType.FORMULA)
				{
					CellAddress source = c.getAddress();
					String formula = c.getCellFormula();
					System.out.print(source + "=" + formula);
					int rowdiff = 3;
					int coldiff = -2;
					CellAddress target = new CellAddress(source.getRow() + rowdiff, source.getColumn() + coldiff);
					String newformula = copyFormula(sheet, formula, coldiff, rowdiff);
					System.out.println("->" + target + "=" + newformula);
				}
			}
		}
		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put("path", "./targets/test.xlsx");
		Fsl.execute("Xls.saveWorkbook", requestNode.toString());
	}

	@Test
	public void testCopySingleFormulaCellToSingleCell() throws IOException
	{
		prepareWorkbookAndSheetIfMissing();
		Sheet sheet = getActiveSheet();
		Row row0 = sheet.createRow(0);
		Cell cell = row0.createCell(0);
		cell.setCellValue(23.5);
		Row row1 = sheet.createRow(1);
		cell = row1.createCell(0);
		cell.setCellValue(76.5);
		Row row2 = sheet.createRow(2);
		cell = row2.createCell(0);
		cell.setCellFormula("SUM(A1:A2)");
		cell = row0.createCell(1);
		cell.setCellValue(12.5);
		cell = row1.createCell(1);
		cell.setCellValue(12.5);

		ObjectNode requestNode = MAPPER.createObjectNode();
		TextNode sourceNode = requestNode.textNode("A3");
		requestNode.set("source", sourceNode);

		TextNode targetNode = requestNode.textNode("B3");
		requestNode.set("target", targetNode);

		String response = Fsl.execute("Xls.copy", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(2).getCell(1);
		FormulaEvaluator formulaEval = Xls.activeWorkbook.getCreationHelper().createFormulaEvaluator();
		assertEquals(25D, formulaEval.evaluate(cell).getNumberValue(), 0D);

		requestNode.put("path", "./targets/test.xlsx");
		Fsl.execute("Xls.saveWorkbook", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
	}

	@Test
	public void testCopySingleFormulaCellToMultipleCells() throws IOException
	{
		prepareWorkbookAndSheetIfMissing();
		Sheet sheet = getActiveSheet();
		Row row0 = sheet.createRow(0);
		Cell cell = row0.createCell(0);
		cell.setCellValue(23.5);
		Row row1 = sheet.createRow(1);
		cell = row1.createCell(0);
		cell.setCellValue(76.5);
		Row row2 = sheet.createRow(2);
		cell = row2.createCell(0);
		cell.setCellFormula("SUM(A1:A2)");
		cell = row0.createCell(1);
		cell.setCellValue(12.5);
		cell = row1.createCell(1);
		cell.setCellValue(12.5);
		cell = row0.createCell(2);
		cell.setCellFormula("A3");
		cell = row1.createCell(2);
		cell.setCellFormula("B3");

		ObjectNode requestNode = MAPPER.createObjectNode();
		TextNode sourceNode = requestNode.textNode("A3");
		requestNode.set("source", sourceNode);

		TextNode targetNode = requestNode.textNode("B3:C3");
		requestNode.set("target", targetNode);

		String response = Fsl.execute("Xls.copy", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		FormulaEvaluator formulaEval = Xls.activeWorkbook.getCreationHelper().createFormulaEvaluator();
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(2).getCell(1);
		assertEquals(25D, formulaEval.evaluate(cell).getNumberValue(), 0D);
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(2).getCell(2);
		assertEquals(125D, formulaEval.evaluate(cell).getNumberValue(), 0D);

		requestNode.put("path", "./targets/test.xlsx");
		Fsl.execute("Xls.saveWorkbook", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
	}

	@Test
	public void testCopySingleFormulaCellToMultipleCellsWithAddresses() throws IOException
	{
		prepareWorkbookAndSheetIfMissing();
		Sheet sheet = getActiveSheet();
		Row row0 = sheet.createRow(0);
		Cell cell = row0.createCell(0);
		cell.setCellValue(23.5);
		Row row1 = sheet.createRow(1);
		cell = row1.createCell(0);
		cell.setCellValue(76.5);
		Row row2 = sheet.createRow(2);
		cell = row2.createCell(0);
		cell.setCellFormula("SUM(A1:A2)");
		cell = row0.createCell(1);
		cell.setCellValue(12.5);
		cell = row1.createCell(1);
		cell.setCellValue(12.5);
		cell = row0.createCell(2);
		cell.setCellFormula("A3");
		cell = row1.createCell(2);
		cell.setCellFormula("B3");

		ObjectNode requestNode = MAPPER.createObjectNode();
		ObjectNode sourceNode = requestNode.objectNode();
		sourceNode.put(Key.TOP_LEFT.key(), "A3");
		sourceNode.put(Key.BOTTOM_RIGHT.key(), "A3");
		requestNode.set("source", sourceNode);

		ObjectNode targetNode = requestNode.objectNode();
		targetNode.put(Key.TOP_LEFT.key(), "B3");
		targetNode.put(Key.BOTTOM_RIGHT.key(), "C3");
		requestNode.set("target", targetNode);

		String response = Fsl.execute("Xls.copy", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		FormulaEvaluator formulaEval = Xls.activeWorkbook.getCreationHelper().createFormulaEvaluator();
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(2).getCell(1);
		assertEquals(25D, formulaEval.evaluate(cell).getNumberValue(), 0D);
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(2).getCell(2);
		assertEquals(125D, formulaEval.evaluate(cell).getNumberValue(), 0D);

		requestNode.put("path", "./targets/test.xlsx");
		Fsl.execute("Xls.saveWorkbook", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
	}

	@Test
	public void testCopySingleFormulaCellToMultipleCellsWithInts() throws IOException
	{
		prepareWorkbookAndSheetIfMissing();
		Sheet sheet = getActiveSheet();
		Row row0 = sheet.createRow(0);
		Cell cell = row0.createCell(0);
		cell.setCellValue(23.5);
		Row row1 = sheet.createRow(1);
		cell = row1.createCell(0);
		cell.setCellValue(76.5);
		Row row2 = sheet.createRow(2);
		cell = row2.createCell(0);
		cell.setCellFormula("SUM(A1:A2)");
		cell = row0.createCell(1);
		cell.setCellValue(12.5);
		cell = row1.createCell(1);
		cell.setCellValue(12.5);
		cell = row0.createCell(2);
		cell.setCellFormula("A3");
		cell = row1.createCell(2);
		cell.setCellFormula("B3");

		ObjectNode requestNode = MAPPER.createObjectNode();
		ObjectNode sourceNode = requestNode.objectNode();
		sourceNode.put(Key.TOP.key(), 2);
		sourceNode.put(Key.LEFT.key(), 0);
		sourceNode.put(Key.BOTTOM.key(), 2);
		sourceNode.put(Key.RIGHT.key(), 0);
		requestNode.set("source", sourceNode);

		ObjectNode targetNode = requestNode.objectNode();
		targetNode.put(Key.TOP.key(), 2);
		targetNode.put(Key.LEFT.key(), 1);
		targetNode.put(Key.BOTTOM.key(), 2);
		targetNode.put(Key.RIGHT.key(), 2);
		requestNode.set("target", targetNode);

		String response = Fsl.execute("Xls.copy", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		FormulaEvaluator formulaEval = Xls.activeWorkbook.getCreationHelper().createFormulaEvaluator();
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(2).getCell(1);
		assertEquals(25D, formulaEval.evaluate(cell).getNumberValue(), 0D);
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(2).getCell(2);
		assertEquals(125D, formulaEval.evaluate(cell).getNumberValue(), 0D);

		requestNode.put("path", "./targets/test.xlsx");
		Fsl.execute("Xls.saveWorkbook", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
	}

	@Test
	public void testSupportedFormulaNames()
	{
		for (String supportedFormulaName : FunctionEval.getNotSupportedFunctionNames())
		{
			System.out.println(supportedFormulaName);
		}
	}

	@Test
	public void testFunctionSupported()
	{
		String function = "SUM(A1:B2)";
		int pos = function.indexOf("(");
		if (pos > -1)
		{
			String name = function.substring(0, pos);
			for (String supportedFormulaName : FunctionEval.getSupportedFunctionNames())
			{
				if (supportedFormulaName.equals(name))
				{
					assertTrue(true);
					return;
				}
			}
		}
		assertFalse(true);
	}

	@Test
	public void testSave() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookIfMissing();
		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.WORKBOOK.key(), WORKBOOK_1);
		requestNode.put(Key.PATH_NAME.key(), "./targets/test.xlsx");
		String result = Fsl.execute("Xls.saveWorkbook", requestNode.toString());
		JsonNode resultNode = MAPPER.readTree(result);
		assertEquals(Executor.OK, resultNode.get(Executor.STATUS).asText());
		assertTrue(new File("./targets/test.xlsx").exists());
	}

	@Test
	public void testSetHeaderFooter() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
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

		testSave();
	}

	@Test
	public void testApplyFontStylesToCell() throws EncryptedDocumentException, IOException
	{
		openAndActivateWorkbook("./resources/xls/applyFontStyles.xlsx");

		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.STYLE.key(), 0);
		requestNode.put(Key.CELL.key(), "A1");
		String response = Fsl.execute("Xls.applyFontStyle", requestNode.toString());
		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		Font font = Xls.activeWorkbook.getFontAt(Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(0).getCell(0).getCellStyle().getFontIndex());
		assertEquals(false, font.getBold());
		assertEquals(false, font.getItalic());
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.STYLE.key(), 1);
		requestNode.put(Key.CELL.key(), "B2");
		response = Fsl.execute("Xls.applyFontStyle", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		font = Xls.activeWorkbook.getFontAt(Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(1).getCell(1).getCellStyle().getFontIndex());
		assertEquals(true, font.getBold());
		assertEquals(false, font.getItalic());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.STYLE.key(), 2);
		requestNode.put(Key.CELL.key(), "C3");
		response = Fsl.execute("Xls.applyFontStyle", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		font = Xls.activeWorkbook.getFontAt(Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(2).getCell(2).getCellStyle().getFontIndex());
		assertEquals(false, font.getBold());
		assertEquals(true, font.getItalic());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.STYLE.key(), 3);
		requestNode.put(Key.CELL.key(), "D4");
		response = Fsl.execute("Xls.applyFontStyle", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		font = Xls.activeWorkbook.getFontAt(Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(3).getCell(3).getCellStyle().getFontIndex());
		assertEquals(true, font.getBold());
		assertEquals(true, font.getItalic());
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.WORKBOOK.key(), "applyFontStyles.xlsx");
		requestNode.put(Key.PATH_NAME.key(), "./targets/applyFontStyles.xlsx");
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
	}

	@Test
	public void testApplyFontStylesToRange() throws EncryptedDocumentException, IOException
	{
		openAndActivateWorkbook("./resources/xls/applyFontStylesRange.xlsx");

		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.STYLE.key(), 0);
		requestNode.put(Key.RANGE.key(), "A1:D1");
		
		String response = Fsl.execute("Xls.applyFontStyle", requestNode.toString());
		
		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		
		Font font = Xls.activeWorkbook.getFontAt(Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(0).getCell(0).getCellStyle().getFontIndex());
		assertEquals(false, font.getBold());
		assertEquals(false, font.getItalic());
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.STYLE.key(), 1);
		requestNode.put(Key.RANGE.key(), "A2:D2");
		
		response = Fsl.execute("Xls.applyFontStyle", requestNode.toString());
		
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		font = Xls.activeWorkbook.getFontAt(Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(1).getCell(0).getCellStyle().getFontIndex());
		assertEquals(true, font.getBold());
		assertEquals(false, font.getItalic());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.STYLE.key(), 2);
		requestNode.put(Key.RANGE.key(), "A3:D3");
		
		response = Fsl.execute("Xls.applyFontStyle", requestNode.toString());
		
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		font = Xls.activeWorkbook.getFontAt(Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(2).getCell(0).getCellStyle().getFontIndex());
		assertEquals(false, font.getBold());
		assertEquals(true, font.getItalic());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.STYLE.key(), 3);
		requestNode.put(Key.RANGE.key(), "A4:D4");
		
		response = Fsl.execute("Xls.applyFontStyle", requestNode.toString());
		
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		font = Xls.activeWorkbook.getFontAt(Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(3).getCell(0).getCellStyle().getFontIndex());
		assertEquals(true, font.getBold());
		assertEquals(true, font.getItalic());
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.WORKBOOK.key(), "applyFontStylesRange.xlsx");
		requestNode.put(Key.PATH_NAME.key(), "./targets/applyFontStylesRange.xlsx");
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
	}
	
	@Test
	public void testApplyNumberFormats() throws EncryptedDocumentException, IOException
	{
		openAndActivateWorkbook("./resources/xls/applyNumberFormats.xlsx");
		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.FORMAT.key(), 2);
		requestNode.put(Key.RANGE.key(), "A1:J1");

		String response = Fsl.execute("Xls.applyNumberFormat", requestNode.toString());
		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		
		DataFormatter df = new DataFormatter();
		Cell cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(0).getCell(0);
		assertEquals("123.46", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(0).getCell(1);
		assertEquals("0.00", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(0).getCell(2);
		assertEquals("0.01", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(0).getCell(3);
		assertEquals("100.00", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(0).getCell(4);
		assertEquals("1234567.89", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(0).getCell(5);
		assertEquals("-123.46", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(0).getCell(6);
		assertEquals("-0.00", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(0).getCell(7);
		assertEquals("-0.01", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(0).getCell(8);
		assertEquals("-100.00", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(0).getCell(9);
		assertEquals("-1234567.89", df.formatCellValue(cell));
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.FORMAT.key(), "0.00");
		requestNode.put(Key.RANGE.key(), "A2:J2");
		
		response = Fsl.execute("Xls.applyNumberFormat", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(1).getCell(0);
		assertEquals("123.46", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(1).getCell(1);
		assertEquals("0.00", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(1).getCell(2);
		assertEquals("0.01", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(1).getCell(3);
		assertEquals("100.00", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(1).getCell(4);
		assertEquals("1234567.89", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(1).getCell(5);
		assertEquals("-123.46", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(1).getCell(6);
		assertEquals("-0.00", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(1).getCell(7);
		assertEquals("-0.01", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(1).getCell(8);
		assertEquals("-100.00", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(1).getCell(9);
		assertEquals("-1234567.89", df.formatCellValue(cell));

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.FORMAT.key(), 4);
		requestNode.put(Key.RANGE.key(), "A3:J3");

		response = Fsl.execute("Xls.applyNumberFormat", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());

		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(2).getCell(0);
		assertEquals("123.46", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(2).getCell(1);
		assertEquals("0.00", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(2).getCell(2);
		assertEquals("0.01", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(2).getCell(3);
		assertEquals("100.00", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(2).getCell(4);
		assertEquals("1,234,567.89", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(2).getCell(5);
		assertEquals("-123.46", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(2).getCell(6);
		assertEquals("-0.00", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(2).getCell(7);
		assertEquals("-0.01", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(2).getCell(8);
		assertEquals("-100.00", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(2).getCell(9);
		assertEquals("-1,234,567.89", df.formatCellValue(cell));

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.FORMAT.key(), "#,##0.00");
		requestNode.put(Key.RANGE.key(), "A4:J4");

		response = Fsl.execute("Xls.applyNumberFormat", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());

		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(3).getCell(0);
		assertEquals("123.46", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(3).getCell(1);
		assertEquals("0.00", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(3).getCell(2);
		assertEquals("0.01", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(3).getCell(3);
		assertEquals("100.00", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(3).getCell(4);
		assertEquals("1,234,567.89", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(3).getCell(5);
		assertEquals("-123.46", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(3).getCell(6);
		assertEquals("-0.00", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(3).getCell(7);
		assertEquals("-0.01", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(3).getCell(8);
		assertEquals("-100.00", df.formatCellValue(cell));
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(3).getCell(9);
		assertEquals("-1,234,567.89", df.formatCellValue(cell));
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.WORKBOOK.key(), "applyNumberFormats.xlsx");
		requestNode.put(Key.PATH_NAME.key(), "./targets/applyNumberFormats.xlsx");
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
	}
	
	@Test
	public void testAutoSizeColumn() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing("autoSizeColumn.xlsx", SHEET0);
		Sheet sheet = getActiveSheet();
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue(new XSSFRichTextString("Das ist eine Testzelle"));

		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "A1");
		
		String response = Fsl.execute("Xls.autoSizeColumns", requestNode.toString());
		
		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.WORKBOOK.key(), "autoSizeColumn.xlsx");
		requestNode.put(Key.PATH_NAME.key(), "./targets/autoSizeColumn.xlsx");
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
	}

	@Test
	public void testRotation() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing("rotate.xlsx", SHEET0);
		Sheet sheet = getActiveSheet();
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue(new XSSFRichTextString("Das ist eine Testzelle"));

		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.ROTATION.key(), 90);
		requestNode.put(Key.CELL.key(), "A1");
		
		String response = Fsl.execute("Xls.rotateCells", requestNode.toString());
		
		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.WORKBOOK.key(), "rotate.xlsx");
		requestNode.put(Key.PATH_NAME.key(), "./targets/rotate.xlsx");
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
	}
	
	@Test
	public void testHorizontalAlignment() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing("horizontalAlign.xlsx", SHEET0);
		Sheet sheet = getActiveSheet();
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue(new XSSFRichTextString("X"));
		cell = row.createCell(1);
		cell.setCellValue(new XSSFRichTextString("Y"));
		cell = row.createCell(2);
		cell.setCellValue(new XSSFRichTextString("Z"));

		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.ALIGNMENT.key(), Key.LEFT.key());
		requestNode.put(Key.CELL.key(), "A1");
		
		String response = Fsl.execute("Xls.alignHorizontally", requestNode.toString());
		
		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.ALIGNMENT.key(), Key.CENTER.key());
		requestNode.put(Key.CELL.key(), "B1");
		
		response = Fsl.execute("Xls.alignHorizontally", requestNode.toString());
		
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.ALIGNMENT.key(), Key.RIGHT.key());
		requestNode.put(Key.CELL.key(), "C1");
		
		response = Fsl.execute("Xls.alignHorizontally", requestNode.toString());
		
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.WORKBOOK.key(), "horizontalAlign.xlsx");
		requestNode.put(Key.PATH_NAME.key(), "./targets/horizontalAlign.xlsx");
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
	}
	
	@Test
	public void testBookkeeperReport() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.WORKBOOK.key(), "BookKeeperReport.xlsx");
		String response = Fsl.execute("Xls.createAndActivateWorkbook", requestNode.toString());
		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.SHEET.key(), SHEET0);
		response = Fsl.execute("Xls.createAndActivateSheetByName", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.LEFT.key(), "Buchhaltungsreport");
		requestNode.put(Key.CENTER.key(), "Ebneter Transporte");
		requestNode.put(Key.RIGHT.key(), SimpleDateFormat.getDateInstance().format(new Date()));
		response = Fsl.execute("Xls.setHeaders", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "A1");
		ArrayNode valuesNode = requestNode.arrayNode();
		valuesNode.add("Typ");
		valuesNode.add("Rechnung");
		valuesNode.add("Datum");
		valuesNode.add("Betrag");
		valuesNode.add("MwSt");
		valuesNode.add("Bezahlt");
		valuesNode.add("Datum");
		valuesNode.add("Offen");
		requestNode.set("values", valuesNode);
		requestNode.put(Key.DIRECTION.key(), Key.RIGHT.key());
		response = Fsl.execute("Xls.setCells", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.RANGE.key(), "A1:H1");
		requestNode.put(Key.STYLE.key(), FontStyle.BOLD.ordinal());
		response = Fsl.execute("Xls.applyFontStyle", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "A2");
		valuesNode = requestNode.arrayNode();
		valuesNode.add("Transport");
		valuesNode.add("R-00030");
		valuesNode.add("23.04.2022");
		valuesNode.add(2345.25);
		valuesNode.add(345.25);
		valuesNode.add(2345.25);
		valuesNode.add("31.04.2022");
		valuesNode.add("D2-F2");
		requestNode.set("values", valuesNode);
		response = Fsl.execute("Xls.setCells", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "A3");
		valuesNode = requestNode.arrayNode();
		valuesNode.add("Car");
		valuesNode.add("R-00032");
		valuesNode.add("24.04.2022");
		valuesNode.add(590.75);
		valuesNode.add(40);
		valuesNode.add(500);
		valuesNode.add("31.04.2022");
		valuesNode.add("D3-F3");
		requestNode.set("values", valuesNode);
		response = Fsl.execute("Xls.setCells", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "A4");
		valuesNode = requestNode.arrayNode();
		valuesNode.add("Total");
		valuesNode.add((JsonNode) null);
		valuesNode.add("");
		valuesNode.add("SUM(D2:D3)");
		valuesNode.add("SUM(E2:E3)");
		valuesNode.add("SUM(F2:F3)");
		valuesNode.add((JsonNode) null);
		valuesNode.add("SUM(H2:H3)");
		requestNode.set("values", valuesNode);
		response = Fsl.execute("Xls.setCells", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.RANGE.key(), "D2:F4");
		requestNode.put(Key.FORMAT.key(), 4);
		response = Fsl.execute("Xls.applyNumberFormat", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.RANGE.key(), "H2:H4");
		requestNode.put(Key.FORMAT.key(), 4);
		response = Fsl.execute("Xls.applyNumberFormat", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.RANGE.key(), "A4:H4");
		requestNode.put(Key.STYLE.key(), FontStyle.BOLD.ordinal());
		response = Fsl.execute("Xls.applyFontStyle", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.RANGE.key(), "A1:H4");
		response = Fsl.execute("Xls.autoSizeColumns", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.WORKBOOK.key(), "BookKeeperReport.xlsx");
		requestNode.put(Key.PATH_NAME.key(), "./targets/BookKeeperReport.xlsx");
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
	}

	private static void openAndActivateWorkbook(String path) throws EncryptedDocumentException, IOException
	{
		File file = new File(path);
		Workbook workbook = XSSFWorkbook.class.cast(WorkbookFactory.create(file));
		Xls.workbooks.put(file.getName(), workbook);
		Xls.activeWorkbook = workbook;
	}

	private static void prepareWorkbookIfMissing()
	{
		if (Objects.isNull(Xls.activeWorkbook))
		{
			Xls.activeWorkbook = new XSSFWorkbook();
			Xls.workbooks.put(WORKBOOK_1, Xls.activeWorkbook);
		}
	}

	private static void prepareWorkbookIfMissing(String workbook)
	{
		if (Objects.isNull(Xls.activeWorkbook))
		{
			Xls.activeWorkbook = new XSSFWorkbook();
			Xls.workbooks.put(workbook, Xls.activeWorkbook);
		}
	}

	private static void prepareWorkbookAndSheetIfMissing()
	{
		prepareWorkbookIfMissing();
		if (Xls.activeWorkbook.getSheetIndex("Sheet0") == -1)
		{
			Sheet sheet = Xls.activeWorkbook.createSheet("Sheet0");
			Xls.activeWorkbook.setActiveSheet(Xls.activeWorkbook.getSheetIndex(sheet));
		}
	}

	private static void prepareWorkbookAndSheetIfMissing(String workbookname, String sheetname)
	{
		prepareWorkbookIfMissing(workbookname);
		if (Xls.activeWorkbook.getSheetIndex(sheetname) == -1)
		{
			Sheet sheet = Xls.activeWorkbook.createSheet(sheetname);
			Xls.activeWorkbook.setActiveSheet(Xls.activeWorkbook.getSheetIndex(sheet));
		}
	}

	private static void releaseAllWorkbooks()
	{
		Xls.workbooks.clear();
		Xls.activeWorkbook = null;
	}

}
