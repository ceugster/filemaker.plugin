package ch.eugster.filemaker.fsl.plugin.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.formula.eval.FunctionEval;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.PrintOrientation;
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

	private static final String WORKBOOK_1 = "./targets/workbook1.xlsx";

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
		assertNull(responseNode.get(Executor.ERRORS));
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
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
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
		assertEquals("missing_sheet 'Schmock'", responseNode.get(Executor.ERRORS).get(0).asText());

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
		assertEquals(7, responseNode.get(Key.COL.key()).asInt());
		assertEquals(1, responseNode.get(Key.ROW.key()).asInt());
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
		assertEquals(2, responseNode.get(Key.COL.key()).asInt());
		assertEquals(1, responseNode.get(Key.ROW.key()).asInt());
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
		assertEquals(0, responseNode.get(Key.COL.key()).asInt());
		assertEquals(2, responseNode.get(Key.ROW.key()).asInt());
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
		assertEquals(8, responseNode.get(Key.COL.key()).asInt());
		assertEquals(23, responseNode.get(Key.ROW.key()).asInt());
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
		assertEquals(10, responseNode.get(Key.COL.key()).asInt());
		assertEquals(8, responseNode.get(Key.ROW.key()).asInt());
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
		assertEquals(10, responseNode.get(Key.COL.key()).asInt());
		assertEquals(8, responseNode.get(Key.ROW.key()).asInt());
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
		assertEquals(7, responseNode.get(Key.COL.key()).asInt());
		assertEquals(1, responseNode.get(Key.ROW.key()).asInt());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testCopyFormula()
	{
		String workbook = "./targets/copyFormula.xlsx";
		prepareWorkbookAndSheetIfMissing(workbook, SHEET0);
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
		requestNode.put(Key.WORKBOOK.key(), workbook);
		Fsl.execute("Xls.saveWorkbook", requestNode.toString());
	}

	@Test
	public void testCopySingleFormulaCellToSingleCell() throws IOException
	{
		String workbook = "./targets/CopySingleFormulaCell.xlsx";
		prepareWorkbookAndSheetIfMissing(workbook, SHEET0);
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
		requestNode.set(Key.SOURCE.key(), sourceNode);

		TextNode targetNode = requestNode.textNode("B3");
		requestNode.set(Key.TARGET.key(), targetNode);

		String response = Fsl.execute("Xls.copy", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(2).getCell(1);
		FormulaEvaluator formulaEval = Xls.activeWorkbook.getCreationHelper().createFormulaEvaluator();
		assertEquals(25D, formulaEval.evaluate(cell).getNumberValue(), 0D);

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.WORKBOOK.key(), workbook);
		Fsl.execute("Xls.saveWorkbook", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
	}

	@Test
	public void testCopySingleFormulaCellToMultipleCells() throws IOException
	{
		String workbook = "./targets/CopySingleCellMultipleCells.xlsx";
		prepareWorkbookAndSheetIfMissing(workbook, SHEET0);
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
		cell = row1.createCell(2);
		cell.setCellFormula("A3");
		cell = row1.createCell(2);
		cell.setCellFormula("B3");

		ObjectNode requestNode = MAPPER.createObjectNode();
		TextNode sourceNode = requestNode.textNode("A3");
		requestNode.set(Key.SOURCE.key(), sourceNode);

		TextNode targetNode = requestNode.textNode("B3:C3");
		requestNode.set(Key.TARGET.key(), targetNode);

		String response = Fsl.execute("Xls.copy", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		FormulaEvaluator formulaEval = Xls.activeWorkbook.getCreationHelper().createFormulaEvaluator();
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(2).getCell(1);
		assertEquals(25D, formulaEval.evaluate(cell).getNumberValue(), 0D);
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(2).getCell(2);
		assertEquals(25D, formulaEval.evaluate(cell).getNumberValue(), 0D);

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.WORKBOOK.key(), workbook);
		Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
	}

	@Test
	public void testCopySingleFormulaCellToMultipleCellsWithAddresses() throws IOException
	{
		String workbook = "./targets/testCopySingleFormulaCellToMultipleCellsWithAddresses.xlsx";
		prepareWorkbookAndSheetIfMissing(workbook, SHEET0);
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
		requestNode.set(Key.SOURCE.key(), sourceNode);

		ObjectNode targetNode = requestNode.objectNode();
		targetNode.put(Key.TOP_LEFT.key(), "B3");
		targetNode.put(Key.BOTTOM_RIGHT.key(), "C3");
		requestNode.set(Key.TARGET.key(), targetNode);

		String response = Fsl.execute("Xls.copy", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		FormulaEvaluator formulaEval = Xls.activeWorkbook.getCreationHelper().createFormulaEvaluator();
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(2).getCell(1);
		assertEquals(25D, formulaEval.evaluate(cell).getNumberValue(), 0D);
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(2).getCell(2);
		assertEquals(125D, formulaEval.evaluate(cell).getNumberValue(), 0D);

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.WORKBOOK.key(), workbook);
		Fsl.execute("Xls.saveWorkbook", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
	}

	@Test
	public void testCopySingleFormulaCellToMultipleCellsWithInts() throws IOException
	{
		String workbook = "./targets/testCopySingleFormulaCellToMultipleCellsWithInts.xlsx";
		prepareWorkbookAndSheetIfMissing(workbook, SHEET0);
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
		requestNode.set(Key.SOURCE.key(), sourceNode);

		ObjectNode targetNode = requestNode.objectNode();
		targetNode.put(Key.TOP.key(), 2);
		targetNode.put(Key.LEFT.key(), 1);
		targetNode.put(Key.BOTTOM.key(), 2);
		targetNode.put(Key.RIGHT.key(), 2);
		requestNode.set(Key.TARGET.key(), targetNode);

		String response = Fsl.execute("Xls.copy", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		FormulaEvaluator formulaEval = Xls.activeWorkbook.getCreationHelper().createFormulaEvaluator();
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(2).getCell(1);
		assertEquals(25D, formulaEval.evaluate(cell).getNumberValue(), 0D);
		cell = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(2).getCell(2);
		assertEquals(125D, formulaEval.evaluate(cell).getNumberValue(), 0D);

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.WORKBOOK.key(), workbook);
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

	@Test
	public void testApplyFontStylesToCell() throws EncryptedDocumentException, IOException
	{
		openAndActivateWorkbook("./resources/xls/applyFontStyles.xlsx");

		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.FORMAT.key(), "Arial");
		requestNode.put(Key.STYLE.key(), 0);
		requestNode.put(Key.SIZE.key(), 14);
		ArrayNode arrayNode = requestNode.arrayNode();
		arrayNode.add(254);
		arrayNode.add(0);
		arrayNode.add(0);
		requestNode.set(Key.COLOR.key(), arrayNode);
		requestNode.put(Key.CELL.key(), "A1");
		String response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		Font font = Xls.activeWorkbook.getFontAt(Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex()).getRow(0).getCell(0).getCellStyle().getFontIndex());
		assertEquals(false, font.getBold());
		assertEquals(false, font.getItalic());
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.STYLE.key(), 1);
		requestNode.put(Key.CELL.key(), "B2");
		response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		Sheet sheet = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex());
		font = Xls.activeWorkbook.getFontAt(sheet.getRow(1).getCell(1).getCellStyle().getFontIndex());
