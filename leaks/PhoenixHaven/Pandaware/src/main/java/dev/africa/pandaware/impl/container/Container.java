package dev.africa.pandaware.impl.container;

import lombok.Getter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class Container<T> {
    private final List<T> items = new CopyOnWriteArrayList<>();
}