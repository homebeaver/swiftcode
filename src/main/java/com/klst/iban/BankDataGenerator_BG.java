package com.klst.iban;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

import com.klst.ibanTest.API_Key_Provider;

/*

BIC	        Institution name	    City	Country
UNCRBGSF	UNICREDIT BULBANK AD	SOFIA	BULGARIA
UNCRBGSF031	UNICREDIT BULBANK AD	SOFIA	BULGARIA
UNCRBGSF081	UNICREDIT BULBANK AD	V. TARNOVO	BULGARIA
UNCRBGSF426	UNICREDIT BULBANK AD	SOFIA	BULGARIA
UNCRBGSF720	UNICREDIT BULBANK AD	PLOVDIV	BULGARIA
UNCRBGSF733	UNICREDIT BULBANK AD	VARNA	BULGARIA

https://en.wikipedia.org/wiki/List_of_banks_in_Bulgaria
SWIFT (BIC) 
UNCRBGSF 
STSABGSF - 2 gefunden 
UBBSBGSF 
BPBIBGSF 
FINVBGSF 
RZBBBGSF 
CECBBGSF 
NASBBGSF 
BUINBGSF 
PRCBBGSF 
SOMBBGSF 
IORTBGSF 
BGUSBGSF 
IABGBGSF 
CITIBGSF 
INGBBGSF 
DEMIBGSF 
BNPABGSX 
TBIBBGSF 
BNPABGSX - duplicate 
CREXBGSF 
TEXIBGSF 
TCZBBGSF 

{"id":7000,"swift_code":"UNCRBGSFXXX","bank_code":7000,"bank":"UNICREDIT BULBANK AD - CENTRALIZED SYSTEM BRANCH","zip":"1000","city":" SOFIA","address":"7 SVETA NEDELYA SQUARE","support_codes":9},

INFORMATION: bId:8300, BankData:[CountryIso:null, Bic:STSABGSFXXX, BankCode:STSA, BranchCode:null, Branch:"", Name:"DSK Bank EAD", Address:"", BankSupports:1, Zip:"", City:""]
{"id":8300,"swift_code":"STSABGSFXXX","bank_code":8300,"bank":"DSK Bank EAD","support_codes":1},
INFORMATION: bId:9300, BankData:[CountryIso:null, Bic:STSABGSFXXX, BankCode:STSA, BranchCode:null, Branch:"", Name:"DSK BANK EAD", Address:"19 MOSKOVSKA STREET", BankSupports:1, Zip:"1040", City:" SOFIA"]
{"id":9300,"swift_code":"STSABGSFXXX","bank_code":9300,"bank":"DSK BANK EAD","zip":"1040","city":" SOFIA","address":"19 MOSKOVSKA STREET","support_codes":1},

{"id":9200,"swift_code":"UBBSBGSFXXX","bank_code":9200,"bank":"UNITED BULGARIAN BANK AD","zip":"1040","city":" SOFIA","address":"UNITED BULGARIAN BANK 5 SVETA SOFIA STREET","support_codes":1},

 */

public class BankDataGenerator_BG extends BankDataGenerator {

	private static final Logger LOG = Logger.getLogger(BankDataGenerator_BG.class.getName());
	
	Map<BusinessIdentifierCode, ArrayList<Object>> bankByBic = new Hashtable<BusinessIdentifierCode, ArrayList<Object>>();

	BankDataGenerator_BG(String api_key) {
		super(api_key);
		bankByBic.put(new BusinessIdentifierCode("UNCRBGSF"), new ArrayList<Object>());
		bankByBic.put(new BusinessIdentifierCode("STSABGSF"), new ArrayList<Object>());
		bankByBic.put(new BusinessIdentifierCode("UBBSBGSF"), new ArrayList<Object>());
		bankByBic.put(new BusinessIdentifierCode("BPBIBGSF"), new ArrayList<Object>());
		bankByBic.put(new BusinessIdentifierCode("FINVBGSF"), new ArrayList<Object>());
		bankByBic.put(new BusinessIdentifierCode("RZBBBGSF"), new ArrayList<Object>());
		bankByBic.put(new BusinessIdentifierCode("CECBBGSF"), new ArrayList<Object>());
		bankByBic.put(new BusinessIdentifierCode("NASBBGSF"), new ArrayList<Object>());
		bankByBic.put(new BusinessIdentifierCode("BUINBGSF"), new ArrayList<Object>());
		bankByBic.put(new BusinessIdentifierCode("PRCBBGSF"), new ArrayList<Object>());
		bankByBic.put(new BusinessIdentifierCode("SOMBBGSF"), new ArrayList<Object>());
		bankByBic.put(new BusinessIdentifierCode("IORTBGSF"), new ArrayList<Object>());
		bankByBic.put(new BusinessIdentifierCode("IABGBGSF"), new ArrayList<Object>());
		bankByBic.put(new BusinessIdentifierCode("BGUSBGSF"), new ArrayList<Object>());
		bankByBic.put(new BusinessIdentifierCode("DEMIBGSF"), new ArrayList<Object>());
		bankByBic.put(new BusinessIdentifierCode("CITIBGSF"), new ArrayList<Object>());
		bankByBic.put(new BusinessIdentifierCode("INGBBGSF"), new ArrayList<Object>());
		bankByBic.put(new BusinessIdentifierCode("BNPABGSX"), new ArrayList<Object>());
		bankByBic.put(new BusinessIdentifierCode("TBIBBGSF"), new ArrayList<Object>());
//		bankByBic.put(new BusinessIdentifierCode("TTBBBG22"), new ArrayList<Object>()); // no longer exists
		bankByBic.put(new BusinessIdentifierCode("CREXBGSF"), new ArrayList<Object>());
		bankByBic.put(new BusinessIdentifierCode("TEXIBGSF"), new ArrayList<Object>());
		bankByBic.put(new BusinessIdentifierCode("TCZBBGSF"), new ArrayList<Object>());
	}
	
	JSONObject updateJSONObjectXXX(JSONObject jo, String key, Object value) {
		if(BANK.equals(key)) {
			LOG.info(key + ":: iban:"+value);
			return super.updateJSONObject(jo, BANK, value);
		}
		return super.updateJSONObject(jo, key, value);
	}
	
	void tryWith(String countryCode, String format, int from, int to, String account) {
		List<BusinessIdentifierCode> bicList = new ArrayList<BusinessIdentifierCode>(bankByBic.keySet());
		
//		outerloop:
		for(int id=from; id<=to; id++) {
			int bankId = id; //*100;
			String bankNo = String.format(format, bankId);
			if(id%100==0) {
				int perCent = 100*id/to;
				System.out.println(","+id + " %:"+ perCent);
			}
			ListIterator<BusinessIdentifierCode> iter = bicList.listIterator();
			while(iter.hasNext()){
				BusinessIdentifierCode bic = iter.next();
				String bankCode = bic.getBankCode();
				String iban = countryCode + PP + bankCode + bankNo + account;
				Hashtable<String, List<JSONObject>> jMap = new Hashtable<String, List<JSONObject>>(); // leer
				boolean found = printBankDataViaApi(bankId, iban, jMap);
				if(found) {
					//LOG.info("gefunden :: iban:"+iban);
					break; // inner
//					break outerloop;
				}
			}
			
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		BankDataGenerator test = new BankDataGenerator_BG(API_Key_Provider.API_KEY);

		test.tryWith("BG", BankDataGenerator.FORMAT_04d, 0000, 9999, "1020345678"); // BG80 BNBG 9661 1020345678
		
	}
}
