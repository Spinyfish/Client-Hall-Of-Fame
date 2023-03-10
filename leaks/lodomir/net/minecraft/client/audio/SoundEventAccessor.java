/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.audio;

import net.minecraft.client.audio.ISoundEventAccessor;
import net.minecraft.client.audio.SoundPoolEntry;

public class SoundEventAccessor
implements ISoundEventAccessor<SoundPoolEntry> {
    private final SoundPoolEntry entry;
    private final int weight;

    SoundEventAccessor(SoundPoolEntry entry, int weight) {
        this.entry = entry;
        this.weight = weight;
    }

    @Override
    public int getWeight() {
        return this.weight;
    }

    @Override
    public SoundPoolEntry cloneEntry() {
        return new SoundPoolEntry(this.entry);
    }
}

