package Excel;
import org.apache.poi.ss.usermodel.*;
import org.testng.annotations.*;
import java.util.Date;
import java.util.List;

import static org.testng.Assert.*;

public class Demo_Methods {

    private static final String TEST_FILE = "./src/test/resources/Test-Data/test-output.xlsx";
    private ExcelUtils excel;

    @BeforeMethod
    public void setUp() {
        excel = new ExcelUtils();
        excel.createWorkbook(TEST_FILE);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        excel.saveAndCloseWorkbook();
//        new File(TEST_FILE).delete(); // optional cleanup
    }

    @Test
    public void testCreateAndWriteData() {
        excel.createSheet("Employees");
        excel.setCellValue("Employees", 0, 0, "Name");
        excel.setCellValue("Employees", 0, 1, "Age");
        excel.setCellValue("Employees", 1, 0, "Alice");
        excel.setCellValueNumeric("Employees", 1, 1, 30);

        assertEquals(excel.getRowData("Employees", 1).get(0), "Alice");
    }

    @Test
    public void testFormattingAndStyles() {
        excel.createSheet("Styles");
        excel.setCellValue("Styles", 0, 0, "Bold Text");

//        excel.setCellStyleBold("Styles", 0, 0);
//        excel.setCellBackgroundColor("Styles", 0, 0, IndexedColors.YELLOW);
//        excel.setCellBackgroundColor("Styles", 0, 0, IndexedColors.LIGHT_GREEN);
//        excel.setCellFontColor("Styles", 0, 0, IndexedColors.RED.getIndex());
        excel.setCellStyle("Styles", 0, 0, IndexedColors.YELLOW, IndexedColors.RED.getIndex(), true);
        
    }

    @Test
    public void testMergeAndAlignment() {
        excel.createSheet("Merged");
        excel.setCellValue("Merged", 0, 0, "Merged Value");
        excel.mergeCells("Merged", 0, 0, 0, 2);
        excel.setCellAlignment("Merged", 0, 0, HorizontalAlignment.CENTER);
        excel.setCellVerticalAlignment("Merged", 0, 0, VerticalAlignment.CENTER);
    }

    @Test
    public void testDateAndBooleanHandling() {
        excel.createSheet("Types");
        Date date = new Date();
        
        excel.setDateCellValue("Types", 0, 0, date);
        excel.setCellValue("Types", 0, 1, "true");

        assertNotNull(excel.getDateCellValue("Types", 0, 0));
        assertEquals(excel.getCellValue(excel.getCell("Types", 0, 1)), "true");
    }

    @Test
    public void testSheetUtilities() {
        excel.createSheet("Data");
        assertTrue(excel.doesSheetExist("Data"));
        excel.hideSheet("Data");
        assertTrue(excel.isSheetHidden("Data"));
        excel.unhideSheet("Data");
        assertFalse(excel.isSheetHidden("Data"));

        List<String> names = excel.getAllSheetNames();
        System.out.println(names.size());
        assertTrue(names.contains("Data"));
    }

    @Test
    public void testCellUtilities() {
        excel.createSheet("Cells");
        excel.setCellValue("Cells", 0, 0, "Hello");

        assertTrue(excel.doesCellExist("Cells", 0, 0));
        assertFalse(excel.isCellEmpty("Cells", 0, 0));
        assertEquals(excel.getNonEmptyCellCount("Cells", 0), 1);
    }

    @Test
    public void testRowAndColumnFunctions() {
        excel.createSheet("Grid");
        for (int i = 0; i < 5; i++) {
            excel.setCellValue("Grid", i, i, "Val" + i);
        }

        assertEquals(excel.getRowCount("Grid"), 5);
        assertEquals(excel.getCellValue(excel.getCell("Grid", 3, 3)), "Val3");
    }

    @Test
    public void testResultFormatting() {
        excel.createSheet("Results");
        excel.writeTestResult("Results", 0, 0, "Passed");
        excel.writeTestResult("Results", 1, 0, "Failed");
    }

    @Test
    public void testAutoSizingAndFreezing() {
        excel.createSheet("Freeze");
        excel.setCellValue("Freeze", 0, 0, "Header");
        excel.autoSizeColumn("Freeze", 0);
        excel.freezeFirstRow("Freeze");
        excel.freezeFirstColumn("Freeze");
    }

    @Test
    public void testHyperlinksAndComments() {
        excel.createSheet("Links");
        excel.setCellValue("Links", 0, 0, "Google");
        excel.addHyperlink("Links", 0, 0, "https://www.google.com");
        excel.addComment("Links", 0, 0, "This is Google");
    }
}
