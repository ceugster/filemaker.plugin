package ch.eugster.filemaker.fsl.plugin.test;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.eugster.filemaker.fsl.plugin.ExecutorSelector;
import ch.eugster.filemaker.fsl.plugin.Fsl;
import ch.eugster.filemaker.fsl.plugin.swissqrbill.SwissQRBillGenerator.Parameter;
import net.codecrete.qrbill.generator.GraphicsFormat;
import net.codecrete.qrbill.generator.Language;
import net.codecrete.qrbill.generator.OutputSize;

public class QRBillTest
{
	private ObjectMapper mapper;

	@BeforeAll
	public static void beforeAll() throws IOException, URISyntaxException
	{
		if (Desktop.isDesktopSupported())
		{
			Desktop desktop = Desktop.getDesktop();
			if (desktop.isSupported(Action.OPEN))
			{
				URL url = QRBillTest.class.getResource("Test.fmp12");
				desktop.open(new File(url.toURI()));
			}
		}
	}

	@BeforeEach
	public void beforeEach() throws URISyntaxException, IOException
	{
		this.mapper = new ObjectMapper();

	}

	@AfterEach
	public void afterEach()
	{
	}

	@Test
	public void testNullParameter() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode source = this.mapper.createObjectNode();
		ObjectNode test = source.putObject("test");
		test.put("properties", "");
		Fsl fsl = new Fsl();
		String result = fsl.execute(ExecutorSelector.CREATE_QRBILL.command(), null);
		JsonNode node = this.mapper.readTree(result);
		assertEquals("Fehler", node.get("result").asText());
		JsonNode errors = node.get("errors");
		assertEquals(ArrayNode.class, errors.getClass());
		assertEquals(1, errors.size());
		for (int i = 0; i < errors.size(); i++)
		{
			assertEquals("Der Übergabeparameter 'json' muss vorhanden sein.", errors.get(i).asText());
		}
	}

	@Test
	public void testInvalidCommand() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode source = this.mapper.createObjectNode();
		ObjectNode test = source.putObject("test");
		test.put("properties", "");
		Fsl fsl = new Fsl();
		String result = fsl.execute("InvalidCommand", source.toString());
		JsonNode target = mapper.readTree(result);
		assertEquals("Fehler", target.get("result").asText());
		JsonNode node = target.get("errors");
		assertEquals(ArrayNode.class, node.getClass());
		ArrayNode errors = ArrayNode.class.cast(node);
		assertEquals(1, errors.size());
		for (int i = 0; i < errors.size(); i++)
		{
			assertEquals(
					"Der Befehl 'InvalidCommand' wird nicht unterstützt. Bitte überprüfen Sie den Befehlsparameter.",
					errors.get(i).asText());
		}
	}

	@Test
	public void testAllParametersProvidedOK() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode source = mapper.createObjectNode();
		ObjectNode test = source.putObject("test");
		test.put("properties", "");
		source.put(Parameter.AMOUNT.key(), new BigDecimal(350));
		source.put(Parameter.CURRENCY.key(), "CHF");
		source.put(Parameter.IBAN.key(), "CH4431999123000889012");
		source.put(Parameter.REFERENCE.key(), "00000000000000000000000000");
		source.put(Parameter.MESSAGE.key(), "Abonnement für 2020");
		ObjectNode db = source.putObject(Parameter.DATABASE.key());
		db.put(Parameter.URL.key(), "jdbc:filemaker://localhost/Test");
		db.put(Parameter.USERNAME.key(), "christian");
		db.put(Parameter.PASSWORD.key(), "ce_eu97");
		ObjectNode qrBill = source.putObject(Parameter.QRBILL.key());
		qrBill.put(Parameter.TABLE.key(), "QRBill");
		qrBill.put(Parameter.NAME_COL.key(), "name");
		qrBill.put(Parameter.CONTAINER_COL.key(), "qrbill");
		qrBill.put(Parameter.NAME_COL.key(), "name");
		qrBill.put(Parameter.WHERE_COL.key(), "id_text");
		qrBill.put(Parameter.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode invoice = source.putObject(Parameter.INVOICE.key());
		invoice.put(Parameter.TABLE.key(), "QRBill");
		invoice.put(Parameter.CONTAINER_COL.key(), "invoice");
		invoice.put(Parameter.WHERE_COL.key(), "id_text");
		invoice.put(Parameter.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode creditor = source.putObject(Parameter.CREDITOR.key());
		creditor.put(Parameter.NAME.key(), "Christian Eugster");
		creditor.put(Parameter.ADDRESS.key(), "Axensteinstrasse 27");
		creditor.put(Parameter.CITY.key(), "9000 St. Gallen");
		creditor.put(Parameter.COUNTRY.key(), "CH");
		ObjectNode debtor = source.putObject(Parameter.DEBTOR.key());
		debtor.put(Parameter.NAME.key(), "Christian Eugster");
		debtor.put(Parameter.ADDRESS.key(), "Axensteinstrasse 27");
		debtor.put(Parameter.CITY.key(), "9000 St. Gallen");
		debtor.put(Parameter.COUNTRY.key(), "CH");
		ObjectNode form = source.putObject(Parameter.FORM.key());
		form.put(Parameter.GRAPHICS_FORMAT.key(), GraphicsFormat.PDF.name());
		form.put(Parameter.OUTPUT_SIZE.key(), OutputSize.A4_PORTRAIT_SHEET.name());
		form.put(Parameter.LANGUAGE.key(), Language.DE.name());

		String result = new Fsl().execute(ExecutorSelector.CREATE_QRBILL.command(), source.toString());
		JsonNode target = mapper.readTree(result);
		assertEquals("OK", target.get("result").asText());
		assertNull(target.get("errors"));
	}

	@Test
	public void testWithDefaultValuesOK() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode source = this.mapper.createObjectNode();
		ObjectNode test = source.putObject("test");
		test.put("properties", "");
		source.put(Parameter.IBAN.key(), "CH4431999123000889012");
		source.put(Parameter.REFERENCE.key(), "00000000000000000000000000");
		source.put(Parameter.MESSAGE.key(), "Abonnement für 2020");
		ObjectNode db = source.putObject(Parameter.DATABASE.key());
		db.put(Parameter.URL.key(), "jdbc:filemaker://localhost/Test");
		db.put(Parameter.USERNAME.key(), "christian");
		db.put(Parameter.PASSWORD.key(), "ce_eu97");
		ObjectNode qrBill = source.putObject(Parameter.QRBILL.key());
		qrBill.put(Parameter.TABLE.key(), "QRBill");
		qrBill.put(Parameter.NAME_COL.key(), "name");
		qrBill.put(Parameter.CONTAINER_COL.key(), "qrbill");
		qrBill.put(Parameter.NAME_COL.key(), "name");
		qrBill.put(Parameter.WHERE_COL.key(), "id_text");
		qrBill.put(Parameter.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode invoice = source.putObject(Parameter.INVOICE.key());
		invoice.put(Parameter.TABLE.key(), "QRBill");
		invoice.put(Parameter.CONTAINER_COL.key(), "invoice");
		invoice.put(Parameter.WHERE_COL.key(), "id_text");
		invoice.put(Parameter.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode creditor = source.putObject(Parameter.CREDITOR.key());
		creditor.put(Parameter.NAME.key(), "Christian Eugster");
		creditor.put(Parameter.ADDRESS.key(), "Axensteinstrasse 27");
		creditor.put(Parameter.CITY.key(), "9000 St. Gallen");
		creditor.put(Parameter.COUNTRY.key(), "CH");
		ObjectNode debtor = source.putObject(Parameter.DEBTOR.key());
		debtor.put(Parameter.NAME.key(), "Christian Eugster");
		debtor.put(Parameter.ADDRESS.key(), "Axensteinstrasse 27");
		debtor.put(Parameter.CITY.key(), "9000 St. Gallen");
		debtor.put(Parameter.COUNTRY.key(), "CH");

		String result = new Fsl().execute(ExecutorSelector.CREATE_QRBILL.command(), source.toString());
		JsonNode target = mapper.readTree(result);
		assertEquals("OK", target.get("result").asText());
		assertNull(target.get("errors"));
	}

	@Test
	public void testMissingOptionalValuesOK() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode source = this.mapper.createObjectNode();
		ObjectNode test = source.putObject("test");
		test.put("properties", "");
		source.put(Parameter.IBAN.key(), "CH4431999123000889012");
		source.put(Parameter.REFERENCE.key(), "00000000000000000000000000");
		ObjectNode db = source.putObject(Parameter.DATABASE.key());
		db.put(Parameter.URL.key(), "jdbc:filemaker://localhost/Test");
		db.put(Parameter.USERNAME.key(), "christian");
		db.put(Parameter.PASSWORD.key(), "ce_eu97");
		ObjectNode qrBill = source.putObject(Parameter.QRBILL.key());
		qrBill.put(Parameter.TABLE.key(), "QRBill");
		qrBill.put(Parameter.NAME_COL.key(), "name");
		qrBill.put(Parameter.CONTAINER_COL.key(), "qrbill");
		qrBill.put(Parameter.NAME_COL.key(), "name");
		qrBill.put(Parameter.WHERE_COL.key(), "id_text");
		qrBill.put(Parameter.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode invoice = source.putObject(Parameter.INVOICE.key());
		invoice.put(Parameter.TABLE.key(), "QRBill");
		invoice.put(Parameter.CONTAINER_COL.key(), "invoice");
		invoice.put(Parameter.WHERE_COL.key(), "id_text");
		invoice.put(Parameter.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode creditor = source.putObject(Parameter.CREDITOR.key());
		creditor.put(Parameter.NAME.key(), "Christian Eugster");
		creditor.put(Parameter.ADDRESS.key(), "Axensteinstrasse 27");
		creditor.put(Parameter.CITY.key(), "9000 St. Gallen");
		creditor.put(Parameter.COUNTRY.key(), "CH");

		String result = new Fsl().execute(ExecutorSelector.CREATE_QRBILL.command(), source.toString());
		JsonNode target = mapper.readTree(result);
		assertEquals("OK", target.get("result").asText());
		assertNull(target.get("errors"));
	}

	@Test
	public void testMissingConnection() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode source = this.mapper.createObjectNode();
		ObjectNode test = source.putObject("test");
		test.put("properties", "");
		source.put(Parameter.IBAN.key(), "CH4431999123000889012");
		source.put(Parameter.REFERENCE.key(), "00000000000000000000000000");
		ObjectNode qrBill = source.putObject(Parameter.QRBILL.key());
		qrBill.put(Parameter.TABLE.key(), "QRBill");
		qrBill.put(Parameter.NAME_COL.key(), "name");
		qrBill.put(Parameter.CONTAINER_COL.key(), "qrbill");
		qrBill.put(Parameter.NAME_COL.key(), "name");
		qrBill.put(Parameter.WHERE_COL.key(), "id_text");
		qrBill.put(Parameter.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode invoice = source.putObject(Parameter.INVOICE.key());
		invoice.put(Parameter.TABLE.key(), "QRBill");
		invoice.put(Parameter.CONTAINER_COL.key(), "invoice");
		invoice.put(Parameter.WHERE_COL.key(), "id_text");
		invoice.put(Parameter.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode creditor = source.putObject(Parameter.CREDITOR.key());
		creditor.put(Parameter.NAME.key(), "Christian Eugster");
		creditor.put(Parameter.ADDRESS.key(), "Axensteinstrasse 27");
		creditor.put(Parameter.CITY.key(), "9000 St. Gallen");
		creditor.put(Parameter.COUNTRY.key(), "CH");

		String result = new Fsl().execute(ExecutorSelector.CREATE_QRBILL.command(), source.toString());
		JsonNode target = mapper.readTree(result);
		assertEquals("Fehler", target.get("result").asText());
		assertEquals(1, target.get("errors").size());
		ArrayNode errors = ArrayNode.class.cast(target.get("errors"));
		assertEquals("Keine Verbindungsdaten zur Datenbank gefunden.", errors.iterator().next().asText());
	}

	@Test
	public void testMissingSubNodes() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode source = this.mapper.createObjectNode();
		ObjectNode test = source.putObject("test");
		test.put("properties", "");
		source.put(Parameter.IBAN.key(), "CH4431999123000889012");
		source.put(Parameter.REFERENCE.key(), "00000000000000000000000000");
		source.putObject(Parameter.DATABASE.key());
		source.putObject(Parameter.QRBILL.key());
		source.putObject(Parameter.INVOICE.key());
		source.putObject(Parameter.CREDITOR.key());
		ObjectNode debtor = source.putObject(Parameter.DEBTOR.key());
		debtor.put(Parameter.NAME.key(), "Christian Eugster");

		String result = new Fsl().execute(ExecutorSelector.CREATE_QRBILL.command(), source.toString());
		JsonNode target = mapper.readTree(result);
		assertEquals("Fehler", target.get("result").asText());
		assertEquals(9, target.get("errors").size());
		ArrayNode errors = ArrayNode.class.cast(target.get("errors"));
		Iterator<JsonNode> iterator = errors.iterator();
		while (iterator.hasNext())
		{
			String message = iterator.next().asText();
			if (message.equals("field_is_mandatory: 'creditor.name'"))
			{
				assertEquals("field_is_mandatory: 'creditor.name'", message);
			}
			else if (message.equals("field_is_mandatory: 'creditor.postalCode'"))
			{
				assertEquals("field_is_mandatory: 'creditor.postalCode'", message);
			}
			else if (message.equals("field_is_mandatory: 'creditor.addressLine2'"))
			{
				assertEquals("field_is_mandatory: 'creditor.addressLine2'", message);
			}
			else if (message.equals("field_is_mandatory: 'creditor.town'"))
			{
				assertEquals("field_is_mandatory: 'creditor.town'", message);
			}
			else if (message.equals("field_is_mandatory: 'creditor.countryCode'"))
			{
				assertEquals("field_is_mandatory: 'creditor.countryCode'", message);
			}
			else if (message.equals("field_is_mandatory: 'debtor.postalCode'"))
			{
				assertEquals("field_is_mandatory: 'debtor.postalCode'", message);
			}
			else if (message.equals("field_is_mandatory: 'debtor.addressLine2'"))
			{
				assertEquals("field_is_mandatory: 'debtor.addressLine2'", message);
			}
			else if (message.equals("field_is_mandatory: 'debtor.town'"))
			{
				assertEquals("field_is_mandatory: 'debtor.town'", message);
			}
			else if (message.equals("field_is_mandatory: 'debtor.countryCode'"))
			{
				assertEquals("field_is_mandatory: 'debtor.countryCode'", message);
			}
			else
			{
				fail();
			}
		}
	}

	@Test
	public void testMissingConnectionDetails() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode source = mapper.createObjectNode();
		ObjectNode test = source.putObject("test");
		test.put("properties", "");
		source.put(Parameter.AMOUNT.key(), new BigDecimal(350));
		source.put(Parameter.CURRENCY.key(), "CHF");
		source.put(Parameter.IBAN.key(), "CH4431999123000889012");
		source.put(Parameter.REFERENCE.key(), "00000000000000000000000000");
		source.put(Parameter.MESSAGE.key(), "Abonnement für 2020");
		source.putObject(Parameter.DATABASE.key());
		ObjectNode qrBill = source.putObject(Parameter.QRBILL.key());
		qrBill.put(Parameter.TABLE.key(), "QRBill");
		qrBill.put(Parameter.NAME_COL.key(), "name");
		qrBill.put(Parameter.CONTAINER_COL.key(), "qrbill");
		qrBill.put(Parameter.NAME_COL.key(), "name");
		qrBill.put(Parameter.WHERE_COL.key(), "id_text");
		qrBill.put(Parameter.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode invoice = source.putObject(Parameter.INVOICE.key());
		invoice.put(Parameter.TABLE.key(), "QRBill");
		invoice.put(Parameter.CONTAINER_COL.key(), "invoice");
		invoice.put(Parameter.WHERE_COL.key(), "id_text");
		invoice.put(Parameter.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode creditor = source.putObject(Parameter.CREDITOR.key());
		creditor.put(Parameter.NAME.key(), "Christian Eugster");
		creditor.put(Parameter.ADDRESS.key(), "Axensteinstrasse 27");
		creditor.put(Parameter.CITY.key(), "9000 St. Gallen");
		creditor.put(Parameter.COUNTRY.key(), "CH");
		ObjectNode debtor = source.putObject(Parameter.DEBTOR.key());
		debtor.put(Parameter.NAME.key(), "Christian Eugster");
		debtor.put(Parameter.ADDRESS.key(), "Axensteinstrasse 27");
		debtor.put(Parameter.CITY.key(), "9000 St. Gallen");
		debtor.put(Parameter.COUNTRY.key(), "CH");
		ObjectNode form = source.putObject(Parameter.FORM.key());
		form.put(Parameter.GRAPHICS_FORMAT.key(), GraphicsFormat.PDF.name());
		form.put(Parameter.OUTPUT_SIZE.key(), OutputSize.A4_PORTRAIT_SHEET.name());
		form.put(Parameter.LANGUAGE.key(), Language.DE.name());

		String result = new Fsl().execute(ExecutorSelector.CREATE_QRBILL.command(), source.toString());
		JsonNode target = mapper.readTree(result);
		assertEquals("Fehler", target.get("result").asText());
		assertEquals(1, target.get("errors").size());
		ArrayNode errors = ArrayNode.class.cast(target.get("errors"));
		assertEquals("The url cannot be null", errors.iterator().next().asText());
	}

	@Test
	public void testMissingTableInfos() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode source = mapper.createObjectNode();
		ObjectNode test = source.putObject("test");
		test.put("properties", "");
		source.put(Parameter.AMOUNT.key(), new BigDecimal(350));
		source.put(Parameter.CURRENCY.key(), "CHF");
		source.put(Parameter.IBAN.key(), "CH4431999123000889012");
		source.put(Parameter.REFERENCE.key(), "00000000000000000000000000");
		source.put(Parameter.MESSAGE.key(), "Abonnement für 2020");
		ObjectNode db = source.putObject(Parameter.DATABASE.key());
		db.put(Parameter.URL.key(), "jdbc:filemaker://localhost/Test");
		db.put(Parameter.USERNAME.key(), "christian");
		db.put(Parameter.PASSWORD.key(), "ce_eu97");
		source.putObject(Parameter.QRBILL.key());
		source.putObject(Parameter.INVOICE.key());
		ObjectNode creditor = source.putObject(Parameter.CREDITOR.key());
		creditor.put(Parameter.NAME.key(), "Christian Eugster");
		creditor.put(Parameter.ADDRESS.key(), "Axensteinstrasse 27");
		creditor.put(Parameter.CITY.key(), "9000 St. Gallen");
		creditor.put(Parameter.COUNTRY.key(), "CH");

		String result = new Fsl().execute(ExecutorSelector.CREATE_QRBILL.command(), source.toString());
		JsonNode target = mapper.readTree(result);
		assertEquals("Fehler", target.get("result").asText());
		assertEquals(1, target.get("errors").size());
		ArrayNode errors = ArrayNode.class.cast(target.get("errors"));
		assertEquals(
				"Fehler in der Datenbankabfrage: SELECT CAST(null AS VARCHAR) , GetAs(null, DEFAULT) FROM null WHERE null = ?",
				errors.iterator().next().asText());
	}

	@Test
	public void testMerge() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode source = this.mapper.createObjectNode();
		source.put(Parameter.IBAN.key(), "CH4431999123000889012");
		source.put(Parameter.REFERENCE.key(), "00000000000000000000000000");
		ObjectNode creditor = source.putObject(Parameter.CREDITOR.key());
		creditor.put(Parameter.NAME.key(), "Christian Eugster");
		creditor.put(Parameter.ADDRESS.key(), "Axensteinstrasse 27");
		creditor.put(Parameter.CITY.key(), "9000 St. Gallen");
		creditor.put(Parameter.COUNTRY.key(), "CH");

		String result = new Fsl().execute(ExecutorSelector.CREATE_QRBILL.command(), source.toString());
		JsonNode target = this.mapper.readTree(result);
		assertEquals("OK", target.get("result").asText());
		assertNull(target.get("errors"));
	}
}
