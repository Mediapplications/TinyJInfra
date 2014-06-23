package com.tinyj.infra.db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.tinyj.infra.exception.DBConnectionException;
import com.tinyj.infra.log.BaseLoggerWrapper;
import com.tinyj.infra.log.Log4jWrapper;
import com.tinyj.infra.lookup.LookUp;


public class DBConnectionManager 
{
	private static final BaseLoggerWrapper sLogger = Log4jWrapper.getInstance().getLogger(DBConnectionManager.class);
	
	//members
	protected boolean mUseDataSource;
	
	//for DataSource connection
	protected String mJndiName;
	protected String mJndiContextFactory;
	protected String mJndiProviderURL;
	protected DataSource mDataSource;
	
	//for JDBC connection
	protected String mJdbcDriverName;
	protected String mDBUrl;
	protected String mUsername;
	protected String mPassword;
	protected Driver mJDBCDriver;
	
	
	
	/**
	 * creates a DBConnectionManager to connect using a data source
	 * 
	 * @param aJndiName the JNDI name of the data source
	 * @param aJndiContextFactory the context factory for the jndi
	 * @param aJndiProviderURL the URL where the JNDI listens
	 * 
	 * @throws DBConnectionException when any exception occures
	 */
	public DBConnectionManager(String aJndiName, String aJndiContextFactory, String aJndiProviderURL)
		throws DBConnectionException
	{
		//TODO: use this constructor to make the connection manager data source
		mUseDataSource = true;
		mJndiName = aJndiName;
		mJndiContextFactory = aJndiContextFactory;
		mJndiProviderURL = aJndiProviderURL;
		
		initDataSource();
	}
	
	
	
	/**
	 * creates a DBConnectionManager to connect using pure JDBC
	 * 
	 * @param aJdbcDriverName the FQN of the class of the driver 
	 * @param aDBUrl the URL where the DB listens
	 * @param aUsername the username to conenct with
	 * @param aPassword the password to connect with
	 * 
	 * @throws DBConnectionException when any exception occures
	 */
	public DBConnectionManager(String aJdbcDriverName, String aDBUrl, String aUsername, String aPassword)
		throws DBConnectionException
	{
		//TODO: use this constructor to make the connection manager use pure jdbc
		mUseDataSource = false;	
		mJdbcDriverName = aJdbcDriverName;
		mDBUrl = aDBUrl;
		mUsername = aUsername;
		mPassword = aPassword;
		
		initJDBCDriver();
	}
	
	
	
	/**
	 * initializes the data source
	 * 
	 * @throws DBConnectionException
	 */
	protected void initDataSource()
		throws DBConnectionException
	{
		sLogger.debug("initDataSource() started.");
		try
		{
			InitialContext ctx = LookUp.getInstance().getInitialContext(mJndiContextFactory, mJndiProviderURL);
			mDataSource = (javax.sql.DataSource) ctx.lookup(mJndiName);
			if (mDataSource == null)
			{
				throw new NamingException("Data source was retrieved as null");
			}
			sLogger.debug("initDataSource() ended.");
		}
		catch(NamingException ne)
		{
			String errMsg = "JNDI name " + mJndiName + " not found at " + mJndiProviderURL + ".";
			sLogger.fatal(errMsg, ne);
			throw new DBConnectionException(errMsg);
		}
	}
	
	
	/**
	 * initializes the JDBC driver
	 * 
	 * @throws DBConnectionException
	 */
	protected void initJDBCDriver()
		throws DBConnectionException
	{
		sLogger.debug("initJDBCDriver() started.");
		try 
		{
			Class.forName(mJdbcDriverName);
			mJDBCDriver = DriverManager.getDriver(mDBUrl);
			sLogger.debug("initJDBCDriver() ended.");
		} 
		catch (ClassNotFoundException cnfe) 
		{
			String errMsg = "Could not instantiate JDBC driver class " + mJdbcDriverName + ".";
			sLogger.fatal(errMsg, cnfe);
			throw new DBConnectionException(errMsg);
		} 
		catch (SQLException sqle) 
		{
			String errMsg = "Could not get Driver from URL " + mDBUrl + ".";
			sLogger.fatal(errMsg, sqle);
			throw new DBConnectionException(errMsg);
		}		
	}
	
	
	
	/**
	 * create a DB connection. if the DBConnectionManager was constructed with data source
	 * parameters, then the createDBConnection() method will use the data source initialized
	 * to create a DB conenction. if it was constructed with JDBC parameters, then it will
	 * use pure JDBC to create a DB connection.
	 * 
	 * @return the created DB connection
	 * 
	 * @throws DBConnectionException when any exception occurs
	 */
	public Connection createDBConnection()
		throws DBConnectionException
	{
		sLogger.debug("createDBConnection() started.");
		Connection con = null;
		//DataSource dataSource = null;
		
		if (mUseDataSource == true)
		{
			//get the connection from the data source
			sLogger.debug("createDBConnection(): getting the connection from the data source...");
			try
			{
				con = mDataSource.getConnection();
				sLogger.debug("createDBConnection() ended.");
				return con;
			}
			catch (SQLException sqle)
			{
				String errMsg = "Could not get DB connection from data source " + mDataSource + ".";
				sLogger.fatal(errMsg, sqle);
				throw new DBConnectionException(errMsg);
			}
		}
		else
		{
			//get the connection from JDBC
			sLogger.debug("createDBConnection(): getting the connection from JDBC...");
			try 
			{
				Properties dbProps = new Properties();
				dbProps.setProperty("user", mUsername);
				dbProps.setProperty("password", mPassword);
				con = mJDBCDriver.connect(mDBUrl, dbProps);
				sLogger.debug("createDBConnection() ended.");
				return con;
			} 
			catch (SQLException sqle) 
			{
				String errMsg = "Could not get DB conenction from URL " + mDBUrl + ".";
				sLogger.fatal(errMsg, sqle);
				throw new DBConnectionException(errMsg);
			}
		}//end if-else
		
	}//end createDBConnection()
	
	
	
	
	/**
	 * closes the connection, PreparedStatement or ResultSet when supplied (not null)
	 * 
	 * @param aCon the connection to close
	 * @param aPs the PreparedStatement to close
	 * @param aRs the ResultSet to close
	 */
	public void closeResources(Connection aCon, PreparedStatement aPs, ResultSet aRs)
	{
		if (aCon != null)
		{
			try
			{
				aCon.close();
			}
			catch(SQLException sqle)
			{
				sLogger.warn("Error closing connection.", sqle);
			}
		}
		
		if (aPs != null)
		{
			try
			{
				aPs.close();
			}
			catch(SQLException sqle)
			{
				sLogger.warn("Error closing PreparedStatement.", sqle);
			}
		}
		
		if (aRs != null)
		{
			try
			{
				aRs.close();
			}
			catch(SQLException sqle)
			{
				sLogger.warn("Error closing ResultSet.", sqle);
			}
		}
	}
}
