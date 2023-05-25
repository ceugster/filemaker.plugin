package ch.eugster.filemaker.fsl.plugin.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.PrintOrientation;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ch.eugster.filemaker.fsl.plugin.Executor;
import ch.eugster.filemaker.fsl.plugin.Fsl;
import ch.eugster.filemaker.fsl.plugin.xls.Xls;

public class XlsFontTest extends XlsTest
{
	@BeforeEach
	public void beforeEach()
	{
		releaseAllWorkbooks();
	}

	@Test
	public void testApplyFontStylesToCell() throws EncryptedDocumentException, IOException
	{
		openExistingWorkbook("./resources/xls/applyFontStyles.xlsx");

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
		openExistingWorkbook("./resources/xls/applyFontStyles.xlsx");

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
		openExistingWorkbook("./resources/xls/applyFontStyles.xlsx");

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
		openExistingWorkbook("./resources/xls/applyFontStyles.xlsx");

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
		openExistingWorkbook("./resources/xls/applyFontStyles.xlsx");

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
		openExistingWorkbook("./resources/xls/applyFontStyles.xlsx");

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
		openExistingWorkbook("./resources/xls/applyFontStyles.xlsx");

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
		openExistingWorkbook("./resources/xls/applyFontStyles.xlsx");

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
		openExistingWorkbook("./resources/xls/applyFontStyles.xlsx");

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
		openExistingWorkbook("./resources/xls/applyFontStyles.xlsx");

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
		openExistingWorkbook("./resources/xls/applyFontStylesRange.xlsx");

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
		openExistingWorkbook("./resources/xls/applyFontStylesRange.xlsx");

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
}
