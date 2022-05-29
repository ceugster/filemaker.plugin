package ch.eugster.filemaker.fsl.plugin.swissqrbill;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.filemaker.jdbc.Driver;

import ch.eugster.filemaker.fsl.plugin.Executor;
import net.codecrete.qrbill.canvas.PDFCanvas;
import net.codecrete.qrbill.generator.Address;
import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.BillFormat;
import net.codecrete.qrbill.generator.GraphicsFormat;
import net.codecrete.qrbill.generator.Language;
import net.codecrete.qrbill.generator.OutputSize;
import net.codecrete.qrbill.generator.QRBill;
import net.codecrete.qrbill.generator.ValidationMessage;
import net.codecrete.qrbill.generator.ValidationResult;

/**
 * Generates swiss qrbills from json parameters. Based on the works of
 * net.codecrete.qrbill/qrbill-generatory by manuelbl,
 * 
 * @author christian
 *
 */
public class SwissQRBillGenerator implements Executor
{
	public String execute(ObjectNode source, ObjectNode result)
	{
		/*
		 * Read properties from json file in user.home/.fsl/qrbill.json
		 */
		try
		{
			source = this.loadProperties(source);
		}
		catch (Exception e)
		{
			this.addError(result, e);
		}

		if (Objects.isNull(result.get("errors")))
		{
			Bill bill = new Bill();
			bill.setAccount(this.getStringValue(source.get(Parameter.IBAN.key())));

			String reference = this.getStringValue(source.get(Parameter.REFERENCE.key()));
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

			Double amount = this.getDoubleValue(source, Parameter.AMOUNT.key());
			if (Objects.nonNull(amount) && !amount.equals(Double.valueOf(0D)))
			{
				bill.setAmountFromDouble(amount);
			}

			bill.setCurrency(this.getCurrency(source));

			Address creditor = new Address();
			JsonNode parent = source.get(Parameter.CREDITOR.key());
			if (Objects.nonNull(parent))
			{
				creditor.setName(this.getStringValue(parent.get(Parameter.NAME.key())));
				creditor.setAddressLine1(this.getStringValue(parent.get(Parameter.ADDRESS.key())));
				creditor.setAddressLine2(this.getStringValue(parent.get(Parameter.CITY.key())));
				creditor.setCountryCode(this.getStringValue(parent.get(Parameter.COUNTRY.key())));
				bill.setCreditor(creditor);
			}

			parent = source.get(Parameter.DEBTOR.key());
			if (Objects.nonNull(parent))
			{
				Address debtor = new Address();
				debtor.setName(this.getStringValue(parent.get(Parameter.NAME.key())));
				debtor.setAddressLine1(this.getStringValue(parent.get(Parameter.ADDRESS.key())));
				debtor.setAddressLine2(this.getStringValue(parent.get(Parameter.CITY.key())));
				debtor.setCountryCode(this.getStringValue(parent.get(Parameter.COUNTRY.key())));
				bill.setDebtor(debtor);
			}

			BillFormat format = new BillFormat();
			format.setGraphicsFormat(this.getGraphicsFormat(source));
			format.setLanguage(this.getLanguage(source));
			format.setOutputSize(this.getOutputSize(source));
			bill.setFormat(format);

			JsonNode message = source.get(Parameter.MESSAGE.key());
			if (Objects.nonNull(message))
			{
				bill.setUnstructuredMessage(message.asText());
			}

			ValidationResult validation = QRBill.validate(bill);
			if (validation.isValid())
			{
				try
				{
					updateDatabase(source, bill);
				}
				catch (Exception e)
				{
					this.addError(result, e);
				}
			}
			List<ValidationMessage> msgs = validation.getValidationMessages();
			if (!msgs.isEmpty())
			{
				ArrayNode errors = ArrayNode.class.cast(result.get("errors"));
				if (Objects.isNull(errors))
				{
					errors = result.putArray("errors");
				}
				for (ValidationMessage msg : msgs)
				{
					errors.add(msg.getMessageKey() + ": '" + msg.getField() + "'");
				}
			}
		}
		result.put("result",
				(Objects.isNull(result.get("errors")) || result.get("errors").isEmpty()) ? "OK" : "Fehler");
		return result.toString();
	}

	private String getStringValue(JsonNode node)
	{
		return Objects.isNull(node) ? null : node.asText();
	}

	private Double getDoubleValue(JsonNode source, String key)
	{
		JsonNode node = source.get(key);
		return Objects.isNull(node) ? null : node.asDouble();
	}

