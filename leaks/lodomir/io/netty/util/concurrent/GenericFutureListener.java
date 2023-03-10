/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.Future;
import java.util.EventListener;

public interface GenericFutureListener<F extends Future<?>>
extends EventListener {
    public void operationComplete(F var1) throws Exception;
}

