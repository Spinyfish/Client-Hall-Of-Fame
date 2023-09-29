package de.verschwiegener.atero.cape;

import java.util.ArrayList;

public class GIFManager {
    
    ArrayList<GIF> gifs = new ArrayList<>();
    
    public void addGif(GIF gif) {
	gifs.add(gif);
    }
    
    public GIF getGIFByName(final String name) {
	return gifs.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

}
