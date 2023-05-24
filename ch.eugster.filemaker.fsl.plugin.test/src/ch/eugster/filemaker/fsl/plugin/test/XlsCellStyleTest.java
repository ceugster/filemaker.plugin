package ch.eugster.filemaker.fsl.plugin.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import ch.eugster.filemaker.fsl.plugin.Executor;
import ch.eugster.filemaker.fsl.plugin.Fsl;
import ch.eugster.filemaker.fsl.plugin.xls.Xls;

public class XlsCellStyleTest extends XlsTest
{
	@Test
	public void testHorizontalAlignment() throws EncryptedDocumentException, IOException
	{
		prepareWorkbookAndSheetIfMissing();
		Sheet sheet = getActiveSheet();
		Row row = sheet.createRow(1);
		Cell leftCell = row.createCell(1);
		leftCell.setCellValue(new XSSFRichTextString("Linksausrichtung"));
		Cell centerCell = row.createCell(3);
		centerCell.setCellValue(new XSSFRichTextString("Mitteausrichtung"));
		Cell rightCell = row.createCell(5);
		rightCell.setCellValue(new XSSFRichTextString("Rechtsausrichtung"));
		Cell distributedCell = row.createCell(7);
		distributedCell.setCellValue(new XSSFRichTextString("Verteilt auf die ganze Breite"));
		Cell errorCell = row.createCell(9);
		errorCell.setCellValue(new XSSFRichTextString("Fehler"));
		
		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "B2");
		ObjectNode alignNode = requestNode.objectNode();
		alignNode.put(Key.HORIZONTAL.key(), HorizontalAlignment.LEFT.name().toLowerCase());
		requestNode.set(Key.ALIGNMENT.key(), alignNode);
		
