/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.client.audio.ISoundEventAccessor;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.util.ResourceLocation;

public class SoundEventAccessorComposite
implements ISoundEventAccessor<SoundPoolEntry> {
    private final List<ISoundEventAccessor<SoundPoolEntry>> soundPool = Lists.newArrayList();
    private final Random rnd = new Random();
    private final ResourceLocation soundLocation;
    private final SoundCategory category;
    private double eventPitch;
    private double eventVolume;

    public SoundEventAccessorComposite(ResourceLocation soundLocation, double pitch, double volume, SoundCategory category) {
        this.soundLocation = soundLocation;
        this.eventVolume = volume;
        this.eventPitch = pitch;
        this.category = category;
    }

    @Override
    public int getWeight() {
        int i = 0;
        for (ISoundEventAccessor<SoundPoolEntry> isoundeventaccessor : this.soundPool) {
            i += isoundeventaccessor.getWeight();
        }
        return i;
    }

    @Override
    public SoundPoolEntry cloneEntry() {
        int i = this.getWeight();
        if (!this.soundPool.isEmpty() && i != 0) {
            int j = this.rnd.nextInt(i);
            for (ISoundEventAccessor<SoundPoolEntry> isoundeventaccessor : this.soundPool) {
                if ((j -= isoundeventaccessor.getWeight()) >= 0) continue;
                SoundPoolEntry soundpoolentry = isoundeventaccessor.cloneEntry();
                soundpoolentry.setPitch(soundpoolentry.getPitch() * this.eventPitch);
                soundpoolentry.setVolume(soundpoolentry.getVolume() * this.eventVolume);
                return soundpoolentry;
            }
            return SoundHandler.missing_sound;
        }
        return SoundHandler.missing_sound;
    }

    public void addSoundToEventPool(ISoundEventAccessor<SoundPoolEntry> sound) {
        this.soundPool.add(sound);
    }

    public ResourceLocation getSoundEventLocation() {
        return this.soundLocation;
    }

    public SoundCategory getSoundCategory() {
        return this.category;
    }
}

