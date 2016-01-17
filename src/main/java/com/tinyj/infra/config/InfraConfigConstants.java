package com.tinyj.infra.config;

/**
 * contains the constants needed for the ConfigurationManager.
 * all constants are defined with visibility "package" so that only the
 * classes in the same package could access them (thus, ConfigurationManager).
 * 
 * @author asaf.peeri
 *
 */
public class InfraConfigConstants 
{
		
	//the system property (VM parameter) that contains the name of the local configuration file
	public static final String LOCAL_CONFIGURATION_SYSTEM_PROPERTY = "com.tinyj.infra.config.local";
	
	//the system property (VM parameter) that indicates whether to show the api result in the log???
	public static final String LOCAL_SHOW_API_RESULT = "com.tinyj.infra.show.api.result";
	
	//the 'true' constant in configuration parameters (the check will be done ignoring the letter case
	public static final String CONFIGURATION_TRUE = "true";
	
	//the 'false' constant in configuration parameters (the check will be done ignoring the letter case
	public static final String CONFIGURATION_FALSE = "false";

	//true if to load remote configuration, false if not
	public static final String LOCAL_PARAM_CONFIG_LOAD_REMOTE = "CONFIG_LOAD_REMOTE";
	
	//true if to use data source to load remote configuration, false if to use jdbc
	public static final String LOCAL_PARAM_CONFIG_USE_DATA_SOURCE = "CONFIG_USE_DATA_SOURCE";
	
	//if the CONFIGURATION_USE_DATA_SOURCE is set to true, should contain the JNDI name of the data source  
	public static final String LOCAL_PARAM_CONFIG_DATA_SOURCE_JNDI_NAME = "CONFIG_DS_JNDI_NAME";
	
	//if the CONFIGURATION_USE_DATA_SOURCE is set to true, should contain the context factory of the use app server  
	//example: weblogic.jndi.WLInitialContextFactory
	public static final String LOCAL_PARAM_CONFIG_DATA_SOURCE_CONTEXT_FACTORY = "CONFIG_DS_CONTEXT_FACTORY";
	
	//if the CONFIGURATION_USE_DATA_SOURCE is set to true, should contain the URL to the app server's JNDI  
	//examples: t3://212.22.156.76:7001
	//			t3://localhost:6001
	public static final String LOCAL_PARAM_CONFIG_DATA_SOURCE_PROVIDER_URL = "CONFIG_DS_PROVIDER_URL";
	
	//if the CONFIGURATION_USE_DATA_SOURCE is set to false, the URL of the DB
	//examples: jdbc:oracle:thin:@212.22.156.77:1521:hikkup
	//			jdbc:oracle:thin:@db-test:1521:dog
	public static final String LOCAL_PARAM_CONFIG_JDBC_DATABASE_URL = "CONFIG_JDBC_DATABASE_URL";
	
	//if the CONFIGURATION_USE_DATA_SOURCE is set to false, the jdbc driver to use
	//example: oracle.jdbc.driver.OracleDriver
	public static final String LOCAL_PARAM_CONFIG_JDBC_DATABASE_DRIVER = "CONFIG_JDBC_DATABASE_DRIVER";
	
	//if the CONFIGURATION_USE_DATA_SOURCE is set to false, the db username
	public static final String LOCAL_PARAM_CONFIG_JDBC_DATABASE_USER = "CONFIG_JDBC_DATABASE_USER";
	
	//if the CONFIGURATION_USE_DATA_SOURCE is set to false, the db password
	public static final String LOCAL_PARAM_CONFIG_JDBC_DATABASE_PASS = "CONFIG_JDBC_DATABASE_PASS";

	//the name of the table to take the remote configuration from
	public static final String CONF_TABLE_NAME = "CONFIGURATION_PARAMS";
	
	//the name of the param name column in the remote configuration table
	public static final String CONF_TABLE_PARAM_NAME_COLUMN = "PARAM_NAME";
	
	//the name of the param value column in the remote configuration table	
	public static final String CONF_TABLE_PARAM_VALUE_COLUMN = "PARAM_VALUE";
	
	
	
	// ******** TempFileManager params
	public static final String TEMP_FILES_MANAGER_FOLDER_LOCATION= "FileManager.tempFolderLocation";
	
	
	
	//C'tor
	private InfraConfigConstants()
	{
		//do nothing. just to prevent from creating instances of this class
	}

}