package com.klst.iban;

import java.util.logging.Logger;

import org.apache.commons.validator.routines.IBANValidator;

import com.klst.iban.Result.BankData;
import com.klst.iban.Result.SepaData;

public class IbanToBankData { 
	
	private static final Logger LOG = Logger.getLogger(IbanToBankData.class.getName());
	
	String api_key = null;
	String iban = null;
	
	public IbanToBankData() {
		this(null);
	}
	public IbanToBankData(String api_key) {
		this.api_key = api_key;
	}
	
	public BankData getBankData(String iban) {
		IBANValidator validator = IBANValidator.getInstance();
		if(!validator.isValid(iban)) return null;

		LOG.info(iban + " is valid.");		
		this.iban = iban;
		return getBankData();
	}
	
	private BankData getBankData() {
		BankData bankData = new BankData();
		SepaData sepaData = new SepaData();
		String countryCode = iban.substring(0, 2);
		String bban = iban.substring(4);
//		if(iban.startsWith("AD")) {
//		// AD2!n4!n4!n12!c | "AD\\d{10}[A-Z0-9]{12}"
//			// Account ist in IBAN REGISTRY alpha, in Beispielen aber num
//			// in iban.com muss es numerisch sein
///*
//{"bank_data":{"bic":"BACAADADXXX","branch":"Serveis Centrals","bank":"andbanc Grup Agr"
//             ,"address":"C\/ Manuel Cerqueda i Escaler, 6","city":"Escaldes-Engordany","state":null,"zip":""
//             ,"phone":"87 33 33","fax":"86 39 05","www":null,"email":null,"country":"Andorra","country_iso":"AD"
//             ,"account":"200359100100","bank_code":"0001","branch_code":"2030"}
//,"sepa_data":{"SCT":"YES","SDD":"NO","COR1":"NO","B2B":"NO","SCC":"NO"}
//,"validations":{"chars":{"code":"006","message":"IBAN does not contain illegal characters"},"account":{"code":"004","message":"Account Number check digit is not performed for this bank or branch"},"iban":{"code":"001","message":"IBAN Check digit is correct"},"structure":{"code":"005","message":"IBAN structure is correct"},"length":{"code":"003","message":"IBAN Length is correct"},"country_support":{"code":"007","message":"Country supports IBAN standard"}},"errors":[]}
//
// */
//			RegexValidator regexValidator = new RegexValidator("(AD)(\\d{2})(\\d{4})(\\d{4})([A-Z0-9]{12})");
////			RegexValidator regexValidator = new RegexValidator("(AD)(\\d{2})(\\d{4})(\\d{4})(\\d{12})");
//			String[] groups = regexValidator.match(iban);
//			LOG.info("#groups="+groups.length);
//			for(int i=0; i<groups.length; i++) {
//				LOG.info("i="+i + ":" + groups[i]);
//			}
//			bankData.setCountryIso(groups[0]);
//			bankData.setBankCode(Integer.parseInt(groups[2]));
//			bankData.setBranchCode(Integer.parseInt(groups[3]));
//			bankData.setAccount(-1); // unbekannt bzw. anonym
////			bankData.setAccount(Long.parseLong(groups[4]));
////			bankData.setBic("BACAADADXXX");
////			bankData.setBranch("Serveis Centrals");
////			bankData.setBank("andbanc Grup Agr");
////			bankData.setAddress("C/ Manuel Cerqueda i Escaler, 6");
////			bankData.setCity("Escaldes-Engordany");
////			bankData.setPhone("87 33 33");
////			bankData.setPhone("86 39 05");
////			
////			sepaData.setSCT("YES");
////			sepaData.setSDD("NO");
////			sepaData.setCOR1("NO");
////			sepaData.setB2B("NO");
////			sepaData.setSCC("NO");
////			LOG.info("BankSupports="+sepaData.getBankSupports());
//
///*
//{"bank_data":{"bic":"BOMLAEADXXX","branch":null,"bank":"Mashreqbank","address":"AL GHURAIR CITY 339-C, AGC AL RIQQA STREET","city":"DUBAI 04"
//             ,"state":null,"zip":"","phone":null,"fax":null,"www":null,"email":null,"country":"United Arab Emirates","country_iso":"AE"
//             ,"account":"1234567890123456","bank_code":"033","branch_code":""}
//,"sepa_data":{"SCT":"NO","SDD":"NO","COR1":"NO","B2B":"NO","SCC":"NO"}
//,"validations":{"chars":{"code":"006","message":"IBAN does not contain illegal characters"},"account":{"code":"004","message":"Account Number check digit is not performed for this bank or branch"},"iban":{"code":"001","message":"IBAN Check digit is correct"},"structure":{"code":"005","message":"IBAN structure is correct"},"length":{"code":"003","message":"IBAN Length is correct"},"country_support":{"code":"007","message":"Country supports IBAN standard"}},"errors":[]}
//
// */
//		} else if(iban.startsWith("AE")) {
//			// AE2!n3!n16!n
//			RegexValidator regexValidator = new RegexValidator("(AE)(\\d{2})(\\d{3})(\\d{16})");
//			String[] groups = regexValidator.match(iban);
//			bankData.setCountryIso(groups[0]);
//			bankData.setBankCode(Integer.parseInt(groups[2]));
//			bankData.setAccount(-1); // unbekannt bzw. anonym
////			bankData.setAccount(Integer.parseInt(groups[3]); // Account zu lang für int
//		} else if(iban.startsWith("AL")) {
//			// AL2!n8!n16!c
///* AL47 212 1100 90000000235698741
//{"bank_data":{"bic":"CDISALTRXXX","branch":"Head Office","bank":"Credins Bank","address":"Rr. \"Ismail Qemali\", nr. 21","city":"Tirane","state":null,"zip":"1019","phone":null,"fax":null,"www":null,"email":null,"country":"Albania","country_iso":"AL"
//             ,"account":"90000000235698741","bank_code":"212","branch_code":"1100"}
//,"sepa_data":{"SCT":"NO","SDD":"NO","COR1":"NO","B2B":"NO","SCC":"NO"}
//,"validations":{"chars":{"code":"006","message":"IBAN does not contain illegal characters"},"account":{"code":"004","message":"Account Number check digit is not performed for this bank or branch"},"iban":{"code":"001","message":"IBAN Check digit is correct"},"structure":{"code":"005","message":"IBAN structure is correct"},"length":{"code":"003","message":"IBAN Length is correct"},"country_support":{"code":"007","message":"Country supports IBAN standard"}},"errors":[]}
// */
////			RegexValidator regexValidator = new RegexValidator("(AL)(\\d{2})(\\d{8})([A-Z0-9]{16})");
//			// iban.com validiert anders:
//			RegexValidator regexValidator = new RegexValidator("(AL)(\\d{2})(\\d{3})(\\d{4})([A-Z0-9]{17})");
//			String[] groups = regexValidator.match(iban);
//			bankData.setCountryIso(groups[0]);
//			bankData.setBankCode(Integer.parseInt(groups[2]));
//			bankData.setBranchCode(Integer.parseInt(groups[3]));
//			bankData.setAccount(-1); // unbekannt bzw. anonym
//		} else if(iban.startsWith("AT")) {
//			// AT2!n5!n11!n
//			RegexValidator regexValidator = new RegexValidator("(AT)(\\d{2})(\\d{5})(\\d{11})");
//			String[] groups = regexValidator.match(iban);
//			bankData.setCountryIso(groups[0]);
//			bankData.setBankCode(Integer.parseInt(groups[2]));
//			bankData.setAccount(-1); // unbekannt bzw. anonym
//			bankData.setAccount(Integer.parseInt(groups[3]));
//		} else if("AZ".equals(countryCode)) {
//			// AZ2!n4!a20!c AZ21NABZ00000000137010001944
///*
//{"bank_data":{"bic":"NABZAZ2XXXX","branch":null,"bank":"Central Bank of the Republic of Azerbaijan","address":"AZ1014 R.Behbudov Str.32","city":"Baku","state":null,"zip":"","phone":"493-11-22","fax":null,"www":null,"email":null,"country":"Azerbaijan","country_iso":"AZ"
//             ,"account":"00000000137010001944","bank_code":"NABZ","branch_code":""}
//,"sepa_data":{"SCT":"NO","SDD":"NO","COR1":"NO","B2B":"NO","SCC":"NO"}
//,"validations":{"chars":{"code":"006","message":"IBAN does not contain illegal characters"},"account":{"code":"004","message":"Account Number check digit is not performed for this bank or branch"},"iban":{"code":"001","message":"IBAN Check digit is correct"},"structure":{"code":"005","message":"IBAN structure is correct"},"length":{"code":"003","message":"IBAN Length is correct"},"country_support":{"code":"007","message":"Country supports IBAN standard"}},"errors":[]}
// */
//			RegexValidator regexValidator = new RegexValidator("([A-Z]{4})([A-Z0-9]{20})");
//			String[] groups = regexValidator.match(bban);
//			bankData.setCountryIso(countryCode);
//			bankData.setBankIdentifier(groups[0]);
////			bankData.setBankCode(Integer.parseInt(groups[0])); // ist nicht int!!!
//			// TODO ist hashCode eine geeignete Kandidat für id?
//			LOG.info("#groups="+groups.length + ", bank_code:"+groups[0] + ", hashCode:"+groups[0].hashCode());
//			bankData.setAccount(-1); // unbekannt bzw. anonym
////			bankData.setAccount(Integer.parseInt(groups[1])); // nicht int!
//		} else if("BA".equals(countryCode)) {
//			// BA2!n 3!n3!n8!n2!n BA391290079401028494
//			RegexValidator regexValidator = new RegexValidator("(\\d{3})(\\d{3})(\\d{8})(\\d{2})");
//			String[] groups = regexValidator.match(bban);
//			bankData.setCountryIso(countryCode);
//			bankData.setBankCode(Integer.parseInt(groups[0]));
//			bankData.setBranchCode(Integer.parseInt(groups[1]));
//			bankData.setAccount(-1); // unbekannt bzw. anonym
////			bankData.setAccount(Integer.parseInt(groups[2]+groups[3])); // zu lang für int
//		} else if(iban.startsWith("BE")) {
//			// IBAN structure BE2!n3!n7!n2!n
////			Validator validatorExt = new Validator("BE", 16, "BE\\d{14}"); // Belgium
//			RegexValidator regexValidator = new RegexValidator("(BE)(\\d{2})(\\d{3})(\\d{7})(\\d{2})");
//			String[] groups = regexValidator.match(iban);
//			LOG.info("#groups="+groups.length);
//			for(int i=0; i<groups.length; i++) {
//				LOG.info("i="+i + ":" + groups[i]);
//			}
//			bankData.setCountryIso(groups[0]);
//			bankData.setBankCode(Integer.parseInt(groups[2]));
//			bankData.setAccount(Integer.parseInt(groups[3]+groups[4]));
//		} else if("BG".equals(countryCode)) {
///* BG2!n4!a4!n2!n8!c BG80 BNBG 9661 1020345678 | BG64UNCR 9660 1010 6880 21
//{"bank_data":{"bic":"BNBGBGSFXXX","branch":"","bank":"BULGARIAN NATIONAL BANK","address":"ALEXANDER BATTENBERG SQUARE 1","city":" SOFIA","state":null,"zip":"1000","phone":"","fax":null,"www":null,"email":null,"country":"Bulgaria","country_iso":"BG"
//,"account":"1020345678","bank_code":"BNBG","branch_code":"9661"}
//,"sepa_data":{"SCT":"YES","SDD":"NO","COR1":"NO","B2B":"NO","SCC":"NO"}
//,"validations":{"chars":{"code":"006","message":"IBAN does not contain illegal characters"},"account":{"code":"004","message":"Account Number check digit is not performed for this bank or branch"},"iban":{"code":"001","message":"IBAN Check digit is correct"},"structure":{"code":"005","message":"IBAN structure is correct"},"length":{"code":"003","message":"IBAN Length is correct"},"country_support":{"code":"007","message":"Country supports IBAN standard"}},"errors":[]}
// */
//			RegexValidator regexValidator = new RegexValidator(BbanValidator.BBAN_FORMATS.get(countryCode));
//			String[] groups = regexValidator.match(bban);
//			bankData.setCountryIso(countryCode);
//			bankData.setBankIdentifier(groups[0]);
////			bankData.setBankCode(Integer.parseInt(groups[0])); // nicht numerisch
//			bankData.setBranchCode(Integer.parseInt(groups[1]));
//			bankData.setAccount(-1); // unbekannt bzw. anonym
////			bankData.setAccount(Integer.parseInt(groups[2]+groups[3])); // zu lang für int
//		} else if("BH".equals(countryCode)) {
//			// BH2!n4!a14!c BH67BMAG00001299123456
//			RegexValidator regexValidator = new RegexValidator(BbanValidator.BBAN_FORMATS.get(countryCode));
//			String[] groups = regexValidator.match(bban);
//			bankData.setCountryIso(countryCode);
//			bankData.setBankIdentifier(groups[0]);
//			bankData.setBranchCode(Integer.parseInt(groups[1]));
//			bankData.setAccount(-1); // unbekannt bzw. anonym
//		} else if("BR".equals(countryCode)) {
///* BR2!n8!n5!n10!n1!a1!c BR1800360305000010009795493C1
//{"bank_data":{"bic":"CEFXBRSPXXX","branch":"PLANALTO, DF","bank":"CAIXA ECONOMICA FEDERAL","address":"SBS QD. 01 - BL. L - TERREO","city":"BRASILIA","state":"DF","zip":"70070-110","phone":"61 34219053","fax":null,"www":"www.caixa.gov.br","email":"seger@caixa.gov.br","country":"Brazil","country_iso":"BR"
//             ,"account":"0009795493C1","bank_code":"00360305","branch_code":"00001"}
//,"sepa_data":{"SCT":"NO","SDD":"NO","COR1":"NO","B2B":"NO","SCC":"NO"}
//,"validations":{"chars":{"code":"006","message":"IBAN does not contain illegal characters"},"account":{"code":"004","message":"Account Number check digit is not performed for this bank or branch"},"iban":{"code":"001","message":"IBAN Check digit is correct"},"structure":{"code":"005","message":"IBAN structure is correct"},"length":{"code":"003","message":"IBAN Length is correct"},"country_support":{"code":"007","message":"Country supports IBAN standard"}},"errors":[]}
// */
//			RegexValidator regexValidator = new RegexValidator(BbanValidator.BBAN_FORMATS.get(countryCode));
//			String[] groups = regexValidator.match(bban);
//			bankData.setCountryIso(countryCode);
//			bankData.setBankIdentifier(groups[0]);
//			bankData.setBranchCode(Integer.parseInt(groups[1]));
//			bankData.setAccount(-1); // unbekannt bzw. anonym
//		} else if("BY".equals(countryCode)) {
//			// BY2!n4!c4!n16!c BY13NBRB3600900000002Z00AB00
//			RegexValidator regexValidator = new RegexValidator(BbanValidator.BBAN_FORMATS.get(countryCode));
//			String[] groups = regexValidator.match(bban);
//			bankData.setCountryIso(countryCode);
//			bankData.setBankIdentifier(groups[0]);
//			bankData.setBranchCode(Integer.parseInt(groups[1]));
//			bankData.setAccount(-1); // unbekannt bzw. anonym
//		} else if("CH".equals(countryCode)) {
//			// CH2!n5!n12!c CH9300762011623852957
//			RegexValidator regexValidator = new RegexValidator(BbanValidator.BBAN_FORMATS.get(countryCode));
//			String[] groups = regexValidator.match(bban);
//			bankData.setCountryIso(countryCode);
//			bankData.setBankIdentifier(groups[0]);
//			bankData.setAccount(-1); // unbekannt bzw. anonym
//		} else if("CR".equals(countryCode)) {
//			// CR2!n4!n14!n CR05015202001026284066
//			RegexValidator regexValidator = new RegexValidator(BbanValidator.BBAN_FORMATS.get(countryCode));
//			String[] groups = regexValidator.match(bban);
//			bankData.setCountryIso(countryCode);
//			bankData.setBankIdentifier(groups[0]);
//			bankData.setAccount(-1); // unbekannt bzw. anonym
//		} else if("CY".equals(countryCode)) {
//			// CY2!n3!n5!n16!c CY17002001280000001200527600
//			RegexValidator regexValidator = new RegexValidator(BbanValidator.BBAN_FORMATS.get(countryCode));
//			String[] groups = regexValidator.match(bban);
//			bankData.setCountryIso(countryCode);
//			bankData.setBankIdentifier(groups[0]);
//			bankData.setBranchCode(Integer.parseInt(groups[1]));
//			bankData.setAccount(-1); // unbekannt bzw. anonym
//		} else if("CZ".equals(countryCode)) {
//			// CZ2!n4!n6!n10!n CZ6508000000192000145399
//			RegexValidator regexValidator = new RegexValidator(BbanValidator.BBAN_FORMATS.get(countryCode));
//			String[] groups = regexValidator.match(bban);
//			bankData.setCountryIso(countryCode);
//			bankData.setBankIdentifier(groups[0]);
//			bankData.setBranchCode(Integer.parseInt(groups[1]));
//			bankData.setAccount(-1); // unbekannt bzw. anonym
		//} else 
		if(BbanValidator.BBAN_DATA.get(countryCode)!=null) {
/* DK2!n4!n9!n1!n DK5000400440116243
{"bank_data":{"bic":"NDEADKKKXXX","branch":"VORDINGBORG AFDELING","bank":"NORDEA","address":"PRINS J\u00d8RGENS ALLE 6","city":"VORDINGBORG","state":null,"zip":"4760","phone":"70 33 33 33","fax":"55 34 01 11","www":"","email":null,"country":"Denmark","country_iso":"DK"
,"account":"0440116243","bank_code":"0040","branch_code":""}
,"sepa_data":{"SCT":"YES","SDD":"NO","COR1":"NO","B2B":"NO","SCC":"NO"}
,"validations":{"chars":{"code":"006","message":"IBAN does not contain illegal characters"},"account":{"code":"201","message":"Account Number check digit not correct"},"iban":{"code":"001","message":"IBAN Check digit is correct"},"structure":{"code":"005","message":"IBAN structure is correct"},"length":{"code":"003","message":"IBAN Length is correct"},"country_support":{"code":"007","message":"Country supports IBAN standard"}},"errors":[]}
 */
			BbanValidator bData = BbanValidator.BBAN_DATA.get(countryCode); // liefert eine Instanz mit Methode
			bankData = bData.getBankData(iban);
		} else {
			LOG.warning(iban + " NOT implemented.");			
		}
		LOG.info(iban + " -> bankData:"+bankData);
		return bankData;
	}

