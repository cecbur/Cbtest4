/**
 * 
 */
package se.cecbur.pensionsmyndighetense.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import se.cecbur.pensionsmyndighetense.Settings;
import se.cecbur.pensionsmyndighetense.Tools;
import se.cecbur.pensionsmyndighetense.WebDriverSettings;

/**
 * @author Cecilia
 *
 */
public class Index implements Page {
	// Create a logger for this class
	private final static Logger logger = LogManager.getLogger(Index.class);
	
	// Create Tools object
	Tools tools =new Tools();
	
	// Create an object for testing popups in the index page
	IndexPopups indexPopups = new IndexPopups();
	
	// Get Title and URL for this page from settings 
	public static final String title=Settings.settings.getIndexTitle(); 	// Title is "Pensionsmyndigheten";
	private static final String url=Settings.settings.getIndexURL(); 	// URL is https://www.pensionsmyndigheten.se/;

	/**
	 * A class for testing the main page of pensionsmyndighetens web site
	 */
	public Index() {
	}	

	/**
	 * main method used for testing all aspects of the main page of pensionsmyndighetens web site
	 * TODO: Only testing of links currently implemented
	 */
	public void test() {
		// Get a web driver
		WebDriver driver = WebDriverSettings.getDriver();

		// And now use this to visit pensionsmyndigheten.se
		driver.get(url);
		// Alternatively the same thing can be done like this
		// driver.navigate().to("http://www.google.com");

		// Wait for the page to load
		logger.trace("Current page title is: " + driver.getTitle());
		tools.waitLoad(driver, this);
		logger.info("Testing page "+this.getClass().getSimpleName()+" with title: " + driver.getTitle());

		// Check the title of the page
		Assert.assertTrue("Unable to find the "+this.getClass().getSimpleName()+" page to test: ", 
				isThisPage(driver));

		// Test all links
		testLinks(driver);
	}	

