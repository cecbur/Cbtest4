/**
 * 
 */
package se.cecbur.pensionsmyndighetense.pages;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import se.cecbur.pensionsmyndighetense.Settings;
import se.cecbur.pensionsmyndighetense.Tools;
import se.cecbur.pensionsmyndighetense.XML;

/**
 * This class is used to create objects that can verify that a driver 
 * is on the correct page. Normally this should be done by a class for that page
 * but when no such class exists an object of this class can be used.
 * 
 * @author Cecilia
 *
 */
public class PageVerify implements Page {
	// Create a logger for this class
	private final static Logger logger = LogManager.getLogger(PageVerify.class);
	
	// Create a static list of verifialble pages
	private static Hashtable<String, Page> pages = createVerifiablePages();
	private String title="";
	private String cleanedTitle="";
	private ArrayList ver;
	
	// Available verification types
	public static enum Verifictionmethods {
	    TITLE, ELEMENTSDISPLAYED_CSSSELECTOR, ELEMENTSDISPLAYED_XPATH
	}
	
	/**
	 * Pass available means of identifying the page to the constructor
	 * 
	 * @param  pageTitle      Title of the page
	 * @param  [subKey]       The key identifying this page will be Tools.strClean(pageTitle)+subKey
	 * @param  verifications  methods and parameters to use for the testing
	 *                        This arraylist contains a verification method (enum) followed by the parameters needed 
	 *                        for that verification, then a new verification method and so on
	 *                        TITLE            Does not need any parameters since it uses the parameter pageTitle
	 *                        ELEMENTISVISIBLE will have string parameters  with the Selenium cssSelector for each
	 *                                         field that should be isDisplayed
	 */
	private PageVerify (String pageTitle, ArrayList verifications) {
		title=pageTitle;
		cleanedTitle=Tools.strClean(title);
		ver=verifications;
	}

	/**
	 * Returns a verifiable page
	 *
	 * @param   key   Keys are SimpleName for other classes and Tools.strClean(title) for pages of this class PageVerify
	 * 
	 * @return        Hashtable of pages. 
	 * 
	 */
	public static Page getPage(String key) {
		if(!pages.containsKey(key)){
			if(pages.containsKey(Tools.strClean(key))) {
				key=Tools.strClean(key);
			}
			else {
				throw new IllegalArgumentException (
						"The key '"+key+"' does not correspond to a known verifiable page"); 
			}
		}
		return pages.get(key);
	}

	/**
	 * This function creates dummy verifiable Page objects for pages that don't have their own class
	 * @return 
	 */
	private static Hashtable<String, Page> createVerifiablePages() {
		logger.trace("Initializing list of verifiable pages");
		Hashtable<String, Page> pagesTmp = new Hashtable<String, Page>(); 
		pagesTmp.put(Index.class.getSimpleName(), new Index()); 
		pagesTmp.put("Next Key", new Index()); 	// TODO: Replace by next page. Index is not needed twice

		// Get a row iterator from the Excel document specifying values for pages that don't have their own class
		Iterator<Row> rows=XML.getRowIterator(Settings.settings.getWebPagesXML(), 
				Settings.settings.getwebPagesXML_NoClassPagesSheetName());
		while (rows.hasNext()) {
			logger.trace("Found rows in the Excel document specifying values for pages that don't have their own class");
			// Get a row
            Row currentRow = rows.next();
            // Get an iterator for the cells in that row
            Iterator<Cell> cellIterator = currentRow.iterator();
            // Ignor empty rows
            if (cellIterator.hasNext()) {
				logger.trace("Found cells in the current row in the Excel document specifying values for pages that don't have their own class");
            	Cell cell=cellIterator.next();
            	String column="";
            	// Read the page title from the first column
				String title=cell.getStringCellValue();
				column="Title";
				// The main part of the key is created from the title string
				String key=Tools.strClean(title);
				if (cellIterator.hasNext()) {
					logger.trace("Found cells after title in the current row in the Excel document specifying values for pages that don't have their own class");
					int tmpVal=cell.getColumnIndex();
					cell=cellIterator.next();
					if(cell.getColumnIndex()-tmpVal==1){
						column="SubTitle";
					}
					else {
						// cellIterator.next() skipped one column
						column="Verifictionmethod";
					}
				}
				else {
					throw new ExceptionInInitializerError(
							"Unable to read subkey / verifictionmethod from row "+cell.getRowIndex()
							+" in file "+Settings.settings.getWebPagesXML()
							+" on sheet '"+Settings.settings.getwebPagesXML_NoClassPagesSheetName()+"'");
				}
				if(column=="SubTitle") {
					logger.trace("Found sub title in the current row ("+cell.getRowIndex()+1+") in the Excel document specifying values for pages that don't have their own class");
					// Add sub key to key variable
					key=key+Tools.strClean(cell.getStringCellValue());
					if (cellIterator.hasNext()) {
						cell=cellIterator.next();
						column="Verifictionmethod";
					}
					else {
						throw new ExceptionInInitializerError(
							"No verificationmethod specified on row "+cell.getRowIndex()+1 	// cell.getRowIndex() starts on 0
							+" in file "+Settings.settings.getWebPagesXML()
							+" on sheet '"+Settings.settings.getwebPagesXML_NoClassPagesSheetName()+"'");
					}
				}
				logger.trace("Creating ArrayList object containing verification methods for verifying the page on row "+cell.getRowIndex()+1+" in the Excel document specifying values for pages that don't have their own class");
				// ArrayList object containing verification methods for verifying that a page is this page
				ArrayList<Object> verifictions = new ArrayList<Object>();
				// Add verification instructions
		        do {
		        	String verifiction=cell.getStringCellValue();
		        	try {
		        		// If the value is a Verifictionmethods ENUM
						logger.trace("Trying to add the string '"+verifiction+"' as a Verifictionmethods ENUM to the ArrayList object containing verification methods (row "+cell.getRowIndex()+1+" in the Excel document specifying values for pages that don't have their own class)");
						verifictions.add(PageVerify.Verifictionmethods.valueOf(
								verifiction));
					} catch (Exception e) {
						// If the value is not a Verifictionmethods ENUM
						logger.trace("Instead adding the string '"+verifiction+"' as string to the ArrayList object containing verification methods (row "+cell.getRowIndex()+1+" in the Excel document specifying values for pages that don't have their own class)");
						verifictions.add(verifiction);
					}
	            } while (cellIterator.hasNext() && (cell=cellIterator.next()).getColumnIndex()>0); 	// Iterate if cellIterator.hasNext() (.getColumnIndex()>0 always returns true)
				logger.trace("Finished interpreting key, title & ArrayList from the row "
						+cell.getRowIndex()+1
						+" in the Excel document specifying values for pages that don't have their own class"
						+" \n   key='"+key
						+"' \n   title='"+title
						+"' \n   verifictions='"+verifictions+"'");
	            // Create a new PageVerify object for the page and add it to the list
				pagesTmp.put(key, new PageVerify(title, verifictions));
            }
        }
		logger.trace("Finished reading the Excel document specifying values for pages that don't have their own class");
        XML.returnLoan(rows); 	// Close workbook
		return pagesTmp;
	}

