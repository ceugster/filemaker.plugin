package ch.eugster.filemaker.fsl.plugin.xls;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellRange;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
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
import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ShortNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.ValueNode;

import ch.eugster.filemaker.fsl.plugin.Executor;
import ch.eugster.filemaker.fsl.plugin.Fsl;

/**
 * 
 */
/**
 * @author christian
 *
 */
public class Xls extends Executor<Xls>
{
	public static ObjectMapper MAPPER = new ObjectMapper();

	public static String SHEET0 = "Sheet0";

	public static Map<String, Workbook> workbooks = new HashMap<String, Workbook>();

	public static Workbook activeWorkbook;

	public static boolean activateSheet(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = true;
		Workbook workbook = getWorkbookIfPresent(requestNode);
		result = Objects.nonNull(workbook);
		if (result)
		{
			result = activateWorkbook(workbook);
			if (result)
			{
				JsonNode sheetNode = requestNode.findPath(Key.SHEET.key());
				Sheet sheet = findSheet(workbook, sheetNode);
				result = Objects.nonNull(sheet);
				if (result)
				{
					workbook.setActiveSheet(workbook.getSheetIndex(sheet));
				}
				else
				{
					Fsl.addErrorMessage("missing_sheet '" + sheetNode.asText() + "'");
				}
			}
		}
		return result;
	}

	public static boolean activateWorkbook(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = true;
		JsonNode workbookNode = requestNode.findPath(Key.WORKBOOK.key());
		if (Objects.nonNull(workbookNode))
		{
			if (TextNode.class.isInstance(workbookNode))
			{
				String name = workbookNode.asText();
				Workbook workbook = Xls.workbooks.get(name);
				if (Objects.nonNull(workbook))
				{
					if (Xls.activeWorkbook != workbook)
					{
						Xls.activeWorkbook = workbook;
					}
				}
				else
				{
					result = Fsl.addErrorMessage("missing_workbook '" + Key.WORKBOOK.key() + "'");
				}
			}
			else
			{
				result = Fsl.addErrorMessage("invalid_argument '" + Key.WORKBOOK.key() + "'");
			}
		}
		else
		{
			result = Fsl.addErrorMessage("missing_argument '" + Key.WORKBOOK.key() + "'");
		}
		return result;
	}

	public static boolean activeSheetPresent(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = Objects.nonNull(Xls.activeWorkbook);
		if (result)
		{
			result = activeSheetPresent();
			responseNode.put("present", result ? 1 : 0);
		}
		return result;
	}

	public static boolean activeWorkbookPresent(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = Objects.nonNull(Xls.activeWorkbook);
		responseNode.put(Executor.RESULT, result ? 1 : 0);
		return result;
	}

