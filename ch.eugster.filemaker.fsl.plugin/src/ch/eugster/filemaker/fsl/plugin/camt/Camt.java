package ch.eugster.filemaker.fsl.plugin.camt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.prowidesoftware.swift.model.mx.AbstractMX;

import ch.eugster.filemaker.fsl.plugin.Executor;
import ch.eugster.filemaker.fsl.plugin.Fsl;

public class Camt extends Executor<Camt>
{
	public static void parse(JsonNode requestNode, ObjectNode responseNode)
	{
		JsonNode source = requestNode.get(Parameter.XML_FILE.key());
		if (Objects.nonNull(source) && source.isTextual())
		{
			parseFile(requestNode, responseNode);
		}
		else
		{
			source = requestNode.get(Parameter.XML_CONTENT.key());
			if (Objects.nonNull(source) && source.isTextual())
			{
				parseContent(requestNode, responseNode);
			}
			else
			{
				Fsl.addErrorMessage("missing_argument 'xml_file' or 'xml_content'");
			}
		}
	}

	public static void parseFile(JsonNode requestNode, ObjectNode responseNode)
	{
		JsonNode source = requestNode.get(Parameter.XML_FILE.key());
		if (Objects.nonNull(source) && source.isTextual())
		{
			File file = new File(String.class.cast(source.asText()));
			if (file.isFile())
			{
				try
				{
					StringBuilder builder = new StringBuilder();
					Reader r = new FileReader(file);
					BufferedReader br = new BufferedReader(r);
					String line = br.readLine();
					while (line != null)
					{
						builder.append(line);
						line = br.readLine();
					}
					br.close();
					responseNode.put(Executor.RESULT, jsonify(builder.toString()));
				}
				catch (Exception e)
				{
					Fsl.addErrorMessage("missing_file '" + Parameter.XML_FILE.key() + "'");
				}
			}
			else
			{
				Fsl.addErrorMessage("invalide_file_parameter '" + Parameter.XML_FILE.key() + "'");
			}
		}
		else
		{
			Fsl.addErrorMessage("wrong_parameter_type '" + Parameter.XML_FILE.key() + "'");
		}
	}
	
	public static void parseContent(JsonNode requestNode, ObjectNode responseNode)
	{
		JsonNode source = requestNode.get(Parameter.XML_CONTENT.key());
		if (Objects.nonNull(source) && source.isTextual())
		{
			try
			{
				responseNode.put(Executor.RESULT, jsonify(source.asText()));
			}
			catch (Exception e)
			{
				Fsl.addErrorMessage("wrong_parameter_type. 'xml_file_path_name'");
			}
		}
		else
		{
			Fsl.addErrorMessage("wrong_parameter_type. 'xml_file_path_name'");
		}
	}
	
	public static void extract(JsonNode requestNode, ObjectNode responseNode) throws JsonMappingException, JsonProcessingException
	{
		Camt.parse(requestNode, responseNode);
		if (!Objects.isNull(responseNode.get(Executor.RESULT)))
		{
			String content = responseNode.get(Executor.RESULT).asText();
			ObjectMapper mapper = new ObjectMapper();
			JsonNode json = mapper.readTree(content);
			Iterator<JsonNode> ntfctns = json.get("bkToCstmrDbtCdtNtfctn").get("ntfctn").elements();
			while (ntfctns.hasNext())
			{
				JsonNode ntfctn = ntfctns.next();
				System.out.println(ntfctn);
				Iterator<JsonNode> ntries = ntfctn.get("ntry").elements();
				while (ntries.hasNext())
				{
					JsonNode ntry = ntries.next();
					System.out.println(ntry);
					Iterator<JsonNode> ntryDtls = ntry.get("ntryDtls").elements();
					while (ntryDtls.hasNext())
					{
						JsonNode ntryDtl = ntryDtls.next();
						System.out.println(ntryDtl);
						Iterator<JsonNode> txDtls = ntryDtl.get("txDtls").elements();
						while (txDtls.hasNext())
						{
							JsonNode txDtl = txDtls.next();
							System.out.println(txDtl);
							Iterator<JsonNode> prtries = txDtl.get("refs").get("prtry").elements();
							while (prtries.hasNext())
							{
								JsonNode prtry = prtries.next();
								System.out.println(prtry);

							}
							Iterator<JsonNode> strds = txDtl.get("rmtInf").get("strd").elements();
							while (strds.hasNext())
							{
								JsonNode strd = strds.next();
								System.out.println(strd);

							}
						}
					}
				}
			}
		}
	}

	private static String jsonify(String content)
	{
		AbstractMX mx = AbstractMX.parse(content);
		if (Objects.isNull(mx))
		{
			Fsl.addErrorMessage("Ungültiges Format.");
		}
		else
		{
			return mx.toJson();
		}
		return null;
	}
	
	public enum Parameter
	{
		XML_FILE("xml_file"), XML_CONTENT("xml_content");
		
		private String key;
		
		private Parameter(String key)
		{
			this.key = key;
		}
		
		public String key()
		{
			return this.key;
		}
	}
}
