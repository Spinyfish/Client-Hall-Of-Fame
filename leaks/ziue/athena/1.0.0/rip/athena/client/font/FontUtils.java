package rip.athena.client.font;

import java.util.*;
import net.minecraft.client.*;
import java.io.*;
import rip.athena.client.utils.render.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.*;
import java.awt.*;
import net.minecraft.util.*;

public class FontUtils
{
    public float FONT_HEIGHT;
    private final CustomFont unicodeFont;
    private final int[] colorCodes;
    private float kerning;
    public HashMap<String, Float> widthMap;
    public HashMap<String, Float> heightMap;
    
    public FontUtils(final String fontName, final int fontType, final int size, final boolean allChar) {
        this(fontName, fontType, size, 0, allChar);
    }
    
    public FontUtils(final String fontName, final int fontType, final int size, final int kerning, final boolean allChar) {
        this(fontName, fontType, size, kerning, allChar, 0);
    }
    
    public FontUtils(final String fontName, final int fontType, final int size, final int kerning, final boolean allChar, final int yAddon) {
        this.FONT_HEIGHT = 0.0f;
        this.colorCodes = new int[32];
        this.widthMap = new HashMap<String, Float>();
        this.heightMap = new HashMap<String, Float>();
        this.unicodeFont = new CustomFont(this.getFont(fontName, fontType, size), true, kerning, allChar, yAddon);
        this.kerning = 0.0f;
        for (int i = 0; i < 32; ++i) {
            final int shadow = (i >> 3 & 0x1) * 85;
            int red = (i >> 2 & 0x1) * 170 + shadow;
            int green = (i >> 1 & 0x1) * 170 + shadow;
            int blue = (i & 0x1) * 170 + shadow;
            if (i == 6) {
                red += 85;
            }
            if (i >= 16) {
                red /= 4;
                green /= 4;
                blue /= 4;
            }
            this.colorCodes[i] = ((red & 0xFF) << 16 | (green & 0xFF) << 8 | (blue & 0xFF));
        }
        this.FONT_HEIGHT = this.getHeight("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
    }
    
    private Font getFont(final String fontName, final int fontType, final int size) {
        Font font = null;
        try {
            final InputStream ex = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("Athena/font/" + fontName)).getInputStream();
            font = Font.createFont(0, ex);
            font = font.deriveFont(fontType, (float)size);
        }
        catch (Exception var3) {
            var3.printStackTrace();
            System.err.println("Failed to load custom font");
        }
        return font;
    }
    
    public int drawString(final String text, final float x, final float y, int color) {
        if (color == 16777215) {
            color = DrawUtils.WHITE.c;
        }
        return this.drawStringWithAlpha(text, x, y, color, (color >> 24 & 0xFF) / 255.0f);
    }
    
    public void drawLimitedString(final String text, final float x, final float y, final int color, final float maxWidth) {
        this.drawLimitedStringWithAlpha(text, x, y, color, (color >> 24 & 0xFF) / 255.0f, maxWidth);
    }
    
