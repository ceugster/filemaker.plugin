# filemaker.plugin
This plugin provides the following features
# Fsl
Fsl is the main entry for using the functions.

Use: Fsl.execute("Xyz.command", "{}")

Actually there are the following modules:

## QRBill
*QRBill* has one command 'generate':

As parameter you have to use a json string with following structure:

{"amount":287.30,"currency":"CHF","iban":"CH450023023099999999A","reference":"RF102320QF02T323eUI234","message":"Rechnungsnr. 10978 / Auftragsnr. 3987","creditor":{"name":"Schreinerei Habegger & Söhne","address_line_1":"Uetlibergstrasse 138","address_line_2":"8045 Zürich","country":"CH"},"debtor":{"name":"Simon Glarner","address_line_1":"Bächliwis 55","address_line_2":"8184 Bachenbülach","country":"CH"},"format":{"graphics_format":"PDF","output_size":"A4_PORTRAIT_SHEET","language":"DE"}}


Below you see an example of the result using the given example above.

![qr-invoice-e1](https://user-images.githubusercontent.com/1636301/236786580-bc1bee67-af0c-43ef-94ef-fd4c117ddc60.svg)

## Xls
Xls provides commands to create an xls file

List of commands:

### "Xls.activateSheet" 
activates the selected sheet

Mandatory Parameters: Name: "sheet" Values: sheetname (String) or sheetindex (Integer)

Optional Parameters: Name: workbook Value: name of workbook

### "Xls.activateWorkbook"
activates the selected workbook

Mandatory Parameters: Name: "workbook" Values: workbook name

### "Xls.activeSheetPresent"
returns if an active sheet is present

Empty Parameter, returns "present" with value 1 if active sheet is present, else 0

### "Xls.activeWorkbookPresent"
returns if an active workbook is present

Empty Parameter, returns "present" with value 1 if active workbook is present, else 0

### "Xls.copy"
copies cells from source to target

Mandatory Parameters: Name: "source" source cell range, "target" target cell range,
source: cell range coordinates as address (A1:A1) or as integers (top row, left col, bottom row, right col)

### "Xls.createAndActivateSheetByName"
creates and activates the named sheet

Mandatory Parameters: Name: "sheet" Value: arbitrary name

### "Xls.createAndActivateWorkbook"
creates and activates the named workbook

Mandatory Parameters: Name: "workbook" Value: arbitrary name

### "Xls.createSheet"
Mandatory Parameters: Name: "sheet" Value: arbitrary name

### "Xls.createWorkbook"
Mandatory Parameters: Name: "workbook" Value: arbitrary name

### "Xls.getActiveSheetIndex"
returns the index of the active sheet

Optional Parameters: Name: "workbook" Value: the workbook used

### "Xls.getActiveSheetName"
returns the name of the active sheet

Optional Parameters: Name: "workbook" Value: the workbook used

### "Xls.getActiveWorkbookName"
returns the name of the active workbook

### "Xls.getCallableMethods"
returns a list of methods

### "Xls.getSheetNames"
returns list of sheet names

Optional Parameters: Name: "workbook" Value: the workbook used

### "Xls.getWorkbookNames"
returns list of workbook names

### "Xls.releaseWorkbook"
removes named workbook

Mandatory Parameters: Name: "workbook" Value: name of the workbook to remove

### "Xls.releaseWorkbooks"
removes all workbooks

### "Xls.saveAndReleaseWorkbook"
saves and releases named workbook

Mandatory Parameters: Name "workbook" Value: name of the workbook to save and remove

### "Xls.saveWorkbook"
saves named workbook

Mandatory Parameters: Name "workbook" Value: name of the workbook to save

### "Xls.setCells"
set a list of cells with values or formulas

Mandatory Parameters: "cell" as address ("A1" or "A1:B2") or integers (top row, left column, bottom row, right column) meaning the start cell

"values" a list of values or formulas

Optional Parameters: "direction" values "right" (default), "up", "down", "left"

### "Xls.setHeaders"
set headers text to the left, center and right of the sheet

Optional Parameters: "left" with text value, "center" with text value, "right" with text value

### "Xls.setFooters"
set footers text to the left, center and right of the sheet

Optional Parameters: "left" with text value, "center" with text value, "right" with text value

"Xls.setPrintOptions"

### "Xls.applyFontStyle"
set font style

Mandatory Parameters: "0" normal, "1" bold, "2" italic, "3" bold and italic 

### "Xls.applyNumberFormat"
set number format

### "Xls.autoSizeColumns"
autosizes columns

Mandatory Parameters: "range" range of cells or "cell"

### "Xls.rotateCells"
rotates cells

Mandatory Parameters: "range" or "cell", "rotation" in degrees

### "Xls.alignHorizontally"
align to left, center or right

Mandatory Parameters: "left", "center", "right"
