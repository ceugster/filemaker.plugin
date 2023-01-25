package ch.eugster.filemaker.fsl.plugin.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.poi.ss.formula.eval.FunctionEval;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
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

import ch.eugster.filemaker.fsl.plugin.Fsl;
import ch.eugster.filemaker.fsl.plugin.xls.Xls;

public class XlsTest extends Xls
{
	private ObjectMapper mapper = new ObjectMapper();

	private static final String WORKBOOK_1 = "./workbook1.xlsx";

	private static final String SHEET_0 = "Arbeitsblatt 1";

	@BeforeAll
	public static void beforeAll()
	{
		File directory = new File(".");
		File[] workbooks = directory.listFiles(new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				return name.startsWith("workbook");
			}
		});
		for (File workbook : workbooks)
		{
			workbook.delete();
		}
	}

	@BeforeEach
	public void beforeEach()
	{
		clearWorkbook();
	}

	@AfterAll
	public static void afterAll()
	{
	}

	@AfterEach
	public void afterEach()
	{
		clearWorkbook();
	}

	@Test
	public void testCreateWorkbook() throws Exception
	{
		String result = Fsl.execute("Xls.createWorkbook", new Object[] { WORKBOOK_1 });
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
		assertEquals(XSSFWorkbook.class, Xls.workbook.getClass());
	}

	@Test
	public void testCreateAlreadyExistingWorkbook() throws Exception
	{
		Xls.workbook = new XSSFWorkbook();
		String result = Fsl.execute("Xls.createWorkbook", new Object[] { WORKBOOK_1 });
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals("Die Arbeitsmappe ist bereits vorhanden", resultNode.get("errors").get(0).asText());
	}

	@Test
	public void testCreateWorkbookWithoutPath() throws Exception
	{
		String result = Fsl.execute("Xls.createWorkbook", new Object[0]);
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals("Kein Dateipfad angegeben", resultNode.get("errors").get(0).asText());
	}

	@Test
	public void testCreateWorkbookInvalidPath() throws Exception
	{
		String result = Fsl.execute("Xls.createWorkbook", new Object[] { "Völlig /< * ? : \falscher Pfad" });
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals("Das Dateiverzeichnis ist ungültig", resultNode.get("errors").get(0).asText());
	}

	@Test
	public void testCreateWorkbookMissingParentDirectory() throws Exception
	{
		String result = Fsl.execute("Xls.createWorkbook", new Object[] { System.getProperty("user.home") + "/blabla/wb.xlsx" });
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals("Das Dateiverzeichnis ist ungültig", resultNode.get("errors").get(0).asText());
	}

	@Test
	public void testCreateSheet() throws JsonMappingException, JsonProcessingException
	{
		String result = Fsl.execute("Xls.createSheet", new Object[] { SHEET_0 });
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
		assertEquals(SHEET_0, Xls.workbook.getSheet(SHEET_0).getSheetName());
	}

	@Test
	public void testCreateSheetAlreadyExisting() throws JsonMappingException, JsonProcessingException
	{
		Xls.workbook = new XSSFWorkbook();
		Xls.workbook.createSheet(SHEET_0);
		String result = Fsl.execute("Xls.createSheet", new Object[] { SHEET_0 });
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals("The workbook already contains a sheet named '" + SHEET_0 + "'", resultNode.get("errors").get(0).asText());
	}

	@Test
	public void testSetActiveSheet() throws JsonMappingException, JsonProcessingException
	{
		String result = Fsl.execute("Xls.setActiveSheet", new Object[] { Integer.valueOf(0) });
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
		assertEquals(SHEET_0, Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex()).getSheetName());
		result = Fsl.execute("Xls.setActiveSheet", new Object[] { SHEET_0 });
		resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
		assertEquals(SHEET_0, Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex()).getSheetName());
	}

	@Test
	public void testSetNotExistingSheetActive() throws JsonMappingException, JsonProcessingException
	{
		String result = Fsl.execute("Xls.setActiveSheet", new Object[] { Integer.valueOf(0) });
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
		result = Fsl.execute("Xls.setActiveSheet", new Object[] { SHEET_0 });
		resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
	}

	@Test
	public void testSetActiveSheetWithoutWorkbook() throws JsonMappingException, JsonProcessingException
	{
		String result = Fsl.execute("Xls.setActiveSheet", new Object[] { Integer.valueOf(0) });
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
		result = Fsl.execute("Xls.setActiveSheet", new Object[] { SHEET_0 });
		resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
	}

	@Test
	public void testSetHeadingsHorizontal() throws JsonMappingException, JsonProcessingException
	{
		int startColumnIndex = 3;
		Object[] parameters = new Object[] { 0, startColumnIndex, "Column1", "Column2", "Column3", "Column4", "Column5" };
		Xls.setHeadingsHorizontal(parameters);
		assertEquals(0, Xls.workbook.getSheetAt(0).getFirstRowNum());
		for (int i = 0; i < parameters.length - startColumnIndex; i++)
		{
			assertEquals("Column" + String.valueOf(i + 1), Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex()).getRow(0).getCell(startColumnIndex + i).getRichStringCellValue().getString());
		}
		parameters = new Object[] { "D1", "Column1", "Column2", "Column3", "Column4", "Column5" };
		Xls.setHeadingsHorizontal(parameters);
		assertEquals(0, Xls.workbook.getSheetAt(0).getFirstRowNum());
		for (int i = 0; i < parameters.length - startColumnIndex; i++)
		{
			assertEquals("Column" + String.valueOf(i + 1), Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex()).getRow(0).getCell(startColumnIndex + i).getRichStringCellValue().getString());
		}
	}

	@Test
	public void testCreateWorkbookUsingFsl() throws JsonMappingException, JsonProcessingException
	{
		String result = Fsl.execute("Xls.createWorkbook", new Object[] { WORKBOOK_1 });
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
	}

	@Test
	public void testCreateAlreadyExistingWorkbookUsingFsl() throws Exception
	{
		Xls.workbook = new XSSFWorkbook();
		String result = Fsl.execute("Xls.createWorkbook", new Object[] { WORKBOOK_1 });
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals("Die Arbeitsmappe ist bereits vorhanden", resultNode.get("errors").get(0).asText());
	}

	@Test
	public void testCreateWorkbookWithoutPathUsingFsl() throws Exception
	{
		String result = Fsl.execute("Xls.createWorkbook", new Object[0]);
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals("Kein Dateipfad angegeben", resultNode.get("errors").get(0).asText());
	}

	@Test
	public void testCreateWorkbookInvalidPathUsingFsl() throws Exception
	{
		String result = Fsl.execute("Xls.createWorkbook", new Object[] { "Völlig /< * ? : \falscher Pfad" });
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals("Das Dateiverzeichnis ist ungültig", resultNode.get("errors").get(0).asText());
	}

	@Test
	public void testCreateWorkbookMissingParentDirectoryUsingFsl() throws Exception
	{
		String result = Fsl.execute("Xls.createWorkbook", new Object[] { System.getProperty("user.home") + "/blabla/wb.xlsx" });
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals("Das Dateiverzeichnis ist ungültig", resultNode.get("errors").get(0).asText());
	}

	@Test
	public void testCreateSheetUsingFsl() throws JsonMappingException, JsonProcessingException
	{
		String result = Fsl.execute("Xls.createSheet", new Object[] { SHEET_0 });
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
	}

	@Test
	public void testSetActiveSheetUsingFsl() throws JsonMappingException, JsonProcessingException
	{
		String result = Fsl.execute("Xls.setActiveSheet", new Object[] { SHEET_0 });
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
	}

	@Test
	public void testUseNonExistingSheetUsingFsl() throws JsonMappingException, JsonProcessingException
	{
		String result = Fsl.execute("Xls.setActiveSheet", new Object[] { Integer.valueOf(0) });
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
		result = Fsl.execute("Xls.setActiveSheet", new Object[] { SHEET_0 });
		resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
	}

	@Test
	public void testSetHeadingsHorizontalUsingFsl() throws JsonMappingException, JsonProcessingException
	{
		int startColumnIndex = 3;
		Object[] parameters = new Object[] { 0, startColumnIndex, "Column1", "Column2", "Column3", "Column4", "Column5" };
		String result = Fsl.execute("Xls.setHeadingsHorizontal", parameters);
		JsonNode resultNode = mapper.readTree(result);
		resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
		assertEquals(0, Xls.workbook.getSheetAt(0).getFirstRowNum());
		for (int i = 0; i < parameters.length - startColumnIndex; i++)
		{
			assertEquals("Column" + String.valueOf(i + 1), Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex()).getRow(0).getCell(startColumnIndex + i).getRichStringCellValue().getString());
		}
		parameters = new Object[] { "D1", "Column1", "Column2", "Column3", "Column4", "Column5" };
		result = Fsl.execute("Xls.setHeadingsHorizontal", parameters);
		resultNode = mapper.readTree(result);
		resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
		assertEquals(0, Xls.workbook.getSheetAt(0).getFirstRowNum());
		for (int i = 0; i < parameters.length - startColumnIndex; i++)
		{
			assertEquals("Column" + String.valueOf(i + 1), Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex()).getRow(0).getCell(startColumnIndex + i).getRichStringCellValue().getString());
		}
	}

	@Test
	public void testSetCellValueUsingFsl() throws JsonMappingException, JsonProcessingException
	{
		String result = Fsl.execute("Xls.setCellValue", new Object[] { 1, 0, 23D });
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
		assertEquals(23D, Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex()).getRow(1).getCell(0).getNumericCellValue(), 0.1);
		result = Fsl.execute("Xls.setCellValue", new Object[] { "A2", 23D });
		resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
		assertEquals(23D, Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex()).getRow(1).getCell(0).getNumericCellValue(), 0.1);
	}

	@Test
	public void testSetCellValueWithInvalidRowUsingFsl() throws JsonMappingException, JsonProcessingException
	{
		String result = Fsl.execute("Xls.setCellValue", new Object[] { -120, 120, 23D });
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals("Invalid row number (-120) outside allowable range (0..1048575)", resultNode.get("errors").get(0).asText());
	}

	@Test
	public void testSetCellValueWithInvalidCellRowUsingFsl() throws JsonMappingException, JsonProcessingException
	{
		String result = Fsl.execute("Xls.setCellValue", new Object[] { "QZ", 23D });
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals("For input string: \"\"", resultNode.get("errors").get(0).asText());
	}

	@Test
	public void testSetCellValueWithInvalidColumnUsingFsl() throws JsonMappingException, JsonProcessingException
	{
		String result = Fsl.execute("Xls.setCellValue", new Object[] { 120, -120, 23D });
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals("Cell index must be >= 0", resultNode.get("errors").get(0).asText());
	}

	@Test
	public void testSetCellValueWithInvalidCellColumnUsingFsl() throws JsonMappingException, JsonProcessingException
	{
		String result = Fsl.execute("Xls.setCellValue", new Object[] { "Q-2", 23D });
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals("Cannot invoke \"org.apache.poi.xssf.usermodel.XSSFRow.getCell(int)\" because \"row\" is null", resultNode.get("errors").get(0).asText());
	}

	@Test
	public void testSetRowValuesUsingFsl() throws JsonMappingException, JsonProcessingException
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, 1);
		calendar.set(Calendar.YEAR, 2022);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Object[] parameters = new Object[2];
		parameters[0] = new Object[] { 1, 0, 23D, calendar, "Test" };
		parameters[1] = new Object[] { "A2", 23D, calendar, "Test" };
		for (Object parameter : parameters)
		{
			String result = Fsl.execute("Xls.setRowValues", Object[].class.cast(parameter));
			JsonNode resultNode = mapper.readTree(result);
			assertEquals("OK", resultNode.get("result").asText());
			assertEquals(23D, Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex()).getRow(1).getCell(0).getNumericCellValue(), 0.1);
			assertEquals(calendar.getTime(), Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex()).getRow(1).getCell(1).getDateCellValue());
			assertEquals("Test", Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex()).getRow(1).getCell(2).getRichStringCellValue().getString());
		}
	}

	@Test
	public void testSetRowValuesInvalidRowUsingFsl() throws JsonMappingException, JsonProcessingException
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, 1);
		calendar.set(Calendar.YEAR, 2022);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		String result = Fsl.execute("Xls.setRowValues", new Object[] { -1, 0, 23D, calendar, "Test" });
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals("Invalid row number (-1) outside allowable range (0..1048575)", resultNode.get("errors").get(0).asText());
	}

	@Test
	public void testSetRowValuesInvalidCellRowUsingFsl() throws JsonMappingException, JsonProcessingException
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, 1);
		calendar.set(Calendar.YEAR, 2022);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		String result = Fsl.execute("Xls.setRowValues", new Object[] { "A 2", 23D, calendar, "Test" });
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals("Cell index must be >= 0", resultNode.get("errors").get(0).asText());
	}

	@Test
	public void testSetRowValuesInvalidValueTypeUsingFsl() throws JsonMappingException, JsonProcessingException
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, 1);
		calendar.set(Calendar.YEAR, 2022);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		String result = Fsl.execute("Xls.setRowValues", new Object[] { "A2", new Object(), calendar, "Test" });
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals("Falscher Parameter (erlaubt sind String, Datum, Number)", resultNode.get("errors").get(0).asText());
	}

	@Test
	public void testSetColumnValuesUsingFsl() throws JsonMappingException, JsonProcessingException
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, 1);
		calendar.set(Calendar.YEAR, 2022);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Object[] parameters = new Object[2];
		parameters[0] = new Object[] { 1, 0, 23D, calendar, "Test" };
		parameters[1] = new Object[] { "A2", 23D, calendar, "Test" };
		for (Object parameter : parameters)
		{
			String result = Fsl.execute("Xls.setRowValues", Object[].class.cast(parameter));
			JsonNode resultNode = mapper.readTree(result);
			assertEquals("OK", resultNode.get("result").asText());
			assertEquals(23D, Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex()).getRow(1).getCell(0).getNumericCellValue(), 0.1);
			assertEquals(calendar.getTime(), Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex()).getRow(1).getCell(1).getDateCellValue());
			assertEquals("Test", Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex()).getRow(1).getCell(2).getRichStringCellValue().getString());
		}
	}

	@Test
	public void testSetColumnValuesInvalidRowUsingFsl() throws JsonMappingException, JsonProcessingException
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, 1);
		calendar.set(Calendar.YEAR, 2022);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		String result = Fsl.execute("Xls.setRowValues", new Object[] { -1, 0, 23D, calendar, "Test" });
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals("Invalid row number (-1) outside allowable range (0..1048575)", resultNode.get("errors").get(0).asText());
	}

	@Test
	public void testSetColumnValuesInvalidCellRowUsingFsl() throws JsonMappingException, JsonProcessingException
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, 1);
		calendar.set(Calendar.YEAR, 2022);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		String result = Fsl.execute("Xls.setRowValues", new Object[] { "A 2", 23D, calendar, "Test" });
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals("Cell index must be >= 0", resultNode.get("errors").get(0).asText());
	}

	@Test
	public void testSetColumnValuesInvalidValueTypeUsingFsl() throws JsonMappingException, JsonProcessingException
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, 1);
		calendar.set(Calendar.YEAR, 2022);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		String result = Fsl.execute("Xls.setRowValues", new Object[] { "A2", new Object(), calendar, "Test" });
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals("Falscher Parameter (erlaubt sind String, Datum, Number)", resultNode.get("errors").get(0).asText());
	}

	@Test
	public void testSetCellFunctionUsingFsl() throws IOException
	{
		Xls.workbook = new XSSFWorkbook();
		XSSFSheet sheet = Xls.workbook.createSheet(SHEET_0);
		for (int i = 0; i < 10; i++)
		{
			XSSFRow row = getOrCreateRow(sheet, i);
			XSSFCell cell = getOrCreateCell(row, 0);
			cell.setCellValue(i);
			cell = getOrCreateCell(row, 1);
			cell.setCellValue(i + 3);
		}
		Object[] parameters = new Object[2];
		parameters[0] = new Object[] { 10, 0, "SUM(A1:A10)" };
		parameters[1] = new Object[] { "A11", "SUM(A1:A10)" };
		for (Object parameter : parameters)
		{
			String result = Fsl.execute("Xls.setCellFormula", Object[].class.cast(parameter));
			JsonNode resultNode = mapper.readTree(result);
			assertEquals("OK", resultNode.get("result").asText());
			FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
			XSSFRow row = sheet.getRow(10);
			XSSFCell cell = row.getCell(0);
			evaluator.evaluateFormulaCell(cell);
			assertEquals(45.0, cell.getNumericCellValue(), 0.1);
			XSSFCell copiedCell = getOrCreateCell(row, cell.getColumnIndex() + 1);
			copiedCell.copyCellFrom(cell, new CellCopyPolicy());
			copiedCell.setAsActiveCell();
		}
	}

	@Test
	public void testCopyAndShiftCellFunctionUsingFsl() throws IOException
	{
		testSetCellFunctionUsingFsl();
		Object[] parameters = new Object[] { 10, 0, 10, 1 };
		int[] columns = new int[] { 0, 1 };
		Double[] expectations = new Double[] { 45.0, 75.0 };
		checkCopyAndShifts(parameters, columns, expectations, 1);
	}

	@Test
	public void testCopyAndShiftCellFunctionRangeUsingFsl() throws IOException
	{
		testSetCellFunctionUsingFsl();
		Object[] parameters = new Object[] { 10, 0, 10, 1, 10, 2 };
		int[] columns = new int[] { 0, 1, 2 };
		Double[] expectations = new Double[] { 45.0, 75.0, 0.0 };
		checkCopyAndShifts(parameters, columns, expectations, 2);
	}

	@Test
	public void testCopyAndShiftCellFunctionWithCellAddressUsingFsl() throws IOException
	{
		testSetCellFunctionUsingFsl();
		Object[] parameters = new Object[] { "A11", "B11" };
		int[] columns = new int[] { 0, 1 };
		Double[] expectations = new Double[] { 45.0, 75.0 };
		checkCopyAndShifts(parameters, columns, expectations, 3);
	}

	@Test
	public void testCopyAndShiftCellFunctionWithCellAddressRangeUsingFsl() throws IOException
	{
		testSetCellFunctionUsingFsl();
		Object[] parameters = new Object[] { "A11", "B11", "C11" };
		int[] columns = new int[] { 0, 1, 2 };
		Double[] expectations = new Double[] { 45.0, 75.0, 0.0 };
		checkCopyAndShifts(parameters, columns, expectations, 4);
	}

	private void checkCopyAndShifts(Object[] parameters, int[] columns, Double[] expectations, int number) throws IOException
	{
		XSSFSheet sheet = Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex());
		String result = Fsl.execute("Xls.copyAndShiftFormulaCell", parameters);
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
		FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
		XSSFRow row = sheet.getRow(10);
		for (int i = 0; i < columns.length; i++)
		{
			XSSFCell cell = row.getCell(columns[i]);
			evaluator.evaluateFormulaCell(cell);
			assertEquals(expectations[i], cell.getNumericCellValue(), 0.1);

		}
		OutputStream os = new FileOutputStream(new File("workbook" + String.valueOf(number) + ".xlsx"));
		Xls.workbook.write(os);
		os.close();
	}

	@Test
	public void testSupportedFormulaNames()
	{
		for (String supportedFormulaName : FunctionEval.getSupportedFunctionNames())
		{
			System.out.println(supportedFormulaName);
		}
	}

	@Test
	public void testSave() throws JsonMappingException, JsonProcessingException
	{
		String result = Fsl.execute("Xls.save", WORKBOOK_1);
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
		assertTrue(new File(WORKBOOK_1).exists());
	}

	@Test
	public void testSetHeaderFooter() throws JsonMappingException, JsonProcessingException
	{
		String result = Fsl.execute("Xls.setHeaders", "Header links", "Header mitte", "Header rechts");
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
		result = Fsl.execute("Xls.setFooters", "Footer links", "Footer mitte", "Footerrechts");
		resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
		testSave();
	}

	@Test
	public void testCreateDebtorReport() throws JsonMappingException, JsonProcessingException
	{
		String json = "{\"column\":{\"0\":\"Rechnungsart\",\"1\":\"Nummer\",\"2\":\"Datum\",\"3\":\"Betrag\",\"4\":\"MWST-Basis\",\"5\":\"R (2.5%)\",\"6\":\"N (7.7%)\",\"7\":\"S (3.7%)\",\"8\":\"Bezahlt\"},\"details\":{\"buchung\":{\"100\":{\"0\":\"Buchung\",\"1\":\"R-0000100\",\"2\":\"09.09.2022\",\"3\":1380,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":1380},\"140\":{\"0\":\"Buchung\",\"1\":\"R-0000140\",\"2\":\"02.09.2022\",\"3\":1570,\"4\":\"Bruttobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":1570},\"141\":{\"0\":\"Buchung\",\"1\":\"R-0000141\",\"2\":\"05.09.2022\",\"3\":809,\"4\":\"Bruttobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":809},\"142\":{\"0\":\"Buchung\",\"1\":\"R-0000142\",\"2\":\"05.09.2022\",\"3\":809,\"4\":\"Bruttobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":809},\"145\":{\"0\":\"Buchung\",\"1\":\"R-0000145\",\"2\":\"16.09.2022\",\"3\":1840,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":1840},\"147\":{\"0\":\"Buchung\",\"1\":\"R-0000147\",\"2\":\"06.09.2022\",\"3\":680,\"4\":\"Bruttobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":680},\"149\":{\"0\":\"Buchung\",\"1\":\"R-0000149\",\"2\":\"07.09.2022\",\"3\":809,\"4\":\"Bruttobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":809},\"166\":{\"0\":\"Buchung\",\"1\":\"R-0000166\",\"2\":\"08.09.2022\",\"3\":1180,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":1180},\"167\":{\"0\":\"Buchung\",\"1\":\"R-0000167\",\"2\":\"12.09.2022\",\"3\":1958,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":1958},\"195\":{\"0\":\"Buchung\",\"1\":\"R-0000195\",\"2\":\"12.09.2022\",\"3\":1129,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":1129},\"196\":{\"0\":\"Buchung\",\"1\":\"R-0000196\",\"2\":\"12.09.2022\",\"3\":809,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":809},\"197\":{\"0\":\"Buchung\",\"1\":\"R-0000197\",\"2\":\"12.09.2022\",\"3\":809,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":809},\"211\":{\"0\":\"Buchung\",\"1\":\"R-0000211\",\"2\":\"13.09.2022\",\"3\":1580,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":1580},\"241\":{\"0\":\"Buchung\",\"1\":\"R-0000241\",\"2\":\"15.09.2022\",\"3\":1100,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":1100},\"242\":{\"0\":\"Buchung\",\"1\":\"R-0000242\",\"2\":\"16.09.2022\",\"3\":1280,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":1280},\"250\":{\"0\":\"Buchung\",\"1\":\"R-0000250\",\"2\":\"19.09.2022\",\"3\":1180,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":1180},\"253\":{\"0\":\"Buchung\",\"1\":\"R-0000253\",\"2\":\"19.09.2022\",\"3\":480,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":480},\"254\":{\"0\":\"Buchung\",\"1\":\"R-0000254\",\"2\":\"19.09.2022\",\"3\":1290,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":1290},\"260\":{\"0\":\"Buchung\",\"1\":\"R-0000260\",\"2\":\"20.09.2022\",\"3\":1158,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":1158},\"261\":{\"0\":\"Buchung\",\"1\":\"R-0000261\",\"2\":\"20.09.2022\",\"3\":1100,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":1100},\"262\":{\"0\":\"Buchung\",\"1\":\"R-0000262\",\"2\":\"20.09.2022\",\"3\":640,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":640},\"272\":{\"0\":\"Buchung\",\"1\":\"R-0000272\",\"2\":\"21.09.2022\",\"3\":170,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":170},\"2743\":{\"0\":\"Buchung\",\"1\":\"R-0002743\",\"2\":\"05.09.2022\",\"3\":1838,\"4\":\"Bruttobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":1838},\"280\":{\"0\":\"Buchung\",\"1\":\"R-0000280\",\"2\":\"21.09.2022\",\"3\":240,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":240},\"281\":{\"0\":\"Buchung\",\"1\":\"R-0000281\",\"2\":\"22.09.2022\",\"3\":680,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":680},\"282\":{\"0\":\"Buchung\",\"1\":\"R-0000282\",\"2\":\"22.09.2022\",\"3\":580,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":580},\"286\":{\"0\":\"Buchung\",\"1\":\"R-0000286\",\"2\":\"22.09.2022\",\"3\":1380,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":1380},\"287\":{\"0\":\"Buchung\",\"1\":\"R-0000287\",\"2\":\"23.09.2022\",\"3\":1900,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":1900},\"288\":{\"0\":\"Buchung\",\"1\":\"R-0000288\",\"2\":\"23.09.2022\",\"3\":340,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":340},\"292\":{\"0\":\"Buchung\",\"1\":\"R-0000292\",\"2\":\"27.09.2022\",\"3\":1958,\"4\":\"Bruttobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":1958},\"2924\":{\"0\":\"Buchung\",\"1\":\"R-0002924\",\"2\":\"06.09.2022\",\"3\":1059,\"4\":\"Bruttobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":1059},\"295\":{\"0\":\"Buchung\",\"1\":\"R-0000295\",\"2\":\"19.09.2022\",\"3\":3000,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":3000},\"297\":{\"0\":\"Buchung\",\"1\":\"R-0000297\",\"2\":\"26.09.2022\",\"3\":1380,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":1380},\"298\":{\"0\":\"Buchung\",\"1\":\"R-0000298\",\"2\":\"26.09.2022\",\"3\":1305,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":1305},\"299\":{\"0\":\"Buchung\",\"1\":\"R-0000299\",\"2\":\"26.09.2022\",\"3\":1305,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":1305},\"300\":{\"0\":\"Buchung\",\"1\":\"R-0000300\",\"2\":\"26.09.2022\",\"3\":1055,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":1055},\"301\":{\"0\":\"Buchung\",\"1\":\"R-0000301\",\"2\":\"26.09.2022\",\"3\":1500,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":1500},\"307\":{\"0\":\"Buchung\",\"1\":\"R-0000307\",\"2\":\"26.09.2022\",\"3\":1280,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":1280},\"309\":{\"0\":\"Buchung\",\"1\":\"R-0000309\",\"2\":\"26.09.2022\",\"3\":1380,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":1380},\"310\":{\"0\":\"Buchung\",\"1\":\"R-0000310\",\"2\":\"26.09.2022\",\"3\":2760,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":2760},\"311\":{\"0\":\"Buchung\",\"1\":\"R-0000311\",\"2\":\"26.09.2022\",\"3\":480,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":480},\"324\":{\"0\":\"Buchung\",\"1\":\"R-0000324\",\"2\":\"29.09.2022\",\"3\":480,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":480},\"342\":{\"0\":\"Buchung\",\"1\":\"R-0000342\",\"2\":\"30.09.2022\",\"3\":1100,\"4\":\"Bruttobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":1100}},\"car\":{\"162\":{\"0\":\"Car\",\"1\":\"R-0000162\",\"2\":\"08.09.2022\",\"3\":1155,\"4\":\"Bruttobetrag\",\"5\":\"0\",\"6\":\"82.6\",\"7\":\"0\",\"8\":1155},\"171\":{\"0\":\"Car\",\"1\":\"R-0000171\",\"2\":\"09.09.2022\",\"3\":800,\"4\":\"Bruttobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":800},\"172\":{\"0\":\"Car\",\"1\":\"R-0000172\",\"2\":\"09.09.2022\",\"3\":1175,\"4\":\"Bruttobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":1225},\"174\":{\"0\":\"Car\",\"1\":\"R-0000174\",\"2\":\"09.09.2022\",\"3\":1242.5,\"4\":\"Bruttobetrag\",\"5\":\"0\",\"6\":\"88.85\",\"7\":\"0\",\"8\":1242.5},\"178\":{\"0\":\"Car\",\"1\":\"R-0000178\",\"2\":\"12.09.2022\",\"3\":3455,\"4\":\"Bruttobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":3455},\"179\":{\"0\":\"Car\",\"1\":\"R-0000179\",\"2\":\"12.09.2022\",\"3\":432,\"4\":\"Bruttobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":432},\"181\":{\"0\":\"Car\",\"1\":\"R-0000181\",\"2\":\"12.09.2022\",\"3\":2300,\"4\":\"Bruttobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":2300},\"183\":{\"0\":\"Car\",\"1\":\"R-0000183\",\"2\":\"12.09.2022\",\"3\":1278,\"4\":\"Bruttobetrag\",\"5\":\"0\",\"6\":\"91.35\",\"7\":\"0\",\"8\":1278},\"184\":{\"0\":\"Car\",\"1\":\"R-0000184\",\"2\":\"12.09.2022\",\"3\":1080,\"4\":\"Bruttobetrag\",\"5\":\"0\",\"6\":\"77.2\",\"7\":\"0\",\"8\":1080},\"185\":{\"0\":\"Car\",\"1\":\"R-0000185\",\"2\":\"12.09.2022\",\"3\":1180,\"4\":\"Bruttobetrag\",\"5\":\"0\",\"6\":\"84.35\",\"7\":\"0\",\"8\":1180},\"186\":{\"0\":\"Car\",\"1\":\"R-0000186\",\"2\":\"12.09.2022\",\"3\":1678,\"4\":\"Bruttobetrag\",\"5\":\"0\",\"6\":\"119.95\",\"7\":\"0\",\"8\":1678},\"187\":{\"0\":\"Car\",\"1\":\"R-0000187\",\"2\":\"12.09.2022\",\"3\":130,\"4\":\"Bruttobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":130},\"193\":{\"0\":\"Car\",\"1\":\"R-0000193\",\"2\":\"12.09.2022\",\"3\":2346.4,\"4\":\"Bruttobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":2346.4},\"239\":{\"0\":\"Car\",\"1\":\"R-0000239\",\"2\":\"15.09.2022\",\"3\":712,\"4\":\"Bruttobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":712},\"240\":{\"0\":\"Car\",\"1\":\"R-0000240\",\"2\":\"19.09.2022\",\"3\":1080,\"4\":\"Bruttobetrag\",\"5\":\"0\",\"6\":\"77.2\",\"7\":\"0\",\"8\":1080},\"255\":{\"0\":\"Car\",\"1\":\"R-0000255\",\"2\":\"19.09.2022\",\"3\":1407.5,\"4\":\"Bruttobetrag\",\"5\":\"0\",\"6\":\"100.65\",\"7\":\"0\",\"8\":1407.5},\"257\":{\"0\":\"Car\",\"1\":\"R-0000257\",\"2\":\"19.09.2022\",\"3\":2485,\"4\":\"Bruttobetrag\",\"5\":\"0\",\"6\":\"177.65\",\"7\":\"0\",\"8\":2685},\"258\":{\"0\":\"Car\",\"1\":\"R-0000258\",\"2\":\"19.09.2022\",\"3\":1285,\"4\":\"Bruttobetrag\",\"5\":\"0\",\"6\":\"91.85\",\"7\":\"0\",\"8\":1285},\"340\":{\"0\":\"Car\",\"1\":\"R-0000340\",\"2\":\"30.09.2022\",\"3\":0,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":0}},\"lager\":{\"153\":{\"0\":\"Lager\",\"1\":\"R-0000153\",\"2\":\"21.09.2022\",\"3\":387.7,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"27.7\",\"7\":\"0\",\"8\":387.7},\"201\":{\"0\":\"Lager\",\"1\":\"R-0000201\",\"2\":\"21.09.2022\",\"3\":775.45,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"55.45\",\"7\":\"0\",\"8\":775.45},\"203\":{\"0\":\"Lager\",\"1\":\"R-0000203\",\"2\":\"21.09.2022\",\"3\":161.55,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"11.55\",\"7\":\"0\",\"8\":161.55},\"204\":{\"0\":\"Lager\",\"1\":\"R-0000204\",\"2\":\"21.09.2022\",\"3\":161.55,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"11.55\",\"7\":\"0\",\"8\":161.55},\"206\":{\"0\":\"Lager\",\"1\":\"R-0000206\",\"2\":\"21.09.2022\",\"3\":161.55,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"11.55\",\"7\":\"0\",\"8\":161.55},\"212\":{\"0\":\"Lager\",\"1\":\"R-0000212\",\"2\":\"21.09.2022\",\"3\":161.55,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"11.55\",\"7\":\"0\",\"8\":161.55},\"214\":{\"0\":\"Lager\",\"1\":\"R-0000214\",\"2\":\"21.09.2022\",\"3\":193.85,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"13.85\",\"7\":\"0\",\"8\":193.85},\"219\":{\"0\":\"Lager\",\"1\":\"R-0000219\",\"2\":\"21.09.2022\",\"3\":310.2,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"22.2\",\"7\":\"0\",\"8\":310.2},\"220\":{\"0\":\"Lager\",\"1\":\"R-0000220\",\"2\":\"21.09.2022\",\"3\":232.65,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"16.65\",\"7\":\"0\",\"8\":232.65},\"229\":{\"0\":\"Lager\",\"1\":\"R-0000229\",\"2\":\"21.09.2022\",\"3\":161.55,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"11.55\",\"7\":\"0\",\"8\":161.55},\"232\":{\"0\":\"Lager\",\"1\":\"R-0000232\",\"2\":\"21.09.2022\",\"3\":581.6,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"41.6\",\"7\":\"0\",\"8\":581.6},\"233\":{\"0\":\"Lager\",\"1\":\"R-0000233\",\"2\":\"21.09.2022\",\"3\":310.2,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"22.2\",\"7\":\"0\",\"8\":310.2},\"234\":{\"0\":\"Lager\",\"1\":\"R-0000234\",\"2\":\"21.09.2022\",\"3\":161.55,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"11.55\",\"7\":\"0\",\"8\":1350},\"236\":{\"0\":\"Lager\",\"1\":\"R-0000236\",\"2\":\"21.09.2022\",\"3\":0,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"0\",\"7\":\"0\",\"8\":0}},\"transport\":{\"154\":{\"0\":\"Transport\",\"1\":\"R-0000154\",\"2\":\"09.09.2022\",\"3\":1453.95,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"103.95\",\"7\":\"0\",\"8\":1453.95},\"173\":{\"0\":\"Transport\",\"1\":\"R-0000173\",\"2\":\"09.09.2022\",\"3\":218.65,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"15.65\",\"7\":\"0\",\"8\":218.65},\"263\":{\"0\":\"Transport\",\"1\":\"R-0000263\",\"2\":\"20.09.2022\",\"3\":157.25,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"11.25\",\"7\":\"0\",\"8\":157.25},\"285\":{\"0\":\"Transport\",\"1\":\"R-0000285\",\"2\":\"22.09.2022\",\"3\":2130.3,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"152.3\",\"7\":\"0\",\"8\":2130.3},\"293\":{\"0\":\"Transport\",\"1\":\"R-0000293\",\"2\":\"23.09.2022\",\"3\":2219.15,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"158.65\",\"7\":\"0\",\"8\":2219.15},\"320\":{\"0\":\"Transport\",\"1\":\"R-0000320\",\"2\":\"28.09.2022\",\"3\":437.25,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"31.25\",\"7\":\"0\",\"8\":437.25},\"321\":{\"0\":\"Transport\",\"1\":\"R-0000321\",\"2\":\"28.09.2022\",\"3\":141.1,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"10.1\",\"7\":\"0\",\"8\":141.1},\"329\":{\"0\":\"Transport\",\"1\":\"R-0000329\",\"2\":\"30.09.2022\",\"3\":109.3,\"4\":\"Nettobetrag\",\"5\":\"0\",\"6\":\"7.8\",\"7\":\"0\",\"8\":109.3}}},\"path\":\"/Users/christian/myWorkbook.xlsx\",\"sheet\":{\"header\":{\"from\":\"01.09.2022\",\"period\":\"Monat\",\"titel\":\"Bezahlte Rechnungen\",\"title\":\"Bezahlte Rechnungen\",\"to\":\"30.09.2022\"}}}";
		String result = Fsl.execute("Xls.createDebtorReport", new Object[] { json });
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
	}

