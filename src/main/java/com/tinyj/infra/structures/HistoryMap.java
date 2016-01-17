package com.tinyj.infra.structures;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Abstract generic class to hold a map that has history
 * on the insertions to it.
 * It is possible to extract from the map entries that were inserted 
 * before/after given time, to extract the oldest entry,and to extract the newest entry
 * @author asaf.peeri
 *
 * @param <K>
 * @param <V>
 */
public abstract class HistoryMap<K,V> implements Map<K,V>
{
	
	protected ArrayList<HistoryEntry<K,V>> mHistoryEntries = new ArrayList<HistoryEntry<K,V>>();
	protected Map<K,HistoryEntry<K,V>> mHistoryEntriesByKey;
	protected Map<K,V> mMap;
	
	/**
	 * Creates the internal map
	 * @return the niternal map
	 */
	protected  abstract Map<K,V> createMap();
	
	protected  abstract Map<K,HistoryEntry<K,V>> createHistoryByKeysMap();

	
	public HistoryMap()
	{
		mMap = createMap();
		mHistoryEntriesByKey = createHistoryByKeysMap();
		
	}
	
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		
		int counter = 0;
		for (HistoryEntry<K, V> entry:mHistoryEntries)
		{
			if (counter > 1)
			{
				sb.append(",");
			}
			sb.append(entry.toString());
		}
		sb.append(")");
		
