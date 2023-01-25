package ch.eugster.filemaker.fsl.plugin.test;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ch.eugster.filemaker.fsl.plugin.Fsl;
import ch.eugster.filemaker.fsl.plugin.converter.Converter.Parameter;

public class ConversionTest
{
	private ObjectMapper mapper;

	private String sourcePath = "resources/xml/camt.054_P_CH0809000000450010065_1111204750_0_2022121623562233.xml";

	private String filepath = "resources/xml/200924_camt.054_P_CH2909000000250094239_1110092703_0_2019042423412214.xml";

	private String targetFilename = "resources/json/camt.054_P_CH0809000000450010065_1111204750_0_2022121623562233.json";

	private String targetContent;

	@BeforeEach
	public void beforeEach() throws IOException
	{
		this.mapper = new ObjectMapper();
		File targetFile = new File(targetFilename);
		targetContent = FileUtils.readFileToString(targetFile, Charset.defaultCharset());
	}

	@AfterEach
	public void afterEach()
	{

	}

	@Test
	public void testWithoutParameter() throws SQLException, IOException
	{
		String result = Fsl.execute("Converter.convertXmlToJson", new Object[0]);

		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertNull(resultNode.get(Parameter.TARGET_JSON.key()));
		assertEquals(ArrayNode.class, resultNode.get("errors").getClass());
		ArrayNode errors = ArrayNode.class.cast(resultNode.get("errors"));
		assertEquals(1, errors.size());
		JsonNode error = errors.elements().next();
		assertEquals("Parameter fehlt", error.asText());
	}

	@Test
	public void testWithInvalidParameter() throws SQLException, IOException
	{
		String result = Fsl.execute("Converter.convertXmlToJson", new Object[] { 0 });

		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertNull(resultNode.get(Parameter.TARGET_JSON.key()));
		assertEquals(ArrayNode.class, resultNode.get("errors").getClass());
		ArrayNode errors = ArrayNode.class.cast(resultNode.get("errors"));
		assertEquals(1, errors.size());
		JsonNode error = errors.elements().next();
		assertEquals("Ungültiger Parameter (muss vom Typ String sein)", error.asText());
	}

	@Test
	public void testParameterAsJsonContainerWithInvalidXmlData() throws SQLException, IOException
	{
		Path path = Paths.get(filepath);
		List<String> lines = Files.readAllLines(path);
		StringBuilder sb = new StringBuilder();
		for (String line : lines)
		{
			sb = sb.append(line);
		}
		String xml = "kaba" + sb.toString();

		ObjectNode parameters = mapper.createObjectNode();
		parameters.put(Parameter.SOURCE_XML.key(), xml);

		String result = Fsl.execute("Converter.convertXmlToJson", parameters.toString());

		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertNull(resultNode.get(Parameter.TARGET_JSON.key()));
		assertEquals(1, resultNode.get("errors").size());
		assertEquals("Ungültiger Parameter", resultNode.get("errors").get(0).asText());
	}

