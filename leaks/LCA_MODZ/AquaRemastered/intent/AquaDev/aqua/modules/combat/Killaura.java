package intent.AquaDev.aqua.modules.combat;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventFakePreMotion;
import events.listeners.EventKillaura;
import events.listeners.EventPacket;
import events.listeners.EventPostRender2D;
import events.listeners.EventPreMotion;
import events.listeners.EventRender3D;
import events.listeners.EventSilentMove;
import events.listeners.EventUpdate;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.modules.visual.ShaderMultiplier;
import intent.AquaDev.aqua.utils.ColorUtils;
import intent.AquaDev.aqua.utils.FriendSystem;
import intent.AquaDev.aqua.utils.RenderUtil;
import intent.AquaDev.aqua.utils.RotationUtil;
import intent.AquaDev.aqua.utils.TimeUtil;
import intent.AquaDev.aqua.utils.Translate;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class Killaura extends Module {
   public static EntityPlayer target = null;
   public static ArrayList<Entity> bots = new ArrayList<>();
   private final Translate translate = new Translate(0.0F, 0.0F);
   private final double scale = 1.0;
   TimeUtil timeUtil = new TimeUtil();
   TimeUtil timer = new TimeUtil();
   public double yaw;
   float lostHealthPercentage = 0.0F;
   float lastHealthPercentage = 0.0F;
   private EntityLivingBase lastTarget;
   private float displayHealth;
   private float health;

   public Killaura() {
      super("Killaura", Module.Type.Combat, "Killaura", 0, Category.Combat);
   }

   @Override
   public void setup() {
      Aqua.setmgr.register(new Setting("Range", this, 6.0, 3.0, 6.0, false));
      Aqua.setmgr.register(new Setting("minCPS", this, 17.0, 1.0, 20.0, false));
      Aqua.setmgr.register(new Setting("maxCPS", this, 19.0, 1.0, 20.0, false));
      Aqua.setmgr.register(new Setting("RotationSpeed", this, 180.0, 1.0, 180.0, false));
      Aqua.setmgr.register(new Setting("InstantRotation", this, true));
      Aqua.setmgr.register(new Setting("Autoblock", this, true));
      Aqua.setmgr.register(new Setting("JumpFix", this, false));
      Aqua.setmgr.register(new Setting("TargetESP", this, true));
      Aqua.setmgr.register(new Setting("MouseSensiFix", this, false));
      Aqua.setmgr.register(new Setting("MouseDelayFix", this, false));
      Aqua.setmgr.register(new Setting("RangeBlock", this, false));
      Aqua.setmgr.register(new Setting("CPSFix", this, false));
      Aqua.setmgr.register(new Setting("Wtap", this, false));
      Aqua.setmgr.register(new Setting("KeepSprint", this, true));
      Aqua.setmgr.register(new Setting("AntiBots", this, true));
      Aqua.setmgr.register(new Setting("RenderPitch", this, false));
      Aqua.setmgr.register(new Setting("ClampPitch", this, false));
      Aqua.setmgr.register(new Setting("Rotations", this, false));
      Aqua.setmgr.register(new Setting("1.9", this, false));
      Aqua.setmgr.register(new Setting("Cubecraft", this, false));
      Aqua.setmgr.register(new Setting("CorrectMovement", this, false));
      Aqua.setmgr.register(new Setting("SilentMoveFix", this, false));
      Aqua.setmgr.register(new Setting("Random", this, false));
      Aqua.setmgr.register(new Setting("LegitAttack", this, false));
      Aqua.setmgr.register(new Setting("ThroughWalls", this, false));
      Aqua.setmgr.register(new Setting("Mode", this, "Watchdog", new String[]{"Watchdog", "Matrix", "Packet"}));
   }

   @Override
   public void onEnable() {
      if (mc.thePlayer != null) {
         mc.thePlayer.sprintReset = false;
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      target = null;
      bots.clear();
      super.onDisable();
   }

   @Override
   public void onEvent(Event event) {
      if (event instanceof EventPacket) {
         if (Aqua.setmgr.getSetting("KillauraWtap").isState() && target != null && target.hurtTime == 9) {
            mc.thePlayer.sprintReset = true;
         }

         Packet packet = EventPacket.getPacket();
         if (packet instanceof S02PacketChat) {
            S02PacketChat s02PacketChat = (S02PacketChat)packet;
            String cp21 = s02PacketChat.getChatComponent().getUnformattedText();
            if (cp21.contains("Cages opened! FIGHT!")) {
               bots.clear();
               Antibot.bots.clear();
            }
         }
      }

      if (event instanceof EventPostRender2D
         && Aqua.setmgr.getSetting("KillauraCPSFix").isState()
         && !mc.getCurrentServerData().serverIP.equalsIgnoreCase("hypixel.net")) {
         float minCPS = (float)Aqua.setmgr.getSetting("KillauraminCPS").getCurrentNumber();
         float maxCPS = (float)Aqua.setmgr.getSetting("KillauramaxCPS").getCurrentNumber();
         float CPS = (float)MathHelper.getRandomDoubleInRange(new Random(), (double)minCPS, (double)maxCPS);
         target = this.searchTargets();
         if (target != null) {
            this.lastTarget = target;
         }

         if ((!Aqua.setmgr.getSetting("Killaura1.9").isState() || Aqua.setmgr.getSetting("KillauraLegitAttack").isState())
            && this.timeUtil.hasReached((long)(1000.0F / CPS))) {
            if (target != null) {
               if (Aqua.setmgr.getSetting("KillauraLegitAttack").isState()) {
                  mc.clickMouse();
               } else {
                  mc.thePlayer.swingItem();
                  mc.playerController.attackEntity(mc.thePlayer, target);
               }
            }

            this.timeUtil.reset();
         }

         if (Aqua.setmgr.getSetting("KillauraCubecraft").isState()) {
            if (Aqua.setmgr.getSetting("Killaura1.9").isState()
               && mc.thePlayer.getHeldItem() != null
               && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
               if (mc.thePlayer.ticksExisted % 12 == 0) {
                  if (target != null) {
                     mc.playerController.attackEntity(mc.thePlayer, target);
                     mc.thePlayer.swingItem();
                  }

                  this.timeUtil.reset();
               }
            } else if (Aqua.setmgr.getSetting("Killaura1.9").isState()
               && mc.thePlayer.getHeldItem() != null
               && mc.thePlayer.getHeldItem().getItem() instanceof ItemAxe) {
               if (mc.thePlayer.ticksExisted % 22 == 0) {
                  if (target != null) {
                     mc.playerController.attackEntity(mc.thePlayer, target);
                     mc.thePlayer.swingItem();
                  }

                  this.timeUtil.reset();
               }
            } else if (mc.thePlayer.ticksExisted % 12 == 0) {
               if (target != null) {
                  mc.playerController.attackEntity(mc.thePlayer, target);
                  mc.thePlayer.swingItem();
               }

               this.timeUtil.reset();
            }
         }
      }

      if (event instanceof EventSilentMove
         && Aqua.setmgr.getSetting("KillauraSilentMoveFix").isState()
         && target != null
         && Aqua.setmgr.getSetting("KillauraCorrectMovement").isState()) {
         ((EventSilentMove)event).setSilent(true);
      }

      if (event instanceof EventRender3D && target != null && Aqua.setmgr.getSetting("KillauraTargetESP").isState()) {
         if (Aqua.moduleManager.getModuleByName("ShaderMultiplier").isToggled()) {
            ShaderMultiplier.drawGlowESP(this::drawTargetESP, false);
         }

         this.drawTargetESP();
      }

      if (event instanceof EventKillaura) {
         if (target != null) {
            mc.gameSettings.keyBindAttack.pressed = false;
         }

         if (Aqua.setmgr.getSetting("KillauraAntiBots").isState()) {
            if (mc.getCurrentServerData().serverIP.equalsIgnoreCase("jartexnetwork.com")
               || mc.getCurrentServerData().serverIP.equalsIgnoreCase("pika.host")
               || mc.getCurrentServerData().serverIP.equalsIgnoreCase("mc.hydracraft.es")
               || mc.getCurrentServerData().serverIP.equalsIgnoreCase("gamster.org")
               || mc.getCurrentServerData().serverIP.equalsIgnoreCase("play.pika.network.com")
               || mc.getCurrentServerData().serverIP.equalsIgnoreCase("jartex.fun")) {
               if (mc.thePlayer.ticksExisted > 110) {
                  for(Entity entity : mc.theWorld.loadedEntityList) {
                     if (entity instanceof EntityPlayer && entity != mc.thePlayer && entity.getCustomNameTag() == "" && !bots.contains((EntityPlayer)entity)) {
                        bots.add(entity);
                     }
                  }
               } else {
                  bots = new ArrayList<>();
               }
            }

            for(Entity entity : mc.theWorld.getLoadedEntityList()) {
               if (entity instanceof EntityPlayer && (this.isBot((EntityPlayer)entity) || entity.isInvisible()) && entity != mc.thePlayer) {
                  bots.add(entity);
                  if (mc.getCurrentServerData().serverIP.equalsIgnoreCase("hypixel.net")) {
                     for(NetworkPlayerInfo playerInfo : mc.thePlayer.sendQueue.getPlayerInfoMap()) {
                        if (playerInfo.getGameProfile().getId().equals(entity.getUniqueID()) && playerInfo.getResponseTime() != 1) {
                           bots.add(entity);
                        }
                     }

                     mc.theWorld.removeEntity(entity);
                  }
               }
            }
         }

         if (Aqua.setmgr.getSetting("KillauraAutoblock").isState()
            && !Aqua.setmgr.getSetting("KillauraMode").getCurrentMode().equalsIgnoreCase("Packet")
            && mc.thePlayer.isSwingInProgress
            && mc.thePlayer.getHeldItem() != null
            && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword
            && target != null) {
            mc.gameSettings.keyBindUseItem.pressed = true;
         }
      }

      if (event instanceof EventFakePreMotion) {
         float[] rota = RotationUtil.Intavee(mc.thePlayer, target);
         if (!Aqua.setmgr.getSetting("KillauraRotations").isState()) {
            ((EventFakePreMotion)event).setPitch(RotationUtil.pitch);
            ((EventFakePreMotion)event).setYaw(RotationUtil.yaw);
            RotationUtil.setYaw(rota[0], 180.0F);
            RotationUtil.setPitch(rota[1], 8.0F);
         }
      }

      if (event instanceof EventPreMotion) {
         if (Aqua.setmgr.getSetting("KillauraAutoblock").isState()
            && Aqua.setmgr.getSetting("KillauraMode").getCurrentMode().equalsIgnoreCase("Packet")
            && target != null
            && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
         }

         if (!Aqua.moduleManager.getModuleByName("Scaffold").isToggled()) {
            if (Aqua.setmgr.getSetting("KillauraAutoblock").isState()) {
               if (Aqua.setmgr.getSetting("KillauraMode").getCurrentMode().equalsIgnoreCase("Watchdog")
                  && mc.thePlayer.getHeldItem() != null
                  && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword
                  && mc.thePlayer.ticksExisted % 2 == 0) {
                  mc.gameSettings.keyBindUseItem.pressed = false;
               }

               if (Aqua.setmgr.getSetting("KillauraMode").getCurrentMode().equalsIgnoreCase("Matrix")
                  && mc.thePlayer.getHeldItem() != null
                  && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword
                  && target == null) {
                  mc.gameSettings.keyBindUseItem.pressed = false;
               }
            }

            float[] rota = RotationUtil.Intavee(mc.thePlayer, target);
            if (Aqua.setmgr.getSetting("KillauraRotations").isState()) {
               if (Aqua.setmgr.getSetting("KillauraInstantRotation").isState()) {
                  ((EventPreMotion)event).setPitch(RotationUtil.pitch);
                  ((EventPreMotion)event).setYaw(RotationUtil.yaw);
                  RotationUtil.setYaw(rota[0], 180.0F);
                  RotationUtil.setPitch(rota[1], 180.0F);
               } else {
                  ((EventPreMotion)event).setPitch(RotationUtil.pitch);
                  ((EventPreMotion)event).setYaw(RotationUtil.yaw);
                  RotationUtil.setYaw(rota[0], (float)Aqua.setmgr.getSetting("KillauraRotationSpeed").getCurrentNumber());
                  RotationUtil.setPitch(rota[1], (float)Aqua.setmgr.getSetting("KillauraRotationSpeed").getCurrentNumber());
               }
            }
         }
      }

      if (event instanceof EventUpdate) {
         if (Aqua.setmgr.getSetting("KillauraRangeBlock").isState()) {
            if (target != null) {
               if (this.timer.hasReached(700L)) {
                  mc.gameSettings.keyBindUseItem.pressed = true;
                  this.timer.reset();
               } else {
                  mc.gameSettings.keyBindUseItem.pressed = false;
               }
            } else {
               mc.gameSettings.keyBindUseItem.pressed = false;
            }
         }

         if (!Aqua.setmgr.getSetting("KillauraCPSFix").isState()) {
            float minCPS = (float)Aqua.setmgr.getSetting("KillauraminCPS").getCurrentNumber();
            float maxCPS = (float)Aqua.setmgr.getSetting("KillauramaxCPS").getCurrentNumber();
            float CPS = (float)MathHelper.getRandomDoubleInRange(new Random(), (double)minCPS, (double)maxCPS);
            target = this.searchTargets();
            if (target != null) {
               this.lastTarget = target;
            }

            if ((!Aqua.setmgr.getSetting("Killaura1.9").isState() || Aqua.setmgr.getSetting("KillauraLegitAttack").isState())
               && this.timeUtil.hasReached((long)(1000.0F / CPS))) {
               if (target != null) {
                  if (Aqua.setmgr.getSetting("KillauraLegitAttack").isState()) {
                     mc.clickMouse();
                  } else {
                     mc.thePlayer.swingItem();
                     mc.playerController.attackEntity(mc.thePlayer, target);
                  }
               }

               this.timeUtil.reset();
            }
         }

         if (Aqua.setmgr.getSetting("KillauraCubecraft").isState()) {
            if (Aqua.setmgr.getSetting("Killaura1.9").isState()
               && mc.thePlayer.getHeldItem() != null
               && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
               if (mc.thePlayer.ticksExisted % 12 == 0) {
                  if (target != null) {
                     mc.playerController.attackEntity(mc.thePlayer, target);
                     mc.thePlayer.swingItem();
                  }

                  this.timeUtil.reset();
               }
            } else if (Aqua.setmgr.getSetting("Killaura1.9").isState()
               && mc.thePlayer.getHeldItem() != null
               && mc.thePlayer.getHeldItem().getItem() instanceof ItemAxe) {
               if (mc.thePlayer.ticksExisted % 22 == 0) {
                  if (target != null) {
                     mc.playerController.attackEntity(mc.thePlayer, target);
                     mc.thePlayer.swingItem();
                  }

                  this.timeUtil.reset();
               }
            } else if (mc.thePlayer.ticksExisted % 12 == 0) {
               if (target != null) {
                  mc.playerController.attackEntity(mc.thePlayer, target);
                  mc.thePlayer.swingItem();
               }

               this.timeUtil.reset();
            }
         }
      }
   }

   public EntityPlayer searchTargets() {
      float range = (float)Aqua.setmgr.getSetting("KillauraRange").getCurrentNumber();
      EntityPlayer player = null;
      double closestDist = 100000.0;

      for(Entity o : mc.theWorld.loadedEntityList) {
         if (!o.getName().equals(mc.thePlayer.getName())
            && o instanceof EntityPlayer
            && !FriendSystem.isFriend(o.getName())
            && !Antibot.bots.contains(o)
            && !bots.contains(o)
            && !mc.session.getUsername().equalsIgnoreCase("Administradora")
            && mc.thePlayer.getDistanceToEntity(o) < range) {
            double dist = (double)mc.thePlayer.getDistanceToEntity(o);
            if (dist < closestDist) {
               closestDist = dist;
               player = (EntityPlayer)o;
            }
         }
      }

      return player;
   }

   boolean isBot(EntityPlayer player) {
      return !this.isInTablist(player) ? true : this.invalidName(player);
   }

   boolean isInTablist(EntityPlayer player) {
      if (Minecraft.getMinecraft().isSingleplayer()) {
         return false;
      } else {
         for(NetworkPlayerInfo playerInfo : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
            if (playerInfo.getGameProfile().getName().equalsIgnoreCase(player.getName())) {
               return true;
            }
         }

         return false;
      }
   }

   boolean invalidName(Entity e) {
      if (e.getName().contains("-")) {
         return true;
      } else if (e.getName().contains("/")) {
         return true;
      } else if (e.getName().contains("|")) {
         return true;
      } else if (e.getName().contains("<")) {
         return true;
      } else {
         return e.getName().contains(">") ? true : e.getName().contains("ยง");
      }
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

   public void drawTargetESP() {
      double x = target.lastTickPosX + (target.posX - target.lastTickPosX) * (double)mc.timer.renderPartialTicks - mc.getRenderManager().getRenderPosX();
      double y = target.lastTickPosY + (target.posY - target.lastTickPosY) * (double)mc.timer.renderPartialTicks - mc.getRenderManager().getRenderPosY();
      double z = target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * (double)mc.timer.renderPartialTicks - mc.getRenderManager().getRenderPosZ();
      int rgb = new Color(Aqua.setmgr.getSetting("HUDColor").getColor()).getRGB();
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GL11.glBlendFunc(770, 771);
      GL11.glEnable(2848);
      GlStateManager.disableDepth();
      GlStateManager.disableTexture2D();
      GlStateManager.enableAlpha();
      GL11.glLineWidth(3.0F);
      GL11.glShadeModel(7425);
      GL11.glDisable(2884);
      double size = (double)target.width * 1.2;
      float factor = (float)Math.sin((double)((float)System.nanoTime() / 3.0E8F));
      GL11.glTranslatef(0.0F, factor, 0.0F);
      GL11.glBegin(5);

      for(int j = 0; j < 361; ++j) {
         RenderUtil.color(ColorUtils.getColorAlpha(rgb, 160));
         double x1 = x + Math.cos(Math.toRadians((double)j)) * size;
         double z1 = z - Math.sin(Math.toRadians((double)j)) * size;
         GL11.glVertex3d(x1, y + 1.0, z1);
         RenderUtil.color(ColorUtils.getColorAlpha(rgb, 0));
         GL11.glVertex3d(x1, y + 1.0 + (double)(factor * 0.4F), z1);
      }

      GL11.glEnd();
      GL11.glBegin(2);

      for(int j = 0; j < 361; ++j) {
         RenderUtil.color(ColorUtils.getColorAlpha(rgb, 50));
         GL11.glVertex3d(x + Math.cos(Math.toRadians((double)j)) * size, y + 1.0, z - Math.sin(Math.toRadians((double)j)) * size);
      }

      GL11.glEnd();
      GlStateManager.enableAlpha();
      GL11.glShadeModel(7424);
      GL11.glDisable(2848);
      GL11.glEnable(2884);
      GlStateManager.enableBlend();
      GlStateManager.enableTexture2D();
      GlStateManager.enableDepth();
      GlStateManager.disableBlend();
      GlStateManager.resetColor();
      GlStateManager.popMatrix();
   }
}
