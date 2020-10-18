package com.klst.iban;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.klst.iban.Result.BankData;

/*

bic Bsp AZ.json hat folgende Felder:
            "id": 1,
            "bank": "ACCESSBANK",
            "city": "BAKU",
            "branch": null,
            "swift_code": "ACABAZ22"
swift_code == bank_code + Ländercode + Ortscode + (optional)branch=/=branch_code

forAll json's:
	generatedAccount = 00000000137010001944
	pp = 99 // muss nicht korrekt sein
	try IbanApi AZ+pp+bank_code+generatedAccount
		get bic // aka swift_code
		get bank aka name
		get bank_code, id aus bank_code A=0,Z=27
		get sepa.getBankSupports()
		get address, city, zip // aka location
		get phone, fax, email // aka contact
{"bank_data":{"bic":"NABZAZ2XXXX","branch":null,"bank":"Central Bank of the Republic of Azerbaijan"
             ,"address":"AZ1014 R.Behbudov Str.32"
             ,"city":"Baku","state":null,"zip":""
             ,"phone":"493-11-22","fax":null,"www":null,"email":null
             ,"account":"00000000137010001944","bank_code":"NABZ","branch_code":""}
branch_code ist nicht der (optional)bic.branch
 */
public class BankDataGenerator extends IbanToBankData {

	private static final Logger LOG = Logger.getLogger(BankDataGenerator.class.getName());

	final static String PP = "99";

	static Integer getBankCode(Object v) {
		if(v==null) {
			return null;
		} else if(v.getClass()==String.class) {
			try {
				return Integer.parseInt((String) v);
			} catch(NumberFormatException e) {
				return null;
			}
		} else if(v.getClass()==Double.class) {
			return ((Double) v).intValue();
		} else {
	        LOG.severe("v.getClass() " + v.getClass());
	        return null;
		}
	}

	BankDataGenerator(String api_key) {
		super(api_key);
	}

	void parseValidationObject(String iban, JSONObject validation, boolean verbose) {
//    	LOG.info("validations for iban "+iban); 
    	parseValidationObject(validation);		
	}
	
    static final String BRANCH_CODE_IN_IBAN = "BRANCH_CODE_IN_IBAN";
	JSONObject updateJSONObject(JSONObject jo, String key, Object value) {
		//LOG.info("key:"+key + " old/new: "+jo.get(key)+"/"+value);	
		if(OPTIONAL_KEYS.contains(key) && value==null) {
			// nix tun
		} else if(SUPPORT_CODES.equals(key) && value.hashCode()==0) {
			// wie null
		} else if(BRANCH_CODE_IN_IBAN.equals(key)) {
			// dies ist ein exit zum patchen von BRANCH_CODE, der aus IBAN gewonnen werden kann, siehe MC
			// hier nix tun
		} else { // MANDATORY_KEYS || value!=null
			jo.put(key, value);
		}
		return jo;
	}

	JSONObject updateJSONObject(JSONObject jo, BankData bankData, String key, int listIndex) {
		if(CITY.equals(key)) {
			String city = bankData.getCity();
			Object cityAlt = jo.get(key);
			if(!city.equals(cityAlt) && listIndex>0) {
				LOG.warning("JSONObject:"+jo + " - diff in "+key+":"+city);
			}
			jo = updateJSONObject(jo, ADDRESS, bankData.getAddress());
			jo = updateJSONObject(jo, ZIP, bankData.getZipString());
			jo = updateJSONObject(jo, CITY, bankData.getCity());
		}
		return jo;
	}
		
	List<JSONObject> getBranchList(String bic, Map<String, List<JSONObject>> jMap) {
		List<JSONObject> branchList = jMap.get(bic);
        if(branchList==null) { // id:16, bic:COBALULXXXX, branchList#=null, bankName:BANK JULIUS BAER LUXEMBOURG S.A.
    		branchList = new ArrayList<JSONObject>();
    		JSONObject le = new JSONObject();
    		updateJSONObject(le, SWIFT_CODE, bic);
        	branchList.add(le);        	
        }
		return branchList;
	}
	
