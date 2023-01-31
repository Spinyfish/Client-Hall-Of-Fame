package net.minecraft.client.gui;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import events.listeners.EventPostRender2D;
import events.listeners.EventRender2D;
import events.listeners.EventRenderShaderESP;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.fr.lavache.anime.Animate;
import intent.AquaDev.aqua.fr.lavache.anime.Easing;
import intent.AquaDev.aqua.modules.visual.Arraylist;
import intent.AquaDev.aqua.modules.visual.ShaderMultiplier;
import intent.AquaDev.aqua.modules.visual.Shadow;
import intent.AquaDev.aqua.notifications.NotificationManager;
import intent.AquaDev.aqua.utils.ColorUtils;
import intent.AquaDev.aqua.utils.RenderUtil;
import java.awt.Color;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.border.WorldBorder;
import net.optifine.CustomColors;
import windows.GamsterUI;

public class GuiIngame extends Gui {
   private static final ResourceLocation vignetteTexPath = new ResourceLocation("textures/misc/vignette.png");
   private static final ResourceLocation widgetsTexPath = new ResourceLocation("textures/gui/widgets.png");
   private static final ResourceLocation pumpkinBlurTexPath = new ResourceLocation("textures/misc/pumpkinblur.png");
   private final Random rand = new Random();
   private final Minecraft mc;
   private final RenderItem itemRenderer;
   Animate anim = new Animate();
   Animate pic = new Animate();
   Animate chestPic = new Animate();
   private final GuiNewChat persistantChatGUI;
   private int updateCounter;
   private String recordPlaying = "";
   private int recordPlayingUpFor;
   private boolean recordIsPlaying;
   public float prevVignetteBrightness = 1.0F;
   private int remainingHighlightTicks;
   private ItemStack highlightingItemStack;
   private final GuiOverlayDebug overlayDebug;
   private final GuiSpectator spectatorGui;
   private final GuiPlayerTabOverlay overlayPlayerList;
   private int titlesTimer;
   private String displayedTitle = "";
   private String displayedSubTitle = "";
   private int titleFadeIn;
   private int titleDisplayTime;
   private int titleFadeOut;
   private int playerHealth = 0;
   private int lastPlayerHealth = 0;
   private long lastSystemTime = 0L;
   private long healthUpdateCounter = 0L;

   public GuiIngame(Minecraft mcIn) {
      this.mc = mcIn;
      this.itemRenderer = mcIn.getRenderItem();
      this.overlayDebug = new GuiOverlayDebug(mcIn);
      this.spectatorGui = new GuiSpectator(mcIn);
      this.persistantChatGUI = new GuiNewChat(mcIn);
      this.overlayPlayerList = new GuiPlayerTabOverlay(mcIn, this);
      this.setDefaultTitlesTimes();
   }

   public void setDefaultTitlesTimes() {
      this.titleFadeIn = 10;
      this.titleDisplayTime = 70;
      this.titleFadeOut = 20;
   }

