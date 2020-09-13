package com.klst.iban;

/* Business Identifier Code , see https://de.wikipedia.org/wiki/ISO_9362
	BIC früher auch Acronym für "Bank Identifier Code"
	SWIFT-BIC, auch BIC-Code, SWIFT-Code, SWIFT-Adresse

	Beispiel: BELADEBEXXX ist der Berliner Sparkasse zugewiesen. 
	          Diese gehört zur Berliner Landesbank (BE LA) in Deutschland (DE) mit Sitz in Berlin (BE).
	
	Aufbau: BBBBCCLLbbb
	bank_code + Ländercode/ISO_3166-1 + Ortscode + (optional)branch_code
	
	Ortscode:
	Das erste Zeichen darf nicht die Ziffer „0“ oder „1“ sein. 
	Wenn das zweite Zeichen kein Buchstabe, sondern eine Ziffer ist, so bedeutet dies:

    0 – es handelt sich um einen Test-BIC
    1 – es handelt sich um einen passiven SWIFT-Teilnehmer
    2 – der Empfänger zahlt die Transaktionskosten

	Der Buchstabe 'O' ist als zweites Zeichen nicht gestattet.
	
	branch_code:
	Ein 8-stelliger BIC kann um „XXX“ auf einen 11-stelligen ergänzt werden, 
	entsprechend kann „XXX“ auch weggelassen werden, andere Kennzeichen nicht. 
	Der Branch-Code darf nicht mit „X“ anfangen, es sei denn, es ist „XXX“.

 */
public class BusinessIdentifierCode {

	String bic;
	
	BusinessIdentifierCode(String bic) {
		this.bic = bic;
	}
	
	public boolean isValid() {
		if(bic==null) return false;
		if(bic.length()<8 || bic.length()>11) return false;
		
		String countryCode = getCountryCode();
		if(!isValid(countryCode)) return false;

		String locationCode = getLocationCode();
		char ch1 = locationCode.charAt(0);
		if(ch1=='0' || ch1=='1') return false;
		if(locationCode.charAt(1)=='O') return false; // Test-BIC

		String branchCode = getBranchCode();
		if(branchCode.length()==0 || branchCode.equals(PRIMARY_OFFICE)) return true;
		if(branchCode.length()==3 && branchCode.charAt(0)=='X') return false;
		return true;
	}
	
	public boolean isTestBIC() {
		String locationCode = getLocationCode();
		return locationCode.charAt(1)=='O';
	}
	
	public boolean isPassive() {
		String locationCode = getLocationCode();
		return locationCode.charAt(1)=='1';
	}

	//4 letters: institution code or bank code.
	public String getInstitutionCode() {
		return getBankCode();
	}
	public String getBankCode() {
		return bic.substring(0,4);
	}
	
	//2 letters: ISO 3166-1 alpha-2 country code + XK
	// DomainValidator getInstance()
	// DomainValidator public boolean isValidCountryCodeTld(String ccTld) {
	public String getCountryCode() {
		return bic.substring(4,6);
	}
	private boolean isValid(String countryCode) {
		// TODO
		return true;
	}

	// 2 letters or digits: location code
	public String getLocationCode() {
		return bic.substring(6,8);
	}
	
	// 3 letters or digits: branch code, optional ('XXX' for primary office)
	public static final String PRIMARY_OFFICE = "XXX";
	public String getBranchCode() {
		String branchCode = "";
		if (bic.length() == 11) {
			branchCode = bic.substring(8, 11);
		}
		return branchCode;
	}
	
	public String toString() {
		return bic;
	}
}
