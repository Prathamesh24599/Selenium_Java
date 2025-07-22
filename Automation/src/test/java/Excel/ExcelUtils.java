package Excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.*;
import java.util.*;

/**
 * Utility class for performing various Excel operations using Apache POI.
 * Supports reading, writing, formatting, and data extraction from Excel files.
 */
public class ExcelUtils {

    private Workbook workbook;
    private Sheet sheet;
    private String filePath;

	/**
     * Opens an existing Excel workbook from a file path.
     *
     * @param filePath Path to the Excel file.
     * @throws IOException If file can't be read.
     * @throws InvalidFormatException If the file format is invalid.
     */
    public ExcelUtils openWorkbook(String filePath) throws IOException, InvalidFormatException {
        this.filePath = filePath;
        FileInputStream fis = new FileInputStream(filePath);
        workbook = filePath.endsWith(".xlsx") ? new XSSFWorkbook(fis) : new HSSFWorkbook(fis);
        fis.close();
		return this;
		
    }

    /**
     * Creates a new Excel workbook.
     *
     * @param filePath Path to save the new workbook.
     */
    public void createWorkbook(String filePath) {
        this.filePath = filePath;
        workbook = new XSSFWorkbook();
    }
    
    /**
     * Check if Excel workbook is created or not.
     *
     * @throws IOException If saving fails.
     */
    private void ensureWorkbookInitialized() {
        if (workbook == null) {
            createWorkbook("auto-generated.xlsx");
        }
    }


    /**
     * Saves and closes the workbook.
     *
     * @throws IOException If saving fails.
     */
    public void saveAndCloseWorkbook() throws IOException {
        FileOutputStream fos = new FileOutputStream(filePath);
        workbook.write(fos);
        fos.close();
        workbook.close();
    }

    /**
     * Creates a new sheet with the specified name.
     *
     * @param sheetName Name of the new sheet.
     */
    public void createSheet(String sheetName) {
    	ensureWorkbookInitialized();
    	 if (workbook.getSheet(sheetName) == null) {
    	        workbook.createSheet(sheetName);
    	    } else {
    	        int counter = 1;
    	        String newName;
    	        do {
    	            newName = sheetName + "_" + counter;
    	            counter++;
    	        } while (workbook.getSheet(newName) != null);

    	        workbook.createSheet(newName);
    	       
    	    }
    }

    /**
     * Deletes a sheet by name.
     *
     * @param sheetName Name of the sheet to delete.
     */
    public void deleteSheet(String sheetName) {
        int index = workbook.getSheetIndex(sheetName);
        if (index != -1) workbook.removeSheetAt(index);
    }

    /**
     * Sets the active sheet for operations.
     *
     * @param sheetName Name of the sheet to activate.
     */
    public void setActiveSheet(String sheetName) {
        sheet = getSheet(sheetName);
    }

    /**
     * Gets a sheet by its name.
     *
     * @param sheetName Name of the sheet.
     * @return The Sheet object.
     */
    public Sheet getSheet(String sheetName) {
        return workbook.getSheet(sheetName);
    }

    /**
     * Checks if workbook is initialized.
     *
     * @return true if workbook is open.
     */
    public boolean isWorkbookOpen() {
        return workbook != null;
    }

    /**
     * Gets the sheet names of the current workbook.
     *
     * @return List of sheet names.
     */
    public List<String> getAllSheetNames() {
        List<String> names = new ArrayList<>();
        int count = workbook.getNumberOfSheets();
        for (int i = 0; i < count; i++) names.add(workbook.getSheetName(i));
        return names;
    }


    /**
     * Gets the row count of a sheet.
     *
     * @param sheetName Sheet name.
     * @return Number of physical rows.
     */
    public int getRowCount(String sheetName) {
        return getSheet(sheetName).getPhysicalNumberOfRows();
    }

