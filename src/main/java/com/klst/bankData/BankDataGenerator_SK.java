package com.klst.iban;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

public class BankDataGenerator_SK extends BankDataGenerator {

	private static final Logger LOG = Logger.getLogger(BankDataGenerator_SK.class.getName());
	
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
	void tryWith(String countryCode, String format, int from, int to, String account) {
		List<Integer> bankCodeList = Arrays.asList(bankCodes);
		Hashtable<String, List<JSONObject>> jMap = new Hashtable<String, List<JSONObject>>(); // leer
		for(int id=from; id<=to; id++) {
			if(bankCodeList.contains(id)) {
	    		String bankCode = String.format(format, id);
	    		String iban = countryCode + PP + bankCode + account;
//	    		LOG.info("bankCode="+bankCode + " iban:"+iban);
	    		printBankDataViaApi(id, iban, jMap);
			}
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		BankDataGenerator test = new BankDataGenerator_SK("testKey");

		test.tryWith("SK", BankDataGenerator.FORMAT_04d, 0000, 9999, "0000198742637541"); // SK31 1200 0000198742637541
		
	}
}
