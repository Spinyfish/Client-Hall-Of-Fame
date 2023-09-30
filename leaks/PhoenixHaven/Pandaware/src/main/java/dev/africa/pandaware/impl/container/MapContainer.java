package dev.africa.pandaware.impl.container;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class MapContainer<K, V> {
    private final Map<K, V> map = new LinkedHashMap<>();
}