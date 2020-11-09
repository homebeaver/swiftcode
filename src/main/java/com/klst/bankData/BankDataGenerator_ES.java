package com.klst.bankData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.klst.ibanTest.API_Key_Provider;

public class BankDataGenerator_ES extends NumericBankCode {

	private static final Logger LOG = Logger.getLogger(BankDataGenerator_ES.class.getName());
	
	static final String COUNTRY_CODE = "ES";
	
    static final String ODS_RESOURCE = RESOURCE_DATA_PATH + COUNTRY_CODE+"/" +"ES_bankCode.ods";
//  nCodename	name	aCode	activeParty
	static final int COL_nID         =  0;  // Código aka bankCode
	static final int COL_Name        =  1;	// Entidad
	static final int NUMCOLUMNS      =  2;
	
	BankDataGenerator_ES(String api_key) {
		super(COUNTRY_CODE, api_key);

		refBankByCodeArray = new ArrayList<Integer>(Arrays.asList(COL_nID, COL_Name, COL_Name));
		
		this.loadBankByCode(ODS_RESOURCE, 0, NUMCOLUMNS);
	}
	
	public void tryWith(String countryCode, String format, int from, int to) {
		List<Integer> bankCodeList = bankByCode==null ? null : new ArrayList<Integer>(bankByCode.keySet());
		int counter = 0;
		for(int id=from; id<=to; id++) {
			String bankCode = String.format(format, id);
//		  int branchTo = 9999;
//		  for(int branch=0; branch<=branchTo; branch++) {
//				if(branch%100==0) {
//					int perCent = 100*branch/branchTo;
//					System.out.println(","+branch + " %:"+ perCent);
//				}
			int branch = 0;
			String branchCode = String.format(FORMAT_04d, branch);
			FakeIban iban = new FakeIban(countryCode, bankCode, branchCode);
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
		NumericBankCode test = new BankDataGenerator_ES(API_Key_Provider.API_KEY);

		test.tryWith(FORMAT_04d, 0, 9999);
	}

}
