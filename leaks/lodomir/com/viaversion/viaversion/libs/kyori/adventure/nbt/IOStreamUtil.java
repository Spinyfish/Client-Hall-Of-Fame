/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.kyori.adventure.nbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

final class IOStreamUtil {
    private IOStreamUtil() {
    }

    static InputStream closeShield(final InputStream stream) {
        return new InputStream(){

            @Override
            public int read() throws IOException {
                return stream.read();
            }

            @Override
            public int read(byte[] b) throws IOException {
                return stream.read(b);
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                return stream.read(b, off, len);
            }
        };
    }

    static OutputStream closeShield(final OutputStream stream) {
        return new OutputStream(){

            @Override
            public void write(int b) throws IOException {
                stream.write(b);
            }

            @Override
            public void write(byte[] b) throws IOException {
                stream.write(b);
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                stream.write(b, off, len);
            }
        };
    }
}

