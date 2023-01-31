package net.minecraft.client.gui;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.CustomColors;
import net.optifine.render.GlBlendState;
import net.optifine.util.FontUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.lwjgl.opengl.GL11;

public class FontRenderer implements IResourceManagerReloadListener {
   private static final ResourceLocation[] unicodePageLocations = new ResourceLocation[256];
   private final int[] charWidth = new int[256];
   public static int FONT_HEIGHT = 9;
   public Random fontRandom = new Random();
   private final byte[] glyphWidth = new byte[65536];
   private final int[] colorCode = new int[32];
   private ResourceLocation locationFontTexture;
   private final TextureManager renderEngine;
   private float posX;
   private float posY;
   private boolean unicodeFlag;
   private boolean bidiFlag;
   private float red;
   private float blue;
   private float green;
   private float alpha;
   private int textColor;
   private boolean randomStyle;
   private boolean boldStyle;
   private boolean italicStyle;
   private boolean underlineStyle;
   private boolean strikethroughStyle;
   public GameSettings gameSettings;
   public ResourceLocation locationFontTextureBase;
   public float offsetBold = 1.0F;
   private final float[] charWidthFloat = new float[256];
   private boolean blend = false;
   private final GlBlendState oldBlendState = new GlBlendState();

   public FontRenderer(GameSettings gameSettingsIn, ResourceLocation location, TextureManager textureManagerIn, boolean unicode) {
      this.gameSettings = gameSettingsIn;
      this.locationFontTextureBase = location;
      this.locationFontTexture = location;
      this.renderEngine = textureManagerIn;
      this.unicodeFlag = unicode;
      this.locationFontTexture = FontUtils.getHdFontLocation(this.locationFontTextureBase);
      this.bindTexture(this.locationFontTexture);

      for(int i = 0; i < 32; ++i) {
         int j = (i >> 3 & 1) * 85;
         int k = (i >> 2 & 1) * 170 + j;
         int l = (i >> 1 & 1) * 170 + j;
         int i1 = (i >> 0 & 1) * 170 + j;
         if (i == 6) {
            k += 85;
         }

         if (gameSettingsIn.anaglyph) {
            int j1 = (k * 30 + l * 59 + i1 * 11) / 100;
            int k1 = (k * 30 + l * 70) / 100;
            int l1 = (k * 30 + i1 * 70) / 100;
            k = j1;
            l = k1;
            i1 = l1;
         }

         if (i >= 16) {
            k /= 4;
            l /= 4;
            i1 /= 4;
         }

         this.colorCode[i] = (k & 0xFF) << 16 | (l & 0xFF) << 8 | i1 & 0xFF;
      }

      this.readGlyphSizes();
   }

   @Override
   public void onResourceManagerReload(IResourceManager resourceManager) {
      this.locationFontTexture = FontUtils.getHdFontLocation(this.locationFontTextureBase);

      for(int i = 0; i < unicodePageLocations.length; ++i) {
         unicodePageLocations[i] = null;
      }

      this.readFontTexture();
      this.readGlyphSizes();
   }

   private void readFontTexture() {
      BufferedImage bufferedimage;
      try {
         bufferedimage = TextureUtil.readBufferedImage(this.getResourceInputStream(this.locationFontTexture));
      } catch (IOException var21) {
         throw new RuntimeException(var21);
      }

      Properties properties = FontUtils.readFontProperties(this.locationFontTexture);
      this.blend = FontUtils.readBoolean(properties, "blend", false);
      int i = bufferedimage.getWidth();
      int j = bufferedimage.getHeight();
      int k = i / 16;
      int l = j / 16;
      float f = (float)i / 128.0F;
      float f1 = Config.limit(f, 1.0F, 2.0F);
      this.offsetBold = 1.0F / f1;
      float f2 = FontUtils.readFloat(properties, "offsetBold", -1.0F);
      if (f2 >= 0.0F) {
         this.offsetBold = f2;
      }

      int[] aint = new int[i * j];
      bufferedimage.getRGB(0, 0, i, j, aint, 0, i);

      for(int i1 = 0; i1 < 256; ++i1) {
         int j1 = i1 % 16;
         int k1 = i1 / 16;
         int l1 = 0;

         for(l1 = k - 1; l1 >= 0; --l1) {
            int i2 = j1 * k + l1;
            boolean flag = true;

            for(int j2 = 0; j2 < l && flag; ++j2) {
               int k2 = (k1 * l + j2) * i;
               int l2 = aint[i2 + k2];
               int i3 = l2 >> 24 & 0xFF;
               if (i3 > 16) {
                  flag = false;
               }
            }

            if (!flag) {
               break;
            }
         }

         if (i1 == 65) {
            i1 = i1;
         }

         if (i1 == 32) {
            if (k <= 8) {
               l1 = (int)(2.0F * f);
            } else {
               l1 = (int)(1.5F * f);
            }
         }

         this.charWidthFloat[i1] = (float)(l1 + 1) / f + 1.0F;
      }

      FontUtils.readCustomCharWidths(properties, this.charWidthFloat);

      for(int j3 = 0; j3 < this.charWidth.length; ++j3) {
         this.charWidth[j3] = Math.round(this.charWidthFloat[j3]);
      }
   }

