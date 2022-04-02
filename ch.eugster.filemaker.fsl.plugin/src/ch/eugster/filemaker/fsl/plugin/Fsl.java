package ch.eugster.filemaker.fsl.plugin;

import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Fsl 
{
	public String execute(String command, String json)
	{
		String result = null;
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode target = mapper.createObjectNode();
		ObjectNode node = null;
		if (Objects.isNull(json))
		{
			result = createErrorMessage(target, "Der Übergabeparameter 'json' muss vorhanden sein.");
		}
		else
		{
			try
			{
				node = ObjectNode.class.cast(mapper.readTree(json));
			}
			catch (Exception e)
			{
				result = createErrorMessage(target, "Der Übergabeparameter 'json' hat ein ungültiges Format.");
			}
		}
		Executor executor = ExecutorSelector.find(command);
		if (Objects.isNull(executor))
		{
			result = createErrorMessage(target, "Der Befehl '" + command + "' wird nicht unterstützt. Bitte überprüfen Sie den Befehlsparameter.");
		}
		else if (!Objects.isNull(node))
		{
			result = executor.execute(node);
		}
		return result;
	}
	
	private String createErrorMessage(ObjectNode root, String message)
	{
		if (Objects.isNull(root.get("result")))
		{
			root.put("result", "Fehler");
		}
		ArrayNode errors = ArrayNode.class.cast(root.get("errors"));
		if (Objects.isNull(errors))
		{
			errors = root.putArray("errors");
		}
		errors.add(message);
		return root.toPrettyString();
	}
}
