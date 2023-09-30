package dev.africa.pandaware.utils.client;

import dev.africa.pandaware.api.interfaces.MinecraftInstance;
import dev.africa.pandaware.utils.java.FileUtils;
import dev.africa.pandaware.utils.math.apache.ApacheMath;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.util.ResourceLocation;

import javax.sound.sampled.*;

@UtilityClass
public class SoundUtils implements MinecraftInstance {

    public void playSound(CustomSound sound, float volume) {
        playSound(sound.getSound(), volume);
    }

    public void playSound(String path, float volume) {
        // gets sound resource
        val resource = new ResourceLocation(path);

        // converts minecraft resource to input stream
        val soundStream = FileUtils.getInputStreamFromResource(resource);

        try {
            // get line instance
            val clip = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));

            // adds close listener
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });

            // opens clip
            clip.open(AudioSystem.getAudioInputStream(soundStream));

            // normalize volume and clamp it
            float normalizedVolume = (volume / 100f);

            // get gain control
            FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            // set volume
            control.setValue((float) (ApacheMath.log10(normalizedVolume) * 20f));

            // plays clip
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Getter
    @AllArgsConstructor
    public enum CustomSound {
        ENABLE("pandaware/sounds/enable.wav"),
        DISABLE("pandaware/sounds/disable.wav");

        private final String sound;
    }
}
