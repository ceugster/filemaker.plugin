package ch.eugster.filemaker.fsl.plugin.xls;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.eval.FunctionEval;
import org.apache.poi.ss.formula.ptg.AreaPtgBase;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.RefPtgBase;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.eugster.filemaker.fsl.plugin.Executor;

/**
 * 
 */
public class Xls extends Executor<Xls>
{
//	public static Map<String, XSSFWorkbook> workbooks = new HashMap<String, XSSFWorkbook>();
//
//	public static Map<String, File> files = new HashMap<String, File>();

	public static Path path;

	public static XSSFWorkbook workbook;

	/**
	 * Create new workbook
	 * 
	 * @param parameters Object[] parameters
	 * 
	 * @param String     Specifies the path where workbook should be saved
	 */
	public static void createWorkbook(Object[] parameters)
	{
		try
		{
			if (parameters.length < 1)
			{
				throw new IllegalArgumentException("Kein Dateipfad angegeben");
			}
			if (String.class.isInstance(parameters[0]))
			{
				path = Paths.get(String.class.cast(parameters[0]));
				if (!path.toFile().getAbsoluteFile().getParentFile().isDirectory())
				{
					throw new IllegalArgumentException("Das Dateiverzeichnis ist ungültig");
				}
			}
			if (Objects.isNull(workbook))
			{
				workbook = new XSSFWorkbook();
			}
			else
			{
				throw new IllegalArgumentException("Die Arbeitsmappe ist bereits vorhanden");
			}
		}
		catch (Exception e)
		{
			addErrorMessage(e.getLocalizedMessage());
		}
	}

