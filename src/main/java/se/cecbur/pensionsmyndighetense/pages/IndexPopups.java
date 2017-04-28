/**
 * 
 */
package se.cecbur.pensionsmyndighetense.pages;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import se.cecbur.pensionsmyndighetense.Settings;
import se.cecbur.pensionsmyndighetense.Tools;

/**
 * @author Cecilia
 *
 */
public class IndexPopups {
	// Create a logger for this class
	private final static Logger logger = LogManager.getLogger(IndexPopups.class);
	
	// Create Tools object
	private Tools tools =new Tools();
	
	// Create lists to hold results for tested popups 
	private ArrayList<Boolean> popupCookieResult =  new ArrayList<Boolean>();
	private ArrayList<Boolean> popupPollResult =  new ArrayList<Boolean>();
	
	// Get Title and URL for Index page from settings 
	private static final String title=Settings.settings.getIndexTitle(); 	// Title is "Pensionsmyndigheten";
	private static final String url=Settings.settings.getIndexURL(); 	// URL is https://www.pensionsmyndigheten.se/;
	
	// Selectors
	private static final String cookieBannerLinkCssSelector="a.pm_cookie-banner__link[href='https://www.pensionsmyndigheten.se/Personuppgifter.html']";
	private static final String cookieBannerButtonCssSelector="button.pm_cookie-banner__button.pm_js-cookie-banner-button";

	/**
	 * A class for testing popups on the main page of Pensionsmyndighetens web site
	 */
	public IndexPopups() {
	}	

	/**
	 * main method used for testing all aspects of the main page of pensionsmyndighetens web site
	 * TODO: Only testing of links currently implemented
	 */
	public void test(WebDriver driver) {
		// Check the title of the page
		Assert.assertEquals("Page at "+url+" has the wrong title: ", title, driver.getTitle());

		// Test all popups
		testCookieBanner(driver, (Math.random()<0.5)); 	// TODO: Remove cookie 50% of the time
		// TODO: Test poll popup iframe
	}	
	
