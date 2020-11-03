package com.klst.iban;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

import com.github.miachm.sods.Range;
import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SpreadSheet;
import com.klst.ibanTest.API_Key_Provider;

public class BankDataGenerator_FI extends BankDataGenerator {

	private static final Logger LOG = Logger.getLogger(BankDataGenerator_FI.class.getName());
	
	static final String RESOURCE_PATH = "src/main/resources/";
	static final String ODS_RESOURCE = "fi/Finnish_monetary_institution_codes_and_BICs_in_excel_format.ods";

	// fmicab : Finnish_monetary_institution_codes_and_BICs
	Map<Integer, ArrayList<String>> fmicab = new Hashtable<Integer, ArrayList<String>>();

	BankDataGenerator_FI(String api_key) {
		super(api_key);
        SpreadSheet spreadSheet = null;
		try {
			spreadSheet = new SpreadSheet(new File(RESOURCE_PATH+ODS_RESOURCE));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOG.info("Number of sheets: " + spreadSheet.getNumSheets());

        List<Sheet> sheets = spreadSheet.getSheets();
        Map<String,Integer> nonEmptySheets = new Hashtable<String,Integer>();
        Sheet nonEmptySheet = null;

        for (Sheet sheet : sheets) {
            Range range = sheet.getDataRange();
            int totalRows = 0;
            int maxRows = 0;
            int numColumns = range.getNumColumns();
            int lastColumn = range.getLastColumn();
            int numRows = range.getNumRows();
            int lastRow = range.getLastRow();
            int numValues = range.getNumValues();
            //                             1002/1003 
    		LOG.info("Columns " + lastColumn +"/" +numColumns+ " , Rows " + lastRow +"/"+numRows + " in sheet " + sheet.getName() + " numValues="+numValues);
//            LOG.info(range.toString()); // too long for print, MAX_PRINTABLE = 1024
    		
//    		range = sheet.getRange(0, 0, numRows); // erste Spalte : National ID
//    		maxRows = getMaxRows(range);
//    		range = sheet.getRange(0, 1, numRows); // zweite Spalte : BIC
//    		maxRows = getMaxRows(range);
//    		range = sheet.getRange(0, 2, numRows); // dritte Spalte : Financial Institution Name
//    		maxRows = getMaxRows(range);
    		
    		for (int r = 0; r < 3 ; r++) {
        		range = sheet.getRange(0, r, numRows);
        		maxRows = getMaxRows(range);
        		if(maxRows>totalRows) totalRows = maxRows;
    		}
    		if(totalRows==0) {
        		LOG.info("empty sheet " + sheet.getName());
    		} else {
        		LOG.info("sheet " + sheet.getName() + " totalRows="+totalRows);
        		nonEmptySheet = sheet;
        		nonEmptySheets.put(nonEmptySheet.getName(), totalRows);
    		}
    	}
        if(nonEmptySheets.size()==1) {
        	Range range = nonEmptySheet.getRange(0, 0, nonEmptySheets.get(nonEmptySheet.getName()));
    		Object[][] values = range.getValues();
    		for (int r = 0; r < range.getNumRows(); r++) {
    			for (int c = 0; c < range.getNumColumns(); c++) {
    				Object v = values[r][c];
//            		Object cellObect = range.getCell(r, c).getValue();
    				if(v==null) {

    				} else {
    					if(v.getClass()==String.class) {
    						try {
    							int nationalID = Integer.parseInt((String) v);
    							Object bic = nonEmptySheet.getRange(r, 1).getValue();
    							Object name = nonEmptySheet.getRange(r, 2).getValue();
                        		System.out.println("r("+r+"),c:"+nationalID + " bic:"+bic + " name:"+name);
                        		fmicab.put(nationalID, new ArrayList<String>(Arrays.asList((String)bic, (String)name)));
    						} catch(NumberFormatException e) {
    							// nix
    						}
    					}	
    				}
//    				LOG.info("r("+r+"),c:"+v + (v==null? "null" : v.getClass()) + cellObect);
    			}
    		}
        }

	}
	
	int getMaxRows(Range range) {
        int maxRows = 0;
		Object[][] values = range.getValues();
		for (int r = 0; r < range.getNumRows(); r++) {
			for (int c = 0; c < range.getNumColumns(); c++) {
				Object v = values[r][c];
				if(v!=null) maxRows = 1+r;
				//LOG.info("r("+r+"),c:"+v);
			}
		}
		if(maxRows>0) {
			LOG.info("Columns " + range.getNumColumns() + " , Rows " + range.getNumRows() + " maxRows="+maxRows);
		}
		return maxRows;
	}

	JSONObject updateJSONObjectXXX(JSONObject jo, String key, Object value) {
		if(BANK.equals(key)) {
			LOG.info(key + ":: iban:"+value);
			return super.updateJSONObject(jo, BANK, value);
		}
		return super.updateJSONObject(jo, key, value);
	}
	
	void tryWith(String countryCode, String format, int from, int to, String account) {
		List<Integer> bankCodeList = new ArrayList<Integer>(fmicab.keySet());
		for(int id=from; id<=to; id++) {
			if(bankCodeList.contains(id)) {
	    		String bankCode = String.format(format, id);
//	    		ArrayList<String> bankProps = fmicab.get(id); // bic + name
	    		Hashtable<String, List<JSONObject>> jMap = new Hashtable<String, List<JSONObject>>(); // leer
    			String iban = countryCode + PP + bankCode + account;
    			printBankDataViaApi(id, iban, jMap);
			}
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		BankDataGenerator test = new BankDataGenerator_FI(API_Key_Provider.API_KEY);

		test.tryWith("FI", BankDataGenerator.FORMAT_03d, 000, 999, "45600000785"); // FI21 123 45600000785
		
	}
}
