/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal;

import io.netty.util.internal.TypeParameterMatcher;

public final class NoOpTypeParameterMatcher
extends TypeParameterMatcher {
    @Override
    public boolean match(Object msg) {
        return true;
    }
}