	/**
	 * Returns the title of the page
	 *
	 * @return  title of the page
	 * 
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Checks if the current page in driver is the page identified by this object
	 *
	 * @param  driver                   web driver to use for the testing
	 * @return                          whether the current page is the page identified by this object
	 */
	public boolean isThisPage(WebDriver driver){
		boolean isThisPage=true;
		int i=0;
		while (i<ver.size()) {
			// Verify that the next value in the list is an enum denoting a verification method
			boolean tmpVal=false;
			for (Verifictionmethods method : Verifictionmethods.values()) {
		        if (method.equals(ver.get(i))) { tmpVal=true; }
		    }
			if (!tmpVal) {
				throw new IllegalArgumentException (
						"Unknown criteria. This is not a valid Verifictionmethods enum: '"+ver.get(i)+"'");
			}
			switch ((Verifictionmethods)ver.get(i)) {
            case TITLE:  
            	// Identify page by title
        		if(!cleanedTitle.equals(Tools.strClean(driver.getTitle()))) {
        			isThisPage=false;
	        		logger.debug("("+title+"): isThisPage() failed title. Expected value '"+cleanedTitle
	        				+"' not equal to real value '"+Tools.strClean(driver.getTitle())+"'");
			}
        		i++;
        		break;
            case ELEMENTSDISPLAYED_CSSSELECTOR:
            case ELEMENTSDISPLAYED_XPATH:
            	// Identify page by displayed element
            	Verifictionmethods verifictionmethod =(Verifictionmethods) ver.get(i);
            	By bySelector;
            	i++;
            	while (i<ver.size() && ver.get(i) instanceof String) {
            		// Create selector for driver.findElement
	            	if (verifictionmethod.equals(PageVerify.Verifictionmethods.ELEMENTSDISPLAYED_CSSSELECTOR)) {
	            		bySelector=By.cssSelector((String) ver.get(i));
					}
	            	else {
	            		bySelector=By.xpath((String)ver.get(i));
					}
	            	// Check if element exists and is displayed
            		try {
						tmpVal=driver.findElement(bySelector).isDisplayed();
						if(!tmpVal) { 
							isThisPage=false; 
			        		logger.debug("("+title+
			        				"): isThisPage() failed on .isDisplayed() returning false for element identified by cssSelector '"
			        				+ver.get(i)+"'");
						}
					} catch (Exception e) { 
						isThisPage=false; 
		        		logger.debug("("+title+
		        				"): isThisPage() failed on not finding the element identified by cssSelector '"
		        				+ver.get(i)+"'");
					}
            		i++;
            	}
            	break;
			}
			
		}
		if (isThisPage) {
			logger.debug("("+title+"): Page confirmed by isThisPage()");
		}
		return isThisPage;
	}
	
	/**
	 * Checks if the current page in driver is the page identified the key
	 *
	 * @param  driver                   web driver to use for the testing
	 * @param  key                      The string key identifying the page
	 * 
	 * @return                          whether the current page is the page identified by the key
	 */
	public boolean isThatPage(WebDriver driver, String key) {
		return getPage(Tools.strClean(key)).isThisPage(driver);
	}
	
}
