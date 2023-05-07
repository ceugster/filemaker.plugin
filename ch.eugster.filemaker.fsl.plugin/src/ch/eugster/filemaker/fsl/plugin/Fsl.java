package ch.eugster.filemaker.fsl.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Fsl<E extends Executor<?>>
{
	private static ObjectNode REQUEST_NODE;
	
	private static ObjectNode RESPONSE_NODE;
	
	private static Map<String, Executor<?>> EXECUTORS = new HashMap<String, Executor<?>>();

	private static ObjectMapper MAPPER = new ObjectMapper();

	private static Reflections REFLECTIONS = new Reflections(
			new ConfigurationBuilder().forPackage("ch.eugster.filemaker.fsl.plugin").addScanners(Scanners.SubTypes));

	private static Logger logger = LoggerFactory.getLogger(Fsl.class);
	
	public static String execute(String command, String parameters)
	{
		Fsl.RESPONSE_NODE = MAPPER.createObjectNode();
		if (Objects.nonNull(command))
		{
			if (!command.trim().isEmpty())
			{
				if (command.contains("."))
				{
					String[] commands = command.split("[.]");
					if (commands.length > 1)
					{
						Executor<?> executor = EXECUTORS.get(commands[0]);
						if (Objects.isNull(executor))
						{
							@SuppressWarnings("rawtypes")
							Set<Class<? extends Executor>> classes = REFLECTIONS.getSubTypesOf(Executor.class);
							for (@SuppressWarnings("rawtypes")
							Class<? extends Executor> clazz : classes)
							{
								if (clazz.getSimpleName().equals(commands[0]))
								{
									try
									{
										EXECUTORS.put(clazz.getSimpleName(), clazz.getConstructor().newInstance());
									}
									catch (Exception e)
									{
										addErrorMessage("invalid_module '" + commands[0] + "'");
									}
								}
							}
							executor = EXECUTORS.get(commands[0]);
						}
						if (Objects.isNull(executor))
						{
							addErrorMessage("missing_module '" + commands[0] + "'");
						}
						else
						{
							if (Objects.nonNull(parameters))
							{
								try
								{
									JsonNode node = MAPPER.readTree(parameters);
									if (ObjectNode.class.isInstance(node))
									{
										Fsl.REQUEST_NODE = ObjectNode.class.cast(node);
										try
										{
											executor.execute(commands[1], REQUEST_NODE, RESPONSE_NODE);
										}
										catch (Exception e)
										{
											Fsl.addErrorMessage("invalid_command '" + commands[1] + "'");
										}
									}
									else
									{
										Fsl.addErrorMessage("invalid_argument 'json'");
									}
								}
								catch (Exception e)
								{
									Fsl.addErrorMessage("invalid_argument 'json'");
								}
							}
							else
							{
								Fsl.addErrorMessage("missing_argument 'json'");
							}
						}
					}
					else
					{
						addErrorMessage("missing_command");
					}
					
				}
				else
				{
					Fsl.addErrorMessage("missing_argument 'json'");
				}
			}
			else
			{
				Fsl.addErrorMessage("missing_command");
			}
			
		}
		else
		{
			Fsl.addErrorMessage("missing_module");
		}
		return RESPONSE_NODE.put(Executor.STATUS, Objects.isNull(RESPONSE_NODE.get(Executor.ERRORS)) ? Executor.OK : Executor.ERROR).toString();
	}
	
	public static boolean hasErrorMessages()
	{
		return Objects.isNull(Fsl.RESPONSE_NODE.get(Executor.ERRORS));
	}
	
	public static boolean addErrorMessage(String message)
	{
		ArrayNode errors = ArrayNode.class.cast(Fsl.RESPONSE_NODE.get(Executor.ERRORS));
		if (Objects.isNull(errors))
		{
			errors = Fsl.RESPONSE_NODE.arrayNode();
			Fsl.RESPONSE_NODE.set(Executor.ERRORS, errors);
		}
		if (errors.findValuesAsText(message).size() == 0)
		{
			errors.add(message);
		}
		return false;
	}
}