	boolean printBankDataViaApi(int bId, String iban, Map<String, List<JSONObject>> jMap) {
    	BankData bankData =	super.retrieveBankData(iban);
    	if(bankData==null) return false;
        String bankName = bankData.getBank();
        if(bankName==null) { // not found 
        	return false;
        }
        String bic = bankData.getBic();
        if(bic==null || bic.isEmpty()) {
        	return false;
        }
        
        Object branch = bankData.getBranch();
        String city = bankData.getCity();
        
        List<JSONObject> branchList = jMap.get(bic);
//        LOG.info("bId:"+bId + ", BankData:"+bankData);
//        LOG.info("bId:"+bId + ", bic:"+bic + ", branchList#="+(branchList==null ? "null" : branchList.size()) + ", bankName:"+bankName);
        branchList = getBranchList(bic, jMap);
        
		for(int i=0; i<branchList.size(); i++) {
			JSONObject jo = branchList.get(i);			
			int listIndex = i+1;
			String swiftCode = (String)jo.get(SWIFT_CODE);
			if(swiftCode.length()==8) swiftCode = swiftCode.concat(BusinessIdentifierCode.PRIMARY_OFFICE);
			if(bic.equals(swiftCode)) {
				listIndex = 0; // LU: bei XXX id*1000, sonst id*1000 +i+1
			}
			jo = updateJSONObject(jo, ID, bankCodeToId(bId, listIndex));
			jo = updateJSONObject(jo, SWIFT_CODE, swiftCode);
			jo = updateJSONObject(jo, BANK_CODE, bId);
			jo = updateJSONObject(jo, BANK, bankName);
			if(branch==null) { 
				// branch aus le belassen
			} else {
				Object branchAlt = jo.get(BRANCH);
            	if(!branch.toString().equals(branchAlt) && branchAlt!=null) {
            		LOG.warning("le:"+jo + ", le.branch ANDERS branch:"+branch);
            	}
				jo = updateJSONObject(jo, BRANCH, branch);
			}
			if(city==null) {
				// city aus le belassen
			} else {
				updateJSONObject(jo, bankData, CITY, listIndex);
			}
			// optional:
			jo = updateJSONObject(jo, BRANCH_CODE, bankData.getBranchCode());
			jo = updateJSONObject(jo, SUPPORT_CODES, bankData.getBankSupports());
			jo = updateJSONObject(jo, PHONE, bankData.getPhone());
			jo = updateJSONObject(jo, FAX, bankData.getFax());
			jo = updateJSONObject(jo, WWW, bankData.getWww());
			jo = updateJSONObject(jo, EMAIL, bankData.getEmail());
			jo = updateJSONObject(jo, BRANCH_CODE_IN_IBAN, iban);
			//System.out.println(jo.toString() + ","); // toString == public static String toJSONString(Map map)
			System.out.println(toOrderedJSONString(jo) + ",");
		}
		return true;
	}
	
    // ordering:
	final static List<String> MANDATORY_KEYS = Arrays.asList(ID, SWIFT_CODE, BANK_CODE, BANK);
	final static List<String> OPTIONAL_KEYS = Arrays.asList(BRANCH_CODE, BRANCH
			, STATE, ZIP, CITY, ADDRESS // location
			, PHONE, FAX, WWW, EMAIL	// contact
			, SUPPORT_CODES);
	
