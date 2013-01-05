package gomoku.util;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * A hashmap that uses two keys for each value. The implementation utilizes two
 * HashMaps where the first key is used to locate a specific HashMap where the
 * other key is used to locate the value.
 *
 * @author Samuel Andersson
 *
 * @param <K1>
 *            The first key
 * @param <K2>
 *            The second key
 * @param <V>
 *            The value
 */
public class HashMap2D<K1 extends Object, K2 extends Object, V extends Object> {

	/** The underlying hashmap structure */
	private HashMap<K1, HashMap<K2, V>> hashmap;

	/**
	 * Create a new HashMap2D
	 */
	public HashMap2D() {
		clear();
	}

	/**
	 * Associates the specified value with the specified keys in this map. If
	 * the map previously contained a mapping for the keys, the old value is
	 * replaced.
	 *
	 * @param k1
	 *            first key with which the specified value is to be associated
	 * @param k2
	 *            second key with which the specified value is to be associated
	 * @param value
	 *            value to be associated with the specified keys
	 */
	public void put(K1 k1, K2 k2, V value) {
		HashMap<K2, V> col = hashmap.get(k1);
		if (col == null) {
			col = new HashMap<K2, V>();
			hashmap.put(k1, col);
		}
		col.put(k2, value);
	}

	/**
	 * Returns the value to which the specified keys is mapped, or null if this
	 * map contains no mapping for the keys.
	 *
	 * A return value of null does not necessarily indicate that the map
	 * contains no mapping for the keys; it's also possible that the map
	 * explicitly maps the keys to null. The containsKey operation may be used
	 * to distinguish these two cases.
	 *
	 * @param k1
	 *            the first key whose associated value is to be returned
	 * @param k2
	 *            the second key whose associated value is to be returned
	 * @return the value to which the specified keys is mapped, or null if this
	 *         map contains no mapping for the keys
	 */
	public V get(K1 k1, K2 k2) {
		HashMap<K2, V> col = hashmap.get(k1);
		if (col == null)
			return null;
		return col.get(k2);
	}

	/**
	 * Removes all of the mappings from this map. The map will be empty after
	 * this call returns.
	 */
	public void clear() {
		hashmap = new HashMap<K1, HashMap<K2, V>>();
	}

	/**
	 * Returns true if this map contains a mapping for the specified keys.
	 *
	 * @param k1
	 *            The first part of the key whose presence in this map is to be
	 *            tested
	 * @param k2
	 *            The second part of the key whose presence in this map is to be
	 *            tested
	 * @return true if this map contains a mapping for the specified key.
	 */
	public boolean containsKey(K1 k1, K2 k2) {
		if (hashmap.containsKey(k1)) {
			return hashmap.get(k1).containsKey(k2);
		}
		return false;
	}

	/**
	 * Returns true if this map maps one or more keys to the specified value.
	 *
	 * @param value
	 *            value whose presence in this map is to be tested
	 * @return true if this map maps one or more keys to the specified value
	 */
	public boolean containsValue(V value) {
		for (Entry<K1, HashMap<K2, V>> mapEntry : hashmap.entrySet()) {
			if (mapEntry.getValue().containsValue(value))
				return true;
		}
		return false;
	}

	/**
	 * Returns true if this map contains no key-value mappings.
	 *
	 * @return true if this map contains no key-value mappings
	 */
	public boolean isEmpty() {
		if (hashmap.isEmpty())
			return true;
		for (Entry<K1, HashMap<K2, V>> mapEntry : hashmap.entrySet()) {
			if (!mapEntry.getValue().isEmpty())
				return false;
		}
		return true;
	}

	/**
	 * Removes the mapping for the specified key from this map if present.
	 *
	 * @param k1
	 *            first part of the key whose mapping is to be removed from the
	 *            map
	 * @param k2
	 *            second part of the key whose mapping is to be removed from the
	 *            map
	 * @return the previous value associated with keys, or null if there was no
	 *         mapping for keys. (A null return can also indicate that the map
	 *         previously associated null with keys.)
	 */
	public V remove(K1 k1, K1 k2) {
		HashMap<K2, V> col = hashmap.get(k1);
		if (col == null)
			return null;
		return col.remove(k2);
	}

	/**
	 * Returns the number of key-value mappings in this map.
	 *
	 * @return the number of key-value mappings in this map
	 */
	public int size() {
		int size = 0;
		for (Entry<K1, HashMap<K2, V>> mapEntry : hashmap.entrySet()) {
			size += mapEntry.getValue().size();
		}
		return size;
	}
}
