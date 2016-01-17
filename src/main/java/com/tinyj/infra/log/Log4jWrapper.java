package com.tinyj.infra.log;

import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;



/**
 * This class is intended to serve as a base for log4j wrapper.
 * Actually, an org.apache.log4j.Logger instance is held internally by Logger, and all the 
 * public methods of org.apache.log4j.Logger (and its inherited methods from 
 * org.apache.log4j.Category), are delegated to the org.apache.log4j.Logger.
 * 
 * It is possible to extend this class to add functionality for the various logging methods.
 * While developing a product, it is best to extend this wrapper to have a product specific
 * Logger, which can override the needed methods in order to add intermediate functionality inside.
 * 
 * class extending this Wrapper should implement the <i>getInstance()</i> method in order to return
 * instance of its own type, implement the <i>createWrapperInstance()</i> method to return its a 
 * NEW instance of its own type, and the desired methods to re-implement (for ex: <i>debug()</i>)
 * 
 * an example of such implementation can be found under the test sources folder with the name:
 * com.tinyj.infra.log.MyLogger
 * 
 * @author asaf.peeri
 *
 */
public class Log4jWrapper extends BaseLoggerWrapper
{
	//the log4j Logger instance is held internally as a member variable
	protected org.apache.log4j.Logger mInternalLog4jLogger;

	
	protected Log4jWrapper()
	{
	}
	
	
	public static BaseLoggerWrapper getInstance()
	{
		if (sInstance == null)
		{
			synchronized(Log4jWrapper.class)
			{
				if (sInstance == null)
				{
					sInstance = new Log4jWrapper();
				}
			}
		}
		
		return sInstance;
	}
	
	
	protected BaseLoggerWrapper createLoggerWrapperInstance()
	{
		return new Log4jWrapper();
	}
	
	
	protected Object createInternalLogger(String aName)
	{
		org.apache.log4j.Logger log4jLogger = org.apache.log4j.LogManager.getLogger(aName);
		
		return log4jLogger;
	}
	
	
	protected Object createInternalRootLogger()
	{
		org.apache.log4j.Logger log4jLogger = org.apache.log4j.LogManager.getRootLogger();
		
		return log4jLogger;
	}
	
	
	protected Object createInternalLoggerByFactory(String aName, Object aFactory)
	{
		org.apache.log4j.spi.LoggerFactory loggerFactory = (org.apache.log4j.spi.LoggerFactory)aFactory;
		org.apache.log4j.Logger log4jLogger = org.apache.log4j.LogManager.getLogger(aName, loggerFactory);
		
		return log4jLogger;
	}
	
	
	public void setConfiguration(String aConfigurationFileFullName)
	{
		String msg = null;
		BaseLoggerWrapper rootLogger = getRootLogger();
		msg = "Log4jWrapper: Going to change configuration file to: " + aConfigurationFileFullName;
		System.out.println(msg);
		rootLogger.warn(msg);
		
		PropertyConfigurator.configureAndWatch(aConfigurationFileFullName);
		
		msg = "Log4jWrapper: Configuration file was changed to: " + aConfigurationFileFullName;
		System.out.println(msg);
		rootLogger.warn(msg);
	}
	
	
	protected void setInternalLogger(Object aLogger)
	{
		mInternalLog4jLogger = (org.apache.log4j.Logger)aLogger;
	}
			
	
	//*************** logging  methods *****************
	public void trace(java.lang.Object message)
	{
		mInternalLog4jLogger.trace(message);
	}
	
	
	public void trace(java.lang.Object message, java.lang.Throwable t)
	{
		mInternalLog4jLogger.trace(message, t);
	}
	
	
	public void debug(java.lang.Object message)
	{
		mInternalLog4jLogger.debug(message);
	}
	
	
	public void debug(java.lang.Object message, java.lang.Throwable t)
	{
		mInternalLog4jLogger.debug(message, t);
	}
	
	
	public void error(java.lang.Object message)
	{
		mInternalLog4jLogger.error(message);
	}
	
	
	public void error(java.lang.Object message,java.lang.Throwable t)
	{
		mInternalLog4jLogger.error(message, t);
	}
	
	
	public void fatal(java.lang.Object message)
	{
		mInternalLog4jLogger.fatal(message);
	}
	
	
	public void fatal(java.lang.Object message, java.lang.Throwable t)
	{
		mInternalLog4jLogger.fatal(message, t);
	}
	
	
	public void info(java.lang.Object message)
	{
		mInternalLog4jLogger.info(message);
	}
	
	
	public void info(java.lang.Object message, java.lang.Throwable t)
	{
		mInternalLog4jLogger.info(message, t);
	}
	
	
	public void warn(java.lang.Object message)
	{
		mInternalLog4jLogger.warn(message);
	}
	
	
	public void warn(java.lang.Object message, java.lang.Throwable t)
	{
		mInternalLog4jLogger.warn(message, t);
	}
	
	
	//additional helper methods
	public boolean getAdditivity()
	{
		return mInternalLog4jLogger.getAdditivity();
	}
	
	
	public java.util.Enumeration getAllAppenders()
	{
		return mInternalLog4jLogger.getAllAppenders();
	}
	
	
	public Appender getAppender(java.lang.String name)
	{
		return mInternalLog4jLogger.getAppender(name);
	}
	
	
	public Level getEffectiveLevel()
	{
		return mInternalLog4jLogger.getEffectiveLevel();
	}
	
	
	public LoggerRepository getLoggerRepository()
	{
		return mInternalLog4jLogger.getLoggerRepository();
	}
	
	
	public final java.lang.String getName()
	{
		return mInternalLog4jLogger.getName();
	}
	
	
	public final Category getParent()
	{
		return mInternalLog4jLogger.getParent();
	}
	
	
	public final Level getLevel()
	{
		return mInternalLog4jLogger.getLevel();
	}
	
	
	public java.util.ResourceBundle getResourceBundle()
	{
		return mInternalLog4jLogger.getResourceBundle();
	}
	
	
	public boolean isAttached(Appender appender)
	{
		return mInternalLog4jLogger.isAttached(appender);
	}
	
	
	public boolean isTraceEnabled()
	{
		return mInternalLog4jLogger.isTraceEnabled();
	}
	
	
	public boolean isDebugEnabled()
	{
		return mInternalLog4jLogger.isDebugEnabled();
	}
	
	
	public boolean isEnabledFor(Priority level)
	{
		return mInternalLog4jLogger.isEnabledFor(level);
	}
	
	
	public boolean isInfoEnabled()
	{
		return mInternalLog4jLogger.isInfoEnabled();
	}
	
	
	public void l7dlog(Priority priority, java.lang.String key, java.lang.Throwable t)
	{
		mInternalLog4jLogger.l7dlog(priority, key, t);
	}
	
	
	public void l7dlog(Priority priority, java.lang.String key, java.lang.Object[] params, java.lang.Throwable t)
	{
		mInternalLog4jLogger.l7dlog(priority, key, params, t);
	}
	
	
	public void log(Priority priority, java.lang.Object message, java.lang.Throwable t)
	{
		mInternalLog4jLogger.log(priority, message, t);
	}
	
	
	public void log(Priority priority, java.lang.Object message)
	{
		mInternalLog4jLogger.log(priority, message);
	}
	
	
	public void log(java.lang.String callerFQCN, Priority level, java.lang.Object message, java.lang.Throwable t)
	{
		mInternalLog4jLogger.log(callerFQCN, level, message, t);
	}
	
	
	public void removeAllAppenders()
	{
		mInternalLog4jLogger.removeAllAppenders();
	}
	
	
	public void removeAppender(Appender appender)
	{
		mInternalLog4jLogger.removeAppender(appender);
	}
	
	
	public void removeAppender(java.lang.String name)
	{
		mInternalLog4jLogger.removeAppender(name);
	}
	
	
	public void setAdditivity(boolean additive)
	{
		mInternalLog4jLogger.setAdditivity(additive);
	}
	
	
	public void setLevel(Level level)
	{
		mInternalLog4jLogger.setLevel(level);
	}
	
		
	public void setResourceBundle(java.util.ResourceBundle bundle)
	{
		mInternalLog4jLogger.setResourceBundle(bundle);
	}
}