	@Test
	public void testParameterAsJsonContainerWithXmlData() throws SQLException, IOException
	{
		Path path = Paths.get(filepath);
		List<String> lines = Files.readAllLines(path);
		StringBuilder sb = new StringBuilder();
		for (String line : lines)
		{
			sb = sb.append(line);
		}
		String xml = sb.toString();

		ObjectNode parameters = mapper.createObjectNode();
		parameters.put(Parameter.SOURCE_XML.key(), xml);

		String result = Fsl.execute("Converter.convertXmlToJson", parameters.toString());

		JsonNode resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
		JsonNode json = resultNode.get(Parameter.TARGET_JSON.key());
		assertNotNull(json);
		assertEquals(json.asText(), resultNode.get(Parameter.TARGET_JSON.key()).asText());
		assertEquals(
				"{\"schemaLocation\":\"urn:iso:std:iso:20022:tech:xsd:camt.054.001.04 camt.054.001.04.xsd\",\"BkToCstmrDbtCdtNtfctn\":{\"GrpHdr\":{\"MsgId\":\"2019042475204228731867\",\"CreDtTm\":\"2020-06-17T23:33:09\",\"MsgPgntn\":{\"PgNb\":\"1\",\"LastPgInd\":\"true\"},\"AddtlInf\":\"SPS/1.6/PROD\"},\"Ntfctn\":{\"Id\":\"20180316375204228731887\",\"CreDtTm\":\"2020-06-17T23:33:09\",\"FrToDt\":{\"FrDtTm\":\"2020-06-17T00:00:00\",\"ToDtTm\":\"2020-06-17T23:59:59\"},\"Acct\":{\"Id\":{\"IBAN\":\"CH2909000000250094239\"},\"Ownr\":{\"Nm\":\"Robert Schneider SA Grands magasins Biel/Bienne\"}},\"Ntry\":{\"Amt\":{\"Ccy\":\"CHF\",\"\":\"1500.00\"},\"CdtDbtInd\":\"CRDT\",\"RvslInd\":\"false\",\"Sts\":\"BOOK\",\"BookgDt\":{\"Dt\":\"2020-06-17\"},\"ValDt\":{\"Dt\":\"2020-06-17\"},\"AcctSvcrRef\":\"0000000000000001\",\"BkTxCd\":{\"Domn\":{\"Cd\":\"PMNT\",\"Fmly\":{\"Cd\":\"RCDT\",\"SubFmlyCd\":\"AUTT\"}}},\"NtryDtls\":{\"Btch\":{\"NbOfTxs\":\"6\"},\"TxDtls\":[{\"Refs\":{\"MsgId\":\"MsgId-001\",\"AcctSvcrRef\":\"0000000000000001\",\"PmtInfId\":\"PmtInfId-001-03\",\"InstrId\":\"InstrId-001-03-02\",\"EndToEndId\":\"EndToEndId-001-03-01\",\"Prtry\":{\"Tp\":\"00\",\"Ref\":\"00000000000000000000001\"}},\"Amt\":{\"Ccy\":\"CHF\",\"\":\"400.00\"},\"CdtDbtInd\":\"CRDT\",\"BkTxCd\":{\"Domn\":{\"Cd\":\"PMNT\",\"Fmly\":{\"Cd\":\"RCDT\",\"SubFmlyCd\":\"AUTT\"}}},\"RltdPties\":{\"Dbtr\":{\"Nm\":\"Bernasconi Maria\",\"PstlAdr\":{\"StrtNm\":\"Place de la Gare\",\"BldgNb\":\"12\",\"PstCd\":\"2502\",\"TwnNm\":\"Biel/Bienne\",\"Ctry\":\"CH\"}},\"DbtrAcct\":{\"Id\":{\"IBAN\":\"CH5109000000250092291\"}},\"CdtrAcct\":{\"Id\":{\"IBAN\":\"CH2909000000250094239\"}}},\"RltdAgts\":{\"DbtrAgt\":{\"FinInstnId\":{\"BICFI\":\"POFICHBEXXX\",\"Nm\":\"POSTFINANCE AG\",\"PstlAdr\":{\"AdrLine\":[\"MINGERSTRASSE 20\",\"3030 BERNE\"]}}}},\"RmtInf\":{\"Ustrd\":\"Kontouebertrag\",\"Strd\":{\"AddtlRmtInf\":[\"?REJECT?0\",\"?ERROR?000\"]}},\"RltdDts\":{\"AccptncDtTm\":\"2020-06-17T20:00:00\"}},{\"Refs\":{\"MsgId\":\"MsgId-001\",\"AcctSvcrRef\":\"0000000000000002\",\"PmtInfId\":\"PmtInfId-001-03\",\"InstrId\":\"InstrId-001-03-01\",\"EndToEndId\":\"EndToEndId-001-03-02\",\"Prtry\":{\"Tp\":\"00\",\"Ref\":\"00000000000000000000002\"}},\"Amt\":{\"Ccy\":\"CHF\",\"\":\"350.00\"},\"CdtDbtInd\":\"CRDT\",\"BkTxCd\":{\"Domn\":{\"Cd\":\"PMNT\",\"Fmly\":{\"Cd\":\"RCDT\",\"SubFmlyCd\":\"AUTT\"}}},\"RltdPties\":{\"Dbtr\":{\"Nm\":\"Bernasconi Maria\",\"PstlAdr\":{\"StrtNm\":\"Place de la Gare\",\"BldgNb\":\"12\",\"PstCd\":\"2502\",\"TwnNm\":\"Biel/Bienne\",\"Ctry\":\"CH\"}},\"DbtrAcct\":{\"Id\":{\"IBAN\":\"CH5109000000250092291\"}},\"CdtrAcct\":{\"Id\":{\"IBAN\":\"CH2909000000250094239\"}}},\"RltdAgts\":{\"DbtrAgt\":{\"FinInstnId\":{\"BICFI\":\"POFICHBEXXX\",\"Nm\":\"POSTFINANCE AG\",\"PstlAdr\":{\"AdrLine\":[\"MINGERSTRASSE 20\",\"3030 BERNE\"]}}}},\"RmtInf\":{\"Ustrd\":\"Kontouebertrag\",\"Strd\":{\"AddtlRmtInf\":[\"?REJECT?0\",\"?ERROR?000\"]}},\"RltdDts\":{\"AccptncDtTm\":\"2020-06-17T20:00:00\"}},{\"Refs\":{\"MsgId\":\"MsgId-001\",\"AcctSvcrRef\":\"0000000000000003\",\"PmtInfId\":\"PmtInfId-001-03\",\"InstrId\":\"InstrId-001-03-03\",\"EndToEndId\":\"EndToEndId-001-03-03\",\"Prtry\":{\"Tp\":\"00\",\"Ref\":\"00000000000000000000003\"}},\"Amt\":{\"Ccy\":\"CHF\",\"\":\"450.00\"},\"CdtDbtInd\":\"CRDT\",\"BkTxCd\":{\"Domn\":{\"Cd\":\"PMNT\",\"Fmly\":{\"Cd\":\"RCDT\",\"SubFmlyCd\":\"AUTT\"}}},\"RltdPties\":{\"Dbtr\":{\"Nm\":\"Bernasconi Maria\",\"PstlAdr\":{\"StrtNm\":\"Place de la Gare\",\"BldgNb\":\"12\",\"PstCd\":\"2502\",\"TwnNm\":\"Biel/Bienne\",\"Ctry\":\"CH\"}},\"DbtrAcct\":{\"Id\":{\"IBAN\":\"CH5109000000250092291\"}},\"CdtrAcct\":{\"Id\":{\"IBAN\":\"CH2909000000250094239\"}}},\"RltdAgts\":{\"DbtrAgt\":{\"FinInstnId\":{\"BICFI\":\"POFICHBEXXX\",\"Nm\":\"POSTFINANCE AG\",\"PstlAdr\":{\"AdrLine\":[\"MINGERSTRASSE 20\",\"3030 BERNE\"]}}}},\"RmtInf\":{\"Ustrd\":\"Kontouebertrag\",\"Strd\":{\"AddtlRmtInf\":[\"?REJECT?0\",\"?ERROR?000\"]}},\"RltdDts\":{\"AccptncDtTm\":\"2020-06-17T20:00:00\"}},{\"Refs\":{\"MsgId\":\"MsgId-001\",\"AcctSvcrRef\":\"0000000000000004\",\"PmtInfId\":\"PmtInfId-001-01\",\"InstrId\":\"InstrId-001-01-03\",\"EndToEndId\":\"EndToEndId-001-03-04\",\"Prtry\":{\"Tp\":\"00\",\"Ref\":\"00000000000000000000004\"}},\"Amt\":{\"Ccy\":\"CHF\",\"\":\"150.00\"},\"CdtDbtInd\":\"CRDT\",\"BkTxCd\":{\"Domn\":{\"Cd\":\"PMNT\",\"Fmly\":{\"Cd\":\"RCDT\",\"SubFmlyCd\":\"AUTT\"}}},\"RltdPties\":{\"Dbtr\":{\"Nm\":\"Bernasconi Maria\",\"PstlAdr\":{\"StrtNm\":\"Place de la Gare\",\"BldgNb\":\"12\",\"PstCd\":\"2502\",\"TwnNm\":\"Biel/Bienne\",\"Ctry\":\"CH\"}},\"DbtrAcct\":{\"Id\":{\"IBAN\":\"CH5109000000250092291\"}},\"CdtrAcct\":{\"Id\":{\"IBAN\":\"CH2909000000250094239\"}}},\"RltdAgts\":{\"DbtrAgt\":{\"FinInstnId\":{\"BICFI\":\"POFICHBEXXX\",\"Nm\":\"POSTFINANCE AG\",\"PstlAdr\":{\"AdrLine\":[\"MINGERSTRASSE 20\",\"3030 BERNE\"]}}}},\"RmtInf\":{\"Ustrd\":\"Rg.-Nr. 001\",\"Strd\":{\"AddtlRmtInf\":[\"?REJECT?0\",\"?ERROR?000\"]}},\"RltdDts\":{\"AccptncDtTm\":\"2020-06-17T20:00:00\"}},{\"Refs\":{\"MsgId\":\"MsgId-001\",\"AcctSvcrRef\":\"0000000000000005\",\"PmtInfId\":\"PmtInfId-001-01\",\"InstrId\":\"InstrId-001-01-01\",\"EndToEndId\":\"EndToEndId-001-03-05\",\"Prtry\":{\"Tp\":\"00\",\"Ref\":\"00000000000000000000005\"}},\"Amt\":{\"Ccy\":\"CHF\",\"\":\"50.00\"},\"CdtDbtInd\":\"CRDT\",\"BkTxCd\":{\"Domn\":{\"Cd\":\"PMNT\",\"Fmly\":{\"Cd\":\"RCDT\",\"SubFmlyCd\":\"AUTT\"}}},\"RltdPties\":{\"Dbtr\":{\"Nm\":\"Bernasconi Maria\",\"PstlAdr\":{\"StrtNm\":\"Place de la Gare\",\"BldgNb\":\"12\",\"PstCd\":\"2502\",\"TwnNm\":\"Biel/Bienne\",\"Ctry\":\"CH\"}},\"DbtrAcct\":{\"Id\":{\"IBAN\":\"CH5109000000250092291\"}},\"CdtrAcct\":{\"Id\":{\"IBAN\":\"CH2909000000250094239\"}}},\"RltdAgts\":{\"DbtrAgt\":{\"FinInstnId\":{\"BICFI\":\"POFICHBEXXX\",\"Nm\":\"POSTFINANCE AG\",\"PstlAdr\":{\"AdrLine\":[\"MINGERSTRASSE 20\",\"3030 BERNE\"]}}}},\"RmtInf\":{\"Ustrd\":\"Rg.-Nr. 1234567890\",\"Strd\":{\"AddtlRmtInf\":[\"?REJECT?0\",\"?ERROR?000\"]}},\"RltdDts\":{\"AccptncDtTm\":\"2020-06-17T20:00:00\"}},{\"Refs\":{\"MsgId\":\"MsgId-001\",\"AcctSvcrRef\":\"0000000000000006\",\"PmtInfId\":\"PmtInfId-001-01\",\"InstrId\":\"InstrId-001-01-02\",\"EndToEndId\":\"EndToEndId-001-03-06\",\"Prtry\":{\"Tp\":\"00\",\"Ref\":\"00000000000000000000006\"}},\"Amt\":{\"Ccy\":\"CHF\",\"\":\"100.00\"},\"CdtDbtInd\":\"CRDT\",\"BkTxCd\":{\"Domn\":{\"Cd\":\"PMNT\",\"Fmly\":{\"Cd\":\"RCDT\",\"SubFmlyCd\":\"AUTT\"}}},\"RltdPties\":{\"Dbtr\":{\"Nm\":\"Bernasconi Maria\",\"PstlAdr\":{\"StrtNm\":\"Place de la Gare\",\"BldgNb\":\"12\",\"PstCd\":\"2502\",\"TwnNm\":\"Biel/Bienne\",\"Ctry\":\"CH\"}},\"DbtrAcct\":{\"Id\":{\"IBAN\":\"CH5109000000250092291\"}},\"CdtrAcct\":{\"Id\":{\"IBAN\":\"CH2909000000250094239\"}}},\"RltdAgts\":{\"DbtrAgt\":{\"FinInstnId\":{\"BICFI\":\"POFICHBEXXX\",\"Nm\":\"POSTFINANCE AG\",\"PstlAdr\":{\"AdrLine\":[\"MINGERSTRASSE 20\",\"3030 BERNE\"]}}}},\"RmtInf\":{\"Ustrd\":\"Rg.-Nr. 987654321\",\"Strd\":{\"AddtlRmtInf\":[\"?REJECT?0\",\"?ERROR?000\"]}},\"RltdDts\":{\"AccptncDtTm\":\"2020-06-17T20:00:00\"}}]},\"AddtlNtryInf\":\"SAMMELGUTSCHRIFT FÜR KONTO: CH2909000000250094239 VERARBEITUNG VOM 17.06.2020 PAKET ID: 9999999999999999\"}}}}",
				resultNode.get(Parameter.TARGET_JSON.key()).asText());
		assertNull(resultNode.get("errors"));
	}