//		assertEquals(true, font.getBold());
//		assertEquals(false, font.getItalic());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.STYLE.key(), 2);
		requestNode.put(Key.CELL.key(), "C3");
		response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		font = Xls.activeWorkbook.getFontAt(sheet.getRow(2).getCell(2).getCellStyle().getFontIndex());
//		assertEquals(false, font.getBold());
//		assertEquals(true, font.getItalic());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.STYLE.key(), 3);
		requestNode.put(Key.CELL.key(), "D4");
		response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		font = Xls.activeWorkbook.getFontAt(sheet.getRow(3).getCell(3).getCellStyle().getFontIndex());
//		assertEquals(true, font.getBold());
//		assertEquals(true, font.getItalic());
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.WORKBOOK.key(), "./targets/applyFontStyles.xlsx");
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
	}

	@Test
	public void testApplyFontStyleBold() throws EncryptedDocumentException, IOException
	{
		openAndActivateWorkbook("./resources/xls/applyFontStyles.xlsx");

		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.BOLD.key(), 1);
		requestNode.put(Key.RANGE.key(), "A1:D1");
		
		String response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		
		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(2, Xls.activeWorkbook.getNumberOfFonts());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.TARGET.key(), "./targets/applyFontStylesBold.xlsx");
		response = Fsl.execute("Xls.saveWorkbook", requestNode.toString());

		Sheet sheet = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex());
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Font font = Xls.activeWorkbook.getFontAt(sheet.getRow(i).getCell(j).getCellStyle().getFontIndex());
				assertEquals(i == 0 ? true : false, font.getBold());

				assertEquals(false, font.getItalic());
				assertEquals(Font.U_NONE, font.getUnderline());
				assertEquals(0, font.getColor());
				assertEquals(12, font.getFontHeightInPoints());
				assertEquals("Calibri", font.getFontName());
				assertEquals(false, font.getStrikeout());
				assertEquals(Font.SS_NONE, font.getTypeOffset());
			}			
		}
	}
	
	@Test
	public void testApplyFontStyleBoldError() throws EncryptedDocumentException, IOException
	{
		openAndActivateWorkbook("./resources/xls/applyFontStyles.xlsx");

		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.BOLD.key(), "A");
		requestNode.put(Key.RANGE.key(), "A1:D1");
		
		String response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		
		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(1, Xls.activeWorkbook.getNumberOfFonts());

		Sheet sheet = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex());
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Font font = Xls.activeWorkbook.getFontAt(sheet.getRow(i).getCell(j).getCellStyle().getFontIndex());
				assertEquals(false, font.getBold());
				assertEquals(false, font.getItalic());
				assertEquals(Font.U_NONE, font.getUnderline());
				assertEquals(0, font.getColor());
				assertEquals(12, font.getFontHeightInPoints());
				assertEquals("Calibri", font.getFontName());
				assertEquals(false, font.getStrikeout());
				assertEquals(Font.SS_NONE, font.getTypeOffset());
			}			
		}
	}
	
	@Test
	public void testApplyFontStyleItalic() throws EncryptedDocumentException, IOException
	{
		openAndActivateWorkbook("./resources/xls/applyFontStyles.xlsx");

		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.ITALIC.key(), 1);
		requestNode.put(Key.RANGE.key(), "A1:D1");
		
		String response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		
		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(2, Xls.activeWorkbook.getNumberOfFonts());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.TARGET.key(), "./targets/applyFontStylesItalic.xlsx");
		response = Fsl.execute("Xls.saveWorkbook", requestNode.toString());

		Sheet sheet = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex());
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Font font = Xls.activeWorkbook.getFontAt(sheet.getRow(i).getCell(j).getCellStyle().getFontIndex());
				assertEquals(i == 0 ? true : false, font.getItalic());

				assertEquals(false, font.getBold());
				assertEquals(Font.U_NONE, font.getUnderline());
				assertEquals(0, font.getColor());
				assertEquals(12, font.getFontHeightInPoints());
				assertEquals("Calibri", font.getFontName());
				assertEquals(false, font.getStrikeout());
				assertEquals(Font.SS_NONE, font.getTypeOffset());
			}			
		}
	}
	
	@Test
	public void testApplyFontStyleUnderline() throws EncryptedDocumentException, IOException
	{
		openAndActivateWorkbook("./resources/xls/applyFontStyles.xlsx");

		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.UNDERLINE.key(), Font.U_DOUBLE);
		requestNode.put(Key.RANGE.key(), "A1:D1");
		
		String response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		
		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(2, Xls.activeWorkbook.getNumberOfFonts());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.TARGET.key(), "./targets/applyFontStylesUnderline.xlsx");
		response = Fsl.execute("Xls.saveWorkbook", requestNode.toString());

		Sheet sheet = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex());
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Font font = Xls.activeWorkbook.getFontAt(sheet.getRow(i).getCell(j).getCellStyle().getFontIndex());
				assertEquals(i == 0 ? Font.U_DOUBLE : Font.U_NONE, font.getUnderline());

				assertEquals(false, font.getBold());
				assertEquals(false, font.getItalic());
				assertEquals(0, font.getColor());
				assertEquals(12, font.getFontHeightInPoints());
				assertEquals("Calibri", font.getFontName());
				assertEquals(false, font.getStrikeout());
				assertEquals(Font.SS_NONE, font.getTypeOffset());
			}			
		}
	}
	
	@Test
	public void testApplyFontStyleColor() throws EncryptedDocumentException, IOException
	{
		openAndActivateWorkbook("./resources/xls/applyFontStyles.xlsx");

		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.COLOR.key(), Font.COLOR_RED);
		requestNode.put(Key.RANGE.key(), "A1:D1");
		
		String response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		
		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(2, Xls.activeWorkbook.getNumberOfFonts());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.TARGET.key(), "./targets/applyFontStylesColor.xlsx");
		response = Fsl.execute("Xls.saveWorkbook", requestNode.toString());

		Sheet sheet = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex());
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Font font = Xls.activeWorkbook.getFontAt(sheet.getRow(i).getCell(j).getCellStyle().getFontIndex());
				assertEquals(i == 0 ? Font.COLOR_RED: 0, font.getColor());

				assertEquals(false, font.getBold());
				assertEquals(false, font.getItalic());
				assertEquals(Font.U_NONE, font.getUnderline());
				assertEquals(12, font.getFontHeightInPoints());
				assertEquals("Calibri", font.getFontName());
				assertEquals(false, font.getStrikeout());
				assertEquals(Font.SS_NONE, font.getTypeOffset());
			}			
		}
	}
	
	@Test
	public void testSetPrintSetup() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();

		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.ORIENTATION.key(), PrintOrientation.LANDSCAPE.name().toLowerCase());
		requestNode.put(Key.COPIES.key(), 2);
		
		String response = Fsl.execute("Xls.setPrintSetup", requestNode.toString());
		
		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.TARGET.key(), "./targets/printSetup.xlsx");
		response = Fsl.execute("Xls.saveWorkbook", requestNode.toString());
	}
	
	@Test
	public void testApplyFontStyleSize() throws EncryptedDocumentException, IOException
	{
		openAndActivateWorkbook("./resources/xls/applyFontStyles.xlsx");

		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.SIZE.key(), 30);
		requestNode.put(Key.RANGE.key(), "A1:D1");
		
		String response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		
		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(2, Xls.activeWorkbook.getNumberOfFonts());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.TARGET.key(), "./targets/applyFontStylesSize.xlsx");
		response = Fsl.execute("Xls.saveWorkbook", requestNode.toString());

		Sheet sheet = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex());
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Font font = Xls.activeWorkbook.getFontAt(sheet.getRow(i).getCell(j).getCellStyle().getFontIndex());
				assertEquals(i == 0 ? 30 : 12, font.getFontHeightInPoints());

				assertEquals(false, font.getBold());
				assertEquals(false, font.getItalic());
				assertEquals(Font.U_NONE, font.getUnderline());
				assertEquals(0, font.getColor());
				assertEquals("Calibri", font.getFontName());
				assertEquals(false, font.getStrikeout());
				assertEquals(Font.SS_NONE, font.getTypeOffset());
			}			
		}
	}
	
	@Test
	public void testApplyFontStyleName() throws EncryptedDocumentException, IOException
	{
		openAndActivateWorkbook("./resources/xls/applyFontStyles.xlsx");

		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.NAME.key(), "Courier new");
		requestNode.put(Key.RANGE.key(), "A1:D1");
		
		String response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		
		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(2, Xls.activeWorkbook.getNumberOfFonts());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.TARGET.key(), "./targets/applyFontStylesName.xlsx");
		response = Fsl.execute("Xls.saveWorkbook", requestNode.toString());

		Sheet sheet = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex());
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Font font = Xls.activeWorkbook.getFontAt(sheet.getRow(i).getCell(j).getCellStyle().getFontIndex());
				assertEquals(i == 0 ? "Courier new" : "Calibri", font.getFontName());

				assertEquals(false, font.getBold());
				assertEquals(false, font.getItalic());
				assertEquals(Font.U_NONE, font.getUnderline());
				assertEquals(0, font.getColor());
				assertEquals(12, font.getFontHeightInPoints());
				assertEquals(false, font.getStrikeout());
				assertEquals(Font.SS_NONE, font.getTypeOffset());
			}			
		}
	}
	
	@Test
	public void testApplyFontStyleStrikeOut() throws EncryptedDocumentException, IOException
	{
		openAndActivateWorkbook("./resources/xls/applyFontStyles.xlsx");

		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.STRIKE_OUT.key(), 1);
		requestNode.put(Key.RANGE.key(), "A1:D1");
		
		String response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		
		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(2, Xls.activeWorkbook.getNumberOfFonts());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.TARGET.key(), "./targets/applyFontStylesStrikeOut.xlsx");
		response = Fsl.execute("Xls.saveWorkbook", requestNode.toString());

		Sheet sheet = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex());
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Font font = Xls.activeWorkbook.getFontAt(sheet.getRow(i).getCell(j).getCellStyle().getFontIndex());
				assertEquals(i == 0 ? true : false, font.getStrikeout());

				assertEquals(false, font.getBold());
				assertEquals(false, font.getItalic());
				assertEquals(Font.U_NONE, font.getUnderline());
				assertEquals(0, font.getColor());
				assertEquals("Calibri", font.getFontName());
				assertEquals(12, font.getFontHeightInPoints());
				assertEquals(Font.SS_NONE, font.getTypeOffset());
			}			
		}
	}
	
	
	@Test
	public void testApplyFontStyleTypeOffset() throws EncryptedDocumentException, IOException
	{
		openAndActivateWorkbook("./resources/xls/applyFontStyles.xlsx");

		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.TYPE_OFFSET.key(), Font.SS_SUPER);
		requestNode.put(Key.RANGE.key(), "A1:D1");
		
		String response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		
		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(2, Xls.activeWorkbook.getNumberOfFonts());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.TARGET.key(), "./targets/applyFontStylesTypeOffset.xlsx");
		response = Fsl.execute("Xls.saveWorkbook", requestNode.toString());

		Sheet sheet = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex());
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Font font = Xls.activeWorkbook.getFontAt(sheet.getRow(i).getCell(j).getCellStyle().getFontIndex());
				assertEquals(i == 0 ? Font.SS_SUPER : Font.SS_NONE, font.getTypeOffset());

				assertEquals(false, font.getBold());
				assertEquals(false, font.getItalic());
				assertEquals(Font.U_NONE, font.getUnderline());
				assertEquals(0, font.getColor());
				assertEquals("Calibri", font.getFontName());
				assertEquals(12, font.getFontHeightInPoints());
				assertEquals(false, font.getStrikeout());
			}			
		}
	}
	
	@Test
	public void testApplyFontStyleBoldToRange() throws EncryptedDocumentException, IOException
	{
		openAndActivateWorkbook("./resources/xls/applyFontStylesRange.xlsx");

		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.BOLD.key(), 1);
		requestNode.put(Key.RANGE.key(), "A1:D1");
		
		String response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		
		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(2, Xls.activeWorkbook.getNumberOfFonts());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.TARGET.key(), "./targets/applyFontStylesRange.xlsx");
		response = Fsl.execute("Xls.saveWorkbook", requestNode.toString());

		Sheet sheet = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex());
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Font font = Xls.activeWorkbook.getFontAt(sheet.getRow(i).getCell(j).getCellStyle().getFontIndex());
				assertEquals(i == 0 ? true : false, font.getBold());
				assertEquals(false, font.getItalic());
				assertEquals(Font.U_NONE, font.getUnderline());
			}			
		}
	}
	
	@Test
	public void testApplyFontStyleItalicToRange() throws EncryptedDocumentException, IOException
	{
		openAndActivateWorkbook("./resources/xls/applyFontStylesRange.xlsx");

		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.BOLD.key(), 1);
		requestNode.put(Key.RANGE.key(), "A1:D1");
		
		String response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		
		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		Sheet sheet = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex());
		Font font = Xls.activeWorkbook.getFontAt(sheet.getRow(0).getCell(0).getCellStyle().getFontIndex());
		assertEquals(true, font.getBold());
		assertEquals(false, font.getItalic());
		assertEquals(Font.U_NONE, font.getUnderline());
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.ITALIC.key(), 1);
		requestNode.put(Key.RANGE.key(), "A2:D2");
		
		response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		font = Xls.activeWorkbook.getFontAt(sheet.getRow(1).getCell(0).getCellStyle().getFontIndex());
		assertEquals(false, font.getBold());
		assertEquals(true, font.getItalic());
		assertEquals(Font.U_NONE, font.getUnderline());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.BOLD.key(), 1);
		requestNode.put(Key.ITALIC.key(), 1);
		requestNode.put(Key.RANGE.key(), "A3:D3");
		
		response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		font = Xls.activeWorkbook.getFontAt(sheet.getRow(2).getCell(0).getCellStyle().getFontIndex());
		assertEquals(true, font.getBold());
		assertEquals(true, font.getItalic());
		assertEquals(Font.U_NONE, font.getUnderline());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.UNDERLINE.key(), Font.U_DOUBLE);
		requestNode.put(Key.RANGE.key(), "A4:D4");
		
		response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		font = Xls.activeWorkbook.getFontAt(sheet.getRow(3).getCell(0).getCellStyle().getFontIndex());
		assertEquals(false, font.getBold());
		assertEquals(false, font.getItalic());
		assertEquals(Font.U_DOUBLE, font.getUnderline());
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.WORKBOOK.key(), "./targets/applyFontStylesRange.xlsx");
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
		requestNode.put(Key.WORKBOOK.key(), "./targets/applyNumberFormats.xlsx");
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
	}
	
	@Test
	public void testCellStyle() throws EncryptedDocumentException, IOException
	{
		String path = "./resources/xls/applyCellStyle.xlsx";
		openAndActivateWorkbook(path);
		
		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "A1");
		ObjectNode borderNode = requestNode.objectNode();
		ObjectNode styleNode = borderNode.objectNode();
		TextNode node = styleNode.textNode(BorderStyle.DOTTED.name());
		styleNode.set(Key.BOTTOM.key(), node);
		borderNode.set(Key.STYLE.key(), styleNode);
		requestNode.set(Key.BORDER.key(), borderNode);
		String response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());
		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "A1");
		borderNode = requestNode.objectNode();
		styleNode = borderNode.objectNode();
		node = styleNode.textNode(BorderStyle.DOTTED.name());
		styleNode.set(Key.BOTTOM.key(), node);
		borderNode.set(Key.STYLE.key(), styleNode);
		requestNode.set(Key.BORDER.key(), borderNode);
		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "A1");
		borderNode = requestNode.objectNode();
		styleNode = borderNode.objectNode();
		node = styleNode.textNode(BorderStyle.THICK.name());
		styleNode.set(Key.LEFT.key(), node);
		borderNode.set(Key.STYLE.key(), styleNode);
		requestNode.set(Key.BORDER.key(), borderNode);
		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "A1");
		borderNode = requestNode.objectNode();
		styleNode = borderNode.objectNode();
		node = styleNode.textNode(BorderStyle.DOUBLE.name());
		styleNode.set(Key.RIGHT.key(), node);
		borderNode.set(Key.STYLE.key(), styleNode);
		requestNode.set(Key.BORDER.key(), borderNode);
		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "A1");
		borderNode = requestNode.objectNode();
		styleNode = borderNode.objectNode();
		node = styleNode.textNode(BorderStyle.DASH_DOT.name());
		styleNode.set(Key.TOP.key(), node);
		borderNode.set(Key.STYLE.key(), styleNode);
		requestNode.set(Key.BORDER.key(), borderNode);
		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "A2");
		borderNode = requestNode.objectNode();
		styleNode = borderNode.objectNode();
		node = styleNode.textNode("xxx");
		styleNode.set(Key.TOP.key(), node);
		borderNode.set(Key.STYLE.key(), styleNode);
		requestNode.set(Key.BORDER.key(), borderNode);
		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.RANGE.key(), "A2");
		borderNode = requestNode.objectNode();
		styleNode = borderNode.objectNode();
		node = styleNode.textNode("xxx");
		styleNode.set(Key.TOP.key(), node);
		borderNode.set(Key.STYLE.key(), styleNode);
		requestNode.set(Key.BORDER.key(), borderNode);
		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.RANGE.key(), "C2:D3");
		borderNode = requestNode.objectNode();
		styleNode = borderNode.objectNode();
		node = styleNode.textNode(BorderStyle.DOTTED.name());
		styleNode.set(Key.BOTTOM.key(), node);
		borderNode.set(Key.STYLE.key(), styleNode);
		requestNode.set(Key.BORDER.key(), borderNode);
		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.RANGE.key(), "C2:D3");
		borderNode = requestNode.objectNode();
		styleNode = borderNode.objectNode();
		node = styleNode.textNode(BorderStyle.DOTTED.name());
		styleNode.set(Key.BOTTOM.key(), node);
		borderNode.set(Key.STYLE.key(), styleNode);
		requestNode.set(Key.BORDER.key(), borderNode);
		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.RANGE.key(), "C2:D3");
		borderNode = requestNode.objectNode();
		styleNode = borderNode.objectNode();
		node = styleNode.textNode(BorderStyle.THICK.name());
		styleNode.set(Key.LEFT.key(), node);
		borderNode.set(Key.STYLE.key(), styleNode);
		requestNode.set(Key.BORDER.key(), borderNode);
		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.RANGE.key(), "C2:D3");
		borderNode = requestNode.objectNode();
		styleNode = borderNode.objectNode();
		node = styleNode.textNode(BorderStyle.DOUBLE.name());
		styleNode.set(Key.RIGHT.key(), node);
		borderNode.set(Key.STYLE.key(), styleNode);
		requestNode.set(Key.BORDER.key(), borderNode);
		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.RANGE.key(), "C2:D3");
		borderNode = requestNode.objectNode();
		styleNode = borderNode.objectNode();
		node = styleNode.textNode(BorderStyle.DASH_DOT.name());
		styleNode.set(Key.TOP.key(), node);
		borderNode.set(Key.STYLE.key(), styleNode);
		requestNode.set(Key.BORDER.key(), borderNode);
		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.RANGE.key(), "C2:D3");
		borderNode = requestNode.objectNode();
		styleNode = borderNode.objectNode();
		node = styleNode.textNode("xxx");
		styleNode.set(Key.TOP.key(), node);
		borderNode.set(Key.STYLE.key(), styleNode);
		requestNode.set(Key.BORDER.key(), borderNode);
		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.WORKBOOK.key(), path);
		requestNode.put(Key.TARGET.key(), "./targets/applyCellStyles.xlsx");
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}
	
	@Test
	public void testAutoSizeColumns() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing("autoSizeColumn.xlsx", SHEET0);
		Sheet sheet = getActiveSheet();
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue(new XSSFRichTextString("Das ist eine Testzelle"));
		cell = row.createCell(1);
		cell.setCellValue(new XSSFRichTextString("Das ist eine zweite Testzelle"));

		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "A1");
		
		String response = Fsl.execute("Xls.autoSizeColumns", requestNode.toString());
		
		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.RANGE.key(), "A1:B1");
		
		response = Fsl.execute("Xls.autoSizeColumns", requestNode.toString());
		
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.WORKBOOK.key(), "./targets/autoSizeColumn.xlsx");
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
	}

	@Test
	public void testRotation() throws JsonMappingException, JsonProcessingException
	{
		String workbook = "./targets/rotate.xlsx";
		prepareWorkbookAndSheetIfMissing(workbook, SHEET0);
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
		requestNode.put(Key.WORKBOOK.key(), workbook);
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
	}
	
	@Test
	public void testHorizontalAlignment() throws JsonMappingException, JsonProcessingException
	{
		String workbook = "./targets/horizontalAlign.xlsx";
		prepareWorkbookAndSheetIfMissing(workbook, SHEET0);
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
		requestNode.put(Key.WORKBOOK.key(), workbook);
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
	}
	
	@Test
	public void testBookkeeperReport() throws JsonMappingException, JsonProcessingException
	{
		String workbook = "./targets/BookKeeperReport.xlsx";
		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.WORKBOOK.key(), workbook);
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
		requestNode.put(Key.BOLD.key(), 1);
		response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
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
		requestNode.put(Key.BOLD.key(), 1);
		response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.RANGE.key(), "A1:H4");
		response = Fsl.execute("Xls.autoSizeColumns", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.WORKBOOK.key(), workbook);
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
	}
	
	@Test
	public void testFonts() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		Sheet sheet = getActiveSheet();
		Cell cell = getOrCreateCell(sheet, new CellAddress("A1"));
		cell.setCellValue(new XSSFRichTextString("Das ist ein Test"));
		cell = getOrCreateCell(sheet, new CellAddress("A2"));
		cell.setCellValue(new XSSFRichTextString("Das ist ein weiterer Test"));

		int numberOfFonts = sheet.getWorkbook().getNumberOfFonts();
		System.out.println("Fonts im Workbook");
		for (int i = 0; i < numberOfFonts; i++)
		{
			Font font = sheet.getWorkbook().getFontAt(i);
			System.out.println(font.getFontName() + ", Size: " + font.getFontHeightInPoints() + ", Bold: " + font.getBold() + ", Italic: " + font.getItalic() + ", Underline: " + font.getUnderline() + ", Strikeout: " + font.getStrikeout() + ", Type Offset: " + font.getTypeOffset() + ", Color: " + font.getColor());
		}
		
		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.NAME.key(), "Arial");
		requestNode.put(Key.SIZE.key(), 30);
		requestNode.put(Key.CELL.key(), "A1");
		
		String response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		
		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Key.INDEX.key()).asInt());
		numberOfFonts = sheet.getWorkbook().getNumberOfFonts();
		assertEquals(2, numberOfFonts);

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.NAME.key(), "Courier New");
		requestNode.put(Key.SIZE.key(), 24);
		requestNode.put(Key.BOLD.key(), 1);
		requestNode.put(Key.CELL.key(), "A2");
		
		response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(2, responseNode.get(Key.INDEX.key()).asInt());
		numberOfFonts = sheet.getWorkbook().getNumberOfFonts();
		assertEquals(3, numberOfFonts);

		System.out.println("Fonts im Workbook");
		for (int i = 0; i < numberOfFonts; i++)
		{
			Font font = sheet.getWorkbook().getFontAt(i);
			System.out.println(font.getFontName() + ", Size: " + font.getFontHeightInPoints() + ", Bold: " + font.getBold() + ", Italic: " + font.getItalic() + ", Underline: " + font.getUnderline() + ", Strikeout: " + font.getStrikeout() + ", Type Offset: " + font.getTypeOffset() + ", Color: " + font.getColor());
		}
		
		System.out.println("Fonts in den Zellen");
		System.out.println(sheet.getRow(0).getCell(0).getCellStyle().getFontIndex());
		System.out.println(sheet.getRow(1).getCell(0).getCellStyle().getFontIndex());
		
		requestNode = MAPPER.createObjectNode();
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
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

	private static void openAndActivateWorkbook(String path) throws EncryptedDocumentException, IOException
	{
		File file = new File(path);
		Workbook workbook = XSSFWorkbook.class.cast(WorkbookFactory.create(file));
		Xls.workbooks.put(path, workbook);
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
