package ch.eugster.filemaker.fsl.plugin;

import java.lang.reflect.Method;
import java.util.Objects;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class Executor<E extends Executor<E>>
{
	protected static final String OK = "OK";

	protected static final String ERROR = "Fehler";

	protected static ObjectNode resultNode;

	public void execute(String command, ObjectNode results, Object[] parameters)
	{
		Executor.resultNode = results;
		Method[] methods = this.getClass().getDeclaredMethods();
		for (Method method : methods)
		{
			if (method.getName().equals(command))
			{
				try
				{
					Object[] params = parameters;
					if (method.getParameterCount() > 0)
					{
						if (method.getParameterTypes()[0].isArray())
						{
							params = new Object[] { parameters };
						}
					}
					else if (method.getParameterCount() > 1)
					{
						params = new Object[] { parameters };
					}
					method.invoke(null, params);
					if (Objects.isNull(resultNode.get("errors")))
					{
						resultNode.put("result", "OK");
					}
				}
				catch (Exception e)
				{
					addErrorMessage(e);
				}
				return;
			}
		}
		addErrorMessage("Unbekannter Befehl");

	}

	/**
	 * 
	 * @param key   key
	 * @param value value
	 */
	protected static void putValue(String key, String value)
	{
		resultNode.put(key, value);
	}

	/**
	 * 
	 * @param key   key
	 * @param value value
	 */
	protected static void putValue(String key, Double value)
	{
		resultNode.put(key, value);
	}

	/**
	 * 
	 * @param key   key
	 * @param value value
	 */
	protected static void putValue(String key, Integer value)
	{
		resultNode.put(key, value);
	}

	/**
	 * 
	 * @param message error message
	 * 
	 * @return "Fehler"
	 */
	protected static String addErrorMessage(String message)
	{
		ArrayNode errors = ArrayNode.class.cast(resultNode.get("errors"));
		if (Objects.isNull(errors))
		{
			errors = resultNode.putArray("errors");
		}
		errors.add(message);
		if (Objects.isNull(resultNode.get("result")) || resultNode.get("result").asText().equals("OK"))
		{
			resultNode.put("result", "Fehler");
		}
		return ERROR;
	}

	public static String addErrorMessage(Exception e)
	{
		return addErrorMessage(e.getLocalizedMessage());
	}
}