	@Test
	public void testParameterAsXmlData() throws SQLException, IOException
	{
		Path path = Paths.get(filepath);
		List<String> lines = Files.readAllLines(path);
		StringBuilder sb = new StringBuilder();
		for (String line : lines)
		{
			sb = sb.append(line);
		}
		String xml = sb.toString();

		String result = Fsl.execute("Converter.convertXmlToJson", new Object[] { xml });

		JsonNode resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
		JsonNode json = resultNode.get(Parameter.TARGET_JSON.key());
		assertNotNull(json);
		assertEquals(json.asText(), resultNode.get(Parameter.TARGET_JSON.key()).asText());
		assertEquals(
				"{\"schemaLocation\":\"urn:iso:std:iso:20022:tech:xsd:camt.054.001.04 camt.054.001.04.xsd\",\"BkToCstmrDbtCdtNtfctn\":{\"GrpHdr\":{\"MsgId\":\"2019042475204228731867\",\"CreDtTm\":\"2020-06-17T23:33:09\",\"MsgPgntn\":{\"PgNb\":\"1\",\"LastPgInd\":\"true\"},\"AddtlInf\":\"SPS/1.6/PROD\"},\"Ntfctn\":{\"Id\":\"20180316375204228731887\",\"CreDtTm\":\"2020-06-17T23:33:09\",\"FrToDt\":{\"FrDtTm\":\"2020-06-17T00:00:00\",\"ToDtTm\":\"2020-06-17T23:59:59\"},\"Acct\":{\"Id\":{\"IBAN\":\"CH2909000000250094239\"},\"Ownr\":{\"Nm\":\"Robert Schneider SA Grands magasins Biel/Bienne\"}},\"Ntry\":{\"Amt\":{\"Ccy\":\"CHF\",\"\":\"1500.00\"},\"CdtDbtInd\":\"CRDT\",\"RvslInd\":\"false\",\"Sts\":\"BOOK\",\"BookgDt\":{\"Dt\":\"2020-06-17\"},\"ValDt\":{\"Dt\":\"2020-06-17\"},\"AcctSvcrRef\":\"0000000000000001\",\"BkTxCd\":{\"Domn\":{\"Cd\":\"PMNT\",\"Fmly\":{\"Cd\":\"RCDT\",\"SubFmlyCd\":\"AUTT\"}}},\"NtryDtls\":{\"Btch\":{\"NbOfTxs\":\"6\"},\"TxDtls\":[{\"Refs\":{\"MsgId\":\"MsgId-001\",\"AcctSvcrRef\":\"0000000000000001\",\"PmtInfId\":\"PmtInfId-001-03\",\"InstrId\":\"InstrId-001-03-02\",\"EndToEndId\":\"EndToEndId-001-03-01\",\"Prtry\":{\"Tp\":\"00\",\"Ref\":\"00000000000000000000001\"}},\"Amt\":{\"Ccy\":\"CHF\",\"\":\"400.00\"},\"CdtDbtInd\":\"CRDT\",\"BkTxCd\":{\"Domn\":{\"Cd\":\"PMNT\",\"Fmly\":{\"Cd\":\"RCDT\",\"SubFmlyCd\":\"AUTT\"}}},\"RltdPties\":{\"Dbtr\":{\"Nm\":\"Bernasconi Maria\",\"PstlAdr\":{\"StrtNm\":\"Place de la Gare\",\"BldgNb\":\"12\",\"PstCd\":\"2502\",\"TwnNm\":\"Biel/Bienne\",\"Ctry\":\"CH\"}},\"DbtrAcct\":{\"Id\":{\"IBAN\":\"CH5109000000250092291\"}},\"CdtrAcct\":{\"Id\":{\"IBAN\":\"CH2909000000250094239\"}}},\"RltdAgts\":{\"DbtrAgt\":{\"FinInstnId\":{\"BICFI\":\"POFICHBEXXX\",\"Nm\":\"POSTFINANCE AG\",\"PstlAdr\":{\"AdrLine\":[\"MINGERSTRASSE 20\",\"3030 BERNE\"]}}}},\"RmtInf\":{\"Ustrd\":\"Kontouebertrag\",\"Strd\":{\"AddtlRmtInf\":[\"?REJECT?0\",\"?ERROR?000\"]}},\"RltdDts\":{\"AccptncDtTm\":\"2020-06-17T20:00:00\"}},{\"Refs\":{\"MsgId\":\"MsgId-001\",\"AcctSvcrRef\":\"0000000000000002\",\"PmtInfId\":\"PmtInfId-001-03\",\"InstrId\":\"InstrId-001-03-01\",\"EndToEndId\":\"EndToEndId-001-03-02\",\"Prtry\":{\"Tp\":\"00\",\"Ref\":\"00000000000000000000002\"}},\"Amt\":{\"Ccy\":\"CHF\",\"\":\"350.00\"},\"CdtDbtInd\":\"CRDT\",\"BkTxCd\":{\"Domn\":{\"Cd\":\"PMNT\",\"Fmly\":{\"Cd\":\"RCDT\",\"SubFmlyCd\":\"AUTT\"}}},\"RltdPties\":{\"Dbtr\":{\"Nm\":\"Bernasconi Maria\",\"PstlAdr\":{\"StrtNm\":\"Place de la Gare\",\"BldgNb\":\"12\",\"PstCd\":\"2502\",\"TwnNm\":\"Biel/Bienne\",\"Ctry\":\"CH\"}},\"DbtrAcct\":{\"Id\":{\"IBAN\":\"CH5109000000250092291\"}},\"CdtrAcct\":{\"Id\":{\"IBAN\":\"CH2909000000250094239\"}}},\"RltdAgts\":{\"DbtrAgt\":{\"FinInstnId\":{\"BICFI\":\"POFICHBEXXX\",\"Nm\":\"POSTFINANCE AG\",\"PstlAdr\":{\"AdrLine\":[\"MINGERSTRASSE 20\",\"3030 BERNE\"]}}}},\"RmtInf\":{\"Ustrd\":\"Kontouebertrag\",\"Strd\":{\"AddtlRmtInf\":[\"?REJECT?0\",\"?ERROR?000\"]}},\"RltdDts\":{\"AccptncDtTm\":\"2020-06-17T20:00:00\"}},{\"Refs\":{\"MsgId\":\"MsgId-001\",\"AcctSvcrRef\":\"0000000000000003\",\"PmtInfId\":\"PmtInfId-001-03\",\"InstrId\":\"InstrId-001-03-03\",\"EndToEndId\":\"EndToEndId-001-03-03\",\"Prtry\":{\"Tp\":\"00\",\"Ref\":\"00000000000000000000003\"}},\"Amt\":{\"Ccy\":\"CHF\",\"\":\"450.00\"},\"CdtDbtInd\":\"CRDT\",\"BkTxCd\":{\"Domn\":{\"Cd\":\"PMNT\",\"Fmly\":{\"Cd\":\"RCDT\",\"SubFmlyCd\":\"AUTT\"}}},\"RltdPties\":{\"Dbtr\":{\"Nm\":\"Bernasconi Maria\",\"PstlAdr\":{\"StrtNm\":\"Place de la Gare\",\"BldgNb\":\"12\",\"PstCd\":\"2502\",\"TwnNm\":\"Biel/Bienne\",\"Ctry\":\"CH\"}},\"DbtrAcct\":{\"Id\":{\"IBAN\":\"CH5109000000250092291\"}},\"CdtrAcct\":{\"Id\":{\"IBAN\":\"CH2909000000250094239\"}}},\"RltdAgts\":{\"DbtrAgt\":{\"FinInstnId\":{\"BICFI\":\"POFICHBEXXX\",\"Nm\":\"POSTFINANCE AG\",\"PstlAdr\":{\"AdrLine\":[\"MINGERSTRASSE 20\",\"3030 BERNE\"]}}}},\"RmtInf\":{\"Ustrd\":\"Kontouebertrag\",\"Strd\":{\"AddtlRmtInf\":[\"?REJECT?0\",\"?ERROR?000\"]}},\"RltdDts\":{\"AccptncDtTm\":\"2020-06-17T20:00:00\"}},{\"Refs\":{\"MsgId\":\"MsgId-001\",\"AcctSvcrRef\":\"0000000000000004\",\"PmtInfId\":\"PmtInfId-001-01\",\"InstrId\":\"InstrId-001-01-03\",\"EndToEndId\":\"EndToEndId-001-03-04\",\"Prtry\":{\"Tp\":\"00\",\"Ref\":\"00000000000000000000004\"}},\"Amt\":{\"Ccy\":\"CHF\",\"\":\"150.00\"},\"CdtDbtInd\":\"CRDT\",\"BkTxCd\":{\"Domn\":{\"Cd\":\"PMNT\",\"Fmly\":{\"Cd\":\"RCDT\",\"SubFmlyCd\":\"AUTT\"}}},\"RltdPties\":{\"Dbtr\":{\"Nm\":\"Bernasconi Maria\",\"PstlAdr\":{\"StrtNm\":\"Place de la Gare\",\"BldgNb\":\"12\",\"PstCd\":\"2502\",\"TwnNm\":\"Biel/Bienne\",\"Ctry\":\"CH\"}},\"DbtrAcct\":{\"Id\":{\"IBAN\":\"CH5109000000250092291\"}},\"CdtrAcct\":{\"Id\":{\"IBAN\":\"CH2909000000250094239\"}}},\"RltdAgts\":{\"DbtrAgt\":{\"FinInstnId\":{\"BICFI\":\"POFICHBEXXX\",\"Nm\":\"POSTFINANCE AG\",\"PstlAdr\":{\"AdrLine\":[\"MINGERSTRASSE 20\",\"3030 BERNE\"]}}}},\"RmtInf\":{\"Ustrd\":\"Rg.-Nr. 001\",\"Strd\":{\"AddtlRmtInf\":[\"?REJECT?0\",\"?ERROR?000\"]}},\"RltdDts\":{\"AccptncDtTm\":\"2020-06-17T20:00:00\"}},{\"Refs\":{\"MsgId\":\"MsgId-001\",\"AcctSvcrRef\":\"0000000000000005\",\"PmtInfId\":\"PmtInfId-001-01\",\"InstrId\":\"InstrId-001-01-01\",\"EndToEndId\":\"EndToEndId-001-03-05\",\"Prtry\":{\"Tp\":\"00\",\"Ref\":\"00000000000000000000005\"}},\"Amt\":{\"Ccy\":\"CHF\",\"\":\"50.00\"},\"CdtDbtInd\":\"CRDT\",\"BkTxCd\":{\"Domn\":{\"Cd\":\"PMNT\",\"Fmly\":{\"Cd\":\"RCDT\",\"SubFmlyCd\":\"AUTT\"}}},\"RltdPties\":{\"Dbtr\":{\"Nm\":\"Bernasconi Maria\",\"PstlAdr\":{\"StrtNm\":\"Place de la Gare\",\"BldgNb\":\"12\",\"PstCd\":\"2502\",\"TwnNm\":\"Biel/Bienne\",\"Ctry\":\"CH\"}},\"DbtrAcct\":{\"Id\":{\"IBAN\":\"CH5109000000250092291\"}},\"CdtrAcct\":{\"Id\":{\"IBAN\":\"CH2909000000250094239\"}}},\"RltdAgts\":{\"DbtrAgt\":{\"FinInstnId\":{\"BICFI\":\"POFICHBEXXX\",\"Nm\":\"POSTFINANCE AG\",\"PstlAdr\":{\"AdrLine\":[\"MINGERSTRASSE 20\",\"3030 BERNE\"]}}}},\"RmtInf\":{\"Ustrd\":\"Rg.-Nr. 1234567890\",\"Strd\":{\"AddtlRmtInf\":[\"?REJECT?0\",\"?ERROR?000\"]}},\"RltdDts\":{\"AccptncDtTm\":\"2020-06-17T20:00:00\"}},{\"Refs\":{\"MsgId\":\"MsgId-001\",\"AcctSvcrRef\":\"0000000000000006\",\"PmtInfId\":\"PmtInfId-001-01\",\"InstrId\":\"InstrId-001-01-02\",\"EndToEndId\":\"EndToEndId-001-03-06\",\"Prtry\":{\"Tp\":\"00\",\"Ref\":\"00000000000000000000006\"}},\"Amt\":{\"Ccy\":\"CHF\",\"\":\"100.00\"},\"CdtDbtInd\":\"CRDT\",\"BkTxCd\":{\"Domn\":{\"Cd\":\"PMNT\",\"Fmly\":{\"Cd\":\"RCDT\",\"SubFmlyCd\":\"AUTT\"}}},\"RltdPties\":{\"Dbtr\":{\"Nm\":\"Bernasconi Maria\",\"PstlAdr\":{\"StrtNm\":\"Place de la Gare\",\"BldgNb\":\"12\",\"PstCd\":\"2502\",\"TwnNm\":\"Biel/Bienne\",\"Ctry\":\"CH\"}},\"DbtrAcct\":{\"Id\":{\"IBAN\":\"CH5109000000250092291\"}},\"CdtrAcct\":{\"Id\":{\"IBAN\":\"CH2909000000250094239\"}}},\"RltdAgts\":{\"DbtrAgt\":{\"FinInstnId\":{\"BICFI\":\"POFICHBEXXX\",\"Nm\":\"POSTFINANCE AG\",\"PstlAdr\":{\"AdrLine\":[\"MINGERSTRASSE 20\",\"3030 BERNE\"]}}}},\"RmtInf\":{\"Ustrd\":\"Rg.-Nr. 987654321\",\"Strd\":{\"AddtlRmtInf\":[\"?REJECT?0\",\"?ERROR?000\"]}},\"RltdDts\":{\"AccptncDtTm\":\"2020-06-17T20:00:00\"}}]},\"AddtlNtryInf\":\"SAMMELGUTSCHRIFT FÜR KONTO: CH2909000000250094239 VERARBEITUNG VOM 17.06.2020 PAKET ID: 9999999999999999\"}}}}",
				resultNode.get(Parameter.TARGET_JSON.key()).asText());
		assertNull(resultNode.get("errors"));
	}

