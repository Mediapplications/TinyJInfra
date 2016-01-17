package com.tinyj.infra.config;


import static com.tinyj.infra.config.InfraConfigConstants.CONFIGURATION_FALSE;
import static com.tinyj.infra.config.InfraConfigConstants.CONFIGURATION_TRUE;
import static com.tinyj.infra.config.InfraConfigConstants.CONF_TABLE_NAME;
import static com.tinyj.infra.config.InfraConfigConstants.CONF_TABLE_PARAM_NAME_COLUMN;
import static com.tinyj.infra.config.InfraConfigConstants.CONF_TABLE_PARAM_VALUE_COLUMN;
import static com.tinyj.infra.config.InfraConfigConstants.LOCAL_CONFIGURATION_SYSTEM_PROPERTY;
import static com.tinyj.infra.config.InfraConfigConstants.LOCAL_PARAM_CONFIG_DATA_SOURCE_CONTEXT_FACTORY;
import static com.tinyj.infra.config.InfraConfigConstants.LOCAL_PARAM_CONFIG_DATA_SOURCE_JNDI_NAME;
import static com.tinyj.infra.config.InfraConfigConstants.LOCAL_PARAM_CONFIG_DATA_SOURCE_PROVIDER_URL;
import static com.tinyj.infra.config.InfraConfigConstants.LOCAL_PARAM_CONFIG_JDBC_DATABASE_DRIVER;
import static com.tinyj.infra.config.InfraConfigConstants.LOCAL_PARAM_CONFIG_JDBC_DATABASE_PASS;
import static com.tinyj.infra.config.InfraConfigConstants.LOCAL_PARAM_CONFIG_JDBC_DATABASE_URL;
import static com.tinyj.infra.config.InfraConfigConstants.LOCAL_PARAM_CONFIG_JDBC_DATABASE_USER;
import static com.tinyj.infra.config.InfraConfigConstants.LOCAL_PARAM_CONFIG_LOAD_REMOTE;
import static com.tinyj.infra.config.InfraConfigConstants.LOCAL_PARAM_CONFIG_USE_DATA_SOURCE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.tinyj.infra.db.DBConnectionManager;
import com.tinyj.infra.exception.ConfigurationException;
import com.tinyj.infra.exception.DBConnectionException;
import com.tinyj.infra.log.BaseLoggerWrapper;
import com.tinyj.infra.log.Log4jWrapper;
import com.tinyj.infra.properties.PropertiesUtils;

public class ConfigurationManager 
{
	
	//private static final Logger sLogger = Logger.getLogger(ConfigurationManager.class);
	private static final BaseLoggerWrapper sLogger = Log4jWrapper.getInstance().getLogger(ConfigurationManager.class);
	
	protected static String sConfigurationFilesNamesConcatenated;
	protected static String[] sConfigurationFilesNames;
	protected static Properties sLocalParameters = new Properties();
	protected static Properties sRemoteParameters = new Properties();
	
	protected static boolean sUseDataSource;
	protected static String sDataSourceJNDIName;
	protected static String sJNDIContextFactory;
	protected static String sJNDIProviderURL;
	protected static String sJDBCUrl;
	protected static String sJDBCDriverName;
	protected static String sJDBCUsername;
	protected static String sJDBCPassword;
	
	protected static DBConnectionManager sConManager;
	
	protected static ReentrantReadWriteLock sLock;
	
	
	
