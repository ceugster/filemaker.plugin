package ch.eugster.filemaker.fsl.plugin.test;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.eugster.filemaker.fsl.plugin.Fsl;
import ch.eugster.filemaker.fsl.plugin.xls.Xls;

public abstract class AbstractXlsTest
{
	protected static ObjectMapper mapper = new ObjectMapper();

	protected static final String WORKBOOK_1 = "./targets/workbook1.xlsx";
	
	protected static final String SHEET0 = "Sheet0";

	protected Xls xls;
	
	@BeforeEach
	protected void beforeEach()
	{
		if (Objects.isNull(xls))
		{
			Fsl fsl = Fsl.getFsl(System.getProperty("user.name"));
			xls = Xls.class.cast(fsl.getExecutor("Xls"));
		}
	}
	
	@AfterEach
	protected void afterEach()
	{
		if (Objects.nonNull(xls))
		{
			Fsl fsl = Fsl.getFsl(System.getProperty("user.name"));
			xls = Xls.class.cast(fsl.getExecutor("Xls"));
			xls.releaseWorkbooks(mapper.createObjectNode(), mapper.createObjectNode());
		}
	}
	
	protected void openExistingWorkbook(String path) throws EncryptedDocumentException, IOException
	{
		File file = new File(path);
		Workbook workbook = WorkbookFactory.create(file);
		xls.activeWorkbook = workbook;
		xls.workbooks.put(path, workbook);
	}

	protected Workbook prepareWorkbookIfMissing()
	{
		return prepareWorkbookIfMissing(WORKBOOK_1);
	}

	protected Workbook prepareWorkbookIfMissing(String workbook)
	{
		xls.activeWorkbook = new XSSFWorkbook();
		xls.workbooks.put(workbook, xls.activeWorkbook);
		return xls.activeWorkbook;
	}

	protected Sheet prepareWorkbookAndSheetIfMissing()
	{
		return prepareWorkbookAndSheetIfMissing(WORKBOOK_1, SHEET0);
	}

	protected Sheet prepareWorkbookAndSheetIfMissing(String wb, String sheetName)
	{
		Workbook workbook = prepareWorkbookIfMissing(wb);
		Sheet sheet = workbook.createSheet(sheetName);
		workbook.setActiveSheet(workbook.getSheetIndex(sheet));
		return sheet;
	}

	protected void releaseAllWorkbooks()
	{
		xls.releaseWorkbooks(mapper.createObjectNode(), mapper.createObjectNode());
	}
}
