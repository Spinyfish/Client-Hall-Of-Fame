package felix.management;

import java.awt.Font;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.lwjgl.opengl.GL11;

import felix.util.font.FontRenderer;
import felix.util.font.TTFFontRenderer;
import felix.util.font.UnicodeFontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class FontManager {

	private FontRenderer defaultFont;
	
	boolean unicode;
	
	private HashMap<String, FontRenderer> fonts = new HashMap<>();

	public FontRenderer getFont(String key, boolean unicode) {
		this.unicode = unicode;
		return fonts.getOrDefault(key, defaultFont);
	}

	public FontManager() {
		defaultFont = new UnicodeFontRenderer(new Font("Verdana", Font.PLAIN, 18));
		try {
			for (int i : new int[] { 2, 4, 6, 8, 10, 12, 14, 15, 16, 17, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36, 38, 40,
					42, 44, 46, 48, 50, 52 }) {
				InputStream istream = getClass().getResourceAsStream("/assets/minecraft/fonts/Display.ttf");
				Font myFont = Font.createFont(Font.PLAIN, istream);
				myFont = myFont.deriveFont(Font.PLAIN, i);
				fonts.put("Display " + i, unicode ? new UnicodeFontRenderer(myFont) : new TTFFontRenderer(myFont));
			}

			for (int i : new int[] { 2, 4, 6, 8, 10, 12, 14, 15, 16, 17, 18, 20, 22, 24, 26, 28, 30, 32, 34, 35, 36, 38,
					40, 42, 44, 46, 48, 50, 52 }) {
				InputStream istream = getClass().getResourceAsStream("/assets/minecraft/fonts/visitor2.ttf");
				Font myFont = Font.createFont(Font.PLAIN, istream);
				myFont = myFont.deriveFont(Font.PLAIN, i);
				fonts.put("Fatality " + i, unicode ? new UnicodeFontRenderer(myFont) : new TTFFontRenderer(myFont));
			}

			for (int i : new int[] { 2, 4, 6, 8, 10, 12, 14, 15, 16, 17, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36, 38, 40,
					42, 44, 46, 48, 50, 52 }) {
				fonts.put("Tahoma " + i, unicode ? new UnicodeFontRenderer(new Font("Tahoma", Font.PLAIN, i)) : new TTFFontRenderer(new Font("Tahoma", Font.PLAIN, i)));
			}

			fonts.put("Verdana 12", new UnicodeFontRenderer(new Font("Verdana", Font.PLAIN, 12)));

			fonts.put("Verdana Bold 16", new UnicodeFontRenderer(new Font("Verdana Bold", Font.PLAIN, 16)));
			fonts.put("Verdana Bold 20", new UnicodeFontRenderer(new Font("Verdana Bold", Font.PLAIN, 20)));
		} 
		catch (Exception ignored) {
			ignored.printStackTrace();
		}
	}
}
