package ch.eugster.filemaker.fsl.plugin.xls;

import java.io.File;
import java.io.FileOutputStream;
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
import org.apache.poi.xssf.usermodel.IndexedColorMap;
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
					result = Fsl.addErrorMessage("missing_sheet '" + sheetNode.asText() + "'");
				}
			}
		}
		return result;
	}

	public static boolean activateWorkbook(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = true;
		JsonNode workbookNode = requestNode.findPath(Key.WORKBOOK.key());
		if (workbookNode.isTextual())
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
		return result;
	}

	public static boolean activeSheetPresent(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = activeSheetPresent();
		responseNode.put("present", result ? 1 : 0);
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
					result = validateRangeAddress(targetSheet.getWorkbook().getSpreadsheetVersion(),
							targetRangeAddress);
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
				result = Fsl.addErrorMessage("invalid_argument '" + Key.WORKBOOK.key() + "'");
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
			workbook = workbooks.remove(wb);
			if (workbook == activeWorkbook)
			{
				activeWorkbook = null;
			}
		}
		else
		{
			workbook = Xls.activeWorkbook;
			Set<Entry<String, Workbook>> entries = workbooks.entrySet();
			Iterator<Entry<String, Workbook>> iterator = entries.iterator();
			while (iterator.hasNext())
			{
				Entry<String, Workbook> entry = iterator.next();
				if (entry.getValue() == workbook)
				{
					workbook = workbooks.remove(entry.getKey());
					Xls.activeWorkbook = null;
					break;
				}
			}
		}
		return result;
	}

	public static void releaseWorkbooks(ObjectNode requestNode, ObjectNode responseNode)
	{
		Xls.workbooks.clear();
		Xls.activeWorkbook = null;
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
		String target = null;
		String wb = null;

		JsonNode workbookNode = requestNode.findPath(Key.WORKBOOK.key());
		if (workbookNode.isTextual())
		{
			wb = workbookNode.asText();
		}
		JsonNode targetNode = requestNode.findPath(Key.TARGET.key());
		if (targetNode.isTextual())
		{
			target = targetNode.asText();
		}
		Workbook workbook = null;
		if (Objects.nonNull(wb))
		{
			workbook = workbooks.get(wb);
			if (Objects.nonNull(workbook))
			{
				if (Objects.nonNull(target))
				{
					result = saveWorkbookAs(workbook, target);
				}
				else
				{
					result = saveWorkbook(workbook);
				}
			}
			else
			{
				result = Fsl.addErrorMessage("invalid argument '" + Key.WORKBOOK.key() + "'");
			}
		}
		else
		{
			workbook = Xls.activeWorkbook;
			if (Objects.nonNull(workbook))
			{
				if (Objects.nonNull(target))
				{
					result = saveWorkbookAs(workbook, target);
				}
				else
				{
					result = saveWorkbook(workbook);
				}
			}
			else
			{
				result = Fsl.addErrorMessage("missing argument '" + Key.WORKBOOK.key() + "'");
			}
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
			result = !cellNode.isMissingNode();
			if (result)
			{
				CellAddress cellAddress = getCellAddress(cellNode, Key.CELL.key());
				if (Objects.nonNull(cellAddress))
				{
					JsonNode valuesNode = requestNode.findPath(Key.VALUES.key());
					if (valuesNode.isArray())
					{
						Direction direction = Direction.DEFAULT;
						if (valuesNode.size() > 0)
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
							if (direction.validRange(cellAddress, valuesNode.size()))
							{
								for (int i = 0; i < valuesNode.size(); i++)
								{
									JsonNode valueNode = valuesNode.get(i);
									JsonNodeType nodeType = valueNode.getNodeType();
									Cell cell = getOrCreateCell(sheet, cellAddress);
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
												Date date = DateFormat.getDateInstance().parse(valueNode.asText());
												cell.setCellFormula(valueNode.asText());
												FormulaEvaluator evaluator = Xls.activeWorkbook.getCreationHelper()
														.createFormulaEvaluator();
												CellType cellType = evaluator.evaluateFormulaCell(cell);
												System.out.println(cellType);
											}
											catch (ParseException pe)
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
														FormulaEvaluator evaluator = Xls.activeWorkbook.getCreationHelper()
																.createFormulaEvaluator();
														CellType cellType = evaluator.evaluateFormulaCell(cell);
														System.out.println(cellType);
													}
													catch (FormulaParseException fpe)
													{
														setRichTextString(cell, valueNode.asText());
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
									cellAddress = direction.nextIndex(cellAddress);
								}
								responseNode.put(Key.ROW.key(), cellAddress.getRow());
								responseNode.put(Key.COL.key(), cellAddress.getColumn());
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

	public static boolean setPrintSetup(ObjectNode requestNode, ObjectNode responseNode)
	{
		boolean result = true;
		
		Sheet sheet = getSheetIfPresent(requestNode);
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
					result = Fsl.addErrorMessage("invalid argument 'orientation'");
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

	protected static boolean activateSheet(Sheet sheet)
	{
		boolean result = activateWorkbook(sheet.getWorkbook());
		if (result)
		{
			Xls.activeWorkbook.setActiveSheet(Xls.activeWorkbook.getSheetIndex(sheet));
		}
		return result;
	}

	protected static boolean activateWorkbook(Workbook workbook)
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

	protected static boolean activeSheetPresent()
	{
		boolean present = Objects.nonNull(Xls.activeWorkbook);
		if (present)
		{
			present = Xls.activeWorkbook.getActiveSheetIndex() > -1;
		}
		return present;
	}

	public static boolean applyFontStyles(ObjectNode requestNode, ObjectNode responseNode)
	{
		Sheet sheet = getSheetIfPresent(requestNode);
		boolean result = Objects.nonNull(sheet);
		if (result)
		{
			CellRangeAddress rangeAddress = null;
			JsonNode cellNode = requestNode.findPath(Key.CELL.key());
			if (Objects.nonNull(cellNode) && !cellNode.isMissingNode())
			{
				CellAddress cellAddress = getCellAddress(cellNode, Key.CELL.key());
				rangeAddress = getCellRangeAddress(cellAddress);
			}
			else
			{
				JsonNode rangeNode = requestNode.findPath(Key.RANGE.key());
				if (Objects.nonNull(rangeNode) && !rangeNode.isMissingNode())
				{
					rangeAddress = getCellRangeAddress(rangeNode);
				}
				else
				{
					result = Fsl.addErrorMessage("missing_argument '" + Key.RANGE.key() + "'");
				}
			}
			if (result && Objects.nonNull(rangeAddress))
			{
				Iterator<CellAddress> cellAddresses = rangeAddress.iterator();
				while (cellAddresses.hasNext())
				{
					CellAddress cellAddress = cellAddresses.next();
					Cell cell = getOrCreateCell(sheet, cellAddress);
					CellStyle cellStyle = cell.getCellStyle();
					MergedCellStyle mcs = new MergedCellStyle(cellStyle);
					int fontIndex = cellStyle.getFontIndex();
					Font font = sheet.getWorkbook().getFontAt(fontIndex);
					MergedFont m = new MergedFont(font);
					m.applyRequestedFontStyles(requestNode);
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

	public static boolean applyCellStyles(ObjectNode requestNode, ObjectNode responseNode)
	{
		Sheet sheet = getSheetIfPresent(requestNode);
		boolean result = Objects.nonNull(sheet);
		if (result)
		{
			CellRangeAddress rangeAddress = null;
			JsonNode cellNode = requestNode.findPath(Key.CELL.key());
			if (Objects.nonNull(cellNode) && !cellNode.isMissingNode())
			{
				CellAddress cellAddress = getCellAddress(cellNode, Key.CELL.key());
				rangeAddress = getCellRangeAddress(cellAddress);
			}
			else
			{
				JsonNode rangeNode = requestNode.findPath(Key.RANGE.key());
				if (Objects.nonNull(rangeNode) && !rangeNode.isMissingNode())
				{
					rangeAddress = getCellRangeAddress(rangeNode);
				}
				else
				{
					result = Fsl.addErrorMessage("missing_argument '" + Key.RANGE.key() + "'");
				}
			}
			if (result && Objects.nonNull(rangeAddress))
			{
				Iterator<CellAddress> cellAddresses = rangeAddress.iterator();
				while (cellAddresses.hasNext())
				{
					CellAddress cellAddress = cellAddresses.next();
					Cell cell = getOrCreateCell(sheet, cellAddress);
					MergedCellStyle m = new MergedCellStyle(cell.getCellStyle());
					result = m.applyRequestedStyles(requestNode);
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
					Fsl.addErrorMessage("invalid argument '" + Key.RANGE.key());
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
						Fsl.addErrorMessage("invalid argument '" + Key.CELL.key());
					}
				}
				else
				{
					Fsl.addErrorMessage("missing argument one of '" + Key.RANGE.key() + "', '" + Key.CELL.key() + "'");
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
								cellStyle.setRotation((short) rotation);
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

	protected static Sheet createSheet(Workbook workbook, JsonNode sheetNode)
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

	protected static void formatNumber(Sheet sheet, CellRangeAddress rangeAddress, int index)
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

	protected static Workbook createWorkbook(String name)
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
				Xls.workbooks.put(name, new XSSFWorkbook());
			}
		}
		else
		{
			Fsl.addErrorMessage("invalid_argument '" + Key.WORKBOOK.key() + "'");
		}
		return workbook;
	}

	protected static void setRichTextString(Cell cell, String value)
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

	protected static List<String> getCallableMethods()
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

	protected static CellAddress getCellAddress(int row, int col)
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

	protected static CellAddress getCellAddress(JsonNode cellNode, String key)
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

	protected static CellAddress getCellAddress(TextNode cellNode, String key)
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

	protected static CellAddress getCellAddress(ObjectNode cellNode, String key)
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

	protected static CellRangeAddress getCellRangeAddress(CellAddress address)
	{
		return getCellRangeAddress(address, address);
	}

	protected static CellRangeAddress getCellRangeAddress(CellAddress topLeftAddress, CellAddress bottomRightAddress)
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

	protected static CellRangeAddress getCellRangeAddress(int topRow, int bottomRow, int leftCol, int rightCol)
	{
		return new CellRangeAddress(Math.min(topRow, bottomRow), Math.max(topRow, bottomRow),
				Math.min(leftCol, rightCol), Math.max(leftCol, rightCol));
	}

	protected static CellRangeAddress getCellRangeAddress(JsonNode rangeNode)
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

	protected static CellRangeAddress getCellRangeAddress(TextNode rangeNode)
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

	protected static CellRangeAddress getCellRangeAddress(ObjectNode rangeNode)
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

	protected static Cell getOrCreateCell(Sheet sheet, CellAddress cellAddress)
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

	protected static Cell getOrCreateCell(Sheet sheet, String address)
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

	protected static Row getOrCreateRow(Sheet sheet, CellAddress cellAddress)
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

	protected static Row getOrCreateRow(Sheet sheet, int rowIndex)
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

	protected static Workbook getWorkbookIfPresent(JsonNode parentNode)
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

	protected static Sheet getSheetIfPresent(JsonNode parentNode)
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

	protected static Sheet findSheet(Workbook workbook, JsonNode sheetNode)
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

	protected static boolean isWithinSheetRange(CellRangeAddress cellRangeAddress)
	{
		CellAddress firstAddress = new CellAddress(cellRangeAddress.getFirstRow(), cellRangeAddress.getFirstColumn());
		CellAddress lastAddress = new CellAddress(cellRangeAddress.getLastRow(), cellRangeAddress.getLastColumn());
		return isWithinSheetRange(firstAddress) && isWithinSheetRange(lastAddress);
	}

	protected static boolean isWithinSheetRange(CellAddress cellAddress)
	{
		return cellAddress.getRow() > -1
				&& cellAddress.getRow() < Xls.activeWorkbook.getSpreadsheetVersion().getMaxRows()
				&& cellAddress.getColumn() > -1
				&& cellAddress.getColumn() < Xls.activeWorkbook.getSpreadsheetVersion().getMaxColumns();
	}

	protected static boolean isWithinSheetRange(Cell cell)
	{
		return isWithinSheetRange(new CellAddress(cell));
	}

	protected static boolean isWithinSheetRange(Row row)
	{
		return row.getRowNum() < Xls.activeWorkbook.getSpreadsheetVersion().getMaxRows();
	}

	protected static void releaseWorkbooks()
	{
		Xls.workbooks.clear();
		Xls.activeWorkbook = null;
	}

	protected static SpreadsheetVersion getVersion()
	{
		SpreadsheetVersion version = null;
		if (workbooks.size() > 0)
		{
			version = workbooks.values().iterator().next().getSpreadsheetVersion();
		}
		return version;
	}

	protected static FormulaParsingWorkbook getFormulaParsingWorkbook(Sheet sheet)
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

	protected static FormulaRenderingWorkbook getFormulaRenderingWorkbook(Sheet sheet)
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

	protected static JsonNode findNode(ObjectNode requestNode, String name)
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

	protected static boolean saveFile(File file, Workbook workbook)
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

	protected static boolean saveWorkbook(Workbook workbook)
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
					result = saveFile(new File(entry.getKey()), workbook);
				}
			}
		}
		else
		{
			result = Fsl.addErrorMessage("missing argument '" + Key.WORKBOOK.key() + "'");
		}
		return result;
	}

	protected static boolean saveWorkbookAs(Workbook workbook, String target)
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
					result = saveFile(new File(target), workbook);
				}
			}
		}
		else
		{
			result = Fsl.addErrorMessage("missing argument '" + Key.WORKBOOK.key() + "'");
		}
		return result;
	}

	protected static CellStyle getCellStyle(Sheet sheet, MergedCellStyle m)
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
					&& cs.getDataFormat() == m.getDataFormat()&& cs.getFillBackgroundColor() == m.getBgColor()
					&& cs.getFillForegroundColor() == m.getFgColor()&& cs.getFontIndex() == m.getFontIndex()
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

	protected static Sheet setSheet(String name)
	{
		Sheet sheet = null;
		if (Objects.nonNull(name))
		{
			if (Objects.nonNull(Xls.activeWorkbook))
			{
				sheet = Xls.activeWorkbook.getSheet(name);
				if (Objects.isNull(sheet))
				{
					Fsl.addErrorMessage("missing sheet '" + Key.NAME.key() + "'");
				}
			}
			else
			{
				Fsl.addErrorMessage("missing argument 'workbook'");
			}
		}
		else
		{
			Fsl.addErrorMessage("missing argument'" + Key.SHEET.key() + "'");
		}
		return sheet;
	}

	protected static boolean validateRangeAddress(SpreadsheetVersion spreadsheetVersion,
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

	protected static CellRangeAddress getCellRangeAddress(CellRange<Cell> cellRange)
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

	protected static Font getFont(Sheet sheet, MergedFont m)
	{
		Font font = null;
		for (int i = 0; i < sheet.getWorkbook().getNumberOfFonts(); i++)
		{
			Font f = sheet.getWorkbook().getFontAt(i);
			if (f.getFontName().equals(m.getName()) && f.getFontHeightInPoints() == m.getSize() && f.getBold() == m.getBold()
					&& f.getItalic() == m.getItalic() && f.getUnderline() == m.getUnderline() && f.getStrikeout() == m.getStrikeOut()
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

	protected static BorderStyle valueOfBorderStyle(String borderStyleName)
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

	static class MergedFont
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
		
		public void applyRequestedFontStyles(ObjectNode requestNode)
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
					Fsl.addErrorMessage("invalid argument 'size'");
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
					Fsl.addErrorMessage("invalid argument 'underline'");
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
					Fsl.addErrorMessage("invalid argument 'type_offset'");
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
					Fsl.addErrorMessage("invalid argument 'color'");
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
	
	static class MergedCellStyle
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
		
		public boolean applyRequestedStyles(ObjectNode requestNode)
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
						result = Fsl.addErrorMessage("invalid argument 'alignment.horizontal'");
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
						result = Fsl.addErrorMessage("invalid argument 'alignment.vertical'");
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
							result = Fsl.addErrorMessage("invalid argument 'border.style.bottom'");
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
							result = Fsl.addErrorMessage("invalid argument 'border.style.left'");
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
							result = Fsl.addErrorMessage("invalid argument 'border.style.right'");
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
							result = Fsl.addErrorMessage("invalid argument 'border.style.top'");
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
							result = Fsl.addErrorMessage("invalid argument 'border.color.bottom'");
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
							result = Fsl.addErrorMessage("invalid argument 'border.color.left'");
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
							result = Fsl.addErrorMessage("invalid argument 'border.color.right'");
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
							result = Fsl.addErrorMessage("invalid argument 'border.color.top'");
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
//					result = Fsl.addErrorMessage("invalid argument 'dataFormat'");
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
						result = Fsl.addErrorMessage("invalid argument 'background.color'");
					}
				}
				else
				{
					result = Fsl.addErrorMessage("invalid argument 'background.color'");
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
						result = Fsl.addErrorMessage("invalid argument 'foreground.color'");
					}
				}
				else
				{
					result = Fsl.addErrorMessage("invalid argument 'foreground.color'");
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
						result = Fsl.addErrorMessage("invalid argument '" + Key.FILL_PATTERN.key() + "'");
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
					result = Fsl.addErrorMessage("invalid argument 'font'");
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