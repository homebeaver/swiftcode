package com.klst.iban;

import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.validator.routines.RegexValidator;

import com.klst.iban.Result.BankData;

/* see https://en.wikipedia.org/wiki/International_Bank_Account_Number
 *     https://www.ecbs.org/iban.htm
 * IBAN consists of up to 34 alphanumeric characters, as follows:
    country code using ISO 3166-1 alpha-2 – two letters,
    check digits – two digits, and
    Basic Bank Account Number (BBAN) – up to 30 alphanumeric characters that are country-specific.
    BBAN format is decided by the national central bank or designated payment authority of each country.
    
   You can retrieve BIC from an IBAN with IBAN Validation tool: https://www.iban.com/iban-checker
   
   Bban.getBankData(String iban) extracts BankData from a valid iban
   

- ES includes: Canary Islands (IC), Ceuta and Melilla (EA)   
- FI includes: Aland Islands (AX)
- GB includes: Isle of Man (IM), Guernsey (GG), Jersey (JE), Channel Islands
- PT includes: Azores and Madeira
- FR includes:
   GY French Guyana, 
   PF French Polynesia, 
   TF French Southern Territories, 
   GP Guadeloupe, 
   MQ Martinique, 
   YT Mayotte, 
   NC New Caledonia, 
   RE Réunion, 
   BL Saint Barthélemy, 
   MF Saint Martin (French part), 
   PM Saint Pierre and Miquelon, 
   WF Wallis and Futuna Islands 
   have their own ISO country code but use "FR" as their IBAN country code.
   
 */
public class Bban {