		String response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "D2");
		alignNode = requestNode.objectNode();
		alignNode.put(Key.HORIZONTAL.key(), HorizontalAlignment.CENTER.name().toLowerCase());
		requestNode.set(Key.ALIGNMENT.key(), alignNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "F2");
		alignNode = requestNode.objectNode();
		alignNode.put(Key.HORIZONTAL.key(), HorizontalAlignment.RIGHT.name().toLowerCase());
		requestNode.set(Key.ALIGNMENT.key(), alignNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "H2");
		alignNode = requestNode.objectNode();
		alignNode.put(Key.HORIZONTAL.key(), HorizontalAlignment.DISTRIBUTED.name().toLowerCase());
		requestNode.set(Key.ALIGNMENT.key(), alignNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "J2");
		alignNode = requestNode.objectNode();
		alignNode.put(Key.HORIZONTAL.key(), "gigi");
		requestNode.set(Key.ALIGNMENT.key(), alignNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("invalid argument 'alignment.horizontal'", responseNode.get(Executor.ERRORS).get(0).asText());

		requestNode = MAPPER.createObjectNode();
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}
	
	@Test
	public void testHorizontalAlignmentRange() throws EncryptedDocumentException, IOException
	{
		prepareWorkbookAndSheetIfMissing();
		Sheet sheet = getActiveSheet();
		Row row = sheet.createRow(1);
		Cell leftCell = row.createCell(1);
		leftCell.setCellValue(new XSSFRichTextString("Linksausrichtung"));
		Cell centerCell = row.createCell(3);
		centerCell.setCellValue(new XSSFRichTextString("Mitteausrichtung"));
		Cell rightCell = row.createCell(5);
		rightCell.setCellValue(new XSSFRichTextString("Rechtsausrichtung"));
		Cell distributedCell = row.createCell(7);
		distributedCell.setCellValue(new XSSFRichTextString("Verteilt auf die ganze Breite"));
		Cell errorCell = row.createCell(9);
		errorCell.setCellValue(new XSSFRichTextString("Fehler"));
		row = sheet.createRow(3);
		leftCell = row.createCell(1);
		leftCell.setCellValue(new XSSFRichTextString("Linksausrichtung"));
		centerCell = row.createCell(3);
		centerCell.setCellValue(new XSSFRichTextString("Mitteausrichtung"));
		rightCell = row.createCell(5);
		rightCell.setCellValue(new XSSFRichTextString("Rechtsausrichtung"));
		distributedCell = row.createCell(7);
		distributedCell.setCellValue(new XSSFRichTextString("Verteilt auf die ganze Breite"));
		errorCell = row.createCell(9);
		errorCell.setCellValue(new XSSFRichTextString("Fehler"));
		
		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.RANGE.key(), "B2:B4");
		ObjectNode alignNode = requestNode.objectNode();
		alignNode.put(Key.HORIZONTAL.key(), HorizontalAlignment.LEFT.name().toLowerCase());
		requestNode.set(Key.ALIGNMENT.key(), alignNode);
		
		String response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.RANGE.key(), "D2:D4");
		alignNode = requestNode.objectNode();
		alignNode.put(Key.HORIZONTAL.key(), HorizontalAlignment.CENTER.name().toLowerCase());
		requestNode.set(Key.ALIGNMENT.key(), alignNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.RANGE.key(), "F2:F4");
		alignNode = requestNode.objectNode();
		alignNode.put(Key.HORIZONTAL.key(), HorizontalAlignment.RIGHT.name().toLowerCase());
		requestNode.set(Key.ALIGNMENT.key(), alignNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.RANGE.key(), "H2:H4");
		alignNode = requestNode.objectNode();
		alignNode.put(Key.HORIZONTAL.key(), HorizontalAlignment.DISTRIBUTED.name().toLowerCase());
		requestNode.set(Key.ALIGNMENT.key(), alignNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.RANGE.key(), "J2:J4");
		alignNode = requestNode.objectNode();
		alignNode.put(Key.HORIZONTAL.key(), "gigi");
		requestNode.set(Key.ALIGNMENT.key(), alignNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("invalid argument 'alignment.horizontal'", responseNode.get(Executor.ERRORS).get(0).asText());

		requestNode = MAPPER.createObjectNode();
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}
	
	@Test
	public void testVerticalAlignment() throws EncryptedDocumentException, IOException
	{
		prepareWorkbookAndSheetIfMissing();
		Sheet sheet = getActiveSheet();
		Row row = sheet.createRow(1);
		Cell leftCell = row.createCell(1);
		leftCell.setCellValue(new XSSFRichTextString("Top"));
		Cell centerCell = row.createCell(3);
		centerCell.setCellValue(new XSSFRichTextString("Center"));
		Cell rightCell = row.createCell(5);
		rightCell.setCellValue(new XSSFRichTextString("Bottom"));
		Cell distributedCell = row.createCell(7);
		distributedCell.setCellValue(new XSSFRichTextString("Bottom"));
		Cell errorCell = row.createCell(9);
		errorCell.setCellValue(new XSSFRichTextString("Fehler"));
		
		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "B2");
		ObjectNode alignNode = requestNode.objectNode();
		alignNode.put(Key.VERTICAL.key(), VerticalAlignment.TOP.name().toLowerCase());
		requestNode.set(Key.ALIGNMENT.key(), alignNode);
		
		String response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "D2");
		alignNode = requestNode.objectNode();
		alignNode.put(Key.VERTICAL.key(), VerticalAlignment.CENTER.name().toLowerCase());
		requestNode.set(Key.ALIGNMENT.key(), alignNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "F2");
		alignNode = requestNode.objectNode();
		alignNode.put(Key.VERTICAL.key(), VerticalAlignment.BOTTOM.name().toLowerCase());
		requestNode.set(Key.ALIGNMENT.key(), alignNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "H2");
		alignNode = requestNode.objectNode();
		alignNode.put(Key.VERTICAL.key(), VerticalAlignment.DISTRIBUTED.name().toLowerCase());
		requestNode.set(Key.ALIGNMENT.key(), alignNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "J2");
		alignNode = requestNode.objectNode();
		alignNode.put(Key.VERTICAL.key(), "gigi");
		requestNode.set(Key.ALIGNMENT.key(), alignNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("invalid argument 'alignment.vertical'", responseNode.get(Executor.ERRORS).get(0).asText());

		requestNode = MAPPER.createObjectNode();
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}
	
	@Test
	public void testVerticalAlignmentRange() throws EncryptedDocumentException, IOException
	{
		prepareWorkbookAndSheetIfMissing();
		Sheet sheet = getActiveSheet();
		Row row = sheet.createRow(1);
		Cell leftCell = row.createCell(1);
		leftCell.setCellValue(new XSSFRichTextString("Top"));
		Cell centerCell = row.createCell(3);
		centerCell.setCellValue(new XSSFRichTextString("Center"));
		Cell rightCell = row.createCell(5);
		rightCell.setCellValue(new XSSFRichTextString("Bottom"));
		Cell distributedCell = row.createCell(7);
		distributedCell.setCellValue(new XSSFRichTextString("Bottom"));
		Cell errorCell = row.createCell(9);
		errorCell.setCellValue(new XSSFRichTextString("Fehler"));
		row = sheet.createRow(3);
		leftCell = row.createCell(1);
		leftCell.setCellValue(new XSSFRichTextString("Top"));
		centerCell = row.createCell(3);
		centerCell.setCellValue(new XSSFRichTextString("Center"));
		rightCell = row.createCell(5);
		rightCell.setCellValue(new XSSFRichTextString("Bottom"));
		distributedCell = row.createCell(7);
		distributedCell.setCellValue(new XSSFRichTextString("Bottom"));
		errorCell = row.createCell(9);
		errorCell.setCellValue(new XSSFRichTextString("Fehler"));
		
		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.RANGE.key(), "B2:B4");
		ObjectNode alignNode = requestNode.objectNode();
		alignNode.put(Key.VERTICAL.key(), VerticalAlignment.TOP.name().toLowerCase());
		requestNode.set(Key.ALIGNMENT.key(), alignNode);
		
		String response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.RANGE.key(), "D2:D4");
		alignNode = requestNode.objectNode();
		alignNode.put(Key.VERTICAL.key(), VerticalAlignment.CENTER.name().toLowerCase());
		requestNode.set(Key.ALIGNMENT.key(), alignNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.RANGE.key(), "F2:F4");
		alignNode = requestNode.objectNode();
		alignNode.put(Key.VERTICAL.key(), VerticalAlignment.BOTTOM.name().toLowerCase());
		requestNode.set(Key.ALIGNMENT.key(), alignNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.RANGE.key(), "H2:H4");
		alignNode = requestNode.objectNode();
		alignNode.put(Key.VERTICAL.key(), VerticalAlignment.DISTRIBUTED.name().toLowerCase());
		requestNode.set(Key.ALIGNMENT.key(), alignNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.RANGE.key(), "J2:J4");
		alignNode = requestNode.objectNode();
		alignNode.put(Key.VERTICAL.key(), "gigi");
		requestNode.set(Key.ALIGNMENT.key(), alignNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("invalid argument 'alignment.vertical'", responseNode.get(Executor.ERRORS).get(0).asText());

		requestNode = MAPPER.createObjectNode();
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}
	
	@Test
	public void testBorderStyles() throws EncryptedDocumentException, IOException
	{
		prepareWorkbookAndSheetIfMissing();
		
		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "B2");
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
		requestNode.put(Key.CELL.key(), "D4");
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
		requestNode.put(Key.CELL.key(), "F6");
		borderNode = requestNode.objectNode();
		styleNode = borderNode.objectNode();
		node = styleNode.textNode(BorderStyle.DASH_DOT.name());
		styleNode.set(Key.RIGHT.key(), node);
		borderNode.set(Key.STYLE.key(), styleNode);
		requestNode.set(Key.BORDER.key(), borderNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "H8");
		borderNode = requestNode.objectNode();
		styleNode = borderNode.objectNode();
		node = styleNode.textNode(BorderStyle.DASH_DOT.name());
		styleNode.set(Key.RIGHT.key(), node);
		borderNode.set(Key.STYLE.key(), styleNode);
		requestNode.set(Key.BORDER.key(), borderNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "J10");
		borderNode = requestNode.objectNode();
		styleNode = borderNode.objectNode();
		node = styleNode.textNode("gigi");
		styleNode.set(Key.TOP.key(), node);
		borderNode.set(Key.STYLE.key(), styleNode);
		requestNode.set(Key.BORDER.key(), borderNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("invalid argument 'border.style.top'", responseNode.get(Executor.ERRORS).get(0).asText());

		requestNode = MAPPER.createObjectNode();
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}
	
	@Test
	public void testBorderStylesRange() throws EncryptedDocumentException, IOException
	{
		prepareWorkbookAndSheetIfMissing();
		
		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.RANGE.key(), "B2:C3");
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
		requestNode.put(Key.RANGE.key(), "E5:F6");
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
		requestNode.put(Key.RANGE.key(), "H8:I9");
		borderNode = requestNode.objectNode();
		styleNode = borderNode.objectNode();
		node = styleNode.textNode(BorderStyle.DASH_DOT.name());
		styleNode.set(Key.RIGHT.key(), node);
		borderNode.set(Key.STYLE.key(), styleNode);
		requestNode.set(Key.BORDER.key(), borderNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.RANGE.key(), "K11:L12");
		borderNode = requestNode.objectNode();
		styleNode = borderNode.objectNode();
		node = styleNode.textNode(BorderStyle.DASH_DOT.name());
		styleNode.set(Key.RIGHT.key(), node);
		borderNode.set(Key.STYLE.key(), styleNode);
		requestNode.set(Key.BORDER.key(), borderNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.RANGE.key(), "N14:O15");
		borderNode = requestNode.objectNode();
		styleNode = borderNode.objectNode();
		node = styleNode.textNode("gigi");
		styleNode.set(Key.TOP.key(), node);
		borderNode.set(Key.STYLE.key(), styleNode);
		requestNode.set(Key.BORDER.key(), borderNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("invalid argument 'border.style.top'", responseNode.get(Executor.ERRORS).get(0).asText());

		requestNode = MAPPER.createObjectNode();
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testDataFormatNumber() throws JsonMappingException, JsonProcessingException
	{
		String workbook = "./targets/dataFormatNumber.xlsx";
		prepareWorkbookAndSheetIfMissing(workbook, SHEET0);
		Sheet sheet = getActiveSheet();
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue(12.666);
		cell = row.createCell(1);
		cell.setCellValue(13.6);
		cell = row.createCell(2);
		cell.setCellValue(1000.3);
		row = sheet.createRow(1);
		cell = row.createCell(0);
		cell.setCellValue(12.666);
		cell = row.createCell(1);
		cell.setCellValue(13.6);
		cell = row.createCell(2);
		cell.setCellValue(1000.3);
		row = sheet.createRow(2);
		cell = row.createCell(0);
		cell.setCellValue(12.666);
		cell = row.createCell(1);
		cell.setCellValue(13.6);
		cell = row.createCell(2);
		cell.setCellValue(1000.3);

		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "A1");
		requestNode.put(Key.DATA_FORMAT.key(), "0.00");
		
		String response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());
		
		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.RANGE.key(), "B1:C1");
		requestNode.put(Key.DATA_FORMAT.key(), "0.00");
		
		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());
		
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "A2");
		requestNode.put(Key.DATA_FORMAT.key(), "#,##0.00");
		
		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());
		
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.RANGE.key(), "B2:C2");
		requestNode.put(Key.DATA_FORMAT.key(), "#,##0.00");
		
		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());
		
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "A3");
		requestNode.put(Key.DATA_FORMAT.key(), "#,##0");
		
		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());
		
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.RANGE.key(), "B3:C3");
		requestNode.put(Key.DATA_FORMAT.key(), "#,##0");
		
		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());
		
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.WORKBOOK.key(), workbook);
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
	}
	
	@Test
	public void testDataFormatTime() throws JsonMappingException, JsonProcessingException
	{
		String workbook = "./targets/dataFormatTime.xlsx";
		prepareWorkbookAndSheetIfMissing(workbook, SHEET0);
		Sheet sheet = getActiveSheet();
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue("1:25");
		cell = row.createCell(1);
		cell.setCellValue("1:25:00");
		cell = row.createCell(2);
		cell.setCellValue("21.10.1954");
		cell = row.createCell(3);
		cell.setCellValue("21.10.1954 1:25");

		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "A1");
		requestNode.put(Key.DATA_FORMAT.key(), "h:mm");
		
		String response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());
		
		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "B1");
		requestNode.put(Key.DATA_FORMAT.key(), "h:mm");
		
		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());
		
		responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.WORKBOOK.key(), workbook);
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
	}
	
	@Test
	public void testBackground() throws JsonMappingException, JsonProcessingException
	{
		prepareWorkbookAndSheetIfMissing("./targets/background.xlsx", SHEET0);
		Sheet sheet = getActiveSheet();
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue(new XSSFRichTextString("Das ist eine Testzelle"));
		cell = row.createCell(1);
		cell.setCellValue(new XSSFRichTextString("Das ist eine zweite Testzelle"));

		ObjectNode requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.CELL.key(), "A1");
		ObjectNode backgroundNode = requestNode.objectNode();
		backgroundNode.put(Key.COLOR.key(), IndexedColors.RED.name());
		requestNode.set(Key.BACKGROUND.key(), backgroundNode);
		String response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());
		
		JsonNode responseNode = MAPPER.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = MAPPER.createObjectNode();
		requestNode.put(Key.WORKBOOK.key(), "./targets/background.xlsx");
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
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
	
}
