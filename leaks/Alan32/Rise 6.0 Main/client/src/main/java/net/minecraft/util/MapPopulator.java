package net.minecraft.util;

import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class MapPopulator {
    public static <K, V> Map<K, V> createMap(final Iterable<K> keys, final Iterable<V> values) {
        return populateMap(keys, values, Maps.newLinkedHashMap());
    }

    public static <K, V> Map<K, V> populateMap(final Iterable<K> keys, final Iterable<V> values, final Map<K, V> map) {
        final Iterator<V> iterator = values.iterator();

        for (final K k : keys) {
            map.put(k, iterator.next());
        }

        if (iterator.hasNext()) {
            throw new NoSuchElementException();
        } else {
            return map;
        }
    }
}
