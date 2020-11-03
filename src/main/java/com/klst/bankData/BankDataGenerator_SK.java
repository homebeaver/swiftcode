package com.klst.bankData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import com.klst.ibanTest.API_Key_Provider;

public class BankDataGenerator_SK extends NumericBankCode {

	private static final Logger LOG = Logger.getLogger(BankDataGenerator_SK.class.getName());
	
	static final String COUNTRY_CODE = "SK";
	
	// numeric bankCode aus https://www.nbs.sk/en/payment-systems/directories/directory-sk
    static final String ODS_RESOURCE = RESOURCE_DATA_PATH + COUNTRY_CODE+"/" +"Directory_IC_DPS_SR.ods";
//  nCodename	name	aCode	activeParty
	static final int COL_nID         =  0;  // aka bankCode
	static final int COL_Name        =  1;
	static final int COL_BIC         =  2;  // aka SWIFT_Code
	static final int COL_activeParty =  3;  // aka SWIFT_Code
	
	BankDataGenerator_SK(String api_key) {
		super(COUNTRY_CODE, api_key);

		columnMapper = new ArrayList<Object>(Arrays.asList( // nur size wird in super benötigt!
				COL_nID , 
				COL_Name ,
				COL_BIC ,
				COL_activeParty ));
		refBankByCodeArray = new ArrayList<Integer>(Arrays.asList(COL_nID, COL_BIC, COL_Name));
		
		this.loadBankByCode(ODS_RESOURCE);
	}
	
	public static void main(String[] args) throws Exception {
		NumericBankCode test = new BankDataGenerator_SK(API_Key_Provider.API_KEY);

		test.tryWith(FORMAT_04d, 0000, 9999);
	}

}
