package ch.eugster.filemaker.fsl.plugin;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class Executor
{
	public static final String STATUS = "status";
	
	public static final String ERRORS = "errors";
	
	public static final String OK = "OK";

	public static final String ERROR = "Fehler";

	public static final String RESULT = "result";
	
	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	public ObjectNode execute(String command, ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean found = false;
		Method[] methods = this.getClass().getDeclaredMethods();
		for (Method method : methods)
		{
			if (method.getModifiers() == Modifier.PUBLIC)
			{
				if (method.getName().equals(command))
				{
					Class<?>[] parameters = method.getParameterTypes();
					if (parameters.length == 2 && parameters[0].equals(ObjectNode.class) && parameters[1].equals(ObjectNode.class))
					{
						found = true;
						try
						{
							method.invoke(this, requestNode, responseNode);
						}
						catch (Exception e)
						{
							addErrorMessage(responseNode, Objects.isNull(e.getLocalizedMessage()) ? e.getClass().getName() : e.getLocalizedMessage());
						}
						break;
					}
				}
			}
		}
		if (!found)
		{
			addErrorMessage(responseNode, "invalid command '" + command + "'");
		}
		responseNode.put(Executor.STATUS, responseNode.has(Executor.ERRORS) ? Executor.ERROR : Executor.OK);
		return responseNode;
	}
	
	public boolean addErrorMessage(ObjectNode responseNode, String message)
	{
		ArrayNode errors = ArrayNode.class.cast(responseNode.get(Executor.ERRORS));
		if (Objects.isNull(errors))
		{
			errors = responseNode.arrayNode();
			responseNode.set(Executor.ERRORS, errors);
		}
		if (errors.findValuesAsText(message).size() == 0)
		{
			errors.add(message);
		}
		return false;
	}
	

}