   private void readGlyphSizes() {
      InputStream inputstream = null;

      try {
         inputstream = this.getResourceInputStream(new ResourceLocation("font/glyph_sizes.bin"));
         inputstream.read(this.glyphWidth);
      } catch (IOException var6) {
         throw new RuntimeException(var6);
      } finally {
         IOUtils.closeQuietly(inputstream);
      }
   }

   private float renderChar(char ch, boolean italic) {
      if (ch != ' ' && ch != 160) {
         int i = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
            .indexOf(ch);
         return i != -1 && !this.unicodeFlag ? this.renderDefaultChar(i, italic) : this.renderUnicodeChar(ch, italic);
      } else {
         return !this.unicodeFlag ? this.charWidthFloat[ch] : 4.0F;
      }
   }

   private float renderDefaultChar(int ch, boolean italic) {
      int i = ch % 16 * 8;
      int j = ch / 16 * 8;
      int k = italic ? 1 : 0;
      this.bindTexture(this.locationFontTexture);
      float f = this.charWidthFloat[ch];
      float f1 = 7.99F;
      GL11.glBegin(5);
      GL11.glTexCoord2f((float)i / 128.0F, (float)j / 128.0F);
      GL11.glVertex3f(this.posX + (float)k, this.posY, 0.0F);
      GL11.glTexCoord2f((float)i / 128.0F, ((float)j + 7.99F) / 128.0F);
      GL11.glVertex3f(this.posX - (float)k, this.posY + 7.99F, 0.0F);
      GL11.glTexCoord2f(((float)i + 7.99F - 1.0F) / 128.0F, (float)j / 128.0F);
      GL11.glVertex3f(this.posX + 7.99F - 1.0F + (float)k, this.posY, 0.0F);
      GL11.glTexCoord2f(((float)i + 7.99F - 1.0F) / 128.0F, ((float)j + 7.99F) / 128.0F);
      GL11.glVertex3f(this.posX + 7.99F - 1.0F - (float)k, this.posY + 7.99F, 0.0F);
      GL11.glEnd();
      return f;
   }

   private ResourceLocation getUnicodePageLocation(int page) {
      if (unicodePageLocations[page] == null) {
         unicodePageLocations[page] = new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", page));
         unicodePageLocations[page] = FontUtils.getHdFontLocation(unicodePageLocations[page]);
      }

      return unicodePageLocations[page];
   }

   private void loadGlyphTexture(int page) {
      this.bindTexture(this.getUnicodePageLocation(page));
   }

