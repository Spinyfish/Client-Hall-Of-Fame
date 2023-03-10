/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.api.protocol;

import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.SimpleProtocol;
import java.util.Collection;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ProtocolPipeline
extends SimpleProtocol {
    public void add(Protocol var1);

    public void add(Collection<Protocol> var1);

    public boolean contains(Class<? extends Protocol> var1);

    public <P extends Protocol> @Nullable P getProtocol(Class<P> var1);

    public List<Protocol> pipes();

    public boolean hasNonBaseProtocols();

    public void cleanPipes();
}

