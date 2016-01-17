/**
 * 
 */
package com.tinyj.infra.structures;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author asaf.peeri
 *
 */
public class ArrayUtils
{

	
	
	/**
	 * 
	 */
	public ArrayUtils()
	{
		
	}
	
	public static <T> List<T> toList(T[] arr)
	{
		if (arr == null)
		{
			return null;
		}
		
		ArrayList<T> results = new ArrayList<T>();
		for (T entry:arr)
		{
			results.add(entry);
		}
		
		return results;
	}
	
	public static <T> T[] toArray(Class<T> cls,Collection<T> col)
	{
		T[] arr = null;
	
		if (col == null)
		{
			return null;
		}
		arr = (T[])Array.newInstance(cls,col.size());
		int counter = 0;
		for (T t:col)
		{
			arr[counter] = t;
			counter++;

		}
		return arr;
	
	}
	
	
	
	

}
