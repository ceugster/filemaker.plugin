package ch.eugster.filemaker.fsl.plugin.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ch.eugster.filemaker.fsl.plugin.Executor;
import ch.eugster.filemaker.fsl.plugin.Fsl;
import ch.eugster.filemaker.fsl.plugin.swissqrbill.QRBill;
import net.codecrete.qrbill.generator.GraphicsFormat;
import net.codecrete.qrbill.generator.Language;
import net.codecrete.qrbill.generator.OutputSize;

public class QRBillTest
{
	private ObjectMapper mapper;

	@BeforeAll
	public static void beforeAll() throws IOException, URISyntaxException, InterruptedException
	{
//		if (Desktop.isDesktopSupported())
//		{
//			Desktop desktop = Desktop.getDesktop();
//			if (desktop.isSupported(Action.OPEN))
//			{
//				File file = new File("resources/fm/Test.fmp12");
//				desktop.open(file);
//			}
//			Thread.sleep(20000L);
//		}
	}

	@BeforeEach
	public void beforeEach() throws URISyntaxException, IOException
	{
		this.mapper = new ObjectMapper();
	}

	@AfterEach
	public void afterEach()
	{
		FileUtils.deleteQuietly(Paths.get(System.getProperty("user.home"), ".fsl", "parameters.json").toFile());
	}

	private void copyConfiguration(String sourcePath) throws IOException
	{
		File target = Paths.get(System.getProperty("user.home"), ".fsl", "parameters.json").toFile();
		if (target.exists())
		{
			target.delete();
		}
		File source = new File(sourcePath).getAbsoluteFile();
		FileUtils.copyFile(source, target);
	}

	@Test
	public void testInvalidCommand() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode parameters = this.mapper.createObjectNode();

		String result = Fsl.execute("InvalidCommand", parameters.toString());