	public static String toOrderedJSONString(Map map){
		if(map == null)
			return "null";
		
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        sb.append('{');
        Iterator iter=MANDATORY_KEYS.iterator();
        while(iter.hasNext()){
        	if(first)
        		first = false;
        	else
        		sb.append(',');
        	
        	Object key = iter.next();
        	Object value = map.get(key);
        	toJSONString(String.valueOf(key),value, sb);
        }
        iter=OPTIONAL_KEYS.iterator();
        while(iter.hasNext()){
        	Object key = iter.next();
        	Object value = map.get(key);
        	if(value!=null && !value.toString().isEmpty()) {
            	if(first)
            		first = false;
            	else
            		sb.append(',');
        		toJSONString(String.valueOf(key),value, sb);
        	}
        }
        sb.append('}');
		return sb.toString();
	}
	public static String toSortedJSONString(Map map){
		if(map == null)
			return "null";
		
        StringBuffer sb = new StringBuffer();
        boolean first = true;
		Set keySet = map.keySet();
		List keyList = (List) keySet.stream().sorted().collect(Collectors.toList());
		Iterator iter=keyList.iterator();
		
        sb.append('{');
        // key alphabetisch:
        while(iter.hasNext()){
        	if(first)
        		first = false;
        	else
        		sb.append(',');
        	
        	Object key = iter.next();
        	Object value = map.get(key);
        	toJSONString(String.valueOf(key),value, sb);
        }
        sb.append('}');
		return sb.toString();
	}
	private static String toJSONString(String key,Object value, StringBuffer sb){
		sb.append('\"');
        if(key == null)
            sb.append("null");
        else
            escape(key, sb);
		sb.append('\"').append(':');
		
		sb.append(JSONValue.toJSONString(value));
		
		return sb.toString();
	}
    static void escape(String s, StringBuffer sb) {
		for(int i=0;i<s.length();i++){
			char ch=s.charAt(i);
			switch(ch){
			case '"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '/':
				sb.append("\\/");
				break;
			default:
                //Reference: http://www.unicode.org/versions/Unicode5.1.0/
				if((ch>='\u0000' && ch<='\u001F') || (ch>='\u007F' && ch<='\u009F') || (ch>='\u2000' && ch<='\u20FF')){
					String ss=Integer.toHexString(ch);
					sb.append("\\u");
					for(int k=0;k<4-ss.length();k++){
						sb.append('0');
					}
					sb.append(ss.toUpperCase());
				}
				else{
					sb.append(ch);
				}
			}
		}//for
	}

	void getBankDataViaApi(int id, String iban, Object branchCode) {
		getBankDataViaApi(id, iban, branchCode, null, null, null);
	}

