package ch.eugster.filemaker.fsl.plugin.swissqrbill;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ch.eugster.filemaker.fsl.plugin.ExecuterCommand;
import ch.eugster.filemaker.fsl.plugin.Executor;
import net.codecrete.qrbill.canvas.PDFCanvas;
import net.codecrete.qrbill.generator.Address;
import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.BillFormat;
import net.codecrete.qrbill.generator.GraphicsFormat;
import net.codecrete.qrbill.generator.Language;
import net.codecrete.qrbill.generator.OutputSize;
import net.codecrete.qrbill.generator.ValidationMessage;
import net.codecrete.qrbill.generator.ValidationResult;

/**
 * Generates swiss qrbills from json parameters. Based on the works of
 * net.codecrete.qrbill/qrbill-generatory by manuelbl,
 * 
 * @author christian
 *
 */
public class QRBill extends Executor<QRBill>
{
	public static String json;

	public Command[] getCommands()
	{
		return Command.values();
	}

	public static void generate(Object[] arguments)
	{
		Parameters parameters = null;
		ObjectMapper mapper = new ObjectMapper();
		if (arguments.length == 1)
		{
			parameters = parseJsonParameters(mapper, arguments[0].toString());
		}
		else
		{
			parameters = parseListParameters(mapper, arguments);
		}
		Parameters properties = parseConfiguration(parameters, mapper);
		if (Objects.isNull(resultNode.get("errors")))
		{
			Bill bill = new Bill();
			if (Objects.nonNull(properties.getIban()))
			{
				bill.setAccount(properties.getIban());
			}

			if (Objects.nonNull(properties.getReference()))
			{
				String reference = properties.getReference();
				if (Objects.isNull(reference) || reference.trim().isEmpty())
				{
					bill.setReferenceType(Bill.REFERENCE_TYPE_NO_REF);
				}
				else
				{
					if (reference.toUpperCase().startsWith("FS"))
					{
						bill.createAndSetCreditorReference(reference);
					}
					else
					{
						bill.createAndSetQRReference(reference);
					}
				}
			}

			Double amount = properties.getAmount();
			if (Objects.nonNull(amount) && !amount.equals(Double.valueOf(0D)))
			{
				bill.setAmountFromDouble(amount);
			}

			if (Objects.nonNull(properties.getCurrency()))
			{
				bill.setCurrency(properties.getCurrency());
			}

			if (Objects.nonNull(properties.getCreditor()))
			{
				Address creditor = new Address();
				creditor.setName(properties.getCreditor().getName());
				creditor.setAddressLine1(properties.getCreditor().getAddress());
				creditor.setAddressLine2(properties.getCreditor().getCity());
				creditor.setCountryCode(properties.getCreditor().getCountry());
				bill.setCreditor(creditor);
			}

			if (Objects.nonNull(properties.getDebtor()))
			{
				Address debtor = new Address();
				debtor.setName(properties.getDebtor().getName());
				debtor.setAddressLine1(properties.getDebtor().getAddress());
				debtor.setAddressLine2(properties.getDebtor().getCity());
				debtor.setCountryCode(properties.getDebtor().getCountry());
				bill.setDebtor(debtor);
			}

			BillFormat format = new BillFormat();
			format.setGraphicsFormat(validateGraphicsFormat(properties));
			format.setLanguage(validateLanguage(properties));
			format.setOutputSize(validateOutputSize(properties));
			bill.setFormat(format);

			String message = properties.getMessage();
			if (Objects.nonNull(message) && !message.trim().isEmpty())
			{
				bill.setUnstructuredMessage(message);
			}

			ValidationResult validation = net.codecrete.qrbill.generator.QRBill.validate(bill);
			if (validation.isValid())
			{
				if (Objects.nonNull(properties.getTarget()))
				{
					Target target = properties.getTarget();
					if (target.isValid())
					{
						if (Objects.nonNull(properties.getSource()))
						{
							Source source = properties.getSource();
							if (source.isValid())
							{
								try
								{
									Blob blob = source.getBlob(properties.getDatabase());
									blob = merge(properties, bill, blob);
									Result updated = target.update(properties.getDatabase(), blob);
									if (updated.getResult() && Objects.nonNull(updated.getFile()))
									{
										resultNode.put("target", updated.getFile().getAbsolutePath());
									}
								}
								catch (Exception e)
								{
									addErrorMessage(e);
								}
							}
							else
							{
								addErrorMessage("Ein Quellobjekt wird erwartet, ist aber nicht verfügbar.");
							}
						}
						else
						{
							try
							{
								Blob blob = generateBlob(bill);
								Result updated = target.update(properties.getDatabase(), blob);
								if (updated.getResult() && Objects.nonNull(updated.getFile()))
								{
									resultNode.put("target", updated.getFile().getAbsolutePath());
								}
							}
							catch (Exception e)
							{
								addErrorMessage(e);
							}
						}
					}
				}
				else
				{
					addErrorMessage("Die Tabellendaten für das Zielobjekt sind fehlerhaft.");
				}
			}
			List<ValidationMessage> msgs = validation.getValidationMessages();
			if (!msgs.isEmpty())
			{
				ArrayNode errors = ArrayNode.class.cast(resultNode.get("errors"));
				if (Objects.isNull(errors))
				{
					errors = resultNode.putArray("errors");
				}
				for (ValidationMessage msg : msgs)
				{
					errors.add(msg.getMessageKey() + ": '" + msg.getField() + "'");
				}
			}
		}
		resultNode.put("result",
				(Objects.isNull(resultNode.get("errors")) || resultNode.get("errors").isEmpty()) ? "OK" : "Fehler");
		json = resultNode.toString();
	}

