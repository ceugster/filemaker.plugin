package ch.eugster.filemaker.fsl.plugin;

import java.util.Objects;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface Executor
{
	String execute(String json, ObjectNode result);

	default boolean addError(ObjectNode result, String message)
	{
		if (Objects.isNull(result.get("result")))
		{
			result = result.put("result", "Fehler");
		}
		else if (result.get("result").asText().equals("OK"))
		{
			result = result.put("result", "Fehler");
		}
		if (Objects.isNull(result.get("errors")))
		{
			result.putArray("errors");
		}
		((ArrayNode) result.get("errors")).add(message);
		return false;
	}

	default boolean addError(ObjectNode source, Exception e)
	{
		return addError(source, e.getLocalizedMessage());
	}

}
