package net.minecraft.client.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.thealtening.api.TheAltening;
import com.thealtening.api.retriever.AsynchronousDataRetriever;
import com.thealtening.api.retriever.BasicDataRetriever;
import com.thealtening.auth.TheAlteningAuthentication;
import com.thealtening.auth.service.AlteningServiceType;
import de.Hero.settings.GuiColorChooser2;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.alt.design.AltManager;
import intent.AquaDev.aqua.alt.design.Login;
import intent.AquaDev.aqua.altloader.AltLoader;
import intent.AquaDev.aqua.altloader.Api;
import intent.AquaDev.aqua.altloader.Callback;
import intent.AquaDev.aqua.altloader.RedeemResponse;
import intent.AquaDev.aqua.fontrenderer.ClientFont;
import intent.AquaDev.aqua.fontrenderer.GlyphPageFontRenderer;
import intent.AquaDev.aqua.fr.lavache.anime.Animate;
import intent.AquaDev.aqua.msauth.MicrosoftAuthentication;
import intent.AquaDev.aqua.utils.RenderUtil;
import io.netty.util.internal.ThreadLocalRandom;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.optifine.CustomPanorama;
import net.optifine.CustomPanoramaProperties;
import net.optifine.reflect.Reflector;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;

public class GuiMainMenu extends GuiScreen implements GuiYesNoCallback {
   private static final AtomicInteger field_175373_f = new AtomicInteger(0);
   private static final Logger logger = LogManager.getLogger();
   private static final Random RANDOM = new Random();
   public Animate anim = new Animate();
   private static final AltLoader altLoader = new AltLoader();
   public static GlyphPageFontRenderer font3 = ClientFont.font(40, "Comfortaa-Regular", true);
   public boolean altManager;
   private boolean doneWaiting = false;
   private long animationStart;
   private static TheAlteningAuthentication alteningAuthentication = TheAlteningAuthentication.mojang();
   private final float updateCounter;
   public static GuiColorChooser2 colorChooser2;
   private String splashText;
   private GuiButton buttonResetDemo;
   private int panoramaTimer;
   private DynamicTexture viewportTexture;
   private final boolean field_175375_v = true;
   String apiKey = "";
   private final Object threadLock = new Object();
   boolean onStart = true;
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
   private GuiButton realmsButton;
   private boolean field_183502_L;
   private GuiScreen field_183503_M;
   private GuiButton modButton;
   private GuiScreen modUpdateNotification;

