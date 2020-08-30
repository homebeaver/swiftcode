package com.klst.iban;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.klst.iban.Result.BankData;

/*

bic aka AZ.json hat folgende Felder:
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
		get bic
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
 */
public class BankDataGenerator extends IbanToBankData {

	private static final Logger LOG = Logger.getLogger(BankDataGenerator.class.getName());

	private final static String PP = "99";

	BankDataGenerator(String api_key) {
		super(api_key);
	}

	void parseValidationObject(String iban, JSONObject validation, boolean verbose) {
//    	LOG.info("validations for iban "+iban); 
    	parseValidationObject(validation);		
	}

	void getBankDataViaApi(int id, String iban, String branchCode) {
    	BankData bankData =	super.retrieveBankData(iban);
    	if(bankData==null) return;

        StringBuffer sb = new StringBuffer();
        String bic = bankData.getBic();
        if(bic==null) { // not found ==> comment
        	sb.append("// ");
        	return;
        }
        sb.append("{\"id\": ").append(id);
		sb.append(", \"swift_code\": ");
        if(bic==null) {
//			sb.append(bic);	        	
        } else {
	        if(bic.endsWith("XXX") && branchCode.length()==3) {
    			sb.append("\"").append(bic.substring(0, 8)).append(branchCode).append("\"");
	        } else {
    			sb.append("\"").append(bic).append("\"");
	        }
        }
		sb.append(", \"bank_code\": ");
		if(bankData.getBankCode()==0) {
			sb.append("\"").append(bankData.getBankIdentifier()).append("\"");
		} else {
			sb.append(bankData.getBankCode());
		}
		sb.append(", \"branch_code\": ");
		if(bankData.getBranchCode()==null) {
			sb.append(bankData.getBranchCode());
		} else {
			sb.append("\"").append(bankData.getBranchCode()).append("\"");
		}
		sb.append(", \"branch\": ");
		if(bankData.getBranch()==null) {
			sb.append(bankData.getBranch());
		} else {
			sb.append("\"").append(bankData.getBranch()).append("\"");
		}
		sb.append(", \"bank\": ");
		if(bankData.getBank()==null) {
			sb.append(bankData.getBank());
		} else {
			sb.append("\"").append(bankData.getBank()).append("\"");
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

	void jsonToList(String filename, String account) throws FileNotFoundException, IOException {
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
        		Object swift_code = je.get("swift_code");
        		// create IBAN and use IBANApi
        		String bankCode = swift_code.toString().substring(0,4);
        		String countryCode = swift_code.toString().substring(4,6);
        		String locationCode = swift_code.toString().substring(6,8);
        		String branchCode = "";
        		if(swift_code.toString().length()==11) {
        			branchCode = swift_code.toString().substring(8,11);
        		}   		
        		int id = bankCodeToId(bankCode);
        		String iban = country_code.toString() + PP + bankCode + account;
//            	LOG.info("id="+id + " swift_code:"+swift_code + " iban:"+iban);
            	getBankDataViaApi(id, iban, branchCode);
        	});
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			reader.close();
		}
//        Object o = jsonParser.parse(response.toString());
//        JSONObject jo = (JSONObject) o;
	}
	
	private static final int radix = 1+Character.hashCode('Z')-Character.hashCode('A');
	// AAAA = 0, BAAA = 1, ZAAA = 28
	static int bankCodeToId(String bankCode) {
		if(bankCode==null) throw new IllegalArgumentException("bankCode is null.");
		if(bankCode.length()!=4) throw new IllegalArgumentException("'"+bankCode+"'.length NOT 4.");
		int id = 0;
		int r = 1;
		for(int i=0; i<bankCode.length(); i++) {
			char ch = bankCode.charAt(i);
			if(!Character.isUpperCase(ch)) throw new IllegalArgumentException("'"+ch+"' is not UpperCase.");
			int hash = Character.hashCode(ch)-Character.hashCode('A');
			int hc = hash*r;
			//LOG.info("ch="+ch + " hash:"+hash + " hc="+hc);
			id = id + hc;
			r = r*radix;
		}
		//LOG.info("id="+id);
		return id;
	}

	private final static String JSON_EXT = ".json";
	private final static String JSON_DIR = "AllCountries/";

	public static void main(String[] args) throws Exception {
		BankDataGenerator test = new BankDataGenerator("testKey");
//		test.jsonToList(JSON_DIR+"AZ"+JSON_EXT, "00000000137010001944");
//		test.jsonToList(JSON_DIR+"BG"+JSON_EXT, "96611020345678"); // +BranchCode TODO
		test.jsonToList(JSON_DIR+"BH"+JSON_EXT, "00001299123456");
//		test.jsonToList(JSON_DIR+"BY"+JSON_EXT, "3600900000002Z00AB00"); // +BranchCode
//		test.jsonToList(JSON_DIR+"DO"+JSON_EXT, "00000001212453611324");
//		test.jsonToList(JSON_DIR+"GI"+JSON_EXT, "000000007099453");
//		test.jsonToList(JSON_DIR+"GT"+JSON_EXT, "01020000001210029690");
//		test.jsonToList(JSON_DIR+"IQ"+JSON_EXT, "850123456789012"); // +BranchCode TODO
		
//		test.jsonToList(JSON_DIR+"KW"+JSON_EXT, "0000000000001234560101");
//		test.jsonToList(JSON_DIR+"LC"+JSON_EXT, "000100010012001200023015");
//		test.jsonToList(JSON_DIR+"LV"+JSON_EXT, "0000435195001");
//		test.jsonToList(JSON_DIR+"NL"+JSON_EXT, "0417164300");
//		test.jsonToList(JSON_DIR+"PK"+JSON_EXT, "0000001123456702");
//		test.jsonToList(JSON_DIR+"PS"+JSON_EXT, "000000000400123456702");
//		test.jsonToList(JSON_DIR+"QA"+JSON_EXT, "00001234567890ABCDEFG");
//		test.jsonToList(JSON_DIR+"RO"+JSON_EXT, "1B31007593840000");
//		test.jsonToList(JSON_DIR+"SC"+JSON_EXT, "11010000000000001497USD");
//		test.jsonToList(JSON_DIR+"SV"+JSON_EXT, "00000000000000700025");
//		test.jsonToList(JSON_DIR+"VG"+JSON_EXT, "0000012345678901");
		
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
