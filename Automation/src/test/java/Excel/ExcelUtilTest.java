package Excel;

import java.io.IOException;
import java.util.List;

import org.testng.annotations.Test;

public class ExcelUtilTest {
	@Test
	public void f() throws Exception, IOException {
		ExcelUtils excel = new ExcelUtils();
		List<String> row = excel.openWorkbook("./src/test/resources/Test-Data/Data_File.xlsx")
						    	.getRowData("Sheet1", 0);
		for (String string : row) {
			System.out.print(string +"\t");
		}
	}
	
	
	@Test
	public void test1() throws Exception {
		ExcelUtils excel = new ExcelUtils();
		Object[][] data = excel.openWorkbook("./src/test/resources/Test-Data/Data_File.xlsx")
				.getExcelData("Sheet1");
		for (Object[] objects : data) {
			for (Object objects2 : objects) {
				System.out.print(objects2+"\t");
			}
			System.out.println();
		}
	}
	
}