   private float renderUnicodeChar(char ch, boolean italic) {
      if (this.glyphWidth[ch] == 0) {
         return 0.0F;
      } else {
         int i = ch / 256;
         this.loadGlyphTexture(i);
         int j = this.glyphWidth[ch] >>> 4;
         int k = this.glyphWidth[ch] & 15;
         float f = (float)j;
         float f1 = (float)(k + 1);
         float f2 = (float)(ch % 16 * 16) + f;
         float f3 = (float)((ch & 255) / 16 * 16);
         float f4 = f1 - f - 0.02F;
         float f5 = italic ? 1.0F : 0.0F;
         GL11.glBegin(5);
         GL11.glTexCoord2f(f2 / 256.0F, f3 / 256.0F);
         GL11.glVertex3f(this.posX + f5, this.posY, 0.0F);
         GL11.glTexCoord2f(f2 / 256.0F, (f3 + 15.98F) / 256.0F);
         GL11.glVertex3f(this.posX - f5, this.posY + 7.99F, 0.0F);
         GL11.glTexCoord2f((f2 + f4) / 256.0F, f3 / 256.0F);
         GL11.glVertex3f(this.posX + f4 / 2.0F + f5, this.posY, 0.0F);
         GL11.glTexCoord2f((f2 + f4) / 256.0F, (f3 + 15.98F) / 256.0F);
         GL11.glVertex3f(this.posX + f4 / 2.0F - f5, this.posY + 7.99F, 0.0F);
         GL11.glEnd();
         return (f1 - f) / 2.0F + 1.0F;
      }
   }

   public int drawStringWithShadow(String text, float x, float y, int color) {
      return this.drawString(text, x, y, color, true);
   }

   public int drawString(String text, int x, int y, int color) {
      return this.drawString(text, (float)x, (float)y, color, false);
   }

   public int drawString(String text, float x, float y, int color, boolean dropShadow) {
      this.enableAlpha();
      if (this.blend) {
         GlStateManager.getBlendState(this.oldBlendState);
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
      }

      this.resetStyles();
      int i;
      if (dropShadow) {
         i = this.renderString(text, x + 1.0F, y + 1.0F, color, true);
         i = Math.max(i, this.renderString(text, x, y, color, false));
      } else {
         i = this.renderString(text, x, y, color, false);
      }

      if (this.blend) {
         GlStateManager.setBlendState(this.oldBlendState);
      }

      return i;
   }

   private String bidiReorder(String text) {
      try {
         Bidi bidi = new Bidi(new ArabicShaping(8).shape(text), 127);
         bidi.setReorderingMode(0);
         return bidi.writeReordered(2);
      } catch (ArabicShapingException var31) {
         return text;
      }
   }

   private void resetStyles() {
      this.randomStyle = false;
      this.boldStyle = false;
      this.italicStyle = false;
      this.underlineStyle = false;
      this.strikethroughStyle = false;
   }

   private void renderStringAtPos(String text, boolean shadow) {
      for(int i = 0; i < text.length(); ++i) {
         char c0 = text.charAt(i);
         if (c0 == 167 && i + 1 < text.length()) {
            int l = "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH).charAt(i + 1));
            if (l < 16) {
               this.randomStyle = false;
               this.boldStyle = false;
               this.strikethroughStyle = false;
               this.underlineStyle = false;
               this.italicStyle = false;
               if (l < 0 || l > 15) {
                  l = 15;
               }

               if (shadow) {
                  l += 16;
               }

               int i1 = this.colorCode[l];
               if (Config.isCustomColors()) {
                  i1 = CustomColors.getTextColor(l, i1);
               }

               this.textColor = i1;
               this.setColor((float)(i1 >> 16) / 255.0F, (float)(i1 >> 8 & 0xFF) / 255.0F, (float)(i1 & 0xFF) / 255.0F, this.alpha);
            } else if (l == 16) {
               this.randomStyle = true;
            } else if (l == 17) {
               this.boldStyle = true;
            } else if (l == 18) {
               this.strikethroughStyle = true;
            } else if (l == 19) {
               this.underlineStyle = true;
            } else if (l == 20) {
               this.italicStyle = true;
            } else if (l == 21) {
               this.randomStyle = false;
               this.boldStyle = false;
               this.strikethroughStyle = false;
               this.underlineStyle = false;
               this.italicStyle = false;
               this.setColor(this.red, this.blue, this.green, this.alpha);
            }

            ++i;
         } else {
            int j = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
               .indexOf(c0);
            if (this.randomStyle && j != -1) {
               int k = this.getCharWidth(c0);

               char c1;
               do {
                  j = this.fontRandom
                     .nextInt(
                        "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
                           .length()
                     );
                  c1 = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
                     .charAt(j);
               } while(k != this.getCharWidth(c1));

               c0 = c1;
            }

            float f1 = j != -1 && !this.unicodeFlag ? this.offsetBold : 0.5F;
            boolean flag = (c0 == 0 || j == -1 || this.unicodeFlag) && shadow;
            if (flag) {
               this.posX -= f1;
               this.posY -= f1;
            }

            float f = this.renderChar(c0, this.italicStyle);
            if (flag) {
               this.posX += f1;
               this.posY += f1;
            }

            if (this.boldStyle) {
               this.posX += f1;
               if (flag) {
                  this.posX -= f1;
                  this.posY -= f1;
               }

               this.renderChar(c0, this.italicStyle);
               this.posX -= f1;
               if (flag) {
                  this.posX += f1;
                  this.posY += f1;
               }

               f += f1;
            }

            this.doDraw(f);
         }
      }
   }

