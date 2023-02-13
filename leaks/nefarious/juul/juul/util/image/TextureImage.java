package juul.util.image;

import java.io.ByteArrayInputStream;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class TextureImage {

	public byte[] pixels;
	public Texture texture;
	public String location;
	public int loadedTicks;
	public Animation intro = new Animation();
	public Animation hover = new Animation();
	
	public Texture getTexture() {
    	if (texture == null) {
    		if (pixels != null) {
    			try {
    				ByteArrayInputStream bias = new ByteArrayInputStream(pixels);
    				texture = TextureLoader.getTexture("PNG", bias);
    			} catch (Exception e) {
    			}
    		}
    	}
		return texture;
	}
	
	public void rectTexture(float x, float y, float w, float h) {
		Draw.rectTexture(x, y, w, h, this.getTexture());
	}
	
}