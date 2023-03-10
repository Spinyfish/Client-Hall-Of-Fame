/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.rewriter;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.rewriter.Rewriter;
import java.util.List;

public interface EntityRewriter<T extends Protocol>
extends Rewriter<T> {
    public EntityType typeFromId(int var1);

    default public EntityType objectTypeFromId(int type) {
        return this.typeFromId(type);
    }

    public int newEntityId(int var1);

    public void handleMetadata(int var1, List<Metadata> var2, UserConnection var3);

    default public <E extends EntityTracker> E tracker(UserConnection connection) {
        return (E)connection.getEntityTracker(this.protocol().getClass());
    }
}

