package ch.eugster.filemaker.fsl.plugin;

import ch.eugster.filemaker.fsl.plugin.converter.XmlToJsonConverter;
import ch.eugster.filemaker.fsl.plugin.swissqrbill.SwissQRBillGenerator;

public enum ExecutorSelector
{
	// @formatter:off
	CREATE_QRBILL("CreateQRBill", new SwissQRBillGenerator()), 
	CONVERT_XML_TO_JSON("ConvertXmlToJson", new XmlToJsonConverter());
	// @formatter:on

	private String command;

	private Executor executor;

	private ExecutorSelector(String command, Executor executor)
	{
		this.command = command;
		this.executor = executor;
	}

	public static final Executor find(String command)
	{
		ExecutorSelector[] selectors = ExecutorSelector.values();
		for (ExecutorSelector selector : selectors)
		{
			if (selector.command.equals(command))
			{
				return selector.executor;
			}
		}
		return null;
	}

	public String command()
	{
		return this.command;
	}
}
