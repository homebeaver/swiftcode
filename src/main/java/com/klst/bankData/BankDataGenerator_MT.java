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
import com.klst.iban.datastore.LocalFileProxy;
import com.klst.ibanTest.API_Key_Provider;
import com.klst.ods.Ods;

// BIC_sort_codes_(asat15-10-2020) Quelle: https://www.centralbankmalta.org/iban
/*

BIC Code	Financial Institution Name	National ID (Sort Code)	Branch	Remarks
MALTMTMT	Central Bank of Malta		01100	Valletta	
EMONMTM2	Emoney plc.					02015	Sliema
...

 */
public class BankDataGenerator_MT extends BankDataGenerator {

	private static final Logger LOG = Logger.getLogger(BankDataGenerator_MT.class.getName());
	
	static final String countryCode = "MT";
    static final String ODS_RESOURCE = LocalFileProxy.RESOURCE_DATA_PATH + "doc/MT/BIC_sort_codes_(asat15-10-2020).ods";
    // BIC Code	| Financial Institution Name | National ID (Sort Code) | Branch | Remarks
	static final int COL_BIC                     =  0;
	static final int COL_BANK                    =  1;  // name
	static final int COL_SORTCODE                =  2;  // aka sortCode aka bankCode
	static final int COL_BRANCH                  =  3;
	static final int COL_REMARKS                 =  4;
	static final int NUMCOLUMNS                  =  5;
	
	Map<Integer, ArrayList<Object>> bankByCode = new Hashtable<Integer, ArrayList<Object>>();
//	Map<String, ArrayList<Object>> bankByBic = new Hashtable<String, ArrayList<Object>>();

	BankDataGenerator_MT(String api_key) {
		super(api_key);
		
        List<Sheet> sheets = Ods.getSheets(ODS_RESOURCE);
    	int numColumns = NUMCOLUMNS;

        Map<String,Integer> nonEmptySheets = new Hashtable<String,Integer>();
        Sheet nonEmptySheet = Ods.getNonEmptySheet(sheets, nonEmptySheets, numColumns);
        LOG.info("file "+ODS_RESOURCE+" has nonEmptySheets/sheets:"+nonEmptySheets.size()+"/"+sheets.size());
		if (nonEmptySheets.size() == 1) {
			Collection<Integer> collection = nonEmptySheets.values();
			int numRows = collection.iterator().next();
			Range range = nonEmptySheet.getRange(0, 0, numRows, numColumns);
			LOG.info("range.getNumRows()=" + range.getNumRows() + " range.getNumColumns()=" + range.getNumColumns());
			Object[][] values = range.getValues();
			// r==0 ist colname, daher start bei 1
			String lastbic = null;
			for (int r = 1; r < range.getNumRows(); r++) {
    			Object bic = values[r][COL_BIC]; // String, aber nicht immer
    			Object sortcode = values[r][COL_SORTCODE]; // String numeric
    			Object name = values[r][COL_BANK];
//				for (int c = 0; c < range.getNumColumns(); c++) {
//					Object v = values[r][c];
//					Object cellObect = range.getCell(r, c).getValue();
//					LOG.info("r(" + r + "),c:" + c + " " + (v == null ? "null" : v.getClass()) + " " + cellObect);
//				}
    			
				Integer bankCode = Ods.getInteger(sortcode);
				if(bic==null || bic.toString().trim().isEmpty()) {
					bic = lastbic;
				}
    			LOG.info("r("+r+"):"+values[r][COL_BIC]+" "+sortcode + " ==> " + bic + ":"+bankCode);
    			lastbic = (String)bic;
    			
    			if(bankCode!=null) {
    				bankByCode.put(bankCode, new ArrayList<Object>(Arrays.asList(new BusinessIdentifierCode((String)bic), name)));
    			}
			}

		}

		LOG.info("bankByCode.size="+bankByCode.size());
	};
	
/*

- SortCode ist eine Art num branch code
- in Map<Integer, ArrayList<Object>> bankByCode ist das der Key
	 SortCode ==> [ bic , name ] // value ist eine Liste der bankProps
	 prop.name wird nicht benötigt

 */
	public void tryWith(String countryCode, String format, int from, int to, String account) {
		List<Integer> sortCodeList = new ArrayList<Integer>(bankByCode.keySet());
		for(int id=from; id<=to; id++) {
			if(sortCodeList.contains(id)) {
	    		String sortCode = String.format(format, id);
	    		ArrayList<Object> bankProps = bankByCode.get(id); // [ bic , name ]
	    		BusinessIdentifierCode bic = (BusinessIdentifierCode)bankProps.get(0);
    			String iban = countryCode + PP + bic.getBankCode()+ sortCode + account;
    			LOG.info("id="+id + " tryWith iban "+iban);
//    			printBankDataViaApi(id, iban);
			}
		}
		
	}

	public static void main(String[] args) throws Exception {
		BankDataGenerator test = new BankDataGenerator_MT(API_Key_Provider.API_KEY);
		test.tryWith(countryCode, BankDataGenerator.FORMAT_05d, 00000, 99999, "0012345MTLCAST001S"); // MT84 MALT 01100 0012345MTLCAST001S
	}
}
