package com.klst.iban;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

import com.klst.iban.Result.BankData;

public class BankDataGenerator extends IbanToBankData {
	
//	private static final Logger LOG = Logger.getLogger(BankDataGenerator.class.getName());
	private static Logger LOG;
	private static final LogManager logManager = LogManager.getLogManager(); // Singleton

	protected static final String RESOURCE_DATA_PATH = "data/doc/";

    static final Map<String,String> FAKE_ACCOUNT = new Hashtable<String, String>();

	static {
    	URL url = BankDataGenerator.class.getClassLoader().getResource("testLogging.properties");
		try {
			File file = new File(url.toURI());
			logManager.readConfiguration(new FileInputStream(file));
		} catch (IOException | URISyntaxException e) {
			LOG = Logger.getLogger(BankDataGenerator.class.getName());
			LOG.warning(e.getMessage());
		}
		LOG = Logger.getLogger(BankDataGenerator.class.getName());
		//                      12345678901234567809
		FAKE_ACCOUNT.put("AD", "200359100100"        ); // 12!c
		FAKE_ACCOUNT.put("AE", "1234567890123456"    ); // 16!n
		FAKE_ACCOUNT.put("AL", "090000000235698741"  ); // 2!n16!c Kontrollzeichen+account
		FAKE_ACCOUNT.put("AT", "40014400144"         ); // 5!n11!n
		FAKE_ACCOUNT.put("AZ", "00000000137010001944"); // 20!c
		FAKE_ACCOUNT.put("BA", "9401028494"          ); // 8!n2!n account+Kontrollzeichen
		FAKE_ACCOUNT.put("BE", "000004141"           ); // 7!n2!n account+Kontrollzeichen
		FAKE_ACCOUNT.put("BG", "1020345678"          ); // 2!n8!c Kontrollzeichen+account
		FAKE_ACCOUNT.put("BH", "00001299123456"      ); // a14!c
		FAKE_ACCOUNT.put("BR", "0009795493C1"        ); // 10!n1!a1!c account+Kontrollzeichen
		FAKE_ACCOUNT.put("BY", "900000002Z00AB00"    ); // 16!c 
		FAKE_ACCOUNT.put("CH", "000600070004"        ); // 12!c
		FAKE_ACCOUNT.put("CR", "02001026284066"      ); // 14!n
		FAKE_ACCOUNT.put("CY", "0000001200527600"    ); // 16!c
		FAKE_ACCOUNT.put("CZ", "2000145399"          ); // 10!n 
		FAKE_ACCOUNT.put("DE", "0532013000"          ); // 10!n
    	FAKE_ACCOUNT.put("DK", "0440116243"          ); // 9!n1!n account+Kontrollzeichen (Großbuchstabe oder Ziffer)
//    	FAKE_ACCOUNT.put("DO", new FAKE_ACCOUNT("([A-Z0-9]{4})(\\d{20})"             )); //N4!c20!n
//    	FAKE_ACCOUNT.put("EE", new FAKE_ACCOUNT("(\\d{2})(\\d{14})"                  )); // 2!n2!n11!n1!n account+Kontrollzeichen (Großbuchstabe oder Ziffer)
//    	FAKE_ACCOUNT.put("EG", new FAKE_ACCOUNT("(\\d{4})(\\d{4})(\\d{17})"       , 1)); // 4!n4!n17!n +BranchCode
//    	FAKE_ACCOUNT.put("ES", new FAKE_ACCOUNT("(\\d{4})(\\d{4})(\\d{12})"       , 1)); // 4!n4!n1!n1!n10!n +BranchCode Kontrollzeichen+account 
//    	FAKE_ACCOUNT.put("FI", new FAKE_ACCOUNT("(\\d{3})(\\d{11})"                  )); // 3!n11!n
//    	FAKE_ACCOUNT.put("FO", new FAKE_ACCOUNT("(\\d{4})(\\d{10})"                  )); // 4!n9!n1!n account+Kontrollzeichen (Großbuchstabe oder Ziffer)
//    	FAKE_ACCOUNT.put("FR", new FAKE_ACCOUNT("(\\d{5})(\\d{5})([A-Z0-9]{13})"  , 1)); // 5!n5!n11!c2!n +BranchCode account+Kontrollzeichen (Großbuchstabe oder Ziffer)
//    	FAKE_ACCOUNT.put("GB", new FAKE_ACCOUNT("([A-Z]{4})(\\d{6})(\\d{8})"      , 1)); //S4!a6!n8!n +BranchCode
//    	FAKE_ACCOUNT.put("GE", new FAKE_ACCOUNT("([A-Z]{2})(\\d{16})"                )); // 2!a16!n
//    	FAKE_ACCOUNT.put("GI", new FAKE_ACCOUNT("([A-Z]{4})([A-Z0-9]{15})"           )); //S4!a15!c
//    	FAKE_ACCOUNT.put("GL", new FAKE_ACCOUNT("(\\d{4})(\\d{10})"                  )); // 4!n9!n1!n account+Kontrollzeichen (Großbuchstabe oder Ziffer)
//    	FAKE_ACCOUNT.put("GR", new FAKE_ACCOUNT("(\\d{3})(\\d{4})([A-Z0-9]{16})"  , 1)); // 3!n4!n16!c +BranchCode
//    	FAKE_ACCOUNT.put("GT", new FAKE_ACCOUNT("([A-Z0-9]{4})([A-Z0-9]{20})"        )); //N4!c20!c
//    	FAKE_ACCOUNT.put("HR", new FAKE_ACCOUNT("(\\d{7})(\\d{10})"                  )); // 7!n10!n
//    	FAKE_ACCOUNT.put("HU", new FAKE_ACCOUNT("(\\d{3})(\\d{4})(\\d{17})"       , 1)); // 3!n4!n1!n15!n1!n +BranchCode account+Kontrollzeichen (Großbuchstabe oder Ziffer)
//    	FAKE_ACCOUNT.put("IE", new FAKE_ACCOUNT("([A-Z]{4})(\\d{6})(\\d{8})"      , 1)); //S4!a6!n8!n +BranchCode
//    	FAKE_ACCOUNT.put("IL", new FAKE_ACCOUNT("(\\d{3})(\\d{3})(\\d{13})"       , 1)); // 3!n3!n13!n +BranchCode
//    	FAKE_ACCOUNT.put("IQ", new FAKE_ACCOUNT("([A-Z]{4})(\\d{3})(\\d{12})"     , 1)); //N4!a3!n12!n +BranchCode
//    	FAKE_ACCOUNT.put("IS", new FAKE_ACCOUNT("(\\d{4})(\\d{18})"                  )); // 4!n2!n6!n10!n Kontrollzeichen+account+sonstige
//    	FAKE_ACCOUNT.put("IT", new FAKE_ACCOUNT("([A-Z]{1})(\\d{5})(\\d{5})([A-Z0-9]{12})", 2, 1)); // 1!a5!n5!n12!c Kontrollzeichen+BankCode+BranchCode+account
//    	FAKE_ACCOUNT.put("JO", new FAKE_ACCOUNT("([A-Z]{4})(\\d{4})([A-Z0-9]{18})", 1)); //N4!a4!n18!c +BranchCode (nicht bei iban.com)
//    	FAKE_ACCOUNT.put("KW", new FAKE_ACCOUNT("([A-Z]{4})([A-Z0-9]{22})"           )); //N4!a22!c
//    	FAKE_ACCOUNT.put("KZ", new FAKE_ACCOUNT("(\\d{3})([A-Z0-9]{13})"             )); // 3!n13!c
//    	FAKE_ACCOUNT.put("LB", new FAKE_ACCOUNT("(\\d{4})([A-Z0-9]{20})"             )); // 4!n20!c
//    	FAKE_ACCOUNT.put("LC", new FAKE_ACCOUNT("([A-Z]{4})([A-Z0-9]{24})"           )); //N4!a24!c
//    	FAKE_ACCOUNT.put("LI", new FAKE_ACCOUNT("(\\d{5})([A-Z0-9]{12})"             )); // 5!n12!c
//    	FAKE_ACCOUNT.put("LT", new FAKE_ACCOUNT("(\\d{5})(\\d{11})"                  )); // 5!n11!n
//    	FAKE_ACCOUNT.put("LU", new FAKE_ACCOUNT("(\\d{3})([A-Z0-9]{13})"             )); // 3!n13!c
//    	FAKE_ACCOUNT.put("LV", new FAKE_ACCOUNT("([A-Z]{4})([A-Z0-9]{13})"           )); //S4!a13!c
//    	FAKE_ACCOUNT.put("LY", new FAKE_ACCOUNT("(\\d{3})(\\d{3})(\\d{15})"       , 1)); // 3!n3!n15!n
//    	FAKE_ACCOUNT.put("MC", new FAKE_ACCOUNT("(\\d{5})(\\d{5})([A-Z0-9]{13})"  , 1)); // 5!n5!n11!c2!n +BranchCode account+Kontrollzeichen
//    	FAKE_ACCOUNT.put("MD", new FAKE_ACCOUNT("([A-Z0-9]{2})([A-Z0-9]{18})"        )); // 2!c18!c  	
//    	FAKE_ACCOUNT.put("ME", new FAKE_ACCOUNT("(\\d{3})(\\d{15})"                  )); // 3!n13!n2!n account+Kontrollzeichen (Großbuchstabe oder Ziffer)
//       	FAKE_ACCOUNT.put("MK", new FAKE_ACCOUNT("(\\d{3})([A-Z0-9]{12})"             )); // 3!n10!c2!n
//    	FAKE_ACCOUNT.put("MR", new FAKE_ACCOUNT("(\\d{5})(\\d{5})(\\d{13})"       , 1)); // 5!n5!n11!n2!n +BranchCode account+Kontrollzeichen (Großbuchstabe oder Ziffer)
    	FAKE_ACCOUNT.put("MT", "0012345MTLCAST001S"); // 18!c
//    	FAKE_ACCOUNT.put("MU", new FAKE_ACCOUNT("([A-Z]{4})(\\d{2})([A-Z0-9]{20})", 1)); //N4!a2!n2!n12!n3!n3!a +BranchCode account+Kontrollzeichen (Großbuchstabe oder Ziffer)
//    	FAKE_ACCOUNT.put("NL", new FAKE_ACCOUNT("([A-Z]{4})(\\d{10})"                )); //S4!a10!n
//    	FAKE_ACCOUNT.put("NO", new FAKE_ACCOUNT("(\\d{4})(\\d{7})"                   )); // 4!n6!n1!n
//    	FAKE_ACCOUNT.put("PK", new FAKE_ACCOUNT("([A-Z]{4})([A-Z0-9]{16})"           )); //N4!a16!c
//    	FAKE_ACCOUNT.put("PL", new FAKE_ACCOUNT("(\\d{8})(\\d{16})"                  )); // 8!n16!n
//    	FAKE_ACCOUNT.put("PS", new FAKE_ACCOUNT("([A-Z]{4})([A-Z0-9]{21})"           )); //N4!a21!c
//    	FAKE_ACCOUNT.put("PT", new FAKE_ACCOUNT("(\\d{4})(\\d{4})(\\d{13})"       , 1)); // 4!n4!n11!n2!n +BranchCode account+Kontrollzeichen (Großbuchstabe oder Ziffer)
//    	FAKE_ACCOUNT.put("QA", new FAKE_ACCOUNT("([A-Z]{4})([A-Z0-9]{21})"           )); //N4!a21!c
//    	FAKE_ACCOUNT.put("RO", new FAKE_ACCOUNT("([A-Z]{4})([A-Z0-9]{16})"           )); //S4!a16!c
//    	FAKE_ACCOUNT.put("RS", new FAKE_ACCOUNT("(\\d{3})(\\d{15})"                  )); // 3!n13!n2!n
//    	FAKE_ACCOUNT.put("SA", new FAKE_ACCOUNT("(\\d{2})([A-Z0-9]{18})"             )); // 2!n18!c
//    	FAKE_ACCOUNT.put("SC", new FAKE_ACCOUNT("([A-Z]{4})([A-Z0-9]{23})"           )); //N4!a2!n2!n16!n3!a BankCode:4!a2!n2!n iban.com nur 4!a account+Kontrollzeichen+sonstige
//    	FAKE_ACCOUNT.put("SE", new FAKE_ACCOUNT("(\\d{3})(\\d{17})"                  )); // 3!n16!n1!n
//    	FAKE_ACCOUNT.put("SI", new FAKE_ACCOUNT("(\\d{5})(\\d{10})"                  )); // 5!n8!n2!n
//    	FAKE_ACCOUNT.put("SK", new FAKE_ACCOUNT("(\\d{4})(\\d{16})"                  )); // 4!n6!n10!n
//    	FAKE_ACCOUNT.put("SM", new FAKE_ACCOUNT("([A-Z]{1})(\\d{5})(\\d{5})([A-Z0-9]{12})", 2, 1)); // 1!a5!n5!n12!c Kontrollzeichen+BankCode+BranchCode+account
//    	FAKE_ACCOUNT.put("ST", new FAKE_ACCOUNT("(\\d{4})(\\d{4})(\\d{13})"       , 1)); // 8!n11!n2!n +BranchCode account+Kontrollzeichen (Großbuchstabe oder Ziffer)
//    	FAKE_ACCOUNT.put("SV", new FAKE_ACCOUNT("([A-Z]{4})(\\d{20})"                )); //N4!a20!n
//    	FAKE_ACCOUNT.put("TL", new FAKE_ACCOUNT("(\\d{3})(\\d{16})"                  )); // 3!n14!n2!n
//    	FAKE_ACCOUNT.put("TN", new FAKE_ACCOUNT("(\\d{2})(\\d{3})(\\d{15})"       , 1)); // 2!n3!n13!n2!n +BranchCode account+Kontrollzeichen
//    	FAKE_ACCOUNT.put("TR", new FAKE_ACCOUNT("(\\d{5})(\\d{17})"                  )); // 5!n1!n16!c Kontrollzeichen+account
//    	FAKE_ACCOUNT.put("UA", new FAKE_ACCOUNT("(\\d{6})([A-Z0-9]{19})"             )); // 6!n19!c
//    	FAKE_ACCOUNT.put("VA", new FAKE_ACCOUNT("(\\d{3})(\\d{15})"                  )); // 3!n15!n
//    	FAKE_ACCOUNT.put("VG", new FAKE_ACCOUNT("([A-Z]{4})(\\d{16})"                )); //N4!a16!n
//    	FAKE_ACCOUNT.put("XK", new FAKE_ACCOUNT("(\\d{2})(\\d{2})(\\d{12})"       , 1)); // 4!n10!n2!n +BranchCode account+Kontrollzeichen

	}

