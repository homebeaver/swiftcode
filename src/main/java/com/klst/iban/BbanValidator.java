package com.klst.iban;

import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.validator.routines.RegexValidator;

import com.klst.iban.Result.BankData;

public class BbanValidator {

    static final Map<String,String> BBAN_FORMATS = new Hashtable<String, String>();
    static final Map<String,BbanValidator> BBAN_DATA = new Hashtable<String, BbanValidator>();
    static {
    	BBAN_DATA.put("AD", new BbanValidator("(\\d{4})(\\d{4})([A-Z0-9]{12})"  , 1)); // 4!n4!n12!c +BranchCode
    	BBAN_DATA.put("AE", new BbanValidator("(\\d{3})(\\d{16})"                  )); // 3!n16!n
    	BBAN_DATA.put("AL", new BbanValidator("(\\d{3})(\\d{4})([A-Z0-9]{17})"  , 1)); // 8!n16!c BankCode:3!n +BranchCode:3!n Kontrollzeichen+account
    	BBAN_DATA.put("AT", new BbanValidator("(\\d{5})(\\d{11})"                  )); // 5!n11!n
    	BBAN_DATA.put("AZ", new BbanValidator("([A-Z]{4})([A-Z0-9]{20})"           )); // 4!a20!c
    	BBAN_DATA.put("BA", new BbanValidator("(\\d{3})(\\d{3})(\\d{10})"       , 1)); // 3!n3!n8!n2!n +BranchCode account+Kontrollzeichen
    	BBAN_DATA.put("BE", new BbanValidator("(\\d{3})(\\d{9})"                   )); // 3!n7!n2!n account+Kontrollzeichen
    	BBAN_DATA.put("BG", new BbanValidator("([A-Z]{4})(\\d{4})([A-Z0-9]{10})", 1)); // 4!a4!n2!n8!c +BranchCode Kontrollzeichen+account
    	BBAN_DATA.put("BH", new BbanValidator("([A-Z]{4})([A-Z0-9]{14})"           )); // 4!a14!c
    	BBAN_DATA.put("BR", new BbanValidator("(\\d{8})(\\d{5})([A-Z0-9]{12})"  , 1)); // 8!n5!n10!n1!a1!c +BranchCode account+Kontrollzeichen
    	BBAN_DATA.put("BY", new BbanValidator("([A-Z0-9]{4})(\\d{4})([A-Z0-9]{16})", 1)); // 4!c4!n16!c +BranchCode
    	BBAN_DATA.put("CH", new BbanValidator("(\\d{5})([A-Z0-9]{12})"             )); // 5!n12!c
    	BBAN_DATA.put("CR", new BbanValidator("(\\d{4})(\\d{14})"                  )); // 4!n14!n
    	BBAN_DATA.put("CY", new BbanValidator("(\\d{3})(\\d{5})([A-Z0-9]{16})"  , 1)); // 3!n5!n16!c +BranchCode
    	BBAN_DATA.put("CZ", new BbanValidator("(\\d{4})(\\d{16})"                  )); // 4!n6!n10!n 
    	BBAN_DATA.put("DE", new BbanValidator("(\\d{8})(\\d{10})"                  )); // 8!n10!n
    	BBAN_DATA.put("DK", new BbanValidator("(\\d{4})(\\d{10})"                  )); // 4!n9!n1!n account+Kontrollzeichen (Großbuchstabe oder Ziffer)
    	BBAN_DATA.put("DO", new BbanValidator("([A-Z0-9]{4})(\\d{20})"             )); // 4!c20!n
    	BBAN_DATA.put("EE", new BbanValidator("(\\d{2})(\\d{14})"                  )); // 2!n2!n11!n1!n account+Kontrollzeichen (Großbuchstabe oder Ziffer)
    	BBAN_DATA.put("EG", new BbanValidator("(\\d{4})(\\d{4})(\\d{17})"       , 1)); // 4!n4!n17!n +BranchCode
    	BBAN_DATA.put("ES", new BbanValidator("(\\d{4})(\\d{4})(\\d{12})"       , 1)); // 4!n4!n1!n1!n10!n +BranchCode Kontrollzeichen+account 
    	BBAN_DATA.put("FI", new BbanValidator("(\\d{3})(\\d{11})"                  )); // 3!n11!n
    	BBAN_DATA.put("FO", new BbanValidator("(\\d{4})(\\d{10})"                  )); // 4!n9!n1!n account+Kontrollzeichen (Großbuchstabe oder Ziffer)
    	BBAN_DATA.put("FR", new BbanValidator("(\\d{5})(\\d{5})([A-Z0-9]{13})"  , 1)); // 5!n5!n11!c2!n +BranchCode account+Kontrollzeichen (Großbuchstabe oder Ziffer)
    	BBAN_DATA.put("GB", new BbanValidator("([A-Z]{4})(\\d{6})(\\d{8})"      , 1)); // 4!a6!n8!n +BranchCode
    	BBAN_DATA.put("GE", new BbanValidator("([A-Z]{2})(\\d{16})"                )); // 2!a16!n
    	BBAN_DATA.put("GI", new BbanValidator("([A-Z]{4})([A-Z0-9]{15})"           )); // 4!a15!c
    	BBAN_DATA.put("GL", new BbanValidator("(\\d{4})(\\d{10})"                  )); // 4!n9!n1!n account+Kontrollzeichen (Großbuchstabe oder Ziffer)
    	BBAN_DATA.put("GR", new BbanValidator("(\\d{3})(\\d{4})([A-Z0-9]{16})"  , 1)); // 3!n4!n16!c +BranchCode
    	BBAN_DATA.put("GT", new BbanValidator("([A-Z0-9]{4})([A-Z0-9]{20})"        )); // 4!c20!c
    	BBAN_DATA.put("HR", new BbanValidator("(\\d{7})(\\d{10})"                  )); // 7!n10!n
    	BBAN_DATA.put("HU", new BbanValidator("(\\d{3})(\\d{4})(\\d{17})"       , 1)); // 3!n4!n1!n15!n1!n +BranchCode account+Kontrollzeichen (Großbuchstabe oder Ziffer)
    	BBAN_DATA.put("IE", new BbanValidator("([A-Z]{4})(\\d{6})(\\d{8})"      , 1)); // 4!a6!n8!n +BranchCode
    	BBAN_DATA.put("IL", new BbanValidator("(\\d{3})(\\d{3})(\\d{13})"       , 1)); // 3!n3!n13!n +BranchCode
    	BBAN_DATA.put("IQ", new BbanValidator("([A-Z]{4})(\\d{3})(\\d{12})"     , 1)); // 4!a3!n12!n +BranchCode
    	BBAN_DATA.put("IS", new BbanValidator("(\\d{4})(\\d{18})"                  )); // 4!n2!n6!n10!n Kontrollzeichen+account+sonstige
    	BBAN_DATA.put("IT", new BbanValidator("([A-Z]{1})(\\d{5})(\\d{5})([A-Z0-9]{12})", 2, 1)); // 1!a5!n5!n12!c Kontrollzeichen+BankCode+BranchCode+account
    	BBAN_DATA.put("JO", new BbanValidator("([A-Z]{4})(\\d{4})([A-Z0-9]{18})", 1)); // 4!a4!n18!c +BranchCode (nicht bei iban.com)
    	BBAN_DATA.put("KW", new BbanValidator("([A-Z]{4})([A-Z0-9]{22})"           )); // 4!a22!c
    	BBAN_DATA.put("KZ", new BbanValidator("(\\d{3})([A-Z0-9]{13})"             )); // 3!n13!c
    	BBAN_DATA.put("LB", new BbanValidator("(\\d{4})([A-Z0-9]{20})"             )); // 4!n20!c
    	BBAN_DATA.put("LC", new BbanValidator("([A-Z]{4})([A-Z0-9]{24})"           )); // 4!a24!c
    	BBAN_DATA.put("LI", new BbanValidator("(\\d{5})([A-Z0-9]{12})"             )); // 5!n12!c
    	BBAN_DATA.put("LT", new BbanValidator("(\\d{5})(\\d{11})"                  )); // 5!n11!n
    	BBAN_DATA.put("LU", new BbanValidator("(\\d{3})([A-Z0-9]{13})"             )); // 3!n13!c
    	BBAN_DATA.put("LV", new BbanValidator("([A-Z]{4})([A-Z0-9]{13})"           )); // 4!a13!c
    	BBAN_DATA.put("MC", new BbanValidator("(\\d{5})(\\d{5})([A-Z0-9]{13})"  , 1)); // 5!n5!n11!c2!n +BranchCode account+Kontrollzeichen
    	BBAN_DATA.put("MD", new BbanValidator("([A-Z0-9]{2})([A-Z0-9]{18})"        )); // 2!c18!c  	
    	BBAN_DATA.put("ME", new BbanValidator("(\\d{3})(\\d{15})"                  )); // 3!n13!n2!n account+Kontrollzeichen (Großbuchstabe oder Ziffer)
       	BBAN_DATA.put("MK", new BbanValidator("(\\d{3})([A-Z0-9]{12})"             )); // 3!n10!c2!n
    	BBAN_DATA.put("MR", new BbanValidator("(\\d{5})(\\d{5})(\\d{13})"       , 1)); // 5!n5!n11!n2!n +BranchCode account+Kontrollzeichen (Großbuchstabe oder Ziffer)
    	BBAN_DATA.put("MT", new BbanValidator("([A-Z]{4})(\\d{5})([A-Z0-9]{18})", 1)); // 4!a5!n18!c + BranchCode
    	BBAN_DATA.put("MU", new BbanValidator("([A-Z]{4})(\\d{2})([A-Z0-9]{20})", 1)); // 4!a2!n2!n12!n3!n3!a +BranchCode account+Kontrollzeichen (Großbuchstabe oder Ziffer)
    	BBAN_DATA.put("NL", new BbanValidator("([A-Z]{4})(\\d{10})"                )); // 4!a10!n
    	BBAN_DATA.put("NO", new BbanValidator("(\\d{4})(\\d{7})"                   )); // 4!n6!n1!n
    	BBAN_DATA.put("PK", new BbanValidator("([A-Z]{4})([A-Z0-9]{16})"           )); // 4!a16!c
    	BBAN_DATA.put("PL", new BbanValidator("(\\d{8})(\\d{16})"                  )); // 8!n16!n
    	BBAN_DATA.put("PS", new BbanValidator("([A-Z]{4})([A-Z0-9]{21})"           )); // 4!a21!c
    	BBAN_DATA.put("PT", new BbanValidator("(\\d{4})(\\d{4})(\\d{13})"       , 1)); // 4!n4!n11!n2!n +BranchCode account+Kontrollzeichen (Großbuchstabe oder Ziffer)
    	BBAN_DATA.put("QA", new BbanValidator("([A-Z]{4})([A-Z0-9]{21})"           )); // 4!a21!c
    	BBAN_DATA.put("RO", new BbanValidator("([A-Z]{4})([A-Z0-9]{16})"           )); // 4!a16!c
    	BBAN_DATA.put("RS", new BbanValidator("(\\d{3})(\\d{15})"                  )); // 3!n13!n2!n
    	BBAN_DATA.put("SA", new BbanValidator("(\\d{2})([A-Z0-9]{18})"             )); // 2!n18!c
    	BBAN_DATA.put("SC", new BbanValidator("([A-Z]{4})([A-Z0-9]{23})"           )); // 4!a2!n2!n16!n3!a BankCode:4!a2!n2!n iban.com nur 4!a account+Kontrollzeichen+sonstige
    	BBAN_DATA.put("SE", new BbanValidator("(\\d{3})(\\d{17})"                  )); // 3!n16!n1!n
    	BBAN_DATA.put("SI", new BbanValidator("(\\d{5})(\\d{10})"                  )); // 5!n8!n2!n
    	BBAN_DATA.put("SK", new BbanValidator("(\\d{4})(\\d{16})"                  )); // 4!n6!n10!n
    	BBAN_DATA.put("SM", new BbanValidator("([A-Z]{1})(\\d{5})(\\d{5})([A-Z0-9]{12})", 2, 1)); // 1!a5!n5!n12!c Kontrollzeichen+BankCode+BranchCode+account
    	BBAN_DATA.put("ST", new BbanValidator("(\\d{4})(\\d{4})(\\d{13})"       , 1)); // 8!n11!n2!n +BranchCode account+Kontrollzeichen (Großbuchstabe oder Ziffer)
    	BBAN_DATA.put("SV", new BbanValidator("([A-Z]{4})(\\d{20})"                )); // 4!a20!n
    	BBAN_DATA.put("TL", new BbanValidator("(\\d{3})(\\d{16})"                  )); // 3!n14!n2!n
    	BBAN_DATA.put("TN", new BbanValidator("(\\d{2})(\\d{3})(\\d{15})"       , 1)); // 2!n3!n13!n2!n +BranchCode account+Kontrollzeichen
    	BBAN_DATA.put("TR", new BbanValidator("(\\d{5})(\\d{17})"                  )); // 5!n1!n16!c Kontrollzeichen+account
    	BBAN_DATA.put("UA", new BbanValidator("(\\d{6})([A-Z0-9]{19})"             )); // 6!n19!c
    	BBAN_DATA.put("VA", new BbanValidator("(\\d{3})(\\d{15})"                  )); // 3!n15!n
    	BBAN_DATA.put("VG", new BbanValidator("([A-Z]{4})(\\d{16})"                )); // 4!a16!n
    	BBAN_DATA.put("XK", new BbanValidator("(\\d{2})(\\d{2})(\\d{12})"       , 1)); // 4!n10!n2!n +BranchCode account+Kontrollzeichen
    }
    