   protected void doDraw(float p_doDraw_1_) {
      if (this.strikethroughStyle) {
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldrenderer = tessellator.getWorldRenderer();
         GlStateManager.disableTexture2D();
         worldrenderer.begin(7, DefaultVertexFormats.POSITION);
         worldrenderer.pos((double)this.posX, (double)(this.posY + (float)(FONT_HEIGHT / 2)), 0.0).endVertex();
         worldrenderer.pos((double)(this.posX + p_doDraw_1_), (double)(this.posY + (float)(FONT_HEIGHT / 2)), 0.0).endVertex();
         worldrenderer.pos((double)(this.posX + p_doDraw_1_), (double)(this.posY + (float)(FONT_HEIGHT / 2) - 1.0F), 0.0).endVertex();
         worldrenderer.pos((double)this.posX, (double)(this.posY + (float)(FONT_HEIGHT / 2) - 1.0F), 0.0).endVertex();
         tessellator.draw();
         GlStateManager.enableTexture2D();
      }

      if (this.underlineStyle) {
         Tessellator tessellator1 = Tessellator.getInstance();
         WorldRenderer worldrenderer1 = tessellator1.getWorldRenderer();
         GlStateManager.disableTexture2D();
         worldrenderer1.begin(7, DefaultVertexFormats.POSITION);
         int i = this.underlineStyle ? -1 : 0;
         worldrenderer1.pos((double)(this.posX + (float)i), (double)(this.posY + (float)FONT_HEIGHT), 0.0).endVertex();
         worldrenderer1.pos((double)(this.posX + p_doDraw_1_), (double)(this.posY + (float)FONT_HEIGHT), 0.0).endVertex();
         worldrenderer1.pos((double)(this.posX + p_doDraw_1_), (double)(this.posY + (float)FONT_HEIGHT - 1.0F), 0.0).endVertex();
         worldrenderer1.pos((double)(this.posX + (float)i), (double)(this.posY + (float)FONT_HEIGHT - 1.0F), 0.0).endVertex();
         tessellator1.draw();
         GlStateManager.enableTexture2D();
      }

      this.posX += p_doDraw_1_;
   }

   private int renderStringAligned(String text, int x, int y, int width, int color, boolean dropShadow) {
      if (this.bidiFlag) {
         int i = this.getStringWidth(this.bidiReorder(text));
         x = x + width - i;
      }

      return this.renderString(text, (float)x, (float)y, color, dropShadow);
   }

   private int renderString(String text, float x, float y, int color, boolean dropShadow) {
      if (text == null) {
         return 0;
      } else {
         if (this.bidiFlag) {
            text = this.bidiReorder(text);
         }

         if ((color & -67108864) == 0) {
            color |= -16777216;
         }

         if (dropShadow) {
            color = (color & 16579836) >> 2 | color & 0xFF000000;
         }

         this.red = (float)(color >> 16 & 0xFF) / 255.0F;
         this.blue = (float)(color >> 8 & 0xFF) / 255.0F;
         this.green = (float)(color & 0xFF) / 255.0F;
         this.alpha = (float)(color >> 24 & 0xFF) / 255.0F;
         this.setColor(this.red, this.blue, this.green, this.alpha);
         this.posX = x;
         this.posY = y;
         this.renderStringAtPos(text, dropShadow);
         return (int)this.posX;
      }
   }

