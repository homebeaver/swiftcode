package com.klst.bankData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

import com.github.miachm.sods.Range;
import com.github.miachm.sods.Sheet;
import com.klst.iban.BankDataGenerator;
import com.klst.iban.BankDataOrdered;
import com.klst.iban.BusinessIdentifierCode;
import com.klst.iban.datastore.DatabaseProxy;
import com.klst.iban.datastore.SqlInstance;
import com.klst.ibanTest.API_Key_Provider;
import com.klst.ods.Ods;

/* Oberklasse für
	// sepa countries:
    	countryToFunc.put("CY", BANKCODE_AND_BRANCHCODE_NUMERIC); ?
    	countryToFunc.put("FR", BANKCODE_AND_BRANCHCODE_NUMERIC);
    	countryToFunc.put("GR", BANKCODE_AND_BRANCHCODE_NUMERIC);
    	countryToFunc.put("HU", BANKCODE_AND_BRANCHCODE_NUMERIC);
    	countryToFunc.put("IT", BANKCODE_AND_BRANCHCODE_NUMERIC);
    	...
    	countryToFunc.put("SM", BANKCODE_AND_BRANCHCODE_NUMERIC);

die Breite von BANKCODE und BRANCHCODE ist hoch, FR,IT jeweils 5-stellig ==> FORMAT_05d
Es wird daher nur BRANCHCODE durchsucht, BANKCODE kommt aus Map<Integer, ArrayList<Object>> bankByCode : tryWith
bankByCode muss befüllt werden:
 - auskodiert
 - aus json file
 - aus db
 - ...
    	
 */
public class NumericBankAndBranchCode extends BankDataGenerator {

	private static final Logger LOG = Logger.getLogger(NumericBankAndBranchCode.class.getName());

	DatabaseProxy db;
	String countryCode;
	String bankCodeFormat;
	String branchCodeFormat;
	Map<Integer, ArrayList<Object>> bankByCode = null;
	ArrayList<Integer> refBankByCodeArray = null; /* Beispiel:
		new ArrayList<Integer>(Arrays.asList(0, 1, 2)); */
	ArrayList<Object> columnMapper = null;

	protected NumericBankAndBranchCode(String countryCode, String bankCodeFormat, String branchCodeFormat, String api_key) {
		super(api_key);
		this.countryCode = countryCode;
		this.bankCodeFormat = bankCodeFormat;
		this.branchCodeFormat = branchCodeFormat;
		this.db = new DatabaseProxy(new SqlInstance(DatabaseProxy.POSTGRESQL_DATASTORE, API_Key_Provider.POSTGRESQL_USER, API_Key_Provider.POSTGRESQL_PW, SqlInstance.POSTGRESQL_DRIVER));
	}

	Integer bankCodeKey(Object[][] rangeValues, int row, int column) {
		Object o = rangeValues[row][column];
		return Ods.getInteger(o);
	}
	
	void populateBankByCode(Object[][] values, int r, List<Integer> refBankByCodeArray) {
		Integer bankCode = bankCodeKey(values, r, refBankByCodeArray.get(0));
		Object bic = new BusinessIdentifierCode((String)values[r][refBankByCodeArray.get(1)]);
		Object name = values[r][refBankByCodeArray.get(2)];
		if(bankCode!=null) {
			bankByCode.put(bankCode, new ArrayList<Object>(Arrays.asList(bankCode, bic, name)));
		} else {
			LOG.info("row "+r+": bankCode is null.");
		}	
	}
	
	void loadBankByCode(String odsFilePath) {
		loadBankByCode(odsFilePath, 1, columnMapper.size());
	}
	void loadBankByCode(String odsFilePath, int firstRow, int numColumns) {
        List<Sheet> sheets = Ods.getSheets(odsFilePath);
        if(sheets==null) System.exit(4);

        Map<String,Integer> nonEmptySheets = new Hashtable<String,Integer>();
        Sheet nonEmptySheet = Ods.getNonEmptySheet(sheets, nonEmptySheets, numColumns);
        LOG.info("file "+odsFilePath+" has nonEmptySheets/sheets:"+nonEmptySheets.size()+"/"+sheets.size());
   
		if (nonEmptySheets.size() == 1) {
			bankByCode = new Hashtable<Integer, ArrayList<Object>>();
			Collection<Integer> collection = nonEmptySheets.values();
			int numRows = collection.iterator().next();
			Range range = nonEmptySheet.getRange(0, 0, numRows, numColumns);
			LOG.info("range.getNumRows()=" + range.getNumRows() + " range.getNumColumns()=" + range.getNumColumns());
			Object[][] values = range.getValues();
			// r==0 ist colname, daher start bei 1
			for (int r = firstRow; r < range.getNumRows(); r++) {
				for (int c = 0; c < range.getNumColumns(); c++) {
					Object v = values[r][c];
					Object cellObect = range.getCell(r, c).getValue();
					LOG.fine("r(" + r + "),c:" + c + " " + (v == null ? "null" : v.getClass()) + " " + cellObect);
				}
				populateBankByCode(values, r, refBankByCodeArray);
//				Integer bankCode = bankCodeKey(values, r, 0);
//				Object bic = new BusinessIdentifierCode((String)values[r][1]);
//				Object name = values[r][2];
//    			if(bankCode!=null) {
//    				bankByCode.put(bankCode, new ArrayList<Object>(Arrays.asList(bankCode, bic, name)));
//    			} else {
//    				LOG.info("row "+r+": bankCode is null.");
//    			}
			}
		} else {
			LOG.warning("There are "+nonEmptySheets.size()+" nonEmptySheets");
			return;
		}
		LOG.info("bankByCode.size="+bankByCode.size());
	}
	