	protected final static String PP = "99"; // fake CheckDigits

	public static class FakeIban {
		String iban;
		public FakeIban(String countryCode, String bankCode) {
			this(countryCode, bankCode, null);
		}
		public FakeIban(String countryCode, String bankCode, String branchCode) {
			iban = countryCode+PP+bankCode+(branchCode==null?"":branchCode)+FAKE_ACCOUNT.get(countryCode);
		}
		public String toString() {
			return iban;
		}
	}

	final static String FORMAT_02d = "%02d";
	final static String FORMAT_03d = "%03d";
	protected final static String FORMAT_04d = "%04d";
	protected final static String FORMAT_05d = "%05d";
	final static String FORMAT_06d = "%06d";
	protected final static String FORMAT_08d = "%08d";

	protected BankDataGenerator(String api_key) {
		super(api_key);
	}
	
	public void tryWith(String countryCode, String format, int from, int to) {
		tryWith(countryCode, format, from, to, FAKE_ACCOUNT.get(countryCode));
	}
	@Deprecated
	public void tryWith(String countryCode, String format, int from, int to, String account) {
		for(int id=from; id<=to; id++) {
    		String bankCode = String.format(format, id);
    		String iban = countryCode + PP + bankCode + account;
//    		LOG.info("bankCode="+bankCode + " iban:"+iban);
    		printBankDataViaApi(id, iban, new Hashtable<String, List<JSONObject>>());
		}
		
	}

