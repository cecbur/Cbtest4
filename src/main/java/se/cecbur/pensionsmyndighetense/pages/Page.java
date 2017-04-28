/**
 * 
 */
package se.cecbur.pensionsmyndighetense.pages;

import javax.naming.ConfigurationException;

import org.openqa.selenium.WebDriver;

/**
 * @author Cecilia
 *
 */
public interface Page {
	public String getTitle();
	public boolean isThisPage(WebDriver driver);
}
