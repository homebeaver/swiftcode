package com.klst.iban;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.validator.routines.IBANValidator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.klst.iban.Result.BankData;
import com.klst.iban.Result.SepaData;
import com.klst.ibanTest.API_Key_Provider;

public class IbanToBankData { 
	
	private static final Logger LOG = Logger.getLogger(IbanToBankData.class.getName());

	static final String IBAN_COM_URL = "https://api.iban.com/clients/api/v4/iban/";
    static final String USER_AGENT = "API Client/1.0";
    static final String FORMAT_XML = "&format=xml";
    static final String FORMAT_JSON = "&format=json";

	String api_key = null;
	String iban = null;
	
	public IbanToBankData() {
		this(null);
	}
	public IbanToBankData(String api_key) {
		this.api_key = api_key;
	}
	
	public BankData getBankData(String iban) {
		IBANValidator validator = IBANValidator.getInstance();
		if(!validator.isValid(iban)) return null;

		LOG.info(iban + " is valid.");		
		this.iban = iban;
		return getBankData();
	}
	
	/**
	 * Validate and retrieve bank data from an IBAN with https://www.iban.com/iban-checker
	 * 
	 * @param iban
	 * @return BankData object
	 */
	public BankData retrieveBankData(String iban) {
		if(this.api_key==null) {
			LOG.warning(iban + " - No api_key provided.");
			return null;
		}
		
        BankData bankData = null;
        try {
        	URL url = new URL(IBAN_COM_URL); // throws MalformedURLException
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection(); // throws IOException
	        //add request header
	        con.setRequestMethod("POST");
	        con.setRequestProperty("User-Agent", USER_AGENT);
	        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	        
	        String urlParameters = "api_key="+api_key+FORMAT_JSON+"&iban="+iban;
	        
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
		        return null;
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
//	        LOG.info("response:\n"+jsonString+"<"); 
	        if(jsonString.isEmpty()) return null;
// Test:
//        	String jsonString = "{\"bank_data\":[],\"sepa_data\":[],\"validations\":[],\"errors\":[{\"code\":\"301\",\"message\":\"API Key is invalid\"}]}";

	        JSONParser jsonParser = new JSONParser();
	        Object o = jsonParser.parse(jsonString); // throws ParseException
	        JSONObject jo = (JSONObject) o;
	        
	        JSONArray errors = (JSONArray) jo.get("errors");
	        if(errors.size()>0) {
	            errors.forEach( error -> parseErrorObject( (JSONObject)error ) );
	            return null;
	        }

	        Object validations_o = jo.get("validations");
	        if(validations_o instanceof JSONObject) {
	        	parseValidationObject(iban, (JSONObject)validations_o, true );
	        }

	        Object bank_data_o = jo.get("bank_data");
	        if(bank_data_o instanceof JSONObject) {
	        	bankData = parseBankDataObject( (JSONObject)bank_data_o );
	        }

	        Object sepa_data_o = jo.get("sepa_data");
	        if(sepa_data_o instanceof JSONObject) {
	        	SepaData sepaData = parseSepaDataObject( (JSONObject)sepa_data_o );
	        	bankData.setBankSupports(sepaData.getBankSupports());
	        }
//        	if(bankData.getBic()!=null && !bankData.getBic().isEmpty()) LOG.info(""+bankData); 

        } catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        return bankData;

	}

    final static String CODE = "code";
    final static String MESSAGE = "message";
    final static String INVALID_KEY = "301"; // Account Error 	API Key is invalid