	public static boolean copy(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = true;
		JsonNode sourceNode = requestNode.findPath(Key.SOURCE.key());
		Sheet sourceSheet = getSheetIfPresent(sourceNode);
		result = Objects.nonNull(sourceSheet);
		if (result)
		{
			CellRangeAddress sourceRangeAddress = getSourceRangeAddress(sourceNode);
			result = validateRangeAddress(sourceSheet.getWorkbook().getSpreadsheetVersion(), sourceRangeAddress);
			if (result)
			{
				JsonNode targetNode = requestNode.findPath(Key.TARGET.key());
				Sheet targetSheet = getSheetIfPresent(targetNode);
				result = Objects.nonNull(targetSheet);
				if (result)
				{
					CellRangeAddress targetRangeAddress = getTargetRangeAddress(targetNode);
					result = validateRangeAddress(targetSheet.getWorkbook().getSpreadsheetVersion(), targetRangeAddress);
					if (result)
					{
						if (sourceSheet == targetSheet)
						{
							result = !sourceRangeAddress.intersects(targetRangeAddress);
							if (!result)
							{
								return Fsl.addErrorMessage("source_range_and_target_range_intersect");
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
											Cell targetCell = getOrCreateCell(targetSheet, targetAddress);
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
													if (sourceCell.getCellStyle().equals(CellType.STRING))
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
								else
								{
									result = Fsl.addErrorMessage("missing_source_cell");
								}
							}
							else
							{
								result = Fsl.addErrorMessage("missing_source_row");
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
							result = Fsl.addErrorMessage("invalid_different source and target range dimensions");
						}
						
					}					
				}
			}				
		}
		return result;
	}

	public static boolean createAndActivateSheetByName(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = Xls.createSheet(requestNode, responseNode);
		if (result)
		{
			result = Xls.activateSheet(requestNode, responseNode);
		}
		return result;
	}

	public static boolean createAndActivateWorkbook(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = createWorkbook(requestNode, responseNode);
		if (result)
		{
			result = activateWorkbook(requestNode, responseNode);
		}
		return result;
	}

	public static boolean createSheet(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = true;
		JsonNode workbookNode = requestNode.findPath(Key.WORKBOOK.key());
		JsonNode sheetNode = requestNode.findPath(Key.SHEET.key());
		Workbook workbook = getWorkbookIfPresent(workbookNode);
		if (Objects.nonNull(workbook))
		{
			Sheet sheet = createSheet(workbook, sheetNode);
			responseNode.put(Key.SHEET.key(), sheet.getSheetName());
		}
		else
		{
			result = Fsl.addErrorMessage("sheet_already_exists 'sheet'");
		}
		return result;
	}

	public static boolean createWorkbook(ObjectNode requestNode, ObjectNode responseNode)
	{
		JsonNode nameNode = requestNode.findPath(Key.WORKBOOK.key());
		boolean result = Objects.nonNull(nameNode) && !nameNode.isMissingNode();
		if (result)
		{
			if (TextNode.class.isInstance(nameNode))
			{
				String name = nameNode.asText();
				Workbook wb = Xls.workbooks.get(name);
				if (Objects.isNull(wb))
				{
					wb = createWorkbook(name);
				}
				else
				{
					result = Fsl.addErrorMessage("workbook_already_exists");
				}
			}
			else
			{
				Fsl.addErrorMessage("invalid_argument '" + Key.WORKBOOK.key() + "'");
			}
		}
		else
		{
			result = Fsl.addErrorMessage("missing_argument '" + Key.WORKBOOK.key() + "'");
		}
		return result;
	}

	public static boolean getActiveSheetIndex(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = activeSheetPresent();
		if (result)
		{
			responseNode.put(Key.SHEET.key(), Xls.activeWorkbook.getActiveSheetIndex());
		}
		else
		{
			result = Fsl.addErrorMessage("no_active_sheet_present");
		}
		return result;
	}

	public static boolean getActiveSheetName(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = activeSheetPresent();
		if (result)
		{
			responseNode.put("name", getActiveSheet().getSheetName());
		}
		else
		{
			result = Fsl.addErrorMessage("active_sheet_not_present");
		}
		return result;
	}

	public static boolean getActiveWorkbookName(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean found = false;
		Set<Entry<String, Workbook>> entrySet = Xls.workbooks.entrySet();
		Iterator<Entry<String, Workbook>> iterator = entrySet.iterator();
		while (iterator.hasNext())
		{
			Entry<String, Workbook> entry = iterator.next();
			if (entry.getValue() == Xls.activeWorkbook)
			{
				responseNode.put("name", entry.getKey());
				found = true;
				break;
			}
		}
		if (!found)
		{
			found = Fsl.addErrorMessage("no_active_workbook_present");
		}
		return found;
	}

	public static boolean getCallableMethods(ObjectNode requestNode, ObjectNode responseNode)
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

	public static boolean getSheetNames(ObjectNode requestNode, ObjectNode responseNode)
	{
		Workbook workbook = getWorkbookIfPresent(requestNode);
		boolean result = Objects.nonNull(workbook);
		if (result)
		{
			ArrayNode sheetNames = responseNode.arrayNode();
			for (int i = 0; i < workbook.getNumberOfSheets(); i++)
			{
				sheetNames.add(workbook.getSheetName(i));
			}
			responseNode.set("names", sheetNames);
		}
		else
		{
			result = Fsl.addErrorMessage("missing_argument 'workbook'");
		}
		return result;
	}

	public static CellRangeAddress getSourceRangeAddress(JsonNode sourceNode)
	{
		CellRangeAddress rangeAddress = null;
		if (Objects.nonNull(sourceNode) && !sourceNode.isMissingNode())
		{
			rangeAddress = getCellRangeAddress(sourceNode);
			if (Objects.isNull(rangeAddress))
			{
				CellAddress cellAddress = getCellAddress(sourceNode, Key.CELL.key());
				rangeAddress = getCellRangeAddress(cellAddress);
				if (Objects.isNull(rangeAddress))
				{
					Fsl.addErrorMessage("missing_argument '" + Key.RANGE.key() + "'");
				}
			}
		}
		else
		{
			Fsl.addErrorMessage("missing_argument '" + Key.SOURCE.key() + "'");
		}
		return rangeAddress;
	}

	public static CellRangeAddress getTargetRangeAddress(JsonNode targetNode)
	{
		CellRangeAddress rangeAddress = null;
		if (Objects.nonNull(targetNode))
		{
			rangeAddress = getCellRangeAddress(targetNode);
			if (Objects.isNull(rangeAddress))
			{
				CellAddress cellAddress = getCellAddress(targetNode, Key.CELL.key());
				rangeAddress = getCellRangeAddress(cellAddress);
				if (Objects.isNull(rangeAddress))
				{
					Fsl.addErrorMessage("missing_argument '" + Key.RANGE.key() + "'");
				}
			}
		}
		else
		{
			Fsl.addErrorMessage("missing_argument '" + Key.TARGET.key() + "'");
		}
		return rangeAddress;
	}

	public static boolean getWorkbookNames(ObjectNode requestNode, ObjectNode responseNode)
	{
		ArrayNode workbookNames = responseNode.arrayNode();
		Set<String> keys = workbooks.keySet();
		keys.forEach(key -> workbookNames.add(key));
		responseNode.set("names", workbookNames);
		return true;
	}

	public static boolean releaseWorkbook(ObjectNode requestNode, ObjectNode responseNode)
	{
		return releaseWorkbook(requestNode);
	}

	public static boolean releaseWorkbooks(ObjectNode requestNode, ObjectNode responseNode)
	{
		Xls.workbooks.clear();
		boolean result = releaseWorkbook();
		return result;
	}

	public static boolean saveAndReleaseWorkbook(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = saveWorkbook(requestNode, responseNode);
		if (result)
		{
			result = releaseWorkbook(requestNode, responseNode);
		}
		return result;
	}

	public static boolean saveWorkbook(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = true;
		Workbook workbook = getWorkbookIfPresent(requestNode);
		if (Objects.nonNull(workbook))
		{
			JsonNode pathNameNode = requestNode.get(Key.PATH_NAME.key());
			if (Objects.nonNull(pathNameNode))
			{
				if (TextNode.class.isInstance(requestNode.get(Key.PATH_NAME.key())))
				{
					String path = TextNode.class.cast(requestNode.get(Key.PATH_NAME.key())).asText();
					result = saveWorkbook(path, workbook);
				}
				else
				{
					result = Fsl.addErrorMessage("invalid_argument '" + Key.PATH_NAME.key() + "'");
				}
			}
			else
			{
				result = Fsl.addErrorMessage("missing_argument '" + Key.PATH_NAME.key() + "'");
			}
		}
		else
		{
			result = Fsl.addErrorMessage("missing_argument '" + Key.WORKBOOK.key() + "'");
		}
		return result;
	}

	public static boolean setCells(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = true;
		Sheet sheet = getSheetIfPresent(requestNode);
		result = Objects.nonNull(sheet);
		if (result)
		{
			JsonNode cellNode = requestNode.findPath(Key.CELL.key());
			result = Objects.nonNull(cellNode) && !cellNode.isMissingNode();
			if (result)
			{
				CellAddress cellAddress = getCellAddress(cellNode, Key.CELL.key());
				if (Objects.nonNull(cellAddress))
				{
					if (!requestNode.get(Key.VALUES.key()).isMissingNode())
					{
						if (ArrayNode.class.isInstance(requestNode.get(Key.VALUES.key())))
						{
							ArrayNode valuesNode = ArrayNode.class.cast(requestNode.get(Key.VALUES.key()));
							Direction direction = Direction.DEFAULT;
							if (valuesNode.size() == 0)
							{
								result = Fsl.addErrorMessage("invalid_argument '" + Key.VALUES.key() + "'");
							}
							else if (valuesNode.size() > 1)
							{
								JsonNode directionNode = requestNode.findPath(Key.DIRECTION.key());
								if (Objects.nonNull(directionNode) && !directionNode.isMissingNode())
								{
									if (TextNode.class.isInstance(directionNode))
									{
										try
										{
											direction = Direction.valueOf(directionNode.asText().toUpperCase());
										}
										catch (Exception e)
										{
											result = Fsl.addErrorMessage("invalid_argument 'direction'");
										}
									}
									else
									{
										result = Fsl.addErrorMessage("invalid_argument 'direction'");
									}
								}
							}
							if (direction.validRange(cellAddress, valuesNode.size()))
							{
								for (int i = 0; i < valuesNode.size(); i++)
								{
									JsonNode valueNode = valuesNode.get(i);
									JsonNodeType nodeType = valueNode.getNodeType();
									Cell cell = getOrCreateCell(sheet, cellAddress);
									if (nodeType.equals(JsonNodeType.NULL))
									{
										// do nothing
									}
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
												cell.setCellFormula(valueNode.asText());
												FormulaEvaluator evaluator = Xls.activeWorkbook.getCreationHelper()
														.createFormulaEvaluator();
												CellType cellType = evaluator.evaluateFormulaCell(cell);
												System.out.println(cellType);
											}
											catch (FormulaParseException e)
											{
												setRichTextString(cell, valueNode.asText());
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
									cellAddress = direction.nextIndex(cellAddress);
								}
							}
						}
						else
						{
							result = Fsl.addErrorMessage("invalid_argument '" + Key.VALUES.key() + "'");
						}
					}
					else
					{
						result = Fsl.addErrorMessage("missing_argument '" + Key.VALUES.key() + "'");
					}
				}
				else
				{
					result = Fsl.addErrorMessage("invalid_argument '" + Key.CELL.key() + "'");
				}
			}
			else
			{
				result = Fsl.addErrorMessage("missing_argument '" + Key.CELL.key() + "'");
			}
		}
		else
		{
			result = Fsl.addErrorMessage("missing_argument '" + Key.SHEET.key() + "'");
		}
		return result;
	}

	public static void setCellValue(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = true;
		JsonNode valueNode = requestNode.findPath(Key.VALUES.key());
		Sheet sheet = getSheetIfPresent(requestNode);
		result = Objects.nonNull(sheet);
		if (result)
		{
			JsonNode cellNode = requestNode.findPath(Key.CELL.key());
			if (Objects.nonNull(cellNode) && !cellNode.isMissingNode())
			{
				if (Objects.nonNull(valueNode) && !valueNode.isMissingNode())
				{
					CellAddress cellAddress = getCellAddress(cellNode, Key.CELL.key());
					Cell cell = getOrCreateCell(sheet, cellAddress);
					if (ValueNode.class.isInstance(valueNode))
					{
						if (TextNode.class.isInstance(valueNode))
						{
							setRichTextString(cell, valueNode.asText());
						}
						else if (NumericNode.class.isInstance(valueNode))
						{
							if (BigIntegerNode.class.isInstance(valueNode) || LongNode.class.isInstance(valueNode))
							{
								cell.setCellValue(valueNode.bigIntegerValue().longValue());
							}
							else if (DoubleNode.class.isInstance(valueNode) || DecimalNode.class.isInstance(valueNode)
									|| FloatNode.class.isInstance(valueNode))
							{
								cell.setCellValue(valueNode.asDouble());
							}
							else if (IntNode.class.isInstance(valueNode) || ShortNode.class.isInstance(valueNode))
							{
								cell.setCellValue(valueNode.asInt());
							}
						}
						else
						{
							Fsl.addErrorMessage("invalid_argument 'value'");
						}
					}
					else
					{
						Fsl.addErrorMessage("invalid_argument 'value'");
					}
				}
				else
				{
					Fsl.addErrorMessage("missing_argument 'value'");
				}
			}
			else
			{
				Fsl.addErrorMessage("missing_argument 'cell'");
			}
		}
		else
		{
			Fsl.addErrorMessage("missing_active_sheet");
		}
	}

//	public static void setColumnFormulae(ObjectNode requestNode, ObjectNode responseNode)
//	{
//		Sheet sheet = getSheetIfPresent(requestNode);
//		if (Objects.nonNull(sheet))
//		{
//			int rowIndex = 0;
//			JsonNode rowIndexNode = requestNode.get("row_index");
//			if (Objects.nonNull(rowIndexNode))
//			{
//				if (rowIndexNode.isInt())
//				{
//					rowIndex = rowIndexNode.asInt() - 1;
//					if (rowIndex < 0)
//					{
//						Fsl.addErrorMessage("invalid_argument 'row_index'");
//					}
//				}
//				else
//				{
//					Fsl.addErrorMessage("invalid_argument 'row_index'");
//				}
//			}
//			int colIndex = 0;
//			JsonNode colIndexNode = requestNode.get("col_index");
//			if (Objects.nonNull(colIndexNode))
//			{
//				if (colIndexNode.isInt())
//				{
//					colIndex = colIndexNode.asInt() - 1;
//					if (colIndex < 0)
//					{
//						Fsl.addErrorMessage("invalid_argument 'col_index'");
//					}
//				}
//				else
//				{
//					Fsl.addErrorMessage("invalid_argument 'col_index'");
//				}
//			}
//			JsonNode formulaeNode = requestNode.get("formulae");
//			if (Objects.nonNull(formulaeNode))
//			{
//				for (int i = 0; i < formulaeNode.size(); i++)
//				{
//					Row row = getOrCreateRow(sheet, i + rowIndex);
//					if (Objects.isNull(row.getCell(colIndex)))
//					{
//						Cell cell = row.createCell(colIndex);
//						cell.setCellFormula(formulaeNode.get(i).asText());
//					}
//				}
//			}
//		}
//	}
//
//	public static void setColumnTitles(ObjectNode requestNode, ObjectNode responseNode)
//	{
//		Sheet sheet = getSheetIfPresent(requestNode);
//		if (Objects.nonNull(sheet))
//		{
//			int rowIndex = 0;
//			JsonNode rowIndexNode = requestNode.get("row_index");
//			if (Objects.nonNull(rowIndexNode))
//			{
//				if (rowIndexNode.isInt())
//				{
//					rowIndex = rowIndexNode.asInt() - 1;
//					if (rowIndex < 0)
//					{
//						Fsl.addErrorMessage("invalid_argument 'row_index'");
//					}
//				}
//				else
//				{
//					Fsl.addErrorMessage("invalid_argument 'row_index'");
//				}
//			}
//			int colIndex = 0;
//			JsonNode colIndexNode = requestNode.get("col_index");
//			if (Objects.nonNull(colIndexNode))
//			{
//				if (colIndexNode.isInt())
//				{
//					colIndex = colIndexNode.asInt() - 1;
//					if (colIndex < 0)
//					{
//						Fsl.addErrorMessage("invalid_argument 'col_index'");
//					}
//				}
//				else
//				{
//					Fsl.addErrorMessage("invalid_argument 'col_index'");
//				}
//			}
//			JsonNode titlesNode = requestNode.get("titles");
//			if (Objects.nonNull(titlesNode))
//			{
//				for (int i = 0; i < titlesNode.size(); i++)
//				{
//					Row row = getOrCreateRow(sheet, i + rowIndex);
//					if (Objects.isNull(row.getCell(colIndex)))
//					{
//						Cell cell = row.createCell(colIndex);
//						setRichTextString(cell, titlesNode.get(i).asText());
//					}
//				}
//			}
//		}
//	}

	public static void setHeaders(ObjectNode requestNode, ObjectNode responseNode)
	{
		Sheet sheet = getSheetIfPresent(requestNode);
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

	public static void setFooters(ObjectNode requestNode, ObjectNode responseNode)
	{
		Sheet sheet = getSheetIfPresent(requestNode);
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

	public static void setPrintOptions(ObjectNode requestNode, ObjectNode responseNode)
	{
		try
		{
			if (ObjectNode.class.isInstance(requestNode.get("bottom_margin")))
			{
			}
			{
				Sheet sheet = getActiveSheet();
				sheet.getPrintSetup().setLandscape(false);
			}
		}
		catch (Exception e)
		{
			Fsl.addErrorMessage(e.getLocalizedMessage());
		}
	}

	protected static Sheet getActiveSheet()
	{
		Sheet sheet = null;
		if (activeSheetPresent())
		{
			sheet = Xls.activeWorkbook.getSheetAt(Xls.activeWorkbook.getActiveSheetIndex());
		}
		else
		{
			Fsl.addErrorMessage("missing_active_sheet");
		}
		return sheet;
	}

	private static boolean activateSheet(Sheet sheet)
	{
		boolean result = activateWorkbook(sheet.getWorkbook());
		if (result)
		{
			Xls.activeWorkbook.setActiveSheet(Xls.activeWorkbook.getSheetIndex(sheet));
		}
		return result;
	}

	private static boolean activateWorkbook(Workbook workbook)
	{
		boolean result = Objects.nonNull(workbook);
		if (result)
		{
			if (workbook != Xls.activeWorkbook)
			{
				Xls.activeWorkbook = workbook;
			}
		}
		return result;
	}

	private static boolean activeSheetPresent()
	{
		boolean present = Objects.nonNull(Xls.activeWorkbook);
		if (present)
		{
			present = Xls.activeWorkbook.getActiveSheetIndex() > -1;
		}
		return present;
	}

	public static boolean applyFontStyle(ObjectNode requestNode, ObjectNode responseNode)
	{
		Sheet sheet = getSheetIfPresent(requestNode);
		boolean result = Objects.nonNull(sheet);
		if (result)
		{
			CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
			Font font = sheet.getWorkbook().createFont();
			JsonNode styleNode = requestNode.findPath(Key.STYLE.key());
			if (Objects.nonNull(styleNode))
			{
				if (IntNode.class.isInstance(styleNode))
				{
					int style = styleNode.asInt();
					if (style > -1 || style < 4)
					{
						CellRangeAddress rangeAddress = null;
						JsonNode cellNode = requestNode.findPath(Key.CELL.key());
						JsonNode rangeNode = requestNode.findPath(Key.RANGE.key());
						if (Objects.nonNull(cellNode) && !cellNode.isMissingNode())
						{
							CellAddress cellAddress = getCellAddress(cellNode, Key.CELL.key());
							rangeAddress = getCellRangeAddress(cellAddress);
						}
						else if (Objects.nonNull(rangeNode) && !rangeNode.isMissingNode())
						{
							rangeAddress = getCellRangeAddress(rangeNode);
						}
						else
						{
							result = Fsl.addErrorMessage("missing_argument '" + Key.RANGE.key() + "'");
						}
						if (Objects.nonNull(rangeAddress))
						{
							FontStyle fontStyle = FontStyle.values()[style];
							fontStyle.setFontStyle(font);
							cellStyle.setFont(font);
							Iterator<CellAddress> cellAddresses = rangeAddress.iterator();
							while (cellAddresses.hasNext())
							{
								CellAddress cellAddress = cellAddresses.next();
								Cell cell = getOrCreateCell(sheet, cellAddress);
								cell.setCellStyle(cellStyle);
							}
						}
					}
					else
					{
						result = Fsl.addErrorMessage("invalid_fontstyle (possible values for 'style' are 0-3)");
					}
				}
				else
				{
					result = Fsl.addErrorMessage("invalid_argument 'style'");
				}
			}
			else
			{
				result = Fsl.addErrorMessage("missing_argument 'style'");
			}
		}
		return result;
	}

	public static boolean applyNumberFormat(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = true;
		Sheet sheet = getSheetIfPresent(requestNode);
		if (Objects.nonNull(sheet))
		{
			CellRangeAddress rangeAddress = null;
			if (Objects.nonNull(requestNode.get(Key.RANGE.key())))
			{
				rangeAddress = getCellRangeAddress(requestNode.get(Key.RANGE.key()));
			}
			else if (Objects.nonNull(requestNode.get(Key.CELL.key())))
			{
				CellAddress cellAddress = getCellAddress(requestNode.get(Key.CELL.key()), Key.CELL.key());
				rangeAddress = getCellRangeAddress(cellAddress);
			}
			if (Objects.nonNull(rangeAddress))
			{
				if (Objects.nonNull(requestNode.get(Key.FORMAT.key())))
				{
					int index = -1;
					if (TextNode.class.isInstance(requestNode.get(Key.FORMAT.key())))
					{
						String format = TextNode.class.cast(requestNode.get(Key.FORMAT.key())).asText();
						if (Objects.nonNull(format))
						{
							DataFormat dataFormat = sheet.getWorkbook().createDataFormat();
							index = dataFormat.getFormat(format);
							formatNumber(sheet, rangeAddress, index);
						}
						else
						{
							result = Fsl.addErrorMessage("invalid_argument '" + Key.FORMAT.key() + "'");
						}
					}
					else if (IntNode.class.isInstance(requestNode.get(Key.FORMAT.key())))
					{
						index = IntNode.class.cast(requestNode.get(Key.FORMAT.key())).asInt();
						formatNumber(sheet, rangeAddress, index);
					}
					else
					{
						result = Fsl.addErrorMessage("invalid_argument '" + Key.FORMAT.key() + "'");
					}
				}
				else
				{
					result = Fsl.addErrorMessage("missing_argument '" + Key.FORMAT.key() + "'");
				}
			}
			else
			{
				Fsl.addErrorMessage("missing_argument '" + Key.CELL.key() + "' or '" + Key.RANGE.key() + "'");
			}
		}
		return result;
	}
	
	public static boolean autoSizeColumns(ObjectNode requestNode, ObjectNode responseNode)
	{
		Sheet sheet = getSheetIfPresent(requestNode);
		boolean result = Objects.nonNull(sheet);
		if (result)
		{
			CellRangeAddress cellRangeAddress = null;
			JsonNode rangeNode = requestNode.findPath(Key.RANGE.key());
			if (Objects.nonNull(rangeNode) && !rangeNode.isMissingNode())
			{
				cellRangeAddress = getCellRangeAddress(rangeNode);
				if (Objects.isNull(cellRangeAddress))
				{
					Fsl.addErrorMessage("invalid argument '"+ Key.RANGE.key());
				}
			}
			else
			{
				JsonNode cellNode = requestNode.findPath(Key.CELL.key());
				if (Objects.nonNull(cellNode) && !cellNode.isMissingNode())
				{
					CellAddress cellAddress = getCellAddress(cellNode, Key.CELL.key());
					if (Objects.nonNull(cellAddress))
					{
						cellRangeAddress = getCellRangeAddress(cellAddress);
					}
					else
					{
						Fsl.addErrorMessage("invalid argument '"+ Key.CELL.key());
					}
				}
				else
				{
					Fsl.addErrorMessage("missing argument one of '"+ Key.RANGE.key() + "', '" + Key.CELL.key() + "'");
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
	
	public static boolean rotateCells(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = IntNode.class.isInstance(requestNode.get(Key.ROTATION.key()));
		if (result)
		{
			int rotation = IntNode.class.cast(requestNode.get(Key.ROTATION.key())).asInt();
			Sheet sheet = getSheetIfPresent(requestNode);
			if (Objects.nonNull(sheet))
			{
				CellRangeAddress rangeAddress = null;
				JsonNode cellNode = requestNode.findPath(Key.CELL.key());
				JsonNode rangeNode = requestNode.findPath(Key.RANGE.key());
				if (Objects.nonNull(cellNode))
				{
					CellAddress cellAddress = getCellAddress(cellNode, Key.CELL.key());
					rangeAddress = getCellRangeAddress(cellAddress);
				}
				else if (Objects.nonNull(rangeNode))
				{
					rangeAddress = getCellRangeAddress(rangeNode);
				}
				else
				{
					Fsl.addErrorMessage("missing argument '" + Key.RANGE.key() + "'");
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
								cellStyle.setRotation((short)rotation);
								cell.setCellStyle(cellStyle);
							}
						}
					}
				}
				else
				{
					Fsl.addErrorMessage("missing_argument '" + Key.RANGE.key() + "'");
				}
			}
			else
			{
				Fsl.addErrorMessage("missing_argument '" + Key.SHEET.key() + "'");
			}
		}
		else
		{
			Fsl.addErrorMessage("missing_argument '" + Key.ROTATION.key() + "'");
		}
		return result;
	}
	
	public static boolean alignHorizontally(ObjectNode requestNode, ObjectNode responseNode)
	{
		Sheet sheet = getSheetIfPresent(requestNode);
		boolean result = Objects.nonNull(sheet);
		if (result)
		{
			CellRangeAddress rangeAddress = null;
			JsonNode cellNode = requestNode.findPath(Key.CELL.key());
			JsonNode rangeNode = requestNode.findPath(Key.RANGE.key());
			if (Objects.nonNull(cellNode) && !cellNode.isMissingNode())
			{
				CellAddress cellAddress = getCellAddress(cellNode, Key.CELL.key());
				rangeAddress = getCellRangeAddress(cellAddress);
			}
			else if (Objects.nonNull(rangeNode) && !rangeNode.isMissingNode())
			{
				rangeAddress = getCellRangeAddress(rangeNode);
			}
			if (Objects.nonNull(rangeAddress))
			{
				if (Objects.nonNull(requestNode.get(Key.ALIGNMENT.key())))
				{
					if (TextNode.class.isInstance(requestNode.get(Key.ALIGNMENT.key())))
					{
						String align = TextNode.class.cast(requestNode.get(Key.ALIGNMENT.key())).asText();
						Key key = Key.valueOf(align.toUpperCase());
						if (Objects.nonNull(key))
						{
							Iterator<CellAddress> cellAddresses = rangeAddress.iterator();
							while (cellAddresses.hasNext())
							{
								CellAddress cellAddress = cellAddresses.next();
								if (Row.class.isInstance(sheet.getRow(cellAddress.getRow())))
								{
									Row row = Row.class.cast(sheet.getRow(cellAddress.getRow()));
									if (Cell.class.isInstance(row.getCell(cellAddress.getColumn())))
									{
										Cell cell = Cell.class.cast(row.getCell(cellAddress.getColumn()));
										CellUtil.setAlignment(cell, HorizontalAlignment.valueOf(align.toUpperCase()));
									}
								}
							}
						}
					}
				}				
			}
			else
			{
				Fsl.addErrorMessage("missing argument '" + Key.RANGE.key() + "'");
			}
		}
		return result;
	}

	private static Sheet createSheet(Workbook workbook, JsonNode sheetNode)
	{
		Sheet sheet = null;
		if (Objects.nonNull(workbook))
		{
			if (Objects.nonNull(sheetNode) && !sheetNode.isMissingNode())
			{
				if (TextNode.class.isInstance(sheetNode))
				{
					sheet = workbook.getSheet(sheetNode.asText());
					if (Objects.isNull(sheet))
					{
						sheet = workbook.createSheet(sheetNode.asText());
					}
					else
					{
						Fsl.addErrorMessage("sheet '" + sheetNode.asText() + "' already exists");
					}
				}
				else 
				{
					Fsl.addErrorMessage("invalid_argument 'sheet'");
				}
			}
			else
			{
				sheet = workbook.createSheet();
			}
		}
		return sheet;
	}

	private static void formatNumber(Sheet sheet, CellRangeAddress rangeAddress, int index)
	{
		CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
		cellStyle.setDataFormat((short) index);
		Iterator<CellAddress> cellAddresses = rangeAddress.iterator();
		while (cellAddresses.hasNext())
		{
			CellAddress cellAddress = cellAddresses.next();
			Cell cell = getOrCreateCell(sheet, cellAddress);
			cell.setCellStyle(cellStyle);
		}
	}

	private static Workbook createWorkbook(String name)
	{
		Workbook workbook = null;
		if (!name.trim().isEmpty())
		{
			if (name.endsWith(".xls"))
			{
				Xls.workbooks.put(name, new HSSFWorkbook());
			}
			else
			{
				Fsl.addErrorMessage("Before: new XSSFWorkbook()");
				Xls.workbooks.put(name, new XSSFWorkbook());
				Fsl.addErrorMessage("After: new XSSFWorkbook()");
			}
		}
		else
		{
			Fsl.addErrorMessage("invalid_argument '" + Key.WORKBOOK.key() + "'");
		}
		return workbook;
	}
	
	private static void setRichTextString(Cell cell, String value)
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
	
	private static List<String> getCallableMethods()
	{
		List<String> callableMethods = new ArrayList<String>();
		Method[] methods = Xls.class.getDeclaredMethods();
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
						callableMethods.add(Xls.class.getSimpleName() + "." + method.getName());
					}
				}
			}
		}
		return callableMethods;
	}

