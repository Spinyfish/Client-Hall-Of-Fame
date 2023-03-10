/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.ui.font;

import java.nio.ByteBuffer;

public class TextureData {
    private int textureId;
    private int width;
    private int height;
    private ByteBuffer buffer;

    public TextureData(int textureId, int width, int height, ByteBuffer buffer) {
        this.textureId = textureId;
        this.width = width;
        this.height = height;
        this.buffer = buffer;
    }

    public int getTextureId() {
        return this.textureId;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public ByteBuffer getBuffer() {
        return this.buffer;
    }
}

