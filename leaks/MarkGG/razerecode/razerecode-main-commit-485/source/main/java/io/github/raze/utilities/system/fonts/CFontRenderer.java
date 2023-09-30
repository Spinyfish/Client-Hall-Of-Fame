/*
 * Copyright (c) 2023. Syncinus / Liticane
 * You cannot redistribute these files
 * without my written permission.
 */

package io.github.raze.utilities.system.fonts;

import java.awt.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SuppressWarnings("WeakerAccess")
public final class CFontRenderer extends CFont {

    private final CharacterData[] boldItalicChars = new CharacterData[256];
    private final CharacterData[] italicChars = new CharacterData[256];
    private final CharacterData[] boldChars = new CharacterData[256];
    private final int[] colorCode = new int[32];
    private final char COLOR_CODE_START = '§';

    public CFontRenderer(ResourceLocation resourceLocation, float size) {
        super(resourceLocation, size);

        setupMinecraftColorCodes();
        setupBoldItalicIDs();
    }

    public double drawStringWithShadow(String text, double x, double y, Color color) {
        double shadowWidth = drawString(text, x + 0.65D, y + 0.65D, Color.BLACK);
        return Math.max(shadowWidth, drawString(text, x, y, color));
    }

    public double drawStringWithShadow(String text, double x, double y, Color color, Color shadowColor) {
        double shadowWidth = drawString(text, x + 0.65D, y + 0.65D, shadowColor);
        return Math.max(shadowWidth, drawString(text, x, y, color));
    }

    public double drawString(String text, double x, double y, Color color) {

        if (text == null) {
            return 0;
        }

        CharacterData[] currentData = characterData;

        boolean bold = false;
        boolean italic = false;
        boolean strike = false;
        boolean uline = false;

        // Scale
        x *= 2;
        y *= 2;

        y -= 4;

        // Start
        GL11.glPushMatrix();
        GL11.glScaled(0.5, 0.5, 0.5);

        // Blend
        GL11.glEnable(GL11.GL_BLEND);

        GL11.glBlendFunc(
                GL11.GL_SRC_ALPHA,
                GL11.GL_ONE_MINUS_SRC_ALPHA
        );

        // Color
        GL11.glColor4d(
                color.getRed() / 255.0F,
                color.getGreen() / 255.0F,
                color.getBlue() / 255.0F,
                color.getAlpha() / 255.0F
        );

        // Texture
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getGlTextureId());

        for (int index = 0; index < text.length(); index++) {
            char character = text.charAt(index);

            if (character == COLOR_CODE_START) {
                int colorIndex = 21;

                try {
                    colorIndex = "0123456789abcdefklmnor".indexOf(text.charAt(index + 1));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (colorIndex < 16) {
                    bold = false;
                    italic = false;
                    uline = false;
                    strike = false;

                    currentData = characterData;
                    GlStateManager.bindTexture(texture.getGlTextureId());

                    if (colorIndex < 0) {
                        colorIndex = 15;
                    }

                    int colorcode = colorCode[colorIndex];

                    GlStateManager.color(
                            (float) (colorcode >> 16 & 255) / 255.0F,
                            (float) (colorcode >> 8 & 255) / 255.0F,
                            (float) (colorcode & 255) / 255.0F,
                            (float) color.getAlpha() / 255.0F
                    );

                } else if (colorIndex == 17) {
                    bold = true;

                    if (italic) {
                        GlStateManager.bindTexture(texItalicBold.getGlTextureId());
                        currentData = boldItalicChars;
                    } else {
                        GlStateManager.bindTexture(texBold.getGlTextureId());
                        currentData = boldChars;
                    }

                } else if (colorIndex == 18) {
                    strike = true;
                } else if (colorIndex == 19) {
                    uline = true;
                } else if (colorIndex == 20) {
                    italic = true;

                    if (bold) {
                        GlStateManager.bindTexture(texItalicBold.getGlTextureId());
                        currentData = boldItalicChars;
                    } else {
                        GlStateManager.bindTexture(texItalic.getGlTextureId());
                        currentData = italicChars;
                    }

                } else {
                    bold = false;
                    italic = false;
                    uline = false;
                    strike = false;

                    GL11.glColor4d(
                            color.getRed() / 255.0F,
                            color.getGreen() / 255.0F,
                            color.getBlue() / 255.0F,
                            color.getAlpha() / 255.0F
                    );

                    GlStateManager.bindTexture(texture.getGlTextureId());
                    currentData = characterData;
                }

                index += 1;
            } else if (character < currentData.length) {

                GL11.glBegin(GL11.GL_TRIANGLES);
                drawChar(currentData, character, (float) x, (float) y);
                GL11.glEnd();

                if (strike) {
                    drawLine(x, y + (double) (currentData[character].height / 2), x + (double) currentData[character].width - 8, y + (double) (currentData[character].height / 2));
                }

                if (uline) {
                    drawLine(x, y + (double) currentData[character].height - 2, x + (double) currentData[character].width - 8, y + (double) currentData[character].height - 2);
                }

                x += currentData[character].width - 8 + charOffset;
            }
        }

        // Finish
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();

        // Get rid of color.
        GL11.glColor4d(1.0, 1.0, 1.0, 1.0);

        return x / 2.0D;
    }