    public void drawLimitedStringWithAlpha(String text, float x, float y, final int color, final float alpha, final float maxWidth) {
        text = this.processString(text);
        x *= 2.0f;
        y *= 2.0f;
        final float originalX = x;
        float curWidth = 0.0f;
        GL11.glPushMatrix();
        GL11.glScaled(0.5, 0.5, 0.5);
        final boolean wasBlend = GL11.glGetBoolean(3042);
        GlStateManager.enableAlpha();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3553);
        int currentColor = color;
        final char[] characters = text.toCharArray();
        int index = 0;
        for (final char c : characters) {
            if (c == '\r') {
                x = originalX;
            }
            if (c == '\n') {
                y += this.getHeight(Character.toString(c)) * 2.0f;
            }
            Label_0367: {
                if (c != '§' && (index == 0 || index == characters.length - 1 || characters[index - 1] != '§')) {
                    if (index >= 1 && characters[index - 1] == '§') {
                        break Label_0367;
                    }
                    GL11.glPushMatrix();
                    this.unicodeFont.drawString(Character.toString(c), x, y, DrawUtils.reAlpha(currentColor, alpha), false);
                    GL11.glPopMatrix();
                    curWidth += this.getStringWidth(Character.toString(c)) * 2.0f;
                    x += this.getStringWidth(Character.toString(c)) * 2.0f;
                    if (curWidth > maxWidth) {
                        break;
                    }
                }
                else if (c == ' ') {
                    x += this.unicodeFont.getWidth(" ");
                }
                else if (c == '§' && index != characters.length - 1) {
                    final int codeIndex = "0123456789abcdefklmnor".indexOf(text.charAt(index + 1));
                    if (codeIndex < 0) {
                        break Label_0367;
                    }
                    if (codeIndex < 16) {
                        currentColor = this.colorCodes[codeIndex];
                    }
                    else if (codeIndex == 21) {
                        currentColor = Color.WHITE.getRGB();
                    }
                }
                ++index;
            }
        }
        if (!wasBlend) {
            GL11.glDisable(3042);
        }
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    public int drawStringWithAlpha(String text, float x, float y, final int color, final float alpha) {
        text = this.processString(text);
        x *= 2.0f;
        y *= 2.0f;
        final float originalX = x;
        GL11.glPushMatrix();
        GL11.glScaled(0.5, 0.5, 0.5);
        final boolean wasBlend = GL11.glGetBoolean(3042);
        GlStateManager.enableAlpha();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3553);
        int currentColor = color;
        final char[] characters = text.toCharArray();
        int index = 0;
        for (final char c : characters) {
            if (c == '\r') {
                x = originalX;
            }
            if (c == '\n') {
                y += this.getHeight(Character.toString(c)) * 2.0f;
            }
            Label_0340: {
                if (c != '§' && (index == 0 || index == characters.length - 1 || characters[index - 1] != '§')) {
                    if (index >= 1 && characters[index - 1] == '§') {
                        break Label_0340;
                    }
                    GL11.glPushMatrix();
                    this.unicodeFont.drawString(Character.toString(c), x, y, DrawUtils.reAlpha(currentColor, alpha), false);
                    GL11.glPopMatrix();
                    x += this.getStringWidth(Character.toString(c)) * 2.0f;
                }
                else if (c == ' ') {
                    x += this.unicodeFont.getWidth(" ");
                }
                else if (c == '§' && index != characters.length - 1) {
                    final int codeIndex = "0123456789abcdefklmnor".indexOf(text.charAt(index + 1));
                    if (codeIndex < 0) {
                        break Label_0340;
                    }
                    if (codeIndex < 16) {
                        currentColor = this.colorCodes[codeIndex];
                    }
                    else if (codeIndex == 21) {
                        currentColor = Color.WHITE.getRGB();
                    }
                }
                ++index;
            }
        }
        if (!wasBlend) {
            GL11.glDisable(3042);
        }
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        return (int)x;
    }
    
    private String processString(String text) {
        String str = "";
        for (final char c : text.toCharArray()) {
            if ((c < '\uc350' || c > '\uea60') && c != '\u26bd') {
                str += c;
            }
        }
        text = str.replace("§r", "").replace('\u25ac', '=').replace('\u2764', '\u2665').replace('\u22c6', '\u2606').replace('\u2620', '\u2606').replace('\u2730', '\u2606').replace("\u272b", "\u2606").replace("\u2719", "+");
        text = text.replace('\u2b05', '\u2190').replace('\u2b06', '\u2191').replace('\u2b07', '\u2193').replace('\u27a1', '\u2192').replace('\u2b08', '\u2197').replace('\u2b0b', '\u2199').replace('\u2b09', '\u2196').replace('\u2b0a', '\u2198');
        return text;
    }
    
    public void drawStringWithGudShadow(final String text, final float x, final float y, final int color) {
        this.drawString(StringUtils.stripControlCodes(text), x + 1.0f, y + 1.0f, this.getShadowColor(color).getRGB());
        this.drawString(text, x, y, color);
    }
    
    public void drawStringWithShadowForChat(final String text, final float x, final float y, final int color) {
        this.drawString(StringUtils.stripControlCodes(text), x + 1.0f, y + 1.0f, this.getShadowColor(color).getRGB());
        this.drawString(text, x, y, color);
    }
    
    public int drawStringWithShadow(final String text, final float x, final float y, final int color) {
        this.drawStringWithAlpha(StringUtils.stripControlCodes(text), x + 0.5f, y + 0.4f, 0, (color >> 24 & 0xFF) / 255.0f);
        return this.drawString(text, x, y, color);
    }
    
    public void drawStringWithSuperShadow(final String text, final float x, final float y, final int color) {
        this.drawStringWithAlpha(StringUtils.stripControlCodes(text), x + 0.8f, y + 0.8f, 0, (color >> 24 & 0xFF) / 255.0f);
        this.drawString(text, x, y, color);
    }
    
    public int drawCenteredString(final String text, final float x, final float y, final int color) {
        return this.drawString(text, x - this.getStringWidth(text) / 2.0f, y, color);
    }
    
    public void drawCenteredStringWithAlpha(final String text, final float x, final float y, final int color, final float alpha) {
        this.drawStringWithAlpha(text, x - this.getStringWidth(text) / 2.0f, y, color, alpha);
    }
    
    public void drawCenteredStringWithShadow(final String text, final float x, final float y, final int color) {
        this.drawCenteredString(StringUtils.stripControlCodes(text), x + 0.5f, y + 0.5f, -16777216);
        this.drawCenteredString(text, x, y, color);
    }
    
    private Color getShadowColor(final int hex) {
        final float a = (hex >> 24 & 0xFF) / 255.0f;
        final float r = (hex >> 16 & 0xFF) / 255.0f;
        final float g = (hex >> 8 & 0xFF) / 255.0f;
        final float b = (hex & 0xFF) / 255.0f;
        return new Color(r * 0.2f, g * 0.2f, b * 0.2f, a * 0.9f);
    }
    
    public void drawOutlinedString(final String str, final float x, final float y, final int internalCol, final int externalCol) {
        this.drawString(str, x - 0.5f, y, externalCol);
        this.drawString(str, x + 0.5f, y, externalCol);
        this.drawString(str, x, y - 0.5f, externalCol);
        this.drawString(str, x, y + 0.5f, externalCol);
        this.drawString(str, x, y, internalCol);
    }
    
    public float getStringWidth(final String s) {
        if (this.widthMap.containsKey(s)) {
            return this.widthMap.get(s);
        }
        float width = 0.0f;
        final String str = StringUtils.stripControlCodes(s);
        for (final char c : str.toCharArray()) {
            width += this.unicodeFont.getWidth(Character.toString(c)) + this.kerning;
        }
        this.widthMap.put(s, width / 2.0f);
        return width / 2.0f;
    }
    
    public float getCharWidth(final char c) {
        return (float)this.unicodeFont.getWidth(String.valueOf(c));
    }
    
    public float getHeight(final String s) {
        if (this.heightMap.containsKey(s)) {
            return this.heightMap.get(s);
        }
        final float height = this.unicodeFont.getHeight(s) / 2.0f;
        this.heightMap.put(s, height);
        return height;
    }
    
    public float getHeight() {
        return this.unicodeFont.getHeight("Athena") / 2.0f;
    }
    
    public CustomFont getFont() {
        return this.unicodeFont;
    }
    
    public String trimStringToWidth(final String text, final int width) {
        return this.trimStringToWidth(text, width, false);
    }
    
    public String trimStringToWidth(String text, final int width, final boolean reverse) {
        text = this.processString(text);
        final StringBuilder stringbuilder = new StringBuilder();
        float f = 0.0f;
        final int i = reverse ? (text.length() - 1) : 0;
        final int j = reverse ? -1 : 1;
        boolean flag = false;
        boolean flag2 = false;
        for (int k = i; k >= 0 && k < text.length() && f < width; k += j) {
            final char c0 = text.charAt(k);
            final float f2 = this.getCharWidth(c0);
            if (flag) {
                flag = false;
                if (c0 != 'l' && c0 != 'L') {
                    if (c0 == 'r' || c0 == 'R') {
                        flag2 = false;
                    }
                }
                else {
                    flag2 = true;
                }
            }
            else if (f2 < 0.0f) {
                flag = true;
            }
            else {
                f += (float)(f2 / (text.contains("=====") ? 2.2 : 2.0));
                if (flag2) {
                    ++f;
                }
            }
            if (f > width) {
                break;
            }
            if (reverse) {
                stringbuilder.insert(0, c0);
            }
            else {
                stringbuilder.append(c0);
            }
        }
        return stringbuilder.toString();
    }
    
    public String trimStringToWidth(String text, final float width, final boolean reverse) {
        text = this.processString(text);
        final StringBuilder stringbuilder = new StringBuilder();
        float f = 0.0f;
        final int i = reverse ? (text.length() - 1) : 0;
        final int j = reverse ? -1 : 1;
        boolean flag = false;
        boolean flag2 = false;
        for (int k = i; k >= 0 && k < text.length() && f < width; k += j) {
            final char c0 = text.charAt(k);
            final float f2 = this.getCharWidth(c0);
            if (flag) {
                flag = false;
                if (c0 != 'l' && c0 != 'L') {
                    if (c0 == 'r' || c0 == 'R') {
                        flag2 = false;
                    }
                }
                else {
                    flag2 = true;
                }
            }
            else if (f2 < 0.0f) {
                flag = true;
            }
            else {
                f += (float)(f2 / (text.contains("=====") ? 2.2 : 2.0));
                if (flag2) {
                    ++f;
                }
            }
            if (f > width) {
                break;
            }
            if (reverse) {
                stringbuilder.insert(0, c0);
            }
            else {
                stringbuilder.append(c0);
            }
        }
        return stringbuilder.toString();
    }
}
