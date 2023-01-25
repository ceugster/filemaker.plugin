package ch.eugster.filemaker.fsl.plugin.camt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prowidesoftware.swift.model.mx.AbstractMX;

import ch.eugster.filemaker.fsl.plugin.Executor;

public class Camt extends Executor<Camt>
{
	public static void parse(Object[] arguments)
	{
		if (arguments.length > 0)
		{
			if (String.class.isInstance(arguments[0]))
			{
				String argument = (String) arguments[0];
				try
				{
					Path path = Paths.get(argument);
					File file = path.toFile();
					if (file.exists())
					{
						if (file.isFile())
						{
							try
							{
								StringBuilder builder = new StringBuilder();
								Reader r = new FileReader(argument);
								BufferedReader br = new BufferedReader(r);
								String line = br.readLine();
								while (line != null)
								{
									builder.append(line);
									line = br.readLine();
								}
								br.close();
								jsonify(builder.toString());
							}
							catch (FileNotFoundException e)
							{
								addErrorMessage("Die Datei '" + new File(argument).getName() + "' kann nicht gefunden werden.");
							}
							catch (IOException e)
							{
								addErrorMessage("Beim Lesen der Datei '" + new File(argument).getName() + "' ist ein Fehler aufgetreten.");
							}
						}
						else
						{
							jsonify(argument);
						}
					}
					else
					{
						jsonify(argument);
					}
				}
				catch (InvalidPathException e)
				{
					jsonify(argument);
				}
			}
			else
			{
				addErrorMessage("Falscher Parametertyp (Erwartet: " + String.class.getName() + ", übergeben: " + (Objects.isNull(arguments[0]) ? "null" : arguments[0].getClass().getName()) + ").");
			}
		}
		else
		{
			addErrorMessage("Falsche Anzahl Parameter (Erwartet: 1, übergeben: " + arguments.length + ").");
		}
	}

	public static void extract(Object[] arguments) throws JsonMappingException, JsonProcessingException
	{
		parse(arguments);
		if (!Objects.isNull(resultNode.get("target")))
		{
			String content = resultNode.get("target").asText();
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

	private static void jsonify(String content)
	{
		AbstractMX mx = AbstractMX.parse(content);
		if (Objects.isNull(mx))
		{
			addErrorMessage("Ungültiges Format.");
		}
		else
		{
			resultNode.put("target", mx.toJson());
		}
	}
}
