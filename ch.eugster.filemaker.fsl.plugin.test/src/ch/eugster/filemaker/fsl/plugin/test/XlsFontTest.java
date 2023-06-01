package ch.eugster.filemaker.fsl.plugin.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.PrintOrientation;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ch.eugster.filemaker.fsl.plugin.Executor;
import ch.eugster.filemaker.fsl.plugin.Fsl;

public final class XlsFontTest extends AbstractXlsTest
{
	@Test
	public void testApplyFontStylesToCell() throws EncryptedDocumentException, IOException
	{
		openExistingWorkbook("./resources/xls/applyFontStylesToCell.xlsx");

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("format", "Arial");
		requestNode.put("style", 0);
		requestNode.put("size", 14);
		ArrayNode arrayNode = requestNode.arrayNode();
		arrayNode.add(254);
		arrayNode.add(0);
		arrayNode.add(0);
		requestNode.set("color", arrayNode);
		requestNode.put("cell", "A1");
		
		String response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		Font font = xls.activeWorkbook.getFontAt(xls.activeWorkbook.getSheetAt(xls.activeWorkbook.getActiveSheetIndex()).getRow(0).getCell(0).getCellStyle().getFontIndex());
		assertEquals(false, font.getBold());
		assertEquals(false, font.getItalic());
		
		requestNode = mapper.createObjectNode();
		requestNode.put("style", 1);
		requestNode.put("cell", "B2");
		response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		Sheet sheet = xls.activeWorkbook.getSheetAt(xls.activeWorkbook.getActiveSheetIndex());
		font = xls.activeWorkbook.getFontAt(sheet.getRow(1).getCell(1).getCellStyle().getFontIndex());
//		assertEquals(true, font.getBold());
//		assertEquals(false, font.getItalic());

		requestNode = mapper.createObjectNode();
		requestNode.put("style", 2);
		requestNode.put("cell", "C3");
		response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		font = xls.activeWorkbook.getFontAt(sheet.getRow(2).getCell(2).getCellStyle().getFontIndex());
//		assertEquals(false, font.getBold());
//		assertEquals(true, font.getItalic());

		requestNode = mapper.createObjectNode();
		requestNode.put("style", 3);
		requestNode.put("cell", "D4");
		response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		font = xls.activeWorkbook.getFontAt(sheet.getRow(3).getCell(3).getCellStyle().getFontIndex());
//		assertEquals(true, font.getBold());
//		assertEquals(true, font.getItalic());
		
		requestNode = mapper.createObjectNode();
		requestNode.put("target", "./targets/applyFontStylesToCell.xlsx");
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
	}

