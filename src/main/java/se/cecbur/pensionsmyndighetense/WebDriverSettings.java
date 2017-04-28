/**
 * 
 */
package se.cecbur.pensionsmyndighetense;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * Handles all initialization for browser drivers for Selenium
 * This class can only be used statically since it depends on the 
 * System Property webdriver.gecko.driver.
 * 
 * @author Cecilia Burman
 *
 */
public class WebDriverSettings {
	// Create a logger for this class
	private final static Logger logger = LogManager.getLogger(WebDriverSettings.class);
	
	// Path to the gecko driver for the Mozilla Firefox browser
	private static String geckoPath = Settings.settings.getGeckoPath();
	// Default browser to use in tests
	private static Browser browser = Browser.FIREFOX;
	// Number of drivers currently in use. (The browser type can not be changed when drivers are in use because the browser setup depends on a system variable.)
	private static int countDriversInUse = 0;

	/**
	 * Defines supported browser types
	 * 
	 */
	public static enum Browser {
		FIREFOX, CROME, EXPLORER // TODO: Only Firefox implemented so far
	}

	/**
	 * This class can only be used statically since it depends on the System Property webdriver.gecko.driver. Use method initialize instead.
	 * 
	 * @throws ExceptionInInitializerError if an object is created from the class
	 * 
	 */
	public WebDriverSettings() throws ExceptionInInitializerError {
		logger.error(this.getClass().getName()
						+ " can only be used statically since it depends on the System Property webdriver.gecko.driver. Use method initialize instead.");
		throw new ExceptionInInitializerError(
				this.getClass().getName()
						+ " can only be used statically since it depends on the System Property webdriver.gecko.driver. Use method initialize instead.");
	}

	/**
	 * Initialize web driver settings etc. so that Selenium tests can be run
	 *
	 * @throws FileNotFoundException  If the browser driver binary can not be found
	 */
	public static void initialize() throws FileNotFoundException {
		// Vait for general settings to be properly initialized
		File tmpVal=Settings.settings.getSettingsJson();
		logger.trace("Setting geckoPath");
		setGeckoPath(geckoPath);
	}

	/**
	 * Use this method to get a driver to run Selenium tests on
	 *
	 * @return WebDriver the driver to run the Selenium tests on
	 */
	public static WebDriver getDriver() {
		// Create Tools object
		Tools tools =new Tools();
		// Create variable for the new driver
		WebDriver driver;
		// Keep count of the number of drivers in use
		countDriversInUse++;
		logger.debug("countDriversInUse is now: "+countDriversInUse);
		// Create the right kind of driver
		if ( browser==Browser.FIREFOX ) {
			driver = new FirefoxDriver();
		}
		else {
			countDriversInUse--;
			logger.debug("countDriversInUse is now: "+countDriversInUse);
			throw new UnsupportedOperationException("The browser "+ browser+" has not been implemented yet"); 	// TODO: Implement all browsers in ENUM Browsers
		}
		// Verify the default value for the implicit time that the driver will wait for a WebElement to appear (unless it has been previously verified)
		verifyImplicitWait(driver);
		// Set default implicit wait for driver
		tools.setTmpImplicitWait(driver);
		// Return the new driver
		return driver;
	}

	/**
	 * Verify the implicit time that the driver will wait for a WebElement to appear
	 * by comparing the setting to a real timeout
	 * 
	 * @param  driver       The driver verify the implicit wait on
	 */
	private static void verifyImplicitWait(WebDriver driver){
		// Measure timeout
		long seconds=measureImplicitWait(driver);
		/* TODO: Remove
		WebElement element;
		long time=System.currentTimeMillis();
		try {
			element = driver.findElement(
					By.cssSelector("This is an element that will not be found"));
		} catch (Exception e) {}
		// Calculate timeout in seconds
		long seconds=(System.currentTimeMillis()-time)/1000;
		*/
		// Get setting for timeout in seconds
		long settingSecs=Settings.settings.getimplicitWait();
		// Check if there is a significant difference between measured timout and setting
		if (Math.abs(settingSecs-seconds)<1
				&& seconds>=0.9*settingSecs 
				&& seconds<=1.1*settingSecs) {
			logger.debug("Setting for implicit timeout verified. Setting is "
					+settingSecs
					+" seconds and a real timeout (without the setting) was "
					+seconds
					+" seconds. \n   (The setting for the timeout can be found in: "
					+Settings.settings.getSettingsJson().getAbsolutePath()+")");
		}
		else {
			logger.info("Setting for implicit timeout could be wrong. Setting is "
					+settingSecs
					+" seconds but a real timeout without that setting was "
					+seconds
					+" seconds. \n   The setting for the timeout can be found in: "
					+Settings.settings.getSettingsJson().getAbsolutePath());
		}
	}
	
