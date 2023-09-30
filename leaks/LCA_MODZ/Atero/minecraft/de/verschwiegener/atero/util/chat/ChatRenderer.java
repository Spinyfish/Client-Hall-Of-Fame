package de.verschwiegener.atero.util.chat;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.design.font.Fontrenderer;
import de.verschwiegener.atero.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.Random;

public class ChatRenderer {

    HashMap<String, Color> colorCodes = new HashMap<>();
    Fontrenderer fontRenderer, fontRendererBold, fontRendererItalic;

    Color[] codes;

    Color messagecolor;
    boolean boldStyle, italicStyle, underlineStyle, strikethroughStyle, randomStyle, effekt;
    Random rnd = new Random();

    public ChatRenderer() {

        codes = new Color[32];
        codes[0] = new Color(0, 0, 0);
        codes[1] = new Color(0, 0, 170);
        codes[2] = new Color(0, 170, 0);
        codes[3] = new Color(0, 170, 170);
        codes[4] = new Color(170, 0, 0);
        codes[5] = new Color(170, 0, 170);
        codes[6] = new Color(255, 170, 0);
        codes[7] = new Color(170, 170, 170);
        codes[8] = new Color(85, 85, 85);
        codes[9] = new Color(85, 85, 255);
        codes[10] = new Color(85, 255, 85);
        codes[11] = new Color(85, 255, 255);
        codes[12] = new Color(255, 85, 85);
        codes[13] = new Color(255, 85, 255);
        codes[14] = new Color(255, 255, 85);
        codes[15] = new Color(255, 255, 255);
        codes[16] = new Color(85, 255, 255);

        colorCodes.putIfAbsent("0", new Color(0, 0, 0, 255));
        colorCodes.putIfAbsent("1", new Color(0, 0, 170, 255));
        colorCodes.putIfAbsent("2", new Color(0, 170, 0, 255));
        colorCodes.putIfAbsent("3", new Color(0, 170, 170, 255));
        colorCodes.putIfAbsent("4", new Color(170, 0, 0, 255));
        colorCodes.putIfAbsent("5", new Color(170, 0, 170, 255));
        colorCodes.putIfAbsent("6", new Color(255, 170, 0, 255));
        colorCodes.putIfAbsent("7", new Color(170, 170, 170, 255));
        colorCodes.putIfAbsent("8", new Color(85, 85, 85, 255));
        colorCodes.putIfAbsent("9", new Color(85, 85, 255, 255));
        colorCodes.putIfAbsent("a", new Color(85, 255, 85, 255));
        colorCodes.putIfAbsent("b", new Color(85, 255, 255, 255));
        colorCodes.putIfAbsent("c", new Color(255, 85, 85, 255));
        colorCodes.putIfAbsent("d", new Color(255, 85, 255, 255));
        colorCodes.putIfAbsent("e", new Color(255, 255, 85, 255));
        colorCodes.putIfAbsent("f", new Color(255, 255, 255, 255));
        colorCodes.putIfAbsent("g", new Color(0, 161, 249, 255));
        fontRenderer = Management.instance.fontmgr.getFontByName("Inter").getFontrenderer();
        fontRendererBold = Management.instance.fontmgr.getFontByName("InterBold").getFontrenderer();
        fontRendererItalic = Management.instance.fontmgr.getFontByName("InterItalic").getFontrenderer();
        messagecolor = Color.white;
    }

