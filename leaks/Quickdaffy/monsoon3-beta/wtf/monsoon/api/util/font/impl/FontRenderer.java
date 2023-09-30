/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package wtf.monsoon.api.util.font.impl;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.opengl.GL11;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.font.IFontRenderer;
import wtf.monsoon.api.util.font.impl.Image;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.impl.module.visual.ClickGUI;

public class FontRenderer
implements IFontRenderer {
    private final float height;
    private final Image defaultFont;
    private final String magicAllowedCharacters = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0";
    private final int[] hexcolors = new int[]{0, 170, 43520, 43690, 0xAA0000, 0xAA00AA, 0xFFAA00, 0xAAAAAA, 0x555555, 0x5555FF, 0x55FF55, 0x55FFFF, 0xFF5555, 0xFF55FF, 0xFFFF55, 0xFFFFFF};
    private final Random random = new Random();

    public FontRenderer(Font font) {
        this.defaultFont = new Image(font, 0, 255);
        this.height = this.defaultFont.getHeight() / 2.0f;
    }

    @Override
    public int drawStringWithShadow(String text, float x, float y, int color) {
        return this.drawString(text, x, y, new Color(color), true);
    }

    @Override
    public int drawString(String text, float x, float y, int color) {
        return this.drawString(text, x, y, new Color(color), false);
    }

    @Override
    public int drawStringWithShadow(String text, float x, float y, Color color) {
        return this.drawString(text, x, y, color, true);
    }

    @Override
    public int drawString(String text, float x, float y, Color color) {
        return this.drawString(text, x, y, color, false);
    }

    public float drawCenteredString(String text, float x, float y, Color color, boolean shadow) {
        return this.drawString(text, x - (float)this.getStringWidth(text) / 2.0f, y, color, shadow);
    }

    public void drawStringWithGradient(String text, float x, float y, Color a, Color b, boolean shadow) {
        int length = text.length();
        double factorIncrease = 1.0f / (float)length;
        double factor = 0.0;
        for (char ch : text.toCharArray()) {
            this.drawString(String.valueOf(ch), x, y, ColorUtil.interpolate(a, b, factor), shadow);
            factor += factorIncrease;
            x += (float)this.getStringWidth(String.valueOf(ch));
        }
    }

    @Override
    public int drawString(String text, float x, float y, int color, boolean dropShadow) {
        return this.drawString(text, x, y, new Color(color), dropShadow);
    }

    @Override
    public int drawString(String text, float x, float y, Color color, boolean dropShadow) {
        if (text.contains("\n")) {
            String[] parts = text.split("\n");
            float newY = 0.0f;
            for (String s : parts) {
                if (dropShadow) {
                    this.drawText(s, x + 0.6f, y + newY + 0.6f, new Color(0, 0, 0, 150), true);
                }
                this.drawText(s, x, y + newY, color, dropShadow);
                newY += (float)this.getHeight();
            }
            return 0;
        }
        if (dropShadow) {
            this.drawText(text, x + 0.5f, y + 0.5f, new Color(0, 0, 0, 150), true);
        }
        return (int)this.drawText(text, x, y, color, false);
    }

    private float drawText(String text, float x, float y, Color color, boolean ignore) {
        if (text == null || text.isEmpty()) {
            return 0.0f;
        }
        if (Wrapper.getModule(ClickGUI.class) != null && Wrapper.getModule(ClickGUI.class).british.getValue().booleanValue()) {
            text = text.replaceAll("(?i)color", "Colour");
        }
        int texture = GlStateManager.getBoundTexture();
        GL11.glPushAttrib((int)8256);
        GL11.glTranslatef((float)x, (float)y, (float)0.0f);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)3008);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        int currentcolor = color.getRGB();
        if ((currentcolor & 0xFC000000) == 0) {
            currentcolor |= 0xFF000000;
        }
        int alpha = currentcolor >> 24 & 0xFF;
        if (text.contains("\u00a7")) {
            String[] parts = text.split("\u00a7");
            float width = 0.0f;
            boolean randomCase = false;
            for (String part : parts) {
                if (part.isEmpty()) continue;
                if (part.equals(parts[0])) {
                    this.getDefaultFont().drawString(part, width, 0.0, ColorUtil.integrateAlpha(new Color(currentcolor), color.getAlpha()));
                    width += (float)this.getDefaultFont().getStringWidth(part);
                    continue;
                }
                String words = part.substring(1);
                char type = part.charAt(0);
                int index = "0123456789abcdefklmnor".indexOf(type);
                switch (index) {
                    case 0: 
                    case 1: 
                    case 2: 
                    case 3: 
                    case 4: 
                    case 5: 
                    case 6: 
                    case 7: 
                    case 8: 
                    case 9: 
                    case 10: 
                    case 11: 
                    case 12: 
                    case 13: 
                    case 14: 
                    case 15: {
                        if (!ignore) {
                            currentcolor = this.hexcolors[index] | alpha << 24;
                        }
                        randomCase = false;
                        break;
                    }
                    case 16: {
                        randomCase = true;
                        break;
                    }
                    case 18: {
                        break;
                    }
                    case 21: {
                        currentcolor = color.getRGB();
                        if ((currentcolor & 0xFC000000) == 0) {
                            currentcolor |= 0xFF000000;
                        }
                        randomCase = false;
                    }
                }
                this.getDefaultFont().drawString(randomCase ? this.randomMagicText(words) : words, width, 0.0, ColorUtil.integrateAlpha(new Color(currentcolor), color.getAlpha()));
                width += (float)this.getDefaultFont().getStringWidth(words);
            }
        } else {
            this.getDefaultFont().drawString(text, 0.0, 0.0, ColorUtil.integrateAlpha(new Color(currentcolor), color.getAlpha()));
        }
        GL11.glDisable((int)2848);
        GL11.glDisable((int)3042);
        GL11.glDisable((int)3008);
        GL11.glTranslatef((float)(-x), (float)(-y), (float)0.0f);
        if (texture >= 0) {
            GL11.glBindTexture((int)3553, (int)texture);
        }
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glPopAttrib();
        return x + (float)this.getStringWidth(text);
    }

    @Override
    public float getStringWidthF(String text) {
        if (text.contains("\u00a7")) {
            String[] parts = text.split("\u00a7");
            float width = 0.0f;
            for (String part : parts) {
                if (part.isEmpty()) continue;
                if (part.equals(parts[0])) {
                    width += (float)this.getDefaultFont().getStringWidth(part);
                    continue;
                }
                width += (float)this.getDefaultFont().getStringWidth(part.substring(1));
            }
            return width / 2.0f;
        }
        return (float)this.defaultFont.getStringWidth(text) / 2.0f;
    }

    @Override
    public int getStringWidth(String text) {
        return (int)this.getStringWidthF(text);
    }

    @Override
    public int getHeight() {
        return (int)this.height;
    }

    @Override
    public float getHeightF() {
        return this.height;
    }

    private String randomMagicText(String text) {
        StringBuilder builder = new StringBuilder();
        text.chars().mapToObj(element -> Character.valueOf((char)element)).forEach(ch -> {
            if (!ChatAllowedCharacters.isAllowedCharacter(ch.charValue())) {
                return;
            }
            builder.append("\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0".charAt(this.random.nextInt("\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0".length())));
        });
        return builder.toString();
    }

    public Image getDefaultFont() {
        return this.defaultFont;
    }
}

