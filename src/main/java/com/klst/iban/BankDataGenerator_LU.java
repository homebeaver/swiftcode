package com.klst.iban;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

public class BankDataGenerator_LU extends BankDataGenerator {

	private static final Logger LOG = Logger.getLogger(BankDataGenerator_LU.class.getName());
	
	BankDataGenerator_LU(String api_key) {
		super(api_key);
	}
	
	int bankCodeToId(int bankCode, int addIndex) {
		return bankCode*1000 + addIndex;
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
//		jMap.forEach((k,v) -> {
//			LOG.info("key:"+k + " - #"+v.size()); // max: INFORMATION: key:CLAOLU2LXXX - #224
//		});
//		BusinessIdentifierCode bicObj = new BusinessIdentifierCode("BCEELULLXXX");
//		List<JSONObject> branchList = jMap.get("BCEELULLXXX");
//      LOG.info("BCEELULLXXX:" + ", branchList#="+(branchList==null ? "null" : branchList.size()));        
		for(int id=from; id<=to; id++) {
    		String bankCode = String.format(format, id);
    		String iban = countryCode + PP + bankCode + account;
//    		LOG.info("bankCode="+bankCode + " iban:"+iban);
    		printBankDataViaApi(id, iban, jMap);
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		BankDataGenerator test = new BankDataGenerator_LU("testKey");
//		test.jsonSort(JSON_DIR+"LU"+JSON_EXT);
		
//		Map<String, List<JSONObject>> jMap = test.jsonMap(JSON_DIR+"LU"+JSON_EXT);
//		LOG.info("jMap.size():"+jMap.size());
//		jMap.forEach((k,v) -> {
//			LOG.info("key:"+k + " - #"+v.size()); // max: INFORMATION: key:CLAOLU2LXXX - #224
//		});

/* "BSUILULLXXX" hat viele branch'es:

{"bank":"CACEIS BANK, LUXEMBOURG BRANCH","city":"LUXEMBOURG","swift_code":"BSUILULLADM","id":68,"branch":"(FUND ADMINISTRATION)"},
{"bank":"CACEIS BANK, LUXEMBOURG BRANCH","city":"LUXEMBOURG","swift_code":"BSUILULLCNA","id":69,"branch":"(CACEIS NORTH AMERICA)"},
{"bank":"CACEIS BANK, LUXEMBOURG BRANCH","city":"LUXEMBOURG","swift_code":"BSUILULLCOR","id":70,"branch":"(COMET ORDER)"},
{"bank":"CACEIS BANK, LUXEMBOURG BRANCH","city":"LUXEMBOURG","swift_code":"BSUILULLCUS","id":71,"branch":"(CUSTODIAN BANK)"},
{"bank":"CACEIS BANK, LUXEMBOURG BRANCH","city":"LUXEMBOURG","swift_code":"BSUILULLDUB","id":598,"branch":"(CACEIS DUBLIN)"},
{"bank":"CACEIS BANK, LUXEMBOURG BRANCH","city":"LUXEMBOURG","swift_code":"BSUILULLETC","id":625,"branch":"(EXECUTION TO CUSTODY)"},
{"bank":"CACEIS BANK, LUXEMBOURG BRANCH","city":"LUXEMBOURG","swift_code":"BSUILULLFND","id":72,"branch":"(REGISTRAR)"},
{"bank":"CACEIS BANK, LUXEMBOURG BRANCH","city":"LUXEMBOURG","swift_code":"BSUILULLFOF","id":73,"branch":"(FUNDS OF FUNDS)"},
{"bank":"CACEIS BANK, LUXEMBOURG BRANCH","city":"LUXEMBOURG","swift_code":"BSUILULLFTM","id":74,"branch":"(FUNDS RECONCILIATION)"},
{"bank":"CACEIS BANK, LUXEMBOURG BRANCH","city":"LUXEMBOURG","swift_code":"BSUILULLPRF","id":75,"branch":"(FUND PROCESSING)"},
{"bank":"CACEIS BANK, LUXEMBOURG BRANCH","city":"LUXEMBOURG","swift_code":"BSUILULLREG","id":76,"branch":"(SERVICE REGISTRE)"},
 - nur PRIMARY_OFFICE wird von API geliefert:
{"bank":"CACEIS BANK, LUXEMBOURG BRANCH","city":"LUXEMBOURG","swift_code":"BSUILULLXXX","id":77,"branch":null},

Lösungsidee:
 - Aufbau einer Liste von bicBranches pro PRIMARY_OFFICE: key: PRIMARY_OFFICE, Liste der Branches (incl XXX)
 - generieren mit tryWith: pro treffer in bicBranches nachschlagen (1: BankData ausgeben, n>1: pro branch BankData, n=0: bicBranches mit einem el aufbauen)
 */
		test.tryWith("LU", BankDataGenerator.FORMAT_03d, 0, 999, "9400644750000"); // LU28 001 9400644750000
	}
}
