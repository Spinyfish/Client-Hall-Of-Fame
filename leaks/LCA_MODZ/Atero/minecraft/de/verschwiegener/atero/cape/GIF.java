package de.verschwiegener.atero.cape;

import java.io.File;
import java.net.URL;
import java.util.Arrays;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.util.TimeUtils;

public class GIF {
    
    private String name;
    private ImageFrame[] frames;
    TimeUtils timer = new TimeUtils();
    int current = 0;
    
    public GIF(String name, URL URL) {
	this.name = name;
	frames = Management.instance.GIFLoader.readGifFromURL(URL, name);
    }
    
    public GIF(String name, File file) {
	this.name = name;
	frames = Management.instance.GIFLoader.readGifFromFile(file, name);
    }

    public GIF(String name, String file) {
	this.name = name;
	frames = Management.instance.GIFLoader.readGifFromAssets(file, name);
	System.out.println("Size: " + frames.length);
	System.out.println("GIF: " + Arrays.toString(frames));
    }
    
    public ImageFrame getNext() {
	if (frames != null) {
	    if (timer.hasReached(100)) {
		timer.reset();
		current++;
		if (current >= frames.length) {
		    current = 0;
		}
	    }
	    return frames[current];
	}
	return null;
    }
    public String getName() {
	return name;
    }

    public ImageFrame get() {
	return frames[1];
    }

}
