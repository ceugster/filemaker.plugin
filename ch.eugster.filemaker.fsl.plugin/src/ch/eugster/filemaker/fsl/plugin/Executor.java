package ch.eugster.filemaker.fsl.plugin;

import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface Executor
{
	String execute(ObjectNode source, ObjectNode target);

	default boolean createErrorMessage(ObjectNode target, String message)
	{
		JsonNode result = target.get("result");
		if (Objects.isNull(result))
		{
			result = target.put("result", "Fehler");
		}
		else if (result.asText().equals("OK"))
		{
			target.put("result", "Fehler");
		}
		JsonNode node = target.get("errors");
		ArrayNode errors = null;
		if (Objects.isNull(node))
		{
			errors = target.putArray("errors");
		}
		else
		{
			errors = ArrayNode.class.cast(node);
		}
		errors.add(message);
		return false;
	}

	default boolean createErrorMessage(ObjectNode target, Exception e)
	{
		return createErrorMessage(target, e.getLocalizedMessage());
	}

}
