package intent.AquaDev.aqua.cape;

import java.util.ArrayList;

public class GIFManager {
   ArrayList<GIF> gifs = new ArrayList<>();

   public void addGif(GIF gif) {
      this.gifs.add(gif);
   }

   public GIF getGIFByName(String name) {
      return this.gifs.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
   }
}
