package felix.util.font;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class UnicodeFontRenderer extends FontRenderer {
	private final UnicodeFont font;
	HashMap<String, Float> widthMap = new HashMap<>();
	HashMap<String, Float> heightMap = new HashMap<>();
	
    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile("§[0123456789abcdefklmnor]");
    private final int[] colorCodes = {
        0x000000,
        0x0000AA,
        0x00AA00,
        0x00AAAA,
        0xAA0000,
        0xAA00AA,
        0xFFAA00,
        0xAAAAAA,
        0x555555,
        0x5555FF,
        0x55FF55,
        0x55FFFF,
        0xFF5555,
        0xFF55FF,
        0xFFFF55,
        0xFFFFFF
    };
	
	private static final char COLOR_INVOKER = '\247';
	
	public UnicodeFontRenderer(Font awtFont) {
		this.font = new UnicodeFont(awtFont);
		this.font.addAsciiGlyphs();
		this.font.getEffects().add(new ColorEffect(Color.WHITE));

		try {
			this.font.loadGlyphs();
		} catch (SlickException var3) {
			throw new RuntimeException(var3);
		}

		String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
		this.FONT_HEIGHT = this.font.getHeight(alphabet) / 2;
	}

	public float drawString(String string, float x, float y, int color) {
		return this.renderString(string, x, y, color);
	}

	public int renderString(String string, float x, float y, int color) {
		if (string == null) {
			return 0;
		} else {
			GL11.glPushMatrix();
			GL11.glScaled(0.5D, 0.5D, 0.5D);
			boolean blend = GL11.glIsEnabled(3042);
			boolean lighting = GL11.glIsEnabled(2896);
			boolean texture = GL11.glIsEnabled(3553);
			if (!blend) {
				GL11.glEnable(3042);
			}

			if (lighting) {
				GL11.glDisable(2896);
			}

			if (texture) {
				GL11.glDisable(3553);
			}

			x *= 2.0F;
			y *= 2.0F;
			String[] parts = COLOR_CODE_PATTERN.split(string);
	        int index = 0;
	        float originalX = x;
	        int currentColor = color;
	        char[] characters = string.toCharArray();
	        font.drawString(x, y, string, new org.newdawn.slick.Color(currentColor));
	        
			if (texture) {
				GL11.glEnable(3553);
			}

			if (lighting) {
				GL11.glEnable(2896);
			}

			if (!blend) {
				GL11.glDisable(3042);
			}

			GlStateManager.color(0.0F, 0.0F, 0.0F);
			GL11.glPopMatrix();
			GlStateManager.bindTexture(0);
			return ((int) x);
		}
	}

	public int drawStringWithShadow(String text, float x, float y, int color) {
		this.drawString(text, x + 0.26F, y + 0.26F, -16777216);
		return this.renderString(text, x, y, color);
	}

	public float getCharWidth(char c) {
		return this.getWidth(Character.toString(c));
	}

	public float getWidth(String string) {
		if (widthMap.containsKey(string)) {
			return widthMap.get(string).intValue();
		}else {
			float width = (float) (this.font.getWidth(string) / 2);
			widthMap.put(string, width);
			return (int) width;
		}
	}

	public float getHeight(String string) {
		if (heightMap.containsKey(string)) {
			return heightMap.get(string);
		}else {
			float height = (float) (this.font.getHeight(string) / 2);
			heightMap.put(string, height);
			return height;
		}
	}

	public void drawCenteredString(String text, float x, float y, int color) {
		this.drawString(text, x - (float) (this.getWidth(text) / 2), y, color);
	}

	@Override
	public void drawCenteredStringWithShadow(String text, float x, float y, int color) {
		this.drawString(text, x - (float) (this.getWidth(text) / 2) + 0.26f, y + 0.26f, -16777216);
		this.drawString(text, x - (float) (this.getWidth(text) / 2), y, color);
	}
}
