/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  sun.audio.AudioPlayer
 *  sun.audio.AudioStream
 */
package wtf.monsoon.api.util.misc;

import java.io.IOException;
import java.io.InputStream;
import net.minecraft.util.Util;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class SoundUtil
extends Util {
    public static void playSound(String resourceLocation) {
        try {
            InputStream sound = SoundUtil.getFileFromResourceAsStream("assets/minecraft/monsoon/sound/" + resourceLocation);
            AudioStream stream = new AudioStream(sound);
            AudioPlayer.player.start((InputStream)stream);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static InputStream getFileFromResourceAsStream(String fileName) {
        ClassLoader classLoader = SoundUtil.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        }
        return inputStream;
    }
}

