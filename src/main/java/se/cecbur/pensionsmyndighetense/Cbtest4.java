package se.cecbur.pensionsmyndighetense;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import se.cecbur.pensionsmyndighetense.pages.Index;

/**
 * This program tests the pensionsmyndigheten.se web site
 * 
 * @param    The name of the file to read the settings from. The file will be created if it does not exist.
 * 			 (Optional. If no file is specified then the file settings.json in 
 *           the sub directory src\main\resources of the home directory will be assumed.)
 *
 */
public class Cbtest4 
{
	// Make arguments available to all
	private static String[] arguments= null;
	
	// Create a logger for this class
	private final static Logger logger = LogManager.getLogger(Cbtest4.class);

    public static void main( String[] args ) throws FileNotFoundException
    {
    	// Make arguments available to all
    	arguments=args;
    	// Log that program has started
    	logger.info("Started test program "+Cbtest4.class.getSimpleName()
    			+ ". Pensionsmyndighetens web-site at "
    			+ Settings.settings.getIndexURL()
    			+ " will be tested.");
    	// Settings that are initialized here and not in Settings because they rely on the settings object already existing
    	// Web driver settings 
        WebDriverSettings.initialize();
        
        // Start testing
        try {
			new Index().test();
		} catch (Exception e) {
			logger.error("Page "+Index.class.getName()+" could not be tested.");
			e.printStackTrace();
		}
    }
    
    static File getArgJsonSettingsFile() {
    	// Wait for arguments if variable arguments is not yet populated
    	int i=0;
    	while (arguments== null && i<20) {
    		try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
    	}
    	if (arguments== null) {
    		throw new ExceptionInInitializerError("Internal error. Could not find the arguments to this program ('"
    				+Cbtest4.class.getName()+"')");
    	}
    	if (arguments.length>=1) {
    		return (new File(arguments[0]));
    	}
    	else {
    		return null;
    	}
    }
}
