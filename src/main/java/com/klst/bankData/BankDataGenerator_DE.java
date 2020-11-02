package com.klst.bankData;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.klst.iban.BankDataGenerator;
import com.klst.iban.BankId;
import com.klst.iban.BankDataGenerator.FakeIban;
import com.klst.ibanTest.API_Key_Provider;

/*

DE iban's beinhalten die "alte" 8-stellige BLZ als bank_code:"42610112"
{"bank_data":{"bic":"ESSEDE5F426","branch":null,"bank":"SEB","address":"","city":"Recklinghausen"
             ,"state":null,"zip":"45657","phone":null,"fax":null,"www":null,"email":null,"country":"Germany"
             ,"country_iso":"DE","account":"0532013000","bank_code":"42610112","branch_code":""}
,"sepa_data":{"SCT":"YES","SDD":"YES","COR1":"YES","B2B":"YES","SCC":"NO"}
,"validations": ...

Aus der BLZ wird der id Schlüssel berechet map: blz -> id , mit der id kann man den Satz 
in der Bankleitzahlendatei der Bundesbank finden

Mapping csv aus AD:

select routingno,count(*),min(c_bank_id)-276000000 as id,min(updated) as updated,min(swiftcode) as swiftcode,min(name) as name from c_bank
where c_bank_id between 276000000 and 276999999
and swiftcode is null
group by 1
--having count(*)>1
order by 4 asc 

-------
--delete from c_bank
--where c_bank_id between 276000000 and 276999999

 */
public class BankDataGenerator_DE extends BankDataGenerator {

	private static final Logger LOG = Logger.getLogger(BankDataGenerator_DE.class.getName());
	
	static final String countryCode = "DE";
	
	Map<String, ArrayList<Object>> bankByCode = new Hashtable<String, ArrayList<Object>>();

	BankDataGenerator_DE(String api_key) {
		super(api_key);
		
		//     BLZ, id
		Map<String, Integer> deBLZtoID = BankId.getInstance().getDeBLZtoID();
		deBLZtoID.forEach((k,y) -> {
			bankByCode.put(k, new ArrayList<Object>());
		});
		LOG.info("bankByCode.size="+bankByCode.size());
	};
		
	public void tryWith(String countryCode, String format, int from, int to) {
		List<String> blzList = new ArrayList<String>(bankByCode.keySet());
		for(int id=from; id<=to; id++) {
    		String blz = String.format(format, id);
			if(blzList.contains(blz)) {
	    		FakeIban iban = new FakeIban(countryCode, blz);
    			LOG.info("id="+id + " tryWith "+iban+" bankCode "+blz);
    			printBankDataViaApi(id, iban.toString());
			}
		}	
	}
	
	public static void main(String[] args) throws Exception {
		BankDataGenerator test = new BankDataGenerator_DE(API_Key_Provider.API_KEY);
		test.tryWith(countryCode, BankDataGenerator.FORMAT_08d, 10000000, 99999999);
	}
}
