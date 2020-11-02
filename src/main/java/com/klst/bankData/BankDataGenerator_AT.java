package com.klst.bankData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.github.miachm.sods.Range;
import com.github.miachm.sods.Sheet;
import com.klst.iban.BankDataGenerator;
import com.klst.iban.BusinessIdentifierCode;
import com.klst.ibanTest.API_Key_Provider;
import com.klst.ods.Ods;

public class BankDataGenerator_AT extends BankDataGenerator {

	private static final Logger LOG = Logger.getLogger(BankDataGenerator_AT.class.getName());
	
	static final String COUNTRY_CODE = "AT";
    static final String ODS_RESOURCE = RESOURCE_DATA_PATH + COUNTRY_CODE+"/" +"sepa-zv-vz_gesamt.ods";
	
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

	Map<Integer, ArrayList<Object>> bankByCode = new Hashtable<Integer, ArrayList<Object>>();
//	Map<Integer, ArrayList<String>> atblz = new Hashtable<Integer, ArrayList<String>>();

	BankDataGenerator_AT(String api_key) {
		super(api_key);
		
        List<Sheet> sheets = Ods.getSheets(ODS_RESOURCE);
    	int numColumns = NUMCOLUMNS;

        Map<String,Integer> nonEmptySheets = new Hashtable<String,Integer>();
        Sheet nonEmptySheet = Ods.getNonEmptySheet(sheets, nonEmptySheets, numColumns);
        LOG.info("file "+ODS_RESOURCE+" has nonEmptySheets/sheets:"+nonEmptySheets.size()+"/"+sheets.size());
    	
		if (nonEmptySheets.size() == 1) {
			Collection<Integer> collection = nonEmptySheets.values();
			int numRows = collection.iterator().next();
			Range range = nonEmptySheet.getRange(0, 0, numRows, numColumns);
			LOG.info("range.getNumRows()=" + range.getNumRows() + " range.getNumColumns()=" + range.getNumColumns());
			Object[][] values = range.getValues();
			// r==0 ist colname, daher start bei 1
			for (int r = 1; r < range.getNumRows(); r++) {
    			Object bic = values[r][COL_SWIFT_Code]; // String, aber nicht immer
    			Object blz = values[r][COL_Bankleitzahl]; // String numeric
    			Object name = values[r][COL_Bankenname];
//				for (int c = 0; c < range.getNumColumns(); c++) {
//					Object v = values[r][c];
//					Object cellObect = range.getCell(r, c).getValue();
//					LOG.info("r(" + r + "),c:" + c + " " + (v == null ? "null" : v.getClass()) + " " + cellObect);
//				}
    			
				Integer bankCode = Ods.getInteger(blz);
    			LOG.info("r("+r+"):"+bankCode+" ==> "+bic+" "+blz+" "+name);
    			if(bankCode!=null) {
    				bankByCode.put(bankCode, new ArrayList<Object>(Arrays.asList(new BusinessIdentifierCode((String)bic), name)));
    			}

			}

		}
		LOG.info("bankByCode.size="+bankByCode.size());

	}
	

	public void tryWith(String countryCode, String format, int from, int to) {
		List<Integer> bankCodeList = new ArrayList<Integer>(bankByCode.keySet());
		for(int id=from; id<=to; id++) {
			if(bankCodeList.contains(id)) {
	    		String bankCode = String.format(format, id);
//	    		ArrayList<Object> bankProps = bankByCode.get(id); // [ bic , name ]
	    		FakeIban iban = new FakeIban(countryCode, bankCode);
    			LOG.info("id="+id + " tryWith "+iban+" bankCode "+bankCode);
    			printBankDataViaApi(id, iban);
			}
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		BankDataGenerator test = new BankDataGenerator_AT(API_Key_Provider.API_KEY);

		test.tryWith(COUNTRY_CODE, BankDataGenerator.FORMAT_05d, 00000, 99999);
		
	}
}
