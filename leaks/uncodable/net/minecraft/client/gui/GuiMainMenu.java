package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.gui.altmanager.GuiLogin;
import me.uncodable.srt.impl.gui.mainmenu.GuiCredits;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.optifine.CustomPanorama;
import net.optifine.CustomPanoramaProperties;
import net.optifine.reflect.Reflector;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;

public class GuiMainMenu extends GuiScreen implements GuiYesNoCallback {
   private static final Logger logger = LogManager.getLogger();
   private static final Random RANDOM = new Random();
   private final int secret;
   private final float updateCounter;
   private String splashText;
   private int panoramaTimer;
   private DynamicTexture viewportTexture;
   private final Object threadLock = new Object();
   private String openGLWarning1;
   private String openGLWarning2;
   private String openGLWarningLink;
   private static final ResourceLocation splashTexts = new ResourceLocation("texts/splashes.txt");
   private static final ResourceLocation minecraftTitleTextures = new ResourceLocation("textures/gui/title/minecraft.png");
   private static final ResourceLocation[] titlePanoramaPaths = new ResourceLocation[]{
      new ResourceLocation("textures/gui/title/background/panorama_0.png"),
      new ResourceLocation("textures/gui/title/background/panorama_1.png"),
      new ResourceLocation("textures/gui/title/background/panorama_2.png"),
      new ResourceLocation("textures/gui/title/background/panorama_3.png"),
      new ResourceLocation("textures/gui/title/background/panorama_4.png"),
      new ResourceLocation("textures/gui/title/background/panorama_5.png")
   };
   public static final String field_96138_a = "Please click " + EnumChatFormatting.UNDERLINE + "here" + EnumChatFormatting.RESET + " for more information.";
   private int field_92024_r;
   private int field_92023_s;
   private int field_92022_t;
   private int field_92021_u;
   private int field_92020_v;
   private int field_92019_w;
   private ResourceLocation backgroundTexture;

   public GuiMainMenu() {
      this.secret = RandomUtils.nextInt(0, 1000000);
      this.openGLWarning2 = field_96138_a;
      this.splashText = "missingno";
      BufferedReader bufferedreader = null;

      try {
         List<String> list = Lists.newArrayList();
         bufferedreader = new BufferedReader(
            new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(splashTexts).getInputStream(), Charsets.UTF_8)
         );

         String s;
         while((s = bufferedreader.readLine()) != null) {
            s = s.trim();
            if (!s.isEmpty()) {
               list.add(s);
            }
         }

         if (!list.isEmpty()) {
            do {
               this.splashText = list.get(RANDOM.nextInt(list.size()));
            } while(this.splashText.hashCode() == 125780783);
         }
      } catch (IOException var12) {
      } finally {
         if (bufferedreader != null) {
            try {
               bufferedreader.close();
            } catch (IOException var11) {
            }
         }
      }

