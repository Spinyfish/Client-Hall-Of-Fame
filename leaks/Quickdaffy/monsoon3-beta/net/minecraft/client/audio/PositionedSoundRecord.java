/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.audio;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.ResourceLocation;

public class PositionedSoundRecord
extends PositionedSound {
    public static PositionedSoundRecord create(ResourceLocation soundResource, float pitch) {
        return new PositionedSoundRecord(soundResource, 0.25f, pitch, false, 0, ISound.AttenuationType.NONE, 0.0f, 0.0f, 0.0f);
    }

    public static PositionedSoundRecord create(ResourceLocation soundResource) {
        return new PositionedSoundRecord(soundResource, 1.0f, 1.0f, false, 0, ISound.AttenuationType.NONE, 0.0f, 0.0f, 0.0f);
    }

    public static PositionedSoundRecord create(ResourceLocation soundResource, float xPosition, float yPosition, float zPosition) {
        return new PositionedSoundRecord(soundResource, 4.0f, 1.0f, false, 0, ISound.AttenuationType.LINEAR, xPosition, yPosition, zPosition);
    }

    public PositionedSoundRecord(ResourceLocation soundResource, float volume, float pitch, float xPosition, float yPosition, float zPosition) {
        this(soundResource, volume, pitch, false, 0, ISound.AttenuationType.LINEAR, xPosition, yPosition, zPosition);
    }

    private PositionedSoundRecord(ResourceLocation soundResource, float volume, float pitch, boolean repeat, int repeatDelay, ISound.AttenuationType attenuationType, float xPosition, float yPosition, float zPosition) {
        super(soundResource);
        this.volume = volume;
        this.pitch = pitch;
        this.xPosF = xPosition;
        this.yPosF = yPosition;
        this.zPosF = zPosition;
        this.repeat = repeat;
        this.repeatDelay = repeatDelay;
        this.attenuationType = attenuationType;
    }
}

