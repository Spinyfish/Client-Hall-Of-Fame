package intent.AquaDev.aqua.modules.visual;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventPostRender2D;
import events.listeners.EventRender2D;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.modules.combat.Killaura;
import intent.AquaDev.aqua.utils.RenderUtil;
import intent.AquaDev.aqua.utils.StencilUtil;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class TargetHUD extends Module {
   float lostHealthPercentage = 0.0F;
   float lastHealthPercentage = 0.0F;
   public int mouseX;
   public int mouseY;
   public int lastMouseX;
   public int lastMouseY;
   public boolean pressed;
   public boolean dragged;

   public TargetHUD() {
      super("TargetHUD", Module.Type.Visual, "TargetHUD", 0, Category.Visual);
      Aqua.setmgr.register(new Setting("ClientColor", this, true));
      Aqua.setmgr.register(new Setting("CornerRadius", this, 4.0, 0.0, 12.0, false));
      Aqua.setmgr.register(new Setting("Mode", this, "Glow", new String[]{"Glow", "Shadow"}));
      Aqua.setmgr.register(new Setting("RenderMode", this, "2D", new String[]{"2D", "Rotation"}));
      Aqua.setmgr
         .register(
            new Setting(
               "TargetHUDMode",
               this,
               "Glow",
               new String[]{"1", "2", "NovolineOld", "Novoline", "Classic", "Hanabi", "Rise", "RiseGlow", "Rise6", "Tenacity", "Tenacity2"}
            )
         );
      Aqua.setmgr.register(new Setting("Color", this));
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @Override
   public void onDisable() {
      super.onDisable();
   }

   @Override
   public void onEvent(Event e) {
      if (e instanceof EventPostRender2D) {
         if (GuiNewChat.animatedChatOpen && Killaura.target == null) {
            if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("2")) {
               this.drawTargetHUD2(mc.thePlayer, Mouse.getX() / 2, (Display.getHeight() - Mouse.getY()) / 2);
            }

            if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("Tenacity")
               || Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("Tenacity2")) {
               this.drawTencityHUD(mc.thePlayer, Mouse.getX() / 2, (Display.getHeight() - Mouse.getY()) / 2);
            }

            if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("NovolineOld")) {
               if (Aqua.setmgr.getSetting("TargetHUDRenderMode").getCurrentMode().equalsIgnoreCase("Rotation")) {
                  this.drawTargetHUDOldNovolineFollowing(mc.thePlayer);
               }

               if (!Aqua.setmgr.getSetting("TargetHUDRenderMode").getCurrentMode().equalsIgnoreCase("Rotation")) {
                  this.drawTargetHUDOldNovoline(mc.thePlayer, Mouse.getX() / 2, (Display.getHeight() - Mouse.getY()) / 2);
               }
            }

            if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("Novoline")) {
               this.drawTargetHUDNovoline(mc.thePlayer, Mouse.getX() / 2, (Display.getHeight() - Mouse.getY()) / 2);
            }

            if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("Rise")) {
               this.drawTargetHUDRise(mc.thePlayer, Mouse.getX() / 2, (Display.getHeight() - Mouse.getY()) / 2);
            }

            if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("Rise6")) {
               this.drawTargetHUDRise6(mc.thePlayer, Mouse.getX() / 2, (Display.getHeight() - Mouse.getY()) / 2);
            }

            if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("RiseGlow")) {
               this.drawTargetHUDRiseGlow(mc.thePlayer, Mouse.getX() / 2, (Display.getHeight() - Mouse.getY()) / 2);
            }

            if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("Hanabi")) {
               this.drawTargetHUDHanabi(mc.thePlayer);
            }

            if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("1")) {
               this.drawTargetHUD(mc.thePlayer, Mouse.getX() / 2, (Display.getHeight() - Mouse.getY()) / 2);
            }

            if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("Classic")) {
               if (Aqua.setmgr.getSetting("TargetHUDRenderMode").getCurrentMode().equalsIgnoreCase("Rotation")) {
                  this.drawTargetHUDClassicFollowing(mc.thePlayer);
               }

               if (!Aqua.setmgr.getSetting("TargetHUDRenderMode").getCurrentMode().equalsIgnoreCase("Rotation")) {
                  this.drawTargetHUDClassic(mc.thePlayer);
               }
            }
         }

         if (Killaura.target != null) {
            if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("Classic")) {
               if (Aqua.setmgr.getSetting("TargetHUDRenderMode").getCurrentMode().equalsIgnoreCase("Rotation")) {
                  this.drawTargetHUDClassicFollowing(Killaura.target);
               }

               if (!Aqua.setmgr.getSetting("TargetHUDRenderMode").getCurrentMode().equalsIgnoreCase("Rotation")) {
                  this.drawTargetHUDClassic(Killaura.target);
               }
            }

            if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("NovolineOld")) {
               if (Aqua.setmgr.getSetting("TargetHUDRenderMode").getCurrentMode().equalsIgnoreCase("Rotation")) {
                  this.drawTargetHUDOldNovolineFollowing(Killaura.target);
               }

               if (!Aqua.setmgr.getSetting("TargetHUDRenderMode").getCurrentMode().equalsIgnoreCase("Rotation")) {
                  this.drawTargetHUDOldNovoline(Killaura.target, Mouse.getX() / 2, (Display.getHeight() - Mouse.getY()) / 2);
               }
            }

            if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("Novoline")) {
               this.drawTargetHUDNovoline(Killaura.target, Mouse.getX() / 2, (Display.getHeight() - Mouse.getY()) / 2);
            }

            if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("Rise")) {
               this.drawTargetHUDRise(Killaura.target, Mouse.getX() / 2, (Display.getHeight() - Mouse.getY()) / 2);
            }

            if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("Rise6")) {
               this.drawTargetHUDRise6(Killaura.target, Mouse.getX() / 2, (Display.getHeight() - Mouse.getY()) / 2);
            }

            if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("RiseGlow")) {
               this.drawTargetHUDRiseGlow(Killaura.target, Mouse.getX() / 2, (Display.getHeight() - Mouse.getY()) / 2);
            }

            if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("Hanabi")) {
               this.drawTargetHUDHanabi(Killaura.target);
            }

            if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("1")) {
               this.drawTargetHUD(Killaura.target, Mouse.getX() / 2, (Display.getHeight() - Mouse.getY()) / 2);
            }

            if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("2")) {
               this.drawTargetHUD2(Killaura.target, Mouse.getX() / 2, (Display.getHeight() - Mouse.getY()) / 2);
            }

            if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("Tenacity")
               || Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("Tenacity2")) {
               this.drawTencityHUD(Killaura.target, Mouse.getX() / 2, (Display.getHeight() - Mouse.getY()) / 2);
            }
         }
      }

      if (e instanceof EventRender2D) {
         if (GuiNewChat.animatedChatOpen && Killaura.target == null) {
            if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("1")) {
               this.drawTargetHUDShaders(mc.thePlayer, Mouse.getX() / 2, (Display.getHeight() - Mouse.getY()) / 2);
            }

            if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("Rise")
               || Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("RiseGlow")) {
               this.drawTargetHUDRiseShaders(mc.thePlayer, Mouse.getX() / 2, (Display.getHeight() - Mouse.getY()) / 2);
            }

            if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("Rise6")) {
               this.drawTargetHUDRise6Shaders(mc.thePlayer, Mouse.getX() / 2, (Display.getHeight() - Mouse.getY()) / 2);
            }

            if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("Tenacity")) {
               this.drawTencityHUDBlur(mc.thePlayer, Mouse.getX() / 2, (Display.getHeight() - Mouse.getY()) / 2);
            }

            if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("2")) {
               this.drawTargetHUD2Blur(mc.thePlayer);
            }
         }

         if (Killaura.target != null) {
            if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("1")) {
               this.drawTargetHUDShaders(Killaura.target, Mouse.getX() / 2, (Display.getHeight() - Mouse.getY()) / 2);
            }

            if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("Rise")
               || Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("RiseGlow")) {
               this.drawTargetHUDRiseShaders(Killaura.target, Mouse.getX() / 2, (Display.getHeight() - Mouse.getY()) / 2);
            }

            if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("Rise6")) {
               this.drawTargetHUDRise6Shaders(Killaura.target, Mouse.getX() / 2, (Display.getHeight() - Mouse.getY()) / 2);
            }

            if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("Tenacity")) {
               this.drawTencityHUDBlur(Killaura.target, Mouse.getX() / 2, (Display.getHeight() - Mouse.getY()) / 2);
            }

            if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("2")) {
               this.drawTargetHUD2Blur(Killaura.target);
            }
         }
      }
   }

   public void drawTencityHUD(EntityLivingBase target1, int mouseX, int mouseY) {
      if (Mouse.isButtonDown(0) && mc.currentScreen instanceof GuiChat) {
         this.mouseX = mouseX;
         this.mouseY = mouseY;
      } else {
         mouseX = this.mouseX;
         mouseY = this.mouseY;
      }

      ScaledResolution s = new ScaledResolution(mc);
      float cornerRadius = (float)Aqua.setmgr.getSetting("TargetHUDCornerRadius").getCurrentNumber();
      new Color(0, 0, 0, 120);
      new Color(59, 59, 59, 160);
      Color healthBarColor = Color.green;
      new Color(20, 81, 208);
      GL11.glPushMatrix();
      int left = mouseX;
      int right2 = 142;
      int right = s.getScaledWidth() / 2 + right2;
      int right3 = 40 + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) + 29;
      int top = mouseY;
      int bottom = mouseY + 50;
      float curTargetHealth = target1.getHealth();
      float healthProcent = target1.getHealth() / target1.getMaxHealth();
      if (this.lastHealthPercentage != healthProcent) {
         this.lostHealthPercentage += this.lastHealthPercentage - healthProcent;
      }

      this.lastHealthPercentage = healthProcent;
      float finalHealthPercentage = healthProcent + this.lostHealthPercentage;
      this.lostHealthPercentage = Math.max(0.0F, this.lostHealthPercentage - this.lostHealthPercentage / 20.0F);
      int rectRight = right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) / 2 - 5;
      float healthPos = finalHealthPercentage * (float)right3;
      if (Aqua.setmgr.getSetting("TargetHUDClientColor").isState()) {
         ESP.getRGB(this.getColor());
      } else {
         ESP.getRGB(this.getColor2());
      }

      Color colorAlpha = RenderUtil.getColorAlpha(
         new Color(
               !Aqua.setmgr.getSetting("TargetHUDClientColor").isState()
                  ? Aqua.setmgr.getSetting("TargetHUDColor").getColor()
                  : Aqua.setmgr.getSetting("HUDColor").getColor()
            )
            .getRGB(),
         90
      );
      if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("Tenacity")) {
         RenderUtil.drawRoundedRect2Alpha(
            (double)(left - 7),
            (double)(top + 11),
            (double)((float)(right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName())) - (float)s.getScaledWidth() / 2.0F - 24.0F),
            43.0,
            4.0,
            new Color(20, 20, 20, 50)
         );
      } else {
         Shadow.drawGlow(
            () -> RenderUtil.drawRoundedRectGradient(
                  (double)(left - 7),
                  (double)(top + 11),
                  (double)((float)(right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName())) - (float)s.getScaledWidth() / 2.0F - 24.0F),
                  43.0,
                  4.0,
                  new Color(Aqua.setmgr.getSetting("HUDColor").getColor()),
                  new Color(Aqua.setmgr.getSetting("ArraylistColor").getColor())
               ),
            false
         );
         RenderUtil.drawRoundedRectGradient(
            (double)(left - 7),
            (double)(top + 11),
            (double)((float)(right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName())) - (float)s.getScaledWidth() / 2.0F - 24.0F),
            43.0,
            4.0,
            new Color(Aqua.setmgr.getSetting("HUDColor").getColor()),
            new Color(Aqua.setmgr.getSetting("ArraylistColor").getColor())
         );
      }

      if (this.lastHealthPercentage != healthProcent) {
         this.lostHealthPercentage += this.lastHealthPercentage - healthProcent;
      }

      this.lastHealthPercentage = healthProcent;
      this.lostHealthPercentage = Math.max(0.0F, this.lostHealthPercentage - this.lostHealthPercentage / 20.0F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("Tenacity")) {
         Shadow.drawGlow(() -> RenderUtil.drawRoundedRect((double)(left + 35), (double)(bottom - 17), (double)healthPos, 5.0, 2.0, -1), false);
      }

      RenderUtil.drawRoundedRect2Alpha((double)(left + 35), (double)(bottom - 17), (double)right3, 5.0, 2.0, new Color(20, 20, 20, 20));
      RenderUtil.drawRoundedRect((double)(left + 35), (double)(bottom - 17), (double)healthPos, 5.0, 2.0, -1);
      Aqua.INSTANCE.tenacityBig.drawString(target1.getName(), (float)(left + 50), (float)(top + 15), -1);
      String distance = String.valueOf(Math.round(mc.thePlayer.getDistanceToEntity(target1)));
      Aqua.INSTANCE.tenacityNormal.drawString(Math.round(target1.getHealth()) + "H - " + distance + ".0M", (float)(left + 50), (float)(top + 40), -1);

      for(Object nextObject : GuiPlayerTabOverlay.field_175252_a.sortedCopy(mc.thePlayer.sendQueue.getPlayerInfoMap())) {
         NetworkPlayerInfo networkPlayerInfo = (NetworkPlayerInfo)nextObject;
         if (mc.theWorld.getPlayerEntityByUUID(networkPlayerInfo.getGameProfile().getId()) == target1) {
            GlStateManager.enableCull();
            mc.getTextureManager().bindTexture(networkPlayerInfo.getLocationSkin());
            GlStateManager.pushMatrix();
            if (target1.hurtTime != 0) {
            }

            StencilUtil.write(false);
            RenderUtil.drawCircle((double)left + 13.3, (double)(top + 32), 16.0, -1);
            StencilUtil.erase(true);
            if (Aqua.setmgr.getSetting("TargetHUDTargetHUDMode").getCurrentMode().equalsIgnoreCase("Tenacity")) {
               double offset = (double)(-(((AbstractClientPlayer)target1).hurtTime * 23));
               RenderUtil.color(new Color(255, (int)(255.0 + offset), (int)(255.0 + offset)));
            }

            Gui.drawScaledCustomSizeModalRect(left - 3, top + 15, 8.0F, 8.0F, 8, 8, 34, 34, 64.0F, 66.0F);
            StencilUtil.dispose();
            GlStateManager.popMatrix();
         }
      }

      GL11.glPopMatrix();
   }

   public void drawTencityHUDBlur(EntityLivingBase target1, int mouseX, int mouseY) {
      if (Mouse.isButtonDown(0) && mc.currentScreen instanceof GuiChat) {
         this.mouseX = mouseX;
         this.mouseY = mouseY;
      } else {
         mouseX = this.mouseX;
         mouseY = this.mouseY;
      }

      ScaledResolution s = new ScaledResolution(mc);
      float cornerRadius = (float)Aqua.setmgr.getSetting("TargetHUDCornerRadius").getCurrentNumber();
      new Color(0, 0, 0, 120);
      new Color(59, 59, 59, 160);
      Color healthBarColor = Color.green;
      new Color(20, 81, 208);
      GL11.glPushMatrix();
      int left = mouseX;
      int right2 = 142;
      int right = s.getScaledWidth() / 2 + right2;
      int right3 = 40 + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) + 29;
      int top = mouseY;
      int bottom = mouseY + 50;
      float curTargetHealth = target1.getHealth();
      float healthProcent = target1.getHealth() / target1.getMaxHealth();
      if (this.lastHealthPercentage != healthProcent) {
         this.lostHealthPercentage += this.lastHealthPercentage - healthProcent;
      }

      this.lastHealthPercentage = healthProcent;
      float finalHealthPercentage = healthProcent + this.lostHealthPercentage;
      this.lostHealthPercentage = Math.max(0.0F, this.lostHealthPercentage - this.lostHealthPercentage / 20.0F);
      int rectRight = right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) / 2 - 5;
      float healthPos = finalHealthPercentage * (float)right3;
      if (Aqua.setmgr.getSetting("TargetHUDClientColor").isState()) {
         ESP.getRGB(this.getColor());
      } else {
         ESP.getRGB(this.getColor2());
      }

      Color colorAlpha = RenderUtil.getColorAlpha(
         new Color(
               !Aqua.setmgr.getSetting("TargetHUDClientColor").isState()
                  ? Aqua.setmgr.getSetting("TargetHUDColor").getColor()
                  : Aqua.setmgr.getSetting("HUDColor").getColor()
            )
            .getRGB(),
         90
      );
      if (Aqua.moduleManager.getModuleByName("Blur").isToggled()) {
         Blur.drawBlurred(
            () -> RenderUtil.drawRoundedRect2Alpha(
                  (double)(left - 7),
                  (double)(top + 11),
                  (double)((float)(right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName())) - (float)s.getScaledWidth() / 2.0F - 24.0F),
                  43.0,
                  4.0,
                  colorAlpha
               ),
            false
         );
      }

      GL11.glPopMatrix();
   }

   public void drawTargetHUDClassic(EntityLivingBase target1) {
      ScaledResolution s = new ScaledResolution(mc);
      float cornerRadius = (float)Aqua.setmgr.getSetting("TargetHUDCornerRadius").getCurrentNumber();
      new Color(0, 0, 0, 120);
      new Color(59, 59, 59, 160);
      Color healthBarColor = Color.green;
      new Color(20, 81, 208);
      GL11.glPushMatrix();
      int left = s.getScaledWidth() / 2 + 5 + 62;
      int right2 = 142;
      int right = s.getScaledWidth() / 2 + right2;
      int right3 = 80 + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) + 29;
      int top = s.getScaledHeight() / 2 - 25 + 70;
      int bottom = s.getScaledHeight() / 2 + 25 + 70;
      float curTargetHealth = target1.getHealth();
      float healthProcent = target1.getHealth() / target1.getMaxHealth();
      if (this.lastHealthPercentage != healthProcent) {
         this.lostHealthPercentage += this.lastHealthPercentage - healthProcent;
      }

      this.lastHealthPercentage = healthProcent;
      float finalHealthPercentage = healthProcent + this.lostHealthPercentage;
      this.lostHealthPercentage = Math.max(0.0F, this.lostHealthPercentage - this.lostHealthPercentage / 20.0F);
      int rectRight = right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) / 2 - 5;
      float healthPos = finalHealthPercentage * (float)right3;
      RenderUtil.drawRoundedRect2Alpha(
         (double)(left - 8),
         (double)(top + 9),
         (double)((float)(right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName())) - (float)s.getScaledWidth() / 2.0F - 20.0F),
         52.0,
         0.0,
         Color.black
      );
      RenderUtil.drawRoundedRect2Alpha(
         (double)(left - 7),
         (double)(top + 10),
         (double)((float)(right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName())) - (float)s.getScaledWidth() / 2.0F - 22.0F),
         50.0,
         0.0,
         new Color(30, 30, 30, 255)
      );
      if (this.lastHealthPercentage != healthProcent) {
         this.lostHealthPercentage += this.lastHealthPercentage - healthProcent;
      }

      this.lastHealthPercentage = healthProcent;
      this.lostHealthPercentage = Math.max(0.0F, this.lostHealthPercentage - this.lostHealthPercentage / 20.0F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      int[] color = Aqua.setmgr.getSetting("TargetHUDClientColor").isState() ? ESP.getRGB(this.getColor()) : ESP.getRGB(this.getColor2());
      RenderUtil.drawRoundedRect((double)(left - 2), (double)(bottom + 2), (double)healthPos, 6.0, 0.0, new Color(color[0], color[1], color[2]).getRGB());
      Aqua.INSTANCE.comfortaa3.drawString(target1.getName(), (float)(left + 50), (float)(top + 15), -1);
      mc.fontRendererObj.drawString("❤", left + 50, top + 32, -1);
      Aqua.INSTANCE.comfortaa3.drawString("Health : " + Math.round(curTargetHealth) + ".0", (float)(left + 60), (float)(bottom - 19), -1);

      for(Object nextObject : GuiPlayerTabOverlay.field_175252_a.sortedCopy(mc.thePlayer.sendQueue.getPlayerInfoMap())) {
         NetworkPlayerInfo networkPlayerInfo = (NetworkPlayerInfo)nextObject;
         if (mc.theWorld.getPlayerEntityByUUID(networkPlayerInfo.getGameProfile().getId()) == target1) {
            GlStateManager.enableCull();
            mc.getTextureManager().bindTexture(networkPlayerInfo.getLocationSkin());
            GlStateManager.pushMatrix();
            if (target1.hurtTime != 0) {
               GL11.glColor4f(1.0F, 0.0F, 0.0F, 1.0F);
            }

            Gui.drawScaledCustomSizeModalRect(s.getScaledWidth() / 2 + 10 + 55, s.getScaledHeight() / 2 - 5 + 65, 8.0F, 8.0F, 8, 8, 34, 34, 64.0F, 66.0F);
            GlStateManager.popMatrix();
         }
      }

      GL11.glPopMatrix();
   }

   public void drawTargetHUDRiseShaders(EntityLivingBase target1, int mouseX, int mouseY) {
      if (Mouse.isButtonDown(0) && mc.currentScreen instanceof GuiChat) {
         this.mouseX = mouseX;
         this.mouseY = mouseY;
      } else {
         mouseX = this.mouseX;
         mouseY = this.mouseY;
      }

      ScaledResolution s = new ScaledResolution(mc);
      float cornerRadius = (float)Aqua.setmgr.getSetting("TargetHUDCornerRadius").getCurrentNumber();
      new Color(0, 0, 0, 120);
      new Color(59, 59, 59, 160);
      Color healthBarColor = Color.green;
      new Color(20, 81, 208);
      int right2 = 142;
      int right = s.getScaledWidth() / 2 + right2;
      int right3 = 16 + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) + 29;
      int bottom = mouseY + 35;
      float curTargetHealth = target1.getHealth();
      float maxTargetHealth = target1.getMaxHealth();
      float healthProcent = target1.getHealth() / target1.getMaxHealth();
      if (this.lastHealthPercentage != healthProcent) {
         this.lostHealthPercentage += this.lastHealthPercentage - healthProcent;
      }

      this.lastHealthPercentage = healthProcent;
      float finalHealthPercentage = healthProcent + this.lostHealthPercentage;
      this.lostHealthPercentage = Math.max(0.0F, this.lostHealthPercentage - this.lostHealthPercentage / 20.0F);
      int rectRight = right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) / 2 - 5;
      float healthPos = finalHealthPercentage * (float)right3;
      float nameWidth = 38.0F;
      int scaleOffset = (int)((float)((EntityPlayer)target1).hurtTime * 0.35F);
      float posX = (float)(mouseX - 30);
      float posY = (float)(mouseY + 32);
      Blur.drawBlurred(
         () -> RenderUtil.drawRoundedRect(
               (double)(posX + 38.0F + 2.0F),
               (double)(posY - 34.0F),
               (double)(129.0F + (float)Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) / 2.0F - 18.0F),
               40.0,
               8.0,
               new Color(0, 0, 0, 0).getRGB()
            ),
         false
      );
   }

   public void drawTargetHUDRise6Shaders(EntityLivingBase target1, int mouseX, int mouseY) {
      if (Mouse.isButtonDown(0) && mc.currentScreen instanceof GuiChat) {
         this.mouseX = mouseX;
         this.mouseY = mouseY;
      } else {
         mouseX = this.mouseX;
         mouseY = this.mouseY;
      }

      ScaledResolution s = new ScaledResolution(mc);
      float cornerRadius = (float)Aqua.setmgr.getSetting("TargetHUDCornerRadius").getCurrentNumber();
      new Color(0, 0, 0, 120);
      new Color(59, 59, 59, 160);
      Color healthBarColor = Color.green;
      new Color(20, 81, 208);
      int right2 = 100;
      int right = s.getScaledWidth() / 2 + right2;
      int right3 = 16 + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) + 29;
      int bottom = mouseY + 35;
      float curTargetHealth = target1.getHealth();
      float maxTargetHealth = target1.getMaxHealth();
      float healthProcent = target1.getHealth() / target1.getMaxHealth();
      if (this.lastHealthPercentage != healthProcent) {
         this.lostHealthPercentage += this.lastHealthPercentage - healthProcent;
      }

      this.lastHealthPercentage = healthProcent;
      float finalHealthPercentage = healthProcent + this.lostHealthPercentage;
      this.lostHealthPercentage = Math.max(0.0F, this.lostHealthPercentage - this.lostHealthPercentage / 20.0F);
      int rectRight = right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) / 2 - 5;
      float healthPos = finalHealthPercentage * (float)right3;
      float nameWidth = 38.0F;
      int scaleOffset = (int)((float)((EntityPlayer)target1).hurtTime * 0.35F);
      float posX = (float)(mouseX - 30);
      float posY = (float)(mouseY + 32);
      Blur.drawBlurred(
         () -> RenderUtil.drawRoundedRect2Alpha(
               (double)(posX + 38.0F + 2.0F),
               (double)(posY - 34.0F),
               (double)((float)(right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName())) - (float)s.getScaledWidth() / 2.0F - 22.0F),
               40.0,
               3.0,
               new Color(0, 0, 0, 70)
            ),
         false
      );
   }

   public void drawTargetHUDRise(EntityLivingBase target1, int mouseX, int mouseY) {
      if (Mouse.isButtonDown(0) && mc.currentScreen instanceof GuiChat) {
         this.mouseX = mouseX;
         this.mouseY = mouseY;
      } else {
         mouseX = this.mouseX;
         mouseY = this.mouseY;
      }

      ScaledResolution s = new ScaledResolution(mc);
      float cornerRadius = (float)Aqua.setmgr.getSetting("TargetHUDCornerRadius").getCurrentNumber();
      new Color(0, 0, 0, 120);
      new Color(59, 59, 59, 160);
      Color healthBarColor = Color.green;
      new Color(20, 81, 208);
      int right2 = 142;
      int right = s.getScaledWidth() / 2 + right2;
      int right3 = 12 + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) + 22;
      int bottom = mouseY + 35;
      float curTargetHealth = target1.getHealth();
      float maxTargetHealth = target1.getMaxHealth();
      float healthProcent = target1.getHealth() / target1.getMaxHealth();
      if (this.lastHealthPercentage != healthProcent) {
         this.lostHealthPercentage += this.lastHealthPercentage - healthProcent;
      }

      this.lastHealthPercentage = healthProcent;
      float finalHealthPercentage = healthProcent + this.lostHealthPercentage;
      this.lostHealthPercentage = Math.max(0.0F, this.lostHealthPercentage - this.lostHealthPercentage / 20.0F);
      int rectRight = right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) / 2 - 5;
      float healthPos = finalHealthPercentage * (float)right3;
      float nameWidth = 38.0F;
      int scaleOffset = (int)((float)((EntityPlayer)target1).hurtTime * 0.35F);
      float posX = (float)(mouseX - 30);
      float posY = (float)(mouseY + 32);
      RenderUtil.drawRoundedRect2Alpha(
         (double)(posX + 38.0F + 2.0F),
         (double)(posY - 34.0F),
         (double)(129.0F + (float)Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) / 2.0F - 18.0F),
         40.0,
         8.0,
         new Color(0, 0, 0, 10)
      );
      if (target1 instanceof AbstractClientPlayer) {
         StencilUtil.write(false);
         RenderUtil.circle(
            (double)(posX + 38.0F + 6.0F + (float)scaleOffset / 2.0F),
            (double)(posY - 34.0F + 5.0F + (float)scaleOffset / 2.0F),
            (double)(30 - scaleOffset),
            Color.BLACK
         );
         StencilUtil.erase(true);
         double offset = (double)(-(((AbstractClientPlayer)target1).hurtTime * 23));
         RenderUtil.color(new Color(255, (int)(255.0 + offset), (int)(255.0 + offset)));
         EntityPlayer en = (EntityPlayer)target1;
         renderPlayerModelTexture(
            (double)(posX + 38.0F + 6.0F + (float)scaleOffset / 2.0F),
            (double)(posY - 34.0F + 5.0F + (float)scaleOffset / 2.0F),
            3.0F,
            3.0F,
            3,
            3,
            30 - scaleOffset,
            30 - scaleOffset,
            24.0F,
            24.5F,
            (AbstractClientPlayer)en
         );
         GlStateManager.resetColor();
         StencilUtil.dispose();
      }

      float offset = 39.0F;
      float drawBarPosX = posX + 38.0F;
      int[] color = Aqua.setmgr.getSetting("TargetHUDClientColor").isState() ? ESP.getRGB(this.getColor()) : ESP.getRGB(this.getColor2());
      RenderUtil.drawRoundedRect((double)(mouseX + 50), (double)(bottom - 14), (double)healthPos, 5.0, 2.0, new Color(color[0], color[1], color[2]).getRGB());
      Aqua.INSTANCE.comfortaa3.drawString(target1.getName(), (float)(mouseX + 63), (float)(mouseY + 5), -1);
      if (target1.getHealth() != 20.0F) {
         Aqua.INSTANCE
            .comfortaa4
            .drawString(String.valueOf(Math.round(Float.parseFloat(target1.getHealth() + ""))), (float)mouseX + healthPos + 52.0F, (float)bottom - 16.5F, -1);
      }
   }

   public void drawTargetHUDRiseGlow(EntityLivingBase target1, int mouseX, int mouseY) {
      if (Mouse.isButtonDown(0) && mc.currentScreen instanceof GuiChat) {
         this.mouseX = mouseX;
         this.mouseY = mouseY;
      } else {
         mouseX = this.mouseX;
         mouseY = this.mouseY;
      }

      ScaledResolution s = new ScaledResolution(mc);
      float cornerRadius = (float)Aqua.setmgr.getSetting("TargetHUDCornerRadius").getCurrentNumber();
      new Color(0, 0, 0, 120);
      new Color(59, 59, 59, 160);
      Color healthBarColor = Color.green;
      new Color(20, 81, 208);
      int left = mouseX;
      int right2 = 142;
      int right = s.getScaledWidth() / 2 + right2;
      int right3 = 12 + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) + 22;
      int top = mouseY;
      int bottom = mouseY + 35;
      float curTargetHealth = target1.getHealth();
      float maxTargetHealth = target1.getMaxHealth();
      float healthProcent = target1.getHealth() / target1.getMaxHealth();
      if (this.lastHealthPercentage != healthProcent) {
         this.lostHealthPercentage += this.lastHealthPercentage - healthProcent;
      }

      this.lastHealthPercentage = healthProcent;
      float finalHealthPercentage = healthProcent + this.lostHealthPercentage;
      this.lostHealthPercentage = Math.max(0.0F, this.lostHealthPercentage - this.lostHealthPercentage / 20.0F);
      int rectRight = right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) / 2 - 5;
      float healthPos = finalHealthPercentage * (float)right3;
      float nameWidth = 38.0F;
      int scaleOffset = (int)((float)((EntityPlayer)target1).hurtTime * 0.35F);
      float posX = (float)(left - 30);
      float posY = (float)(top + 32);
      RenderUtil.drawRoundedRect2Alpha(
         (double)(posX + 38.0F + 2.0F),
         (double)(posY - 34.0F),
         (double)(129.0F + (float)Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) / 2.0F - 18.0F),
         40.0,
         8.0,
         new Color(0, 0, 0, 10)
      );
      if (target1 instanceof AbstractClientPlayer) {
         StencilUtil.write(false);
         RenderUtil.circle(
            (double)(posX + 38.0F + 6.0F + (float)scaleOffset / 2.0F),
            (double)(posY - 34.0F + 5.0F + (float)scaleOffset / 2.0F),
            (double)(30 - scaleOffset),
            Color.BLACK
         );
         StencilUtil.erase(true);
         double offset = (double)(-(((AbstractClientPlayer)target1).hurtTime * 23));
         RenderUtil.color(new Color(255, (int)(255.0 + offset), (int)(255.0 + offset)));
         EntityPlayer en = (EntityPlayer)target1;
         renderPlayerModelTexture(
            (double)(posX + 38.0F + 6.0F + (float)scaleOffset / 2.0F),
            (double)(posY - 34.0F + 5.0F + (float)scaleOffset / 2.0F),
            3.0F,
            3.0F,
            3,
            3,
            30 - scaleOffset,
            30 - scaleOffset,
            24.0F,
            24.5F,
            (AbstractClientPlayer)en
         );
         GlStateManager.resetColor();
         StencilUtil.dispose();
      }

      float offset = 39.0F;
      float drawBarPosX = posX + 38.0F;
      int[] color = Aqua.setmgr.getSetting("TargetHUDClientColor").isState() ? ESP.getRGB(this.getColor()) : ESP.getRGB(this.getColor2());
      Shadow.drawGlow(
         () -> RenderUtil.drawRoundedRect2Alpha(
               (double)(posX + 38.0F + 2.0F),
               (double)(posY - 34.0F),
               (double)(129.0F + (float)Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) / 2.0F - 18.0F),
               40.0,
               8.0,
               new Color(0, 0, 0, 170)
            ),
         false
      );
      RenderUtil.drawRoundedRect((double)(left + 50), (double)(bottom - 14), (double)healthPos, 5.0, 2.0, new Color(color[0], color[1], color[2]).getRGB());
      Shadow.drawGlow(() -> Aqua.INSTANCE.comfortaa3.drawString(target1.getName(), (float)(left + 63), (float)(top + 3), -1), false);
      Aqua.INSTANCE.comfortaa3.drawString(target1.getName(), (float)(left + 63), (float)(top + 3), -1);
      if (target1.getHealth() != 20.0F) {
         Aqua.INSTANCE
            .comfortaa4
            .drawString(String.valueOf(Math.round(Float.parseFloat(target1.getHealth() + ""))), (float)left + healthPos + 52.0F, (float)bottom - 16.5F, -1);
      }
   }

   public void drawTargetHUDRise6(EntityLivingBase target1, int mouseX, int mouseY) {
      if (Mouse.isButtonDown(0) && mc.currentScreen instanceof GuiChat) {
         this.mouseX = mouseX;
         this.mouseY = mouseY;
      } else {
         mouseX = this.mouseX;
         mouseY = this.mouseY;
      }

      ScaledResolution s = new ScaledResolution(mc);
      float cornerRadius = (float)Aqua.setmgr.getSetting("TargetHUDCornerRadius").getCurrentNumber();
      new Color(0, 0, 0, 120);
      new Color(59, 59, 59, 160);
      Color healthBarColor = Color.green;
      new Color(20, 81, 208);
      int left = mouseX;
      int right2 = 100;
      int right = s.getScaledWidth() / 2 + right2;
      int right3 = 12 + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) + 22;
      int bottom = mouseY + 35;
      float curTargetHealth = target1.getHealth();
      float maxTargetHealth = target1.getMaxHealth();
      float healthProcent = target1.getHealth() / target1.getMaxHealth();
      if (this.lastHealthPercentage != healthProcent) {
         this.lostHealthPercentage += this.lastHealthPercentage - healthProcent;
      }

      this.lastHealthPercentage = healthProcent;
      float finalHealthPercentage = healthProcent + this.lostHealthPercentage;
      this.lostHealthPercentage = Math.max(0.0F, this.lostHealthPercentage - this.lostHealthPercentage / 20.0F);
      int rectRight = right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) / 2 - 5;
      float healthPos = finalHealthPercentage * (float)right3;
      float nameWidth = 38.0F;
      int scaleOffset = (int)((float)((EntityPlayer)target1).hurtTime * 0.35F);
      float posX = (float)(left - 30);
      float posY = (float)(mouseY + 32);
      ShaderMultiplier.drawGlowESP(
         () -> RenderUtil.drawRoundedRect2Alpha(
               (double)(posX + 38.0F + 2.0F),
               (double)(posY - 34.0F),
               (double)((float)(right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName())) - (float)s.getScaledWidth() / 2.0F - 22.0F),
               40.0,
               3.0,
               new Color(0, 0, 0, 180)
            ),
         false
      );
      RenderUtil.drawRoundedRect2Alpha(
         (double)(posX + 38.0F + 2.0F),
         (double)(posY - 34.0F),
         (double)((float)(right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName())) - (float)s.getScaledWidth() / 2.0F - 22.0F),
         40.0,
         3.0,
         new Color(0, 0, 0, 50)
      );
      if (target1 instanceof AbstractClientPlayer) {
         StencilUtil.write(false);
         RenderUtil.drawRoundedRect((double)(left + 14), (double)(mouseY + 2), 32.0, 32.0, 3.0, Color.white.getRGB());
         StencilUtil.erase(true);
         double offset = (double)(-(((AbstractClientPlayer)target1).hurtTime * 3));
         RenderUtil.color(new Color(255, (int)(255.0 + offset), (int)(255.0 + offset)));
         EntityPlayer en = (EntityPlayer)target1;
         renderPlayerModelTexture(
            (double)(posX + 38.0F + 6.0F), (double)(posY - 33.0F + 5.0F - 2.0F), 3.0F, 3.0F, 3, 3, 32, 32, 24.0F, 24.5F, (AbstractClientPlayer)en
         );
         GlStateManager.resetColor();
         StencilUtil.dispose();
      }

      float offset = 39.0F;
      float drawBarPosX = posX + 38.0F;
      int[] color = Aqua.setmgr.getSetting("TargetHUDClientColor").isState() ? ESP.getRGB(this.getColor()) : ESP.getRGB(this.getColor2());
      Shadow.drawGlow(
         () -> RenderUtil.drawRoundedRect(
               (double)(left + 50), (double)(bottom - 14), (double)healthPos, 5.0, 2.0, new Color(color[0], color[1], color[2]).getRGB()
            ),
         false
      );
      RenderUtil.drawRoundedRect2Alpha((double)(left + 50), (double)(bottom - 14), (double)right3, 5.0, 2.0, new Color(40, 40, 40, 60));
      RenderUtil.drawRoundedRect((double)(left + 50), (double)(bottom - 14), (double)healthPos, 5.0, 2.0, new Color(color[0], color[1], color[2]).getRGB());
      Aqua.INSTANCE.comfortaa4.drawString("Name: ", (float)(left + 50), (float)(mouseY + 5), Color.white.getRGB());
      Aqua.INSTANCE.comfortaa5.drawString(target1.getName(), (float)(left + 80), (float)mouseY + 5.5F, new Color(color[0], color[1], color[2]).getRGB());
      if (target1.getHealth() < 19.0F) {
         Aqua.INSTANCE
            .comfortaa4
            .drawString(String.valueOf(Math.round(Float.parseFloat(target1.getHealth() + ""))), (float)left + healthPos + 52.0F, (float)bottom - 16.5F, -1);
      }
   }

   public void drawTargetHUDClassicFollowing(EntityLivingBase target1) {
      ScaledResolution s = new ScaledResolution(mc);
      float cornerRadius = (float)Aqua.setmgr.getSetting("TargetHUDCornerRadius").getCurrentNumber();
      new Color(0, 0, 0, 120);
      new Color(59, 59, 59, 160);
      Color healthBarColor = Color.green;
      new Color(20, 81, 208);
      GL11.glPushMatrix();
      int left = (int)(
         (double)(s.getScaledWidth() / 2 - 140) - RenderUtil.interpolate((double)mc.thePlayer.getRotationYawHead(), (double)mc.thePlayer.rotationYawHead, 1.0)
      );
      int right2 = 142;
      int right = s.getScaledWidth() / 2 + right2;
      int right3 = 80 + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) + 29;
      int top = (int)(
         (double)(s.getScaledHeight() / 2)
            + RenderUtil.interpolate((double)mc.thePlayer.getRotationPitchHead(), (double)mc.thePlayer.rotationPitchHead, 1.0)
            - 30.0
      );
      int bottom = s.getScaledHeight() / 2 + 25 + 70;
      float curTargetHealth = target1.getHealth();
      float healthProcent = target1.getHealth() / target1.getMaxHealth();
      if (this.lastHealthPercentage != healthProcent) {
         this.lostHealthPercentage += this.lastHealthPercentage - healthProcent;
      }

      this.lastHealthPercentage = healthProcent;
      float finalHealthPercentage = healthProcent + this.lostHealthPercentage;
      this.lostHealthPercentage = Math.max(0.0F, this.lostHealthPercentage - this.lostHealthPercentage / 20.0F);
      int rectRight = right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) / 2 - 5;
      float healthPos = finalHealthPercentage * (float)right3;
      RenderUtil.drawRoundedRect2Alpha(
         (double)(left - 8),
         (double)(top + 9),
         (double)((float)(right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName())) - (float)s.getScaledWidth() / 2.0F - 20.0F),
         (double)((float)bottom / 6.0F - 6.0F),
         0.0,
         Color.black
      );
      RenderUtil.drawRoundedRect2Alpha(
         (double)(left - 7),
         (double)(top + 10),
         (double)((float)(right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName())) - (float)s.getScaledWidth() / 2.0F - 22.0F),
         (double)((float)bottom / 6.0F - 8.0F),
         0.0,
         new Color(30, 30, 30, 255)
      );
      if (this.lastHealthPercentage != healthProcent) {
         this.lostHealthPercentage += this.lastHealthPercentage - healthProcent;
      }

      this.lastHealthPercentage = healthProcent;
      this.lostHealthPercentage = Math.max(0.0F, this.lostHealthPercentage - this.lostHealthPercentage / 20.0F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      int[] color = Aqua.setmgr.getSetting("TargetHUDClientColor").isState() ? ESP.getRGB(this.getColor()) : ESP.getRGB(this.getColor2());
      RenderUtil.drawRoundedRect(
         (double)(left - 2),
         (double)(top + 50),
         (double)healthPos,
         (double)((float)bottom / 15.0F - 19.0F),
         0.0,
         new Color(color[0], color[1], color[2]).getRGB()
      );
      Aqua.INSTANCE.comfortaa3.drawString(target1.getName(), (float)(left + 50), (float)(top + 15), -1);
      mc.fontRendererObj.drawString("❤", left + 50, top + 32, -1);
      Aqua.INSTANCE.comfortaa3.drawString("Health : " + Math.round(curTargetHealth) + ".0", (float)(left + 60), (float)(top + 31), -1);

      for(Object nextObject : GuiPlayerTabOverlay.field_175252_a.sortedCopy(mc.thePlayer.sendQueue.getPlayerInfoMap())) {
         NetworkPlayerInfo networkPlayerInfo = (NetworkPlayerInfo)nextObject;
         if (mc.theWorld.getPlayerEntityByUUID(networkPlayerInfo.getGameProfile().getId()) == target1) {
            GlStateManager.enableCull();
            mc.getTextureManager().bindTexture(networkPlayerInfo.getLocationSkin());
            GlStateManager.pushMatrix();
            if (target1.hurtTime != 0) {
               GL11.glColor4f(1.0F, 0.0F, 0.0F, 1.0F);
            }

            Gui.drawScaledCustomSizeModalRect(
               (int)(
                  (double)(s.getScaledWidth() / 2 - 142)
                     - RenderUtil.interpolate((double)mc.thePlayer.getRotationYawHead(), (double)mc.thePlayer.rotationYawHead, 1.0)
               ),
               top + 14,
               8.0F,
               8.0F,
               8,
               8,
               34,
               34,
               64.0F,
               66.0F
            );
            GlStateManager.popMatrix();
         }
      }

      GL11.glPopMatrix();
   }

   public void drawTargetHUDOldNovoline(EntityLivingBase target1, int mouseX, int mouseY) {
      if (Mouse.isButtonDown(0) && mc.currentScreen instanceof GuiChat) {
         this.mouseX = mouseX;
         this.mouseY = mouseY;
      } else {
         mouseX = this.mouseX;
         mouseY = this.mouseY;
      }

      ScaledResolution s = new ScaledResolution(mc);
      float cornerRadius = (float)Aqua.setmgr.getSetting("TargetHUDCornerRadius").getCurrentNumber();
      new Color(0, 0, 0, 120);
      new Color(59, 59, 59, 160);
      Color healthBarColor = Color.green;
      new Color(20, 81, 208);
      GL11.glPushMatrix();
      int left = mouseX;
      int right2 = 142;
      int right = s.getScaledWidth() / 2 + right2;
      int right3 = Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) + 29;
      int top = mouseY;
      int bottom = mouseY + 50;
      float curTargetHealth = target1.getHealth();
      float healthProcent = target1.getHealth() / target1.getMaxHealth();
      if (this.lastHealthPercentage != healthProcent) {
         this.lostHealthPercentage += this.lastHealthPercentage - healthProcent;
      }

      this.lastHealthPercentage = healthProcent;
      float finalHealthPercentage = healthProcent + this.lostHealthPercentage;
      this.lostHealthPercentage = Math.max(0.0F, this.lostHealthPercentage - this.lostHealthPercentage / 20.0F);
      int rectRight = right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) / 2 - 5;
      float healthPos = finalHealthPercentage * (float)right3;
      RenderUtil.drawRoundedRect2Alpha(
         (double)(mouseX - 7),
         (double)(mouseY + 12),
         (double)((float)(right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName())) - (float)s.getScaledWidth() / 2.0F - 62.0F),
         44.0,
         0.0,
         Color.BLACK
      );
      RenderUtil.drawRoundedRect2Alpha(
         (double)(mouseX - 6),
         (double)(mouseY + 13),
         (double)((float)(right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName())) - (float)s.getScaledWidth() / 2.0F - 64.0F),
         42.0,
         0.0,
         new Color(45, 45, 45, 255)
      );
      if (this.lastHealthPercentage != healthProcent) {
         this.lostHealthPercentage += this.lastHealthPercentage - healthProcent;
      }

      this.lastHealthPercentage = healthProcent;
      this.lostHealthPercentage = Math.max(0.0F, this.lostHealthPercentage - this.lostHealthPercentage / 20.0F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      int[] color = Aqua.setmgr.getSetting("TargetHUDClientColor").isState() ? ESP.getRGB(this.getColor()) : ESP.getRGB(this.getColor2());
      RenderUtil.drawRoundedRect(
         (double)(mouseX + 40),
         (double)(bottom - 20),
         (double)((float)(right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName())) - (float)s.getScaledWidth() / 2.0F - 113.0F),
         12.0,
         0.0,
         Color.black.getRGB()
      );
      RenderUtil.drawRoundedRect((double)(mouseX + 40), (double)(bottom - 20), (double)healthPos, 12.0, 0.0, new Color(color[0], color[1], color[2]).getRGB());
      mc.fontRendererObj.drawStringWithShadow(target1.getName(), (float)(mouseX + 40), (float)(mouseY + 17), -1);
      mc.fontRendererObj.drawStringWithShadow(Math.round(curTargetHealth) + ".0 ", (float)(mouseX + 41), (float)(bottom - 5), -1);
      mc.fontRendererObj.drawStringWithShadow("❤", (float)(mouseX + 62), (float)(bottom - 6), new Color(color[0], color[1], color[2]).getRGB());

      for(Object nextObject : GuiPlayerTabOverlay.field_175252_a.sortedCopy(mc.thePlayer.sendQueue.getPlayerInfoMap())) {
         NetworkPlayerInfo networkPlayerInfo = (NetworkPlayerInfo)nextObject;
         if (mc.theWorld.getPlayerEntityByUUID(networkPlayerInfo.getGameProfile().getId()) == target1) {
            GlStateManager.enableCull();
            mc.getTextureManager().bindTexture(networkPlayerInfo.getLocationSkin());
            GlStateManager.pushMatrix();
            if (target1.hurtTime != 0) {
            }

            double offset = (double)(-(((AbstractClientPlayer)target1).hurtTime * 23));
            RenderUtil.color(new Color(255, (int)(255.0 + offset), (int)(255.0 + offset)));
            Gui.drawScaledCustomSizeModalRect(left - 4, top + 14, 8.0F, 8.0F, 8, 8, 39, 40, 64.0F, 66.0F);
            GlStateManager.popMatrix();
         }
      }

      GL11.glPopMatrix();
   }

   public void drawTargetHUDNovoline(EntityLivingBase target1, int mouseX, int mouseY) {
      if (Mouse.isButtonDown(0) && mc.currentScreen instanceof GuiChat) {
         this.mouseX = mouseX;
         this.mouseY = mouseY;
      } else {
         mouseX = this.mouseX;
         mouseY = this.mouseY;
      }

      ScaledResolution s = new ScaledResolution(mc);
      float cornerRadius = (float)Aqua.setmgr.getSetting("TargetHUDCornerRadius").getCurrentNumber();
      new Color(0, 0, 0, 120);
      new Color(59, 59, 59, 160);
      Color healthBarColor = Color.green;
      new Color(20, 81, 208);
      GL11.glPushMatrix();
      int left = mouseX;
      int right2 = 142;
      int right = s.getScaledWidth() / 2 + right2;
      int right3 = Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) + 29;
      int top = mouseY;
      int bottom = mouseY + 50;
      float curTargetHealth = target1.getHealth();
      float healthProcent = target1.getHealth() / target1.getMaxHealth();
      if (this.lastHealthPercentage != healthProcent) {
         this.lostHealthPercentage += this.lastHealthPercentage - healthProcent;
      }

      this.lastHealthPercentage = healthProcent;
      float finalHealthPercentage = healthProcent + this.lostHealthPercentage;
      this.lostHealthPercentage = Math.max(0.0F, this.lostHealthPercentage - this.lostHealthPercentage / 20.0F);
      int rectRight = right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) / 2 - 5;
      float healthPos = finalHealthPercentage * (float)right3;
      Shadow.drawGlow(
         () -> RenderUtil.drawRoundedRect2Alpha(
               (double)(left - 7),
               (double)(top + 12),
               (double)((float)(right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName())) - (float)s.getScaledWidth() / 2.0F - 62.0F),
               44.0,
               2.0,
               new Color(0, 0, 0, 220)
            ),
         false
      );
      RenderUtil.drawRoundedRect2Alpha(
         (double)(left - 7),
         (double)(top + 12),
         (double)((float)(right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName())) - (float)s.getScaledWidth() / 2.0F - 62.0F),
         44.0,
         2.0,
         new Color(0, 0, 0, 70)
      );
      if (this.lastHealthPercentage != healthProcent) {
         this.lostHealthPercentage += this.lastHealthPercentage - healthProcent;
      }

      this.lastHealthPercentage = healthProcent;
      this.lostHealthPercentage = Math.max(0.0F, this.lostHealthPercentage - this.lostHealthPercentage / 20.0F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      int[] color = Aqua.setmgr.getSetting("TargetHUDClientColor").isState() ? ESP.getRGB(this.getColor()) : ESP.getRGB(this.getColor2());
      RenderUtil.drawRoundedRect(
         (double)(left + 40),
         (double)(bottom - 20),
         (double)((float)(right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName())) - (float)s.getScaledWidth() / 2.0F - 113.0F),
         12.0,
         0.0,
         Color.black.getRGB()
      );
      RenderUtil.drawRoundedRectGradient(
         (double)(left + 40),
         (double)(bottom - 20),
         (double)healthPos,
         12.0,
         0.0,
         new Color(Aqua.setmgr.getSetting("HUDColor").getColor()),
         new Color(Aqua.setmgr.getSetting("ArraylistColor").getColor())
      );
      mc.fontRendererObj.drawStringWithShadow(target1.getName(), (float)(left + 40), (float)(top + 17), -1);
      mc.fontRendererObj.drawStringWithShadow(Math.round(curTargetHealth) + ".0 ", (float)(left + 41), (float)(bottom - 5), -1);
      mc.fontRendererObj.drawStringWithShadow("❤", (float)(left + 62), (float)(bottom - 6), new Color(color[0], color[1], color[2]).getRGB());

      for(Object nextObject : GuiPlayerTabOverlay.field_175252_a.sortedCopy(mc.thePlayer.sendQueue.getPlayerInfoMap())) {
         NetworkPlayerInfo networkPlayerInfo = (NetworkPlayerInfo)nextObject;
         if (mc.theWorld.getPlayerEntityByUUID(networkPlayerInfo.getGameProfile().getId()) == target1) {
            GlStateManager.enableCull();
            mc.getTextureManager().bindTexture(networkPlayerInfo.getLocationSkin());
            GlStateManager.pushMatrix();
            if (target1.hurtTime != 0) {
            }

            double offset = (double)(-(((AbstractClientPlayer)target1).hurtTime * 23));
            RenderUtil.color(new Color(255, (int)(255.0 + offset), (int)(255.0 + offset)));
            Gui.drawScaledCustomSizeModalRect(left - 4, top + 14, 8.0F, 8.0F, 8, 8, 39, 40, 64.0F, 66.0F);
            GlStateManager.popMatrix();
         }
      }

      GL11.glPopMatrix();
   }

   public void drawTargetHUDOldNovolineFollowing(EntityLivingBase target1) {
      ScaledResolution s = new ScaledResolution(mc);
      float cornerRadius = (float)Aqua.setmgr.getSetting("TargetHUDCornerRadius").getCurrentNumber();
      new Color(0, 0, 0, 120);
      new Color(59, 59, 59, 160);
      Color healthBarColor = Color.green;
      new Color(20, 81, 208);
      GL11.glPushMatrix();
      float angle = getAngle(target1) % 360.0F + 180.0F;
      int left = (int)(
         (double)(s.getScaledWidth() / 2 - 140) - RenderUtil.interpolate((double)mc.thePlayer.getRotationYawHead(), (double)mc.thePlayer.rotationYawHead, 1.0)
      );
      int right2 = 142;
      int right = s.getScaledWidth() / 2 + right2;
      int right3 = Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) + 29;
      int top = (int)(
         (double)(s.getScaledHeight() / 2)
            + RenderUtil.interpolate((double)mc.thePlayer.getRotationPitchHead(), (double)mc.thePlayer.rotationPitchHead, 1.0)
            - 30.0
      );
      int bottom = s.getScaledHeight() / 2 + 25 + 60;
      float curTargetHealth = target1.getHealth();
      float healthProcent = target1.getHealth() / target1.getMaxHealth();
      if (this.lastHealthPercentage != healthProcent) {
         this.lostHealthPercentage += this.lastHealthPercentage - healthProcent;
      }

      this.lastHealthPercentage = healthProcent;
      float finalHealthPercentage = healthProcent + this.lostHealthPercentage;
      this.lostHealthPercentage = Math.max(0.0F, this.lostHealthPercentage - this.lostHealthPercentage / 20.0F);
      int rectRight = right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) / 2 - 5;
      float healthPos = finalHealthPercentage * (float)right3;
      RenderUtil.drawRoundedRect2Alpha(
         (double)(left - 6),
         (double)(top + 12),
         (double)((float)(right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName())) - (float)s.getScaledWidth() / 2.0F - 62.0F),
         (double)((float)bottom / 6.0F - 18.0F),
         0.0,
         Color.BLACK
      );
      RenderUtil.drawRoundedRect2Alpha(
         (double)(left - 5),
         (double)(top + 13),
         (double)((float)(right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName())) - (float)s.getScaledWidth() / 2.0F - 64.0F),
         (double)((float)bottom / 6.0F - 20.0F),
         0.0,
         new Color(45, 45, 45, 255)
      );
      if (this.lastHealthPercentage != healthProcent) {
         this.lostHealthPercentage += this.lastHealthPercentage - healthProcent;
      }

      this.lastHealthPercentage = healthProcent;
      this.lostHealthPercentage = Math.max(0.0F, this.lostHealthPercentage - this.lostHealthPercentage / 20.0F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      int[] color = Aqua.setmgr.getSetting("TargetHUDClientColor").isState() ? ESP.getRGB(this.getColor()) : ESP.getRGB(this.getColor2());
      RenderUtil.drawRoundedRect(
         (double)(left + 40),
         (double)(top + 30),
         (double)((float)(right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName())) - (float)s.getScaledWidth() / 2.0F - 113.0F),
         (double)((float)bottom / 15.0F - 11.0F),
         0.0,
         Color.black.getRGB()
      );
      RenderUtil.drawRoundedRect(
         (double)(left + 40),
         (double)(top + 30),
         (double)healthPos,
         (double)((float)bottom / 15.0F - 11.0F),
         0.0,
         new Color(color[0], color[1], color[2]).getRGB()
      );
      mc.fontRendererObj.drawStringWithShadow(target1.getName(), (float)(left + 40), (float)(top + 17), -1);

      for(Object nextObject : GuiPlayerTabOverlay.field_175252_a.sortedCopy(mc.thePlayer.sendQueue.getPlayerInfoMap())) {
         NetworkPlayerInfo networkPlayerInfo = (NetworkPlayerInfo)nextObject;
         if (mc.theWorld.getPlayerEntityByUUID(networkPlayerInfo.getGameProfile().getId()) == target1) {
            GlStateManager.enableCull();
            mc.getTextureManager().bindTexture(networkPlayerInfo.getLocationSkin());
            GlStateManager.pushMatrix();
            if (target1.hurtTime != 0) {
            }

            Gui.drawScaledCustomSizeModalRect(
               (int)(
                  (double)(s.getScaledWidth() / 2 - 143)
                     - RenderUtil.interpolate((double)mc.thePlayer.getRotationYawHead(), (double)mc.thePlayer.rotationYawHead, 1.0)
               ),
               top + 14,
               8.0F,
               8.0F,
               8,
               8,
               35,
               34,
               64.0F,
               66.0F
            );
            GlStateManager.popMatrix();
         }
      }

      GL11.glPopMatrix();
   }

   public void drawTargetHUD2(EntityLivingBase target1, int mouseX, int mouseY) {
      if (Mouse.isButtonDown(0) && mc.currentScreen instanceof GuiChat) {
         this.mouseX = mouseX;
         this.mouseY = mouseY;
      } else {
         mouseX = this.mouseX;
         mouseY = this.mouseY;
      }

      ScaledResolution s = new ScaledResolution(mc);
      float cornerRadius = (float)Aqua.setmgr.getSetting("TargetHUDCornerRadius").getCurrentNumber();
      new Color(0, 0, 0, 120);
      new Color(59, 59, 59, 160);
      Color healthBarColor = Color.green;
      new Color(20, 81, 208);
      GL11.glPushMatrix();
      int left = mouseX;
      int right2 = 142;
      int right = s.getScaledWidth() / 2 + right2;
      int right3 = 80 + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) + 29;
      int top = mouseY;
      int bottom = mouseY + 50;
      float curTargetHealth = target1.getHealth();
      float healthProcent = target1.getHealth() / target1.getMaxHealth();
      if (this.lastHealthPercentage != healthProcent) {
         this.lostHealthPercentage += this.lastHealthPercentage - healthProcent;
      }

      this.lastHealthPercentage = healthProcent;
      float finalHealthPercentage = healthProcent + this.lostHealthPercentage;
      this.lostHealthPercentage = Math.max(0.0F, this.lostHealthPercentage - this.lostHealthPercentage / 20.0F);
      int rectRight = right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) / 2 - 5;
      float healthPos = finalHealthPercentage * (float)right3;
      RenderUtil.drawRoundedRect2Alpha(
         (double)(left - 7),
         (double)(mouseY + 11),
         (double)((float)(right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName())) - (float)s.getScaledWidth() / 2.0F - 22.0F),
         48.0,
         (double)cornerRadius,
         new Color(20, 20, 20, 60)
      );
      if (this.lastHealthPercentage != healthProcent) {
         this.lostHealthPercentage += this.lastHealthPercentage - healthProcent;
      }

      this.lastHealthPercentage = healthProcent;
      this.lostHealthPercentage = Math.max(0.0F, this.lostHealthPercentage - this.lostHealthPercentage / 20.0F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      int[] color = Aqua.setmgr.getSetting("TargetHUDClientColor").isState() ? ESP.getRGB(this.getColor()) : ESP.getRGB(this.getColor2());
      Shadow.drawGlow(
         () -> RenderUtil.drawRoundedRect(
               (double)(left - 2), (double)(bottom + 2), (double)healthPos, 4.0, 2.0, new Color(color[0], color[1], color[2]).getRGB()
            ),
         false
      );
      RenderUtil.drawRoundedRect((double)(left - 2), (double)(bottom + 2), (double)healthPos, 4.0, 2.0, new Color(color[0], color[1], color[2]).getRGB());
      Aqua.INSTANCE.comfortaa3.drawString(target1.getName(), (float)(left + 50), (float)(mouseY + 15), -1);
      mc.fontRendererObj.drawString("❤", left + 50, mouseY + 32, -1);
      Aqua.INSTANCE.comfortaa3.drawString("Health : " + Math.round(curTargetHealth) + ".0", (float)(left + 60), (float)(bottom - 19), -1);

      for(Object nextObject : GuiPlayerTabOverlay.field_175252_a.sortedCopy(mc.thePlayer.sendQueue.getPlayerInfoMap())) {
         NetworkPlayerInfo networkPlayerInfo = (NetworkPlayerInfo)nextObject;
         if (mc.theWorld.getPlayerEntityByUUID(networkPlayerInfo.getGameProfile().getId()) == target1) {
            GlStateManager.enableCull();
            mc.getTextureManager().bindTexture(networkPlayerInfo.getLocationSkin());
            GlStateManager.pushMatrix();
            if (target1.hurtTime != 0) {
            }

            StencilUtil.write(false);
            RenderUtil.drawRoundedRect((double)(left - 2), (double)(top + 15), 34.0, 34.0, (double)cornerRadius, Color.white.getRGB());
            StencilUtil.erase(true);
            double offset = (double)(-(((AbstractClientPlayer)target1).hurtTime * 23));
            RenderUtil.color(new Color(255, (int)(255.0 + offset), (int)(255.0 + offset)));
            Gui.drawScaledCustomSizeModalRect(left - 2, top + 15, 8.0F, 8.0F, 8, 8, 34, 34, 64.0F, 66.0F);
            StencilUtil.dispose();
            GlStateManager.popMatrix();
         }
      }

      GL11.glPopMatrix();
   }

   public void drawTargetHUD2Blur(EntityLivingBase target1) {
      if (Mouse.isButtonDown(0) && mc.currentScreen instanceof GuiChat) {
         this.mouseX = this.mouseX;
         this.mouseY = this.mouseY;
      } else {
         this.mouseX = this.mouseX;
         this.mouseY = this.mouseY;
      }

      ScaledResolution s = new ScaledResolution(mc);
      float cornerRadius = (float)Aqua.setmgr.getSetting("TargetHUDCornerRadius").getCurrentNumber();
      new Color(0, 0, 0, 120);
      new Color(59, 59, 59, 160);
      Color healthBarColor = Color.green;
      new Color(20, 81, 208);
      int left = this.mouseX;
      int right2 = 142;
      int right = s.getScaledWidth() / 2 + right2;
      int right3 = 80 + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) + 29;
      int top = this.mouseY;
      int bottom = this.mouseY + 50;
      float healthProcent = target1.getHealth() / target1.getMaxHealth();
      Blur.drawBlurred(
         () -> RenderUtil.drawRoundedRect2Alpha(
               (double)(left - 7),
               (double)(top + 11),
               (double)((float)(right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName())) - (float)s.getScaledWidth() / 2.0F - 22.0F),
               48.0,
               (double)cornerRadius,
               new Color(20, 20, 20, 90)
            ),
         false
      );
   }

   public void drawTargetHUD(EntityLivingBase target1, int mouseX, int mouseY) {
      if (Mouse.isButtonDown(0) && mc.currentScreen instanceof GuiChat) {
         this.mouseX = mouseX;
         this.mouseY = mouseY;
      } else {
         mouseX = this.mouseX;
         mouseY = this.mouseY;
      }

      ScaledResolution s = new ScaledResolution(mc);
      float cornerRadius = (float)Aqua.setmgr.getSetting("TargetHUDCornerRadius").getCurrentNumber();
      new Color(0, 0, 0, 120);
      new Color(59, 59, 59, 160);
      Color healthBarColor = Color.green;
      new Color(20, 81, 208);
      int left = mouseX;
      int right2 = 142;
      int right = s.getScaledWidth() / 2 + right2;
      int right3 = 16 + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) + 29;
      int top = mouseY;
      int bottom = mouseY + 50;
      float curTargetHealth = target1.getHealth();
      float healthProcent = target1.getHealth() / target1.getMaxHealth();
      if (this.lastHealthPercentage != healthProcent) {
         this.lostHealthPercentage += this.lastHealthPercentage - healthProcent;
      }

      this.lastHealthPercentage = healthProcent;
      float finalHealthPercentage = healthProcent + this.lostHealthPercentage;
      this.lostHealthPercentage = Math.max(0.0F, this.lostHealthPercentage - this.lostHealthPercentage / 20.0F);
      int rectRight = right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) / 2 - 5;
      float healthPos = finalHealthPercentage * (float)right3;
      RenderUtil.drawRoundedRect2Alpha(
         (double)(mouseX - 7),
         (double)(mouseY + 10),
         (double)((float)(right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName())) - (float)s.getScaledWidth() / 2.0F - 22.0F),
         53.0,
         (double)cornerRadius,
         new Color(20, 20, 20, 90)
      );
      if (this.lastHealthPercentage != healthProcent) {
         this.lostHealthPercentage += this.lastHealthPercentage - healthProcent;
      }

      this.lastHealthPercentage = healthProcent;
      this.lostHealthPercentage = Math.max(0.0F, this.lostHealthPercentage - this.lostHealthPercentage / 20.0F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      int[] color = Aqua.setmgr.getSetting("TargetHUDClientColor").isState() ? ESP.getRGB(this.getColor()) : ESP.getRGB(this.getColor2());
      RenderUtil.drawRoundedRect(
         (double)(mouseX + 50), (double)bottom, (double)(healthPos + 10.0F), 8.0, 2.0, new Color(color[0], color[1], color[2]).getRGB()
      );
      Aqua.INSTANCE.comfortaa3.drawString(target1.getName(), (float)(mouseX + 50), (float)(mouseY + 15), -1);
      mc.fontRendererObj.drawString("❤", mouseX + 50, mouseY + 32, -1);
      Aqua.INSTANCE.comfortaa3.drawString("Health : " + Math.round(curTargetHealth) + ".0", (float)(mouseX + 60), (float)(bottom - 19), -1);

      for(Object nextObject : GuiPlayerTabOverlay.field_175252_a.sortedCopy(mc.thePlayer.sendQueue.getPlayerInfoMap())) {
         NetworkPlayerInfo networkPlayerInfo = (NetworkPlayerInfo)nextObject;
         if (mc.theWorld.getPlayerEntityByUUID(networkPlayerInfo.getGameProfile().getId()) == target1) {
            GlStateManager.enableCull();
            mc.getTextureManager().bindTexture(networkPlayerInfo.getLocationSkin());
            Gui.drawScaledCustomSizeModalRect(left, top + 15, 8.0F, 8.0F, 8, 8, 43, 43, 64.0F, 66.0F);
         }
      }
   }

   public void drawTargetHUDHanabi(EntityLivingBase target1) {
      ScaledResolution s = new ScaledResolution(mc);
      new Color(0, 0, 0, 120);
      new Color(59, 59, 59, 160);
      Color healthBarColor = Color.green;
      new Color(20, 81, 208);
      int left = s.getScaledWidth() / 2 + 5 + 62;
      int right2 = 142;
      int right = s.getScaledWidth() / 2 + right2;
      int right3 = 159;
      int top = s.getScaledHeight() / 2 - 25 + 70;
      int bottom = s.getScaledHeight() / 2 + 25 + 70;
      float curTargetHealth = target1.getHealth();
      float maxTargetHealth = target1.getMaxHealth();
      float healthProcent = target1.getHealth() / target1.getMaxHealth();
      if (this.lastHealthPercentage != healthProcent) {
         this.lostHealthPercentage += this.lastHealthPercentage - healthProcent;
      }

      this.lastHealthPercentage = healthProcent;
      float finalHealthPercentage = healthProcent + this.lostHealthPercentage;
      this.lostHealthPercentage = Math.max(0.0F, this.lostHealthPercentage - this.lostHealthPercentage / 20.0F);
      int rectRight = right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) / 2;
      float healthPos = finalHealthPercentage * (float)right3;
      RenderUtil.drawRoundedRect2Alpha((double)(left + 2), (double)(top + 18), 159.0, 40.0, 0.0, new Color(0, 0, 0, 200));
      if (this.lastHealthPercentage != healthProcent) {
         this.lostHealthPercentage += this.lastHealthPercentage - healthProcent;
      }

      this.lastHealthPercentage = healthProcent;
      this.lostHealthPercentage = Math.max(0.0F, this.lostHealthPercentage - this.lostHealthPercentage / 20.0F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      Aqua.INSTANCE.comfortaa3.drawString("" + Math.round(curTargetHealth) + ".0", (float)(left + 140), (float)(bottom - 4), -1);
      mc.fontRendererObj.drawString("❤", left + 130, bottom - 3, -1);
      RenderUtil.drawRoundedRectGradient((double)(left + 2), (double)(bottom + 8), (double)healthPos, 5.0, 0.0, new Color(0, 216, 245), new Color(0, 55, 245));
      Aqua.INSTANCE.comfortaa3.drawString(target1.getName(), (float)(left + 44), (float)(top + 23), -1);
      Aqua.INSTANCE
         .comfortaa5
         .drawString(
            "XYZ:" + Math.round(mc.thePlayer.posX) + " " + Math.round(mc.thePlayer.posY) + " " + Math.round(mc.thePlayer.posZ) + " | Hurt: true",
            (float)(left + 44),
            (float)(top + 37),
            -1
         );

      for(Object nextObject : GuiPlayerTabOverlay.field_175252_a.sortedCopy(mc.thePlayer.sendQueue.getPlayerInfoMap())) {
         NetworkPlayerInfo networkPlayerInfo = (NetworkPlayerInfo)nextObject;
         if (mc.theWorld.getPlayerEntityByUUID(networkPlayerInfo.getGameProfile().getId()) == target1) {
            GlStateManager.enableCull();
            mc.getTextureManager().bindTexture(networkPlayerInfo.getLocationSkin());
            Gui.drawScaledCustomSizeModalRect(s.getScaledWidth() / 2 + 10 + 62, s.getScaledHeight() / 2 - 5 + 70, 8.0F, 8.0F, 8, 8, 36, 36, 64.0F, 66.0F);
         }
      }
   }

   public void drawTargetHUDShaders(EntityLivingBase target1, int mouseX, int mouseY) {
      if (Mouse.isButtonDown(0) && mc.currentScreen instanceof GuiChat) {
         this.mouseX = mouseX;
         this.mouseY = mouseY;
      } else {
         mouseX = this.mouseX;
         mouseY = this.mouseY;
      }

      ScaledResolution s = new ScaledResolution(mc);
      float cornerRadius = (float)Aqua.setmgr.getSetting("TargetHUDCornerRadius").getCurrentNumber();
      new Color(0, 0, 0, 120);
      new Color(59, 59, 59, 160);
      Color healthBarColor = Color.green;
      new Color(20, 81, 208);
      int left = mouseX;
      int right2 = 142;
      int right = s.getScaledWidth() / 2 + right2;
      int right3 = 16 + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) + 29;
      int top = mouseY;
      int bottom = mouseY + 50;
      float curTargetHealth = target1.getHealth();
      float healthProcent = target1.getHealth() / target1.getMaxHealth();
      if (this.lastHealthPercentage != healthProcent) {
         this.lostHealthPercentage += this.lastHealthPercentage - healthProcent;
      }

      this.lastHealthPercentage = healthProcent;
      float finalHealthPercentage = healthProcent + this.lostHealthPercentage;
      this.lostHealthPercentage = Math.max(0.0F, this.lostHealthPercentage - this.lostHealthPercentage / 20.0F);
      int rectRight = right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName()) / 2 - 5;
      float healthPos = finalHealthPercentage * (float)right3;
      int[] color = Aqua.setmgr.getSetting("TargetHUDClientColor").isState() ? ESP.getRGB(this.getColor()) : ESP.getRGB(this.getColor2());
      if (Aqua.setmgr.getSetting("TargetHUDMode").getCurrentMode().equalsIgnoreCase("Glow")) {
         Arraylist.drawGlowArray(
            () -> RenderUtil.drawRoundedRect(
                  (double)(left - 7),
                  (double)(top + 10),
                  (double)((float)(right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName())) - (float)s.getScaledWidth() / 2.0F - 22.0F),
                  53.0,
                  (double)cornerRadius,
                  new Color(color[0], color[1], color[2]).getRGB()
               ),
            false
         );
      }

      if (Aqua.setmgr.getSetting("TargetHUDMode").getCurrentMode().equalsIgnoreCase("Shadow")) {
         Shadow.drawGlow(
            () -> RenderUtil.drawRoundedRect(
                  (double)(left - 7),
                  (double)(top + 10),
                  (double)((float)(right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName())) - (float)s.getScaledWidth() / 2.0F - 22.0F),
                  53.0,
                  (double)cornerRadius,
                  new Color(20, 20, 20, 255).getRGB()
               ),
            false
         );
      }

      Blur.drawBlurred(
         () -> RenderUtil.drawRoundedRect(
               (double)(left - 7),
               (double)(top + 10),
               (double)((float)(right + Aqua.INSTANCE.comfortaa3.getStringWidth(target1.getName())) - (float)s.getScaledWidth() / 2.0F - 22.0F),
               53.0,
               (double)cornerRadius,
               new Color(20, 20, 20, 150).getRGB()
            ),
         false
      );
      if (this.lastHealthPercentage != healthProcent) {
         this.lostHealthPercentage += this.lastHealthPercentage - healthProcent;
      }

      this.lastHealthPercentage = healthProcent;
      this.lostHealthPercentage = Math.max(0.0F, this.lostHealthPercentage - this.lostHealthPercentage / 20.0F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      Arraylist.drawGlowArray(
         () -> RenderUtil.drawRoundedRect(
               (double)(left + 50), (double)bottom, (double)(healthPos + 10.0F), 12.0, 2.0, new Color(color[0], color[1], color[2]).getRGB()
            ),
         false
      );

      for(Object nextObject : GuiPlayerTabOverlay.field_175252_a.sortedCopy(mc.thePlayer.sendQueue.getPlayerInfoMap())) {
         NetworkPlayerInfo networkPlayerInfo = (NetworkPlayerInfo)nextObject;
         if (mc.theWorld.getPlayerEntityByUUID(networkPlayerInfo.getGameProfile().getId()) == target1) {
            GlStateManager.enableCull();
            mc.getTextureManager().bindTexture(networkPlayerInfo.getLocationSkin());
         }
      }
   }

   public int getColor2() {
      try {
         return Aqua.setmgr.getSetting("TargetHUDColor").getColor();
      } catch (Exception var2) {
         return Color.white.getRGB();
      }
   }

   public int getColor() {
      try {
         return Aqua.setmgr.getSetting("HUDColor").getColor();
      } catch (Exception var2) {
         return Color.white.getRGB();
      }
   }

   public static float getAngle(Entity entity) {
      double x = RenderUtil.interpolate(entity.posX, entity.lastTickPosX, 1.0) - RenderUtil.interpolate(mc.thePlayer.posX, mc.thePlayer.lastTickPosX, 1.0);
      double z = RenderUtil.interpolate(entity.posZ, entity.lastTickPosZ, 1.0) - RenderUtil.interpolate(mc.thePlayer.posZ, mc.thePlayer.lastTickPosZ, 1.0);
      float yaw = (float)(-Math.toDegrees(Math.atan2(x, z)));
      return (float)((double)yaw - RenderUtil.interpolate((double)mc.thePlayer.rotationYaw, (double)mc.thePlayer.prevRotationYaw, 1.0));
   }

   public static void renderPlayerModelTexture(
      double x, double y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight, AbstractClientPlayer target
   ) {
      ResourceLocation skin = target.getLocationSkin();
      Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
      GL11.glEnable(3042);
      Gui.drawScaledCustomSizeModalRect((int)x, (int)y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
      GL11.glDisable(3042);
   }
}
