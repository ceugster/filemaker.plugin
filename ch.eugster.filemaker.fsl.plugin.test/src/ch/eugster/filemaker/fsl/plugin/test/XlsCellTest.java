package ch.eugster.filemaker.fsl.plugin.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFEvaluationWorkbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaParsingWorkbook;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaRenderingWorkbook;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.eval.FunctionEval;
import org.apache.poi.ss.formula.ptg.AreaPtgBase;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.RefPtgBase;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import ch.eugster.filemaker.fsl.plugin.Executor;
import ch.eugster.filemaker.fsl.plugin.Fsl;

public final class XlsCellTest extends AbstractXlsTest
{
	@Test
	public void testSetCellsRightByIntegers() throws JsonMappingException, JsonProcessingException
	{
		Sheet sheet = prepareWorkbookAndSheetIfMissing();
		ObjectNode requestNode = mapper.createObjectNode();
		ObjectNode cellNode = requestNode.objectNode();
		cellNode.put("row", 1);
		cellNode.put("col", 1);
		requestNode.set("cell", cellNode);
		TextNode directionNode = requestNode.textNode("right");
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

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(10, sheet.getRow(1).getCell(6).getNumericCellValue(), 0);
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testSetCellsRight() throws JsonMappingException, JsonProcessingException
	{
		Sheet sheet = prepareWorkbookAndSheetIfMissing();
		CellAddress cellAddress = new CellAddress("B2");
		ObjectNode requestNode = mapper.createObjectNode();
		TextNode startNode = requestNode.textNode(cellAddress.formatAsString());
		requestNode.set("cell", startNode);
		TextNode directionNode = requestNode.textNode("right");
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

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(10, sheet.getRow(1).getCell(6).getNumericCellValue(), 0);
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testSetCellsRightOneCell() throws JsonMappingException, JsonProcessingException
	{
		Sheet sheet = prepareWorkbookAndSheetIfMissing();
		CellAddress cellAddress = new CellAddress("B2");
		ObjectNode requestNode = mapper.createObjectNode();
		TextNode firstNode = requestNode.textNode(cellAddress.formatAsString());
		requestNode.set("cell", firstNode);

		ArrayNode valuesNode = requestNode.arrayNode();
		valuesNode.add("Title");
		requestNode.set("values", valuesNode);
		requestNode.put("path", "targets/CellsRightOneCell.xlsx");

		String response = Fsl.execute("Xls.setCells", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals("Title", sheet.getRow(1)
				.getCell(1).getStringCellValue());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testSetCellsLeft() throws JsonMappingException, JsonProcessingException
	{
		Sheet sheet = prepareWorkbookAndSheetIfMissing();
		CellAddress cellAddress = new CellAddress("G3");
		ObjectNode requestNode = mapper.createObjectNode();
		TextNode startNode = requestNode.textNode(cellAddress.formatAsString());
		requestNode.set("cell", startNode);
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

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(10, sheet.getRow(2).getCell(1).getNumericCellValue(), 0);
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testSetCellsUp() throws JsonMappingException, JsonProcessingException
	{
		Sheet sheet = prepareWorkbookAndSheetIfMissing();
		CellAddress cellAddress = new CellAddress("I30");
		ObjectNode requestNode = mapper.createObjectNode();
		TextNode startNode = requestNode.textNode(cellAddress.formatAsString());
		requestNode.set("cell", startNode);
		TextNode directionNode = requestNode.textNode("up");
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

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(10, sheet.getRow(24).getCell(8).getNumericCellValue(), 0);
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testSetCellsDown() throws JsonMappingException, JsonProcessingException
	{
		Sheet sheet = prepareWorkbookAndSheetIfMissing();
		CellAddress cellAddress = new CellAddress("K3");
		ObjectNode requestNode = mapper.createObjectNode();
		TextNode startNode = requestNode.textNode(cellAddress.formatAsString());
		requestNode.set("cell", startNode);
		TextNode directionNode = requestNode.textNode("down");
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

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(10, sheet.getRow(7).getCell(10).getNumericCellValue(), 0);
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testSetCellsWithAddressValues() throws JsonMappingException, JsonProcessingException
	{
		Sheet sheet = prepareWorkbookAndSheetIfMissing();
		CellAddress cellAddress = new CellAddress("K3");
		ObjectNode requestNode = mapper.createObjectNode();
		ObjectNode cellNode = requestNode.objectNode();
		cellNode.put("row", cellAddress.getRow());
		cellNode.put("col", cellAddress.getColumn());
		requestNode.set("cell", cellNode);
		TextNode directionNode = requestNode.textNode("down");
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

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(10, sheet.getRow(7).getCell(10).getNumericCellValue(), 0);
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testSetCellsWithoutDirection() throws JsonMappingException, JsonProcessingException
	{
		Sheet sheet = prepareWorkbookAndSheetIfMissing();
		CellAddress cellAddress = new CellAddress("B2");
		ObjectNode requestNode = mapper.createObjectNode();
		TextNode startNode = requestNode.textNode(cellAddress.formatAsString());
		requestNode.set("cell", startNode);

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

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(10, sheet.getRow(1).getCell(6)
				.getNumericCellValue(), 0);
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testCopyFormula()
	{
		String workbook = "./targets/copyFormula.xlsx";
		Sheet sheet = prepareWorkbookAndSheetIfMissing(workbook, SHEET0);
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
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("workbook", workbook);
		Fsl.execute("Xls.saveWorkbook", requestNode.toString());
	}
	
	@Test
	public void testTimeAsStringToCell() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("cell", "A1");
		ArrayNode valuesNode = requestNode.arrayNode();
		valuesNode.add("12:33");
		requestNode.set("values", valuesNode);
		
		String response = Fsl.execute("Xls.setCells", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = mapper.createObjectNode();
		Fsl.execute("Xls.saveWorkbook", requestNode.toString());
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
	}

	@Test
	public void testDateAsStringToCell() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("cell", "A1");
		ArrayNode valuesNode = requestNode.arrayNode();
		valuesNode.add("21.10.1954");
		requestNode.set("values", valuesNode);
		
		String response = Fsl.execute("Xls.setCells", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = mapper.createObjectNode();
		Fsl.execute("Xls.saveWorkbook", requestNode.toString());
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
	}

	@Test
	public void testDateTimeAsStringToCell() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing();
		
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("cell", "A1");
		ArrayNode valuesNode = requestNode.arrayNode();
		valuesNode.add("21.10.1954 10:31");
		requestNode.set("values", valuesNode);
		
		String response = Fsl.execute("Xls.setCells", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = mapper.createObjectNode();
		Fsl.execute("Xls.saveWorkbook", requestNode.toString());
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
	}

	@Test
	public void testCopySingleFormulaCellToSingleCell() throws IOException
	{
		String workbook = "./targets/CopySingleFormulaCell.xlsx";
		Sheet sheet = prepareWorkbookAndSheetIfMissing(workbook, SHEET0);
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

		ObjectNode requestNode = mapper.createObjectNode();
		TextNode sourceNode = requestNode.textNode("A3");
		requestNode.set("source", sourceNode);

		TextNode targetNode = requestNode.textNode("B3");
		requestNode.set("target", targetNode);

		String response = Fsl.execute("Xls.copy", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		cell = sheet.getRow(2).getCell(1);
		FormulaEvaluator formulaEval = xls.activeWorkbook.getCreationHelper().createFormulaEvaluator();
		assertEquals(25D, formulaEval.evaluate(cell).getNumberValue(), 0D);

		requestNode = mapper.createObjectNode();
		requestNode.put("workbook", workbook);
		Fsl.execute("Xls.saveWorkbook", requestNode.toString());
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
	}

	@Test
	public void testCopySingleFormulaCellToMultipleCells() throws IOException
	{
		String workbook = "./targets/CopySingleCellMultipleCells.xlsx";
		Sheet sheet = prepareWorkbookAndSheetIfMissing(workbook, SHEET0);
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

		ObjectNode requestNode = mapper.createObjectNode();
		TextNode sourceNode = requestNode.textNode("A3");
		requestNode.set("source", sourceNode);

		TextNode targetNode = requestNode.textNode("B3:C3");
		requestNode.set("target", targetNode);

		String response = Fsl.execute("Xls.copy", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		FormulaEvaluator formulaEval = xls.activeWorkbook.getCreationHelper().createFormulaEvaluator();
		cell = sheet.getRow(2).getCell(1);
		assertEquals(25D, formulaEval.evaluate(cell).getNumberValue(), 0D);
		cell = sheet.getRow(2).getCell(2);
		assertEquals(25D, formulaEval.evaluate(cell).getNumberValue(), 0D);

		requestNode = mapper.createObjectNode();
		requestNode.put("workbook", workbook);
		Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
	}

	@Test
	public void testCopySingleFormulaCellToMultipleCellsWithAddresses() throws IOException
	{
		String workbook = "./targets/testCopySingleFormulaCellToMultipleCellsWithAddresses.xlsx";
		Sheet sheet = prepareWorkbookAndSheetIfMissing(workbook, SHEET0);
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

		ObjectNode requestNode = mapper.createObjectNode();
		ObjectNode sourceNode = requestNode.objectNode();
		sourceNode.put("top_left", "A3");
		sourceNode.put("bottom_right", "A3");
		requestNode.set("source", sourceNode);

		ObjectNode targetNode = requestNode.objectNode();
		targetNode.put("top_left", "B3");
		targetNode.put("bottom_right", "C3");
		requestNode.set("target", targetNode);

		String response = Fsl.execute("Xls.copy", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		FormulaEvaluator formulaEval = xls.activeWorkbook.getCreationHelper().createFormulaEvaluator();
		cell = sheet.getRow(2).getCell(1);
		assertEquals(25D, formulaEval.evaluate(cell).getNumberValue(), 0D);
		cell = sheet.getRow(2).getCell(2);
		assertEquals(125D, formulaEval.evaluate(cell).getNumberValue(), 0D);

		requestNode = mapper.createObjectNode();
		requestNode.put("workbook", workbook);
		Fsl.execute("Xls.saveWorkbook", requestNode.toString());
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
	}

	@Test
	public void testCopySingleFormulaCellToMultipleCellsWithInts() throws IOException
	{
		String workbook = "./targets/testCopySingleFormulaCellToMultipleCellsWithInts.xlsx";
		Sheet sheet = prepareWorkbookAndSheetIfMissing(workbook, SHEET0);
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

		ObjectNode requestNode = mapper.createObjectNode();
		ObjectNode sourceNode = requestNode.objectNode();
		sourceNode.put("top", 2);
		sourceNode.put("left", 0);
		sourceNode.put("bottom", 2);
		sourceNode.put("right", 0);
		requestNode.set("source", sourceNode);

		ObjectNode targetNode = requestNode.objectNode();
		targetNode.put("top", 2);
		targetNode.put("left", 1);
		targetNode.put("bottom", 2);
		targetNode.put("right", 2);
		requestNode.set("target", targetNode);

		String response = Fsl.execute("Xls.copy", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		FormulaEvaluator formulaEval = xls.activeWorkbook.getCreationHelper().createFormulaEvaluator();
		cell = sheet.getRow(2).getCell(1);
		assertEquals(25D, formulaEval.evaluate(cell).getNumberValue(), 0D);
		cell = sheet.getRow(2).getCell(2);
		assertEquals(125D, formulaEval.evaluate(cell).getNumberValue(), 0D);

		requestNode = mapper.createObjectNode();
		requestNode.put("workbook", workbook);
		Fsl.execute("Xls.saveWorkbook", requestNode.toString());
		responseNode = mapper.readTree(response);
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

	protected String copyFormula(Sheet sheet, String formula, int rowDiff, int colDiff)
	{
		FormulaParsingWorkbook workbookWrapper = getFormulaParsingWorkbook(sheet);
		Ptg[] ptgs = FormulaParser.parse(formula, workbookWrapper, FormulaType.CELL,
				sheet.getWorkbook().getSheetIndex(sheet));
		for (int i = 0; i < ptgs.length; i++)
		{
			if (ptgs[i] instanceof RefPtgBase)
			{ // base class for cell references
				RefPtgBase ref = (RefPtgBase) ptgs[i];
				if (ref.isRowRelative())
				{
					ref.setRow(ref.getRow() + rowDiff);
				}
				if (ref.isColRelative())
				{
					ref.setColumn(ref.getColumn() + colDiff);
				}
			}
			else if (ptgs[i] instanceof AreaPtgBase)
			{ // base class for range references
				AreaPtgBase ref = (AreaPtgBase) ptgs[i];
				if (ref.isFirstColRelative())
				{
					ref.setFirstColumn(ref.getFirstColumn() + colDiff);
				}
				if (ref.isLastColRelative())
				{
					ref.setLastColumn(ref.getLastColumn() + colDiff);
				}
				if (ref.isFirstRowRelative())
				{
					ref.setFirstRow(ref.getFirstRow() + rowDiff);
				}
				if (ref.isLastRowRelative())
				{
					ref.setLastRow(ref.getLastRow() + rowDiff);
				}
			}
		}

		formula = FormulaRenderer.toFormulaString(getFormulaRenderingWorkbook(sheet), ptgs);
		return formula;
	}

	protected FormulaParsingWorkbook getFormulaParsingWorkbook(Sheet sheet)
	{
		FormulaParsingWorkbook workbookWrapper = null;
		if (XSSFSheet.class.isInstance(sheet))
		{
			workbookWrapper = XSSFEvaluationWorkbook.create(XSSFSheet.class.cast(sheet).getWorkbook());
		}
		else
		{
			workbookWrapper = HSSFEvaluationWorkbook.create(HSSFSheet.class.cast(sheet).getWorkbook());
		}
		return workbookWrapper;
	}

	protected FormulaRenderingWorkbook getFormulaRenderingWorkbook(Sheet sheet)
	{
		FormulaRenderingWorkbook workbookWrapper = null;
		if (XSSFSheet.class.isInstance(sheet))
		{
			workbookWrapper = XSSFEvaluationWorkbook.create(XSSFSheet.class.cast(sheet).getWorkbook());
		}
		else
		{
			workbookWrapper = HSSFEvaluationWorkbook.create(HSSFSheet.class.cast(sheet).getWorkbook());
		}
		return workbookWrapper;
	}

}
