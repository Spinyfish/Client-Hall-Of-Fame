package intent.AquaDev.aqua.modules.movement;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventPacket;
import events.listeners.EventPlayerMove;
import events.listeners.EventTick;
import events.listeners.EventTimerDisabler;
import events.listeners.EventUpdate;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.modules.misc.Disabler;
import intent.AquaDev.aqua.utils.PlayerUtil;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class Fly extends Module {
   public boolean verusDmg;

   public Fly() {
      super("Fly", Module.Type.Movement, "Fly", 0, Category.Movement);
      Aqua.setmgr.register(new Setting("MotionReset", this, true));
      Aqua.setmgr.register(new Setting("FakeDMG", this, false));
      Aqua.setmgr.register(new Setting("Boost", this, 3.0, 0.3, 9.0, false));
      Aqua.setmgr.register(new Setting("SentinelSpeed", this, 3.0, 0.3, 9.0, false));
      Aqua.setmgr
         .register(
            new Setting(
               "Mode",
               this,
               "Motion",
               new String[]{
                  "Motion", "Hypixel", "Minemora", "Verus", "Verus2", "Verus3", "Cubecraft", "CubecraftNew", "CubecraftSmooth", "Creative", "Watchdog", "Test"
               }
            )
         );
   }

   @Override
   public void onEnable() {
      if (mc.thePlayer != null && Aqua.setmgr.getSetting("FlyFakeDMG").isState()) {
         mc.thePlayer.performHurtAnimation();
         mc.thePlayer.playSound("game.player.hurt", 1.0F, 1.0F);
      }

      if (Aqua.setmgr.getSetting("FlyMode").getCurrentMode().equalsIgnoreCase("Test")) {
      }

      if (Aqua.setmgr.getSetting("FlyMode").getCurrentMode().equalsIgnoreCase("Verus3")) {
         sendPacketUnlogged(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, new ItemStack(Items.water_bucket), 0.0F, 0.5F, 0.0F));
         sendPacketUnlogged(
            new C08PacketPlayerBlockPlacement(
               new BlockPos(getX(), getY() - 1.5, getZ()), 1, new ItemStack(Blocks.stone.getItem(mc.theWorld, new BlockPos(-1, -1, -1))), 0.0F, 0.94F, 0.0F
            )
         );
      }

      if (Aqua.setmgr.getSetting("FlyMode").getCurrentMode().equalsIgnoreCase("Verus")) {
         sendPacketUnlogged(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, new ItemStack(Items.water_bucket), 0.0F, 0.5F, 0.0F));
         sendPacketUnlogged(
            new C08PacketPlayerBlockPlacement(
               new BlockPos(getX(), getY() - 1.5, getZ()), 1, new ItemStack(Blocks.stone.getItem(mc.theWorld, new BlockPos(-1, -1, -1))), 0.0F, 0.94F, 0.0F
            )
         );
         mc.getNetHandler()
            .addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3.001, mc.thePlayer.posZ, false));
         mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
         mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
      }

      if (Aqua.setmgr.getSetting("FlyMode").getCurrentMode().equalsIgnoreCase("Verus2")) {
         sendPacketUnlogged(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, new ItemStack(Items.water_bucket), 0.0F, 0.5F, 0.0F));
         sendPacketUnlogged(
            new C08PacketPlayerBlockPlacement(
               new BlockPos(getX(), getY() - 1.5, getZ()), 1, new ItemStack(Blocks.stone.getItem(mc.theWorld, new BlockPos(-1, -1, -1))), 0.0F, 0.94F, 0.0F
            )
         );
         mc.getNetHandler()
            .addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3.001, mc.thePlayer.posZ, false));
         mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
         mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
      }

      super.onEnable();
   }

   @Override
   public void onDisable() {
      Disabler.sendPacketUnlogged(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
      this.verusDmg = false;
      mc.gameSettings.keyBindSneak.pressed = false;
      mc.thePlayer.capabilities.isFlying = false;
      mc.thePlayer.capabilities.isCreativeMode = false;
      mc.timer.timerSpeed = 1.0F;
      if (Aqua.setmgr.getSetting("FlyMotionReset").isState()) {
         mc.thePlayer.motionZ = 0.0;
         mc.thePlayer.motionX = 0.0;
      }

      super.onDisable();
   }

   @Override
   public void onEvent(Event event) {
      if (event instanceof EventPlayerMove) {
         if (Aqua.setmgr.getSetting("FlyMode").getCurrentMode().equalsIgnoreCase("CubecraftSmooth")) {
            if (mc.gameSettings.keyBindJump.pressed) {
               ((EventPlayerMove)event).setY(1.0);
            }

            if (mc.gameSettings.keyBindSneak.pressed) {
               ((EventPlayerMove)event).setY(-1.0);
            }

            if (mc.thePlayer.ticksExisted % 1 == 0) {
               mc.gameSettings.keyBindSprint.pressed = true;
               double x = mc.thePlayer.posX;
               double y = mc.thePlayer.posY;
               double z = mc.thePlayer.posZ;
               if (mc.thePlayer.isMoving()) {
                  double yaw1 = Math.toRadians((double)mc.thePlayer.rotationYaw);
                  double speed1 = (double)((float)Aqua.setmgr.getSetting("FlySentinelSpeed").getCurrentNumber());
                  if (mc.thePlayer.ticksExisted % 1 == 0 && !mc.gameSettings.keyBindJump.pressed) {
                     ((EventPlayerMove)event).setY(mc.thePlayer.motionY = 0.0);
                  }
               } else {
                  ((EventPlayerMove)event).setX(0.0);
                  ((EventPlayerMove)event).setZ(0.0);
                  if (!mc.gameSettings.keyBindJump.pressed) {
                     ((EventPlayerMove)event).setY(mc.thePlayer.motionY = 0.0);
                  }
               }
            } else {
               ((EventPlayerMove)event).setX(0.0);
               ((EventPlayerMove)event).setZ(0.0);
            }
         }

         if (Aqua.setmgr.getSetting("FlyMode").getCurrentMode().equalsIgnoreCase("CubecraftNew")) {
            if (mc.thePlayer.ticksExisted % 3 == 0) {
               mc.gameSettings.keyBindSprint.pressed = true;
               double x = mc.thePlayer.posX;
               double y = mc.thePlayer.posY;
               double z = mc.thePlayer.posZ;
               if (mc.gameSettings.keyBindJump.pressed) {
                  ((EventPlayerMove)event).setY(2.0);
               }

               if (mc.gameSettings.keyBindSneak.pressed) {
                  ((EventPlayerMove)event).setY(-2.0);
               }

               if (mc.thePlayer.isMoving()) {
                  double yaw1 = Math.toRadians((double)mc.thePlayer.rotationYaw);
                  double speed1 = (double)((float)Aqua.setmgr.getSetting("FlySentinelSpeed").getCurrentNumber());
                  if (mc.thePlayer.ticksExisted % 2 == 0) {
                     ((EventPlayerMove)event).setY(mc.thePlayer.motionY = 0.2);
                  }
               } else {
                  ((EventPlayerMove)event).setX(0.0);
                  ((EventPlayerMove)event).setZ(0.0);
                  if (!mc.gameSettings.keyBindJump.pressed) {
                     ((EventPlayerMove)event).setY(mc.thePlayer.motionY = 0.08);
                  }
               }
            } else {
               ((EventPlayerMove)event).setX(0.0);
               ((EventPlayerMove)event).setZ(0.0);
            }
         }
      }

      if (event instanceof EventTimerDisabler
         && Aqua.setmgr.getSetting("FlyMode").getCurrentMode().equalsIgnoreCase("Hypixel")
         && mc.thePlayer.ticksExisted % 2 == 0) {
      }

      if (event instanceof EventPacket
         && Aqua.setmgr.getSetting("FlyMode").getCurrentMode().equalsIgnoreCase("Zonecraft")
         && mc.thePlayer.ticksExisted % 2 == 0) {
      }

      if (event instanceof EventTick && Aqua.setmgr.getSetting("FlyMode").getCurrentMode().equalsIgnoreCase("Test")) {
      }

      if (event instanceof EventUpdate) {
         mc.thePlayer.cameraYaw = 0.035F;
         if (Aqua.setmgr.getSetting("FlyMode").getCurrentMode().equalsIgnoreCase("Test")) {
            Disabler.sendPacketUnlogged(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
            mc.thePlayer.onGround = true;
            if (mc.thePlayer.isMoving()) {
               PlayerUtil.setSpeed(0.05);
            }

            mc.thePlayer.motionY = 0.0;
         }

         if (Aqua.setmgr.getSetting("FlyMode").getCurrentMode().equalsIgnoreCase("Hypixel")) {
            mc.thePlayer.motionY = 0.0;
            if (mc.thePlayer.ticksExisted % 22 == 0) {
               mc.gameSettings.keyBindForward.pressed = true;
               float yawRandom = (float)MathHelper.getRandomDoubleInRange(new Random(), 1.0, 180.0);
               double yaw = Math.toRadians((double)mc.thePlayer.rotationYaw);
               double x3 = -Math.sin(yaw) * 7.0;
               double z3 = Math.cos(yaw) * 7.0;
               double y3 = 1.8;
               mc.getNetHandler()
                  .getNetworkManager()
                  .sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + x3, mc.thePlayer.posY - y3, mc.thePlayer.posZ + z3, false));
            } else {
               mc.timer.timerSpeed = 1.0F;
               mc.gameSettings.keyBindForward.pressed = false;
            }
         }

         if (Aqua.setmgr.getSetting("FlyMode").getCurrentMode().equalsIgnoreCase("Watchdog") && mc.thePlayer.fallDistance > 3.0F) {
            mc.thePlayer.motionY = 0.0;
         }

         if (Aqua.setmgr.getSetting("FlyMode").getCurrentMode().equalsIgnoreCase("Creative")) {
            mc.thePlayer.capabilities.isFlying = true;
            mc.thePlayer.capabilities.isCreativeMode = true;
         }

         if (Aqua.setmgr.getSetting("FlyMode").getCurrentMode().equalsIgnoreCase("Motion")) {
            if (mc.gameSettings.keyBindJump.pressed) {
               mc.thePlayer.motionY = 1.0;
            } else if (mc.gameSettings.keyBindSneak.pressed) {
               mc.thePlayer.motionY = -1.0;
            } else {
               mc.thePlayer.motionY = 0.0;
            }

            if (mc.thePlayer.isMoving()) {
               PlayerUtil.setSpeed(4.0);
            } else {
               mc.thePlayer.motionX = 0.0;
               mc.thePlayer.motionZ = 0.0;
            }
         }

         if (Aqua.setmgr.getSetting("FlyMode").getCurrentMode().equalsIgnoreCase("Verus3")) {
            PlayerUtil.setSpeed(PlayerUtil.getSpeed());
            mc.thePlayer.onGround = true;
            if (mc.gameSettings.keyBindJump.pressed) {
               mc.thePlayer.motionY = 1.0;
               mc.timer.timerSpeed = 1.0F;
            }

            sendPacketUnlogged(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, new ItemStack(Items.water_bucket), 0.0F, 0.5F, 0.0F));
            sendPacketUnlogged(
               new C08PacketPlayerBlockPlacement(
                  new BlockPos(getX(), getY() - 1.5, getZ()), 1, new ItemStack(Blocks.stone.getItem(mc.theWorld, new BlockPos(-1, -1, -1))), 0.0F, 0.94F, 0.0F
               )
            );
            if (mc.thePlayer.ticksExisted % 5 == 0) {
               if (mc.gameSettings.keyBindJump.pressed) {
                  mc.thePlayer.motionY = 1.0;
               } else if (mc.gameSettings.keyBindSneak.pressed) {
                  mc.thePlayer.motionY = -1.0;
               } else {
                  mc.thePlayer.motionY = 0.17F;
               }
            } else {
               mc.timer.timerSpeed = 1.0F;
            }
         }

         if (Aqua.setmgr.getSetting("FlyMode").getCurrentMode().equalsIgnoreCase("CubecraftNew")) {
            double speed1 = (double)((float)Aqua.setmgr.getSetting("FlySentinelSpeed").getCurrentNumber());
            if (mc.thePlayer.ticksExisted % 3 == 0) {
               PlayerUtil.setSpeed(speed1);
            } else {
               PlayerUtil.setSpeed(0.0);
            }

            sendPacketUnlogged(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, new ItemStack(Items.water_bucket), 0.0F, 0.5F, 0.0F));
            sendPacketUnlogged(
               new C08PacketPlayerBlockPlacement(
                  new BlockPos(getX(), getY() - 1.5, getZ()), 1, new ItemStack(Blocks.stone.getItem(mc.theWorld, new BlockPos(-1, -1, -1))), 0.0F, 0.94F, 0.0F
               )
            );
         }

         if (Aqua.setmgr.getSetting("FlyMode").getCurrentMode().equalsIgnoreCase("CubecraftSmooth")) {
            double speed1 = (double)((float)Aqua.setmgr.getSetting("FlySentinelSpeed").getCurrentNumber());
            PlayerUtil.setSpeed(speed1);
            sendPacketUnlogged(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, new ItemStack(Items.water_bucket), 0.0F, 0.5F, 0.0F));
            sendPacketUnlogged(
               new C08PacketPlayerBlockPlacement(
                  new BlockPos(getX(), getY() - 1.5, getZ()), 1, new ItemStack(Blocks.stone.getItem(mc.theWorld, new BlockPos(-1, -1, -1))), 0.0F, 0.94F, 0.0F
               )
            );
         }

         if (Aqua.setmgr.getSetting("FlyMode").getCurrentMode().equalsIgnoreCase("Cubecraft")) {
            PlayerUtil.setSpeed((double)((float)Aqua.setmgr.getSetting("FlySentinelSpeed").getCurrentNumber()));
            mc.thePlayer.onGround = true;
            if (mc.gameSettings.keyBindJump.pressed) {
               mc.thePlayer.motionY = 1.0;
               mc.timer.timerSpeed = 1.0F;
            }

            sendPacketUnlogged(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, new ItemStack(Items.water_bucket), 0.0F, 0.5F, 0.0F));
            sendPacketUnlogged(
               new C08PacketPlayerBlockPlacement(
                  new BlockPos(getX(), getY() - 1.5, getZ()), 1, new ItemStack(Blocks.stone.getItem(mc.theWorld, new BlockPos(-1, -1, -1))), 0.0F, 0.94F, 0.0F
               )
            );
            if (mc.thePlayer.ticksExisted % 5 == 0) {
               if (mc.gameSettings.keyBindJump.pressed) {
                  mc.thePlayer.motionY = 1.0;
               } else if (mc.gameSettings.keyBindSneak.pressed) {
                  mc.thePlayer.motionY = -1.0;
               } else {
                  mc.thePlayer.motionY = 0.17F;
               }
            } else {
               mc.timer.timerSpeed = 1.0F;
            }
         }

         if (Aqua.setmgr.getSetting("FlyMode").getCurrentMode().equalsIgnoreCase("Verus2")) {
            mc.thePlayer.onGround = true;
            sendPacketUnlogged(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, new ItemStack(Items.water_bucket), 0.0F, 0.5F, 0.0F));
            sendPacketUnlogged(
               new C08PacketPlayerBlockPlacement(
                  new BlockPos(getX(), getY() - 1.5, getZ()), 1, new ItemStack(Blocks.stone.getItem(mc.theWorld, new BlockPos(-1, -1, -1))), 0.0F, 0.94F, 0.0F
               )
            );
            float speed = (float)Aqua.setmgr.getSetting("FlyBoost").getCurrentNumber();
            if (mc.thePlayer.hurtTime != 0) {
               PlayerUtil.setSpeed((double)speed);
               mc.timer.timerSpeed = 0.6F;
            } else {
               PlayerUtil.setSpeed(PlayerUtil.getSpeed());
               mc.timer.timerSpeed = 1.0F;
            }

            if (!mc.gameSettings.keyBindJump.pressed && mc.gameSettings.keyBindSneak.pressed) {
               mc.thePlayer.motionY = -1.0;
            } else if (mc.gameSettings.keyBindJump.pressed && !mc.gameSettings.keyBindSneak.pressed) {
               mc.thePlayer.motionY = 1.0;
            } else {
               mc.thePlayer.motionY = 0.0;
            }
         }

         if (Aqua.setmgr.getSetting("FlyMode").getCurrentMode().equalsIgnoreCase("Verus")) {
            sendPacketUnlogged(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, new ItemStack(Items.water_bucket), 0.0F, 0.5F, 0.0F));
            sendPacketUnlogged(
               new C08PacketPlayerBlockPlacement(
                  new BlockPos(getX(), getY() - 1.5, getZ()), 1, new ItemStack(Blocks.stone.getItem(mc.theWorld, new BlockPos(-1, -1, -1))), 0.0F, 0.94F, 0.0F
               )
            );
            mc.thePlayer.onGround = true;
            if (mc.thePlayer.hurtTime != 0) {
               this.verusDmg = true;
            }

            if (!this.verusDmg) {
               mc.timer.timerSpeed = 0.2F;
            } else {
               if (!mc.gameSettings.keyBindJump.pressed && mc.gameSettings.keyBindSneak.pressed) {
                  mc.thePlayer.motionY = 0.0;
               } else if (mc.gameSettings.keyBindJump.pressed && !mc.gameSettings.keyBindSneak.pressed) {
                  mc.thePlayer.motionY = 0.0;
               } else {
                  mc.thePlayer.motionY = 0.0;
               }

               mc.timer.timerSpeed = 0.2F;
               PlayerUtil.setSpeed(5.0);
            }
         }

         if (Aqua.setmgr.getSetting("FlyMode").getCurrentMode().equalsIgnoreCase("Minemora")) {
            if (mc.thePlayer.ticksExisted % 2 == 0) {
               mc.thePlayer.motionY = 0.03;
               mc.timer.timerSpeed = 1.4F;
            } else {
               mc.thePlayer.motionY = -0.02;
               mc.timer.timerSpeed = 1.4F;
            }
         }
      }
   }

   public static void sendPacketUnlogged(Packet<? extends INetHandler> packet) {
      mc.getNetHandler().getNetworkManager().sendPacket(packet);
   }

   public static double getX() {
      return mc.thePlayer.posX;
   }

   public static double getY() {
      return mc.thePlayer.posY;
   }

   public static double getZ() {
      return mc.thePlayer.posZ;
   }
}
