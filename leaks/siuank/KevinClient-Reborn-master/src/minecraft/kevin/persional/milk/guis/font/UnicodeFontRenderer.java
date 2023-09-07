package kevin.persional.milk.guis.font;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class UnicodeFontRenderer extends FontRenderer {
    private final UnicodeFont font;
    public HashMap widthMap = new HashMap();
    public HashMap heightMap = new HashMap();

    // TODO: 2021/5/20 中文字体加载速度过于拉胯 待修复 提供几个解决方法:1.使用Dll渲染中文，参考csgo的opengl注入外纪(老嘎嘎说的，可行?) 2.获取字体资源，用哪个字加载哪个字
    public UnicodeFontRenderer(Font awtFont, float n, int p_addGlyphs_1_, int p_addGlyphs_2_, boolean b) {
        super(Minecraft.getMinecraft().gameSettings, new ResourceLocation("textures/font/ascii.png"), Minecraft.getMinecraft().getTextureManager(), false);
        (this.font = new UnicodeFont(awtFont)).addAsciiGlyphs();
        this.font.getEffects().add(new ColorEffect(Color.WHITE));
        if (p_addGlyphs_2_ > -1 && p_addGlyphs_1_ > -1) {
            this.font.addGlyphs(p_addGlyphs_1_, p_addGlyphs_2_);
        }

        if (b) {
            this.font.addGlyphs(0, 65535);
        }

        try {
            this.font.loadGlyphs();
        } catch (SlickException var7) {
            throw new RuntimeException(var7);
        }

        this.FONT_HEIGHT = this.font.getHeight("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789") / 2;
    }

    public int drawOutlinedString(String str, float x, float y, int color) {
        String replaced = this.replaceColor(str);
        this.drawString(replaced, x - 0.3F, y, true);
        this.drawString(replaced, x + 0.3F, y, true);
        this.drawString(replaced, x, y + 0.3F, true);
        this.drawString(replaced, x, y - 0.3F, true);
        return this.drawString(str, x, y, color);
    }

    public String replaceColor(String str) {
        str = str.replaceAll("\u00a71", "\u00a70");
        str = str.replaceAll("\u00a72", "\u00a70");
        str = str.replaceAll("\u00a73", "\u00a70");
        str = str.replaceAll("\u00a74", "\u00a70");
        str = str.replaceAll("\u00a75", "\u00a70");
        str = str.replaceAll("\u00a76", "\u00a70");
        str = str.replaceAll("\u00a77", "\u00a70");
        str = str.replaceAll("\u00a78", "\u00a70");
        str = str.replaceAll("\u00a79", "\u00a70");
        str = str.replaceAll("\u00a70", "\u00a70");
        str = str.replaceAll("\u00a7a", "\u00a70");
        str = str.replaceAll("\u00a7b", "\u00a70");
        str = str.replaceAll("\u00a7c", "\u00a70");
        str = str.replaceAll("\u00a7d", "\u00a70");
        str = str.replaceAll("\u00a7e", "\u00a70");
        str = str.replaceAll("\u00a7f", "\u00a70");
        str = str.replaceAll("\u00a7r", "\u00a70");
        str = str.replaceAll("\u00a7A", "\u00a70");
        str = str.replaceAll("\u00a7B", "\u00a70");
        str = str.replaceAll("\u00a7C", "\u00a70");
        str = str.replaceAll("\u00a7D", "\u00a70");
        str = str.replaceAll("\u00a7E", "\u00a70");
        str = str.replaceAll("\u00a7F", "\u00a70");
        str = str.replaceAll("\u00a7R", "\u00a70");
        str = str.replaceAll("\u00a7r", "\u00a70");
        return str;
    }

    public int drawString(String text, float x, float y, boolean always) {
        if (text == null) {
            return 0;
        } else {
            text = "\u00a7r" + text + "\u00a7r";
            float len = -1.0F;
            String[] var11;
            int var10 = (var11 = text.split("\u00a7")).length;

            for (int var9 = 0; var9 < var10; ++var9) {
                String str = var11[var9];
                if (str.length() >= 1) {
                    Color col = Color.BLACK;
                    str = str.substring(1, str.length());
                    this.Draw(str, x + len, y, (new Color(col.getRed(), col.getGreen(), col.getBlue())).getRGB());
                    len += (float) (this.getStringWidth(str) + 1);
                }
            }

            return (int) len;
        }
    }

    public int drawString(String text, int x, int y, int color) {
        int ColorBak = color;

        if (text == null) {
            return 0;
        } else {
            text = "\u00a7r" + text + "\u00a7r";
            float len = -1.0F;
            String[] var12;
            int var11 = (var12 = text.split("\u00a7")).length;

            for (int var10 = 0; var10 < var11; ++var10) {
                String str = var12[var10];
                if (str.length() >= 1) {
                    switch (str.charAt(0)) {
                        case '0':
                            color = (new Color(0, 0, 0)).getRGB();
                            break;
                        case '1':
                            color = (new Color(0, 0, 170)).getRGB();
                            break;
                        case '2':
                            color = (new Color(0, 170, 0)).getRGB();
                            break;
                        case '3':
                            color = (new Color(0, 170, 170)).getRGB();
                            break;
                        case '4':
                            color = (new Color(170, 0, 0)).getRGB();
                            break;
                        case '5':
                            color = (new Color(170, 0, 170)).getRGB();
                            break;
                        case '6':
                            color = (new Color(255, 170, 0)).getRGB();
                            break;
                        case '7':
                            color = (new Color(170, 170, 170)).getRGB();
                            break;
                        case '8':
                            color = (new Color(85, 85, 85)).getRGB();
                            break;
                        case '9':
                            color = (new Color(85, 85, 255)).getRGB();
                            break;
                        case 'a':
                            color = (new Color(85, 255, 85)).getRGB();
                            break;
                        case 'b':
                            color = (new Color(85, 255, 255)).getRGB();
                            break;
                        case 'c':
                            color = (new Color(255, 85, 85)).getRGB();
                            break;
                        case 'd':
                            color = (new Color(255, 85, 255)).getRGB();
                            break;
                        case 'e':
                            color = (new Color(255, 255, 85)).getRGB();
                            break;
                        case 'f':
                            color = (new Color(255, 255, 255)).getRGB();
                            break;
                        case 'r':
                            color = ColorBak;
                    }

                    Color col = new Color(color);
                    str = str.substring(1);
                    this.Draw(str, (float) x + len, (float) y, (new Color(col.getRed(), col.getGreen(), col.getBlue())).getRGB());
                    len += (float) (this.getStringWidth(str) + 1);
                }
            }

            return (int) len;
        }
    }

    public int drawString(String text, float x, float y, int color, boolean dorpshadow) {
        int ColorBak = color;

        if (text == null) {
            return 0;
        } else {
            text = "\u00a7r" + text + "\u00a7r";
            float len = -1.0F;
            String[] var13;
            int var12 = (var13 = text.split("\u00a7")).length;

            for (int var11 = 0; var11 < var12; ++var11) {
                String str = var13[var11];
                if (str.length() >= 1) {
                    switch (str.charAt(0)) {
                        case '0':
                            color = (new Color(0, 0, 0)).getRGB();
                            break;
                        case '1':
                            color = (new Color(0, 0, 170)).getRGB();
                            break;
                        case '2':
                            color = (new Color(0, 170, 0)).getRGB();
                            break;
                        case '3':
                            color = (new Color(0, 170, 170)).getRGB();
                            break;
                        case '4':
                            color = (new Color(170, 0, 0)).getRGB();
                            break;
                        case '5':
                            color = (new Color(170, 0, 170)).getRGB();
                            break;
                        case '6':
                            color = (new Color(255, 170, 0)).getRGB();
                            break;
                        case '7':
                            color = (new Color(170, 170, 170)).getRGB();
                            break;
                        case '8':
                            color = (new Color(85, 85, 85)).getRGB();
                            break;
                        case '9':
                            color = (new Color(85, 85, 255)).getRGB();
                            break;
                        case 'a':
                            color = (new Color(85, 255, 85)).getRGB();
                            break;
                        case 'b':
                            color = (new Color(85, 255, 255)).getRGB();
                            break;
                        case 'c':
                            color = (new Color(255, 85, 85)).getRGB();
                            break;
                        case 'd':
                            color = (new Color(255, 85, 255)).getRGB();
                            break;
                        case 'e':
                            color = (new Color(255, 255, 85)).getRGB();
                            break;
                        case 'f':
                            color = (new Color(255, 255, 255)).getRGB();
                            break;
                        case 'r':
                            color = ColorBak;
                    }

                    Color col = new Color(color);
                    str = str.substring(1, str.length());
                    if (dorpshadow) {
                        this.Draw(str, x + len + 0.5F, y + 0.5F, this.getColor(0, 0, 0, 80));
                    }

                    this.Draw(str, x + len, y, (new Color(col.getRed(), col.getGreen(), col.getBlue())).getRGB());
                    len += (float) (this.getStringWidth(str) + 1);
                }
            }

            return (int) len;
        }
    }

    public int drawString(String text, float x, float y, int color, int alpha) {
        int ColorBak = color;
        if (text == null) {
            return 0;
        } else {
            text = "\u00a7r" + text + "\u00a7r";
            float len = -1.0F;
            String[] var13;
            int var12 = (var13 = text.split("\u00a7")).length;

            for (int var11 = 0; var11 < var12; ++var11) {
                String str = var13[var11];
                if (str.length() >= 1) {
                    switch (str.charAt(0)) {
                        case '0':
                            color = (new Color(0, 0, 0)).getRGB();
                            break;
                        case '1':
                            color = (new Color(0, 0, 170)).getRGB();
                            break;
                        case '2':
                            color = (new Color(0, 170, 0)).getRGB();
                            break;
                        case '3':
                            color = (new Color(0, 170, 170)).getRGB();
                            break;
                        case '4':
                            color = (new Color(170, 0, 0)).getRGB();
                            break;
                        case '5':
                            color = (new Color(170, 0, 170)).getRGB();
                            break;
                        case '6':
                            color = (new Color(255, 170, 0)).getRGB();
                            break;
                        case '7':
                            color = (new Color(170, 170, 170)).getRGB();
                            break;
                        case '8':
                            color = (new Color(85, 85, 85)).getRGB();
                            break;
                        case '9':
                            color = (new Color(85, 85, 255)).getRGB();
                            break;
                        case 'a':
                            color = (new Color(85, 255, 85)).getRGB();
                            break;
                        case 'b':
                            color = (new Color(85, 255, 255)).getRGB();
                            break;
                        case 'c':
                            color = (new Color(255, 85, 85)).getRGB();
                            break;
                        case 'd':
                            color = (new Color(255, 85, 255)).getRGB();
                            break;
                        case 'e':
                            color = (new Color(255, 255, 85)).getRGB();
                            break;
                        case 'f':
                            color = (new Color(255, 255, 255)).getRGB();
                            break;
                        case 'r':
                            color = ColorBak;
                    }

                    Color col = new Color(color);
                    str = str.substring(1, str.length());
                    this.Draw(str, x + len, y, (new Color(col.getRed(), col.getGreen(), alpha)).getRGB());
                    len += (float) (this.getStringWidth(str) + 1);
                }
            }

            return (int) len;
        }
    }

    public int drawStringWithShadow(String text, float x, float y, int color, int alpha) {
        int ColorBak = color;

        if (text == null) {
            return 0;
        } else {
            text = "\u00a7r" + text + "\u00a7r";
            float len = -1.0F;
            String[] var13;
            int var12 = (var13 = text.split("\u00a7")).length;

            for (int var11 = 0; var11 < var12; ++var11) {
                String str = var13[var11];
                if (str.length() >= 1) {
                    switch (str.charAt(0)) {
                        case '0':
                            color = (new Color(0, 0, 0)).getRGB();
                            break;
                        case '1':
                            color = (new Color(0, 0, 170)).getRGB();
                            break;
                        case '2':
                            color = (new Color(0, 170, 0)).getRGB();
                            break;
                        case '3':
                            color = (new Color(0, 170, 170)).getRGB();
                            break;
                        case '4':
                            color = (new Color(170, 0, 0)).getRGB();
                            break;
                        case '5':
                            color = (new Color(170, 0, 170)).getRGB();
                            break;
                        case '6':
                            color = (new Color(255, 170, 0)).getRGB();
                            break;
                        case '7':
                            color = (new Color(170, 170, 170)).getRGB();
                            break;
                        case '8':
                            color = (new Color(85, 85, 85)).getRGB();
                            break;
                        case '9':
                            color = (new Color(85, 85, 255)).getRGB();
                            break;
                        case 'a':
                            color = (new Color(85, 255, 85)).getRGB();
                            break;
                        case 'b':
                            color = (new Color(85, 255, 255)).getRGB();
                            break;
                        case 'c':
                            color = (new Color(255, 85, 85)).getRGB();
                            break;
                        case 'd':
                            color = (new Color(255, 85, 255)).getRGB();
                            break;
                        case 'e':
                            color = (new Color(255, 255, 85)).getRGB();
                            break;
                        case 'f':
                            color = (new Color(255, 255, 255)).getRGB();
                            break;
                        case 'r':
                            color = ColorBak;
                    }

                    Color col = new Color(color);
                    str = str.substring(1, str.length());
                    int Shadowcolor = (color & 16579836) >> 2 | color & -16777216;
                    this.Draw(str, x + len + 0.5F, y + 0.5F, this.getColor(0, 0, 0, 80));
                    this.Draw(str, x + len, y, this.getColor(col.getRed(), col.getGreen(), col.getBlue(), alpha));
                    len += (float) (this.getStringWidth(str) + 1);
                }
            }

            return (int) len;
        }
    }

    public int drawString(String text, float x, float y, int color) {
        int ColorBak = color;
        if (text == null) {
            return 0;
        } else {
            text = "\u00a7r" + text + "\u00a7r";
            float len = -1.0F;
            String[] var12;
            int var11 = (var12 = text.split("\u00a7")).length;

            for (int var10 = 0; var10 < var11; ++var10) {
                String str = var12[var10];
                if (str.length() >= 1) {
                    switch (str.charAt(0)) {
                        case '0':
                            color = (new Color(0, 0, 0)).getRGB();
                            break;
                        case '1':
                            color = (new Color(0, 0, 170)).getRGB();
                            break;
                        case '2':
                            color = (new Color(0, 170, 0)).getRGB();
                            break;
                        case '3':
                            color = (new Color(0, 170, 170)).getRGB();
                            break;
                        case '4':
                            color = (new Color(170, 0, 0)).getRGB();
                            break;
                        case '5':
                            color = (new Color(170, 0, 170)).getRGB();
                            break;
                        case '6':
                            color = (new Color(255, 170, 0)).getRGB();
                            break;
                        case '7':
                            color = (new Color(170, 170, 170)).getRGB();
                            break;
                        case '8':
                            color = (new Color(85, 85, 85)).getRGB();
                            break;
                        case '9':
                            color = (new Color(85, 85, 255)).getRGB();
                            break;
                        case 'a':
                            color = (new Color(85, 255, 85)).getRGB();
                            break;
                        case 'b':
                            color = (new Color(85, 255, 255)).getRGB();
                            break;
                        case 'c':
                            color = (new Color(255, 85, 85)).getRGB();
                            break;
                        case 'd':
                            color = (new Color(255, 85, 255)).getRGB();
                            break;
                        case 'e':
                            color = (new Color(255, 255, 85)).getRGB();
                            break;
                        case 'f':
                            color = (new Color(255, 255, 255)).getRGB();
                            break;
                        case 'r':
                            color = ColorBak;
                    }

                    Color col = new Color(color);
                    str = str.substring(1, str.length());
                    this.Draw(str, x + len, y, (new Color(col.getRed(), col.getGreen(), col.getBlue())).getRGB());
                    len += (float) (this.getStringWidth(str) + 1);
                }
            }

            return (int) len;
        }
    }

    public int getColor(int red, int green, int blue, int alpha) {
        byte color = 0;
        int color1 = color | alpha << 24;
        color1 |= red << 16;
        color1 |= green << 8;
        color1 |= blue;
        return color1;
    }

    private int Draw(String string, float x, float y, int color) {
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

        this.font.drawString(x *= 2.0F, y * 2.0F - 8.0F, string, new org.newdawn.slick.Color(color));
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
        GlStateManager.bindCurrentTexture();
        return (int) x;
    }

    public int drawStringWithShadow(String text, int x, int y, int color) {
        int ColorBak = color;

        if (text == null) {
            return 0;
        } else {
            text = "\u00a7r" + text + "\u00a7r";
            float len = -1.0F;
            String[] var12;
            int var11 = (var12 = text.split("\u00a7")).length;

            for (int var10 = 0; var10 < var11; ++var10) {
                String str = var12[var10];
                if (str.length() >= 1) {
                    switch (str.charAt(0)) {
                        case '0':
                            color = (new Color(0, 0, 0)).getRGB();
                            break;
                        case '1':
                            color = (new Color(0, 0, 170)).getRGB();
                            break;
                        case '2':
                            color = (new Color(0, 170, 0)).getRGB();
                            break;
                        case '3':
                            color = (new Color(0, 170, 170)).getRGB();
                            break;
                        case '4':
                            color = (new Color(170, 0, 0)).getRGB();
                            break;
                        case '5':
                            color = (new Color(170, 0, 170)).getRGB();
                            break;
                        case '6':
                            color = (new Color(255, 170, 0)).getRGB();
                            break;
                        case '7':
                            color = (new Color(170, 170, 170)).getRGB();
                            break;
                        case '8':
                            color = (new Color(85, 85, 85)).getRGB();
                            break;
                        case '9':
                            color = (new Color(85, 85, 255)).getRGB();
                            break;
                        case 'a':
                            color = (new Color(85, 255, 85)).getRGB();
                            break;
                        case 'b':
                            color = (new Color(85, 255, 255)).getRGB();
                            break;
                        case 'c':
                            color = (new Color(255, 85, 85)).getRGB();
                            break;
                        case 'd':
                            color = (new Color(255, 85, 255)).getRGB();
                            break;
                        case 'e':
                            color = (new Color(255, 255, 85)).getRGB();
                            break;
                        case 'f':
                            color = (new Color(255, 255, 255)).getRGB();
                            break;
                        case 'r':
                            color = ColorBak;
                    }

                    Color col = new Color(color);
                    str = str.substring(1, str.length());
                    int Shadowcolor = (color & 16579836) >> 2 | color & -16777216;
                    this.Draw(str, (float) x + len + 0.5F, (float) y + 0.5F, this.getColor(0, 0, 0, 80));
                    this.Draw(str, (float) x + len, (float) y, this.getColor(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha()));
                    len += (float) (this.getStringWidth(str) + 1);
                }
            }

            return (int) len;
        }
    }

    public int drawStringWithShadow(String text, float x, float y, int color) {
        if(font == null)
            return 0;
        int ColorBak = color;
        if (text == null) {
            return 0;
        } else {
            text = "\u00a7r" + text + "\u00a7r";
            float len = -1.0F;
            String[] var12;
            int var11 = (var12 = text.split("\u00a7")).length;

            for (int var10 = 0; var10 < var11; ++var10) {
                String str = var12[var10];
                if (str.length() >= 1) {
                    switch (str.charAt(0)) {
                        case '0':
                            color = (new Color(0, 0, 0)).getRGB();
                            break;
                        case '1':
                            color = (new Color(0, 0, 170)).getRGB();
                            break;
                        case '2':
                            color = (new Color(0, 170, 0)).getRGB();
                            break;
                        case '3':
                            color = (new Color(0, 170, 170)).getRGB();
                            break;
                        case '4':
                            color = (new Color(170, 0, 0)).getRGB();
                            break;
                        case '5':
                            color = (new Color(170, 0, 170)).getRGB();
                            break;
                        case '6':
                            color = (new Color(255, 170, 0)).getRGB();
                            break;
                        case '7':
                            color = (new Color(170, 170, 170)).getRGB();
                            break;
                        case '8':
                            color = (new Color(85, 85, 85)).getRGB();
                            break;
                        case '9':
                            color = (new Color(85, 85, 255)).getRGB();
                            break;
                        case 'a':
                            color = (new Color(85, 255, 85)).getRGB();
                            break;
                        case 'b':
                            color = (new Color(85, 255, 255)).getRGB();
                            break;
                        case 'c':
                            color = (new Color(255, 85, 85)).getRGB();
                            break;
                        case 'd':
                            color = (new Color(255, 85, 255)).getRGB();
                            break;
                        case 'e':
                            color = (new Color(255, 255, 85)).getRGB();
                            break;
                        case 'f':
                            color = (new Color(255, 255, 255)).getRGB();
                            break;
                        case 'r':
                            color = ColorBak;
                    }

                    Color col = new Color(color);
                    str = str.substring(1, str.length());
                    int Shadowcolor = (color & 16579836) >> 2 | color & -16777216;
                    this.Draw(str, x + len + 0.5F, y + 0.5F, this.getColor(0, 0, 0, 80));
                    this.Draw(str, x + len, y, this.getColor(col.getRed(), col.getGreen(), col.getBlue(), col.getAlpha()));
                    len += (float) (this.getStringWidth(str) + 1);
                }
            }

            return (int) len;
        }
    }

    public int getCharWidth(char c) {
        return this.getStringWidth(Character.toString(c));
    }

    public int getStringWidth(String string) {


        float len = -1.0F;
        string = "\u00a7r" + string;
        String[] var8;
        int var7 = (var8 = string.split("\u00a7")).length;

        for (int var6 = 0; var6 < var7; ++var6) {
            String str = var8[var6];
            if (str.length() >= 1) {
                str = str.substring(1, str.length());
                len += (float) (this.font.getWidth(str) / 2 + 1);
            }
        }

        return (int) len;
    }

    public int getStringHeight(String string) {
        return this.font.getHeight(string) / 2;
    }

    public void drawCenteredString(String text, float x, float y, int color) {
        this.drawString(text, x - (float) (this.getStringWidth(text) / 2), y, color);
    }
}