		return sb.toString();
	}
	
	
	/////////HistoryEntry class ////////////////////
	public static class HistoryEntry<K,V>
	{
		private long mEntryTime;
		private V mValue;
		private K mKey;
		
		public HistoryEntry(long aEntryTime,K aKey,V aValue)
		{
			setEntryTime(aEntryTime);
			setValue(aValue);
			setKey(aKey);
		}
		
		
		
		
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append("[time:" );
			sb.append(new Timestamp(getEntryTime()));
			sb.append(",key:"+getKey());
			sb.append(",val:"+getValue());
			sb.append("]");
			
			return sb.toString();
		}

		public void setEntryTime(long entryTime)
		{
			mEntryTime = entryTime;
		}

		public long getEntryTime()
		{
			return mEntryTime;
		}

		public void setValue(V value)
		{
			mValue = value;
		}

		public V getValue()
		{
			return mValue;
		}

		public void setKey(K key)
		{
			mKey = key;
		}

		public K getKey()
		{
			return mKey;
		}
	}
	
	//////////End of HistoryEntry class ///////////////


	
	public HistoryEntry<K,V> getHistoryEntry(K aKey)
	{
		return mHistoryEntriesByKey.get(aKey);
	}


	public void clear()
	{
		mHistoryEntries.clear();
		mMap.clear();
	}

	public boolean containsKey(Object aKey)
	{
		return mMap.containsKey(aKey);
	}

	public boolean containsValue(Object aValue)
	{
		return mMap.containsValue(aValue);
	}

	public Set<java.util.Map.Entry<K, V>> entrySet()
	{
		return mMap.entrySet();
	}

	public V get(Object aKey)
	{
		return mMap.get(aKey);
	}

	public boolean isEmpty()
	{
		return mMap.isEmpty();
	}

	public Set<K> keySet()
	{
		return mMap.keySet();
	}

	public V put(K aKey, V aValue)
	{
		V oldValue = mMap.put(aKey,aValue);
		HistoryEntry<K,V> obj = new HistoryEntry<K,V>(System.currentTimeMillis(),aKey,aValue);
		mHistoryEntries.add(obj);
		
		mHistoryEntriesByKey.put(aKey,obj);
		return oldValue;
		
	}

	public void putAll(Map<? extends K, ? extends V> aMap)
	{
		Set<?> mapEntries = aMap.entrySet();
		for (Object mapEntryObj:mapEntries)
		{
			Entry<K,V> entry = (Entry<K,V>)mapEntryObj;
			K key = entry.getKey();
			V value = entry.getValue();
			put(key,value);
		}
		
	}

	public V remove(Object aKey)
	{
		V oldValue = mMap.remove(aKey);
		if (oldValue == null)
		{
			return null;
		}
		
		int counter = 0;
		for (HistoryEntry<K,V> aEntry:mHistoryEntries)
		{
			if (aEntry.getKey().equals(aKey))
			{
				break;
			}
			counter++;
		}
		
		mHistoryEntries.remove(counter);
		mHistoryEntriesByKey.remove(aKey);
		
		return oldValue;
	}

	public int size()
	{
		return mMap.size(); 
	}

	public Collection<V> values()
	{
		return mMap.values();
	}
	
	
	public V getOldest()
	{
		if (isEmpty())
		{
			return null;
		}
		
		return mHistoryEntries.get(0).getValue();
	}
	
	public V getLatest()
	{
		if (isEmpty())
		{
			return null;
		}
		
		return mHistoryEntries.get(mHistoryEntries.size()-1).getValue();
	}
	
	
	public List<HistoryEntry<K,V>> getEntriesBeforeInclusive(java.sql.Date aTime)
	{
		return getEntriesBefore(aTime.getTime());
	}
	
	public List<HistoryEntry<K,V>> getEntriesAfterInclusive(java.sql.Date aTime)
	{
		return getEntriesAfterInclusive(aTime.getTime());
	}
	
	public List<HistoryEntry<K,V>> getEntriesAfter(java.sql.Date aTime)
	{
		return getEntriesAfter(aTime.getTime());
	}
	
	public List<HistoryEntry<K, V>> getEntriesBefore(java.sql.Date aTime)
	{
		return getEntriesBefore(aTime.getTime());
	}

	
	
	public List<HistoryEntry<K,V>> getEntriesBeforeInclusive(Date aTime)
	{
		return getEntriesBefore(aTime.getTime());
	}
	
	public List<HistoryEntry<K,V>> getEntriesAfterInclusive(Date aTime)
	{
		return getEntriesAfterInclusive(aTime.getTime());
	}
	
	public List<HistoryEntry<K,V>> getEntriesAfter(Date aTime)
	{
		return getEntriesAfter(aTime.getTime());
	}
	
	public List<HistoryEntry<K, V>> getEntriesBefore(Date aTime)
	{
		return getEntriesBefore(aTime.getTime());
	}

	
	
	
	public List<HistoryEntry<K,V>> getEntriesBeforeInclusive(Timestamp aTime)
	{
		return getEntriesBefore(aTime.getTime());
	}
	
	public List<HistoryEntry<K,V>> getEntriesAfterInclusive(Timestamp aTime)
	{
		return getEntriesAfterInclusive(aTime.getTime());
	}
	
	public List<HistoryEntry<K,V>> getEntriesAfter(Timestamp aTime)
	{
		return getEntriesAfter(aTime.getTime());
	}
	
	public List<HistoryEntry<K, V>> getEntriesBefore(Timestamp aTime)
	{
		return getEntriesBefore(aTime.getTime());
	}

	

	
	public List<HistoryEntry<K,V>> getEntriesBeforeInclusive(long aTime)
	{
		List<HistoryEntry<K, V>> results = new ArrayList<HistoryEntry<K,V>>();
		for (HistoryEntry<K, V> historyEntry:mHistoryEntries)
		{
			if (historyEntry.getEntryTime() <= aTime)
			{
				results.add(historyEntry);
			}
		}
		return results;
	}
	
	public List<HistoryEntry<K,V>> getEntriesAfterInclusive(long aTime)
	{
		List<HistoryEntry<K, V>> results = new ArrayList<HistoryEntry<K,V>>();
		for (HistoryEntry<K, V> historyEntry:mHistoryEntries)
		{
			if (historyEntry.getEntryTime() >= aTime)
			{
				results.add(historyEntry);
			}
		}
		return results;

	}
	
	public List<HistoryEntry<K,V>> getEntriesAfter(long aTime)
	{
		List<HistoryEntry<K, V>> results = new ArrayList<HistoryEntry<K,V>>();
		for (HistoryEntry<K, V> historyEntry:mHistoryEntries)
		{
			if (historyEntry.getEntryTime() > aTime)
			{
				results.add(historyEntry);
			}
		}
		return results;

	}
	
	

	
	public List<HistoryEntry<K, V>> getEntriesBefore(long aTime)
	{
		List<HistoryEntry<K, V>> results = new ArrayList<HistoryEntry<K,V>>();
		for (HistoryEntry<K, V> historyEntry:mHistoryEntries)
		{
			if (historyEntry.getEntryTime() < aTime)
			{
				results.add(historyEntry);
			}
		}
		return results;

	}
	
	
	public void removeOldest()
	{
		if (mMap.size() == 0)
		{
			return;
		}
		
		HistoryEntry<K, V> entry = mHistoryEntries.get(0);
		mHistoryEntries.remove(0);
		mMap.remove(entry.getKey());
	}
	
	public void removeLatest()
	{
		if (mMap.size() == 0)
		{
			return;
		}
		
		int index = mMap.size()-1;
			 
		HistoryEntry<K, V> entry = mHistoryEntries.get(index);
		mHistoryEntries.remove(0);
		mMap.remove(entry.getKey());
	}
	
	
	public void removeBefore(java.sql.Date aTime)
	{
		removeBefore(aTime.getTime());
	}
	
	public void removeBeforeInclusive(java.sql.Date aTime)
	{
		removeBeforeInclusive(aTime.getTime());
	}
	
	public void removeAfter(java.sql.Date aTime)
	{
		removeAfter(aTime.getTime());
	}
	
	public void removeAfterInclusive(java.sql.Date aTime)
	{
		removeAfterInclusive(aTime.getTime());
	}
	
	

	public void removeBefore(Date aTime)
	{
		removeBefore(aTime.getTime());
	}
	
	public void removeBeforeInclusive(Date aTime)
	{
		removeBeforeInclusive(aTime.getTime());
	}
	
	public void removeAfter(Date aTime)
	{
		removeAfter(aTime.getTime());
	}
	
	public void removeAfterInclusive(Date aTime)
	{
		removeAfterInclusive(aTime.getTime());
	}
	
	
	public void removeBefore(Timestamp aTime)
	{
		removeBefore(aTime.getTime());
	}
	
	public void removeBeforeInclusive(Timestamp aTime)
	{
		removeBeforeInclusive(aTime.getTime());
	}
	
	public void removeAfter(Timestamp aTime)
	{
		removeAfter(aTime.getTime());
	}
	
	public void removeAfterInclusive(Timestamp aTime)
	{
		removeAfterInclusive(aTime.getTime());
	}
	
	


	
	public void removeBefore(long aTime)
	{
		if (mMap.size() == 0)
		{
			return;
		}
		
		ArrayList<HistoryEntry<K, V>> toRemove = new ArrayList<HistoryEntry<K,V>>();
		
		for (HistoryEntry<K,V> entry:mHistoryEntries)
		{
			if (entry.getEntryTime() < aTime)
			{
				toRemove.add(entry);
			}
		}
		
		removeEntries(toRemove);
	}
	
	public void removeBeforeInclusive(long aTime)
	{
		if (mMap.size() == 0)
		{
			return;
		}
		
		ArrayList<HistoryEntry<K, V>> toRemove = new ArrayList<HistoryEntry<K,V>>();
		
		for (HistoryEntry<K,V> entry:mHistoryEntries)
		{
			if (entry.getEntryTime() <= aTime)
			{
				toRemove.add(entry);
			}
		}
		
		removeEntries(toRemove);
	}
	

	public void removeAfter(long aTime)
	{
		if (mMap.size() == 0)
		{
			return;
		}
		
		ArrayList<HistoryEntry<K, V>> toRemove = new ArrayList<HistoryEntry<K,V>>();
		
		for (HistoryEntry<K,V> entry:mHistoryEntries)
		{
			if (entry.getEntryTime() > aTime)
			{
				toRemove.add(entry);
			}
		}
		
		removeEntries(toRemove);
	}
	
	public void removeAfterInclusive(long aTime)
	{
		if (mMap.size() == 0)
		{
			return;
		}
		
		ArrayList<HistoryEntry<K, V>> toRemove = new ArrayList<HistoryEntry<K,V>>();
		
		for (HistoryEntry<K,V> entry:mHistoryEntries)
		{
			if (entry.getEntryTime() >= aTime)
			{
				toRemove.add(entry);
			}
		}
		removeEntries(toRemove);
	}
	
	protected void removeEntries(ArrayList<HistoryEntry<K, V>> aToRemove)
	{
		
		for (HistoryEntry<K, V> aEntry:aToRemove)
		{
			mHistoryEntries.remove(aEntry);
			mMap.remove(aEntry.getKey());
			mHistoryEntriesByKey.remove(aEntry.getKey());
		}
	}

	
}
