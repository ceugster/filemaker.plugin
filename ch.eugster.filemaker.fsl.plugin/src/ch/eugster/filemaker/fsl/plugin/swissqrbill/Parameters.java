package ch.eugster.filemaker.fsl.plugin.swissqrbill;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Parameters
{
	@JsonProperty("iban")
	private String iban;

	@JsonProperty("reference")
	private String reference;

	@JsonProperty("currency")
	private String currency;

	@JsonProperty("amount")
	private Double amount;

	@JsonProperty("message")
	private String message;

	@JsonProperty("database")
	private Database database;

	@JsonProperty("source")
	private Source source;

	@JsonProperty("target")
	private Target target;

	@JsonProperty("form")
	private Form form;

	@JsonProperty("creditor")
	private Creditor creditor;

	@JsonProperty("debtor")
	private Debtor debtor;

	@JsonProperty("test")
	private Test test;

	public Test getTest()
	{
		return test;
	}

	public void setTest(Test test)
	{
		this.test = test;
	}

	public Form getForm()
	{
		return form;
	}

	public String getIban()
	{
		return iban;
	}

	public void setIban(String iban)
	{
		this.iban = iban;
	}

	public String getReference()
	{
		return reference;
	}

	public void setReference(String reference)
	{
		this.reference = reference;
	}

	public String getCurrency()
	{
		return currency;
	}

	public void setCurrency(String currency)
	{
		this.currency = currency;
	}

	public Double getAmount()
	{
		return amount;
	}

	public void setAmount(Double amount)
	{
		this.amount = amount;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public Database getDatabase()
	{
		return database;
	}

	public void setDatabase(Database database)
	{
		this.database = database;
	}

	public Source getSource()
	{
		return source;
	}

	public void setSource(Source source)
	{
		this.source = source;
	}

	public Target getTarget()
	{
		return target;
	}

	public void setTarget(Target target)
	{
		this.target = target;
	}

	public void setForm(Form form)
	{
		this.form = form;
	}

	public Creditor getCreditor()
	{
		return creditor;
	}

	public void setCreditor(Creditor creditor)
	{
		this.creditor = creditor;
	}

	public Debtor getDebtor()
	{
		return debtor;
	}

	public void setDebtor(Debtor debtor)
	{
		this.debtor = debtor;
	}

	public void merge(Parameters other)
	{
		if (Objects.nonNull(other.getAmount()))
		{
			this.setAmount(other.getAmount());
		}
		if (Objects.nonNull(other.getCurrency()))
		{
			this.setCurrency(other.getCurrency());
		}
		Database database = other.getDatabase();
		if (Objects.nonNull(database))
		{
			if (Objects.isNull(this.getDatabase()))
			{
				this.setDatabase(database);
			}
			else
			{
				if (Objects.nonNull(database.getUrl()))
				{
					this.getDatabase().setUrl(database.getUrl());
				}
				if (Objects.nonNull(database.getUsername()))
				{
					this.getDatabase().setUsername(database.getUsername());
				}
				if (Objects.nonNull(database.getPassword()))
				{
					this.getDatabase().setPassword(database.getPassword());
				}
			}
		}
		Form form = other.getForm();
		if (Objects.nonNull(form))
		{
			if (Objects.isNull(this.getForm()))
			{
				this.setForm(form);
			}
			else
			{
				if (Objects.nonNull(form.getGraphicsFormat()))
				{
					this.getForm().setGraphicsFormat(form.getGraphicsFormat());
				}
				if (Objects.nonNull(form.getOutputSize()))
				{
					this.getForm().setOutputSize(form.getOutputSize());
				}
				if (Objects.nonNull(form.getLanguage()))
				{
					this.getForm().setLanguage(form.getLanguage());
				}
			}
		}
		if (Objects.nonNull(other.getIban()))
		{
			this.setIban(other.getIban());
		}
		if (Objects.nonNull(other.getMessage()))
		{
			this.setMessage(other.getMessage());
		}
		if (Objects.nonNull(other.getReference()))
		{
			this.setReference(other.getReference());
		}
		Source source = other.getSource();
		if (Objects.nonNull(source))
		{
			if (Objects.isNull(this.getSource()))
			{
				this.setSource(source);
			}
			else
			{
				if (Objects.nonNull(source.getContainerCol()))
				{
					this.getSource().setContainerCol(source.getContainerCol());
				}
				if (Objects.nonNull(source.getPath()))
				{
					this.getSource().setPath(source.getPath());
				}
				if (Objects.nonNull(source.getTable()))
				{
					this.getSource().setTable(source.getTable());
				}
				if (Objects.nonNull(source.getWhereCol()))
				{
					this.getSource().setWhereCol(source.getWhereCol());
				}
				if (Objects.nonNull(source.getWhereVal()))
				{
					this.getSource().setWhereVal(source.getWhereVal());
				}
			}
		}
		Target target = other.getTarget();
		if (Objects.nonNull(target))
		{
			if (Objects.isNull(this.getTarget()))
			{
				this.setTarget(target);
			}
			else
			{
				if (Objects.nonNull(target.getContainerCol()))
				{
					this.getTarget().setContainerCol(target.getContainerCol());
				}
				if (Objects.nonNull(target.getNameCol()))
				{
					this.getTarget().setNameCol(target.getNameCol());
				}
				if (Objects.nonNull(target.getPath()))
				{
					this.getTarget().setPath(target.getPath());
				}
				if (Objects.nonNull(target.getTable()))
				{
					this.getTarget().setTable(target.getTable());
				}
				if (Objects.nonNull(target.getWhereCol()))
				{
					this.getTarget().setWhereCol(target.getWhereCol());
				}
				if (Objects.nonNull(target.getWhereVal()))
				{
					this.getTarget().setWhereVal(target.getWhereVal());
				}
			}
		}
		Creditor creditor = other.getCreditor();
		if (Objects.nonNull(creditor))
		{
			if (Objects.isNull(this.getCreditor()))
			{
				this.setCreditor(creditor);
			}
			else
			{
				if (Objects.nonNull(creditor.getName()))
				{
					this.getCreditor().setName(creditor.getName());
				}
				if (Objects.nonNull(creditor.getAddress()))
				{
					this.getCreditor().setAddress(creditor.getAddress());
				}
				if (Objects.nonNull(creditor.getCity()))
				{
					this.getCreditor().setCity(creditor.getCity());
				}
				if (Objects.nonNull(creditor.getCountry()))
				{
					this.getCreditor().setCountry(creditor.getCountry());
				}
			}
		}
		Debtor debtor = other.getDebtor();
		if (Objects.nonNull(debtor))
		{
			if (Objects.isNull(this.getDebtor()))
			{
				this.setDebtor(debtor);
			}
			else
			{
				if (Objects.nonNull(debtor.getName()))
				{
					this.getDebtor().setName(debtor.getName());
				}
				if (Objects.nonNull(debtor.getAddress()))
				{
					this.getDebtor().setAddress(debtor.getAddress());
				}
				if (Objects.nonNull(debtor.getCity()))
				{
					this.getDebtor().setCity(debtor.getCity());
				}
				if (Objects.nonNull(debtor.getCountry()))
				{
					this.getDebtor().setCountry(debtor.getCountry());
				}
			}
		}
		Test test = other.getTest();
		if (Objects.nonNull(test))
		{
			if (Objects.isNull(this.getTest()))
			{
				this.setTest(test);
			}
			else
			{
				if (Objects.nonNull(test.getProperties()))
				{
					this.getTest().setProperties(test.getProperties());
				}
			}
		}
	}
}
