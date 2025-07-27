package pom.test;

import org.testng.annotations.Test;

import pom.data.DataProviders;
import pom.pages.BaseTestSuite;
import pom.pages1.Home_Page1;
import pom.pages1.Login_Page1;

public class DemoTest extends BaseTestSuite{
	@Test(dataProvider = "validLoginData", dataProviderClass = DataProviders.class,
			priority = 1, 
			groups = { "smoke", "regression","positive" }, 
			description = "Test successful login with valid credentials")
	public void testSuccessfulLogin(String username, String password) {
//		logger.info("Testing successful login with username: {}", username);

		// Test execution
		Login_Page1 loginPage = new Login_Page1(getDriver());
	Home_Page1 homePage = loginPage.performValidLogin(username, password);
	
		// Assertions
		homePage.verifySuccessMessage();

	}
	
	@Test
	public void Demo1() throws Exception {
//		 final String WEB_CONFIG_PATH = "src/test/resources/config/web.json";
//		 ObjectMapper mapper = new ObjectMapper();
//		 JsonNode jnode = mapper.readTree(new File(WEB_CONFIG_PATH));
//		 System.out.println(jnode.toPrettyString());
		
	}
}
