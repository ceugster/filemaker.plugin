package ch.eugster.filemaker.fsl.plugin.ui;

import ch.eugster.filemaker.fsl.plugin.Executor;

public class Ui extends Executor<Ui>
{
//	private static final ObjectMapper mapper = new ObjectMapper();
//
//	/**
//	 * Select file from system with file selector dialog
//	 * 
//	 * @param arguments Object[] arguments
//	 * 
//	 * @param String    of type json with optional entries:
//	 * 
//	 * @param String    "filename"
//	 * @param Array     "extensions"
//	 * @param Integer   "index" set index in extensions
//	 * @param Array     "types" names for extensions in same order as in extensions
//	 * @param String    "title" dialog title
//	 */
//	public static void selectFile(Object[] arguments)
//	{
//		if (Objects.nonNull(arguments) && String.class.isInstance(arguments[0]))
//		{
//			String content = String.class.cast(arguments[0]);
//			try
//			{
//				JsonNode properties = mapper.readTree(content);
//				Display display = null;
//				try
//				{
//					display = new Display();
//				}
//				catch (Exception e)
//				{
//					addErrorMessage(((InvocationTargetException) e).getTargetException().getClass().getName());
//				}
//				Shell shell = new Shell(display);
//				FileDialog dialog = new FileDialog(shell);
//				if (properties.has("filename"))
//				{
//					dialog.setFileName(properties.get("filename").asText());
//				}
//				if (properties.has("extensions"))
//				{
//					List<String> extensions = new ArrayList<String>();
//					Iterator<JsonNode> elements = properties.get("extensions").elements();
//					while (elements.hasNext())
//					{
//						JsonNode element = elements.next();
//						extensions.add(element.asText());
//					}
//					dialog.setFilterExtensions(extensions.toArray(new String[extensions.size()]));
//				}
//				if (properties.has("index"))
//				{
//					int index = properties.get("index").asInt();
//					dialog.setFilterIndex(index);
//				}
//				if (properties.has("types"))
//				{
//					List<String> types = new ArrayList<String>();
//					Iterator<JsonNode> elements = properties.get("types").elements();
//					while (elements.hasNext())
//					{
//						JsonNode element = elements.next();
//						types.add(element.asText());
//					}
//					dialog.setFilterNames(types.toArray(new String[types.size()]));
//				}
//				if (properties.has("title"))
//				{
//					dialog.setText(properties.get("title").asText());
//				}
//				String path = properties.has("path") ? properties.get("path").asText() : System.getProperty("user.home");
//				dialog.setFilterPath(path);
//				String filename = dialog.open();
//				resultNode.put("target", Objects.nonNull(filename) ? filename : "");
//				display.dispose();
//			}
//			catch (JsonMappingException e)
//			{
//				addErrorMessage(e.getLocalizedMessage());
//			}
//			catch (JsonProcessingException e)
//			{
//				addErrorMessage(e.getLocalizedMessage());
//			}
//		}
//	}
//
//	/**
//	 * Select file from system with file selector dialog
//	 * 
//	 * @param arguments Object[] arguments
//	 * 
//	 * @param String    of type json with optional entries:
//	 * 
//	 * @param String    "message"
//	 * @param String    "text"
//	 * @param String    "path" initial path
//	 */
//	public static void selectDirectory(Object[] arguments)
//	{
//		if (Objects.nonNull(arguments) && arguments.length > 0 && String.class.isInstance(arguments[0]))
//		{
//			String content = String.class.cast(arguments[0]);
//			try
//			{
//				JsonNode properties = mapper.readTree(content);
//
//				Display display = new Display();
//				Shell shell = new Shell(display);
//				DirectoryDialog dialog = new DirectoryDialog(shell);
//				if (properties.has("message"))
//				{
//					dialog.setMessage(properties.get("message").asText());
//				}
//				if (properties.has("text"))
//				{
//					dialog.setText(properties.get("text").asText());
//				}
//				String path = properties.has("path") ? properties.get("path").asText() : System.getProperty("user.home");
//				dialog.setFilterPath(path);
//				String filename = dialog.open();
//				if (Objects.nonNull(filename))
//				{
//					resultNode.put("target", filename);
//				}
//				display.dispose();
//			}
//			catch (JsonMappingException e)
//			{
//				addErrorMessage(e.getLocalizedMessage());
//			}
//			catch (JsonProcessingException e)
//			{
//				addErrorMessage(e.getLocalizedMessage());
//			}
//
//		}
//	}

}
