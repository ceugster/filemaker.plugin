package ch.eugster.filemaker.fsl.plugin.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
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

public final class XlsCellStyleTest extends AbstractXlsTest
{
	@Test
	public void testHorizontalAlignment() throws EncryptedDocumentException, IOException
	{
		Sheet sheet = prepareWorkbookAndSheetIfMissing();
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
		
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("cell", "B2");
		ObjectNode alignNode = requestNode.objectNode();
		alignNode.put("horizontal", HorizontalAlignment.LEFT.name().toLowerCase());
		requestNode.set("alignment", alignNode);
		
		String response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put("cell", "D2");
		alignNode = requestNode.objectNode();
		alignNode.put("horizontal", HorizontalAlignment.CENTER.name().toLowerCase());
		requestNode.set("alignment", alignNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put("cell", "F2");
		alignNode = requestNode.objectNode();
		alignNode.put("horizontal", HorizontalAlignment.RIGHT.name().toLowerCase());
		requestNode.set("alignment", alignNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put("cell", "H2");
		alignNode = requestNode.objectNode();
		alignNode.put("horizontal", HorizontalAlignment.DISTRIBUTED.name().toLowerCase());
		requestNode.set("alignment", alignNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put("cell", "J2");
		alignNode = requestNode.objectNode();
		alignNode.put("horizontal", "gigi");
		requestNode.set("alignment", alignNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("invalid argument 'alignment.horizontal'", responseNode.get(Executor.ERRORS).get(0).asText());

		requestNode = mapper.createObjectNode();
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}
	
	@Test
	public void testHorizontalAlignmentRange() throws EncryptedDocumentException, IOException
	{
		Sheet sheet = prepareWorkbookAndSheetIfMissing();
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
		
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("range", "B2:B4");
		ObjectNode alignNode = requestNode.objectNode();
		alignNode.put("horizontal", HorizontalAlignment.LEFT.name().toLowerCase());
		requestNode.set("alignment", alignNode);
		
		String response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put("range", "D2:D4");
		alignNode = requestNode.objectNode();
		alignNode.put("horizontal", HorizontalAlignment.CENTER.name().toLowerCase());
		requestNode.set("alignment", alignNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put("range", "F2:F4");
		alignNode = requestNode.objectNode();
		alignNode.put("horizontal", HorizontalAlignment.RIGHT.name().toLowerCase());
		requestNode.set("alignment", alignNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put("range", "H2:H4");
		alignNode = requestNode.objectNode();
		alignNode.put("horizontal", HorizontalAlignment.DISTRIBUTED.name().toLowerCase());
		requestNode.set("alignment", alignNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put("range", "J2:J4");
		alignNode = requestNode.objectNode();
		alignNode.put("horizontal", "gigi");
		requestNode.set("alignment", alignNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("invalid argument 'alignment.horizontal'", responseNode.get(Executor.ERRORS).get(0).asText());

		requestNode = mapper.createObjectNode();
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}
	
	@Test
	public void testVerticalAlignment() throws EncryptedDocumentException, IOException
	{
		Sheet sheet = prepareWorkbookAndSheetIfMissing();
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
		
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("cell", "B2");
		ObjectNode alignNode = requestNode.objectNode();
		alignNode.put("vertical", VerticalAlignment.TOP.name().toLowerCase());
		requestNode.set("alignment", alignNode);
		
		String response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put("cell", "D2");
		alignNode = requestNode.objectNode();
		alignNode.put("vertical", VerticalAlignment.CENTER.name().toLowerCase());
		requestNode.set("alignment", alignNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put("cell", "F2");
		alignNode = requestNode.objectNode();
		alignNode.put("vertical", VerticalAlignment.BOTTOM.name().toLowerCase());
		requestNode.set("alignment", alignNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put("cell", "H2");
		alignNode = requestNode.objectNode();
		alignNode.put("vertical", VerticalAlignment.DISTRIBUTED.name().toLowerCase());
		requestNode.set("alignment", alignNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put("cell", "J2");
		alignNode = requestNode.objectNode();
		alignNode.put("vertical", "gigi");
		requestNode.set("alignment", alignNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("invalid argument 'alignment.vertical'", responseNode.get(Executor.ERRORS).get(0).asText());

		requestNode = mapper.createObjectNode();
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}
	
	@Test
	public void testVerticalAlignmentRange() throws EncryptedDocumentException, IOException
	{
		Sheet sheet = prepareWorkbookAndSheetIfMissing();
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
		
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("range", "B2:B4");
		ObjectNode alignNode = requestNode.objectNode();
		alignNode.put("vertical", VerticalAlignment.TOP.name().toLowerCase());
		requestNode.set("alignment", alignNode);
		
		String response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put("range", "D2:D4");
		alignNode = requestNode.objectNode();
		alignNode.put("vertical", VerticalAlignment.CENTER.name().toLowerCase());
		requestNode.set("alignment", alignNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put("range", "F2:F4");
		alignNode = requestNode.objectNode();
		alignNode.put("vertical", VerticalAlignment.BOTTOM.name().toLowerCase());
		requestNode.set("alignment", alignNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put("range", "H2:H4");
		alignNode = requestNode.objectNode();
		alignNode.put("vertical", VerticalAlignment.DISTRIBUTED.name().toLowerCase());
		requestNode.set("alignment", alignNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put("range", "J2:J4");
		alignNode = requestNode.objectNode();
		alignNode.put("vertical", "gigi");
		requestNode.set("alignment", alignNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("invalid argument 'alignment.vertical'", responseNode.get(Executor.ERRORS).get(0).asText());

		requestNode = mapper.createObjectNode();
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}
	
	@Test
	public void testBorderStyles() throws EncryptedDocumentException, IOException
	{
		prepareWorkbookAndSheetIfMissing();
		
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("cell", "B2");
		ObjectNode borderNode = requestNode.objectNode();
		ObjectNode styleNode = borderNode.objectNode();
		TextNode node = styleNode.textNode(BorderStyle.DOTTED.name());
		styleNode.set("bottom", node);
		borderNode.set("style", styleNode);
		requestNode.set("border", borderNode);

		String response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put("cell", "D4");
		borderNode = requestNode.objectNode();
		styleNode = borderNode.objectNode();
		node = styleNode.textNode(BorderStyle.THICK.name());
		styleNode.set("left", node);
		borderNode.set("style", styleNode);
		requestNode.set("border", borderNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put("cell", "F6");
		borderNode = requestNode.objectNode();
		styleNode = borderNode.objectNode();
		node = styleNode.textNode(BorderStyle.DASH_DOT.name());
		styleNode.set("right", node);
		borderNode.set("style", styleNode);
		requestNode.set("border", borderNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put("cell", "H8");
		borderNode = requestNode.objectNode();
		styleNode = borderNode.objectNode();
		node = styleNode.textNode(BorderStyle.DASH_DOT.name());
		styleNode.set("right", node);
		borderNode.set("style", styleNode);
		requestNode.set("border", borderNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put("cell", "J10");
		borderNode = requestNode.objectNode();
		styleNode = borderNode.objectNode();
		node = styleNode.textNode("gigi");
		styleNode.set("top", node);
		borderNode.set("style", styleNode);
		requestNode.set("border", borderNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("invalid argument 'border.style.top'", responseNode.get(Executor.ERRORS).get(0).asText());

		requestNode = mapper.createObjectNode();
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}
	
	@Test
	public void testBorderStylesRange() throws EncryptedDocumentException, IOException
	{
		prepareWorkbookAndSheetIfMissing();
		
		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("range", "B2:C3");
		ObjectNode borderNode = requestNode.objectNode();
		ObjectNode styleNode = borderNode.objectNode();
		TextNode node = styleNode.textNode(BorderStyle.DOTTED.name());
		styleNode.set("bottom", node);
		borderNode.set("style", styleNode);
		requestNode.set("border", borderNode);

		String response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put("range", "E5:F6");
		borderNode = requestNode.objectNode();
		styleNode = borderNode.objectNode();
		node = styleNode.textNode(BorderStyle.THICK.name());
		styleNode.set("left", node);
		borderNode.set("style", styleNode);
		requestNode.set("border", borderNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put("range", "H8:I9");
		borderNode = requestNode.objectNode();
		styleNode = borderNode.objectNode();
		node = styleNode.textNode(BorderStyle.DASH_DOT.name());
		styleNode.set("right", node);
		borderNode.set("style", styleNode);
		requestNode.set("border", borderNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put("range", "K11:L12");
		borderNode = requestNode.objectNode();
		styleNode = borderNode.objectNode();
		node = styleNode.textNode(BorderStyle.DASH_DOT.name());
		styleNode.set("right", node);
		borderNode.set("style", styleNode);
		requestNode.set("border", borderNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put("range", "N14:O15");
		borderNode = requestNode.objectNode();
		styleNode = borderNode.objectNode();
		node = styleNode.textNode("gigi");
		styleNode.set("top", node);
		borderNode.set("style", styleNode);
		requestNode.set("border", borderNode);

		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());

		responseNode = mapper.readTree(response);
		assertEquals(Executor.ERROR, responseNode.get(Executor.STATUS).asText());
		assertEquals(1, responseNode.get(Executor.ERRORS).size());
		assertEquals("invalid argument 'border.style.top'", responseNode.get(Executor.ERRORS).get(0).asText());

		requestNode = mapper.createObjectNode();
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
	}

	@Test
	public void testDataFormatNumber() throws JsonMappingException, JsonProcessingException
	{
		String workbook = "./targets/dataFormatNumber.xlsx";
		Sheet sheet = prepareWorkbookAndSheetIfMissing(workbook, SHEET0);
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

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("cell", "A1");
		requestNode.put("data_format", "0.00");
		
		String response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = mapper.createObjectNode();
		requestNode.put("range", "B1:C1");
		requestNode.put("data_format", "0.00");
		
		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = mapper.createObjectNode();
		requestNode.put("cell", "A2");
		requestNode.put("data_format", "#,##0.00");
		
		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = mapper.createObjectNode();
		requestNode.put("range", "B2:C2");
		requestNode.put("data_format", "#,##0.00");
		
		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = mapper.createObjectNode();
		requestNode.put("cell", "A3");
		requestNode.put("data_format", "#,##0");
		
		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = mapper.createObjectNode();
		requestNode.put("range", "B3:C3");
		requestNode.put("data_format", "#,##0");
		
		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = mapper.createObjectNode();
		requestNode.put("workbook", workbook);
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
	}
	
	@Test
	public void testDataFormatTime() throws JsonMappingException, JsonProcessingException
	{
		String workbook = "./targets/dataFormatTime.xlsx";
		Sheet sheet = prepareWorkbookAndSheetIfMissing(workbook, SHEET0);
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue("1:25");
		cell = row.createCell(1);
		cell.setCellValue("1:25:00");
		cell = row.createCell(2);
		cell.setCellValue("21.10.1954");
		cell = row.createCell(3);
		cell.setCellValue("21.10.1954 1:25");

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("cell", "A1");
		requestNode.put("data_format", "h:mm");
		
		String response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = mapper.createObjectNode();
		requestNode.put("cell", "B1");
		requestNode.put("data_format", "h:mm");
		
		response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = mapper.createObjectNode();
		requestNode.put("workbook", workbook);
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
	}
	
	@Test
	public void testBackground() throws JsonMappingException, JsonProcessingException
	{
		Sheet sheet = prepareWorkbookAndSheetIfMissing("./targets/background.xlsx", SHEET0);
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue(new XSSFRichTextString("Das ist eine Testzelle"));
		cell = row.createCell(1);
		cell.setCellValue(new XSSFRichTextString("Das ist eine zweite Testzelle"));

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("cell", "A1");
		ObjectNode backgroundNode = requestNode.objectNode();
		backgroundNode.put("color", IndexedColors.RED.name());
		requestNode.set("background", backgroundNode);
		
		String response = Fsl.execute("Xls.applyCellStyles", requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));

		requestNode = mapper.createObjectNode();
		requestNode.put("workbook", "./targets/background.xlsx");
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
	}
	
	@Test
	public void testAutoSizeColumns() throws JsonMappingException, JsonProcessingException
	{
		Sheet sheet = prepareWorkbookAndSheetIfMissing("autoSizeColumn.xlsx", SHEET0);
		
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue(new XSSFRichTextString("Das ist eine Testzelle"));
		cell = row.createCell(1);
		cell.setCellValue(new XSSFRichTextString("Das ist eine zweite Testzelle"));

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("cell", "A1");
		
		String response = Fsl.execute("Xls.autoSizeColumns", requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = mapper.createObjectNode();
		requestNode.put("range", "A1:B1");
		
		response = Fsl.execute("Xls.autoSizeColumns", requestNode.toString());
		
		responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = mapper.createObjectNode();
		requestNode.put("workbook", "./targets/autoSizeColumn.xlsx");
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
	}

	@Test
	public void testRotation() throws JsonMappingException, JsonProcessingException
	{
		String workbook = "./targets/rotate.xlsx";
		Sheet sheet = prepareWorkbookAndSheetIfMissing(workbook, SHEET0);
		
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue(new XSSFRichTextString("Das ist eine Testzelle"));

		ObjectNode requestNode = mapper.createObjectNode();
		requestNode.put("rotation", 90);
		requestNode.put("cell", "A1");
		
		String response = Fsl.execute("Xls.rotateCells", requestNode.toString());
		
		JsonNode responseNode = mapper.readTree(response);
		assertEquals(Executor.OK, responseNode.get(Executor.STATUS).asText());
		assertNull(responseNode.get(Executor.ERRORS));
		
		requestNode = mapper.createObjectNode();
		requestNode.put("workbook", workbook);
		response = Fsl.execute("Xls.saveAndReleaseWorkbook", requestNode.toString());
	}
	
}