   public GuiMainMenu() {
      this.animationStart = System.currentTimeMillis();
      this.openGLWarning2 = field_96138_a;
      this.field_183502_L = false;
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

   private boolean func_183501_a() {
      return Minecraft.getMinecraft().gameSettings.getOptionOrdinalValue(GameSettings.Options.REALMS_NOTIFICATIONS) && this.field_183503_M != null;
   }

   @Override
   public void updateScreen() {
      ++this.panoramaTimer;
      if (this.func_183501_a()) {
         this.field_183503_M.updateScreen();
      }
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }

   @Override
   protected void keyTyped(char typedChar, int keyCode) throws IOException {
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

      int i = 24;
      int j = height / 4 + 48;
      if (this.mc.isDemo()) {
         this.addDemoButtons(j, 24);
      } else {
         this.buttonList.add(new IMGButton(1, width / 2 - 105, j + 72 - 20, 50, 50, "Singleplayer", new ResourceLocation("Aqua/gui/sp1.png")));
      }

      this.buttonList.add(new IMGButton(2, width / 2 + 10, j + 72 - 20, 50, 50, "Multiplayer", new ResourceLocation("Aqua/gui/mp.png")));
      this.buttonList.add(new IMGButton(0, width / 2 + 65, j + 72 - 20, 50, 50, "Options", new ResourceLocation("Aqua/gui/settings.png")));
      this.buttonList.add(new IMGButton(26, width / 2 - 50, j + 72 - 20, 50, 50, "AltManager", new ResourceLocation("Aqua/gui/am.png")));
      synchronized(this.threadLock) {
         this.field_92023_s = this.fontRendererObj.getStringWidth(this.openGLWarning1);
         this.field_92024_r = this.fontRendererObj.getStringWidth(this.openGLWarning2);
         int k = Math.max(this.field_92023_s, this.field_92024_r);
         this.field_92022_t = (width - k) / 2;
         this.field_92021_u = this.buttonList.get(0).yPosition - 24;
         this.field_92020_v = this.field_92022_t + k;
         this.field_92019_w = this.field_92021_u + 24;
      }

      if (!Aqua.allowed) {
         try {
            Display.releaseContext();
         } catch (LWJGLException var7) {
            var7.printStackTrace();
         }
      }

      this.mc.setConnectedToRealms(false);
      if (Minecraft.getMinecraft().gameSettings.getOptionOrdinalValue(GameSettings.Options.REALMS_NOTIFICATIONS) && !this.field_183502_L) {
         new RealmsBridge();
         this.field_183502_L = true;
      }

      if (this.func_183501_a()) {
         this.field_183503_M.setGuiSize(width, height);
         this.field_183503_M.initGui();
      }
   }

   private void addSingleplayerMultiplayerButtons(int p_73969_1_, int p_73969_2_) {
      this.buttonList.add(new GuiButton(1, width / 2 - 50, p_73969_1_, I18n.format("menu.singleplayer")));
      this.buttonList.add(new GuiButton(2, width / 2 - 50, p_73969_1_ + p_73969_2_ * 1, I18n.format("menu.multiplayer")));
   }

   private void addDemoButtons(int p_73972_1_, int p_73972_2_) {
      this.buttonList.add(new GuiButton(11, width / 2 - 100, p_73972_1_, I18n.format("menu.playdemo")));
      this.buttonList.add(this.buttonResetDemo = new GuiButton(12, width / 2 - 100, p_73972_1_ + p_73972_2_ * 1, I18n.format("menu.resetdemo")));
      ISaveFormat isaveformat = this.mc.getSaveLoader();
      WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");
      if (worldinfo == null) {
         this.buttonResetDemo.enabled = false;
      }
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      if (button.id == 0) {
         this.altManager = false;
         this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
      }

      if (button.id == 26) {
         this.altManager = true;
         this.mc.displayGuiScreen(new AltManager(this));
      }

      if (button.id == 5) {
         this.altManager = false;
         this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
      }

      if (button.id == 1) {
         this.altManager = false;
         this.mc.displayGuiScreen(new GuiSelectWorld(this));
      }

      if (button.id == 2) {
         this.altManager = false;
         this.mc.displayGuiScreen(new GuiMultiplayer(this));
      }

      if (button.id == 14 && this.realmsButton.visible) {
         this.switchToRealms();
      }

      if (button.id == 6 && Reflector.GuiModList_Constructor.exists()) {
         this.mc.displayGuiScreen((GuiScreen)Reflector.newInstance(Reflector.GuiModList_Constructor, this));
      }

      if (button.id == 11) {
         this.mc.launchIntegratedServer("Demo_World", "Demo_World", DemoWorldServer.demoWorldSettings);
      }

      if (button.id == 12) {
         ISaveFormat isaveformat = this.mc.getSaveLoader();
         WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");
         if (worldinfo != null) {
            GuiYesNo guiyesno = GuiSelectWorld.makeDeleteWorldYesNo(this, worldinfo.getWorldName(), 12);
            this.mc.displayGuiScreen(guiyesno);
         }
      }
   }

   private void switchToRealms() {
      RealmsBridge realmsbridge = new RealmsBridge();
      realmsbridge.switchToRealms(this);
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

   private void drawPanorama(int p_73970_1_, int p_73970_2_, float p_73970_3_) {
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
         float f = ((float)(k % 8) / 8.0F - 0.5F) / 64.0F;
         float f1 = ((float)(k / 8) / 8.0F - 0.5F) / 64.0F;
         float f2 = 0.0F;
         GlStateManager.translate(f, f1, 0.0F);
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
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            int i1 = 255 / (k + 1);
            float f3 = 0.0F;
            worldrenderer.pos(-1.0, -1.0, 1.0).tex(0.0, 0.0).color(255, 255, 255, i1).endVertex();
            worldrenderer.pos(1.0, -1.0, 1.0).tex(1.0, 0.0).color(255, 255, 255, i1).endVertex();
            worldrenderer.pos(1.0, 1.0, 1.0).tex(1.0, 1.0).color(255, 255, 255, i1).endVertex();
            worldrenderer.pos(-1.0, 1.0, 1.0).tex(0.0, 1.0).color(255, 255, 255, i1).endVertex();
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
      worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      GlStateManager.disableAlpha();
      int i = 3;
      int j = 3;
      CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();
      if (custompanoramaproperties != null) {
         j = custompanoramaproperties.getBlur2();
      }

      for(int k = 0; k < j; ++k) {
         float f = 1.0F / (float)(k + 1);
         int l = width;
         int i1 = height;
         float f1 = (float)(k - 1) / 256.0F;
         worldrenderer.pos((double)l, (double)i1, (double)zLevel).tex((double)(0.0F + f1), 1.0).color(1.0F, 1.0F, 1.0F, f).endVertex();
         worldrenderer.pos((double)l, 0.0, (double)zLevel).tex((double)(1.0F + f1), 1.0).color(1.0F, 1.0F, 1.0F, f).endVertex();
         worldrenderer.pos(0.0, 0.0, (double)zLevel).tex((double)(1.0F + f1), 0.0).color(1.0F, 1.0F, 1.0F, f).endVertex();
         worldrenderer.pos(0.0, (double)i1, (double)zLevel).tex((double)(0.0F + f1), 0.0).color(1.0F, 1.0F, 1.0F, f).endVertex();
      }

      tessellator.draw();
      GlStateManager.enableAlpha();
      GlStateManager.colorMask(true, true, true, true);
   }

   private void renderSkybox(int p_73971_1_, int p_73971_2_, float p_73971_3_) {
      this.mc.getFramebuffer().unbindFramebuffer();
      GlStateManager.viewport(0, 0, 256, 256);
      this.drawPanorama(p_73971_1_, p_73971_2_, p_73971_3_);
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
      float f2 = width > height ? 120.0F / (float)width : 120.0F / (float)height;
      float f = (float)height * f2 / 256.0F;
      float f1 = (float)width * f2 / 256.0F;
      int k = width;
      int l = height;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      worldrenderer.pos(0.0, (double)l, (double)zLevel).tex((double)(0.5F - f), (double)(0.5F + f1)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      worldrenderer.pos((double)k, (double)l, (double)zLevel).tex((double)(0.5F - f), (double)(0.5F - f1)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      worldrenderer.pos((double)k, 0.0, (double)zLevel).tex((double)(0.5F + f), (double)(0.5F - f1)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      worldrenderer.pos(0.0, 0.0, (double)zLevel).tex((double)(0.5F + f), (double)(0.5F + f1)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      tessellator.draw();
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      if (Aqua.INSTANCE.ircClient.getNickname().equalsIgnoreCase("ClientQUI9240")) {
         System.exit(0);
      }

      GlStateManager.disableAlpha();
      this.renderSkybox(mouseX, mouseY, partialTicks);
      GlStateManager.enableAlpha();
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      int k = 30;
      int i1 = 16777215;
      int j1 = 0;
      int k1 = Integer.MIN_VALUE;
      CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();
      if (custompanoramaproperties != null) {
         i1 = custompanoramaproperties.getOverlay1Bottom();
         j1 = custompanoramaproperties.getOverlay2Top();
         k1 = custompanoramaproperties.getOverlay2Bottom();
      }

      if (Aqua.INSTANCE.ircClient.getNickname().equalsIgnoreCase("DaddyGay")) {
         Aqua.INSTANCE.shaderBackgroundMM.renderShader();
      } else if (!GL11.glGetString(7937).contains("AMD") && !GL11.glGetString(7937).contains("RX ")) {
         Aqua.INSTANCE.shaderBackgroundMM.renderShader();
      } else {
         this.drawDefaultBackground();
      }

      RenderUtil.drawRoundedRect2Alpha(
         (double)((float)width / 2.0F - 105.0F), (double)((float)height / 4.0F + 85.0F), 230.0, 80.0, 2.0, new Color(0, 0, 0, 30)
      );
      if (this.onStart) {
         new ScaledResolution(this.mc);
      }

      if (this.altManager) {
         double animationTime = 10.0;
         double waitTime = 1000.0;
         this.doneWaiting = (double)(System.currentTimeMillis() - this.animationStart) < animationTime + waitTime;
         double animationProgress = this.doneWaiting
            ? 1.0 - ((double)System.currentTimeMillis() - ((double)this.animationStart + animationTime + waitTime)) / animationTime
            : Math.min(1.0, (double)(System.currentTimeMillis() - this.animationStart) / animationTime);
         ScaledResolution sr = new ScaledResolution(this.mc);
         RenderUtil.drawRoundedRect2Alpha((double)(sr.getScaledWidth() - 140), animationProgress + 4.0, 111.5, 130.0, 3.0, new Color(0, 0, 0, 100));
         Aqua.INSTANCE.comfortaa4.drawString("AltLogin", (float)(sr.getScaledWidth() - 105), (float)(animationProgress + 10.0), -1);
         Aqua.INSTANCE
            .comfortaa4
            .drawCenteredString(
               "Name : §4" + this.mc.getSession().getUsername(), (float)(sr.getScaledWidth() - 85), (float)(animationProgress + 25.0), Color.white.getRGB()
            );
         if (!this.mouseOver(mouseX, mouseY, sr.getScaledWidth() - 121, 45, 75, 15)) {
            RenderUtil.drawRoundedRect2Alpha((double)(sr.getScaledWidth() - 121), animationProgress + 45.0, 75.0, 15.0, 3.0, new Color(0, 0, 0, 60));
         } else {
            if (Mouse.isButtonDown(0)) {
               StringBuilder randomName = new StringBuilder();
               String alphabet = "1234567891012121314151638926704982";
               int random = ThreadLocalRandom.current().nextInt(2, 5);

               for(int i = 0; i < random; ++i) {
                  randomName.append(alphabet.charAt(ThreadLocalRandom.current().nextInt(1, alphabet.length())));
               }

               if (Mouse.isButtonDown(1)) {
                  this.login(getClipboardString(), "a");
               } else {
                  this.login("Aqua" + randomName + "User", "a");
               }
            }

            RenderUtil.drawRoundedRect2Alpha((double)(sr.getScaledWidth() - 121), animationProgress + 45.0, 75.0, 15.0, 3.0, new Color(156, 1, 120, 118));
            RenderUtil.drawRoundedRect2Alpha((double)(sr.getScaledWidth() - 245), animationProgress + 45.0, 100.0, 30.0, 3.0, new Color(0, 0, 0, 60));
            Aqua.INSTANCE.comfortaa4.drawString("Hold Right Mouse Click", (float)(sr.getScaledWidth() - 243), (float)(animationProgress + 48.0), -1);
            Aqua.INSTANCE.comfortaa4.drawString("for Clipboard", (float)(sr.getScaledWidth() - 223), (float)(animationProgress + 63.0), -1);
         }

         if (this.mouseOver(mouseX, mouseY, sr.getScaledWidth() - 121, 65, 75, 15)) {
            if (Mouse.isButtonDown(0)) {
               MicrosoftAuthentication.getInstance().loginWithPopUpWindow();
            }

            RenderUtil.drawRoundedRect2Alpha((double)(sr.getScaledWidth() - 121), animationProgress + 65.0, 75.0, 15.0, 3.0, new Color(156, 1, 120, 118));
         } else {
            RenderUtil.drawRoundedRect2Alpha((double)(sr.getScaledWidth() - 121), animationProgress + 65.0, 75.0, 15.0, 3.0, new Color(0, 0, 0, 60));
         }

         if (this.mouseOver(mouseX, mouseY, sr.getScaledWidth() - 121, 85, 75, 15)) {
            if (Mouse.isButtonDown(0)) {
               String token = GuiScreen.getClipboardString();
               if (token.equals("")) {
                  try {
                     Class<?> oclass = Class.forName("java.awt.Desktop");
                     Object object = oclass.getMethod("getDesktop").invoke(null);
                     oclass.getMethod("browse", URI.class).invoke(object, new URI("https://easymc.io/"));
                  } catch (Throwable var23) {
                  }
               }

               Api.redeem(token, new Callback<Object>() {
                  @Override
                  public void done(Object o) {
                     if (!(o instanceof String)) {
                        if (GuiMainMenu.altLoader.savedSession == null) {
                           GuiMainMenu.altLoader.savedSession = GuiMainMenu.this.mc.getSession();
                        }

                        RedeemResponse response;
                        GuiMainMenu.altLoader.easyMCSession = response = (RedeemResponse)o;
                        GuiMainMenu.altLoader.setEasyMCSession(response);
                     }
                  }
               });
            }

            RenderUtil.drawRoundedRect2Alpha((double)(sr.getScaledWidth() - 121), animationProgress + 85.0, 75.0, 15.0, 3.0, new Color(156, 1, 120, 118));
         } else {
            RenderUtil.drawRoundedRect2Alpha((double)(sr.getScaledWidth() - 121), animationProgress + 85.0, 75.0, 15.0, 3.0, new Color(0, 0, 0, 60));
         }

         if (this.mouseOver(mouseX, mouseY, sr.getScaledWidth() - 121, 105, 75, 15)) {
            if (Mouse.isButtonDown(0)) {
               String clipboard = getClipboardString();
               if (!clipboard.contains("@alt")) {
                  if (clipboard.contains("api-")) {
                     this.apiKey = clipboard;
                  }

                  if (this.apiKey != null) {
                     BasicDataRetriever basicDataRetriever = TheAltening.newBasicRetriever(this.apiKey);
                     AsynchronousDataRetriever asynchronousDataRetriever = basicDataRetriever.toAsync();

                     try {
                        this.loginAltening(asynchronousDataRetriever.getAccount().getToken(), "test");
                     } catch (Exception var22) {
                     }

                     return;
                  }
               }

               this.loginAltening(getClipboardString(), "test");
            }

            RenderUtil.drawRoundedRect2Alpha((double)(sr.getScaledWidth() - 121), animationProgress + 105.0, 75.0, 15.0, 3.0, new Color(156, 1, 120, 118));
         } else {
            RenderUtil.drawRoundedRect2Alpha((double)(sr.getScaledWidth() - 121), animationProgress + 105.0, 75.0, 15.0, 3.0, new Color(0, 0, 0, 60));
         }

         Aqua.INSTANCE.comfortaa4.drawCenteredString("Cracked", (float)(sr.getScaledWidth() - 85), (float)(animationProgress + 48.0), -1);
         Aqua.INSTANCE.comfortaa4.drawCenteredString("Microsoft", (float)(sr.getScaledWidth() - 85), (float)(animationProgress + 68.0), -1);
         Aqua.INSTANCE.comfortaa4.drawCenteredString("EasyMC", (float)(sr.getScaledWidth() - 85), (float)(animationProgress + 88.0), -1);
         Aqua.INSTANCE.comfortaa4.drawCenteredString("TheAltening", (float)(sr.getScaledWidth() - 85), (float)(animationProgress + 108.0), -1);
      }

      if (this.mouseOver(mouseX, mouseY, 0, 5, 140, 110)) {
         RenderUtil.drawRoundedRect2Alpha(-1.0, 5.0, 140.0, 110.0, 3.0, new Color(0, 0, 0, 100));
         Aqua.INSTANCE.comfortaa4.drawString("Look on The DC!", 1.0F, 30.0F, -1);
      } else {
         RenderUtil.drawRoundedRect2Alpha(-1.0, 5.0, 140.0, 20.0, 3.0, new Color(0, 0, 0, 100));
      }

      RenderUtil.drawRoundedRect2Alpha(0.0, 23.0, 138.5, 2.0, 0.0, new Color(255, 255, 255, 255));
      Aqua.INSTANCE.comfortaa4.drawString(Aqua.name + " b" + Aqua.build, 45.0F, 10.0F, -1);
      new ScaledResolution(this.mc);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)(width / 2 + 90), 70.0F, 0.0F);
      GlStateManager.rotate(-20.0F, 0.0F, 0.0F, 1.0F);
      float f = 1.8F - MathHelper.abs(MathHelper.sin((float)(Minecraft.getSystemTime() % 1000L) / 1000.0F * (float) Math.PI * 2.0F) * 0.1F);
      GlStateManager.popMatrix();
      String s = "" + Aqua.name + " : " + Aqua.INSTANCE.ircClient.getRank().name() + "";
      if (this.mc.isDemo()) {
         s = s + " Demo";
      }

      if (Reflector.FMLCommonHandler_getBrandings.exists()) {
         Object object = Reflector.call(Reflector.FMLCommonHandler_instance);
         List<String> list = Lists.reverse((List<String>)Reflector.call(object, Reflector.FMLCommonHandler_getBrandings, true));

         for(int l1 = 0; l1 < list.size(); ++l1) {
            String s1 = list.get(l1);
            if (!Strings.isNullOrEmpty(s1)) {
               this.drawString(this.fontRendererObj, s1, 2, height - (10 + l1 * (FontRenderer.FONT_HEIGHT + 1)), 16777215);
            }
         }

         if (Reflector.ForgeHooksClient_renderMainMenu.exists()) {
            Reflector.call(Reflector.ForgeHooksClient_renderMainMenu, this, this.fontRendererObj, width, height);
         }
      } else {
         Aqua.INSTANCE.comfortaa3.drawString(s, 5.0F, (float)(height - 12), -1);
      }

      String s2 = "Client by LCA_MODZ";
      Aqua.INSTANCE
         .comfortaa3
         .drawString("Client by LCA_MODZ", (float)(width - this.fontRendererObj.getStringWidth("Client by LCA_MODZ") - 13), (float)(height - 13), -1);
      if (this.openGLWarning1 != null && this.openGLWarning1.length() > 0) {
         drawRect(this.field_92022_t - 2, this.field_92021_u - 2, this.field_92020_v + 2, this.field_92019_w - 1, 1428160512);
         this.drawString(this.fontRendererObj, this.openGLWarning1, this.field_92022_t, this.field_92021_u, -1);
         this.drawString(this.fontRendererObj, this.openGLWarning2, (width - this.field_92024_r) / 2, this.buttonList.get(0).yPosition - 12, -1);
      }

      if (this.onStart && Mouse.isButtonDown(0)) {
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
      if (this.func_183501_a()) {
         this.field_183503_M.drawScreen(mouseX, mouseY, partialTicks);
      }

      if (this.modUpdateNotification != null) {
         this.modUpdateNotification.drawScreen(mouseX, mouseY, partialTicks);
      }
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

      if (this.func_183501_a()) {
         this.field_183503_M.mouseClicked(mouseX, mouseY, mouseButton);
      }
   }

   public void login(String Email, String password) {
      Aqua.INSTANCE.ircClient.setIngameName(Minecraft.getMinecraft().session.getUsername());
      alteningAuthentication.updateService(AlteningServiceType.MOJANG);

      try {
         Minecraft.getMinecraft().session = Login.logIn(Email, password);
      } catch (Exception var8) {
         YggdrasilAuthenticationService authenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
         YggdrasilUserAuthentication authentication = (YggdrasilUserAuthentication)authenticationService.createUserAuthentication(Agent.MINECRAFT);
         authentication.setUsername(Email);
         authentication.setPassword(password);

         try {
            authentication.logIn();
            Minecraft.getMinecraft().session = new Session(
               authentication.getSelectedProfile().getName(),
               authentication.getSelectedProfile().getId().toString(),
               authentication.getAuthenticatedToken(),
               "mojang"
            );
         } catch (Exception var7) {
            Minecraft.getMinecraft().session = new Session(Email, "", "", "mojang");
         }
      }
   }

   @Override
   public void onGuiClosed() {
      if (this.field_183503_M != null) {
         this.field_183503_M.onGuiClosed();
      }
   }

   private boolean mouseOver(int x, int y, int modX, int modY, int modWidth, int modHeight) {
      return x >= modX && x <= modX + modWidth && y >= modY && y <= modY + modHeight;
   }

   public void loginAltening(String Email, String password) {
      Aqua.INSTANCE.ircClient.setIngameName(Minecraft.getMinecraft().session.getUsername());
      alteningAuthentication.updateService(AlteningServiceType.THEALTENING);

      try {
         Minecraft.getMinecraft().session = Login.logIn(Email, password);
      } catch (Exception var8) {
         YggdrasilAuthenticationService authenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
         YggdrasilUserAuthentication authentication = (YggdrasilUserAuthentication)authenticationService.createUserAuthentication(Agent.MINECRAFT);
         authentication.setUsername(Email);
         authentication.setPassword(password);

         try {
            authentication.logIn();
            Minecraft.getMinecraft().session = new Session(
               authentication.getSelectedProfile().getName(),
               authentication.getSelectedProfile().getId().toString(),
               authentication.getAuthenticatedToken(),
               "mojang"
            );
         } catch (Exception var7) {
            Minecraft.getMinecraft().session = new Session(Email, "", "", "mojang");
         }
      }
   }
}
