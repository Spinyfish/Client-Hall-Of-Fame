package de.fanta.utils;



import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SoundUtil {
    private static Clip clip;

//    public static final URL button = SoundUtil.class.getClassLoader().getResource("ressources/sounds/buttonClick.wav");
    public static final URL toggleOnSound = SoundUtil.class.getClassLoader().getResource("assets/minecraft/Fanta/sounds/toggleSound.wav");
    public static final URL toggleOffSound = SoundUtil.class.getClassLoader().getResource("assets/minecraft/Fanta/sounds/toggleSound2.wav");
 //   public static final URL loginSuccessful = SoundUtil.class.getClassLoader().getResource("ressources/sounds/loginSuccessful.wav");
  //  public static final URL loginFailed = SoundUtil.class.getClassLoader().getResource("ressources/sounds/loginFailed.wav");


    public static void play(URL filePath) {
        try {
            clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(filePath));
            FloatControl floatControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            floatControl.setValue(6.0206F);
            clip.start();
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }

    }
}
