package com.klst.bankData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

import com.klst.iban.BankDataGenerator;
import com.klst.ibanTest.API_Key_Provider;

public class BankDataGenerator_FI extends NumericBankCode {

	private static final Logger LOG = Logger.getLogger(BankDataGenerator_FI.class.getName());

	static final String COUNTRY_CODE = "FI";
    static final String ODS_RESOURCE = RESOURCE_DATA_PATH + COUNTRY_CODE+"/" +"Finnish_monetary_institution_codes_and_BICs_in_excel_format.ods";
//    National ID | BIC Code | Financial Institution Name
	static final int COL_nID      =  0;  // aka bankCode
	static final int COL_BIC       = 1;  // aka SWIFT_Code
	static final int COL_Name     =  2;

	// fmicab : Finnish_monetary_institution_codes_and_BICs
	Map<Integer, ArrayList<String>> fmicab = new Hashtable<Integer, ArrayList<String>>();
    // =====> Map<Integer, ArrayList<Object>> bankByCode

	BankDataGenerator_FI(String api_key) {
		super(COUNTRY_CODE, api_key);
		
//		columnMapper = new ArrayList<Object>();
		columnMapper = new ArrayList<Object>(Arrays.asList( // nur size wird in super benötigt!
				0 , // "key" , // BANK_CODE ,
				1 , //Bank_Data.SWIFT_CODE ,
				2 )); // BANK name
		refBankByCodeArray = new ArrayList<Integer>(Arrays.asList(COL_nID, COL_BIC, COL_Name));
		
		this.loadBankByCode(ODS_RESOURCE, 2);
	}
	

	public static void main(String[] args) throws Exception {
		NumericBankCode test = new BankDataGenerator_FI(API_Key_Provider.API_KEY);

		test.tryWith(BankDataGenerator.FORMAT_03d, 000, 999);
		
	}
}
