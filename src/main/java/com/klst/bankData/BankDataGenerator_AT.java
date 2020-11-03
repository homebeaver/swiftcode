package com.klst.bankData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import com.klst.ibanTest.API_Key_Provider;

public class BankDataGenerator_AT extends NumericBankCode {

	private static final Logger LOG = Logger.getLogger(BankDataGenerator_AT.class.getName());

	static final String COUNTRY_CODE = "AT";
    static final String ODS_RESOURCE = RESOURCE_DATA_PATH + COUNTRY_CODE+"/" +"sepa-zv-vz_gesamt.ods";

	static final int COL_Kennzeichen      =  0;
	static final int COL_Identnummer      =  1;
	static final int COL_Bankleitzahl     =  2;  // aka bankCode
	static final int COL_Institutsart     =  3;
	static final int COL_Sektor           =  4;
	static final int COL_Firmenbuchnummer =  5;
	static final int COL_Bankenname       =  6;
	static final int COL_Strasse          =  7;
	static final int COL_PLZ              =  8;
	static final int COL_Ort              =  9;
	static final int COL_Postadresse_Str  = 10;
	static final int COL_Postadresse_PLZ  = 11;
	static final int COL_Postadresse_Ort  = 12;
	static final int COL_Postfach         = 13;
	static final int COL_Bundesland       = 14;
	static final int COL_Telefon          = 15;
	static final int COL_Fax              = 16;
	static final int COL_eMail            = 17;
	static final int COL_SWIFT_Code       = 18;
	static final int COL_Homepage         = 19;
	static final int COL_Gruendungsdatum  = 20;
	static final int COL_SWIFT8           = 21;
	static final int COL_passive          = 22;
	static final int NUMCOLUMNS           = 23;
	
	static final int COL_nID = COL_Bankleitzahl;
	static final int COL_BIC = COL_SWIFT_Code;
	static final int COL_Name = COL_Bankenname;
	
	BankDataGenerator_AT(String api_key) {
		super(COUNTRY_CODE, api_key);

		columnMapper = new ArrayList<Object>(Arrays.asList( // nur size wird in super benötigt!
				COL_Kennzeichen ,
				COL_Identnummer ,
				COL_Bankleitzahl ,
				COL_Institutsart ,
				COL_Sektor ,
				COL_Firmenbuchnummer ,
				COL_Bankenname ,
				COL_Strasse ,
				COL_PLZ ,
				COL_Ort ,
				COL_Postadresse_Str ,
				COL_Postadresse_PLZ ,
				COL_Postadresse_Ort ,
				COL_Postfach ,
				COL_Bundesland ,
				COL_Telefon ,
				COL_Fax ,
				COL_eMail ,
				COL_SWIFT_Code ,
				COL_Homepage ,
				COL_Gruendungsdatum ,
				COL_SWIFT8 ,
				COL_passive ));
		refBankByCodeArray = new ArrayList<Integer>(Arrays.asList(COL_nID, COL_BIC, COL_Name));

		this.loadBankByCode(ODS_RESOURCE);
	}

	public static void main(String[] args) throws Exception {
		NumericBankCode test = new BankDataGenerator_AT(API_Key_Provider.API_KEY);

		test.tryWith(FORMAT_05d, 00000, 99999);
	}

}