	//init the configuration manager upon static initialization
	static
	{
		try
		{
			sLock = new ReentrantReadWriteLock();
			initConfiguration();
		}
		catch(Throwable t)
		{
			System.out.println("cannot load configuration manager: " + t.toString());
			t.printStackTrace(System.out);
			
			System.err.println("cannot load configuration manager: " + t.toString());
			t.printStackTrace(System.err);
		}
	}
	
	
	private ConfigurationManager()
	{
		//empty private constructor. just to prevent from creating instances of this class
	}
	
	
	/**
	 * the application's startup class should call this method in order to instantiate its
	 * static occurence for the first time.
	 */
	public static void init()
	{
		//do nothing. this method should be called in order to create the first static occurence of this class.
	}
	
	
	public static Set<Entry<Object, Object>> getParamsWithPrefix(String aPrefix)
	{
		if (aPrefix == null)
		{
			return null;
		}

		Set<Entry<Object, Object>> localEntries = null;
		Set<Entry<Object, Object>> remoteEntries = null;
		
		if (sLocalParameters != null)
		{
			localEntries = sLocalParameters.entrySet();
		}
		
		if (sRemoteParameters != null)
		{
			remoteEntries = sRemoteParameters.entrySet();
		}
		
		
		Set<Entry<Object,Object>> result = new HashSet<Entry<Object,Object>>();
		handleEntries(aPrefix,localEntries,result);
		handleEntries(aPrefix,remoteEntries,result);
				
		
		return result;
		
	}
	
	
	
	
	private static void handleEntries(String aPrefix, Set<Entry<Object, Object>> aEntries, Set<Entry<Object, Object>> aResult)
	{
		if (aEntries == null || aPrefix == null)
		{
			return;
		}
		if (aPrefix.equals("*"))
		{
			aResult.addAll(aEntries);
			return;
		}
		
		for (Entry<Object,Object> entry:aEntries)
		{
			String name = (String)entry.getKey();
			if (name.indexOf(aPrefix) == 0)
			{
				aResult.add(entry);
			}
		}
		
		 
	}


