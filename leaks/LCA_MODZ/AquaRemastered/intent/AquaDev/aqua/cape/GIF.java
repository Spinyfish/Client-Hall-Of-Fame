package intent.AquaDev.aqua.cape;

import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.utils.TimeUtil;
import java.net.URL;

public class GIF {
   private String name;
   private ImageFrame[] frames;
   TimeUtil timer = new TimeUtil();
   int current = 0;

   public GIF(String name, URL URL) {
      this.name = name;
      this.frames = Aqua.INSTANCE.GIFLoader.readGifFromURL(URL, name);
   }

   public GIF(String name, String file) {
      this.name = name;
      this.frames = Aqua.INSTANCE.GIFLoader.readGifFromAssets(file, name);
   }

   public ImageFrame getNext() {
      if (this.frames != null) {
         if (this.timer.hasReached(100L)) {
            this.timer.reset();
            ++this.current;
            if (this.current >= this.frames.length) {
               this.current = 0;
            }
         }

         return this.frames[this.current];
      } else {
         return null;
      }
   }

   public String getName() {
      return this.name;
   }

   public ImageFrame get() {
      return this.frames[1];
   }
}
