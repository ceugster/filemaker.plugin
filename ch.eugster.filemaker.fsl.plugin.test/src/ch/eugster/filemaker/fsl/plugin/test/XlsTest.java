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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
		String result = Fsl.execute("Xls.createWorkbook",
				new Object[] { System.getProperty("user.home") + "/blabla/wb.xlsx" });
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
		assertEquals("The workbook already contains a sheet named '" + SHEET_0 + "'",
				resultNode.get("errors").get(0).asText());
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
		Object[] parameters = new Object[] { 0, startColumnIndex, "Column1", "Column2", "Column3", "Column4",
				"Column5" };
		Xls.setHeadingsHorizontal(parameters);
		assertEquals(0, Xls.workbook.getSheetAt(0).getFirstRowNum());
		for (int i = 0; i < parameters.length - startColumnIndex; i++)
		{
			assertEquals("Column" + String.valueOf(i + 1), Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex())
					.getRow(0).getCell(startColumnIndex + i).getRichStringCellValue().getString());
		}
		parameters = new Object[] { "D1", "Column1", "Column2", "Column3", "Column4", "Column5" };
		Xls.setHeadingsHorizontal(parameters);
		assertEquals(0, Xls.workbook.getSheetAt(0).getFirstRowNum());
		for (int i = 0; i < parameters.length - startColumnIndex; i++)
		{
			assertEquals("Column" + String.valueOf(i + 1), Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex())
					.getRow(0).getCell(startColumnIndex + i).getRichStringCellValue().getString());
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
		String result = Fsl.execute("Xls.createWorkbook",
				new Object[] { System.getProperty("user.home") + "/blabla/wb.xlsx" });
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
		Object[] parameters = new Object[] { 0, startColumnIndex, "Column1", "Column2", "Column3", "Column4",
				"Column5" };
		String result = Fsl.execute("Xls.setHeadingsHorizontal", parameters);
		JsonNode resultNode = mapper.readTree(result);
		resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
		assertEquals(0, Xls.workbook.getSheetAt(0).getFirstRowNum());
		for (int i = 0; i < parameters.length - startColumnIndex; i++)
		{
			assertEquals("Column" + String.valueOf(i + 1), Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex())
					.getRow(0).getCell(startColumnIndex + i).getRichStringCellValue().getString());
		}
		parameters = new Object[] { "D1", "Column1", "Column2", "Column3", "Column4", "Column5" };
		result = Fsl.execute("Xls.setHeadingsHorizontal", parameters);
		resultNode = mapper.readTree(result);
		resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
		assertEquals(0, Xls.workbook.getSheetAt(0).getFirstRowNum());
		for (int i = 0; i < parameters.length - startColumnIndex; i++)
		{
			assertEquals("Column" + String.valueOf(i + 1), Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex())
					.getRow(0).getCell(startColumnIndex + i).getRichStringCellValue().getString());
		}
	}

	@Test
	public void testSetCellValueUsingFsl() throws JsonMappingException, JsonProcessingException
	{
		String result = Fsl.execute("Xls.setCellValue", new Object[] { 1, 0, 23D });
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
		assertEquals(23D,
				Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex()).getRow(1).getCell(0).getNumericCellValue(),
				0.1);
		result = Fsl.execute("Xls.setCellValue", new Object[] { "A2", 23D });
		resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
		assertEquals(23D,
				Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex()).getRow(1).getCell(0).getNumericCellValue(),
				0.1);
	}

	@Test
	public void testSetCellValueWithInvalidRowUsingFsl() throws JsonMappingException, JsonProcessingException
	{
		String result = Fsl.execute("Xls.setCellValue", new Object[] { -120, 120, 23D });
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals("Invalid row number (-120) outside allowable range (0..1048575)",
				resultNode.get("errors").get(0).asText());
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
		assertEquals(
				"Cannot invoke \"org/apache/poi/xssf/usermodel/XSSFRow.getCell(I)Lorg/apache/poi/xssf/usermodel/XSSFCell;\"",
				resultNode.get("errors").get(0).asText());
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
			assertEquals(23D, Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex()).getRow(1).getCell(0)
					.getNumericCellValue(), 0.1);
			assertEquals(calendar.getTime(), Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex()).getRow(1)
					.getCell(1).getDateCellValue());
			assertEquals("Test", Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex()).getRow(1).getCell(2)
					.getRichStringCellValue().getString());
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
		assertEquals("Invalid row number (-1) outside allowable range (0..1048575)",
				resultNode.get("errors").get(0).asText());
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
		assertEquals("Falscher Parameter (erlaubt sind String, Datum, Number)",
				resultNode.get("errors").get(0).asText());
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
			assertEquals(23D, Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex()).getRow(1).getCell(0)
					.getNumericCellValue(), 0.1);
			assertEquals(calendar.getTime(), Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex()).getRow(1)
					.getCell(1).getDateCellValue());
			assertEquals("Test", Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex()).getRow(1).getCell(2)
					.getRichStringCellValue().getString());
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
		assertEquals("Invalid row number (-1) outside allowable range (0..1048575)",
				resultNode.get("errors").get(0).asText());
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
		assertEquals("Falscher Parameter (erlaubt sind String, Datum, Number)",
				resultNode.get("errors").get(0).asText());
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

	private void checkCopyAndShifts(Object[] parameters, int[] columns, Double[] expectations, int number)
			throws IOException
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
