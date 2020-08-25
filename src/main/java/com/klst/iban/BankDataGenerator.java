package com.klst.iban;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.klst.iban.Result.BankData;
import com.klst.iban.Result.SepaData;

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

	private static final String IBAN_COM_URL = "https://api.iban.com/clients/api/v4/iban/";
    private static final String USER_AGENT = "API Client/1.0";
    private static final String FORMAT_XML = "&format=xml";
    private static final String FORMAT_JSON = "&format=json";

	private final static String PP = "99";

	BankDataGenerator(String api_key) {
		super(api_key);
	}

	void getBankDataViaApi(int id, String iban, String branchCode) {
        try {
        	URL url = new URL(IBAN_COM_URL); // throws MalformedURLException
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection(); // throws IOException
	        //add reuqest header
	        con.setRequestMethod("POST");
	        con.setRequestProperty("User-Agent", USER_AGENT);
	        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	        
	        String urlParameters = "api_key="+API_KEY+FORMAT_JSON+"&iban="+iban;
	        
	        // Send post request
	        con.setDoOutput(true); // true: use the URL connection for output,
	        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
	        wr.writeBytes(urlParameters);
	        wr.flush();
	        wr.close();

	        int responseCode = con.getResponseCode();
	        //LOG.info("Sending 'POST' request to URL "+IBAN_COM_URL + " parameters:"+urlParameters);
	        if(responseCode!=200) {
		        LOG.warning("Sending 'POST' request to URL "+IBAN_COM_URL + " parameters:"+urlParameters + " returns "+responseCode);	        	
	        }
	        //assertEquals(200, responseCode);
	 
	        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	        String inputLine;
	        StringBuffer response = new StringBuffer();
	 
	        while ((inputLine = in.readLine()) != null) {
	            response.append(inputLine);
	        }
	        in.close();
	        
	        // json: {"bank_data":[],"sepa_data":[],"validations":[],"errors":[{"code":"301","message":"API Key is invalid"}]}
	        // xml: <?xml version="1.0"?><result><bank_data/><sepa_data/><validations><chars><code/><message/></chars><iban><code/><message/></iban><account><code/><message/></account><structure><code/><message/></structure><length><code/><message/></length><country_support><code/><message/></country_support></validations><errors><error><code>301</code><message>API Key is invalid</message></error></errors></result>
	        String jsonString = response.toString();
//	        LOG.info("response:\n"+jsonString);   
// Test:
//        	String jsonString = "{\"bank_data\":[],\"sepa_data\":[],\"validations\":[],\"errors\":[{\"code\":\"301\",\"message\":\"API Key is invalid\"}]}";

	        JSONParser jsonParser = new JSONParser();
	        Object o = jsonParser.parse(jsonString); // throws ParseException
	        JSONObject jo = (JSONObject) o;
	        
	        JSONArray errors = (JSONArray) jo.get("errors");
	        if(errors.size()>0) {
	            errors.forEach( error -> parseErrorObject( (JSONObject)error ) );
	            return;
	        }

	        BankData bankData = null;
	        Object bank_data_o = jo.get("bank_data");
	        if(bank_data_o instanceof JSONObject) {
	        	bankData = parseBankDataObject( (JSONObject)bank_data_o );
	        }

	        Object sepa_data_o = jo.get("sepa_data");
	        if(sepa_data_o instanceof JSONObject) {
	        	SepaData sepaData = parseSepaDataObject( (JSONObject)sepa_data_o );
	        	bankData.setBankSupports(sepaData.getBankSupports());
	        }

	        StringBuffer sb = new StringBuffer();
	        String bic = bankData.getBic();
	        if(bic==null) { // not found ==> comment
	        	sb.append("// ");
	        }
	        sb.append("{\"id\": ").append(id);
			sb.append(", \"swift_code\": ");
	        if(bic==null) {
				sb.append(bic);	        	
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
    		sb.append(",\"zip\": ");
    		if(bankData.getZipString()==null) {
    			sb.append(bankData.getZipString());
    		} else {
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
    		if(bankData.getPhone()!=null) {
        		sb.append(", \"phone\": ");
    			sb.append("\"").append(bankData.getPhone()).append("\"");
    		}
    		if(bankData.getFax()!=null) {
        		sb.append(", \"fax\": ");
    			sb.append("\"").append(bankData.getFax()).append("\"");
    		}
    		if(bankData.getWww()!=null) {
        		sb.append(", \"www\": ");
    			sb.append("\"").append(bankData.getWww()).append("\"");
    		}
    		if(bankData.getEmail()!=null) {
        		sb.append(", \"email\": ");
    			sb.append("\"").append(bankData.getEmail()).append("\"");
    		}
    		sb.append ("},");
			System.out.println(sb.toString());

	        Object validations_o = jo.get("validations");
	        if(validations_o instanceof JSONObject) {
	        	parseValidationObject( (JSONObject)validations_o );
	        }

        } catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

    private final static String CODE = "code";
    private final static String MESSAGE = "message";
    private final static String INVALID_KEY = "301"; // Account Error 	API Key is invalid

	private String parseErrorObject(JSONObject errorOrValidation) {
		return parseErrorObject(errorOrValidation, false);
	}
	private String parseErrorObject(JSONObject error, boolean verbose) {
		String code = (String) error.get(CODE);
		String message = (String) error.get(MESSAGE);
		if(verbose) {
			if(code.startsWith("0")) {
				LOG.info(CODE+":"+code + ", " + MESSAGE+":"+message);
			} else {
				LOG.warning(CODE+":"+code + ", " + MESSAGE+":"+message);
			}
		}
		return code;
	}

	private BankData parseBankDataObject(JSONObject bank_data) {
		BankData bankData = new BankData();
		String bic = (String) bank_data.get("bic");
		bankData.setBic(bic);
		String bank = (String) bank_data.get("bank"); // aka bank nam
		bankData.setBank(bank);
		String city = (String) bank_data.get("city");
		bankData.setCity(city);
		String bank_code = (String) bank_data.get("bank_code");
		bankData.setBankIdentifier(bank_code);
		// optional:
		bankData = getOptionalKey(bank_data, "branch", bankData);
		bankData = getOptionalKey(bank_data, "address", bankData);
		bankData = getOptionalKey(bank_data, "state", bankData);
//		bankData = getOptionalKey(bank_data, "zip", bankData); // int
		bankData = getOptionalKey(bank_data, "phone", bankData);
		bankData = getOptionalKey(bank_data, "fax", bankData);
		bankData = getOptionalKey(bank_data, "www", bankData);
		bankData = getOptionalKey(bank_data, "email", bankData);
		// country, country_iso
		// account
		return bankData;
	}

	private BankData getOptionalKey(JSONObject bank_data, String key, BankData bankData) {
		Object o = bank_data.get(key);
		if(o!=null) {
			bankData.setBranch(o);
		}
		return bankData;	
	}
	
	private SepaData parseSepaDataObject(JSONObject sepa_data) {
		SepaData sepaData = new SepaData();
//		String sSCT = (String) sepa_data.get("SCT");
//		String sSDD = (String) sepa_data.get("SDD");
//		String sCOR1 = (String) sepa_data.get("COR1");
//		String sB2B = (String) sepa_data.get("B2B");
//		String sSCC = (String) sepa_data.get("SCC");
		sepaData.setSCT((String)sepa_data.get("SCT"));
		sepaData.setSDD((String)sepa_data.get("SDD"));
		sepaData.setCOR1((String)sepa_data.get("COR1"));
		sepaData.setB2B((String)sepa_data.get("B2B"));
		sepaData.setSCC((String)sepa_data.get("SCC"));
		return sepaData;
	}

    //201 	Validation Failed 	Account Number check digit not correct
/*

001 	Validation Success 	IBAN Check digit is correct
002 	Validation Success 	Account Number check digit is correct
003 	Validation Success 	IBAN Length is correct
004 	Validation Success 	Account Number check digit is not performed for this bank or branch
005 	Validation Success 	IBAN structure is correct
006 	Validation Success 	IBAN does not contain illegal characters
007 	Validation Success 	Country supports IBAN standard

201 	Validation Failed 	Account Number check digit not correct
202 	Validation Failed 	IBAN Check digit not correct
203 	Validation Failed 	IBAN Length is not correct
205 	Validation Failed 	IBAN structure is not correct
206 	Validation Failed 	IBAN contains illegal characters
207 	Validation Failed 	Country does not support IBAN standard

,"validations":{"chars":{"code":"006","message":"IBAN does not contain illegal characters"}
             ,"account":{"code":"002","message":"Account Number check digit is correct"}
             ,"iban":{"code":"001","message":"IBAN Check digit is correct"} //  OR
             ,"iban":{"code":"202","message":"IBAN Check digit not correct"}
             ,"structure":{"code":"005","message":"IBAN structure is correct"}
             ,"length":{"code":"003","message":"IBAN Length is correct"}
             ,"country_support":{"code":"007","message":"Country supports IBAN standard"}}

 */
	private void parseValidationObject(JSONObject validation) {
		boolean verbose = false;
		String code = parseErrorObject( (JSONObject)validation.get("chars"), verbose);
		code = parseErrorObject( (JSONObject)validation.get("account"), verbose);
		code = parseErrorObject( (JSONObject)validation.get("iban"), verbose);
		code = parseErrorObject( (JSONObject)validation.get("structure"), verbose);
		code = parseErrorObject( (JSONObject)validation.get("length"), verbose);
		code = parseErrorObject( (JSONObject) validation.get("country_support"), verbose);
		return;
	}
	
	void jsonToList(String filename, String account) throws FileNotFoundException, IOException {
		LOG.info("filename:"+filename);
		File file = new File(filename);
		if(!file.exists()) {
			LOG.info("not existing file:"+file);
			return;
		}
		
		//Object charsetName;
		// FileInputStream throws FileNotFoundException
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
//        String inputLine;
//        StringBuffer response = new StringBuffer();
// 
        // readLine throws IOException
//        while ((inputLine = reader.readLine()) != null) {
//            response.append(inputLine);
//        }
//        reader.close();
        
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
	private final static String API_KEY = "testKey";

	private final static String JSON_EXT = ".json";
	private final static String JSON_DIR = "AllCountries/";

	public static void main(String[] args) throws Exception {
		BankDataGenerator test = new BankDataGenerator(API_KEY);
//		test.jsonToList(JSON_DIR+"AZ"+JSON_EXT, "00000000137010001944");
//		test.jsonToList(JSON_DIR+"BH"+JSON_EXT, "00001299123456");
//		test.jsonToList(JSON_DIR+"BY"+JSON_EXT, "3600900000002Z00AB00"); // +BranchCode
		LOG.info("Id of AAAA/00 is "+test.bankCodeToId("AAAA"));
		LOG.info("Id of BAAA/01 is "+test.bankCodeToId("BAAA"));
		LOG.info("Id of CAAA/02 is "+test.bankCodeToId("CAAA"));
		LOG.info("Id of DAAA/03 is "+test.bankCodeToId("DAAA"));
		LOG.info("Id of EAAA/04 is "+test.bankCodeToId("EAAA"));
		LOG.info("Id of FAAA/05 is "+test.bankCodeToId("FAAA"));
		LOG.info("Id of AAAA is "+test.bankCodeToId("GAAA"));
		LOG.info("Id of AAAA is "+test.bankCodeToId("HAAA"));
		LOG.info("Id of AAAA is "+test.bankCodeToId("IAAA"));
		LOG.info("Id of AAAA is "+test.bankCodeToId("JAAA"));
		LOG.info("Id of AAAA is "+test.bankCodeToId("KAAA"));
		LOG.info("Id of AAAA is "+test.bankCodeToId("LAAA"));
		LOG.info("Id of AAAA is "+test.bankCodeToId("MAAA"));
		LOG.info("Id of AAAA is "+test.bankCodeToId("NAAA"));
		LOG.info("Id of AAAA is "+test.bankCodeToId("OAAA"));
		LOG.info("Id of PAAA/15 is "+test.bankCodeToId("PAAA"));
		LOG.info("Id of AAAA is "+test.bankCodeToId("QAAA"));
		LOG.info("Id of AAAA is "+test.bankCodeToId("RAAA"));
		LOG.info("Id of AAAA is "+test.bankCodeToId("SAAA"));
		LOG.info("Id of AAAA is "+test.bankCodeToId("TAAA"));
		LOG.info("Id of AAAA is "+test.bankCodeToId("UAAA"));
		LOG.info("Id of AAAA is "+test.bankCodeToId("VAAA"));
		LOG.info("Id of AAAA is "+test.bankCodeToId("WAAA"));
		LOG.info("Id of AAAA is "+test.bankCodeToId("XAAA"));
		LOG.info("Id of AAAA is "+test.bankCodeToId("YAAA"));
		LOG.info("Id of ZAAA/25 is "+test.bankCodeToId("ZAAA"));
		LOG.info("Id of ABAA/26 is "+test.bankCodeToId("ABAA"));
//		//LOG.info("Id of ABAA is "+test.bankCodeToId("AB")); // exception
	}

}
