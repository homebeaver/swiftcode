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

public class BankDataGenerator_AT extends BankDataGenerator {

	private static final Logger LOG = Logger.getLogger(BankDataGenerator_AT.class.getName());
	
	static final String RESOURCE_PATH = "src/main/resources/";
	static final String ODS_RESOURCE = "at/sepa-zv-vz_gesamt.ods";
	
	static final int COL_Kennzeichen      =  0;
	static final int COL_Identnummer      =  1;
	static final int COL_Bankleitzahl     =  2;  // aka bankCode
	static final int COL_Institutsart     =  3;
	static final int COL_Sektor           =  4;
	static final int COL_Firmenbuchnummer =  5;
	static final int COL_Bankenname       =  6;
	static final int COL_Strasse          =  7;
	static final int COL_PLZ              =  8;
	static final int COL_Ort              =  9;
	static final int COL_Postadresse_Str  = 10;
	static final int COL_Postadresse_PLZ  = 11;
	static final int COL_Postadresse_Ort  = 12;
	static final int COL_Postfach         = 13;
	static final int COL_Bundesland       = 14;
	static final int COL_Telefon          = 15;
	static final int COL_Fax              = 16;
	static final int COL_eMail            = 17;
	static final int COL_SWIFT_Code       = 18;
	static final int COL_Homepage         = 19;
	static final int COL_Gruendungsdatum  = 20;
	static final int COL_SWIFT8           = 21;
	static final int COL_passive          = 22;
	static final int NUMCOLUMNS           = 23;

	Map<Integer, ArrayList<String>> atblz = new Hashtable<Integer, ArrayList<String>>();

	BankDataGenerator_AT(String api_key) {
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
    		
    		for (int c = 0; c < NUMCOLUMNS ; c++) {
        		range = sheet.getRange(0, c, numRows);
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
        	Range range = nonEmptySheet.getRange(0, COL_Bankleitzahl, nonEmptySheets.get(nonEmptySheet.getName()));
    		Object[][] values = range.getValues();
    		for (int r = 0; r < range.getNumRows(); r++) {
    			for (int c = 0; c < range.getNumColumns(); c++) {
    				Object v = values[r][c];
//            		Object cellObect = range.getCell(r, c).getValue();
    				Integer bankCode = getBankCode(v);
    				if(bankCode==null) {

    				} else {
    					Object bic = nonEmptySheet.getRange(r, COL_SWIFT_Code).getValue();
    					Object name = nonEmptySheet.getRange(r, COL_Bankenname).getValue();
    					System.out.println("r("+r+"),c:"+bankCode + " bic:"+bic + " name:"+name);
    					atblz.put(bankCode, new ArrayList<String>(Arrays.asList((String)bic, (String)name)));
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
	
	Integer getBankCode(Object v) {
		if(v==null) {
			return null;
		} else if(v.getClass()==String.class) {
			try {
				return Integer.parseInt((String) v);
			} catch(NumberFormatException e) {
				return null;
			}
		} else if(v.getClass()==Double.class) {
			return ((Double) v).intValue();
		} else {
	        LOG.severe("v.getClass() " + v.getClass());
	        return null;
		}
	}

	JSONObject updateJSONObjectXXX(JSONObject jo, String key, Object value) {
		if(BANK.equals(key)) {
			LOG.info(key + ":: iban:"+value);
			return super.updateJSONObject(jo, BANK, value);
		}
		return super.updateJSONObject(jo, key, value);
	}
	
	void tryWith(String countryCode, String format, int from, int to, String account) {
		List<Integer> bankCodeList = new ArrayList<Integer>(atblz.keySet());
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
		BankDataGenerator test = new BankDataGenerator_AT(API_Key_Provider.API_KEY);

		test.tryWith("AT", BankDataGenerator.FORMAT_05d, 00000, 99999, "00234573201"); // AT61 19043 00234573201
		
	}
}
