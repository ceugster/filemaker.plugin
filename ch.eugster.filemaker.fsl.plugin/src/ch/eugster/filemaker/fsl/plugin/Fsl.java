package ch.eugster.filemaker.fsl.plugin;

import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Fsl
{
	public String execute(String command, String json)
	{
		String result = null;
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode results = mapper.createObjectNode();
		ObjectNode source = null;
		if (Objects.isNull(json))
		{
			result = createErrorMessage(results, "Der Übergabeparameter 'json' muss vorhanden sein.");
		}
		else
		{
			try
			{
				source = ObjectNode.class.cast(mapper.readTree(json));
			}
			catch (Exception e)
			{
				result = createErrorMessage(results, "Der Übergabeparameter 'json' hat ein ungültiges Format.");
			}
		}
		Executor executor = ExecutorSelector.find(command);
		if (Objects.isNull(executor))
		{
			result = createErrorMessage(results,
					"Der Befehl '" + command + "' wird nicht unterstützt. Bitte überprüfen Sie den Befehlsparameter.");
		}
		else if (!Objects.isNull(source))
		{
			result = executor.execute(source, results);
		}
		if (Objects.isNull(results.get("errors")))
		{
			if (Objects.isNull(results.get("result")))
			{
				results.put("result", "OK");
			}
		}
		return result;
	}

	private String createErrorMessage(ObjectNode results, String message)
	{
		JsonNode result = results.get("result");
		if (Objects.isNull(result))
		{
			results.put("result", "Fehler");
		}
		else if (result.asText().equals("OK"))
		{
			results.put("result", "Fehler");
		}
		ArrayNode errors = ArrayNode.class.cast(results.get("errors"));
		if (Objects.isNull(errors))
		{
			errors = results.putArray("errors");
		}
		errors.add(message);
		return results.toPrettyString();
	}
}
