package com.klst.merge

import groovy.json.JsonSlurper
import groovy.lang.Binding
import groovy.lang.Script
import groovy.sql.Sql

import java.sql.SQLException

import com.klst.iban.datastore.DatabaseProxy
import com.klst.iban.datastore.SqlInstance

class Bankdata extends Script {

	static final URL_PREFIX = 'https://raw.githubusercontent.com/homebeaver/bankdata/main/iban-countries/'
	//static final DATA_FILE = z.B: 'AD.json'
	static final TABLENAME = "bankdata"
	// ADempiere:
	static final SUPER_USER_ID = 100
	static final SYSTEM_CLIENT_ID = 0
	
	def CLASSNAME = this.getClass().getName()
	Sql sqlInstance
	
	public Bankdata() {
		println "${CLASSNAME}:ctor"
	}

	public Bankdata(Binding binding) {
		super(binding);
		println "${CLASSNAME}:ctor binding"
		def db = [url:DatabaseProxy.H2_DATASTORE, user:'SA', password:'', driver:SqlInstance.H2_DRIVER]
		try {
			sqlInstance = Sql.newInstance(db.url, db.user, db.password, db.driver)
		} catch (Exception e) {
			println "${CLASSNAME}:ctor ${e} Datenbank ${db.url} nicht erreichbar."
			//throw e
		}
	}

	// @see https://www.postgresql.org/docs/10/errcodes-appendix.html
	static final SUCCESSFUL_COMPLETION = "00000"
	static final UNDEFINED_TABLE = "42P01" // PG
	static final DUPLICATE_TABLE = "42P07"
	static final TABLE_NOT_FOUND = "42S02" // H2
	def lastSQLState = SUCCESSFUL_COMPLETION
	
	// Returns:true if the first result is a ResultSet object; or an update count
	def doSql = { sql , param=[] ->
		def current = sqlInstance.connection.autoCommit = false
		def res
		try {
			def isResultSet = sqlInstance.execute(sql,param)
			if(isResultSet) {
				println "${CLASSNAME}:doSql isQuery : ${sql}"
				res = isResultSet // true
			} else {
				res = sqlInstance.getUpdateCount()
//				println "${CLASSNAME}:doSql updates = ${res} : ${sql} param =  ${param}" // log.fine
				sqlInstance.commit();
			}
			lastSQLState = SUCCESSFUL_COMPLETION
		} catch(SQLException ex) {
			println "${CLASSNAME}:doSql ${ex} ErrorCode:${ex.getErrorCode()} SQLState:${ex.getSQLState()}"
			lastSQLState = ex.getSQLState()
			sqlInstance.rollback()
			println "${CLASSNAME}:doSql Transaction rollback."
		}
		sqlInstance.connection.autoCommit = current
		return res                  
	}

	def createTable = { tableName=TABLENAME ->
		def sql = """
CREATE TABLE ${tableName}
(
	country_code  character(2) NOT NULL,                -- PK, mit FK in Tabelle country_code
	id            numeric(10,0) NOT NULL,               -- PK
	swift_code    VARCHAR( 11),
	bank_id       VARCHAR( 10) NOT NULL,
--	bank_code INTEGER,
	branch_code   VARCHAR(10),
	bank          VARCHAR(120) NOT NULL,
	branch        VARCHAR(120),
	support_codes TINYINT,
-- location:
	state         VARCHAR(120),
	zip           VARCHAR(120),
	city          VARCHAR(120),
	address       VARCHAR(120),
-- contact:
	PHONE         VARCHAR(120),
	FAX           VARCHAR(120),
	WWW           VARCHAR(120),
	EMAIL         VARCHAR(120),
  CONSTRAINT ${tableName}_pkey PRIMARY KEY (country_code, id),
  CONSTRAINT ${tableName}_FK FOREIGN KEY (country_code) 
    REFERENCES country_code( alpha2 ) ON DELETE RESTRICT ON UPDATE RESTRICT  
);
"""
		def res = doSql(sql)
		println "${CLASSNAME}:createTable res=${res}" // 0 or catched exception
		if(res==null) {
			throw new SQLException("CREATE TABLE ${tableName}", lastSQLState)
		}
	}
	
	/*

Spalten des json objects

bank_code müsste demnach in zwei Spalten gemapped werden:
 - in bank_id varchar(10) immer
 - und in bank_code, nur wenn es numerisch ist (das lasse ich weg)

	 */
	def cols = [country_code: ["country_code"]
		, id                : ["id"]
		, swift_code        : ["swift_code"]
		, bank_code         : ["bank_id"] // numeric ==> int bank_code, oder String, z.B. BG ==> bank_id varchar(10)
		, branch_code       : ["branch_code"] // ==> branch_code varchar(10) NULL
		, bank              : ["bank"] // name
		, branch            : ["branch"] // name
		, state             : ["state"]
		, zip               : ["zip"]
		, city              : ["city"]
		, address           : ["address"]
//		, phone             : ["phone"]
		// ... TODO
		, support_codes     : ["support_codes"]
		]
//	Closure<String> colkeys = { -> cols.keySet().collect { it }.join(', ') }
		// name colkeys irreführend, da es das erste element von values ist
	Closure<String> colkeys = { -> cols.values().collect { it.get(0) }.join(', ') }
	def data = [:]
	def processLine = { line , tableName=TABLENAME ->
		
	}