	@Test
	public void testWithSourcePathOK() throws SQLException, IOException
	{
		ObjectNode parameters = mapper.createObjectNode();
		parameters.put(Parameter.SOURCE_XML.key(), filepath);

		String result = Fsl.execute("Converter.convertXmlToJson", parameters.toString());

		JsonNode resultNode = mapper.readTree(result);
		JsonNode json = resultNode.get(Parameter.TARGET_JSON.key());
		assertNotNull(json);
		assertEquals(json.asText(), resultNode.get(Parameter.TARGET_JSON.key()).asText());
		assertEquals(
				"{\"schemaLocation\":\"urn:iso:std:iso:20022:tech:xsd:camt.054.001.04 camt.054.001.04.xsd\",\"BkToCstmrDbtCdtNtfctn\":{\"GrpHdr\":{\"MsgId\":\"2019042475204228731867\",\"CreDtTm\":\"2020-06-17T23:33:09\",\"MsgPgntn\":{\"PgNb\":\"1\",\"LastPgInd\":\"true\"},\"AddtlInf\":\"SPS/1.6/PROD\"},\"Ntfctn\":{\"Id\":\"20180316375204228731887\",\"CreDtTm\":\"2020-06-17T23:33:09\",\"FrToDt\":{\"FrDtTm\":\"2020-06-17T00:00:00\",\"ToDtTm\":\"2020-06-17T23:59:59\"},\"Acct\":{\"Id\":{\"IBAN\":\"CH2909000000250094239\"},\"Ownr\":{\"Nm\":\"Robert Schneider SA Grands magasins Biel/Bienne\"}},\"Ntry\":{\"Amt\":{\"Ccy\":\"CHF\",\"\":\"1500.00\"},\"CdtDbtInd\":\"CRDT\",\"RvslInd\":\"false\",\"Sts\":\"BOOK\",\"BookgDt\":{\"Dt\":\"2020-06-17\"},\"ValDt\":{\"Dt\":\"2020-06-17\"},\"AcctSvcrRef\":\"0000000000000001\",\"BkTxCd\":{\"Domn\":{\"Cd\":\"PMNT\",\"Fmly\":{\"Cd\":\"RCDT\",\"SubFmlyCd\":\"AUTT\"}}},\"NtryDtls\":{\"Btch\":{\"NbOfTxs\":\"6\"},\"TxDtls\":[{\"Refs\":{\"MsgId\":\"MsgId-001\",\"AcctSvcrRef\":\"0000000000000001\",\"PmtInfId\":\"PmtInfId-001-03\",\"InstrId\":\"InstrId-001-03-02\",\"EndToEndId\":\"EndToEndId-001-03-01\",\"Prtry\":{\"Tp\":\"00\",\"Ref\":\"00000000000000000000001\"}},\"Amt\":{\"Ccy\":\"CHF\",\"\":\"400.00\"},\"CdtDbtInd\":\"CRDT\",\"BkTxCd\":{\"Domn\":{\"Cd\":\"PMNT\",\"Fmly\":{\"Cd\":\"RCDT\",\"SubFmlyCd\":\"AUTT\"}}},\"RltdPties\":{\"Dbtr\":{\"Nm\":\"Bernasconi Maria\",\"PstlAdr\":{\"StrtNm\":\"Place de la Gare\",\"BldgNb\":\"12\",\"PstCd\":\"2502\",\"TwnNm\":\"Biel/Bienne\",\"Ctry\":\"CH\"}},\"DbtrAcct\":{\"Id\":{\"IBAN\":\"CH5109000000250092291\"}},\"CdtrAcct\":{\"Id\":{\"IBAN\":\"CH2909000000250094239\"}}},\"RltdAgts\":{\"DbtrAgt\":{\"FinInstnId\":{\"BICFI\":\"POFICHBEXXX\",\"Nm\":\"POSTFINANCE AG\",\"PstlAdr\":{\"AdrLine\":[\"MINGERSTRASSE 20\",\"3030 BERNE\"]}}}},\"RmtInf\":{\"Ustrd\":\"Kontouebertrag\",\"Strd\":{\"AddtlRmtInf\":[\"?REJECT?0\",\"?ERROR?000\"]}},\"RltdDts\":{\"AccptncDtTm\":\"2020-06-17T20:00:00\"}},{\"Refs\":{\"MsgId\":\"MsgId-001\",\"AcctSvcrRef\":\"0000000000000002\",\"PmtInfId\":\"PmtInfId-001-03\",\"InstrId\":\"InstrId-001-03-01\",\"EndToEndId\":\"EndToEndId-001-03-02\",\"Prtry\":{\"Tp\":\"00\",\"Ref\":\"00000000000000000000002\"}},\"Amt\":{\"Ccy\":\"CHF\",\"\":\"350.00\"},\"CdtDbtInd\":\"CRDT\",\"BkTxCd\":{\"Domn\":{\"Cd\":\"PMNT\",\"Fmly\":{\"Cd\":\"RCDT\",\"SubFmlyCd\":\"AUTT\"}}},\"RltdPties\":{\"Dbtr\":{\"Nm\":\"Bernasconi Maria\",\"PstlAdr\":{\"StrtNm\":\"Place de la Gare\",\"BldgNb\":\"12\",\"PstCd\":\"2502\",\"TwnNm\":\"Biel/Bienne\",\"Ctry\":\"CH\"}},\"DbtrAcct\":{\"Id\":{\"IBAN\":\"CH5109000000250092291\"}},\"CdtrAcct\":{\"Id\":{\"IBAN\":\"CH2909000000250094239\"}}},\"RltdAgts\":{\"DbtrAgt\":{\"FinInstnId\":{\"BICFI\":\"POFICHBEXXX\",\"Nm\":\"POSTFINANCE AG\",\"PstlAdr\":{\"AdrLine\":[\"MINGERSTRASSE 20\",\"3030 BERNE\"]}}}},\"RmtInf\":{\"Ustrd\":\"Kontouebertrag\",\"Strd\":{\"AddtlRmtInf\":[\"?REJECT?0\",\"?ERROR?000\"]}},\"RltdDts\":{\"AccptncDtTm\":\"2020-06-17T20:00:00\"}},{\"Refs\":{\"MsgId\":\"MsgId-001\",\"AcctSvcrRef\":\"0000000000000003\",\"PmtInfId\":\"PmtInfId-001-03\",\"InstrId\":\"InstrId-001-03-03\",\"EndToEndId\":\"EndToEndId-001-03-03\",\"Prtry\":{\"Tp\":\"00\",\"Ref\":\"00000000000000000000003\"}},\"Amt\":{\"Ccy\":\"CHF\",\"\":\"450.00\"},\"CdtDbtInd\":\"CRDT\",\"BkTxCd\":{\"Domn\":{\"Cd\":\"PMNT\",\"Fmly\":{\"Cd\":\"RCDT\",\"SubFmlyCd\":\"AUTT\"}}},\"RltdPties\":{\"Dbtr\":{\"Nm\":\"Bernasconi Maria\",\"PstlAdr\":{\"StrtNm\":\"Place de la Gare\",\"BldgNb\":\"12\",\"PstCd\":\"2502\",\"TwnNm\":\"Biel/Bienne\",\"Ctry\":\"CH\"}},\"DbtrAcct\":{\"Id\":{\"IBAN\":\"CH5109000000250092291\"}},\"CdtrAcct\":{\"Id\":{\"IBAN\":\"CH2909000000250094239\"}}},\"RltdAgts\":{\"DbtrAgt\":{\"FinInstnId\":{\"BICFI\":\"POFICHBEXXX\",\"Nm\":\"POSTFINANCE AG\",\"PstlAdr\":{\"AdrLine\":[\"MINGERSTRASSE 20\",\"3030 BERNE\"]}}}},\"RmtInf\":{\"Ustrd\":\"Kontouebertrag\",\"Strd\":{\"AddtlRmtInf\":[\"?REJECT?0\",\"?ERROR?000\"]}},\"RltdDts\":{\"AccptncDtTm\":\"2020-06-17T20:00:00\"}},{\"Refs\":{\"MsgId\":\"MsgId-001\",\"AcctSvcrRef\":\"0000000000000004\",\"PmtInfId\":\"PmtInfId-001-01\",\"InstrId\":\"InstrId-001-01-03\",\"EndToEndId\":\"EndToEndId-001-03-04\",\"Prtry\":{\"Tp\":\"00\",\"Ref\":\"00000000000000000000004\"}},\"Amt\":{\"Ccy\":\"CHF\",\"\":\"150.00\"},\"CdtDbtInd\":\"CRDT\",\"BkTxCd\":{\"Domn\":{\"Cd\":\"PMNT\",\"Fmly\":{\"Cd\":\"RCDT\",\"SubFmlyCd\":\"AUTT\"}}},\"RltdPties\":{\"Dbtr\":{\"Nm\":\"Bernasconi Maria\",\"PstlAdr\":{\"StrtNm\":\"Place de la Gare\",\"BldgNb\":\"12\",\"PstCd\":\"2502\",\"TwnNm\":\"Biel/Bienne\",\"Ctry\":\"CH\"}},\"DbtrAcct\":{\"Id\":{\"IBAN\":\"CH5109000000250092291\"}},\"CdtrAcct\":{\"Id\":{\"IBAN\":\"CH2909000000250094239\"}}},\"RltdAgts\":{\"DbtrAgt\":{\"FinInstnId\":{\"BICFI\":\"POFICHBEXXX\",\"Nm\":\"POSTFINANCE AG\",\"PstlAdr\":{\"AdrLine\":[\"MINGERSTRASSE 20\",\"3030 BERNE\"]}}}},\"RmtInf\":{\"Ustrd\":\"Rg.-Nr. 001\",\"Strd\":{\"AddtlRmtInf\":[\"?REJECT?0\",\"?ERROR?000\"]}},\"RltdDts\":{\"AccptncDtTm\":\"2020-06-17T20:00:00\"}},{\"Refs\":{\"MsgId\":\"MsgId-001\",\"AcctSvcrRef\":\"0000000000000005\",\"PmtInfId\":\"PmtInfId-001-01\",\"InstrId\":\"InstrId-001-01-01\",\"EndToEndId\":\"EndToEndId-001-03-05\",\"Prtry\":{\"Tp\":\"00\",\"Ref\":\"00000000000000000000005\"}},\"Amt\":{\"Ccy\":\"CHF\",\"\":\"50.00\"},\"CdtDbtInd\":\"CRDT\",\"BkTxCd\":{\"Domn\":{\"Cd\":\"PMNT\",\"Fmly\":{\"Cd\":\"RCDT\",\"SubFmlyCd\":\"AUTT\"}}},\"RltdPties\":{\"Dbtr\":{\"Nm\":\"Bernasconi Maria\",\"PstlAdr\":{\"StrtNm\":\"Place de la Gare\",\"BldgNb\":\"12\",\"PstCd\":\"2502\",\"TwnNm\":\"Biel/Bienne\",\"Ctry\":\"CH\"}},\"DbtrAcct\":{\"Id\":{\"IBAN\":\"CH5109000000250092291\"}},\"CdtrAcct\":{\"Id\":{\"IBAN\":\"CH2909000000250094239\"}}},\"RltdAgts\":{\"DbtrAgt\":{\"FinInstnId\":{\"BICFI\":\"POFICHBEXXX\",\"Nm\":\"POSTFINANCE AG\",\"PstlAdr\":{\"AdrLine\":[\"MINGERSTRASSE 20\",\"3030 BERNE\"]}}}},\"RmtInf\":{\"Ustrd\":\"Rg.-Nr. 1234567890\",\"Strd\":{\"AddtlRmtInf\":[\"?REJECT?0\",\"?ERROR?000\"]}},\"RltdDts\":{\"AccptncDtTm\":\"2020-06-17T20:00:00\"}},{\"Refs\":{\"MsgId\":\"MsgId-001\",\"AcctSvcrRef\":\"0000000000000006\",\"PmtInfId\":\"PmtInfId-001-01\",\"InstrId\":\"InstrId-001-01-02\",\"EndToEndId\":\"EndToEndId-001-03-06\",\"Prtry\":{\"Tp\":\"00\",\"Ref\":\"00000000000000000000006\"}},\"Amt\":{\"Ccy\":\"CHF\",\"\":\"100.00\"},\"CdtDbtInd\":\"CRDT\",\"BkTxCd\":{\"Domn\":{\"Cd\":\"PMNT\",\"Fmly\":{\"Cd\":\"RCDT\",\"SubFmlyCd\":\"AUTT\"}}},\"RltdPties\":{\"Dbtr\":{\"Nm\":\"Bernasconi Maria\",\"PstlAdr\":{\"StrtNm\":\"Place de la Gare\",\"BldgNb\":\"12\",\"PstCd\":\"2502\",\"TwnNm\":\"Biel/Bienne\",\"Ctry\":\"CH\"}},\"DbtrAcct\":{\"Id\":{\"IBAN\":\"CH5109000000250092291\"}},\"CdtrAcct\":{\"Id\":{\"IBAN\":\"CH2909000000250094239\"}}},\"RltdAgts\":{\"DbtrAgt\":{\"FinInstnId\":{\"BICFI\":\"POFICHBEXXX\",\"Nm\":\"POSTFINANCE AG\",\"PstlAdr\":{\"AdrLine\":[\"MINGERSTRASSE 20\",\"3030 BERNE\"]}}}},\"RmtInf\":{\"Ustrd\":\"Rg.-Nr. 987654321\",\"Strd\":{\"AddtlRmtInf\":[\"?REJECT?0\",\"?ERROR?000\"]}},\"RltdDts\":{\"AccptncDtTm\":\"2020-06-17T20:00:00\"}}]},\"AddtlNtryInf\":\"SAMMELGUTSCHRIFT FÜR KONTO: CH2909000000250094239 VERARBEITUNG VOM 17.06.2020 PAKET ID: 9999999999999999\"}}}}",
				resultNode.get(Parameter.TARGET_JSON.key()).asText());
		assertEquals("OK", resultNode.get("result").asText());
		assertNull(resultNode.get("errors"));
	}

