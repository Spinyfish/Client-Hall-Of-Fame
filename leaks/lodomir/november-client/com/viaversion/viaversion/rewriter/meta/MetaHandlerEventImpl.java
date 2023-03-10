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
import com.viaversion.viaversion.rewriter.meta.MetaHandlerEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MetaHandlerEventImpl
implements MetaHandlerEvent {
    private final UserConnection connection;
    private final EntityType entityType;
    private final int entityId;
    private final List<Metadata> metadataList;
    private final Metadata meta;
    private List<Metadata> extraData;
    private boolean cancel;

    public MetaHandlerEventImpl(UserConnection connection, @Nullable EntityType entityType, int entityId, Metadata meta, List<Metadata> metadataList) {
        this.connection = connection;
        this.entityType = entityType;
        this.entityId = entityId;
        this.meta = meta;
        this.metadataList = metadataList;
    }

    @Override
    public UserConnection user() {
        return this.connection;
    }

    @Override
    public int entityId() {
        return this.entityId;
    }

    @Override
    public @Nullable EntityType entityType() {
        return this.entityType;
    }

    @Override
    public Metadata meta() {
        return this.meta;
    }

    @Override
    public void cancel() {
        this.cancel = true;
    }

    @Override
    public boolean cancelled() {
        return this.cancel;
    }

    @Override
    public @Nullable Metadata metaAtIndex(int index) {
        for (Metadata meta : this.metadataList) {
            if (index != meta.id()) continue;
            return meta;
        }
        return null;
    }

    @Override
    public List<Metadata> metadataList() {
        return Collections.unmodifiableList(this.metadataList);
    }

    @Override
    public @Nullable List<Metadata> extraMeta() {
        return this.extraData;
    }

    @Override
    public void createExtraMeta(Metadata metadata) {
        (this.extraData != null ? this.extraData : (this.extraData = new ArrayList<Metadata>())).add(metadata);
    }
}