	protected boolean printBankDataViaApi(int bId, FakeIban iban) {
		return printBankDataViaApi(bId, iban.toString(), new Hashtable<String, List<JSONObject>>());
	}
	
	protected boolean printBankDataViaApi(int bId, String iban, Map<String, List<JSONObject>> jMap) {
		BankData bankData = super.retrieveBankData(iban); // iban wird nicht validiert!
		if (bankData == null)
			return false;
		String bankName = bankData.getBank(); // mandatory
		if (bankName == null) { // not found
			return false;
		}
		String bic = bankData.getBic(); // mandatory, kann aber leer sein, siehe Minitest "LY83002048000020100120361"
		if (bic == null || bic.isEmpty()) {
			LOG.config("no bic bankData:" + bankData);
			return false;
		}

		// TODO wo bleibt bankCode bzw bankData.getBankIdentifier()

		Object branch = bankData.getBranch();
		String city = bankData.getCity();

		List<JSONObject> branchList = jMap.get(bic);
		branchList = getBranchList(bic, jMap);

		for(int i=0; i<branchList.size(); i++) {
			JSONObject jo = branchList.get(i);			
			int listIndex = i+1;
			String swiftCode = (String)jo.get(Bank_Data.SWIFT_CODE);
			if(swiftCode.length()==8) swiftCode = swiftCode.concat(BusinessIdentifierCode.PRIMARY_OFFICE);
			if(bic.equals(swiftCode)) {
				listIndex = 0; // LU: bei XXX id*1000, sonst id*1000 +i+1
			}
			String countryCode = iban.substring(0, 2);
			try {
				Long bankId = BankId.getBankId(countryCode, bankData.getBankIdentifier(), bankData.getBranchCode());
				jo = updateJSONObject(jo, Bank_Data.ID, bankCodeToId(bankId, listIndex));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			jo = updateJSONObject(jo, Bank_Data.SWIFT_CODE, swiftCode);
			// if(BankId.SORTCODE_LIKE.equals(BankId.countryToFunc.get(countryCode))) { ...
			jo = updateJSONObject(jo, BANK_CODE, bankData.getBankIdentifier());
//			String bankIdentifier = bankData.getBankIdentifier();
//			try {
//				int bankCode = new Integer(Integer.parseInt(bankIdentifier));
//				jo = updateJSONObject(jo, BANK_CODE, bankCode);
//			} catch (NumberFormatException e) {
//				jo = updateJSONObject(jo, BANK_CODE, bankIdentifier);
//			}
			
			jo = updateJSONObject(jo, BANK, bankName);
			if(branch==null) { 
				// branch aus le belassen
			} else {
				Object branchAlt = jo.get(BRANCH);
            	if(!branch.toString().equals(branchAlt) && branchAlt!=null) {
            		LOG.warning("le:"+jo + ", le.branch ANDERS branch:"+branch);
            	}
				jo = updateJSONObject(jo, BRANCH, branch);
			}
			if(city==null) {
				// city aus le belassen
			} else {
				updateJSONObject(jo, bankData, CITY, listIndex);
			}
			// optional:
			jo = updateJSONObject(jo, BRANCH_CODE, bankData.getBranchCode());
			jo = updateJSONObject(jo, Bank_Data.SUPPORT_CODES, bankData.getBankSupports());
			jo = updateJSONObject(jo, PHONE, bankData.getPhone());
			jo = updateJSONObject(jo, FAX, bankData.getFax());
			jo = updateJSONObject(jo, WWW, bankData.getWww());
			jo = updateJSONObject(jo, EMAIL, bankData.getEmail());
			jo = updateJSONObject(jo, BRANCH_CODE_IN_IBAN, iban);
			//System.out.println(jo.toString() + ","); // toString == public static String toJSONString(Map map)
			System.out.println(BankDataOrdered.toOrderedJSONString(jo) + ",");
		}
		return true;	
	}

	int bankCodeToId(Long bankId, int addIndex) {
		return Integer.parseInt(bankId.toString());
	}

	List<JSONObject> getBranchList(String bic, Map<String, List<JSONObject>> jMap) {
		List<JSONObject> branchList = jMap.get(bic);
        if(branchList==null) { // id:16, bic:COBALULXXXX, branchList#=null, bankName:BANK JULIUS BAER LUXEMBOURG S.A.
    		branchList = new ArrayList<JSONObject>();
    		JSONObject le = new JSONObject();
    		updateJSONObject(le, Bank_Data.SWIFT_CODE, bic);
        	branchList.add(le);        	
        }
		return branchList;
	}

    static final String BRANCH_CODE_IN_IBAN = "BRANCH_CODE_IN_IBAN";
	JSONObject updateJSONObject(JSONObject jo, String key, Object value) {
		//LOG.info("key:"+key + " old/new: "+jo.get(key)+"/"+value);	
		if(BankDataOrdered.OPTIONAL_KEYS.contains(key) && value==null) {
			// nix tun
		} else if(Bank_Data.BANK_CODE.equals(key)) {
			try {
				int bankCode = new Integer(Integer.parseInt(value.toString()));
				jo.put(key, bankCode);
			} catch (NumberFormatException e) {
				jo.put(key, value);
			}			
		} else if(Bank_Data.BRANCH_CODE.equals(key)) {
			try {
				int branchCode = new Integer(Integer.parseInt(value.toString()));
				jo.put(key, branchCode);
			} catch (NumberFormatException e) {
				jo.put(key, value);
			}						
		} else if(Bank_Data.SUPPORT_CODES.equals(key) && value.hashCode()==0) {
			// wie null
		} else if(BRANCH_CODE_IN_IBAN.equals(key)) {
			// dies ist ein exit zum patchen von BRANCH_CODE, der aus IBAN gewonnen werden kann, siehe MC
			// hier nix tun
		} else { // MANDATORY_KEYS || value!=null
			jo.put(key, value);
		}
		return jo;
	}

	JSONObject updateJSONObject(JSONObject jo, BankData bankData, String key, int listIndex) {
		if(CITY.equals(key)) {
			String city = bankData.getCity();
			Object cityAlt = jo.get(key);
			if(!city.equals(cityAlt) && listIndex>0) {
				LOG.warning("JSONObject:"+jo + " - diff in "+key+":"+city);
			}
			jo = updateJSONObject(jo, ADDRESS, bankData.getAddress());
			jo = updateJSONObject(jo, ZIP, bankData.getZipString());
			jo = updateJSONObject(jo, CITY, bankData.getCity());
		}
		return jo;
	}


}
