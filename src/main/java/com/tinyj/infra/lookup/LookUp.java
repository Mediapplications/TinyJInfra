package com.tinyj.infra.lookup;

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;



/**
 * This class is used to retrieve an InitialContext object according to given environment variables.
 * it is useful to get a basic InitialContext, which requires only JNDI context factory and provider
 * URL, but there is also an option to give a Properties object containing more environment variables
 * and get an InitialContext object that is initialized with the given environment.
 * 
 * It is also possible to extend this class to create a specific LookUp class. For example, this
 * class can be extended to be <i>MyAppLookUp</i> which implements a getInstance() method that
 * initializes the instance with a pre-defined environment Properties file. Then, to get the initial
 * context, the method <i>getInitialContext()</i> should be called (it uses the internal environment).
 * 
 * A class as described above can look like the following:
 * 
 *  public class MyAppLookUp
 *  {   
 *  	public static LookUp getInstance()
 *  	{
 *  		Properties env = new Properties();
 *			env.put(InitialContext.INITIAL_CONTEXT_FACTORY,"org.jnp.interfaces.NamingContextFactory");
 *			env.put(InitialContext.PROVIDER_URL,"jnp://192.168.10.59");
 *			env.put(NamingContext.JNP_DISABLE_DISCOVERY,"true");
 *	    	
 *			return LookUp.getInstance(env);
 *  	}
 *  }
 *  
 * 
 * @author asaf.peeri
 *
 */
public class LookUp
{
	//static
	protected static LookUp sInstance;
	
	//members
	protected Properties mEnv;
	
	
	//c'tor
	protected LookUp(Properties aEnv)
	{
		mEnv = aEnv;
	}
	
	
	/**
	 * get the instance of the LookUp class
	 * @return
	 */
	public static LookUp getInstance()
	{
		return getInstance(null);
	}
	
	
	/**
	 * get the instance of the LookUp class according to the given environment properties
	 * 
	 * @return the instance
	 */
	protected static LookUp getInstance(Properties aEnv)
	{
		if (sInstance == null)
		{
			synchronized(LookUp.class)
			{
				if (sInstance == null)
				{
					sInstance = new LookUp(aEnv);
				}
			}
		}
		
		return sInstance;
	}
	
	
	/**
	 * 
	 * @param aJNDIContextFactory the JNDI context factory
	 * @param aJNDIProviderURL the target host URL
	 * 
	 * @return the initial context
	 * 
	 * @throws NamingException when any exception is thrown while trying to initialize the initial context
	 */
	public InitialContext getInitialContext(String aJNDIContextFactory, String aJNDIProviderURL) 
		throws NamingException 
	{
		Hashtable<String, String> env = getBasicEnvProperties(aJNDIContextFactory, aJNDIProviderURL);
	    InitialContext initialContext = new InitialContext(env);
	
	    return initialContext;
	}
	
	
	/**
	 * get the initial context according to a given JNDI context and provider URL
	 * 
	 * @param aJNDIContextFactory the JNDI context factory
	 * @param aJNDIProviderURL the target host URL
	 * @return
	 */
	protected Hashtable<String, String> getBasicEnvProperties(String aJNDIContextFactory, String aJNDIProviderURL)
	{
	    Hashtable<String, String> env = new Hashtable<String, String>();
	    env.put(Context.INITIAL_CONTEXT_FACTORY, aJNDIContextFactory);
	    env.put(Context.PROVIDER_URL, aJNDIProviderURL);
	    
	    return env;
	}
	
	
	/**
	 * get the initial context according to a given environment properties object
	 * @param props the environment properties to initialize the initial context with
	 * @return the initial context
	 * 
	 * @throws NamingException when any exception is thrown while trying to initialize the initial context
	 */
	public InitialContext getInitialContext(Properties props) 
		throws NamingException 
	{
		InitialContext initialContext = new InitialContext(props);
	
	    return initialContext;
	}
	
	
	
	/**
	 * get the initial context according to the environment properties object saved in this instance.
	 * this method should be used only by subclasses of this class, where initialization of the
	 * environment properties is done internally for a specific cause.
	 * 
	 * @param props the environment properties to initialize the initial context with
	 * @return the initial context
	 * 
	 * @throws NamingException when any exception is thrown while trying to initialize the initial context
	 */
	public InitialContext getInitialContext()
		throws NamingException
	{
		InitialContext initialContext = null;
		
		if (mEnv != null)
		{
			initialContext = getInitialContext(mEnv);
			return initialContext;
		}
		else
		{
			return null;
		}
	}
	
	
	/**
	 * looks up a remote object according to the given environment properties
	 * 
	 * @param aJNDIObjectName the object's name in the JNDI
	 * @param aEnv the environment to use to initialize the initial context
	 * 
	 * @return the remote object retrieved
	 */
	public Object lookup(String aJNDIObjectName, Properties aEnv)
		throws NamingException
	{
		InitialContext ic = getInitialContext(aEnv);
		Object remoteObject = ic.lookup(aJNDIObjectName);
		
		return remoteObject;
	}
	
	
	/**
	 * looks up a remote object according to pre-defined environment properties
	 * 
	 * @param aJNDIObjectName the object's name in the JNDI
	 * 
	 * @return the remote object retrieved
	 */
	public Object lookup(String aJNDIObjectName)
		throws NamingException
	{
		return lookup(aJNDIObjectName, mEnv);
	}
}
