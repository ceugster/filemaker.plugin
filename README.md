# filemaker.plugin
This plugin provides the following features
## Fsl
Fsl is the main entry for using the functions.

Use: Fsl.execute("Xyz.command", "{}")

Actually there are the following modules:

### QRBill
*QRBill* has one command 'generate':

As parameter you have to use a json string with following structure:

{"amount":287.30,"currency":"CHF","iban":"CH450023023099999999A","reference":"RF102320QF02T323eUI234","message":"Rechnungsnr. 10978 / Auftragsnr. 3987","creditor":{"name":"Schreinerei Habegger & Söhne","address_line_1":"Uetlibergstrasse 138","address_line_2":"8045 Zürich","country":"CH"},"debtor":{"name":"Simon Glarner","address_line_1":"Bächliwis 55","address_line_2":"8184 Bachenbülach","country":"CH"},"format":{"graphics_format":"PDF","output_size":"A4_PORTRAIT_SHEET","language":"DE"}}

### Xls
*Xls* provides commands to create an xls file

List of commands:

"Xls.activateSheet"

"Xls.activateWorkbook"

"Xls.activeSheetPresent"

"Xls.activeWorkbookPresent"

"Xls.copy"

"Xls.createAndActivateSheetByName"

"Xls.createAndActivateWorkbook"

"Xls.createSheet"

"Xls.createWorkbook"

"Xls.getActiveSheetIndex"

"Xls.getActiveSheetName"

"Xls.getActiveWorkbookName"

"Xls.getCallableMethods"

"Xls.getSheetNames"

"Xls.getWorkbookNames"

"Xls.releaseWorkbook"

"Xls.releaseWorkbooks"

"Xls.saveAndReleaseWorkbook"

"Xls.saveWorkbook"

"Xls.setCells"

"Xls.setCellValue"

"Xls.setHeaders"

"Xls.setFooters"

"Xls.setPrintOptions"

"Xls.applyFontStyle"

"Xls.applyNumberFormat"

"Xls.autoSizeColumns"

"Xls.rotateCells"

"Xls.alignHorizontally"
![qr-invoice-e1](https://user-images.githubusercontent.com/1636301/236786580-bc1bee67-af0c-43ef-94ef-fd4c117ddc60.svg)