      this.updateCounter = RANDOM.nextFloat();
      this.openGLWarning1 = "";
      if (!GLContext.getCapabilities().OpenGL20 && !OpenGlHelper.areShadersSupported()) {
         this.openGLWarning1 = I18n.format("title.oldgl1");
         this.openGLWarning2 = I18n.format("title.oldgl2");
         this.openGLWarningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
      }
   }

   @Override
   public void updateScreen() {
      ++this.panoramaTimer;
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }

   @Override
   protected void keyTyped(char typedChar, int keyCode) {
   }

   @Override
   public void initGui() {
      this.viewportTexture = new DynamicTexture(256, 256);
      this.backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background", this.viewportTexture);
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(new Date());
      if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24) {
         this.splashText = "Merry X-mas!";
      } else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1) {
         this.splashText = "Happy new year!";
      } else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
         this.splashText = "OOoooOOOoooo! Spooky!";
      }

      int j = this.height / 4 + 48;
      this.addSingleplayerMultiplayerButtons(j);
      this.buttonList.add(new GuiButton(0, this.width / 2 - 100, j + 72 + 12, 98, 20, I18n.format("menu.options")));
      this.buttonList.add(new GuiButton(4, this.width / 2 + 2, j + 72 + 12, 98, 20, I18n.format("menu.quit")));
      this.buttonList.add(new GuiButtonLanguage(5, this.width / 2 - 124, j + 72 + 12));
      this.buttonList.add(new GuiButton(1000, this.width - 110, this.height - 32, 98, 20, "Credits"));
      synchronized(this.threadLock) {
         this.field_92023_s = this.fontRendererObj.getStringWidth(this.openGLWarning1);
         this.field_92024_r = this.fontRendererObj.getStringWidth(this.openGLWarning2);
         int k = Math.max(this.field_92023_s, this.field_92024_r);
         this.field_92022_t = (this.width - k) / 2;
         this.field_92021_u = this.buttonList.get(0).yPosition - 24;
         this.field_92020_v = this.field_92022_t + k;
         this.field_92019_w = this.field_92021_u + 24;
      }

      this.mc.func_181537_a(false);
   }

   private void addSingleplayerMultiplayerButtons(int p_73969_1_) {
      this.buttonList.add(new GuiButton(1, this.width / 2 - 100, p_73969_1_, I18n.format("menu.singleplayer")));
      this.buttonList.add(new GuiButton(2, this.width / 2 - 100, p_73969_1_ + 24, I18n.format("menu.multiplayer")));
      this.buttonList.add(new GuiButton(13, this.width / 2 - 100, p_73969_1_ + 48, I18n.format("Alt Login")));
   }

   @Override
   protected void actionPerformed(GuiButton button) {
      switch(button.id) {
         case 0:
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
            break;
         case 1:
            this.mc.displayGuiScreen(new GuiSelectWorld(this));
            break;
         case 2:
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
            break;
         case 4:
            this.mc.shutdown();
            break;
         case 5:
            this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
            break;
         case 6:
            if (Reflector.GuiModList_Constructor.exists()) {
               this.mc.displayGuiScreen((GuiScreen)Reflector.newInstance(Reflector.GuiModList_Constructor, this));
            }
            break;
         case 12:
            ISaveFormat isaveformat = this.mc.getSaveLoader();
            WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");
            if (worldinfo != null) {
               GuiYesNo guiyesno = GuiSelectWorld.func_152129_a(this, worldinfo.getWorldName(), 12);
               this.mc.displayGuiScreen(guiyesno);
            }
            break;
         case 13:
            this.mc.displayGuiScreen(new GuiLogin());
            break;
         case 1000:
            this.mc.displayGuiScreen(new GuiCredits());
      }
   }

   @Override
   public void confirmClicked(boolean result, int id) {
      if (result && id == 12) {
         ISaveFormat isaveformat = this.mc.getSaveLoader();
         isaveformat.flushCache();
         isaveformat.deleteWorldDirectory("Demo_World");
         this.mc.displayGuiScreen(this);
      } else if (id == 13) {
         if (result) {
            try {
               Class<?> oclass = Class.forName("java.awt.Desktop");
               Object object = oclass.getMethod("getDesktop").invoke(null);
               oclass.getMethod("browse", URI.class).invoke(object, new URI(this.openGLWarningLink));
            } catch (Throwable var5) {
               logger.error("Couldn't open link", var5);
            }
         }

         this.mc.displayGuiScreen(this);
      }
   }

   private void drawPanorama(float p_73970_3_) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      GlStateManager.matrixMode(5889);
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
      GlStateManager.matrixMode(5888);
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.disableCull();
      GlStateManager.depthMask(false);
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      int i = 8;
      int j = 64;
      CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();
      if (custompanoramaproperties != null) {
         j = custompanoramaproperties.getBlur1();
      }

      for(int k = 0; k < j; ++k) {
         GlStateManager.pushMatrix();
         float f = ((float)(k % i) / (float)i - 0.5F) / 64.0F;
         float f1 = ((float)(k / i) / (float)i - 0.5F) / 64.0F;
         float f2 = 0.0F;
         GlStateManager.translate(f, f1, f2);
         GlStateManager.rotate(MathHelper.sin(((float)this.panoramaTimer + p_73970_3_) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(-((float)this.panoramaTimer + p_73970_3_) * 0.1F, 0.0F, 1.0F, 0.0F);

         for(int l = 0; l < 6; ++l) {
            GlStateManager.pushMatrix();
            if (l == 1) {
               GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
            }

            if (l == 2) {
               GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            }

            if (l == 3) {
               GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            }

            if (l == 4) {
               GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (l == 5) {
               GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            }

            ResourceLocation[] aresourcelocation = titlePanoramaPaths;
            if (custompanoramaproperties != null) {
               aresourcelocation = custompanoramaproperties.getPanoramaLocations();
            }

            this.mc.getTextureManager().bindTexture(aresourcelocation[l]);
            worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181709_i);
            int i1 = 255 / (k + 1);
            worldrenderer.func_181662_b(-1.0, -1.0, 1.0).func_181673_a(0.0, 0.0).func_181669_b(255, 255, 255, i1).func_181675_d();
            worldrenderer.func_181662_b(1.0, -1.0, 1.0).func_181673_a(1.0, 0.0).func_181669_b(255, 255, 255, i1).func_181675_d();
            worldrenderer.func_181662_b(1.0, 1.0, 1.0).func_181673_a(1.0, 1.0).func_181669_b(255, 255, 255, i1).func_181675_d();
            worldrenderer.func_181662_b(-1.0, 1.0, 1.0).func_181673_a(0.0, 1.0).func_181669_b(255, 255, 255, i1).func_181675_d();
            tessellator.draw();
            GlStateManager.popMatrix();
         }

         GlStateManager.popMatrix();
         GlStateManager.colorMask(true, true, true, false);
      }

      worldrenderer.setTranslation(0.0, 0.0, 0.0);
      GlStateManager.colorMask(true, true, true, true);
      GlStateManager.matrixMode(5889);
      GlStateManager.popMatrix();
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      GlStateManager.depthMask(true);
      GlStateManager.enableCull();
      GlStateManager.enableDepth();
   }

   private void rotateAndBlurSkybox(float p_73968_1_) {
      this.mc.getTextureManager().bindTexture(this.backgroundTexture);
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, 256, 256);
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.colorMask(true, true, true, false);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181709_i);
      GlStateManager.disableAlpha();
      int i = 3;
      int j = 3;
      CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();
      if (custompanoramaproperties != null) {
         j = custompanoramaproperties.getBlur2();
      }

      for(int k = 0; k < j; ++k) {
         float f = 1.0F / (float)(k + 1);
         int l = this.width;
         int i1 = this.height;
         float f1 = (float)(k - i / 2) / 256.0F;
         worldrenderer.func_181662_b((double)l, (double)i1, (double)this.zLevel)
            .func_181673_a((double)(0.0F + f1), 1.0)
            .func_181666_a(1.0F, 1.0F, 1.0F, f)
            .func_181675_d();
         worldrenderer.func_181662_b((double)l, 0.0, (double)this.zLevel)
            .func_181673_a((double)(1.0F + f1), 1.0)
            .func_181666_a(1.0F, 1.0F, 1.0F, f)
            .func_181675_d();
         worldrenderer.func_181662_b(0.0, 0.0, (double)this.zLevel).func_181673_a((double)(1.0F + f1), 0.0).func_181666_a(1.0F, 1.0F, 1.0F, f).func_181675_d();
         worldrenderer.func_181662_b(0.0, (double)i1, (double)this.zLevel)
            .func_181673_a((double)(0.0F + f1), 0.0)
            .func_181666_a(1.0F, 1.0F, 1.0F, f)
            .func_181675_d();
      }

      tessellator.draw();
      GlStateManager.enableAlpha();
      GlStateManager.colorMask(true, true, true, true);
   }

   private void renderSkybox(float p_73971_3_) {
      this.mc.getFramebuffer().unbindFramebuffer();
      GlStateManager.viewport(0, 0, 256, 256);
      this.drawPanorama(p_73971_3_);
      this.rotateAndBlurSkybox(p_73971_3_);
      int i = 3;
      CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();
      if (custompanoramaproperties != null) {
         i = custompanoramaproperties.getBlur3();
      }

      for(int j = 0; j < i; ++j) {
         this.rotateAndBlurSkybox(p_73971_3_);
         this.rotateAndBlurSkybox(p_73971_3_);
      }

      this.mc.getFramebuffer().bindFramebuffer(true);
      GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
      float f2 = this.width > this.height ? 120.0F / (float)this.width : 120.0F / (float)this.height;
      float f = (float)this.height * f2 / 256.0F;
      float f1 = (float)this.width * f2 / 256.0F;
      int k = this.width;
      int l = this.height;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181709_i);
      worldrenderer.func_181662_b(0.0, (double)l, (double)this.zLevel)
         .func_181673_a((double)(0.5F - f), (double)(0.5F + f1))
         .func_181666_a(1.0F, 1.0F, 1.0F, 1.0F)
         .func_181675_d();
      worldrenderer.func_181662_b((double)k, (double)l, (double)this.zLevel)
         .func_181673_a((double)(0.5F - f), (double)(0.5F - f1))
         .func_181666_a(1.0F, 1.0F, 1.0F, 1.0F)
         .func_181675_d();
      worldrenderer.func_181662_b((double)k, 0.0, (double)this.zLevel)
         .func_181673_a((double)(0.5F + f), (double)(0.5F - f1))
         .func_181666_a(1.0F, 1.0F, 1.0F, 1.0F)
         .func_181675_d();
      worldrenderer.func_181662_b(0.0, 0.0, (double)this.zLevel)
         .func_181673_a((double)(0.5F + f), (double)(0.5F + f1))
         .func_181666_a(1.0F, 1.0F, 1.0F, 1.0F)
         .func_181675_d();
      tessellator.draw();
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      GlStateManager.disableAlpha();
      this.renderSkybox(partialTicks);
      GlStateManager.enableAlpha();
      int i = 274;
      int j = this.width / 2 - i / 2;
      int k = 30;
      int l = -2130706433;
      int i1 = 16777215;
      int j1 = 0;
      int k1 = Integer.MIN_VALUE;
      CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();
      if (custompanoramaproperties != null) {
         l = custompanoramaproperties.getOverlay1Top();
         i1 = custompanoramaproperties.getOverlay1Bottom();
         j1 = custompanoramaproperties.getOverlay2Top();
         k1 = custompanoramaproperties.getOverlay2Bottom();
      }

      if (l != 0 || i1 != 0) {
         this.drawGradientRect(0, 0, this.width, this.height, l, i1);
      }

      if (j1 != 0 || k1 != 0) {
         this.drawGradientRect(0, 0, this.width, this.height, j1, k1);
      }

      this.mc.getTextureManager().bindTexture(minecraftTitleTextures);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      if ((double)this.updateCounter < 1.0E-4) {
         this.drawTexturedModalRect(j, k, 0, 0, 99, 44);
         this.drawTexturedModalRect(j + 99, k, 129, 0, 27, 44);
         this.drawTexturedModalRect(j + 99 + 26, k, 126, 0, 3, 44);
         this.drawTexturedModalRect(j + 99 + 26 + 3, k, 99, 0, 26, 44);
         this.drawTexturedModalRect(j + 155, k, 0, 45, 155, 44);
      } else {
         this.drawTexturedModalRect(j, k, 0, 0, 155, 44);
         this.drawTexturedModalRect(j + 155, k, 0, 45, 155, 44);
      }

      GlStateManager.pushMatrix();
      GlStateManager.translate((float)(this.width / 2 + 90), 70.0F, 0.0F);
      GlStateManager.rotate(-20.0F, 0.0F, 0.0F, 1.0F);
      float f = 1.8F - MathHelper.abs(MathHelper.sin((float)(Minecraft.getSystemTime() % 1000L) / 1000.0F * (float) Math.PI * 2.0F) * 0.1F);
      f = f * 100.0F / (float)(this.fontRendererObj.getStringWidth(this.splashText) + 32);
      GlStateManager.scale(f, f, f);
      this.drawCenteredString(this.fontRendererObj, this.splashText, 0, -8, -256);
      GlStateManager.popMatrix();
      String s = String.format("%s %s", Ries.INSTANCE.getName(), Ries.INSTANCE.getBuild());
      if (this.secret >= 995000) {
         s = s.concat(" (For Mipe <3)");
      }

      this.drawString(this.fontRendererObj, s, 2, this.height - 10, -1);
      String s2 = "This product is not affiliated with Dodge.";
      this.drawString(this.fontRendererObj, s2, this.width - this.fontRendererObj.getStringWidth(s2) - 2, this.height - 10, -1);
      this.drawCenteredString(this.fontRendererObj, "Extreme Visual Update and Overhaul soon...", this.width / 2, 2, -1);
      GlStateManager.pushMatrix();
      GlStateManager.scale(0.5, 0.5, 0.5);
      this.drawCenteredString(
         this.fontRendererObj, "People kept pushing me to release, so I had no time to finish the entire client...", this.width, 24, 16777215
      );
      GlStateManager.popMatrix();
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      synchronized(this.threadLock) {
         if (this.openGLWarning1.length() > 0
            && mouseX >= this.field_92022_t
            && mouseX <= this.field_92020_v
            && mouseY >= this.field_92021_u
            && mouseY <= this.field_92019_w) {
            GuiConfirmOpenLink guiconfirmopenlink = new GuiConfirmOpenLink(this, this.openGLWarningLink, 13, true);
            guiconfirmopenlink.disableSecurityWarning();
            this.mc.displayGuiScreen(guiconfirmopenlink);
         }
      }
   }
}