    public void drawChat(String line, int x, int y) {
        String[] args = line.replace(" ", "# ").split("#");
        int xoffset = 0;
        messagecolor = Color.WHITE;
        for (String str : args) {
            messagecolor = Color.white;
            for (String str2 : str.replace("§", "#§").split("#")) {
                if (str2.length() > 0) {
                    if (str2.startsWith("§")) {
                        System.out.println("ColorCode: " + str2.substring(1,2));
                        messagecolor = getColorCode(str2.substring(1, 2));
                        str2 = str2.substring(2);
                    }
                    xoffset = xoffset / 2;
                    //System.out.println("MessageColor: " + messagecolor);
                    if (boldStyle) { 
                        xoffset += ChatFontRenderer.drawString(str2, x + xoffset, y, messagecolor);
                        // fontRendererBold.drawString(str, x + xoffset, y, messagecolor.getRGB());
                        // xoffset += fontRendererBold.getStringWidth(str);
                    } else if (italicStyle) {
                        xoffset += ChatFontRenderer.drawString(str2, x + xoffset, y, messagecolor);
                        // fontRendererItalic.drawString(str, x + xoffset, y, messagecolor.getRGB());
                        // xoffset += fontRendererItalic.getStringWidth(str);
                    } else if (randomStyle) {
                        fontRenderer.drawString(getRandomString(str2.length()), x + xoffset, y, messagecolor.getRGB());
                        xoffset += fontRenderer.getStringWidth(str2);
                    } else {
                        xoffset += ChatFontRenderer.drawString(str2, x + xoffset, y, messagecolor);
                    }
                    if (underlineStyle) {
                    }
                }
            }
        }
    }
    public Color getColorCode(String character) {
        int i = "0123456789abcdefg".indexOf(character);
        if (i >= 0 && i < this.codes.length) {
            Color j = this.codes[i];
            return j;
        } else {
            int mode = character.charAt(0);
            // System.out.println("Mode: " + mode);
            switch (mode) {
                case 110:
                    // underline
                    effekt = true;
                    underlineStyle = true;
                    break;
                case 107:
                    // random
                    effekt = true;
                    randomStyle = true;
                    break;
                case 109:
                    // stricketrough
                    effekt = true;
                    strikethroughStyle = true;
                    break;
                case 111:
                    // italic
                    effekt = true;
                    italicStyle = true;
                    boldStyle = false;
                    break;
                case 108:
                    // bold
                    effekt = true;
                    boldStyle = true;
                    italicStyle = false;
                    break;
                case 114:
                    // reset
                    messagecolor = Color.white;
                    boldStyle = false;
                    italicStyle = false;
                    underlineStyle = false;
                    strikethroughStyle = false;
                    randomStyle = false;
                    effekt = false;
                    break;
                default:
                    System.out.println("Error: " + mode);
                    break;
            }
            return messagecolor;
        }
    }

