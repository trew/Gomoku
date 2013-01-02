package tictactoe.util;

import java.util.HashMap;
import java.util.Map.Entry;

public class HashMap2D<K1 extends Object, K2 extends Object, V extends Object> {

	private HashMap<K1, HashMap<K2, V>> hashmap;

	public HashMap2D() {
		clear();
	}

	public void put(K1 k1, K2 k2, V value) {
		HashMap<K2, V> col = hashmap.get(k1);
		if (col == null) {
			col = new HashMap<K2, V>();
			hashmap.put(k1, col);
		}
		col.put(k2, value);
	}

	public V get(K1 k1, K2 k2) {
		HashMap<K2, V> col = hashmap.get(k1);
		if (col == null) return null;
		return col.get(k2);
	}

	public void clear() {
		hashmap = new HashMap<K1, HashMap<K2, V>>();
	}

	public boolean containsValue(V value) {
		for (Entry<K1, HashMap<K2, V>> mapEntry : hashmap.entrySet()) {
			if (mapEntry.getValue().containsValue(value)) return true;
		}
		return false;
	}

	public boolean isEmpty() {
		if (hashmap.isEmpty()) return true;
		for (Entry<K1, HashMap<K2, V>> mapEntry : hashmap.entrySet()) {
			if (!mapEntry.getValue().isEmpty()) return false;
		}
		return true;
	}

	public V remove(K1 k1, K1 k2) {
		HashMap<K2, V> col = hashmap.get(k1);
		if (col == null) return null;
		return col.remove(k2);
	}

	public int size() {
		int size = 0;
		for (Entry<K1, HashMap<K2, V>> mapEntry : hashmap.entrySet()) {
			size += mapEntry.getValue().size();
		}
		return size;
	}
}
