/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.kyori.adventure.util;

import java.util.ServiceLoader;

final class Services0 {
    private Services0() {
    }

    static <S> ServiceLoader<S> loader(Class<S> type) {
        return ServiceLoader.load(type, type.getClassLoader());
    }
}

