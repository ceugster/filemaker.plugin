package ch.eugster.filemaker.fsl.plugin.test;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import ch.eugster.filemaker.fsl.plugin.swissqrbill.QRBillParameter;
import ch.eugster.filemaker.fsl.plugin.swissqrbill.QRBillParameter.QRBillCreditor;
import ch.eugster.filemaker.fsl.plugin.swissqrbill.QRBillParameter.QRBillDatabase;
import ch.eugster.filemaker.fsl.plugin.swissqrbill.QRBillParameter.QRBillDebtor;
import ch.eugster.filemaker.fsl.plugin.swissqrbill.QRBillParameter.QRBillForm;
import ch.eugster.filemaker.fsl.plugin.swissqrbill.QRBillParameter.QRBillMain;
import ch.eugster.filemaker.fsl.plugin.swissqrbill.QRBillParameter.QRBillReadInvoice;
import ch.eugster.filemaker.fsl.plugin.swissqrbill.QRBillParameter.QRBillWrite;
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
		Fsl fsl = new Fsl();
		String result = fsl.execute(ExecutorSelector.CREATE_QRBILL.command(), null);
		JsonNode target = mapper.readTree(result);
		assertEquals("Fehler", target.get("result").asText());
		JsonNode node = target.get("errors");
		assertEquals(ArrayNode.class, node.getClass());
		ArrayNode errors = ArrayNode.class.cast(node);
		assertEquals(1, errors.size());
		for (int i = 0; i < errors.size(); i++)
		{
			assertEquals("Der Übergabeparameter 'json' muss vorhanden sein.", errors.get(i).asText());
		}
	}

	@Test
	public void testInvalidCommand() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode root = mapper.createObjectNode();
		Fsl fsl = new Fsl();
		String result = fsl.execute("InvalidCommand", root.toPrettyString());
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
	public void testAllParametersOK()
	{
		ObjectNode source = mapper.createObjectNode();
		source.put(QRBillMain.AMOUNT.key(), new BigDecimal(350));
		source.put(QRBillMain.CURRENCY.key(), "CHF");
		source.put(QRBillMain.IBAN.key(), "CH4431999123000889012");
		source.put(QRBillMain.REFERENCE.key(), "00000000000000000000000000");
		source.put(QRBillMain.INVOICE.key(), "R123456");
		source.put(QRBillMain.MESSAGE.key(), "Abonnement für 2020");
		ObjectNode db = source.putObject(QRBillMain.DATABASE.key());
		db.put(QRBillDatabase.URL.key(), "jdbc:filemaker://localhost/Test");
		db.put(QRBillDatabase.USERNAME.key(), "christian");
		db.put(QRBillDatabase.PASSWORD.key(), "ce_eu97");
		ObjectNode writeQRBill = db.putObject(QRBillDatabase.WRITE_QRBILL.key());
		writeQRBill.put(QRBillWrite.TABLE.key(), "QRBill");
		writeQRBill.put(QRBillWrite.NAME_COL.key(), "name");
		writeQRBill.put(QRBillWrite.QRBILL_COL.key(), "qrbill");
		writeQRBill.put(QRBillWrite.WHERE_COL.key(), "id_text");
		writeQRBill.put(QRBillWrite.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode readInvoice = db.putObject(QRBillDatabase.READ_INVOICE.key());
		readInvoice.put(QRBillReadInvoice.TABLE.key(), "QRBill");
		readInvoice.put(QRBillReadInvoice.INVOICE_COL.key(), "invoice");
		readInvoice.put(QRBillReadInvoice.WHERE_COL.key(), "id_text");
		readInvoice.put(QRBillReadInvoice.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode creditor = source.putObject(QRBillMain.CREDITOR.key());
		creditor.put(QRBillCreditor.NAME.key(), "Christian Eugster");
		creditor.put(QRBillCreditor.ADDRESS.key(), "Axensteinstrasse 27");
		creditor.put(QRBillCreditor.CITY.key(), "9000 St. Gallen");
		creditor.put(QRBillCreditor.COUNTRY.key(), "CH");
		ObjectNode debtor = source.putObject(QRBillMain.DEBTOR.key());
		debtor.put(QRBillDebtor.NUMBER.key(), "K123456");
		debtor.put(QRBillDebtor.NAME.key(), "Christian Eugster");
		debtor.put(QRBillDebtor.ADDRESS.key(), "Axensteinstrasse 27");
		debtor.put(QRBillDebtor.CITY.key(), "9000 St. Gallen");
		debtor.put(QRBillDebtor.COUNTRY.key(), "CH");
		ObjectNode form = source.putObject(QRBillMain.FORM.key());
		form.put(QRBillForm.GRAPHICS_FORMAT.key(), GraphicsFormat.PDF.name());
		form.put(QRBillForm.OUTPUT_SIZE.key(), OutputSize.A4_PORTRAIT_SHEET.name());
		form.put(QRBillForm.LANGUAGE.key(), Language.DE.name());
		ObjectNode target = mapper.createObjectNode();
		QRBillParameter.checkAll(source, target);
		assertEquals(new BigDecimal(350).doubleValue(),
				target.get(QRBillMain.AMOUNT.key()).decimalValue().doubleValue());
		assertEquals("CHF", target.get(QRBillMain.CURRENCY.key()).asText());
		assertEquals("CH4431999123000889012", target.get(QRBillMain.IBAN.key()).asText());
		assertEquals("000000000000000000000000000", target.get(QRBillMain.REFERENCE.key()).asText());
		assertEquals("R123456", target.get(QRBillMain.INVOICE.key()).asText());
		assertEquals("Abonnement für 2020", target.get(QRBillMain.MESSAGE.key()).asText());
		assertEquals(ObjectNode.class, target.get(QRBillMain.DATABASE.key()).getClass());
		db = ObjectNode.class.cast(target.get(QRBillMain.DATABASE.key()));
		assertEquals("jdbc:filemaker://localhost/Test", db.get(QRBillDatabase.URL.key()).asText());
		assertEquals("christian", db.get(QRBillDatabase.USERNAME.key()).asText());
		assertEquals("ce_eu97", db.get(QRBillDatabase.PASSWORD.key()).asText());
		assertEquals(ObjectNode.class, db.get(QRBillDatabase.WRITE_QRBILL.key()).getClass());
		writeQRBill = ObjectNode.class.cast(db.get(QRBillDatabase.WRITE_QRBILL.key()));
		assertEquals("QRBill", writeQRBill.get(QRBillWrite.TABLE.key()).asText());
		assertEquals("name", writeQRBill.get(QRBillWrite.NAME_COL.key()).asText());
		assertEquals("qrbill", writeQRBill.get(QRBillWrite.QRBILL_COL.key()).asText());
		assertEquals("id_text", writeQRBill.get(QRBillWrite.WHERE_COL.key()).asText());
		assertEquals("7019891C-7AA9-4831-B0DC-EB69F5012BDC", writeQRBill.get(QRBillWrite.WHERE_VAL.key()).asText());
		assertEquals(ObjectNode.class, db.get(QRBillDatabase.READ_INVOICE.key()).getClass());
		readInvoice = ObjectNode.class.cast(db.get(QRBillDatabase.READ_INVOICE.key()));
		assertEquals("QRBill", readInvoice.get(QRBillReadInvoice.TABLE.key()).asText());
		assertEquals("invoice", readInvoice.get(QRBillReadInvoice.INVOICE_COL.key()).asText());
		assertEquals("id_text", readInvoice.get(QRBillReadInvoice.WHERE_COL.key()).asText());
		assertEquals("7019891C-7AA9-4831-B0DC-EB69F5012BDC",
				readInvoice.get(QRBillReadInvoice.WHERE_VAL.key()).asText());
		assertEquals(ObjectNode.class, target.get(QRBillMain.CREDITOR.key()).getClass());
		creditor = ObjectNode.class.cast(target.get(QRBillMain.CREDITOR.key()));
		assertEquals("Christian Eugster", creditor.get(QRBillCreditor.NAME.key()).asText());
		assertEquals("Axensteinstrasse 27", creditor.get(QRBillCreditor.ADDRESS.key()).asText());
		assertEquals("9000 St. Gallen", creditor.get(QRBillCreditor.CITY.key()).asText());
		assertEquals("CH", creditor.get(QRBillCreditor.COUNTRY.key()).asText());
		assertEquals(ObjectNode.class, target.get(QRBillMain.DEBTOR.key()).getClass());
		debtor = ObjectNode.class.cast(target.get(QRBillMain.DEBTOR.key()));
		assertEquals("K123456", debtor.get(QRBillDebtor.NUMBER.key()).asText());
		assertEquals("Christian Eugster", debtor.get(QRBillDebtor.NAME.key()).asText());
		assertEquals("Axensteinstrasse 27", debtor.get(QRBillDebtor.ADDRESS.key()).asText());
		assertEquals("9000 St. Gallen", debtor.get(QRBillDebtor.CITY.key()).asText());
		assertEquals("CH", debtor.get(QRBillDebtor.COUNTRY.key()).asText());
		assertEquals(ObjectNode.class, target.get(QRBillMain.FORM.key()).getClass());
		form = ObjectNode.class.cast(target.get(QRBillMain.FORM.key()));
		assertEquals(GraphicsFormat.PDF.name(), form.get(QRBillForm.GRAPHICS_FORMAT.key()).asText());
		assertEquals(OutputSize.A4_PORTRAIT_SHEET.name(), form.get(QRBillForm.OUTPUT_SIZE.key()).asText());
		assertEquals(Language.DE.name(), form.get(QRBillForm.LANGUAGE.key()).asText());
		assertEquals("OK", target.get("result").asText());
		assertNull(target.get("errors"));
	}

	@Test
	public void createSourceParameterOK()
	{
		ObjectNode source = mapper.createObjectNode();
		source.put(QRBillMain.AMOUNT.key(), new BigDecimal(350));
		source.put(QRBillMain.CURRENCY.key(), "CHF");
		source.put(QRBillMain.IBAN.key(), "CH4431999123000889012");
		source.put(QRBillMain.REFERENCE.key(), "00000000000000000000000000");
		source.put(QRBillMain.INVOICE.key(), "R123456");
		source.put(QRBillMain.MESSAGE.key(), "Abonnement für 2020");
		ObjectNode db = source.putObject(QRBillMain.DATABASE.key());
		db.put(QRBillDatabase.URL.key(), "jdbc:filemaker://localhost/Test");
		db.put(QRBillDatabase.USERNAME.key(), "christian");
		db.put(QRBillDatabase.PASSWORD.key(), "ce_eu97");
		ObjectNode writeQRBill = db.putObject(QRBillDatabase.WRITE_QRBILL.key());
		writeQRBill.put(QRBillWrite.TABLE.key(), "QRBill");
		writeQRBill.put(QRBillWrite.NAME_COL.key(), "name");
		writeQRBill.put(QRBillWrite.QRBILL_COL.key(), "qrbill");
		writeQRBill.put(QRBillWrite.WHERE_COL.key(), "id_text");
		writeQRBill.put(QRBillWrite.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode readInvoice = db.putObject(QRBillDatabase.READ_INVOICE.key());
		readInvoice.put(QRBillReadInvoice.TABLE.key(), "QRBill");
		readInvoice.put(QRBillReadInvoice.INVOICE_COL.key(), "invoice");
		readInvoice.put(QRBillReadInvoice.WHERE_COL.key(), "id_text");
		readInvoice.put(QRBillReadInvoice.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode creditor = source.putObject(QRBillMain.CREDITOR.key());
		creditor.put(QRBillCreditor.NAME.key(), "Christian Eugster");
		creditor.put(QRBillCreditor.ADDRESS.key(), "Axensteinstrasse 27");
		creditor.put(QRBillCreditor.CITY.key(), "9000 St. Gallen");
		creditor.put(QRBillCreditor.COUNTRY.key(), "CH");
		ObjectNode debtor = source.putObject(QRBillMain.DEBTOR.key());
		debtor.put(QRBillDebtor.NUMBER.key(), "K123456");
		debtor.put(QRBillDebtor.NAME.key(), "Christian Eugster");
		debtor.put(QRBillDebtor.ADDRESS.key(), "Axensteinstrasse 27");
		debtor.put(QRBillDebtor.CITY.key(), "9000 St. Gallen");
		debtor.put(QRBillDebtor.COUNTRY.key(), "CH");
		ObjectNode form = source.putObject(QRBillMain.FORM.key());
		form.put(QRBillForm.GRAPHICS_FORMAT.key(), GraphicsFormat.PDF.name());
		form.put(QRBillForm.OUTPUT_SIZE.key(), OutputSize.A4_PORTRAIT_SHEET.name());
		form.put(QRBillForm.LANGUAGE.key(), Language.DE.name());
		ObjectNode target = mapper.createObjectNode();
		QRBillParameter.checkAll(source, target);
		assertEquals(new BigDecimal(350).doubleValue(),
				target.get(QRBillMain.AMOUNT.key()).decimalValue().doubleValue());
		assertEquals("CHF", target.get(QRBillMain.CURRENCY.key()).asText());
		assertEquals("CH4431999123000889012", target.get(QRBillMain.IBAN.key()).asText());
		assertEquals("000000000000000000000000000", target.get(QRBillMain.REFERENCE.key()).asText());
		assertEquals("R123456", target.get(QRBillMain.INVOICE.key()).asText());
		assertEquals("Abonnement für 2020", target.get(QRBillMain.MESSAGE.key()).asText());
		assertEquals(ObjectNode.class, target.get(QRBillMain.DATABASE.key()).getClass());
		db = ObjectNode.class.cast(target.get(QRBillMain.DATABASE.key()));
		assertEquals("jdbc:filemaker://localhost/Test", db.get(QRBillDatabase.URL.key()).asText());
		assertEquals("christian", db.get(QRBillDatabase.USERNAME.key()).asText());
		assertEquals("ce_eu97", db.get(QRBillDatabase.PASSWORD.key()).asText());
		assertEquals(ObjectNode.class, db.get(QRBillDatabase.WRITE_QRBILL.key()).getClass());
		writeQRBill = ObjectNode.class.cast(db.get(QRBillDatabase.WRITE_QRBILL.key()));
		assertEquals("QRBill", writeQRBill.get(QRBillWrite.TABLE.key()).asText());
		assertEquals("name", writeQRBill.get(QRBillWrite.NAME_COL.key()).asText());
		assertEquals("qrbill", writeQRBill.get(QRBillWrite.QRBILL_COL.key()).asText());
		assertEquals("id_text", writeQRBill.get(QRBillWrite.WHERE_COL.key()).asText());
		assertEquals("7019891C-7AA9-4831-B0DC-EB69F5012BDC", writeQRBill.get(QRBillWrite.WHERE_VAL.key()).asText());
		assertEquals(ObjectNode.class, db.get(QRBillDatabase.READ_INVOICE.key()).getClass());
		readInvoice = ObjectNode.class.cast(db.get(QRBillDatabase.READ_INVOICE.key()));
		assertEquals("QRBill", readInvoice.get(QRBillReadInvoice.TABLE.key()).asText());
		assertEquals("invoice", readInvoice.get(QRBillReadInvoice.INVOICE_COL.key()).asText());
		assertEquals("id_text", readInvoice.get(QRBillReadInvoice.WHERE_COL.key()).asText());
		assertEquals("7019891C-7AA9-4831-B0DC-EB69F5012BDC",
				readInvoice.get(QRBillReadInvoice.WHERE_VAL.key()).asText());
		assertEquals(ObjectNode.class, target.get(QRBillMain.CREDITOR.key()).getClass());
		creditor = ObjectNode.class.cast(target.get(QRBillMain.CREDITOR.key()));
		assertEquals("Christian Eugster", creditor.get(QRBillCreditor.NAME.key()).asText());
		assertEquals("Axensteinstrasse 27", creditor.get(QRBillCreditor.ADDRESS.key()).asText());
		assertEquals("9000 St. Gallen", creditor.get(QRBillCreditor.CITY.key()).asText());
		assertEquals("CH", creditor.get(QRBillCreditor.COUNTRY.key()).asText());
		assertEquals(ObjectNode.class, target.get(QRBillMain.DEBTOR.key()).getClass());
		debtor = ObjectNode.class.cast(target.get(QRBillMain.DEBTOR.key()));
		assertEquals("K123456", debtor.get(QRBillDebtor.NUMBER.key()).asText());
		assertEquals("Christian Eugster", debtor.get(QRBillDebtor.NAME.key()).asText());
		assertEquals("Axensteinstrasse 27", debtor.get(QRBillDebtor.ADDRESS.key()).asText());
		assertEquals("9000 St. Gallen", debtor.get(QRBillDebtor.CITY.key()).asText());
		assertEquals("CH", debtor.get(QRBillDebtor.COUNTRY.key()).asText());
		assertEquals(ObjectNode.class, target.get(QRBillMain.FORM.key()).getClass());
		form = ObjectNode.class.cast(target.get(QRBillMain.FORM.key()));
		assertEquals(GraphicsFormat.PDF.name(), form.get(QRBillForm.GRAPHICS_FORMAT.key()).asText());
		assertEquals(OutputSize.A4_PORTRAIT_SHEET.name(), form.get(QRBillForm.OUTPUT_SIZE.key()).asText());
		assertEquals(Language.DE.name(), form.get(QRBillForm.LANGUAGE.key()).asText());
	}

	@Test
	public void testMinimumParametersOK()
	{
		ObjectNode source = mapper.createObjectNode();
		source.put(QRBillMain.CURRENCY.key(), "CHF");
		source.put(QRBillMain.IBAN.key(), "CH4431999123000889012");
		source.put(QRBillMain.REFERENCE.key(), "00000000000000000000000000");
		source.put(QRBillMain.INVOICE.key(), "R123456");
		ObjectNode db = source.putObject(QRBillMain.DATABASE.key());
		db.put(QRBillDatabase.URL.key(), "jdbc:filemaker://localhost/Test");
		db.put(QRBillDatabase.USERNAME.key(), "christian");
		db.put(QRBillDatabase.PASSWORD.key(), "ce_eu97");
		ObjectNode writeQRBill = db.putObject(QRBillDatabase.WRITE_QRBILL.key());
		writeQRBill.put(QRBillWrite.TABLE.key(), "QRBill");
		writeQRBill.put(QRBillWrite.NAME_COL.key(), "name");
		writeQRBill.put(QRBillWrite.QRBILL_COL.key(), "qrbill");
		writeQRBill.put(QRBillWrite.WHERE_COL.key(), "id_text");
		writeQRBill.put(QRBillWrite.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode readInvoice = db.putObject(QRBillDatabase.READ_INVOICE.key());
		readInvoice.put(QRBillReadInvoice.TABLE.key(), "QRBill");
		readInvoice.put(QRBillReadInvoice.INVOICE_COL.key(), "invoice");
		readInvoice.put(QRBillReadInvoice.WHERE_COL.key(), "id_text");
		readInvoice.put(QRBillReadInvoice.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode creditor = source.putObject(QRBillMain.CREDITOR.key());
		creditor.put(QRBillCreditor.NAME.key(), "Christian Eugster");
		creditor.put(QRBillCreditor.ADDRESS.key(), "Axensteinstrasse 27");
		creditor.put(QRBillCreditor.CITY.key(), "9000 St. Gallen");
		creditor.put(QRBillCreditor.COUNTRY.key(), "CH");
		ObjectNode form = source.putObject(QRBillMain.FORM.key());
		form.put(QRBillForm.GRAPHICS_FORMAT.key(), GraphicsFormat.PDF.name());
		form.put(QRBillForm.OUTPUT_SIZE.key(), OutputSize.A4_PORTRAIT_SHEET.name());
		form.put(QRBillForm.LANGUAGE.key(), Language.DE.name());
		ObjectNode target = mapper.createObjectNode();
		QRBillParameter.checkAll(source, target);
		assertNull(target.get(QRBillMain.AMOUNT.key()));
		assertEquals("CHF", target.get(QRBillMain.CURRENCY.key()).asText());
		assertEquals("CH4431999123000889012", target.get(QRBillMain.IBAN.key()).asText());
		assertEquals("000000000000000000000000000", target.get(QRBillMain.REFERENCE.key()).asText());
		assertEquals("R123456", target.get(QRBillMain.INVOICE.key()).asText());
		assertNull(target.get(QRBillMain.MESSAGE.key()));
		assertEquals(ObjectNode.class, target.get(QRBillMain.DATABASE.key()).getClass());
		assertEquals(ObjectNode.class, target.get(QRBillMain.DATABASE.key()).getClass());
		db = ObjectNode.class.cast(target.get(QRBillMain.DATABASE.key()));
		assertEquals("jdbc:filemaker://localhost/Test", db.get(QRBillDatabase.URL.key()).asText());
		assertEquals("christian", db.get(QRBillDatabase.USERNAME.key()).asText());
		assertEquals("ce_eu97", db.get(QRBillDatabase.PASSWORD.key()).asText());
		assertEquals(ObjectNode.class, db.get(QRBillDatabase.WRITE_QRBILL.key()).getClass());
		writeQRBill = ObjectNode.class.cast(db.get(QRBillDatabase.WRITE_QRBILL.key()));
		assertEquals("QRBill", writeQRBill.get(QRBillWrite.TABLE.key()).asText());
		assertEquals("name", writeQRBill.get(QRBillWrite.NAME_COL.key()).asText());
		assertEquals("qrbill", writeQRBill.get(QRBillWrite.QRBILL_COL.key()).asText());
		assertEquals("id_text", writeQRBill.get(QRBillWrite.WHERE_COL.key()).asText());
		assertEquals("7019891C-7AA9-4831-B0DC-EB69F5012BDC", writeQRBill.get(QRBillWrite.WHERE_VAL.key()).asText());
		assertEquals(ObjectNode.class, db.get(QRBillDatabase.READ_INVOICE.key()).getClass());
		readInvoice = ObjectNode.class.cast(db.get(QRBillDatabase.READ_INVOICE.key()));
		assertEquals("QRBill", readInvoice.get(QRBillReadInvoice.TABLE.key()).asText());
		assertEquals("invoice", readInvoice.get(QRBillReadInvoice.INVOICE_COL.key()).asText());
		assertEquals("id_text", readInvoice.get(QRBillReadInvoice.WHERE_COL.key()).asText());
		assertEquals("7019891C-7AA9-4831-B0DC-EB69F5012BDC",
				readInvoice.get(QRBillReadInvoice.WHERE_VAL.key()).asText());
		assertEquals(ObjectNode.class, target.get(QRBillMain.CREDITOR.key()).getClass());
		creditor = ObjectNode.class.cast(target.get(QRBillMain.CREDITOR.key()));
		assertEquals("Christian Eugster", creditor.get(QRBillCreditor.NAME.key()).asText());
		assertEquals("Axensteinstrasse 27", creditor.get(QRBillCreditor.ADDRESS.key()).asText());
		assertEquals("9000 St. Gallen", creditor.get(QRBillCreditor.CITY.key()).asText());
		assertEquals("CH", creditor.get(QRBillCreditor.COUNTRY.key()).asText());
		assertNull(target.get(QRBillMain.DEBTOR.key()));
		assertEquals(ObjectNode.class, target.get(QRBillMain.FORM.key()).getClass());
		form = ObjectNode.class.cast(target.get(QRBillMain.FORM.key()));
		assertEquals(GraphicsFormat.PDF.name(), form.get(QRBillForm.GRAPHICS_FORMAT.key()).asText());
		assertEquals(OutputSize.A4_PORTRAIT_SHEET.name(), form.get(QRBillForm.OUTPUT_SIZE.key()).asText());
		assertEquals(Language.DE.name(), form.get(QRBillForm.LANGUAGE.key()).asText());
		assertEquals("OK", target.get("result").asText());
		assertNull(target.get("errors"));
	}

	@Test
	public void testMissingSubParameterError() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode source = mapper.createObjectNode();
		source.put(QRBillMain.CURRENCY.key(), "CHF");
		source.put(QRBillMain.IBAN.key(), "CH4431999123000889012");
		source.put(QRBillMain.REFERENCE.key(), "00000000000000000000000000");
		source.put(QRBillMain.INVOICE.key(), "R123456");
		ObjectNode db = source.putObject(QRBillMain.DATABASE.key());
		db.put(QRBillDatabase.URL.key(), "jdbc:filemaker://localhost/Test");
		db.put(QRBillDatabase.USERNAME.key(), "christian");
		db.put(QRBillDatabase.PASSWORD.key(), "ce_eu97");
		ObjectNode writeQRBill = db.putObject(QRBillDatabase.WRITE_QRBILL.key());
		writeQRBill.put(QRBillWrite.TABLE.key(), "QRBill");
		writeQRBill.put(QRBillWrite.NAME_COL.key(), "name");
		writeQRBill.put(QRBillWrite.QRBILL_COL.key(), "qrbill");
		writeQRBill.put(QRBillWrite.WHERE_COL.key(), "id_text");
		writeQRBill.put(QRBillWrite.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode readInvoice = db.putObject(QRBillDatabase.READ_INVOICE.key());
		readInvoice.put(QRBillReadInvoice.TABLE.key(), "QRBill");
		readInvoice.put(QRBillReadInvoice.INVOICE_COL.key(), "invoice");
		readInvoice.put(QRBillReadInvoice.WHERE_COL.key(), "id_text");
		readInvoice.put(QRBillReadInvoice.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		source.putObject(QRBillMain.CREDITOR.key());
		source.putObject(QRBillMain.DEBTOR.key());
		ObjectNode form = source.putObject(QRBillMain.FORM.key());
		form.put(QRBillForm.GRAPHICS_FORMAT.key(), GraphicsFormat.PDF.name());
		form.put(QRBillForm.OUTPUT_SIZE.key(), OutputSize.A4_PORTRAIT_SHEET.name());
		form.put(QRBillForm.LANGUAGE.key(), Language.DE.name());
		ObjectNode target = mapper.createObjectNode();
		QRBillParameter.checkAll(source, target);
		assertNull(target.get(QRBillMain.AMOUNT.key()));
		assertEquals("CHF", target.get(QRBillMain.CURRENCY.key()).asText());
		assertEquals("CH4431999123000889012", target.get(QRBillMain.IBAN.key()).asText());
		assertEquals("000000000000000000000000000", target.get(QRBillMain.REFERENCE.key()).asText());
		assertEquals("R123456", target.get(QRBillMain.INVOICE.key()).asText());
		assertNull(target.get(QRBillMain.MESSAGE.key()));
		assertEquals(ObjectNode.class, target.get(QRBillMain.DATABASE.key()).getClass());
		assertEquals(ObjectNode.class, target.get(QRBillMain.DATABASE.key()).getClass());
		db = ObjectNode.class.cast(target.get(QRBillMain.DATABASE.key()));
		assertEquals("jdbc:filemaker://localhost/Test", db.get(QRBillDatabase.URL.key()).asText());
		assertEquals("christian", db.get(QRBillDatabase.USERNAME.key()).asText());
		assertEquals("ce_eu97", db.get(QRBillDatabase.PASSWORD.key()).asText());
		assertEquals(ObjectNode.class, db.get(QRBillDatabase.WRITE_QRBILL.key()).getClass());
		writeQRBill = ObjectNode.class.cast(db.get(QRBillDatabase.WRITE_QRBILL.key()));
		assertEquals("QRBill", writeQRBill.get(QRBillWrite.TABLE.key()).asText());
		assertEquals("name", writeQRBill.get(QRBillWrite.NAME_COL.key()).asText());
		assertEquals("qrbill", writeQRBill.get(QRBillWrite.QRBILL_COL.key()).asText());
		assertEquals("id_text", writeQRBill.get(QRBillWrite.WHERE_COL.key()).asText());
		assertEquals("7019891C-7AA9-4831-B0DC-EB69F5012BDC", writeQRBill.get(QRBillWrite.WHERE_VAL.key()).asText());
		assertEquals(ObjectNode.class, db.get(QRBillDatabase.READ_INVOICE.key()).getClass());
		readInvoice = ObjectNode.class.cast(db.get(QRBillDatabase.READ_INVOICE.key()));
		assertEquals("QRBill", readInvoice.get(QRBillReadInvoice.TABLE.key()).asText());
		assertEquals("invoice", readInvoice.get(QRBillReadInvoice.INVOICE_COL.key()).asText());
		assertEquals("id_text", readInvoice.get(QRBillReadInvoice.WHERE_COL.key()).asText());
		assertEquals("7019891C-7AA9-4831-B0DC-EB69F5012BDC",
				readInvoice.get(QRBillReadInvoice.WHERE_VAL.key()).asText());
		assertNull(target.get(QRBillMain.CREDITOR.key()));
		assertNull(target.get(QRBillMain.DEBTOR.key()));
		assertEquals(ObjectNode.class, target.get(QRBillMain.FORM.key()).getClass());
		form = ObjectNode.class.cast(target.get(QRBillMain.FORM.key()));
		assertEquals(GraphicsFormat.PDF.name(), form.get(QRBillForm.GRAPHICS_FORMAT.key()).asText());
		assertEquals(OutputSize.A4_PORTRAIT_SHEET.name(), form.get(QRBillForm.OUTPUT_SIZE.key()).asText());
		assertEquals(Language.DE.name(), form.get(QRBillForm.LANGUAGE.key()).asText());
		assertEquals("Fehler", target.get("result").asText());
		assertEquals(9, target.get("errors").size());
		ArrayNode errors = ArrayNode.class.cast(target.get("errors"));
		Iterator<JsonNode> iterator = errors.iterator();
		while (iterator.hasNext())
		{
			JsonNode node = iterator.next();
			String msg = node.asText();
			if ("Der Parameter 'creditor' ist zwingend erforderlich.".equals(msg))
			{
				assertTrue(true);
			}
			if ("Der Parameter 'creditor.name' ist zwingend erforderlich.".equals(msg))
			{
				assertTrue(true);
			}
			else if ("Der Parameter 'creditor.address' ist zwingend erforderlich.".equals(msg))
			{
				assertTrue(true);
			}
			else if ("Der Parameter 'creditor.city' ist zwingend erforderlich.".equals(msg))
			{
				assertTrue(true);
			}
			else if ("Der Parameter 'creditor.country' ist zwingend erforderlich.".equals(msg))
			{
				assertTrue(true);
			}
			else if ("Der Parameter 'debtor.name' ist zwingend erforderlich.".equals(msg))
			{
				assertTrue(true);
			}
			else if ("Der Parameter 'debtor.address' ist zwingend erforderlich.".equals(msg))
			{
				assertTrue(true);
			}
			else if ("Der Parameter 'debtor.city' ist zwingend erforderlich.".equals(msg))
			{
				assertTrue(true);
			}
			else if ("Der Parameter 'debtor.country' ist zwingend erforderlich.".equals(msg))
			{
				assertTrue(true);
			}
			else if ("Der Parameter 'debtor.number' ist zwingend erforderlich.".equals(msg))
			{
				assertTrue(true);
			}
			else
			{
				fail();
			}
		}
	}

	@Test
	public void testQRBillOnlyWithoutExistingInvoiceOk() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode source = mapper.createObjectNode();
		source.put(QRBillMain.AMOUNT.key(), new BigDecimal(350));
		source.put(QRBillMain.CURRENCY.key(), "CHF");
		source.put(QRBillMain.IBAN.key(), "CH4431999123000889012");
		source.put(QRBillMain.REFERENCE.key(), "00000000000000000000000000");
		source.put(QRBillMain.INVOICE.key(), "R123456");
		source.put(QRBillMain.MESSAGE.key(), "Abonnement für 2020");
		ObjectNode db = source.putObject(QRBillMain.DATABASE.key());
		db.put(QRBillDatabase.URL.key(), "jdbc:filemaker://localhost/Test");
		db.put(QRBillDatabase.USERNAME.key(), "christian");
		db.put(QRBillDatabase.PASSWORD.key(), "ce_eu97");
		ObjectNode writeQRBill = db.putObject(QRBillDatabase.WRITE_QRBILL.key());
		writeQRBill.put(QRBillWrite.TABLE.key(), "QRBill");
		writeQRBill.put(QRBillWrite.NAME_COL.key(), "name");
		writeQRBill.put(QRBillWrite.QRBILL_COL.key(), "qrbill");
		writeQRBill.put(QRBillWrite.WHERE_COL.key(), "id_text");
		writeQRBill.put(QRBillWrite.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode creditor = source.putObject(QRBillMain.CREDITOR.key());
		creditor.put(QRBillCreditor.NAME.key(), "Christian Eugster");
		creditor.put(QRBillCreditor.ADDRESS.key(), "Axensteinstrasse 27");
		creditor.put(QRBillCreditor.CITY.key(), "9000 St. Gallen");
		creditor.put(QRBillCreditor.COUNTRY.key(), "CH");
		ObjectNode debtor = source.putObject(QRBillMain.DEBTOR.key());
		debtor.put(QRBillDebtor.NUMBER.key(), "K123456");
		debtor.put(QRBillDebtor.NAME.key(), "Christian Eugster");
		debtor.put(QRBillDebtor.ADDRESS.key(), "Axensteinstrasse 27");
		debtor.put(QRBillDebtor.CITY.key(), "9000 St. Gallen");
		debtor.put(QRBillDebtor.COUNTRY.key(), "CH");
		ObjectNode form = source.putObject(QRBillMain.FORM.key());
		form.put(QRBillForm.GRAPHICS_FORMAT.key(), GraphicsFormat.PDF.name());
		form.put(QRBillForm.OUTPUT_SIZE.key(), OutputSize.A4_PORTRAIT_SHEET.name());
		form.put(QRBillForm.LANGUAGE.key(), Language.DE.name());
		ObjectNode target = mapper.createObjectNode();
		QRBillParameter.checkAll(source, target);
		assertEquals(new BigDecimal(350).doubleValue(),
				target.get(QRBillMain.AMOUNT.key()).decimalValue().doubleValue());
		assertEquals("CHF", target.get(QRBillMain.CURRENCY.key()).asText());
		assertEquals("CH4431999123000889012", target.get(QRBillMain.IBAN.key()).asText());
		assertEquals("000000000000000000000000000", target.get(QRBillMain.REFERENCE.key()).asText());
		assertEquals("R123456", target.get(QRBillMain.INVOICE.key()).asText());
		assertEquals("Abonnement für 2020", target.get(QRBillMain.MESSAGE.key()).asText());
		assertEquals(ObjectNode.class, target.get(QRBillMain.DATABASE.key()).getClass());
		db = ObjectNode.class.cast(target.get(QRBillMain.DATABASE.key()));
		assertEquals("jdbc:filemaker://localhost/Test", db.get(QRBillDatabase.URL.key()).asText());
		assertEquals("christian", db.get(QRBillDatabase.USERNAME.key()).asText());
		assertEquals("ce_eu97", db.get(QRBillDatabase.PASSWORD.key()).asText());
		assertEquals(ObjectNode.class, db.get(QRBillDatabase.WRITE_QRBILL.key()).getClass());
		writeQRBill = ObjectNode.class.cast(db.get(QRBillDatabase.WRITE_QRBILL.key()));
		assertEquals("QRBill", writeQRBill.get(QRBillWrite.TABLE.key()).asText());
		assertEquals("name", writeQRBill.get(QRBillWrite.NAME_COL.key()).asText());
		assertEquals("qrbill", writeQRBill.get(QRBillWrite.QRBILL_COL.key()).asText());
		assertEquals("id_text", writeQRBill.get(QRBillWrite.WHERE_COL.key()).asText());
		assertEquals("7019891C-7AA9-4831-B0DC-EB69F5012BDC", writeQRBill.get(QRBillWrite.WHERE_VAL.key()).asText());
		assertNull(db.get(QRBillDatabase.READ_INVOICE.key()));
		assertEquals(ObjectNode.class, target.get(QRBillMain.CREDITOR.key()).getClass());
		creditor = ObjectNode.class.cast(target.get(QRBillMain.CREDITOR.key()));
		assertEquals("Christian Eugster", creditor.get(QRBillCreditor.NAME.key()).asText());
		assertEquals("Axensteinstrasse 27", creditor.get(QRBillCreditor.ADDRESS.key()).asText());
		assertEquals("9000 St. Gallen", creditor.get(QRBillCreditor.CITY.key()).asText());
		assertEquals("CH", creditor.get(QRBillCreditor.COUNTRY.key()).asText());
		assertEquals(ObjectNode.class, target.get(QRBillMain.DEBTOR.key()).getClass());
		debtor = ObjectNode.class.cast(target.get(QRBillMain.DEBTOR.key()));
		assertEquals("K123456", debtor.get(QRBillDebtor.NUMBER.key()).asText());
		assertEquals("Christian Eugster", debtor.get(QRBillDebtor.NAME.key()).asText());
		assertEquals("Axensteinstrasse 27", debtor.get(QRBillDebtor.ADDRESS.key()).asText());
		assertEquals("9000 St. Gallen", debtor.get(QRBillDebtor.CITY.key()).asText());
		assertEquals("CH", debtor.get(QRBillDebtor.COUNTRY.key()).asText());
		assertEquals(ObjectNode.class, target.get(QRBillMain.FORM.key()).getClass());
		form = ObjectNode.class.cast(target.get(QRBillMain.FORM.key()));
		assertEquals(GraphicsFormat.PDF.name(), form.get(QRBillForm.GRAPHICS_FORMAT.key()).asText());
		assertEquals(OutputSize.A4_PORTRAIT_SHEET.name(), form.get(QRBillForm.OUTPUT_SIZE.key()).asText());
		assertEquals(Language.DE.name(), form.get(QRBillForm.LANGUAGE.key()).asText());
		assertEquals("OK", target.get("result").asText());
		assertNull(target.get("errors"));
	}

	@Test
	public void testWithoutMandatoryValuesError() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode source = mapper.createObjectNode();
		ObjectNode target = mapper.createObjectNode();
		QRBillParameter.checkAll(source, target);
		assertNull(target.get(QRBillMain.AMOUNT.key()));
		assertNull(target.get(QRBillMain.CURRENCY.key()));
		assertNull(target.get(QRBillMain.IBAN.key()));
		assertNull(target.get(QRBillMain.REFERENCE.key()));
		assertNull(target.get(QRBillMain.INVOICE.key()));
		assertNull(target.get(QRBillMain.MESSAGE.key()));
		assertNull(target.get(QRBillMain.DATABASE.key()));
		assertNull(target.get(QRBillMain.CREDITOR.key()));
		assertNull(target.get(QRBillMain.DEBTOR.key()));
		assertNull(target.get(QRBillMain.FORM.key()));
		assertEquals("Fehler", target.get("result").asText());
		assertEquals(7, target.get("errors").size());
		ArrayNode errors = ArrayNode.class.cast(target.get("errors"));
		Iterator<JsonNode> iterator = errors.iterator();
		while (iterator.hasNext())
		{
			JsonNode node = iterator.next();
			String msg = node.asText();
			if ("Der Parameter 'currency' ist zwingend erforderlich.".equals(msg))
			{
				assertTrue(true);
			}
			else if ("Der Parameter 'iban' ist zwingend erforderlich.".equals(msg))
			{
				assertTrue(true);
			}
			else if ("Der Parameter 'reference' ist zwingend erforderlich.".equals(msg))
			{
				assertTrue(true);
			}
			else if ("Der Parameter 'invoice' ist zwingend erforderlich.".equals(msg))
			{
				assertTrue(true);
			}
			else if ("Der Parameter 'database' ist zwingend erforderlich.".equals(msg))
			{
				assertTrue(true);
			}
			else if ("Der Parameter 'creditor' ist zwingend erforderlich.".equals(msg))
			{
				assertTrue(true);
			}
			else if ("Der Parameter 'form' ist zwingend erforderlich.".equals(msg))
			{
				assertTrue(true);
			}
			else
			{
				fail();
			}
		}
	}

	@Test
	public void testIbanReferenceGraphicsFormatOutputSizeLanguageInvalidError()
			throws JsonMappingException, JsonProcessingException
	{
		ObjectNode source = mapper.createObjectNode();
		source.put(QRBillMain.CURRENCY.key(), "CHF");
		source.put(QRBillMain.IBAN.key(), "CH9431999123000889012");
		source.put(QRBillMain.REFERENCE.key(), "000000000000000000000000001");
		source.put(QRBillMain.INVOICE.key(), "R123456");
		ObjectNode db = source.putObject(QRBillMain.DATABASE.key());
		db.put(QRBillDatabase.URL.key(), "jdbc:filemaker://localhost/Test");
		db.put(QRBillDatabase.USERNAME.key(), "christian");
		db.put(QRBillDatabase.PASSWORD.key(), "ce_eu97");
		ObjectNode writeQRBill = db.putObject(QRBillDatabase.WRITE_QRBILL.key());
		writeQRBill.put(QRBillWrite.TABLE.key(), "QRBill");
		writeQRBill.put(QRBillWrite.NAME_COL.key(), "name");
		writeQRBill.put(QRBillWrite.QRBILL_COL.key(), "qrbill");
		writeQRBill.put(QRBillWrite.WHERE_COL.key(), "id_text");
		writeQRBill.put(QRBillWrite.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode readInvoice = db.putObject(QRBillDatabase.READ_INVOICE.key());
		readInvoice.put(QRBillReadInvoice.TABLE.key(), "QRBill");
		readInvoice.put(QRBillReadInvoice.INVOICE_COL.key(), "invoice");
		readInvoice.put(QRBillReadInvoice.WHERE_COL.key(), "id_text");
		readInvoice.put(QRBillReadInvoice.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode creditor = source.putObject(QRBillMain.CREDITOR.key());
		creditor.put(QRBillCreditor.NAME.key(), "Christian Eugster");
		creditor.put(QRBillCreditor.ADDRESS.key(), "Axensteinstrasse 27");
		creditor.put(QRBillCreditor.CITY.key(), "9000 St. Gallen");
		creditor.put(QRBillCreditor.COUNTRY.key(), "CH");
		ObjectNode form = source.putObject(QRBillMain.FORM.key());
		form.put(QRBillForm.GRAPHICS_FORMAT.key(), "XXX");
		form.put(QRBillForm.OUTPUT_SIZE.key(), "XXX");
		form.put(QRBillForm.LANGUAGE.key(), "XX");
		ObjectNode target = mapper.createObjectNode();

		QRBillParameter.checkAll(source, target);

		assertNull(target.get(QRBillMain.AMOUNT.key()));
		assertEquals("CHF", target.get(QRBillMain.CURRENCY.key()).asText());
		assertNull(target.get(QRBillMain.IBAN.key()));
		assertNull(target.get(QRBillMain.REFERENCE.key()));
		assertEquals("R123456", target.get(QRBillMain.INVOICE.key()).asText());
		assertNull(target.get(QRBillMain.MESSAGE.key()));
		assertEquals(ObjectNode.class, target.get(QRBillMain.DATABASE.key()).getClass());
		db = ObjectNode.class.cast(target.get(QRBillMain.DATABASE.key()));
		assertEquals("jdbc:filemaker://localhost/Test", db.get(QRBillDatabase.URL.key()).asText());
		assertEquals("christian", db.get(QRBillDatabase.USERNAME.key()).asText());
		assertEquals("ce_eu97", db.get(QRBillDatabase.PASSWORD.key()).asText());
		assertEquals(ObjectNode.class, db.get(QRBillDatabase.WRITE_QRBILL.key()).getClass());
		writeQRBill = ObjectNode.class.cast(db.get(QRBillDatabase.WRITE_QRBILL.key()));
		assertEquals("QRBill", writeQRBill.get(QRBillWrite.TABLE.key()).asText());
		assertEquals("name", writeQRBill.get(QRBillWrite.NAME_COL.key()).asText());
		assertEquals("qrbill", writeQRBill.get(QRBillWrite.QRBILL_COL.key()).asText());
		assertEquals("id_text", writeQRBill.get(QRBillWrite.WHERE_COL.key()).asText());
		assertEquals("7019891C-7AA9-4831-B0DC-EB69F5012BDC", writeQRBill.get(QRBillWrite.WHERE_VAL.key()).asText());
		assertEquals(ObjectNode.class, db.get(QRBillDatabase.READ_INVOICE.key()).getClass());
		readInvoice = ObjectNode.class.cast(db.get(QRBillDatabase.READ_INVOICE.key()));
		assertEquals("QRBill", readInvoice.get(QRBillReadInvoice.TABLE.key()).asText());
		assertEquals("invoice", readInvoice.get(QRBillReadInvoice.INVOICE_COL.key()).asText());
		assertEquals("id_text", readInvoice.get(QRBillReadInvoice.WHERE_COL.key()).asText());
		assertEquals("7019891C-7AA9-4831-B0DC-EB69F5012BDC",
				readInvoice.get(QRBillReadInvoice.WHERE_VAL.key()).asText());
		assertEquals(ObjectNode.class, target.get(QRBillMain.CREDITOR.key()).getClass());
		creditor = ObjectNode.class.cast(target.get(QRBillMain.CREDITOR.key()));
		assertEquals("Christian Eugster", creditor.get(QRBillCreditor.NAME.key()).asText());
		assertEquals("Axensteinstrasse 27", creditor.get(QRBillCreditor.ADDRESS.key()).asText());
		assertEquals("9000 St. Gallen", creditor.get(QRBillCreditor.CITY.key()).asText());
		assertEquals("CH", creditor.get(QRBillCreditor.COUNTRY.key()).asText());
		assertNull(target.get(QRBillMain.DEBTOR.key()));
		assertNull(target.get(QRBillMain.FORM.key()));
		assertEquals("Fehler", target.get("result").asText());
		assertEquals(5, target.get("errors").size());
		ArrayNode errors = ArrayNode.class.cast(target.get("errors"));
		Iterator<JsonNode> iterator = errors.iterator();
		while (iterator.hasNext())
		{
			JsonNode node = iterator.next();
			String msg = node.asText();
			if ("Der Parameter 'iban' ist ungültig.".equals(msg))
			{
				assertTrue(true);
			}
			else if ("Der Parameter 'reference' ist ungültig.".equals(msg))
			{
				assertTrue(true);
			}
			else if ("Der Parameter 'form.graphics_format' ist ungültig.".equals(msg))
			{
				assertTrue(true);
			}
			else if ("Der Parameter 'form.output_size' ist ungültig.".equals(msg))
			{
				assertTrue(true);
			}
			else if ("Der Parameter 'form.language' ist ungültig.".equals(msg))
			{
				assertTrue(true);
			}
			else
			{
				fail();
			}
		}
	}

	@Test
	public void testFsl() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode source = mapper.createObjectNode();
		source.put(QRBillMain.AMOUNT.key(), new BigDecimal(350));
		source.put(QRBillMain.CURRENCY.key(), "CHF");
		source.put(QRBillMain.IBAN.key(), "CH4431999123000889012");
		source.put(QRBillMain.REFERENCE.key(), "1234560000123456");
		source.put(QRBillMain.INVOICE.key(), "R123456");
		source.put(QRBillMain.MESSAGE.key(), "Abonnement für 2020");
		ObjectNode db = source.putObject(QRBillMain.DATABASE.key());
		db.put(QRBillDatabase.URL.key(), "jdbc:filemaker://localhost/Test");
		db.put(QRBillDatabase.USERNAME.key(), "christian");
		db.put(QRBillDatabase.PASSWORD.key(), "ce_eu97");
		ObjectNode writeQRBill = db.putObject(QRBillDatabase.WRITE_QRBILL.key());
		writeQRBill.put(QRBillWrite.TABLE.key(), "QRBill");
		writeQRBill.put(QRBillWrite.NAME_COL.key(), "name");
		writeQRBill.put(QRBillWrite.QRBILL_COL.key(), "qrbill");
		writeQRBill.put(QRBillWrite.WHERE_COL.key(), "id_text");
		writeQRBill.put(QRBillWrite.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode creditor = source.putObject(QRBillMain.CREDITOR.key());
		creditor.put(QRBillCreditor.NAME.key(), "Christian Eugster");
		creditor.put(QRBillCreditor.ADDRESS.key(), "Axensteinstrasse 27");
		creditor.put(QRBillCreditor.CITY.key(), "9000 St. Gallen");
		creditor.put(QRBillCreditor.COUNTRY.key(), "CH");
		ObjectNode debtor = source.putObject(QRBillMain.DEBTOR.key());
		debtor.put(QRBillDebtor.NUMBER.key(), "K123456");
		debtor.put(QRBillDebtor.NAME.key(), "Christian Eugster");
		debtor.put(QRBillDebtor.ADDRESS.key(), "Axensteinstrasse 27");
		debtor.put(QRBillDebtor.CITY.key(), "9000 St. Gallen");
		debtor.put(QRBillDebtor.COUNTRY.key(), "CH");
		ObjectNode form = source.putObject(QRBillMain.FORM.key());
		form.put(QRBillForm.GRAPHICS_FORMAT.key(), GraphicsFormat.PDF.name());
		form.put(QRBillForm.OUTPUT_SIZE.key(), OutputSize.QR_BILL_EXTRA_SPACE.name());
		form.put(QRBillForm.LANGUAGE.key(), Language.DE.name());
		String result = new Fsl().execute("CreateQRBill", source.toString());
		JsonNode target = mapper.readTree(result);
		assertEquals(new BigDecimal(350).doubleValue(),
				target.get(QRBillMain.AMOUNT.key()).decimalValue().doubleValue());
		assertEquals("CHF", target.get(QRBillMain.CURRENCY.key()).asText());
		assertEquals("CH4431999123000889012", target.get(QRBillMain.IBAN.key()).asText());
		assertEquals("000000000012345600001234567", target.get(QRBillMain.REFERENCE.key()).asText());
		assertEquals("R123456", target.get(QRBillMain.INVOICE.key()).asText());
		assertEquals("Abonnement für 2020", target.get(QRBillMain.MESSAGE.key()).asText());
		assertEquals(ObjectNode.class, target.get(QRBillMain.DATABASE.key()).getClass());
		db = ObjectNode.class.cast(target.get(QRBillMain.DATABASE.key()));
		assertEquals("jdbc:filemaker://localhost/Test", db.get(QRBillDatabase.URL.key()).asText());
		assertEquals("christian", db.get(QRBillDatabase.USERNAME.key()).asText());
		assertEquals("ce_eu97", db.get(QRBillDatabase.PASSWORD.key()).asText());
		assertEquals(ObjectNode.class, db.get(QRBillDatabase.WRITE_QRBILL.key()).getClass());
		writeQRBill = ObjectNode.class.cast(db.get(QRBillDatabase.WRITE_QRBILL.key()));
		assertEquals("QRBill", writeQRBill.get(QRBillWrite.TABLE.key()).asText());
		assertEquals("name", writeQRBill.get(QRBillWrite.NAME_COL.key()).asText());
		assertEquals("qrbill", writeQRBill.get(QRBillWrite.QRBILL_COL.key()).asText());
		assertEquals("id_text", writeQRBill.get(QRBillWrite.WHERE_COL.key()).asText());
		assertEquals("7019891C-7AA9-4831-B0DC-EB69F5012BDC", writeQRBill.get(QRBillWrite.WHERE_VAL.key()).asText());
		assertNull(db.get(QRBillDatabase.READ_INVOICE.key()));
		assertEquals(ObjectNode.class, target.get(QRBillMain.CREDITOR.key()).getClass());
		creditor = ObjectNode.class.cast(target.get(QRBillMain.CREDITOR.key()));
		assertEquals("Christian Eugster", creditor.get(QRBillCreditor.NAME.key()).asText());
		assertEquals("Axensteinstrasse 27", creditor.get(QRBillCreditor.ADDRESS.key()).asText());
		assertEquals("9000 St. Gallen", creditor.get(QRBillCreditor.CITY.key()).asText());
		assertEquals("CH", creditor.get(QRBillCreditor.COUNTRY.key()).asText());
		assertEquals(ObjectNode.class, target.get(QRBillMain.DEBTOR.key()).getClass());
		debtor = ObjectNode.class.cast(target.get(QRBillMain.DEBTOR.key()));
		assertEquals("K123456", debtor.get(QRBillDebtor.NUMBER.key()).asText());
		assertEquals("Christian Eugster", debtor.get(QRBillDebtor.NAME.key()).asText());
		assertEquals("Axensteinstrasse 27", debtor.get(QRBillDebtor.ADDRESS.key()).asText());
		assertEquals("9000 St. Gallen", debtor.get(QRBillDebtor.CITY.key()).asText());
		assertEquals("CH", debtor.get(QRBillDebtor.COUNTRY.key()).asText());
		assertEquals(ObjectNode.class, target.get(QRBillMain.FORM.key()).getClass());
		form = ObjectNode.class.cast(target.get(QRBillMain.FORM.key()));
		assertEquals(GraphicsFormat.PDF.name(), form.get(QRBillForm.GRAPHICS_FORMAT.key()).asText());
		assertEquals(OutputSize.QR_BILL_EXTRA_SPACE.name(), form.get(QRBillForm.OUTPUT_SIZE.key()).asText());
		assertEquals(Language.DE.name(), form.get(QRBillForm.LANGUAGE.key()).asText());
		assertEquals("OK", target.get("result").asText());
		assertNull(target.get("errors"));
	}

	@Test
	public void testFslWithInvoiceAppend() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode source = mapper.createObjectNode();
		source.put(QRBillMain.AMOUNT.key(), new BigDecimal(350));
		source.put(QRBillMain.CURRENCY.key(), "CHF");
		source.put(QRBillMain.IBAN.key(), "CH4431999123000889012");
		source.put(QRBillMain.REFERENCE.key(), "1234560000123456");
		source.put(QRBillMain.INVOICE.key(), "R123456");
		source.put(QRBillMain.MESSAGE.key(), "Abonnement für 2020");
		ObjectNode db = source.putObject(QRBillMain.DATABASE.key());
		db.put(QRBillDatabase.URL.key(), "jdbc:filemaker://localhost/Test");
		db.put(QRBillDatabase.USERNAME.key(), "christian");
		db.put(QRBillDatabase.PASSWORD.key(), "ce_eu97");
		ObjectNode writeQRBill = db.putObject(QRBillDatabase.WRITE_QRBILL.key());
		writeQRBill.put(QRBillWrite.TABLE.key(), "QRBill");
		writeQRBill.put(QRBillWrite.NAME_COL.key(), "name");
		writeQRBill.put(QRBillWrite.QRBILL_COL.key(), "qrbill");
		writeQRBill.put(QRBillWrite.WHERE_COL.key(), "id_text");
		writeQRBill.put(QRBillWrite.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode readInvoice = db.putObject(QRBillDatabase.READ_INVOICE.key());
		readInvoice.put(QRBillReadInvoice.TABLE.key(), "QRBill");
		readInvoice.put(QRBillReadInvoice.INVOICE_COL.key(), "invoice");
		readInvoice.put(QRBillReadInvoice.WHERE_COL.key(), "id_text");
		readInvoice.put(QRBillReadInvoice.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode creditor = source.putObject(QRBillMain.CREDITOR.key());
		creditor.put(QRBillCreditor.NAME.key(), "Christian Eugster");
		creditor.put(QRBillCreditor.ADDRESS.key(), "Axensteinstrasse 27");
		creditor.put(QRBillCreditor.CITY.key(), "9000 St. Gallen");
		creditor.put(QRBillCreditor.COUNTRY.key(), "CH");
		ObjectNode debtor = source.putObject(QRBillMain.DEBTOR.key());
		debtor.put(QRBillDebtor.NUMBER.key(), "K123456");
		debtor.put(QRBillDebtor.NAME.key(), "Christian Eugster");
		debtor.put(QRBillDebtor.ADDRESS.key(), "Axensteinstrasse 27");
		debtor.put(QRBillDebtor.CITY.key(), "9000 St. Gallen");
		debtor.put(QRBillDebtor.COUNTRY.key(), "CH");
		ObjectNode form = source.putObject(QRBillMain.FORM.key());
		form.put(QRBillForm.GRAPHICS_FORMAT.key(), GraphicsFormat.PDF.name());
		form.put(QRBillForm.OUTPUT_SIZE.key(), OutputSize.QR_BILL_EXTRA_SPACE.name());
		form.put(QRBillForm.LANGUAGE.key(), Language.DE.name());
		String result = new Fsl().execute("CreateQRBill", source.toString());
		JsonNode target = mapper.readTree(result);
		assertEquals(new BigDecimal(350).doubleValue(),
				target.get(QRBillMain.AMOUNT.key()).decimalValue().doubleValue());
		assertEquals("CHF", target.get(QRBillMain.CURRENCY.key()).asText());
		assertEquals("CH4431999123000889012", target.get(QRBillMain.IBAN.key()).asText());
		assertEquals("000000000012345600001234567", target.get(QRBillMain.REFERENCE.key()).asText());
		assertEquals("R123456", target.get(QRBillMain.INVOICE.key()).asText());
		assertEquals("Abonnement für 2020", target.get(QRBillMain.MESSAGE.key()).asText());
		assertEquals(ObjectNode.class, target.get(QRBillMain.DATABASE.key()).getClass());
		db = ObjectNode.class.cast(target.get(QRBillMain.DATABASE.key()));
		assertEquals("jdbc:filemaker://localhost/Test", db.get(QRBillDatabase.URL.key()).asText());
		assertEquals("christian", db.get(QRBillDatabase.USERNAME.key()).asText());
		assertEquals("ce_eu97", db.get(QRBillDatabase.PASSWORD.key()).asText());
		assertEquals(ObjectNode.class, db.get(QRBillDatabase.WRITE_QRBILL.key()).getClass());
		writeQRBill = ObjectNode.class.cast(db.get(QRBillDatabase.WRITE_QRBILL.key()));
		assertEquals("QRBill", writeQRBill.get(QRBillWrite.TABLE.key()).asText());
		assertEquals("name", writeQRBill.get(QRBillWrite.NAME_COL.key()).asText());
		assertEquals("qrbill", writeQRBill.get(QRBillWrite.QRBILL_COL.key()).asText());
		assertEquals("id_text", writeQRBill.get(QRBillWrite.WHERE_COL.key()).asText());
		assertEquals("7019891C-7AA9-4831-B0DC-EB69F5012BDC", writeQRBill.get(QRBillWrite.WHERE_VAL.key()).asText());
		readInvoice = ObjectNode.class.cast(db.get(QRBillDatabase.READ_INVOICE.key()));
		assertEquals("QRBill", readInvoice.get(QRBillReadInvoice.TABLE.key()).asText());
		assertEquals("invoice", readInvoice.get(QRBillReadInvoice.INVOICE_COL.key()).asText());
		assertEquals("id_text", readInvoice.get(QRBillReadInvoice.WHERE_COL.key()).asText());
		assertEquals("7019891C-7AA9-4831-B0DC-EB69F5012BDC",
				readInvoice.get(QRBillReadInvoice.WHERE_VAL.key()).asText());
		assertEquals(ObjectNode.class, target.get(QRBillMain.CREDITOR.key()).getClass());
		creditor = ObjectNode.class.cast(target.get(QRBillMain.CREDITOR.key()));
		assertEquals("Christian Eugster", creditor.get(QRBillCreditor.NAME.key()).asText());
		assertEquals("Axensteinstrasse 27", creditor.get(QRBillCreditor.ADDRESS.key()).asText());
		assertEquals("9000 St. Gallen", creditor.get(QRBillCreditor.CITY.key()).asText());
		assertEquals("CH", creditor.get(QRBillCreditor.COUNTRY.key()).asText());
		assertEquals(ObjectNode.class, target.get(QRBillMain.DEBTOR.key()).getClass());
		debtor = ObjectNode.class.cast(target.get(QRBillMain.DEBTOR.key()));
		assertEquals("K123456", debtor.get(QRBillDebtor.NUMBER.key()).asText());
		assertEquals("Christian Eugster", debtor.get(QRBillDebtor.NAME.key()).asText());
		assertEquals("Axensteinstrasse 27", debtor.get(QRBillDebtor.ADDRESS.key()).asText());
		assertEquals("9000 St. Gallen", debtor.get(QRBillDebtor.CITY.key()).asText());
		assertEquals("CH", debtor.get(QRBillDebtor.COUNTRY.key()).asText());
		assertEquals(ObjectNode.class, target.get(QRBillMain.FORM.key()).getClass());
		form = ObjectNode.class.cast(target.get(QRBillMain.FORM.key()));
		assertEquals(GraphicsFormat.PDF.name(), form.get(QRBillForm.GRAPHICS_FORMAT.key()).asText());
		assertEquals(OutputSize.QR_BILL_EXTRA_SPACE.name(), form.get(QRBillForm.OUTPUT_SIZE.key()).asText());
		assertEquals(Language.DE.name(), form.get(QRBillForm.LANGUAGE.key()).asText());
		assertEquals("OK", target.get("result").asText());
		assertNull(target.get("errors"));
	}

	@Test
	public void testFslNoConnection() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode source = mapper.createObjectNode();
		source.put(QRBillMain.AMOUNT.key(), new BigDecimal(350));
		source.put(QRBillMain.CURRENCY.key(), "CHF");
		source.put(QRBillMain.IBAN.key(), "CH4431999123000889012");
		source.put(QRBillMain.REFERENCE.key(), "1234560000123456");
		source.put(QRBillMain.INVOICE.key(), "R123456");
		source.put(QRBillMain.MESSAGE.key(), "Abonnement für 2020");
		ObjectNode db = source.putObject(QRBillMain.DATABASE.key());
		db.put(QRBillDatabase.URL.key(), "jdbc:filemaker://localhost/Invalid");
		db.put(QRBillDatabase.USERNAME.key(), "christian");
		db.put(QRBillDatabase.PASSWORD.key(), "ce_eu97");
		ObjectNode writeQRBill = db.putObject(QRBillDatabase.WRITE_QRBILL.key());
		writeQRBill.put(QRBillWrite.TABLE.key(), "QRBill");
		writeQRBill.put(QRBillWrite.NAME_COL.key(), "name");
		writeQRBill.put(QRBillWrite.QRBILL_COL.key(), "qrbill");
		writeQRBill.put(QRBillWrite.WHERE_COL.key(), "id_text");
		writeQRBill.put(QRBillWrite.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode creditor = source.putObject(QRBillMain.CREDITOR.key());
		creditor.put(QRBillCreditor.NAME.key(), "Christian Eugster");
		creditor.put(QRBillCreditor.ADDRESS.key(), "Axensteinstrasse 27");
		creditor.put(QRBillCreditor.CITY.key(), "9000 St. Gallen");
		creditor.put(QRBillCreditor.COUNTRY.key(), "CH");
		ObjectNode debtor = source.putObject(QRBillMain.DEBTOR.key());
		debtor.put(QRBillDebtor.NUMBER.key(), "K123456");
		debtor.put(QRBillDebtor.NAME.key(), "Christian Eugster");
		debtor.put(QRBillDebtor.ADDRESS.key(), "Axensteinstrasse 27");
		debtor.put(QRBillDebtor.CITY.key(), "9000 St. Gallen");
		debtor.put(QRBillDebtor.COUNTRY.key(), "CH");
		ObjectNode form = source.putObject(QRBillMain.FORM.key());
		form.put(QRBillForm.GRAPHICS_FORMAT.key(), GraphicsFormat.PDF.name());
		form.put(QRBillForm.OUTPUT_SIZE.key(), OutputSize.QR_BILL_EXTRA_SPACE.name());
		form.put(QRBillForm.LANGUAGE.key(), Language.DE.name());
		String result = new Fsl().execute("CreateQRBill", source.toString());
		JsonNode target = mapper.readTree(result);
		assertEquals(new BigDecimal(350).doubleValue(),
				target.get(QRBillMain.AMOUNT.key()).decimalValue().doubleValue());
		assertEquals("CHF", target.get(QRBillMain.CURRENCY.key()).asText());
		assertEquals("CH4431999123000889012", target.get(QRBillMain.IBAN.key()).asText());
		assertEquals("000000000012345600001234567", target.get(QRBillMain.REFERENCE.key()).asText());
		assertEquals("R123456", target.get(QRBillMain.INVOICE.key()).asText());
		assertEquals("Abonnement für 2020", target.get(QRBillMain.MESSAGE.key()).asText());
		assertEquals(ObjectNode.class, target.get(QRBillMain.DATABASE.key()).getClass());
		db = ObjectNode.class.cast(target.get(QRBillMain.DATABASE.key()));
		assertEquals("jdbc:filemaker://localhost/Invalid", db.get(QRBillDatabase.URL.key()).asText());
		assertEquals("christian", db.get(QRBillDatabase.USERNAME.key()).asText());
		assertEquals("ce_eu97", db.get(QRBillDatabase.PASSWORD.key()).asText());
		assertEquals(ObjectNode.class, db.get(QRBillDatabase.WRITE_QRBILL.key()).getClass());
		writeQRBill = ObjectNode.class.cast(db.get(QRBillDatabase.WRITE_QRBILL.key()));
		assertEquals("QRBill", writeQRBill.get(QRBillWrite.TABLE.key()).asText());
		assertEquals("name", writeQRBill.get(QRBillWrite.NAME_COL.key()).asText());
		assertEquals("qrbill", writeQRBill.get(QRBillWrite.QRBILL_COL.key()).asText());
		assertEquals("id_text", writeQRBill.get(QRBillWrite.WHERE_COL.key()).asText());
		assertEquals("7019891C-7AA9-4831-B0DC-EB69F5012BDC", writeQRBill.get(QRBillWrite.WHERE_VAL.key()).asText());
		assertNull(db.get(QRBillDatabase.READ_INVOICE.key()));
		assertEquals(ObjectNode.class, target.get(QRBillMain.CREDITOR.key()).getClass());
		creditor = ObjectNode.class.cast(target.get(QRBillMain.CREDITOR.key()));
		assertEquals("Christian Eugster", creditor.get(QRBillCreditor.NAME.key()).asText());
		assertEquals("Axensteinstrasse 27", creditor.get(QRBillCreditor.ADDRESS.key()).asText());
		assertEquals("9000 St. Gallen", creditor.get(QRBillCreditor.CITY.key()).asText());
		assertEquals("CH", creditor.get(QRBillCreditor.COUNTRY.key()).asText());
		assertEquals(ObjectNode.class, target.get(QRBillMain.DEBTOR.key()).getClass());
		debtor = ObjectNode.class.cast(target.get(QRBillMain.DEBTOR.key()));
		assertEquals("K123456", debtor.get(QRBillDebtor.NUMBER.key()).asText());
		assertEquals("Christian Eugster", debtor.get(QRBillDebtor.NAME.key()).asText());
		assertEquals("Axensteinstrasse 27", debtor.get(QRBillDebtor.ADDRESS.key()).asText());
		assertEquals("9000 St. Gallen", debtor.get(QRBillDebtor.CITY.key()).asText());
		assertEquals("CH", debtor.get(QRBillDebtor.COUNTRY.key()).asText());
		assertEquals(ObjectNode.class, target.get(QRBillMain.FORM.key()).getClass());
		form = ObjectNode.class.cast(target.get(QRBillMain.FORM.key()));
		assertEquals(GraphicsFormat.PDF.name(), form.get(QRBillForm.GRAPHICS_FORMAT.key()).asText());
		assertEquals(OutputSize.QR_BILL_EXTRA_SPACE.name(), form.get(QRBillForm.OUTPUT_SIZE.key()).asText());
		assertEquals(Language.DE.name(), form.get(QRBillForm.LANGUAGE.key()).asText());
		assertEquals("Fehler", target.get("result").asText());
		assertEquals(1, target.get("errors").size());
		assertEquals("[FileMaker][FileMaker JDBC]  (802): Unable to open file", target.get("errors").get(0).asText());
	}

	@Test
	public void testFslInvalidPassword() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode source = mapper.createObjectNode();
		source.put(QRBillMain.AMOUNT.key(), new BigDecimal(350));
		source.put(QRBillMain.CURRENCY.key(), "CHF");
		source.put(QRBillMain.IBAN.key(), "CH4431999123000889012");
		source.put(QRBillMain.REFERENCE.key(), "1234560000123456");
		source.put(QRBillMain.INVOICE.key(), "R123456");
		source.put(QRBillMain.MESSAGE.key(), "Abonnement für 2020");
		ObjectNode db = source.putObject(QRBillMain.DATABASE.key());
		db.put(QRBillDatabase.URL.key(), "jdbc:filemaker://localhost/Test");
		db.put(QRBillDatabase.USERNAME.key(), "christian");
		db.put(QRBillDatabase.PASSWORD.key(), "wrongPassword");
		ObjectNode writeQRBill = db.putObject(QRBillDatabase.WRITE_QRBILL.key());
		writeQRBill.put(QRBillWrite.TABLE.key(), "QRBill");
		writeQRBill.put(QRBillWrite.NAME_COL.key(), "name");
		writeQRBill.put(QRBillWrite.QRBILL_COL.key(), "qrbill");
		writeQRBill.put(QRBillWrite.WHERE_COL.key(), "id_text");
		writeQRBill.put(QRBillWrite.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode creditor = source.putObject(QRBillMain.CREDITOR.key());
		creditor.put(QRBillCreditor.NAME.key(), "Christian Eugster");
		creditor.put(QRBillCreditor.ADDRESS.key(), "Axensteinstrasse 27");
		creditor.put(QRBillCreditor.CITY.key(), "9000 St. Gallen");
		creditor.put(QRBillCreditor.COUNTRY.key(), "CH");
		ObjectNode debtor = source.putObject(QRBillMain.DEBTOR.key());
		debtor.put(QRBillDebtor.NUMBER.key(), "K123456");
		debtor.put(QRBillDebtor.NAME.key(), "Christian Eugster");
		debtor.put(QRBillDebtor.ADDRESS.key(), "Axensteinstrasse 27");
		debtor.put(QRBillDebtor.CITY.key(), "9000 St. Gallen");
		debtor.put(QRBillDebtor.COUNTRY.key(), "CH");
		ObjectNode form = source.putObject(QRBillMain.FORM.key());
		form.put(QRBillForm.GRAPHICS_FORMAT.key(), GraphicsFormat.PDF.name());
		form.put(QRBillForm.OUTPUT_SIZE.key(), OutputSize.QR_BILL_EXTRA_SPACE.name());
		form.put(QRBillForm.LANGUAGE.key(), Language.DE.name());
		String result = new Fsl().execute("CreateQRBill", source.toString());
		JsonNode target = mapper.readTree(result);
		assertEquals(new BigDecimal(350).doubleValue(),
				target.get(QRBillMain.AMOUNT.key()).decimalValue().doubleValue());
		assertEquals("CHF", target.get(QRBillMain.CURRENCY.key()).asText());
		assertEquals("CH4431999123000889012", target.get(QRBillMain.IBAN.key()).asText());
		assertEquals("000000000012345600001234567", target.get(QRBillMain.REFERENCE.key()).asText());
		assertEquals("R123456", target.get(QRBillMain.INVOICE.key()).asText());
		assertEquals("Abonnement für 2020", target.get(QRBillMain.MESSAGE.key()).asText());
		assertEquals(ObjectNode.class, target.get(QRBillMain.DATABASE.key()).getClass());
		db = ObjectNode.class.cast(target.get(QRBillMain.DATABASE.key()));
		assertEquals("jdbc:filemaker://localhost/Test", db.get(QRBillDatabase.URL.key()).asText());
		assertEquals("christian", db.get(QRBillDatabase.USERNAME.key()).asText());
		assertEquals("wrongPassword", db.get(QRBillDatabase.PASSWORD.key()).asText());
		assertEquals(ObjectNode.class, db.get(QRBillDatabase.WRITE_QRBILL.key()).getClass());
		writeQRBill = ObjectNode.class.cast(db.get(QRBillDatabase.WRITE_QRBILL.key()));
		assertEquals("QRBill", writeQRBill.get(QRBillWrite.TABLE.key()).asText());
		assertEquals("name", writeQRBill.get(QRBillWrite.NAME_COL.key()).asText());
		assertEquals("qrbill", writeQRBill.get(QRBillWrite.QRBILL_COL.key()).asText());
		assertEquals("id_text", writeQRBill.get(QRBillWrite.WHERE_COL.key()).asText());
		assertEquals("7019891C-7AA9-4831-B0DC-EB69F5012BDC", writeQRBill.get(QRBillWrite.WHERE_VAL.key()).asText());
		assertNull(db.get(QRBillDatabase.READ_INVOICE.key()));
		assertEquals(ObjectNode.class, target.get(QRBillMain.CREDITOR.key()).getClass());
		creditor = ObjectNode.class.cast(target.get(QRBillMain.CREDITOR.key()));
		assertEquals("Christian Eugster", creditor.get(QRBillCreditor.NAME.key()).asText());
		assertEquals("Axensteinstrasse 27", creditor.get(QRBillCreditor.ADDRESS.key()).asText());
		assertEquals("9000 St. Gallen", creditor.get(QRBillCreditor.CITY.key()).asText());
		assertEquals("CH", creditor.get(QRBillCreditor.COUNTRY.key()).asText());
		assertEquals(ObjectNode.class, target.get(QRBillMain.DEBTOR.key()).getClass());
		debtor = ObjectNode.class.cast(target.get(QRBillMain.DEBTOR.key()));
		assertEquals("K123456", debtor.get(QRBillDebtor.NUMBER.key()).asText());
		assertEquals("Christian Eugster", debtor.get(QRBillDebtor.NAME.key()).asText());
		assertEquals("Axensteinstrasse 27", debtor.get(QRBillDebtor.ADDRESS.key()).asText());
		assertEquals("9000 St. Gallen", debtor.get(QRBillDebtor.CITY.key()).asText());
		assertEquals("CH", debtor.get(QRBillDebtor.COUNTRY.key()).asText());
		assertEquals(ObjectNode.class, target.get(QRBillMain.FORM.key()).getClass());
		form = ObjectNode.class.cast(target.get(QRBillMain.FORM.key()));
		assertEquals(GraphicsFormat.PDF.name(), form.get(QRBillForm.GRAPHICS_FORMAT.key()).asText());
		assertEquals(OutputSize.QR_BILL_EXTRA_SPACE.name(), form.get(QRBillForm.OUTPUT_SIZE.key()).asText());
		assertEquals(Language.DE.name(), form.get(QRBillForm.LANGUAGE.key()).asText());
		assertEquals("Fehler", target.get("result").asText());
		assertEquals(1, target.get("errors").size());
		assertEquals("[FileMaker][FileMaker JDBC]  (212): Invalid account/password",
				target.get("errors").get(0).asText());
	}

}
