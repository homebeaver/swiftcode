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
		//                      12345678901234567890
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
		FAKE_ACCOUNT.put("CZ", "0000192000145399"    ); // 6!n10!n
		FAKE_ACCOUNT.put("DE", "0532013000"          ); // 10!n
    	FAKE_ACCOUNT.put("DK", "0440116243"          ); // 9!n1!n account+Kontrollzeichen (Großbuchstabe oder Ziffer)
    	FAKE_ACCOUNT.put("DO", "00000001212453611324"); // 20!n
    	FAKE_ACCOUNT.put("EE", "221020145685"        ); // 11!n1!n account+Kontrollzeichen (Großbuchstabe oder Ziffer)
    	FAKE_ACCOUNT.put("EG", "00000000263180002"   ); // 17!n
    	FAKE_ACCOUNT.put("ES", "450200051332"        ); // 1!n1!n10!n Kontrollzeichen+account 
    	FAKE_ACCOUNT.put("FI", "45600000785"         ); // 11!n
    	FAKE_ACCOUNT.put("FO", "0001631634"          ); // 9!n1!n account+Kontrollzeichen (Großbuchstabe oder Ziffer)
    	FAKE_ACCOUNT.put("FR", "0500013M02606"       ); // 11!c2!n account+Kontrollzeichen (Großbuchstabe oder Ziffer)
    	FAKE_ACCOUNT.put("GB", "41305901"            ); // 8!n
    	FAKE_ACCOUNT.put("GE", "0000000101904917"    ); // 16!n
    	FAKE_ACCOUNT.put("GI", "000000007099453"     ); // 15!c
    	FAKE_ACCOUNT.put("GL", "0001000206"          ); // 9!n1!n account+Kontrollzeichen (Großbuchstabe oder Ziffer)
    	FAKE_ACCOUNT.put("GR", "0000000012300695"    ); // 16!c
    	FAKE_ACCOUNT.put("GT", "01020000001210029690"); // 20!c
    	FAKE_ACCOUNT.put("HR", "1863000160"          ); // 10!n
    	FAKE_ACCOUNT.put("HU", "61111101800000000"   ); // 1!n15!n1!n account+Kontrollzeichen (Großbuchstabe oder Ziffer)
    	FAKE_ACCOUNT.put("IE", "12345678"            ); // 8!n
    	FAKE_ACCOUNT.put("IL", "0000099999999"       ); // 13!n
    	FAKE_ACCOUNT.put("IQ", "123456789012"        ); // 12!n
    	FAKE_ACCOUNT.put("IS", "260076545510730339"  ); // 2!n6!n10!n Kontrollzeichen+account+sonstige
    	FAKE_ACCOUNT.put("IT", "000000123456"        ); // 1!a5!n5!n 12!c Kontrollzeichen+BankCode+BranchCode+account
    	FAKE_ACCOUNT.put("JO", "000000000131000302"  ); // n18!c
    	FAKE_ACCOUNT.put("KW", "0000000000001234560101"); // a22!c
    	FAKE_ACCOUNT.put("KZ", "KZT5004100100"       ); // 13!c
    	FAKE_ACCOUNT.put("LB", "00000001001901229114"); // 20!c
    	FAKE_ACCOUNT.put("LC", "000100010012001200023015"); // 24!c
    	FAKE_ACCOUNT.put("LI", "0002324013AA"        ); // 12!c
    	FAKE_ACCOUNT.put("LT", "10099679931"         ); // 11!n
    	FAKE_ACCOUNT.put("LU", "9400644750000"       ); // 13!c
    	FAKE_ACCOUNT.put("LV", "0551008657797"       ); // 13!c
    	FAKE_ACCOUNT.put("LY", "000020100120361"     ); // 15!n
    	FAKE_ACCOUNT.put("MC", "1234567890191"       ); // 11!c2!n account+Kontrollzeichen
    	FAKE_ACCOUNT.put("MD", "000225100013104168"  ); // 18!c  	
    	FAKE_ACCOUNT.put("ME", "000012345678951"     ); // 13!n2!n account+Kontrollzeichen (Großbuchstabe oder Ziffer)
       	FAKE_ACCOUNT.put("MK", "120000058984"        ); // 10!c2!n
    	FAKE_ACCOUNT.put("MR", "0000123456753"       ); // 11!n2!n account+Kontrollzeichen (Großbuchstabe oder Ziffer)
    	FAKE_ACCOUNT.put("MT", "0012345MTLCAST001S"  ); // 18!c
    	FAKE_ACCOUNT.put("MU", "01101030300200000MUR"); // 2!n12!n3!n3!a account+Kontrollzeichen (Großbuchstabe oder Ziffer)
    	FAKE_ACCOUNT.put("NL", "0417164300"          ); // 10!n
    	FAKE_ACCOUNT.put("NO", "1117947"             ); // 6!n1!n
    	FAKE_ACCOUNT.put("PK", "0000001123456702"    ); // 16!c
    	FAKE_ACCOUNT.put("PL", "0000071219812874"    ); // 16!n
    	FAKE_ACCOUNT.put("PS", "000000000400123456702"); // 21!c
    	FAKE_ACCOUNT.put("PT", "0000539169561"       ); // 11!n2!n account+Kontrollzeichen (Großbuchstabe oder Ziffer)
    	FAKE_ACCOUNT.put("QA", "00001234567890ABCDEFG"); // 21!c
    	FAKE_ACCOUNT.put("RO", "410SV20462054100"    ); // 16!c
    	FAKE_ACCOUNT.put("RS", "005601001611379"     ); // 13!n2!n
    	FAKE_ACCOUNT.put("SA", "000000608010167519"  ); // 18!c
