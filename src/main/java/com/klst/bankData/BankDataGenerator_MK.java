package com.klst.bankData;

import java.util.logging.Logger;

import com.klst.iban.BankDataGenerator;
import com.klst.ibanTest.API_Key_Provider;

public class BankDataGenerator_MK extends NumericBankCode {

	private static final Logger LOG = Logger.getLogger(BankDataGenerator_MK.class.getName());
	
	static final String COUNTRY_CODE = "MK"; // North Macedonia - non SEPA

	BankDataGenerator_MK(String api_key) {
		super(COUNTRY_CODE, api_key);
	}
	
	public static void main(String[] args) throws Exception {
		NumericBankCode test = new BankDataGenerator_MK(API_Key_Provider.API_KEY);

		test.tryWith(BankDataGenerator.FORMAT_03d, 000, 999);	
	}

}
