package ch.eugster.filemaker.fsl.plugin.swissqrbill;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import net.codecrete.qrbill.generator.GraphicsFormat;
import net.codecrete.qrbill.generator.Language;
import net.codecrete.qrbill.generator.OutputSize;

public interface QRBillParameter
{
	String key();

	String parentKey();

	boolean mandatory();

	boolean addError(String message);

	List<String> errors();

	void clearErrors();

	default String path()
	{
		return parentKey() + key();
	}

	default QRBillParameter[] parameter()
	{
		return new QRBillParameter[0];
	}

	default boolean validate(JsonNode value, ObjectNode source)
	{
		clearErrors();
		if (this.mandatory() && Objects.isNull(value))
		{
			return this.addError("Der Parameter '" + this.path() + "' ist zwingend erforderlich.");
		}
		else if (Objects.isNull(value))
		{
			return true;
		}
		boolean result = true;
		if (value.isContainerNode())
		{
			if (value.isObject())
			{
				ObjectNode sourceNode = ObjectNode.class.cast(source.get(this.key()));
				if (this.equals(QRBillMain.DATABASE))
				{
					for (QRBillDatabase database : QRBillDatabase.values())
					{
						if (!database.validate(sourceNode.get(database.key()), sourceNode))
						{
							result = false;
						}
					}
				}
				else if (this.equals(QRBillMain.CREDITOR))
				{
					for (QRBillCreditor creditor : QRBillCreditor.values())
					{
						if (!creditor.validate(sourceNode.get(creditor.key()), sourceNode))
						{
							result = false;
						}
					}
				}
				else if (this.equals(QRBillMain.DEBTOR))
				{
					for (QRBillDebtor debtor : QRBillDebtor.values())
					{
						if (!debtor.validate(sourceNode.get(debtor.key()), sourceNode))
						{
							result = false;
						}
					}
				}
				else if (this.equals(QRBillMain.FORM))
				{
					for (QRBillForm form : QRBillForm.values())
					{
						if (!form.validate(sourceNode.get(form.key()), sourceNode))
						{
							result = false;
						}
					}
				}
				else if (this.equals(QRBillDatabase.READ_INVOICE))
				{
					for (QRBillReadInvoice readInvoice : QRBillReadInvoice.values())
					{
						if (!readInvoice.validate(sourceNode.get(readInvoice.key()), sourceNode))
						{
							result = false;
						}
					}
				}
				else if (this.equals(QRBillDatabase.WRITE_QRBILL))
				{
					for (QRBillWrite writeQRBill : QRBillWrite.values())
					{
						if (!writeQRBill.validate(sourceNode.get(writeQRBill.key()), sourceNode))
						{
							result = false;
						}
					}
				}
			}
			else if (value.isArray())
			{
//				ArrayNode values = ArrayNode.class.cast(value);
//				values.elements()
			}
		}
		else if (value.isValueNode())
		{
			if (value.isTextual())
			{
				if (this.mandatory() && value.asText().isEmpty())
				{
					result = false;
				}
			}
			else if (value.isBigInteger())
			{
				if (this.mandatory() && value.bigIntegerValue().equals(BigInteger.ZERO))
				{
					result = false;
				}
			}
			else if (value.isBigDecimal())
			{
				if (this.mandatory() && value.decimalValue().equals(BigDecimal.ZERO))
				{
					result = false;
				}
			}
			else if (value.isDouble())
			{
				if (this.mandatory() && Double.valueOf(value.asDouble()).equals(Double.valueOf(0D)))
				{
					result = false;
				}
			}
			else if (value.isInt())
			{
				if (this.mandatory() && Integer.valueOf(value.asInt()).equals(Integer.valueOf(0)))
				{
					result = false;
				}
			}
		}
		return result;
	}

