package crispy.fonts.greatfont;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.StringUtils;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author: AmirCC
 * 11:47 pm, 09/10/2020, Monday
 * I made it from a youtube tutorial, But i improved it, Spacing/Smooting/GL_TEXTURE_2D Glitches!
 * The chat formatting colors are taken from roberts[Robert Kjellberg] CFontRenderer.
 **/
public class TTFRenderer {

    /**
     * TODO's V
     * - It most extend Minecraft's font renderer so you can easily switch between ascii and ttf font rendering.
     * - Need to fix the spacing between characters for arial and some other fonts.
     * - It wont load some ttf fonts, idk its because of the font names/fontrenderer or maybe intellij retarded, needs to be fixed.
     */

    private final UnicodeFont unicodeFont;
    private final int[] colorCodes = new int[32];
    //spacing between each character
    private final float spacing;

    public TTFRenderer(String fontName, int fontType, int size) {
        this(fontName, fontType, size, 0);
    }

    public TTFRenderer(String fontName, int fontType, int size, float spacing) {
        this.unicodeFont = new UnicodeFont(new Font(fontName, fontType, size));
        this.spacing = spacing;
        this.unicodeFont.addAsciiGlyphs();
        this.unicodeFont.getEffects().add(new ColorEffect(java.awt.Color.WHITE));

        try {
            this.unicodeFont.loadGlyphs();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 32; i++) {
            int shadow = (i >> 3 & 1) * 85;
            int red = (i >> 2 & 1) * 170 + shadow;
            int green = (i >> 1 & 1) * 170 + shadow;
            int blue = (i >> 0 & 1) * 170 + shadow;
            if (i == 6) {
                red += 85;
            }
            if (i >= 16) {
                red /= 4;
                green /= 4;
                blue /= 4;
            }
            this.colorCodes[i] = (red & 255) << 16 | (green & 255) << 8 | blue & 255;
        }

    }

    public int drawString(String text, float x, float y, int color) {
        x *= 2.0F;
        y *= 2.0F;
        float originalX = x;
        GL11.glPushMatrix();
        //I am not retarded, Scaling it down just to make it smoother
        GL11.glScaled(0.5F, 0.5F, 0.5F);
        boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean lighting = GL11.glIsEnabled(GL11.GL_LIGHTING);
        boolean texture = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);
        if (!blend)
            glEnable(GL11.GL_BLEND);
        if (lighting)
            glDisable(GL11.GL_LIGHTING);
        if (texture)
            glDisable(GL11.GL_TEXTURE_2D);

        int currentColor = color;
        char[] characters = text.toCharArray();

        int index = 0;
        for (char c : characters) {
            if (c == '\r') {
                x = originalX;
            }
            if (c == '\n') {
                y += getHeight(Character.toString(c)) * 2.0F;
            }
            if (c != '\247' && (index == 0 || index == characters.length - 1 || characters[index - 1] != '\247')) {
                unicodeFont.drawString(x, y, Character.toString(c), new org.newdawn.slick.Color(currentColor));
                x += (getWidth(Character.toString(c)) * 2.0F);
            } else if (c == ' ') {
                x += unicodeFont.getSpaceWidth();
            } else if (c == '\247' && index != characters.length - 1) {
                int codeIndex = "0123456789abcdefg".indexOf(text.charAt(index + 1));
                if (codeIndex < 0) continue;
                int col = this.colorCodes[codeIndex];
                currentColor = col;
            }
            index++;
        }
        GL11.glScaled(2.0F, 2.0F, 2.0F);
        if (texture) {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            //This will prevent visual glitches
            GlStateManager.bindCurrentTexture();
            GlStateManager.bindTexture(0);
        }
        if (lighting)
            GL11.glEnable(GL11.GL_LIGHTING);
        if (!blend)
            GL11.glDisable(GL11.GL_BLEND);
        glPopMatrix();
        return (int) x;
    }

    public int drawStringWithShadow(String text, float x, float y, int color) {
        drawString(StringUtils.stripControlCodes(text), x + 0.5F, y + 0.5F, 0x000000);
        return drawString(text, x, y, color);
    }

    public void drawCenteredString(String text, float x, float y, int color) {
        drawString(text, x - (int) getWidth(text) / 2, y, color);
    }

    public void drawCenteredStringWithShadow(String text, float x, float y, int color) {
        drawCenteredString(StringUtils.stripControlCodes(text), x + 0.5F, y + 0.5F, 0x000000);
        drawCenteredString(text, x, y, color);
    }

    public float getWidth(String s) {
        float width = 0.0F;

        String str = StringUtils.stripControlCodes(s);
        for (char c : str.toCharArray()) {
            width += unicodeFont.getWidth(Character.toString(c)) + this.spacing;
        }

        return width / 2.0F;
    }

    public float getCharWidth(char c) {
        return unicodeFont.getWidth(String.valueOf(c));
    }

    public float getHeight(String s) {
        return unicodeFont.getHeight(s) / 2.0F;
    }

    public UnicodeFont getFont() {
        return this.unicodeFont;
    }

}
