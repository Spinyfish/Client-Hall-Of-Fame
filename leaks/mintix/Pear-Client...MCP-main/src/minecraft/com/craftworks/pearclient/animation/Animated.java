package com.craftworks.pearclient.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class Animated {
    private final String folder;
    private final int frames;
    private final int fpt;
    private int currentTick = 0;
    private int currentFrame = 0;
    private ResourceLocation[] textures;

    public Animated(String folder, int frames, int fpt) {
        this.folder = folder;
        this.frames = frames;
        this.fpt = fpt;
        this.textures = new ResourceLocation[frames];

        for(int i = 0; i < frames; ++i) {
            this.textures[i] = new ResourceLocation(folder + "/" + i + ".png");
        }

    }

    public ResourceLocation getTexture() {
        return this.textures[this.currentFrame];
    }

    public void update() {
        if (this.currentTick > Minecraft.getMinecraft().getDebugFPS()) {
            this.currentTick = 0;
            ++this.currentFrame;
            if (this.currentFrame > this.textures.length - 1) {
                this.currentFrame = 0;
            }
        }

        ++this.currentTick;
    }
}