	/**
	 * Measure the implicit time that the driver will wait for a WebElement to appear
	 * 
	 * @param  driver       The driver to measure on
	 */
	/* TODO
	static long measureImplicitWait(){
		WebDriver driver = new FirefoxDriver();
		driver.get("https://www.google.se/");
		return measureImplicitWait(driver);
	}
	*/
	private static long measureImplicitWait(WebDriver driver){
		// Measure timeout
		WebElement element;
		long time=System.currentTimeMillis();
		try {
			element = driver.findElement(
					By.cssSelector("This is an element that will not be found"));
		} catch (Exception e) {}
		// Return timeout in seconds
		return (System.currentTimeMillis()-time)/1000;
	}
	
	/**
	 * Closes the driver (e.g. to allow a change of browser)
	 *
	 * @param  driver       The driver to quit, clean and close
	 */
	public static void quitDriver(WebDriver driver) {
		countDriversInUse--;
		logger.info("Closing web driver. \n   Please ignore channel errors following. For more information see: https://github.com/mozilla/geckodriver/issues/387");
		driver.close();
		driver.quit();
		logger.debug("countDriversInUse is now: "+countDriversInUse);
	}

	/**
	 * Number of drivers in use
	 *
	 * @return  int   The number of drivers currently in use
	 */
	public static int getDriverCount() {
		logger.trace("countDriversInUse is: "+countDriversInUse);
		return countDriversInUse;
	}

	/**
	 * Get the current browser type
	 *
	 * @return  Browser ENUM identifying the browser type in use
	 */
	public static Browser getBrowser() {
		logger.trace("browser is: "+browser.name());
		return browser;
	}

	/**
	 * Set the type of browser to use
	 * Currently only Firefox is supported 	// TODO: Implement all browsers in ENUM Browsers
	 *
	 * @param  ENUM    identifying the browser-type to change to
	 * @return boolean true if the new browser was successfully set
	 * @throws UnsupportedOperationException  For other browsers than Firefox 	// TODO: Implement all browsers in ENUM Browsers
	 * @throws IOException                    If a driver is already in use 
	 *                                        because it depends on the System 
	 *                                        Property webdriver.gecko.driver
	 */
	public static boolean setBrowser(Browser par_browser)
			throws UnsupportedOperationException, IOException {
		if(countDriversInUse>0) {
			throw new IOException("Can not change browser while drivers are in use. Currently "+countDriversInUse+" drivers are in use.");
		}
		if ( par_browser==Browser.FIREFOX ) {
			setGeckoPath(geckoPath);
			
		}
		else {
			throw new UnsupportedOperationException("The browser "+ par_browser+" has not been implemented yet"); 	// TODO: Implement all browsers in ENUM Browsers
		}
		logger.debug("browser is now: "+browser.name());
		return true;
	}

	/**
	 * Get the path to the Gecko driver for Firefox 	// TODO: Implement all browsers in ENUM Browsers
	 *
	 * @return String The path to the Gecko driver for Firefox
	 */
	public static String getGeckoPath() {
		logger.trace("geckoPath is: "+geckoPath);
		return geckoPath;
	}

	/**
	 * Set the path to the Gecko driver for Firefox 	// TODO: Implement all browsers in ENUM Browsers
	 *
	 * @param String The path to the Gecko driver for Firefox
	 * @throws FileNotFoundException  If the path to the Gecko driver for 
	 *                                Firefox can not be found
	 */
	public static void setGeckoPath(String par_geckoPath)
			throws FileNotFoundException {
		File varTmpDir = new File(par_geckoPath);
		if (varTmpDir.exists()) {
			geckoPath = par_geckoPath;
			System.setProperty("webdriver.gecko.driver", geckoPath);
		} else {
			throw new FileNotFoundException("Could not find geckodriver: "
					+ par_geckoPath);
		}
		logger.debug("geckoPath is now: "+geckoPath);
	}

}