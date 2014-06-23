package com.tinyj.infra.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.zip.ZipFile;

import com.tinyj.infra.config.ConfigurationManager;
import com.tinyj.infra.config.InfraConfigConstants;
import com.tinyj.infra.exception.FileDeleteErrorException;
import com.tinyj.infra.exception.FileLoadErrorException;
import com.tinyj.infra.exception.FileSaveErrorException;


/**
 * this class handles the save and load of temporary files.
 * it holds its own temp folder configuration, where all files will be saved flat (no hierarchical
 * structure).
 * 
 * the class is not responsible for the duplicate names of files, for cleaning up the temp
 * folder, or archiving old files. some operations, though, can be achieved using its API.
 * 
 * @author asaf.peeri
 *
 */
public class TempFilesManager
{
	protected static TempFilesManager sInstance;
	
	private String mTempFolderLocation;
	//private Map<String, TempFileDescriptor> mTemporaryFilesMap = new HashMap<String, TempFileDescriptor>();
	
	/**
	 * creates a TempFilesManager instance with the given temp folder location
	 */
	protected TempFilesManager(String aTempFolderLocation)
	{
		mTempFolderLocation = aTempFolderLocation;
	}
	
	
	/**
	 * gets the singleton instance of the TempFilesManager
	 * 
	 * @return the instance
	 */
	public static TempFilesManager getInstance()
	{
		if (sInstance == null)
		{
			synchronized(TempFilesManager.class)
			{
				if (sInstance == null)
				{
					sInstance = new TempFilesManager(ConfigurationManager.getParam(InfraConfigConstants.TEMP_FILES_MANAGER_FOLDER_LOCATION));
				}
			}
		}
		return sInstance;
	}
	
	
	/**
	 * converts the given file name to a short file name.
	 * a short file name is the actual file name without the folders location.
	 * for ex: <i>/home/mytemp/myfile.txt</i> will convert to <i>myfile.txt</i>
	 * 
	 * @param aFileName the original file name
	 * 
	 * @return the short file name
	 */
	public String getShortFileName(String aFileName)
	{
		//if the original file name is null, we return null
		if (aFileName == null)
		{
			return null;
		}
		
		int lastSeparatorIndex = aFileName.lastIndexOf(File.separator);
		
		//check if the short file name is not actually empty. if so: return null
		if (lastSeparatorIndex == aFileName.length() - 1)
		{
			return null;
		}
		
		//construct the short file name
		String shortFileName = aFileName.substring(lastSeparatorIndex+1);
		
		return shortFileName;
	}
	
	
	
