package net.optifine;

public class TextureAnimationFrame {
   public final int index;
   public final int duration;
   public int counter;

   public TextureAnimationFrame(int index, int duration) {
      this.index = index;
      this.duration = duration;
      this.counter = 0;
   }
}
