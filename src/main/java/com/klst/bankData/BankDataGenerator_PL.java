package com.klst.bankData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import com.klst.ibanTest.API_Key_Provider;

public class BankDataGenerator_PL extends NumericBankCode {

	private static final Logger LOG = Logger.getLogger(BankDataGenerator_PL.class.getName());
	
	static final String COUNTRY_CODE = "PL";
	
	// numeric bankCode aus https://www.ewib.nbp.pl/faces/pages/stronaGlowna.xhtml
    static final String ODS_RESOURCE = RESOURCE_DATA_PATH + COUNTRY_CODE+"/" +"plewibnra.ods";
/* das file hat keine header Zeile
 * Es gibt 33 Spalten A .. AE und 3135 Zeilen
 */
	static final int COL_nID         =  4;  // E aka bankCode
	static final int COL_Name        =  1;	// B
	static final int COL_BIC         = 19;  // T aka SWIFT_Code
	static final int NUMCOLUMNS      = 32;
	
	BankDataGenerator_PL(String api_key) {
		super(COUNTRY_CODE, api_key);

		refBankByCodeArray = new ArrayList<Integer>(Arrays.asList(COL_nID, COL_BIC, COL_Name));
		
		this.loadBankByCode(ODS_RESOURCE, 0, NUMCOLUMNS);
	}
	
	public static void main(String[] args) throws Exception {
		NumericBankCode test = new BankDataGenerator_PL(API_Key_Provider.API_KEY);

		test.tryWith(FORMAT_08d, 10000000, 99999999);
	}

}