	String parseErrorObject(JSONObject errorOrValidation) {
		return parseErrorObject(errorOrValidation, true);
	}
	private String parseErrorObject(JSONObject error, boolean verbose) {
		String code = (String) error.get(CODE);
		String message = (String) error.get(MESSAGE);
		if(verbose) {
			if(code.startsWith("0")) {
				LOG.config(CODE+":"+code + ", " + MESSAGE+":"+message);
			} else {
				LOG.warning(CODE+":"+code + ", " + MESSAGE+":"+message);
			}
		}
		return code;
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
             ,"account":{"code":"002","message":"Account Number check digit is correct"} OR
             ,"account":{"code":"201","message":"Account Number check digit not correct"}
             ,"iban":{"code":"001","message":"IBAN Check digit is correct"} //  OR
             ,"iban":{"code":"202","message":"IBAN Check digit not correct"}
             ,"iban":{"code":"203","message":"IBAN Length is not correct. Kosovo IBAN must be 20 characters long.t"}
             ,"iban":{"code":"205","message":"IBAN structure is not correct"}
             ,"iban":{"code":"206","message":"IBAN contains illegal characters"}
             ,"structure":{"code":"005","message":"IBAN structure is correct"}
             ,"length":{"code":"003","message":"IBAN Length is correct"}
             ,"country_support":{"code":"007","message":"Country supports IBAN standard"} OR
             ,"country_support":{"code":"207","message":"Country does not support IBAN standard"}
             }

 */
	void parseValidationObject(String iban, JSONObject validation, boolean verbose) {
    	LOG.info("validations for iban "+iban); 
    	parseValidationObject( validation, verbose );		
	}
	void parseValidationObject(JSONObject validation) {
		parseValidationObject(validation, false);
	}
	void parseValidationObject(JSONObject validation, boolean verbose) {
		String code = parseErrorObject( (JSONObject)validation.get("chars"), verbose);
		code = parseErrorObject( (JSONObject)validation.get("account"), verbose);
		code = parseErrorObject( (JSONObject)validation.get("iban"), verbose);
		code = parseErrorObject( (JSONObject)validation.get("structure"), verbose);
		code = parseErrorObject( (JSONObject)validation.get("length"), verbose);
		code = parseErrorObject( (JSONObject) validation.get("country_support"), verbose);
		return;
	}

	BankData parseBankDataObject(JSONObject bank_data) {
		BankData bankData = new BankData();
		String bic = (String) bank_data.get(BIC);
		bankData.setBic(bic);
		String bank = (String) bank_data.get(BANK); // aka bank name
		bankData.setBank(bank);
		String city = (String) bank_data.get(CITY);
		bankData.setCity(city);
		String bank_code = (String) bank_data.get(BANK_CODE);
		bankData.setBankIdentifier(bank_code);
		// optional:
		bankData = getOptionalKey(bank_data, BRANCH, bankData);
		bankData = getOptionalKey(bank_data, ADDRESS, bankData);
		bankData = getOptionalKey(bank_data, STATE, bankData);
		bankData = getOptionalKey(bank_data, ZIP, bankData);
		bankData = getOptionalKey(bank_data, PHONE, bankData);
		bankData = getOptionalKey(bank_data, FAX, bankData);
		bankData = getOptionalKey(bank_data, WWW, bankData);
		bankData = getOptionalKey(bank_data, EMAIL, bankData);
		// country, country_iso
		// account
		return bankData;
	}
	
    static final String BIC = "bic";
    static final String SWIFT_CODE = "swift_code"; // aka BIC
    static final String BANK = "bank"; // bank name
    static final String BANK_CODE = "bank_code"; // String BankData.bankIdentifier, int BankData.bankCode
    static final String ID = "id"; // unique per country, value can be bankCode
    static final String BRANCH = "branch";
    static final String BRANCH_CODE = "branch_code";
    static final String ADDRESS = "address";
    static final String STATE = "state";
    static final String CITY = "city";
    static final String ZIP = "zip";
    static final String PHONE = "phone";
    static final String FAX = "fax";
    static final String WWW = "www";
    static final String EMAIL = "email";
    static final String SUPPORT_CODES = "support_codes";
    