	/**
	 * tests all links in the main page of pensionsmyndighetens web site
	 *
	 * @param  driver   web driver to use for the testing
	 */
	private void testLinks(WebDriver driver) {
		logger.info("Testing all links in " + Index.class.getSimpleName());
		// Verify that we are on the right page
		Assert.assertEquals("Wrong title on page: ", title, driver.getTitle());
		// Test popups in page (Popups are also tested by method testLink() each time we return to this page)
		indexPopups.test(driver);

		logger.debug("Testing the link to the page 'Lättläst' in the header menu in page " + Index.class.getSimpleName());
		try {
			testLink(driver, "a.pm_header__nav-link[href='/lattlast']",
					"Lättläst | Pensionsmyndigheten"); 	// TODO: Key should be class name (not title)
		} catch (Exception e) {
			logger.error("The link to the page 'Lättläst' in the header menu in page " 
					+ Index.class.getSimpleName()+ " could not be tested");
			e.printStackTrace();
			returnToIndexByLogo(driver, tools);
		}
		logger.debug("Testing the link to the page 'Teckenspråk' in the header menu in page " + Index.class.getSimpleName());
		try {
			testLink(driver, "a.pm_header__nav-link[href='/tecken']", 
					"Teckenspråk | Pensionsmyndigheten"); 	// TODO: Key should be class name (not title)
		} catch (Exception e) {
			logger.error("The link to the page 'Teckenspråk' in the header menu in page " 
					+ Index.class.getSimpleName()+ " could not be tested");
			e.printStackTrace();
			returnToIndexByLogo(driver, tools); 
		}
		logger.debug("Testing the link to the page 'Other languages' in the header menu in page " + Index.class.getSimpleName());
		try {
			testLink(driver, "a.pm_header__nav-link[href='/other-languages']", 
					"Other languages | Pensionsmyndigheten"); 	// TODO: Key should be class name (not title)
		} catch (Exception e) {
			logger.error("The link to the page 'Other languages' in the header menu in page " 
					+ Index.class.getSimpleName()+ " could not be tested");
			e.printStackTrace();
			returnToIndexByLogo(driver, tools);
		}
		logger.debug("Testing the link to the page 'Mina sidor' in the header menu in page " + Index.class.getSimpleName());
		try {
			testLink(driver, "a.pm_header__nav-link[href='/service/overview/']", 
					"Inloggning till Pensionsmyndigheten"); 	// TODO: Key should be class name (not title)
		} catch (Exception e) {
			logger.error("The link to the page 'Mina sidor' in the header menu in page " 
					+ Index.class.getSimpleName()+ " could not be tested");
			e.printStackTrace();
			returnToIndexByLogo(driver, tools);
		}
		// Open the menu and test the link to 'Förstå din pension'. The first target group link in the menu  
		logger.debug("Testing the link to the page 'Förstå din pension'. The first target group link in the menu in page " + Index.class.getSimpleName());
		try {
			openMenu(driver);
			testLink(driver, "a#pm-menu-first-targetgroup[href='/forsta-din-pension']", 
					"Förstå din pension | Pensionsmyndigheten"); 	// TODO: Key should be class name (not title)
		} catch (Exception e) {
			logger.error("The link to the page 'Förstå din pension' in the first target group in the menu in page " 
					+ Index.class.getSimpleName()+ " could not be tested");
			e.printStackTrace();
			returnToIndexByLogo(driver, tools);
		}
		logger.debug("Testing link 1 'Så fungerar pensionen' in sub menu 1 in the menu in page " + Index.class.getSimpleName());
		try {
			// Open the menu and then the first sub menu
			openMenu(driver, "pm-submenu-1-item-0");
			// test the first link in the first sub menu 'Så fungerar pensionen'.
			testLink(driver, "a.pm_menu__sub-list-item-link.pm_js-menu__link[href='/forsta-din-pension/sa-fungerar-pensionen']", 
					"Förstå din pension | Pensionsmyndigheten"
					+"Så fungerar pensionen"); 	// TODO: Key should be class name (not title)
		} catch (Exception e) {
			logger.error("Could not test the first menu choice 'Så fungerar pensionen' in the first submenu on '"
					+Index.class.getSimpleName()+"'. It should have linked to a specific subsection on page 'Förstå din pension'.");
			e.printStackTrace();
			returnToIndexByLogo(driver, tools);
		}
		logger.debug("Testing link 2 'Vad påverkar din pension?' in sub menu 1 in the menu in page " + Index.class.getSimpleName());
		try {
			// Open the menu and then the first sub menu
			openMenu(driver, "pm-submenu-1-item-0");
			// test the second link in the first sub menu 'Vad påverkar din pension?'.
			testLink(driver, "a.pm_menu__sub-list-item-link.pm_js-menu__link[href='/forsta-din-pension/vad-paverkar-din-pension']", 
					"Förstå din pension | Pensionsmyndigheten"
					+"Vad påverkar din pension?"); 	// TODO: Key should be class name (not title)
		} catch (Exception e) {
			logger.error("Could not test the second menu choice 'Förstå din pension' in the first submenu on '"
					+Index.class.getSimpleName()+"'. It should have linked to a specific subsection on page 'Förstå din pension'.");
			e.printStackTrace();
			returnToIndexByLogo(driver, tools);
		}
		logger.debug("Testing link 3 'Tjänstepensionsguiden' in sub menu 1 in the menu in page " + Index.class.getSimpleName());
		try {
			// Open the menu and then the first sub menu
			openMenu(driver, "pm-submenu-1-item-0");
			// test the third link in the first sub menu 'Tjänstepensionsguiden'.
			testLink(driver, "a.pm_menu__sub-list-item-link.pm_js-menu__link[href='/forsta-din-pension/tjanstepension']", 
					"Förstå din pension | Pensionsmyndigheten"
					+"Tjänstepensionsguiden"); 	// TODO: Key should be class name (not title)
		} catch (Exception e) {
			logger.error("Could not test the third menu choice 'Tjänstepensionsguiden' in the first submenu on '"
					+Index.class.getSimpleName()+"'. It should have linked to a specific subsection on page 'Förstå din pension'.");
			e.printStackTrace();
			returnToIndexByLogo(driver, tools);
		}
		// TODO: Test the rest of the menu items
		// Verify that popup windows have been tested
		indexPopups.popupResults();
		// Return the browser driver
		logger.info("Finished testing " +this.getClass().getSimpleName() );
		logger.debug("Returning the browser driver");
		WebDriverSettings.quitDriver(driver);
	}
	
	/**
	 * tests a link in the main page of Pensionsmyndighetens web site
	 *
	 * @param  driver           web driver to use for the testing
	 * @param  linkCssSelector  WebDriver cssSelector identifier for the link to be tested
	 * @param  page             A page with method isThisPage() that will be used to verify that the link led to the correct page
	 */
	private void testLink(WebDriver driver, String linkCssSelector, String pageKey) {
		// Find and follow link
		tools.testLink(driver, pageKey, linkCssSelector);
		// Return to Index page
		returnToIndexByLogo(driver, tools);
		// Test popups in page
		indexPopups.test(driver);
	}
	
