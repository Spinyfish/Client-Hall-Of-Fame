package juul.util.image;

import juul.util.Utils;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

import juul.inject.wrappers.GlStateManagerWrapper;

public class Draw implements Utils {
	
	public static void rectTexture(float x, float y, float w, float h, Texture texture, int color) {
		if (texture == null) {
			return;
		}

		float alpha = (float) (color >> 24 & 255) / 255.0F;
		float red = (float) (color >> 16 & 255) / 255.0F;
		float green = (float) (color >> 8 & 255) / 255.0F;
		float blue = (float) (color & 255) / 255.0F;

		glStateManager.color(red, green, blue, alpha);

		texture.bind();

		float tw = (w / texture.getTextureWidth()) / (w / texture.getImageWidth());
		float th = (h / texture.getTextureHeight()) / (h / texture.getImageHeight());

		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2f(x, y);
		GL11.glTexCoord2f(0, th);
		GL11.glVertex2f(x, y + h);
		GL11.glTexCoord2f(tw, th);
		GL11.glVertex2f(x + w, y + h);
		GL11.glTexCoord2f(tw, 0);
		GL11.glVertex2f(x + w, y);
		GL11.glEnd();
		
	}
	
	public static void rectTexture(float x, float y, float u, float v, float tw, float th, float w, float h, Texture texture, int color) {
		if (texture == null) {
			return;
		}

		w += (0.5f*(u/tw));

		h += (0.5f*(v/th));
		
		/*float alpha = (float) (color >> 24 & 255) / 255.0F;
		float red = (float) (color >> 16 & 255) / 255.0F;
		float green = (float) (color >> 8 & 255) / 255.0F;
		float blue = (float) (color & 255) / 255.0F;*/

		//GlStateManager.color(red, green, blue, alpha);

		texture.bind();

		float texWidth = (w / texture.getTextureWidth()) / (tw / texture.getImageWidth());
		float texHeight = (h / texture.getTextureHeight()) / (th / texture.getImageHeight());
		
		//float textPosX = u/tw;
		//System.out.println(tw + " " + u/tw + " " + w/tw);
		//System.out.println(u + " " + tw + " "+ w);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        
		float offsetX = ((u - (1f*(u/tw)))/texture.getTextureWidth()*2f);
		float offsetY = ((v - (1f*(v/th)))/texture.getTextureHeight()*2f);
		
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(offsetX, offsetY);
		GL11.glVertex2f(x, y);
		GL11.glTexCoord2f(offsetX, texHeight + offsetY);
		GL11.glVertex2f(x, y + h);
		GL11.glTexCoord2f(texWidth + offsetX, texHeight + offsetY);
		GL11.glVertex2f(x + w, y + h);
		GL11.glTexCoord2f(texWidth + offsetX, offsetY);
		GL11.glVertex2f(x + w, y);
		GL11.glEnd();
		
	}

	public static void rectTexture(float x, float y, float w, float h, Texture texture) {
		rectTexture(x, y, w, h, texture, -1);
	}

}