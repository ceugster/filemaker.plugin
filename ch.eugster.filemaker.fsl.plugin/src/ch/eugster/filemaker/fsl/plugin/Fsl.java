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
		ObjectNode params = mapper.createObjectNode();
		Executor executor = ExecutorSelector.find(command);
		if (Objects.isNull(executor))
		{
			result = createErrorMessage(params,
					"Der Befehl '" + command + "' wird nicht unterstützt. Bitte überprüfen Sie den Befehlsparameter.");
		}
		else
		{
			try
			{
				result = executor.execute(json, params);
			}
			catch (Exception e)
			{
				result = createErrorMessage(params, "Der Übergabeparameter 'json' hat ein ungültiges Format.");
			}
		}
		return result;
	}

	private String createErrorMessage(ObjectNode result, String message)
	{
		JsonNode resultNode = result.get("result");
		if (Objects.isNull(resultNode))
		{
			result.put("result", "Fehler");
		}
		else if (resultNode.asText().equals("OK"))
		{
			result.put("result", "Fehler");
		}
		ArrayNode errors = ArrayNode.class.cast(result.get("errors"));
		if (Objects.isNull(errors))
		{
			errors = result.putArray("errors");
		}
		errors.add(message);
		return result.toPrettyString();
	}
}
