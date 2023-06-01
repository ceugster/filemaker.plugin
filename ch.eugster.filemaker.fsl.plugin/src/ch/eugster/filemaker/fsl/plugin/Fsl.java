package ch.eugster.filemaker.fsl.plugin;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.LogManager;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Fsl
{
	private static Map<String, Executor> executors = new HashMap<String, Executor>();

	private static ObjectMapper mapper = new ObjectMapper();

	private static Reflections reflections = new Reflections("ch.eugster.filemaker.fsl.plugin");

	private static Logger logger = LoggerFactory.getLogger(Fsl.class);
	
	private static boolean doLog = false;
	
	public static String execute(String command, String parameters)
	{
		initializeLogging();

		logger.info("Build argument node");
		ObjectNode requestNode = null;
		ObjectNode responseNode = mapper.createObjectNode();
		try
		{
			requestNode = ObjectNode.class.cast(mapper.readTree(parameters));
			if (Objects.nonNull(command) && !command.trim().isEmpty())
			{
				String[] commandParts = command.split("[.]");
				if (commandParts.length == 2)
				{
					logger.info("Get or create executor '{}'", commandParts[0]);
					Executor executor = getExecutor(commandParts[0].trim());
					if (Objects.nonNull(executor))
					{
						logger.info("Execute '" + commandParts[1] + "'");
						executor.execute(commandParts[1].trim(), requestNode, responseNode);
					}
					else
					{
						logger.error("Invalid module '" + commandParts[0] + "'");
						addErrorMessage(responseNode, "invalid module '" + commandParts[0] + "'");
					}
				}
				else
				{
					logger.error("invalid command '{}'", command);
					addErrorMessage(responseNode, "invalid command '" + command + "'");
				}
			}
			else
			{
				logger.error("Missing command");
				addErrorMessage(responseNode, "missing command");
			}
		}
		catch (Exception e)
		{
			logger.error("An error occurred while building the argument node ({})", e.getLocalizedMessage());
			addErrorMessage(responseNode, "invalid argument '" + parameters + "'");
		}

		return responseNode.put(Executor.STATUS, Objects.isNull(responseNode.get(Executor.ERRORS)) ? Executor.OK : Executor.ERROR).toString();
	}
	
	public static boolean addErrorMessage(ObjectNode responseNode, String message)
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
	
	public static void log(Level level, String message)
	{
		logger.atLevel(level).log(message);
	}
	
	public static Executor getExecutor(String executorName)
	{
		Executor executor = null;
		executor = executors.get(executorName);
		if (Objects.isNull(executor))
		{
			try
			{
				Iterator<Class<? extends Executor>> clazzes = reflections.getSubTypesOf(Executor.class).iterator();
				while (clazzes.hasNext())
				{
					Class<? extends Executor> clazz = clazzes.next();
					if (clazz.getSimpleName().equals(executorName))
					{
						executor = clazz.getConstructor().newInstance();
						executors.put(executorName, executor);
						break;
					}
				}
			}
			catch (Exception e)
			{
			}
		}
		return executor;
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
