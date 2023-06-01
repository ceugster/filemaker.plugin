package ch.eugster.filemaker.fsl.plugin.xls;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFEvaluationWorkbook;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaParsingWorkbook;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaRenderingWorkbook;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.eval.FunctionNameEval;
import org.apache.poi.ss.formula.ptg.AreaPtgBase;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.RefPtgBase;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellRange;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintOrientation;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import ch.eugster.filemaker.fsl.plugin.Executor;

/**
 * 
 */
/**
 * @author christian
 *
 */
public class Xls extends Executor
{
	public static final String SHEET0 = "Sheet0";

	public static ObjectMapper mapper = new ObjectMapper();

	public Map<String, Workbook> workbooks = new HashMap<String, Workbook>();

	public Workbook activeWorkbook;

	public Xls()
	{
		System.out.println(this);
	}
	/**
	 * Activates the sheet with the given name in argument 'sheet'
	 * 
	 * @param requestNode  mandatory arguments are: 'sheet', containing the name of
	 *                     the sheet to activate. Optional arguments are:
	 *                     'workbook', containing the name of the workbook the sheet
	 *                     belongs to. If missing the active workbook is used
	 * @param responseNode 'status' returns 'OK' if successful, else 'Fehler' and a
	 *                     string array 'errors' containing error messages
	 * @return true if the sheet has been activated, else false
	 */
	public boolean activateSheet(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = true;
		Workbook workbook = getWorkbookIfPresent(requestNode, responseNode);
		result = Objects.nonNull(workbook);
		if (result)
		{
			result = activateWorkbook(workbook);
			if (result)
			{
				JsonNode sheetNode = requestNode.findPath(Key.SHEET.key());
				Sheet sheet = findSheet(responseNode, workbook, sheetNode);
				result = Objects.nonNull(sheet);
				if (result)
				{
					int sheetIndex = workbook.getSheetIndex(sheet);
					workbook.setActiveSheet(sheetIndex);
					responseNode.put(Key.INDEX.key(), sheetIndex);
					responseNode.put(Key.SHEET.key(), sheet.getSheetName());
					responseNode.put(Key.WORKBOOK.key(), getActiveWorkbookName(responseNode));
				}
				else
				{
					result = addErrorMessage(responseNode, "missing sheet '" + sheetNode.asText() + "'");
				}
			}
		}
		return result;
	}

	/**
	 * Activates existing workbook. If workbook does not exist, an error occurs.
	 * 
	 * @param requestNode  mandatory arguments are: 'workbook', the (path)name of
	 *                     the workbook as provided when created
	 * @param responseNode 'status' returns 'OK' if successful, else 'Fehler' and a
	 *                     string array 'errors' containing error messages
	 * @return true if workbook has been activated, else false
	 */
	public boolean activateWorkbook(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = true;
		JsonNode workbookNode = requestNode.findPath(Key.WORKBOOK.key());
		if (workbookNode.isTextual())
		{
			String name = workbookNode.asText();
			Workbook workbook = workbooks.get(name);
			if (Objects.nonNull(workbook))
			{
				if (activeWorkbook != workbook)
				{
					activeWorkbook = workbook;
				}
			}
			else
			{
				result = addErrorMessage(responseNode, "missing_workbook '" + Key.WORKBOOK.key() + "'");
			}
		}
		else if (workbookNode.isMissingNode())
		{
			result = addErrorMessage(responseNode, "missing argument '" + Key.WORKBOOK.key() + "'");
		}
		else
		{
			result = addErrorMessage(responseNode, "invalid argument '" + Key.WORKBOOK.key() + "'");
		}
		return result;
	}

	/**
	 * Checks if an active sheet is present. If an active sheet is present
	 * responseNode returns its name in attribute sheet
	 * 
	 * No arguments
	 * 
	 * @param requestNode  empty
	 * @param responseNode contains 'status' with 'OK', 'index' containing the index
	 *                     of the sheet, 'sheet' containing the name of the sheet,
	 *                     and 'workbook', the (path)name of the workbook if
	 *                     successful, else 'status' with 'Fehler' string array of
	 *                     error messages
	 * @return true if an active sheet is present, else false
	 */
	public boolean activeSheetPresent(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = activeSheetPresent();
		if (result)
		{
			Sheet sheet = getActiveSheet(responseNode);
			responseNode.put(Key.INDEX.key(), activeWorkbook.getSheetIndex(sheet));
			responseNode.put(Key.SHEET.key(), sheet.getSheetName());
			responseNode.put(Key.WORKBOOK.key(), getActiveWorkbookName(responseNode));

		}
		else
		{
			result = addErrorMessage(responseNode, "no active sheet present");
		}
		return result;
	}

	/**
	 * Checks if an active workbook is present. If an active workbook is present
	 * responseNode returns its name in attribute workbook
	 * 
	 * No arguments
	 * 
	 * @param requestNode  empty
	 * @param responseNode 'status' returns 'OK' if successful, 'workbook'
	 *                     containing the (path)name of the workbook, else 'Fehler'
	 *                     and a string array 'errors' containing error messages
	 * @return true if an active sheet is present, else false
	 */
	public boolean activeWorkbookPresent(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = Objects.nonNull(activeWorkbook);
		if (result)
		{
			ObjectNode workbookNode = null;
			Set<Entry<String, Workbook>> entries = workbooks.entrySet();
			Iterator<Entry<String, Workbook>> iterator = entries.iterator();
			while (iterator.hasNext())
			{
				Entry<String, Workbook> entry = iterator.next();
				if (entry.getValue() == activeWorkbook)
				{
					workbookNode = responseNode.put(Key.WORKBOOK.key(), entry.getKey());
					break;
				}
			}
			result = Objects.nonNull(workbookNode);
		}
		return result;
	}