//	@Test
//	public void testDocumentWithHeadersAndValues() throws IOException
//	{
//
//		String result = Fsl.execute("Xls.createWorkbook", new Object[] { WORKBOOK_1 });
//		JsonNode resultNode = mapper.readTree(result);
//		assertEquals("OK", resultNode.get("result").asText());
//		result = Fsl.execute("Xls.createSheet", new Object[] { SHEET_0 });
//		resultNode = mapper.readTree(result);
//		assertEquals("OK", resultNode.get("result").asText());
//		result = Fsl.execute("Xls.setHeadings", new Object[] { 0, "StringHeader", "NumericHeader", "SumHeader" });
//		resultNode = mapper.readTree(result);
//		assertEquals("OK", resultNode.get("result").asText());
//		result = Fsl.execute("Xls.setRowValues", new Object[] { 1, 0, 1, "First row", 1064D });
//		resultNode = mapper.readTree(result);
//		assertEquals("OK", resultNode.get("result").asText());
//		result = Fsl.execute("Xls.setColumnSum", new Object[] { 2, 2, 1, 1, 1 });
//		resultNode = mapper.readTree(result);
//		assertEquals("OK", resultNode.get("result").asText());
//		result = Fsl.execute("Xls.setRowStyleNumber", new Object[] { 1, 1, 2, "0.00" });
//		resultNode = mapper.readTree(result);
//		assertEquals("OK", resultNode.get("result").asText());
//		result = Fsl.execute("Xls.setColumnStyleBold", new Object[] { 2, 2, 1 });
//		resultNode = mapper.readTree(result);
//		assertEquals("OK", resultNode.get("result").asText());
//		result = Fsl.execute("Xls.save", new Object[] { WORKBOOK_1 });
//		resultNode = mapper.readTree(result);
//		assertEquals("OK", resultNode.get("result").asText());
//		if (Desktop.isDesktopSupported())
//		{
//			Desktop desktop = Desktop.getDesktop();
//			desktop.edit(new File(WORKBOOK_1));
//		}
//	}

	private static void clearWorkbook()
	{
		Xls.workbook = null;
	}
}
