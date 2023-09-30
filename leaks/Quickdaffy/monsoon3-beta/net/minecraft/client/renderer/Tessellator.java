/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.optifine.SmartAnimations;

public class Tessellator {
    private WorldRenderer worldRenderer;
    private WorldVertexBufferUploader vboUploader = new WorldVertexBufferUploader();
    private static final Tessellator instance = new Tessellator(0x200000);

    public static Tessellator getInstance() {
        return instance;
    }

    public Tessellator(int bufferSize) {
        this.worldRenderer = new WorldRenderer(bufferSize);
    }

    public void draw() {
        if (this.worldRenderer.animatedSprites != null) {
            SmartAnimations.spritesRendered(this.worldRenderer.animatedSprites);
        }
        this.worldRenderer.finishDrawing();
        this.vboUploader.func_181679_a(this.worldRenderer);
    }

    public WorldRenderer getWorldRenderer() {
        return this.worldRenderer;
    }
}

