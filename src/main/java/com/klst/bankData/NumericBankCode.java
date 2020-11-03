package com.klst.bankData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.github.miachm.sods.Range;
import com.github.miachm.sods.Sheet;
import com.klst.iban.BankDataGenerator;
import com.klst.iban.BusinessIdentifierCode;
import com.klst.ods.Ods;

/* Oberklasse für

    	countryToFunc.put("AT", NUMERIC_BANKCODE);  subclass existiert
    	countryToFunc.put("BE", NUMERIC_BANKCODE);
    	countryToFunc.put("CH", NUMERIC_BANKCODE);
    	countryToFunc.put("CZ", NUMERIC_BANKCODE);
    	countryToFunc.put("DE", NUMERIC_BANKCODE_WITH_MAP);
    	countryToFunc.put("DK", NUMERIC_BANKCODE);
    	countryToFunc.put("EE", NUMERIC_BANKCODE);
    	countryToFunc.put("FI", NUMERIC_BANKCODE);  subclass existiert
    	countryToFunc.put("HR", NUMERIC_BANKCODE);
    	countryToFunc.put("IS", NUMERIC_BANKCODE);
    	countryToFunc.put("LI", NUMERIC_BANKCODE);
    	countryToFunc.put("LT", NUMERIC_BANKCODE);
    	countryToFunc.put("LU", NUMERIC_BANKCODE);
    	countryToFunc.put("NO", NUMERIC_BANKCODE);
    	countryToFunc.put("PL", NUMERIC_BANKCODE);
    	countryToFunc.put("SE", NUMERIC_BANKCODE);
    	countryToFunc.put("SI", NUMERIC_BANKCODE);
    	countryToFunc.put("SK", NUMERIC_BANKCODE);  subclass existiert, datan generiert
    	countryToFunc.put("VA", NUMERIC_BANKCODE);


 */
public class NumericBankCode extends BankDataGenerator {

	private static final Logger LOG = Logger.getLogger(NumericBankCode.class.getName());

	String countryCode;
	Map<Integer, ArrayList<Object>> bankByCode = null;
	ArrayList<Integer> refBankByCodeArray = null; /* Beispiel:
		new ArrayList<Integer>(Arrays.asList(0, 1, 2)); */
	ArrayList<Object> columnMapper = null;

	protected NumericBankCode(String countryCode, String api_key) {
		super(api_key);
		this.countryCode = countryCode;
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
		loadBankByCode(odsFilePath, 1);
	}
	void loadBankByCode(String odsFilePath, int firstRow) {
        List<Sheet> sheets = Ods.getSheets(odsFilePath);
    	int numColumns = columnMapper.size();

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
	
	void tryWith(String format, int from, int to) {
		tryWith(countryCode, format, from, to);
	}

	public void tryWith(String countryCode, String format, int from, int to) {
		List<Integer> bankCodeList = bankByCode==null ? null : new ArrayList<Integer>(bankByCode.keySet());
		for(int id=from; id<=to; id++) {
			String bankCode = String.format(format, id);
			FakeIban iban = new FakeIban(countryCode, bankCode);
			if(bankCodeList==null) {
				// suchen
				LOG.info("id="+id + " tryWith "+iban+" bankCode "+bankCode);
//    			printBankDataViaApi(id, iban);
			} else if(bankCodeList.contains(id)) {
    			LOG.info("id="+id + " tryWith "+iban+" bankCode "+bankCode);
//    			printBankDataViaApi(id, iban);
			} else {
				// unnötige iban.com Abfrage
			}
		}
		
	}

}