	default boolean updateTarget(ObjectNode source, ObjectNode target)
	{
		boolean result = true;
		JsonNode value = source.get(this.key());
		if (!Objects.isNull(value))
		{
			if (value.isContainerNode())
			{
				if (value.isObject())
				{
					ObjectNode sourceNode = ObjectNode.class.cast(source.get(this.key()));
					ObjectNode targetNode = target.putObject(this.key());
					if (this.equals(QRBillMain.DATABASE))
					{
						for (QRBillDatabase database : QRBillDatabase.values())
						{
							if (!database.updateTarget(sourceNode, targetNode))
							{
								result = false;
							}
						}
					}
					else if (this.equals(QRBillMain.CREDITOR))
					{
						for (QRBillCreditor creditor : QRBillCreditor.values())
						{
							if (!creditor.updateTarget(sourceNode, targetNode))
							{
								result = false;
							}
						}
					}
					else if (this.equals(QRBillMain.DEBTOR))
					{
						for (QRBillDebtor debtor : QRBillDebtor.values())
						{
							if (!debtor.updateTarget(sourceNode, targetNode))
							{
								result = false;
							}
						}
					}
					else if (this.equals(QRBillMain.FORM))
					{
						for (QRBillForm form : QRBillForm.values())
						{
							if (!form.updateTarget(sourceNode, targetNode))
							{
								result = false;
							}
						}
					}
					else if (this.equals(QRBillDatabase.READ_INVOICE))
					{
						for (QRBillReadInvoice readInvoice : QRBillReadInvoice.values())
						{
							if (!readInvoice.updateTarget(sourceNode, targetNode))
							{
								result = false;
							}
						}
					}
					else if (this.equals(QRBillDatabase.WRITE_QRBILL))
					{
						for (QRBillWrite writeQRBill : QRBillWrite.values())
						{
							if (!writeQRBill.updateTarget(sourceNode, targetNode))
							{
								result = false;
							}
						}
					}
				}
				else if (value.isArray())
				{
//						ArrayNode sourceNode = ArrayNode.class.cast(value.get(this.key()));
//						ArrayNode targetNode = target.putArray(this.key());
//						Iterator<JsonNode> children = sourceNode.elements();
//						while (children.hasNext())
//						{
////							JsonNode child = children.next();
////							result = this.validate(child, sourceNode);
//
//						}
				}
			}
			else if (value.isValueNode())
			{
				if (value.isTextual())
				{
					target.put(this.key(), value.asText());
				}
				else if (value.isBigDecimal())
				{
					target.put(this.key(), value.decimalValue());
				}
				else if (value.isBigInteger())
				{
					target.put(this.key(), value.bigIntegerValue());
				}
				else if (value.isInt())
				{
					target.put(this.key(), value.asInt());
				}
				else if (value.isDouble())
				{
					target.put(this.key(), value.asDouble());
				}
				else if (value.isBoolean())
				{
					target.put(this.key(), value.asBoolean());
				}
			}
		}
		return result;
	}

	static boolean checkAll(ObjectNode source, ObjectNode target)
	{
		List<String> errors = new ArrayList<String>();
		for (QRBillMain main : QRBillMain.values())
		{
			JsonNode value = source.get(main.key());
			if (main.validate(value, source))
			{
				main.updateTarget(source, target);
			}
			else
			{
				errors.addAll(main.errors());
			}
		}
		if (errors.isEmpty())
		{
			target.put("result", "OK");
		}
		else
		{
			target.put("result", "Fehler");
			ArrayNode errorNode = target.putArray("errors");
			{
				for (String error : errors)
				{
					errorNode.add(error);
				}
			}
		}
		return errors.size() == 0;
	}

	enum QRBillMain implements QRBillParameter
	{
		// @formatter:off
		AMOUNT("amount", false), 
		CURRENCY("currency", true),
		IBAN("iban", true), 
		INVOICE("invoice", true), 
		MESSAGE("message", false),
		REFERENCE("reference", true),
		DATABASE("database", true, QRBillDatabase.values()), 
		CREDITOR("creditor", true, QRBillCreditor.values()),
		DEBTOR("debtor", false, QRBillDebtor.values()),
		FORM("form", true, QRBillForm.values());
		// @formatter:on

		private List<String> errors = new ArrayList<String>();

		private String key;

		private boolean mandatory;

		private QRBillParameter[] parameter = new QRBillParameter[0];

		private QRBillMain(String key, boolean mandatory)
		{
			this.key = key;
			this.mandatory = mandatory;
		}

		private QRBillMain(String key, boolean mandatory, QRBillParameter[] parameter)
		{
			this(key, mandatory);
			this.parameter = parameter;
		}

		public List<String> errors()
		{
			for (QRBillParameter parameter : this.parameter())
			{
				errors.addAll(parameter.errors());
			}
			return errors;
		}

