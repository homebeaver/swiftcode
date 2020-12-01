package com.klst.bankData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.github.miachm.sods.Range;
import com.github.miachm.sods.Sheet;
import com.klst.iban.BankDataGenerator;
import com.klst.iban.BankDataOrdered;
import com.klst.iban.BusinessIdentifierCode;
import com.klst.iban.datastore.DatabaseProxy;
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

	private static File getFile(String filename) throws FileNotFoundException {
		LOG.info("filename:"+filename);
		File file = new File(filename);
		if(!file.exists()) {
			throw new FileNotFoundException("Not existing file:"+file);
		}
		return file;
	}
	static List<JSONObject> jsonToList(String filename) throws FileNotFoundException, IOException {
		List<JSONObject> result = null;
		File file = getFile(filename);
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        JSONParser jsonParser = new JSONParser(); // gibt es auch in jdk 1.8 nashorn.jar jdk.nashorn.internal.parser.JSONParser
        try {
        	Object o = jsonParser.parse(reader);
        	JSONObject jo = (JSONObject) o;
        	Object country = jo.get("country");
        	Object country_code = jo.get("country_code");
        	LOG.info("country_code:"+country_code + " country:"+country);
        	Object list = jo.get("list");
        	result = (JSONArray)list;
        	LOG.info("result List.size:"+result.size());
 		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			reader.close();
		}
       	return result;
	}

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
//		this.db = new DatabaseProxy(new SqlInstance(DatabaseProxy.POSTGRESQL_DATASTORE, API_Key_Provider.POSTGRESQL_USER, API_Key_Provider.POSTGRESQL_PW, SqlInstance.POSTGRESQL_DRIVER));
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

	static boolean equalsKey(String key, JSONObject o1,JSONObject o2) {
		return o1.get(key)==null ? o2.get(key)==null : o1.get(key).equals(o2.get(key));
	}
	
	private int counter;
	private int count() { counter++; return counter;}
	Map<Integer, JSONObject> template = new Hashtable<Integer, JSONObject>();
	protected JSONObject doPrint(Long bankId, JSONObject jo) {
		//Bank_Data.BRANCH_CODE = "branch_code";
		Object bank_codeObject = jo.get("bank_code");
		Object branch_codeObject = jo.get("branch_code");
		Integer branch_code;
        try {
    		branch_code = new Integer(Integer.parseInt(branch_codeObject.toString())); 
        } catch (NumberFormatException e) {
        	LOG.warning("branch_code "+branch_codeObject + " is not numeric.");
        	return jo;
        }        
        if(branch_code==0) {
        	// TODO in db einfügen insert (template filiale)
        	template.put((Integer)bank_codeObject, jo);
    		System.out.println(BankDataOrdered.toOrderedJSONString(jo) + ",");
        } else {
        	// filiale<>0 : suchen nach bankId = bank_code+branch_code
        	//gefunden: sollte nicht sein beim befüllen
        	//nicht gefunden:
        	//               - suche nach template: filiale bank_code+0
        	//               - ist diese equals jo (ohne "branch_code" natürlich) ==> nix tun
        	//               - else insert jo in db
        	JSONObject temp = template.get((Integer)bank_codeObject);
        	if(temp==null) {
        		LOG.warning("temp is null!");
        		System.out.println(BankDataOrdered.toOrderedJSONString(jo) + ",");
        	} else {
        		if(equalsKey("bank", jo, temp) && equalsKey("branch", jo, temp)
        		&& equalsKey("zip", jo, temp) && equalsKey("city", jo, temp)
            	&& equalsKey("address", jo, temp)
            	&& equalsKey("support_codes", jo, temp) ) {
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
		// loop über Filialen:
		bankCodeList.forEach(bankCode -> {
			for(int branch=from; branch<=to; branch++) {
				if(branch%1000==0) {
					int perCent = 100*branch/(to*bankCodeList.size());
					System.out.println(bankCode+","+branch + " %:"+ perCent);
				}
				String bankId = String.format(format, bankCode);
				String branchId = String.format(branchCodeFormat, branch);
				FakeIban iban = new FakeIban(countryCode, bankId, branchId);
				if(printBankDataViaApi(bankCode, iban)) count(); // mit callback doPrint(JSONObject jo)
			}
		});
	}

}
