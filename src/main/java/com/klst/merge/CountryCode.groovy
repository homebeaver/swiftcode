package com.klst.merge

import groovy.json.JsonSlurper
import groovy.lang.Binding
import groovy.lang.Script
import groovy.sql.Sql

import java.sql.SQLException

import com.klst.iban.datastore.SqlInstance

class CountryCode extends Script {

	static final URL_PREFIX = 'https://raw.githubusercontent.com/homebeaver/swiftcode/master%2Bdev/data/ref/address/'
	static final DATA_FILE = 'ISO-country.json'
	static final TABLENAME = "country_code"
	// ADempiere:
	static final SUPER_USER_ID = 100
	static final SYSTEM_CLIENT_ID = 0
	
	def CLASSNAME = this.getClass().getName()
	Sql sqlInstance
	
	public CountryCode() {
		println "${CLASSNAME}:ctor"
	}

	public CountryCode(Binding binding) {
		super(binding);
		println "${CLASSNAME}:ctor binding"
		def db = [url:'jdbc:h2:~/data/H2/bankdata', user:'SA', password:'', driver:SqlInstance.H2_DRIVER]
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
  alpha2 character(2) NOT NULL,
  alpha3 character(3),                                 -- kann null sein, z.B. für XK
  numeric numeric(3,0),
  name character varying(100) NOT NULL,
  
  CONSTRAINT ${tableName}_pkey PRIMARY KEY (alpha2)
);
"""
		def res = doSql(sql)
		println "${CLASSNAME}:createTable res=${res}" // 0 or catched exception
		if(res==null) {
			throw new SQLException("CREATE TABLE ${tableName}", lastSQLState)
		}
	}
	
	def cols = [alpha2: ["utf"]
		, alpha3      : ["utf"]
		, numeric     : ["int"]
		, name        : ["utf"]
		]
	Closure<String> colkeys = { -> cols.keySet().collect { it }.join(', ') }
	def data = [:]
	def processLine = { line , tableName=TABLENAME ->
		
	}

	Integer countryLoad(String jsonString, tableName=TABLENAME) {
		def jsonSlurper = new JsonSlurper()
		object = jsonSlurper.parseText(jsonString)
		assert object instanceof Map
		// "data": "ISO 3166-1 country",
		println "${CLASSNAME}:countryLoad country_code: '${object.data}', no of objects:${object.list.size()}"
		List isoList = object.list
		isoList.each { country ->
			println "${CLASSNAME}:countryLoad ${country}"
//			String alpha2 = country.get("alpha2")
//			String alpha3 = country.get("alpha3")
//			String name = country.get("name")
//			def id = country.get("numeric")
			data = [:]
			cols.each {
				def name = it.key
				if(it.value.get(0)=='int') {
					data.put(name, country.get(name))
				} else {
					def str = country.get(name).replace("'","''"); // wg 'Côte d'Ivoire'
					data.put(name, (str.length()==0 ? null : "'"+str+"'"))
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
			// inserts == 1
			println "${CLASSNAME}:countryLoad inserts=${inserts} : ${data}"
		}
		return object.list.size()
	}
	
	def jsonString = new StringBuilder()
	// see https://stackoverflow.com/questions/11863474/how-to-read-text-file-from-remote-system-and-then-write-it-into-array-of-string
	def	populate = { filename , charsetName="Cp1252" ->
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

	def	deletefrom = { tablename=TABLENAME ->
		def sql = """
DELETE FROM ${tablename} 
"""
		def res = doSql(sql)			
		println "${CLASSNAME}:deletefrom ${res}" // 0 or catched exception
		if(res==null) {
			throw new SQLException("${sql}", lastSQLState)
		}
	}
	
	void initialLoad(String filepathname) {
		sqlInstance.connection.autoCommit = false
		populate( filepathname )
		sqlInstance.commit()
	}
	
	void deleteAndLoad(String blzfilepathname) {
		deletefrom()
		
		sqlInstance.connection.autoCommit = false
		populate( blzfilepathname )
		sqlInstance.commit()
	}
	
	void loadOrInitialLoad(createTableAndInitialLoad=false) {
		
		try {
			deleteAndLoad(DATA_FILE)
		} catch(SQLException ex) {
			println "${CLASSNAME}:loadOrInitialLoad SQLState ${ex.getSQLState()}"
			if(ex.getSQLState()==UNDEFINED_TABLE || ex.getSQLState()==TABLE_NOT_FOUND) {
				println "${CLASSNAME}:loadOrInitialLoad table ${TABLENAME} does not exist - try to create ..."
				createTableAndInitialLoad = true
			}
		}
		if(createTableAndInitialLoad) try {
			createTable()
			initialLoad(DATA_FILE)
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
