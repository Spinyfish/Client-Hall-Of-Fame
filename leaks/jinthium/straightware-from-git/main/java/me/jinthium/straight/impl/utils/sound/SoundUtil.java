package me.jinthium.straight.impl.utils.sound;

import me.jinthium.straight.api.util.MinecraftInstance;

public class SoundUtil implements MinecraftInstance {

    private static int ticksExisted;

    public static void toggleSound(final boolean enable) {
        if (mc.thePlayer != null && mc.thePlayer.ticksExisted != ticksExisted) {
            if (enable) {
                playSound("toggle.enable");
            } else {
                playSound("toggle.disable");
            }
            ticksExisted = mc.thePlayer.ticksExisted;
        }
    }

    public static void playSound(final String sound) {
        playSound(sound, 1, 1);
    }

    public static void playSound(final String sound, final float volume, final float pitch) {
        mc.theWorld.playSound(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, sound, volume, pitch, false);
    }
}