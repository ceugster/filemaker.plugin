package ch.eugster.filemaker.fsl.plugin;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.logging.LogManager;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

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
	
	private static boolean doLog = false;
	
	public static String execute(String command, String parameters)
	{
		initializeLogging();
		
		Fsl.RESPONSE_NODE = MAPPER.createObjectNode();
		if (Objects.nonNull(command) && !command.trim().isEmpty())
		{
			String[] commands = command.split("[.]");
			if (commands.length == 2)
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
								addErrorMessage("invalid module '" + commands[0] + "'");
							}
						}
					}
					executor = EXECUTORS.get(commands[0]);
				}
				if (Objects.isNull(executor))
				{
					addErrorMessage("missing module '" + commands[0] + "'");
				}
				else
				{
					if (Objects.isNull(parameters))
					{
						parameters = "{}";
					}
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
								Fsl.addErrorMessage("invalid command '" + commands[1] + "' (not found)");
							}
						}
						else
						{
							Fsl.addErrorMessage("invalid json parameter (must be a valid json string)");
						}
					}
					catch (Exception e)
					{
						Fsl.addErrorMessage("invalid json parameter (must be a valid json string)");
					}
				}
			}
			else
			{
				addErrorMessage("invalid command");
			}
		}
		else
		{
			Fsl.addErrorMessage("missing command");
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
	
	public static void log(Level level, String message)
	{
		logger.atLevel(level).log(message);
	}
	
	private static void initializeLogging()
	{
		Path cfgPath = Paths.get(System.getProperty("user.home"), ".fsl", "fsl-log.cfg");
		Path logPath = Paths.get(System.getProperty("user.home"), ".fsl", "fsl.log");
		System.setProperty("java.util.logging.config.file", cfgPath.toString());
		doLog = logPath.getParent().toFile().exists();
		if (!doLog)
		{
			try
			{
				doLog = logPath.getParent().toFile().mkdirs();
			} 
			catch (Exception e)
			{
				doLog = false;
			}
		}
		if (doLog)
		{
			if (!cfgPath.toFile().exists())
			{
				Properties properties = new Properties();
				properties.setProperty("handlers", "java.util.logging.FileHandler, java.util.logging.ConsoleHandler");
				properties.setProperty("java.util.logging.FileHandler.pattern", "%h/.fsl/fsl.log");
				properties.setProperty("java.util.logging.FileHandler.formatter", "java.util.logging.SimpleFormatter");
				properties.setProperty("java.util.logging.FileHandler.level", "INFO");
				properties.setProperty("java.util.logging.ConsoleHandler.level", "INFO");
				OutputStream os = null;
				try
				{
					os = new FileOutputStream(cfgPath.toFile());
					properties.store(os, "Please do not change the content of this file");
				}
				catch (Exception e)
				{
					doLog = false;
				}
				finally
				{
					if (Objects.nonNull(os)) 
					{
						try
						{
							os.close();
						}
						catch (Exception e)
						{
						}
					}
				}
			}
			if (doLog)
			{
				try
				{
					InputStream is = new FileInputStream(cfgPath.toFile());
					LogManager.getLogManager().readConfiguration(is);
				}
				catch (Exception e)
				{
					doLog = false;
				}
			}
		}
	}
}
