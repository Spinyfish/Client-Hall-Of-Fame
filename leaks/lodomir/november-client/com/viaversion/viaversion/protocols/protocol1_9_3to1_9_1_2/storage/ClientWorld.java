/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Environment;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ClientWorld
extends StoredObject {
    private Environment environment;

    public ClientWorld(UserConnection connection) {
        super(connection);
    }

    public @Nullable Environment getEnvironment() {
        return this.environment;
    }

    public void setEnvironment(int environmentId) {
        this.environment = Environment.getEnvironmentById(environmentId);
    }
}

