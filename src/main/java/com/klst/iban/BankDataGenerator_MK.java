package com.klst.iban;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

import com.klst.iban.Result.BankData;

public class BankDataGenerator_MK extends BankDataGenerator {

	private static final Logger LOG = Logger.getLogger(BankDataGenerator_MK.class.getName());
	
	BankDataGenerator_MK(String api_key) {
		super(api_key);
	}

	int bankCodeToId(int bankCode, int addIndex) {
		return bankCode*1000 + addIndex;
	}

	JSONObject updateJSONObject(JSONObject jo, BankData bankData, String key, int listIndex) {
		if(CITY.equals(key)) {
			String city = bankData.getCity();
			Object cityAlt = jo.get(key);
			if(!city.equals(cityAlt) && listIndex>0) {
				//LOG.warning("JSONObject:"+jo + " - diff in "+key+":"+city);
				if(jo.get(BRANCH)==null) {
					jo = updateJSONObject(jo, BRANCH, "branch "+cityAlt);
				}
			}
			jo = updateJSONObject(jo, ADDRESS, bankData.getAddress());
			jo = updateJSONObject(jo, ZIP, bankData.getZipString());
			jo = updateJSONObject(jo, CITY, bankData.getCity());
		}
		return jo;
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
		BankDataGenerator test = new BankDataGenerator_MK("testKey");

		test.tryWith("MK", BankDataGenerator.FORMAT_03d, 000, 999, "120000058984"); // MK07 250 120000058984
	}
}