    static final Map<String,Bban> BBAN = new Hashtable<String, Bban>();
    static {
    	BBAN.put("AD", new Bban("(\\d{4})(\\d{4})([A-Z0-9]{12})"  , 1)); // 4!n4!n12!c +BranchCode
    	BBAN.put("AE", new Bban("(\\d{3})(\\d{16})"                  )); // 3!n16!n
    	BBAN.put("AL", new Bban("(\\d{3})(\\d{4})([A-Z0-9]{17})"  , 1)); // 8!n16!c BankCode:3!n +BranchCode:3!n Kontrollzeichen+account
    	BBAN.put("AT", new Bban("(\\d{5})(\\d{11})"                  )); // 5!n11!n
    	BBAN.put("AZ", new Bban("([A-Z]{4})([A-Z0-9]{20})"           )); //N4!a20!c
    	BBAN.put("BA", new Bban("(\\d{3})(\\d{3})(\\d{10})"       , 1)); // 3!n3!n8!n2!n +BranchCode account+Kontrollzeichen
    	BBAN.put("BE", new Bban("(\\d{3})(\\d{9})"                   )); // 3!n7!n2!n account+Kontrollzeichen
       	BBAN.put("BG", new Bban("([A-Z]{4})(\\d{4})([A-Z0-9]{10})", 1)); //S4!a4!n2!n8!c +BranchCode Kontrollzeichen+account
    	BBAN.put("BH", new Bban("([A-Z]{4})([A-Z0-9]{14})"           )); //N4!a14!c
    	BBAN.put("BR", new Bban("(\\d{8})(\\d{5})([A-Z0-9]{12})"  , 1)); // 8!n5!n10!n1!a1!c +BranchCode account+Kontrollzeichen
    	BBAN.put("BY", new Bban("([A-Z0-9]{4})(\\d{4})([A-Z0-9]{16})", 1)); //N4!c4!n16!c +BranchCode
    	BBAN.put("CH", new Bban("(\\d{5})([A-Z0-9]{12})"             )); // 5!n12!c
    	BBAN.put("CR", new Bban("(\\d{4})(\\d{14})"                  )); // 4!n14!n
    	BBAN.put("CY", new Bban("(\\d{3})(\\d{5})([A-Z0-9]{16})"  , 1)); // 3!n5!n16!c +BranchCode
    	BBAN.put("CZ", new Bban("(\\d{4})(\\d{16})"                  )); // 4!n6!n10!n 
    	BBAN.put("DE", new Bban("(\\d{8})(\\d{10})"                  )); // 8!n10!n
    	BBAN.put("DK", new Bban("(\\d{4})(\\d{10})"                  )); // 4!n9!n1!n account+Kontrollzeichen (Großbuchstabe oder Ziffer)
    	BBAN.put("DO", new Bban("([A-Z0-9]{4})(\\d{20})"             )); //N4!c20!n
    	BBAN.put("EE", new Bban("(\\d{2})(\\d{14})"                  )); // 2!n2!n11!n1!n account+Kontrollzeichen (Großbuchstabe oder Ziffer)
    	BBAN.put("EG", new Bban("(\\d{4})(\\d{4})(\\d{17})"       , 1)); // 4!n4!n17!n +BranchCode
    	BBAN.put("ES", new Bban("(\\d{4})(\\d{4})(\\d{12})"       , 1)); // 4!n4!n1!n1!n10!n +BranchCode Kontrollzeichen+account 
    	BBAN.put("FI", new Bban("(\\d{3})(\\d{11})"                  )); // 3!n11!n
    	BBAN.put("FO", new Bban("(\\d{4})(\\d{10})"                  )); // 4!n9!n1!n account+Kontrollzeichen (Großbuchstabe oder Ziffer)
    	BBAN.put("FR", new Bban("(\\d{5})(\\d{5})([A-Z0-9]{13})"  , 1)); // 5!n5!n11!c2!n +BranchCode account+Kontrollzeichen (Großbuchstabe oder Ziffer)
    	BBAN.put("GB", new Bban("([A-Z]{4})(\\d{6})(\\d{8})"      , 1)); //S4!a6!n8!n +BranchCode
    	BBAN.put("GE", new Bban("([A-Z]{2})(\\d{16})"                )); // 2!a16!n
    	BBAN.put("GG", new Bban("([A-Z]{4})(\\d{6})(\\d{8})"      , 1)); //S4!a6!n8!n +BranchCode, included in GB
    	BBAN.put("GI", new Bban("([A-Z]{4})([A-Z0-9]{15})"           )); //S4!a15!c
    	BBAN.put("GL", new Bban("(\\d{4})(\\d{10})"                  )); // 4!n9!n1!n account+Kontrollzeichen (Großbuchstabe oder Ziffer)
    	BBAN.put("GR", new Bban("(\\d{3})(\\d{4})([A-Z0-9]{16})"  , 1)); // 3!n4!n16!c +BranchCode
    	BBAN.put("GT", new Bban("([A-Z0-9]{4})([A-Z0-9]{20})"        )); //N4!c20!c
    	BBAN.put("HR", new Bban("(\\d{7})(\\d{10})"                  )); // 7!n10!n
    	BBAN.put("HU", new Bban("(\\d{3})(\\d{4})(\\d{17})"       , 1)); // 3!n4!n1!n15!n1!n +BranchCode account+Kontrollzeichen (Großbuchstabe oder Ziffer)
    	BBAN.put("IE", new Bban("([A-Z]{4})(\\d{6})(\\d{8})"      , 1)); //S4!a6!n8!n +BranchCode
    	BBAN.put("IL", new Bban("(\\d{3})(\\d{3})(\\d{13})"       , 1)); // 3!n3!n13!n +BranchCode
    	BBAN.put("IM", new Bban("([A-Z]{4})(\\d{6})(\\d{8})"      , 1)); //S4!a6!n8!n +BranchCode, included in GB
    	BBAN.put("IQ", new Bban("([A-Z]{4})(\\d{3})(\\d{12})"     , 1)); //N4!a3!n12!n +BranchCode
    	BBAN.put("IS", new Bban("(\\d{4})(\\d{18})"                  )); // 4!n2!n6!n10!n Kontrollzeichen+account+sonstige
    	BBAN.put("IT", new Bban("([A-Z]{1})(\\d{5})(\\d{5})([A-Z0-9]{12})", 2, 1)); // 1!a5!n5!n12!c Kontrollzeichen+BankCode+BranchCode+account
    	BBAN.put("JE", new Bban("([A-Z]{4})(\\d{6})(\\d{8})"      , 1)); //S4!a6!n8!n +BranchCode, included in GB
    	BBAN.put("JO", new Bban("([A-Z]{4})(\\d{4})([A-Z0-9]{18})", 1)); //N4!a4!n18!c +BranchCode (nicht bei iban.com)
    	BBAN.put("KW", new Bban("([A-Z]{4})([A-Z0-9]{22})"           )); //N4!a22!c
    	BBAN.put("KZ", new Bban("(\\d{3})([A-Z0-9]{13})"             )); // 3!n13!c
    	BBAN.put("LB", new Bban("(\\d{4})([A-Z0-9]{20})"             )); // 4!n20!c
    	BBAN.put("LC", new Bban("([A-Z]{4})([A-Z0-9]{24})"           )); //N4!a24!c
    	BBAN.put("LI", new Bban("(\\d{5})([A-Z0-9]{12})"             )); // 5!n12!c
    	BBAN.put("LT", new Bban("(\\d{5})(\\d{11})"                  )); // 5!n11!n
    	BBAN.put("LU", new Bban("(\\d{3})([A-Z0-9]{13})"             )); // 3!n13!c
    	BBAN.put("LV", new Bban("([A-Z]{4})([A-Z0-9]{13})"           )); //S4!a13!c
    	BBAN.put("LY", new Bban("(\\d{3})(\\d{3})(\\d{15})"       , 1)); // 3!n3!n15!n
    	BBAN.put("MC", new Bban("(\\d{5})(\\d{5})([A-Z0-9]{13})"  , 1)); // 5!n5!n11!c2!n +BranchCode account+Kontrollzeichen
    	BBAN.put("MD", new Bban("([A-Z0-9]{2})([A-Z0-9]{18})"        )); // 2!c18!c  	
    	BBAN.put("ME", new Bban("(\\d{3})(\\d{15})"                  )); // 3!n13!n2!n account+Kontrollzeichen (Großbuchstabe oder Ziffer)
       	BBAN.put("MK", new Bban("(\\d{3})([A-Z0-9]{12})"             )); // 3!n10!c2!n
    	BBAN.put("MR", new Bban("(\\d{5})(\\d{5})(\\d{13})"       , 1)); // 5!n5!n11!n2!n +BranchCode account+Kontrollzeichen (Großbuchstabe oder Ziffer)
    	BBAN.put("MT", new Bban("([A-Z]{4})(\\d{5})([A-Z0-9]{18})", 1)); //S4!a5!n18!c + BranchCode
    	BBAN.put("MU", new Bban("([A-Z]{4})(\\d{2})([A-Z0-9]{20})", 1)); //N4!a2!n2!n12!n3!n3!a +BranchCode account+Kontrollzeichen (Großbuchstabe oder Ziffer)
    	BBAN.put("NL", new Bban("([A-Z]{4})(\\d{10})"                )); //S4!a10!n
    	BBAN.put("NO", new Bban("(\\d{4})(\\d{7})"                   )); // 4!n6!n1!n
    	BBAN.put("PK", new Bban("([A-Z]{4})([A-Z0-9]{16})"           )); //N4!a16!c
    	BBAN.put("PL", new Bban("(\\d{8})(\\d{16})"                  )); // 8!n16!n
    	BBAN.put("PS", new Bban("([A-Z]{4})([A-Z0-9]{21})"           )); //N4!a21!c
    	BBAN.put("PT", new Bban("(\\d{4})(\\d{4})(\\d{13})"       , 1)); // 4!n4!n11!n2!n +BranchCode account+Kontrollzeichen (Großbuchstabe oder Ziffer)
    	BBAN.put("QA", new Bban("([A-Z]{4})([A-Z0-9]{21})"           )); //N4!a21!c
    	BBAN.put("RO", new Bban("([A-Z]{4})([A-Z0-9]{16})"           )); //S4!a16!c
    	BBAN.put("RS", new Bban("(\\d{3})(\\d{15})"                  )); // 3!n13!n2!n
    	BBAN.put("SA", new Bban("(\\d{2})([A-Z0-9]{18})"             )); // 2!n18!c
    	BBAN.put("SC", new Bban("([A-Z]{4})([A-Z0-9]{23})"           )); //N4!a2!n2!n16!n3!a BankCode:4!a2!n2!n iban.com nur 4!a account+Kontrollzeichen+sonstige
    	BBAN.put("SE", new Bban("(\\d{3})(\\d{17})"                  )); // 3!n16!n1!n
    	BBAN.put("SI", new Bban("(\\d{5})(\\d{10})"                  )); // 5!n8!n2!n
    	BBAN.put("SK", new Bban("(\\d{4})(\\d{16})"                  )); // 4!n6!n10!n
    	BBAN.put("SM", new Bban("([A-Z]{1})(\\d{5})(\\d{5})([A-Z0-9]{12})", 2, 1)); // 1!a5!n5!n12!c Kontrollzeichen+BankCode+BranchCode+account
    	BBAN.put("ST", new Bban("(\\d{4})(\\d{4})(\\d{13})"       , 1)); // 8!n11!n2!n +BranchCode account+Kontrollzeichen (Großbuchstabe oder Ziffer)
    	BBAN.put("SV", new Bban("([A-Z]{4})(\\d{20})"                )); //N4!a20!n
    	BBAN.put("TL", new Bban("(\\d{3})(\\d{16})"                  )); // 3!n14!n2!n
    	BBAN.put("TN", new Bban("(\\d{2})(\\d{3})(\\d{15})"       , 1)); // 2!n3!n13!n2!n +BranchCode account+Kontrollzeichen
    	BBAN.put("TR", new Bban("(\\d{5})(\\d{17})"                  )); // 5!n1!n16!c Kontrollzeichen+account
    	BBAN.put("UA", new Bban("(\\d{6})([A-Z0-9]{19})"             )); // 6!n19!c
    	BBAN.put("VA", new Bban("(\\d{3})(\\d{15})"                  )); // 3!n15!n
    	BBAN.put("VG", new Bban("([A-Z]{4})(\\d{16})"                )); //N4!a16!n
    	BBAN.put("XK", new Bban("(\\d{2})(\\d{2})(\\d{12})"       , 1)); // 4!n10!n2!n +BranchCode account+Kontrollzeichen
    	// IBAN.com Countries which have Partial/Experimental use of the IBAN system :
    	BBAN.put("AO", new Bban("(\\d{4})(\\d{4})(\\d{11})"       , 1)); // lt wikipedia + KK: bbbb ssss kkkk kkkk kkkK K
       	BBAN.put("BF", new Bban("([A-Z]{5})(\\d{5})(\\d{14})"     , 1)); // BJbb bsss sskk kkkk kkkk kkKK
       	BBAN.put("BI", new Bban("(\\d{3})(\\d{9})"                   )); // BIpp kkkk kkkk kkkk
       	BBAN.put("BJ", new Bban("([A-Z]{5})(\\d{5})(\\d{14})"     , 1)); // BJbb bsss sskk kkkk kkkk kkKK
    	BBAN.put("CF", new Bban("(\\d{5})(\\d{5})(\\d{13})"       , 1)); // bbbb bsss sskk kkkk kkkk kKK
    	BBAN.put("CG", new Bban("(\\d{5})(\\d{5})(\\d{13})"       , 1)); // bbbb bsss sskk kkkk kkkk kKK
    	BBAN.put("CI", new Bban("CI(\\d{3})(\\d{5})(\\d{14})"     , 1)); // CIbb bsss sskk kkkk kkkk kkKK
    	BBAN.put("CM", new Bban("(\\d{5})(\\d{5})(\\d{13})"       , 1)); // bbbb bsss sskk kkkk kkkk kKK
    	BBAN.put("CV", new Bban("(\\d{4})(\\d{4})(\\d{13})"       , 1)); // bbbb ssss kkkk kkkk kkkK K 
    	BBAN.put("DJ", new Bban("(\\d{5})(\\d{5})(\\d{13})"       , 1)); // 
    	BBAN.put("DZ", new Bban("(\\d{3})(\\d{5})(\\d{12})"       , 1)); // bbbs ssss kkkk kkkk kkKK
    	BBAN.put("GA", new Bban("(\\d{5})(\\d{5})(\\d{13})"       , 1)); // bbbb bsss sskk kkkk kkkk kKK
    	BBAN.put("GQ", new Bban("(\\d{5})(\\d{5})(\\d{13})"       , 1)); // 
       	BBAN.put("GW", new Bban("([A-Z]{5})(\\d{5})(\\d{11})"     , 1)); // GWbb bsss sskk kkkk kkkk k
       	BBAN.put("HN", new Bban("([A-Z]{4})(\\d{20})"                )); // 
    	BBAN.put("IR", new Bban("(\\d{3})(\\d{3})(\\d{6})(\\d{10})", 2, 0)); // kkkk kkkk kkkk kkkk kkkk kk
    	BBAN.put("KM", new Bban("(\\d{5})(\\d{5})(\\d{13})"       , 1)); // 
    	BBAN.put("MA", new Bban("(\\d{2})(\\d{3})(\\d{5})(\\d{14})", 2, 1)); // 
    	BBAN.put("MG", new Bban("(\\d{3})(\\d{20})"                  )); // bbbb bsss sskk kkkk kkkk kKK
    	BBAN.put("ML", new Bban("([A-Z]{5})(\\d{5})(\\d{14})"     , 1)); // bbbb bsss sskk kkkk kkkk kkKK
    	BBAN.put("MZ", new Bban("(\\d{4})(\\d{4})(\\d{13})"       , 1)); // bbbb ssss kkkk kkkk kkkK K
    	BBAN.put("NE", new Bban("NE(\\d{3})(\\d{5})(\\d{14})"     , 1)); // 
    	BBAN.put("NI", new Bban("([A-Z]{4})(\\d{24})"                )); // 
    	BBAN.put("SN", new Bban("SN(\\d{3})(\\d{5})(\\d{14})"     , 1)); // bbbb bsss sskk kkkk kkkk kkKK
    	BBAN.put("TD", new Bban("(\\d{5})(\\d{5})(\\d{13})"       , 1)); // 
    	BBAN.put("TG", new Bban("([A-Z]{5})(\\d{5})(\\d{14})"     , 1)); // 
    }
    
    static final int GROUP_BANK_IDENTIFIER = 0;
    
    String format;
    RegexValidator regexValidator;
    Integer groupBankCode = null; // default GROUP_BANK_IDENTIFIER, but 1 for IT
    Integer groupBranchCode = null;
    
    Bban(String format) {
    	this.format = format;
    	this.regexValidator = new RegexValidator(format);
    	this.groupBankCode = GROUP_BANK_IDENTIFIER;
    }
    
    Bban(String format, int groupBranchCode) {
    	this(format);
     	this.groupBranchCode = groupBranchCode;
    }
    Bban(String format, int groupBranchCode, int groupBankCode) {
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