    /**
     * Gets the column count for a specific row.
     *
     * @param sheetName Sheet name.
     * @param rowIndex Row index.
     * @return Number of physical cells.
     */
    public int getColumnCount(String sheetName, int rowIndex) {
        Row row = getSheet(sheetName).getRow(rowIndex);
        return (row != null) ? row.getPhysicalNumberOfCells() : 0;
    }
    
    /**
     * Gets or creates a sheet with given name
     * 
     * @param sheetName Sheet name.
     * @return Existing or New sheet 
     */
    public Sheet getOrCreateSheet(String SheetName) {
    	Sheet s = workbook.getSheet(SheetName);
    	return s != null ? s : workbook.createSheet(SheetName);
    }
    
    /**
     * Sets a cell's string value.
     *
     * @param sheetName Sheet name.
     * @param row Row index.
     * @param col Column index.
     * @param value Value to set.
     */
    public void setCellValue(String sheetName, int row, int col, String value) {
    	ensureWorkbookInitialized();
    	Sheet sheet = getOrCreateSheet(sheetName);
        Row r = sheet.getRow(row);
        if (r == null) r = getSheet(sheetName).createRow(row);
        Cell c = r.createCell(col);
        if (c == null) c = r.createCell(col);
        c.setCellValue(value);
    }

    /**
     * Sets a cell's numeric value.
     *
     * @param sheetName Sheet name.
     * @param row Row index.
     * @param col Column index.
     * @param value Numeric value to set.
     */
    public void setCellValueNumeric(String sheetName, int row, int col, double value) {
    	ensureWorkbookInitialized();
        Sheet sheet = getOrCreateSheet(sheetName);
        Row r = sheet.getRow(row);
        if (r == null) r = sheet.createRow(row);
        Cell c = r.getCell(col);
        if (c == null) c = r.createCell(col);
        c.setCellValue(value);
    }

