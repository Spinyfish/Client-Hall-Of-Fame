package net.minecraft.client.audio;

import com.google.common.collect.Lists;

import java.util.List;

public class SoundList {
    private final List<SoundList.SoundEntry> soundList = Lists.newArrayList();

    /**
     * if true it will override all the sounds from the resourcepacks loaded before
     */
    private boolean replaceExisting;
    private SoundCategory category;

    public List<SoundList.SoundEntry> getSoundList() {
        return this.soundList;
    }

    public boolean canReplaceExisting() {
        return this.replaceExisting;
    }

    public void setReplaceExisting(final boolean p_148572_1_) {
        this.replaceExisting = p_148572_1_;
    }

    public SoundCategory getSoundCategory() {
        return this.category;
    }

    public void setSoundCategory(final SoundCategory soundCat) {
        this.category = soundCat;
    }

    public static class SoundEntry {
        private String name;
        private float volume = 1.0F;
        private float pitch = 1.0F;
        private int weight = 1;
        private SoundList.SoundEntry.Type type = SoundList.SoundEntry.Type.FILE;
        private boolean streaming = false;

        public String getSoundEntryName() {
            return this.name;
        }

        public void setSoundEntryName(final String nameIn) {
            this.name = nameIn;
        }

        public float getSoundEntryVolume() {
            return this.volume;
        }

        public void setSoundEntryVolume(final float volumeIn) {
            this.volume = volumeIn;
        }

        public float getSoundEntryPitch() {
            return this.pitch;
        }

        public void setSoundEntryPitch(final float pitchIn) {
            this.pitch = pitchIn;
        }

        public int getSoundEntryWeight() {
            return this.weight;
        }

        public void setSoundEntryWeight(final int weightIn) {
            this.weight = weightIn;
        }

        public SoundList.SoundEntry.Type getSoundEntryType() {
            return this.type;
        }

        public void setSoundEntryType(final SoundList.SoundEntry.Type typeIn) {
            this.type = typeIn;
        }

        public boolean isStreaming() {
            return this.streaming;
        }

        public void setStreaming(final boolean isStreaming) {
            this.streaming = isStreaming;
        }

        public enum Type {
            FILE("file"),
            SOUND_EVENT("event");

            private final String field_148583_c;

            Type(final String p_i45109_3_) {
                this.field_148583_c = p_i45109_3_;
            }

            public static SoundList.SoundEntry.Type getType(final String p_148580_0_) {
                for (final SoundList.SoundEntry.Type soundlist$soundentry$type : values()) {
                    if (soundlist$soundentry$type.field_148583_c.equals(p_148580_0_)) {
                        return soundlist$soundentry$type;
                    }
                }

                return null;
            }
        }
    }
}
