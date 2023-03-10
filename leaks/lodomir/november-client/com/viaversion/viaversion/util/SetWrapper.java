/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ForwardingSet
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.viaversion.viaversion.util;

import com.google.common.collect.ForwardingSet;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.NonNull;

public class SetWrapper<E>
extends ForwardingSet<E> {
    private final Set<E> set;
    private final Consumer<E> addListener;

    public SetWrapper(Set<E> set, Consumer<E> addListener) {
        this.set = set;
        this.addListener = addListener;
    }

    public boolean add(@NonNull E element) {
        this.addListener.accept(element);
        return super.add(element);
    }

    public boolean addAll(Collection<? extends E> collection) {
        for (E element : collection) {
            this.addListener.accept(element);
        }
        return super.addAll(collection);
    }

    protected Set<E> delegate() {
        return this.originalSet();
    }

    public Set<E> originalSet() {
        return this.set;
    }
}

