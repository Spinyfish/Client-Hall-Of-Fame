package de.hero.clickgui.classes;

import java.awt.Color;

import org.lwjgl.input.Mouse;

import de.fanta.utils.RenderUtil;
import net.minecraft.client.gui.Gui;

public class GuiColorChooser extends Gui {

	public double x, y;
	private int width = 100, height = 80;

	private double hueChooserX;

	private double colorChooserX, colorChooserY;
	
	public GuiColorChooser(int x, int y) {
		this.x = x;
		this.y = y;
		this.color = Color.red.getRGB();
	}

	public GuiColorChooser(int x, int y, int color) {
		this.x = x;
		this.y = y;
		this.color = color;
		Color c = new Color(color);
		this.hsbValues = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsbValues);
		setHueChooserByHue(this.hsbValues[0]);
		setHueChooserBySB(this.hsbValues[1], this.hsbValues[2]);
	}

	public void draw(int mouseX, int mouseY) {
		int backGroundColor = new Color(38,38,38,150).getRGB();
		drawRect2(x, y, x + width, y + height, backGroundColor);
		int chooserWidth = width-10;
		
		
		for (float i = 0; i < chooserWidth; i+=.5) {
			float f = (1.0F / chooserWidth) * i;
			drawRect2(x + 5 + i, y + height - 12.75, x + 5 + i + .5, y + height - 8.25, Color.HSBtoRGB(f, 1, 1));
		}
		
		int hsbChooserWidth = width-10;
		int hsbChooserHeight = height-25;
		for(float e = 0; e < hsbChooserWidth; e += 1) {
			for(float f = 0; f < hsbChooserHeight; f += 1) {
				float xPos = (float) (x+5+e);
				float yPos = (float) (f);
				float satuartion = 1.0F/hsbChooserWidth*e;
				float brightness = 1.0F/hsbChooserHeight*f;
				drawRect2(xPos, y+5+hsbChooserHeight-yPos-1, xPos+1F, y+5+hsbChooserHeight-yPos+1F-1, Color.HSBtoRGB(this.hsbValues[0], satuartion, brightness));
			}
		}
		drawHollowRect(x + 4.5, y + 4.5, width - 9, height - 24, 0.5, Color.white.getRGB());
//		drawHollowRect(x + 4.5, y + height - 15.5, width - 9, 6, 0.5, Color.white.getRGB());

		/*
		 * Hue Chooser
		 */
		
		int max = 255;
		Color onlyHue = new Color(Color.HSBtoRGB(hsbValues[0], 1, 1));
		int hueChooserColor = new Color(max-onlyHue.getRed(), max-onlyHue.getGreen(), max-onlyHue.getBlue()).getRGB();
		
		drawRect2(x + 5 + hueChooserX, y + height - 12.75, x + 5 + hueChooserX + .5, y + height - 8.25, hueChooserColor);
		/*
		 * Color Chooser
		 */
		
		Color allColor = new Color(Color.HSBtoRGB(hsbValues[0], hsbValues[1], hsbValues[2]));
		int colorChooserColor = new Color(max-allColor.getRed(), max-allColor.getGreen(), max-allColor.getBlue()).getRGB();
		
		drawRect2(x+5+colorChooserX-2.5,  y+5+colorChooserY-.25, x+5+colorChooserX+2.5, y+5+colorChooserY+.25, colorChooserColor);
		drawRect2(x+5+colorChooserX-.25,  y+5+colorChooserY-2.5, x+5+colorChooserX+.25, y+5+colorChooserY+2.5, colorChooserColor);
		
		if (Mouse.isButtonDown(0)) {
			if (mouseY >= y + 5 + height - 20 && mouseY <= y + 5 + height - 10) {
				double diff = mouseX - x - 5;
				if (diff > width-10.5)
					diff = width-10.5;
				if (diff < 0)
					diff = 0;
				hueChooserX = diff;
				setHueChooserByHue((float) ((1.0F / (width-10)) * hueChooserX));
			}
			if (mouseX >= x+5 && mouseX <= x+width-5 && mouseY >= y + 5 && mouseY <= y + height - 20) {
				double diffX = mouseX - x - 5;
				if (diffX > width-10)
					diffX = width-10;
				if (diffX < 0)
					diffX = 0;
				double diffY = mouseY - y - 5;
				if (diffY > 55)
					diffY = 55;
				if (diffY < 0.25)
					diffY = 0.25;
				colorChooserX = diffX;
				colorChooserY = diffY;
				this.hsbValues[1] = (float) ((1.0F / (width-10)) * colorChooserX);
				this.hsbValues[2] = 1-(float) ((1.0F / 55.0F) * colorChooserY);
			}
		}
		this.color = Color.HSBtoRGB(hsbValues[0], hsbValues[1], hsbValues[2]);
	}

	public int color = Color.decode("#FFFFFF").getRGB();
	public float[] hsbValues = new float[3];

	public void setHue(float hue) {
		if (hue > 1)
			hue = 1;
		this.hsbValues[0] = hue;
	}

	public void setHueChooserByHue(float hue) {
		this.hueChooserX = (width-10)*hue;
		setHue(hue);
	}

	public void setHueChooserBySB(float s, float b) {
		this.colorChooserX = (width-10)*s;
		this.colorChooserY = 55-55*b;
	}
	
	public void setSaturation(float sat) {
		if (sat > 1)
			sat = 1;
		this.hsbValues[1] = sat;
	}

	public void setBrightness(float bright) {
		if (bright > 1)
			bright = 1;
		this.hsbValues[2] = bright;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}

}
