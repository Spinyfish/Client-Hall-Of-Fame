/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viabackwards.api.entities.storage;

import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class WrappedMetadata {
    private final List<Metadata> metadataList;

    public WrappedMetadata(List<Metadata> metadataList) {
        this.metadataList = metadataList;
    }

    public boolean has(Metadata data) {
        return this.metadataList.contains(data);
    }

    public void remove(Metadata data) {
        this.metadataList.remove(data);
    }

    public void remove(int index) {
        this.metadataList.removeIf(meta -> meta.id() == index);
    }

    public void add(Metadata data) {
        this.metadataList.add(data);
    }

    public @Nullable Metadata get(int index) {
        for (Metadata meta : this.metadataList) {
            if (index != meta.id()) continue;
            return meta;
        }
        return null;
    }

    public List<Metadata> metadataList() {
        return this.metadataList;
    }

    public String toString() {
        return "MetaStorage{metaDataList=" + this.metadataList + '}';
    }
}