   public int getStringWidth(String text) {
      if (text == null) {
         return 0;
      } else {
         float f = 0.0F;
         boolean flag = false;

         for(int i = 0; i < text.length(); ++i) {
            char c0 = text.charAt(i);
            float f1 = this.getCharWidthFloat(c0);
            if (f1 < 0.0F && i < text.length() - 1) {
               c0 = text.charAt(++i);
               if (c0 == 'l' || c0 == 'L') {
                  flag = true;
               } else if (c0 == 'r' || c0 == 'R') {
                  flag = false;
               }

               f1 = 0.0F;
            }

            f += f1;
            if (flag && f1 > 0.0F) {
               f += this.unicodeFlag ? 1.0F : this.offsetBold;
            }
         }

         return Math.round(f);
      }
   }

   public int getCharWidth(char character) {
      return Math.round(this.getCharWidthFloat(character));
   }

   private float getCharWidthFloat(char p_getCharWidthFloat_1_) {
      if (p_getCharWidthFloat_1_ == 167) {
         return -1.0F;
      } else if (p_getCharWidthFloat_1_ != ' ' && p_getCharWidthFloat_1_ != 160) {
         int i = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
            .indexOf(p_getCharWidthFloat_1_);
         if (p_getCharWidthFloat_1_ > 0 && i != -1 && !this.unicodeFlag) {
            return this.charWidthFloat[i];
         } else if (this.glyphWidth[p_getCharWidthFloat_1_] != 0) {
            int j = this.glyphWidth[p_getCharWidthFloat_1_] >>> 4;
            int k = this.glyphWidth[p_getCharWidthFloat_1_] & 15;
            if (k > 7) {
               k = 15;
               j = 0;
            }

            ++k;
            return (float)((k - j) / 2 + 1);
         } else {
            return 0.0F;
         }
      } else {
         return this.charWidthFloat[32];
      }
   }

   public String trimStringToWidth(String text, int width) {
      return this.trimStringToWidth(text, width, false);
   }

   public String trimStringToWidth(String text, int width, boolean reverse) {
      StringBuilder stringbuilder = new StringBuilder();
      float f = 0.0F;
      int i = reverse ? text.length() - 1 : 0;
      int j = reverse ? -1 : 1;
      boolean flag = false;
      boolean flag1 = false;

      for(int k = i; k >= 0 && k < text.length() && f < (float)width; k += j) {
         char c0 = text.charAt(k);
         float f1 = this.getCharWidthFloat(c0);
         if (flag) {
            flag = false;
            if (c0 == 'l' || c0 == 'L') {
               flag1 = true;
            } else if (c0 == 'r' || c0 == 'R') {
               flag1 = false;
            }
         } else if (f1 < 0.0F) {
            flag = true;
         } else {
            f += f1;
            if (flag1) {
               ++f;
            }
         }

         if (f > (float)width) {
            break;
         }

         if (reverse) {
            stringbuilder.insert(0, c0);
         } else {
            stringbuilder.append(c0);
         }
      }

      return stringbuilder.toString();
   }

   private String trimStringNewline(String text) {
      while(text != null && text.endsWith("\n")) {
         text = text.substring(0, text.length() - 1);
      }

      return text;
   }

   public void drawSplitString(String str, int x, int y, int wrapWidth, int textColor) {
      if (this.blend) {
         GlStateManager.getBlendState(this.oldBlendState);
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
      }

      this.resetStyles();
      this.textColor = textColor;
      str = this.trimStringNewline(str);
      this.renderSplitString(str, x, y, wrapWidth, false);
      if (this.blend) {
         GlStateManager.setBlendState(this.oldBlendState);
      }
   }

   private void renderSplitString(String str, int x, int y, int wrapWidth, boolean addShadow) {
      for(String s : this.listFormattedStringToWidth(str, wrapWidth)) {
         this.renderStringAligned(s, x, y, wrapWidth, this.textColor, addShadow);
         y += FONT_HEIGHT;
      }
   }

   public int splitStringWidth(String str, int maxLength) {
      return FONT_HEIGHT * this.listFormattedStringToWidth(str, maxLength).size();
   }