	/**
	 * gets a parameter from the Configuration, whether it is local parameter or remote parameter
	 * 
	 * @param aParamName the parameter name to get
	 * 
	 * @return the parameter value
	 */
	public static String getParam(String aParamName)
	{
		try
		{
			sLock.readLock().lock();
			//there is no support for null parameter name
			if (aParamName == null)
			{
				return null;
			}
			
			//first look the param in the local props, if not found - look in the remote props
			String paramValue = sLocalParameters.getProperty(aParamName.trim());
			if (paramValue == null)
			{
				//the parameter is not found in the local props. look in the remote props
				paramValue = sRemoteParameters.getProperty(aParamName);
				if (paramValue == null)
				{
					//the parameter is not found in the local and remote props
					return null;
				}
			}

			//return what was found
			return paramValue.trim();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
		finally
		{
			sLock.readLock().unlock();
		}
	}

	
	/**
	 * Gets the parameter according to its name and converts the value to int
	 * @param aParamName name of parameter
	 * @return value as int
	 */
	public static int getParamAsInt(String aParamName)
	{
		String paramValStr = getParam(aParamName);
		return Integer.parseInt(paramValStr);
	}
	
	
	public static int getParamAsInt(String aParamName, int aDefaultValue)
	{
		if (!isPresent(aParamName))
		{
			return aDefaultValue;
		}
		
		String paramValStr = getParam(aParamName);
		return Integer.parseInt(paramValStr);
	}
	
	
	public static boolean isPresent(String aParamName)
	{
		try
		{
			sLock.readLock().lock();

			String paramValStr = getParam(aParamName);
			return paramValStr != null;
		}
		catch (Exception ex)
		{
			return false;
		}
		finally
		{
			sLock.readLock().unlock();
		}
		
		
	}
	/**
	 * Gets the parameter according to its name and converts the value to long
	 * @param aParamName name of parameter
	 * @return value as long
	 */
	public static long getParamAsLong(String aParamName)
	{
		String paramValStr = getParam(aParamName);
		return Long.parseLong(paramValStr);
	}
	
	public static long getParamAsLong(String aParamName, long aDefault)
	{
		if (!isPresent(aParamName))
		{
			return aDefault;
		}
		String paramValStr = getParam(aParamName);
		return Long.parseLong(paramValStr);
	}
	
	
	
	/**
	 * Gets the parameter according to its name and converts the value to byte
	 * @param aParamName name of parameter
	 * @return value as byte
	 */
	public static byte getParamAsByte(String aParamName)
	{
		String paramValStr = getParam(aParamName);
		return Byte.parseByte(paramValStr);
	}
	
	
	public static byte getParamAsByte(String aParamName, byte aDefault)
	{
		if (!isPresent(aParamName))
		{
			return aDefault;
		}
		String paramValStr = getParam(aParamName);
		return Byte.parseByte(paramValStr);
	}
	
	/**
	 * Gets the parameter according to its name and converts the value to short
	 * @param aParamName name of parameter
	 * @return value as short
	 */
	public static short getParamAsShort(String aParamName)
	{
		String paramValStr = getParam(aParamName);
		return Short.parseShort(paramValStr);
	}
	
	
	public static short getParamAsShort(String aParamName, short aDefault)
	{
		if (!isPresent(aParamName))
		{
			return aDefault;
		}
		String paramValStr = getParam(aParamName);
		return Short.parseShort(paramValStr);
	}
	
	/**
	 * Gets the parameter according to its name and converts the value to float
	 * @param aParamName name of parameter
	 * @return value as float
	 */
	public static float getParamAsFloat(String aParamName)
	{
		String paramValStr = getParam(aParamName);
		return Float.parseFloat(paramValStr);
	}
	
	
	public static float getParamAsFloat(String aParamName, float aDefault)
	{
		if (!isPresent(aParamName))
		{
			return aDefault; 
		}
		String paramValStr = getParam(aParamName);
		return Float.parseFloat(paramValStr);
	}
	
	
	
	/**
	 * Gets the parameter according to its name and converts the value to double
	 * @param aParamName name of parameter
	 * @return value as double
	 */
	public static double getParamAsDouble(String aParamName)
	{
		String paramValStr = getParam(aParamName);
		return Double.parseDouble(paramValStr);
	}
	
	
	public static double getParamAsDouble(String aParamName, double aDefault)
	{
		if (!isPresent(aParamName))
		{
			return aDefault;
		}
		String paramValStr = getParam(aParamName);
		return Double.parseDouble(paramValStr);
	}
	
	
	
	/**
	 * Gets the parameter according to its name and converts the value to boolean
	 * @param aParamName name of parameter
	 * @return value as boolean
	 */
	public static boolean getParamAsBoolean(String aParamName)
	{
		String paramValStr = getParam(aParamName);
		return Boolean.parseBoolean(paramValStr);
	}

	
	public static boolean getParamAsBoolean(String aParamName, boolean aDefault)
	{
		if (!isPresent(aParamName))
		{
			return aDefault;
		}
		String paramValStr = getParam(aParamName);
		return Boolean.parseBoolean(paramValStr);
	}
	
	/**
	 * save a remote parameter
	 * @param aParamName - paramter name tu update
	 * @param aParamValue - new parameter value.
	 */
	public static void saveParam(String aParamName,String aParamValue)
	{
		saveParameterToDB(aParamName,aParamValue);
	}
	
	
	
	/**
	 * returns the properties table that is holding the local parameters
	 * 
	 * @return the properties object holding the local parameters
	 */
	protected Properties getLocalParameters()
	{
		return sLocalParameters;
	}
	
	
	
	/**
	 * returns the properties table that is holding the remote parameters
	 * 
	 * @return the properties object holding the remote parameters
	 */
	protected Properties getRemoteParameters()
	{
		return sRemoteParameters;
	}
	
	
	/**
	 * returns an Iterator on on all the keys of the local configuration parameters
	 * 
	 * @return an Iterator on on all the keys of the local configuration parameters
	 */
	public static Iterator<Object> localKeys()
	{
		//sLocalParameters.
		if (sLocalParameters != null)
		{
			return sLocalParameters.keySet().iterator();
		}
		else
		{
			return null;
		}
	}
	
	
	/**
	 * returns an Iterator on on all the keys of the remote configuration parameters
	 * 
	 * @return returns an Iterator on on all the keys of the remote configuration parameters
	 */
	public static Iterator<Object> remoteKeys()
	{
		if (sRemoteParameters != null)
		{
			return sRemoteParameters.keySet().iterator();
		}
		else
		{
			return null;
		}
	}
	
	
	
	/**
	 * this method can be used in order to reload all the configuration manager from scratch 
	 */
	public static void reload()
	{
		initConfiguration();
	}
	
	
	
	/**
	 * this method can be used in order to reload only the local parameters of the configuration manager 
	 */
	public static void reloadLocal()
	{
		initLocalConfiguration();
	}
	
	
	
	/**
	 * this method can be used in order to reload only the remote parameters of the configuration manager 
	 */
	public static void reloadRemote()
	{
		initRemoteConfiguration();
	}
	
	
	
	/**
	 * this method initialize the local properties configuration file and the
	 * remote DB configuration file
	 * 
	 * @throws ConfigurationException when any com.tinyj.infra.exception occures
	 */
	protected static void initConfiguration()
		throws ConfigurationException
	{
		try
		{
			sLock.writeLock().lock();
			String msg = "initConfiguration() started.";
			System.out.println(msg); //write to the system output stream, just in case the logger isn't available
			sLogger.debug(msg);
			initLocalConfiguration();
			initRemoteConfiguration();
			msg = "initConfiguration() ended.";
			System.out.println(msg); //write to the system output stream, just in case the logger isn't available
			sLogger.debug(msg);
		
		}
		catch (ConfigurationException ex)
		{
			throw ex;
		}
		finally
		{
			sLock.writeLock().unlock();

		}
	
	}
	
	
	
	/**
	 * initialize the local configuration from the configuration file set by the <i>com.tinyj.infra.config.local</i>
	 * system property
	 */
	protected static void initLocalConfiguration()
	{
		String currConfigFileName = null;
		FileInputStream fis = null;
		
		try
		{
			sLock.writeLock().lock();
			String msg = "initLocalConfiguration() started.";
			System.out.println(msg); //write to the system output stream, just in case the logger isn't available
			sLogger.debug(msg);
			sConfigurationFilesNamesConcatenated = System.getProperty(LOCAL_CONFIGURATION_SYSTEM_PROPERTY);
			if (sConfigurationFilesNamesConcatenated == null)
			{
				String errMsg = "Configuarion file not set. Please set the System property '" + LOCAL_CONFIGURATION_SYSTEM_PROPERTY + "' to point the location of the configuration properties file.";
				System.err.println(errMsg); //write to the system error stream, just in case the logger isn't available
				sLogger.fatal(errMsg);
				throw new ConfigurationException(errMsg);
			}
			else
			{
				msg = "ConfigurationManager is going to parse the configuration file(s): " + sConfigurationFilesNamesConcatenated;
				System.out.println(msg); //write to the system output stream, just in case the logger isn't available
				sLogger.debug(msg);
				
				//check if we have more than one configuration file in the system property
				if (sConfigurationFilesNamesConcatenated.indexOf(";") == -1)
				{
					//there is only one configuration file
					msg = "ConfigurationManager: there is only one configuration file to parse";
					System.out.println(msg); //write to the system output stream, just in case the logger isn't available
					sLogger.debug(msg);
					
					sConfigurationFilesNames = new String[1];
					sConfigurationFilesNames[0] = sConfigurationFilesNamesConcatenated;
				}
				else
				{
					//there are multiple configuration files, so split the string by ";"
					msg = "ConfigurationManager: there are multiple configuration files to parse";
					System.out.println(msg); //write to the system output stream, just in case the logger isn't available
					sLogger.debug(msg);
					
					sConfigurationFilesNames = sConfigurationFilesNamesConcatenated.split(";");
				}
			}
			
			
			sLocalParameters = new Properties();
			Properties tempLocalParameters = new Properties();
						
			for (int i=0 ; i<sConfigurationFilesNames.length ; ++i)
			{
				currConfigFileName = sConfigurationFilesNames[i];
				
				//check if the configuration file exists
				File configurationFile = new File(currConfigFileName);
				if (!configurationFile.exists())
				{
					String errMsg = "Configuarion file " + currConfigFileName + " does not exist. if you intended to specify multiple configuration files, make sure they are separated by ';' character (not ',')";
					System.err.println(errMsg); //write to the system error stream, just in case the logger isn't available
					sLogger.fatal(errMsg);
					throw new ConfigurationException(errMsg);
				}
				else
				{
					msg = "ConfigurationManager: loading configuration file: " + currConfigFileName;
					System.out.println(msg); //write to the system output stream, just in case the logger isn't available
					sLogger.debug(msg);
				}
				
				
				//as in Java 1.5, properties file can only be loaded nativley in ISO 8859-1 encoding
				//we try to "override" this by using our proprietary method inside PropertiesUtils
				//if it fails, then we try to use the native Properties.load
				fis = new FileInputStream(configurationFile);
				//tempLocalParameters.load(fis);
				try
				{
					tempLocalParameters = PropertiesUtils.loadProperties(fis, "UTF-8");
				}
				catch (Exception e)
				{
					sLogger.error("error loading configuration properties file in UTF-8. trying native Properties load...", e);
					tempLocalParameters.load(fis);
				}
				
				sLocalParameters.putAll(tempLocalParameters);
				fis.close();
			}
			
			
			msg = "initLocalConfiguration() ended.";
			System.out.println(msg); //write to the system output stream, just in case the logger isn't available
			sLogger.debug(msg);
		}
		catch (ConfigurationException ce)
		{
			throw ce;
		}
		catch(FileNotFoundException fnfe)
		{
			String errMsg = "Configuarion file " + currConfigFileName + " does not exist.";
			System.err.println(errMsg); //write to the system error stream, just in case the logger isn't available
			sLogger.fatal(errMsg, fnfe);
			throw new ConfigurationException(errMsg);
		}
		catch(IOException ioe)
		{
			String errMsg = "could not read configuration file " + currConfigFileName + ".";
			System.err.println(errMsg); //write to the system error stream, just in case the logger isn't available
			sLogger.fatal(errMsg, ioe);
			throw new ConfigurationException(errMsg);
		}
		finally
		{
			if (fis != null)
			{
				try
				{
					fis.close();
				}
				catch (IOException ioe)
				{
					String errMsg = "ConfigurationManager: could not close file input stream. ignoring...";
					System.err.println(errMsg); //write to the system error stream, just in case the logger isn't available
					sLogger.fatal(errMsg, ioe);
				}
			}
			sLock.writeLock().unlock();
		}
	}
	
	
	
	
	/**
	 * initializes the remote configuration. parameters to the DB where the remote configuration sits should be
	 * set in the local configuration (the properties file).
	 * There are 2 ways to instantiate the DBConnectionManager in order to load the remote configuration: (1)using DataSource and (2)using pure JDBC.
	 * there is also an option to choose whether to load the remote configuration or not using the local parameter:
	 * 
	 * CONFIG_LOAD_REMOTE = true | false
	 * 
	 * The needed local parameters for loading the remote configuration using a DataSource are:
	 * 
	 * CONFIG_USE_DATA_SOURCE = true
	 * CONFIG_DS_JNDI_NAME = <i>the jndi name of the data source</i>
	 * CONFIG_DS_CONTEXT_FACTORY  = <i>the context factory to use in order to lookup the data source JNDI</i>
	 * CONFIG_DS_PROVIDER_URL = <i>the URL where the JNDI rests</i>
	 * 
	 * The needed local parameters for loading the remote configuration using a pure JDBC are:
	 * 
	 * CONFIG_USE_DATA_SOURCE = false
	 * CONFIG_JDBC_DATABASE_DRIVER = <i>the JDBC driver class name</i> 
	 * CONFIG_JDBC_DATABASE_URL = <i>the DB URL</i>
	 * CONFIG_JDBC_DATABASE_USER = <i>the username to connect to the DB</i>
	 * CONFIG_JDBC_DATABASE_PASS = <i>the password to connect to the DB</i>
	 * 
	 */
	protected static void initRemoteConfiguration()
	{
		//get from the properties file a parameter indicating whether to load the
		//remote configuration from the DB
		try 
		{
			sLock.writeLock().lock();

			String msg = "initRemoteConfiguration() started.";
			System.out.println(msg); //write to the system output stream, just in case the logger isn't available
			sLogger.debug(msg);
			String loadRemoteConfigParam = sLocalParameters.getProperty(LOCAL_PARAM_CONFIG_LOAD_REMOTE);
			if (loadRemoteConfigParam == null || !loadRemoteConfigParam.equalsIgnoreCase(CONFIGURATION_TRUE))
			{
				//no need to load the remote configuration, so just create an empty Properties for it
				msg = "Remote configuration is not loaded. Set the param '" + 
						LOCAL_PARAM_CONFIG_LOAD_REMOTE + 
						"' to " + CONFIGURATION_TRUE + " if you want to load it.";
				System.out.println(msg); //write to the system output stream, just in case the logger isn't available
				sLogger.warn(msg);
				sRemoteParameters = new Properties();
				msg = "initLocalConfiguration() ended.";
				System.out.println(msg); //write to the system output stream, just in case the logger isn't available
				sLogger.debug(msg);
				return;
			}
			
			String useDataSourceParam = sLocalParameters.getProperty(LOCAL_PARAM_CONFIG_USE_DATA_SOURCE);
			if (useDataSourceParam == null || !useDataSourceParam.equalsIgnoreCase(CONFIGURATION_TRUE))
			{
				//jdbc will be used to connect to the DB in order to load configuration
				msg = "ConfigurationManager will use JDBC connection to load remote configuration. " + 
						"Set the parameter " + LOCAL_PARAM_CONFIG_USE_DATA_SOURCE + " to " + CONFIGURATION_TRUE + 
						" if you want to use data source connection instead.";
				System.out.println(msg); //write to the system output stream, just in case the logger isn't available
				sLogger.debug(msg);
				
				sUseDataSource = false;
				sJDBCDriverName = sLocalParameters.getProperty(LOCAL_PARAM_CONFIG_JDBC_DATABASE_DRIVER);
				sJDBCUrl = sLocalParameters.getProperty(LOCAL_PARAM_CONFIG_JDBC_DATABASE_URL);
				sJDBCUsername = sLocalParameters.getProperty(LOCAL_PARAM_CONFIG_JDBC_DATABASE_USER);
				sJDBCPassword = sLocalParameters.getProperty(LOCAL_PARAM_CONFIG_JDBC_DATABASE_PASS);
				
				if (sJDBCDriverName == null || sJDBCUrl == null || sJDBCUsername == null || sJDBCPassword == null)
				{
					String errMsg = "Required parameters missing from configuration file for JDBC connection. Check that the all params exist: " +
										LOCAL_PARAM_CONFIG_JDBC_DATABASE_DRIVER + ", " +
										LOCAL_PARAM_CONFIG_JDBC_DATABASE_URL + ", " +
										LOCAL_PARAM_CONFIG_JDBC_DATABASE_USER + ", " +
										LOCAL_PARAM_CONFIG_JDBC_DATABASE_PASS + 
										"\nor set " + LOCAL_PARAM_CONFIG_USE_DATA_SOURCE + " to '" + CONFIGURATION_TRUE + "' to use data source instead. ";
					System.err.println(errMsg); //write to the system error stream, just in case the logger isn't available
					sLogger.fatal(errMsg);
					throw new ConfigurationException(errMsg);
				}
				
				//init the DB connection manager
				
					sConManager = new DBConnectionManager(sJDBCDriverName, sJDBCUrl, sJDBCUsername, sJDBCPassword);
				
				
				
			}
			else
			{
				//data source will be used to connect to the DB in order to load configuration
				msg = "ConfigurationManager will use data source connection to load remote configuration. " + 
						"Set the parameter " + LOCAL_PARAM_CONFIG_USE_DATA_SOURCE + " to " + CONFIGURATION_FALSE + 
						" if you want to use JDBC connection instead.";
				System.out.println(msg); //write to the system output stream, just in case the logger isn't available
				sLogger.debug(msg);
				
				sUseDataSource = true;
				sDataSourceJNDIName = sLocalParameters.getProperty(LOCAL_PARAM_CONFIG_DATA_SOURCE_JNDI_NAME);
				sJNDIContextFactory = sLocalParameters.getProperty(LOCAL_PARAM_CONFIG_DATA_SOURCE_CONTEXT_FACTORY);
				sJNDIProviderURL = sLocalParameters.getProperty(LOCAL_PARAM_CONFIG_DATA_SOURCE_PROVIDER_URL);
				
				if (sDataSourceJNDIName == null || sJNDIContextFactory == null || sJNDIProviderURL == null)
				{
					String errMsg = "Required parameters missing from configuration file for data source connection. Check that the all params exist: " + 
										LOCAL_PARAM_CONFIG_DATA_SOURCE_JNDI_NAME + ", " +
										LOCAL_PARAM_CONFIG_DATA_SOURCE_CONTEXT_FACTORY + ", " +
										LOCAL_PARAM_CONFIG_DATA_SOURCE_PROVIDER_URL + 
										"\nor set " + LOCAL_PARAM_CONFIG_USE_DATA_SOURCE + " to '" + CONFIGURATION_FALSE + "' to use JDBC instead. ";
					System.err.println(errMsg); //write to the system error stream, just in case the logger isn't available
					sLogger.fatal(errMsg);
					throw new ConfigurationException(errMsg);
				}
				
				//init the DB connection manager
				try 
				{
					sConManager = new DBConnectionManager(sDataSourceJNDIName, sJNDIContextFactory, sJNDIProviderURL);
				} 
				catch (DBConnectionException e) 
				{
					String errMsg = "Could not instantiate a DBConnectionManager: " + e.getMessage();
					System.err.println(errMsg); //write to the system error stream, just in case the logger isn't available
					sLogger.fatal(errMsg, e);
					throw new ConfigurationException(errMsg);
				}
				
			}
			
			//go to load the parameters from the database
			loadParametersFromDB();
			msg = "initRemoteConfiguration() ended.";
			System.out.println(msg); //write to the system output stream, just in case the logger isn't available
			sLogger.debug(msg);
			
		} 
		catch (ConfigurationException e)
		{
			throw e;
		}
		catch (DBConnectionException e) 
		{
			String errMsg = "Could not instantiate a DBConnectionManager: " + e.getMessage();
			System.err.println(errMsg); //write to the system error stream, just in case the logger isn't available
			sLogger.fatal(errMsg, e);
			throw new ConfigurationException(errMsg);
		}
		catch (Exception e)
		{
		}
		finally
		{
			sLock.writeLock().unlock();
		}
		
	}
	
	
	
	
	/**
	 * loads the remote parameters from the DB 
	 */
	protected static void loadParametersFromDB()
	{
		Connection con = null;
		
		String query = "select " + CONF_TABLE_PARAM_NAME_COLUMN + ", " + CONF_TABLE_PARAM_VALUE_COLUMN + 
						" from " + CONF_TABLE_NAME;
		PreparedStatement ps = null;
		ResultSet rs = null;


		try 
		{

			sLock.writeLock().lock();
			String msg = "loadParametersFromDB() started.";
			System.out.println(msg); //write to the system output stream, just in case the logger isn't available
			sLogger.debug(msg);
			//DBConnectionManager conMgr = new DB
			String paramName = null;
			String paramValue = null;
			String parameterNameValuePrint = null;
		
			con = sConManager.createDBConnection();
			ps = con.prepareStatement(query);
			rs = ps.executeQuery();
			
			while (rs.next())
			{
				paramName = rs.getString(CONF_TABLE_PARAM_NAME_COLUMN);
				paramValue = rs.getString(CONF_TABLE_PARAM_VALUE_COLUMN);
				parameterNameValuePrint = "[" + paramName + ", " + paramValue + "]";
				if (paramName == null || paramValue == null)
				{
					msg = "Parameter names or values cannot be null. It will not be added. " + parameterNameValuePrint;
					System.out.println(msg); //write to the system output stream, just in case the logger isn't available
					sLogger.warn(msg);
				}
				else
				{
					msg = "Adding parameter: " + parameterNameValuePrint;
					System.out.println(msg); //write to the system output stream, just in case the logger isn't available
					sLogger.debug(msg);
					sRemoteParameters.put(paramName, paramValue);
				}
			}
			
			msg = "loadParametersFromDB() ended.";
			System.out.println(msg); //write to the system output stream, just in case the logger isn't available
			sLogger.debug(msg);
		}
		catch (DBConnectionException dbce) 
		{
			String errMsg = "Could not create a connection to the DB.";
			System.err.println(errMsg); //write to the system error stream, just in case the logger isn't available
			sLogger.fatal(errMsg, dbce);
			throw new ConfigurationException(errMsg);
		}
		catch (SQLException sqle) 
		{
			String errMsg = "Could not load paramters from the DB.";
			System.err.println(errMsg); //write to the system error stream, just in case the logger isn't available
			sLogger.fatal(errMsg, sqle);
			throw new ConfigurationException(errMsg);
		}
		finally
		{
			sLock.writeLock().unlock();

			sConManager.closeResources(con, ps, rs);
		}
	}
	
	
	/**
	 * updates a paramter in the configuration table
	 * @param param - the name of the parameter
	 * @param paramvalue - the parameter value
	 */
	protected static void saveParameterToDB(String param, String paramvalue)
	{
		String msg = "saveParameterToDB() started.";
		System.out.println(msg); //write to the system output stream, just in case the logger isn't available
		sLogger.debug(msg);
		//DBConnectionManager conMgr = new DB
		Connection con = null;

		String query = "update " +  CONF_TABLE_NAME +" set "  + CONF_TABLE_PARAM_VALUE_COLUMN + " = " +paramvalue+
						" where " + CONF_TABLE_PARAM_NAME_COLUMN + " = '" + param+"'";
		PreparedStatement ps = null;
		ResultSet rs = null;
	
		
		try 
		{
			sLock.writeLock().lock();
			con = sConManager.createDBConnection();
			ps = con.prepareStatement(query);
			ps.executeUpdate();
			
			//reloading remote configuration parameters
			msg = "saveParameterToDB() reloading remote configuration.";
			System.out.println(msg); //write to the system output stream, just in case the logger isn't available
			sLogger.debug(msg);
			reloadRemote();
			
			sLogger.debug("saveParameterToDB() ended.");
		}
		catch (DBConnectionException dbce) 
		{
			String errMsg = "Could not save a parameter to the DB.";
			System.err.println(errMsg); //write to the system error stream, just in case the logger isn't available
			sLogger.error(errMsg, dbce);
			throw new ConfigurationException(errMsg);
		}
		catch (SQLException sqle) 
		{
			String errMsg = "Could not save a parameter to the DB.";
			System.err.println(errMsg); //write to the system error stream, just in case the logger isn't available
			sLogger.error(errMsg, sqle);
			throw new ConfigurationException(errMsg);
		}
		finally
		{
			sConManager.closeResources(con, ps, rs);
			sLock.writeLock().unlock();

		}
	}
	
}
