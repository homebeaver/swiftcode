package com.klst.bankData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.klst.ibanTest.API_Key_Provider;

/*

es gibt pro bank IID http://www.fbf.fr/fr/la-federation-bancaire-francaise/adherents
mehrere Filialen/Zweigstelle mit branchCode (code guichet/Zweigstelle).
Zu den 209 IID wurden nur 176 mit Filiale 00000 bei iban.com gefunden.
- BAYERISCHE LANDESBANK	10108 hat demnach keine Filiale in FR ? Auch die Suche 0<=branch<=99999 bestätigt es!


Beide, IID und code guichet, haben das Format FORMAT_05d. BankId wird daher 10-stellig: "id":1009618816

 */
public class BankDataGenerator_FR extends NumericBankCode {

	private static final Logger LOG = Logger.getLogger(BankDataGenerator_FR.class.getName());
	
	static final String COUNTRY_CODE = "FR";
	
    static final String ODS_RESOURCE = RESOURCE_DATA_PATH + COUNTRY_CODE+"/" +"fr-banken-tsv.ods";
//  nCodename	name	aCode	activeParty
	static final int COL_Name        =  0;
	static final int COL_nID         =  1;  // aka bankCode
	static final int COL_www         =  2;
	static final int NUMCOLUMNS      =  3;
	
	BankDataGenerator_FR(String api_key) {
		super(COUNTRY_CODE, api_key);

		refBankByCodeArray = new ArrayList<Integer>(Arrays.asList(COL_nID, COL_www, COL_Name));
		
		this.loadBankByCode(ODS_RESOURCE, 0, NUMCOLUMNS);
	}
	
	public void tryWith(String countryCode, String format, int from, int to) {
		List<Integer> bankCodeList = bankByCode==null ? null : new ArrayList<Integer>(bankByCode.keySet());
		int counter = 0;
		for(int id=from; id<=to; id++) {
			String bankCode = String.format(format, id);
//		  for(int branch=0; branch<=99999; branch++) {
//				if(branch%100==0) {
//					int perCent = 100*branch/99999;
//					System.out.println(","+branch + " %:"+ perCent);
//				}
			int branch = 0;
			String branchCode = String.format(FORMAT_05d, branch);
			FakeIban iban = new FakeIban(countryCode, bankCode, branchCode);
			// FR7610000123451234567890107
			// FR9945850000000500013M02606
			if(bankCodeList==null) {
				// suchen
				LOG.info("id="+id + " tryWith "+iban+" bankCode "+bankCode);
//    			if(printBankDataViaApi(id, iban)) counter++;
			} else if(bankCodeList.contains(id)) {
    			LOG.info("id="+id + " do "+iban+" bankCode "+bankCode);
    			if(printBankDataViaApi(id, iban)) counter++;
			} else {
				// unnötige iban.com Abfrage
			}
//		  }
		}
		System.out.println("done "+counter + "/"+ (bankCodeList==null ? to : bankByCode.size()));
	}

	public static void main(String[] args) throws Exception {
		NumericBankCode test = new BankDataGenerator_FR(API_Key_Provider.API_KEY);

		test.tryWith(FORMAT_05d, 10000, 99999);
	}

}