    public void drawchat2(String line, int x, int y) {
        String[] args = line.replace("§", "#§").split("#");
        int xoffset = 0;
        messagecolor = Color.white;
        boldStyle = false;
        italicStyle = false;
        underlineStyle = false;
        strikethroughStyle = false;
        randomStyle = false;
        effekt = false;
        for (String str : args) {
            if (str.length() > 1) {
                if (colorCodes.containsKey(str.substring(1, 2))) {
                    messagecolor = colorCodes.get(str.substring(1, 2).toLowerCase());
                } else {
                    // System.out.println("Case: " + str.substring(1, 2));
                    switch (str.substring(1, 2)) {
                        case "k":
                            effekt = true;
                            randomStyle = true;
                            break;
                        case "m":
                            effekt = true;
                            strikethroughStyle = true;
                            break;
                        case "o":
                            effekt = true;
                            italicStyle = true;
                            boldStyle = false;
                            break;
                        case "l":
                            effekt = true;
                            boldStyle = true;
                            italicStyle = false;
                            break;
                        case "n":
                            effekt = true;
                            underlineStyle = true;
                            break;
                        case "r":
                            messagecolor = Color.white;
                            boldStyle = false;
                            italicStyle = false;
                            underlineStyle = false;
                            strikethroughStyle = false;
                            randomStyle = false;
                            effekt = false;
                            break;
                    }
                }
                if (effekt) {
                    if (boldStyle) {
                        if (str.startsWith("§")) {
                            Minecraft.getMinecraft().fontRendererObj.drawString(str.substring(2), x + xoffset, y, messagecolor.getRGB());
                            xoffset +=  Minecraft.getMinecraft().fontRendererObj.getStringWidth(str.substring(2));
                        } else {
                            Minecraft.getMinecraft().fontRendererObj.drawString(str, x + xoffset, y, messagecolor.getRGB());
                            xoffset +=  Minecraft.getMinecraft().fontRendererObj.getStringWidth(str);
                        }
                    } else if (italicStyle) {
                        if (str.startsWith("§")) {
                            Minecraft.getMinecraft().fontRendererObj.drawString(str.substring(2), x + xoffset, y, messagecolor.getRed());
                            xoffset += Minecraft.getMinecraft().fontRendererObj.getStringWidth(str.substring(2));
                        } else {
                            Minecraft.getMinecraft().fontRendererObj.drawString(str, x + xoffset, y, messagecolor.getRed());
                            xoffset +=  Minecraft.getMinecraft().fontRendererObj.getStringWidth(str);
                        }
                    } else if (randomStyle) {
                        Minecraft.getMinecraft().fontRendererObj.drawString(getRandomString(str.length() - 2), x + xoffset, y,
                                messagecolor.getRGB());
                        xoffset +=  Minecraft.getMinecraft().fontRendererObj.getStringWidth(str);
                    }
                    if (underlineStyle) {
                        // drawLine(x + xoffset, y + (fontRenderer.getBaseStringHeight() * 2) - 3,
                        // fontRenderer.getStringWidth2(str.substring(2)), messagecolor);
                        Minecraft.getMinecraft().fontRendererObj.drawString(str.substring(2), x + xoffset, y, messagecolor.getRGB());
                    }
                    if (strikethroughStyle) {
                        // drawLine(x + xoffset, y + (fontRenderer.getBaseStringHeight()),
                        // fontRenderer.getStringWidth(str.substring(2)), messagecolor);
                        // System.out.println("Str: " + str.substring(2));
                        Minecraft.getMinecraft().fontRendererObj.drawString(str.substring(2), x + xoffset, y, messagecolor.getRGB());
                    }
                } else {
                    if (str.startsWith("§")) {
                        Minecraft.getMinecraft().fontRendererObj.drawString(str.substring(2), x + xoffset, y, messagecolor.getRGB());
                        xoffset +=  Minecraft.getMinecraft().fontRendererObj.getStringWidth(str.substring(2));
                    } else {
                        Minecraft.getMinecraft().fontRendererObj.drawString(str, x + xoffset, y, messagecolor.getRGB());
                        xoffset +=  Minecraft.getMinecraft().fontRendererObj.getStringWidth(str);
                    }
                }
            }
        }
    }

    private void drawLine(int x, int y, int width, Color color) {
        RenderUtil.enable();
        GL11.glScaled(0.5D, 0.5D, 0.5D);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x + width, y);
        // GL11.glEnd();
        RenderUtil.disable();

        /*
         * Tessellator tessellator1 = Tessellator.getInstance(); WorldRenderer
         * worldrenderer1 = tessellator1.getWorldRenderer();
         * GlStateManager.disableTexture2D(); worldrenderer1.begin(7,
         * DefaultVertexFormats.POSITION); int l = -1; worldrenderer1.pos((double)(x +
         * (float)l), (double)(y + (float)10), 0.0D).endVertex();
         * worldrenderer1.pos((double)(x), (double)(y + (float)10), 0.0D).endVertex();
         * worldrenderer1.pos((double)(x), (double)(y + (float)10 - 1.0F),
         * 0.0D).endVertex(); worldrenderer1.pos((double)(x + (float)l), (double)(y +
         * (float)10 - 1.0F), 0.0D).endVertex(); tessellator1.draw();
         * GlStateManager.enableTexture2D();
         */

    }

    private String getRandomString(int count) {
        String str = "";
        for (int i = 0; i < count; i++) {
            str += (char) (rnd.nextInt(26) + 'a');
        }
        return str;
    }

}
