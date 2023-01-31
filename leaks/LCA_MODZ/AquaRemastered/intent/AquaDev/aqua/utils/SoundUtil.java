package intent.AquaDev.aqua.utils;

import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.FloatControl.Type;

public class SoundUtil {
   private static Clip clip;
   public static final URL toggleOnSound = SoundUtil.class.getClassLoader().getResource("assets/minecraft/Aqua/gui/toggleSound.wav");
   public static final URL toggleOffSound = SoundUtil.class.getClassLoader().getResource("assets/minecraft/Aqua/gui/toggleSound2.wav");

   public static void play(URL filePath) {
      try {
         clip = AudioSystem.getClip();
         clip.open(AudioSystem.getAudioInputStream(filePath));
         FloatControl floatControl = (FloatControl)clip.getControl(Type.MASTER_GAIN);
         floatControl.setValue(6.0206F);
         clip.start();
      } catch (UnsupportedAudioFileException | IOException | LineUnavailableException var2) {
         var2.printStackTrace();
      }
   }
}