	/**
	 * Copies a range of cells to an other place not intersecting the source range
	 * 
	 * Mandatory parameters are: the source range as address (i.e. A1:B2) or as
	 * cells (top-left to bottom-right) or integers (top, left, bottom, right) the
	 * target range as address (i.e. A1:B2) or as cells (top-left to bottom-right)
	 * or integers (top, left, bottom, right)
	 * 
	 * Optional parameters are: the source and/or target workbook and source and/or
	 * target sheet
	 * 
	 * @param requestNode  mandatory parameters are: the source cell or range as
	 *                     address (i.e. A1:B2) or as cells (top-left to
	 *                     bottom-right) or integers (top, left, bottom, right) the
	 *                     target range as address (i.e. A1:B2) or as cells
	 *                     (top-left to bottom-right) or as integers (top, left,
	 *                     bottom, right), the target cell or range as above. source
	 *                     and target must not intersect. Optional parameters are:
	 *                     the source and/or target workbook and source and/or
	 *                     target sheet
	 * @param responseNode 'status' returns 'OK' if successful, else 'Fehler' and a
	 *                     string array 'errors' containing error messages
	 * @return true if successfully copied, else false
	 */
	public boolean copy(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = true;
		JsonNode sourceNode = requestNode.findPath(Key.SOURCE.key());
		Sheet sourceSheet = getSheetIfPresent(requestNode, responseNode);
		result = Objects.nonNull(sourceSheet);
		if (result)
		{
			CellRangeAddress sourceRangeAddress = getSourceRangeAddress(responseNode, sourceNode);
			result = validateRangeAddress(responseNode, sourceSheet.getWorkbook().getSpreadsheetVersion(), sourceRangeAddress);
			if (result)
			{
				JsonNode targetNode = requestNode.findPath(Key.TARGET.key());
				Sheet targetSheet = getSheetIfPresent(requestNode, responseNode);
				result = Objects.nonNull(targetSheet);
				if (result)
				{
					CellRangeAddress targetRangeAddress = getTargetRangeAddress(responseNode, targetNode);
					result = validateRangeAddress(responseNode, targetSheet.getWorkbook().getSpreadsheetVersion(),
							targetRangeAddress);
					if (result)
					{
						if (sourceSheet == targetSheet)
						{
							result = !sourceRangeAddress.intersects(targetRangeAddress);
							if (!result)
							{
								return addErrorMessage(responseNode, "source range and target range must not intersect");
							}
						}
						if (sourceRangeAddress.getNumberOfCells() == 1)
						{
							Row sourceRow = sourceSheet.getRow(sourceRangeAddress.getFirstRow());
							if (Objects.nonNull(sourceRow))
							{
								Cell sourceCell = sourceRow.getCell(sourceRangeAddress.getFirstColumn());
								if (Objects.nonNull(sourceCell))
								{
									Iterator<CellAddress> targetAddresses = targetRangeAddress.iterator();
									while (targetAddresses.hasNext())
									{
										CellAddress sourceAddress = new CellAddress(sourceCell);
										CellAddress targetAddress = targetAddresses.next();
										int rowDiff = targetAddress.getRow() - sourceAddress.getRow();
										int colDiff = targetAddress.getColumn() - sourceAddress.getColumn();
										if (sourceCell.getCellType().equals(CellType.FORMULA))
										{
											String copiedFormula = copyFormula(sourceSheet, sourceCell.getCellFormula(),
													rowDiff, colDiff);
											Cell targetCell = getOrCreateCell(responseNode, targetSheet, targetAddress);
											targetCell.setCellFormula(copiedFormula);
										}
										else
										{
											int targetTop = targetRangeAddress.getFirstRow();
											int targetBottom = targetRangeAddress.getLastRow();
											int targetLeft = targetRangeAddress.getFirstColumn();
											int targetRight = targetRangeAddress.getLastColumn();
											for (int r = targetTop; r <= targetBottom; r++)
											{
												Row targetRow = getOrCreateRow(targetSheet, r);
												for (int cell = targetLeft; cell <= targetRight; cell++)
												{
													Cell targetCell = getOrCreateCell(targetRow, cell);
													if (sourceCell.getCellType().equals(CellType.STRING))
													{
														targetCell.setCellValue(sourceCell.getRichStringCellValue());

													}
													else if (sourceCell.getCellType().equals(CellType.NUMERIC))
													{
														targetCell.setCellValue(sourceCell.getNumericCellValue());
													}
												}
											}
										}
									}
								}
							}
						}
						else if (sourceRangeAddress.getNumberOfCells() == targetRangeAddress.getNumberOfCells()
								&& sourceRangeAddress.getLastRow()
										- sourceRangeAddress.getFirstRow() == targetRangeAddress.getLastRow()
												- targetRangeAddress.getFirstRow())
						{
							Iterator<CellAddress> sourceAddresses = sourceRangeAddress.iterator();
							Iterator<CellAddress> targetAddresses = targetRangeAddress.iterator();
							while (sourceAddresses.hasNext())
							{
								CellAddress sourceAddress = sourceAddresses.next();
								Row sourceRow = sourceSheet.getRow(sourceAddress.getRow());
								if (Objects.nonNull(sourceRow))
								{
									Cell sourceCell = sourceRow.getCell(sourceAddress.getColumn());
									if (Objects.nonNull(sourceCell))
									{
										CellAddress targetAddress = targetAddresses.next();
										Row targetRow = getOrCreateRow(targetSheet, targetAddress.getRow());
										Cell targetCell = getOrCreateCell(targetRow, targetAddress.getColumn());
										copyCell(sourceCell, targetCell);
									}
								}
							}
						}
						else
						{
							result = addErrorMessage(responseNode, "source and target range dimensions must not differ");
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * Creates and activates a sheet
	 * 
	 * @param requestNode  mandatory 'sheet' containing the name of the sheet,
	 *                     optional 'workbook' containing the name of the workbook
	 * @param responseNode 'status' returns 'OK' if successful, else 'Fehler' and a
	 *                     string array 'errors' containing error messages
	 * @return true if the sheet has been created and activated successfully, else
	 *         false
	 */
	public boolean createAndActivateSheet(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = createSheet(requestNode, responseNode);
		if (result)
		{
			result = activateSheet(requestNode, responseNode);
		}
		return result;
	}

	/**
	 * Creates and activates a workbook
	 * 
	 * @param requestNode  mandatory 'workbook' containing the name of the workbook
	 * @param responseNode 'status' returns 'OK' if successful, else 'Fehler' and a
	 *                     string array 'errors' containing error messages
	 * @return true if the workbook has been created and activated successfully,
	 *         else false
	 */
	public boolean createAndActivateWorkbook(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = createWorkbook(requestNode, responseNode);
		if (result)
		{
			result = activateWorkbook(requestNode, responseNode);
		}
		return result;
	}

	/**
	 * Creates a sheet in the active workbook or in the workbook provided by
	 * argument workbook
	 * 
	 * @param requestNode  mandatory 'sheet' containing the name of the sheet,
	 *                     optional 'workbook' containing the name of the workbook,
	 *                     if not provided, the active workbook is used
	 * @param responseNode 'status' returns 'OK' and 'index' the index of the sheet,
	 *                     'sheet' containing the name of the sheet created,
	 *                     'workbook' the name of the workbook on which the sheet is
	 *                     created if successful, else 'Fehler' and a string array
	 *                     'errors' containing error messages
	 * @return true if the sheet has been created successfully, else false
	 */
	public boolean createSheet(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = true;
		Workbook workbook = getWorkbookIfPresent(requestNode, responseNode);
		result = Objects.nonNull(workbook);
		if (result)
		{
			JsonNode sheetNode = requestNode.findPath(Key.SHEET.key());
			if (sheetNode.isTextual())
			{
				Sheet sheet = createSheet(responseNode, workbook, sheetNode);
				responseNode.put(Key.INDEX.key(), workbook.getSheetIndex(sheet));
				responseNode.put(Key.SHEET.key(), sheet.getSheetName());
				responseNode.put(Key.WORKBOOK.key(), getWorkbookName(responseNode, workbook));
			}
			else if (sheetNode.isMissingNode())
			{
				result = addErrorMessage(responseNode, "missing argument '" + Key.SHEET.key() + "'");
			}
			else
			{
				result = addErrorMessage(responseNode, "invalid argument '" + Key.SHEET.key() + "'");
			}
		}
		return result;
	}

	/**
	 * Creates a workbook
	 * 
	 * @param requestNode  mandatory 'workbook' containing the (path)name of the
	 *                     workbook being created
	 * @param responseNode 'status' returns 'OK' if successful, else 'Fehler' and a
	 *                     string array 'errors' containing error messages
	 * @return true if the workbook has been created successfully, else false
	 */
	public boolean createWorkbook(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = true;
		JsonNode nameNode = requestNode.findPath(Key.WORKBOOK.key());
		if (nameNode.isTextual())
		{
			String name = nameNode.asText();
			Workbook wb = workbooks.get(name);
			if (Objects.isNull(wb))
			{
				wb = createWorkbook(responseNode, name);
			}
			else
			{
				File file = new File(nameNode.asText());
				result = addErrorMessage(responseNode, "workbook '" + file.getName() + "' already exists");
			}
		}
		else if (nameNode.isMissingNode())
		{
			result = addErrorMessage(responseNode, "missing argument '" + Key.WORKBOOK.key() + "'");
		}
		else
		{
			result = addErrorMessage(responseNode, "invalid argument '" + Key.WORKBOOK.key() + "'");
		}
		return result;
	}

	/**
	 * Returns index and sheet name of active sheet and name of the active workbook
	 * the sheet belongs to
	 * 
	 * @param requestNode  empty
	 * @param responseNode 'status' returns 'OK' if successful, 'index' containing
	 *                     the index of the sheet, 'sheet' containing the name of
	 *                     the sheet, and 'workbook', the (path)name of the
	 *                     workbook, else 'Fehler' and a string array 'errors'
	 *                     containing error messages
	 * @return true if active sheet present, else false
	 */
	public boolean getActiveSheet(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = activeSheetPresent();
		if (result)
		{
			int sheetIndex = activeWorkbook.getActiveSheetIndex();
			responseNode.put(Key.INDEX.key(), sheetIndex);
			responseNode.put(Key.SHEET.key(), activeWorkbook.getSheetAt(sheetIndex).getSheetName());
			responseNode.put(Key.WORKBOOK.key(), getActiveWorkbookName(responseNode));
		}
		else
		{
			result = addErrorMessage(responseNode, "no active sheet present");
		}
		return result;
	}

	/**
	 * Returns the name of the active workbook
	 * 
	 * @param requestNode  empty
	 * @param responseNode 'status' returns 'OK' if successful, 'workbook', the
	 *                     (path)name of the workbook, else 'Fehler' and a string
	 *                     array 'errors' containing error messages
	 * @return true if active workbook present, else false
	 */
	public boolean getActiveWorkbook(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = false;
		Set<Entry<String, Workbook>> entrySet = workbooks.entrySet();
		Iterator<Entry<String, Workbook>> iterator = entrySet.iterator();
		while (iterator.hasNext())
		{
			Entry<String, Workbook> entry = iterator.next();
			if (entry.getValue() == activeWorkbook)
			{
				responseNode.put("name", entry.getKey());
				result = true;
				break;
			}
		}
		if (!result)
		{
			result = addErrorMessage(responseNode, "no active workbook spresent");
		}
		return result;
	}

	/**
	 * Returns an string array of all callable methods
	 * 
	 * @param requestNode  empty
	 * @param responseNode 'status' returns 'OK' if successful, 'methods' as a
	 *                     string array of method names available, else 'Fehler' and
	 *                     a string array 'errors' containing error messages
	 * @return true if successully, else false
	 */
	public boolean getCallableMethods(ObjectNode requestNode, ObjectNode responseNode)
	{
		List<String> callableMethods = getCallableMethods();
		ArrayNode methods = responseNode.arrayNode();
		for (String method : callableMethods)
		{
			methods.add(method);
		}
		responseNode.set("methods", methods);
		return true;
	}

	/**
	 * Returns the sheet names and indexes of the given or active workbook
	 *
	 * @param requestNode  optional 'workbook' the name of the workbook, if not
	 *                     present, the active workbook is used
	 * @param responseNode 'status' returns 'OK' if successful, 'sheet' as an array
	 *                     of sheet names and 'index' as an array of sheet indexes,
	 *                     else 'Fehler' and a string array 'errors' containing
	 *                     error messages
	 * @return true if successful, else false
	 */
	public boolean getSheets(ObjectNode requestNode, ObjectNode responseNode)
	{
		Workbook workbook = getWorkbookIfPresent(requestNode, responseNode);
		boolean result = Objects.nonNull(workbook);
		if (result)
		{
			ArrayNode sheets = responseNode.arrayNode();
			ArrayNode indexes = responseNode.arrayNode();
			for (int i = 0; i < workbook.getNumberOfSheets(); i++)
			{
				sheets.add(workbook.getSheetName(i));
				indexes.add(i);
			}
			responseNode.set(Key.SHEET.key(), sheets);
			responseNode.set(Key.INDEX.key(), indexes);
		}
		return result;
	}

	/**
	 * Returns an array of the names of the present workbooks
	 * 
	 * @param requestNode  empty
	 * @param responseNode 'status' returns 'OK' 'workbook' an string array of the
	 *                     workbooks present if successful, else 'Fehler' and a
	 *                     string array 'errors' containing error messages
	 * @return true if successfully, else false
	 */
	public boolean getWorkbookNames(ObjectNode requestNode, ObjectNode responseNode)
	{
		ArrayNode workbookNames = responseNode.arrayNode();
		Set<String> keys = workbooks.keySet();
		keys.forEach(key -> workbookNames.add(key));
		responseNode.set(Key.WORKBOOK.key(), workbookNames);
		return true;
	}

	/**
	 * Releases workbook without saving it
	 * 
	 * @param requestNode  optional 'workbook' containing the (path)name of the
	 *                     workbook to release
	 * @param responseNode 'status' returns 'OK' if successful, else 'Fehler' and a
	 *                     string array 'errors' containing error messages
	 * @return true if successfully, else false
	 */
	public boolean releaseWorkbook(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = true;
		String wb = null;

		JsonNode workbookNode = requestNode.findPath(Key.WORKBOOK.key());
		if (workbookNode.isTextual())
		{
			wb = workbookNode.asText();
		}
		Workbook workbook = null;
		if (Objects.nonNull(wb))
		{
			try
			{
				workbook = workbooks.remove(wb);
				workbook.close();
				if (workbook == activeWorkbook)
				{
					activeWorkbook = null;
				}
			}
			catch (IOException e)
			{
				addErrorMessage(responseNode, "workbook could no be closed");
			}
		}
		else
		{
			workbook = activeWorkbook;
			Set<Entry<String, Workbook>> entries = workbooks.entrySet();
			Iterator<Entry<String, Workbook>> iterator = entries.iterator();
			while (iterator.hasNext())
			{
				Entry<String, Workbook> entry = iterator.next();
				if (entry.getValue() == workbook)
				{
					workbook = workbooks.remove(entry.getKey());
					activeWorkbook = null;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Releases all workbooks
	 * 
	 * @param requestNode  empty
	 * @param responseNode 'status' returns 'OK' if successful, else 'Fehler' and a
	 *                     string array 'errors' containing error messages
	 * @return true if successfully, else false
	 */
	public boolean releaseWorkbooks(ObjectNode requestNode, ObjectNode responseNode)
	{
		Set<Entry<String, Workbook>> entries = workbooks.entrySet();
		Iterator<Entry<String, Workbook>> iterator = entries.iterator();
		while (iterator.hasNext())
		{
			Entry<String, Workbook> entry = iterator.next();
			try
			{
				entry.getValue().close();
			}
			catch (IOException e)
			{
				
			}
		}
		workbooks.clear();
		activeWorkbook = null;
		return true;
	}

	/**
	 * Saves and releases the provided or active workbook
	 * 
	 * @param requestNode  optional 'workbook' containing the name of the workbook
	 * @param responseNode 'status' returns 'OK' if successful, else 'Fehler' and a
	 *                     string array 'errors' containing error messages
	 * @return true if successfully, else false
	 */
	public boolean saveAndReleaseWorkbook(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = saveWorkbook(requestNode, responseNode);
		if (result)
		{
			result = releaseWorkbook(requestNode, responseNode);
		}
		return result;
	}

	/**
	 * Saves the given workbook but does not release it, means it is further
	 * editable
	 * 
	 * @param requestNode  optional 'workbook' containing the name of the workbook,
	 *                     optional 'target' containing the (path)name where to save
	 *                     it. If 'target' is not provided, the (path)name of the
	 *                     workbook when created is used. When 'workbook' is not
	 *                     provided, the active workbook is used.
	 * @param responseNode 'status' returns 'OK' if successful, else 'Fehler' and a
	 *                     string array 'errors' containing error messages
	 * @return true if successfully, else false
	 */
	public boolean saveWorkbook(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = true;
		Workbook workbook = getWorkbookIfPresent(requestNode, responseNode);
		result = Objects.nonNull(workbook);
		if (result)
		{
			String wb = getWorkbookName(requestNode, workbook);
			if (Objects.nonNull(wb))
			{
				workbook = workbooks.get(wb);
				if (Objects.nonNull(workbook))
				{
					JsonNode targetNode = requestNode.findPath(Key.TARGET.key());
					if (targetNode.isTextual())
					{
						String target = targetNode.asText();
						result = saveWorkbookAs(responseNode, workbook, target);
					}
					else
					{
						result = saveWorkbook(responseNode, workbook);
					}
				}
				else
				{
					result = addErrorMessage(responseNode, "invalid argument '" + Key.WORKBOOK.key() + "'");
				}
			}
			else
			{
				result = addErrorMessage(responseNode, "invalid argument '" + Key.TARGET.key() + "'");
			}
		}
		return result;
	}

	public boolean setCells(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = true;
		Sheet sheet = getSheetIfPresent(requestNode, responseNode);
		result = Objects.nonNull(sheet);
		if (result)
		{
			JsonNode cellNode = requestNode.findPath(Key.CELL.key());
			JsonNode valuesNode = requestNode.findPath(Key.VALUES.key());
			if (cellNode.isMissingNode())
			{
				result = addErrorMessage(responseNode, "missing argument '" + Key.CELL.key() + "'");
			}
			else if (valuesNode.isMissingNode())
			{
				result = addErrorMessage(responseNode, "missing argument '" + Key.VALUES.key() + "'");
			}
			else if (valuesNode.isArray())
			{
				ArrayNode valuesArrayNode = ArrayNode.class.cast(valuesNode);
				if (cellNode.isArray())
				{
					if (cellNode.size() == valuesArrayNode.size())
					{
						result = setCells(responseNode, sheet, ArrayNode.class.cast(cellNode), valuesArrayNode);
					}
					else
					{
						result = addErrorMessage(responseNode, "size of 'cell' array does not equal to size of 'values' array");
					}
				}
				else
				{
					Direction direction = Direction.DEFAULT;
					JsonNode directionNode = requestNode.findPath(Key.DIRECTION.key());
					if (directionNode.isTextual())
					{
						try
						{
							direction = Direction.valueOf(directionNode.asText().toUpperCase());
						}
						catch (Exception e)
						{
							result = addErrorMessage(responseNode, "invalid argument 'direction'");
						}
					}
					else if (directionNode.isMissingNode())
					{
					}
					else
					{
						result = addErrorMessage(responseNode, "invalid argument 'direction'");
					}
					if (result)
					{
						if (cellNode.isTextual())
						{
							setCells(responseNode, sheet, TextNode.class.cast(cellNode), valuesArrayNode, direction);
						}
						else if (cellNode.isObject())
						{
							setCells(responseNode, sheet, ObjectNode.class.cast(cellNode), valuesArrayNode, direction);
						}
						else
						{
							result = addErrorMessage(responseNode, "invalid argument '" + Key.CELL.key() + "'");
						}
					}
				}
			}
			else
			{
				result = addErrorMessage(responseNode, "invalid argument '" + Key.VALUES.key() + "'");
			}
		}
		else
		{
			result = addErrorMessage(responseNode, "missing_argument '" + Key.SHEET.key() + "'");
		}
		return result;
	}

	public boolean setPrintSetup(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = true;

		Sheet sheet = getSheetIfPresent(requestNode, responseNode);
		if (Objects.nonNull(sheet))
		{
			JsonNode orientationNode = requestNode.findPath(Key.ORIENTATION.key());
			PrintOrientation orientation = PrintOrientation.DEFAULT;
			if (orientationNode.isTextual())
			{
				try
				{
					orientation = PrintOrientation.valueOf(orientationNode.asText().toUpperCase());
				}
				catch (Exception e)
				{
					result = addErrorMessage(responseNode, "invalid argument 'orientation'");
				}
			}
			switch (orientation)
			{
				case LANDSCAPE:
				{
					sheet.getPrintSetup().setLandscape(true);
				}
				case PORTRAIT:
				{
					sheet.getPrintSetup().setNoOrientation(false);
					sheet.getPrintSetup().setLandscape(false);
				}
				default:
				{
					sheet.getPrintSetup().setNoOrientation(true);
				}

			}

			int copies = 1;
			JsonNode copiesNode = requestNode.findPath(Key.COPIES.key());
			if (copiesNode.isInt())
			{
				copies = copiesNode.asInt();
			}
			if (copies > 0 && copies <= Short.MAX_VALUE)
			{
				sheet.getPrintSetup().setCopies((short) copies);
			}
		}
		return result;
	}

	public void setHeaders(ObjectNode requestNode, ObjectNode responseNode)
	{
		Sheet sheet = getSheetIfPresent(requestNode, responseNode);
		if (Objects.nonNull(sheet))
		{
			Header header = sheet.getHeader();
			JsonNode leftNode = requestNode.findPath(Key.LEFT.key());
			if (Objects.nonNull(leftNode) && (leftNode.isTextual()))
			{
				header.setLeft(leftNode.asText());

			}
			JsonNode centerNode = requestNode.findPath(Key.CENTER.key());
			if (Objects.nonNull(centerNode) && (centerNode.isTextual()))
			{
				header.setCenter(centerNode.asText());

			}
			JsonNode rightNode = requestNode.findPath(Key.RIGHT.key());
			if (Objects.nonNull(rightNode) && (rightNode.isTextual()))
			{
				header.setRight(rightNode.asText());

			}
		}
	}

	public void setFooters(ObjectNode requestNode, ObjectNode responseNode)
	{
		Sheet sheet = getSheetIfPresent(requestNode, responseNode);
		if (Objects.nonNull(sheet))
		{
			Footer footer = sheet.getFooter();
			JsonNode leftNode = requestNode.findPath(Key.LEFT.key());
			if (Objects.nonNull(leftNode) && (leftNode.isTextual()))
			{
				footer.setLeft(leftNode.asText());

			}
			JsonNode centerNode = requestNode.findPath(Key.CENTER.key());
			if (Objects.nonNull(centerNode) && (centerNode.isTextual()))
			{
				footer.setCenter(centerNode.asText());

			}
			JsonNode rightNode = requestNode.findPath(Key.RIGHT.key());
			if (Objects.nonNull(rightNode) && (rightNode.isTextual()))
			{
				footer.setRight(rightNode.asText());

			}
		}
	}

	protected Sheet getActiveSheet(ObjectNode responseNode)
	{
		Sheet sheet = null;
		if (activeSheetPresent())
		{
			sheet = activeWorkbook.getSheetAt(activeWorkbook.getActiveSheetIndex());
		}
		else
		{
			addErrorMessage(responseNode, "missing_active_sheet");
		}
		return sheet;
	}

	protected boolean activateSheet(Sheet sheet)
	{
		boolean result = activateWorkbook(sheet.getWorkbook());
		if (result)
		{
			activeWorkbook.setActiveSheet(activeWorkbook.getSheetIndex(sheet));
		}
		return result;
	}

	protected boolean activateWorkbook(Workbook workbook)
	{
		boolean result = Objects.nonNull(workbook);
		if (result)
		{
			if (workbook != activeWorkbook)
			{
				activeWorkbook = workbook;
			}
		}
		return result;
	}

	protected boolean activeSheetPresent()
	{
		boolean present = Objects.nonNull(activeWorkbook);
		if (present)
		{
			present = activeWorkbook.getActiveSheetIndex() > -1;
		}
		return present;
	}

	public boolean applyFontStyles(ObjectNode requestNode, ObjectNode responseNode)
	{
		Sheet sheet = getSheetIfPresent(requestNode, responseNode);
		boolean result = Objects.nonNull(sheet);
		if (result)
		{
			CellRangeAddress rangeAddress = null;
			JsonNode cellNode = requestNode.findPath(Key.CELL.key());
			if (Objects.nonNull(cellNode) && !cellNode.isMissingNode())
			{
				CellAddress cellAddress = getCellAddress(responseNode, cellNode, Key.CELL.key());
				rangeAddress = getCellRangeAddress(responseNode, cellAddress);
			}
			else
			{
				JsonNode rangeNode = requestNode.findPath(Key.RANGE.key());
				if (Objects.nonNull(rangeNode) && !rangeNode.isMissingNode())
				{
					rangeAddress = getCellRangeAddress(responseNode, rangeNode);
				}
				else
				{
					result = addErrorMessage(responseNode, "missing_argument '" + Key.RANGE.key() + "'");
				}
			}
			if (result && Objects.nonNull(rangeAddress))
			{
				Iterator<CellAddress> cellAddresses = rangeAddress.iterator();
				while (cellAddresses.hasNext())
				{
					CellAddress cellAddress = cellAddresses.next();
					Cell cell = getOrCreateCell(responseNode, sheet, cellAddress);
					CellStyle cellStyle = cell.getCellStyle();
					MergedCellStyle mcs = new MergedCellStyle(cellStyle);
					int fontIndex = cellStyle.getFontIndex();
					Font font = sheet.getWorkbook().getFontAt(fontIndex);
					MergedFont m = new MergedFont(font);
					m.applyRequestedFontStyles(requestNode, responseNode);
					font = getFont(sheet, m);
					if (font.getIndex() != fontIndex)
					{
						mcs.setFontIndex(font.getIndex());
						cellStyle = getCellStyle(sheet, mcs);
						cell.setCellStyle(cellStyle);
					}

					responseNode.put(Key.INDEX.key(), font.getIndex());
				}
			}
		}
		return result;
	}

	public boolean applyCellStyles(ObjectNode requestNode, ObjectNode responseNode)
	{
		Sheet sheet = getSheetIfPresent(requestNode, responseNode);
		boolean result = Objects.nonNull(sheet);
		if (result)
		{
			CellRangeAddress rangeAddress = null;
			JsonNode cellNode = requestNode.findPath(Key.CELL.key());
			if (Objects.nonNull(cellNode) && !cellNode.isMissingNode())
			{
				CellAddress cellAddress = getCellAddress(responseNode, cellNode, Key.CELL.key());
				rangeAddress = getCellRangeAddress(responseNode, cellAddress);
			}
			else
			{
				JsonNode rangeNode = requestNode.findPath(Key.RANGE.key());
				if (Objects.nonNull(rangeNode) && !rangeNode.isMissingNode())
				{
					rangeAddress = getCellRangeAddress(responseNode, rangeNode);
				}
				else
				{
					result = addErrorMessage(responseNode, "missing_argument '" + Key.RANGE.key() + "'");
				}
			}
			if (result && Objects.nonNull(rangeAddress))
			{
				Iterator<CellAddress> cellAddresses = rangeAddress.iterator();
				while (cellAddresses.hasNext())
				{
					CellAddress cellAddress = cellAddresses.next();
					Cell cell = getOrCreateCell(responseNode, sheet, cellAddress);
					MergedCellStyle m = new MergedCellStyle(cell.getCellStyle());
					result = m.applyRequestedStyles(requestNode, responseNode);
					if (result)
					{
						CellStyle cellStyle = getCellStyle(sheet, m);
						cell.setCellStyle(cellStyle);
					}
					else
					{
						break;
					}
				}
			}
		}
		return result;
	}

	public boolean autoSizeColumns(ObjectNode requestNode, ObjectNode responseNode)
	{
		Sheet sheet = getSheetIfPresent(requestNode, responseNode);
		boolean result = Objects.nonNull(sheet);
		if (result)
		{
			CellRangeAddress cellRangeAddress = null;
			JsonNode rangeNode = requestNode.findPath(Key.RANGE.key());
			if (Objects.nonNull(rangeNode) && !rangeNode.isMissingNode())
			{
				cellRangeAddress = getCellRangeAddress(responseNode, rangeNode);
				if (Objects.isNull(cellRangeAddress))
				{
					addErrorMessage(responseNode, "invalid argument '" + Key.RANGE.key());
				}
			}
			else
			{
				JsonNode cellNode = requestNode.findPath(Key.CELL.key());
				if (Objects.nonNull(cellNode) && !cellNode.isMissingNode())
				{
					CellAddress cellAddress = getCellAddress(responseNode, cellNode, Key.CELL.key());
					if (Objects.nonNull(cellAddress))
					{
						cellRangeAddress = getCellRangeAddress(responseNode, cellAddress);
					}
					else
					{
						addErrorMessage(responseNode, "invalid argument '" + Key.CELL.key());
					}
				}
				else
				{
					addErrorMessage(responseNode, "missing argument one of '" + Key.RANGE.key() + "', '" + Key.CELL.key() + "'");
				}
			}
			result = Objects.nonNull(cellRangeAddress);
			if (result)
			{
				int leftCol = cellRangeAddress.getFirstColumn();
				int rightCol = cellRangeAddress.getLastColumn();
				for (int colIndex = leftCol; colIndex <= rightCol; colIndex++)
				{
					sheet.autoSizeColumn(colIndex);
				}
			}
		}
		return result;
	}

	public boolean rotateCells(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = IntNode.class.isInstance(requestNode.get(Key.ROTATION.key()));
		if (result)
		{
			int rotation = IntNode.class.cast(requestNode.get(Key.ROTATION.key())).asInt();
			Sheet sheet = getSheetIfPresent(requestNode, responseNode);
			if (Objects.nonNull(sheet))
			{
				CellRangeAddress rangeAddress = null;
				JsonNode cellNode = requestNode.findPath(Key.CELL.key());
				JsonNode rangeNode = requestNode.findPath(Key.RANGE.key());
				if (Objects.nonNull(cellNode))
				{
					CellAddress cellAddress = getCellAddress(responseNode, cellNode, Key.CELL.key());
					rangeAddress = getCellRangeAddress(responseNode, cellAddress);
				}
				else if (Objects.nonNull(rangeNode))
				{
					rangeAddress = getCellRangeAddress(responseNode, rangeNode);
				}
				else
				{
					addErrorMessage(responseNode, "missing argument '" + Key.RANGE.key() + "'");
				}
				if (Objects.nonNull(rangeAddress))
				{
					Iterator<CellAddress> cellAddresses = rangeAddress.iterator();
					while (cellAddresses.hasNext())
					{
						CellAddress cellAddress = cellAddresses.next();
						Row row = sheet.getRow(cellAddress.getRow());
						if (Objects.nonNull(row))
						{
							Cell cell = row.getCell(cellAddress.getColumn());
							if (Objects.nonNull(cell))
							{
								CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
								cellStyle.setRotation((short) rotation);
								cell.setCellStyle(cellStyle);
							}
						}
					}
				}
				else
				{
					addErrorMessage(responseNode, "missing_argument '" + Key.RANGE.key() + "'");
				}
			}
			else
			{
				addErrorMessage(responseNode, "missing_argument '" + Key.SHEET.key() + "'");
			}
		}
		else
		{
			addErrorMessage(responseNode, "missing_argument '" + Key.ROTATION.key() + "'");
		}
		return result;
	}

	protected Sheet createSheet(ObjectNode responseNode, Workbook workbook, JsonNode sheetNode)
	{
		Sheet sheet = null;
		if (Objects.nonNull(workbook))
		{
			if (sheetNode.isTextual())
			{
				sheet = workbook.getSheet(sheetNode.asText());
				if (Objects.isNull(sheet))
				{
					sheet = workbook.createSheet(sheetNode.asText());
				}
				else
				{
					addErrorMessage(responseNode, "sheet '" + sheetNode.asText() + "' already exists");
				}
			}
			else if (Objects.isNull(sheetNode) || sheetNode.isMissingNode())
			{
				addErrorMessage(responseNode, "missing argument 'sheet'");
			}
			else
			{
				addErrorMessage(responseNode, "invalid argument 'sheet'");
			}
		}
		else
		{
			addErrorMessage(responseNode, "missing workbook");
		}
		return sheet;
	}

	protected boolean setCells(ObjectNode responseNode, Sheet sheet, TextNode cellNode, ArrayNode valuesNode, Direction direction)
	{
		boolean result = true;
		try
		{
			CellAddress cellAddress = getCellAddress(responseNode, cellNode.asText());
			result = setCells(responseNode, sheet, cellAddress, valuesNode, direction);
		}
		catch (Exception e)
		{
			result = addErrorMessage(responseNode, "invalid argument '" + cellNode.asText() + "'");
		}
		return result;
	}

	protected boolean setCells(ObjectNode responseNode, Sheet sheet, ObjectNode cellNode, ArrayNode valuesNode, Direction direction)
	{
		boolean result = true;
		try
		{
			CellAddress cellAddress = getCellAddress(responseNode, cellNode);
			result = setCells(responseNode, sheet, cellAddress, valuesNode, direction);
		}
		catch (Exception e)
		{
			result = addErrorMessage(responseNode, "invalid argument '" + cellNode.asText() + "'");
		}
		return result;
	}

	protected boolean setCells(ObjectNode responseNode, Sheet sheet, ArrayNode cellNode, ArrayNode valuesNode)
	{
		boolean result = true;
		for (int i = 0; i < cellNode.size(); i++)
		{
			CellAddress cellAddress = getCellAddress(responseNode, cellNode.get(i), Key.CELL.key());
			JsonNode valueNode = valuesNode.get(i);
			result = setCell(responseNode, sheet, cellAddress, valueNode);

		}
		return result;
	}

	protected boolean setCells(ObjectNode responseNode, Sheet sheet, CellAddress cellAddress, ArrayNode valuesNode, Direction direction)
	{
		boolean result = Objects.nonNull(cellAddress);
		if (result)
		{
			if (valuesNode.size() > 0)
			{
				if (direction.validRange(responseNode, sheet.getWorkbook(), cellAddress, valuesNode.size()))
				{
					for (int i = 0; i < valuesNode.size(); i++)
					{
						JsonNode valueNode = valuesNode.get(i);
						result = setCell(responseNode, sheet, cellAddress, valueNode);
						if (result)
						{
							cellAddress = direction.nextIndex(cellAddress);
						}
						else
						{
							break;
						}
					}
				}
			}
			else
			{
				result = addErrorMessage(responseNode, "invalid_argument '" + Key.VALUES.key() + "'");
			}
		}
		return result;
	}

	protected boolean setCell(ObjectNode responseNode, Sheet sheet, CellAddress cellAddress, JsonNode valueNode)
	{
		boolean result = true;
		JsonNodeType nodeType = valueNode.getNodeType();
		Cell cell = getOrCreateCell(responseNode, sheet, cellAddress);
		if (nodeType.equals(JsonNodeType.NUMBER))
		{
			cell.setCellValue(valueNode.asDouble());
		}
		else if (nodeType.equals(JsonNodeType.STRING))
		{
			if (!valueNode.asText().trim().isEmpty())
			{
				try
				{
					cell.setCellValue(DateUtil.parseDateTime(valueNode.asText()));
					MergedCellStyle mcs = new MergedCellStyle(cell.getCellStyle());
					int formatIndex = BuiltinFormats.getBuiltinFormat("m/d/yy");
					mcs.setDataFormat((short) formatIndex);
					CellStyle cellStyle = getCellStyle(sheet, mcs);
					cell.setCellStyle(cellStyle);
				}
				catch (Exception tpe)
				{
					try
					{
						Date date = DateFormat.getDateInstance().parse(valueNode.asText());
						cell.setCellValue(DateUtil.getExcelDate(date));
						MergedCellStyle mcs = new MergedCellStyle(cell.getCellStyle());
						int formatIndex = BuiltinFormats.getBuiltinFormat("m/d/yy");
						mcs.setDataFormat((short) formatIndex);
						CellStyle cellStyle = getCellStyle(sheet, mcs);
						cell.setCellStyle(cellStyle);
					}
					catch (ParseException dpe)
					{
						try
						{
							double time = DateUtil.convertTime(valueNode.asText());
							cell.setCellValue(time);
							MergedCellStyle mcs = new MergedCellStyle(cell.getCellStyle());
							int formatIndex = BuiltinFormats.getBuiltinFormat("h:mm");
							mcs.setDataFormat((short) formatIndex);
							CellStyle cellStyle = getCellStyle(sheet, mcs);
							cell.setCellStyle(cellStyle);
						}
						catch (Exception e)
						{
							try
							{
								cell.setCellFormula(valueNode.asText());
								FormulaEvaluator evaluator = activeWorkbook.getCreationHelper()
										.createFormulaEvaluator();
								CellType cellType = evaluator.evaluateFormulaCell(cell);
								System.out.println(cellType);
							}
							catch (FormulaParseException fpe)
							{
								setRichTextString(responseNode, cell, valueNode.asText());
							}
						}
					}
				}
			}
		}
		else if (nodeType.equals(JsonNodeType.BOOLEAN))
		{
			cell.setCellValue(valueNode.asBoolean());
		}
		else
		{
			// TODO Other types?
			System.out.println();
		}
		return result;
	}

	protected Workbook createWorkbook(ObjectNode responseNode, String name)
	{
		Workbook workbook = null;
		if (!name.trim().isEmpty())
		{
			if (name.endsWith(".xls"))
			{
				workbooks.put(name, new HSSFWorkbook());
			}
			else
			{
				workbooks.put(name, new XSSFWorkbook());
			}
		}
		else
		{
			addErrorMessage(responseNode, "invalid_argument '" + Key.WORKBOOK.key() + "'");
		}
		return workbook;
	}

	protected void setRichTextString(ObjectNode responseNode, Cell cell, String value)
	{
		if (XSSFCell.class.isInstance(cell))
		{
			cell.setCellValue(new XSSFRichTextString(value));
		}
		else
		{
			cell.setCellValue(new HSSFRichTextString(value));
		}
	}

	protected List<String> getCallableMethods()
	{
		List<String> callableMethods = new ArrayList<String>();
		Method[] methods = this.getClass().getDeclaredMethods();
		for (Method method : methods)
		{
			if (Modifier.isPublic(method.getModifiers()))
			{
				Parameter[] parameters = method.getParameters();
				if (parameters.length == 2)
				{
					boolean equals = true;
					for (Parameter parameter : parameters)
					{
						if (!parameter.getType().equals(ObjectNode.class))
						{
							equals = false;
						}
					}
					if (equals)
					{
						callableMethods.add(this.getClass().getSimpleName() + "." + method.getName());
					}
				}
			}
		}
		return callableMethods;
	}

	protected CellAddress getCellAddress(ObjectNode responseNode, int row, int col)
	{
		CellAddress cellAddress = null;
		try
		{
			cellAddress = new CellAddress(row, col);
		}
		catch (Exception e)
		{
			addErrorMessage(responseNode, "invalid_argument '" + " + Key.CELL.key() + " + "'");
		}
		return cellAddress;
	}

	protected CellAddress getCellAddress(ObjectNode responseNode, JsonNode cellNode, String key)
	{
		CellAddress cellAddress = null;
		if (cellNode.isTextual())
		{
			cellAddress = getCellAddress(responseNode, cellNode.asText());
		}
		else if (cellNode.isObject())
		{
			cellAddress = getCellAddress(responseNode, ObjectNode.class.cast(cellNode));
		}
		else if (Objects.isNull(cellNode) || cellNode.isMissingNode())
		{
			addErrorMessage(responseNode, "missing argument '" + key + "'");
		}
		else
		{
			addErrorMessage(responseNode, "invalid argument '" + key + "'");
		}
		return cellAddress;
	}

	protected CellAddress getCellAddress(ObjectNode responseNode, String cell)
	{
		CellAddress cellAddress = null;
		try
		{
			cellAddress = new CellAddress(cell);
		}
		catch (Exception e)
		{
			addErrorMessage(responseNode, "invalid cell address '" + cell + "'");
		}
		return cellAddress;
	}

	protected CellAddress getCellAddress(ObjectNode responseNode, ObjectNode cellNode)
	{
		CellAddress cellAddress = null;
		if (Objects.nonNull(cellNode) && !cellNode.isMissingNode())
		{
			JsonNode rowNode = cellNode.findPath(Key.ROW.key());
			if (rowNode.isInt())
			{
				int rowIndex = rowNode.asInt();
				JsonNode colNode = cellNode.findPath(Key.COL.key());
				if (colNode.isInt())
				{
					int colIndex = colNode.asInt();
					cellAddress = new CellAddress(rowIndex, colIndex);
				}
				else if (colNode.isMissingNode())
				{
					addErrorMessage(responseNode, "missing argument '" + Key.COL.key());
				}
				else
				{
					addErrorMessage(responseNode, "invalid argument '" + Key.COL.key());
				}
			}
			else if (rowNode.isMissingNode())
			{
				addErrorMessage(responseNode, "missing argument '" + Key.ROW.key());
			}
			else
			{
				addErrorMessage(responseNode, "invalid argument '" + Key.ROW.key());
			}
		}
		else
		{
			addErrorMessage(responseNode, "missing argument '" + Key.CELL.key());
		}
		return cellAddress;
	}

	protected CellRangeAddress getCellRangeAddress(ObjectNode responseNode, CellAddress address)
	{
		return getCellRangeAddress(responseNode, address, address);
	}

	protected CellRangeAddress getCellRangeAddress(ObjectNode responseNode, CellAddress topLeftAddress, CellAddress bottomRightAddress)
	{
		CellRangeAddress cellRangeAddress = null;
		if (Objects.nonNull(topLeftAddress))
		{
			int topRow = topLeftAddress.getRow();
			int leftCol = topLeftAddress.getColumn();
			if (Objects.nonNull(bottomRightAddress))
			{
				int bottomRow = bottomRightAddress.getRow();
				int rightCol = bottomRightAddress.getColumn();
				cellRangeAddress = getCellRangeAddress(topRow, bottomRow, leftCol, rightCol);
			}
			else
			{
				addErrorMessage(responseNode, "missing bottom right values");
			}
		}
		else
		{
			addErrorMessage(responseNode, "missing top left values");
		}
		return cellRangeAddress;
	}

	protected CellRangeAddress getCellRangeAddress(int topRow, int bottomRow, int leftCol, int rightCol)
	{
		return new CellRangeAddress(Math.min(topRow, bottomRow), Math.max(topRow, bottomRow),
				Math.min(leftCol, rightCol), Math.max(leftCol, rightCol));
	}

	protected CellRangeAddress getCellRangeAddress(ObjectNode responseNode, JsonNode rangeNode)
	{
		CellRangeAddress rangeAddress = null;
		if (Objects.nonNull(rangeNode) && !rangeNode.isMissingNode())
		{
			if (rangeNode.isTextual())
			{
				rangeAddress = getCellRangeAddress(responseNode, rangeNode.asText());
			}
			else if (rangeNode.isObject())
			{
				rangeAddress = getCellRangeAddress(responseNode, ObjectNode.class.cast(rangeNode));
			}
			else
			{
				addErrorMessage(responseNode, "invalid argument '" + Key.RANGE.key() + "'");
			}
		}
		else
		{
			addErrorMessage(responseNode, "missing argument '" + Key.RANGE.key() + "'");
		}
		return rangeAddress;
	}

	protected CellRangeAddress getCellRangeAddress(ObjectNode responseNode, String range)
	{
		CellRangeAddress rangeAddress = null;
		if (Objects.nonNull(range))
		{
			try
			{
				String[] cells = range.split(":");
				switch (cells.length)
				{
					case 1:
					{
						CellAddress address = new CellAddress(cells[0]);
						rangeAddress = getCellRangeAddress(responseNode, address, address);
						break;
					}
					case 2:
					{
						CellAddress topLeftAddress = new CellAddress(cells[0]);
						CellAddress bottomRightAddress = new CellAddress(cells[1]);
						rangeAddress = getCellRangeAddress(responseNode, topLeftAddress, bottomRightAddress);
						break;
					}
					default:
					{
						addErrorMessage(responseNode, "invalid_argument '" + Key.RANGE.key() + "'");
					}
				}
			}
			catch (Exception e)
			{
				addErrorMessage(responseNode, "invalid_argument '" + Key.RANGE.key() + "'");
			}
		}
		else
		{
			addErrorMessage(responseNode, "invalid_argument '" + Key.RANGE.key() + "'");
		}
		return rangeAddress;
	}

	protected CellRangeAddress getCellRangeAddress(ObjectNode responseNode, ObjectNode rangeNode)
	{
		CellRangeAddress cellRangeAddress = null;
		if (Objects.nonNull(rangeNode))
		{
			CellAddress topLeftAddress = null;
			CellAddress bottomRightAddress = null;
			JsonNode topLeftNode = rangeNode.get(Key.TOP_LEFT.key());
			if (Objects.nonNull(topLeftNode) && !topLeftNode.isMissingNode())
			{
				topLeftAddress = getCellAddress(responseNode, topLeftNode, Key.TOP_LEFT.key());
				if (Objects.isNull(topLeftAddress))
				{
					addErrorMessage(responseNode, "invalid argument '" + Key.TOP_LEFT.key() + "'");
				}
			}
			else
			{
				JsonNode topNode = rangeNode.get(Key.TOP.key());
				if (Objects.nonNull(topNode) && !topNode.isMissingNode())
				{
					if (IntNode.class.isInstance(topNode))
					{
						int top = topNode.asInt();
						JsonNode leftNode = rangeNode.get(Key.LEFT.key());
						if (Objects.nonNull(leftNode) && !leftNode.isMissingNode())
						{
							if (IntNode.class.isInstance(leftNode))
							{
								int left = leftNode.asInt();
								topLeftAddress = new CellAddress(top, left);
							}
							else
							{
								addErrorMessage(responseNode, "invalid argument '" + Key.LEFT.key() + "'");
							}
						}
						else
						{
							addErrorMessage(responseNode, "missing argument '" + Key.LEFT.key() + "'");
						}
					}
					else
					{
						addErrorMessage(responseNode, "invalid argument '" + Key.TOP.key() + "'");
					}
				}
				else
				{
					addErrorMessage(responseNode, "missing argument '" + Key.TOP.key() + "'");
				}
			}
			if (Objects.nonNull(topLeftAddress))
			{
				JsonNode bottomRightNode = rangeNode.get(Key.BOTTOM_RIGHT.key());
				if (Objects.nonNull(bottomRightNode) && !bottomRightNode.isMissingNode())
				{
					bottomRightAddress = getCellAddress(responseNode, bottomRightNode, Key.BOTTOM_RIGHT.key());
					if (Objects.isNull(bottomRightAddress))
					{
						addErrorMessage(responseNode, "invalid argument '" + Key.BOTTOM_RIGHT.key() + "'");
					}
				}
				else
				{
					JsonNode bottomNode = rangeNode.get(Key.BOTTOM.key());
					if (Objects.nonNull(bottomNode) && !bottomNode.isMissingNode())
					{
						if (IntNode.class.isInstance(bottomNode))
						{
							int bottom = bottomNode.asInt();
							JsonNode rightNode = rangeNode.get(Key.RIGHT.key());
							if (Objects.nonNull(rightNode) && !rightNode.isMissingNode())
							{
								if (IntNode.class.isInstance(rightNode))
								{
									int right = rightNode.asInt();
									bottomRightAddress = new CellAddress(bottom, right);
								}
								else
								{
									addErrorMessage(responseNode, "invalid argument '" + Key.RIGHT.key() + "'");
								}
							}
							else
							{
								addErrorMessage(responseNode, "missing argument '" + Key.RIGHT.key() + "'");
							}
						}
						else
						{
							addErrorMessage(responseNode, "invalid argument '" + Key.BOTTOM.key() + "'");
						}
					}
					else
					{
						addErrorMessage(responseNode, "missing argument '" + Key.BOTTOM.key() + "'");
					}
				}
			}
			if (Objects.nonNull(topLeftAddress))
			{
				if (Objects.nonNull(bottomRightAddress))
				{
					cellRangeAddress = getCellRangeAddress(responseNode, topLeftAddress, bottomRightAddress);
				}
				else
				{
					addErrorMessage(responseNode, "missing argument '" + Key.BOTTOM_RIGHT.key() + "'");
				}
			}
			else
			{
				addErrorMessage(responseNode, "missing argument '" + Key.TOP_LEFT.key() + "'");
			}
		}
		else
		{
			addErrorMessage(responseNode, "missing_argument '" + Key.RANGE.key() + "'");
		}
		return cellRangeAddress;
	}

	protected Cell getOrCreateCell(ObjectNode responseNode, Sheet sheet, CellAddress cellAddress)
	{
		Cell cell = null;
		Row row = null;
		if (isWithinSheetRange(cellAddress))
		{
			row = getOrCreateRow(responseNode, sheet, cellAddress);
			cell = getOrCreateCell(row, cellAddress.getColumn());
		}
		else
		{
			addErrorMessage(responseNode, "celladdress_out_of_range");
		}
		return cell;
	}

	protected Cell getOrCreateCell(ObjectNode responseNode, Sheet sheet, String address)
	{
		return getOrCreateCell(responseNode, sheet, new CellAddress(address));
	}

	protected Cell getOrCreateCell(Row row, int columnIndex)
	{
		Cell cell = null;
		if (Objects.nonNull(row))
		{
			cell = row.getCell(columnIndex);
			if (Objects.isNull(cell))
			{
				cell = row.createCell(columnIndex);
			}
		}
		return cell;
	}

	protected Row getOrCreateRow(ObjectNode responseNode, Sheet sheet, CellAddress cellAddress)
	{
		Row row = null;
		if (Objects.nonNull(sheet))
		{
			if (isWithinSheetRange(cellAddress))
			{
				row = sheet.getRow(cellAddress.getRow());
				if (Objects.isNull(row))
				{
					row = sheet.createRow(cellAddress.getRow());
				}
			}
		}
		return row;
	}

	protected Row getOrCreateRow(Sheet sheet, int rowIndex)
	{
		Row row = null;
		if (Objects.nonNull(sheet))
		{
			if (isWithinSheetRange(new CellAddress(rowIndex, 0)))
			{
				row = sheet.getRow(rowIndex);
				if (Objects.isNull(row))
				{
					row = sheet.createRow(rowIndex);
				}
			}
		}
		return row;
	}

	protected Workbook getWorkbookIfPresent(ObjectNode requestNode, ObjectNode responseNode)
	{
		Workbook workbook = null;
		JsonNode workbookNode = requestNode.findPath(Key.WORKBOOK.key());
		if (workbookNode.isTextual())
		{
			workbook = workbooks.get(workbookNode.asText());
			if (Objects.isNull(workbook))
			{
				addErrorMessage(responseNode, "invalid argument '" + Key.WORKBOOK.key() + "'");
			}
		}
		else if (workbookNode.isMissingNode())
		{
			if (Objects.nonNull(this.activeWorkbook))
			{
				workbook = activeWorkbook;
			}
			else
			{
				addErrorMessage(responseNode, "missing argument '" + Key.WORKBOOK.key() + "'");
			}
		}
		else
		{
			addErrorMessage(responseNode, "invalid argument '" + Key.WORKBOOK.key() + "'");
		}
		return workbook;
	}

	protected Sheet getSheetIfPresent(ObjectNode parentNode, ObjectNode responseNode)
	{
		Sheet sheet = null;
		Workbook workbook = getWorkbookIfPresent(parentNode, responseNode);
		if (Objects.nonNull(workbook))
		{
			JsonNode sheetNode = parentNode.findPath(Key.SHEET.key());
			if (Objects.nonNull(sheetNode) && !sheetNode.isMissingNode())
			{
				if (TextNode.class.isInstance(sheetNode))
				{
					sheet = workbook.getSheet(sheetNode.asText());
				}
				else if (IntNode.class.isInstance(sheetNode))
				{
					sheet = workbook.getSheetAt(sheetNode.asInt());
				}
				else
				{
					addErrorMessage(responseNode, "invalid_argument '" + Key.SHEET.key() + "'");
				}
			}
			else
			{
				sheet = workbook.getSheetAt(workbook.getActiveSheetIndex());
			}
		}
		return sheet;
	}

	protected Sheet findSheet(ObjectNode responseNode, Workbook workbook, JsonNode sheetNode)
	{
		Sheet sheet = null;
		if (Objects.nonNull(workbook))
		{
			if (Objects.nonNull(sheetNode) && !sheetNode.isMissingNode())
			{
				if (TextNode.class.isInstance(sheetNode))
				{
					sheet = workbook.getSheet(sheetNode.asText());
				}
				else if (IntNode.class.isInstance(sheetNode))
				{
					int sheetIndex = sheetNode.asInt();
					if (workbook.getNumberOfSheets() > sheetIndex)
					{
						sheet = workbook.getSheetAt(sheetNode.asInt());
					}
					else
					{
						addErrorMessage(responseNode, "missing sheet '" + sheetIndex + "'");
					}
				}
				else
				{
					addErrorMessage(responseNode, "invalid_argument '" + Key.SHEET.key() + "'");
				}
			}
			else
			{
				addErrorMessage(responseNode, "missing_argument '" + Key.SHEET.key() + "'");
			}
		}
		return sheet;
	}

	protected boolean isWithinSheetRange(CellRangeAddress cellRangeAddress)
	{
		CellAddress firstAddress = new CellAddress(cellRangeAddress.getFirstRow(), cellRangeAddress.getFirstColumn());
		CellAddress lastAddress = new CellAddress(cellRangeAddress.getLastRow(), cellRangeAddress.getLastColumn());
		return isWithinSheetRange(firstAddress) && isWithinSheetRange(lastAddress);
	}

	protected boolean isWithinSheetRange(CellAddress cellAddress)
	{
		return cellAddress.getRow() > -1
				&& cellAddress.getRow() < activeWorkbook.getSpreadsheetVersion().getMaxRows()
				&& cellAddress.getColumn() > -1
				&& cellAddress.getColumn() < activeWorkbook.getSpreadsheetVersion().getMaxColumns();
	}

	protected boolean isWithinSheetRange(Cell cell)
	{
		return isWithinSheetRange(new CellAddress(cell));
	}

	protected boolean isWithinSheetRange(Row row)
	{
		return row.getRowNum() < activeWorkbook.getSpreadsheetVersion().getMaxRows();
	}

	protected void releaseWorkbooks()
	{
		workbooks.clear();
		activeWorkbook = null;
	}

	protected SpreadsheetVersion getVersion()
	{
		SpreadsheetVersion version = null;
		if (workbooks.size() > 0)
		{
			version = workbooks.values().iterator().next().getSpreadsheetVersion();
		}
		return version;
	}

	protected FormulaParsingWorkbook getFormulaParsingWorkbook(Sheet sheet)
	{
		FormulaParsingWorkbook workbookWrapper = null;
		if (XSSFSheet.class.isInstance(sheet))
		{
			workbookWrapper = XSSFEvaluationWorkbook.create(XSSFSheet.class.cast(sheet).getWorkbook());
		}
		else
		{
			workbookWrapper = HSSFEvaluationWorkbook.create(HSSFSheet.class.cast(sheet).getWorkbook());
		}
		return workbookWrapper;
	}

	protected FormulaRenderingWorkbook getFormulaRenderingWorkbook(Sheet sheet)
	{
		FormulaRenderingWorkbook workbookWrapper = null;
		if (XSSFSheet.class.isInstance(sheet))
		{
			workbookWrapper = XSSFEvaluationWorkbook.create(XSSFSheet.class.cast(sheet).getWorkbook());
		}
		else
		{
			workbookWrapper = HSSFEvaluationWorkbook.create(HSSFSheet.class.cast(sheet).getWorkbook());
		}
		return workbookWrapper;
	}

	protected JsonNode findNode(ObjectNode requestNode, String name)
	{
		return requestNode.findParent(name);
	}

	protected void copyCell(Cell sourceCell, Cell targetCell)
	{
		CellType cellType = sourceCell.getCellType();
		switch (cellType)
		{
			case BLANK:
				break;
			case _NONE:
				break;
			case FORMULA:
			{
				String formula = sourceCell.getCellFormula();
				CellAddress sourceCellAddress = new CellAddress(sourceCell);
				CellAddress targetCellAddress = new CellAddress(targetCell);
				int rowDiff = targetCellAddress.getRow() - sourceCellAddress.getRow();
				int colDiff = targetCellAddress.getColumn() - sourceCellAddress.getColumn();
				formula = copyFormula(sourceCell.getRow().getSheet(), formula, rowDiff, colDiff);
				targetCell.setCellFormula(formula);
				break;
			}
			default:
			{
				CellUtil.copyCell(sourceCell, targetCell, null, null);
				break;
			}
		}
	}

	protected String copyFormula(Sheet sheet, String formula, int rowDiff, int colDiff)
	{
		FormulaParsingWorkbook workbookWrapper = getFormulaParsingWorkbook(sheet);
		Ptg[] ptgs = FormulaParser.parse(formula, workbookWrapper, FormulaType.CELL,
				sheet.getWorkbook().getSheetIndex(sheet));
		for (int i = 0; i < ptgs.length; i++)
		{
			if (ptgs[i] instanceof RefPtgBase)
			{ // base class for cell references
				RefPtgBase ref = (RefPtgBase) ptgs[i];
				if (ref.isRowRelative())
				{
					ref.setRow(ref.getRow() + rowDiff);
				}
				if (ref.isColRelative())
				{
					ref.setColumn(ref.getColumn() + colDiff);
				}
			}
			else if (ptgs[i] instanceof AreaPtgBase)
			{ // base class for range references
				AreaPtgBase ref = (AreaPtgBase) ptgs[i];
				if (ref.isFirstColRelative())
				{
					ref.setFirstColumn(ref.getFirstColumn() + colDiff);
				}
				if (ref.isLastColRelative())
				{
					ref.setLastColumn(ref.getLastColumn() + colDiff);
				}
				if (ref.isFirstRowRelative())
				{
					ref.setFirstRow(ref.getFirstRow() + rowDiff);
				}
				if (ref.isLastRowRelative())
				{
					ref.setLastRow(ref.getLastRow() + rowDiff);
				}
			}
		}

		formula = FormulaRenderer.toFormulaString(getFormulaRenderingWorkbook(sheet), ptgs);
		return formula;
	}

	protected CellRangeAddress getSourceRangeAddress(ObjectNode responseNode, JsonNode sourceNode)
	{
		CellRangeAddress rangeAddress = null;
		if (Objects.nonNull(sourceNode) && !sourceNode.isMissingNode())
		{
			rangeAddress = getCellRangeAddress(responseNode, sourceNode);
			if (Objects.isNull(rangeAddress))
			{
				CellAddress cellAddress = getCellAddress(responseNode, sourceNode, Key.CELL.key());
				rangeAddress = getCellRangeAddress(responseNode, cellAddress);
				if (Objects.isNull(rangeAddress))
				{
					addErrorMessage(responseNode, "missing_argument '" + Key.RANGE.key() + "'");
				}
			}
		}
		else
		{
			addErrorMessage(responseNode, "missing_argument '" + Key.SOURCE.key() + "'");
		}
		return rangeAddress;
	}

	protected CellRangeAddress getTargetRangeAddress(ObjectNode responseNode, JsonNode targetNode)
	{
		CellRangeAddress rangeAddress = null;
		if (Objects.nonNull(targetNode))
		{
			rangeAddress = getCellRangeAddress(responseNode, targetNode);
			if (Objects.isNull(rangeAddress))
			{
				CellAddress cellAddress = getCellAddress(responseNode, targetNode, Key.CELL.key());
				rangeAddress = getCellRangeAddress(responseNode, cellAddress);
				if (Objects.isNull(rangeAddress))
				{
					addErrorMessage(responseNode, "missing_argument '" + Key.RANGE.key() + "'");
				}
			}
		}
		else
		{
			addErrorMessage(responseNode, "missing_argument '" + Key.TARGET.key() + "'");
		}
		return rangeAddress;
	}

	protected boolean saveFile(ObjectNode responseNode, File file, Workbook workbook)
	{
		boolean result = true;
		OutputStream os = null;
		try
		{
			os = new FileOutputStream(file);
			workbook.write(os);
		}
		catch (Exception e)
		{
			result = addErrorMessage(responseNode, e.getLocalizedMessage());
		}
		finally
		{
			if (Objects.nonNull(os))
			{
				try
				{
					os.close();
				}
				catch (Exception e)
				{
				}
			}
		}
		return result;
	}

	protected boolean saveWorkbook(ObjectNode responseNode, Workbook workbook)
	{
		boolean result = false;
		if (Objects.nonNull(workbook))
		{
			Set<Entry<String, Workbook>> entries = workbooks.entrySet();
			Iterator<Entry<String, Workbook>> iterator = entries.iterator();
			while (iterator.hasNext())
			{
				Entry<String, Workbook> entry = iterator.next();
				if (entry.getValue() == workbook)
				{
					result = saveFile(responseNode, new File(entry.getKey()), workbook);
				}
			}
		}
		else
		{
			result = addErrorMessage(responseNode, "missing argument '" + Key.WORKBOOK.key() + "'");
		}
		return result;
	}

	protected boolean saveWorkbookAs(ObjectNode responseNode, Workbook workbook, String target)
	{
		boolean result = false;
		if (Objects.nonNull(workbook))
		{
			Set<Entry<String, Workbook>> entries = workbooks.entrySet();
			Iterator<Entry<String, Workbook>> iterator = entries.iterator();
			while (iterator.hasNext())
			{
				Entry<String, Workbook> entry = iterator.next();
				if (entry.getValue() == workbook)
				{
					result = saveFile(responseNode, new File(target), workbook);
					break;
				}
			}
		}
		else
		{
			result = addErrorMessage(responseNode, "missing argument '" + Key.WORKBOOK.key() + "'");
		}
		return result;
	}

	protected CellStyle getCellStyle(Sheet sheet, MergedCellStyle m)
	{
		CellStyle cellStyle = null;
		for (int i = 0; i < sheet.getWorkbook().getNumCellStyles(); i++)
		{
			CellStyle cs = sheet.getWorkbook().getCellStyleAt(i);
			if (cs.getAlignment().equals(m.getHalign()) && cs.getVerticalAlignment().equals(m.getValign())
					&& cs.getBorderBottom().equals(m.getBottom()) && cs.getBorderLeft().equals(m.getLeft())
					&& cs.getBorderRight().equals(m.getRight()) && cs.getBorderTop().equals(m.getTop())
					&& cs.getBottomBorderColor() == m.getbColor() && cs.getLeftBorderColor() == m.getlColor()
					&& cs.getRightBorderColor() == m.getrColor() && cs.getTopBorderColor() == m.gettColor()
					&& cs.getDataFormat() == m.getDataFormat() && cs.getFillBackgroundColor() == m.getBgColor()
					&& cs.getFillForegroundColor() == m.getFgColor() && cs.getFontIndex() == m.getFontIndex()
					&& cs.getFillPattern().equals(m.getFillPattern()) && cs.getShrinkToFit() == m.getShrinkToFit()
					&& cs.getWrapText() == m.getWrapText())
			{
				cellStyle = cs;
				break;
			}
		}
		if (Objects.isNull(cellStyle))
		{
			cellStyle = sheet.getWorkbook().createCellStyle();
			m.applyToCellStyle(sheet, cellStyle);
		}
		return cellStyle;
	}

//	protected Sheet setSheet(ObjectNode requestNode, ObjectNode responseNode, String name)
//	{
//		Sheet sheet = getSheetIfPresent(requestNode, responseNode);
//		if (Objects.nonNull(name))
//		{
//			if (Objects.nonNull(activeWorkbook))
//			{
//				sheet = activeWorkbook.getSheet(name);
//				if (Objects.isNull(sheet))
//				{
//					addErrorMessage(responseNode, "missing sheet '" + Key.NAME.key() + "'");
//				}
//			}
//			else
//			{
//				addErrorMessage(responseNode, "missing argument 'workbook'");
//			}
//		}
//		else
//		{
//			addErrorMessage(responseNode, "missing argument'" + Key.SHEET.key() + "'");
//		}
//		return sheet;
//	}

	protected String getActiveWorkbookName(ObjectNode responseNode)
	{
		return getWorkbookName(responseNode, activeWorkbook);
	}

	protected String getWorkbookName(ObjectNode responseNode, Workbook workbook)
	{
		String name = null;
		Set<Entry<String, Workbook>> entrySet = workbooks.entrySet();
		Iterator<Entry<String, Workbook>> iterator = entrySet.iterator();
		while (iterator.hasNext())
		{
			Entry<String, Workbook> entry = iterator.next();
			if (entry.getValue() == workbook)
			{
				name = entry.getKey();
				break;
			}
		}
		if (Objects.isNull(name))
		{
			addErrorMessage(responseNode, "missing workbook");
		}
		return name;
	}

	protected boolean validateRangeAddress(ObjectNode responseNode, SpreadsheetVersion spreadsheetVersion,
			CellRangeAddress cellRangeAddress)
	{
		boolean result = true;
		if (Objects.nonNull(cellRangeAddress))
		{
			try
			{
				cellRangeAddress.validate(spreadsheetVersion);
			}
			catch (IllegalArgumentException e)
			{
				result = addErrorMessage(responseNode, "invalid range");
			}
		}
		else
		{
			result = addErrorMessage(responseNode, "missing range");
		}
		return result;
	}

	protected CellRangeAddress getCellRangeAddress(CellRange<Cell> cellRange)
	{
		int top = cellRange.getTopLeftCell().getRowIndex();
		int left = cellRange.getTopLeftCell().getColumnIndex();
		int bottom = top + cellRange.getHeight();
		int right = left + cellRange.getWidth();
		return new CellRangeAddress(top, bottom, left, right);
	}

	protected boolean isFunctionSupported(String function)
	{
		int pos = function.indexOf("(");
		if (pos > -1)
		{
			String name = function.substring(0, pos - 1);
			FunctionNameEval functionEval = new FunctionNameEval(name);
			System.out.println(functionEval);
		}
		return true;
	}

	protected Font getFont(Sheet sheet, MergedFont m)
	{
		Font font = null;
		for (int i = 0; i < sheet.getWorkbook().getNumberOfFonts(); i++)
		{
			Font f = sheet.getWorkbook().getFontAt(i);
			if (f.getFontName().equals(m.getName()) && f.getFontHeightInPoints() == m.getSize()
					&& f.getBold() == m.getBold() && f.getItalic() == m.getItalic()
					&& f.getUnderline() == m.getUnderline() && f.getStrikeout() == m.getStrikeOut()
					&& f.getTypeOffset() == m.getTypeOffset() && f.getColor() == m.getColor())
			{
				font = f;
				break;
			}
		}
		if (Objects.isNull(font))
		{
			font = sheet.getWorkbook().createFont();
			font.setFontName(m.getName());
			font.setFontHeightInPoints(m.getSize().shortValue());
			font.setBold(m.getBold().booleanValue());
			font.setItalic(m.getItalic().booleanValue());
			font.setUnderline(m.getUnderline().byteValue());
			font.setStrikeout(m.getStrikeOut().booleanValue());
			font.setTypeOffset(m.getTypeOffset().shortValue());
			font.setColor(m.getColor().shortValue());
		}
		return font;
	}

	protected BorderStyle valueOfBorderStyle(String borderStyleName)
	{
		BorderStyle borderStyle = null;
		try
		{
			borderStyle = BorderStyle.valueOf(borderStyleName.toUpperCase());
		}
		catch (Exception e)
		{
		}
		return borderStyle;
	}

//	protected String makeGivenPathGeneric(String path)
//	{
//		if (System.getProperty("os.name").toLowerCase().contains("win"))
//		{
//			path = path.replace("\\", "/");
//		}
//		return path;
//	}
//	
//	protected String makeGivenPathOSSpecific(String path)
//	{
//		if (System.getProperty("os.name").toLowerCase().contains("win"))
//		{
//			path = path.replace("/", "\\");
//		}
//		return path;
//	}
	
	public Workbook getActiveWorkbook()
	{
		return this.activeWorkbook;
	}
	
	public enum Key
	{
		// @formatter:off
		ADDRESS("address"),
		ALIGNMENT("alignment"),
		BACKGROUND("background"),
		BLUE("blue"),
		BOLD("bold"),
		BORDER("border"),
		BOTTOM_RIGHT("bottom_right"),
		BOTTOM("bottom"),
		CELL("cell"),
		CENTER("center"),
		COL("col"),
		COLOR("color"),
		COPIES("copies"),
		DATA_FORMAT("data_format"),
		DIRECTION("direction"),
		FILL_PATTERN("fill_pattern"),
		FONT("font"),
		FOREGROUND("foreground"),
		FORMAT("format"),
		GREEN("green"),
		HEIGHT("height"),
		HORIZONTAL("horizontal"),
		INDEX("index"),
		ITALIC("italic"),
		ITEMS("items"),
		LEFT("left"),
		ORIENTATION("orientation"),
		NAME("name"),
		RANGE("range"),
		RED("red"),
		RIGHT("right"),
		ROTATION("rotation"),
		ROW("row"),
		SHEET("sheet"),
		SHRINK_TO_FIT("shrink_to_fit"),
		SOURCE("source"),
		START("start"),
		SIZE("size"),
		STRIKE_OUT("strike_out"),
		STYLE("style"),
		TARGET("target"),
		TOP("top"),
		TOP_LEFT("top_left"),
		TYPE_OFFSET("type_offset"),
		UNDERLINE("underline"),
		VALUES("values"),
		VERTICAL("vertical"),
		WORKBOOK("workbook"),
		WRAP_TEXT("wrap_text");
		// @formatter:on

		private Key(String key)
		{
			this.key = key;
		}

		private String key;

		public String key()
		{
			return key;
		}
	}

	public enum Direction
	{
		DOWN("down"), LEFT("left"), RIGHT("right"), UP("up"), DEFAULT("right");

		private Direction(String value)
		{
			this.value = value;
		}

		private String value;

		public String direction()
		{
			return this.value;
		}

		public CellAddress nextIndex(CellAddress cellAddress)
		{
			switch (this)
			{
				case LEFT:
					return new CellAddress(cellAddress.getRow(), cellAddress.getColumn() - 1);
				case RIGHT:
					return new CellAddress(cellAddress.getRow(), cellAddress.getColumn() + 1);
				case UP:
					return new CellAddress(cellAddress.getRow() - 1, cellAddress.getColumn());
				case DOWN:
					return new CellAddress(cellAddress.getRow() + 1, cellAddress.getColumn());
				case DEFAULT:
					return new CellAddress(cellAddress.getRow(), cellAddress.getColumn() + 1);
				default:
					return new CellAddress(cellAddress.getRow(), cellAddress.getColumn() + 1);
			}
		}

		public boolean validRange(ObjectNode responseNode, Workbook workbook, CellAddress cellAddress, int numberOfCells)
		{
			switch (this)
			{
				case LEFT:
				{
					boolean valid = cellAddress.getColumn() - numberOfCells + 1 < 0 ? false : true;
					if (!valid)
					{
						addErrorMessage(responseNode, "minimal_horizontal_cell_position exceeds sheet's extent negatively");
					}
					return valid;
				}
				case RIGHT:
				{
					boolean valid = cellAddress.getColumn() + numberOfCells - 1 > workbook
							.getSpreadsheetVersion().getLastColumnIndex() ? false : true;
					if (!valid)
					{
						addErrorMessage(responseNode, "maximal_horizontal_cell_position exceeds sheet's extent positively");
					}
					return valid;
				}
				case UP:
				{
					boolean valid = cellAddress.getRow() - numberOfCells + 1 < 0 ? false : true;
					if (!valid)
					{
						addErrorMessage(responseNode, "minimal_vertical_cell_position exceeds sheet's extent negatively");
					}
					return valid;
				}
				case DOWN:
				{
					boolean valid = cellAddress.getRow() + numberOfCells - 1 > workbook
							.getSpreadsheetVersion().getLastRowIndex() ? false : true;
					if (!valid)
					{
						addErrorMessage(responseNode, "maximal_vertical_cell_position exceeds sheet's extent positively");
					}
					return valid;
				}
				case DEFAULT:
				{
					return Direction.RIGHT.validRange(responseNode, workbook, cellAddress, numberOfCells);
				}
				default:
					return false;
			}
		}
		
		private void addErrorMessage(ObjectNode responseNode, String message)
		{
			ArrayNode errors = ArrayNode.class.cast(responseNode.get(Executor.ERRORS));
			if (Objects.isNull(errors))
			{
				errors = responseNode.arrayNode();
				responseNode.set(Executor.ERRORS, errors);
			}
			errors.add(message);
		}
	}

	class MergedFont
	{
		String name;
		Short size;
		Boolean bold;
		Boolean italic;
		Byte underline;
		Boolean strikeOut;
		Short typeOffset;
		Short color;

		public MergedFont(Font font)
		{
			name = font.getFontName();
			size = font.getFontHeightInPoints();
			bold = font.getBold();
			italic = font.getItalic();
			underline = font.getUnderline();
			strikeOut = font.getStrikeout();
			typeOffset = font.getTypeOffset();
			color = font.getColor();
		}

		public void applyToFont(Sheet sheet, Font font)
		{
			font.setFontName(name);
			font.setFontHeightInPoints(size);
			font.setBold(bold);
			font.setItalic(italic);
			font.setUnderline(underline);
			font.setStrikeout(strikeOut);
			font.setTypeOffset(typeOffset);
			font.setColor(color);
		}

		public void applyRequestedFontStyles(ObjectNode requestNode, ObjectNode responseNode)
		{
			JsonNode nameNode = requestNode.findPath(Key.NAME.key());
			if (nameNode.isTextual())
			{
				name = nameNode.asText();
			}
			JsonNode sizeNode = requestNode.findPath(Key.SIZE.key());
			if (sizeNode.isInt())
			{
				int s = sizeNode.asInt();
				if (s >= Short.MIN_VALUE && s <= Short.MAX_VALUE)
				{
					size = (short) s;
				}
				else
				{
					addErrorMessage(responseNode, "invalid argument 'size'");
				}
			}
			JsonNode boldNode = requestNode.findPath(Key.BOLD.key());
			if (boldNode.isInt())
			{
				bold = boldNode.asBoolean();
			}
			JsonNode italicNode = requestNode.findPath(Key.ITALIC.key());
			if (italicNode.isInt())
			{
				italic = italicNode.asBoolean();
			}
			JsonNode underlineNode = requestNode.findPath(Key.UNDERLINE.key());
			if (underlineNode.isInt())
			{
				int u = underlineNode.asInt();
				if (u >= Byte.MIN_VALUE && u <= Byte.MAX_VALUE)
				{
					underline = (byte) u;
				}
				else
				{
					addErrorMessage(responseNode, "invalid argument 'underline'");
				}
			}
			JsonNode strikeOutNode = requestNode.findPath(Key.STRIKE_OUT.key());
			if (strikeOutNode.isInt())
			{
				strikeOut = strikeOutNode.asBoolean();
			}
			JsonNode typeOffsetNode = requestNode.findPath(Key.TYPE_OFFSET.key());
			if (typeOffsetNode.isInt())
			{
				int t = typeOffsetNode.asInt();
				if (t >= Short.MIN_VALUE && t <= Short.MAX_VALUE)
				{
					typeOffset = (short) t;
				}
				else
				{
					addErrorMessage(responseNode, "invalid argument 'type_offset'");
				}
			}
			JsonNode colorNode = requestNode.findPath(Key.COLOR.key());
			if (colorNode.isInt())
			{
				int w = colorNode.asInt();
				if (w >= Short.MIN_VALUE && w <= Short.MAX_VALUE)
				{
					color = (short) w;
				}
				else
				{
					addErrorMessage(responseNode, "invalid argument 'color'");
				}
			}
		}

		public String getName()
		{
			return name;
		}

		public Short getSize()
		{
			return size;
		}

		public Boolean getBold()
		{
			return bold;
		}

		public Boolean getItalic()
		{
			return italic;
		}

		public Byte getUnderline()
		{
			return underline;
		}

		public Boolean getStrikeOut()
		{
			return strikeOut;
		}

		public Short getTypeOffset()
		{
			return typeOffset;
		}

		public Short getColor()
		{
			return color;
		}
	}

	class MergedCellStyle
	{
		private HorizontalAlignment halign;
		private VerticalAlignment valign;
		private BorderStyle bottom;
		private BorderStyle left;
		private BorderStyle right;
		private BorderStyle top;
		private Short bColor;
		private Short lColor;
		private Short rColor;
		private Short tColor;
		private Short dataFormat;
		private Short bgColor;
		private Short fgColor;
		private FillPatternType fillPattern;
		private Integer fontIndex;
		private Boolean shrinkToFit;
		private Boolean wrapText;

		public MergedCellStyle(CellStyle c)
		{
			halign = c.getAlignment();
			valign = c.getVerticalAlignment();
			bottom = c.getBorderBottom();
			left = c.getBorderLeft();
			right = c.getBorderRight();
			top = c.getBorderTop();
			bColor = c.getBottomBorderColor();
			lColor = c.getLeftBorderColor();
			rColor = c.getRightBorderColor();
			tColor = c.getTopBorderColor();
			dataFormat = c.getDataFormat();
			bgColor = c.getFillBackgroundColor();
			fgColor = c.getFillForegroundColor();
			fillPattern = c.getFillPattern();
			fontIndex = c.getFontIndex();
			shrinkToFit = c.getShrinkToFit();
			wrapText = c.getWrapText();
		}

		public void applyToCellStyle(Sheet sheet, CellStyle cellStyle)
		{
			cellStyle.setAlignment(halign);
			cellStyle.setVerticalAlignment(valign);
			cellStyle.setBorderBottom(bottom);
			cellStyle.setBorderLeft(left);
			cellStyle.setBorderRight(right);
			cellStyle.setBorderTop(top);
			cellStyle.setBottomBorderColor(bColor);
			cellStyle.setLeftBorderColor(lColor);
			cellStyle.setRightBorderColor(rColor);
			cellStyle.setTopBorderColor(tColor);
			cellStyle.setDataFormat(dataFormat);
			cellStyle.setFillBackgroundColor(bgColor);
			cellStyle.setFillForegroundColor(fgColor);
			cellStyle.setFillPattern(fillPattern);
			cellStyle.setFont(sheet.getWorkbook().getFontAt(fontIndex));
			cellStyle.setShrinkToFit(shrinkToFit);
			cellStyle.setWrapText(wrapText);

		}

		public boolean applyRequestedStyles(ObjectNode requestNode, ObjectNode responseNode)
		{
			boolean result = true;
			JsonNode alignmentNode = requestNode.findPath(Key.ALIGNMENT.key());
			if (alignmentNode.isObject())
			{
				JsonNode horizontalNode = alignmentNode.findPath(Key.HORIZONTAL.key());
				if (horizontalNode.isTextual())
				{
					try
					{
						halign = HorizontalAlignment.valueOf(horizontalNode.asText().toUpperCase());
					}
					catch (Exception e)
					{
						result = addErrorMessage(responseNode, "invalid argument 'alignment.horizontal'");
					}
				}
				JsonNode verticalNode = alignmentNode.findPath(Key.VERTICAL.key());
				if (verticalNode.isTextual())
				{
					try
					{
						valign = VerticalAlignment.valueOf(verticalNode.asText().toUpperCase());
					}
					catch (Exception e)
					{
						result = addErrorMessage(responseNode, "invalid argument 'alignment.vertical'");
					}
				}
			}
			JsonNode borderNode = requestNode.findPath(Key.BORDER.key());
			if (borderNode.isObject())
			{
				JsonNode styleNode = borderNode.findPath(Key.STYLE.key());
				if (styleNode.isObject())
				{
					JsonNode bottomNode = styleNode.findPath(Key.BOTTOM.key());
					if (bottomNode.isTextual())
					{
						BorderStyle borderStyle = valueOfBorderStyle(bottomNode.asText());
						if (Objects.nonNull(borderStyle))
						{
							bottom = borderStyle;
						}
						else
						{
							result = addErrorMessage(responseNode, "invalid argument 'border.style.bottom'");
						}
					}
					JsonNode leftNode = styleNode.findPath(Key.LEFT.key());
					if (leftNode.isTextual())
					{
						BorderStyle borderStyle = valueOfBorderStyle(leftNode.asText());
						if (Objects.nonNull(borderStyle))
						{
							left = borderStyle;
						}
						else
						{
							result = addErrorMessage(responseNode, "invalid argument 'border.style.left'");
						}
					}
					JsonNode rightNode = styleNode.findPath(Key.RIGHT.key());
					if (rightNode.isTextual())
					{
						BorderStyle borderStyle = valueOfBorderStyle(rightNode.asText());
						if (Objects.nonNull(borderStyle))
						{
							right = borderStyle;
						}
						else
						{
							result = addErrorMessage(responseNode, "invalid argument 'border.style.right'");
						}
					}
					JsonNode topNode = styleNode.findPath(Key.TOP.key());
					if (topNode.isTextual())
					{
						BorderStyle borderStyle = valueOfBorderStyle(topNode.asText());
						if (Objects.nonNull(borderStyle))
						{
							top = borderStyle;
						}
						else
						{
							result = addErrorMessage(responseNode, "invalid argument 'border.style.top'");
						}
					}
				}
				JsonNode colorNode = borderNode.findPath(Key.COLOR.key());
				if (colorNode.isObject())
				{
					JsonNode bottomNode = colorNode.findPath(Key.BOTTOM.key());
					if (bottomNode.isInt())
					{
						int c = bottomNode.asInt();
						if (c >= Short.MIN_VALUE && c <= Short.MAX_VALUE)
						{
							bColor = (short) c;
						}
						else
						{
							result = addErrorMessage(responseNode, "invalid argument 'border.color.bottom'");
						}
					}
					JsonNode leftNode = colorNode.findPath(Key.LEFT.key());
					if (leftNode.isInt())
					{
						int c = leftNode.asInt();
						if (c >= Short.MIN_VALUE && c <= Short.MAX_VALUE)
						{
							lColor = (short) c;
						}
						else
						{
							result = addErrorMessage(responseNode, "invalid argument 'border.color.left'");
						}
					}
					JsonNode rightNode = colorNode.findPath(Key.RIGHT.key());
					if (rightNode.isInt())
					{
						int c = rightNode.asInt();
						if (c >= Short.MIN_VALUE && c <= Short.MAX_VALUE)
						{
							rColor = (short) c;
						}
						else
						{
							result = addErrorMessage(responseNode, "invalid argument 'border.color.right'");
						}
					}
					JsonNode topNode = colorNode.findPath(Key.TOP.key());
					if (topNode.isInt())
					{
						int c = topNode.asInt();
						if (c >= Short.MIN_VALUE && c <= Short.MAX_VALUE)
						{
							tColor = (short) c;
						}
						else
						{
							result = addErrorMessage(responseNode, "invalid argument 'border.color.top'");
						}
					}
				}
			}
			JsonNode dataFormatNode = requestNode.findPath(Key.DATA_FORMAT.key());
			if (TextNode.class.isInstance(dataFormatNode))
			{
				String df = dataFormatNode.asText();
				int formatIndex = BuiltinFormats.getBuiltinFormat(df);
				dataFormat = (short) formatIndex;
				System.out.println();
//				}
//				else
//				{
//					result = addErrorMessage(responseNode, "invalid argument 'dataFormat'");
//				}
			}
			JsonNode bgNode = requestNode.findPath(Key.BACKGROUND.key());
			if (bgNode.isObject())
			{
				JsonNode colorNode = bgNode.findPath(Key.COLOR.key());
				if (colorNode.isTextual())
				{
					try
					{
						bgColor = IndexedColors.valueOf(colorNode.asText().toUpperCase()).getIndex();
					}
					catch (Exception e)
					{
						result = addErrorMessage(responseNode, "invalid argument 'background.color'");
					}
				}
				else
				{
					result = addErrorMessage(responseNode, "invalid argument 'background.color'");
				}
			}
			JsonNode fgNode = requestNode.findPath(Key.FOREGROUND.key());
			if (fgNode.isObject())
			{
				JsonNode colorNode = fgNode.findPath(Key.COLOR.key());
				if (colorNode.isTextual())
				{
					try
					{
						fgColor = IndexedColors.valueOf(colorNode.asText().toUpperCase()).getIndex();
					}
					catch (Exception e)
					{
						result = addErrorMessage(responseNode, "invalid argument 'foreground.color'");
					}
				}
				else
				{
					result = addErrorMessage(responseNode, "invalid argument 'foreground.color'");
				}
			}
			JsonNode fillPatternNode = requestNode.findPath(Key.FILL_PATTERN.key());
			if (Objects.nonNull(fillPatternNode) && !fillPatternNode.isMissingNode())
			{
				if (TextNode.class.isInstance(fillPatternNode))
				{
					try
					{
						fillPattern = FillPatternType.valueOf(fillPatternNode.asText());
					}
					catch (Exception e)
					{
						result = addErrorMessage(responseNode, "invalid argument '" + Key.FILL_PATTERN.key() + "'");
					}
				}
			}
			JsonNode fontIndexNode = requestNode.findPath(Key.FONT.key());
			if (Objects.nonNull(fontIndexNode) && !fontIndexNode.isMissingNode())
			{
				if (IntNode.class.isInstance(fontIndexNode))
				{
					fontIndex = fontIndexNode.asInt();
				}
				else
				{
					result = addErrorMessage(responseNode, "invalid argument 'font'");
				}
			}
			JsonNode shrinkToFitNode = requestNode.findPath(Key.SHRINK_TO_FIT.key());
			if (Objects.nonNull(shrinkToFitNode) && !shrinkToFitNode.isMissingNode())
			{
				if (shrinkToFitNode.isInt())
				{
					shrinkToFit = shrinkToFitNode.asBoolean();
				}
			}
			JsonNode wrapTextNode = requestNode.findPath(Key.WRAP_TEXT.key());
			if (Objects.nonNull(wrapTextNode) && !wrapTextNode.isMissingNode())
			{
				if (wrapTextNode.isInt())
				{
					wrapText = wrapTextNode.asBoolean();
				}
			}
			return result;
		}

		public void setHalign(HorizontalAlignment halign)
		{
			this.halign = halign;
		}

		public void setValign(VerticalAlignment valign)
		{
			this.valign = valign;
		}

		public void setBottom(BorderStyle bottom)
		{
			this.bottom = bottom;
		}

		public void setLeft(BorderStyle left)
		{
			this.left = left;
		}

		public void setRight(BorderStyle right)
		{
			this.right = right;
		}

		public void setTop(BorderStyle top)
		{
			this.top = top;
		}

		public void setbColor(Short bColor)
		{
			this.bColor = bColor;
		}

		public void setlColor(Short lColor)
		{
			this.lColor = lColor;
		}

		public void setrColor(Short rColor)
		{
			this.rColor = rColor;
		}

		public void settColor(Short tColor)
		{
			this.tColor = tColor;
		}

		public void setDataFormat(Short dataFormat)
		{
			this.dataFormat = dataFormat;
		}

		public void setBgColor(Short bgColor)
		{
			this.bgColor = bgColor;
		}

		public void setFgColor(Short fgColor)
		{
			this.fgColor = fgColor;
		}

		public void setFillPattern(FillPatternType fillPattern)
		{
			this.fillPattern = fillPattern;
		}

		public void setFontIndex(Integer fontIndex)
		{
			this.fontIndex = fontIndex;
		}

		public void setShrinkToFit(Boolean shrinkToFit)
		{
			this.shrinkToFit = shrinkToFit;
		}

		public void setWrapText(Boolean wrapText)
		{
			this.wrapText = wrapText;
		}

		public HorizontalAlignment getHalign()
		{
			return halign;
		}

		public VerticalAlignment getValign()
		{
			return valign;
		}

		public BorderStyle getBottom()
		{
			return bottom;
		}

		public BorderStyle getLeft()
		{
			return left;
		}

		public BorderStyle getRight()
		{
			return right;
		}

		public BorderStyle getTop()
		{
			return top;
		}

		public Short getbColor()
		{
			return bColor;
		}

		public Short getlColor()
		{
			return lColor;
		}

		public Short getrColor()
		{
			return rColor;
		}

		public Short gettColor()
		{
			return tColor;
		}

		public Short getDataFormat()
		{
			return dataFormat;
		}

		public Short getBgColor()
		{
			return bgColor;
		}

		public Short getFgColor()
		{
			return fgColor;
		}

		public FillPatternType getFillPattern()
		{
			return fillPattern;
		}

		public Integer getFontIndex()
		{
			return fontIndex;
		}

		public Boolean getShrinkToFit()
		{
			return shrinkToFit;
		}

		public Boolean getWrapText()
		{
			return wrapText;
		}
	}
}