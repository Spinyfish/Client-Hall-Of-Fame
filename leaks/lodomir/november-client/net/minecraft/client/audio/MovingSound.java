/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.audio;

import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.ResourceLocation;

public abstract class MovingSound
extends PositionedSound
implements ITickableSound {
    protected boolean donePlaying = false;

    protected MovingSound(ResourceLocation location) {
        super(location);
    }

    @Override
    public boolean isDonePlaying() {
        return this.donePlaying;
    }
}

