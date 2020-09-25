package com.klst.iban;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

public class BankDataGenerator_ME extends BankDataGenerator {

	private static final Logger LOG = Logger.getLogger(BankDataGenerator_ME.class.getName());
	
	BankDataGenerator_ME(String api_key) {
		super(api_key);
	}
	
	void tryWith(String countryCode, String format, int from, int to, String account) {
		Map<String, List<JSONObject>> jMap;
		try {
			jMap = super.jsonMap(JSON_DIR+countryCode+JSON_EXT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		for(int id=from; id<=to; id++) {
    		String bankCode = String.format(format, id);
    		String iban = countryCode + PP + bankCode + account;
//    		LOG.info("bankCode="+bankCode + " iban:"+iban);
    		printBankDataViaApi(id, iban, jMap);
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		BankDataGenerator test = new BankDataGenerator_ME("testKey");

		test.tryWith("ME", BankDataGenerator.FORMAT_03d, 0, 999, "000012345678951"); // ME25 505 000012345678951
	}
}
