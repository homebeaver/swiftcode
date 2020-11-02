package com.klst.bankData;

import java.util.logging.Logger;

import com.klst.iban.BankDataGenerator;
import com.klst.ibanTest.API_Key_Provider;

/*

branch code wird in iban-com api völlig ignoriert
dh für bankCode=X liefert branchCode=0 dasselbe wie branchCode=9999

in https://www.ecbs.org/banks/andorra/ sind 5 gelistet, ich finde 7-1, in Thomanphan/swiftcode gab es 9

 */
public class BankDataGenerator_AD extends BankDataGenerator {

	private static final Logger LOG = Logger.getLogger(BankDataGenerator_AD.class.getName());
	
	static final String countryCode = "AD";
	
	BankDataGenerator_AD(String api_key) {
		super(api_key);
	}
	
	public void tryWith(String countryCode, String format, int from, int to) {
		
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
				FakeIban iban = new FakeIban(countryCode, bankCode, branchCode);
				LOG.info("id="+id + " tryWith "+iban+" bankCode "+bankCode);
				boolean found = printBankDataViaApi(bankId, iban.toString());
//				if(found) {
//					break; // inner
//				}
			}
			
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		BankDataGenerator test = new BankDataGenerator_AD(API_Key_Provider.API_KEY);

		// outerloop nur einmal!!!
		test.tryWith(countryCode, BankDataGenerator.FORMAT_04d, 0000, 1);
		
	}
}