	private static CellAddress getCellAddress(int row, int col)
	{
		CellAddress cellAddress = null;
		try
		{
			cellAddress = new CellAddress(row, col);
		}
		catch (Exception e)
		{
			Fsl.addErrorMessage("invalid_argument '" + " + Key.CELL.key() + " + "'");
		}
		return cellAddress;
	}
	
	private static CellAddress getCellAddress(JsonNode cellNode, String key)
	{
		CellAddress cellAddress = null;
		if (Objects.nonNull(cellNode) && !cellNode.isMissingNode())
		{
			if (TextNode.class.isInstance(cellNode))
			{
				cellAddress = getCellAddress(TextNode.class.cast(cellNode), key);
			}
			else if (ObjectNode.class.isInstance(cellNode))
			{
				cellAddress = getCellAddress(ObjectNode.class.cast(cellNode), key);
			}
			else
			{
				Fsl.addErrorMessage("invalid_argument '" + key + "'");
			}
		}
		else
		{
			Fsl.addErrorMessage("missing_argument '" + key + "'");
		}
		return cellAddress;
	}
	
	private static CellAddress getCellAddress(TextNode cellNode, String key)
	{
		CellAddress cellAddress = null;
		try
		{
			cellAddress = new CellAddress(cellNode.asText());
		}
		catch (Exception e)
		{
			Fsl.addErrorMessage("invalid cell address (" + key + ")");
		}
		return cellAddress;
	}

