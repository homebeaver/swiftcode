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
import com.klst.ods.Ods;

public class BankDataGenerator_IE extends BankDataGenerator {

	private static final Logger LOG = Logger.getLogger(BankDataGenerator_IE.class.getName());

	static final String COUNTRY_CODE = "IE";

	static final String RESOURCE_PATH = "src/main/resources/";
	static final String ODS_RESOURCE = "ie/NSC_List_SEPA.ods";

	static final int COL_NSC                     =  0;
	static final int COL_BIC                     =  1;
	static final int COL_SEPA_Reachable          =  2;
	static final int COL_Bank                    =  3;
	static final int COL_Branch                  =  4;
	static final int COL_Building_name           =  5;
	static final int COL_Street_and_number       =  6;
	static final int COL_City                    =  7;
	static final int COL_Postcode                =  8;
	static final int COL_County                  =  9;
	static final int COL_POBox                   = 10;
	static final int COL_Branch_telephone_number = 11;
	static final int COL_Branch_fax_number       = 12;
	static final int COL_Branch_email_address    = 13;
	static final int COL_Currency                = 14;
	static final int COL_Country                 = 15;
	static final int NUMCOLUMNS           = 16;

	Map<Integer, ArrayList<Object>> atblz = new Hashtable<Integer, ArrayList<Object>>();

	BankDataGenerator_IE(String api_key) {
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
        	Range range = nonEmptySheet.getRange(0, COL_NSC, nonEmptySheets.get(nonEmptySheet.getName()));
    		Object[][] values = range.getValues();
    		for (int r = 0; r < range.getNumRows(); r++) {
    			for (int c = 0; c < range.getNumColumns(); c++) {
    				Object v = values[r][c];
//            		Object cellObect = range.getCell(r, c).getValue();
    				Integer bankCode = Ods.getInteger(v);; // sortCode
    				if(bankCode==null) {

    				} else {
    					Object bic = nonEmptySheet.getRange(r, COL_BIC).getValue();
    					Object name = nonEmptySheet.getRange(r, COL_Bank).getValue();
    					Object country = nonEmptySheet.getRange(r, COL_Country).getValue();
    					
    					System.out.println("r("+r+"),c:"+bankCode + " bic:"+bic + " name:"+name + " country:"+country);
    					atblz.put(bankCode, new ArrayList<Object>(Arrays.asList(new BusinessIdentifierCode((String)bic), name)));
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
	
	public void tryWith(String countryCode, String format, int from, int to) {
		List<Integer> sortCodeList = new ArrayList<Integer>(atblz.keySet());
		for(int id=from; id<=to; id++) {
			if(sortCodeList.contains(id)) {
	    		String sortCode = String.format(format, id);
	    		ArrayList<Object> bankProps = atblz.get(id); // bic + name
	    		BusinessIdentifierCode bic = (BusinessIdentifierCode)bankProps.get(0);
	    		FakeIban iban = new FakeIban(countryCode, bic.getBankCode(), sortCode);
    			LOG.info("id="+id + " tryWith "+iban+" bankCode "+bic.getBankCode() +" branchCode "+sortCode);
    			printBankDataViaApi(id, iban);
			}
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		BankDataGenerator test = new BankDataGenerator_IE(API_Key_Provider.API_KEY);

		test.tryWith(COUNTRY_CODE, BankDataGenerator.FORMAT_06d, 000000, 999999);
		
	}
}
