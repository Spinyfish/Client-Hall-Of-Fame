/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.audio;

import net.minecraft.util.ResourceLocation;

public interface ISound {
    public ResourceLocation getSoundLocation();

    public boolean canRepeat();

    public int getRepeatDelay();

    public float getVolume();

    public float getPitch();

    public float getXPosF();

    public float getYPosF();

    public float getZPosF();

    public AttenuationType getAttenuationType();

    public static enum AttenuationType {
        NONE(0),
        LINEAR(2);

        private final int type;

        private AttenuationType(int typeIn) {
            this.type = typeIn;
        }

        public int getTypeInt() {
            return this.type;
        }
    }
}

