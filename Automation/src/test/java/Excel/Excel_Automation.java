package Excel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class Excel_Automation {
	@DataProvider(name = "loginData")
    public Object[][] getData() throws Exception {
        return ExcelUtilities.getExcelData("./src/test/resources/Test-Data/Data_File.xlsx", "Sheet1");
    }

	

	@Test
	public void f() throws Exception {
		File file = new File("./src/test/resources/Test-Data/Data_File.xlsx");
		FileInputStream inputStream = new FileInputStream(file);
		Workbook workbook = new XSSFWorkbook(inputStream);
		Sheet sheet1 = workbook.getSheet("Sheet1");
		System.out.println(sheet1.getPhysicalNumberOfRows());
		for (int i = 0; i < sheet1.getPhysicalNumberOfRows(); i++) {
			Row row = sheet1.getRow(i);
			for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
				Cell cell = row.getCell(j);
				switch (cell.getCellType()) {
		        case STRING:
		            System.out.print(cell.getStringCellValue()+ "\t");
		            break;
		        case NUMERIC:
		        	System.out.print(cell.getNumericCellValue()+ "\t");
		        default:
		            break;
				}
			}
			System.out.println();
		}
		 workbook.close();
         inputStream.close();
 	}
	
	@Test(dataProvider = "loginData")
	public void Excel_DataProvider(String sr, String fname, String lname, String gender, String country, String age, String date, String id) {
		System.out.println(sr +"\t"+fname +"\t"+ lname+"\t"+gender+"\t"+ country+"\t"+ age+"\t"+ date+"\t"+id);
		        
	}
	
	
	@Test
	public void CSV_Test() throws Exception, IOException {
		   String filePath = "./src/test/resources/Test-Data/industry.csv";

	       BufferedReader reader = new BufferedReader(new FileReader(filePath));
	       String line;

	       while ((line = reader.readLine()) != null) {
		       String[] values = line.split(",");
		       for (String string : values) {
		    	   System.out.print(string+"\t");
		       }
		       System.out.println();
	       }
	       reader.close();
	 }
	
	
}
