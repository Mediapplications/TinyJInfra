/**
 * 
 */
package com.tinyj.infra.stream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

import com.tinyj.infra.exception.HTTPStatusException;

/**
 * Class that implements com.tinyj.infra.stream utils
 * @author asaf.peeri
 *
 */
public class StreamUtils
{
	/**
	 * Reads entire input com.tinyj.infra.stream content and writes it to the given output com.tinyj.infra.stream
	 * @param aInputStream input com.tinyj.infra.stream to read from
	 * @param aOutputStream output com.tinyj.infra.stream to write to
	 * @throws IOException
	 */
	public static void inputStreamToOutputStream(InputStream aInputStream, OutputStream aOutputStream) throws IOException
	{
		int chunkSize = 8196;
		byte[] buffer = new byte[chunkSize];
		
		int read = chunkSize;
		
		while (read > -1 )
		{
			read  = aInputStream.read(buffer);
			
			//if something was read - write the data to the output com.tinyj.infra.stream
			if (read > 0)
			{
				aOutputStream.write(buffer,0,read);
			}
		}
	}
	
	
	/**
	 * Reads a com.tinyj.infra.stream and creates a string from the read bytes
	 * @param aInputStream com.tinyj.infra.stream to read
	 * @param aCharsetEncoding charset encoding to create the string 
	 * @return
	 * @throws IOException
	 */
	public static String inputStreamToString(InputStream aInputStream,String aCharsetEncoding) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		inputStreamToOutputStream(aInputStream,baos);
		byte[] buff = baos.toByteArray();
		String str = new String(buff,aCharsetEncoding);
		return str;
	}
	
	/**
	 * Gets an input com.tinyj.infra.stream that enables reading the HTTP response from the given HTTP request
	 * @param aURL URL of HTTP request
	 * @param aTimeout timeout in miliseconds
	 * @param aFollowRedirects true whether to follow http redirects, false otherwise
	 * @return
	 * @throws HTTPStatusException
	 * @throws HttpException
	 * @throws IOException
	 */
	public static InputStream getStreamFromURL(String aURL, int aTimeout, boolean aFollowRedirects, boolean aReturnResponseOnlyFor200Code) throws HTTPStatusException, HttpException, IOException
	{
		HttpClient client = new HttpClient();
		
	
		client.setTimeout(aTimeout);
		GetMethod getMethod = new GetMethod(aURL);
		
		getMethod.setFollowRedirects(aFollowRedirects);
		
		client.executeMethod(getMethod);
		

		int responseCode = getMethod.getStatusCode();
				
		if (responseCode == 200)
		{
			return getMethod.getResponseBodyAsStream();
		}
		
		
		if (aReturnResponseOnlyFor200Code)
		{
			getMethod.releaseConnection();
			throw new HTTPStatusException("HTTP status is not OK",responseCode);
		}
		else
		{
			return getMethod.getResponseBodyAsStream();
		}
	}
	
	
	
	/**
	 * Gets an input com.tinyj.infra.stream that enables reading the HTTP response from the given HTTP request
	 * the "follow redirects" behavior for this methos is always true
	 * @param aURL URL of HTTP request
	 * @param aTimeout timeout in miliseconds
	 * @return
	 * @throws HTTPStatusException
	 * @throws HttpException
	 * @throws IOException
	 */
	public static InputStream getStreamFromURL(String aURL, int aTimeout) throws HTTPStatusException, HttpException, IOException
	{
		return getStreamFromURL(aURL, aTimeout, true, true);
	}
}
