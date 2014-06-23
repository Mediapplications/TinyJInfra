/**
 * 
 */
package com.tinyj.infra.resourcebundle;

import java.util.HashMap;
import java.util.Locale;

/**
 * @author asaf
 *
 */
public class ExternalResourceBundleFactory
{

	private static ExternalResourceBundleFactory sFactory = null;
	private HashMap<String,ExternalResourceBundle> mBundlesMap = new HashMap<String, ExternalResourceBundle>();

	
	public static ExternalResourceBundleFactory getInstance()
	{
		return sFactory;
	}
	
	
	static
	{
		sFactory = new ExternalResourceBundleFactory();
	}
	

	protected ExternalResourceBundleFactory()
	{
		// TODO Auto-generated constructor stub
	}

	
	protected ExternalResourceBundle getResourceBundle(String aPath,String aResBundlePrefix)
	{
		return getResourceBundle(aPath, aResBundlePrefix, Locale.getDefault());
	}

	
	
//	public synchronized ExternalResourceBundle getResourceBundle(String aPath,String aBaseFile,Locale aLocale)
//	{
//		return getResourceBundle(aPath, aBaseFile, aLocale.toString());
//	}
	
	public ExternalResourceBundle getResourceBundle(String aPath,String aResBundlePrefix,Locale aLocale)
	{
		//create a hash key for this specific extenral resourceBundle
		StringBuilder key = new StringBuilder();
		key.append(aPath);
		key.append("|");
		key.append(aResBundlePrefix);
		key.append("|");
		key.append(aLocale);
		
		String strKey = key.toString();
		
		//look up if this resourceBundle already exists
		ExternalResourceBundle bundle = mBundlesMap.get(strKey);
		
		if (bundle == null)
		{
			//sync threads only if there is a need to create a new resource bundle
			synchronized(this)
			{
				//recheck
				bundle = mBundlesMap.get(strKey);
				if (bundle == null)
				{
					//it doesn't exist, so create a new one and put it in the hash map
					bundle = new ExternalResourceBundle(aPath,aResBundlePrefix,aLocale);
					mBundlesMap.put(strKey, bundle);
				}
			}
		}
		
		return bundle;
	}
	
	

}