	/* ermittelt BankData per iban-checker iban.com api und gibt das Ergebnis aus
	 * 
	 * @param id - (nicht immer) eindeutige Id für die Institution/Bank
	 * @param iban - generierte nicht valide IBAN, countryCode+PP+BBAN
	 * @param branchCode - der branchCode aus der BBAN (das ist nicht bic.branchCode!)
	 * 
	 * @param swift_code (optional): bic aka swift_code liefert die iban-api, allerdings immer die PRIMARY_OFFICE
	 *        dadurch werden keine IBANs für "swift_code": "UNCRBGSF720" , "(PLOVDIV BRANCH)"gefunden
	 * @param bank (optional): alternativer bankName wenn vorhanden 
	 * @param branchName (optional): alternativer Name wenn vorhanden 
	 * TODO @param cityName (optional): alternativer Name wenn vorhanden 
	 */
	void getBankDataViaApi(int id, String iban, Object branchCode, BusinessIdentifierCode swift_code, String bank, String branchName) {
    	BankData bankData =	super.retrieveBankData(iban);
    	if(bankData==null) return;

        StringBuffer sb = new StringBuffer();
        
        String bankName = bankData.getBank();
        if(bankName==null) { // not found 
        	if(bank==null) return;
        	bankName = bank;      	
        }
//    	bankName = bank; // wg. BE encoding   	
        
        String bic = bankData.getBic();
        if(bic==null) {
        	if(swift_code==null) return;
        	bic = swift_code.toString(); // wg. NL ?
        }
//LOG.info("bic:"+bic);        
        sb.append("{\"id\": ").append(id);
        
		sb.append(", \"swift_code\": ");
        if(bic==null) {
			sb.append(bic);	        	
        } else {
        	if(bic.endsWith("XXX")) {
        		if(branchCode instanceof String) {
        			String branchCodeS = (String)branchCode;
        			if(branchCodeS.length()==3) {
        				sb.append("\"").append(bic.substring(0, 8)).append(branchCode).append("\"");
        	        } else {
            			sb.append("\"").append(bic).append("\"");
        	        }
       			} else {
       				// branchCode kann auch int sein, z.B. für BG
       				sb.append("\"").append(bic).append("\"");
        		}
	        } else {
    			sb.append("\"").append(bic).append("\"");
        	}
//	        if(bic.endsWith("XXX") && branchCode.length()==3) {
//    			sb.append("\"").append(bic.substring(0, 8)).append(branchCode).append("\"");
//	        } else {
//    			sb.append("\"").append(bic).append("\"");
//	        }
        }
		sb.append(", \"bank_code\": ");
		if(bankData.getBankCode()==0) {
			sb.append("\"").append(bankData.getBankIdentifier()).append("\"");
		} else {
			sb.append(bankData.getBankCode());
		}
		sb.append(", \"branch_code\": ");
		if(bankData.getBranchCode()==null) {
			if(branchCode instanceof String) {
				sb.append(bankData.getBranchCode());
			} else {
				sb.append(branchCode);
			}			
		} else {
			sb.append("\"").append(bankData.getBranchCode()).append("\"");
		}
		sb.append(", \"branch\": ");
		if(bankData.getBranch()==null) {
			sb.append(bankData.getBranch());
		} else {
			sb.append("\"").append(bankData.getBranch()).append("\"");
		}
		if(bankName!=null) {
			sb.append(", \"bank\": ");
			sb.append("\"").append(bankName).append("\"");
		}
		sb.append(", \"address\": ");
		if(bankData.getAddress()==null) {
			sb.append(bankData.getAddress());
		} else {
			sb.append("\"").append(bankData.getAddress()).append("\"");
		}
		if(bankData.getZipString()!=null && !bankData.getZipString().isEmpty()) {
			sb.append(", \"zip\": ");
			sb.append("\"").append(bankData.getZipString()).append("\"");
		}
		sb.append(", \"city\": ");
		if(bankData.getCity()==null) {
			sb.append(bankData.getCity());
		} else {
			sb.append("\"").append(bankData.getCity()).append("\"");
		}
		
		// optional:
		if(bankData.getBankSupports()>0) {
    		sb.append(", \"support_codes\": ");
			sb.append(bankData.getBankSupports());
		}
		if(bankData.getPhone()!=null && !bankData.getPhone().toString().isEmpty()) {
    		sb.append(", \"phone\": ");
			sb.append("\"").append(bankData.getPhone()).append("\"");
		}
		if(bankData.getFax()!=null && !bankData.getFax().toString().isEmpty()) {
    		sb.append(", \"fax\": ");
			sb.append("\"").append(bankData.getFax()).append("\"");
		}
		if(bankData.getWww()!=null && !bankData.getWww().toString().isEmpty()) {
    		sb.append(", \"www\": ");
			sb.append("\"").append(bankData.getWww()).append("\"");
		}
		if(bankData.getEmail()!=null && !bankData.getEmail().toString().isEmpty()) {
    		sb.append(", \"email\": ");
			sb.append("\"").append(bankData.getEmail()).append("\"");
		}
		sb.append ("},");
		System.out.println(sb.toString());
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
        JSONParser jsonParser = new JSONParser();
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
		List<JSONObject> jList = jsonToList(filename);
		Hashtable<String, List<JSONObject>> result = new Hashtable<String, List<JSONObject>>();
    	jList.forEach(le -> {
    		JSONObject je = (JSONObject)le;
    		Object swift_code = je.get(SWIFT_CODE);
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
	
	// sortiert das JSONArray aus file filename nach swift_code
	void jsonSort(String filename) throws FileNotFoundException, IOException {
		jsonSort(filename, false);
	}
	void jsonSort(String filename, boolean verbose) throws FileNotFoundException, IOException {
		List<JSONObject> jList = jsonToList(filename);
		jList.sort((m1, m2) -> {
			String k1 = (String)m1.get(SWIFT_CODE);
			String k2 = (String)m2.get(SWIFT_CODE);
			return k1.compareTo(k2);
		});
//		int passive = 0;
    	jList.forEach(le -> {
    		JSONObject je = (JSONObject)le;
    		Object id = je.get(ID);
    		Object swift_code = je.get(SWIFT_CODE);
    		// create IBAN and use IBANApi
    		BusinessIdentifierCode bic = new BusinessIdentifierCode(swift_code.toString());
    		String bankCode = bic.getBankCode();
//    		String country_code = bic.getCountryCode();
    		String isValid = bic.isValid() ? ", isValid" : ", INVALID";
    		String isPassive = bic.isPassive() ? ", isPassive" : ", isActive";
    		
    		int bcId = bic.bankCodeToId();
        	if(bic.isPassive()) {
        		if(verbose) LOG.info("id="+id + " bic:"+bic + isValid + isPassive + " bcId="+bcId + " je:"+je.toJSONString());
//        		passive++; // must be final
        	} else {
        		System.out.println(je.toJSONString() + ",");
        	}
    	});	
	}
	
	void jsonToList(String filename, String account) throws FileNotFoundException, IOException {
		List<JSONObject> jList = jsonToList(filename);
    	jList.forEach(le -> {
    		JSONObject je = (JSONObject)le;
    		Object id = je.get(ID);
    		Object swift_code = je.get(SWIFT_CODE);
    		Object bank = je.get(BANK);
    		Object city = je.get(CITY);
    		Object branch = je.get(BRANCH);
    		// create IBAN and use IBANApi
    		BusinessIdentifierCode bic = new BusinessIdentifierCode(swift_code.toString());
    		String bankCode = bic.getBankCode();
    		String country_code = bic.getCountryCode();
    		String isValid = bic.isValid() ? ", isValid" : ", INVALID";
    		String isPassive = bic.isPassive() ? ", isPassive" : ", isActive";
    		
    		int bcId = bic.bankCodeToId();
    		String iban = country_code.toString() + PP + bankCode + account;
        	LOG.info("id="+id + " bic:"+bic + isValid + isPassive + " bcId="+bcId + " branch:"+branch + " je:"+je.toJSONString());
        	
        	//getBankDataViaApi(id, iban, null);
    	});
	}
	
	private void jsonNLList(String filename, String account) throws FileNotFoundException, IOException {
		LOG.info("filename:"+filename);
		File file = new File(filename);
		if(!file.exists()) {
			LOG.warning("not existing file:"+file);
			return;
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        JSONParser jsonParser = new JSONParser();
        try {
        	Object o = jsonParser.parse(reader);
        	JSONObject jo = (JSONObject) o;
        	Object country = jo.get("country");
        	Object country_code = jo.get("country_code");
        	LOG.info("country_code:"+country_code + " country:"+country);
        	Object list = jo.get("list");
        	List<JSONObject> jList = (JSONArray)list;
        	LOG.info("jList.size:"+jList.size());
        	jList.forEach(le -> {
        		JSONObject je = (JSONObject)le;
        		String bank = je.get(BANK).toString();
        		Object swift_code = je.get(SWIFT_CODE);
        		// create IBAN and use IBANApi
        		BusinessIdentifierCode bic = new BusinessIdentifierCode(swift_code.toString());
        		String bankCode = bic.getBankCode();
        		int id = bic.bankCodeToId();
        		String iban = country_code.toString() + PP + bankCode + account;
//            	LOG.info("id="+id + " swift_code:"+swift_code + " iban:"+iban);
            	getBankDataViaApi(id, iban, bic.getBranchCode(), bic, bank, null);
        	});
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			reader.close();
		}
//        Object o = jsonParser.parse(response.toString());
//        JSONObject jo = (JSONObject) o;
	}

	private void jsonBEList(String filename, String account) throws FileNotFoundException, IOException {
		LOG.info("filename:"+filename);
		File file = new File(filename);
		if(!file.exists()) {
			LOG.warning("not existing file:"+file);
			return;
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        JSONParser jsonParser = new JSONParser();
        try {
        	Object o = jsonParser.parse(reader);
        	JSONObject jo = (JSONObject) o;
        	Object country = jo.get("country");
        	Object country_code = jo.get("country_code");
        	LOG.info("country_code:"+country_code + " country:"+country);
        	Object list = jo.get("list");
        	List<JSONObject> jList = (JSONArray)list;
        	LOG.info("jList.size:"+jList.size());
        	jList.forEach(le -> {
        		JSONObject je = (JSONObject)le;
        		String bank = je.get(BANK).toString();
        		Object swift_code = je.get(SWIFT_CODE);
        		// create IBAN and use IBANApi
        		BusinessIdentifierCode bic = new BusinessIdentifierCode(swift_code.toString());
        		String bankCode = bic.getBankCode();
        		String iban = country_code.toString() + PP + bankCode + account;
        		int id = Integer.parseInt(bankCode);
//            	LOG.info("id="+id + " swift_code:"+swift_code + " iban:"+iban);
            	getBankDataViaApi(id, iban, bic.getBranchCode(), bic, bank, null);
        	});
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			reader.close();
		}
//        Object o = jsonParser.parse(response.toString());
//        JSONObject jo = (JSONObject) o;
	}

	final static String FORMAT_02d = "%02d";
	final static String FORMAT_03d = "%03d";
	final static String FORMAT_04d = "%04d";
	final static String FORMAT_05d = "%05d";
	final static String FORMAT_06d = "%06d";
	void tryWith(String countryCode, String format, int from, int to, String account) {
		for(int id=from; id<=to; id++) {
    		String bankCode = String.format(format, id);
    		String iban = countryCode + PP + bankCode + account;
//    		LOG.info("bankCode="+bankCode + " iban:"+iban);
        	getBankDataViaApi(id, iban, "");
		}
		
	}
	
	int bankCodeToId(int bankCode, int addIndex) {
		return bankCode;
	}

	final static String JSON_EXT = ".json";
	final static String JSON_DIR = "AllCountries/";

	public static void main(String[] args) throws Exception {
		BankDataGenerator test = new BankDataGenerator("testKey");
//		test.jsonToList(JSON_DIR+"AZ"+JSON_EXT, "00000000137010001944");
//		test.jsonToList(JSON_DIR+"BG"+JSON_EXT, "96611020345678"); // +BranchCode TODO
//		test.jsonToList(JSON_DIR+"BH"+JSON_EXT, "00001299123456");
//		test.jsonToList(JSON_DIR+"BY"+JSON_EXT, "3600900000002Z00AB00"); // +BranchCode
//		test.jsonToList(JSON_DIR+"DO"+JSON_EXT, "00000001212453611324");
//		test.jsonToList(JSON_DIR+"GI"+JSON_EXT, "000000007099453");
//		test.jsonToList(JSON_DIR+"GT"+JSON_EXT, "01020000001210029690");
//		test.jsonToList(JSON_DIR+"IQ"+JSON_EXT, "850123456789012"); // +BranchCode TODO
		
//		test.jsonToList(JSON_DIR+"KW"+JSON_EXT, "0000000000001234560101");
//		test.jsonToList(JSON_DIR+"LC"+JSON_EXT, "000100010012001200023015");
//		test.jsonToList(JSON_DIR+"LV"+JSON_EXT, "0000435195001");
//		test.jsonToList(JSON_DIR+"NL"+JSON_EXT, "0417164300");
//		test.jsonNLList(JSON_DIR+"NL"+JSON_EXT, "0417164300");		
//		test.jsonToList(JSON_DIR+"PK"+JSON_EXT, "0000001123456702");
//		test.jsonToList(JSON_DIR+"PS"+JSON_EXT, "000000000400123456702");
//		test.jsonToList(JSON_DIR+"QA"+JSON_EXT, "00001234567890ABCDEFG");
//		test.jsonToList(JSON_DIR+"RO"+JSON_EXT, "1B31007593840000");
//		test.jsonToList(JSON_DIR+"SC"+JSON_EXT, "11010000000000001497USD");
//		test.jsonToList(JSON_DIR+"SV"+JSON_EXT, "00000000000000700025");
//		test.jsonToList(JSON_DIR+"VG"+JSON_EXT, "0000012345678901");

//		test.jsonBEList(JSON_DIR+"BE"+JSON_EXT, "123456789");	// BE94 049 123456789
		
//		test.tryWith("AE", 0, 999, "1234567890123456"); // AE07 033 1234567890123456 TODO
		// das folgende liefert immer XXX-branch, 
		// BOMLAEADCSU, BOMLAEADEBC, BOMLAEADFTC, BOMLAEADHDO, BOMLAEADRAM, BOMLAEADTID, BOMLAEADGTS werden nicht gefunden
		// test.tryWith("AE", 30, 35, "1234567890123456"); // AE07 033 1234567890123456
		
		// HABAEE2XHAM wird nicht gefunden, "id": 22 - wie HABAEE2XXXX
		// es gibt noch EPBEEE2X mit 4x branch
//		test.tryWith("EE", FORMAT_02d, 0, 99, "00221020145685"); // EE38 22 00221020145685
		
		// "id": 7, "swift_code": "BSUILULLXXX" hat viele branch'es
		test.tryWith("LU", FORMAT_03d, 0, 10, "9400644750000"); // LU28 001 9400644750000
		
//		LOG.info("Id of AAAA/00 is "+test.bankCodeToId("AAAA"));
//		LOG.info("Id of BAAA/01 is "+test.bankCodeToId("BAAA"));
//		LOG.info("Id of CAAA/02 is "+test.bankCodeToId("CAAA"));
//		LOG.info("Id of DAAA/03 is "+test.bankCodeToId("DAAA"));
//		LOG.info("Id of EAAA/04 is "+test.bankCodeToId("EAAA"));
//		LOG.info("Id of FAAA/05 is "+test.bankCodeToId("FAAA"));
//		LOG.info("Id of AAAA is "+test.bankCodeToId("GAAA"));
//		LOG.info("Id of AAAA is "+test.bankCodeToId("HAAA"));
//		LOG.info("Id of AAAA is "+test.bankCodeToId("IAAA"));
//		LOG.info("Id of AAAA is "+test.bankCodeToId("JAAA"));
//		LOG.info("Id of AAAA is "+test.bankCodeToId("KAAA"));
//		LOG.info("Id of AAAA is "+test.bankCodeToId("LAAA"));
//		LOG.info("Id of AAAA is "+test.bankCodeToId("MAAA"));
//		LOG.info("Id of AAAA is "+test.bankCodeToId("NAAA"));
//		LOG.info("Id of AAAA is "+test.bankCodeToId("OAAA"));
//		LOG.info("Id of PAAA/15 is "+test.bankCodeToId("PAAA"));
//		LOG.info("Id of AAAA is "+test.bankCodeToId("QAAA"));
//		LOG.info("Id of AAAA is "+test.bankCodeToId("RAAA"));
//		LOG.info("Id of AAAA is "+test.bankCodeToId("SAAA"));
//		LOG.info("Id of AAAA is "+test.bankCodeToId("TAAA"));
//		LOG.info("Id of AAAA is "+test.bankCodeToId("UAAA"));
//		LOG.info("Id of AAAA is "+test.bankCodeToId("VAAA"));
//		LOG.info("Id of AAAA is "+test.bankCodeToId("WAAA"));
//		LOG.info("Id of AAAA is "+test.bankCodeToId("XAAA"));
//		LOG.info("Id of AAAA is "+test.bankCodeToId("YAAA"));
//		LOG.info("Id of ZAAA/25 is "+test.bankCodeToId("ZAAA"));
//		LOG.info("Id of ABAA/26 is "+test.bankCodeToId("ABAA"));
//		//LOG.info("Id of ABAA is "+test.bankCodeToId("AB")); // exception
	}

}