	private Connection createConnection(ObjectNode source) throws Exception
	{
		Connection connection = null;
		JsonNode db = source.get(Parameter.DATABASE.key());
		if (Objects.nonNull(db))
		{
			String url = this.getStringValue(db.get(Parameter.URL.key()));
			String username = this.getStringValue(db.get(Parameter.USERNAME.key()));
			String password = Objects.isNull(db.get(Parameter.PASSWORD.key())) ? ""
					: db.get(Parameter.PASSWORD.key()).asText();

			Driver driver = new Driver();
			DriverManager.registerDriver(driver);
			connection = DriverManager.getConnection(url, username, password);
		}
		else
		{
			throw new Exception("Keine Verbindungsdaten zur Datenbank gefunden.");
		}
		return connection;
	}

	private void closeConnection(Connection connection)
	{
		try
		{
			if (connection != null)
			{
				connection.close();
			}
		}
		catch (SQLException e)
		{
		}
	}

	private void updateDatabase(ObjectNode source, Bill bill) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = this.createConnection(source);
			Invoice invoice = new Invoice();
			if (Objects.isNull(source.get(Parameter.INVOICE.key())))
			{
				invoice.setName("Rechnung." + bill.getFormat().getGraphicsFormat().name().toLowerCase());
				invoice.setBlob(QRBill.generate(bill));
			}
			else
			{
				invoice = this.readInvoice(connection, source);
				invoice = this.appendQRBillToInvoice(source, bill, invoice);
			}

