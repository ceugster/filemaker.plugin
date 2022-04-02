package ch.eugster.filemaker.fsl.plugin.swissqrbill;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.filemaker.jdbc.Driver;

import ch.eugster.filemaker.fsl.plugin.Executor;
import ch.eugster.filemaker.fsl.plugin.swissqrbill.QRBillParameter.QRBillForm;
import ch.eugster.filemaker.fsl.plugin.swissqrbill.QRBillParameter.QRBillMain;
import ch.eugster.filemaker.fsl.plugin.swissqrbill.QRBillParameter.QRBillWrite;
import net.codecrete.qrbill.canvas.PDFCanvas;
import net.codecrete.qrbill.generator.Address;
import net.codecrete.qrbill.generator.Bill;
import net.codecrete.qrbill.generator.BillFormat;
import net.codecrete.qrbill.generator.GraphicsFormat;
import net.codecrete.qrbill.generator.Language;
import net.codecrete.qrbill.generator.OutputSize;
import net.codecrete.qrbill.generator.QRBill;
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
	private ObjectMapper mapper = new ObjectMapper();

	public String execute(ObjectNode source)
	{
		ObjectNode target = mapper.createObjectNode();
		this.mapper = new ObjectMapper();
		if (QRBillParameter.checkAll(source, target))
		{
			Bill bill = new Bill();
			bill.setAccount(target.get(QRBillParameter.QRBillMain.IBAN.key()).asText());
			if (!Objects.isNull(target.get(QRBillParameter.QRBillMain.AMOUNT.key())))
			{
				bill.setAmountFromDouble(target.get(QRBillParameter.QRBillMain.AMOUNT.key()).asDouble());
			}
			Address address = new Address();
			address.setName(target.get(QRBillParameter.QRBillMain.CREDITOR.key())
					.get(QRBillParameter.QRBillCreditor.NAME.key()).asText());
			address.setAddressLine1(target.get(QRBillParameter.QRBillMain.CREDITOR.key())
					.get(QRBillParameter.QRBillCreditor.ADDRESS.key()).asText());
			address.setAddressLine2(target.get(QRBillParameter.QRBillMain.CREDITOR.key())
					.get(QRBillParameter.QRBillCreditor.CITY.key()).asText());
			address.setCountryCode(target.get(QRBillParameter.QRBillMain.CREDITOR.key())
					.get(QRBillParameter.QRBillCreditor.COUNTRY.key()).asText());
			bill.setCreditor(address);
			bill.setCurrency(target.get(QRBillParameter.QRBillMain.CURRENCY.key()).asText());
			if (!Objects.isNull(target.get(QRBillParameter.QRBillMain.DEBTOR.key())))
			{
				address = new Address();
				address.setName(target.get(QRBillParameter.QRBillMain.DEBTOR.key())
						.get(QRBillParameter.QRBillDebtor.NAME.key()).asText());
				address.setAddressLine1(target.get(QRBillParameter.QRBillMain.DEBTOR.key())
						.get(QRBillParameter.QRBillDebtor.ADDRESS.key()).asText());
				address.setAddressLine2(target.get(QRBillParameter.QRBillMain.DEBTOR.key())
						.get(QRBillParameter.QRBillDebtor.CITY.key()).asText());
				address.setCountryCode(target.get(QRBillParameter.QRBillMain.DEBTOR.key())
						.get(QRBillParameter.QRBillDebtor.COUNTRY.key()).asText());
				bill.setDebtor(address);
			}
			BillFormat format = new BillFormat();
			format.setLanguage(getLanguage(target.get(QRBillParameter.QRBillMain.FORM.key())
					.get(QRBillParameter.QRBillForm.LANGUAGE.key()).asText()));
			format.setOutputSize(getOutputSize(target.get(QRBillParameter.QRBillMain.FORM.key())
					.get(QRBillParameter.QRBillForm.OUTPUT_SIZE.key()).asText()));
			format.setGraphicsFormat(getGraphicsFormat(target.get(QRBillParameter.QRBillMain.FORM.key())
					.get(QRBillParameter.QRBillForm.GRAPHICS_FORMAT.key()).asText()));
			bill.setFormat(format);
			bill.setReference(target.get(QRBillParameter.QRBillMain.REFERENCE.key()).asText());
			bill.setUnstructuredMessage(target.get(QRBillParameter.QRBillMain.MESSAGE.key()).asText());
			ValidationResult validation = QRBill.validate(bill);
			if (validation.isValid())
			{
				Connection connection = null;
				try
				{
					connection = this.createConnection(target);
					JsonNode readInvoice = target.get(QRBillParameter.QRBillDatabase.READ_INVOICE.key());
					if (Objects.isNull(readInvoice))
					{
						if (updateDatabase(connection, target, QRBill.generate(bill)))
						{

						}
					}
					else
					{
						byte[] bytes = this.readInvoice(connection, target, bill);
						if (!Objects.isNull(bytes))
						{
							bytes = this.appendQRBillToInvoice(target, bill, bytes);
							if (!Objects.isNull(target.get(QRBillParameter.QRBillMain.DATABASE.key())))
							{
								if (updateDatabase(connection, target, QRBill.generate(bill)))
								{

								}
							}
						}
					}
				}
				finally
				{
					this.closeConnection(connection);
				}
			}
		}
		return target.toString();

	}

	private Connection createConnection(ObjectNode target)
	{
		JsonNode db = target.get(QRBillParameter.QRBillMain.DATABASE.key());
		String url = db.get(QRBillParameter.QRBillDatabase.URL.key()).asText();
		String username = db.get(QRBillParameter.QRBillDatabase.USERNAME.key()).asText();
		String password = Objects.isNull(db.get(QRBillParameter.QRBillDatabase.PASSWORD.key())) ? ""
				: db.get(QRBillParameter.QRBillDatabase.PASSWORD.key()).asText();

		Driver driver = new Driver();
		Connection connection = null;
		try
		{
			DriverManager.registerDriver(driver);
			connection = DriverManager.getConnection(url, username, password);
		}
		catch (SQLException e)
		{
			QRBillParameter.addErrorNode(target, e.getLocalizedMessage());
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

	private boolean updateDatabase(Connection connection, ObjectNode target, byte[] bytes)
	{
		String filename = target.get(QRBillMain.INVOICE.key()) + "."
				+ target.get(QRBillMain.FORM.key()).get(QRBillForm.GRAPHICS_FORMAT.key()).asText().toLowerCase();

		JsonNode db = target.get(QRBillParameter.QRBillMain.DATABASE.key());
		JsonNode writeQRBill = db.get(QRBillParameter.QRBillDatabase.WRITE_QRBILL.key());
		String writeQRBillTable = writeQRBill.get(QRBillWrite.TABLE.key()).asText();
		String writeQRBillQRBillColumn = writeQRBill.get(QRBillParameter.QRBillWrite.QRBILL_COL.key()).asText();
		String writeQRBillNameColumn = writeQRBill.get(QRBillParameter.QRBillWrite.NAME_COL.key()).asText();
		String writeQRBillWhereCol = writeQRBill.get(QRBillParameter.QRBillWrite.WHERE_COL.key()).asText();
		String writeQRBillWhereVal = writeQRBill.get(QRBillParameter.QRBillWrite.WHERE_VAL.key()).asText();

		PreparedStatement pstm = null;
		int result = 0;
		try
		{
			String sql = "UPDATE " + writeQRBillTable + " SET " + writeQRBillQRBillColumn + " = ? AS '" + filename
					+ "', " + writeQRBillNameColumn + " = ? WHERE " + writeQRBillWhereCol + " = ?";
			pstm = connection.prepareStatement(sql);
			pstm.setBytes(1, bytes);
			pstm.setString(2, filename);
			pstm.setString(3, writeQRBillWhereVal);
			pstm.closeOnCompletion();
			System.out.println(sql);
			result = pstm.executeUpdate();
		}
		catch (SQLException e)
		{
			return QRBillParameter.addErrorNode(target, e.getLocalizedMessage());
		}
		return result == 1;

	}

	private byte[] readInvoice(Connection connection, ObjectNode target, Bill bill)
	{
		JsonNode db = target.get(QRBillParameter.QRBillMain.DATABASE.key());
		JsonNode readInvoice = db.get(QRBillParameter.QRBillDatabase.READ_INVOICE.key());
		String readInvoiceTable = readInvoice.get(QRBillParameter.QRBillReadInvoice.TABLE.key()).asText();
		String readInvoiceColumn = readInvoice.get(QRBillParameter.QRBillReadInvoice.INVOICE_COL.key()).asText();
		String readInvoiceWhereCol = readInvoice.get(QRBillParameter.QRBillReadInvoice.WHERE_COL.key()).asText();
		String readInvoiceWhereVal = readInvoice.get(QRBillParameter.QRBillReadInvoice.WHERE_VAL.key()).asText();

		byte[] bytes = null;
		PreparedStatement pstm = null;
		try
		{
			String sql = "SELECT GetAs(" + readInvoiceColumn + ", 'FILE') FROM " + readInvoiceTable + " WHERE "
					+ readInvoiceWhereCol + " = ?";
			pstm = connection.prepareStatement(sql);
			pstm.setString(1, readInvoiceWhereVal);
			System.out.println(sql);
			ResultSet rst = pstm.executeQuery();
			if (rst.next())
			{
				bytes = rst.getBytes(readInvoiceColumn);
			}
		}
		catch (SQLException e)
		{
			QRBillParameter.addErrorNode(target, e.getLocalizedMessage());
		}
		return bytes;
	}

	private byte[] appendQRBillToInvoice(ObjectNode target, Bill bill, byte[] bytes)
	{
		PDFCanvas canvas = null;
		if (!Objects.isNull(bytes))
		{
			try
			{
				byte[] targetArray = null;
				InputStream is = null;
				try
				{
					is = new ByteArrayInputStream(bytes);
					targetArray = new byte[is.available()];
					is.read(targetArray);
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
				bytes = canvas.toByteArray();
			}
			catch (IOException e)
			{
				QRBillParameter.addErrorNode(target, e.getLocalizedMessage());
			}
			finally
			{
				try
				{
					if (canvas != null)
					{
						bytes = canvas.toByteArray();
						canvas.close();
					}
				}
				catch (IOException e)
				{

				}
			}
		}
		else
		{
			QRBillParameter.addErrorNode(target,
					"Die Rechnung existiert nicht. Sie muss für die Verarbeitung vorhanden sein.");
		}
		return bytes;
	}

	private Language getLanguage(String value) throws IllegalArgumentException
	{
		for (Language language : Language.values())
		{
			if (language.name().equals(value))
			{
				return language;
			}
		}
		throw new IllegalArgumentException("Invalid language");
	}

	private OutputSize getOutputSize(String value) throws IllegalArgumentException
	{
		for (OutputSize outputSize : OutputSize.values())
		{
			if (outputSize.name().equals(value))
			{
				return outputSize;
			}
		}
		throw new IllegalArgumentException("Invalid outputSize");
	}

	private GraphicsFormat getGraphicsFormat(String value) throws IllegalArgumentException
	{
		for (GraphicsFormat graphicsFormat : GraphicsFormat.values())
		{
			if (graphicsFormat.name().equals(value))
			{
				return graphicsFormat;
			}
		}
		throw new IllegalArgumentException("Invalid outputSize");
	}

}