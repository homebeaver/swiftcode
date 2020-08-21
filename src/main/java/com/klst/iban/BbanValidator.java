package com.klst.iban;

import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.validator.routines.RegexValidator;

import com.klst.iban.Result.BankData;

public class BbanValidator {

    static final Map<String,String> BBAN_FORMATS = new Hashtable<String, String>();
    static final Map<String,BbanValidator> BBAN_DATA = new Hashtable<String, BbanValidator>();
    static {
    	BBAN_FORMATS.put("AD", "(\\d{4})(\\d{4})([A-Z0-9]{12})");
    	BBAN_FORMATS.put("AE", "(\\d{3})(\\d{16})");
//    	BBAN_FORMATS.put("AL", "(\\d{8})([A-Z0-9]{16})"); // iban.com validiert anders:
    	BBAN_FORMATS.put("AL", "(\\d{3})(\\d{4})([A-Z0-9]{17})");
    	BBAN_FORMATS.put("AT", "(\\d{5})(\\d{11})");
    	BBAN_FORMATS.put("AZ", "([A-Z]{4})([A-Z0-9]{20})");
    	BBAN_FORMATS.put("BA", "(\\d{3})(\\d{3})(\\d{8})(\\d{2})");
    	BBAN_FORMATS.put("BE", "(\\d{3})(\\d{7})(\\d{2})"                        ); // 3!n7!n2!n
    	BBAN_FORMATS.put("BG", "([A-Z]{4})(\\d{4})(\\d{2})([A-Z0-9]{8})"         ); // 4!a4!n2!n8!c
    	BBAN_FORMATS.put("BH", "([A-Z]{4})([A-Z0-9]{14})"                        ); // 4!a14!c
    	BBAN_FORMATS.put("BR", "(\\d{8})(\\d{5})(\\d{10})([A-Z]{1})([A-Z0-9]{1})"); // 8!n5!n10!n1!a1!c
    	BBAN_FORMATS.put("BY", "([A-Z0-9]{4})(\\d{4})([A-Z0-9]{16})"             ); // 4!c4!n16!c
    	BBAN_FORMATS.put("CH", "(\\d{5})([A-Z0-9]{12})"                          ); // 5!n12!c
    	BBAN_FORMATS.put("CR", "(\\d{4})(\\d{14})"                               ); // 4!n14!n
    	BBAN_FORMATS.put("CY", "(\\d{3})(\\d{5})([A-Z0-9]{16})"                  ); // 3!n5!n16!c  	
    	BBAN_FORMATS.put("CZ", "(\\d{4})(\\d{6})(\\d{10})"                       ); // 4!n6!n10!n 
    	BBAN_FORMATS.put("DE", "(\\d{8})(\\d{10})"                               ); // 8!n10!n
    	BBAN_FORMATS.put("DK", "(\\d{4})(\\d{9})(\\d{1})"                        ); // 4!n9!n1!n
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
    	BBAN_DATA.put("KW", new BbanValidator("([A-Z]{4})([A-Z0-9]{22})"        , 1)); // 4!a22!c +BranchCode
    	BBAN_DATA.put("KZ", new BbanValidator("(\\d{3})([A-Z0-9]{13})"             )); // 3!n13!c
    	BBAN_DATA.put("LB", new BbanValidator("(\\d{4})([A-Z0-9]{20})"             )); // 4!n20!c
    	BBAN_DATA.put("LC", new BbanValidator("([A-Z]{4})([A-Z0-9]{24})"           )); // 4!a24!c
    	BBAN_DATA.put("LI", new BbanValidator("(\\d{5})([A-Z0-9]{12})"             )); // 5!n12!c
    	BBAN_DATA.put("LT", new BbanValidator("(\\d{5})(\\d{11})"                  )); // 5!n11!n
    	BBAN_DATA.put("LU", new BbanValidator("(\\d{3})([A-Z0-9]{13})"             )); // 3!n13!c
    	BBAN_DATA.put("LV", new BbanValidator("([A-Z]{4})([A-Z0-9]{13})"           )); // 4!a13!c
    	// ...
    	// "XK"
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
		String countryCode = iban.substring(0, 2);
		String bban = iban.substring(4);
		BankData bankData = new BankData();
		String[] groups = this.regexValidator.match(bban);
		bankData.setCountryIso(countryCode);
		bankData.setBankIdentifier(groups[groupBankCode]);
		if(this.groupBranchCode!=null) bankData.setBranchCode(Integer.parseInt(groups[this.groupBranchCode]));
		bankData.setAccount(-1); // unbekannt bzw. anonym
    	return bankData;
    	
    }
    static String getBban(String iban, String countryCode) {
		countryCode = iban.substring(0, 2);
		return iban.substring(4); 	
    }
//    static String getBban(String iban) {
//		String countryCode = iban.substring(0, 2);
//		return iban.substring(4); 	
//    }
    static BankData XXgetBankData(BbanValidator bbanVal, String iban) {
		String countryCode = iban.substring(0, 2);
		String bban = iban.substring(4);
		BankData bankData = new BankData();
//		RegexValidator regexValidator = new RegexValidator(BbanValidator.BBAN_FORMATS.get(countryCode));
//		String[] groups = regexValidator.match(bban);
		String[] groups = bbanVal.regexValidator.match(bban);
		bankData.setCountryIso(countryCode);
		bankData.setBankIdentifier(groups[GROUP_BANK_IDENTIFIER]);
		if(bbanVal.groupBranchCode!=null) bankData.setBranchCode(Integer.parseInt(groups[bbanVal.groupBranchCode]));
		bankData.setAccount(-1); // unbekannt bzw. anonym
    	return bankData;
    }
}
