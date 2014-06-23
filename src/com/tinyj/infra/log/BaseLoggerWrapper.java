package com.tinyj.infra.log;


import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;



/**
 * BaseLoggerWrapper is a wrapper for any logger framework, but is designed to comply with the functionalities
 * of the log4j logger. The purpose of this wrapper is not the ability to change the internal logging
 * framework, but to offer intermediate operations when calling the regular logging methods.
 * An example for the intended wrapping use, can be a Logger that sends an SNMP trap call for every log 
 * message that contains one of a list of pre-defined words.
 * 
 * The way to do such a thing is by extending the BaseLoggerWrapper and implement one of its methods differently 
 * than the original implementation.
 * 
 * first, it is needed to create a new static method <i>getInstance()</i> which will return an instance
 * of the extending class. this is needed in order to activate the <i>getLogger()</i> which calls the
 * method <i>createLoggerWrapperInstance()</i> (look below) that returns a NEW instance of the extending
 * class.  
 * The extending class should re-implement the following abstract methods:
 * 
 * 
 * - protected BaseLoggerWrapper createLoggerWrapperInstance() - should implement a creation of the wrapper instance
 * 
 * - protected Object createInternalLogger(String aName) - should implement a creation of the internally
 * 			used logger inside the wrapper (for example org.apache.log4j.Logger)
 * 
 * - protected Object createInternalRootLogger() - should implement a creation of the root logger
 * 
 * - protected Object createInternalLoggerByFactory(String aName, Object aFactory) - should implement a
 * 			creation of a logger according to the given factory. factory is given here as an object, but
 * 			should be casted to the desired true factory type.
 * 
 * - public void setConfiguration(String aConfigurationFileFullName) - should implement a configuration
 * 			set according to the given configuration file name
 * 
 * 
 * In addition, the rest of the abstract methods are needed to be implemented too. These methods are 
 * taken as a base functionality framework from the log4j framework (although many of them are redundant 
 * or missing from other logging implementation, but as mentioned above, the intention of this wrapper 
 * was not to provide generic logger wrapping mechanism).
 * 
 * An example implementation is the Log4jWrapper in this package.
 * 
 * 
 * @author asaf.peeri
 *
 */
public abstract class BaseLoggerWrapper
{
	//a static instance of the base logger wrapper for extender classes
	protected static BaseLoggerWrapper sInstance;
	
	
	/**
	 * creates an instance of the wrapper, to be returned to the user. Of course the returned wrapper
	 * has to extend this com.tinyj.infra.log.Logger class.
	 * 
	 * @return a wrapper instance to be returned to the user.
	 */
	protected abstract BaseLoggerWrapper createLoggerWrapperInstance();
	
	
	/**
	 * creates an instance of the internal logger that will be wrapped according to the given category. 
	 * an example of this internal logger can be the org.apache.log4j.Logger.
	 * 
	 * @param aLoggerCategoryName the name of the internal Logger category
	 * 
	 * @return the created internal logger instance according to the given category
	 */
	protected abstract Object createInternalLogger(String aLoggerCategoryName);
	
	
	/**
	 * creates an instance of the internal logger root category that will be wrapped.
	 * 
	 * @return the created internal logger instance with the root category
	 */
	protected abstract Object createInternalRootLogger();
	
	
	/**
	 * creates an instance of the internal logger that will be wrapped according to the given logger
	 * category and logger factory.
	 * 
	 * @param aLoggerCategoryName the name of the internal Logger category
	 * @param aFactory the factory for the logger
	 * 
	 * @return the created internal logger instance according to the given category and factory
	 */
	protected abstract Object createInternalLoggerByFactory(String aLoggerCategoryName, Object aFactory);
	
	
	/**
	 * sets the current configuration according to the given configuration file.
	 * 
	 * @param aConfigurationFileFullName the configuration file full name (and path)
	 */
	public abstract void setConfiguration(String aConfigurationFileFullName);
	
	
	
