/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util;

import io.netty.util.UniqueName;
import io.netty.util.internal.PlatformDependent;
import java.util.concurrent.ConcurrentMap;

public final class Signal
extends Error {
    private static final long serialVersionUID = -221145131122459977L;
    private static final ConcurrentMap<String, Boolean> map = PlatformDependent.newConcurrentHashMap();
    private final UniqueName uname;

    public static Signal valueOf(String name) {
        return new Signal(name);
    }

    @Deprecated
    public Signal(String name) {
        super(name);
        this.uname = new UniqueName(map, name, new Object[0]);
    }

    public void expect(Signal signal) {
        if (this != signal) {
            throw new IllegalStateException("unexpected signal: " + signal);
        }
    }

    @Override
    public Throwable initCause(Throwable cause) {
        return this;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

    @Override
    public String toString() {
        return this.uname.name();
    }
}

