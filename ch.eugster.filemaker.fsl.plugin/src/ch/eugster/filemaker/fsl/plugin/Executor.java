package ch.eugster.filemaker.fsl.plugin;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface Executor
{
	String execute(ObjectNode source, ObjectNode result);

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

	default ObjectNode loadProperties(ObjectNode source) throws Exception
	{
		String path = System.getProperty("user.home") + File.separator + ".fsl/qrbill.json";
		ObjectMapper mapper = new ObjectMapper();
		try
		{
			if (Objects.nonNull(source.get("test")))
			{
				if (Objects.nonNull(source.get("test").get("properties"))
						&& source.get("test").get("properties").isTextual())
				{
					path = source.get("test").get("properties").asText();
				}
			}
			JsonNode properties = mapper.readTree(new File(path));
			this.merge(source, ObjectNode.class.cast(properties));
			System.out.println(properties.toPrettyString());
			return ObjectNode.class.cast(properties);
		}
		catch (JsonParseException e)
		{
			throw new Exception("Die Datei 'qrbill.json' kann nicht validiert werden (ungültiges Format).");
		}
		catch (IOException e)
		{
			return source;
		}
	}

	default void merge(ObjectNode source, ObjectNode target)
	{
		Iterator<String> fieldNames = source.fieldNames();
		while (fieldNames.hasNext())
		{
			String fieldName = fieldNames.next();
			ObjectNode targetNode = ObjectNode.class.cast(target);
			JsonNode targetField = target.get(fieldName);
			if (Objects.isNull(targetField))
			{
				JsonNode fieldNode = source.get(fieldName).deepCopy();
				targetNode.set(fieldName, fieldNode);
			}
			else if (targetField.isValueNode())
			{
				JsonNode fieldNode = source.get(fieldName);
				targetNode.set(fieldName, fieldNode);
			}
			else if (targetField.isObject())
			{
				JsonNode sourceFieldNode = source.get(fieldName);
				if (sourceFieldNode.isObject())
				{
					this.merge(ObjectNode.class.cast(sourceFieldNode), ObjectNode.class.cast(targetField));
				}
			}
		}
	}
}
