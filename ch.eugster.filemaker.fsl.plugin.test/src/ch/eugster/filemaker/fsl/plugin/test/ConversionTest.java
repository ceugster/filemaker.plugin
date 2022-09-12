package ch.eugster.filemaker.fsl.plugin.test;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.eugster.filemaker.fsl.plugin.Fsl;
import ch.eugster.filemaker.fsl.plugin.converter.Converter.Parameter;

public class ConversionTest
{
	private ObjectMapper mapper;

	@BeforeEach
	public void beforeEach()
	{
		this.mapper = new ObjectMapper();
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
		String filename = "resources/200924_camt.054_P_CH2909000000250094239_1110092703_0_2019042423412214.xml";
		Path path = Paths.get(filename);
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
		String filename = "resources/200924_camt.054_P_CH2909000000250094239_1110092703_0_2019042423412214.xml";
		Path path = Paths.get(filename);
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
		String filename = "resources/200924_camt.054_P_CH2909000000250094239_1110092703_0_2019042423412214.xml";
		Path path = Paths.get(filename);
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
		String filename = "resources/200924_camt.054_P_CH2909000000250094239_1110092703_0_2019042423412214.xml";

		ObjectNode parameters = mapper.createObjectNode();
		parameters.put(Parameter.SOURCE_XML.key(), filename);

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
	public void testParametersWithJsonContainerAndXmlFilepath() throws SQLException, IOException
	{
		String filename = "resources/200924_camt.054_P_CH2909000000250094239_1110092703_0_2019042423412214.xml";
		File file = new File(filename);

		ObjectNode parameters = mapper.createObjectNode();
		parameters.put(Parameter.SOURCE_XML.key(), file.getAbsolutePath());

		String result = Fsl.execute("Converter.convertXmlToJson", parameters.toString());

		JsonNode resultNode = mapper.readTree(result);
		assertEquals("OK", resultNode.get("result").asText());
		assertNotNull(resultNode.get(Parameter.TARGET_JSON.key()).asText());

		Path jsonPath = Paths.get("json", "camt.054.001.04.json");
		String json = mapper.readTree(jsonPath.toFile()).toString();
		assertEquals(json, resultNode.get(Parameter.TARGET_JSON.key()).asText());
	}

	@Test
	public void testParametersWithJsonContainerAndInvalidXmlFilepath() throws SQLException, IOException
	{
		String filename = "resources/200924_camt";
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
