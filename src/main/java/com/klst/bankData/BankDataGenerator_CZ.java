package com.klst.bankData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import com.klst.ibanTest.API_Key_Provider;

public class BankDataGenerator_CZ extends NumericBankCode {

	private static final Logger LOG = Logger.getLogger(BankDataGenerator_CZ.class.getName());
	
	static final String COUNTRY_CODE = "CZ";
	
	// numeric bankCode aus https://www.cnb.cz/en/payments/accounts-bank-codes/
    static final String ODS_RESOURCE = RESOURCE_DATA_PATH + COUNTRY_CODE+"/" +"kody_bank_CR.ods";
//Kód platebního styku | Poskytovatel platebních služeb | BIC kód (SWIFT) | Systém CERTIS
	static final int COL_nID         =  0;  // aka bankCode
	static final int COL_Name        =  1;
	static final int COL_BIC         =  2;  // aka SWIFT_Code
	static final int COL_CERTIS      =  3;
	
	BankDataGenerator_CZ(String api_key) {
		super(COUNTRY_CODE, api_key);

		columnMapper = new ArrayList<Object>(Arrays.asList( // nur size wird in super benötigt!
				COL_nID , 
				COL_Name ,
				COL_BIC ,
				COL_CERTIS ));
		refBankByCodeArray = new ArrayList<Integer>(Arrays.asList(COL_nID, COL_BIC, COL_Name));
		
		this.loadBankByCode(ODS_RESOURCE);
	}
	
	public static void main(String[] args) throws Exception {
		NumericBankCode test = new BankDataGenerator_CZ(API_Key_Provider.API_KEY);

		test.tryWith(FORMAT_04d, 0000, 9999);
	}

}
