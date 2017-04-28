/**
 * 
 */
package se.cecbur.pensionsmyndighetense;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonGenerator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import se.cecbur.pensionsmyndighetense.pages.PageVerify;

/**
 * @author Cecilia
 *
 */
public class Settings {
	// Create a logger for this class
	private final static Logger logger = LogManager.getLogger(Settings.class);
	
	// A static variable accessible to all and providing the settings for the program
	public static final Settings settings=new Settings();
	
	// The home directory and sub directories
	private Path home = java.nio.file.Paths.get(".").toAbsolutePath().getParent();
	private Path resourcesDir  = home.resolve("src").resolve("main").resolve("resources");
	
	// Json file for settings
	private File settingsJson=resourcesDir.resolve("settings.json").toFile();
	
	// Data files
	// XML file with data on web pages that don't have their own class
	private File webPagesXML=resourcesDir.resolve("Pages.xlsx").toFile();
	private String webPagesXML_NoClassPages="NoClassPages"; 	// tab with information about pages that don't have their own class in the program
	// XML file with data for testcases
	private File testcasesXML=resourcesDir.resolve("TestCases.xlsx").toFile();
	
	// Paths to drivers for browsers
	private String geckoPath = home + "\\bin\\geckodriver.exe";
	
	// Default implicit timeout for web drivers in seconds
	private long implicitWait = 0;
	
	// Titles of web pages
	private String indexTitle = "Pensionsmyndigheten";
	private String omkakorTitle = "Om kakor och personuppgiftslagen (PuL) | Pensionsmyndigheten";
	private String lattlastTitle = "Lättläst | Pensionsmyndigheten";
	
	// URL:s to web pages
	private String indexURL = "https://www.pensionsmyndigheten.se";
	
	/**
	 * Initializes values and settings before testing starts
	 * 
	 * @throws    FileNotFoundException  When the driver for web browser can not be found
	 * @throws    IOException            when json settings file can neither be read nor created
	 * 
	 */
	private Settings() {
		// Check if Log4j settings file exists and print suggestion if it does not
		File varTmpDir = new File(resourcesDir.resolve("log4j2.xml").toString());
		if (! varTmpDir.exists()) {
			logger.info("Log4j2 settings file not found at: "
					+varTmpDir.getAbsolutePath()
					+" \n If desired, a new file may be created with the following default content: \n "
					+"<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n "
					+"<Configuration status=\"WARN\"> \n "
					+"   <Appenders> \n "
					+"      <Console name=\"ConsoleAppender\" target=\"SYSTEM_OUT\"> \n "
					+"         <PatternLayout pattern=\"%d [%t] %-5level %logger{36} - %msg%n%throwable\"/> \n "
					+"      </Console> \n "
					+"   </Appenders> \n "
					+"   <Loggers> \n "
					+"      <Root level=\"ERROR\"> \n "
					+"         <AppenderRef ref=\"ConsoleAppender\"/> \n "
					+"      </Root> \n "
					+"   </Loggers> \n "
					+"</Configuration>"
					);
			// Logging levels can be found here: https://logging.apache.org/log4j/2.x/log4j-api/apidocs/org/apache/logging/log4j/Level.html
		}
		
		// Check if path to json configuration file has been given as parameter
		File tmpVal=Cbtest4.getArgJsonSettingsFile();
		logger.trace("Using config file "+tmpVal);
		if (tmpVal!=null) {
			logger.info("Using config file "+tmpVal);
			settingsJson=tmpVal;
		}
		
		// Read settings from json configuration file
		try {
			readJson(); 	// throws IOException if the json configuration file can neither be read or created
		} catch (Exception e) {
			// Error 
			e.printStackTrace();
			System.exit(3);
		}
		
		// Settings that are initialized in public static void main because they rely on the settings object already existing
		// Web driver settings 
		// WebDriverSettings.initialize(); 	// throws FileNotFoundException if web driver executable can not be found
		// Create page objects that can be used to verify that the current page is a certain page
		// PageVerify.createVerifiablePages();
	}
	