	private static CellAddress getCellAddress(ObjectNode cellNode, String key)
	{
		CellAddress cellAddress = null;
		if (Objects.nonNull(cellNode) && !cellNode.isMissingNode())
		{
			JsonNode rowNode = cellNode.findPath(Key.ROW.key());
			if (Objects.nonNull(rowNode) && !rowNode.isMissingNode())
			{
				if (IntNode.class.isInstance(rowNode))
				{
					int rowIndex = rowNode.asInt();
					JsonNode colNode = cellNode.findPath(Key.COL.key());
					if (Objects.nonNull(colNode) && !colNode.isMissingNode())
					{
						if (IntNode.class.isInstance(colNode))
						{
							int colIndex = colNode.asInt();
							cellAddress = new CellAddress(rowIndex, colIndex);
						}
						else
						{
							Fsl.addErrorMessage("invalid argument '" + Key.COL.key());
						}
					}
					else
					{
						Fsl.addErrorMessage("missing argument '" + Key.COL.key());
					}
				}
				else
				{
					Fsl.addErrorMessage("invalid argument '" + Key.ROW.key());
				}
			}
			else
			{
				Fsl.addErrorMessage("missing argument '" + Key.ROW.key());
			}
		}
		else
		{
			Fsl.addErrorMessage("missing argument '" + Key.CELL.key());
		}
		return cellAddress;
	}

