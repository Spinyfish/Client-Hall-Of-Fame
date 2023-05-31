// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.utils.fontrenderer;

import java.util.Objects;
import java.io.InputStream;
import java.awt.Font;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import java.awt.FontFormatException;
import java.io.IOException;
import org.newdawn.slick.SlickException;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.Minecraft;
import java.util.HashMap;
import java.awt.Color;
import java.util.regex.Pattern;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.UnicodeFont;
import java.util.Map;

public class FontRenderer
{
    private final String path;
    private final float size;
    public final int FONT_HEIGHT = 9;
    private final Map<String, Float> cachedStringWidth;
    private float antiAliasingFactor;
    private UnicodeFont unicodeFont;
    private int prevScaleFactor;
    private final ColorEffect colorEffect;
    private final Pattern pattern;
    
    public FontRenderer(final String path, final float size) {
        this.colorEffect = new ColorEffect(Color.WHITE);
        this.pattern = Pattern.compile("§[0123456789abcdefklmnor]");
        this.path = path;
        this.size = size;
        this.cachedStringWidth = new HashMap<String, Float>();
        this.prevScaleFactor = new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
        final ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
        try {
            this.prevScaleFactor = resolution.getScaleFactor();
            (this.unicodeFont = new UnicodeFont(this.getFontFromInput(path).deriveFont(this.size * this.prevScaleFactor / 2.0f))).addAsciiGlyphs();
            this.unicodeFont.getEffects().add(new ColorEffect(Color.WHITE));
            this.unicodeFont.loadGlyphs();
            this.antiAliasingFactor = (float)resolution.getScaleFactor();
        }
        catch (SlickException | IOException | FontFormatException ex2) {
            final Exception ex;
            final Exception e = ex;
            e.printStackTrace();
        }
    }
    
    public void drawStringShadow(final String text, final float x, final float y, final Color color) {
        this.drawString(text, x + 1.0f, y + 1.0f, Color.BLACK);
        this.drawString(text, x, y, color);
    }
    
    public void drawCenteredString(final String text, final float x, final float y, final Color color) {
        this.drawString(text, x - ((int)this.getWidth(text) >> 1), y, color);
    }
    
    public void drawCenteredStringShadow(final String text, final float x, final float y, final Color color) {
        this.drawString(text, x + 1.0f, y + 1.0f, Color.BLACK);
        this.drawString(text, x - ((int)this.getWidth(text) >> 1), y, color);
    }
    
    public void drawString(final String text, float x, float y, final Color color) {
        final ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
        if (text == null) {
            return;
        }
        if (resolution.getScaleFactor() != this.prevScaleFactor) {
            try {
                this.prevScaleFactor = resolution.getScaleFactor();
                (this.unicodeFont = new UnicodeFont(this.getFontFromInput(this.path).deriveFont(this.size * this.prevScaleFactor / 2.0f))).addAsciiGlyphs();
                this.unicodeFont.getEffects().add(this.colorEffect);
                this.unicodeFont.loadGlyphs();
            }
            catch (SlickException | IOException | FontFormatException ex2) {
                final Exception ex;
                final Exception e = ex;
                throw new RuntimeException(e);
            }
        }
        this.antiAliasingFactor = (float)resolution.getScaleFactor();
        GL11.glPushMatrix();
        GL11.glScalef(1.0f / this.antiAliasingFactor, 1.0f / this.antiAliasingFactor, 1.0f / this.antiAliasingFactor);
        x *= this.antiAliasingFactor;
        y *= this.antiAliasingFactor;
        final float originalX = x;
        final float red = color.getRed() / 255.0f;
        final float green = color.getGreen() / 255.0f;
        final float blue = color.getBlue() / 255.0f;
        final float alpha = color.getAlpha() / 255.0f;
        final char[] characters = text.toCharArray();
        int index = 0;
        GL11.glColor4f(red, green, blue, alpha);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        final String[] parts = this.pattern.split(text);
        final int[] colorCodes = { 0, 170, 43520, 43690, 11141120, 11141290, 16755200, 11184810, 5592405, 5592575, 5635925, 5636095, 16733525, 16733695, 16777045, 16777215 };
        Color currentColor = color;
        String[] array;
        for (int length = (array = parts).length, i = 0; i < length; ++i) {
            final String s = array[i];
            String[] split;
            for (int length2 = (split = s.split("\n")).length, j = 0; j < length2; ++j) {
                final String s2 = split[j];
                String[] split2;
                for (int length3 = (split2 = s2.split("\r")).length, k = 0; k < length3; ++k) {
                    final String s3 = split2[k];
                    this.unicodeFont.drawString(x, y, s3, new org.newdawn.slick.Color(currentColor.getRGB()));
                    x += this.unicodeFont.getWidth(s3);
                    index += s3.length();
                    if (index < characters.length && characters[index] == '\r') {
                        x = originalX;
                        ++index;
                    }
                }
                if (index < characters.length && characters[index] == '\n') {
                    x = originalX;
                    y += this.getHeight(s2) * 2.0f;
                    ++index;
                }
            }
            if (index < characters.length) {
                final char colorCode = characters[index];
                if (colorCode == '§') {
                    final char colorChar = characters[index + 1];
                    final int codeIndex = "0123456789abcdef".indexOf(colorChar);
                    if (codeIndex < 0) {
                        if (colorChar == 'r') {
                            currentColor = color;
                        }
                    }
                    else {
                        currentColor = new Color(colorCodes[codeIndex]);
                    }
                    index += 2;
                }
            }
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
    }
    
    public float getWidth(final String text) {
        final String textWithOutColorCodes = text.replace("§0", "").replace("§1", "").replace("§2", "").replace("§3", "").replace("§4", "").replace("§5", "").replace("§6", "").replace("§7", "").replace("§8", "").replace("§9", "").replace("§a", "").replace("§b", "").replace("§c", "").replace("§d", "").replace("§e", "").replace("§f", "").replace("§k", "").replace("§l", "").replace("§m", "").replace("§n", "").replace("§o", "").replace("§r", "");
        if (this.cachedStringWidth.size() > 1000) {
            this.cachedStringWidth.clear();
        }
        return this.cachedStringWidth.computeIfAbsent(text, e -> this.unicodeFont.getWidth(textWithOutColorCodes) / this.antiAliasingFactor);
    }
    
    public String getPath() {
        return this.path;
    }
    
    public float getSize() {
        return this.size;
    }
    
    private Font getFontFromInput(final String path) throws IOException, FontFormatException {
        return Font.createFont(0, Objects.requireNonNull(FontRenderer.class.getClassLoader().getResourceAsStream(path)));
    }
    
    public float getHeight(final String s) {
        return this.unicodeFont.getHeight(s) / 2.0f;
    }
}