	/**
	 * extracts the extension of the given file name.
	 * if the given file name is null, a null is returned. if the given file name does not have 
	 * an extension, an empty string is returned.
	 * 
	 * @param aFileName the original file name
	 * 
	 * @return the extension of the given file name
	 */
	public String getFileExtension(String aFileName)
	{
		//if the original file name is null, we return null
		if (aFileName == null)
		{
			return null;
		}
		
		int lastDotIndex = aFileName.lastIndexOf(".");
		
		//check if the extensionis not actually empty. if so: return an empty string
		if (lastDotIndex == aFileName.length() - 1)
		{
			return "";
		}
		
		//construct the file extension
		String fileExtension = aFileName.substring(lastDotIndex+1);
		
		return fileExtension;
	}
	
	
	/**
	 * Creates an output com.tinyj.infra.stream to a file with the given name in the temp file manager
	 * @param aFileName
	 * @return
	 * @throws FileSaveErrorException
	 */
	public OutputStream createOutputStreamForFile(String aFileName) throws FileSaveErrorException
	{
		String shortFileName = getShortFileName(aFileName);
		if (shortFileName == null)
		{
			throw new FileSaveErrorException("cannot save file with null or empty file name", 5002);
		}
		
		try
		{
			FileOutputStream fos = new FileOutputStream(mTempFolderLocation + "/" + shortFileName);
			return fos;
		}
		catch(FileNotFoundException fnfe)
		{
			throw new FileSaveErrorException(fnfe.getMessage(), 5000);
		}
		catch(IOException ioe)
		{
			throw new FileSaveErrorException(ioe.getMessage(), 5001);
		}
	
	}
	
	
	public ZipFile getZipFile(String aFileName) throws FileLoadErrorException
	{
		String shortFileName = getShortFileName(aFileName);
		if (shortFileName == null)
		{
			throw new FileLoadErrorException("cannot create zip file with null or empty file name", 5002);
		}
		
		try
		{
			return new ZipFile(mTempFolderLocation + "/" + aFileName);
		}
		catch(FileNotFoundException fnfe)
		{
			throw new FileLoadErrorException(fnfe.getMessage(), 5000);
		}
		catch(IOException ioe)
		{
			throw new FileLoadErrorException(ioe.getMessage(), 5001);
		}
	
	}
 
	
	/**
	 * saves the given data InputStream onto the TempFilesManager's temp folder, with the given
	 * file name. if the given file name contains folders information, it is converted into
	 * short file name format.
	 *  
	 * @param aFileName the file name to be saved
	 * @param aDataToSave the data InputStream of the file to save
	 * 
	 * @return the file name that was actually saved. this should be used by the caller in order
	 * to retrieve the file at a later time.
	 * 
	 * @throws FileSaveErrorException when an error occurs during the save operation
	 */
	public String saveTemporaryFile(String aFileName, InputStream aDataToSave)
		throws FileSaveErrorException
	{
		String shortFileName = getShortFileName(aFileName);
		if (shortFileName == null)
		{
			throw new FileSaveErrorException("cannot save file with null or empty file name", 5002);
		}
		
		try
		{
			FileUtils.writeFile(mTempFolderLocation + "/" + shortFileName, aDataToSave);
		}
		catch(FileNotFoundException fnfe)
		{
			throw new FileSaveErrorException(fnfe.getMessage(), 5000);
		}
		catch(IOException ioe)
		{
			throw new FileSaveErrorException(ioe.getMessage(), 5001);
		}
		
		//after save is done successfully, return the name of the temporary file
		return shortFileName;
	}
	
	
	/**
	 * loads a file according to the given file name. if the file name is not in short name
	 * mode, it is converted into short mode and only then loads.
	 * 
	 * @param aFileName the file name to load
	 * 
	 * @return the InputStream of the loaded file. null if the file name is null
	 * 
	 * @throws FileLoadErrorException if the given file name does not exist
	 */
	public InputStream loadTemporaryFile(String aFileName)
		throws FileLoadErrorException
	{
		if (aFileName == null)
		{
			return null;
		}
		else
		{
			String shortFileName = getShortFileName(aFileName);
			File file = new File(mTempFolderLocation + "/" + shortFileName);
			if (!file.exists())
			{
				throw new FileLoadErrorException("file not found: " + shortFileName, 5002);
			}
			
			FileInputStream fis = null;
			try
			{
				fis = new FileInputStream(file);
			}
			catch(FileNotFoundException fnfe)
			{
				throw new FileLoadErrorException("file not found: " + shortFileName, 5002);
			}
			
			return fis;
		}
	}
	
	
	public URI getURI(String aShortFileName)
	{
		File file = new File(mTempFolderLocation + "/" + aShortFileName);
		return file.toURI();
	}

	
	
	
	/**
	 * deletes a file according to the given file name
	 * 
	 * @param aFileName the file name to delete
	 * 
	 * @throws FileDeleteErrorException when the file does not exist or if an unknown error
	 * occured while trying to delete it
	 */
	public void deleteTemporaryFile(String aFileName)
		throws FileDeleteErrorException
	{
		if (aFileName == null)
		{
			return;
		}
		else
		{
			String shortFileName = getShortFileName(aFileName);
			File file = new File(mTempFolderLocation + "/" + shortFileName);
			if (!file.exists())
			{
				throw new FileDeleteErrorException("file not found: " + shortFileName, 5002);
			}
			
			boolean status = file.delete();
			
			if (status == false)
			{
				throw new FileDeleteErrorException("file could not be deleted for an unknown reason: " + shortFileName, 5003);
			}
			
			return;
		}
	}
	
	
	/**
	 * deletes the whole content of the temp directory
	 * 
	 * @throws FileDeleteErrorException where any error occured during deletion
	 */
	public void cleanTempDirectory()
		throws FileDeleteErrorException
	{
		File tempDirectory = new File(mTempFolderLocation);
		if (FileUtils.deleteDirectoryContent(tempDirectory) == false)
		{
			throw new FileDeleteErrorException("temp directory contents were not deleted completely for an unknown reason", 5003);
		}
	}
	
	
	
	
}