    /**
     * Gets a string representation of a cell's value.
     *
     * @param cell Cell to read.
     * @return Cell value as String.
     */
    public String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC:
                return DateUtil.isCellDateFormatted(cell) ? cell.getDateCellValue().toString() : String.valueOf(cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA: return cell.getCellFormula();
            default: return "";
        }
    }
    
    /**
     * Reads Excel data from a given sheet and returns it as a two-dimensional Object array.
     * This method skips the first row (usually considered the header).
     *
     * @param sheetName Name of the sheet to read from.
     * @return 2D Object array containing the sheet data excluding header row.
     * @throws Exception if file reading or parsing fails.
     */
    public Object[][] getExcelData(String sheetName) throws Exception {
    	Sheet sheet = getSheet(sheetName);
    
        int rows = sheet.getPhysicalNumberOfRows();
        int cols = sheet.getRow(0).getPhysicalNumberOfCells();

        Object[][] data = new Object[rows - 1][cols]; // Skip header row

        for (int i = 1; i < rows; i++) {
            Row row = sheet.getRow(i);
            for (int j = 0; j < cols; j++) {
                Cell cell = row.getCell(j);
                data[i - 1][j] = getCellValue(cell);
            }
        }
        return data;
    }


    /**
     * Retrieves all cell values in a row.
     *
     * @param sheetName Sheet name.
     * @param rowIndex Row index.
     * @return List of cell values.
     */
    public List<String> getRowData(String sheetName, int rowIndex) {
        List<String> data = new ArrayList<>();
        Row row = getSheet(sheetName).getRow(rowIndex);
        if (row != null) for (Cell cell : row) data.add(getCellValue(cell));
        return data;
    }

    /**
     * Retrieves all cell values in a column.
     *
     * @param sheetName Sheet name.
     * @param colIndex Column index.
     * @return List of cell values.
     */
    public List<String> getColumnData(String sheetName, int colIndex) {
        List<String> data = new ArrayList<>();
        for (Row row : getSheet(sheetName)) {
            Cell cell = row.getCell(colIndex);
            data.add(getCellValue(cell));
        }
        return data;
    }

    /**
     * Gets numeric cell value as double.
     *
     * @param sheetName Sheet name.
     * @param row Row index.
     * @param col Column index.
     * @return Numeric value.
     */
    public double getNumericCellValue(String sheetName, int row, int col) {
        return getSheet(sheetName).getRow(row).getCell(col).getNumericCellValue();
    }

    /**
     * Gets last row index with data.
     *
     * @param sheetName Sheet name.
     * @return Index of last row.
     */
    public int getLastRowIndex(String sheetName) {
        return getSheet(sheetName).getLastRowNum();
    }
    
 // --- Part 3: Styling, Formatting, Merging, and Advanced Operations ---

    /**
     * Auto-sizes a specific column.
     *
     * @param sheetName Sheet name.
     * @param colIndex  Column index to resize.
     */
    public void autoSizeColumn(String sheetName, int colIndex) {
        getSheet(sheetName).autoSizeColumn(colIndex);
    }

    /**
     * Auto-sizes all columns in the first row.
     *
     * @param sheetName Sheet name.
     */
    public void autoSizeAllColumns(String sheetName) {
        Sheet sheet = getSheet(sheetName);
        Row row = sheet.getRow(0);
        if (row != null) {
            for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
                sheet.autoSizeColumn(i);
            }
        }
    }

    /**
     * Merges cells between given row/column indices, ensuring workbook and sheet exist.
     *
     * @param sheetName Sheet name.
     * @param firstRow  Starting row.
     * @param lastRow   Ending row.
     * @param firstCol  Starting column.
     * @param lastCol   Ending column.
     */
    public void mergeCells(String sheetName, int firstRow, int lastRow, int firstCol, int lastCol) {
        ensureWorkbookInitialized();
        Sheet sheet = getOrCreateSheet(sheetName);
        sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
    }

    /**
     * Checks if a specific cell is empty.
     *
     * @param sheetName Sheet name.
     * @param row       Row index.
     * @param col       Column index.
     * @return True if empty or null, false otherwise.
     */
    public boolean isCellEmpty(String sheetName, int row, int col) {
        Cell cell = getSheet(sheetName).getRow(row).getCell(col);
        return cell == null || getCellValue(cell).trim().isEmpty();
    }

    /**
     * Checks if an entire row is empty.
     *
     * @param sheetName Sheet name.
     * @param row       Row index.
     * @return True if row is empty.
     */
    public boolean isRowEmpty(String sheetName, int row) {
        Row r = getSheet(sheetName).getRow(row);
        if (r == null) return true;
        for (Cell c : r) {
            if (!getCellValue(c).trim().isEmpty()) return false;
        }
        return true;
    }

    /**
     * Finds the row index of a specific text in a column.
     *
     * @param sheetName Sheet name.
     * @param colIndex  Column to search.
     * @param text      Text to find.
     * @return Row index or -1 if not found.
     */
    public int findTextInColumn(String sheetName, int colIndex, String text) {
        Sheet sheet = getSheet(sheetName);
        for (Row row : sheet) {
            Cell cell = row.getCell(colIndex);
            if (cell != null && getCellValue(cell).equalsIgnoreCase(text)) {
                return row.getRowNum();
            }
        }
        return -1;
    }

    /**
     * Sets a cell's text in bold font.
     *
     * @param sheetName Sheet name.
     * @param row       Row index.
     * @param col       Column index.
     */
    public void setCellStyleBold(String sheetName, int row, int col) {
    	ensureWorkbookInitialized();
        Sheet sheet = getOrCreateSheet(sheetName);
        Row r = sheet.getRow(row);
        if (r == null) r = sheet.createRow(row);
        Cell cell = r.getCell(col);
        if (cell == null) cell = r.createCell(col);

        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        cell.setCellStyle(style);
    }

    /**
     * Sets a cell's background color.
     *
     * @param sheetName Sheet name.
     * @param row       Row index.
     * @param col       Column index.
     * @param color     Indexed color.
     */
    public void setCellBackgroundColor(String sheetName, int row, int col, IndexedColors color) {
    	ensureWorkbookInitialized();
        Sheet sheet = getOrCreateSheet(sheetName);
        Row r = sheet.getRow(row);
        if (r == null) r = sheet.createRow(row);
        Cell cell = r.getCell(col);
        if (cell == null) cell = r.createCell(col);

        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(color.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cell.setCellStyle(style);
    }
    
    /**
     * Sets a cell's style including background color, font color, and bold option.
     * This method ensures that multiple style properties (fill + font) are applied 
     * together without overwriting each other — unlike applying styles separately.
     *
     * @param sheetName      Name of the sheet where the cell is located.
     * @param row            Row index (0-based).
     * @param col            Column index (0-based).
     * @param bgColor        Background color from {@link IndexedColors}.
     * @param fontColorIndex Font color index (e.g., {@code IndexedColors.RED.getIndex()}).
     * @param bold           Whether the text should be bold.
     *
     * Example usage:
     * <pre>{@code
     *     excel.setCellValue("Styles", 0, 0, "Styled Text");
     *     excel.setCellStyle("Styles", 0, 0, IndexedColors.LIGHT_GREEN, IndexedColors.RED.getIndex(), true);
     * }</pre>
     */
    public void setCellStyle(String sheetName, int row, int col, IndexedColors bgColor, short fontColorIndex, boolean bold) {
        ensureWorkbookInitialized();
        Sheet sheet = getOrCreateSheet(sheetName);
        Row r = sheet.getRow(row);
        if (r == null) r = sheet.createRow(row);
        Cell cell = r.getCell(col);
        if (cell == null) cell = r.createCell(col);

        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(bgColor.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font font = workbook.createFont();
        font.setColor(fontColorIndex);
        font.setBold(bold);
        style.setFont(font);

        cell.setCellStyle(style);
    }

    /**
     * Writes a test result in a cell with appropriate color formatting.
     *
     * @param sheetName Sheet name.
     * @param row       Row index.
     * @param col       Column index.
     * @param status    Test status (Passed/Failed).
     */
    public void writeTestResult(String sheetName, int row, int col, String status) {
        setCellValue(sheetName, row, col, status);
        if ("Passed".equalsIgnoreCase(status)) {
            setCellBackgroundColor(sheetName, row, col, IndexedColors.LIGHT_GREEN);
        } else {
            setCellBackgroundColor(sheetName, row, col, IndexedColors.RED);
        }
    }

    /**
     * Sets the font color of a specific cell.
     *
     * @param sheetName Sheet name.
     * @param row Row index.
     * @param col Column index.
     * @param colorIndex Font color index.
     */
    public void setCellFontColor(String sheetName, int row, int col, short colorIndex) {
        Cell cell = getSheet(sheetName).getRow(row).getCell(col);
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setColor(colorIndex);
        style.setFont(font);
        cell.setCellStyle(style);
    }

    /**
     * Clears contents of a specific cell.
     *
     * @param sheetName Sheet name.
     * @param row Row index.
     * @param col Column index.
     */
    public void clearCell(String sheetName, int row, int col) {
        Row r = getSheet(sheetName).getRow(row);
        if (r != null) {
            Cell cell = r.getCell(col);
            if (cell != null) r.removeCell(cell);
        }
    }

    /**
     * Clears all data from a sheet.
     *
     * @param sheetName Sheet name.
     */
    public void clearSheet(String sheetName) {
        Sheet sheet = getSheet(sheetName);
        for (Iterator<Row> rowIt = sheet.iterator(); rowIt.hasNext();) {
            Row row = rowIt.next();
            sheet.removeRow(row);
        }
    }

    /**
     * Removes formatting from a specific cell.
     *
     * @param sheetName Sheet name.
     * @param row Row index.
     * @param col Column index.
     */
    public void clearCellStyle(String sheetName, int row, int col) {
        Cell cell = getSheet(sheetName).getRow(row).getCell(col);
        cell.setCellStyle(null);
    }

    /**
     * Copies contents of one cell to another.
     *
     * @param sheetName Sheet name.
     * @param fromRow Source row.
     * @param fromCol Source column.
     * @param toRow Destination row.
     * @param toCol Destination column.
     */
    public void copyCell(String sheetName, int fromRow, int fromCol, int toRow, int toCol) {
        Cell fromCell = getSheet(sheetName).getRow(fromRow).getCell(fromCol);
        Cell toCell = getSheet(sheetName).getRow(toRow).createCell(toCol);
        toCell.setCellValue(getCellValue(fromCell));
    }

    /**
     * Clones a sheet within the workbook.
     *
     * @param sheetName Sheet to clone.
     * @param newSheetName New sheet name.
     */
    public void cloneSheet(String sheetName, String newSheetName) {
        int index = workbook.getSheetIndex(sheetName);
        Sheet cloned = workbook.cloneSheet(index);
        int newIndex = workbook.getSheetIndex(cloned);
        workbook.setSheetName(newIndex, newSheetName);
    }

    /**
     * Returns cell object by coordinates.
     *
     * @param sheetName Sheet name.
     * @param row Row index.
     * @param col Column index.
     * @return Cell object or null.
     */
    public Cell getCell(String sheetName, int row, int col) {
        Row r = getSheet(sheetName).getRow(row);
        return r != null ? r.getCell(col) : null;
    }

    /**
     * Creates a custom CellStyle with specified properties.
     *
     * @param fontSize Font size.
     * @param bold Whether font is bold.
     * @param bgColor Background color.
     * @param alignment Horizontal alignment.
     * @return The custom CellStyle.
     */
    public CellStyle createCustomCellStyle(short fontSize, boolean bold, IndexedColors bgColor, HorizontalAlignment alignment) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints(fontSize);
        font.setBold(bold);
        style.setFont(font);
        style.setFillForegroundColor(bgColor.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(alignment);
        return style;
    }

    /**
     * Applies a custom CellStyle to a specific cell.
     *
     * @param sheetName Sheet name.
     * @param row Row index.
     * @param col Column index.
     * @param style CellStyle to apply.
     */
    public void applyCustomStyleToCell(String sheetName, int row, int col, CellStyle style) {
        Cell cell = getSheet(sheetName).getRow(row).getCell(col);
        cell.setCellStyle(style);
    }
    
    /**
     * Checks if a specific sheet exists in the workbook.
     *
     * @param sheetName Sheet name to check.
     * @return True if sheet exists.
     */
    public boolean doesSheetExist(String sheetName) {
        return workbook.getSheet(sheetName) != null;
    }

    /**
     * Renames a sheet.
     *
     * @param oldName Current sheet name.
     * @param newName New sheet name.
     */
    public void renameSheet(String oldName, String newName) {
        int index = workbook.getSheetIndex(oldName);
        if (index != -1) {
            workbook.setSheetName(index, newName);
        }
    }

    /**
     * Sets column width.
     *
     * @param sheetName Sheet name.
     * @param colIndex Column index.
     * @param width Width in units (1 unit = 1/256th character width).
     */
    public void setColumnWidth(String sheetName, int colIndex, int width) {
        getSheet(sheetName).setColumnWidth(colIndex, width);
    }

    /**
     * Sets row height.
     *
     * @param sheetName Sheet name.
     * @param rowIndex Row index.
     * @param height Height in points.
     */
    public void setRowHeight(String sheetName, int rowIndex, short height) {
        getSheet(sheetName).getRow(rowIndex).setHeight(height);
    }

    /**
     * Adds a comment to a cell.
     *
     * @param sheetName Sheet name.
     * @param row Row index.
     * @param col Column index.
     * @param commentText Text of the comment.
     */
    public void addComment(String sheetName, int row, int col, String commentText) {
        Sheet sheet = getSheet(sheetName);
        Drawing<?> drawing = sheet.createDrawingPatriarch();
        CreationHelper factory = workbook.getCreationHelper();
        ClientAnchor anchor = factory.createClientAnchor();
        anchor.setCol1(col);
        anchor.setRow1(row);
        Comment comment = drawing.createCellComment(anchor);
        comment.setString(factory.createRichTextString(commentText));
        sheet.getRow(row).getCell(col).setCellComment(comment);
    }

    /**
     * Gets the number of merged regions in a sheet.
     *
     * @param sheetName Sheet name.
     * @return Number of merged regions.
     */
    public int getMergedRegionCount(String sheetName) {
        return getSheet(sheetName).getNumMergedRegions();
    }

    /**
     * Freezes the first row in a sheet.
     *
     * @param sheetName Sheet name.
     */
    public void freezeFirstRow(String sheetName) {
        getSheet(sheetName).createFreezePane(0, 1);
    }

    /**
     * Freezes the first column in a sheet.
     *
     * @param sheetName Sheet name.
     */
    public void freezeFirstColumn(String sheetName) {
        getSheet(sheetName).createFreezePane(1, 0);
    }

    /**
     * Freezes both first row and first column.
     *
     * @param sheetName Sheet name.
     */
    public void freezeFirstRowAndColumn(String sheetName) {
        getSheet(sheetName).createFreezePane(1, 1);
    }

    /**
     * Unfreezes panes in a sheet.
     *
     * @param sheetName Sheet name.
     */
    public void unfreezePane(String sheetName) {
        getSheet(sheetName).createFreezePane(0, 0);
    }

    /**
     * Protects a sheet with a password.
     *
     * @param sheetName Sheet name.
     * @param password Password to protect.
     */
    public void protectSheet(String sheetName, String password) {
        getSheet(sheetName).protectSheet(password);
    }

    /**
     * Checks if a cell is formula-based.
     *
     * @param cell Cell to check.
     * @return True if formula.
     */
    public boolean isFormulaCell(Cell cell) {
        return cell.getCellType() == CellType.FORMULA;
    }

    /**
     * Gets formula string from a formula cell.
     *
     * @param cell Cell containing formula.
     * @return Formula string.
     */
    public String getCellFormula(Cell cell) {
        return cell.getCellFormula();
    }

    /**
     * Evaluates a formula cell and returns result as string.
     *
     * @param cell Formula cell.
     * @return Evaluated result.
     */
    public String evaluateFormula(Cell cell) {
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        CellValue value = evaluator.evaluate(cell);
        switch (value.getCellType()) {
            case STRING: return value.getStringValue();
            case NUMERIC: return String.valueOf(value.getNumberValue());
            case BOOLEAN: return String.valueOf(value.getBooleanValue());
            default: return "";
        }
    }

    /**
     * Checks if a sheet is hidden.
     *
     * @param sheetName Sheet name.
     * @return True if hidden.
     */
    public boolean isSheetHidden(String sheetName) {
        int index = workbook.getSheetIndex(sheetName);
        return workbook.isSheetHidden(index);
    }

    /**
     * Hides a sheet.
     *
     * @param sheetName Sheet name.
     */
    public void hideSheet(String sheetName) {
        int index = workbook.getSheetIndex(sheetName);
        workbook.setSheetHidden(index, true);
    }

    /**
     * Unhides a hidden sheet.
     *
     * @param sheetName Sheet name.
     */
    public void unhideSheet(String sheetName) {
        int index = workbook.getSheetIndex(sheetName);
        workbook.setSheetHidden(index, false);
    }

    /**
     * Applies border to a cell.
     *
     * @param sheetName Sheet name.
     * @param row Row index.
     * @param col Column index.
     */
    public void setCellBorder(String sheetName, int row, int col) {
        Cell cell = getSheet(sheetName).getRow(row).getCell(col);
        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        cell.setCellStyle(style);
    }

    /**
     * Wraps text in a cell.
     *
     * @param sheetName Sheet name.
     * @param row Row index.
     * @param col Column index.
     */
    public void setCellTextWrap(String sheetName, int row, int col) {
        Cell cell = getSheet(sheetName).getRow(row).getCell(col);
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        cell.setCellStyle(style);
    }

    /**
     * Aligns text in a cell horizontally.
     *
     * @param sheetName Sheet name.
     * @param row Row index.
     * @param col Column index.
     * @param alignment HorizontalAlignment enum value.
     */
    public void setCellAlignment(String sheetName, int row, int col, HorizontalAlignment alignment) {
        Cell cell = getSheet(sheetName).getRow(row).getCell(col);
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(alignment);
        cell.setCellStyle(style);
    }
    
    /**
     * Sets vertical alignment in a cell.
     *
     * @param sheetName Sheet name.
     * @param row Row index.
     * @param col Column index.
     * @param alignment VerticalAlignment enum.
     */
    public void setCellVerticalAlignment(String sheetName, int row, int col, VerticalAlignment alignment) {
        Cell cell = getSheet(sheetName).getRow(row).getCell(col);
        CellStyle style = workbook.createCellStyle();
        style.setVerticalAlignment(alignment);
        cell.setCellStyle(style);
    }

    /**
     * Adds a hyperlink to a cell.
     *
     * @param sheetName Sheet name.
     * @param row Row index.
     * @param col Column index.
     * @param link URL or hyperlink target.
     */
    public void addHyperlink(String sheetName, int row, int col, String link) {
        CreationHelper createHelper = workbook.getCreationHelper();
        Hyperlink hyperlink = createHelper.createHyperlink(HyperlinkType.URL);
        hyperlink.setAddress(link);
        Cell cell = getSheet(sheetName).getRow(row).getCell(col);
        cell.setHyperlink(hyperlink);
    }

    /**
     * Applies a custom font to a cell.
     *
     * @param sheetName Sheet name.
     * @param row Row index.
     * @param col Column index.
     * @param fontName Font name.
     * @param fontSize Font size.
     */
    public void setCustomFont(String sheetName, int row, int col, String fontName, short fontSize) {
        Cell cell = getSheet(sheetName).getRow(row).getCell(col);
        Font font = workbook.createFont();
        font.setFontName(fontName);
        font.setFontHeightInPoints(fontSize);
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        cell.setCellStyle(style);
    }

    /**
     * Locks a specific cell for sheet protection.
     *
     * @param sheetName Sheet name.
     * @param row Row index.
     * @param col Column index.
     */
    public void lockCell(String sheetName, int row, int col) {
        Cell cell = getSheet(sheetName).getRow(row).getCell(col);
        CellStyle style = workbook.createCellStyle();
        style.setLocked(true);
        cell.setCellStyle(style);
    }

    /**
     * Unlocks a specific cell for sheet protection.
     *
     * @param sheetName Sheet name.
     * @param row Row index.
     * @param col Column index.
     */
    public void unlockCell(String sheetName, int row, int col) {
        Cell cell = getSheet(sheetName).getRow(row).getCell(col);
        CellStyle style = workbook.createCellStyle();
        style.setLocked(false);
        cell.setCellStyle(style);
    }

    /**
     * Counts how many cells in a row are non-empty.
     *
     * @param sheetName Sheet name.
     * @param row Row index.
     * @return Number of non-empty cells.
     */
    public int getNonEmptyCellCount(String sheetName, int row) {
        Row r = getSheet(sheetName).getRow(row);
        int count = 0;
        if (r != null) {
            for (Cell c : r) {
                if (!getCellValue(c).isEmpty()) count++;
            }
        }
        return count;
    }

    /**
     * Highlights a row with a given background color.
     *
     * @param sheetName Sheet name.
     * @param row Row index.
     * @param color Background color.
     */
    public void highlightRow(String sheetName, int row, IndexedColors color) {
        Row r = getSheet(sheetName).getRow(row);
        if (r != null) {
            for (Cell c : r) {
                CellStyle style = workbook.createCellStyle();
                style.setFillForegroundColor(color.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                c.setCellStyle(style);
            }
        }
    }

    /**
     * Highlights a column with a given background color.
     *
     * @param sheetName Sheet name.
     * @param col Column index.
     * @param color Background color.
     */
    public void highlightColumn(String sheetName, int col, IndexedColors color) {
        Sheet s = getSheet(sheetName);
        for (Row r : s) {
            Cell c = r.getCell(col);
            if (c != null) {
                CellStyle style = workbook.createCellStyle();
                style.setFillForegroundColor(color.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                c.setCellStyle(style);
            }
        }
    }

    /**
     * Removes all merged regions from a sheet.
     *
     * @param sheetName Sheet name.
     */
    public void removeAllMergedRegions(String sheetName) {
        Sheet s = getSheet(sheetName);
        for (int i = s.getNumMergedRegions() - 1; i >= 0; i--) {
            s.removeMergedRegion(i);
        }
    }

    /**
     * Checks if a sheet is empty.
     *
     * @param sheetName Sheet name.
     * @return True if no rows exist.
     */
    public boolean isSheetEmpty(String sheetName) {
        Sheet s = getSheet(sheetName);
        return s.getPhysicalNumberOfRows() == 0;
    }

    /**
     * Converts a numeric column index to Excel column name.
     *
     * @param index Column index (0-based).
     * @return Column name like A, B, Z, AA, AB...
     */
    public String getExcelColumnName(int index) {
        StringBuilder sb = new StringBuilder();
        while (index >= 0) {
            sb.insert(0, (char) ('A' + index % 26));
            index = index / 26 - 1;
        }
        return sb.toString();
    }

    /**
     * Converts Excel column name to index.
     *
     * @param name Excel column name like A, B, Z, AA.
     * @return 0-based column index.
     */
    public int getExcelColumnIndex(String name) {
        int result = 0;
        for (int i = 0; i < name.length(); i++) {
            result *= 26;
            result += name.charAt(i) - 'A' + 1;
        }
        return result - 1;
    }

    /**
     * Checks if a cell exists.
     *
     * @param sheetName Sheet name.
     * @param row Row index.
     * @param col Column index.
     * @return True if cell is present.
     */
    public boolean doesCellExist(String sheetName, int row, int col) {
        Row r = getSheet(sheetName).getRow(row);
        return r != null && r.getCell(col) != null;
    }

    /**
     * Reads a boolean value from a cell.
     *
     * @param sheetName Sheet name.
     * @param row Row index.
     * @param col Column index.
     * @return Boolean value.
     */
    public boolean getBooleanCellValue(String sheetName, int row, int col) {
        return getSheet(sheetName).getRow(row).getCell(col).getBooleanCellValue();
    }

    /**
     * Reads a date value from a cell.
     *
     * @param sheetName Sheet name.
     * @param row Row index.
     * @param col Column index.
     * @return Date object.
     */
    public Date getDateCellValue(String sheetName, int row, int col) {
        return getSheet(sheetName).getRow(row).getCell(col).getDateCellValue();
    }

    /**
     * Sets a date in a cell.
     *
     * @param sheetName Sheet name.
     * @param row Row index.
     * @param col Column index.
     * @param date Date value.
     */
    public void setDateCellValue(String sheetName, int row, int col, Date date) {
        Row r = getSheet(sheetName).getRow(row);
        if (r == null) r = getSheet(sheetName).createRow(row);
        Cell c = r.createCell(col);
        c.setCellValue(date);
    }

    /**
     * Returns total number of cells in a sheet (excluding empty rows).
     *
     * @param sheetName Sheet name.
     * @return Cell count.
     */
    public int getTotalCellCount(String sheetName) {
        Sheet sheet = getSheet(sheetName);
        int count = 0;
        for (Row row : sheet) {
            count += row.getPhysicalNumberOfCells();
        }
        return count;
    }

    /**
     * Returns all cell addresses in a row.
     *
     * @param sheetName Sheet name.
     * @param row Row index.
     * @return List of addresses like A1, B1, C1...
     */
    public List<String> getRowAddresses(String sheetName, int row) {
        List<String> addresses = new ArrayList<>();
        Row r = getSheet(sheetName).getRow(row);
        if (r != null) {
            for (Cell c : r) {
                addresses.add(c.getAddress().formatAsString());
            }
        }
        return addresses;
   
    }


}