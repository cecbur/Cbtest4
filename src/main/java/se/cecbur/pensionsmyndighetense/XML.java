/**
 * 
 */
package se.cecbur.pensionsmyndighetense;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import se.cecbur.pensionsmyndighetense.pages.Page;




/**
 * @author Cecilia
 *
 */
public class XML {
	// Create a logger for this class
	private final static Logger logger = LogManager.getLogger(XML.class);

	private static Hashtable<Object, Workbook> loans = new Hashtable<Object, Workbook>();
	
    public static Iterator<Row> getRowIterator(File xlsxFile, String sheetName) {
    	Workbook workbook;
        Iterator<Row> iterator;
        try {
            FileInputStream excelFile = new FileInputStream(xlsxFile);
            workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheet(sheetName);
            iterator = datatypeSheet.iterator();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new IllegalArgumentException ("Could not find .xlsx file: "+ xlsxFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException ("Could not find .xlsx file: "+ xlsxFile);
        }
        loans.put(iterator, workbook);
		return iterator;
    }
    
    public static void returnLoan(Object object){
    	Workbook workbook=loans.get(object);
    	loans.remove(object);
    	if(!loans.contains(workbook)){
    		try {
				workbook.close();
			} catch (IOException e) {
				logger.warn("An object was returned that has not been loaned from class "+XML.class.getName()+". The object was: "+object);
				e.printStackTrace();
			}
    	}
    }

    public static void cbtemp() { 	// TODO: Remove

        try {

            FileInputStream excelFile = new FileInputStream(
            		Settings.settings.getTestcasesXML());
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();

            while (iterator.hasNext()) {

                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();

                while (cellIterator.hasNext()) {

                    Cell currentCell = cellIterator.next();
                    //getCellTypeEnum shown as deprecated for version 3.15
                    //getCellTypeEnum ill be renamed to getCellType starting from version 4.0
                    if (currentCell.getCellTypeEnum() == CellType.STRING) {
                        System.out.print(currentCell.getStringCellValue() + "--");
                    } else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        System.out.print(currentCell.getNumericCellValue() + "--");
                    }

                }
                System.out.println();
                workbook.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
	
	
	/*

    // private static final File testcasesXML = Settings.settings. "/tmp/MyFirstExcel.xlsx";
	
    public static void cbtemp(){
        try
        {
        	// import org.apache.poi.ss.usermodel.*;
        	
        	/*
        	Workbook[] wbs = new Workbook[] { new HSSFWorkbook(), new XSSFWorkbook() };
        	for(int i=0; i<wbs.length; i++) {
        	   Workbook wb = wbs[i];
        	   CreationHelper createHelper = wb.getCreationHelper();

        	   // create a new sheet
        	   Sheet s = wb.createSheet();
        	   // declare a row object reference
        	   Row r = null;
        	   // declare a cell object reference
        	   Cell c = null;
        	   // create 2 cell styles
        	   CellStyle cs = wb.createCellStyle();
        	   CellStyle cs2 = wb.createCellStyle();
        	   DataFormat df = wb.createDataFormat();
        	   */
        	/*
        	Workbook[] wbs = new Workbook[] { new HSSFWorkbook(), new XSSFWorkbook() };
        	Workbook wb = new XSSFWorkbook(Settings.settings.getTestcasesXML());
            Sheet mySheet = wb.getSheetAt(0);
            Iterator<Row> rowIter = mySheet.rowIterator();

        	
        	// System.out.println(Settings.settings.getTestcasesXML());
            // String excelPath = "C:\\Jackson\\Employee.xls";
            FileInputStream fileInputStream = new FileInputStream(Settings.settings.getTestcasesXML());

            // Create Workbook instance holding .xls file
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);

            // Get the first worksheet
            XSSFSheet sheet = workbook.getSheetAt(0);
            HSSFSheet sheet = workbook.getSheetAt(0);

            // Iterate through each rows
            Iterator<Row> rowIterator = sheet.iterator();

            while (rowIterator.hasNext())
            {
                // Get Each Row
                Row row = rowIterator.next();

                // Iterating through Each column of Each Row
                Iterator<Cell> cellIterator = row.cellIterator();

                while (cellIterator.hasNext())
                {
                    Cell cell = cellIterator.next();

                    // Checking the cell format
                    switch (cell.getCellTypeEnum())
                    {
                    case NUMERIC: 	// CellType.NUMERIC
                        System.out.print(cell.getNumericCellValue() + "\t");
                        break;
                    case STRING: 	// CellType.STRING
                        System.out.print(cell.getStringCellValue() + "\t");
                        break;
                    case BOOLEAN: 	// CellType.BOOLEAN
                        System.out.print(cell.getBooleanCellValue() + "\t");
                        break;
                    }
                }
                System.out.println("");
            }

        } catch (IOException ie)
        {
            ie.printStackTrace();
        }

    }
    */
}