		JsonNode target = this.mapper.readTree(result);
		assertEquals(Executor.ERROR, target.get(Executor.STATUS).asText());
		JsonNode node = target.get(Executor.ERRORS);
		assertEquals(ArrayNode.class, node.getClass());
		ArrayNode errors = ArrayNode.class.cast(node);
		assertEquals(1, errors.size());
		assertEquals("invalid command 'InvalidCommand'", errors.get(0).asText());
	}

	@Test
	public void testInvalidParameters() throws IOException
	{
		String result = Fsl.execute("QRBill.generate", null);

		JsonNode resultNode = mapper.readTree(result);
		assertEquals(Executor.ERROR, resultNode.get(Executor.STATUS).asText());
		assertEquals(ArrayNode.class, resultNode.get(Executor.ERRORS).getClass());
		assertEquals(1, resultNode.get(Executor.ERRORS).size());
		assertEquals("invalid argument 'null'", resultNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testMinimalParametersValid() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode parameters = this.mapper.createObjectNode();
		parameters.put(QRBill.Key.CURRENCY.key(), "CHF");
		parameters.put(QRBill.Key.IBAN.key(), "CH4431999123000889012");
		parameters.put(QRBill.Key.REFERENCE.key(), "00000000000000000000000000");
		ObjectNode creditor = parameters.putObject("creditor");
		creditor.put(QRBill.Key.NAME.key(), "Christian Eugster");
		creditor.put(QRBill.Key.ADDRESS_LINE_1.key(), "Axensteinstrasse 27");
		creditor.put(QRBill.Key.ADDRESS_LINE_2.key(), "9000 St. Gallen");
		creditor.put(QRBill.Key.COUNTRY.key(), "CH");

		String result = Fsl.execute("QRBill.generate", parameters.toString());

		JsonNode resultNode = mapper.readTree(result);
		assertEquals(Executor.OK, resultNode.get(Executor.STATUS).asText());
		assertNotNull(resultNode.get(Executor.RESULT));
		assertNull(resultNode.get(Executor.ERRORS));
	}

	@Test
	public void testMinimalParametersWithNonQRIban() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode parameters = this.mapper.createObjectNode();
		parameters.put(QRBill.Key.CURRENCY.key(), "CHF");
		parameters.put(QRBill.Key.IBAN.key(), "CH450023023099999999A");
		parameters.put(QRBill.Key.REFERENCE.key(), "RF49N73GBST73AKL38ZX");
		ObjectNode creditor = parameters.putObject("creditor");
		creditor.put(QRBill.Key.NAME.key(), "Christian Eugster");
		creditor.put(QRBill.Key.ADDRESS_LINE_1.key(), "Axensteinstrasse 7");
		creditor.put(QRBill.Key.ADDRESS_LINE_2.key(), "9000 St. Gallen");
		creditor.put(QRBill.Key.COUNTRY.key(), "CH");

		String result = Fsl.execute("QRBill.generate", parameters.toString());

		JsonNode resultNode = mapper.readTree(result);
		assertEquals(Executor.OK, resultNode.get(Executor.STATUS).asText());
		assertNotNull(resultNode.get(Executor.RESULT));
		assertNull(resultNode.get(Executor.ERRORS));
	}

	@Test
	public void testMissingAllMandatories() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode parameters = this.mapper.createObjectNode();

		String result = Fsl.execute("QRBill.generate", parameters.toString());

		JsonNode resultNode = mapper.readTree(result);
		assertEquals(Executor.ERROR, resultNode.get(Executor.STATUS).asText());
		assertEquals(ArrayNode.class, resultNode.get(Executor.ERRORS).getClass());
		assertEquals(7, resultNode.get(Executor.ERRORS).size());
		ArrayNode errors = ArrayNode.class.cast(resultNode.get(Executor.ERRORS));
		Iterator<JsonNode> node = errors.iterator();
		while (node.hasNext())
		{
			String errorMessage = node.next().asText();
			if (errorMessage.equals("field_is_mandatory: 'account'"))
				assertEquals(errorMessage, "field_is_mandatory: 'account'");
			else if (errorMessage.equals("field_is_mandatory: 'creditor.name'"))
				assertEquals(errorMessage, "field_is_mandatory: 'creditor.name'");
			else if (errorMessage.equals("field_is_mandatory: 'creditor.postalCode'"))
				assertEquals(errorMessage, "field_is_mandatory: 'creditor.postalCode'");
			else if (errorMessage.equals("field_is_mandatory: 'creditor.addressLine2'"))
				assertEquals(errorMessage, "field_is_mandatory: 'creditor.addressLine2'");
			else if (errorMessage.equals("field_is_mandatory: 'creditor.town'"))
				assertEquals(errorMessage, "field_is_mandatory: 'creditor.town'");
			else if (errorMessage.equals("field_is_mandatory: 'creditor.countryCode'"))
				assertEquals(errorMessage, "field_is_mandatory: 'creditor.countryCode'");
			else if (errorMessage.equals("field_is_mandatory: 'currency'"))
				assertEquals(errorMessage, "field_is_mandatory: 'currency'");
			else fail();
		}
	}

	@Test
	public void testWithForeignIban() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode parameters = this.mapper.createObjectNode();
		parameters.put(QRBill.Key.CURRENCY.key(), "CHF");
		parameters.put(QRBill.Key.IBAN.key(), "IT12V0827358981000302206625");
		parameters.put(QRBill.Key.REFERENCE.key(), "00000000000000000000000000");
		ObjectNode creditor = parameters.putObject("creditor");
		creditor.put(QRBill.Key.NAME.key(), "Christian Eugster");
		creditor.put(QRBill.Key.ADDRESS_LINE_1.key(), "Axensteinstrasse 27");
		creditor.put(QRBill.Key.ADDRESS_LINE_2.key(), "9000 St. Gallen");
		creditor.put(QRBill.Key.COUNTRY.key(), "CH");

		String result = Fsl.execute("QRBill.generate", parameters.toString());

		JsonNode resultNode = mapper.readTree(result);
		assertEquals(Executor.ERROR, resultNode.get(Executor.STATUS).asText());
		assertEquals(1, resultNode.get(Executor.ERRORS).size());
		assertEquals("account_is_ch_li_iban: 'account'", resultNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testWithNormalIban() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode parameters = this.mapper.createObjectNode();
		parameters.put(QRBill.Key.CURRENCY.key(), "CHF");
		parameters.put(QRBill.Key.IBAN.key(), "CH6309000000901197203");
		parameters.put(QRBill.Key.REFERENCE.key(), "00000000000000000000000000");
		ObjectNode creditor = parameters.putObject("creditor");
		creditor.put(QRBill.Key.NAME.key(), "Christian Eugster");
		creditor.put(QRBill.Key.ADDRESS_LINE_1.key(), "Axensteinstrasse 27");
		creditor.put(QRBill.Key.ADDRESS_LINE_2.key(), "9000 St. Gallen");
		creditor.put(QRBill.Key.COUNTRY.key(), "CH");

		String result = Fsl.execute("QRBill.generate", parameters.toString());

		JsonNode resultNode = mapper.readTree(result);
		assertEquals(Executor.ERROR, resultNode.get(Executor.STATUS).asText());
		assertEquals(1, resultNode.get(Executor.ERRORS).size());
		assertEquals("valid_iso11649_creditor_ref: 'reference'", resultNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testInvalidCurrency() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode parameters = this.mapper.createObjectNode();
		parameters.put(QRBill.Key.CURRENCY.key(), "USD");
		parameters.put(QRBill.Key.IBAN.key(), "CH4431999123000889012");
		parameters.put(QRBill.Key.REFERENCE.key(), "00000000000000000000000000");
		ObjectNode creditor = parameters.putObject("creditor");
		creditor.put(QRBill.Key.NAME.key(), "Christian Eugster");
		creditor.put(QRBill.Key.ADDRESS_LINE_1.key(), "Axensteinstrasse 27");
		creditor.put(QRBill.Key.ADDRESS_LINE_2.key(), "9000 St. Gallen");
		creditor.put(QRBill.Key.COUNTRY.key(), "CH");

		String result = Fsl.execute("QRBill.generate", parameters.toString());

		JsonNode resultNode = mapper.readTree(result);
		assertEquals(Executor.ERROR, resultNode.get(Executor.STATUS).asText());
		assertEquals(1, resultNode.get(Executor.ERRORS).size());
		assertEquals("currency_is_chf_or_eur: 'currency'", resultNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testInvalidReference() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode parameters = this.mapper.createObjectNode();
		parameters.put(QRBill.Key.CURRENCY.key(), "CHF");
		parameters.put(QRBill.Key.IBAN.key(), "CH4431999123000889012");
		parameters.put(QRBill.Key.REFERENCE.key(), "FS000000000000000000000000");
		ObjectNode creditor = parameters.putObject("creditor");
		creditor.put(QRBill.Key.NAME.key(), "Christian Eugster");
		creditor.put(QRBill.Key.ADDRESS_LINE_1.key(), "Axensteinstrasse 27");
		creditor.put(QRBill.Key.ADDRESS_LINE_2.key(), "9000 St. Gallen");
		creditor.put(QRBill.Key.COUNTRY.key(), "CH");

		String result = Fsl.execute("QRBill.generate", parameters.toString());

		JsonNode resultNode = mapper.readTree(result);
		assertEquals(Executor.ERROR, resultNode.get(Executor.STATUS).asText());
		assertEquals(1, resultNode.get(Executor.ERRORS).size());
		assertEquals("valid_qr_ref_no: 'reference'", resultNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testAllValid() throws IOException
	{
		this.copyConfiguration("resources/cfg/qrbill_all.json");

		ObjectNode parameters = mapper.createObjectNode();
		parameters.put("amount", new BigDecimal(350));
		parameters.put(QRBill.Key.CURRENCY.key(), "CHF");
		parameters.put(QRBill.Key.IBAN.key(), "CH4431999123000889012");
		parameters.put(QRBill.Key.REFERENCE.key(), "00000000000000000000000000");
		parameters.put("message", "Abonnement für 2020");
		ObjectNode creditor = parameters.putObject("creditor");
		creditor.put(QRBill.Key.NAME.key(), "Christian Eugster");
		creditor.put(QRBill.Key.ADDRESS_LINE_1.key(), "Axensteinstrasse 27");
		creditor.put(QRBill.Key.ADDRESS_LINE_2.key(), "9000 St. Gallen");
		creditor.put(QRBill.Key.COUNTRY.key(), "CH");
		ObjectNode debtor = parameters.putObject("debtor");
		debtor.put(QRBill.Key.NAME.key(), "Christian Eugster");
		debtor.put(QRBill.Key.ADDRESS_LINE_1.key(), "Axensteinstrasse 27");
		debtor.put(QRBill.Key.ADDRESS_LINE_2.key(), "9000 St. Gallen");
		debtor.put(QRBill.Key.COUNTRY.key(), "CH");
		ObjectNode form = parameters.putObject("format");
		form.put(QRBill.Key.GRAPHICS_FORMAT.key(), GraphicsFormat.PDF.name());
		form.put(QRBill.Key.OUTPUT_SIZE.key(), OutputSize.A4_PORTRAIT_SHEET.name());
		form.put(QRBill.Key.LANGUAGE.key(), Language.DE.name());

		String result = Fsl.execute("QRBill.generate", parameters.toString());
		JsonNode resultNode = mapper.readTree(result);
		assertEquals(Executor.OK, resultNode.get(Executor.STATUS).asText());
		assertNotNull(resultNode.get(Executor.RESULT));
		assertNull(resultNode.get(Executor.ERRORS));
	}

	@Test
	public void testInvalidGraphicsFormat() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode parameters = this.mapper.createObjectNode();
		parameters.put(QRBill.Key.CURRENCY.key(), "USD");
		parameters.put(QRBill.Key.IBAN.key(), "CH6309000000901197203");
		parameters.put(QRBill.Key.REFERENCE.key(), "00000000000000000000000000");
		ObjectNode creditor = parameters.putObject("creditor");
		creditor.put(QRBill.Key.NAME.key(), "Christian Eugster");
		creditor.put(QRBill.Key.ADDRESS_LINE_1.key(), "Axensteinstrasse 27");
		creditor.put(QRBill.Key.ADDRESS_LINE_2.key(), "9000 St. Gallen");
		creditor.put(QRBill.Key.COUNTRY.key(), "CH");
		ObjectNode form = parameters.putObject("format");
		form.put(QRBill.Key.GRAPHICS_FORMAT.key(), "blabla");
		form.put(QRBill.Key.LANGUAGE.key(), Language.IT.toString());
		form.put(QRBill.Key.OUTPUT_SIZE.key(), OutputSize.A4_PORTRAIT_SHEET.toString());

		String result = Fsl.execute("QRBill.generate", parameters.toString());

		JsonNode resultNode = mapper.readTree(result);
		assertEquals(Executor.ERROR, resultNode.get(Executor.STATUS).asText());
		assertEquals(1, resultNode.get(Executor.ERRORS).size());
		assertEquals(
				"invalid_json_format_parameter 'No enum constant net.codecrete.qrbill.generator.GraphicsFormat.blabla'",
				resultNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testInvalidLanguage() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode parameters = this.mapper.createObjectNode();
		parameters.put(QRBill.Key.CURRENCY.key(), "USD");
		parameters.put(QRBill.Key.IBAN.key(), "CH6309000000901197203");
		parameters.put(QRBill.Key.REFERENCE.key(), "00000000000000000000000000");
		ObjectNode creditor = parameters.putObject("creditor");
		creditor.put(QRBill.Key.NAME.key(), "Christian Eugster");
		creditor.put(QRBill.Key.ADDRESS_LINE_1.key(), "Axensteinstrasse 27");
		creditor.put(QRBill.Key.ADDRESS_LINE_2.key(), "9000 St. Gallen");
		creditor.put(QRBill.Key.COUNTRY.key(), "CH");
		ObjectNode form = parameters.putObject("format");
		form.put(QRBill.Key.GRAPHICS_FORMAT.key(), GraphicsFormat.PDF.toString());
		form.put(QRBill.Key.LANGUAGE.key(), "blabla");
		form.put(QRBill.Key.OUTPUT_SIZE.key(), OutputSize.A4_PORTRAIT_SHEET.toString());

		String result = Fsl.execute("QRBill.generate", parameters.toString());

		JsonNode resultNode = mapper.readTree(result);
		assertEquals(Executor.ERROR, resultNode.get(Executor.STATUS).asText());
		assertEquals(1, resultNode.get(Executor.ERRORS).size());
		assertEquals("invalid_json_format_parameter 'No enum constant net.codecrete.qrbill.generator.Language.blabla'",
				resultNode.get(Executor.ERRORS).get(0).asText());
	}

	@Test
	public void testInvalidOutputSize() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode parameters = this.mapper.createObjectNode();
		parameters.put(QRBill.Key.CURRENCY.key(), "USD");
		parameters.put(QRBill.Key.IBAN.key(), "CH6309000000901197203");
		parameters.put(QRBill.Key.REFERENCE.key(), "00000000000000000000000000");
		ObjectNode creditor = parameters.putObject("creditor");
		creditor.put(QRBill.Key.NAME.key(), "Christian Eugster");
		creditor.put(QRBill.Key.ADDRESS_LINE_1.key(), "Axensteinstrasse 27");
		creditor.put(QRBill.Key.ADDRESS_LINE_2.key(), "9000 St. Gallen");
		creditor.put(QRBill.Key.COUNTRY.key(), "CH");
		ObjectNode form = parameters.putObject("format");
		form.put(QRBill.Key.GRAPHICS_FORMAT.key(), GraphicsFormat.PDF.toString());
		form.put(QRBill.Key.LANGUAGE.key(), Language.IT.toString());
		form.put(QRBill.Key.OUTPUT_SIZE.key(), "blabla");

		String result = Fsl.execute("QRBill.generate", parameters.toString());

		JsonNode resultNode = mapper.readTree(result);
		assertEquals(Executor.ERROR, resultNode.get(Executor.STATUS).asText());
		assertEquals(1, resultNode.get(Executor.ERRORS).size());
		assertEquals(
				"invalid_json_format_parameter 'No enum constant net.codecrete.qrbill.generator.OutputSize.blabla'",
				resultNode.get(Executor.ERRORS).get(0).asText());
	}

}