 	private BankData getOptionalKey(JSONObject bank_data, String key, BankData bankData) {
		Object value = bank_data.get(key);
		if(value!=null) {
			if(key.equals(BRANCH)) bankData.setBranch(value);
			else if(key.equals(ADDRESS)) bankData.setAddress(value);
			else if(key.equals(STATE)) bankData.setState(value);
			else if(key.equals(ZIP)) bankData.setZipString((String)value);
			else if(key.equals(PHONE)) bankData.setPhone(value);
			else if(key.equals(FAX)) bankData.setFax(value);
			else if(key.equals(WWW)) bankData.setWww(value);
			else if(key.equals(EMAIL)) bankData.setEmail(value);
			else {
				LOG.severe("unsupported key "+key);
			}
		}
		return bankData;	
	}

	private SepaData parseSepaDataObject(JSONObject sepa_data) {
		SepaData sepaData = new SepaData();
		sepaData.setSCT((String)sepa_data.get("SCT"));
		sepaData.setSDD((String)sepa_data.get("SDD"));
		sepaData.setCOR1((String)sepa_data.get("COR1"));
		sepaData.setB2B((String)sepa_data.get("B2B"));
		sepaData.setSCC((String)sepa_data.get("SCC"));
		return sepaData;
	}

	private BankData getBankData() {
		BankData bankData = new BankData();
		SepaData sepaData = new SepaData();
		String countryCode = iban.substring(0, 2);
		String bban = iban.substring(4);
		if(Bban.BBAN.get(countryCode)!=null) {
			Bban bData = Bban.BBAN.get(countryCode); // liefert eine Instanz mit Methode
			bankData = bData.getBankData(iban);
		} else {
			LOG.warning(iban + " NOT implemented.");			
		}
		LOG.info(iban + " -> bankData:"+bankData);
		return bankData;
	}

