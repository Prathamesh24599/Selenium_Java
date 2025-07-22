package pom.utils;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public class Assertions {

	// verify_element_on_page
	public static void verify_element_on_page(WebDriver driver, By selector, String verified_attribute,
			String page_name) {
		try {
			WebElement element = driver.findElement(selector);
			Assert.assertTrue(element.isDisplayed(),
					String.format("%s should be on the %s page, but it is not.", verified_attribute, page_name));
		} catch (NoSuchElementException e) {
			Assert.fail(String.format("%s should be on the %s page, but it is not.", verified_attribute, page_name));
		}
	}

	// verify_element_not_on_page
	public static void verify_element_not_on_page(WebDriver driver, By selector, String verified_attribute,
			String page_name) {
		try {
			driver.findElement(selector);
			Assert.fail(String.format("%s should not be on the %s page, but it is.", verified_attribute, page_name));
		} catch (NoSuchElementException e) {
			Assert.assertTrue(true);
		}
	}

	// verify_selector_on_page
	public static void verify_selector_on_page(WebDriver driver, By selector, String verified_attribute,
			String page_name, int timeout) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
		try {
			wait.until(ExpectedConditions.presenceOfElementLocated(selector));
		} catch (TimeoutException e) {
			Assert.fail(String.format("Selector %s should be on the %s page, but it is not.", verified_attribute,
					page_name));
		}
	}

	// verify_selector_not_on_page
	public static void verify_selector_not_on_page(WebDriver driver, By selector, String verified_attribute,
			String page_name, int timeout) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
		try {
			wait.until(ExpectedConditions.presenceOfElementLocated(selector));
			Assert.fail(String.format("Selector %s should not be on the %s page, but it is.", verified_attribute,
					page_name));
		} catch (TimeoutException e) {
			Assert.assertTrue(true);
		}
	}

	// verify_selection_status
	public static void verify_selection_status(boolean is_selected, String expected_selection_status,
			String verified_attribute) {
		switch (expected_selection_status.toLowerCase()) {
		case "selected":
			Assert.assertTrue(is_selected, String.format("%s selection status is [%s] but expected [selected]",
					verified_attribute, is_selected));
			break;
		case "not selected":
			Assert.assertFalse(is_selected, String.format("%s selection status is [%s] but expected [not selected]",
					verified_attribute, is_selected));
			break;
		default:
			Assert.fail("Wrong selection status given, expect [selected] or [not selected]");
		}
	}

	// verify_checkbox_status_is_checked
	public static void verify_checkbox_status_is_checked(boolean is_checked, boolean expected_status,
			String checkbox_name) {
		Assert.assertEquals(is_checked, expected_status, String.format("Checkbox [%s] should be [%s] but it's not.",
				checkbox_name, expected_status ? "checked" : "unchecked"));
	}

	// verify_element_in_section
	public static void verify_element_in_section(WebElement section, WebElement element, String verified_attribute,
			String section_name) {
		Assert.assertTrue(section.findElements(By.xpath(".//*")).contains(element),
				String.format("%s should be in the %s section, but it is not.", verified_attribute, section_name));
	}

	// verify_element_not_in_section
	public static void verify_element_not_in_section(WebElement section, WebElement element, String verified_attribute,
			String section_name) {
		Assert.assertFalse(section.findElements(By.xpath(".//*")).contains(element),
				String.format("%s should not be in the %s section, but it is.", verified_attribute, section_name));
	}

	// verify_object_equals
	public static void verify_object_equals(Object expected, Object actual, String verified_attribute) {
		Assert.assertEquals(actual, expected,
				String.format("%s should be \"%s\", but \"%s\"", verified_attribute, expected, actual));
	}

	// verify_object_contains
	public static void verify_object_contains(String expected, String actual, String verified_attribute) {
		Assert.assertTrue(actual.toLowerCase().contains(expected.toLowerCase()),
				String.format("%s should contain \"%s\", but \"%s\"", verified_attribute, expected, actual));
	}

	// verify_object_not_contains
	public static void verify_object_not_contains(String expected, String actual, String verified_attribute) {
		Assert.assertFalse(actual.toLowerCase().contains(expected.toLowerCase()),
				String.format("%s should not contain \"%s\", but \"%s\"", verified_attribute, expected, actual));
	}

	// verify_include
	public static void verify_include(List<?> array, Object object, String verified_attribute) {
		Assert.assertTrue(array.contains(object),
				String.format("%s should include \"%s\" but not", verified_attribute, object));
	}

	// verify_not_include
	public static void verify_not_include(List<?> array, Object object, String verified_attribute) {
		Assert.assertFalse(array.contains(object),
				String.format("%s should not include \"%s\" but does", verified_attribute, object));
	}

	// verify_truthy
	public static void verify_truthy(boolean actual, String verified_attribute) {
		Assert.assertTrue(actual, String.format("%s should be true, but \"%s\"", verified_attribute, actual));
	}

	// verify_falsey
	public static void verify_falsey(boolean actual, String verified_attribute) {
		Assert.assertFalse(actual, String.format("%s should be false, but \"%s\"", verified_attribute, actual));
	}

	// verify_more_than
	public static void verify_more_than(Number expected, Number actual, String verified_attribute) {
		Assert.assertTrue(actual.doubleValue() > expected.doubleValue(),
				String.format("%s value should be more than [%s], but not", verified_attribute, expected));
	}

	// verify_match_array
	public static void verify_match_array(List<?> expected_array, List<?> actual_array, String verified_attribute) {
		Assert.assertEquals(actual_array, expected_array,
				String.format("%s should match [%s] but not", verified_attribute, expected_array));
	}

	// verify_regex
	public static void verify_regex(String expected, String regex, String verified_attribute) {
		Assert.assertTrue(expected.matches(regex),
				String.format("%s should match format \"%s\", but \"%s\"", verified_attribute, regex, expected));
	}
}