	@Test
	public void testApplyFontStyleBold() throws EncryptedDocumentException, IOException
	{
		openExistingWorkbook("./resources/xls/applyFontStylesBold.xlsx");
		System.out.println(xls.activeWorkbook);
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("bold", 1);
		requestNode.put("range", "A1:D1");
		
		String response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(2, xls.activeWorkbook.getNumberOfFonts());

		requestNode = mapper.createObjectNode();
		requestNode.put("target", "./targets/applyFontStylesBold.xlsx");
		response = Fsl.execute("Xls.saveWorkbook", requestNode.toString());

		Sheet sheet = xls.activeWorkbook.getSheetAt(xls.activeWorkbook.getActiveSheetIndex());
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Font font = xls.activeWorkbook.getFontAt(sheet.getRow(i).getCell(j).getCellStyle().getFontIndex());
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

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("bold", "A");
		requestNode.put("range", "A1:D1");
		
		String response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(1, xls.activeWorkbook.getNumberOfFonts());

		Sheet sheet = xls.activeWorkbook.getSheetAt(xls.activeWorkbook.getActiveSheetIndex());
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Font font = xls.activeWorkbook.getFontAt(sheet.getRow(i).getCell(j).getCellStyle().getFontIndex());
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
		openExistingWorkbook("./resources/xls/applyFontStylesItalic.xlsx");

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("italic", 1);
		requestNode.put("range", "A1:D1");
		
		String response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(2, xls.activeWorkbook.getNumberOfFonts());

		requestNode = mapper.createObjectNode();
		requestNode.put("target", "./targets/applyFontStylesItalic.xlsx");
		response = Fsl.execute("Xls.saveWorkbook", requestNode.toString());

		Sheet sheet = xls.activeWorkbook.getSheetAt(xls.activeWorkbook.getActiveSheetIndex());
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Font font = xls.activeWorkbook.getFontAt(sheet.getRow(i).getCell(j).getCellStyle().getFontIndex());
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
		openExistingWorkbook("./resources/xls/applyFontStylesUnderline.xlsx");

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("underline", Font.U_DOUBLE);
		requestNode.put("range", "A1:D1");
		
		String response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(2, xls.activeWorkbook.getNumberOfFonts());

		requestNode = mapper.createObjectNode();
		requestNode.put("target", "./targets/applyFontStylesUnderline.xlsx");
		response = Fsl.execute("Xls.saveWorkbook", requestNode.toString());

		Sheet sheet = xls.activeWorkbook.getSheetAt(xls.activeWorkbook.getActiveSheetIndex());
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Font font = xls.activeWorkbook.getFontAt(sheet.getRow(i).getCell(j).getCellStyle().getFontIndex());
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
		openExistingWorkbook("./resources/xls/applyFontStylesColor.xlsx");

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("color", Font.COLOR_RED);
		requestNode.put("range", "A1:D1");
		
		String response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(2, xls.activeWorkbook.getNumberOfFonts());

		requestNode = mapper.createObjectNode();
		requestNode.put("target", "./targets/applyFontStylesColor.xlsx");
		response = Fsl.execute("Xls.saveWorkbook", requestNode.toString());

		Sheet sheet = xls.activeWorkbook.getSheetAt(xls.activeWorkbook.getActiveSheetIndex());
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Font font = xls.activeWorkbook.getFontAt(sheet.getRow(i).getCell(j).getCellStyle().getFontIndex());
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

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("orientation", PrintOrientation.LANDSCAPE.name().toLowerCase());
		requestNode.put("copies", 2);
		
		String response = Fsl.execute("Xls.setPrintSetup", requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put("target", "./targets/printSetup.xlsx");
		response = Fsl.execute("Xls.saveWorkbook", requestNode.toString());
	}
	
	@Test
	public void testApplyFontStyleSize() throws EncryptedDocumentException, IOException
	{
		openExistingWorkbook("./resources/xls/applyFontStylesSize.xlsx");

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("size", 30);
		requestNode.put("range", "A1:D1");
		
		String response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(2, xls.activeWorkbook.getNumberOfFonts());

		requestNode = mapper.createObjectNode();
		requestNode.put("target", "./targets/applyFontStylesSize.xlsx");
		response = Fsl.execute("Xls.saveWorkbook", requestNode.toString());

		Sheet sheet = xls.activeWorkbook.getSheetAt(xls.activeWorkbook.getActiveSheetIndex());
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Font font = xls.activeWorkbook.getFontAt(sheet.getRow(i).getCell(j).getCellStyle().getFontIndex());
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
		openExistingWorkbook("./resources/xls/applyFontStylesName.xlsx");

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("name", "Courier new");
		requestNode.put("range", "A1:D1");
		
		String response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(2, xls.activeWorkbook.getNumberOfFonts());

		requestNode = mapper.createObjectNode();
		requestNode.put("target", "./targets/applyFontStylesName.xlsx");
		response = Fsl.execute("Xls.saveWorkbook", requestNode.toString());

		Sheet sheet = xls.activeWorkbook.getSheetAt(xls.activeWorkbook.getActiveSheetIndex());
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Font font = xls.activeWorkbook.getFontAt(sheet.getRow(i).getCell(j).getCellStyle().getFontIndex());
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
		openExistingWorkbook("./resources/xls/applyFontStylesStrikeOut.xlsx");

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("strike_out", 1);
		requestNode.put("range", "A1:D1");
		
		String response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(2, xls.activeWorkbook.getNumberOfFonts());

		requestNode = mapper.createObjectNode();
		requestNode.put("target", "./targets/applyFontStylesStrikeOut.xlsx");
		response = Fsl.execute("Xls.saveWorkbook", requestNode.toString());

		Sheet sheet = xls.activeWorkbook.getSheetAt(xls.activeWorkbook.getActiveSheetIndex());
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Font font = xls.activeWorkbook.getFontAt(sheet.getRow(i).getCell(j).getCellStyle().getFontIndex());
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
		openExistingWorkbook("./resources/xls/applyFontStylesTypeOffset.xlsx");

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("type_offset", Font.SS_SUPER);
		requestNode.put("range", "A1:D1");
		
		String response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(2, xls.activeWorkbook.getNumberOfFonts());

		requestNode = mapper.createObjectNode();
		requestNode.put("target", "./targets/applyFontStylesTypeOffset.xlsx");
		response = Fsl.execute("Xls.saveWorkbook", requestNode.toString());

		Sheet sheet = xls.activeWorkbook.getSheetAt(xls.activeWorkbook.getActiveSheetIndex());
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Font font = xls.activeWorkbook.getFontAt(sheet.getRow(i).getCell(j).getCellStyle().getFontIndex());
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
		openExistingWorkbook("./resources/xls/applyFontStylesBoldToRange.xlsx");

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("bold", 1);
		requestNode.put("range", "A1:D1");
		
		String response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		assertEquals(2, xls.activeWorkbook.getNumberOfFonts());

		requestNode = mapper.createObjectNode();
		requestNode.put("target", "./targets/applyFontStylesBoldToRange.xlsx");
		response = Fsl.execute("Xls.saveWorkbook", requestNode.toString());

		Sheet sheet = xls.activeWorkbook.getSheetAt(xls.activeWorkbook.getActiveSheetIndex());
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Font font = xls.activeWorkbook.getFontAt(sheet.getRow(i).getCell(j).getCellStyle().getFontIndex());
				assertEquals(i == 0 ? true : false, font.getBold());
				assertEquals(false, font.getItalic());
				assertEquals(Font.U_NONE, font.getUnderline());
			}			
		}
	}
	
	@Test
	public void testApplyFontStyleItalicToRange() throws EncryptedDocumentException, IOException
	{
		openExistingWorkbook("./resources/xls/applyFontStylesItalicToRange.xlsx");

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("bold", 1);
		requestNode.put("range", "A1:D1");
		
		String response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		Sheet sheet = xls.activeWorkbook.getSheetAt(xls.activeWorkbook.getActiveSheetIndex());
		Font font = xls.activeWorkbook.getFontAt(sheet.getRow(0).getCell(0).getCellStyle().getFontIndex());
		assertEquals(true, font.getBold());
		assertEquals(false, font.getItalic());
		assertEquals(Font.U_NONE, font.getUnderline());
		
		requestNode = mapper.createObjectNode();
		requestNode.put("italic", 1);
		requestNode.put("range", "A2:D2");
		
		response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		font = xls.activeWorkbook.getFontAt(sheet.getRow(1).getCell(0).getCellStyle().getFontIndex());
		assertEquals(false, font.getBold());
		assertEquals(true, font.getItalic());
		assertEquals(Font.U_NONE, font.getUnderline());

		requestNode = mapper.createObjectNode();
		requestNode.put("bold", 1);
		requestNode.put("italic", 1);
		requestNode.put("range", "A3:D3");
		
		response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		font = xls.activeWorkbook.getFontAt(sheet.getRow(2).getCell(0).getCellStyle().getFontIndex());
		assertEquals(true, font.getBold());
		assertEquals(true, font.getItalic());
		assertEquals(Font.U_NONE, font.getUnderline());

		requestNode = mapper.createObjectNode();
		requestNode.put("underline", Font.U_DOUBLE);
		requestNode.put("range", "A4:D4");
		
		response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		font = xls.activeWorkbook.getFontAt(sheet.getRow(3).getCell(0).getCellStyle().getFontIndex());
		assertEquals(false, font.getBold());
		assertEquals(false, font.getItalic());
		assertEquals(Font.U_DOUBLE, font.getUnderline());
		
		requestNode = mapper.createObjectNode();
		requestNode.put("target", "./targets/applyFontStylesItalicToRange.xlsx");
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
	}
	
	@Test
	public void testFonts() throws JsonMappingException, JsonProcessingException
	{
		Sheet sheet = prepareWorkbookAndSheetIfMissing();
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue(new XSSFRichTextString("Das ist ein Test"));
		row = sheet.createRow(1);
		cell = row.createCell(0);
		cell.setCellValue(new XSSFRichTextString("Das ist ein weiterer Test"));

		int numberOfFonts = sheet.getWorkbook().getNumberOfFonts();
		System.out.println("Fonts im Workbook");
		for (int i = 0; i < numberOfFonts; i++)
		{
			Font font = sheet.getWorkbook().getFontAt(i);
			System.out.println(font.getFontName() + ", Size: " + font.getFontHeightInPoints() + ", Bold: " + font.getBold() + ", Italic: " + font.getItalic() + ", Underline: " + font.getUnderline() + ", Strikeout: " + font.getStrikeout() + ", Type Offset: " + font.getTypeOffset() + ", Color: " + font.getColor());
		}
		
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("name", "Arial");
		requestNode.put("size", 30);
		requestNode.put("cell", "A1");
		
		String response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get("index").asInt());
		numberOfFonts = sheet.getWorkbook().getNumberOfFonts();
		assertEquals(2, numberOfFonts);

		requestNode = mapper.createObjectNode();
		requestNode.put("name", "Courier New");
		requestNode.put("size", 24);
		requestNode.put("bold", 1);
		requestNode.put("cell", "A2");
		
		response = Fsl.execute("Xls.applyFontStyles", requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertEquals(2, responseNode.get("index").asInt());
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
		
		requestNode = mapper.createObjectNode();
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
	}
}
