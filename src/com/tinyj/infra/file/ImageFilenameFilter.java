/**
 * 
 */
package com.tinyj.infra.file;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author asaf.peeri
 *
 */
public class ImageFilenameFilter implements FilenameFilter
{

	/**
	 * 
	 */
	public ImageFilenameFilter()
	{
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	public boolean accept(File aDir, String aName)
	{
		if (aName == null)
		{
			return false;
		}
		String extension = FileUtils.getFileExtension(aName);
		if (extension == null)
		{
			return false;
		}
		return extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg");
	}

}
