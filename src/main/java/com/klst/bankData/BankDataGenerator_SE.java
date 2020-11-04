package com.klst.bankData;

import java.util.logging.Logger;

import com.klst.iban.BankDataGenerator;
import com.klst.ibanTest.API_Key_Provider;

public class BankDataGenerator_SE extends NumericBankCode {

	private static final Logger LOG = Logger.getLogger(BankDataGenerator_SE.class.getName());
	
	static final String COUNTRY_CODE = "SE";

	BankDataGenerator_SE(String api_key) {
		super(COUNTRY_CODE, api_key);
	}
	
	public static void main(String[] args) throws Exception {
		NumericBankCode test = new BankDataGenerator_SE(API_Key_Provider.API_KEY);

		test.tryWith(BankDataGenerator.FORMAT_03d, 000, 999);	
	}

}
