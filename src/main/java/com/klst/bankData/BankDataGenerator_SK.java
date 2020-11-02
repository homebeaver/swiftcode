package com.klst.bankData;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.klst.iban.BankDataGenerator;

public class BankDataGenerator_SK extends BankDataGenerator {

	private static final Logger LOG = Logger.getLogger(BankDataGenerator_SK.class.getName());
	
	static final String COUNTRY_CODE = "SK";
	
	BankDataGenerator_SK(String api_key) {
		super(api_key);
	}

	// numeric bankCode aus https://www.nbs.sk/en/payment-systems/directories/directory-sk
	Integer[] bankCodes =
		{200
		,900
		,720
		,1100
		,1111
		,3000
		,3100
		,5200
		,5600
		,5900
		,6500
		,7500
		,7930
		,8100
		,8120
		,8170
		,8160
		,8180
		,8320
		,8330
		,8360
		,8370
		,8420
		,2010
		,5800
		};
	
	// TODO gleiche Oberklsse mit AT bilden
	public void tryWith(String countryCode, String format, int from, int to) {
		List<Integer> bankCodeList = Arrays.asList(bankCodes);
		for(int id=from; id<=to; id++) {
			if(bankCodeList.contains(id)) {
	    		String bankCode = String.format(format, id);
	    		FakeIban iban = new FakeIban(countryCode, bankCode);
	    		LOG.info("id="+id + " tryWith "+iban+" bankCode "+bankCode);
//	    		printBankDataViaApi(id, iban);
			}
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		BankDataGenerator test = new BankDataGenerator_SK("testKey");

		test.tryWith(COUNTRY_CODE, BankDataGenerator.FORMAT_04d, 0000, 9999);
		
	}
}