	void tryWith(int from, int to) {
		tryWith(countryCode, bankCodeFormat, from, to);
	}

	private int counter;
	private int count() { counter++; return counter;}
	Map<Integer, JSONObject> template = new Hashtable<Integer, JSONObject>();
	protected JSONObject doPrint(Long bankId, JSONObject jo) {
		//Bank_Data.BRANCH_CODE = "branch_code";
		Object bido = jo.get("bank_code");
		Object bco = jo.get("branch_code");
		Integer bci;
        try {
    		bci = new Integer(Integer.parseInt(bco.toString())); 
        } catch (NumberFormatException e) {
        	LOG.warning("branch_code "+bco + " is not numeric.");
        	return jo;
        }        
        if(bci==0) {
        	// TODO in db einfügen insert (template filiale)
        	template.put((Integer)bido, jo);
    		System.out.println(BankDataOrdered.toOrderedJSONString(jo) + ",");
        } else {
        	// filiale<>0 : suchen nach bankId = bank_code+branch_code
        	//gefunden: sollte nicht sein beim befüllen
        	//nicht gefunden:
        	//               - suche nach template: filiale bank_code+0
        	//               - ist diese equals jo (ohne "branch_code" natürlich) ==> nix tun
        	//               - else insert jo in db
        	JSONObject temp = template.get((Integer)bido);
        	if(temp==null) {
        		LOG.warning("temp is null!");
        	} else {
        		if(jo.get("bank").equals(temp.get("bank")) && jo.get("branch").equals(temp.get("branch")) 
        		&& jo.get("zip").equals(temp.get("zip")) && jo.get("city").equals(temp.get("city"))
            	&& jo.get("address").equals(temp.get("address")) 
            	&& (jo.get("support_codes")==null ? true : jo.get("support_codes").equals(temp.get("support_codes"))) ) {
        			// nix tun
        		} else {
        			// else insert jo in db
        			System.out.println(BankDataOrdered.toOrderedJSONString(jo) + ",");
        		}
        	}
        }
//		Object bc = jo.get("bank_code"); //Bank_Data.BANK_CODE);
//		BankData bankData = db.getBankdata(new InternationalBankAccountNumber(VALID_AD_IBAN));
		return jo;
	}

	public void tryWith(String countryCode, String format, int from, int to) {
		if(bankByCode==null) {
			LOG.severe("Map bankByCode darf nicht null sein.");
		}
		List<Integer> bankCodeList = new ArrayList<Integer>(bankByCode.keySet());
		counter = 0;
		bankCodeList.forEach(bankCode -> {
			// zuerst Filiale 0 initialisieren
			String bankId = String.format(format, bankCode);
			String branchId = String.format(branchCodeFormat, 0);
			FakeIban iban = new FakeIban(countryCode, bankId, branchId);
			if(printBankDataViaApi(bankCode, iban)) count(); // mit callback doPrint(JSONObject jo)
			else {
				LOG.config(bankId+"/"+branchId + " nicht initialisiert.");
			}
		});
		System.out.println("done "+counter + "/"+ bankByCode.size());
		// loop über Filialn
		bankCodeList.forEach(bankCode -> {
			for(int branch=from; branch<=to; branch++) {
				if(branch%100==0) {
					int perCent = 100*branch/to;
					System.out.println(bankCode+","+branch + " %:"+ perCent);
				}
				String bankId = String.format(format, bankCode);
				String branchId = String.format(branchCodeFormat, branch);
				FakeIban iban = new FakeIban(countryCode, bankId, branchId);
				if(printBankDataViaApi(bankCode, iban)) count(); // mit callback doPrint(JSONObject jo)
			}
		});
		
//		int counter = 0;
//		for(int id=from; id<=to; id++) {
//			String bankCode = String.format(format, id);
//			  for(int branch=9800; branch<=9999; branch++) {
//					if(branch%100==0) {
//						int perCent = 100*branch/99999;
//						System.out.println(id+","+branch + " %:"+ perCent);
//					}
////			int branch = 0;
//			if(id%100==0) {
//				int perCent = 100*id/99999;
//				System.out.println(""+id+","+branch + " %:"+ perCent);
//			}
//			String branchCode = String.format(FORMAT_05d, branch);
//			FakeIban iban = new FakeIban(countryCode, bankCode, branchCode);
//			if(bankCodeList==null) {
//				// suchen
//				LOG.info("id="+id + " tryWith "+iban+" bankCode "+bankCode);
////    			if(printBankDataViaApi(id, iban)) counter++;
//			} else if(bankCodeList.contains(id)) {
//    			LOG.info("id="+id + " do "+iban+" bankCode "+bankCode);
//    			if(printBankDataViaApi(id, iban)) counter++;
//			} else {
//				// unnötige iban.com Abfrage
//			}
//		  }
//		}
//		System.out.println("done "+counter + "/"+ (bankCodeList==null ? to : bankByCode.size()));
	}

}