	/**
	 * Set sheet headers
	 * 
	 * @param parameters Object[] parameters
	 * 
	 * @param String     Specifies the left content to set
	 * @param String     Specifies the center content to set
	 * @param String     Specifies the right content to set
	 */
	public static void setHeaders(Object[] parameters)
	{
		try
		{
			prepareWorkbookAndSheetIfMissing();
			XSSFSheet sheet = Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex());
			Header header = sheet.getHeader();
			if (parameters.length > 0)
			{
				if (parameters.length > 2)
				{
					header.setRight(parameters[2].toString());
				}
				if (parameters.length > 1)
				{
					header.setCenter(parameters[1].toString());
				}
				if (parameters.length > 0)
				{
					header.setLeft(parameters[0].toString());
				}
			}
		}
		catch (Exception e)
		{
			addErrorMessage(e.getLocalizedMessage());
		}
	}

	/**
	 * Set sheet footers
	 * 
	 * @param parameters Object[] parameters
	 * 
	 * @param String     Specifies the left content to set
	 * @param String     Specifies the center content to set
	 * @param String     Specifies the right content to set
	 */
	public static void setFooters(Object[] parameters)
	{
		try
		{
			prepareWorkbookAndSheetIfMissing();
			XSSFSheet sheet = Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex());
			Footer footer = sheet.getFooter();
			if (parameters.length > 0)
			{
				if (parameters.length > 2)
				{
					footer.setRight(parameters[2].toString());
				}
				if (parameters.length > 1)
				{
					footer.setCenter(parameters[1].toString());
				}
				if (parameters.length > 0)
				{
					footer.setLeft(parameters[0].toString());
				}
			}
		}
		catch (Exception e)
		{
			addErrorMessage(e.getLocalizedMessage());
		}
	}

	/**
	 * Create sheet
	 * 
	 * @param parameters Object[] parameters
	 * 
	 * @param String     Specifies the sheet name
	 */
	public static void createSheet(Object[] parameters)
	{
		try
		{
			prepareWorkbookIfMissing();
			if (parameters.length < 1)
			{
				throw new IllegalArgumentException("Name für das Arbeitsblatt fehlt");
			}
			if (String.class.isInstance(parameters[0]))
			{
				String name = String.class.cast(parameters[0]);
				XSSFSheet sheet = workbook.createSheet(name);
				workbook.setActiveSheet(workbook.getSheetIndex(sheet));
			}
			else
			{
				throw new IllegalArgumentException("Falscher Parameter (" + parameters[0].getClass().getSimpleName() + " statt String)");
			}
		}
		catch (Exception e)
		{
			addErrorMessage(e.getLocalizedMessage());
		}
	}

	/**
	 * Get or create sheet
	 * 
	 * @param parameters Object[] parameters
	 * 
	 * @param String     Specifies the sheet name
	 */
	public static void getOrCreateSheet(Object[] parameters)
	{
		try
		{
			prepareWorkbookIfMissing();
			if (parameters.length < 1)
			{
				throw new IllegalArgumentException("Name für das Arbeitsblatt fehlt");
			}
			if (String.class.isInstance(parameters[0]))
			{
				String name = parameters[0].toString();
				XSSFSheet sheet = workbook.createSheet(name);
				workbook.setActiveSheet(workbook.getSheetIndex(sheet));
			}
			else
			{
				throw new IllegalArgumentException("Falscher Parameter: " + parameters[0] + " (muss String sein)");
			}
		}
		catch (Exception e)
		{
			addErrorMessage(e.getLocalizedMessage());
		}
	}

	/**
	 * Use sheet with given name
	 * 
	 * @param parameters Object[] parameters
	 * 
	 * @param Object     Specifies sheet to activate: Integer (index) or String
	 *                   (name of sheet)
	 */
	public static void setActiveSheet(Object[] parameters)
	{
		try
		{
			if (parameters.length < 1)
			{
				throw new IllegalArgumentException("Name für das Arbeitsblatt fehlt");
			}
			prepareWorkbookAndSheetIfMissing();
			if (String.class.isInstance(parameters[0]))
			{
				String name = String.class.cast(parameters[0]);
				int activeSheetIndex = workbook.getActiveSheetIndex();
				if (!workbook.getSheetAt(activeSheetIndex).getSheetName().equals(name))
				{
					activeSheetIndex = workbook.getSheetIndex(name);
					workbook.setActiveSheet(activeSheetIndex);
				}
			}
			else if (Integer.class.isInstance(parameters[0]))
			{
				int activeSheetIndex = Integer.class.cast(parameters[0]).intValue();
				if (workbook.getActiveSheetIndex() != activeSheetIndex)
				{
					workbook.setActiveSheet(activeSheetIndex);
				}
			}
			else
			{
				throw new IllegalArgumentException("Falscher Parameter (" + parameters[0].getClass().getSimpleName() + " statt String)");
			}
		}
		catch (Exception e)
		{
			addErrorMessage(e.getLocalizedMessage());
		}
	}

	/**
	 * Add row to the end in current sheet
	 * 
	 * @param parameters Object[] parameters
	 */
	public static void addRow(Objects[] parameters)
	{
		try
		{
			XSSFSheet sheet = workbook.getSheetAt(workbook.getActiveSheetIndex());
			int rowIndex = workbook.getSpreadsheetVersion().getLastRowIndex() + 1;
			int maxRows = workbook.getSpreadsheetVersion().getMaxRows();
			if (rowIndex < maxRows)
			{
				getOrCreateRow(sheet, rowIndex);
			}
			else
			{
				throw new IndexOutOfBoundsException(rowIndex + " ist grösser als Maximalwert (" + maxRows + ")");
			}
		}
		catch (Exception e)
		{
			addErrorMessage(e.getLocalizedMessage());
		}
	}

	/**
	 * Create row in current sheet
	 * 
	 * @param parameters Object[] parameters
	 * 
	 * @param parameters Object[] parameters
	 */
	public static void createRow(Objects[] parameters)
	{
		try
		{
			int rowIndex = -1;
			if (parameters.length < 1)
			{
				throw new IllegalArgumentException("Keine Zeile angegeben");
			}
			if (String.class.isInstance(parameters[0]))
			{
				CellAddress cellAddress = new CellAddress(String.class.cast(parameters[0]));
				rowIndex = cellAddress.getRow();
			}
			else if (Integer.class.isInstance(parameters[0]))
			{
				rowIndex = Integer.class.cast(parameters[0]).intValue();
			}
			else
			{
				throw new IllegalArgumentException("Falscher Parameter");
			}
			if (rowIndex > -1 && rowIndex < workbook.getSpreadsheetVersion().getMaxRows())
			{
				XSSFSheet sheet = workbook.getSheetAt(workbook.getActiveSheetIndex());
				if (Objects.isNull(sheet.getRow(rowIndex)))
				{
					sheet.createRow(rowIndex);
				}
			}
			else
			{
				throw new IllegalArgumentException("Zeile ausserhalb des gültigen Bereichs");
			}
		}
		catch (Exception e)
		{
			addErrorMessage(e.getLocalizedMessage());
		}
	}

	/**
	 * Set headings at row 0
	 * 
	 * @param parameters Object[] parameters
	 * 
	 * @param Object     Specifies the row from String (for CellAddress) or Integer
	 * @param Integer    Optional Specifies the starting column (Integer) if first
	 *                   parameter is of type Integer
	 * @param String[]   Specifies the headers to apply
	 */
	public static void setHeadingsHorizontal(Object[] parameters)
	{
		try
		{
			prepareWorkbookAndSheetIfMissing();
			int firstIndexInArray = 0;
			int rowIndex = 0;
			int startColumnIndex = 0;
			if (parameters.length > 2)
			{
				if (Integer.class.isInstance(parameters[0]) && Integer.class.isInstance(parameters[1]))
				{
					rowIndex = Integer.class.cast(parameters[0]).intValue();
					startColumnIndex = Integer.class.cast(parameters[1]).intValue();
					firstIndexInArray = 2;
				}
				else if (String.class.isInstance(parameters[0]))
				{
					CellAddress cellAddress = new CellAddress(String.class.cast(parameters[0]));
					rowIndex = cellAddress.getRow();
					startColumnIndex = cellAddress.getColumn();
					firstIndexInArray = 1;
				}
				else
				{
					throw new IllegalArgumentException("Ungültige(r) Parameter an 1./2. Stelle");
				}
				XSSFSheet sheet = workbook.getSheetAt(workbook.getActiveSheetIndex());
				XSSFRow row = sheet.getRow(rowIndex);
				if (Objects.isNull(row))
				{
					row = sheet.createRow(rowIndex);
				}
				for (int i = 0; i < parameters.length - firstIndexInArray; i++)
				{
					if (String.class.isInstance(parameters[firstIndexInArray + i]))
					{
						XSSFCell cell = row.getCell(startColumnIndex + i);
						if (Objects.isNull(cell))
						{
							cell = row.createCell(startColumnIndex + i);
						}
						cell.setCellValue(new XSSFRichTextString(String.class.cast(parameters[firstIndexInArray + i])));
					}
					else
					{
						throw new IllegalArgumentException("Ungültiger Parameter an " + String.valueOf(i) + " Stelle");
					}
				}
			}
		}
		catch (Exception e)
		{
			addErrorMessage(e.getLocalizedMessage());
		}
	}

	/**
	 * Set single cell value
	 * 
	 * @param parameters Object[] parameters
	 * 
	 * @param Object     Specifies the row as String (for CellAddress) or Integer
	 * @param Integer    Optional: Specifies the column if first parameter is of
	 *                   type integer
	 * @param Object     Specifies the value to apply to cell, one of allowed types
	 *                   in excel
	 */
	public static void setCellValue(Object[] parameters)
	{
		try
		{
			prepareWorkbookAndSheetIfMissing();
			if (parameters.length == 2)
			{
				if (String.class.isInstance(parameters[0]))
				{
					CellAddress cellAddress = new CellAddress(String.class.cast(parameters[0]));
					XSSFSheet sheet = workbook.getSheetAt(workbook.getActiveSheetIndex());
					XSSFRow row = sheet.getRow(cellAddress.getRow());
					XSSFCell cell = row.getCell(cellAddress.getColumn());
					setCellValue(cell, parameters[1]);
				}
				else
				{
					throw new IllegalArgumentException("Ungültiger Parameter");
				}
			}
			else if (parameters.length == 3)
			{
				if (Integer.class.isInstance(parameters[0]) && Integer.class.isInstance(parameters[1]))
				{
					int rowIndex = Integer.class.cast(parameters[0]).intValue();
					int columnIndex = Integer.class.cast(parameters[1]).intValue();
					XSSFSheet sheet = workbook.getSheetAt(workbook.getActiveSheetIndex());
					XSSFRow row = sheet.getRow(rowIndex);
					if (Objects.isNull(row))
					{
						row = sheet.createRow(rowIndex);
					}
					XSSFCell cell = row.getCell(columnIndex);
					if (Objects.isNull(cell))
					{
						cell = row.createCell(columnIndex);
					}
					setCellValue(cell, parameters[2]);
				}
				else
				{
					throw new IllegalArgumentException("Ungültiger Parameter");
				}
			}
			else
			{
				throw new IllegalArgumentException("Ungültige Anzahl Parameter (erwartet werden 2 oder 3)");
			}
		}
		catch (Exception e)
		{
			addErrorMessage(e.getLocalizedMessage());
		}
	}

	public static void setPrintOptions(Object[] parameters)
	{
		try
		{
			prepareWorkbookAndSheetIfMissing();
			if (parameters.length > 0)
			{
				XSSFSheet sheet = workbook.getSheetAt(workbook.getActiveSheetIndex());
				sheet.getPrintSetup().setLandscape(false);
			}
		}
		catch (Exception e)
		{
			addErrorMessage(e.getLocalizedMessage());
		}
	}

	/**
	 * Set row values
	 * 
	 * @param parameters Object[] parameters
	 * 
	 * @param Integer    Specifies the row
	 * @param Integer    Specifies the starting column
	 * @param Object     Specifies the value to apply to cell
	 * 
	 *                   or
	 * 
	 * @param String     Specifies the starting cell address
	 * @param Object     Specifies the value to apply to cell
	 */
	public static void setRowValues(Object[] parameters)
	{
		try
		{
			prepareWorkbookAndSheetIfMissing();
			int rowIndex = 0;
			int startColumnIndex = 0;
			int firstValueIndex = 0;
			if (String.class.isInstance(parameters[0]))
			{
				if (parameters.length < 2)
				{
					throw new IllegalArgumentException("Zuwenige Parameter");
				}
				CellAddress startCellAddress = new CellAddress(String.class.cast(parameters[0]));
				rowIndex = startCellAddress.getRow();
				startColumnIndex = startCellAddress.getColumn();
				firstValueIndex = 1;
			}
			else if (Integer.class.isInstance(parameters[0]) && Integer.class.isInstance(parameters[1]))
			{
				if (parameters.length < 3)
				{
					throw new IllegalArgumentException("Zuwenige Parameter");
				}
				rowIndex = Integer.class.cast(parameters[0]).intValue();
				startColumnIndex = Integer.class.cast(parameters[1]).intValue();
				firstValueIndex = 2;
			}
			else
			{
				throw new IllegalArgumentException("Ungültige Parameter");
			}
			XSSFSheet sheet = workbook.getSheetAt(workbook.getActiveSheetIndex());
			XSSFRow row = getOrCreateRow(sheet, rowIndex);
			for (int i = 0; i < parameters.length - firstValueIndex; i++)
			{
				XSSFCell cell = getOrCreateCell(row, startColumnIndex + i);
				setCellValue(cell, parameters[firstValueIndex + i]);
			}
		}
		catch (Exception e)
		{
			addErrorMessage(e.getLocalizedMessage());
		}
	}

	/**
	 * Set column values
	 * 
	 * @param parameters Object[] parameters
	 * 
	 * @param Integer    Specifies the starting row
	 * @param Integer    Specifies the column
	 * @param Object     Specifies the value to apply to cell
	 * 
	 *                   or
	 * 
	 * @param String     Specifies the starting cell address
	 * @param Object     Specifies the value to apply to cell
	 */
	public static void setColumnValues(Object[] parameters)
	{
		try
		{
			prepareWorkbookAndSheetIfMissing();
			int startRowIndex = 0;
			int columnIndex = 0;
			int firstValueIndex = 0;
			if (String.class.isInstance(parameters[0]))
			{
				if (parameters.length < 2)
				{
					throw new IllegalArgumentException("Zuwenige Parameter");
				}
				CellAddress startCellAddress = new CellAddress(String.class.cast(parameters[0]));
				startRowIndex = startCellAddress.getRow();
				columnIndex = startCellAddress.getColumn();
				firstValueIndex = 1;
			}
			else if (Integer.class.isInstance(parameters[0]) && Integer.class.isInstance(parameters[1]))
			{
				if (parameters.length < 3)
				{
					throw new IllegalArgumentException("Zuwenige Parameter");
				}
				startRowIndex = Integer.class.cast(parameters[0]).intValue();
				columnIndex = Integer.class.cast(parameters[1]).intValue();
				firstValueIndex = 2;
			}
			else
			{
				throw new IllegalArgumentException("Ungültige Parameter");
			}
			XSSFSheet sheet = workbook.getSheetAt(workbook.getActiveSheetIndex());
			for (int i = 0; i < parameters.length - firstValueIndex; i++)
			{
				XSSFRow row = getOrCreateRow(sheet, startRowIndex + i);
				XSSFCell cell = getOrCreateCell(row, columnIndex);
				setCellValue(cell, parameters[firstValueIndex + i]);
			}
		}
		catch (Exception e)
		{
			addErrorMessage(e.getLocalizedMessage());
		}
	}

	/**
	 * Set single cell formula
	 * 
	 * @param parameters Object[] parameters
	 * 
	 * @param Integer    Specifies the row
	 * @param Integer    Specifies the column
	 * @param Object     Specifies the formula as a String to apply to cell
	 */
	public static void setCellFormula(Object[] parameters)
	{
		try
		{
			if (String.class.isInstance(parameters[0]))
			{
				if (parameters.length < 2)
				{
					throw new IllegalArgumentException("Zuwenige Parameter");
				}

				CellAddress cellAddress = new CellAddress(String.class.cast(parameters[0]));
				XSSFSheet sheet = workbook.getSheetAt(workbook.getActiveSheetIndex());
				int rowIndex = cellAddress.getRow();
				int columnIndex = cellAddress.getColumn();
				XSSFRow row = getOrCreateRow(sheet, rowIndex);
				XSSFCell cell = getOrCreateCell(row, columnIndex);
				cell.setCellFormula(String.class.cast(parameters[1]));
			}
			else if (Integer.class.isInstance(parameters[0]) && Integer.class.isInstance(parameters[1]))
			{
				if (parameters.length < 3)
				{
					throw new IllegalArgumentException("Zuwenige Parameter");
				}

				int rowIndex = Integer.class.cast(parameters[0]).intValue();
				int columnIndex = Integer.class.cast(parameters[1]).intValue();
				XSSFSheet sheet = workbook.getSheetAt(workbook.getActiveSheetIndex());
				XSSFRow row = getOrCreateRow(sheet, rowIndex);
				XSSFCell cell = getOrCreateCell(row, columnIndex);
				cell.setCellFormula(String.class.cast(parameters[2]));
			}
			else
			{
				throw new IllegalArgumentException("Ungültige Parameter");
			}

		}
		catch (Exception e)
		{
			addErrorMessage(e.getLocalizedMessage());
		}
	}

	/**
	 * Copy and shift formula
	 * 
	 * @param parameters Object[] parameters
	 * 
	 * @param Integer    Specifies the row of source cell
	 * @param Integer    Specifies the column of source cell
	 * @param Integer    Specifies the target start row
	 * @param Integer    Specifies the target start column
	 * @param Integer    Optional Specifies the target number of rows
	 * @param Integer    Optional Specifies the target number of column
	 * 
	 *                   or
	 * 
	 * @param String     Specifies the formula cell to copy
	 * @param String     Specifies the target start cell of range
	 * @param String     Optional Specifies the target end cell of range
	 */
	public static void copyAndShiftFormulaCell(Object[] parameters)
	{
		try
		{
			prepareWorkbookAndSheetIfMissing();
			CellAddress sourceCellAddress = null;
			CellAddress startTargetCellAddress = null;
			CellAddress endTargetCellAddress = null;
			if (parameters.length == 2)
			{
				if (String.class.isInstance(parameters[0]) && String.class.isInstance(parameters[1]))
				{
					sourceCellAddress = new CellAddress(String.class.cast(parameters[0]));
					startTargetCellAddress = new CellAddress(String.class.cast(parameters[1]));

				}
				else
				{
					throw new IllegalArgumentException("Ungültige Parametertypen");
				}
			}
			else if (parameters.length == 3)
			{
				if (String.class.isInstance(parameters[0]) && String.class.isInstance(parameters[1]) || String.class.isInstance(parameters[2]))
				{
					sourceCellAddress = new CellAddress(String.class.cast(parameters[0]));
					startTargetCellAddress = new CellAddress(String.class.cast(parameters[1]));
					endTargetCellAddress = new CellAddress(String.class.cast(parameters[2]));

				}
				else
				{
					throw new IllegalArgumentException("Ungültige Parametertypen");
				}
			}
			else if (parameters.length == 4)
			{
				if (Integer.class.isInstance(parameters[0]) && Integer.class.isInstance(parameters[1]) && Integer.class.isInstance(parameters[2]) && Integer.class.isInstance(parameters[3]))
				{
					sourceCellAddress = new CellAddress(Integer.class.cast(parameters[0]).intValue(), Integer.class.cast(parameters[1]).intValue());
					startTargetCellAddress = new CellAddress(Integer.class.cast(parameters[2]).intValue(), Integer.class.cast(parameters[3]).intValue());
				}
				else
				{
					throw new IllegalArgumentException("Ungültige Parametertypen");
				}
			}
			else if (parameters.length == 6)
			{
				if (Integer.class.isInstance(parameters[0]) && Integer.class.isInstance(parameters[1]) && Integer.class.isInstance(parameters[2]) && Integer.class.isInstance(parameters[3]) && Integer.class.isInstance(parameters[4])
						&& Integer.class.isInstance(parameters[5]))
				{
					sourceCellAddress = new CellAddress(Integer.class.cast(parameters[0]).intValue(), Integer.class.cast(parameters[1]).intValue());
					startTargetCellAddress = new CellAddress(Integer.class.cast(parameters[2]).intValue(), Integer.class.cast(parameters[3]).intValue());
					endTargetCellAddress = new CellAddress(Integer.class.cast(parameters[4]).intValue(), Integer.class.cast(parameters[5]).intValue());
				}
				else
				{
					throw new IllegalArgumentException("Ungültige Parametertypen");
				}
			}
			else
			{
				throw new IllegalArgumentException("Ungültige Anzahl Parameter");
			}
			int startRowDifferenceIndex = startTargetCellAddress.getRow() - sourceCellAddress.getRow();
			int startColumnDifferenceIndex = startTargetCellAddress.getColumn() - sourceCellAddress.getColumn();
			int endRowDifferenceIndex = Objects.isNull(endTargetCellAddress) ? startRowDifferenceIndex : endTargetCellAddress.getRow() - sourceCellAddress.getRow();
			int endColumnDifferenceIndex = Objects.isNull(endTargetCellAddress) ? startColumnDifferenceIndex : endTargetCellAddress.getColumn() - sourceCellAddress.getColumn();
			XSSFCell sourceCell = getOrCreateCell(sourceCellAddress);
			for (int rowIndex = startRowDifferenceIndex; rowIndex <= endRowDifferenceIndex; rowIndex++)
			{
				XSSFRow row = getOrCreateRow(Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex()), sourceCell.getRowIndex() + rowIndex);
				for (int columnIndex = startColumnDifferenceIndex; columnIndex <= endColumnDifferenceIndex; columnIndex++)
				{
					XSSFCell cell = getOrCreateCell(row, sourceCell.getColumnIndex() + columnIndex);
					cell.copyCellFrom(sourceCell, new CellCopyPolicy());
					if (cell.getCellType().equals(CellType.FORMULA))
					{
						XSSFEvaluationWorkbook evaluationWorkbook = XSSFEvaluationWorkbook.create(Xls.workbook);
						Ptg[] ptgs = FormulaParser.parse(cell.getCellFormula(), evaluationWorkbook, FormulaType.CELL, Xls.workbook.getActiveSheetIndex());
						for (Ptg ptg : ptgs)
						{
							if (ptg instanceof RefPtgBase)
							{
								RefPtgBase ref = RefPtgBase.class.cast(ptg);
								if (ref.isRowRelative()) ref.setRow(ref.getRow() + rowIndex);
								if (ref.isColRelative()) ref.setColumn(ref.getColumn() + columnIndex);
							}
							else if (ptg instanceof AreaPtgBase)
							{
								AreaPtgBase ref = AreaPtgBase.class.cast(ptg);
								if (ref.isFirstColRelative()) ref.setFirstColumn(ref.getFirstColumn() + columnIndex);
								if (ref.isLastColRelative()) ref.setLastColumn(ref.getLastColumn() + columnIndex);
								if (ref.isFirstRowRelative()) ref.setFirstRow(ref.getFirstRow() + rowIndex);
								if (ref.isLastRowRelative()) ref.setLastRow(ref.getLastRow() + rowIndex);
							}
						}
						String formula = FormulaRenderer.toFormulaString(evaluationWorkbook, ptgs);
						cell.setCellFormula(formula);
					}
				}
			}

		}
		catch (Exception e)
		{
			addErrorMessage(e.getLocalizedMessage());
		}
	}

	/**
	 * Apply formulas to row
	 * 
	 * @param parameters Object[] parameters
	 * 
	 * @param Integer    Specifies the starting row
	 * @param Integer    Specifies the starting column
	 * @param Integer    Specifies the ending row
	 * @param Integer    Specifies the ending column
	 * @param String     Specifies the formula as string to apply to cell
	 * 
	 *                   or
	 * 
	 * @param String     Specifies the starting row
	 * @param String     Specifies the starting column
	 * @param String     Specifies the formula as string to apply to cell
	 */
	public static void setRowRangeSum(Object[] parameters)
	{
		try
		{
			prepareWorkbookAndSheetIfMissing();
			if (parameters.length == 2)
			{
				if (String.class.isInstance(parameters[0]) && String.class.isInstance(parameters[1]) || String.class.isInstance(parameters[2]))
				{
					CellAddress startCellRange = new CellAddress(String.class.cast(parameters[0]));
					CellAddress endCellRange = new CellAddress(String.class.cast(parameters[1]));
					setSumCell(startCellRange, endCellRange, Target.SUM_ROW_VALUES);

				}

			}
			else if (parameters.length == 4)
			{
				if (Integer.class.isInstance(parameters[0]) && Integer.class.isInstance(parameters[1]) && Integer.class.isInstance(parameters[2]) && Integer.class.isInstance(parameters[3]) && String.class.isInstance(parameters[4]))
				{
					CellAddress startCellRange = new CellAddress(Integer.class.cast(parameters[0]).intValue(), Integer.class.cast(parameters[1]).intValue());
					CellAddress endCellRange = new CellAddress(Integer.class.cast(parameters[2]).intValue(), Integer.class.cast(parameters[3]).intValue());
					setSumCell(startCellRange, endCellRange, Target.SUM_ROW_VALUES);
				}
				else
				{
					throw new IllegalArgumentException("Ungültige Parametertypen");
				}
			}
			else
			{
				throw new IllegalArgumentException("Ungültige Anzahl Parameter");
			}

		}
		catch (Exception e)
		{
			addErrorMessage(e.getLocalizedMessage());
		}
	}

	/**
	 * Apply number style to given cell range
	 * 
	 * @param parameters Object[] parameters
	 * 
	 * @param Integer    Specifies the starting row
	 * @param Integer    Specifies the starting column
	 * @param Integer    Specifies the ending row
	 * @param Integer    Specifies the ending column
	 * @param String     Specifies the style to apply to cell
	 * 
	 *                   or
	 * 
	 * @param String     Specifies the starting row
	 * @param String     Specifies the starting column
	 * @param String     Specifies the style to apply to cell
	 */
	public static void applyNumberStyleToCellRange(Object[] parameters)
	{
		try
		{
			prepareWorkbookAndSheetIfMissing();
			if (parameters.length == 3)
			{
				if (String.class.isInstance(parameters[0]) && String.class.isInstance(parameters[1]) || String.class.isInstance(parameters[2]))
				{
					CellAddress startCellRange = new CellAddress(String.class.cast(parameters[0]));
					CellAddress endCellRange = new CellAddress(String.class.cast(parameters[1]));
					String style = String.class.cast(parameters[2]);
					applyNumberStyleToCellRange(startCellRange, endCellRange, style);

				}

			}
			else if (parameters.length == 5)
			{
				if (Integer.class.isInstance(parameters[0]) && Integer.class.isInstance(parameters[1]) && Integer.class.isInstance(parameters[2]) && Integer.class.isInstance(parameters[3]) && String.class.isInstance(parameters[4]))
				{
					CellAddress startCellRange = new CellAddress(Integer.class.cast(parameters[0]).intValue(), Integer.class.cast(parameters[1]).intValue());
					CellAddress endCellRange = new CellAddress(Integer.class.cast(parameters[2]).intValue(), Integer.class.cast(parameters[3]).intValue());
					String style = String.class.cast(parameters[4]);
					applyNumberStyleToCellRange(startCellRange, endCellRange, style);
				}
				else
				{
					throw new IllegalArgumentException("Ungültige Parametertypen");
				}
			}
			else
			{
				throw new IllegalArgumentException("Ungültige Anzahl Parameter");
			}

		}
		catch (Exception e)
		{
			addErrorMessage(e.getLocalizedMessage());
		}
	}

	/**
	 * Apply font style to given cell range
	 * 
	 * @param parameters Object[] parameters
	 * 
	 * @param Integer    Specifies the starting row
	 * @param Integer    Specifies the starting column
	 * @param Integer    Specifies the ending row
	 * @param Integer    Specifies the ending column
	 * @param Integer    Specifies the style to apply to font (0=normal, 1=Bold,
	 *                   2=Italic, 3=Both
	 * 
	 *                   or
	 * 
	 * @param String     Specifies the starting row
	 * @param String     Specifies the starting column
	 * @param String     Specifies the style to apply to cell
	 */
	public static void applyFontStyleToCellRange(Object[] parameters)
	{
		try
		{
			prepareWorkbookAndSheetIfMissing();
			if (parameters.length == 5)
			{
				if (Integer.class.isInstance(parameters[0]) && Integer.class.isInstance(parameters[1]) && Integer.class.isInstance(parameters[2]) && Integer.class.isInstance(parameters[3]) && Integer.class.isInstance(parameters[4]))
				{
					CellAddress startCellRange = new CellAddress(Integer.class.cast(parameters[0]).intValue(), Integer.class.cast(parameters[1]).intValue());
					CellAddress endCellRange = new CellAddress(Integer.class.cast(parameters[2]).intValue(), Integer.class.cast(parameters[3]).intValue());
					int style = Integer.class.cast(parameters[4]).intValue();
					applyFontStyleToCellRange(startCellRange, endCellRange, style);
				}
				else
				{
					throw new IllegalArgumentException("Ungültige Parametertypen");
				}
			}
			if (parameters.length == 3)
			{
				if (String.class.isInstance(parameters[0]) && String.class.isInstance(parameters[1]) || String.class.isInstance(parameters[2]))
				{
					CellAddress startCellRange = new CellAddress(String.class.cast(parameters[0]));
					CellAddress endCellRange = new CellAddress(String.class.cast(parameters[1]));
					int style = Integer.class.cast(parameters[2]).intValue();
					applyFontStyleToCellRange(startCellRange, endCellRange, style);

				}

			}
			else
			{
				throw new IllegalArgumentException("Ungültige Anzahl Parameter");
			}

		}
		catch (Exception e)
		{
			addErrorMessage(e.getLocalizedMessage());
		}
	}

	public static void save(Object[] parameters)
	{
		OutputStream os = null;
		try
		{
			prepareWorkbookAndSheetIfMissing();
			if (parameters.length < 1)
			{
				throw new IllegalArgumentException("Kein Dateipfad angegeben");
			}
			if (String.class.isInstance(parameters[0]))
			{
				path = Paths.get(String.class.cast(parameters[0]));
				if (!path.toFile().getParentFile().isDirectory())
				{
					throw new IllegalArgumentException("Das Dateiverzeichnis ist ungültig");
				}
			}
			File file = new File(parameters[0].toString());
			os = new FileOutputStream(file);
			workbook.write(os);
			workbook.close();
			workbook = null;
		}
		catch (Exception e)
		{
			addErrorMessage(e.getLocalizedMessage());
		}
		finally
		{
			if (!Objects.isNull(os))
			{
				try
				{
					os.close();
				}
				catch (Exception e)
				{
				}
			}
			if (!Objects.isNull(workbook))
			{
				try
				{
					workbook.close();
				}
				catch (Exception e)
				{
				}
			}
		}
	}

	public static void createDebtorReport(Object[] arguments)
	{
		JsonNode json = null;
		ObjectMapper mapper = new ObjectMapper();
		try
		{
			int rowIndex = 0;
			int startRow = 0;
			int endRow = 0;
			json = mapper.readTree(arguments[0].toString());
			prepareWorkbookAndSheetIfMissing();

			XSSFSheet sheet = workbook.getSheetAt(workbook.getActiveSheetIndex());
			sheet.getPrintSetup().setLandscape(true);
			try
			{
				String title = json.get("sheet").get("header").get("title").asText();
				sheet.getHeader().setLeft(title);
			}
			catch (Exception e)
			{
			}
			try
			{
				String period = json.get("sheet").get("header").get("period").asText();
				sheet.getHeader().setCenter(period);
			}
			catch (Exception e)
			{
			}
			try
			{
				String from = json.get("sheet").get("header").get("from").asText();
				String to = json.get("sheet").get("header").get("to").asText();
				sheet.getHeader().setRight(from + " - " + to);
			}
			catch (Exception e)
			{
			}
			try
			{
				sheet.getFooter().setCenter("Seite &P von &N");
			}
			catch (Exception e)
			{
			}

			XSSFRow row = getOrCreateRow(rowIndex++);
			Iterator<Entry<String, JsonNode>> columns = json.get("column").fields();
			while (columns.hasNext())
			{
				Entry<String, JsonNode> column = columns.next();
				getOrCreateCell(row, Integer.valueOf(column.getKey()), column.getValue().asText(), true);
			}

			JsonNode details = json.get("details");
			Iterator<Entry<String, JsonNode>> types = details.fields();
			while (types.hasNext())
			{
				Entry<String, JsonNode> type = types.next();
				Iterator<Entry<String, JsonNode>> receipts = type.getValue().fields();
				startRow = rowIndex;
				String typeName = "";
				while (receipts.hasNext())
				{
					row = getOrCreateRow(rowIndex++);
					Entry<String, JsonNode> receipt = receipts.next();
					Iterator<Entry<String, JsonNode>> receiptColumns = receipt.getValue().fields();
					while (receiptColumns.hasNext())
					{
						Entry<String, JsonNode> receiptColumn = receiptColumns.next();
						int columnIndex = Integer.valueOf(receiptColumn.getKey());
						switch (columnIndex)
						{
						case 0:
						{
							getOrCreateCell(new CellAddress(row.getRowNum(), columnIndex), receiptColumn.getValue().asText());
							typeName = receiptColumn.getValue().asText();
							break;
						}
						case 1:
						{
							getOrCreateCell(new CellAddress(row.getRowNum(), columnIndex), receiptColumn.getValue().asText());
							break;
						}
						case 2:
						{
							getOrCreateCell(new CellAddress(row.getRowNum(), columnIndex), receiptColumn.getValue().asText());
							break;
						}
						case 3:
						{
							getOrCreateCell(new CellAddress(row.getRowNum(), columnIndex), receiptColumn.getValue().asDouble(), "0.00");
							break;
						}
						case 4:
						{
							getOrCreateCell(new CellAddress(row.getRowNum(), columnIndex), receiptColumn.getValue().asText());
							break;
						}
						case 5:
						{
							getOrCreateCell(new CellAddress(row.getRowNum(), columnIndex), receiptColumn.getValue().asDouble(), "0.00");
							break;
						}
						case 6:
						{
							getOrCreateCell(new CellAddress(row.getRowNum(), columnIndex), receiptColumn.getValue().asDouble(), "0.00");
							break;
						}
						case 7:
						{
							getOrCreateCell(new CellAddress(row.getRowNum(), columnIndex), receiptColumn.getValue().asDouble(), "0.00");
							break;
						}
						case 8:
						{
							getOrCreateCell(new CellAddress(row.getRowNum(), columnIndex), receiptColumn.getValue().asDouble(), "0.00");
							break;
						}
						default:
						{
//						throw new IllegalArgumentException("Ungültige Spaltennummer (" + String.valueOf(columnIndex) + ")");
							break;
						}
						}
					}
					endRow = row.getRowNum();
				}
				row = getOrCreateRow(rowIndex++);
				getOrCreateCell(new CellAddress(row.getRowNum(), 0), "Total " + typeName, true);
				getOrCreateCell(new CellAddress(row.getRowNum(), 3), "SUM", new CellRangeAddress(startRow, endRow, 3, 3), "0.00", true);
				getOrCreateCell(new CellAddress(row.getRowNum(), 5), "SUM", new CellRangeAddress(startRow, endRow, 5, 5), "0.00", true);
				getOrCreateCell(new CellAddress(row.getRowNum(), 6), "SUM", new CellRangeAddress(startRow, endRow, 6, 6), "0.00", true);
				getOrCreateCell(new CellAddress(row.getRowNum(), 7), "SUM", new CellRangeAddress(startRow, endRow, 7, 7), "0.00", true);
				getOrCreateCell(new CellAddress(row.getRowNum(), 8), "SUM", new CellRangeAddress(startRow, endRow, 8, 8), "0.00", true);
			}

			for (int i = 0; i < 9; i++)
			{
				sheet.autoSizeColumn(i);
			}
			String reference = new CellRangeAddress(0, rowIndex, 0, 8).formatAsString();
			workbook.setPrintArea(workbook.getActiveSheetIndex(), reference);
			File file = new File(System.getProperty("user.home") + File.separator + "myWorkbook.xlsx");
			save(new Object[] { file.getAbsolutePath() });
		}
		catch (Exception e)
		{
			addErrorMessage(e.getLocalizedMessage());
		}
	}

	protected static void setCellStyle(XSSFCell cell, String styleFormat)
	{
		CellStyle style = workbook.createCellStyle();
		DataFormat format = workbook.createDataFormat();
		style.setDataFormat(format.getFormat(styleFormat));
		cell.setCellStyle(style);
	}

	protected static void setSumCell(CellAddress startCellRange, CellAddress endCellRange, Target target)
	{
		prepareWorkbookAndSheetIfMissing();
		XSSFSheet sheet = workbook.getSheetAt(workbook.getActiveSheetIndex());
		if (target.equals(Target.SUM_COLUMN_VALUES))
		{
			for (int i = startCellRange.getColumn(); i < endCellRange.getColumn(); i++)
			{
				CellAddress startCellRow = new CellAddress(startCellRange.getRow(), i);
				CellAddress endCellRow = new CellAddress(endCellRange.getRow(), i);
				int targetCellRowIndex = endCellRange.getRow() + 1;
				String formula = "SUM(" + startCellRow + ":" + endCellRow + ")";
				XSSFRow row = sheet.getRow(targetCellRowIndex);
				if (Objects.isNull(row))
				{
					row = sheet.createRow(targetCellRowIndex);
				}
				XSSFCell cell = row.getCell(i);
				if (Objects.isNull(cell))
				{
					cell = row.createCell(i);
				}
				cell.setCellFormula(formula);
				cell.setCellType(CellType.FORMULA);
			}
		}
		else if (target.equals(Target.SUM_ROW_VALUES))
		{
			for (int i = startCellRange.getRow(); i < endCellRange.getRow(); i++)
			{
				CellAddress startCellColumn = new CellAddress(startCellRange.getRow(), i);
				CellAddress endCellColumn = new CellAddress(endCellRange.getRow(), i);
				int targetCellColumnIndex = endCellRange.getColumn() + 1;
				String formula = "SUM(" + startCellColumn + ":" + endCellColumn + ")";
				XSSFRow row = sheet.getRow(targetCellColumnIndex);
				if (Objects.isNull(row))
				{
					row = sheet.createRow(targetCellColumnIndex);
				}
				XSSFCell cell = row.getCell(i);
				if (Objects.isNull(cell))
				{
					cell = row.createCell(i);
				}
				cell.setCellFormula(formula);
				cell.setCellType(CellType.FORMULA);
			}
		}
	}

	protected static void applyNumberStyleToCellRange(CellAddress startCellRange, CellAddress endCellRange, String style)
	{
		prepareWorkbookAndSheetIfMissing();
		CellRangeAddress cellRange = new CellRangeAddress(startCellRange.getRow(), startCellRange.getColumn(), endCellRange.getRow(), endCellRange.getColumn());
		XSSFSheet sheet = workbook.getSheetAt(workbook.getActiveSheetIndex());
		XSSFCellStyle cellStyle = workbook.createCellStyle();
		DataFormat dataFormat = workbook.createDataFormat();
		cellStyle.setDataFormat(dataFormat.getFormat(style));
		Iterator<CellAddress> cellAddresses = cellRange.iterator();
		while (cellAddresses.hasNext())
		{
			CellAddress cellAddress = cellAddresses.next();
			XSSFRow row = sheet.getRow(cellAddress.getRow());
			if (Objects.isNull(row))
			{
				row = sheet.createRow(0);
			}
			XSSFCell cell = row.getCell(cellAddress.getColumn());
			if (Objects.isNull(cell))
			{
				cell = row.createCell(cellAddress.getColumn());
			}
			cell.setCellStyle(cellStyle);
		}

	}

	protected static void applyFontStyleToCellRange(CellAddress startCellRange, CellAddress endCellRange, int style) throws IllegalArgumentException
	{
		prepareWorkbookAndSheetIfMissing();
		CellRangeAddress cellRange = new CellRangeAddress(startCellRange.getRow(), startCellRange.getColumn(), endCellRange.getRow(), endCellRange.getColumn());
		XSSFSheet sheet = workbook.getSheetAt(workbook.getActiveSheetIndex());
		XSSFCellStyle cellStyle = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		if (style < 0 || style > 3)
		{
			throw new IllegalArgumentException("Ungültiger Fontstyle (gültig sind 0, 1, 2, 3)");
		}
		FontStyle fontStyle = FontStyle.values()[style];
		fontStyle.setFontStyle(font);
		cellStyle.setFont(font);
		Iterator<CellAddress> cellAddresses = cellRange.iterator();
		while (cellAddresses.hasNext())
		{
			CellAddress cellAddress = cellAddresses.next();
			XSSFRow row = sheet.getRow(cellAddress.getRow());
			if (Objects.isNull(row))
			{
				row = sheet.createRow(0);
			}
			XSSFCell cell = row.getCell(cellAddress.getColumn());
			if (Objects.isNull(cell))
			{
				cell = row.createCell(cellAddress.getColumn());
			}
			cell.setCellStyle(cellStyle);
		}

	}

	protected static void setCellValue(XSSFCell cell, Object parameter) throws IllegalArgumentException
	{
		if (Number.class.isAssignableFrom(parameter.getClass()))
		{
			String stringValue = parameter.toString();
			Double value = Double.valueOf(stringValue);
			cell.setCellValue(value.doubleValue());
			cell.setCellType(CellType.NUMERIC);
		}
		else if (parameter.getClass().equals(Date.class))
		{
			Date value = Date.class.cast(parameter);
			cell.setCellValue(value);
			cell.setCellType(CellType.NUMERIC);
		}
		else if (Calendar.class.isAssignableFrom(parameter.getClass()))
		{
			Calendar value = Calendar.class.cast(parameter);
			cell.setCellValue(value);
			cell.setCellType(CellType.NUMERIC);
		}
		else if (parameter.getClass().equals(String.class))
		{
			String value = String.class.cast(parameter);
			cell.setCellValue(value);
			cell.setCellType(CellType.STRING);
		}
		else
		{
			throw new IllegalArgumentException("Falscher Parameter (erlaubt sind String, Datum, Number)");
		}
	}

	protected static void prepareWorkbookIfMissing()
	{
		if (Objects.isNull(workbook))
		{
			workbook = new XSSFWorkbook();
		}
	}

	protected static void prepareWorkbookAndSheetIfMissing()
	{
		if (Objects.isNull(workbook))
		{
			workbook = new XSSFWorkbook();
		}
		if (workbook.getNumberOfSheets() == 0)
		{
			XSSFSheet sheet = workbook.createSheet("Arbeitsblatt 1");
			workbook.setActiveSheet(workbook.getSheetIndex(sheet));
		}
	}

	protected static XSSFRow getOrCreateRow(int rowIndex)
	{
		XSSFSheet sheet = workbook.getSheetAt(workbook.getActiveSheetIndex());
		XSSFRow row = sheet.getRow(rowIndex);
		if (Objects.isNull(row))
		{
			row = sheet.createRow(rowIndex);
		}
		return row;
	}

	protected static XSSFRow getOrCreateRow(XSSFSheet sheet, int rowIndex)
	{
		XSSFRow row = sheet.getRow(rowIndex);
		if (Objects.isNull(row))
		{
			row = sheet.createRow(rowIndex);
		}
		return row;
	}

	protected static XSSFRow getOrCreateRow(CellAddress cellAddress)
	{
		return getOrCreateRow(Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex()), cellAddress.getRow());
	}

	protected static XSSFCell getOrCreateCell(XSSFRow row, int columnIndex)
	{
		return getOrCreateCell(row, columnIndex, false);
	}

	protected static XSSFCell getOrCreateCell(XSSFRow row, int columnIndex, boolean bold)
	{
		XSSFCell cell = row.getCell(columnIndex);
		if (Objects.isNull(cell))
		{
			cell = row.createCell(columnIndex);
		}
		Font font = workbook.createFont();
		font.setBold(bold);
		CellStyle style = workbook.createCellStyle();
		style.setFont(font);
		return cell;
	}

	protected static XSSFCell getOrCreateCell(XSSFRow row, int columnIndex, String value)
	{
		return getOrCreateCell(row, columnIndex, value, false);
	}

	protected static XSSFCell getOrCreateCell(XSSFRow row, int columnIndex, String value, boolean bold)
	{
		XSSFCell cell = row.getCell(columnIndex);
		if (Objects.isNull(cell))
		{
			cell = row.createCell(columnIndex);
		}
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBold(bold);
		style.setFont(font);
		cell.setCellStyle(style);
		cell.setCellType(CellType.STRING);
		cell.setCellValue(new XSSFRichTextString(value));
		return cell;
	}

	protected static XSSFCell getOrCreateCell(XSSFRow row, int columnIndex, Double value, String styleFormat)
	{
		return getOrCreateCell(row, columnIndex, value, styleFormat, false);
	}

	protected static XSSFCell getOrCreateCell(XSSFRow row, int columnIndex, Double value, String styleFormat, boolean bold)
	{
		XSSFCell cell = row.getCell(columnIndex);
		if (Objects.isNull(cell))
		{
			cell = row.createCell(columnIndex);
		}
		CellStyle style = workbook.createCellStyle();
		DataFormat format = workbook.createDataFormat();
		Font font = workbook.createFont();
		font.setBold(bold);
		style.setFont(font);
		style.setDataFormat(format.getFormat(styleFormat));
		style.setAlignment(HorizontalAlignment.RIGHT);
		cell.setCellType(CellType.NUMERIC);
		cell.setCellValue(value);
		cell.setCellStyle(style);
		return cell;
	}

	protected static XSSFCell getOrCreateCell(XSSFRow row, int columnIndex, String formula, CellRangeAddress range, String styleFormat)
	{
		return getOrCreateCell(row, columnIndex, formula, range, styleFormat, false);
	}

	protected static XSSFCell getOrCreateCell(XSSFRow row, int columnIndex, String formula, CellRangeAddress range, String styleFormat, boolean bold)
	{
		XSSFCell cell = row.getCell(columnIndex);
		if (Objects.isNull(cell))
		{
			cell = row.createCell(columnIndex);
		}
		CellStyle style = workbook.createCellStyle();
		DataFormat format = workbook.createDataFormat();
		Font font = workbook.createFont();
		font.setBold(bold);
		style.setAlignment(HorizontalAlignment.RIGHT);
		style.setFont(font);
		style.setDataFormat(format.getFormat(styleFormat));
		cell.setCellFormula(formula + "(" + range.formatAsString() + ")");
		cell.setCellStyle(style);
		return cell;
	}

	protected static XSSFCell getOrCreateCell(CellAddress cellAddress)
	{
		return getOrCreateCell(cellAddress, false);
	}

	protected static XSSFCell getOrCreateCell(CellAddress cellAddress, boolean bold)
	{
		XSSFRow row = getOrCreateRow(Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex()), cellAddress.getRow());
		return getOrCreateCell(row, cellAddress.getColumn(), bold);
	}

	protected static XSSFCell getOrCreateCell(CellAddress cellAddress, String value)
	{
		return getOrCreateCell(cellAddress, value, false);
	}

	protected static XSSFCell getOrCreateCell(CellAddress cellAddress, String value, boolean bold)
	{
		XSSFRow row = getOrCreateRow(Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex()), cellAddress.getRow());
		return getOrCreateCell(row, cellAddress.getColumn(), value, bold);
	}

	protected static XSSFCell getOrCreateCell(CellAddress cellAddress, String formula, CellRangeAddress range, String styleFormat)
	{
		return getOrCreateCell(cellAddress, formula, range, styleFormat, false);
	}

	protected static XSSFCell getOrCreateCell(CellAddress cellAddress, String formula, CellRangeAddress range, String styleFormat, boolean bold)
	{
		XSSFRow row = getOrCreateRow(Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex()), cellAddress.getRow());
		return getOrCreateCell(row, cellAddress.getColumn(), formula, range, styleFormat, bold);
	}

	protected static XSSFCell getOrCreateCell(CellAddress cellAddress, Double value, String styleFormat)
	{
		return getOrCreateCell(cellAddress, value, styleFormat, false);
	}

	protected static XSSFCell getOrCreateCell(CellAddress cellAddress, Double value, String styleFormat, boolean bold)
	{
		XSSFRow row = getOrCreateRow(Xls.workbook.getSheetAt(Xls.workbook.getActiveSheetIndex()), cellAddress.getRow());
		return getOrCreateCell(row, cellAddress.getColumn(), value, styleFormat, bold);
	}

	protected static boolean isSupportedFormula(XSSFCell cell)
	{
		if (cell.getCellType().equals(CellType.FORMULA))
		{
			String formula = cell.getCellFormula();
			String[] parts = formula.split("(");
			for (String supportedFormulaName : FunctionEval.getSupportedFunctionNames())
			{
				System.out.println(supportedFormulaName);
				if (parts[0].equals(supportedFormulaName))
				{
					return true;
				}
			}
		}
		return false;
	}

	protected enum Target
	{
		SUM_ROW_VALUES, SUM_COLUMN_VALUES;
	}

	protected enum FontStyle
	{
		NORMAL, BOLD, ITALIC, BOLD_ITALIC;

		public void setFontStyle(Font font)
		{
			switch (this)
			{
			case NORMAL:
			{
				font.setBold(false);
				font.setItalic(false);
			}
			case BOLD:
			{
				font.setBold(true);
				font.setItalic(false);
			}
			case ITALIC:
			{
				font.setBold(false);
				font.setItalic(true);
			}
			case BOLD_ITALIC:
			{
				font.setBold(true);
				font.setItalic(true);
			}
			}
		}
	}

	protected enum VerticalPosition
	{
		HEADER("Kopf"), FOOTER("Fuss");

		private String verticalPosition;

		private VerticalPosition(String verticalPosition)
		{
			this.verticalPosition = verticalPosition;
		}

		public String verticalPosition()
		{
			return this.verticalPosition;
		}
	}

	protected enum HorizontalPosition
	{
		LEFT("Links"), CENTER("Mitte"), RIGHT("Rechts");

		private String horizontalPosition;

		private HorizontalPosition(String position)
		{
			this.horizontalPosition = position;
		}

		public String horizontalPosition()
		{
			return this.horizontalPosition;
		}
	}

}