package com.tinyj.infra.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.tinyj.infra.stream.StreamUtils;

public class FileUtils
{
	/**
	 * writes a file according to the given name, path and data.
	 * 
	 * @param aFileName the file name and path
	 * @param aFileData the data of the file
	 * 
	 * @throws IOException when any error occurs during write
	 */
	public static void writeFile(String aFullFileName, InputStream aFileData) 
		throws IOException
	{
		FileOutputStream fos = null;
		byte[] buffer = new byte[8096];
		int bytesRead = -1;
		
		try
		{
			fos = new FileOutputStream(aFullFileName);
			
			while ( (bytesRead = aFileData.read(buffer)) != -1)
			{
				fos.write(buffer, 0, bytesRead);
				fos.flush();
			}
		}
		catch (FileNotFoundException fnfe)
		{
			System.out.println("Error occured while saving file. File not found: " + aFullFileName + ": " + fnfe.toString());
			throw fnfe;
		}
		catch (IOException ioe)
		{
			System.out.println("Error occured while saving file: " + aFullFileName + ": " + ioe.toString());
			throw ioe;
		}
		finally
		{
			try
			{
				fos.close();
			}
			catch(IOException ioe)
			{
				//failed to close file
				System.out.println("Error occured trying to close output com.tinyj.infra.stream. ignoring...");
				ioe.printStackTrace();
			}
		}
	}
	
	
	/**
	 * writes a file according to the given name, path and data.
	 * 
	 * @param aFullFileName the file name and path
	 * @param aFileData the data bytes of the file to be written
	 * 
	 * @throws IOException when any error occurs during write
	 */
	public static void writeFile(String aFullFileName, byte[] aFileData)
		throws IOException
	{
		ByteArrayInputStream bais = new ByteArrayInputStream(aFileData);
		writeFile(aFullFileName, bais);
	}
	
	
	public static String readFileToString(String aFullName,String aEncoding) throws Exception
	{
		
		byte[] buffer = readFileToBuffer(aFullName);
		if (buffer == null)
		{
			return null;
		}
		
		return new String(buffer,aEncoding);
	}
	
	public static byte[] readFileToBuffer(String aFullName) throws Exception
	{
		FileInputStream fis = null;
		
		try
		{
			if (aFullName == null)
			{
				throw new Exception("File name cannot be null!");
			}
			
			fis = new FileInputStream(aFullName);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			StreamUtils.inputStreamToOutputStream(fis,baos);
			return baos.toByteArray();
		}
		catch (Exception ex)
		{
		  throw ex;	
		}
		finally
		{
			if (fis != null)
			{
				fis.close();
			}
		}
		
	}
	
	
	
	/**
	 * Reads the contents of the given file to an input com.tinyj.infra.stream
	 * @param aFulleName
	 * @return
	 * @throws Exception
	 */
	public static InputStream readFile(String aFullName) throws Exception
	{
		
		try
		{

			byte[] buff = readFileToBuffer(aFullName);
			if (buff == null)
			{
				return null;
			}
			ByteArrayInputStream bais = new ByteArrayInputStream(buff);
			
			return bais;
		}
		catch (Exception ex)
		{
		  throw ex;	
		}
		finally
		{
		}
		
	}
	
	/**
	 * deletes a whole directory content.
	 * 
	 * @param path a File representing the path of the folder to delete its content
	 * 
	 * @return true if all was completed successfully, false otherwise
	 */
	public static boolean deleteDirectoryContent(File path)
	{
		boolean deletePerfect = true;
		
		if (path.exists())
		{
			boolean status = false;
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++)
			{
				if (files[i].isDirectory())
				{
					deleteDirectoryContent(files[i]);
				}
				
				status = files[i].delete();
				if (status == false)
				{
					deletePerfect = false;
				}
			}
		}
		