	@Test
	public void testPflugerWithSourcePathOK() throws SQLException, IOException
	{
		ObjectNode parameters = mapper.createObjectNode();
		parameters.put(Parameter.SOURCE_XML.key(), sourcePath);

		String result = Fsl.execute("Converter.convertXmlToJson", parameters.toString());

		JsonNode resultNode = mapper.readTree(result);
		JsonNode json = resultNode.get(Parameter.TARGET_JSON.key());
		assertNotNull(json);
		assertEquals(json.asText(), resultNode.get(Parameter.TARGET_JSON.key()).asText());
		assertEquals(
				"{\"schemaLocation\":\"urn:iso:std:iso:20022:tech:xsd:camt.054.001.04 camt.054.001.04.xsd\",\"BkToCstmrDbtCdtNtfctn\":{\"GrpHdr\":{\"MsgId\":\"20221216375204007304861\",\"CreDtTm\":\"2022-12-16T23:39:55\",\"MsgPgntn\":{\"PgNb\":\"1\",\"LastPgInd\":\"true\"},\"AddtlInf\":\"SPS/1.7/PROD\"},\"Ntfctn\":{\"Id\":\"20221216375204007304863\",\"CreDtTm\":\"2022-12-16T23:39:55\",\"FrToDt\":{\"FrDtTm\":\"2022-12-10T00:00:00\",\"ToDtTm\":\"2022-12-16T23:59:59\"},\"RptgSrc\":{\"Prtry\":\"OTHR\"},\"Acct\":{\"Id\":{\"IBAN\":\"CH0809000000450010065\"},\"Ownr\":{\"Nm\":\"Pfluger Christoph August Der Zeitpunkt Solothurn\"}},\"Ntry\":{\"NtryRef\":\"CH5630000001450010065\",\"Amt\":{\"Ccy\":\"CHF\",\"\":\"50.00\"},\"CdtDbtInd\":\"CRDT\",\"RvslInd\":\"false\",\"Sts\":\"BOOK\",\"BookgDt\":{\"Dt\":\"2022-12-16\"},\"ValDt\":{\"Dt\":\"2022-12-16\"},\"AcctSvcrRef\":\"350220009M2I8XBU\",\"BkTxCd\":{\"Domn\":{\"Cd\":\"PMNT\",\"Fmly\":{\"Cd\":\"RCDT\",\"SubFmlyCd\":\"VCOM\"}}},\"NtryDtls\":{\"Btch\":{\"NbOfTxs\":\"3\"},\"TxDtls\":[{\"Refs\":{\"AcctSvcrRef\":\"221215CH09LS26M5\",\"InstrId\":\"20221215000800994396358\",\"Prtry\":{\"Tp\":\"00\",\"Ref\":\"20221216375204708963137\"}},\"Amt\":{\"Ccy\":\"CHF\",\"\":\"20.00\"},\"CdtDbtInd\":\"CRDT\",\"BkTxCd\":{\"Domn\":{\"Cd\":\"PMNT\",\"Fmly\":{\"Cd\":\"RCDT\",\"SubFmlyCd\":\"AUTT\"}}},\"RltdPties\":{\"Dbtr\":{\"Nm\":\"Pfluger, Christoph August\",\"PstlAdr\":{\"StrtNm\":\"Werkhofstrasse\",\"BldgNb\":\"19\",\"PstCd\":\"4500\",\"TwnNm\":\"Solothurn\",\"Ctry\":\"CH\"}},\"DbtrAcct\":{\"Id\":{\"IBAN\":\"CH9209000000407516054\"}},\"UltmtDbtr\":{\"Nm\":\"Linda Biedermann\",\"PstlAdr\":{\"StrtNm\":\"Florastr. 16\",\"PstCd\":\"4500\",\"TwnNm\":\"Solothurn\",\"Ctry\":\"CH\"}},\"CdtrAcct\":{\"Id\":{\"IBAN\":\"CH5630000001450010065\"}}},\"RltdAgts\":{\"DbtrAgt\":{\"FinInstnId\":{\"BICFI\":\"POFICHBEXXX\",\"Nm\":\"POSTFINANCE AG\",\"PstlAdr\":{\"AdrLine\":[\"MINGERSTRASSE 20\",\"3030 BERN\"]}}}},\"RmtInf\":{\"Strd\":{\"CdtrRefInf\":{\"Tp\":{\"CdOrPrtry\":{\"Prtry\":\"QRR\"}},\"Ref\":\"000000372142141220226485603\"},\"AddtlRmtInf\":[\"?REJECT?0\",\"?ERROR?000\",\"Rechnung Nr. 372142\"]}},\"RltdDts\":{\"AccptncDtTm\":\"2022-12-16T20:00:00\"}},{\"Refs\":{\"AcctSvcrRef\":\"221215CH09LSZ5WQ\",\"InstrId\":\"20221215000800994366463\",\"Prtry\":{\"Tp\":\"00\",\"Ref\":\"20221216375204708885249\"}},\"Amt\":{\"Ccy\":\"CHF\",\"\":\"10.00\"},\"CdtDbtInd\":\"CRDT\",\"BkTxCd\":{\"Domn\":{\"Cd\":\"PMNT\",\"Fmly\":{\"Cd\":\"RCDT\",\"SubFmlyCd\":\"AUTT\"}}},\"RltdPties\":{\"Dbtr\":{\"Nm\":\"Pfluger, Christoph August\",\"PstlAdr\":{\"StrtNm\":\"Werkhofstrasse\",\"BldgNb\":\"19\",\"PstCd\":\"4500\",\"TwnNm\":\"Solothurn\",\"Ctry\":\"CH\"}},\"DbtrAcct\":{\"Id\":{\"IBAN\":\"CH9209000000407516054\"}},\"UltmtDbtr\":{\"Nm\":\"Christoph Pfluger\",\"PstlAdr\":{\"StrtNm\":\"Werkhofstr. 19\",\"PstCd\":\"4500\",\"TwnNm\":\"Solothurn\",\"Ctry\":\"CH\"}},\"CdtrAcct\":{\"Id\":{\"IBAN\":\"CH5630000001450010065\"}}},\"RltdAgts\":{\"DbtrAgt\":{\"FinInstnId\":{\"BICFI\":\"POFICHBEXXX\",\"Nm\":\"POSTFINANCE AG\",\"PstlAdr\":{\"AdrLine\":[\"MINGERSTRASSE 20\",\"3030 BERN\"]}}}},\"RmtInf\":{\"Strd\":{\"CdtrRefInf\":{\"Tp\":{\"CdOrPrtry\":{\"Prtry\":\"QRR\"}},\"Ref\":\"000000372144141220225496407\"},\"AddtlRmtInf\":[\"?REJECT?0\",\"?ERROR?000\",\"Rechnung Nr. 372144\"]}},\"RltdDts\":{\"AccptncDtTm\":\"2022-12-16T20:00:00\"}},{\"Refs\":{\"AcctSvcrRef\":\"221215CH09LUCD83\",\"InstrId\":\"20221215000800994414138\",\"Prtry\":{\"Tp\":\"00\",\"Ref\":\"20221216375204708914844\"}},\"Amt\":{\"Ccy\":\"CHF\",\"\":\"20.00\"},\"CdtDbtInd\":\"CRDT\",\"BkTxCd\":{\"Domn\":{\"Cd\":\"PMNT\",\"Fmly\":{\"Cd\":\"RCDT\",\"SubFmlyCd\":\"AUTT\"}}},\"RltdPties\":{\"Dbtr\":{\"Nm\":\"Pfluger, Christoph August\",\"PstlAdr\":{\"StrtNm\":\"Werkhofstrasse\",\"BldgNb\":\"19\",\"PstCd\":\"4500\",\"TwnNm\":\"Solothurn\",\"Ctry\":\"CH\"}},\"DbtrAcct\":{\"Id\":{\"IBAN\":\"CH9209000000407516054\"}},\"UltmtDbtr\":{\"Nm\":\"Linda Biedermann\",\"PstlAdr\":{\"StrtNm\":\"Spinngasse 6\",\"PstCd\":\"4552\",\"TwnNm\":\"Derendingen\",\"Ctry\":\"CH\"}},\"CdtrAcct\":{\"Id\":{\"IBAN\":\"CH5630000001450010065\"}}},\"RltdAgts\":{\"DbtrAgt\":{\"FinInstnId\":{\"BICFI\":\"POFICHBEXXX\",\"Nm\":\"POSTFINANCE AG\",\"PstlAdr\":{\"AdrLine\":[\"MINGERSTRASSE 20\",\"3030 BERN\"]}}}},\"RmtInf\":{\"Strd\":{\"CdtrRefInf\":{\"Tp\":{\"CdOrPrtry\":{\"Prtry\":\"QRR\"}},\"Ref\":\"000000372143141220225247907\"},\"AddtlRmtInf\":[\"?REJECT?0\",\"?ERROR?000\",\"Rechnung Nr. 372143\"]}},\"RltdDts\":{\"AccptncDtTm\":\"2022-12-16T20:00:00\"}}]},\"AddtlNtryInf\":\"SAMMELGUTSCHRIFT FÜR KONTO: CH5630000001450010065 VERARBEITUNG VOM 16.12.2022 PAKET ID: 221216CH000008UO\"}}}}",
				resultNode.get(Parameter.TARGET_JSON.key()).asText());
		assertEquals("OK", resultNode.get("result").asText());
		assertNull(resultNode.get("errors"));
	}

