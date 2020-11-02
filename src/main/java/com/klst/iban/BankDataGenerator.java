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
	}

	protected final static String PP = "99"; // fake CheckDigits

	final static String FORMAT_02d = "%02d";
	final static String FORMAT_03d = "%03d";
	protected final static String FORMAT_04d = "%04d";
	protected final static String FORMAT_05d = "%05d";
	final static String FORMAT_06d = "%06d";
	protected final static String FORMAT_08d = "%08d";

	protected BankDataGenerator(String api_key) {
		super(api_key);
	}
	
	public void tryWith(String countryCode, String format, int from, int to, String account) {
		for(int id=from; id<=to; id++) {
    		String bankCode = String.format(format, id);
    		String iban = countryCode + PP + bankCode + account;
//    		LOG.info("bankCode="+bankCode + " iban:"+iban);
    		printBankDataViaApi(id, iban, new Hashtable<String, List<JSONObject>>());
		}
		
	}

	protected boolean printBankDataViaApi(int bId, String iban) {
		return printBankDataViaApi(bId, iban, new Hashtable<String, List<JSONObject>>());
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
