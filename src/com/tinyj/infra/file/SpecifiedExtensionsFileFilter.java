/**
 * 
 */
package com.tinyj.infra.file;

import java.io.File;
import java.io.FilenameFilter;

/**
 * This file name filter returns all files that match the given
 * list of file extensions. For example, it is possible to implement
 * a file filter for images using:
 * SpecifiedExtensionFileFilter("jpg","bmp","gif","png")
 * @author asaf.peeri
 *
 */
public class SpecifiedExtensionsFileFilter implements FilenameFilter
{

	private String[] mFileExtensions;
	/**
	 * 
	 */
	public SpecifiedExtensionsFileFilter(String... aFileExtensions)
	{
		mFileExtensions = aFileExtensions;
	}

	public boolean accept(File aDir, String aName)
	{
		String extension = FileUtils.getFileExtension(aName);
		for (String extensionToCompare:mFileExtensions)
		{
			if (extension.equals(extensionToCompare))
			{
				return true;
			}
		}
		return false;
	}

}
