package com.klst.bankData;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Logger;

import com.klst.ibanTest.API_Key_Provider;

/*
ABI (5 cifre, 0nnnn) + CAB (5 cifre)
CAB(Codice di Avviamento Bancario)

SM SanMarino, 5 Banken siehe https://www.bcsm.sm/site/en/home/functions/statutary-functions/payments-system/authorized-banks.html
ASTTSMSMXXX	3262	ASSET BANCA S.P.A.SASM
BASMSMSMXXX	3171	BANCA AGRICOLA COMMERCIALE ISTITUTO BANCARIO SAMMARINESE SPA
BASMSMSMXXX	3034	BANCA AGRICOLA COMMERCIALE REPUBBLICA DI SAN MARINOSASM
                  , CAB 09800, 09804, 09801, 09811, 09812
BSDISMSDXXX	3287	BANCA SAMMARINESE DI INVESTIMEN TOSASM
BTITSMS1XXX	3145	BANCA IMPRESA DI SAN MARINO S.P.A.
CRRNSMSMXXX	3530	CREDITO INDUSTRIALE SAMMARINESE - SERRAVALLE (RSM)SASM
CRRNSMSMXXX	3178	BANCA CIS S.P.A.
CSSMSMSMXXX	6067	CASSA DI RISPARMIO DELLA REPUBBLICA DI SAN MARINOSASM
ICSMSMSMXXX	3225	BANCA CENTRALE DELLA REPUBBLICA DI SAN MARINOSASM
MAOISMSMXXX	8540	BANCA DI SAN MARINO S.P.A.SASM

Authorized Banks

Corporate name:
Banca Agricola Commerciale Istituto Bancario Sammarinese s.p.a.
Head Office: Via 3 settembre, 316 - 47891 Dogana
Telephone/Fax: 0549 871111 / 871222
ABI (Italian Bankers’ Association) Code: 03034
SWIFT BIC: BASMSMSM

Corporate name:
Banca di San Marino s.p.a.
Head Office: Strada della Croce, 39 - 47896 Faetano
Telephone/Fax: 0549 873411 / 873401
ABI (Italian Bankers’ Association) Code: 08540
SWIFT BIC: MAOISMSM

Corporate name:
Banca Nazionale Sammarinese s.p.a.
Head Office: Piazza G. Bertoldi, 8 - 47899 Serravalle
Telephone/Fax: 0549 8740 / 874116
ABI (Italian Bankers’ Association) Code: 03530
SWIFT BIC: CRRNSMSM

Corporate name:
Banca Sammarinese di Investimento s.p.a.
Head Office: Via Monaldo da Falciano, 3 - 47891 Rovereta
Telephone/Fax: 0549 888801 / 888802
ABI (Italian Bankers’ Association) Code: 03287
SWIFT BIC: BSDISMSD

Corporate name:
Cassa di Risparmio della Repubblica di San Marino s.p.a.
Head Office: P.tta del Titano, 2 - 47890 San Marino
Telephone/Fax: 0549 872311 / 872700
ABI (Italian Bankers’ Association) Code: 06067
SWIFT BIC: CSSMSMSM

 */
public class BankDataGenerator_SM extends NumericBankAndBranchCode {

	private static final Logger LOG = Logger.getLogger(BankDataGenerator_SM.class.getName());

	static final String COUNTRY_CODE = "SM";

	BankDataGenerator_SM(String api_key) {
		super(COUNTRY_CODE, FORMAT_05d, FORMAT_05d, api_key);

		bankByCode = new Hashtable<Integer, ArrayList<Object>>();
		ArrayList<Object> emptyList = new ArrayList<Object>();
		bankByCode.put(3262, emptyList);
		bankByCode.put(3171, emptyList);
		bankByCode.put(3034, emptyList);
		bankByCode.put(3287, emptyList);
		bankByCode.put(3145, emptyList);
		bankByCode.put(3530, emptyList);
		bankByCode.put(3178, emptyList);
		bankByCode.put(6067, emptyList);
		bankByCode.put(3225, emptyList);
		bankByCode.put(8540, emptyList);
	}

	public static void main(String[] args) throws Exception {
		NumericBankAndBranchCode test = new BankDataGenerator_SM(API_Key_Provider.API_KEY);

		test.tryWith(9800, 9999);
	}

}
