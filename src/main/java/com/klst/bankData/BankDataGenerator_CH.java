package com.klst.bankData;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.klst.iban.datastore.LocalFileProxy;
import com.klst.ibanTest.API_Key_Provider;

public class BankDataGenerator_CH extends NumericBankCode {

	private static final Logger LOG = Logger.getLogger(BankDataGenerator_CH.class.getName());

	static final String COUNTRY_CODE = "CH";
    static final String ODS_RESOURCE = RESOURCE_DATA_PATH + COUNTRY_CODE+"/" +"public.json";

	
	BankDataGenerator_CH(String api_key) {
		super(COUNTRY_CODE, api_key);

		String jsonString = LocalFileProxy.loadFile(new File(ODS_RESOURCE));
        JSONParser jsonParser = new JSONParser();
        Object o = null;
		try {
			o = jsonParser.parse(jsonString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // throws ParseException
        JSONObject jo = (JSONObject) o;
        Object metaData = jo.get("metaData");
        if(metaData instanceof JSONObject) {
        	JSONObject jmetaData = (JSONObject)metaData;
	        LOG.info("metaData.validForClearingDay:"+(String)jmetaData.get("validForClearingDay")
	        	+ ", createdStamp:"+(String)jmetaData.get("createdStamp")
	        	); 
        }
        Object entries = jo.get("entries");
        if(entries instanceof JSONArray) {
        	List<JSONObject> array = (JSONArray)entries;
        	LOG.info("JSONArray.size="+array.size());
        	bankByCode = new Hashtable<Integer, ArrayList<Object>>();
        	Iterator iter = array.listIterator();
        	while(iter.hasNext()) {
        		JSONObject jEntry = (JSONObject)iter.next();
        		//LOG.info(""+jEntry);
/*
Beispiel:
			"group": "01",
			"iid": 100,
			"branchId": "0000",
			"sicIid": "001008",
			"headOffice": 100,
			"iidType": "HEADQUARTERS",
			"validSince": "2017-09-11",
			"sicParticipation": "SIC_PARTICIPATION_AND_LSV_AS_DEBTOR_FI",
			"euroSicParticipation": "EURO_SIC_PARTICIPATION",
			"language": "DE",
			"shortName": "SNB",
			"bankOrInstitutionName": "Schweizerische Nationalbank",
			"domicileAddress": "Börsenstrasse 15",
			"postalAddress": "Postfach 2800",
			"zipCode": "8022",
			"place": "Zürich",
			"phone": "058 631 00 00",
			"countryCode": "CH",
			"postalAccount": "30-5-5",
			"bic": "SNBZCHZZXXX"

 */
        		Long iid = (Long)jEntry.get("iid");
        		Integer bankCode = null;
        		try {
        			bankCode = new Integer( Integer.parseInt(jEntry.get("iid").toString()) );
        		} catch (NumberFormatException e) {
        		}
        		String bic = (String)jEntry.get("bic");
        		String name = (String)jEntry.get("bankOrInstitutionName");
        		LOG.info("entry.iid:"+iid + " bic:"+bic);
        		bankByCode.put(bankCode, new ArrayList<Object>(Arrays.asList(bankCode, bic, name)));
        	}

        }
		LOG.info("bankByCode.size="+bankByCode.size());
	}

	public static void main(String[] args) throws Exception {
		NumericBankCode test = new BankDataGenerator_CH(API_Key_Provider.API_KEY);

//		test.tryWith(FORMAT_05d, 00000, 99999);
	}

}
