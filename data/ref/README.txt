-- $Id: README.txt 46063 2016-03-31 18:10:42Z ehanussek.ext $
-- Changehistory:
-- 2015-03-17 EUG : created 
-- 2015-06-15 EUG : DE-Cities BUG beschrieben
 
Quelle: databene-benerator/src/main/resources/org/databene/domain

Es gibt Eigennamen (named entities) aus den Kategorien
 - PER-Person
 - LOC-Ort
 - ORG-Organisation
 - OTH-Andere
(zu Kategoriene siehe http://www.uni-hildesheim.de/konvens2014/data/konvens2014-workshop-proceedings.pdf)

Namen befinden sich in csv-files (","-Separator, ohne Header) und sind gewichtet. 
Die Bezugsgrösse für DE ist 81000000, in address/country.csv zu lesen: 
DE,de_DE,49,1[5-7][0-9],Germany,81000000,Bundesland,Kreis,Gemeinde

In familyName_DE.csv bedeutet
Müller,265025
dass die Häufigkeit des Namen "Müller" 265025/81000000 ist, also 0,327% (Frequency in percent)

DE-Cities (";"-Separator, mit Header) haben einen BUG
postalCode/PLZ in SN,Sachsen / ST,Sachsen-Anhalt / TH,Thüringen / BR,Brandenburg sind nur 4-stellig
und entsprechen daher nicht dem postalCodeFormat.properties. Bsp. Leipzig 4103 ==> 04103 wäre korrekt

city/state/street
In 1.7.0.0.230 http://redstgt.libelle.com:8888/redmine/projects/ldm/repository/revisions/42467 wurden 
databene city/state/street-files gelöscht und teilweise durch OSM-files ersetzt  