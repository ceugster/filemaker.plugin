package ch.eugster.filemaker.fsl.plugin;

import ch.eugster.filemaker.fsl.plugin.camt.Camt;
import ch.eugster.filemaker.fsl.plugin.converter.Converter;
import ch.eugster.filemaker.fsl.plugin.swissqrbill.QRBill;
import ch.eugster.filemaker.fsl.plugin.ui.Ui;
import ch.eugster.filemaker.fsl.plugin.xls.Xls;

public enum ExecutorSelector
{
	// @formatter:off
	CAMT(Camt.class.getSimpleName(), new Camt()),
	CONVERT(Converter.class.getSimpleName(), new Converter()),
	QRBILL(QRBill.class.getSimpleName(), new QRBill()), 
	UI(Ui.class.getSimpleName(), new Ui()), 
	XLS(Xls.class.getSimpleName(), new Xls());
	// @formatter:on

	private String executorName;

	private Executor<? extends Executor<?>> executor;

	private ExecutorSelector(String name, Executor<?> executor)
	{
		this.executorName = name;
		this.executor = executor;
	}

	public static final Executor<?> find(String executorName)
	{
		ExecutorSelector[] selectors = ExecutorSelector.values();
		for (ExecutorSelector selector : selectors)
		{
			if (selector.executorName.equals(executorName))
			{
				return selector.executor;
			}
		}
		return null;
	}

	public String executorName()
	{
		return this.executorName;
	}
}