	public static void main(String[] args) throws Exception {
		IbanToBankData test = new IbanToBankData();
		test.getBankData("AD1200012030200359100100");
		test.getBankData("AE070331234567890123456");
		test.getBankData("AL47212110090000000235698741");
		test.getBankData("AT611904300234573201");
		test.getBankData("AZ21NABZ00000000137010001944");
		test.getBankData("BA391290079401028494");
		test.getBankData("BE94049123456789");
		test.getBankData("BG80BNBG96611020345678");
		test.getBankData("BH67BMAG00001299123456");
		test.getBankData("BR1800360305000010009795493C1");
		test.getBankData("BY13NBRB3600900000002Z00AB00");
		test.getBankData("CH9300762011623852957");	
		test.getBankData("CR05015202001026284066");	
		test.getBankData("CY17002001280000001200527600");	
		test.getBankData("CZ6508000000192000145399");	
		test.getBankData("DE89370400440532013000");	
		test.getBankData("DK5000400440116243");	
		test.getBankData("DO28BAGR00000001212453611324");
		test.getBankData("EE382200221020145685");
/*
{"bank_data":{"bic":"HABAEE2XXXX","branch":null,"bank":"SWEDBANK AS","address":"LIIVALAIA 8","city":"TALLINN","state":null,"zip":"15040","phone":null,"fax":null,"www":null,"email":null,"country":"Estonia","country_iso":"EE"
             ,"account":"00221020145685","bank_code":"22","branch_code":""}
,"sepa_data":{"SCT":"YES","SDD":"NO","COR1":"NO","B2B":"NO","SCC":"NO"}
,"validations":{"chars":{"code":"006","message":"IBAN does not contain illegal characters"},"account":{"code":"002","message":"Account Number check digit is correct"},"iban":{"code":"001","message":"IBAN Check digit is correct"},"structure":{"code":"005","message":"IBAN structure is correct"},"length":{"code":"003","message":"IBAN Length is correct"},"country_support":{"code":"007","message":"Country supports IBAN standard"}},"errors":[]}		
 */
		test.getBankData("EG380019000500000000263180002");
		test.getBankData("ES9121000418450200051332");
		test.getBankData("FI2112345600000785");
		test.getBankData("FO6264600001631634");
		test.getBankData("FR1420041010050500013M02606");
/*
{"bank_data":{"bic":"PSSTFRPPLIL","branch":null,"bank":"LA BANQUE POSTALE","address":"3 RUE PAUL DUEZ","city":"LILLE CEDEX 9","state":null,"zip":"59900","phone":null,"fax":null,"www":null,"email":null,"country":"FRANCE","country_iso":"FR"
,"account":"0500013M026","bank_code":"20041","branch_code":"01005"}
,"sepa_data":{"SCT":"YES","SDD":"YES","COR1":"YES","B2B":"YES","SCC":"NO"}
,"validations":{"chars":{"code":"006","message":"IBAN does not contain illegal characters"},"account":{"code":"002","message":"Account Number check digit is correct"},"iban":{"code":"001","message":"IBAN Check digit is correct"},"structure":{"code":"005","message":"IBAN structure is correct"},"length":{"code":"003","message":"IBAN Length is correct"},"country_support":{"code":"007","message":"Country supports IBAN standard"}},"errors":[]}
 */
		test.getBankData("GB29NWBK60161331926819");
		test.getBankData("GE29NB0000000101904917");
		test.getBankData("GI75NWBK000000007099453");
		test.getBankData("GL8964710001000206");
		test.getBankData("GR1601101250000000012300695");
		test.getBankData("GT82TRAJ01020000001210029690");
		test.getBankData("HR1210010051863000160");
		test.getBankData("HU42117730161111101800000000");
		test.getBankData("IE29AIBK93115212345678");
		test.getBankData("IL620108000000099999999");
		test.getBankData("IQ98NBIQ850123456789012");
		test.getBankData("IS140159260076545510730339");
/*
{"bank_data":{"bic":"NBIIISREXXX","branch":null,"bank":"Landsbankinn hf","address":"AUSTURSTRAETI 11","city":"REYKJAVIK","state":null,"zip":"155","phone":null,"fax":null,"www":null,"email":null,"country":"Iceland","country_iso":"IS"
,"account":"007654","bank_code":"0159","branch_code":""}
,"sepa_data":{"SCT":"YES","SDD":"NO","COR1":"NO","B2B":"NO","SCC":"NO"}
,"validations":{"chars":{"code":"006","message":"IBAN does not contain illegal characters"},"account":{"code":"002","message":"Account Number check digit is correct"},"iban":{"code":"001","message":"IBAN Check digit is correct"},"structure":{"code":"005","message":"IBAN structure is correct"},"length":{"code":"003","message":"IBAN Length is correct"},"country_support":{"code":"007","message":"Country supports IBAN standard"}},"errors":[]}
 */
		test.getBankData("IT60X0542811101000000123456");
		test.getBankData("JO94CBJO0010000000000131000302");
/*
{"bank_data":{"bic":"CBJOJOAXXXX","branch":null,"bank":"CENTRAL BANK OF JORDAN","address":"King Hussein Street 11118 AMMAN","city":"","state":null,"zip":"","phone":null,"fax":null,"www":null,"email":null,"country":"Jordan","country_iso":"JO"
,"account":"0010000000000131000302","bank_code":"CBJO","branch_code":""}
,"sepa_data":{"SCT":"NO","SDD":"NO","COR1":"NO","B2B":"NO","SCC":"NO"}
,"validations":{"chars":{"code":"006","message":"IBAN does not contain illegal characters"},"account":{"code":"004","message":"Account Number check digit is not performed for this bank or branch"},"iban":{"code":"001","message":"IBAN Check digit is correct"},"structure":{"code":"005","message":"IBAN structure is correct"},"length":{"code":"003","message":"IBAN Length is correct"},"country_support":{"code":"007","message":"Country supports IBAN standard"}},"errors":[]}
 */
		test.getBankData("KW81CBKU0000000000001234560101");
		test.getBankData("KZ86125KZT5004100100");
		test.getBankData("LB62099900000001001901229114");
		test.getBankData("LC55HEMM000100010012001200023015");
		test.getBankData("LI21088100002324013AA");
		test.getBankData("LT121000011101001000");
		test.getBankData("LU280019400644750000");
		test.getBankData("LV80BANK0000435195001");
		test.getBankData("MC5811222000010123456789030");
		test.getBankData("MD24AG000225100013104168");
		test.getBankData("ME25505000012345678951");
		test.getBankData("MK07250120000058984");
		test.getBankData("MR1300020001010000123456753");
		test.getBankData("MT84MALT011000012345MTLCAST001S");
		test.getBankData("MU17BOMM0101101030300200000MUR");
		test.getBankData("NL91ABNA0417164300");
		test.getBankData("NO9386011117947");
		test.getBankData("PK36SCBL0000001123456702");
		test.getBankData("PL61109010140000071219812874");
/*
{"bank_data":{"bic":"WBKPPLPPXXX","branch":"1 Oddzial w Warszawie","bank":"Santander Bank Polska Spolka Akcyjna","address":"ul. Kasprowicza 119A","city":"Warszawa","state":null,"zip":"01-949","phone":null,"fax":null,"www":"www.santander.pl","email":null,"country":"Poland","country_iso":"PL"
             ,"account":"0000071219812874","bank_code":"10901014","branch_code":""}
,"sepa_data":{"SCT":"YES","SDD":"NO","COR1":"NO","B2B":"NO","SCC":"NO"}
,"validations":{"chars":{"code":"006","message":"IBAN does not contain illegal characters"},"account":{"code":"002","message":"Account Number check digit is correct"},"iban":{"code":"001","message":"IBAN Check digit is correct"},"structure":{"code":"005","message":"IBAN structure is correct"},"length":{"code":"003","message":"IBAN Length is correct"},"country_support":{"code":"007","message":"Country supports IBAN standard"}},"errors":[]}
 */
		test.getBankData("PS92PALS000000000400123456702");
		test.getBankData("PT50000201231234567890154");
/*
{"bank_data":{"bic":null,"branch":null,"bank":null,"address":null,"city":null,"state":null,"zip":null,"phone":null,"fax":null,"www":null,"email":null,"country":"Portugal","country_iso":"PT"
             ,"account":"1234567890154","bank_code":"0002","branch_code":"0123"}
,"sepa_data":{"SCT":"NO","SDD":"NO","COR1":"NO","B2B":"NO","SCC":"NO"}
,"validations":{"chars":{"code":"006","message":"IBAN does not contain illegal characters"},"account":{"code":"002","message":"Account Number check digit is correct"},"iban":{"code":"001","message":"IBAN Check digit is correct"},"structure":{"code":"005","message":"IBAN structure is correct"},"length":{"code":"003","message":"IBAN Length is correct"},"country_support":{"code":"007","message":"Country supports IBAN standard"}},"errors":[]}
 */
		test.getBankData("QA58DOHB00001234567890ABCDEFG");
		test.getBankData("RO49AAAA1B31007593840000");
		test.getBankData("RS35260005601001611379");
		test.getBankData("SA0380000000608010167519");
		test.getBankData("SC18SSCB11010000000000001497USD");
/*
{"bank_data":{"bic":"SSCBSCSCXXX","branch":null,"bank":"CENTRAL BANK OF SEYCHELLES","address":"","city":"VICTORIA","state":null,"zip":"","phone":null,"fax":null,"www":null,"email":null,"country":"Seychelles","country_iso":"SC"
             ,"account":"11010000000000001497USD","bank_code":"SSCB","branch_code":""}
,"sepa_data":{"SCT":"NO","SDD":"NO","COR1":"NO","B2B":"NO","SCC":"NO"}
,"validations":{"chars":{"code":"006","message":"IBAN does not contain illegal characters"},"account":{"code":"004","message":"Account Number check digit is not performed for this bank or branch"},"iban":{"code":"001","message":"IBAN Check digit is correct"},"structure":{"code":"005","message":"IBAN structure is correct"},"length":{"code":"003","message":"IBAN Length is correct"},"country_support":{"code":"007","message":"Country supports IBAN standard"}},"errors":[]}
 */
		test.getBankData("SE4550000000058398257466");
		test.getBankData("SI56263300012039086");
		test.getBankData("SK3112000000198742637541");
		test.getBankData("SM86U0322509800000000270100");
		test.getBankData("ST23000100010051845310146");
		test.getBankData("SV62CENR00000000000000700025");
		test.getBankData("TL380080012345678910157");
		test.getBankData("TN5910006035183598478831");
		test.getBankData("TR330006100519786457841326");
		test.getBankData("UA213223130000026007233566001");
		test.getBankData("VA59001123000012345678");
		test.getBankData("VG96VPVG0000012345678901");
		test.getBankData("XK051212012345678906");
	}
}
