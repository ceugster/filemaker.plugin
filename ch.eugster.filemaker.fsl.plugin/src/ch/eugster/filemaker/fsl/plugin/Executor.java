package ch.eugster.filemaker.fsl.plugin;

import java.lang.reflect.Method;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class Executor<E extends Executor<E>>
{
	public static final String STATUS = "status";
	
	public static final String ERRORS = "errors";
	
	public static final String OK = "OK";

	public static final String ERROR = "Fehler";

	public static final String RESULT = "result";
	
	public void execute(String command, JsonNode requestNode, ObjectNode responseNode)
	{
		Method[] methods = this.getClass().getDeclaredMethods();
		for (Method method : methods)
		{
			if (method.getModifiers() == (1 | 8))
			{
				if (method.getName().equals(command))
				{
					try
					{
						method.invoke(null, requestNode, responseNode);
					}
					catch (Exception e)
					{
						Fsl.addErrorMessage(Objects.isNull(e.getLocalizedMessage()) ? e.getClass().getName() : e.getLocalizedMessage());
					}
					return;
				}
			}
		}
		Fsl.addErrorMessage("invalid command '" + command + "'");

	}
}
