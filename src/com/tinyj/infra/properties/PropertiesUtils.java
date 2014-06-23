package com.tinyj.infra.properties;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Properties;
import java.util.StringTokenizer;

import com.tinyj.infra.stream.StreamUtils;

/**
 * Helper methods for handling properties and property files
 * @author asaf.peeri
 *
 */
public class PropertiesUtils
{
	
	//this method implementation didn't work well
	//it was replaced by the "loadProperties" method below
	
//	public static Properties readProperties(InputStream aInputStream,String aEncoding) throws Exception
//	{
//		Properties props = new Properties();
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		StreamUtils.inputStreamToOutputStream(aInputStream, baos);
//		String str = new String(baos.toByteArray(),aEncoding);
//		StringReader reader = new StringReader(str);
//		StringTokenizer tokenzier = new StringTokenizer(str,"\r\n");
//		int firstEqualsCharIndex= -1;
//		while (tokenzier.hasMoreElements())
//		{
//			String line = (String)tokenzier.nextElement();
//			//String[] pair = line.split("=");
//			
//			if (line.startsWith("#"))
//			{
//				//this is a comment line, so skip over it
//				continue;
//			}
//			
//			firstEqualsCharIndex = line.indexOf("=");
//			if (firstEqualsCharIndex == -1)
//			{
//				throw new Exception("Failed to read properties from stream. The stream does not contain properties format. No equals (=) char was found in line: " + line);
//			}
//			
//			String[] pair = new String[2];
//			pair[0] = line.substring(0, firstEqualsCharIndex);
//			pair[1] = line.substring(firstEqualsCharIndex+1, line.length());
//			
//			//check the key is not empty
//			if (pair[0] != null && pair[0].trim().equals(""))
//			{
//				throw new Exception("Failed to read properties from stream. The stream does not contain properties format. key is empty in line: " + line);
//			}
//			
////			if (pair.length < 1 || pair.length > 2)
////			{
////				throw new Exception("Failed to read properties from stream. The stream does not contain properties format");
////			}
//			
//			String key = pair[0];
//			String value = "";
//			if (pair.length == 2)
//			{
//				value = pair[1];
//			}
//			props.put(key,value);
//		}
//		
//		return props;
//	}
	
	
	
	/**
	 * this method loads the properties file in the specified encoding
	 * as Java's Properties.load() method can only read properties files in ISO-8859-1 encoding
	 * this method can read the properties file in any desired encoding
	 * 
	 * @param is the inputStream of the properties file to load
	 * @param encoding the encoding of which the properties file should be read
	 */
   public static Properties loadProperties(InputStream is, String encoding) 
   	throws IOException
   {
      StringBuilder sb = new StringBuilder();
      InputStreamReader isr = new InputStreamReader(is, encoding);
      while(true)
      {
         int temp = isr.read();
         if(temp < 0)
            break;

         char c = (char) temp;
         sb.append(c);
      }

      String inputString = escapifyStr(sb.toString());
      byte[] bs = inputString.getBytes("ISO-8859-1");
      ByteArrayInputStream bais = new ByteArrayInputStream(bs);

      Properties ps = new Properties();
      ps.load(bais);
      return ps;
   }
      
   private static char hexDigit(char ch, int offset)
   {
      int val = (ch >> offset) & 0xF;
      if(val <= 9)
         return (char) ('0' + val);
      
      return (char) ('A' + val - 10);
   }

   
   private static String escapifyStr(String str)
   {      
      StringBuilder result = new StringBuilder();

      int len = str.length();
      for(int x = 0; x < len; x++)
      {
         char ch = str.charAt(x);
         if(ch <= 0x007e)
         {
            result.append(ch);
            continue;
         }
         
         result.append('\\');
         result.append('u');
         result.append(hexDigit(ch, 12));
         result.append(hexDigit(ch, 8));
         result.append(hexDigit(ch, 4));
         result.append(hexDigit(ch, 0));
      }
      return result.toString();
   }
	
	
	public static void main(String[] args)
	{
		try
		{
			File f = new File("/home/john/public2/employees/smith/Resource_iw_IL.properties");
			FileInputStream fis = new FileInputStream(f);
			//PropertiesUtils.readProperties(fis, "UTF-8");
			PropertiesUtils.loadProperties(fis, "UTF-8");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
}