	/**
	 * Read settings from the json settings file
	 *
	 * @param  settingsJsonFile            The name of the file to read the settings from
	 * 
	 * @throws IOException                 If the file can neither be read nor created
	 * @throws ExceptionInInitializerError a setting is missing in the file
	 * 
	 */
	private void readJson() throws IOException{
		if (settingsJson.exists()) {
			logger.info("Reading settings from json file "+settingsJson
					+" \n   (If this file is removed, a new one will be created with standard values)");
			// Read json content from json settings file 
			JsonReader reader = Json.createReader(new FileReader(settingsJson));
			JsonObject jsonObject = reader.readObject();
			logger.debug("Read the following from the json settings file: \n   "+ jsonObject.toString());
			// Parse json settings and update variables
			try {
				home=Paths.get(jsonObject.getString("home"));
				webPagesXML=new File(jsonObject.getString("webPagesXML"));
				webPagesXML_NoClassPages=jsonObject.getString("webPagesXML_NoClassPages");
				testcasesXML=new File(jsonObject.getString("testcasesXML"));
				geckoPath=jsonObject.getString("geckoPath");
				implicitWait=jsonObject.getInt("implicitWait");
				indexTitle=jsonObject.getJsonObject("pageTitles").getString("indexTitle");
				omkakorTitle=jsonObject.getJsonObject("pageTitles").getString("omkakorTitle");
				lattlastTitle=jsonObject.getJsonObject("pageTitles").getString("lattlastTitle");
				indexURL=jsonObject.getJsonObject("pageURLs").getString("indexURL");
				logger.debug("Found settings: \n"
						+"   home="+home+"\n"
						+"   webPagesXML="+webPagesXML+"\n"
						+"   webPagesXML_NoClassPages="+webPagesXML_NoClassPages+"\n"
						+"   testcasesXML="+testcasesXML+"\n"
						+"   geckoPath="+geckoPath+"\n"
						+"   implicitWait="+implicitWait+"\n"
						+"   indexTitle="+indexTitle+"\n"
						+"   omkakorTitle="+omkakorTitle+"\n"
						+"   lattlastTitle="+lattlastTitle+"\n"
						+"   indexURL="+indexURL
						);
			} catch (Exception e) {
				// A value is missing in the json settings file
				e.printStackTrace();
				String tmpVal = "One of the values "
						+"home, "
						+"webPagesXML, "
						+"webPagesXML_NoClassPages, "
						+"testcasesXML, "
						+"geckoPath, "
						+"implicitWait, "
						+"indexTitle, "
						+"omkakorTitle, "
						+"lattlastTitle or "
						+"indexURL "
						+"could not be found in the configuration file "
						+settingsJson;
				String tmpVal2 = "(Remove the file and restart to create a new file with default values.)";
				logger.fatal(tmpVal+". \n   "+tmpVal2);
				throw new ExceptionInInitializerError(tmpVal+": "+settingsJson+" \n   "+tmpVal2);
			}
		}
		else {
			// Create json file with (default) settings
			writeJson();
			logger.warn("Created settings file "+settingsJson
					+"\n   Please verify the settings in the file and then restart to run this program");
			System.exit(1);
		}
	}
	
	/**
	 * Write settings to the json settings file
	 *
	 * @param  settingsJsonFile The name of the file to write the settings to
	 *                          The file will be created if it does not exist 
	 *                          and replaced if it does exist
	 * 
	 * @throws IOException      If the file can neither be deleted nor created
	 * 
	 */
	void writeJson(String settingsJsonFile) throws IOException{
		settingsJson=new File(settingsJsonFile);
		writeJson();
	}
	void writeJson() throws IOException{
		if (settingsJson.exists()) {
			logger.info("Writing current settings to json file "+settingsJson);
			settingsJson.delete();
		}
		// Create directory for json file if it does not exist
		settingsJson.getParentFile().mkdirs();
		// Write settings to json file
		FileWriter writer = new FileWriter(settingsJson);
		JsonGenerator jsonGenerator = Json.createGenerator(writer);
		jsonGenerator.writeStartObject()
				.write("home", home.toString())
				.write("webPagesXML", webPagesXML.getAbsolutePath())
				.write("webPagesXML_NoClassPages", webPagesXML_NoClassPages)
				.write("testcasesXML", testcasesXML.getAbsolutePath())
				.write("geckoPath", geckoPath)
				.write("implicitWait", implicitWait)
				.writeStartObject("pageTitles")
					.write("indexTitle", indexTitle)
					.write("omkakorTitle", omkakorTitle)
					.write("lattlastTitle", lattlastTitle)
					.writeEnd()
				.writeStartObject("pageURLs")
					.write("indexURL", indexURL)
					.writeEnd()
				.writeEnd();
		jsonGenerator.close();
	}
	
	/**
	 * Get the Json file for settings
	 *
	 * @return   a file object pointing to the current Json file for settings
	 */
	public File getSettingsJson(){
		return settingsJson;
	}

	/**
	 * Get the XML file with data on web pages (that don't have their own class)
	 *
	 * @return   a file object pointing to the current XML file with data on web pages (that don't have their own class)
	 */
	public File getWebPagesXML(){
		return webPagesXML;
	}

	/**
	 * Get the name of the XML sheet with data on web pages that don't have their own class
	 *
	 * @return   the name of the XML sheet with data on web pages that don't have their own class
	 */
	public String getwebPagesXML_NoClassPagesSheetName(){
		return webPagesXML_NoClassPages;
	}

	/**
	 * Get the XML file with data for testcases
	 *
	 * @return   a file object pointing to the current XML file with data for testcases
	 */
	public File getTestcasesXML(){
		return testcasesXML;
	}

	/**
	 * Get the path to the gecko executable that is used to connect to the Firefox browser
	 *
	 * @return   path to the gecko executable
	 */
	public String getGeckoPath(){
		return geckoPath;
	}

	/**
	 * Get the implicit time that the driver will wait for a WebElement to appear
	 *
	 * @return   implicit time that the driver will wait for a WebElement to appear in seconds
	 */
	public long getimplicitWait(){
		return implicitWait;
	}

	/**
	 * Get the title of the index page
	 *
	 * @return   the title of the index page
	 */
	public String getIndexTitle(){
		return indexTitle;
	}

	/**
	 * Get the title of the index page
	 *
	 * @return   the title of the index page
	 */
	public String getLattlastTitle(){
		return lattlastTitle;
	}

	/**
	 * Get the URL of the index page
	 *
	 * @return    the URL of the index page
	 */
	public String getIndexURL(){
		return indexURL;
	}

}