//    	FAKE_ACCOUNT.put("SC", "SSCB 11 010000000000001497USD"); //N4!a2!n2!n16!n3!a BankCode:4!a2!n2!n iban.com nur 4!a account+Kontrollzeichen+sonstige
    	FAKE_ACCOUNT.put("SE", "00000058398257466"   ); // 16!n1!n
    	FAKE_ACCOUNT.put("SI", "0015556761"          ); // 8!n2!n
    	FAKE_ACCOUNT.put("SK", "1234561234567890"    ); // 6!n10!n
    	FAKE_ACCOUNT.put("SM", "000000270100"        ); // 1!a5!n5!n12!c Kontrollzeichen+BankCode+BranchCode+account
    	FAKE_ACCOUNT.put("ST", "0051845310146"       ); // 11!n2!n account+Kontrollzeichen (Großbuchstabe oder Ziffer)
    	FAKE_ACCOUNT.put("SV", "00000000000000700025"); // a20!n
    	FAKE_ACCOUNT.put("TL", "0601000086313706"    ); // 14!n2!n
    	FAKE_ACCOUNT.put("TN", "035183598478831"     ); // 13!n2!n account+Kontrollzeichen
    	FAKE_ACCOUNT.put("TR", "00000696117500104"   ); // 1!n16!c Kontrollzeichen+account
    	FAKE_ACCOUNT.put("UA", "0000026007233566001" ); // 19!c
    	FAKE_ACCOUNT.put("VA", "123000012345678"     ); // 15!n
    	FAKE_ACCOUNT.put("VG", "0000012345678901"    ); // a16!n
    	FAKE_ACCOUNT.put("XK", "012345678906"        ); // 10!n2!n account+Kontrollzeichen
// TODO Countries which have Partial/Experimental use of the IBAN
	}

	protected final static String PP = "99"; // fake CheckDigits

	public static class FakeIban {
		String iban;
		public FakeIban(String countryCode, String bankCode) {
			this(countryCode, bankCode, null);
		}
		public FakeIban(String countryCode, String bankCode, String branchCode) {
			if("IT".equals(countryCode) || "SM".equals(countryCode)) {
				iban = countryCode+PP+"X"+bankCode+branchCode+FAKE_ACCOUNT.get(countryCode);
			} else {
				iban = countryCode+PP+bankCode+(branchCode==null?"":branchCode)+FAKE_ACCOUNT.get(countryCode);
			}
		}
		public String toString() {
			return iban;
		}
	}

	protected final static String FORMAT_02d = "%02d";
	protected final static String FORMAT_03d = "%03d";
	protected final static String FORMAT_04d = "%04d";
	protected final static String FORMAT_05d = "%05d";
	final static String FORMAT_06d = "%06d";
	protected final static String FORMAT_07d = "%07d";
	protected final static String FORMAT_08d = "%08d";

	protected BankDataGenerator(String api_key) {
		super(api_key);
	}
	
	public void tryWith(String countryCode, String format, int from, int to) {
		for(int id=from; id<=to; id++) {
    		String bankCode = String.format(format, id);
    		String iban = countryCode + PP + bankCode + FAKE_ACCOUNT.get(countryCode);
//    		LOG.info("bankCode="+bankCode + " iban:"+iban);
    		printBankDataViaApi(id, iban, new Hashtable<String, List<JSONObject>>());
		}
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
		String bic = bankData.getBic(); // mandatory, kann aber null/leer sein, siehe Minitest "LY83002048000020100120361"
		if (bic == null || bic.trim().isEmpty()) {
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
