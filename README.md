# Due to a problem the plugin can not be created. As soon as possible it will be done.
# filemaker.plugin
This plugin provides the following features
# Fsl
Fsl is the main entry for using the functions.

Use: Fsl.execute("Xyz.command", "{}")

- The first parameter consists of two parts that are separated by a dot. The first part is the name of the module, in which the method resides, the second one is the name of the method.

- The second parameter has to be a valid json string containing the mandatory and optional parameters for the method as described below. 

- Every call returns a jsonstring with the attributes 'result' ('OK' if successfully, else 'Fehler'. Some functions return values. They are described in the respective command. If it returns 'Fehler' then another attribute 'errors' contains a list of error messages describing the errors that occured.

Actually there are the following modules:

## QRBill
This module uses the https://github.com/manuelbl/SwissQRBill library.

*QRBill* has one command 'generate':

As parameter you have to use a json string with the following attributes:

```
{
  "amount" : 287.3,
  "currency" : "CHF",
  "iban" : "CH4431999123000889012",
  "reference" : "000000000000000000000000000",
  "message" : "Rechnungsnr. 10978 / Auftragsnr. 3987",
  "creditor" : {
    "name" : "Schreinerei Habegger & Söhne",
    "address_line_1" : "Uetlibergstrasse 138",
    "address_line_2" : "8045 Zürich",
    "country" : "CH"
  },
  "debtor" : {
    "name" : "Simon Glarner",
    "address_line_1" : "Bächliwis 55",
    "address_line_2" : "8184 Bachenbülach",
    "country" : "CH"
  },
  "format" : {
    "graphics_format" : "PDF",
    "output_size" : "A4_PORTRAIT_SHEET",
    "language" : "DE"
  }
}
```
or in a FileMaker script:

```
JSONSetElement ( $request ; 
    [ "amount" ; 287.3 ; JSONNumber ] ;
    [ "currency" ; "CHF" ; JSONString ] ;
    [ "iban" ; "CH4431999123000889012" ; JSONString ] ;
    [ "reference" ; "000000000000000000000000000" ; JSONString] ;
    [ "message" ; "Rechnungsnr. 10978 / Auftragsnr. 3987" ; JSONString ] ;
    [ "creditor.name" ; "Schreinerei Habegger & Söhne" ; JSONString ] ;
    [ "creditor.address_line_1" ; "Uetlibergstrasse 138" ; JSONString ] ;
    [ "creditor.address_line_2" ; "8045 Zürich" ; JSONString ] ;
    [ "creditor.country" ; "CH" ; JSONString ] ;
    [ "debtor.name" ; "Simon Glarner" ; JSONString ] ;
    [ "debtor.address_line_1" ; "Bächliwis 55" ; JSONString ] ;
    [ "debtor.address_line_2" ; "8184 Bachenbülach" ; JSONString ] ;
    [ "debtor.country" ; "CH" ; JSONString ] ;
    [ "format.graphics_format" ; "PDF" ; JSONString ] ;
    [ "format.output_size" ; "QR_BILL_EXTRA_SPACE" ; JSONString ] ;
    [ "format.language" ; "DE" ; JSONString ]
)
```

Mandatory attributes are: 'amount' (number), 'currency' (String), 'iban' (a valid qriban), 'creditor' with all its parts (name: the name of the creditor; address_line_1: street and streetnumber; address_line_2: zip and city; country: the iso3166 alpha-2 country code, defaults to 'CH')

Some attributes are restricted to some values:

- currency: CHF EUR
- country: CH
- graphics_format: PDF SVG PNG
- output_size: A4_PORTRAIT_SHEET QR_BILL_ONLY QR_CODE_ONLY QR_BILL_EXTRA_SPACE
- language: DE FR IT EN

Below you see an example of the result using the given example above.

![QRBill](https://github.com/ceugster/filemaker.plugin/assets/1636301/93f77dd1-ab64-464d-b126-54268ae79522)

## Xls
Xls provides commands to create an xls file

List of commands:

### "Xls.activateSheet" 
activates the selected sheet

Mandatory Parameters: Name: "sheet" Values: sheetname (String) or sheetindex (int)

Optional Parameters: Name: workbook Value: name of workbook

returns 'index' Index of sheet, 'sheet' name of Sheet, 'workbook' name of workbook, the sheet belongs to

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
