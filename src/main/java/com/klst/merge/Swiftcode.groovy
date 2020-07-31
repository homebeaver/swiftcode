package com.klst.merge

import groovy.json.JsonSlurper;
import groovy.lang.Binding;
import groovy.lang.Script;
import groovy.sql.Sql;
import java.sql.SQLException

class Swiftcode extends Script {

	def CLASSNAME = this.getClass().getName()
	def DEFAULT_FROM_SCHEMA = "adempiere"
	def DEFAULT_TO_SCHEMA = "adempiere"
	def SUPER_USER_ID = 100
	def SYSTEM_CLIENT_ID = 0
	def GERDENWORD_CLIENT_ID = 11
	def DEFAULT_CLIENT_ID = 1000000
	Sql sqlInstance
	
	public Swiftcode() {
		println "${CLASSNAME}:ctor"
	}

	public Swiftcode(Binding binding) {
		super(binding);
		println "${CLASSNAME}:ctor binding"
		// ACHTUNG : nonstd port
		def db = [url:'jdbc:postgresql://localhost:5433/ad393', user:'adempiere', password:'adempiereIstEsNicht', driver:'org.postgresql.Driver']
		try {
			sqlInstance = Sql.newInstance(db.url, db.user, db.password, db.driver)
		} catch (Exception e) {
			println "${CLASSNAME}:ctor ${e} Datenbank ${db.url} nicht erreichbar."
			//throw e
		}
	}

	// Returns:true if the first result is a ResultSet object; or an update count
	def doSql = { sql , param=[] ->
		def current = null
		try {
			current = sqlInstance.connection.autoCommit = false	
		} catch(Exception ex) {
			return false
		}
		def res
		try {
			def isResultSet = sqlInstance.execute(sql,param)
			if(isResultSet) {
//				println "${CLASSNAME}:doSql isQuery : ${sql}"
				res = isResultSet // true
			} else {
				res = sqlInstance.getUpdateCount()
				println "${CLASSNAME}:doSql updates = ${res} : ${sql} param =  ${param}"
				sqlInstance.commit();
			}
		} catch(SQLException ex) {
			println "${CLASSNAME}:doSql ${ex}"
			sqlInstance.rollback()
			println "${CLASSNAME}:doSql Transaction rollback."
		}
		sqlInstance.connection.autoCommit = current
		return res                  
	}

	def TABLENAME = "c_bank"
	
	void checkSwiftCodes(List swiftRecord, tablename=TABLENAME) {
		def sql = """
SELECT * FROM ${tablename} 
WHERE swiftcode = ?
"""
		swiftRecord.each() { row ->
			// The Swift code consists of 8 or 11 characters
			// 0..3 : Institution/Bank Code "ZLAB"
			// 4..5 : Country Code "DE"
			// 6..7 : Location Code "B1"  second char has a value of "1": This means that this is a passive participant in the SWIFT network.
			// Also, the second character (8th in the BIC code) sometimes carries this information:
			//       if the second character is "0", then it is typically a test BIC as opposed to a BIC used on the live network.
			//       if the second character is "1", then it denotes a passive participant in the SWIFT network
			//       if the second character is "2", then it typically indicates a reverse billing BIC, where the recipient pays for the message as opposed to the more usual mode whereby the sender pays for the message.
			// Last 3 characters - branch code, optional ('XXX' for primary office) (letters and digits)
			def isAktiv = true 
			if(row.swift_code.substring(7,8)=="1") { // Bsp.ZLABDEB1
				isAktiv = false
			}
			def resultSet = doSql(sql,[row.swift_code])
			if(resultSet) {
				def first = sqlInstance.firstRow(sql,[row.swift_code])
				if(first==null && isAktiv) {
					println "${CLASSNAME}:checkSwiftCodes: new ${row}"
					// TODO insert into ${tablename} - aber unter welcher id, denn row
				} else {
					println "${CLASSNAME}:checkSwiftCodes: ${row.swift_code} is NOT AktivSWIFTparticipant : ${row}"
				}
			} else {
				println "${CLASSNAME}:checkSwiftCodes: ??? ${row}"		
			}
		}
	}
	
	void jsonLoad(String jsonString) {
		def jsonSlurper = new JsonSlurper()
		
		println "${CLASSNAME}:jsonLoad"
		object = jsonSlurper.parseText(jsonString)
		assert object instanceof Map
		println "${CLASSNAME}:jsonLoad country_code: ${object.country_code} \n${object.list}"
		checkSwiftCodes(object.list)
	}

	def jsonString = new StringBuilder()
	// see https://stackoverflow.com/questions/11863474/how-to-read-text-file-from-remote-system-and-then-write-it-into-array-of-string
	def	readFromRemote = { filename , charsetName="Cp1252" ->
		println "${CLASSNAME}:readFromRemote ${filename}"
		File file = new File(filename)
		BufferedReader reader = null
		def done = 0
		this.jsonString = new StringBuilder()
		if(file.exists()) {
			println "${CLASSNAME}:readFromRemote canRead: ${file.canRead()} file: ${file.getAbsolutePath()}"
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetName))
		} else {
			println "${CLASSNAME}:readFromRemote canRead: File does not exist ${file.getAbsolutePath()} - try url"
			URL url = new URL(filename)
			reader = new BufferedReader(new InputStreamReader(url.openStream()))
		}
		reader.eachLine { line ->
			this.jsonString.append(line)
			done = done+1
		}
		println "${CLASSNAME}:readFromRemote done ${done} lines"
		jsonLoad(this.jsonString.toString())
	}

	@Override
	public Object run() {  // nur Test
		println "${CLASSNAME}:run"
		println "${CLASSNAME}:run ${this.sqlInstance}"

		def urlprefix = 'https://raw.githubusercontent.com/homebeaver/swiftcode/master/AllCountries/'		
		// TODO Laden in c_bank, c_bank_id ermitteln/darf nicht mit Ext_bankleitzahlen kollidieren
//		readFromRemote(urlprefix + 'DE.json')
//		readFromRemote(urlprefix + 'AD.json') // Andorra
		readFromRemote(urlprefix + 'IS.json') // Iceland
		
		return this;
	}

  // wird in eclipse benötigt, damit ein "Run As Groovy Script" möglich ist (ohne Inhalt)
  // nach dem Instanzieren wird run() ausgeführt
  static main(args) {
  }

}