	Integer countryLoad(String jsonString, tableName=TABLENAME) {
		def jsonSlurper = new JsonSlurper()
		object = jsonSlurper.parseText(jsonString)
		assert object instanceof Map
		// "country_code": "AD",
		// "ISO_3166_1": { "name": "Andorra" 
		// "list": [ ...
		println "${CLASSNAME}:countryLoad country_code: '${object.country_code}', no of objects:${object.list.size()}"
		println "${CLASSNAME}:countryLoad colkeys:${colkeys}"
		List oList = object.list
		oList.each { bank ->
			println "${CLASSNAME}:countryLoad ${bank}"
			data = [:]
			cols.each {
				def name = it.key // name des json objekts
				def dbcol = it.value.get(0) // name der db spalte zum json objekt
				if(name=='country_code') {
					data.put(name, "'"+object.country_code+"'") // immer '<country_code>'
				} else if(bank.get(name).is(null)) {
					data.put(name, null)
				} else if(name=='id' || name=='support_codes') {
					data.put(name, bank.get(name))
				} else if(name=='bank_code') { // kann numerisch sein, wird nach varchar abgebildet 
					def bank_code = bank.get(name)
					//println "${CLASSNAME}:countryLoad bank_code.class:${bank_code.class}"
					data.put(name, bank_code ? "'"+bank_code+"'" : null)
//					if(bank_code instanceof Integer) { // jsonSlurper liefert Integer, org.json.simple.parser.JSONParser dagegen Long
//						data.put(name, bank_code)
//					}
				} else {
//					def str = bank.get(name).replace("'","''"); // wg 'Côte d'Ivoire'
					def str = bank.get(name)
//					println "${CLASSNAME}:countryLoad str:${str}"
					data.put(name, (str.length()==0 ? null : "'"+str.replace("'","''")+"'"))
				}
			}
			Closure<String> values = { -> data.values().collect { it }.join(', ') }
			def sql = """
INSERT INTO ${tableName} 
( ${colkeys} )
VALUES ( ${values} )
"""
			sqlInstance.execute(sql,[])
			def inserts = sqlInstance.getUpdateCount()
//			def inserts = 0
			println "${CLASSNAME}:countryLoad inserts=${inserts} : ${sql}"
		}
		return object.list.size()
	}
	
	def jsonString = new StringBuilder()
	// see https://stackoverflow.com/questions/11863474/how-to-read-text-file-from-remote-system-and-then-write-it-into-array-of-string
	def	populate = { filename , charsetName="UTF-8" ->
		println "${CLASSNAME}:populate from ${filename}"
		File file = new File(filename)
		BufferedReader reader = null
		def done = 0
		if(file.exists()) {
			println "${CLASSNAME}:populate canRead: ${file.canRead()} file: ${file.getAbsolutePath()}"
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetName))
		} else {
			println "${CLASSNAME}:populate canRead: File does not exist ${file.getAbsolutePath()} - try url"
			URL url = new URL(URL_PREFIX +filename)
			reader = new BufferedReader(new InputStreamReader(url.openStream())) // , "UTF-8"
		}
		reader.eachLine { line ->
			this.jsonString.append(line)
			done = done+1
		}
		countryLoad(this.jsonString.toString())
		println "${CLASSNAME}:populate done ${done} lines"
	}

	def	deletefrom = { String countryCode, tablename=TABLENAME ->
		def sql = """
DELETE FROM ${tablename} WHERE country_code = '${countryCode}'
"""
		def res = doSql(sql)			
		println "${CLASSNAME}:deletefrom ${res}" // 0 or catched exception
		if(res==null) {
			throw new SQLException("${sql}", lastSQLState)
		}
	}
	
	void initialLoad(String countryCode) {
		sqlInstance.connection.autoCommit = false
		populate( countryCode+".json" )
		sqlInstance.commit()
	}
	
	void deleteAndLoad(String countryCode) {
		deletefrom(countryCode)
		
		def datafile = countryCode+".json"
		sqlInstance.connection.autoCommit = false
		populate( datafile )
		sqlInstance.commit()
	}
	
	void loadOrInitialLoad(createTableAndInitialLoad=false) {
		
		def countryCode = "DK"
		try {
			deleteAndLoad(countryCode)
		} catch(SQLException ex) {
			println "${CLASSNAME}:loadOrInitialLoad SQLState ${ex.getSQLState()}"
			if(ex.getSQLState()==UNDEFINED_TABLE || ex.getSQLState()==TABLE_NOT_FOUND) {
				println "${CLASSNAME}:loadOrInitialLoad table ${TABLENAME} does not exist - try to create ..."
				createTableAndInitialLoad = true
			}
		}
		if(createTableAndInitialLoad) try {
			createTable()
			initialLoad(countryCode)
		} catch(SQLException ex) {
			if(ex.getSQLState()==DUPLICATE_TABLE) {
				println "${CLASSNAME}:loadOrInitialLoad table ${TABLENAME} exists"
			} else {
				throw ex
			}
		}
		
	}
	@Override
	public Object run() {  // nur Test
		println "${CLASSNAME}:run"
		println "${CLASSNAME}:run sqlInstance:${this.sqlInstance}"
		if(this.sqlInstance) {
			println "${CLASSNAME}:run Connection:${this.sqlInstance.getConnection()}"
			loadOrInitialLoad()
		}
		
		return this;
	}

  // wird in eclipse benötigt, damit ein "Run As Groovy Script" möglich ist (ohne Inhalt)
  // nach dem Instanzieren wird run() ausgeführt
  static main(args) {
  }

}