		public void clearErrors()
		{
			this.errors.clear();
		}

		public String parentKey()
		{
			return "";
		}

		public boolean mandatory()
		{
			return this.mandatory;
		}

		public String key()
		{
			return key;
		}

		public QRBillParameter[] parameter()
		{
			return this.parameter;
		}

		public boolean validate(JsonNode value, ObjectNode source)
		{
			clearErrors();
			switch (this)
			{
				case CURRENCY:
				{
					if (Objects.isNull(value))
					{
						return addError("Der Parameter '" + this.key() + "' ist zwingend erforderlich.");
					}
					Set<Currency> currencies = Currency.getAvailableCurrencies();
					for (Currency currency : currencies)
					{
						if (currency.getCurrencyCode().equals(value.asText()))
						{
							return true;
						}
					}
					return addError("Der Parameter '" + this.key() + "' ist ungültig (die Währung '" + value.asText()
							+ "' wird nicht unterstützt).");
				}
				case IBAN:
				{
					if (Objects.isNull(value))
					{
						return addError("Der Parameter '" + this.key() + "' ist zwingend erforderlich.");
					}
					String iban = value.asText().trim();
					if (iban.length() < 15 || iban.length() > 34)
					{
						return addError("Der Parameter '" + this.key() + "' ist ungültig (falsche Länge).");
					}
					iban = iban.substring(4) + iban.substring(0, 4);
					StringBuilder builder = new StringBuilder();
					for (int i = 0; i < iban.length(); i++)
					{
						builder.append(Character.getNumericValue(iban.charAt(i)));
					}
					BigInteger ibanNumber = new BigInteger(builder.toString());
					if (ibanNumber.mod(new BigInteger("97")).intValue() == 1)
					{
						return true;
					}
					return addError("Der Parameter '" + this.key() + "' ist ungültig.");
				}
				case REFERENCE:
				{
					if (Objects.isNull(value))
					{
						return addError("Der Parameter '" + this.key() + "' ist zwingend erforderlich.");
					}
					String reference = value.asText();
					if (reference.length() > 27)
					{
						return addError("Der Parameter '" + this.key() + "' darf aus maximal 27 Ziffern bestehen.");
					}
					StringBuilder builder = new StringBuilder(value.asText());
					while (builder.length() < 26)
					{
						builder = builder.insert(0, "0");
					}
					reference = builder.toString();
					if (reference.length() > 25 && reference.length() < 28)
					{
						String referenceWithoutErrorCheckingNumber = reference.length() == 27
								? reference.substring(0, reference.length() - 1)
								: reference;
						int[] table =
						{ 0, 9, 4, 6, 8, 2, 7, 1, 3, 5 };
						int keep = 0;
						for (int i = 0; i < referenceWithoutErrorCheckingNumber.length(); i++)
						{
							keep = table[(keep + reference.charAt(i) - '0') % 10];
						}
						int errorCheckingNumber = (10 - keep) % 10;
						if (reference.length() == 26)
						{
							reference = reference + String.valueOf(errorCheckingNumber);
							source.put(QRBillMain.REFERENCE.key, reference);
							return true;
						}
						if (Integer.valueOf(reference.substring(26)) == errorCheckingNumber)
						{
							source.put(QRBillMain.REFERENCE.key, reference);
							return true;
						}
						return addError("Der Parameter '" + this.key() + "' ist ungültig.");
					}
				}
				default:
				{
					return QRBillParameter.super.validate(value, source);
				}
			}
		}

		public boolean addError(String error)
		{
			this.errors.add(error);
			return false;
		}
	}

	enum QRBillDatabase implements QRBillParameter
	{
		// @formatter:off
		URL("url", "database.", true), 
		USERNAME("username", "database.", true),
		PASSWORD("password", "database.", false), 
		WRITE_QRBILL("writeqrbill", "database.", true, QRBillWrite.values()),
		READ_INVOICE("readinvoice", "database.", false, QRBillReadInvoice.values());
		// @formatter:on

		private List<String> errors = new ArrayList<String>();

		private String key;

		private String parentKey = "";

		private boolean mandatory;

		private QRBillParameter[] parameter = new QRBillParameter[0];

