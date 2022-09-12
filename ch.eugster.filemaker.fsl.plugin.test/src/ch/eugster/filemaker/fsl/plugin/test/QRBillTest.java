package ch.eugster.filemaker.fsl.plugin.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.eugster.filemaker.fsl.plugin.Fsl;
import ch.eugster.filemaker.fsl.plugin.swissqrbill.Parameters;
import ch.eugster.filemaker.fsl.plugin.swissqrbill.QRBill.Parameter;
import net.codecrete.qrbill.generator.GraphicsFormat;
import net.codecrete.qrbill.generator.Language;
import net.codecrete.qrbill.generator.OutputSize;

public class QRBillTest
{
	private ObjectMapper mapper;

	@BeforeAll
	public static void beforeAll() throws IOException, URISyntaxException, InterruptedException
	{
		if (Desktop.isDesktopSupported())
		{
			Desktop desktop = Desktop.getDesktop();
			if (desktop.isSupported(Action.OPEN))
			{
				File file = new File("resources/fm/Test.fmp12");
				desktop.open(file);
			}
			Thread.sleep(20000L);
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
		FileUtils.deleteQuietly(Paths.get(System.getProperty("user.home"), ".fsl", "qrbill.json").toFile());
	}

	private void copyConfiguration(String sourcePath) throws IOException
	{
		File target = Paths.get(System.getProperty("user.home"), ".fsl", "qrbill.json").toFile();
		if (target.exists())
		{
			target.delete();
		}
		File source = new File(sourcePath).getAbsoluteFile();
		FileUtils.copyFile(source, target);
	}

	@Test
	public void testNullParameter() throws JsonMappingException, JsonProcessingException
	{
		String result = Fsl.execute("QRBill.generate", new Object[0]);

		JsonNode resultNode = this.mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals(ArrayNode.class, resultNode.get("errors").getClass());
		assertEquals(1, resultNode.get("errors").size());
		ArrayNode errors = ArrayNode.class.cast(resultNode.get("errors"));
		assertEquals("Falsche Anzahl Parameter", errors.get(0).asText());
//		Iterator<JsonNode> iterator = errors.iterator();
//		while (iterator.hasNext())
//		{
//			String error = iterator.next().asText();
//			if (error.equals("field_is_mandatory: 'account'"))
//			{
//				assertEquals("field_is_mandatory: 'account'", error);
//			}
//			else if (error.equals("field_is_mandatory: 'creditor.name'"))
//			{
//				assertEquals("field_is_mandatory: 'creditor.name'", error);
//			}
//			else if (error.equals("field_is_mandatory: 'creditor.postalCode'"))
//			{
//				assertEquals("field_is_mandatory: 'creditor.postalCode'", error);
//			}
//			else if (error.equals("field_is_mandatory: 'creditor.addressLine2'"))
//			{
//				assertEquals("field_is_mandatory: 'creditor.addressLine2'", error);
//			}
//			else if (error.equals("field_is_mandatory: 'creditor.town'"))
//			{
//				assertEquals("field_is_mandatory: 'creditor.town'", error);
//			}
//			else if (error.equals("field_is_mandatory: 'creditor.countryCode'"))
//			{
//				assertEquals("field_is_mandatory: 'creditor.countryCode'", error);
//			}
//			else
//			{
//				fail();
//			}
//		}
	}

	@Test
	public void testInvalidCommand() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode parameters = this.mapper.createObjectNode();

		String result = Fsl.execute("InvalidCommand", parameters.toString());

		JsonNode target = this.mapper.readTree(result);
		assertEquals("Fehler", target.get("result").asText());
		JsonNode node = target.get("errors");
		assertEquals(ArrayNode.class, node.getClass());
		ArrayNode errors = ArrayNode.class.cast(node);
		assertEquals(1, errors.size());
		assertEquals("Der Befehl ist ungültig.", errors.get(0).asText());
	}