			int r = 0;
			JsonNode parent = source.get(Parameter.QRBILL.key());
			if (Objects.nonNull(parent))
			{
				String table = this.getStringValue(parent.get(Parameter.TABLE.key()));
				String containerColumn = this.getStringValue(parent.get(Parameter.CONTAINER_COL.key()));
				String nameColumn = this.getStringValue(parent.get(Parameter.NAME_COL.key()));
				String whereCol = this.getStringValue(parent.get(Parameter.WHERE_COL.key()));
				String whereVal = this.getStringValue(parent.get(Parameter.WHERE_VAL.key()));

				String sql = "UPDATE " + table + " SET " + containerColumn + " = ? AS '" + invoice.getName() + "', "
						+ nameColumn + " = ? WHERE " + whereCol + " = ?";
				PreparedStatement pstm = null;
				pstm = connection.prepareStatement(sql);
				pstm.setBytes(1, invoice.getBlob());
				pstm.setString(2, invoice.getName());
				pstm.setString(3, whereVal);
				pstm.closeOnCompletion();
				System.out.println(sql);
				r = pstm.executeUpdate();
			}
			else
			{
				throw new Exception("Informationen zur Zieltabelle fehlen.");
			}
			if (r != 1)
			{
				throw new Exception("Die Zieltabelle wurde nicht aktualisiert.");
			}
		}
		finally
		{
			if (Objects.nonNull(connection))
			{
				this.closeConnection(connection);
			}
		}
	}

	private Invoice readInvoice(Connection connection, ObjectNode source) throws Exception
	{
		Invoice invoice = new Invoice();
		JsonNode parent = source.get(Parameter.INVOICE.key());
		if (Objects.nonNull(parent))
		{
			String table = this.getStringValue(parent.get(Parameter.TABLE.key()));
			String column = this.getStringValue(parent.get(Parameter.CONTAINER_COL.key()));
			String whereCol = this.getStringValue(parent.get(Parameter.WHERE_COL.key()));
			String whereVal = this.getStringValue(parent.get(Parameter.WHERE_VAL.key()));

			String sql = "SELECT CAST(" + column + " AS VARCHAR) , GetAs(" + column + ", DEFAULT) FROM " + table
					+ " WHERE " + whereCol + " = ?";
			ResultSet rst = null;
			try
			{
				PreparedStatement pstm = null;
				pstm = connection.prepareStatement(sql);
				pstm.closeOnCompletion();
				pstm.setString(1, whereVal);
				System.out.println(sql);
				rst = pstm.executeQuery();
				if (rst.next())
				{
					invoice.setName(rst.getString(1));
					invoice.setBlob(rst.getBytes(2));
				}
			}
			catch (Exception e)
			{
				throw new Exception("Fehler in der Datenbankabfrage: " + sql);
			}
			finally
			{
				if (Objects.nonNull(rst))
				{
					rst.close();
				}
			}
		}
		return invoice;
	}

	private Invoice appendQRBillToInvoice(ObjectNode source, Bill bill, Invoice invoice) throws Exception
	{
		PDFCanvas canvas = null;
		if (Objects.nonNull(invoice) && Objects.nonNull(invoice.getBlob()))
		{
			try
			{
				byte[] targetArray = null;
				InputStream is = null;
				try
				{
					is = new ByteArrayInputStream(invoice.getBlob());
					targetArray = new byte[is.available()];
					is.read(targetArray);
				}
				catch (NullPointerException e)
				{
					throw new Exception(
							"Die Rechnung, an die der Einzahlungsschein angefügt werden soll, kann nicht gelesen werden.");
				}
				finally
				{
					if (is != null)
					{
						is.close();
					}
				}
				canvas = new PDFCanvas(targetArray, PDFCanvas.LAST_PAGE);
				QRBill.draw(bill, canvas);
				invoice.setBlob(canvas.toByteArray());
			}
			finally
			{
				if (canvas != null)
				{
					invoice.setBlob(canvas.toByteArray());
					canvas.close();
				}
			}
		}
		else
		{
			throw new Exception("Rechnung existiert nicht. Sie muss für die Verarbeitung vorhanden sein.");
		}
		return invoice;
	}

	private String getCurrency(JsonNode source)
	{
		String currency = "CHF";
		JsonNode node = source.get(Parameter.CURRENCY.key());
		if (Objects.nonNull(node))
		{
			String value = node.asText();
			if (Objects.nonNull(value) && !value.trim().isEmpty())
			{
				String[] validCurrencies = new String[]
				{ "CHF", "EUR" };
				for (String validCurrency : validCurrencies)
				{
					if (validCurrency.equals(value))
					{
						currency = validCurrency;
					}
				}
			}
		}
		return currency;
	}

	private Language getLanguage(JsonNode source) throws IllegalArgumentException
	{
		Language language = Language.DE;
		JsonNode parent = source.get(Parameter.FORM.key());
		if (Objects.nonNull(parent))
		{
			JsonNode node = parent.get(Parameter.LANGUAGE.key());
			if (Objects.nonNull(node))
			{
				String value = node.asText();
				if (Objects.nonNull(value) && !value.trim().isEmpty())
				{
					for (Language l : Language.values())
					{
						if (l.name().equals(value))
						{
							language = l;
						}
					}
				}
			}
		}
		return language;
	}

	private OutputSize getOutputSize(JsonNode source) throws IllegalArgumentException
	{
		OutputSize size = OutputSize.A4_PORTRAIT_SHEET;
		JsonNode parent = source.get(Parameter.FORM.key());
		if (Objects.nonNull(parent))
		{
			JsonNode node = parent.get(Parameter.OUTPUT_SIZE.key());
			if (Objects.nonNull(node))
			{
				String value = node.asText();
				if (Objects.nonNull(value) && !value.trim().isEmpty())
				{
					for (OutputSize outputSize : OutputSize.values())
					{
						if (outputSize.name().equals(value))
						{
							size = outputSize;
						}
					}
				}
			}
		}
		return size;
	}

	private GraphicsFormat getGraphicsFormat(JsonNode source) throws IllegalArgumentException
	{
		GraphicsFormat format = GraphicsFormat.PDF;
		JsonNode parent = source.get(Parameter.FORM.key());
		if (Objects.nonNull(parent))
		{
			JsonNode node = parent.get(Parameter.GRAPHICS_FORMAT.key());
			if (Objects.nonNull(node))
			{
				String value = node.asText();
				if (Objects.nonNull(value) && !value.trim().isEmpty())
				{
					for (GraphicsFormat graphicsFormat : GraphicsFormat.values())
					{
						if (graphicsFormat.name().equals(value))
						{
							format = graphicsFormat;
						}
					}
				}
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
		AMOUNT("amount"),
		MESSAGE("message"),
		
		DATABASE("database"),
		URL("url"), 
		USERNAME("username"),
		PASSWORD("password"),
		
		QRBILL("qrbill"),
		INVOICE("invoice"),

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
		LANGUAGE("language");
		// @formatter:on

		private String key;

		private Parameter(String key)
		{
			this.key = key;
		}

		public String key()
		{
			return this.key;
		}

	}

	class Invoice
	{
		private String name;

		private byte[] blob;

		public void setName(String name)
		{
			this.name = name;
		}

		public String getName()
		{
			return this.name;
		}

		public void setBlob(byte[] blob)
		{
			this.blob = blob;
		}

		public byte[] getBlob()
		{
			return this.blob;
		}
	}
}