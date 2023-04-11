package byron.Mono.utils;

import java.awt.Color;

public class ColorUtils {
	
	public static Color darkpurple = new Color(28,28,28);

		
	  public static int astolfoColors(int yOffset, int yTotal) {
		  float speed = 1900.0F;

	        float hue;
	        for(hue = (float)(System.currentTimeMillis() % (long)((int)speed)) + (float)((yTotal - yOffset) * 9); hue > speed; hue -= speed) {
	        }

	        hue /= speed;
	        if ((double)hue > 1.5D) {
	            hue = 0.7F - (hue - 1.0F);
	        }

	        ++hue;
	        return Color.HSBtoRGB(hue, 0.4F, 1.0F);
	    }
	 
	   
	  
}