	@Test
	public void testParametersWithJsonContainerAndXmlFilepath() throws SQLException, IOException
	{
		File file = new File(filepath);

		ObjectNode parameters = mapper.createObjectNode();
		parameters.put(Parameter.SOURCE_XML.key(), file.getAbsolutePath());

		String result = Fsl.execute("Converter.convertXmlToJson", parameters.toString());

		JsonNode resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
		assertNotNull(resultNode.get(Parameter.TARGET_JSON.key()).asText());

		Path jsonPath = Paths.get("resources", "json", "camt.054.001.04.json");
		String json = mapper.readTree(jsonPath.toFile()).toString();
		assertEquals(json, resultNode.get(Parameter.TARGET_JSON.key()).asText());
	}

	@Test
	public void testParametersWithJsonContainerAndInvalidXmlFilepath() throws SQLException, IOException
	{
		String filename = "resources/xml/200924_camt";
		File file = new File(filename);

		ObjectNode parameters = mapper.createObjectNode();
		parameters.put(Parameter.SOURCE_XML.key(), file.getAbsolutePath());

		String result = Fsl.execute("Converter.convertXmlToJson", parameters.toString());

		JsonNode resultNode = mapper.readTree(result);
		assertEquals("Fehler", resultNode.get("result").asText());
		assertNull(resultNode.get(Parameter.TARGET_JSON.key()));

		assertEquals("Ungültiger Parameter", resultNode.get("errors").get(0).asText());
	}
}
