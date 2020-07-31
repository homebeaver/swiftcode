--Satzaufbau der Bankleitzahlendatei 
--Quelle https://www.bundesbank.de/Redaktion/DE/Standardartikel/Aufgaben/Unbarer_Zahlungsverkehr/bankleitzahlen_download.html 
--
--Feld- Inhalt                                                                      Anzahl der Stellen
--Nr.                                                                                  Nummerierung der Stellen
-- 1 Bankleitzahl                                                                    8   1 - 8
-- 2 Merkmal, ob bankleitzahlführender Zahlungsdienstleister („1“) oder nicht („2“)  1   9
-- 3 Bezeichnung des Zahlungsdienstleisters (ohne Rechtsform)                       58  10 - 67
-- 4 Postleitzahl                                                                    5  68 - 72
-- 5 Ort                                                                            35  73 - 107
-- 6 Kurzbezeichnung des Zahlungsdienstleisters mit Ort (ohne Rechtsform)           27 108 - 134
-- 7 Institutsnummer für PAN                                                         5 135 - 139
-- 8 Business Identifier Code – BIC                                                 11 140 - 150
-- 9 Kennzeichen für Prüfzifferberechnungsmethode                                    2 151 - 152
--10 Nummer des Datensatzes                                                          6 153 - 158
--11 Änderungskennzeichen                                                            1 159
--   „A“ (Addition) für neue, 
--   „D“ (Deletion) für gelöschte,
--   „U“(Unchanged) für unveränderte und 
--   „M“ (Modified) für veränderte Datensätze
--12 Hinweis auf eine beabsichtigteBankleitzahllöschung                              1 160
--   "0", sofern keine Angabe
--   "1", sofern BLZ im Feld 1 zur Löschung vorgesehen ist
--13 Hinweis auf Nachfolge-Bankleitzahl                                              8 161 - 168
--14 Kennzeichen für die IBAN-Regel (nur erweiterte Bankleitzahlendatei)             6 169 - 174
--
--key: 10/Nummer des Datensatzes

-- DROP TABLE ext_bankleitzahlen;

CREATE TABLE ext_bankleitzahlen
(
  ext_bankleitzahlen_id numeric(10,0) NOT NULL,        -- fld 10 Konflikt mit c_bank_id 100 und 50000 GW
  routingno character varying(20) NOT NULL,            -- fld  1 BLZ , len  8
  merkmal character(1) NOT NULL DEFAULT '1'::bpchar,   -- fld  2
  name character varying(100) NOT NULL,                -- fld  3     , len 58
  postal character varying(10),                        -- fld  4     , len  5
  city character varying(60),                          -- fld  5     , len 35
                                                       -- fld  6     , len 27 (omit)
  pan character varying(5),                            -- fld  7
  swiftcode character varying(20),                     -- fld  8              , https://de.wikipedia.org/wiki/ISO_9362 ==bic , len 8 oder 11 (3optional
  checkdigitmethod character(2),                       -- fld  9     , len  2 , https://de.wikipedia.org/wiki/Pr%C3%BCfziffer
  chg character(1) NOT NULL DEFAULT 'U'::bpchar,       -- fld 11
  del character(1) NOT NULL DEFAULT '0'::bpchar,       -- fld 12
  newroutingno character varying(8),                   -- fld 13 BLZ , len  8
  ibanrule character varying(6),                       -- fld 14
  
  CONSTRAINT ext_bankleitzahlen_pkey PRIMARY KEY (ext_bankleitzahlen_id),
  CONSTRAINT ext_bankleitzahlen_merkmal_check CHECK (merkmal = ANY (ARRAY['1'::bpchar, '2'::bpchar])),
  CONSTRAINT ext_bankleitzahlen_chg_check CHECK (chg = ANY (ARRAY['A'::bpchar, 'D'::bpchar, 'U'::bpchar, 'M'::bpchar])),
  CONSTRAINT ext_bankleitzahlen_del_check CHECK (del = ANY (ARRAY['0'::bpchar, '1'::bpchar]))
);

