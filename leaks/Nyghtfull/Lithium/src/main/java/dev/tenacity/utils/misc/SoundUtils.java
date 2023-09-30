package dev.tenacity.utils.misc;

import dev.tenacity.utils.Utils;
import net.minecraft.util.ResourceLocation;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;

public class SoundUtils implements Utils {

    public static void playSound(ResourceLocation location, float volume) {
        Multithreading.runAsync((() -> {
            try {
                BufferedInputStream bufferedInputStream = new BufferedInputStream(mc.getResourceManager().getResource(location).getInputStream());
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedInputStream);

                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);

                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float range = gainControl.getMaximum() - gainControl.getMinimum();
                float gain = (range * volume) + gainControl.getMinimum();
                gainControl.setValue(gain);

                clip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }));
    }

}
