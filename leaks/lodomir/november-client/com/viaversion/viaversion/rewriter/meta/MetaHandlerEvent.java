/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.rewriter.meta;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface MetaHandlerEvent {
    public UserConnection user();

    public int entityId();

    public @Nullable EntityType entityType();

    default public int index() {
        return this.meta().id();
    }

    default public void setIndex(int index) {
        this.meta().setId(index);
    }

    public Metadata meta();

    public void cancel();

    public boolean cancelled();

    public @Nullable Metadata metaAtIndex(int var1);

    public List<Metadata> metadataList();

    public @Nullable List<Metadata> extraMeta();

    public void createExtraMeta(Metadata var1);
}

