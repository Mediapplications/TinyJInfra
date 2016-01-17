/**
 * 
 */
package com.tinyj.infra.file;

import java.io.File;
import java.io.FilenameFilter;

/**
 * This filter can be used to return all files in directory
 * @author asaf.peeri
 *
 */
public class AllFilesFilter implements FilenameFilter
{

	/**
	 * 
	 */
	public AllFilesFilter()
	{
		// TODO Auto-generated constructor stub
	}

	public boolean accept(File aDir, String aName)
	{
		return true;
	}

}