		private QRBillDatabase(String key, String parentKey, boolean mandatory)
		{
			this.key = key;
			this.parentKey = (Objects.isNull(parentKey) || parentKey.isEmpty()) ? "" : parentKey;
			this.mandatory = mandatory;
		}

		private QRBillDatabase(String key, String parentKey, boolean mandatory, QRBillParameter[] parameter)
		{
			this(key, parentKey, mandatory);
			this.parameter = parameter;
		}

		public List<String> errors()
		{
			for (QRBillParameter parameter : this.parameter())
			{
				errors.addAll(parameter.errors());
			}
			return errors;
		}

		public void clearErrors()
		{
			this.errors.clear();
		}

		public boolean mandatory()
		{
			return this.mandatory;
		}

		public String key()
		{
			return key;
		}

		public String parentKey()
		{
			return this.parentKey;
		}

		public QRBillParameter[] parameter()
		{
			return parameter;
		}

		public boolean addError(String error)
		{
			this.errors.add(error);
			return false;
		}
	}

	enum QRBillWrite implements QRBillParameter
	{
		// @formatter:off
		TABLE("table", "writeqrbill.", true), 
		QRBILL_COL("qrbill", "writeqrbill.", true),
		NAME_COL("name", "writeqrbill.", true),
		WHERE_COL("where_col", "writeqrbill.", true), 
		WHERE_VAL("where_val", "writeqrbill.", true);
		// @formatter:on

		private List<String> errors = new ArrayList<String>();

		private String key;

		private String parentKey = "";

		private boolean mandatory;

		private QRBillWrite(String key, String parentKey, boolean mandatory)
		{
			this.key = key;
			this.parentKey = (Objects.isNull(parentKey) || parentKey.isEmpty()) ? "" : parentKey;
			this.mandatory = mandatory;
		}

		public String key()
		{
			return this.key;
		}

		public String parentKey()
		{
			return parentKey;
		}

		public void clearErrors()
		{
			this.errors.clear();
		}

		public boolean mandatory()
		{
			return this.mandatory;
		}

		public List<String> errors()
		{
			for (QRBillParameter parameter : this.parameter())
			{
				errors.addAll(parameter.errors());
			}
			return errors;
		}

		public boolean addError(String error)
		{
			this.errors.add(error);
			return false;
		}
	}

	enum QRBillReadInvoice implements QRBillParameter
	{
		// @formatter:off
			TABLE("table", "readinvoice.", true), 
			INVOICE_COL("invoice", "readinvoice.", true),
			WHERE_COL("where_col", "readinvoice.", true), 
			WHERE_VAL("where_val", "readinvoice.", true);
			// @formatter:on

		private String key;

		private String parentKey;

		private boolean mandatory;

		private List<String> errors = new ArrayList<String>();

		private QRBillReadInvoice(String key, String parentKey, boolean mandatory)
		{
			this.key = key;
			this.parentKey = (Objects.isNull(parentKey) || parentKey.isEmpty()) ? "" : parentKey;
			this.mandatory = mandatory;
		}

		public String key()
		{
			return this.key;
		}

		public String parentKey()
		{
			return this.parentKey;
		}

		public void clearErrors()
		{
			this.errors.clear();
		}

		public boolean mandatory()
		{
			return this.mandatory;
		}

		public List<String> errors()
		{
			for (QRBillParameter parameter : this.parameter())
			{
				errors.addAll(parameter.errors());
			}
			return errors;
		}

		public boolean addError(String error)
		{
			this.errors.add(error);
			return false;
		}
	}

	enum QRBillCreditor implements QRBillParameter
	{
		// @formatter:off
		NAME("name", "creditor.", true), 
		ADDRESS("address", "creditor.", true),
		CITY("city", "creditor.", true), 
		COUNTRY("country", "creditor.", true);
		// @formatter:on

		private List<String> errors = new ArrayList<String>();

		private String key;

		private String parentKey = "";

		private boolean mandatory;

		private QRBillCreditor(String key, String parentKey, boolean mandatory)
		{
			this.key = key;
			this.parentKey = (Objects.isNull(parentKey) || parentKey.isEmpty()) ? "" : parentKey;
			this.mandatory = mandatory;
		}

		public List<String> errors()
		{
			for (QRBillParameter parameter : this.parameter())
			{
				errors.addAll(parameter.errors());
			}
			return errors;
		}

