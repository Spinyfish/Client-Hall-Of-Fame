/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.kyori.adventure.nbt;

import java.io.IOException;

interface BinaryTagScope
extends AutoCloseable {
    @Override
    public void close() throws IOException;

    public static class NoOp
    implements BinaryTagScope {
        static final NoOp INSTANCE = new NoOp();

        private NoOp() {
        }

        @Override
        public void close() {
        }
    }
}