	private static CellRangeAddress getCellRangeAddress(CellAddress address)
	{
		return getCellRangeAddress(address, address);
	}

	private static CellRangeAddress getCellRangeAddress(CellAddress topLeftAddress, CellAddress bottomRightAddress)
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
				Fsl.addErrorMessage("missing bottom right values");
			}
		}
		else
		{
			Fsl.addErrorMessage("missing top left values");
		}
		return cellRangeAddress;
	}

	private static CellRangeAddress getCellRangeAddress(int topRow, int bottomRow, int leftCol, int rightCol)
	{
		return new CellRangeAddress(Math.min(topRow, bottomRow), Math.max(topRow, bottomRow),
				Math.min(leftCol, rightCol), Math.max(leftCol, rightCol));
	}

	private static CellRangeAddress getCellRangeAddress(JsonNode rangeNode)
	{
		CellRangeAddress rangeAddress = null;
		if (Objects.nonNull(rangeNode) && !rangeNode.isMissingNode())
		{
			if (TextNode.class.isInstance(rangeNode))
			{
				rangeAddress = getCellRangeAddress(TextNode.class.cast(rangeNode));
			}
			else if (ObjectNode.class.isInstance(rangeNode))
			{
				rangeAddress = getCellRangeAddress(ObjectNode.class.cast(rangeNode));
			}
			else
			{
				Fsl.addErrorMessage("invalid argument '" + Key.RANGE.key() + "'");
			}
		}
		else
		{
			Fsl.addErrorMessage("missing argument '" + Key.RANGE.key() + "'");
		}
		return rangeAddress;
	}
	
	private static CellRangeAddress getCellRangeAddress(TextNode rangeNode)
	{
		CellRangeAddress rangeAddress = null;
		if (Objects.nonNull(rangeNode))
		{
			try
			{
				String[] cells = rangeNode.asText().split(":");
				switch (cells.length)
				{
					case 1:
					{
						CellAddress address = new CellAddress(cells[0]);
						rangeAddress = getCellRangeAddress(address, address);
						break;
					}
					case 2:
					{
						CellAddress topLeftAddress = new CellAddress(cells[0]);
						CellAddress bottomRightAddress = new CellAddress(cells[1]);
						rangeAddress = getCellRangeAddress(topLeftAddress, bottomRightAddress);
						break;
					}
					default:
					{
						Fsl.addErrorMessage("invalid_argument '" + Key.RANGE.key() + "'");
					}
				}
			}
			catch (Exception e)
			{
				Fsl.addErrorMessage("invalid_argument '" + Key.RANGE.key() + "'");
			}
		}
		else
		{
			Fsl.addErrorMessage("invalid_argument '" + Key.RANGE.key() + "'");
		}
		return rangeAddress;
	}

	private static CellRangeAddress getCellRangeAddress(ObjectNode rangeNode)
	{
		CellRangeAddress cellRangeAddress = null;
		if (Objects.nonNull(rangeNode))
		{
			CellAddress topLeftAddress = null;
			CellAddress bottomRightAddress = null;
			JsonNode topLeftNode = rangeNode.get(Key.TOP_LEFT.key());
			if (Objects.nonNull(topLeftNode) && !topLeftNode.isMissingNode())
			{
				topLeftAddress = getCellAddress(topLeftNode, Key.TOP_LEFT.key());
				if (Objects.isNull(topLeftAddress))
				{
					Fsl.addErrorMessage("invalid argument '" + Key.TOP_LEFT.key() + "'");
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
								Fsl.addErrorMessage("invalid argument '" + Key.LEFT.key() + "'");
							}
						}
						else
						{
							Fsl.addErrorMessage("missing argument '" + Key.LEFT.key() + "'");
						}
					}
					else
					{
						Fsl.addErrorMessage("invalid argument '" + Key.TOP.key() + "'");
					}
				}
				else
				{
					Fsl.addErrorMessage("missing argument '" + Key.TOP.key() + "'");
				}
			}
			if (Objects.nonNull(topLeftAddress))
			{
				JsonNode bottomRightNode = rangeNode.get(Key.BOTTOM_RIGHT.key());
				if (Objects.nonNull(bottomRightNode) && !bottomRightNode.isMissingNode())
				{
					bottomRightAddress = getCellAddress(bottomRightNode, Key.BOTTOM_RIGHT.key());
					if (Objects.isNull(bottomRightAddress))
					{
						Fsl.addErrorMessage("invalid argument '" + Key.BOTTOM_RIGHT.key() + "'");
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
									Fsl.addErrorMessage("invalid argument '" + Key.RIGHT.key() + "'");
								}
							}
							else
							{
								Fsl.addErrorMessage("missing argument '" + Key.RIGHT.key() + "'");
							}
						}
						else
						{
							Fsl.addErrorMessage("invalid argument '" + Key.BOTTOM.key() + "'");
						}
					}
					else
					{
						Fsl.addErrorMessage("missing argument '" + Key.BOTTOM.key() + "'");
					}
				}
			}
			if (Objects.nonNull(topLeftAddress))
			{
				if (Objects.nonNull(bottomRightAddress))
				{
					cellRangeAddress = getCellRangeAddress(topLeftAddress, bottomRightAddress);
				}
				else
				{
					Fsl.addErrorMessage("missing argument '" + Key.BOTTOM_RIGHT.key() + "'");
				}
			}
			else
			{
				Fsl.addErrorMessage("missing argument '" + Key.TOP_LEFT.key() + "'");
			}
		}
		else
		{
			Fsl.addErrorMessage("missing_argument '" + Key.RANGE.key() + "'");
		}
		return cellRangeAddress;
	}

	private static Cell getOrCreateCell(Sheet sheet, CellAddress cellAddress)
	{
		Cell cell = null;
		Row row = null;
		if (isWithinSheetRange(cellAddress))
		{
			row = getOrCreateRow(sheet, cellAddress);
			cell = getOrCreateCell(row, cellAddress.getColumn());
		}
		else
		{
			Fsl.addErrorMessage("celladdress_out_of_range");
		}
		return cell;
	}

	private static Cell getOrCreateCell(Sheet sheet, String address)
	{
		return getOrCreateCell(sheet, new CellAddress(address));
	}

	protected static Cell getOrCreateCell(Row row, int columnIndex)
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

	private static Row getOrCreateRow(Sheet sheet, CellAddress cellAddress)
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

	private static Row getOrCreateRow(Sheet sheet, int rowIndex)
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

	private static Workbook getWorkbookIfPresent(JsonNode parentNode)
	{
		Workbook workbook = null;
		JsonNode workbookNode = parentNode.findPath(Key.WORKBOOK.key());
		if (Objects.nonNull(workbookNode) && !workbookNode.isMissingNode())
		{
			if (TextNode.class.isInstance(workbookNode))
			{
				workbook = Xls.workbooks.get(workbookNode.asText());
			}
			else
			{
				Fsl.addErrorMessage("invalid_argument '" + Key.WORKBOOK.key() + "'");
			}
		}
		else
		{
			if (Objects.nonNull(Xls.activeWorkbook))
			{
				workbook = Xls.activeWorkbook;
			}
			else
			{
				Fsl.addErrorMessage("missing_argument '" + Key.WORKBOOK.key() + "'");
			}
		}
		return workbook;
	}

	private static Sheet getSheetIfPresent(JsonNode parentNode)
	{
		Sheet sheet = null;
		JsonNode workbookNode = parentNode.findPath(Key.WORKBOOK.key());
		Workbook workbook = getWorkbookIfPresent(workbookNode);
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
					Fsl.addErrorMessage("invalid_argument '" + Key.SHEET.key() + "'");
				}
			}
			else
			{
				sheet = workbook.getSheetAt(workbook.getActiveSheetIndex());
			}
		}
		return sheet;
	}
	
	private static Sheet findSheet(Workbook workbook, JsonNode sheetNode)
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
						Fsl.addErrorMessage("missing sheet '" + sheetIndex + "'");
					}
				}
				else
				{
					Fsl.addErrorMessage("invalid_argument '" + Key.SHEET.key() + "'");
				}
			}
			else
			{
				Fsl.addErrorMessage("missing_argument '" + Key.SHEET.key() + "'");
			}
		}
		return sheet;
	}
	
	private static boolean isWithinSheetRange(CellRangeAddress cellRangeAddress)
	{
		CellAddress firstAddress = new CellAddress(cellRangeAddress.getFirstRow(), cellRangeAddress.getFirstColumn());
		CellAddress lastAddress = new CellAddress(cellRangeAddress.getLastRow(), cellRangeAddress.getLastColumn());
		return isWithinSheetRange(firstAddress) && isWithinSheetRange(lastAddress);
	}

	private static boolean isWithinSheetRange(CellAddress cellAddress)
	{
		return cellAddress.getRow() > -1
				&& cellAddress.getRow() < Xls.activeWorkbook.getSpreadsheetVersion().getMaxRows()
				&& cellAddress.getColumn() > -1
				&& cellAddress.getColumn() < Xls.activeWorkbook.getSpreadsheetVersion().getMaxColumns();
	}
	
	private static boolean isWithinSheetRange(Cell cell)
	{
		return isWithinSheetRange(new CellAddress(cell));
	}
	
	private static boolean isWithinSheetRange(Row row)
	{
		return row.getRowNum() < Xls.activeWorkbook.getSpreadsheetVersion().getMaxRows();
	}

	private static boolean releaseWorkbook()
	{
		boolean result = Objects.nonNull(Xls.activeWorkbook);
		if (result)
		{
			String name = null;
			Set<Entry<String, Workbook>> workbooks = Xls.workbooks.entrySet();
			Iterator<Entry<String, Workbook>> iterator = workbooks.iterator();
			while (iterator.hasNext())
			{
				Entry<String, Workbook> workbook = iterator.next();
				if (workbook == Xls.activeWorkbook)
				{
					name = workbook.getKey();
					break;
				}
			}
			result = Objects.nonNull(name);
			if (result)
			{
				Xls.workbooks.remove(name);
				Xls.activeWorkbook = null;
			}
		}
		return result;
	}

	private static boolean releaseWorkbook(ObjectNode requestNode)
	{
		boolean result = Objects.nonNull(requestNode.get(Key.WORKBOOK.key()));
		if (result)
		{
			if (TextNode.class.isInstance(requestNode.get(Key.WORKBOOK.key())))
			{
				Workbook workbook = Xls.workbooks.remove(TextNode.class.cast(requestNode.get(Key.WORKBOOK.key())));
				if (Objects.nonNull(workbook))
				{
					if (workbook == Xls.activeWorkbook)
					{
						Xls.activeWorkbook = null;
					}
				}
				else
				{
					result = Fsl.addErrorMessage("missing_argument 'workbook'");
				}
			}
		}
		else
		{
			result = releaseWorkbook();
		}
		return result;
	}

	private static void releaseWorkbooks()
	{
		Xls.workbooks.clear();
		Xls.activeWorkbook = null;
	}

	private static SpreadsheetVersion getVersion()
	{
		SpreadsheetVersion version = null;
		if (workbooks.size() > 0)
		{
			version = workbooks.values().iterator().next().getSpreadsheetVersion();
		}
		return version;
	}
	
	private static FormulaParsingWorkbook getFormulaParsingWorkbook(Sheet sheet)
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

	private static FormulaRenderingWorkbook getFormulaRenderingWorkbook(Sheet sheet)
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
	
	private static JsonNode findNode(ObjectNode requestNode, String name)
	{
		return requestNode.findParent(name);
	}

	protected static void copyCell(Cell sourceCell, Cell targetCell)
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

	protected static String copyFormula(Sheet sheet, String formula, int rowDiff, int colDiff)
	{
		FormulaParsingWorkbook workbookWrapper = getFormulaParsingWorkbook(sheet);
		Ptg[] ptgs = FormulaParser.parse(formula, workbookWrapper, FormulaType.CELL, sheet.getWorkbook().getSheetIndex(sheet));
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

	private static boolean saveFile(File file, Workbook workbook)
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
			result = Fsl.addErrorMessage(e.getLocalizedMessage());
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

	private static boolean saveWorkbook(String path, Workbook workbook)
	{
		boolean result = true;
		if (Objects.nonNull(workbook))
		{
			if (Objects.nonNull(path))
			{
				result = saveFile(new File(path), workbook);
			}
			else
			{
				result = Fsl.addErrorMessage("missing_argument 'path_name'");
			}
		}
		else
		{
			result = Fsl.addErrorMessage("missing_argument 'workbook'");
		}
		return result;
	}

	private static void setCellStyle(Cell cell, String styleFormat)
	{
		CellStyle style = Xls.activeWorkbook.createCellStyle();
		DataFormat format = Xls.activeWorkbook.createDataFormat();
		style.setDataFormat(format.getFormat(styleFormat));
		cell.setCellStyle(style);
	}

	private static Sheet setSheet(String name)
	{
		Sheet sheet = null;
		if (Objects.nonNull(name))
		{
			if (Objects.nonNull(Xls.activeWorkbook))
			{
				sheet = Xls.activeWorkbook.getSheet(name);
				if (Objects.isNull(sheet))
				{
					Fsl.addErrorMessage("missing_sheet 'name'");
				}
			}
			else
			{
				Fsl.addErrorMessage("missing_workbook");
			}
		}
		else
		{
			Fsl.addErrorMessage("missing_parameter 'name'");
		}
		return sheet;
	}

	private static boolean validateRangeAddress(SpreadsheetVersion spreadsheetVersion,
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
				result = Fsl.addErrorMessage("invalid range");
			}
		}
		else
		{
			result = Fsl.addErrorMessage("missing range");
		}
		return result;
	}
	
	private static CellRangeAddress getCellRangeAddress(CellRange<Cell> cellRange)
	{
		int top = cellRange.getTopLeftCell().getRowIndex();
		int left = cellRange.getTopLeftCell().getColumnIndex();
		int bottom = top + cellRange.getHeight();
		int right = left + cellRange.getWidth();
		return new CellRangeAddress(top, bottom, left, right);
	}

	public enum Key
	{
		// @formatter:off
		ADDRESS("address"),
		ALIGNMENT("alignment"),
		BOTTOM_RIGHT("bottom_right"),
		BOTTOM("bottom"),
		CELL("cell"),
		CENTER("center"),
		COL("col"),
		DIRECTION("direction"),
		FORMAT("format"),
		INDEX("index"),
		ITEMS("items"),
		LEFT("left"),
		PATH_NAME("path_name"),
		RANGE("range"),
		RIGHT("right"),
		ROTATION("rotation"),
		ROW("row"),
		SHEET("sheet"),
		SOURCE("source"),
		START("start"),
		STYLE("style"),
		TARGET("target"),
		TOP("top"),
		TOP_LEFT("top_left"),
		VALUES("values"),
		WORKBOOK("workbook");
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

	protected enum Direction
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

		public boolean validRange(CellAddress cellAddress, int numberOfCells)
		{
			switch (this)
			{
				case LEFT:
				{
					boolean valid = cellAddress.getColumn() - numberOfCells + 1 < 0 ? false : true;
					if (!valid)
					{
						Fsl.addErrorMessage("minimal_horizontal_cell_position exceeds sheet's extent negatively");
					}
					return valid;
				}
				case RIGHT:
				{
					boolean valid = cellAddress.getColumn() + numberOfCells - 1 > Xls.activeWorkbook
							.getSpreadsheetVersion().getLastColumnIndex() ? false : true;
					if (!valid)
					{
						Fsl.addErrorMessage("maximal_horizontal_cell_position exceeds sheet's extent positively");
					}
					return valid;
				}
				case UP:
				{
					boolean valid = cellAddress.getRow() - numberOfCells + 1 < 0 ? false : true;
					if (!valid)
					{
						Fsl.addErrorMessage("minimal_vertical_cell_position exceeds sheet's extent negatively");
					}
					return valid;
				}
				case DOWN:
				{
					boolean valid = cellAddress.getRow() + numberOfCells - 1 > Xls.activeWorkbook
							.getSpreadsheetVersion().getLastRowIndex() ? false : true;
					if (!valid)
					{
						Fsl.addErrorMessage("maximal_vertical_cell_position exceeds sheet's extent positively");
					}
					return valid;
				}
				case DEFAULT:
				{
					boolean valid = cellAddress.getColumn() + numberOfCells - 1 > Xls.activeWorkbook
							.getSpreadsheetVersion().getLastColumnIndex() ? false : true;
					if (!valid)
					{
						Fsl.addErrorMessage("maximal_horizontal_cell_position exceeds sheet's extent positively");
					}
					return valid;
				}
				default:
					return false;
			}
		}
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
					break;
				}
				case BOLD:
				{
					font.setBold(true);
					font.setItalic(false);
					break;
				}
				case ITALIC:
				{
					font.setBold(false);
					font.setItalic(true);
					break;
				}
				case BOLD_ITALIC:
				{
					font.setBold(true);
					font.setItalic(true);
					break;
				}
			}
		}
	}

	private enum BuiltinDataFormats
	{

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

}