   public void setUnicodeFlag(boolean unicodeFlagIn) {
      this.unicodeFlag = unicodeFlagIn;
   }

   public boolean getUnicodeFlag() {
      return this.unicodeFlag;
   }

   public void setBidiFlag(boolean bidiFlagIn) {
      this.bidiFlag = bidiFlagIn;
   }

   public List<String> listFormattedStringToWidth(String str, int wrapWidth) {
      return Arrays.asList(this.wrapFormattedStringToWidth(str, wrapWidth).split("\n"));
   }

   String wrapFormattedStringToWidth(String str, int wrapWidth) {
      if (str.length() <= 1) {
         return str;
      } else {
         int i = this.sizeStringToWidth(str, wrapWidth);
         if (str.length() <= i) {
            return str;
         } else {
            String s = str.substring(0, i);
            char c0 = str.charAt(i);
            boolean flag = c0 == ' ' || c0 == '\n';
            String s1 = getFormatFromString(s) + str.substring(i + (flag ? 1 : 0));
            return s + "\n" + this.wrapFormattedStringToWidth(s1, wrapWidth);
         }
      }
   }

   private int sizeStringToWidth(String str, int wrapWidth) {
      int i = str.length();
      float f = 0.0F;
      int j = 0;
      int k = -1;

      for(boolean flag = false; j < i; ++j) {
         char c0 = str.charAt(j);
         switch(c0) {
            case '\n':
               --j;
               break;
            case ' ':
               k = j;
            default:
               f += (float)this.getCharWidth(c0);
               if (flag) {
                  ++f;
               }
               break;
            case '§':
               if (j < i - 1) {
                  char c1 = str.charAt(++j);
                  if (c1 == 'l' || c1 == 'L') {
                     flag = true;
                  } else if (c1 == 'r' || c1 == 'R' || isFormatColor(c1)) {
                     flag = false;
                  }
               }
         }

         if (c0 == '\n') {
            k = ++j;
            break;
         }

         if (Math.round(f) > wrapWidth) {
            break;
         }
      }

      return j != i && k != -1 && k < j ? k : j;
   }

   private static boolean isFormatColor(char colorChar) {
      return colorChar >= '0' && colorChar <= '9' || colorChar >= 'a' && colorChar <= 'f' || colorChar >= 'A' && colorChar <= 'F';
   }

   private static boolean isFormatSpecial(char formatChar) {
      return formatChar >= 'k' && formatChar <= 'o' || formatChar >= 'K' && formatChar <= 'O' || formatChar == 'r' || formatChar == 'R';
   }

   public static String getFormatFromString(String text) {
      String s = "";
      int i = -1;
      int j = text.length();

      while((i = text.indexOf(167, i + 1)) != -1) {
         if (i < j - 1) {
            char c0 = text.charAt(i + 1);
            if (isFormatColor(c0)) {
               s = "§" + c0;
            } else if (isFormatSpecial(c0)) {
               s = s + "§" + c0;
            }
         }
      }

      return s;
   }

   public boolean getBidiFlag() {
      return this.bidiFlag;
   }

   public int getColorCode(char character) {
      int i = "0123456789abcdef".indexOf(character);
      if (i >= 0 && i < this.colorCode.length) {
         int j = this.colorCode[i];
         if (Config.isCustomColors()) {
            j = CustomColors.getTextColor(i, j);
         }

         return j;
      } else {
         return 16777215;
      }
   }

   protected void setColor(float p_setColor_1_, float p_setColor_2_, float p_setColor_3_, float p_setColor_4_) {
      GlStateManager.color(p_setColor_1_, p_setColor_2_, p_setColor_3_, p_setColor_4_);
   }

   protected void enableAlpha() {
      GlStateManager.enableAlpha();
   }

   protected void bindTexture(ResourceLocation p_bindTexture_1_) {
      this.renderEngine.bindTexture(p_bindTexture_1_);
   }

   protected InputStream getResourceInputStream(ResourceLocation p_getResourceInputStream_1_) throws IOException {
      return Minecraft.getMinecraft().getResourceManager().getResource(p_getResourceInputStream_1_).getInputStream();
   }
}
