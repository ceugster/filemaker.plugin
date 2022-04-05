package ch.eugster.filemaker.fsl.plugin.camt;

import java.util.ArrayList;
import java.util.List;

public class CamtCreditor
{
	private String iban;

	private IdType type;

	private String name;

	private List<CamtDebtor> debtors = new ArrayList<CamtDebtor>();

	enum IdType
	{
		IBAN, OTHER;
	}
}
