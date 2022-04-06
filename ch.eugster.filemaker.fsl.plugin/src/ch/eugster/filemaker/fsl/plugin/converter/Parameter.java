package ch.eugster.filemaker.fsl.plugin.converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface Parameter
{
	String key();

	String parentKey();

	boolean mandatory();

	List<String> errors();

	boolean addError(String message);

	void clearErrors();

	default String path()
	{
		return parentKey() + key();
	}

	default Parameter[] parameter()
	{
		return new Parameter[0];
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
		if (value.isValueNode())
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
		}
		return result;
	}

	default boolean updateTarget(ObjectNode source, ObjectNode target)
	{
		boolean result = true;
		JsonNode value = source.get(this.key());
		if (!Objects.isNull(value))
		{
			if (value.isValueNode())
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
		for (ConversionParameter param : ConversionParameter.values())
		{
			JsonNode value = source.get(param.key());
			if (param.validate(value, source))
			{
				param.updateTarget(source, target);
			}
			else
			{
				errors.addAll(param.errors());
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

	static boolean addErrorNode(ObjectNode target, String message)
	{
		JsonNode result = target.get("result");
		if (Objects.isNull(result))
		{
			target.put("result", "Fehler");
		}
		else if (result.asText().equals("OK"))
		{
			target.put("result", "Fehler");
		}
		JsonNode errors = target.get("errors");
		if (Objects.isNull(errors))
		{
			target.putArray("errors");
		}
		ArrayNode.class.cast(target.get("errors")).add(message);
		return false;
	}

	enum ConversionParameter implements Parameter
	{
		// @formatter:off
		SOURCE_XML("sourceXml", true), 
		TARGET_JSON("targetJson", false);
		// @formatter:on

		private String key;

		private boolean mandatory;

		private List<String> errors = new ArrayList<String>();

		private ConversionParameter(String key, boolean mandatory)
		{
			this.key = key;
			this.mandatory = mandatory;
		}

		public String key()
		{
			return this.key;
		}

		@Override
		public String parentKey()
		{
			return "";
		}

		public boolean mandatory()
		{
			return this.mandatory;
		}

		public List<String> errors()
		{
			return errors;
		}

		public boolean addError(String error)
		{
			this.errors.add(error);
			return false;
		}

		@Override
		public void clearErrors()
		{
			this.errors.clear();
		}
	}
}
