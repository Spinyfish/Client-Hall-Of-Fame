package intent.AquaDev.aqua.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class UnicodeFontRenderer4 {
   public static UnicodeFontRenderer4 instance;
   public final int FONT_HEIGHT = 9;
   private final int[] colorCodes = new int[32];
   private final float kerning;
   private final Map<String, Float> cachedStringWidth = new HashMap<>();
   private final float antiAliasingFactor;
   private UnicodeFont unicodeFont;

   private UnicodeFontRenderer4(String fontName, int fontType, float fontSize, float kerning, float antiAliasingFactor) {
      this.antiAliasingFactor = antiAliasingFactor;

      try {
         this.unicodeFont = new UnicodeFont(this.getFontByName(fontName).deriveFont(fontSize * this.antiAliasingFactor));
      } catch (IOException | FontFormatException var12) {
         var12.printStackTrace();
      }

      this.kerning = kerning;
      this.unicodeFont.addAsciiGlyphs();
      this.unicodeFont.getEffects().add(new ColorEffect(Color.WHITE));

      try {
         this.unicodeFont.loadGlyphs();
      } catch (Exception var11) {
         var11.printStackTrace();
      }

      for(int i = 0; i < 32; ++i) {
         int shadow = (i >> 3 & 1) * 85;
         int red = (i >> 2 & 1) * 170 + shadow;
         int green = (i >> 1 & 1) * 170 + shadow;
         int blue = (i & 1) * 170 + shadow;
         if (i == 6) {
            red += 85;
         }

         if (i >= 16) {
            red /= 4;
            green /= 4;
            blue /= 4;
         }

         this.colorCodes[i] = (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF;
      }
   }

   private UnicodeFontRenderer4(Font font, float kerning, float antiAliasingFactor) {
      this.antiAliasingFactor = antiAliasingFactor;
      this.unicodeFont = new UnicodeFont(new Font(font.getName(), font.getStyle(), (int)((float)font.getSize() * antiAliasingFactor)));
      this.kerning = kerning;
      this.unicodeFont.addAsciiGlyphs();
      this.unicodeFont.getEffects().add(new ColorEffect(Color.WHITE));

      try {
         this.unicodeFont.loadGlyphs();
      } catch (Exception var9) {
         var9.printStackTrace();
      }

      for(int i = 0; i < 32; ++i) {
         int shadow = (i >> 3 & 1) * 85;
         int red = (i >> 2 & 1) * 170 + shadow;
         int green = (i >> 1 & 1) * 170 + shadow;
         int blue = (i & 1) * 170 + shadow;
         if (i == 6) {
            red += 85;
         }

         if (i >= 16) {
            red /= 4;
            green /= 4;
            blue /= 4;
         }

         this.colorCodes[i] = (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF;
      }
   }

   public static UnicodeFontRenderer4 getFontOnPC(String name, int size) {
      return getFontOnPC(name, size, 0);
   }

   public static UnicodeFontRenderer4 getFontOnPC(String name, int size, int fontType) {
      return getFontOnPC(name, size, fontType, 0.0F);
   }

   public static UnicodeFontRenderer4 getFontOnPC(String name, int size, int fontType, float kerning) {
      return getFontOnPC(name, size, fontType, kerning, 3.0F);
   }

   public static UnicodeFontRenderer4 getFontOnPC(String name, int size, int fontType, float kerning, float antiAliasingFactor) {
      return new UnicodeFontRenderer4(new Font(name, fontType, size), kerning, antiAliasingFactor);
   }

   public static UnicodeFontRenderer4 getFontFromAssets(String name, int size) {
      return getFontOnPC(name, size, 0);
   }

   public static UnicodeFontRenderer4 getFontFromAssets(String name, int size, int fontType) {
      return getFontOnPC(name, fontType, size, 0.0F);
   }

   public static UnicodeFontRenderer4 getFontFromAssets(String name, int size, float kerning, int fontType) {
      return getFontFromAssets(name, size, fontType, kerning, 3.0F);
   }

   public static UnicodeFontRenderer4 getFontFromAssets(String name, int size, int fontType, float kerning, float antiAliasingFactor) {
      return new UnicodeFontRenderer4(name, fontType, (float)size, kerning, antiAliasingFactor);
   }

   private Font getFontByName(String name) throws IOException, FontFormatException {
      return this.getFontFromInput("/assets/minecraft/Aqua/fonts/" + name + ".ttf");
   }

   private Font getFontFromInput(String path) throws IOException, FontFormatException {
      return Font.createFont(0, Objects.requireNonNull(UnicodeFontRenderer2.class.getResourceAsStream(path)));
   }

   public void drawStringScaled(String text, int givenX, int givenY, int color, double givenScale) {
      GL11.glPushMatrix();
      GL11.glTranslated((double)givenX, (double)givenY, 0.0);
      GL11.glScaled(givenScale, givenScale, givenScale);
      this.drawString(text, 0.0F, 0.0F, color);
      GL11.glPopMatrix();
   }

   public void drawStringScaledShadow(String text, int givenX, int givenY, int color, double givenScale, float x, float y) {
      GL11.glPushMatrix();
      GL11.glTranslated((double)givenX, (double)givenY, 0.0);
      GL11.glScaled(givenScale, givenScale, givenScale);
      this.drawString(StringUtils.stripControlCodes(text), x + 0.5F, y + 0.5F, 0);
      GL11.glPopMatrix();
   }

   public int drawString(String text, float x, float y, int color) {
      if (text == null) {
         return 0;
      } else {
         x *= 2.0F;
         y *= 2.0F;
         float originalX = x;
         GL11.glPushMatrix();
         GlStateManager.scale(1.0F / this.antiAliasingFactor, 1.0F / this.antiAliasingFactor, 1.0F / this.antiAliasingFactor);
         GL11.glScaled(0.5, 0.5, 0.5);
         x *= this.antiAliasingFactor;
         y *= this.antiAliasingFactor;
         float red = (float)(color >> 16 & 0xFF) / 255.0F;
         float green = (float)(color >> 8 & 0xFF) / 255.0F;
         float blue = (float)(color & 0xFF) / 255.0F;
         float alpha = (float)(color >> 24 & 0xFF) / 255.0F;
         GlStateManager.color(red, green, blue, alpha);
         boolean blend = GL11.glIsEnabled(3042);
         boolean lighting = GL11.glIsEnabled(2896);
         boolean texture = GL11.glIsEnabled(3553);
         boolean alphaTest = GL11.glIsEnabled(3008);
         if (!blend) {
            GL11.glEnable(3042);
         }

         if (lighting) {
            GL11.glDisable(2896);
         }

         if (texture) {
            GL11.glDisable(3553);
         }

         if (alphaTest) {
            GL11.glEnable(3008);
         }

         int currentColor = color;
         char[] characters = text.toCharArray();
         int index = 0;

         for(char c : characters) {
            if (c == '\r') {
               x = originalX;
            }

            if (c == '\n') {
               y += this.getHeight(Character.toString(c)) * 2.0F;
            }

            if (c == 167 || index != 0 && index != characters.length - 1 && characters[index - 1] == 167) {
               if (c == ' ') {
                  x += (float)this.unicodeFont.getSpaceWidth();
               } else if (c == 167 && index != characters.length - 1) {
                  int codeIndex = "0123456789abcdefg".indexOf(text.charAt(index + 1));
                  if (codeIndex < 0) {
                     continue;
                  }

                  currentColor = this.colorCodes[codeIndex];
               }
            } else {
               this.unicodeFont.drawString(x, y, Character.toString(c), new org.newdawn.slick.Color(currentColor));
               x += this.getWidth(Character.toString(c)) * 2.0F * this.antiAliasingFactor;
            }

            ++index;
         }

         GL11.glScaled(2.0, 2.0, 2.0);
         if (texture) {
            GL11.glEnable(3553);
         }

         if (lighting) {
            GL11.glEnable(2896);
         }

         if (!blend) {
            GL11.glDisable(3042);
         }

         if (alphaTest) {
            GL11.glEnable(3008);
         }

         GlStateManager.bindTexture(0);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glPopMatrix();
         return (int)x / 2;
      }
   }

   public int drawStringWithShadow(String text, float x, float y, int color) {
      this.drawString(StringUtils.stripControlCodes(text), x + 0.5F, y + 0.5F, 0);
      return this.drawString(text, x, y, color);
   }

   public void drawCenteredString(String text, float x, float y, int color) {
      this.drawString(text, x - (float)((int)this.getWidth(text) / 2), y, color);
   }

   public void drawCenteredTextScaled(String text, int givenX, int givenY, int color, double givenScale) {
      GL11.glPushMatrix();
      GL11.glTranslated((double)givenX, (double)givenY, 0.0);
      GL11.glScaled(givenScale, givenScale, givenScale);
      this.drawCenteredString(text, 0.0F, 0.0F, color);
      GL11.glPopMatrix();
   }

   public void drawCenteredStringWithShadow(String text, float x, float y, int color) {
      this.drawCenteredString(StringUtils.stripControlCodes(text), x + 0.5F, y + 0.5F, color);
      this.drawCenteredString(text, x, y, color);
   }

   public float getWidth(String s) {
      if (this.cachedStringWidth.size() > 1000) {
         this.cachedStringWidth.clear();
      }

      return this.cachedStringWidth.computeIfAbsent(s, e -> {
         float width = 0.0F;
         String str = StringUtils.stripControlCodes(s);

         for(char c : str.toCharArray()) {
            width += (float)this.unicodeFont.getWidth(Character.toString(c)) + this.kerning;
         }

         return width / 2.0F / this.antiAliasingFactor;
      });
   }

   public int getStringWidth(String text) {
      if (EnumChatFormatting.getTextWithoutFormattingCodes(text) == null) {
         return 0;
      } else {
         int i = 0;
         boolean flag = false;

         for(int j = 0; j < EnumChatFormatting.getTextWithoutFormattingCodes(text).length(); ++j) {
            char c0 = EnumChatFormatting.getTextWithoutFormattingCodes(text).charAt(j);
            float k = this.getWidth(EnumChatFormatting.getTextWithoutFormattingCodes(String.valueOf(c0)));
            if (k < 0.0F && j < EnumChatFormatting.getTextWithoutFormattingCodes(text).length() - 1) {
               c0 = EnumChatFormatting.getTextWithoutFormattingCodes(text).charAt(++j);
               if (c0 == 'l' || c0 == 'L') {
                  flag = true;
               } else if (c0 == 'r' || c0 == 'R') {
                  flag = false;
               }

               k = 0.0F;
            }

            i = (int)((float)i + k);
            if (flag && k > 0.0F) {
               ++i;
            }
         }

         return i;
      }
   }

   public float getCharWidth(char c) {
      return (float)this.unicodeFont.getWidth(String.valueOf(c));
   }

   public float getHeight(String s) {
      return (float)this.unicodeFont.getHeight(s) / 2.0F;
   }

   public UnicodeFont getFont() {
      return this.unicodeFont;
   }

   public String trimStringToWidth(String par1Str, int par2) {
      StringBuilder builder = new StringBuilder();
      float var5 = 0.0F;
      int var6 = 0;
      int var7 = 1;
      boolean var8 = false;
      boolean var9 = false;

      for(int var10 = var6; var10 >= 0 && var10 < par1Str.length() && var5 < (float)par2; var10 += var7) {
         char var11 = par1Str.charAt(var10);
         float var12 = this.getCharWidth(var11);
         if (var8) {
            var8 = false;
            if (var11 == 'l' || var11 == 'L') {
               var9 = true;
            } else if (var11 == 'r' || var11 == 'R') {
               var9 = false;
            }
         } else if (var12 < 0.0F) {
            var8 = true;
         } else {
            var5 += var12;
            if (var9) {
               ++var5;
            }
         }

         if (var5 > (float)par2) {
            break;
         }

         builder.append(var11);
      }

      return builder.toString();
   }

   public void drawSplitString(ArrayList<String> lines, int x, int y, int color) {
      this.drawString(String.join("\n\r", lines), (float)x, (float)y, color);
   }

   public List<String> splitString(String text, int wrapWidth) {
      List<String> lines = new ArrayList<>();
      String[] splitText = text.split(" ");
      StringBuilder currentString = new StringBuilder();

      for(String word : splitText) {
         String potential = currentString + " " + word;
         if (this.getWidth(potential) >= (float)wrapWidth) {
            lines.add(currentString.toString());
            currentString = new StringBuilder();
         }

         currentString.append(word).append(" ");
      }

      lines.add(currentString.toString());
      return lines;
   }

   public static UnicodeFontRenderer4 getInstance() {
      return instance;
   }
}
