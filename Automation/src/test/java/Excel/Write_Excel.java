package Excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.Test;

public class Write_Excel {
	@Test
	public void f() throws Exception {
		FileOutputStream file = new FileOutputStream("./src/test/resources/Test-Data/Lang.xlsx");
		Workbook workbook = new XSSFWorkbook();
		Sheet mySheet = workbook.createSheet("Sheet1");
		
		Row row1 = mySheet.createRow(0);
		row1.createCell(0).setCellValue("Sr.No");
		row1.createCell(1).setCellValue("Language");
		row1.createCell(2).setCellValue("Framework");
		
		Row row2 = mySheet.createRow(1);
		row2.createCell(0).setCellValue("1");
		row2.createCell(1).setCellValue("Java");
		row2.createCell(2).setCellValue("TestNG");
		
		Row row3 = mySheet.createRow(2);
		row3.createCell(0).setCellValue("2");
		row3.createCell(1).setCellValue("Python");
		row3.createCell(2).setCellValue("PyTest");
		
		Row row4 = mySheet.createRow(3);
		row4.createCell(0).setCellValue("3");
		row4.createCell(1).setCellValue("C#");
		row4.createCell(2).setCellValue("NUnit");
		
		workbook.write(file);
		workbook.close();
		file.close();
		
	}
	

	@Test
	public void CSV_Writing() throws Exception {
		String filePath = "./src/test/resources/Test-Data/write.csv";
		
		String[][] data = {
	            {"ID", "FirstName", "LastName", "Country", "Salary"},
	            {"1", "Dulce", "Abril", "United States", "1562"},
	            {"2", "Francesca", "Beaudreau", "France", "5412"},
	            {"3", "Haley", "Mcgee", "United Kingdom", "9021"}
	        };
		
		FileWriter writer = new FileWriter(new File(filePath));
		for (String[] row : data) {
			writer.append(String.join(",", row));
			writer.append("\n");
		}
		
		writer.close();
	}
}
