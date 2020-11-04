package com.klst.bankData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import com.klst.iban.BankDataGenerator;
import com.klst.ibanTest.API_Key_Provider;

public class BankDataGenerator_HR extends NumericBankCode {

	private static final Logger LOG = Logger.getLogger(BankDataGenerator_HR.class.getName());

	static final String COUNTRY_CODE = "HR";
	
	// Quelle: https://www.hnb.hr/en/core-functions/payment-system/bank-codes
    static final String ODS_RESOURCE = RESOURCE_DATA_PATH + COUNTRY_CODE+"/" +"tf-pp-ds-vbb-xlsx-e-vbb.ods";
//    National ID | BIC Code | Financial Institution Name
	static final int COL_Nix      =  0;
	static final int COL_Name     =  1;
	static final int COL_nID      =  2;  // aka bankCode
	static final int COL_BIC      =  3;  // aka SWIFT_Code
	static final int NUMCOLUMNS   =  4;

	BankDataGenerator_HR(String api_key) {
		super(COUNTRY_CODE, api_key);
		
		refBankByCodeArray = new ArrayList<Integer>(Arrays.asList(COL_nID, COL_BIC, COL_Name));
		
		this.loadBankByCode(ODS_RESOURCE, 4, NUMCOLUMNS);
	}
	

	public static void main(String[] args) throws Exception {
		NumericBankCode test = new BankDataGenerator_HR(API_Key_Provider.API_KEY);

		test.tryWith(BankDataGenerator.FORMAT_07d, 0000000, 9999999);
		
	}
}
