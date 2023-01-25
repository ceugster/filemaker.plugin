package ch.eugster.filemaker.fsl.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

public class Fsl<E extends Executor<?>>
{
	private static Map<String, Executor<?>> executors = new HashMap<String, Executor<?>>();

	private static Reflections reflections = new Reflections(
			new ConfigurationBuilder().forPackage("ch.eugster.filemaker.fsl.plugin").addScanners(Scanners.SubTypes));

	public static String execute(String command, Object... parameters)
	{
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode results = mapper.createObjectNode();
		results.put("result", "OK");
		String[] commands = command.split("[.]");
		if (commands.length > 1)
		{
			Executor<?> executor = executors.get(commands[0]);
			if (Objects.isNull(executor))
			{
				@SuppressWarnings("rawtypes")
				Set<Class<? extends Executor>> classes = reflections.getSubTypesOf(Executor.class);
				for (@SuppressWarnings("rawtypes")
				Class<? extends Executor> clazz : classes)
				{
					if (clazz.getSimpleName().equals(commands[0]))
					{
						try
						{
							executors.put(clazz.getSimpleName(), clazz.getConstructor().newInstance());
						}
						catch (Exception e)
						{
							addErrorMessage(results, e.getLocalizedMessage());
						}
					}
				}
				executor = executors.get(commands[0]);
			}
			if (Objects.isNull(executor))
			{
				addErrorMessage(results, commands[0] + " ist kein gültiges Modul.");
			}
			else
			{
				try
				{
					executor.execute(commands[1], results, parameters);
				}
				catch (Exception e)
				{
					addErrorMessage(results, e.getLocalizedMessage());
				}
			}
		}
		else
		{
			addErrorMessage(results, "Der Befehl ist ungültig.");
		}
		return results.toString();
	}

	private static void addErrorMessage(ObjectNode results, String message)
	{
		ArrayNode errors = ArrayNode.class.cast(results.get("errors"));
		if (Objects.isNull(errors))
		{
			errors = results.putArray("errors");
		}
		errors.add(message);
		if (Objects.isNull(results.get("result")) || results.get("result").asText().equals("OK"))
		{
			results.put("result", "Fehler");
		}
	}
}