	@Test
	public void testInvalidProperties() throws IOException
	{
		this.copyConfiguration("resources/cfg/qrbill_invalid.json");

		ObjectNode parameters = this.mapper.createObjectNode();

		String result = Fsl.execute("QRBill.generate", parameters.toString());

		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals(ArrayNode.class, resultNode.get("errors").getClass());
		assertEquals(1, resultNode.get("errors").size());
		assertEquals("Die Konfigurationsdatei 'qrbill.json' ist fehlerhaft.", resultNode.get("errors").get(0).asText());
	}

	@Test
	public void testInvalidContent() throws IOException
	{
		this.copyConfiguration("resources/cfg/qrbill_with_unknown_property.json");

		ObjectNode parameters = this.mapper.createObjectNode();

		String result = Fsl.execute("QRBill.generate", parameters.toString());

		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals(ArrayNode.class, resultNode.get("errors").getClass());
		assertEquals(1, resultNode.get("errors").size());
		ArrayNode errors = ArrayNode.class.cast(resultNode.get("errors"));
		assertEquals("Die Konfigurationsdatei 'qrbill.json' enthält ungültige Elemente.", errors.get(0).asText());
	}

	@Test
	public void testWithoutConfiguration() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode parameters = this.mapper.createObjectNode();

		String result = Fsl.execute("QRBill.generate", parameters.toString());

		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals(ArrayNode.class, resultNode.get("errors").getClass());
		assertEquals(6, resultNode.get("errors").size());
		ArrayNode errors = ArrayNode.class.cast(resultNode.get("errors"));
		Iterator<JsonNode> iterator = errors.iterator();
		while (iterator.hasNext())
		{
			String error = iterator.next().asText();
			if (error.equals("field_is_mandatory: 'account'"))
			{
				assertEquals("field_is_mandatory: 'account'", error);
			}
			else if (error.equals("field_is_mandatory: 'creditor.name'"))
			{
				assertEquals("field_is_mandatory: 'creditor.name'", error);
			}
			else if (error.equals("field_is_mandatory: 'creditor.postalCode'"))
			{
				assertEquals("field_is_mandatory: 'creditor.postalCode'", error);
			}
			else if (error.equals("field_is_mandatory: 'creditor.addressLine2'"))
			{
				assertEquals("field_is_mandatory: 'creditor.addressLine2'", error);
			}
			else if (error.equals("field_is_mandatory: 'creditor.town'"))
			{
				assertEquals("field_is_mandatory: 'creditor.town'", error);
			}
			else if (error.equals("field_is_mandatory: 'creditor.countryCode'"))
			{
				assertEquals("field_is_mandatory: 'creditor.countryCode'", error);
			}
			else
			{
				fail();
			}
		}
	}

	@Test
	public void testWithoutConfigurationButMinimalParameters() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode parameters = this.mapper.createObjectNode();
		parameters.put(Parameter.IBAN.key(), "CH4431999123000889012");
		ObjectNode creditor = parameters.putObject(Parameter.CREDITOR.key());
		creditor.put(Parameter.NAME.key(), "Christian Eugster");
		creditor.put(Parameter.ADDRESS.key(), "Axensteinstrasse 27");
		creditor.put(Parameter.CITY.key(), "9000 St. Gallen");
		creditor.put(Parameter.COUNTRY.key(), "CH");

		String result = Fsl.execute("QRBill.generate", parameters.toString());

		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals(ArrayNode.class, resultNode.get("errors").getClass());
		assertEquals(1, resultNode.get("errors").size());
		ArrayNode errors = ArrayNode.class.cast(resultNode.get("errors"));
		assertEquals("mandatory_for_qr_iban: 'reference'", errors.get(0).asText());
	}

	@Test
	public void testWithoutConfigurationButMinimalParametersPlusReference()
			throws JsonMappingException, JsonProcessingException
	{
		ObjectNode parameters = this.mapper.createObjectNode();
		parameters.put(Parameter.IBAN.key(), "CH4431999123000889012");
		parameters.put(Parameter.REFERENCE.key(), "00000000000000000000000000");
		ObjectNode creditor = parameters.putObject(Parameter.CREDITOR.key());
		creditor.put(Parameter.NAME.key(), "Christian Eugster");
		creditor.put(Parameter.ADDRESS.key(), "Axensteinstrasse 27");
		creditor.put(Parameter.CITY.key(), "9000 St. Gallen");
		creditor.put(Parameter.COUNTRY.key(), "CH");

		String result = Fsl.execute("QRBill.generate", parameters.toString());

		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals(ArrayNode.class, resultNode.get("errors").getClass());
		assertEquals(1, resultNode.get("errors").size());
		ArrayNode errors = ArrayNode.class.cast(resultNode.get("errors"));
		assertEquals("Die Tabellendaten für das Zielobjekt sind fehlerhaft.", errors.get(0).asText());
	}

	@Test
	public void testWithoutConfigurationButMinimalParametersPlusReferenceWithPath()
			throws JsonMappingException, JsonProcessingException
	{
		ObjectNode parameters = this.mapper.createObjectNode();
		parameters.put(Parameter.IBAN.key(), "CH4431999123000889012");
		parameters.put(Parameter.REFERENCE.key(), "00000000000000000000000000");
		ObjectNode target = parameters.putObject(Parameter.TARGET.key());
		Path targetPath = Paths.get(System.getProperty("user.home"), "Rechnung.pdf");
		target.put(Parameter.PATH.key(), targetPath.toFile().getAbsolutePath());
		ObjectNode creditor = parameters.putObject(Parameter.CREDITOR.key());
		creditor.put(Parameter.NAME.key(), "Christian Eugster");
		creditor.put(Parameter.ADDRESS.key(), "Axensteinstrasse 27");
		creditor.put(Parameter.CITY.key(), "9000 St. Gallen");
		creditor.put(Parameter.COUNTRY.key(), "CH");

		String result = Fsl.execute("QRBill.generate", parameters.toString());

		JsonNode resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
		assertNull(resultNode.get("errors"));
		assertNotNull(resultNode.get("target"));
		assertEquals(targetPath.toFile().getAbsolutePath(), resultNode.get("target").asText());
	}

	@Test
	public void testConfigurationWithTargetDatabaseCreditorIban() throws IOException
	{
		this.copyConfiguration("resources/cfg/qrbill_with_target_database_creditor_iban.json");

		ObjectNode parameters = mapper.createObjectNode();
		parameters.put(Parameter.REFERENCE.key(), "00000000000000000000000000");
		ObjectNode target = parameters.putObject(Parameter.TARGET.key());
		target.put(Parameter.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");

		String result = Fsl.execute("QRBill.generate", parameters.toString());

		JsonNode resultNode = this.mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
		assertNull(resultNode.get("errors"));
	}

	@Test
	public void testConfigurationWithTargetSourceDatabaseCreditorIban() throws IOException
	{
		this.copyConfiguration("resources/cfg/qrbill_with_target_database_creditor_iban.json");

		ObjectNode parameters = mapper.createObjectNode();
		parameters.put(Parameter.REFERENCE.key(), "00000000000000000000000000");
		ObjectNode target = parameters.putObject(Parameter.TARGET.key());
		target.put(Parameter.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode source = parameters.putObject(Parameter.SOURCE.key());
		source.put(Parameter.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");

		String result = Fsl.execute("QRBill.generate", parameters.toString());

		JsonNode resultNode = this.mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals(ArrayNode.class, resultNode.get("errors").getClass());
		assertEquals(1, resultNode.get("errors").size());
		ArrayNode errors = ArrayNode.class.cast(resultNode.get("errors"));
		assertEquals("Ein Quellobjekt wird erwartet, ist aber nicht verfügbar.", errors.get(0).asText());
	}

	@Test
	public void testMergeAll() throws IOException
	{
		this.copyConfiguration("resources/cfg/qrbill_all.json");

		ObjectNode parameters = mapper.createObjectNode();
		parameters.put(Parameter.AMOUNT.key(), new BigDecimal(350));
		parameters.put(Parameter.CURRENCY.key(), "CHF");
		parameters.put(Parameter.IBAN.key(), "CH4431999123000889012");
		parameters.put(Parameter.REFERENCE.key(), "00000000000000000000000000");
		parameters.put(Parameter.MESSAGE.key(), "Abonnement für 2020");
		ObjectNode db = parameters.putObject(Parameter.DATABASE.key());
		db.put(Parameter.URL.key(), "jdbc:filemaker://localhost/Test");
		db.put(Parameter.USERNAME.key(), "christian");
		db.put(Parameter.PASSWORD.key(), "ce_eu97");
		ObjectNode target = parameters.putObject(Parameter.TARGET.key());
		target.put(Parameter.TABLE.key(), "QRBill");
		target.put(Parameter.NAME_COL.key(), "name");
		target.put(Parameter.CONTAINER_COL.key(), "qrbill");
		target.put(Parameter.NAME_COL.key(), "name");
		target.put(Parameter.WHERE_COL.key(), "id_text");
		target.put(Parameter.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode source = parameters.putObject(Parameter.SOURCE.key());
		source.put(Parameter.TABLE.key(), "QRBill");
		source.put(Parameter.CONTAINER_COL.key(), "invoice");
		source.put(Parameter.WHERE_COL.key(), "id_text");
		source.put(Parameter.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode creditor = parameters.putObject(Parameter.CREDITOR.key());
		creditor.put(Parameter.NAME.key(), "Christian Eugster");
		creditor.put(Parameter.ADDRESS.key(), "Axensteinstrasse 27");
		creditor.put(Parameter.CITY.key(), "9000 St. Gallen");
		creditor.put(Parameter.COUNTRY.key(), "CH");
		ObjectNode debtor = parameters.putObject(Parameter.DEBTOR.key());
		debtor.put(Parameter.NAME.key(), "Christian Eugster");
		debtor.put(Parameter.ADDRESS.key(), "Axensteinstrasse 27");
		debtor.put(Parameter.CITY.key(), "9000 St. Gallen");
		debtor.put(Parameter.COUNTRY.key(), "CH");
		ObjectNode form = parameters.putObject(Parameter.FORM.key());
		form.put(Parameter.GRAPHICS_FORMAT.key(), GraphicsFormat.PDF.name());
		form.put(Parameter.OUTPUT_SIZE.key(), OutputSize.A4_PORTRAIT_SHEET.name());
		form.put(Parameter.LANGUAGE.key(), Language.DE.name());

		String result = Fsl.execute("QRBill.generate", parameters.toString());
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
		assertNull(resultNode.get("errors"));
	}

	@Test
	public void testMergeList() throws IOException
	{
		this.copyConfiguration("resources/cfg/qrbill_all.json");

		ObjectNode base = mapper.createObjectNode();
		base.put(Parameter.AMOUNT.key(), new BigDecimal(350));
		base.put(Parameter.CURRENCY.key(), "CHF");
		base.put(Parameter.IBAN.key(), "CH4431999123000889012");
		base.put(Parameter.REFERENCE.key(), "00000000000000000000000000");
		base.put(Parameter.MESSAGE.key(), "Abonnement für 2020");
		ObjectNode db = mapper.createObjectNode();
		db.put(Parameter.URL.key(), "jdbc:filemaker://localhost/Test");
		db.put(Parameter.USERNAME.key(), "christian");
		db.put(Parameter.PASSWORD.key(), "ce_eu97");
		ObjectNode target = mapper.createObjectNode();
		target.put(Parameter.TABLE.key(), "QRBill");
		target.put(Parameter.NAME_COL.key(), "name");
		target.put(Parameter.CONTAINER_COL.key(), "qrbill");
		target.put(Parameter.NAME_COL.key(), "name");
		target.put(Parameter.WHERE_COL.key(), "id_text");
		target.put(Parameter.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode source = mapper.createObjectNode();
		source.put(Parameter.TABLE.key(), "QRBill");
		source.put(Parameter.CONTAINER_COL.key(), "invoice");
		source.put(Parameter.WHERE_COL.key(), "id_text");
		source.put(Parameter.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode creditor = mapper.createObjectNode();
		creditor.put(Parameter.NAME.key(), "Christian Eugster");
		creditor.put(Parameter.ADDRESS.key(), "Axensteinstrasse 27");
		creditor.put(Parameter.CITY.key(), "9000 St. Gallen");
		creditor.put(Parameter.COUNTRY.key(), "CH");
		ObjectNode debtor = mapper.createObjectNode();
		debtor.put(Parameter.NAME.key(), "Christian Eugster");
		debtor.put(Parameter.ADDRESS.key(), "Axensteinstrasse 27");
		debtor.put(Parameter.CITY.key(), "9000 St. Gallen");
		debtor.put(Parameter.COUNTRY.key(), "CH");
		ObjectNode form = mapper.createObjectNode();
		form.put(Parameter.GRAPHICS_FORMAT.key(), GraphicsFormat.PDF.name());
		form.put(Parameter.OUTPUT_SIZE.key(), OutputSize.A4_PORTRAIT_SHEET.name());
		form.put(Parameter.LANGUAGE.key(), Language.DE.name());

		String result = Fsl.execute("QRBill.generate", base.toString(), db.toString(), source.toString(),
				target.toString(), creditor.toString(), debtor.toString(), form.toString());
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
		assertNull(resultNode.get("errors"));
	}

	@Test
	public void testIbanWithoutReference() throws IOException
	{
		this.copyConfiguration("resources/cfg/qrbill_all.json");

		ObjectNode parameters = mapper.createObjectNode();
		parameters.put(Parameter.AMOUNT.key(), new BigDecimal(350));
		parameters.put(Parameter.CURRENCY.key(), "CHF");
		parameters.put(Parameter.IBAN.key(), "CH4431999123000889012");
		parameters.put(Parameter.REFERENCE.key(), "00000000000000000000000000");
		parameters.put(Parameter.MESSAGE.key(), "Abonnement für 2020");
		ObjectNode db = parameters.putObject(Parameter.DATABASE.key());
		db.put(Parameter.URL.key(), "jdbc:filemaker://localhost/Test");
		db.put(Parameter.USERNAME.key(), "christian");
		db.put(Parameter.PASSWORD.key(), "ce_eu97");
		ObjectNode target = parameters.putObject(Parameter.TARGET.key());
		target.put(Parameter.TABLE.key(), "QRBill");
		target.put(Parameter.NAME_COL.key(), "name");
		target.put(Parameter.CONTAINER_COL.key(), "qrbill");
		target.put(Parameter.NAME_COL.key(), "name");
		target.put(Parameter.WHERE_COL.key(), "id_text");
		target.put(Parameter.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode source = parameters.putObject(Parameter.SOURCE.key());
		source.put(Parameter.TABLE.key(), "QRBill");
		source.put(Parameter.CONTAINER_COL.key(), "invoice");
		source.put(Parameter.WHERE_COL.key(), "id_text");
		source.put(Parameter.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode creditor = parameters.putObject(Parameter.CREDITOR.key());
		creditor.put(Parameter.NAME.key(), "Christian Eugster");
		creditor.put(Parameter.ADDRESS.key(), "Axensteinstrasse 27");
		creditor.put(Parameter.CITY.key(), "9000 St. Gallen");
		creditor.put(Parameter.COUNTRY.key(), "CH");
		ObjectNode debtor = parameters.putObject(Parameter.DEBTOR.key());
		debtor.put(Parameter.NAME.key(), "Christian Eugster");
		debtor.put(Parameter.ADDRESS.key(), "Axensteinstrasse 27");
		debtor.put(Parameter.CITY.key(), "9000 St. Gallen");
		debtor.put(Parameter.COUNTRY.key(), "CH");
		ObjectNode form = parameters.putObject(Parameter.FORM.key());
		form.put(Parameter.GRAPHICS_FORMAT.key(), GraphicsFormat.PDF.name());
		form.put(Parameter.OUTPUT_SIZE.key(), OutputSize.A4_PORTRAIT_SHEET.name());
		form.put(Parameter.LANGUAGE.key(), Language.DE.name());

		String result = Fsl.execute("QRBill.generate", parameters.toString());
		JsonNode resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
		assertNull(resultNode.get("errors"));
	}

	@Test
	public void testMinimalParametersWithPathsOK() throws IOException
	{
		this.copyConfiguration("resources/cfg/qrbill_with_paths_iban_creditor.json");

		ObjectNode parameters = mapper.createObjectNode();
		parameters.put(Parameter.AMOUNT.key(), new BigDecimal(350));
		parameters.put(Parameter.REFERENCE.key(), "00000000000000000000000000");
		parameters.put(Parameter.MESSAGE.key(), "Abonnement für 2020");
		ObjectNode debtor = parameters.putObject(Parameter.DEBTOR.key());
		debtor.put(Parameter.NAME.key(), "Christian Eugster");
		debtor.put(Parameter.ADDRESS.key(), "Axensteinstrasse 27");
		debtor.put(Parameter.CITY.key(), "9000 St. Gallen");
		debtor.put(Parameter.COUNTRY.key(), "CH");

		String result = Fsl.execute("QRBill.generate", parameters.toString());

		JsonNode resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
		assertNull(resultNode.get("errors"));
		assertNotNull(resultNode.get("target"));
		Parameters params = mapper.readValue(new File("resources/cfg/qrbill_with_paths_iban_creditor.json"),
				Parameters.class);
		assertEquals(params.getTarget().getPath(), resultNode.get("target").asText());
	}

	@Test
	public void testMinimalParametersWithDatabaseOK() throws IOException
	{
		this.copyConfiguration("resources/cfg/qrbill_with_db_iban_creditor.json");

		ObjectNode parameters = mapper.createObjectNode();
		parameters.put(Parameter.AMOUNT.key(), new BigDecimal(350));
		parameters.put(Parameter.REFERENCE.key(), "00000000000000000000000000");
		parameters.put(Parameter.MESSAGE.key(), "Abonnement für 2020");
		ObjectNode source = parameters.putObject(Parameter.SOURCE.key());
		source.put(Parameter.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode target = parameters.putObject(Parameter.TARGET.key());
		target.put(Parameter.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode debtor = parameters.putObject(Parameter.DEBTOR.key());
		debtor.put(Parameter.NAME.key(), "Christian Eugster");
		debtor.put(Parameter.ADDRESS.key(), "Axensteinstrasse 27");
		debtor.put(Parameter.CITY.key(), "9000 St. Gallen");
		debtor.put(Parameter.COUNTRY.key(), "CH");

		String result = Fsl.execute("QRBill.generate", parameters.toString());

		JsonNode resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
		assertNull(resultNode.get("errors"));
	}

	@Test
	public void testMissingSourceObjectDb() throws IOException
	{
		this.copyConfiguration("resources/cfg/qrbill_with_db_iban_creditor.json");

		ObjectNode parameters = mapper.createObjectNode();
		parameters.put(Parameter.AMOUNT.key(), new BigDecimal(350));
		parameters.put(Parameter.REFERENCE.key(), "00000000000000000000000000");
		parameters.put(Parameter.MESSAGE.key(), "Abonnement für 2020");
		ObjectNode source = parameters.putObject(Parameter.SOURCE.key());
		source.put(Parameter.CONTAINER_COL.key(), "qrcode");
		source.put(Parameter.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode target = parameters.putObject(Parameter.TARGET.key());
		target.put(Parameter.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode debtor = parameters.putObject(Parameter.DEBTOR.key());
		debtor.put(Parameter.NAME.key(), "Christian Eugster");
		debtor.put(Parameter.ADDRESS.key(), "Axensteinstrasse 27");
		debtor.put(Parameter.CITY.key(), "9000 St. Gallen");
		debtor.put(Parameter.COUNTRY.key(), "CH");

		String result = Fsl.execute("QRBill.generate", parameters.toString());

		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals(1, resultNode.get("errors").size());
		assertEquals("Ein Quellobjekt wird erwartet, ist aber nicht verfügbar.",
				resultNode.get("errors").get(0).asText());
	}

	@Test
	public void testMissingSourceObjectPath() throws IOException
	{
		this.copyConfiguration("resources/cfg/qrbill_with_db_iban_creditor.json");

		ObjectNode parameters = mapper.createObjectNode();
		parameters.put(Parameter.AMOUNT.key(), new BigDecimal(350));
		parameters.put(Parameter.REFERENCE.key(), "00000000000000000000000000");
		parameters.put(Parameter.MESSAGE.key(), "Abonnement für 2020");
		ObjectNode source = parameters.putObject(Parameter.SOURCE.key());
		source.put(Parameter.PATH.key(), "/Users/christian/missing.pdf");
		ObjectNode target = parameters.putObject(Parameter.TARGET.key());
		target.put(Parameter.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode debtor = parameters.putObject(Parameter.DEBTOR.key());
		debtor.put(Parameter.NAME.key(), "Christian Eugster");
		debtor.put(Parameter.ADDRESS.key(), "Axensteinstrasse 27");
		debtor.put(Parameter.CITY.key(), "9000 St. Gallen");
		debtor.put(Parameter.COUNTRY.key(), "CH");

		String result = Fsl.execute("QRBill.generate", parameters.toString());

		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals(1, resultNode.get("errors").size());
		assertEquals("Ein Quellobjekt wird erwartet, ist aber nicht verfügbar.",
				resultNode.get("errors").get(0).asText());
	}

	@Test
	public void testMinimalParametersWithDatabasePathOK() throws IOException
	{
		ObjectNode parameters = mapper.createObjectNode();
		parameters.put(Parameter.IBAN.key(), "CH4431999123000889012");
		parameters.put(Parameter.AMOUNT.key(), new BigDecimal(350));
		parameters.put(Parameter.REFERENCE.key(), "00000000000000000000000000");
		parameters.put(Parameter.MESSAGE.key(), "Abonnement für 2020");
		ObjectNode db = parameters.putObject(Parameter.DATABASE.key());
		db.put(Parameter.URL.key(), "jdbc:filemaker://localhost/Test");
		db.put(Parameter.USERNAME.key(), "christian");
		db.put(Parameter.PASSWORD.key(), "ce_eu97");
		ObjectNode source = parameters.putObject(Parameter.SOURCE.key());
		source.put(Parameter.TABLE.key(), "QRBill");
		source.put(Parameter.CONTAINER_COL.key(), "invoice");
		source.put(Parameter.WHERE_COL.key(), "id_text");
		source.put(Parameter.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode target = parameters.putObject(Parameter.TARGET.key());
		Path path = Paths.get(System.getProperty("user.home"), "Rechnung.pdf");
		target.put(Parameter.PATH.key(), path.toFile().getAbsolutePath());
		ObjectNode creditor = parameters.putObject(Parameter.CREDITOR.key());
		creditor.put(Parameter.NAME.key(), "Christian Eugster");
		creditor.put(Parameter.ADDRESS.key(), "Axensteinstrasse 27");
		creditor.put(Parameter.CITY.key(), "9000 St. Gallen");
		creditor.put(Parameter.COUNTRY.key(), "CH");
		ObjectNode debtor = parameters.putObject(Parameter.DEBTOR.key());
		debtor.put(Parameter.NAME.key(), "Christian Eugster");
		debtor.put(Parameter.ADDRESS.key(), "Axensteinstrasse 27");
		debtor.put(Parameter.CITY.key(), "9000 St. Gallen");
		debtor.put(Parameter.COUNTRY.key(), "CH");

		String result = Fsl.execute("QRBill.generate", parameters.toString());

		JsonNode resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
		assertNull(resultNode.get("errors"));
		assertNotNull(resultNode.get("target"));
		Parameters params = mapper.readValue(new File("resources/cfg/qrbill_with_paths_iban_creditor.json"),
				Parameters.class);
		assertEquals(params.getTarget().getPath(), resultNode.get("target").asText());
	}

	@Test
	public void testMinimalParametersWithPathDatabaseOK() throws IOException
	{
		ObjectNode parameters = mapper.createObjectNode();
		parameters.put(Parameter.IBAN.key(), "CH4431999123000889012");
		parameters.put(Parameter.AMOUNT.key(), new BigDecimal(350));
		parameters.put(Parameter.REFERENCE.key(), "00000000000000000000000000");
		parameters.put(Parameter.MESSAGE.key(), "Abonnement für 2020");
		ObjectNode db = parameters.putObject(Parameter.DATABASE.key());
		db.put(Parameter.URL.key(), "jdbc:filemaker://localhost/Test");
		db.put(Parameter.USERNAME.key(), "christian");
		db.put(Parameter.PASSWORD.key(), "ce_eu97");
		ObjectNode source = parameters.putObject(Parameter.SOURCE.key());
		source.put(Parameter.PATH.key(), "resources/invoice.pdf");
		ObjectNode target = parameters.putObject(Parameter.TARGET.key());
		target.put(Parameter.TABLE.key(), "QRBill");
		target.put(Parameter.CONTAINER_COL.key(), "qrbill");
		target.put(Parameter.NAME_COL.key(), "name");
		target.put(Parameter.WHERE_COL.key(), "id_text");
		target.put(Parameter.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode creditor = parameters.putObject(Parameter.CREDITOR.key());
		creditor.put(Parameter.NAME.key(), "Christian Eugster");
		creditor.put(Parameter.ADDRESS.key(), "Axensteinstrasse 27");
		creditor.put(Parameter.CITY.key(), "9000 St. Gallen");
		creditor.put(Parameter.COUNTRY.key(), "CH");
		ObjectNode debtor = parameters.putObject(Parameter.DEBTOR.key());
		debtor.put(Parameter.NAME.key(), "Christian Eugster");
		debtor.put(Parameter.ADDRESS.key(), "Axensteinstrasse 27");
		debtor.put(Parameter.CITY.key(), "9000 St. Gallen");
		debtor.put(Parameter.COUNTRY.key(), "CH");

		String result = Fsl.execute("QRBill.generate", parameters.toString());

		JsonNode resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
		assertNull(resultNode.get("errors"));
	}

	@Test
	public void testWrongConnectionInfo() throws JsonMappingException, JsonProcessingException
	{
		ObjectNode parameters = mapper.createObjectNode();
		parameters.put(Parameter.IBAN.key(), "CH4431999123000889012");
		parameters.put(Parameter.AMOUNT.key(), new BigDecimal(350));
		parameters.put(Parameter.REFERENCE.key(), "00000000000000000000000000");
		parameters.put(Parameter.MESSAGE.key(), "Abonnement für 2020");
		ObjectNode db = parameters.putObject(Parameter.DATABASE.key());
		db.put(Parameter.URL.key(), "jdbc:filemaker://localhost/Wrong");
		db.put(Parameter.USERNAME.key(), "christiano");
		db.put(Parameter.PASSWORD.key(), "ce_eu98");
		ObjectNode target = parameters.putObject(Parameter.TARGET.key());
		target.put(Parameter.TABLE.key(), "QRBill");
		target.put(Parameter.CONTAINER_COL.key(), "qrbill");
		target.put(Parameter.NAME_COL.key(), "name");
		target.put(Parameter.WHERE_COL.key(), "id_text");
		target.put(Parameter.WHERE_VAL.key(), "7019891C-7AA9-4831-B0DC-EB69F5012BDC");
		ObjectNode creditor = parameters.putObject(Parameter.CREDITOR.key());
		creditor.put(Parameter.NAME.key(), "Christian Eugster");
		creditor.put(Parameter.ADDRESS.key(), "Axensteinstrasse 27");
		creditor.put(Parameter.CITY.key(), "9000 St. Gallen");
		creditor.put(Parameter.COUNTRY.key(), "CH");
		ObjectNode debtor = parameters.putObject(Parameter.DEBTOR.key());
		debtor.put(Parameter.NAME.key(), "Christian Eugster");
		debtor.put(Parameter.ADDRESS.key(), "Axensteinstrasse 27");
		debtor.put(Parameter.CITY.key(), "9000 St. Gallen");
		debtor.put(Parameter.COUNTRY.key(), "CH");

		String result = Fsl.execute("QRBill.generate", parameters.toString());

		JsonNode resultNode = this.mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertEquals(ArrayNode.class, resultNode.get("errors").getClass());
		assertEquals(1, resultNode.get("errors").size());
		ArrayNode errors = ArrayNode.class.cast(resultNode.get("errors"));
		assertEquals("Die Zugangsdaten zur Datenbank sind fehlerhaft.", errors.get(0).asText());
	}
}
