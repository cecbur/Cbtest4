/**
 * 
 */
package se.cecbur.pensionsmyndighetense;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import se.cecbur.pensionsmyndighetense.pages.Page;
import se.cecbur.pensionsmyndighetense.pages.PageVerify;

/**
 * @author Cecilia
 *
 */
public class Tools {
	// Create a logger for this class
	private final static Logger logger = LogManager.getLogger(Tools.class);

	/**
	 * 
	 */
	public Tools() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Find a link in the current page and verify that it leads to the correct page
	 *
	 * @param  driver           web driver to use for the testing
	 * @param  pageTitle        The title of the page that the link should lead to
	 * @param  linkCssSelector  string used by Selenium cssSelector to identify the link
	 * 
	 */
	public void testLink(WebDriver driver, String pageKey, String linkCssSelector) {
		Page page=PageVerify.getPage(pageKey);
		logger.debug("Verifying link to '" + page.getTitle()+"'");
		// Find the link
		WebElement element = driver.findElement(By.cssSelector(linkCssSelector));
		Assert.assertNotNull("Could not find a link with cssSelector='"
				+linkCssSelector
				+"'. The link should have led to a page titled '"+page.getTitle()
				+"'",
				element);
		// Follow link
		String tmpVal=element.getText();
		logger.trace("Clicking link "
				+((tmpVal==null || Tools.strClean(tmpVal).isEmpty()) 
						? "" 
						: "'"+tmpVal+"'"));
		element.click();
		// Wait for the page to load
		try {
			waitLoad(driver, page);
		} catch (Exception e) {
			// Do nothing. Assert below will do the job	
			e.printStackTrace();
		}
		// Verify that we are on the right page
		Assert.assertTrue(
				"Link led to the wrong page. \n"
				+"(Link with cssSelector '"+linkCssSelector
				+"' should lead to a page with title '" + page.getTitle() +"') \n"
				+"   Title: ", 
				page.isThisPage(driver));
	}

	
	/**
	 * Wait for a page to load
	 *
	 * @param  driver                   Selenium web driver that is opening the page
	 * @param  titleStart               The beginning of the title of the opening web page
	 * 
	 */
	public void waitLoad(final WebDriver driver, final Page page) {
		// Wait for the page to load, timeout after 10 seconds
		logger.debug("Waiting for a page with title '" +page.getTitle()+"'");
		try {
			(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver d) {
					return page.isThisPage(d);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			String tmpVal="";
			try {
				tmpVal=driver.getTitle();
				tmpVal="The title of the current page is '"+tmpVal+"' ('"
						+strClean(tmpVal)+"') \n ";
			} catch (Exception e1) {
				tmpVal="";
			}
			Assert.assertTrue(
					"Timeout failed for a page to open with title '"
					+page.getTitle()+"'. \n "
					+tmpVal, 
					page.isThisPage(driver));
		}
		logger.debug("Page title is now: '" + driver.getTitle()+"'");
	}
	
	/**
	 * Verify the implicit time that the driver will wait for a WebElement to appear
	 * unless it has been previously verified
	 * 
	 * @param  driver        web driver to change setting for
	 * @param  implicitWait  new implicit wait time in seconds. 
	 *                       If left out the time will be set to the default time
	 * @return               the new temporary implicit wait time in seconds
	 */
	public long setTmpImplicitWait(WebDriver driver){
		return setTmpImplicitWait(driver, Settings.settings.getimplicitWait());
	}
	public long setTmpImplicitWait(WebDriver driver, long implicitWait){
		// Set the implicit time
		driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
		if (implicitWait==Settings.settings.getimplicitWait()) {
			logger.debug("ImplicitWait now has the default value of " + implicitWait+" seconds");
		} else {
			logger.debug("ImplicitWait is now temporarily set to " + implicitWait+" seconds");
		}
		return implicitWait;
	}


	/**
	 * Clean a string from unusual characters
	 *  - Converts string to lower case
	 *  - Removes all characters except English alphabet, numbers and spaces
	 *  - Replaces all sequences of spaces with one space
	 *  - Removes leading and trailing spaces
	 *
	 * @param  stringToClean   string to clean
	 * @return                 Cleaned string
	 */
	public static String strClean(String stringToClean){
		logger.trace("String '" + stringToClean+ "cleaned");
		return stringToClean.
				toLowerCase().
				replaceAll("[^a-zA-Z0-9 ]","").
				replaceAll("  *", " ").
				trim();
	}
}