		public void clearErrors()
		{
			this.errors.clear();
		}

		public boolean mandatory()
		{
			return this.mandatory;
		}

		public String key()
		{
			return key;
		}

		public String parentKey()
		{
			return parentKey;
		}

		public boolean addError(String error)
		{
			this.errors.add(error);
			return false;
		}
	}

	enum QRBillDebtor implements QRBillParameter
	{
		// @formatter:off
		NUMBER("number", "debtor.", true), 
		NAME("name", "debtor.", true),
		ADDRESS("address", "debtor.", true), 
		CITY("city", "debtor.", true),
		COUNTRY("country", "debtor.", true);
		// @formatter:on

		private List<String> errors = new ArrayList<String>();

		private String key;

		private String parentKey = "";

		private boolean mandatory;

		private QRBillDebtor(String key, boolean mandatory)
		{
			this.key = key;
			this.mandatory = mandatory;
		}

		private QRBillDebtor(String key, String parentKey, boolean mandatory)
		{
			this.key = key;
			this.parentKey = (Objects.isNull(parentKey) || parentKey.isEmpty()) ? "" : parentKey;
			this.mandatory = mandatory;
		}

		public List<String> errors()
		{
			for (QRBillParameter parameter : this.parameter())
			{
				errors.addAll(parameter.errors());
			}
			return errors;
		}

		public void clearErrors()
		{
			this.errors.clear();
		}

		public boolean mandatory()
		{
			return this.mandatory;
		}

		public String key()
		{
			return key;
		}

		public String parentKey()
		{
			return parentKey;
		}

		public boolean addError(String error)
		{
			this.errors.add(error);
			return false;
		}
	}

	enum QRBillForm implements QRBillParameter
	{
		// @formatter:off
		GRAPHICS_FORMAT("graphics_format", "form.", true), 
		OUTPUT_SIZE("output_size", "form.", false),
		LANGUAGE("language", "form.", false);
		// @formatter:on

		private List<String> errors = new ArrayList<String>();

		private String key;

		private String parentKey = "";

		private boolean mandatory;

		private QRBillForm(String key, boolean mandatory)
		{
			this.key = key;
			this.mandatory = mandatory;
		}

		private QRBillForm(String key, String parentKey, boolean mandatory)
		{
			this.key = key;
			this.parentKey = (Objects.isNull(parentKey) || parentKey.isEmpty()) ? "" : parentKey;
			this.mandatory = mandatory;
		}

		public List<String> errors()
		{
			for (QRBillParameter parameter : this.parameter())
			{
				errors.addAll(parameter.errors());
			}
			return errors;
		}

		public void clearErrors()
		{
			this.errors.clear();
		}

		public boolean mandatory()
		{
			return this.mandatory;
		}

		public String key()
		{
			return this.key;
		}

		public String parentKey()
		{
			return this.parentKey;
		}

		public boolean validate(JsonNode value, ObjectNode source)
		{
			clearErrors();
			if (Objects.isNull(value) || value.asText().isEmpty())
			{
				return addError(
						"Der Parameter '" + QRBillMain.FORM.key() + "." + this.key() + "' ist zwingend erforderlich.");
			}
			switch (this)
			{
				case GRAPHICS_FORMAT:
				{
					for (GraphicsFormat gf : GraphicsFormat.values())
					{
						if (gf.name().equals(value.asText()))
						{
							return true;
						}
					}
					return addError("Der Parameter '" + QRBillMain.FORM.key() + "." + this.key() + "' ist ungültig.");
				}
				case OUTPUT_SIZE:
				{
					for (OutputSize os : OutputSize.values())
					{
						if (os.name().equals(value.asText()))
						{
							return true;
						}
					}
					return addError("Der Parameter '" + QRBillMain.FORM.key() + "." + this.key() + "' ist ungültig.");
				}
				case LANGUAGE:
				{
					for (Language l : Language.values())
					{
						if (l.name().equals(value.asText()))
						{
							return true;
						}
					}
					return addError("Der Parameter '" + QRBillMain.FORM.key() + "." + this.key() + "' ist ungültig.");
				}
				default:
				{
					return QRBillParameter.super.validate(value, source);
				}
			}
		}

		public boolean addError(String error)
		{
			errors.add(error);
			return false;
		}
	}
}