	/**
	 * Open the menu in the main page of Pensionsmyndighetens web site
	 *
	 * @param  driver         web driver to use for the testing
	 * @param  ariaControls   the attribute that identifies which sub menu to expand
	 * 
	 */
	private void openMenu(WebDriver driver, final String ariaControls) {
		// Open the menu
		openMenu(driver);
		// If sub menu is not already open
		if(!driver.findElement(By.cssSelector("ul#pm-submenu-1-item-0")).isDisplayed()) {
			// Wait until top level menu is open 
			(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver d) {
					return d.findElement(By.cssSelector(
							"button.pm_menu__expand-btn[aria-controls='"+ariaControls+"']"
							)).isDisplayed();
				}
			});
			// Click on sub menu expand button to open sub menu
			driver.findElement(By.cssSelector(
					"button.pm_menu__expand-btn[aria-controls='"+ariaControls+"']"
					)).click();
		}
		// Assert that menu is now open
		Assert.assertTrue("Unable to open the sub menu identified by '"+ariaControls+"' in "
				+this.getClass().getSimpleName(), 
				driver.findElement(By.cssSelector("ul#pm-submenu-1-item-0")).isDisplayed());
	}
	private void openMenu(WebDriver driver) {
		// If menu is not already open
		if(!driver.findElement(By.id("pm-menu")).isDisplayed()) {
			// Click on menu to open it
			driver.findElement(By.cssSelector(
					"button.pm_header__nav-link.pm_header__nav-link--menu.pm_js-menu-open[aria-controls='pm-menu']"
					)).click();
		}
		// Wait for menu to open 
		(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return d.findElement(By.id("pm-menu")).isDisplayed();
			}
		});
		// Assert that menu is now open
		Assert.assertTrue("Unable to open the menu in "
				+this.getClass().getSimpleName(), 
				driver.findElement(By.id("pm-menu")).isDisplayed());
	}
	
	/**
	 * Returns to the main page of Pensionsmyndighetens web site
	 *
	 * @param  driver   web driver to use for the testing
	 */
	static void returnToIndexByLogo(WebDriver driver, Tools tools) {
		logger.debug("Returning to the page "+Index.class.getSimpleName()+" by clicking the logo in the upper left corner of the current page '"+driver.getTitle()+"'");
		//Store the title of the current page in case anything goes wrong
		String oldTitle = driver.getTitle();
		// Try 3 times in case it does not work first time (as has happened, perhaps because of intermittent Internet?)
		int i=0;
		do {
			i++;
			// Find the logo that leads back to the index page by it's id
			WebElement element;
			try {
				element = driver.findElement(By.id("pm-header-logo"));
			} catch (Exception e) {
				logger.warn("Unable to find the logo that leads back to the index page by it's id 'pm-header-logo'. Trying to find it by href='https://www.pensionsmyndigheten.se' instead. Current page is "
						+ driver.getCurrentUrl());
				element = driver.findElement(By.cssSelector("a[href='" + url
						+ "']"));
			}
			logger.debug("Clicking element with text='" + element.getText()
					+"', name='" + element.getTagName()
					+"', location='" + element.getLocation()
					+"', size='" + element.getSize()
					+"' and element.toString()='" + element.toString()+"'");
			element.click();
			// Wait for the index page to load. The title should start with "Pension"
			tools.waitLoad(driver, new Index());
			logger.warn("Failed to return to page "+Index.class.getSimpleName()
					+". Three attempts will be made");
		} while (!PageVerify.getPage(Index.class.getSimpleName()).isThisPage(driver)
				&& i<3);
		logger.trace("Page title is now: " + driver.getTitle());
		Assert.assertTrue("The link logo in the upper left corner of page '"
				+oldTitle+"' should have led to "+Index.class.getSimpleName()
				+" but led to the wrong page.", 
				PageVerify.getPage(Index.class.getSimpleName()).isThisPage(driver));
	}
	
	/**
	 * Checks if the current page in driver is the main page of Pensionsmyndighetens web site
	 *
	 * @param  driver   web driver to use for the testing
	 * 
	 */
	public boolean isThisPage(WebDriver driver){
		boolean isThisPage=true;
		if( ! Tools.strClean(title).equals(Tools.strClean(driver.getTitle())) ) {isThisPage=false;}
		return isThisPage;
	}

	/**
	 * Returns the title of the main page of Pensionsmyndighetens web site
	 *
	 * @param  driver   web driver to use for the testing
	 * 
	 */
	public String getTitle() {
		return title;
	}
}