    @Override
    public int getStringWidth(String text) {

        if (text == null) {
            return 0;
        }

        int width = 0;
        int size = text.length();

        boolean bold = false;
        boolean italic = false;

        CharacterData[] currentData = characterData;

        for (int i = 0; i < size; i++) {
            char character = text.charAt(i);

            if (character == COLOR_CODE_START) {
                int colorIndex = "0123456789abcdefklmnor".indexOf(character);

                if (colorIndex < 16) {
                    bold = false;
                    italic = false;
                } else if (colorIndex == 17) {
                    bold = true;

                    if (italic) {
                        currentData = boldItalicChars;
                    } else {
                        currentData = boldChars;
                    }
                } else if (colorIndex == 20) {
                    italic = true;

                    if (bold) {
                        currentData = boldItalicChars;
                    } else {
                        currentData = italicChars;
                    }
                } else if (colorIndex == 21) {
                    bold = false;
                    italic = false;
                    currentData = characterData;
                }

                i++;
            } else if (character < currentData.length) {
                width += currentData[character].width - 8 + charOffset;
            }
        }

        return width / 2;
    }

    public void setAntiAliasing(boolean antiAliasing) {
        super.setAntiAliasing(antiAliasing);
        setupBoldItalicIDs();
    }

    public void setFractionalMetrics(boolean fractionalMetrics) {
        super.setFractionalMetrics(fractionalMetrics);
        setupBoldItalicIDs();
    }

    private DynamicTexture texBold;
    private DynamicTexture texItalic;
    private DynamicTexture texItalicBold;

    private void setupBoldItalicIDs() {
        texBold = setupTexture(font.deriveFont(Font.BOLD), antiAliasing, fractionalMetrics, boldChars);
        texItalic = setupTexture(font.deriveFont(Font.ITALIC), antiAliasing, fractionalMetrics, italicChars);
        texItalicBold = setupTexture(font.deriveFont(Font.BOLD + Font.ITALIC), antiAliasing, fractionalMetrics, boldItalicChars);
    }

    private void drawLine(double x, double y, double x1, double y1) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glLineWidth(1.0f);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x1, y1);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private void setupMinecraftColorCodes() {
        for (int index = 0; index < 32; index++) {
            int alpha = (index >> 3 & 0x1) * 85;
            int red = (index >> 2 & 0x1) * 170 + alpha;
            int green = (index >> 1 & 0x1) * 170 + alpha;
            int blue = (index & 0x1) * 170 + alpha;

            if (index == 6) {
                red += 85;
            }

            if (index >= 16) {
                red /= 4;
                green /= 4;
                blue /= 4;
            }

            colorCode[index] = ((red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF);
        }
    }
}