    static final int GROUP_BANK_IDENTIFIER = 0;
    
    String format;
    RegexValidator regexValidator;
    Integer groupBankCode = null; // default GROUP_BANK_IDENTIFIER, but 1 for IT
    Integer groupBranchCode = null;
    
    BbanValidator(String format) {
    	this.format = format;
    	this.regexValidator = new RegexValidator(format);
    	this.groupBankCode = GROUP_BANK_IDENTIFIER;
    }
    
    BbanValidator(String format, int groupBranchCode) {
    	this(format);
     	this.groupBranchCode = groupBranchCode;
    }
    BbanValidator(String format, int groupBranchCode, int groupBankCode) {
    	this(format, groupBranchCode);
     	this.groupBankCode = groupBankCode;
    }
   
    public BankData getBankData(String iban) {
		BankData bankData = new BankData();
		String bban = getBban(iban, bankData);
		String[] groups = this.regexValidator.match(bban);
		bankData.setBankIdentifier(groups[groupBankCode]);
		if(this.groupBranchCode!=null) bankData.setBranchCode(Integer.parseInt(groups[this.groupBranchCode]));
		bankData.setAccount(-1); // unbekannt bzw. anonym
    	return bankData;
    	
    }
    
    private static String getBban(String iban, BankData bankData) {
    	String countryCode = iban.substring(0, 2);
    	bankData.setCountryIso(countryCode);
		return iban.substring(4); 	
    }
}
