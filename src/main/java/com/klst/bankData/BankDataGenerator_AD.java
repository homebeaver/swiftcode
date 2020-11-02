package com.klst.bankData;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

import com.klst.iban.BankDataGenerator;
import com.klst.ibanTest.API_Key_Provider;

/*

branch code wird völlig ignoriert
dh für bankCode=X liefert branchCode=0 dasselbe wie branchCode=9999

in https://www.ecbs.org/banks/andorra/ sind 5 gelistet, ich finde 7-1, in Thomanphan/swiftcode gab es 9

 */
public class BankDataGenerator_AD extends BankDataGenerator {

	private static Logger LOG;
	private static final LogManager logManager = LogManager.getLogManager(); // Singleton
	
	static {
    	URL url = BankDataGenerator_AD.class.getClassLoader().getResource("testLogging.properties");
		try {
			File file = new File(url.toURI());
			logManager.readConfiguration(new FileInputStream(file));
		} catch (IOException | URISyntaxException e) {
			LOG = Logger.getLogger(BankDataGenerator_AD.class.getName());
			LOG.warning(e.getMessage());
		}
		LOG = Logger.getLogger(BankDataGenerator_AD.class.getName());
	}
	
	BankDataGenerator_AD(String api_key) {
		super(api_key);
	}
	
	public void tryWith(String countryCode, String format, int from, int to, String account) {
//		List<BusinessIdentifierCode> bicList = new ArrayList<BusinessIdentifierCode>(bankByBic.keySet());
		
//		outerloop: branch
		for(int id=from; id<=to; id++) {
			int bankId = id; //*100;
			String branchCode = String.format(format, bankId);
			if(id%100==0) {
				int perCent = 100*id/to;
				System.out.println(","+id + " %:"+ perCent);
			}
			// innerloop: bank 0..9
			for(int i=1; i<=999; i++) {
				String bankCode = String.format(BankDataGenerator.FORMAT_04d, i);
				String iban = countryCode + PP + bankCode + branchCode + account;
				Hashtable<String, List<JSONObject>> jMap = new Hashtable<String, List<JSONObject>>(); // leer
				LOG.fine(iban);
				boolean found = printBankDataViaApi(bankId, iban, jMap);
//				if(found) {
//					break; // inner
//				}
			}
			
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		BankDataGenerator test = new BankDataGenerator_AD(API_Key_Provider.API_KEY);

		test.tryWith("AD", BankDataGenerator.FORMAT_04d, 0000, 1, "200359100100"); // AD12 0001 2030 200359100100
		
	}
}
