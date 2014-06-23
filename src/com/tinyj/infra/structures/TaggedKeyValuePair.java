package com.tinyj.infra.structures;

/**
 * Key value pair with tagging that is provided to give meaningful names
 * for "first" and "second"
 * @author asaf.peeri
 *
 * @param <First>
 * @param <Second>
 */
public class TaggedKeyValuePair<First extends Comparable,Second extends Comparable> extends KeyValuePair<First,Second>
{
	private String mTagFirst;
	private String mTagSecond;
	
	public TaggedKeyValuePair(First first,Second second,String aTagFirst,String aTagSecond)
	{
		super(first,second);
		mTagFirst = aTagFirst;
		mTagSecond = aTagSecond;
	}
	
	public Object get(String aTag)
	{
		if (aTag.equals(mTagFirst))
		{
			return getFirst();
		}
		if (aTag.equals(mTagSecond))
		{
			return getSecond();
		}
		
		return null;
	}
	
	
}
