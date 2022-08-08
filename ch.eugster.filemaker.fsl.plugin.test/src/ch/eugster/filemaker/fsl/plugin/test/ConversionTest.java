package ch.eugster.filemaker.fsl.plugin.test;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.eugster.filemaker.fsl.plugin.Fsl;
import ch.eugster.filemaker.fsl.plugin.converter.XmlToJsonConverter.Parameter;

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
	public void testParametersOK() throws SQLException, IOException
	{
		String filename = "resources/200924_camt.054_P_CH2909000000250094239_1110092703_0_2019042423412214.xml";
		File file = new File(filename);
		InputStream is = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		StringBuilder sb = new StringBuilder();
		try
		{
			while (br.ready())
			{
				sb = sb.append(br.readLine());
			}
		}
		finally
		{
			br.close();
		}
		String xml = sb.toString();

		ObjectNode parameters = mapper.createObjectNode();
		parameters.put(Parameter.SOURCE_XML.key(), xml);
		parameters.put(Parameter.TARGET_JSON.key(), "");

		String result = new Fsl().execute("ConvertXmlToJson", parameters.toString());

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
	public void testWithSourcePathOK() throws SQLException, IOException
	{
		String filename = "resources/200924_camt.054_P_CH2909000000250094239_1110092703_0_2019042423412214.xml";

		ObjectNode parameters = mapper.createObjectNode();
		parameters.put(Parameter.SOURCE_XML.key(), filename);

		String result = new Fsl().execute("ConvertXmlToJson", parameters.toString());

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
	public void testParametersError() throws SQLException, IOException
	{
		String filename = "resources/200924_camt.054_P_CH2909000000250094239_1110092703_0_2019042423412214.xml";
		File file = new File(filename);

		ObjectNode parameters = mapper.createObjectNode();
		parameters.put(Parameter.SOURCE_XML.key(), file.getAbsolutePath());

		String result = new Fsl().execute("ConvertXmlToJson", parameters.toString());

		JsonNode target = mapper.readTree(result);
		assertNotNull(target.get(Parameter.TARGET_JSON.key()));
		assertEquals(filename, target.get(Parameter.TARGET_JSON.key()).asText());
		assertNull(target.get(Parameter.TARGET_JSON.key()));
		assertEquals("Fehler", target.get("result").asText());
		assertEquals(1, target.get("errors").size());
		assertEquals(
				"Konversionsfehler: Unexpected character '2' (code 50) in prolog; expected '<'\n at [row,col {unknown-source}]: [1,1]",
				target.get("errors").get(0).asText());
	}
}
