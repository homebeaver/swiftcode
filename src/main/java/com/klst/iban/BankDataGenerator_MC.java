package com.klst.iban;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

public class BankDataGenerator_MC extends BankDataGenerator {

	private static final Logger LOG = Logger.getLogger(BankDataGenerator_MC.class.getName());
	
/* numeric bankCode aka CIB aus https://www.amaf.mc/fr/bic-iban :

Nom d'établissement                                 	CIB 	Code Guichet 	Code Pays 	BIC
CIC Lyonnaise de Banque (Agence de Monaco) Monaco     	10096	18079	MC 	CMCIMCM1LYB
CIC Lyonnaise de Banque (Agence de Monaco) Fontvieille 	10096	18414	MC 	CMCIMCM1LYB
CIC Lyonnaise de Banque (Agence de Monaco) Entreprises 	10096	18579	MC 	CMCIMCM1LYB
Crédit Mobilier de Monaco                           	10160	99001	MC 	CMMDMCM1XXX
...

 */
	static final Map<Integer,List<Integer>> CIB = new Hashtable<Integer, List<Integer>>();
    static {
    	CIB.put(10096, new ArrayList<Integer>(Arrays.asList(18079, 18414, 18579)));
    	CIB.put(10160, new ArrayList<Integer>(Arrays.asList(99001)));
    	CIB.put(11498, new ArrayList<Integer>(Arrays.asList(1)));
    	CIB.put(11668, new ArrayList<Integer>(Arrays.asList(40001)));
    	CIB.put(11999, new ArrayList<Integer>(Arrays.asList(1)));
    	CIB.put(12098, new ArrayList<Integer>(Arrays.asList(4122,4123,4124,4125,4126,4127,4128,4133,4134,4135,4136,4137,4138)));
    	CIB.put(12448, new ArrayList<Integer>(Arrays.asList(61017, 61088, 61091)));
    	CIB.put(12739, new ArrayList<Integer>(Arrays.asList(70,71,72,73,74,75,76,77)));
    	CIB.put(13338, new ArrayList<Integer>(Arrays.asList(1)));
    	CIB.put(13368, new ArrayList<Integer>(Arrays.asList(1)));
    	CIB.put(13369, new ArrayList<Integer>(Arrays.asList(9)));
    	CIB.put(14508, new ArrayList<Integer>(Arrays.asList(1)));
    	CIB.put(14607, new ArrayList<Integer>(Arrays.asList(758, 764, 796)));
    	CIB.put(14908, new ArrayList<Integer>(Arrays.asList(1)));
    	CIB.put(15638, new ArrayList<Integer>(Arrays.asList(1)));
    	CIB.put(16038, new ArrayList<Integer>(Arrays.asList(1)));
    	CIB.put(16548, new ArrayList<Integer>(Arrays.asList(342, 343)));
    	CIB.put(16648, new ArrayList<Integer>(Arrays.asList(14)));
    	CIB.put(17288, new ArrayList<Integer>(Arrays.asList(1)));
    	CIB.put(17569, new ArrayList<Integer>(Arrays.asList(1, 5)));
    	CIB.put(18315, new ArrayList<Integer>(Arrays.asList(20000)));
    	CIB.put(18759, new ArrayList<Integer>(Arrays.asList(1)));
    	CIB.put(19106, new ArrayList<Integer>(Arrays.asList(698)));
    	CIB.put(24349, new ArrayList<Integer>(Arrays.asList(1)));
    	CIB.put(30002, new ArrayList<Integer>(Arrays.asList(3214,3243,3260,3290,3291,5430)));
    	CIB.put(30003, new ArrayList<Integer>(Arrays.asList(909,910,945,952,957,1504,2308)));
    	CIB.put(30004, new ArrayList<Integer>(Arrays.asList(9170,9172,9174,9178,9179)));
    }
    
	BankDataGenerator_MC(String api_key) {
		super(api_key);
		CIB.forEach((key,v) -> {
			List<Integer> al = new ArrayList<Integer>();
			al.addAll(v);
			filialen.put(key, al);
		});
//		filialen.putAll(CIB); // beim remove(0) geht ein element in List<Integer> verloren
	}
	
	Map<Integer,List<Integer>> filialen = new Hashtable<Integer, List<Integer>>();
	int bankCodeToId(int bankCode, int addIndex) {
		List<Integer> branchCodeList = filialen.get(bankCode); // Code Guichet List
		int i = branchCodeList.size();
		branchCodeList.remove(0);
		return bankCode*100 + i;
	}

	JSONObject updateJSONObject(JSONObject jo, String key, Object value) {
		if(BRANCH_CODE_IN_IBAN.equals(key)) {
			//LOG.info(key + ":: iban:"+value);
			String countryCode = value.toString().substring(0, 2);
			Bban bData = Bban.BBAN.get(countryCode);
			return super.updateJSONObject(jo, BRANCH_CODE, bData.getBankData(value.toString()).getBranchCode());
		}
		return super.updateJSONObject(jo, key, value);
	}
	
	void tryWith(String countryCode, String format, int from, int to, String account) {
		List<Integer> bankCodeList = new ArrayList<Integer>(CIB.keySet());
		Hashtable<String, List<JSONObject>> jMap = new Hashtable<String, List<JSONObject>>(); // leer
		for(int id=from; id<=to; id++) {
			if(bankCodeList.contains(id)) {
	    		String bankCode = String.format(format, id);
	    		List<Integer> branchCodeList = CIB.get(id); // Code Guichet List
	    		for(int i = 0; i < branchCodeList.size(); i++){
	    			int codeGuichet = branchCodeList.get(i);
	    			String branchCode = String.format(BankDataGenerator.FORMAT_05d, codeGuichet);
	    			String iban = countryCode + PP + bankCode + branchCode + account;
	    			//LOG.info("bankCode="+bankCode + " iban:"+iban);
	    			printBankDataViaApi(id, iban, jMap);
	    		}
			}
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		BankDataGenerator test = new BankDataGenerator_MC("testKey");

		test.tryWith("MC", BankDataGenerator.FORMAT_05d, 00000, 99999, "0123456789030"); // MC58 11222 00001 0123456789030
		
	}
}