	/**
	 * main method used for testing all aspects of the main page of pensionsmyndighetens web site
	 * TODO: Only testing of links currently implemented
	 */
	public void testCookieBanner(WebDriver driver, boolean removeCookie) {
		// Test link in cookie popup banner
		if (popupCookieResult.size()==0){
			testPopupLink(driver, popupCookieResult, cookieBannerLinkCssSelector,
					"Om kakor och personuppgiftslagen (PuL) | Pensionsmyndigheten");
		}
		// Test button in cookie popup banner
		if (popupCookieResult.size()==1){
			// Verify cookie banner button
			try {
				// Set timeout to 0 temporarily
				tools.setTmpImplicitWait(driver, 0);
				// Find the button by its cssSelector
				WebElement element = driver.findElement(By.cssSelector(cookieBannerButtonCssSelector));
				// Reset timeout to default value
				tools.setTmpImplicitWait(driver);
				if(element!=null && element.isDisplayed()){
					logger.debug("Verifying the button in the cookie banner (identified by cssSelector '"
							+cookieBannerButtonCssSelector+"' and in page '"+title+"')");
					// Click on button in cookie popup banner
					element.click();
					// Wait for banner to disappear
					(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
						public Boolean apply(WebDriver d) {
							try {
								return (! d.findElement(By.cssSelector(cookieBannerButtonCssSelector)).isDisplayed() );
							} catch (Exception e) {return true;}
						}
					});
					// Verify that banner has disappeared
					boolean bannerVisible=true;
					// Verify that banner button has disappeared
					try {
						bannerVisible=element.isDisplayed();
					} catch (Exception e1) {bannerVisible=false;}
					// Verify that banner link has disappeared
					if (!bannerVisible){
						try {
							if( driver.findElement(By.cssSelector(cookieBannerLinkCssSelector)).isDisplayed() ) {
								bannerVisible=true;
							}
						} catch (Exception e) { // link has disappeared
						}
					}
					popupCookieResult.add(!bannerVisible);
					Assert.assertFalse("Cookie banner did not disappear when the button in the banner was clicked. The cssSelector for the button was '"
							+cookieBannerButtonCssSelector+"'",
							bannerVisible);
					// Cookie does not have to to be removed. Banner appears at next test run anyway
					Cookie cookie=driver.manage().getCookieNamed("COOKIE_CONSENT");
					if (Tools.strClean(".pensionsmyndigheten.se").equals(
							Tools.strClean(cookie.getDomain()))) {
						if(removeCookie){
							logger.debug("Deleting cookie '"+cookie+"'");
							driver.manage().deleteCookie(cookie);
						}
						else {
							logger.debug("The cookie that was set when the cookie banner button was clicked will not be removed. \n   Cookie: '"
									+cookie+"'");
						}
					}
				}
			} catch (org.openqa.selenium.NoSuchElementException e) {
				// Just reset timeout to default value if the popup is not found
				tools.setTmpImplicitWait(driver);
			}
		}
	}	
	
	/**
	 * Log popups that did not turn up and hence could not be tested
	 */
	void popupResults() {
		// Verify that popup windows have been tested
		logger.debug("Verifying that popup windows have been tested");
		if(popupCookieResult.size()==0){
			logger.warn("Cookie popup dialogue has not been tested on page '"+title+"' ("  +this.getClass().getSimpleName() +")");
		}
		else {
	        Assert.assertTrue("Link in cookie popup banner failed verification", popupCookieResult.get(0));
	        if(popupCookieResult.size()==1){
				logger.warn("Only the link in the cookie popup dialogue has been tested on page '"+title+"' ("  +this.getClass().getSimpleName() +")");
			}
	        else {
		        Assert.assertTrue("Button in cookie popup banner failed verification", popupCookieResult.get(1));
	        }
		}
		if(popupPollResult.size()==0){
			logger.warn("Poll popup dialogue has not been tested on page '"+title+"' ("  +this.getClass().getSimpleName() +")");
		}
		else {
	        Assert.assertTrue("Link to poll in popup banner failed verification", popupPollResult.get(0));
		}
	}	
	
	/**
	 * test link in popup in the main page of Pensionsmyndighetens web site
	 *
	 * @param  WebDriver                web driver to use for the testing
	 * @param  result                   An array list where the result of the 
	 *                                  testing of this popup is stored
	 * @param  linkCssSelector          Selenium cssSelector that identifies the link to test
	 * @param  pageKey                  The key to the page that the link should lead to, as defined in PageVerify
	 */
	
	private void testPopupLink(
			WebDriver driver, 
			ArrayList<Boolean> result,
			String linkCssSelector,
			String pageKey) 
	{
		// Verify link in popup
		try {
			Page page=PageVerify.getPage(pageKey);
			// Set timeout to 0 temporarily
			tools.setTmpImplicitWait(driver, 0);
			// Find the link by its cssSelector
			WebElement element = driver.findElement(By.cssSelector(linkCssSelector));
			// Reset timeout to default value
			tools.setTmpImplicitWait(driver);
			if(element!=null){
				logger.debug("Verifying the popup link to '"+page.getTitle()+"'");
				logger.debug("The link to '"+page.getTitle()+"' is identified by cssSelector '"+linkCssSelector+"'");
				tools.testLink(driver, pageKey, linkCssSelector);
				String strVal="Link with cssSelector '"+linkCssSelector+"' led to the wrong page: ";
				try {
					Assert.assertTrue(strVal, 
							PageVerify.getPage(pageKey).isThisPage(driver));
					result.add(true);
				} catch (Exception e) {
					result.add(false);
					// Let it fail on failed assert
					Assert.assertTrue(strVal, 
							PageVerify.getPage(pageKey).isThisPage(driver));
				}
				// Return to Index page
				Index.returnToIndexByLogo(driver, tools);
			}
		} catch (org.openqa.selenium.NoSuchElementException e) {
			// Just reset timeout to default value if the popup is not found
			tools.setTmpImplicitWait(driver);
		}
	}
	

}