/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.connection;

import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.api.connection.UserConnection;

public abstract class StoredObject
implements StorableObject {
    private final UserConnection user;

    protected StoredObject(UserConnection user) {
        this.user = user;
    }

    public UserConnection getUser() {
        return this.user;
    }
}

