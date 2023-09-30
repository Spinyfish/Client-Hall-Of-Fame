package felix.util.font;

import net.minecraft.client.gui.VanillaFontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;

public abstract class FontRenderer {

	public int FONT_HEIGHT = 9;

	public abstract int drawStringWithShadow(final String text, float x, float y, int color);
	
	public abstract float drawString(final String text, float x, float y, int color);
	
	public abstract void drawCenteredString(final String text, float x, float y, int color);
	
	public abstract float getWidth(final String text);
	
	public abstract float getHeight(final String text);

	public abstract void drawCenteredStringWithShadow(String text, float x, float y, int color);

}
