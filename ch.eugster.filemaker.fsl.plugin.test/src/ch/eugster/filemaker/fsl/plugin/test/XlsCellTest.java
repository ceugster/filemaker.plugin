package ch.eugster.filemaker.fsl.plugin.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;

import org.apache.poi.ss.formula.eval.FunctionEval;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellAddress;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import ch.eugster.filemaker.fsl.plugin.Executor;
import ch.eugster.filemaker.fsl.plugin.Fsl;
import ch.eugster.filemaker.fsl.plugin.xls.Xls;

public class XlsCellTest extends XlsTest
{
	@Test
	public void testSetCellsRightByIntegers() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		ObjectNode requestNode = MAPPER.createObjectNode();
		ObjectNode cellNode = requestNode.objectNode();
		cellNode.put("row", 1);
		cellNode.put("col", 1);
		requestNode.set(Key.CELL.key(), cellNode);
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
	public void testTimeAsStringToCell() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		
		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "A1");
		ArrayNode valuesNode = requestNode.arrayNode();
		valuesNode.add("12:33");
		requestNode.set(Key.VALUES.key(), valuesNode);
		
		String response = Fsl.execute("Xls.setCells", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = MAPPER.createObjectNode();
		Fsl.execute("Xls.saveWorkbook", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
	}

	@Test
	public void testDateAsStringToCell() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		
		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "A1");
		ArrayNode valuesNode = requestNode.arrayNode();
		valuesNode.add("21.10.1954");
		requestNode.set(Key.VALUES.key(), valuesNode);
		
		String response = Fsl.execute("Xls.setCells", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = MAPPER.createObjectNode();
		Fsl.execute("Xls.saveWorkbook", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
	}

	@Test
	public void testDateTimeAsStringToCell() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		
		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "A1");
		ArrayNode valuesNode = requestNode.arrayNode();
		valuesNode.add("21.10.1954 10:31");
		requestNode.set(Key.VALUES.key(), valuesNode);
		
		String response = Fsl.execute("Xls.setCells", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = MAPPER.createObjectNode();
		Fsl.execute("Xls.saveWorkbook", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
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

}
