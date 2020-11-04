package com.klst.iban;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class BankDataGenerator_LU extends BankDataGenerator {

	private static final Logger LOG = Logger.getLogger(BankDataGenerator_LU.class.getName());
	
	static final String COUNTRY_CODE = "LU";

	BankDataGenerator_LU(String api_key) {
		super(api_key);
	}
	
	int bankCodeToId(int bankCode, int addIndex) {
		return bankCode*1000 + addIndex;
	}

	private File getFile(String filename) throws FileNotFoundException {
		LOG.info("filename:"+filename);
		File file = new File(filename);
		if(!file.exists()) {
			//LOG.warning("not existing file:"+file);
			throw new FileNotFoundException("Not existing file:"+file);
		}
		return file;
	}

	List<JSONObject> jsonToList(String filename) throws FileNotFoundException, IOException {
		List<JSONObject> result = null;
		File file = getFile(filename);
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        JSONParser jsonParser = new JSONParser(); // gibt es auch in jdk 1.8 nashorn.jar jdk.nashorn.internal.parser.JSONParser
        try {
        	Object o = jsonParser.parse(reader);
        	JSONObject jo = (JSONObject) o;
        	Object country = jo.get("country");
        	Object country_code = jo.get("country_code");
        	LOG.info("country_code:"+country_code + " country:"+country);
        	Object list = jo.get("list");
        	result = (JSONArray)list;
        	LOG.info("result List.size:"+result.size());
 		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			reader.close();
		}
       	return result;
	}

	// Aufbau einer Liste von bicBranches pro PRIMARY_OFFICE: key: PRIMARY_OFFICE, Liste der Branches (incl XXX)
	Map<String, List<JSONObject>> jsonMap(String filename) throws FileNotFoundException, IOException {
		//String jsonString = LocalFileProxy.loadFile(new File(filename));
		List<JSONObject> jList = jsonToList(filename);
		Hashtable<String, List<JSONObject>> result = new Hashtable<String, List<JSONObject>>();
    	jList.forEach(le -> {
    		JSONObject je = (JSONObject)le;
    		Object swift_code = je.get(Bank_Data.SWIFT_CODE);
    		BusinessIdentifierCode bic = new BusinessIdentifierCode(swift_code.toString());
        	String key = bic.bic8()+BusinessIdentifierCode.PRIMARY_OFFICE;
        	List<JSONObject> branchList = null;
        	if(result.containsKey(key)) {
        		result.get(key).add(je);
        	} else { // new branchList
        		branchList = new ArrayList<JSONObject>();
            	branchList.add(je);
            	result.put(key, branchList);
        	}
    	});
    	LOG.info("result Map.size:"+result.size());
    	return result;
	}

	public void tryWith(String countryCode, String format, int from, int to) {
		Map<String, List<JSONObject>> jMap;
		try {
			jMap = jsonMap("AllCountries/"+countryCode+".json");
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
    		FakeIban iban = new FakeIban(countryCode, bankCode);
			LOG.info("id="+id + " tryWith "+iban+" bankCode "+bankCode);
    		printBankDataViaApi(id, iban.toString(), jMap);
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
		test.tryWith(COUNTRY_CODE, BankDataGenerator.FORMAT_03d, 0, 999);
	}
}
