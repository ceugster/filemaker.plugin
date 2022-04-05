package ch.eugster.filemaker.fsl.plugin.test;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ch.eugster.filemaker.fsl.plugin.Fsl;
import ch.eugster.filemaker.fsl.plugin.converter.Parameter.ConversionParameter;

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
		String filename = "200924_camt.054_P_CH2909000000250094239_1110092703_0_2019042423412214.xml";
		InputStream is = ConversionTest.class.getResourceAsStream(filename);
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		StringBuilder sb = new StringBuilder();
		while (br.ready())
		{
			sb = sb.append(br.readLine());
		}
		String xml = sb.toString();
		ObjectNode source = mapper.createObjectNode();
		source.put(ConversionParameter.SOURCE_XML.key(), xml);
		String result = new Fsl().execute("ConvertXmlToJson", source.toString());
		JsonNode target = mapper.readTree(result);
		assertNotNull(target.get(ConversionParameter.SOURCE_XML.key()));
		assertEquals(xml, target.get(ConversionParameter.SOURCE_XML.key()).asText());
		System.out.println(target.get(ConversionParameter.SOURCE_XML.key()).asText());
		assertNotNull(target.get(ConversionParameter.TARGET_JSON.key()));
		assertEquals(
				"{\"schemaLocation\":\"urn:iso:std:iso:20022:tech:xsd:camt.054.001.04 camt.054.001.04.xsd\",\"BkToCstmrDbtCdtNtfctn\":{\"GrpHdr\":{\"MsgId\":\"2019042475204228731867\",\"CreDtTm\":\"2020-06-17T23:33:09\",\"MsgPgntn\":{\"PgNb\":\"1\",\"LastPgInd\":\"true\"},\"AddtlInf\":\"SPS/1.6/PROD\"},\"Ntfctn\":{\"Id\":\"20180316375204228731887\",\"CreDtTm\":\"2020-06-17T23:33:09\",\"FrToDt\":{\"FrDtTm\":\"2020-06-17T00:00:00\",\"ToDtTm\":\"2020-06-17T23:59:59\"},\"Acct\":{\"Id\":{\"IBAN\":\"CH2909000000250094239\"},\"Ownr\":{\"Nm\":\"Robert Schneider SA Grands magasins Biel/Bienne\"}},\"Ntry\":{\"Amt\":{\"Ccy\":\"CHF\",\"\":\"1500.00\"},\"CdtDbtInd\":\"CRDT\",\"RvslInd\":\"false\",\"Sts\":\"BOOK\",\"BookgDt\":{\"Dt\":\"2020-06-17\"},\"ValDt\":{\"Dt\":\"2020-06-17\"},\"AcctSvcrRef\":\"0000000000000001\",\"BkTxCd\":{\"Domn\":{\"Cd\":\"PMNT\",\"Fmly\":{\"Cd\":\"RCDT\",\"SubFmlyCd\":\"AUTT\"}}},\"NtryDtls\":{\"Btch\":{\"NbOfTxs\":\"6\"},\"TxDtls\":[{\"Refs\":{\"MsgId\":\"MsgId-001\",\"AcctSvcrRef\":\"0000000000000001\",\"PmtInfId\":\"PmtInfId-001-03\",\"InstrId\":\"InstrId-001-03-02\",\"EndToEndId\":\"EndToEndId-001-03-01\",\"Prtry\":{\"Tp\":\"00\",\"Ref\":\"00000000000000000000001\"}},\"Amt\":{\"Ccy\":\"CHF\",\"\":\"400.00\"},\"CdtDbtInd\":\"CRDT\",\"BkTxCd\":{\"Domn\":{\"Cd\":\"PMNT\",\"Fmly\":{\"Cd\":\"RCDT\",\"SubFmlyCd\":\"AUTT\"}}},\"RltdPties\":{\"Dbtr\":{\"Nm\":\"Bernasconi Maria\",\"PstlAdr\":{\"StrtNm\":\"Place de la Gare\",\"BldgNb\":\"12\",\"PstCd\":\"2502\",\"TwnNm\":\"Biel/Bienne\",\"Ctry\":\"CH\"}},\"DbtrAcct\":{\"Id\":{\"IBAN\":\"CH5109000000250092291\"}},\"CdtrAcct\":{\"Id\":{\"IBAN\":\"CH2909000000250094239\"}}},\"RltdAgts\":{\"DbtrAgt\":{\"FinInstnId\":{\"BICFI\":\"POFICHBEXXX\",\"Nm\":\"POSTFINANCE AG\",\"PstlAdr\":{\"AdrLine\":[\"MINGERSTRASSE 20\",\"3030 BERNE\"]}}}},\"RmtInf\":{\"Ustrd\":\"Kontouebertrag\",\"Strd\":{\"AddtlRmtInf\":[\"?REJECT?0\",\"?ERROR?000\"]}},\"RltdDts\":{\"AccptncDtTm\":\"2020-06-17T20:00:00\"}},{\"Refs\":{\"MsgId\":\"MsgId-001\",\"AcctSvcrRef\":\"0000000000000002\",\"PmtInfId\":\"PmtInfId-001-03\",\"InstrId\":\"InstrId-001-03-01\",\"EndToEndId\":\"EndToEndId-001-03-02\",\"Prtry\":{\"Tp\":\"00\",\"Ref\":\"00000000000000000000002\"}},\"Amt\":{\"Ccy\":\"CHF\",\"\":\"350.00\"},\"CdtDbtInd\":\"CRDT\",\"BkTxCd\":{\"Domn\":{\"Cd\":\"PMNT\",\"Fmly\":{\"Cd\":\"RCDT\",\"SubFmlyCd\":\"AUTT\"}}},\"RltdPties\":{\"Dbtr\":{\"Nm\":\"Bernasconi Maria\",\"PstlAdr\":{\"StrtNm\":\"Place de la Gare\",\"BldgNb\":\"12\",\"PstCd\":\"2502\",\"TwnNm\":\"Biel/Bienne\",\"Ctry\":\"CH\"}},\"DbtrAcct\":{\"Id\":{\"IBAN\":\"CH5109000000250092291\"}},\"CdtrAcct\":{\"Id\":{\"IBAN\":\"CH2909000000250094239\"}}},\"RltdAgts\":{\"DbtrAgt\":{\"FinInstnId\":{\"BICFI\":\"POFICHBEXXX\",\"Nm\":\"POSTFINANCE AG\",\"PstlAdr\":{\"AdrLine\":[\"MINGERSTRASSE 20\",\"3030 BERNE\"]}}}},\"RmtInf\":{\"Ustrd\":\"Kontouebertrag\",\"Strd\":{\"AddtlRmtInf\":[\"?REJECT?0\",\"?ERROR?000\"]}},\"RltdDts\":{\"AccptncDtTm\":\"2020-06-17T20:00:00\"}},{\"Refs\":{\"MsgId\":\"MsgId-001\",\"AcctSvcrRef\":\"0000000000000003\",\"PmtInfId\":\"PmtInfId-001-03\",\"InstrId\":\"InstrId-001-03-03\",\"EndToEndId\":\"EndToEndId-001-03-03\",\"Prtry\":{\"Tp\":\"00\",\"Ref\":\"00000000000000000000003\"}},\"Amt\":{\"Ccy\":\"CHF\",\"\":\"450.00\"},\"CdtDbtInd\":\"CRDT\",\"BkTxCd\":{\"Domn\":{\"Cd\":\"PMNT\",\"Fmly\":{\"Cd\":\"RCDT\",\"SubFmlyCd\":\"AUTT\"}}},\"RltdPties\":{\"Dbtr\":{\"Nm\":\"Bernasconi Maria\",\"PstlAdr\":{\"StrtNm\":\"Place de la Gare\",\"BldgNb\":\"12\",\"PstCd\":\"2502\",\"TwnNm\":\"Biel/Bienne\",\"Ctry\":\"CH\"}},\"DbtrAcct\":{\"Id\":{\"IBAN\":\"CH5109000000250092291\"}},\"CdtrAcct\":{\"Id\":{\"IBAN\":\"CH2909000000250094239\"}}},\"RltdAgts\":{\"DbtrAgt\":{\"FinInstnId\":{\"BICFI\":\"POFICHBEXXX\",\"Nm\":\"POSTFINANCE AG\",\"PstlAdr\":{\"AdrLine\":[\"MINGERSTRASSE 20\",\"3030 BERNE\"]}}}},\"RmtInf\":{\"Ustrd\":\"Kontouebertrag\",\"Strd\":{\"AddtlRmtInf\":[\"?REJECT?0\",\"?ERROR?000\"]}},\"RltdDts\":{\"AccptncDtTm\":\"2020-06-17T20:00:00\"}},{\"Refs\":{\"MsgId\":\"MsgId-001\",\"AcctSvcrRef\":\"0000000000000004\",\"PmtInfId\":\"PmtInfId-001-01\",\"InstrId\":\"InstrId-001-01-03\",\"EndToEndId\":\"EndToEndId-001-03-04\",\"Prtry\":{\"Tp\":\"00\",\"Ref\":\"00000000000000000000004\"}},\"Amt\":{\"Ccy\":\"CHF\",\"\":\"150.00\"},\"CdtDbtInd\":\"CRDT\",\"BkTxCd\":{\"Domn\":{\"Cd\":\"PMNT\",\"Fmly\":{\"Cd\":\"RCDT\",\"SubFmlyCd\":\"AUTT\"}}},\"RltdPties\":{\"Dbtr\":{\"Nm\":\"Bernasconi Maria\",\"PstlAdr\":{\"StrtNm\":\"Place de la Gare\",\"BldgNb\":\"12\",\"PstCd\":\"2502\",\"TwnNm\":\"Biel/Bienne\",\"Ctry\":\"CH\"}},\"DbtrAcct\":{\"Id\":{\"IBAN\":\"CH5109000000250092291\"}},\"CdtrAcct\":{\"Id\":{\"IBAN\":\"CH2909000000250094239\"}}},\"RltdAgts\":{\"DbtrAgt\":{\"FinInstnId\":{\"BICFI\":\"POFICHBEXXX\",\"Nm\":\"POSTFINANCE AG\",\"PstlAdr\":{\"AdrLine\":[\"MINGERSTRASSE 20\",\"3030 BERNE\"]}}}},\"RmtInf\":{\"Ustrd\":\"Rg.-Nr. 001\",\"Strd\":{\"AddtlRmtInf\":[\"?REJECT?0\",\"?ERROR?000\"]}},\"RltdDts\":{\"AccptncDtTm\":\"2020-06-17T20:00:00\"}},{\"Refs\":{\"MsgId\":\"MsgId-001\",\"AcctSvcrRef\":\"0000000000000005\",\"PmtInfId\":\"PmtInfId-001-01\",\"InstrId\":\"InstrId-001-01-01\",\"EndToEndId\":\"EndToEndId-001-03-05\",\"Prtry\":{\"Tp\":\"00\",\"Ref\":\"00000000000000000000005\"}},\"Amt\":{\"Ccy\":\"CHF\",\"\":\"50.00\"},\"CdtDbtInd\":\"CRDT\",\"BkTxCd\":{\"Domn\":{\"Cd\":\"PMNT\",\"Fmly\":{\"Cd\":\"RCDT\",\"SubFmlyCd\":\"AUTT\"}}},\"RltdPties\":{\"Dbtr\":{\"Nm\":\"Bernasconi Maria\",\"PstlAdr\":{\"StrtNm\":\"Place de la Gare\",\"BldgNb\":\"12\",\"PstCd\":\"2502\",\"TwnNm\":\"Biel/Bienne\",\"Ctry\":\"CH\"}},\"DbtrAcct\":{\"Id\":{\"IBAN\":\"CH5109000000250092291\"}},\"CdtrAcct\":{\"Id\":{\"IBAN\":\"CH2909000000250094239\"}}},\"RltdAgts\":{\"DbtrAgt\":{\"FinInstnId\":{\"BICFI\":\"POFICHBEXXX\",\"Nm\":\"POSTFINANCE AG\",\"PstlAdr\":{\"AdrLine\":[\"MINGERSTRASSE 20\",\"3030 BERNE\"]}}}},\"RmtInf\":{\"Ustrd\":\"Rg.-Nr. 1234567890\",\"Strd\":{\"AddtlRmtInf\":[\"?REJECT?0\",\"?ERROR?000\"]}},\"RltdDts\":{\"AccptncDtTm\":\"2020-06-17T20:00:00\"}},{\"Refs\":{\"MsgId\":\"MsgId-001\",\"AcctSvcrRef\":\"0000000000000006\",\"PmtInfId\":\"PmtInfId-001-01\",\"InstrId\":\"InstrId-001-01-02\",\"EndToEndId\":\"EndToEndId-001-03-06\",\"Prtry\":{\"Tp\":\"00\",\"Ref\":\"00000000000000000000006\"}},\"Amt\":{\"Ccy\":\"CHF\",\"\":\"100.00\"},\"CdtDbtInd\":\"CRDT\",\"BkTxCd\":{\"Domn\":{\"Cd\":\"PMNT\",\"Fmly\":{\"Cd\":\"RCDT\",\"SubFmlyCd\":\"AUTT\"}}},\"RltdPties\":{\"Dbtr\":{\"Nm\":\"Bernasconi Maria\",\"PstlAdr\":{\"StrtNm\":\"Place de la Gare\",\"BldgNb\":\"12\",\"PstCd\":\"2502\",\"TwnNm\":\"Biel/Bienne\",\"Ctry\":\"CH\"}},\"DbtrAcct\":{\"Id\":{\"IBAN\":\"CH5109000000250092291\"}},\"CdtrAcct\":{\"Id\":{\"IBAN\":\"CH2909000000250094239\"}}},\"RltdAgts\":{\"DbtrAgt\":{\"FinInstnId\":{\"BICFI\":\"POFICHBEXXX\",\"Nm\":\"POSTFINANCE AG\",\"PstlAdr\":{\"AdrLine\":[\"MINGERSTRASSE 20\",\"3030 BERNE\"]}}}},\"RmtInf\":{\"Ustrd\":\"Rg.-Nr. 987654321\",\"Strd\":{\"AddtlRmtInf\":[\"?REJECT?0\",\"?ERROR?000\"]}},\"RltdDts\":{\"AccptncDtTm\":\"2020-06-17T20:00:00\"}}]},\"AddtlNtryInf\":\"SAMMELGUTSCHRIFT FÜR KONTO: CH2909000000250094239 VERARBEITUNG VOM 17.06.2020 PAKET ID: 9999999999999999\"}}}}",
				target.get(ConversionParameter.TARGET_JSON.key()).asText());
		assertEquals("OK", target.get("result").asText());
		assertNull(target.get("errors"));
	}
}
