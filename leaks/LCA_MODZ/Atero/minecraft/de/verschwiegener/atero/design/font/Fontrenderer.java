package de.verschwiegener.atero.design.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

import de.verschwiegener.atero.Management;
import net.minecraft.util.StringUtils;

public class Fontrenderer {

    private final UnicodeFont unicodefont;

    public Fontrenderer(final Font font, final float fontSize, final boolean bold, final boolean italic) {
	unicodefont = new UnicodeFont(font, (int) (fontSize * 5F), bold, italic);
	unicodefont.getEffects().add(new ColorEffect(Color.white));
	unicodefont.addAsciiGlyphs();
	try {
	    unicodefont.loadGlyphs();
	} catch (final Exception e) {
	    e.printStackTrace();
	}
    }

    public void drawString(final String text, final float x, final float y, final int color) {
	if (text == null)
	    return;
	GL11.glPushMatrix();
	GL11.glPushAttrib(1048575);
	GL11.glDisable(2929);
	GL11.glDisable(3553);
	GL11.glEnable(2848);
	GL11.glEnable(3042);
	GL11.glBlendFunc(770, 771);

	GL11.glScaled(0.5F, 0.5F, 0.5F);
	
	boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean lighting = GL11.glIsEnabled(GL11.GL_LIGHTING);
        boolean texture = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
        if (!blend)
            GL11.glEnable(GL11.GL_BLEND);
        if (lighting)
            GL11.glDisable(GL11.GL_LIGHTING);
        if (texture)
            GL11.glDisable(GL11.GL_TEXTURE_2D);

	unicodefont.drawString(x, y, text, new org.newdawn.slick.Color(color));
	
	if (texture)
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        if (lighting)
            GL11.glEnable(GL11.GL_LIGHTING);
        if (!blend)
            GL11.glDisable(GL11.GL_BLEND);

	GL11.glDisable(3042);
	GL11.glEnable(3553);
	GL11.glEnable(2929);
	GL11.glDisable(2848);
	GL11.glDisable(3042);
	GL11.glPopAttrib();
	GL11.glPopMatrix();
    }

    public void drawStringWithShadows(final String text, final int x, final int y, final float depth, final int color) {
	drawString(StringUtils.stripControlCodes(text), x + depth, y + depth, 0x000000);
	drawString(text, x, y, color);
    }

    public int getStringHeight(final String text) {
	return unicodefont.getHeight(text) / 2;
    }

    public int getBaseStringHeight() {
	final String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
	return unicodefont.getHeight(alphabet) / 2;
    }

    public int getSpaceWidth() {
	return unicodefont.getSpaceWidth();
    }

    public int getStringWidth(final String text) {
	return unicodefont.getWidth(text);
    }

    public int getStringWidth2(final String text) {
	return unicodefont.getWidth(text) / 2;
    }

}