		return deletePerfect;
	}
	
	
	/**
	 * Concatenates a path of file from file paths.
	 * for example: <i>"home","/john","/work","myfiles"</i> will be concatenated to <i>"/home/john/work/myfiles"<i>
	 * @param aPathElements elements to concatenate
	 * @return
	 */
	public static String concatFilePath(String ...aPathElements)
	{
		String osName = System.getProperty("os.name");
		//String osName="Windows";
		if (osName.indexOf("Linux") != -1)
			return concactFilePathForLinux(aPathElements);
		
		return concactFilePathForWindows(aPathElements).replace("//", "/");
			
	}


	private static String concactFilePathForWindows(String[] aPathElements)
	{
		if (aPathElements == null)
			return null;
		
		if (aPathElements.length == 0)
			return "";
		
		
		
		String adjustedPathElement = adjustFirstPathForWindows(aPathElements[0]);
		
		StringBuilder result = new StringBuilder();
		result.append(adjustedPathElement);
		for (int counter = 1; counter < aPathElements.length; counter++)
		{
			String pathElement = aPathElements[counter];
			pathElement = pathElement.replace('\\', '/');
			if (pathElement.charAt(0) != '/')
				result.append('/');
			
			result.append(pathElement);
		}
	
		return result.toString().replace("//", "/");
	}


	/**
	 * This method modifies the correct representation of the first element
	 * path in windows which may be a drive letter path (for example: <i> c:\ </i>
	 * @param aPathElement
	 * @return
	 */
	private static String adjustFirstPathForWindows(String aPathElement)
	{
		
		
		aPathElement = aPathElement.replace("\\","/");
		String modifiedELement = aPathElement;
		if (modifiedELement.indexOf('/') != 0)
				modifiedELement = '/' + modifiedELement; 
		
		int pathLength = aPathElement.length();
		if (pathLength < 2)
			return modifiedELement;
		
		char firstChar = aPathElement.charAt(0);
		if (!Character.isLetter(firstChar))
			return modifiedELement;
		
		char secondChar = aPathElement.charAt(1);
		if (secondChar != ':')
			return modifiedELement;
		
		/*
		if (pathLength == 2)
			return aPathElement;
		
		if (aPathElement.charAt(2) == '/' && pathLength == 3)
			return aPathElement.substring(0,2);
			*/
		
		return aPathElement;
	}
	
	
	public static String getShortFileName(String aFileName)
	{
		File f = new File(aFileName);
		return f.getName();
		
	}
	
	
	public static String getFileNameWithNoExtension(String aFileName)
	{
		int lastDotIndex = aFileName.lastIndexOf(".");
		//If there is no . in the file name
		if (lastDotIndex == -1 )
		{
			return aFileName;
		}
		
		return aFileName.substring(0,lastDotIndex);
	}

	
	public static String getFileExtension(String aFileName)
	{
		
		int lastDotIndex = aFileName.lastIndexOf(".");
		//If there is no . in the file name
		if (lastDotIndex == -1 )
		{
			return null;
		}
		
		if (lastDotIndex == aFileName.length()-1)
		{
			return "";
		}
		
		return aFileName.substring(lastDotIndex+1);
	}

	private static String concactFilePathForLinux(String[] aPathElements)
	{
		if (aPathElements == null)
			return null;
		
		if (aPathElements.length == 0)
			return "";
		
		
		
		StringBuilder sb = new StringBuilder();
		
		boolean addSlashAtBeginning = false;
		for (String pathElement:aPathElements)
		{
			pathElement = pathElement.replace('\\','/');
			if (pathElement.charAt(0) != '/' && addSlashAtBeginning)
				sb.append('/');
			if (!pathElement.endsWith("/"))
			{
				addSlashAtBeginning = true;
			}
			
			sb.append(pathElement);
		}
		
		return sb.toString();

	}
	
	
	/**
	 * Replaces the file extension of a given file to the given extension
	 * @param aFileName
	 * @param aNewExtension
	 * @return
	 * @throws Exception
	 */
	public static String replaceExtension(String aFileName,String aNewExtension) throws Exception
	{
		
		if (aFileName == null)
		{
			throw new NullPointerException("The file name is null");
		}
		
		if (aNewExtension == null)
		{
			throw new NullPointerException("The extension to replace with is null");
		}
		
		int lastDotIndex = aFileName.lastIndexOf(".");
		if (lastDotIndex == -1)
		{
			return aFileName + "." + aNewExtension;
		}
		
		String fileNoExtension = aFileName.substring(0,lastDotIndex);
		
		return fileNoExtension + "." + aNewExtension;
	}
	
	
	
	
	
}
