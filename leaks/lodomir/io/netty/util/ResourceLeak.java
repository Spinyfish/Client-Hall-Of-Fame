/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util;

public interface ResourceLeak {
    public void record();

    public boolean close();
}