	private static Parameters parseJsonParameters(ObjectMapper mapper, String json)
	{
		Parameters parameters = new Parameters();
		try
		{
			parameters = mapper.readValue(json, Parameters.class);
		}
		catch (JsonMappingException e1)
		{
			addErrorMessage("Die Übergabeparameter enthalten ungültige Elemente.");
		}
		catch (JsonProcessingException e1)
		{
			addErrorMessage("Beim Verarbeiten der Übergabeparameter ist ein Fehler aufgetreten.");
		}
		catch (IllegalArgumentException e)
		{
			addErrorMessage(e);
		}
		return parameters;
	}

	private static Parameters parseListParameters(ObjectMapper mapper, Object[] arguments)
	{
		try
		{
			if (arguments.length == 7)
			{
				ObjectNode base = ObjectNode.class.cast(mapper.readTree(arguments[0].toString()));
				ObjectNode db = ObjectNode.class.cast(mapper.readTree(arguments[1].toString()));
				ObjectNode source = ObjectNode.class.cast(mapper.readTree(arguments[2].toString()));
				ObjectNode target = ObjectNode.class.cast(mapper.readTree(arguments[3].toString()));
				ObjectNode creditor = ObjectNode.class.cast(mapper.readTree(arguments[4].toString()));
				ObjectNode debtor = ObjectNode.class.cast(mapper.readTree(arguments[5].toString()));
				ObjectNode form = ObjectNode.class.cast(mapper.readTree(arguments[6].toString()));

				base.set(Parameter.DATABASE.key(), db);
				base.set(Parameter.SOURCE.key(), source);
				base.set(Parameter.TARGET.key(), target);
				base.set(Parameter.CREDITOR.key(), creditor);
				base.set(Parameter.DEBTOR.key(), debtor);
				base.set(Parameter.FORM.key(), form);

				System.out.println(base.toPrettyString());
				return parseJsonParameters(mapper, base.toString());
			}
			else
			{
				throw new IllegalArgumentException("Falsche Anzahl Parameter");
			}
		}
		catch (Exception e)
		{
			addErrorMessage(e);
		}
		return null;
	}

	private static Parameters parseConfiguration(Parameters parameters, ObjectMapper mapper)
	{
		Path path = Paths.get(System.getProperty("user.home"), ".fsl", "qrbill.json");
		Parameters properties = parameters;
		try
		{
			properties = mapper.readValue(path.toFile(), Parameters.class);
			properties.merge(parameters);
		}
		catch (StreamReadException e)
		{
			addErrorMessage("Die Konfigurationsdatei '" + path.getFileName() + "' ist fehlerhaft.");
		}
		catch (DatabindException e)
		{
			addErrorMessage("Die Konfigurationsdatei '" + path.getFileName() + "' enthält ungültige Elemente.");
		}
		catch (FileNotFoundException e)
		{
		}
		catch (IOException e)
		{
			addErrorMessage("Die Konfigurationsdatei '" + path.getFileName() + "' kann ncht gelesen werden.");
		}
		return properties;
	}

