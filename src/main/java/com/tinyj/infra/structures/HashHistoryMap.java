/**
 * 
 */
package com.tinyj.infra.structures;

import java.util.HashMap;
import java.util.Map;

/**
 * HashMap based implementation of HistoryMap
 * @author asaf.peeri
 *
 */
public class HashHistoryMap<K,V> extends HistoryMap<K,V>
{

	@Override
	protected Map<K, V> createMap()
	{
		return new HashMap<K, V>();
	}

	@Override
	protected Map<K,HistoryEntry<K, V>> createHistoryByKeysMap()
	{
		return new HashMap<K, HistoryEntry<K,V>>();
	}
	
	
	
}