	public static void main(String[] args) throws Exception {
		IbanToBankData test = new IbanToBankData(API_Key_Provider.API_KEY);
		test.getBankData("AD1200012030200359100100");
/*
{"bank_data":{"bic":"BACAADADXXX","branch":"Serveis Centrals","bank":"andbanc Grup Agr"
		             ,"address":"C\/ Manuel Cerqueda i Escaler, 6","city":"Escaldes-Engordany","state":null,"zip":""
		             ,"phone":"87 33 33","fax":"86 39 05","www":null,"email":null,"country":"Andorra","country_iso":"AD"
		             ,"account":"200359100100","bank_code":"0001","branch_code":"2030"}
,"sepa_data":{"SCT":"YES","SDD":"NO","COR1":"NO","B2B":"NO","SCC":"NO"}
,"validations":{"chars":{"code":"006","message":"IBAN does not contain illegal characters"},"account":{"code":"004","message":"Account Number check digit is not performed for this bank or branch"},"iban":{"code":"001","message":"IBAN Check digit is correct"},"structure":{"code":"005","message":"IBAN structure is correct"},"length":{"code":"003","message":"IBAN Length is correct"},"country_support":{"code":"007","message":"Country supports IBAN standard"}},"errors":[]}
 */
		test.getBankData("AE070331234567890123456");
/*
{"bank_data":{"bic":"BOMLAEADXXX","branch":null,"bank":"Mashreqbank","address":"AL GHURAIR CITY 339-C, AGC AL RIQQA STREET","city":"DUBAI 04"
		             ,"state":null,"zip":"","phone":null,"fax":null,"www":null,"email":null,"country":"United Arab Emirates","country_iso":"AE"
		             ,"account":"1234567890123456","bank_code":"033","branch_code":""}
,"sepa_data":{"SCT":"NO","SDD":"NO","COR1":"NO","B2B":"NO","SCC":"NO"}
,"validations":{"chars":{"code":"006","message":"IBAN does not contain illegal characters"},"account":{"code":"004","message":"Account Number check digit is not performed for this bank or branch"},"iban":{"code":"001","message":"IBAN Check digit is correct"},"structure":{"code":"005","message":"IBAN structure is correct"},"length":{"code":"003","message":"IBAN Length is correct"},"country_support":{"code":"007","message":"Country supports IBAN standard"}},"errors":[]}
 */
		test.getBankData("AL47212110090000000235698741");
/* AL47 212 1100 90000000235698741
{"bank_data":{"bic":"CDISALTRXXX","branch":"Head Office","bank":"Credins Bank","address":"Rr. \"Ismail Qemali\", nr. 21","city":"Tirane","state":null,"zip":"1019","phone":null,"fax":null,"www":null,"email":null,"country":"Albania","country_iso":"AL"
		             ,"account":"90000000235698741","bank_code":"212","branch_code":"1100"}
,"sepa_data":{"SCT":"NO","SDD":"NO","COR1":"NO","B2B":"NO","SCC":"NO"}
,"validations":{"chars":{"code":"006","message":"IBAN does not contain illegal characters"},"account":{"code":"004","message":"Account Number check digit is not performed for this bank or branch"},"iban":{"code":"001","message":"IBAN Check digit is correct"},"structure":{"code":"005","message":"IBAN structure is correct"},"length":{"code":"003","message":"IBAN Length is correct"},"country_support":{"code":"007","message":"Country supports IBAN standard"}},"errors":[]}
 */
		test.getBankData("AT611904300234573201");
		test.getBankData("AZ21NABZ00000000137010001944");
/*
{"bank_data":{"bic":"NABZAZ2XXXX","branch":null,"bank":"Central Bank of the Republic of Azerbaijan","address":"AZ1014 R.Behbudov Str.32","city":"Baku","state":null,"zip":"","phone":"493-11-22","fax":null,"www":null,"email":null,"country":"Azerbaijan","country_iso":"AZ"
		             ,"account":"00000000137010001944","bank_code":"NABZ","branch_code":""}
,"sepa_data":{"SCT":"NO","SDD":"NO","COR1":"NO","B2B":"NO","SCC":"NO"}
,"validations":{"chars":{"code":"006","message":"IBAN does not contain illegal characters"},"account":{"code":"004","message":"Account Number check digit is not performed for this bank or branch"},"iban":{"code":"001","message":"IBAN Check digit is correct"},"structure":{"code":"005","message":"IBAN structure is correct"},"length":{"code":"003","message":"IBAN Length is correct"},"country_support":{"code":"007","message":"Country supports IBAN standard"}},"errors":[]}
 */
		test.getBankData("BA391290079401028494");
		test.getBankData("BE94049123456789");
		test.getBankData("BG80BNBG96611020345678");
/*
{"bank_data":{"bic":"BNBGBGSFXXX","branch":"","bank":"BULGARIAN NATIONAL BANK","address":"ALEXANDER BATTENBERG SQUARE 1","city":" SOFIA","state":null,"zip":"1000","phone":"","fax":null,"www":null,"email":null,"country":"Bulgaria","country_iso":"BG"
             ,"account":"1020345678","bank_code":"BNBG","branch_code":"9661"}
,"sepa_data":{"SCT":"YES","SDD":"NO","COR1":"NO","B2B":"NO","SCC":"NO"}
,"validations":{"chars":{"code":"006","message":"IBAN does not contain illegal characters"},"account":{"code":"004","message":"Account Number check digit is not performed for this bank or branch"},"iban":{"code":"001","message":"IBAN Check digit is correct"},"structure":{"code":"005","message":"IBAN structure is correct"},"length":{"code":"003","message":"IBAN Length is correct"},"country_support":{"code":"007","message":"Country supports IBAN standard"}},"errors":[]}
 */
		test.getBankData("BH67BMAG00001299123456");
		test.getBankData("BR1800360305000010009795493C1");
		test.getBankData("BY13NBRB3600900000002Z00AB00");
		test.getBankData("CH9300762011623852957");	
		test.getBankData("CR05015202001026284066");	
		test.getBankData("CY17002001280000001200527600");	
		test.getBankData("CZ6508000000192000145399");	
		test.getBankData("DE89370400440532013000");	
		test.getBankData("DK5000400440116243");	
/* DK2!n4!n9!n1!n DK5000400440116243
{"bank_data":{"bic":"NDEADKKKXXX","branch":"VORDINGBORG AFDELING","bank":"NORDEA","address":"PRINS J\u00d8RGENS ALLE 6","city":"VORDINGBORG","state":null,"zip":"4760","phone":"70 33 33 33","fax":"55 34 01 11","www":"","email":null,"country":"Denmark","country_iso":"DK"
             ,"account":"0440116243","bank_code":"0040","branch_code":""}
,"sepa_data":{"SCT":"YES","SDD":"NO","COR1":"NO","B2B":"NO","SCC":"NO"}
,"validations":{"chars":{"code":"006","message":"IBAN does not contain illegal characters"},"account":{"code":"201","message":"Account Number check digit not correct"},"iban":{"code":"001","message":"IBAN Check digit is correct"},"structure":{"code":"005","message":"IBAN structure is correct"},"length":{"code":"003","message":"IBAN Length is correct"},"country_support":{"code":"007","message":"Country supports IBAN standard"}},"errors":[]}
 */
		test.getBankData("DO28BAGR00000001212453611324");
		test.getBankData("EE382200221020145685");
/*
{"bank_data":{"bic":"HABAEE2XXXX","branch":null,"bank":"SWEDBANK AS","address":"LIIVALAIA 8","city":"TALLINN","state":null,"zip":"15040","phone":null,"fax":null,"www":null,"email":null,"country":"Estonia","country_iso":"EE"
             ,"account":"00221020145685","bank_code":"22","branch_code":""}
,"sepa_data":{"SCT":"YES","SDD":"NO","COR1":"NO","B2B":"NO","SCC":"NO"}
,"validations":{"chars":{"code":"006","message":"IBAN does not contain illegal characters"},"account":{"code":"002","message":"Account Number check digit is correct"},"iban":{"code":"001","message":"IBAN Check digit is correct"},"structure":{"code":"005","message":"IBAN structure is correct"},"length":{"code":"003","message":"IBAN Length is correct"},"country_support":{"code":"007","message":"Country supports IBAN standard"}},"errors":[]}		
 */
		test.getBankData("EG380019000500000000263180002");
		test.getBankData("ES9121000418450200051332");
		test.getBankData("FI2112345600000785");
		test.getBankData("FO6264600001631634");
		test.getBankData("FR1420041010050500013M02606");
/*
{"bank_data":{"bic":"PSSTFRPPLIL","branch":null,"bank":"LA BANQUE POSTALE","address":"3 RUE PAUL DUEZ","city":"LILLE CEDEX 9","state":null,"zip":"59900","phone":null,"fax":null,"www":null,"email":null,"country":"FRANCE","country_iso":"FR"
             ,"account":"0500013M026","bank_code":"20041","branch_code":"01005"}
,"sepa_data":{"SCT":"YES","SDD":"YES","COR1":"YES","B2B":"YES","SCC":"NO"}
,"validations":{"chars":{"code":"006","message":"IBAN does not contain illegal characters"},"account":{"code":"002","message":"Account Number check digit is correct"},"iban":{"code":"001","message":"IBAN Check digit is correct"},"structure":{"code":"005","message":"IBAN structure is correct"},"length":{"code":"003","message":"IBAN Length is correct"},"country_support":{"code":"007","message":"Country supports IBAN standard"}},"errors":[]}
 */
		test.getBankData("GB29NWBK60161331926819");
		test.getBankData("GE29NB0000000101904917");
		test.getBankData("GI75NWBK000000007099453");
		test.getBankData("GL8964710001000206");
		test.getBankData("GR1601101250000000012300695");
		test.getBankData("GT82TRAJ01020000001210029690");
		test.getBankData("HR1210010051863000160");
		test.getBankData("HU42117730161111101800000000");
		test.getBankData("IE29AIBK93115212345678");
		test.getBankData("IL620108000000099999999");
		test.getBankData("IQ98NBIQ850123456789012");
		test.getBankData("IS140159260076545510730339");
/*
{"bank_data":{"bic":"NBIIISREXXX","branch":null,"bank":"Landsbankinn hf","address":"AUSTURSTRAETI 11","city":"REYKJAVIK","state":null,"zip":"155","phone":null,"fax":null,"www":null,"email":null,"country":"Iceland","country_iso":"IS"
             ,"account":"007654","bank_code":"0159","branch_code":""}
,"sepa_data":{"SCT":"YES","SDD":"NO","COR1":"NO","B2B":"NO","SCC":"NO"}
,"validations":{"chars":{"code":"006","message":"IBAN does not contain illegal characters"},"account":{"code":"002","message":"Account Number check digit is correct"},"iban":{"code":"001","message":"IBAN Check digit is correct"},"structure":{"code":"005","message":"IBAN structure is correct"},"length":{"code":"003","message":"IBAN Length is correct"},"country_support":{"code":"007","message":"Country supports IBAN standard"}},"errors":[]}
 */
		test.getBankData("IT60X0542811101000000123456");
		test.getBankData("JO94CBJO0010000000000131000302");
/*
{"bank_data":{"bic":"CBJOJOAXXXX","branch":null,"bank":"CENTRAL BANK OF JORDAN","address":"King Hussein Street 11118 AMMAN","city":"","state":null,"zip":"","phone":null,"fax":null,"www":null,"email":null,"country":"Jordan","country_iso":"JO"
             ,"account":"0010000000000131000302","bank_code":"CBJO","branch_code":""}
,"sepa_data":{"SCT":"NO","SDD":"NO","COR1":"NO","B2B":"NO","SCC":"NO"}
,"validations":{"chars":{"code":"006","message":"IBAN does not contain illegal characters"},"account":{"code":"004","message":"Account Number check digit is not performed for this bank or branch"},"iban":{"code":"001","message":"IBAN Check digit is correct"},"structure":{"code":"005","message":"IBAN structure is correct"},"length":{"code":"003","message":"IBAN Length is correct"},"country_support":{"code":"007","message":"Country supports IBAN standard"}},"errors":[]}
 */
		test.getBankData("KW81CBKU0000000000001234560101");
		test.getBankData("KZ86125KZT5004100100");
		test.getBankData("LB62099900000001001901229114");
		test.getBankData("LC55HEMM000100010012001200023015");
		test.getBankData("LI21088100002324013AA");
		test.getBankData("LT121000011101001000");
		test.getBankData("LU280019400644750000");
		test.getBankData("LV80BANK0000435195001");
		test.getBankData("MC5811222000010123456789030");
		test.getBankData("MD24AG000225100013104168");
		test.getBankData("ME25505000012345678951");
		test.getBankData("MK07250120000058984");
		test.getBankData("MR1300020001010000123456753");
		test.getBankData("MT84MALT011000012345MTLCAST001S");
		test.getBankData("MU17BOMM0101101030300200000MUR");
		test.getBankData("NL91ABNA0417164300");
		test.getBankData("NO9386011117947");
		test.getBankData("PK36SCBL0000001123456702");
		test.getBankData("PL61109010140000071219812874");
/*
{"bank_data":{"bic":"WBKPPLPPXXX","branch":"1 Oddzial w Warszawie","bank":"Santander Bank Polska Spolka Akcyjna","address":"ul. Kasprowicza 119A","city":"Warszawa","state":null,"zip":"01-949","phone":null,"fax":null,"www":"www.santander.pl","email":null,"country":"Poland","country_iso":"PL"
             ,"account":"0000071219812874","bank_code":"10901014","branch_code":""}
,"sepa_data":{"SCT":"YES","SDD":"NO","COR1":"NO","B2B":"NO","SCC":"NO"}
,"validations":{"chars":{"code":"006","message":"IBAN does not contain illegal characters"},"account":{"code":"002","message":"Account Number check digit is correct"},"iban":{"code":"001","message":"IBAN Check digit is correct"},"structure":{"code":"005","message":"IBAN structure is correct"},"length":{"code":"003","message":"IBAN Length is correct"},"country_support":{"code":"007","message":"Country supports IBAN standard"}},"errors":[]}
 */
		test.getBankData("PS92PALS000000000400123456702");
		test.getBankData("PT50000201231234567890154");
/*
{"bank_data":{"bic":null,"branch":null,"bank":null,"address":null,"city":null,"state":null,"zip":null,"phone":null,"fax":null,"www":null,"email":null,"country":"Portugal","country_iso":"PT"
             ,"account":"1234567890154","bank_code":"0002","branch_code":"0123"}
,"sepa_data":{"SCT":"NO","SDD":"NO","COR1":"NO","B2B":"NO","SCC":"NO"}
,"validations":{"chars":{"code":"006","message":"IBAN does not contain illegal characters"},"account":{"code":"002","message":"Account Number check digit is correct"},"iban":{"code":"001","message":"IBAN Check digit is correct"},"structure":{"code":"005","message":"IBAN structure is correct"},"length":{"code":"003","message":"IBAN Length is correct"},"country_support":{"code":"007","message":"Country supports IBAN standard"}},"errors":[]}
 */
		test.getBankData("QA58DOHB00001234567890ABCDEFG");
		test.getBankData("RO49AAAA1B31007593840000");
		test.getBankData("RS35260005601001611379");
		test.getBankData("SA0380000000608010167519");
		test.getBankData("SC18SSCB11010000000000001497USD");
/*
{"bank_data":{"bic":"SSCBSCSCXXX","branch":null,"bank":"CENTRAL BANK OF SEYCHELLES","address":"","city":"VICTORIA","state":null,"zip":"","phone":null,"fax":null,"www":null,"email":null,"country":"Seychelles","country_iso":"SC"
             ,"account":"11010000000000001497USD","bank_code":"SSCB","branch_code":""}
,"sepa_data":{"SCT":"NO","SDD":"NO","COR1":"NO","B2B":"NO","SCC":"NO"}
,"validations":{"chars":{"code":"006","message":"IBAN does not contain illegal characters"},"account":{"code":"004","message":"Account Number check digit is not performed for this bank or branch"},"iban":{"code":"001","message":"IBAN Check digit is correct"},"structure":{"code":"005","message":"IBAN structure is correct"},"length":{"code":"003","message":"IBAN Length is correct"},"country_support":{"code":"007","message":"Country supports IBAN standard"}},"errors":[]}
 */
		test.getBankData("SE4550000000058398257466");
		test.getBankData("SI56263300012039086");
		test.getBankData("SK3112000000198742637541");
		test.getBankData("SM86U0322509800000000270100");
		test.getBankData("ST23000100010051845310146");
		test.getBankData("SV62CENR00000000000000700025");
		test.getBankData("TL380080012345678910157");
		test.getBankData("TN5910006035183598478831");
		test.getBankData("TR330006100519786457841326");
		test.getBankData("UA213223130000026007233566001");
		test.getBankData("VA59001123000012345678");
		test.getBankData("VG96VPVG0000012345678901");
		test.getBankData("XK051212012345678906");
		
		test = new IbanToBankData();
		test.retrieveBankData("XK0512120123 5678906");
		test.retrieveBankData("XK051212012345678906");
/*
{"bank_data":{"bic":"NLPRXKPRXXX","branch":null,"bank":"NLB PRISHTINA","address":"KOSTA NOVAKOVIQ PN","city":"PRISTINA","state":null,"zip":"10000","phone":null,"fax":null,"www":null,"email":null,"country":"Kosovo","country_iso":"XK"
             ,"account":"012345678906","bank_code":"12","branch_code":"12"}
,"sepa_data":{"SCT":"NO","SDD":"NO","COR1":"NO","B2B":"NO","SCC":"NO"}
,"validations":{"chars":{"code":"006","message":"IBAN does not contain illegal characters"},"account":{"code":"002","message":"Account Number check digit is correct"},"iban":{"code":"001","message":"IBAN Check digit is correct"},"structure":{"code":"005","message":"IBAN structure is correct"},"length":{"code":"003","message":"IBAN Length is correct"},"country_support":{"code":"007","message":"Country supports IBAN standard"}},"errors":[]}<

 */
	}
}
