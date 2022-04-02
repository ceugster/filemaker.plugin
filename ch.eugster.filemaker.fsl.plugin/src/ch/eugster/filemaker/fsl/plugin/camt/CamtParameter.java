package ch.eugster.filemaker.fsl.plugin.camt;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface CamtParameter
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

	default CamtParameter[] parameter()
	{
		return new CamtParameter[0];
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
				if (this.equals(CamtMain.DATABASE))
				{
					for (CamtDatabase database : CamtDatabase.values())
					{
						if (!database.validate(sourceNode.get(database.key()), sourceNode))
						{
							result = false;
						}
					}
				}
				else if (this.equals(CamtDatabase.READ_XML))
				{
					for (CamtReadXml readXml : CamtReadXml.values())
					{
						if (!readXml.validate(sourceNode.get(readXml.key()), sourceNode))
						{
							result = false;
						}
					}
				}
				else if (this.equals(CamtDatabase.WRITE_JSON))
				{
					for (CamtWriteJson writeJson : CamtWriteJson.values())
					{
						if (!writeJson.validate(sourceNode.get(writeJson.key()), sourceNode))
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
					if (this.equals(CamtMain.DATABASE))
					{
						for (CamtDatabase database : CamtDatabase.values())
						{
							if (!database.updateTarget(sourceNode, targetNode))
							{
								result = false;
							}
						}
					}
					else if (this.equals(CamtDatabase.READ_XML))
					{
						for (CamtReadXml readXml : CamtReadXml.values())
						{
							if (!readXml.updateTarget(sourceNode, targetNode))
							{
								result = false;
							}
						}
					}
					else if (this.equals(CamtDatabase.WRITE_JSON))
					{
						for (CamtWriteJson writeJson : CamtWriteJson.values())
						{
							if (!writeJson.updateTarget(sourceNode, targetNode))
							{
								result = false;
							}
						}
					}
					{
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
		for (CamtMain main : CamtMain.values())
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

	static boolean addErrorNode(ObjectNode target, String message)
	{
		JsonNode result = target.get("result");
		if (Objects.isNull(result))
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

	enum CamtMain implements CamtParameter
	{
		// @formatter:off
		FILENAME("Camt.xml", true), 
		SIZE("size", false),
		HASH("hash", false), 
		DATABASE("database", true, CamtDatabase.values());
		// @formatter:on

		private String key;

		private boolean mandatory;

		private CamtDatabase[] database = new CamtDatabase[0];

		private List<String> errors = new ArrayList<String>();

		private CamtMain(String key, boolean mandatory)
		{
			this.key = key;
			this.mandatory = mandatory;
		}

		private CamtMain(String key, boolean mandatory, CamtDatabase[] database)
		{
			this(key, mandatory);
			this.database = database;
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

		public CamtDatabase[] database()
		{
			return this.database;
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

	enum CamtDatabase implements CamtParameter
	{
		//	@formatter:off	
		URL("url", true), 
		USERNAME("username", true),
		PASSWORD("password", false),
		READ_XML("readXml", true, CamtReadXml.values()),
		WRITE_JSON("writeJson", true, CamtWriteJson.values());
		//	@formatter:on

		private String key;

		private boolean mandatory;

		private CamtParameter[] parameter = new CamtParameter[0];

		private List<String> errors = new ArrayList<String>();

		private CamtDatabase(String key, boolean mandatory)
		{
			this.key = key;
			this.mandatory = mandatory;
		}

		private CamtDatabase(String key, boolean mandatory, CamtParameter[] parameter)
		{
			this(key, mandatory);
			this.parameter = parameter;
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

		public CamtParameter[] parameter()
		{
			return this.parameter;
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

	enum CamtReadXml implements CamtParameter
	{
		// @formatter:off
		TABLE("table", true), 
		COLUMN("column", true),
		WHERE_COL("where_col", true), 
		WHERE_VAL("where_val", true);

		private List<String> errors = new ArrayList<String>();

		private String key;

		private boolean mandatory;

		private CamtReadXml(String key, boolean mandatory)
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
			return CamtMain.DATABASE.key() + ".";
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

	enum CamtWriteJson implements CamtParameter
	{
		// @formatter:off
		TABLE("table", true), 
		JSON_COL("json", true),
		NAME_COL("name", true),
		WHERE_COL("where_col", true), 
		WHERE_VAL("where_val", true);
		// @formatter:on

		private String key;

		private boolean mandatory;

		private List<String> errors = new ArrayList<String>();

		private CamtWriteJson(String key, boolean mandatory)
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
			return CamtMain.DATABASE.key() + ".";
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

	public enum CamtPath05400104
	{
		CREDITOR("BkToCstmrDbtCdtNtfctn.Ntfctn.Acct");

		private String path;

		private CamtPath05400104(String path)
		{
			this.path = path;
		}

		public String path()
		{
			return this.path;
		}
	}
}