	/**
	 * a method that should implement a set of the given internal logger instance inside the
	 * current wrapper (as a member variable)
	 * @param aLogger
	 */
	protected abstract void setInternalLogger(Object aLogger);
		
	
	/**
	 * returns a logger according to the given category
	 * 
	 * @param aLoggerCategoryName the category for the returned logger
	 * 
	 * @return the logger assigned with the given category
	 */
	public BaseLoggerWrapper getLogger(String aLoggerCategoryName) 
	{
		Object internalLoggerInstance = createInternalLogger(aLoggerCategoryName);
		BaseLoggerWrapper loggerWrapper = createLoggerWrapperInstance();
		loggerWrapper.setInternalLogger(internalLoggerInstance);
		return loggerWrapper;
		
	}
	
	
	/**
	 * returns a logger with the given class name as a category for the logger
	 * 
	 * @param clazz the class that its name will be used as a category for the returned logger
	 * 
	 * @return the logger assigned with the given class name as category 
	 */
	public BaseLoggerWrapper getLogger(Class clazz) 
	{
		return getLogger(clazz.getName());
	}
	
	
	/**
	 * returns a logger wrapper assinged with the root category
	 * 
	 * @return the logger wrapper assigned with the root category
	 */
	public BaseLoggerWrapper getRootLogger() 
	{	
		Object internalLoggerInstance = createInternalRootLogger();
		BaseLoggerWrapper loggerWrapper = createLoggerWrapperInstance();
		loggerWrapper.setInternalLogger(internalLoggerInstance);
		return loggerWrapper;	
	}
	
	
	/**
	 * returns a logger wrapper with an internal logger created by the given factory, assigned with the
	 * given category
	 *  
	 * @param aLoggerCategoryName the category for the returned logger 
	 * @param factory the factory that will be used to create the internal logger
	 * 
	 * @return the logger wrapper assigned with the given category, holding internal logger created by the
	 * given factory
	 */
	public BaseLoggerWrapper getLogger(String aLoggerCategoryName, LoggerFactory factory) 
	{
		Object internalLoggerInstance = createInternalLoggerByFactory(aLoggerCategoryName, factory);
		BaseLoggerWrapper loggerWrapper = createLoggerWrapperInstance();
		loggerWrapper.setInternalLogger(internalLoggerInstance);
		return loggerWrapper;
	}
	
	
	//*************** abstract logging  methods *****************
	public abstract void trace(java.lang.Object message);
	
	
	public abstract void trace(java.lang.Object message, java.lang.Throwable t);
	
	
	public abstract void debug(java.lang.Object message);
		
	
	public abstract void debug(java.lang.Object message, java.lang.Throwable t);
		
	
	public abstract void error(java.lang.Object message);
		
	
	public abstract void error(java.lang.Object message,java.lang.Throwable t);
		
	
	public abstract void fatal(java.lang.Object message);
		
	
	public abstract void fatal(java.lang.Object message, java.lang.Throwable t);
		
	
	public abstract void info(java.lang.Object message);
		
	
	public abstract void info(java.lang.Object message, java.lang.Throwable t);
		
	
	public abstract void warn(java.lang.Object message);
		
	
	public abstract void warn(java.lang.Object message, java.lang.Throwable t);
		
	
	//**************** abstract additional helper methods *******************
	public abstract boolean getAdditivity();
		
	
	public abstract java.util.Enumeration getAllAppenders();
		
	
	public abstract Appender getAppender(java.lang.String name);
		
	
	public abstract Level getEffectiveLevel();
		
	
	public abstract LoggerRepository getLoggerRepository();
		
	
	public abstract java.lang.String getName();
		
	
	public abstract Category getParent();
		
	
	public abstract Level getLevel();
		
	
	public abstract java.util.ResourceBundle getResourceBundle();
		
	
	public abstract boolean isAttached(Appender appender);
	
	
	public abstract boolean isTraceEnabled();
	
	
	public abstract boolean isDebugEnabled();
		
	
	public abstract boolean isEnabledFor(Priority level);
		
	
	public abstract boolean isInfoEnabled();
		
	
	public abstract void l7dlog(Priority priority, java.lang.String key, java.lang.Throwable t);
		
	
	public abstract void l7dlog(Priority priority, java.lang.String key, java.lang.Object[] params, java.lang.Throwable t);
		
	
	public abstract void log(Priority priority, java.lang.Object message, java.lang.Throwable t);
		
	
	public abstract void log(Priority priority, java.lang.Object message);
		
	
	public abstract void log(java.lang.String callerFQCN, Priority level, java.lang.Object message, java.lang.Throwable t);
		
	
	public abstract void removeAllAppenders();
		
	
	public abstract void removeAppender(Appender appender);
		
	
	public abstract void removeAppender(java.lang.String name);
	
	
	public abstract void setAdditivity(boolean additive);
		
	
	public abstract void setLevel(Level level);
		
		
	public abstract void setResourceBundle(java.util.ResourceBundle bundle);
}
