/**
 * 
 */
package com.tinyj.infra.structures;


/**
 * Generic for implementing a pair
 * @author asaf.peeri
 *
 * @param <First>
 * @param <Second>
 */
public class KeyValuePair<First extends Comparable,Second extends Comparable> implements Comparable {

	private First first;
	private Second second;
	
	
	public KeyValuePair(First first,Second second) {
		this.first = first;
		this.second = second;
	}
	
	public KeyValuePair() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	public void setFirst(First first) {
		this.first = first;
	}

	public First getFirst() {
		return first;
	}

	public void setSecond(Second second) {
		this.second = second;
	}

	public Second getSecond() {
		return second;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj)
	{
		KeyValuePair<First,Second> otherPair = (KeyValuePair<First,Second>)obj;
		return  first.equals(otherPair.getFirst()) && second.equals(otherPair.getSecond());

				
	}

	@SuppressWarnings("unchecked")
	public int compareTo(Object o) {
		
		Comparable firstComp = (Comparable)first;
		Comparable secondComp = (Comparable)second;
		
		KeyValuePair<First,Second> otherPair = (KeyValuePair<First,Second>)o;
		int res = firstComp.compareTo(otherPair.getFirst());
		if (res == 0)
			return secondComp.compareTo(otherPair.getSecond());
		
		return res;
		
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		sb.append(first.toString());
		sb.append(",");
		sb.append(second.toString());
		sb.append(")");
		return sb.toString();
	}
	
	public int hashCode()
	{
		
		StringBuilder sb = new StringBuilder();
		sb.append("#TINYJ#");
		sb.append(first.hashCode());
		sb.append("#TINYJ#");
		sb.append(second.hashCode());
		
		return sb.toString().hashCode();
	}
}

