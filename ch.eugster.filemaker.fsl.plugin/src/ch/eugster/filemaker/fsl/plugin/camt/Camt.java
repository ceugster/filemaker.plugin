package ch.eugster.filemaker.fsl.plugin.camt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.prowidesoftware.swift.model.mx.AbstractMX;

import ch.eugster.filemaker.fsl.plugin.Executor;
import ch.eugster.filemaker.fsl.plugin.Fsl;

public class Camt extends Executor<Camt>
{
	protected static final String IDENTIFIER_KEY = "identifier";
	
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
				Fsl.addErrorMessage("missing argument 'xml_file' or 'xml_content'");
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
					String[] result = jsonify(builder.toString());
					responseNode.put(IDENTIFIER_KEY, result[0]);
					responseNode.put(Executor.RESULT, result[1]);
				}
				catch (Exception e)
				{
					Fsl.addErrorMessage("missing file '" + Parameter.XML_FILE.key() + "'");
				}
			}
			else
			{
				Fsl.addErrorMessage("invalid argument '" + Parameter.XML_FILE.key() + "'");
			}
		}
		else
		{
			Fsl.addErrorMessage("invalid argument '" + Parameter.XML_FILE.key() + "'");
		}
	}
	
	public static void parseContent(JsonNode requestNode, ObjectNode responseNode)
	{
		JsonNode source = requestNode.get(Parameter.XML_CONTENT.key());
		if (Objects.nonNull(source) && source.isTextual())
		{
			try
			{
				String[] result = jsonify(source.asText());
				responseNode.put(IDENTIFIER_KEY, result[0]);
				responseNode.put(Executor.RESULT, result[1]);
			}
			catch (Exception e)
			{
				Fsl.addErrorMessage("wrong_parameter_type 'xml_file_path_name'");
			}
		}
		else
		{
			Fsl.addErrorMessage("wrong_parameter_type 'xml_file_path_name'");
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
	
	public static void extractTags(ObjectNode requestNode, ObjectNode responseNode)
	{
		JsonNode pathNode = requestNode.get(Camt.Parameter.XML_FILE.key());
		if (TextNode.class.isInstance(pathNode))
		{
			String path = pathNode.asText();
	        try { 
	            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder builder = factory.newDocumentBuilder();
	            Document doc = builder.parse (new File(path)); 
	            NodeList list = doc.getElementsByTagName("xs:element"); 
	            for(int i = 0 ; i < list.getLength(); i++)
	            {
	                Node first = (Node)list.item(i);
	                System.out.println(first);
//	                if(first.)
//	                {
//	                    String nm = first.getAttribute("name"); 
//	                    System.out.println(nm); 
//	                    String nm1 = first.getAttribute("type"); 
//	                    System.out.println(nm1); 
//	                }
	            }
	        } 
	        catch (ParserConfigurationException e) 
	        {
	            e.printStackTrace();
	        }
	        catch (SAXException e) 
	        { 
	            e.printStackTrace();
	        }
	        catch (IOException ed) 
	        {
	            ed.printStackTrace();
	        }
		}
	}

	private static String[] jsonify(String content)
	{
		String[] result = null;
		AbstractMX mx = AbstractMX.parse(content);
		if (Objects.nonNull(mx))
		{
			result = new String[2];
			result[0] = mx.getNamespace().substring(mx.getNamespace().lastIndexOf(":") + 1);
			result[1] = mx.toJson();
		}
		else
		{
			Fsl.addErrorMessage("invalid format '" + content + "'");
		}
		return result;
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
	
	public enum Identifier
	{
		
	}
}