	private static Blob generateBlob(Bill bill)
	{
		Blob blob = new Blob();
		blob.setBlob(net.codecrete.qrbill.generator.QRBill.generate(bill));
		blob.setName("invoice.pdf");
		return blob;
	}

	private static Blob merge(Parameters properties, Bill bill, Blob blob) throws Exception
	{
		if (Objects.nonNull(blob) && Objects.nonNull(blob.getBlob()))
		{
			InputStream is = null;
			byte[] targetArray = null;
			try
			{
				is = new ByteArrayInputStream(blob.getBlob());
				targetArray = new byte[is.available()];
				is.read(targetArray);
			}
			catch (NullPointerException e)
			{
				throw new Exception("Das Quellobjekt kann nicht gelesen werden.");
			}
			finally
			{
				if (is != null)
				{
					is.close();
				}
			}
			PDFCanvas canvas = null;
			try
			{
				canvas = new PDFCanvas(targetArray, PDFCanvas.NEW_PAGE_AT_END);
				net.codecrete.qrbill.generator.QRBill.draw(bill, canvas);
				blob.setBlob(canvas.toByteArray());
			}
			catch (IOException e)
			{
				throw new Exception("Beim Erstellen der Rechnung ist ein Fehler aufgetreten.");
			}
			finally
			{
				if (canvas != null)
				{
					blob.setBlob(canvas.toByteArray());
					canvas.close();
				}
			}
		}
		else
		{
			throw new Exception("Ein Quellobjekt wird erwartet, ist aber nicht verfügbar.");
		}
		return blob;
	}

	private static Language validateLanguage(Parameters properties) throws IllegalArgumentException
	{
		Language language = Language.DE;
		Form form = properties.getForm();
		if (Objects.nonNull(form))
		{
			if (Objects.nonNull(form.getLanguage()))
			{
				language = form.getLanguage();
			}
		}
		return language;
	}

	private static OutputSize validateOutputSize(Parameters properties) throws IllegalArgumentException
	{
		OutputSize size = OutputSize.A4_PORTRAIT_SHEET;
		Form form = properties.getForm();
		if (Objects.nonNull(form))
		{
			if (Objects.nonNull(form.getOutputSize()))
			{
				size = form.getOutputSize();
			}
		}
		return size;
	}

	private static GraphicsFormat validateGraphicsFormat(Parameters properties) throws IllegalArgumentException
	{
		GraphicsFormat format = GraphicsFormat.PDF;
		Form form = properties.getForm();
		if (Objects.nonNull(form))
		{
			if (Objects.nonNull(form.getGraphicsFormat()))
			{
				format = form.getGraphicsFormat();
			}
		}
		return format;
	}

	public enum Parameter
	{
		// @formatter:off
		IBAN("iban"),
		REFERENCE("reference"),
		CURRENCY("currency"),
		AMOUNT("amount", Double.class),
		MESSAGE("message"),
		
		DATABASE("database"),
		URL("url"), 
		USERNAME("username"),
		PASSWORD("password"),
		
		TARGET("target"),
		SOURCE("source"),

		PATH("path"),
		
		TABLE("table"), 
		CONTAINER_COL("container_col"),
		NAME_COL("name_col"),
		WHERE_COL("where_col"), 
		WHERE_VAL("where_val"),

		CREDITOR("creditor"),
		DEBTOR("debtor"),

		NAME("name"), 
		ADDRESS("address"),
		CITY("city"), 
		COUNTRY("country"),
		
		FORM("form"),
		GRAPHICS_FORMAT("graphics_format"), 
		OUTPUT_SIZE("output_size"),
		LANGUAGE("language"),
		
		TEST("test"),
		PROPERTIES("properties");
		// @formatter:on

		private String key;

		private Class<?> clazz;

		private Parameter(String key)
		{
			this(key, String.class);
		}

		private Parameter(String key, Class<?> clazz)
		{
			this.key = key;
			this.clazz = clazz;
		}

		public String key()
		{
			return this.key;
		}

		public Class<?> clazz()
		{
			return this.clazz;
		}
	}

	enum Command implements ExecuterCommand
	{
		// @formatter:off
		GENERATE("generate");
		// @formatter:on

		private String command;

		private Command(String command)
		{
			this.command = command;
		}

		public String getCommand()
		{
			return this.command;
		}
	}
}