   public void renderGameOverlay(float partialTicks) {
      ScaledResolution scaledresolution = new ScaledResolution(this.mc);
      int i = scaledresolution.getScaledWidth();
      int j = scaledresolution.getScaledHeight();
      this.mc.entityRenderer.setupOverlayRendering();
      this.pic
         .setEase(Easing.LINEAR)
         .setMin(0.0F)
         .setMax((float)scaledresolution.getScaledWidth() / 2.0F)
         .setSpeed(this.mc.currentScreen instanceof GuiInventory ? 700.0F : 100000.0F)
         .setReversed(this.mc.currentScreen instanceof GuiInventory)
         .update();
      this.chestPic
         .setEase(Easing.LINEAR)
         .setMin(0.0F)
         .setMax((float)scaledresolution.getScaledWidth() / 2.0F)
         .setSpeed(this.mc.currentScreen instanceof GuiChest ? 700.0F : 100000.0F)
         .setReversed(this.mc.currentScreen instanceof GuiChest)
         .update();
      ScaledResolution scaledRes = new ScaledResolution(this.mc);
      float posX = (float)Aqua.setmgr.getSetting("GuiElementsPosX").getCurrentNumber();
      float posY = (float)Aqua.setmgr.getSetting("GuiElementsPosY").getCurrentNumber();
      float width1 = (float)Aqua.setmgr.getSetting("GuiElementsWidth").getCurrentNumber();
      float height1 = (float)Aqua.setmgr.getSetting("GuiElementsHeight").getCurrentNumber();
      if (Aqua.moduleManager.getModuleByName("GuiElements").isToggled()) {
         if (Aqua.setmgr.getSetting("GuiElementsChestPic").isState() && this.mc.currentScreen instanceof GuiChest) {
            try {
               RenderUtil.drawImage(
                  (int)((float)scaledRes.getScaledWidth() + this.pic.getValue() - posX),
                  (int)((float)scaledRes.getScaledHeight() - posY),
                  (int)width1,
                  (int)height1,
                  GuiContainer.resourceLocation
               );
            } catch (Exception var21) {
            }
         }

         if (Aqua.setmgr.getSetting("GuiElementsInvPic").isState() && this.mc.currentScreen instanceof GuiInventory) {
            float alpha1 = (float)Aqua.setmgr.getSetting("GuiElementsBackgroundAlpha").getCurrentNumber();
            int color = Aqua.setmgr.getSetting("HUDColor").getColor();
            Color colorAlpha = ColorUtils.getColorAlpha(color, (int)alpha1);
            if (Aqua.setmgr.getSetting("GuiElementsBackgroundColor").isState()) {
               Gui.drawRect2(0.0, 0.0, (double)this.mc.displayWidth, (double)this.mc.displayHeight, colorAlpha.getRGB());
            }

            try {
               if (Aqua.setmgr.getSetting("GuiElementsMode").getCurrentMode().equalsIgnoreCase("test")) {
                  ShaderMultiplier.drawGlowESP(
                     () -> RenderUtil.drawGif(
                           (int)((float)scaledRes.getScaledWidth() + this.pic.getValue() - posX),
                           (int)((float)scaledRes.getScaledHeight() - posY),
                           380,
                           380,
                           "test2"
                        ),
                     false
                  );
                  RenderUtil.drawGif(
                     (int)((float)scaledRes.getScaledWidth() + this.pic.getValue() - posX),
                     (int)((float)scaledRes.getScaledHeight() - posY),
                     380,
                     380,
                     "test2"
                  );
               } else {
                  if (Aqua.setmgr.getSetting("GuiElementsGlowColor").isState()) {
                     ShaderMultiplier.drawGlowESP(
                        () -> RenderUtil.drawImageHUDColor(
                              (int)((float)scaledRes.getScaledWidth() + this.pic.getValue() - posX),
                              (int)((float)scaledRes.getScaledHeight() - posY),
                              (int)width1,
                              (int)height1,
                              GuiContainer.resourceLocation
                           ),
                        false
                     );
                  } else {
                     ShaderMultiplier.drawGlowESP(
                        () -> RenderUtil.drawImage(
                              (int)((float)scaledRes.getScaledWidth() + this.pic.getValue() - posX),
                              (int)((float)scaledRes.getScaledHeight() - posY),
                              (int)width1,
                              (int)height1,
                              GuiContainer.resourceLocation
                           ),
                        false
                     );
                  }

                  RenderUtil.drawImage(
                     (int)((float)scaledRes.getScaledWidth() + this.pic.getValue() - posX),
                     (int)((float)scaledRes.getScaledHeight() - posY),
                     (int)width1,
                     (int)height1,
                     GuiContainer.resourceLocation
                  );
               }
            } catch (Exception var20) {
            }
         }

         if (Aqua.setmgr.getSetting("GuiElementsChestPic").isState() && this.mc.currentScreen instanceof GuiChest) {
            float alpha1 = (float)Aqua.setmgr.getSetting("GuiElementsBackgroundAlpha").getCurrentNumber();
            int color = Aqua.setmgr.getSetting("HUDColor").getColor();
            Color colorAlpha = ColorUtils.getColorAlpha(color, (int)alpha1);
            if (Aqua.setmgr.getSetting("GuiElementsBackgroundColor").isState()) {
               Gui.drawRect2(0.0, 0.0, (double)this.mc.displayWidth, (double)this.mc.displayHeight, colorAlpha.getRGB());
            }

            try {
               RenderUtil.drawImage(
                  (int)((float)scaledRes.getScaledWidth() + this.chestPic.getValue() - posX),
                  (int)((float)scaledRes.getScaledHeight() - posY),
                  (int)width1,
                  (int)height1,
                  GuiContainer.resourceLocation
               );
            } catch (Exception var19) {
            }
         }
      }

      EventRender2D event = new EventRender2D();
      if (!this.mc.isSingleplayer()
         && this.mc.getCurrentServerData().serverIP.equalsIgnoreCase("mc.gamster.org")
         && !Aqua.setmgr.getSetting("LongjumpMode").getCurrentMode().equalsIgnoreCase("Gamster")) {
         this.mc.displayGuiScreen(new GamsterUI());
      }

      if (Aqua.moduleManager.getModuleByName("Notifications").isToggled()) {
         NotificationManager.render();
      }

      Aqua.INSTANCE.onEvent(event);
      GlStateManager.enableBlend();
      if (Config.isVignetteEnabled()) {
         this.renderVignette(this.mc.thePlayer.getBrightness(partialTicks), scaledresolution);
      } else {
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      }

      ItemStack itemstack = this.mc.thePlayer.inventory.armorItemInSlot(3);
      if (this.mc.gameSettings.thirdPersonView == 0 && itemstack != null && itemstack.getItem() == Item.getItemFromBlock(Blocks.pumpkin)) {
         this.renderPumpkinOverlay(scaledresolution);
      }

      if (!this.mc.thePlayer.isPotionActive(Potion.confusion)) {
         float f = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * partialTicks;
         if (f > 0.0F) {
            this.renderPortal(f, scaledresolution);
         }
      }

      if (this.mc.playerController.isSpectator()) {
         this.spectatorGui.renderTooltip(scaledresolution, partialTicks);
      } else {
         this.renderTooltip(scaledresolution, partialTicks);
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(icons);
      GlStateManager.enableBlend();
      if (this.showCrosshair()) {
         GlStateManager.tryBlendFuncSeparate(775, 769, 1, 0);
         GlStateManager.enableAlpha();
         this.drawTexturedModalRect(i / 2 - 7, j / 2 - 7, 0, 0, 16, 16);
      }

      GlStateManager.enableAlpha();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      this.mc.mcProfiler.startSection("bossHealth");
      this.renderBossHealth();
      this.mc.mcProfiler.endSection();
      if (this.mc.playerController.shouldDrawHUD()) {
         this.renderPlayerStats(scaledresolution);
      }

      GlStateManager.disableBlend();
      if (this.mc.thePlayer.getSleepTimer() > 0) {
         this.mc.mcProfiler.startSection("sleep");
         GlStateManager.disableDepth();
         GlStateManager.disableAlpha();
         int j1 = this.mc.thePlayer.getSleepTimer();
         float f1 = (float)j1 / 100.0F;
         if (f1 > 1.0F) {
            f1 = 1.0F - (float)(j1 - 100) / 10.0F;
         }

         int k = (int)(220.0F * f1) << 24 | 1052704;
         drawRect(0, 0, i, j, k);
         GlStateManager.enableAlpha();
         GlStateManager.enableDepth();
         this.mc.mcProfiler.endSection();
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      int k1 = i / 2 - 91;
      if (this.mc.thePlayer.isRidingHorse()) {
         this.renderHorseJumpBar(scaledresolution, k1);
      } else if (this.mc.playerController.gameIsSurvivalOrAdventure()) {
         this.renderExpBar(scaledresolution, k1);
      }

      if (this.mc.gameSettings.heldItemTooltips && !this.mc.playerController.isSpectator()) {
         this.renderSelectedItem(scaledresolution);
      } else if (this.mc.thePlayer.isSpectator()) {
         this.spectatorGui.renderSelectedItem(scaledresolution);
      }

      if (this.mc.isDemo()) {
         this.renderDemo(scaledresolution);
      }

      if (this.mc.gameSettings.showDebugInfo) {
         this.overlayDebug.renderDebugInfo(scaledresolution);
      }

      if (this.recordPlayingUpFor > 0) {
         this.mc.mcProfiler.startSection("overlayMessage");
         float f2 = (float)this.recordPlayingUpFor - partialTicks;
         int l1 = (int)(f2 * 255.0F / 20.0F);
         if (l1 > 255) {
            l1 = 255;
         }

         if (l1 > 8) {
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)(i / 2), (float)(j - 68), 0.0F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            int l = 16777215;
            if (this.recordIsPlaying) {
               l = MathHelper.hsvToRGB(f2 / 50.0F, 0.7F, 0.6F) & 16777215;
            }

            this.getFontRenderer()
               .drawString(this.recordPlaying, -this.getFontRenderer().getStringWidth(this.recordPlaying) / 2, -4, l + (l1 << 24 & 0xFF000000));
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
         }

         this.mc.mcProfiler.endSection();
      }

      if (this.titlesTimer > 0) {
         this.mc.mcProfiler.startSection("titleAndSubtitle");
         float f3 = (float)this.titlesTimer - partialTicks;
         int i2 = 255;
         if (this.titlesTimer > this.titleFadeOut + this.titleDisplayTime) {
            float f4 = (float)(this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut) - f3;
            i2 = (int)(f4 * 255.0F / (float)this.titleFadeIn);
         }

         if (this.titlesTimer <= this.titleFadeOut) {
            i2 = (int)(f3 * 255.0F / (float)this.titleFadeOut);
         }

         i2 = MathHelper.clamp_int(i2, 0, 255);
         if (i2 > 8) {
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)(i / 2), (float)(j / 2), 0.0F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0F, 4.0F, 4.0F);
            int j2 = i2 << 24 & 0xFF000000;
            this.getFontRenderer()
               .drawString(this.displayedTitle, (float)(-this.getFontRenderer().getStringWidth(this.displayedTitle) / 2), -10.0F, 16777215 | j2, true);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scale(2.0F, 2.0F, 2.0F);
            this.getFontRenderer()
               .drawString(this.displayedSubTitle, (float)(-this.getFontRenderer().getStringWidth(this.displayedSubTitle) / 2), 5.0F, 16777215 | j2, true);
            GlStateManager.popMatrix();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
         }

         this.mc.mcProfiler.endSection();
      }

