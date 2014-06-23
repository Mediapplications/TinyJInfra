package com.tinyj.infra.resourcebundle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.Properties;

import com.tinyj.infra.file.FileUtils;

/**
 * This class handles resource bundle from external file
 * @author asaf
 *
 */
public class ExternalResourceBundle
{
	
	
	private String mFullFileName = null;
	private Properties mProperties = null;
	//ResourceBundle mBundle = null;
	private String mPath = null;
	private String mResBundlePrefix = null;


	/**
	 * Creates a resource bundle object
	 * @param aPath path to resource bundle files
	 * @param aResBundlePrefix the prefix name for the resource files. A resource file for example will be
	 * myresource_en_US.properties, sothe prefix is "myresource_"
	 * @param aLocale the locale for this resource bundle 
	 */
	public ExternalResourceBundle(String aPath,String aResBundlePrefix,Locale aLocale)
	{

		mPath = aPath;
		mResBundlePrefix = aResBundlePrefix;
		
		StringBuilder sb = new StringBuilder();
		sb.append(aResBundlePrefix);
		sb.append("_");
		sb.append(aLocale);
		sb.append(".properties");
		String fileName = sb.toString();
		
		mFullFileName = FileUtils.concatFilePath(aPath,fileName);
		mProperties = new Properties();
		try
		{
			mProperties.load(new FileInputStream(mFullFileName));
		}
		catch (Exception ex)
		{
			mProperties = null;
		}
		
		
		//we need to load the external bundle
//		try
//		{
//			//the location should end with a / so it will be counted
//			//by the class loader as a directory and not a JAR file
//			//so here we ensure that this is a directory
//			if (!mPath.endsWith("/"))
//			{
//				mPath = mPath + "/";;
//			}
//			
//			File bundlesDirectory = new File(mPath);
//			URL bundlesURL = bundlesDirectory.toURL();
//			
//			URL[] urlsForClassLoader = new URL[]{bundlesURL};
//			URLClassLoader bundleClassLoader = new URLClassLoader(urlsForClassLoader); 
//			mBundle = Utf8ResourceBundle.getBundle(mBaseFileName, aLocale, bundleClassLoader);
//		}
//		catch (MalformedURLException e)
//		{
//			e.printStackTrace();
//			//throw new LoadBundleException("the URL created from '" + mPath + "' is malformed: " + e.getMessage());
//			
//		}
//		catch (Throwable t)
//		{
//			t.printStackTrace();
//			//throw new LoadBundleException("error loading bundle from '" + mPath + "': " + t.getMessage());
//		}
	}
	
	/**
	 * Returns true is this resource bundle is valid and can be used
	 * @return
	 */
	public boolean isValid()
	{
		//return mBundle != null;
		return mProperties != null;
	}
	

	/**
	 * Gets a formatted value from the bundle
	 * @param aKey key of the value in bundle
	 * @param params parameters for formatting
	 * @return
	 * @throws Exception
	 */
	public String getValue(String aKey,Object... aParams) throws Exception
	{
//		String str = mBundle.getString(aKey);
//        
//        //mBundle.getSt
//        if (aParams == null || aParams.length == 0)
//        	return str;
//         
//        return String.format(mBundle.getLocale(), str, aParams);
        
		if (mPath == null)
		{
			throw new NullPointerException("Provided path to resource file is null");
		}
		
		if (mResBundlePrefix == null)
		{
			throw new NullPointerException("Provided base file name for resource files is null");
		}
		
		if (mProperties == null)
		{
			if (mFullFileName != null)
			{
				File f = new File(mFullFileName);
				if (!f.exists())
				{
					throw new FileNotFoundException("The properties file " + mFullFileName + " does not exist");
				}
			}
			throw new NullPointerException("Properties object is null");
		}
		
		String rawValue = mProperties.getProperty(aKey);
		
		if (rawValue == null)
		{
			return null;
		}

		byte[] bytes = rawValue.getBytes("ISO-8859-1");
		rawValue = new String(bytes,"UTF-8");
		
		
		return String.format(rawValue,aParams);
	}
	
	
	
	
//	public ExternalResourceBundle(String aPath,String aBaseFileName,Locale aLocale)
//	{
//		this(aPath,aBaseFileName,aLocale.toString());
//	}
	
	
	public ExternalResourceBundle(String aPath,String aBaseFileName)
	{
		this(aPath,aBaseFileName,Locale.getDefault());
	}
	
	
	
	
	public static void main(String[] args)
	{
		try
		{
			Locale l = new Locale("en","US","bla");
			System.out.println(l.getDefault());
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