      Scoreboard scoreboard = this.mc.theWorld.getScoreboard();
      ScoreObjective scoreobjective = null;
      ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(this.mc.thePlayer.getName());
      if (scoreplayerteam != null) {
         int i1 = scoreplayerteam.getChatFormat().getColorIndex();
         if (i1 >= 0) {
            scoreobjective = scoreboard.getObjectiveInDisplaySlot(3 + i1);
         }
      }

      ScoreObjective scoreobjective1 = scoreobjective != null ? scoreobjective : scoreboard.getObjectiveInDisplaySlot(1);
      if (scoreobjective1 != null) {
         this.renderScoreboardBlur(scoreobjective1, scaledresolution);
      }

      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.disableAlpha();
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, (float)(j - 48), 0.0F);
      this.mc.mcProfiler.startSection("chat");
      this.persistantChatGUI.drawChat2(this.updateCounter);
      if (scoreobjective1 != null) {
         this.renderScoreboard(scoreobjective1, scaledresolution);
      }

      this.mc.mcProfiler.endSection();
      GlStateManager.popMatrix();
      scoreobjective1 = scoreboard.getObjectiveInDisplaySlot(0);
      if (!this.mc.gameSettings.keyBindPlayerList.isKeyDown()
         || this.mc.isIntegratedServerRunning() && this.mc.thePlayer.sendQueue.getPlayerInfoMap().size() <= 1 && scoreobjective1 == null) {
         this.overlayPlayerList.updatePlayerList(false);
      } else {
         this.overlayPlayerList.updatePlayerList(true);
         this.overlayPlayerList.renderPlayerlist(i, scoreboard, scoreobjective1);
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      EventPostRender2D even1t = new EventPostRender2D();
      Aqua.INSTANCE.onEvent(even1t);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableLighting();
      if (Aqua.moduleManager.getModuleByName("Notifications").isToggled()) {
         NotificationManager.render2();
      }

      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, (float)(j - 48), 0.0F);
      this.mc.mcProfiler.startSection("chat");
      this.persistantChatGUI.drawChat(this.updateCounter);
      this.mc.mcProfiler.endSection();
      GlStateManager.popMatrix();
      new EventRenderShaderESP();
      Aqua.INSTANCE.onEvent(even1t);
      GlStateManager.enableAlpha();
   }

   protected void renderTooltip(ScaledResolution sr, float partialTicks) {
      if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.getTextureManager().bindTexture(widgetsTexPath);
         EntityPlayer entityplayer = (EntityPlayer)this.mc.getRenderViewEntity();
         int i = sr.getScaledWidth() / 2;
         float f = zLevel;
         zLevel = -90.0F;
         this.anim
            .setEase(Easing.LINEAR)
            .setMin(11.0F)
            .setMax(40.0F)
            .setSpeed(GuiNewChat.animatedChatOpen ? 45.0F : 100.0F)
            .setReversed(!GuiNewChat.animatedChatOpen)
            .update();
         if (!Aqua.moduleManager.getModuleByName("CustomHotbar").isToggled()) {
            this.drawTexturedModalRect((float)(i - 91), (float)sr.getScaledHeight() - this.anim.getValue(), 0, 0, 182, 22);
            this.drawTexturedModalRect(
               (float)(i - 91 - 1 + entityplayer.inventory.currentItem * 20), (float)sr.getScaledHeight() - this.anim.getValue() - 1.0F, 0, 22, 24, 22
            );
         }

         zLevel = f;
         GlStateManager.enableRescaleNormal();
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         RenderHelper.enableGUIStandardItemLighting();
         if (!Aqua.moduleManager.getModuleByName("CustomHotbar").isToggled()) {
            for(int j = 0; j < 9; ++j) {
               int k = sr.getScaledWidth() / 2 - 90 + j * 20 + 2;
               int l = (int)((float)sr.getScaledHeight() - this.anim.getValue() + 2.0F);
               this.renderHotbarItem(j, k, l, partialTicks, entityplayer);
            }
         }

         RenderHelper.disableStandardItemLighting();
         GlStateManager.disableRescaleNormal();
         GlStateManager.disableBlend();
      }
   }

   public void renderHorseJumpBar(ScaledResolution scaledRes, int x) {
      this.mc.mcProfiler.startSection("jumpBar");
      this.mc.getTextureManager().bindTexture(Gui.icons);
      float f = this.mc.thePlayer.getHorseJumpPower();
      int i = 182;
      int j = (int)(f * 183.0F);
      int k = scaledRes.getScaledHeight() - 32 + 3;
      this.drawTexturedModalRect(x, k, 0, 84, 182, 5);
      if (j > 0) {
         this.drawTexturedModalRect(x, k, 0, 89, j, 5);
      }

      this.mc.mcProfiler.endSection();
   }

   public void renderExpBar(ScaledResolution scaledRes, int x) {
      this.mc.mcProfiler.startSection("expBar");
      this.mc.getTextureManager().bindTexture(Gui.icons);
      int i = this.mc.thePlayer.xpBarCap();
      if (i > 0) {
         int j = 182;
         int k = (int)(this.mc.thePlayer.experience * 183.0F);
         int l = scaledRes.getScaledHeight() - 32 + 3;
         this.drawTexturedModalRect((float)x, (float)l - this.anim.getValue() + 22.0F, 0, 64, 182, 5);
         if (k > 0) {
            this.drawTexturedModalRect((float)x, (float)l - this.anim.getValue() + 22.0F, 0, 69, k, 5);
         }
      }

      this.mc.mcProfiler.endSection();
      if (this.mc.thePlayer.experienceLevel > 0) {
         this.mc.mcProfiler.startSection("expLevel");
         int k1 = 8453920;
         if (Config.isCustomColors()) {
            k1 = CustomColors.getExpBarTextColor(k1);
         }

         String s = "" + this.mc.thePlayer.experienceLevel;
         int l1 = (scaledRes.getScaledWidth() - this.getFontRenderer().getStringWidth(s)) / 2;
         int i1 = scaledRes.getScaledHeight() - 31 - 4;
         int j1 = 0;
         this.getFontRenderer().drawString(s, l1 + 1, (int)((float)i1 - this.anim.getValue() + 22.0F), 0);
         this.getFontRenderer().drawString(s, l1 - 1, (int)((float)i1 - this.anim.getValue() + 22.0F), 0);
         this.getFontRenderer().drawString(s, l1, (int)((float)(i1 + 1) - this.anim.getValue() + 22.0F), 0);
         this.getFontRenderer().drawString(s, l1, (int)((float)(i1 - 1) - this.anim.getValue() + 22.0F), 0);
         this.getFontRenderer().drawString(s, l1, (int)((float)i1 - this.anim.getValue() + 22.0F), k1);
         this.mc.mcProfiler.endSection();
      }
   }

   public void renderSelectedItem(ScaledResolution scaledRes) {
      this.mc.mcProfiler.startSection("selectedItemName");
      if (this.remainingHighlightTicks > 0 && this.highlightingItemStack != null) {
         String s = this.highlightingItemStack.getDisplayName();
         if (this.highlightingItemStack.hasDisplayName()) {
            s = EnumChatFormatting.ITALIC + s;
         }

         int i = (scaledRes.getScaledWidth() - this.getFontRenderer().getStringWidth(s)) / 2;
         int j = scaledRes.getScaledHeight() - 59;
         if (!this.mc.playerController.shouldDrawHUD()) {
            j += 14;
         }

         int k = (int)((float)this.remainingHighlightTicks * 256.0F / 10.0F);
         if (k > 255) {
            k = 255;
         }

         if (k > 0) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            this.getFontRenderer().drawStringWithShadow(s, (float)i, (float)j, 16777215 + (k << 24));
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
         }
      }

      this.mc.mcProfiler.endSection();
   }

   public void renderDemo(ScaledResolution scaledRes) {
      this.mc.mcProfiler.startSection("demo");
      String s = "";
      if (this.mc.theWorld.getTotalWorldTime() >= 120500L) {
         s = I18n.format("demo.demoExpired");
      } else {
         s = I18n.format("demo.remainingTime", StringUtils.ticksToElapsedTime((int)(120500L - this.mc.theWorld.getTotalWorldTime())));
      }

      int i = this.getFontRenderer().getStringWidth(s);
      this.getFontRenderer().drawStringWithShadow(s, (float)(scaledRes.getScaledWidth() - i - 10), 5.0F, 16777215);
      this.mc.mcProfiler.endSection();
   }

   protected boolean showCrosshair() {
      if (this.mc.gameSettings.showDebugInfo && !this.mc.thePlayer.hasReducedDebug() && !this.mc.gameSettings.reducedDebugInfo) {
         return false;
      } else if (this.mc.playerController.isSpectator()) {
         if (this.mc.pointedEntity != null) {
            return true;
         } else {
            if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
               BlockPos blockpos = this.mc.objectMouseOver.getBlockPos();
               if (this.mc.theWorld.getTileEntity(blockpos) instanceof IInventory) {
                  return true;
               }
            }

            return false;
         }
      } else {
         return true;
      }
   }

   public void renderStreamIndicator(ScaledResolution scaledRes) {
   }

   private void renderScoreboard(ScoreObjective objective, ScaledResolution scaledRes) {
      int color = Aqua.setmgr.getSetting("HUDColor").getColor();
      int secondColor = Aqua.setmgr.getSetting("ArraylistColor").getColor();
      int finalColor = Aqua.setmgr.getSetting("CustomScoreboardFade").isState()
         ? Arraylist.getGradientOffset(new Color(color), new Color(secondColor), 15.0).getRGB()
         : color;
      Scoreboard scoreboard = objective.getScoreboard();
      Collection<Score> collection = scoreboard.getSortedScores(objective);
      List<Score> list = Lists.newArrayList(Iterables.filter(collection, new Predicate<Score>() {
         public boolean apply(Score p_apply_1_) {
            return p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#");
         }
      }));
      if (list.size() > 15) {
         collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
      } else {
         collection = list;
      }

      int i = this.getFontRenderer().getStringWidth(objective.getDisplayName());

      for(Score score : collection) {
         ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(score.getPlayerName());
         String s = ScorePlayerTeam.formatPlayerName(scoreplayerteam, score.getPlayerName()) + ": " + EnumChatFormatting.RED + score.getScorePoints();
         i = Math.max(i, this.getFontRenderer().getStringWidth(s));
      }

      int var10000 = collection.size();
      this.getFontRenderer();
      int i1 = var10000 * FontRenderer.FONT_HEIGHT;
      float posY = (float)Aqua.setmgr.getSetting("CustomScoreboardScorePosY").getCurrentNumber();
      int j1 = Aqua.moduleManager.getModuleByName("CustomScoreboard").isToggled()
         ? (int)((float)(scaledRes.getScaledHeight() / 2 + i1 / 3) + posY)
         : scaledRes.getScaledHeight() / 2 + i1 / 3;
      int k1 = 3;
      int l1 = scaledRes.getScaledWidth() - i - k1;
      int j = 0;

      for(Score score1 : collection) {
         ++j;
         ScorePlayerTeam scoreplayerteam1 = scoreboard.getPlayersTeam(score1.getPlayerName());
         String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.getPlayerName());
         String s2 = EnumChatFormatting.RED + "" + score1.getScorePoints();
         this.getFontRenderer();
         int k = j1 - j * FontRenderer.FONT_HEIGHT;
         int l = scaledRes.getScaledWidth() - k1 + 2;
         if (Aqua.setmgr.getSetting("CustomScoreboardShaders").isState()) {
            if (Aqua.setmgr.getSetting("CustomScoreboardMode").getCurrentMode().equalsIgnoreCase("Glow")) {
               Arraylist.drawGlowArray(() -> {
                  int var10000x = l1 - 2;
                  this.getFontRenderer();
                  drawRect(var10000x, k, l, k + FontRenderer.FONT_HEIGHT, finalColor);
               }, false);
               var10000 = l1 - 2;
               this.getFontRenderer();
               drawRect(var10000, k, l, k + FontRenderer.FONT_HEIGHT, 1342177280);
            } else if (Aqua.setmgr.getSetting("CustomScoreboardMode").getCurrentMode().equalsIgnoreCase("Shadow")) {
               Shadow.drawGlow(() -> {
                  int var10000x = l1 - 2;
                  this.getFontRenderer();
                  drawRect(var10000x, k, l, k + FontRenderer.FONT_HEIGHT, Color.black.getRGB());
               }, false);
               var10000 = l1 - 2;
               this.getFontRenderer();
               drawRect(var10000, k, l, k + FontRenderer.FONT_HEIGHT, 1342177280);
            }
         }

         this.getFontRenderer().drawString(s1, l1, k, 553648127);
         this.getFontRenderer().drawString(s2, l - this.getFontRenderer().getStringWidth(s2), k, 553648127);
         if (j == collection.size()) {
            String s3 = objective.getDisplayName();
            if (Aqua.setmgr.getSetting("CustomScoreboardShaders").isState()) {
               if (Aqua.setmgr.getSetting("CustomScoreboardMode").getCurrentMode().equalsIgnoreCase("Glow")) {
                  Arraylist.drawGlowArray(() -> {
                     int var10000x = l1 - 2;
                     this.getFontRenderer();
                     drawRect(var10000x, k - FontRenderer.FONT_HEIGHT - 1, l, k - 1, finalColor);
                  }, false);
                  var10000 = l1 - 2;
                  this.getFontRenderer();
                  drawRect(var10000, k - FontRenderer.FONT_HEIGHT - 1, l, k - 1, 1342177280);
               } else if (Aqua.setmgr.getSetting("CustomScoreboardMode").getCurrentMode().equalsIgnoreCase("Shadow")) {
                  Shadow.drawGlow(() -> {
                     int var10000x = l1 - 2;
                     this.getFontRenderer();
                     drawRect(var10000x, k - FontRenderer.FONT_HEIGHT - 1, l, k - 1, Color.black.getRGB());
                  }, false);
                  var10000 = l1 - 2;
                  this.getFontRenderer();
                  drawRect(var10000, k - FontRenderer.FONT_HEIGHT - 1, l, k - 1, 1342177280);
               }
            }

            if (Aqua.setmgr.getSetting("CustomScoreboardShaders").isState()) {
               if (Aqua.setmgr.getSetting("CustomScoreboardMode").getCurrentMode().equalsIgnoreCase("Glow")) {
                  Arraylist.drawGlowArray(() -> drawRect(l1 - 2, k - 1, l, k, finalColor), false);
                  drawRect(l1 - 2, k - 1, l, k, 1342177280);
               } else if (Aqua.setmgr.getSetting("CustomScoreboardMode").getCurrentMode().equalsIgnoreCase("Shadow")) {
                  Shadow.drawGlow(() -> drawRect(l1 - 2, k - 1, l, k, Color.black.getRGB()), false);
                  drawRect(l1 - 2, k - 1, l, k, 1342177280);
               }
            }

            FontRenderer var33 = this.getFontRenderer();
            int var10002 = l1 + i / 2 - this.getFontRenderer().getStringWidth(s3) / 2;
            this.getFontRenderer();
            var33.drawString(s3, var10002, k - FontRenderer.FONT_HEIGHT, 553648127);
         }
      }
   }

   private void renderScoreboardBlur(ScoreObjective objective, ScaledResolution scaledRes) {
      boolean bloom;
      if (Aqua.moduleManager.getModuleByName("CustomScoreboard").isToggled() && Aqua.moduleManager.getModuleByName("Shadow").isToggled()) {
         bloom = true;
      } else {
         bloom = false;
      }

      boolean bloomGlow;
      if (Aqua.moduleManager.getModuleByName("CustomScoreboard").isToggled() && Aqua.moduleManager.getModuleByName("ShaderMultiplier").isToggled()) {
         bloomGlow = true;
      } else {
         bloomGlow = false;
      }

      Scoreboard scoreboard = objective.getScoreboard();
      Collection<Score> collection = scoreboard.getSortedScores(objective);
      List<Score> list = Lists.newArrayList(Iterables.filter(collection, new Predicate<Score>() {
         public boolean apply(Score p_apply_1_) {
            return p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#");
         }
      }));
      if (list.size() > 15) {
         collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
      } else {
         collection = list;
      }

      int i = this.getFontRenderer().getStringWidth(objective.getDisplayName());

      for(Score score : collection) {
         ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(score.getPlayerName());
         String s = ScorePlayerTeam.formatPlayerName(scoreplayerteam, score.getPlayerName()) + ": " + EnumChatFormatting.RED + score.getScorePoints();
         i = Math.max(i, this.getFontRenderer().getStringWidth(s));
      }

      int var10000 = collection.size();
      this.getFontRenderer();
      int i1 = var10000 * FontRenderer.FONT_HEIGHT;
      int j1 = scaledRes.getScaledHeight() / 2 + i1 + 145;
      int k1 = 3;
      int l1 = scaledRes.getScaledWidth() - i - k1;
      int j = 0;

      for(Score score1 : collection) {
         ++j;
         ScorePlayerTeam scoreplayerteam1 = scoreboard.getPlayersTeam(score1.getPlayerName());
         String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.getPlayerName());
         String s2 = EnumChatFormatting.RED + "" + score1.getScorePoints();
         this.getFontRenderer();
         int k = j1 - j * FontRenderer.FONT_HEIGHT;
         int l = scaledRes.getScaledWidth() - k1 + 2;
         String s3 = Aqua.setmgr.getSetting("CustomScoreboardMode").getCurrentMode();
         switch(s3) {
            case "Glow":
               if (bloomGlow) {
                  ShaderMultiplier.drawGlowESP(() -> {
                     int var10000x = l1 - 2;
                     this.getFontRenderer();
                     drawRect(var10000x, k, l, k + FontRenderer.FONT_HEIGHT, Aqua.setmgr.getSetting("HUDColor").getColor());
                  }, false);
               }
               break;
            case "Shadow":
               if (bloom) {
                  Shadow.drawGlow(() -> {
                     int var10000x = l1 - 2;
                     this.getFontRenderer();
                     drawRect(var10000x, k, l, k + FontRenderer.FONT_HEIGHT, new Color(0, 0, 0, 255).getRGB());
                  }, false);
               }
         }

         var10000 = l1 - 2;
         this.getFontRenderer();
         drawRect(var10000, k, l, k + FontRenderer.FONT_HEIGHT, 1342177280);
         this.getFontRenderer().drawString(s1.replaceAll(this.mc.getCurrentServerData().serverIP.toLowerCase(), "aquaclient.one"), l1, k, 553648127);
         this.getFontRenderer().drawString(s2, l - this.getFontRenderer().getStringWidth(s2), k, 553648127);
         if (j == collection.size()) {
            s3 = objective.getDisplayName();
            String var30 = Aqua.setmgr.getSetting("CustomScoreboardMode").getCurrentMode();
            switch(var30) {
               case "Glow":
                  if (bloomGlow) {
                     ShaderMultiplier.drawGlowESP(() -> {
                        int var10000x = l1 - 2;
                        this.getFontRenderer();
                        drawRect(var10000x, k - FontRenderer.FONT_HEIGHT - 1, l, k - 1, Aqua.setmgr.getSetting("HUDColor").getColor());
                     }, false);
                     ShaderMultiplier.drawGlowESP(() -> drawRect(l1 - 2, k - 1, l, k, Aqua.setmgr.getSetting("HUDColor").getColor()), false);
                  }
                  break;
               case "Shadow":
                  if (bloom) {
                     Shadow.drawGlow(() -> {
                        int var10000x = l1 - 2;
                        this.getFontRenderer();
                        drawRect(var10000x, k - FontRenderer.FONT_HEIGHT - 1, l, k - 1, new Color(0, 0, 0, 255).getRGB());
                     }, false);
                     Shadow.drawGlow(() -> drawRect(l1 - 2, k - 1, l, k, new Color(0, 0, 0, 255).getRGB()), false);
                  }
            }

            var10000 = l1 - 2;
            this.getFontRenderer();
            drawRect(var10000, k - FontRenderer.FONT_HEIGHT - 1, l, k - 1, 1342177280);
            drawRect(l1 - 2, k - 1, l, k, 1342177280);
            FontRenderer var33 = this.getFontRenderer();
            int var10002 = l1 + i / 2 - this.getFontRenderer().getStringWidth(s3) / 2;
            this.getFontRenderer();
            var33.drawString(s3, var10002, k - FontRenderer.FONT_HEIGHT, 553648127);
         }
      }
   }

   private void renderPlayerStats(ScaledResolution scaledRes) {
      if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
         EntityPlayer entityplayer = (EntityPlayer)this.mc.getRenderViewEntity();
         int i = MathHelper.ceiling_float_int(entityplayer.getHealth());
         boolean flag = this.healthUpdateCounter > (long)this.updateCounter && (this.healthUpdateCounter - (long)this.updateCounter) / 3L % 2L == 1L;
         if (i < this.playerHealth && entityplayer.hurtResistantTime > 0) {
            this.lastSystemTime = Minecraft.getSystemTime();
            this.healthUpdateCounter = (long)(this.updateCounter + 20);
         } else if (i > this.playerHealth && entityplayer.hurtResistantTime > 0) {
            this.lastSystemTime = Minecraft.getSystemTime();
            this.healthUpdateCounter = (long)(this.updateCounter + 10);
         }

         if (Minecraft.getSystemTime() - this.lastSystemTime > 1000L) {
            this.playerHealth = i;
            this.lastPlayerHealth = i;
            this.lastSystemTime = Minecraft.getSystemTime();
         }

         this.playerHealth = i;
         int j = this.lastPlayerHealth;
         this.rand.setSeed((long)(this.updateCounter * 312871));
         boolean flag1 = false;
         FoodStats foodstats = entityplayer.getFoodStats();
         int k = foodstats.getFoodLevel();
         int l = foodstats.getPrevFoodLevel();
         IAttributeInstance iattributeinstance = entityplayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
         int i1 = scaledRes.getScaledWidth() / 2 - 91;
         int j1 = scaledRes.getScaledWidth() / 2 + 91;
         int k1 = scaledRes.getScaledHeight() - 39;
         float f = (float)iattributeinstance.getAttributeValue();
         float f1 = entityplayer.getAbsorptionAmount();
         int l1 = MathHelper.ceiling_float_int((f + f1) / 2.0F / 10.0F);
         int i2 = Math.max(10 - (l1 - 2), 3);
         int j2 = k1 - (l1 - 1) * i2 - 10;
         float f2 = f1;
         int k2 = entityplayer.getTotalArmorValue();
         int l2 = -1;
         if (entityplayer.isPotionActive(Potion.regeneration)) {
            l2 = this.updateCounter % MathHelper.ceiling_float_int(f + 5.0F);
         }

         this.mc.mcProfiler.startSection("armor");

         for(int i3 = 0; i3 < 10; ++i3) {
            if (k2 > 0) {
               int j3 = i1 + i3 * 8;
               if (i3 * 2 + 1 < k2) {
                  this.drawTexturedModalRect((float)j3, (float)j2 - this.anim.getValue() + 22.0F, 34, 9, 9, 9);
               }

               if (i3 * 2 + 1 == k2) {
                  this.drawTexturedModalRect((float)j3, (float)j2 - this.anim.getValue() + 22.0F, 25, 9, 9, 9);
               }

               if (i3 * 2 + 1 > k2) {
                  this.drawTexturedModalRect((float)j3, (float)j2 - this.anim.getValue() + 22.0F, 16, 9, 9, 9);
               }
            }
         }

         this.mc.mcProfiler.endStartSection("health");

         for(int i6 = MathHelper.ceiling_float_int((f + f1) / 2.0F) - 1; i6 >= 0; --i6) {
            int j6 = 16;
            if (entityplayer.isPotionActive(Potion.poison)) {
               j6 += 36;
            } else if (entityplayer.isPotionActive(Potion.wither)) {
               j6 += 72;
            }

            int k3 = 0;
            if (flag) {
               k3 = 1;
            }

            int l3 = MathHelper.ceiling_float_int((float)(i6 + 1) / 10.0F) - 1;
            int i4 = i1 + i6 % 10 * 8;
            int j4 = k1 - l3 * i2;
            if (i <= 4) {
               j4 += this.rand.nextInt(2);
            }

            if (i6 == l2) {
               j4 -= 2;
            }

            int k4 = 0;
            if (entityplayer.worldObj.getWorldInfo().isHardcoreModeEnabled()) {
               k4 = 5;
            }

            this.drawTexturedModalRect((float)i4, (float)j4 - this.anim.getValue() + 22.0F, 16 + k3 * 9, 9 * k4, 9, 9);
            if (flag) {
               if (i6 * 2 + 1 < j) {
                  this.drawTexturedModalRect((float)i4, (float)j4 - this.anim.getValue() + 22.0F, j6 + 54, 9 * k4, 9, 9);
               }

               if (i6 * 2 + 1 == j) {
                  this.drawTexturedModalRect((float)i4, (float)j4 - this.anim.getValue() + 22.0F, j6 + 63, 9 * k4, 9, 9);
               }
            }

            if (f2 <= 0.0F) {
               if (i6 * 2 + 1 < i) {
                  this.drawTexturedModalRect((float)i4, (float)j4 - this.anim.getValue() + 22.0F, j6 + 36, 9 * k4, 9, 9);
               }

               if (i6 * 2 + 1 == i) {
                  this.drawTexturedModalRect((float)i4, (float)j4 - this.anim.getValue() + 22.0F, j6 + 45, 9 * k4, 9, 9);
               }
            } else {
               if (f2 == f1 && f1 % 2.0F == 1.0F) {
                  this.drawTexturedModalRect((float)i4, (float)j4 - this.anim.getValue() + 22.0F, j6 + 153, 9 * k4, 9, 9);
               } else {
                  this.drawTexturedModalRect((float)i4, (float)j4 - this.anim.getValue() + 22.0F, j6 + 144, 9 * k4, 9, 9);
               }

               f2 -= 2.0F;
            }
         }

         Entity entity = entityplayer.ridingEntity;
         if (entity == null) {
            this.mc.mcProfiler.endStartSection("food");

            for(int k6 = 0; k6 < 10; ++k6) {
               int j7 = k1;
               int l7 = 16;
               int k8 = 0;
               if (entityplayer.isPotionActive(Potion.hunger)) {
                  l7 += 36;
                  k8 = 13;
               }

               if (entityplayer.getFoodStats().getSaturationLevel() <= 0.0F && this.updateCounter % (k * 3 + 1) == 0) {
                  j7 = k1 + (this.rand.nextInt(3) - 1);
               }

               int j9 = j1 - k6 * 8 - 9;
               this.drawTexturedModalRect((float)j9, (float)j7 - this.anim.getValue() + 22.0F, 16 + k8 * 9, 27, 9, 9);
               if (k6 * 2 + 1 < k) {
                  this.drawTexturedModalRect((float)j9, (float)j7 - this.anim.getValue() + 22.0F, l7 + 36, 27, 9, 9);
               }

               if (k6 * 2 + 1 == k) {
                  this.drawTexturedModalRect((float)j9, (float)j7 - this.anim.getValue() + 22.0F, l7 + 45, 27, 9, 9);
               }
            }
         } else if (entity instanceof EntityLivingBase) {
            this.mc.mcProfiler.endStartSection("mountHealth");
            EntityLivingBase entitylivingbase = (EntityLivingBase)entity;
            int i7 = (int)Math.ceil((double)entitylivingbase.getHealth());
            float f3 = entitylivingbase.getMaxHealth();
            int j8 = (int)(f3 + 0.5F) / 2;
            if (j8 > 30) {
               j8 = 30;
            }

            int i9 = k1;

            for(int k9 = 0; j8 > 0; k9 += 20) {
               int l4 = Math.min(j8, 10);
               j8 -= l4;

               for(int i5 = 0; i5 < l4; ++i5) {
                  int j5 = 52;
                  int k5 = 0;
                  int l5 = j1 - i5 * 8 - 9;
                  this.drawTexturedModalRect(l5, i9, 52 + k5 * 9, 9, 9, 9);
                  if (i5 * 2 + 1 + k9 < i7) {
                     this.drawTexturedModalRect((float)l5, (float)i9 - this.anim.getValue() + 22.0F, 88, 9, 9, 9);
                  }

                  if (i5 * 2 + 1 + k9 == i7) {
                     this.drawTexturedModalRect((float)l5, (float)i9 - this.anim.getValue() + 22.0F, 97, 9, 9, 9);
                  }
               }

               i9 -= 10;
            }
         }

         this.mc.mcProfiler.endStartSection("air");
         if (entityplayer.isInsideOfMaterial(Material.water)) {
            int l6 = this.mc.thePlayer.getAir();
            int k7 = MathHelper.ceiling_double_int((double)(l6 - 2) * 10.0 / 300.0);
            int i8 = MathHelper.ceiling_double_int((double)l6 * 10.0 / 300.0) - k7;

            for(int l8 = 0; l8 < k7 + i8; ++l8) {
               if (l8 < k7) {
                  this.drawTexturedModalRect((float)(j1 - l8 * 8 - 9), (float)j2 - this.anim.getValue() + 22.0F, 16, 18, 9, 9);
               } else {
                  this.drawTexturedModalRect((float)(j1 - l8 * 8 - 9), (float)j2 - this.anim.getValue() + 22.0F, 25, 18, 9, 9);
               }
            }
         }

         this.mc.mcProfiler.endSection();
      }
   }

   private void renderBossHealth() {
      if (BossStatus.bossName != null && BossStatus.statusBarTime > 0) {
         --BossStatus.statusBarTime;
         FontRenderer fontrenderer = this.mc.fontRendererObj;
         ScaledResolution scaledresolution = new ScaledResolution(this.mc);
         int i = scaledresolution.getScaledWidth();
         int j = 182;
         int k = i / 2 - 91;
         int l = (int)(BossStatus.healthScale * 183.0F);
         int i1 = 12;
         this.drawTexturedModalRect(k, 12, 0, 74, 182, 5);
         this.drawTexturedModalRect(k, 12, 0, 74, 182, 5);
         if (l > 0) {
            this.drawTexturedModalRect(k, 12, 0, 79, l, 5);
         }

         String s = BossStatus.bossName;
         this.getFontRenderer().drawStringWithShadow(s, (float)(i / 2 - this.getFontRenderer().getStringWidth(s) / 2), 2.0F, 16777215);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.getTextureManager().bindTexture(icons);
      }
   }

   private void renderPumpkinOverlay(ScaledResolution scaledRes) {
      GlStateManager.disableDepth();
      GlStateManager.depthMask(false);
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableAlpha();
      this.mc.getTextureManager().bindTexture(pumpkinBlurTexPath);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldrenderer.pos(0.0, (double)scaledRes.getScaledHeight(), -90.0).tex(0.0, 1.0).endVertex();
      worldrenderer.pos((double)scaledRes.getScaledWidth(), (double)scaledRes.getScaledHeight(), -90.0).tex(1.0, 1.0).endVertex();
      worldrenderer.pos((double)scaledRes.getScaledWidth(), 0.0, -90.0).tex(1.0, 0.0).endVertex();
      worldrenderer.pos(0.0, 0.0, -90.0).tex(0.0, 0.0).endVertex();
      tessellator.draw();
      GlStateManager.depthMask(true);
      GlStateManager.enableDepth();
      GlStateManager.enableAlpha();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void renderVignette(float lightLevel, ScaledResolution scaledRes) {
      if (!Config.isVignetteEnabled()) {
         GlStateManager.enableDepth();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      } else {
         lightLevel = 1.0F - lightLevel;
         lightLevel = MathHelper.clamp_float(lightLevel, 0.0F, 1.0F);
         WorldBorder worldborder = this.mc.theWorld.getWorldBorder();
         float f = (float)worldborder.getClosestDistance(this.mc.thePlayer);
         double d0 = Math.min(
            worldborder.getResizeSpeed() * (double)worldborder.getWarningTime() * 1000.0, Math.abs(worldborder.getTargetSize() - worldborder.getDiameter())
         );
         double d1 = Math.max((double)worldborder.getWarningDistance(), d0);
         if ((double)f < d1) {
            f = 1.0F - (float)((double)f / d1);
         } else {
            f = 0.0F;
         }

         this.prevVignetteBrightness = (float)((double)this.prevVignetteBrightness + (double)(lightLevel - this.prevVignetteBrightness) * 0.01);
         GlStateManager.disableDepth();
         GlStateManager.depthMask(false);
         GlStateManager.tryBlendFuncSeparate(0, 769, 1, 0);
         if (f > 0.0F) {
            GlStateManager.color(0.0F, f, f, 1.0F);
         } else {
            GlStateManager.color(this.prevVignetteBrightness, this.prevVignetteBrightness, this.prevVignetteBrightness, 1.0F);
         }

         this.mc.getTextureManager().bindTexture(vignetteTexPath);
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldrenderer = tessellator.getWorldRenderer();
         worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
         worldrenderer.pos(0.0, (double)scaledRes.getScaledHeight(), -90.0).tex(0.0, 1.0).endVertex();
         worldrenderer.pos((double)scaledRes.getScaledWidth(), (double)scaledRes.getScaledHeight(), -90.0).tex(1.0, 1.0).endVertex();
         worldrenderer.pos((double)scaledRes.getScaledWidth(), 0.0, -90.0).tex(1.0, 0.0).endVertex();
         worldrenderer.pos(0.0, 0.0, -90.0).tex(0.0, 0.0).endVertex();
         tessellator.draw();
         GlStateManager.depthMask(true);
         GlStateManager.enableDepth();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      }
   }

   private void renderPortal(float timeInPortal, ScaledResolution scaledRes) {
      if (timeInPortal < 1.0F) {
         timeInPortal *= timeInPortal;
         timeInPortal *= timeInPortal;
         timeInPortal = timeInPortal * 0.8F + 0.2F;
      }

      GlStateManager.disableAlpha();
      GlStateManager.disableDepth();
      GlStateManager.depthMask(false);
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.color(1.0F, 1.0F, 1.0F, timeInPortal);
      this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
      TextureAtlasSprite textureatlassprite = this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.portal.getDefaultState());
      float f = textureatlassprite.getMinU();
      float f1 = textureatlassprite.getMinV();
      float f2 = textureatlassprite.getMaxU();
      float f3 = textureatlassprite.getMaxV();
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldrenderer.pos(0.0, (double)scaledRes.getScaledHeight(), -90.0).tex((double)f, (double)f3).endVertex();
      worldrenderer.pos((double)scaledRes.getScaledWidth(), (double)scaledRes.getScaledHeight(), -90.0).tex((double)f2, (double)f3).endVertex();
      worldrenderer.pos((double)scaledRes.getScaledWidth(), 0.0, -90.0).tex((double)f2, (double)f1).endVertex();
      worldrenderer.pos(0.0, 0.0, -90.0).tex((double)f, (double)f1).endVertex();
      tessellator.draw();
      GlStateManager.depthMask(true);
      GlStateManager.enableDepth();
      GlStateManager.enableAlpha();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void renderHotbarItem(int index, int xPos, int yPos, float partialTicks, EntityPlayer player) {
      ItemStack itemstack = player.inventory.mainInventory[index];
      if (itemstack != null) {
         float f = (float)itemstack.animationsToGo - partialTicks;
         if (f > 0.0F) {
            GlStateManager.pushMatrix();
            float f1 = 1.0F + f / 5.0F;
            GlStateManager.translate((float)(xPos + 8), (float)(yPos + 12), 0.0F);
            GlStateManager.scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
            GlStateManager.translate((float)(-(xPos + 8)), (float)(-(yPos + 12)), 0.0F);
         }

         this.itemRenderer.renderItemAndEffectIntoGUI(itemstack, xPos, yPos);
         if (f > 0.0F) {
            GlStateManager.popMatrix();
         }

         this.itemRenderer.renderItemOverlays(this.mc.fontRendererObj, itemstack, xPos, yPos);
      }
   }

   public void updateTick() {
      if (this.recordPlayingUpFor > 0) {
         --this.recordPlayingUpFor;
      }

      if (this.titlesTimer > 0) {
         --this.titlesTimer;
         if (this.titlesTimer <= 0) {
            this.displayedTitle = "";
            this.displayedSubTitle = "";
         }
      }

      ++this.updateCounter;
      if (this.mc.thePlayer != null) {
         ItemStack itemstack = this.mc.thePlayer.inventory.getCurrentItem();
         if (itemstack == null) {
            this.remainingHighlightTicks = 0;
         } else if (this.highlightingItemStack != null
            && itemstack.getItem() == this.highlightingItemStack.getItem()
            && ItemStack.areItemStackTagsEqual(itemstack, this.highlightingItemStack)
            && (itemstack.isItemStackDamageable() || itemstack.getMetadata() == this.highlightingItemStack.getMetadata())) {
            if (this.remainingHighlightTicks > 0) {
               --this.remainingHighlightTicks;
            }
         } else {
            this.remainingHighlightTicks = 40;
         }

         this.highlightingItemStack = itemstack;
      }
   }

   public void setRecordPlayingMessage(String recordName) {
      this.setRecordPlaying(I18n.format("record.nowPlaying", recordName), true);
   }

   public void setRecordPlaying(String message, boolean isPlaying) {
      this.recordPlaying = message;
      this.recordPlayingUpFor = 60;
      this.recordIsPlaying = isPlaying;
   }

   public void displayTitle(String title, String subTitle, int timeFadeIn, int displayTime, int timeFadeOut) {
      if (title == null && subTitle == null && timeFadeIn < 0 && displayTime < 0 && timeFadeOut < 0) {
         this.displayedTitle = "";
         this.displayedSubTitle = "";
         this.titlesTimer = 0;
      } else if (title != null) {
         this.displayedTitle = title;
         this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
      } else if (subTitle != null) {
         this.displayedSubTitle = subTitle;
      } else {
         if (timeFadeIn >= 0) {
            this.titleFadeIn = timeFadeIn;
         }

         if (displayTime >= 0) {
            this.titleDisplayTime = displayTime;
         }

         if (timeFadeOut >= 0) {
            this.titleFadeOut = timeFadeOut;
         }

         if (this.titlesTimer > 0) {
            this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
         }
      }
   }

   public void setRecordPlaying(IChatComponent component, boolean isPlaying) {
      this.setRecordPlaying(component.getUnformattedText(), isPlaying);
   }

   public GuiNewChat getChatGUI() {
      return this.persistantChatGUI;
   }

   public int getUpdateCounter() {
      return this.updateCounter;
   }

   public FontRenderer getFontRenderer() {
      return this.mc.fontRendererObj;
   }

   public GuiSpectator getSpectatorGui() {
      return this.spectatorGui;
   }

   public GuiPlayerTabOverlay getTabList() {
      return this.overlayPlayerList;
   }

   public void resetPlayersOverlayFooterHeader() {
      this.overlayPlayerList.resetFooterHeader();
   }
}
