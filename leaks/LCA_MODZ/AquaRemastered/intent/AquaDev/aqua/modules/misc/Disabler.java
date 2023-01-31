package intent.AquaDev.aqua.modules.misc;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventPacket;
import events.listeners.EventReceivedPacket;
import events.listeners.EventTick;
import events.listeners.EventTimerDisabler;
import events.listeners.EventUpdate;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.modules.combat.Killaura;
import intent.AquaDev.aqua.utils.PacketUtils;
import intent.AquaDev.aqua.utils.PathFinder;
import intent.AquaDev.aqua.utils.TimeUtil;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import net.minecraft.block.BlockAir;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C18PacketSpectate;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class Disabler extends Module {
   public static int worldChanges;
   private final Queue<Packet<?>> packet = new ConcurrentLinkedDeque<>();
   public LinkedList packetQueue = new LinkedList();
   public int state;
   public int state2;
   public int state3;
   public int stage;
   public int stage2;
   public int stage3;
   public TimeUtil timer = new TimeUtil();
   public TimeUtil timer2 = new TimeUtil();
   public ArrayList<Packet> packets = new ArrayList<>();
   public TimeUtil helper3 = new TimeUtil();
   public TimeUtil helper = new TimeUtil();
   public TimeUtil helper2 = new TimeUtil();
   private final boolean inGround = true;
   private final boolean cancelFlag = true;
   private final boolean transaction = true;
   private final boolean transactionMultiply = true;
   private final boolean transactionSend = true;
   private boolean teleported;
   private boolean cancel;
   private TimeUtil delay = new TimeUtil();
   private TimeUtil release = new TimeUtil();
   private boolean releasePacket = false;
   private List<Packet> packetList = new ArrayList<>();

   public Disabler() {
      super("Disabler", Module.Type.Misc, "Disabler", 0, Category.Misc);
      Aqua.setmgr.register(new Setting("WatchdogRandom", this, false));
      Aqua.setmgr
         .register(
            new Setting(
               "Modes",
               this,
               "Ghostly",
               new String[]{
                  "Ghostly",
                  "BMC",
                  "Cubecraft",
                  "SentinelNew",
                  "BMC2",
                  "Minebox",
                  "Watchdog",
                  "Watchdog2",
                  "HycraftCombat",
                  "Matrix",
                  "HypixelDev",
                  "Intave",
                  "Hycraft",
                  "Timer"
               }
            )
         );
   }

   public static void sendPacketUnlogged(Packet<? extends INetHandler> packet) {
      mc.getNetHandler().getNetworkManager().sendPacket(packet);
   }

   public static List<Vec3> calculatePath(Vec3 startPos, Vec3 endPos) {
      System.out.println("Test-1");
      PathFinder pathfinder = new PathFinder(startPos, endPos);
      System.out.println("Test");
      pathfinder.calculatePath(5000);
      System.out.println("Test2");
      int i = 0;
      Vec3 lastLoc = null;
      Vec3 lastDashLoc = null;
      List<Vec3> path = new ArrayList<>();
      List<Vec3> pathFinderPath = pathfinder.getPath();

      for(Vec3 pathElm : pathFinderPath) {
         if (i != 0 && i != pathFinderPath.size() - 1) {
            boolean canContinue = true;
            if (pathElm.squareDistanceTo(lastDashLoc) > 30.0) {
               canContinue = false;
            } else {
               double smallX = Math.min(lastDashLoc.xCoord, pathElm.xCoord);
               double smallY = Math.min(lastDashLoc.yCoord, pathElm.yCoord);
               double smallZ = Math.min(lastDashLoc.zCoord, pathElm.zCoord);
               double bigX = Math.max(lastDashLoc.xCoord, pathElm.xCoord);
               double bigY = Math.max(lastDashLoc.yCoord, pathElm.yCoord);
               double bigZ = Math.max(lastDashLoc.zCoord, pathElm.zCoord);

               label50:
               for(int x = (int)smallX; (double)x <= bigX; ++x) {
                  for(int y = (int)smallY; (double)y <= bigY; ++y) {
                     for(int z = (int)smallZ; (double)z <= bigZ; ++z) {
                        if (!PathFinder.checkPositionValidity(x, y, z, false)) {
                           canContinue = false;
                           break label50;
                        }
                     }
                  }
               }
            }

            if (!canContinue) {
               path.add(lastLoc.addVector(0.5, 0.0, 0.5));
               lastDashLoc = lastLoc;
            }
         } else {
            if (lastLoc != null) {
               path.add(lastLoc.addVector(0.5, 0.0, 0.5));
            }

            path.add(pathElm.addVector(0.5, 0.0, 0.5));
            lastDashLoc = pathElm;
         }

         lastLoc = pathElm;
         ++i;
      }

      return path;
   }

   @Override
   public void onEvent(Event e) {
      if (e instanceof EventTimerDisabler) {
         Packet packet = EventTimerDisabler.getPacket();
         if (Aqua.setmgr.getSetting("DisablerModes").getCurrentMode().equalsIgnoreCase("Intave")) {
            sendPacketUnlogged(new C00PacketKeepAlive());
         }

         if (Aqua.setmgr.getSetting("DisablerModes").getCurrentMode().equalsIgnoreCase("Matrix")) {
            if (packet instanceof C00PacketKeepAlive && mc.thePlayer.ticksExisted % 5 == 0) {
               e.setCancelled(true);
            }

            if (packet instanceof C0FPacketConfirmTransaction && mc.thePlayer.ticksExisted % 2 == 0) {
               e.setCancelled(true);
            }

            if (packet instanceof C16PacketClientStatus) {
               C16PacketClientStatus clientStatus = (C16PacketClientStatus)packet;
               if (clientStatus.getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
                  e.setCancelled(true);
               }
            }
         }
      }

      if (e instanceof EventReceivedPacket && Aqua.setmgr.getSetting("DisablerModes").getCurrentMode().equalsIgnoreCase("SentinelNew")) {
         if (this.packet instanceof S07PacketRespawn) {
            e.setCancelled(true);
            boolean respawned = true;
         } else {
            boolean respawned = false;
         }

         if (this.packet instanceof C0FPacketConfirmTransaction) {
            e.setCancelled(true);
         }
      }

      if (e instanceof EventTick && Aqua.setmgr.getSetting("DisablerModes").getCurrentMode().equalsIgnoreCase("Timer")) {
         Packet packet = EventPacket.getPacket();
         if (packet instanceof C0FPacketConfirmTransaction) {
            e.setCancelled(true);
            this.cancel = false;
         } else {
            this.cancel = true;
         }

         if (packet instanceof C03PacketPlayer.C06PacketPlayerPosLook && !Aqua.moduleManager.getModuleByName("Scaffold").isToggled()) {
            e.setCancelled(true);
         }

         if (!this.cancel) {
            this.packets.forEach(PacketUtils::sendPacketNoEvent);
            this.packets.clear();
            this.cancel = false;
         }
      }

      if (e instanceof EventPacket) {
         if (Aqua.setmgr.getSetting("DisablerModes").getCurrentMode().equalsIgnoreCase("Timer") && this.packet instanceof C03PacketPlayer) {
            C03PacketPlayer c03 = (C03PacketPlayer)this.packet;
            if (!c03.isMoving() && !mc.thePlayer.isUsingItem()) {
               e.setCancelled(true);
            }
         }

         if (Aqua.setmgr.getSetting("DisablerModes").getCurrentMode().equalsIgnoreCase("SentinelNew")) {
            Packet packet = EventPacket.getPacket();
            if (packet instanceof S01PacketEncryptionRequest) {
               e.setCancelled(true);
            }

            if (packet instanceof C03PacketPlayer) {
               C03PacketPlayer c03 = (C03PacketPlayer)packet;
               if (!c03.isMoving() && !mc.thePlayer.isUsingItem()) {
               }
            }

            float yawRandom = (float)MathHelper.getRandomDoubleInRange(new Random(), 2.0, 4.0);
            if (packet instanceof C00PacketKeepAlive) {
               e.setCancelled(true);
            }

            if (packet instanceof C00PacketServerQuery && mc.thePlayer.ticksExisted % 2 == 0) {
               e.setCancelled(true);
            }

            if (packet instanceof C01PacketEncryptionResponse && mc.thePlayer.ticksExisted % 2 == 0) {
               e.setCancelled(true);
            }
         }
      }

      if (!(e instanceof EventUpdate) || !mc.isSingleplayer()) {
         if (e instanceof EventUpdate
            && Aqua.setmgr.getSetting("DisablerModes").getCurrentMode().equalsIgnoreCase("SentinelNew")
            && mc.thePlayer.ticksExisted % 150 == 0) {
         }

         if (e instanceof EventPacket) {
            if (Aqua.setmgr.getSetting("DisablerModes").getCurrentMode().equalsIgnoreCase("Hycraft")
               && EventPacket.getPacket() instanceof S08PacketPlayerPosLook) {
               if (((S08PacketPlayerPosLook)EventPacket.getPacket()).getYaw() > 680.0F
                  || ((S08PacketPlayerPosLook)EventPacket.getPacket()).getYaw() < -680.0F
                  || ((S08PacketPlayerPosLook)EventPacket.getPacket()).getPitch() < -410.0F
                  || ((S08PacketPlayerPosLook)EventPacket.getPacket()).getPitch() > 590.0F) {
                  e.setCancelled(true);
               }

               if (((S08PacketPlayerPosLook)EventPacket.getPacket()).getX() > 2.0E7
                  || ((S08PacketPlayerPosLook)EventPacket.getPacket()).getY() > 2.0E7
                  || ((S08PacketPlayerPosLook)EventPacket.getPacket()).getZ() > 2.0E7) {
                  e.setCancelled(true);
               }
            }

            if (Aqua.setmgr.getSetting("DisablerModes").getCurrentMode().equalsIgnoreCase("Intave")) {
               Packet packet = EventPacket.getPacket();
               if (packet instanceof C0FPacketConfirmTransaction
                  && mc.thePlayer.ticksExisted % 2 == 0
                  && !Aqua.moduleManager.getModuleByName("Scaffold").isToggled()) {
                  e.setCancelled(true);
               }

               if (Aqua.moduleManager.getModuleByName("Scaffold").isToggled() && packet instanceof C0BPacketEntityAction) {
                  e.setCancelled(true);
               }
            }

            Packet p = EventPacket.getPacket();
            if (Aqua.setmgr.getSetting("DisablerModes").getCurrentMode().equalsIgnoreCase("Matrix")) {
               if (p instanceof C00PacketKeepAlive && mc.thePlayer.ticksExisted % 5 == 0) {
                  e.setCancelled(true);
               }

               if (p instanceof C0FPacketConfirmTransaction) {
                  e.setCancelled(true);
               }

               if (p instanceof C16PacketClientStatus) {
                  C16PacketClientStatus clientStatus = (C16PacketClientStatus)p;
                  if (clientStatus.getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
                     e.setCancelled(true);
                  }
               }

               if (p instanceof C0BPacketEntityAction) {
               }
            }

            if (Aqua.setmgr.getSetting("DisablerModes").getCurrentMode().equalsIgnoreCase("BMC2")) {
               if (p instanceof C01PacketPing) {
                  sendPacketUnlogged(new C18PacketSpectate(UUID.randomUUID()));
                  float voidTP = (float)MathHelper.getRandomDoubleInRange(new Random(), 0.78, 0.98);
                  sendPacketUnlogged(new C18PacketSpectate(UUID.randomUUID()));
                  sendPacketUnlogged(new C0CPacketInput(voidTP, voidTP, false, false));
                  e.setCancelled(true);
               }

               if (p instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
                  e.setCancelled(true);
               }
            }

            if (Aqua.setmgr.getSetting("DisablerModes").getCurrentMode().equalsIgnoreCase("Watchdog")) {
               boolean worldChange;
               if (this.packet instanceof S07PacketRespawn) {
                  worldChange = true;
               } else {
                  worldChange = false;
               }

               if (worldChange && p instanceof S08PacketPlayerPosLook) {
                  e.setCancelled(true);
               }

               if (worldChange && p instanceof C0FPacketConfirmTransaction) {
                  e.setCancelled(true);
               }

               if (worldChange && p instanceof C00PacketKeepAlive) {
                  e.setCancelled(true);
               }

               if (mc.thePlayer.ticksExisted < 50 && p instanceof S08PacketPlayerPosLook) {
                  e.setCancelled(true);
                  S08PacketPlayerPosLook s08 = (S08PacketPlayerPosLook)p;
                  float watchdogRandom1 = (float)MathHelper.getRandomDoubleInRange(new Random(), 1.0, 1.1213131123);
                  s08.y += (double)watchdogRandom1;
                  mc.getNetHandler()
                     .addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(s08.getX(), s08.getY(), s08.getZ(), s08.getYaw(), s08.getPitch(), false));
                  mc.getNetHandler()
                     .addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(s08.getX(), s08.getY() + 1.0, s08.getZ(), s08.getYaw(), s08.getPitch(), false));
               }
            }

            if (Aqua.setmgr.getSetting("DisablerModes").getCurrentMode().equalsIgnoreCase("Minebox")) {
               if (p instanceof C0FPacketConfirmTransaction) {
                  e.setCancelled(true);
               }

               if (p instanceof C0BPacketEntityAction) {
                  C0BPacketEntityAction c0B = (C0BPacketEntityAction)p;
                  if (c0B.getAction().equals(C0BPacketEntityAction.Action.START_SPRINTING)) {
                     if (EntityPlayerSP.serverSprintState) {
                        this.sendPacketSilent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                        EntityPlayerSP.serverSprintState = false;
                     }

                     e.setCancelled(true);
                  }

                  if (c0B.getAction().equals(C0BPacketEntityAction.Action.STOP_SPRINTING)) {
                     e.setCancelled(true);
                  }
               }
            }

            if (Aqua.setmgr.getSetting("DisablerModes").getCurrentMode().equalsIgnoreCase("Cubecraft")) {
               if (p instanceof C03PacketPlayer.C06PacketPlayerPosLook && !Aqua.moduleManager.getModuleByName("Scaffold").isToggled()) {
                  e.setCancelled(true);
               }

               if (p instanceof C03PacketPlayer) {
                  C03PacketPlayer c03 = (C03PacketPlayer)p;
                  if (!c03.isMoving() && !mc.thePlayer.isUsingItem()) {
                     e.setCancelled(true);
                  }
               }

               if (p instanceof C00PacketKeepAlive) {
                  e.setCancelled(true);
               }

               if (p instanceof C0FPacketConfirmTransaction) {
                  e.setCancelled(true);
               }

               if (p instanceof C0CPacketInput) {
                  e.setCancelled(true);
               }

               if (p instanceof C01PacketPing) {
                  e.setCancelled(true);
               }

               if (Killaura.target == null) {
                  assert p instanceof C0BPacketEntityAction;

                  C0BPacketEntityAction c0B = (C0BPacketEntityAction)p;
                  if (c0B.getAction().equals(C0BPacketEntityAction.Action.START_SPRINTING)) {
                     if (EntityPlayerSP.serverSprintState) {
                        this.sendPacketSilent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                        EntityPlayerSP.serverSprintState = false;
                     }

                     e.setCancelled(true);
                  }
               }
            }
         }

         if (Aqua.setmgr.getSetting("DisablerModes").getCurrentMode().equalsIgnoreCase("BMC")) {
            Packet p = EventPacket.getPacket();
            if (mc.thePlayer.ticksExisted < 200 && p instanceof S07PacketRespawn) {
               this.teleported = false;
            }

            if (mc.thePlayer.ticksExisted < 250 && p instanceof S07PacketRespawn) {
               this.packet.clear();
               return;
            }

            if (p instanceof C0BPacketEntityAction) {
               C0BPacketEntityAction c0B = (C0BPacketEntityAction)p;
               if (c0B.getAction().equals(C0BPacketEntityAction.Action.START_SPRINTING)) {
                  if (EntityPlayerSP.serverSprintState) {
                     this.sendPacketSilent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                     EntityPlayerSP.serverSprintState = false;
                  }

                  e.setCancelled(true);
               }

               if (c0B.getAction().equals(C0BPacketEntityAction.Action.STOP_SPRINTING)) {
                  e.setCancelled(true);
               }
            }

            if (p instanceof C00PacketKeepAlive || p instanceof C0FPacketConfirmTransaction) {
               this.packet.add(p);
               e.setCancelled(true);
               if (this.packet.size() > 500) {
                  this.sendPacketSilent(this.packet.poll());
                  e.setCancelled(true);
               }

               C0FPacketConfirmTransaction packetConfirmTransaction = (C0FPacketConfirmTransaction)this.packet;
               double x = ((S08PacketPlayerPosLook)this.packet).getX() - mc.thePlayer.posX;
               double y = ((S08PacketPlayerPosLook)this.packet).getY() - mc.thePlayer.posY;
               double z = ((S08PacketPlayerPosLook)this.packet).getZ() - mc.thePlayer.posZ;
               double diff = Math.sqrt(x * x + y * y + z * z);
               if (diff > 0.0) {
                  e.setCancelled(true);
               }
            }

            if (p instanceof C03PacketPlayer) {
               C03PacketPlayer c03 = (C03PacketPlayer)p;
               if (mc.thePlayer.ticksExisted % 50 == 0) {
                  sendPacketUnlogged(new C18PacketSpectate(UUID.randomUUID()));
                  float voidTP = (float)MathHelper.getRandomDoubleInRange(new Random(), 0.78, 0.98);
                  sendPacketUnlogged(new C0CPacketInput(voidTP, voidTP, false, false));
               }

               if (mc.thePlayer.ticksExisted % 120 == 0) {
                  float var37 = (float)MathHelper.getRandomDoubleInRange(new Random(), 0.01, 20.0);
               }
            }
         }

         if (Aqua.setmgr.getSetting("DisablerModes").getCurrentMode().equalsIgnoreCase("Ghostly")) {
            if (mc.thePlayer != null && mc.thePlayer.ticksExisted == 1) {
               this.packetQueue.clear();
            }

            Packet packet = EventPacket.getPacket();
            if (packet instanceof S08PacketPlayerPosLook) {
               this.getClass();
               C0FPacketConfirmTransaction packetConfirmTransaction = (C0FPacketConfirmTransaction)packet;
               double x = ((S08PacketPlayerPosLook)packet).getX() - mc.thePlayer.posX;
               double y = ((S08PacketPlayerPosLook)packet).getY() - mc.thePlayer.posY;
               double z = ((S08PacketPlayerPosLook)packet).getZ() - mc.thePlayer.posZ;
               double diff = Math.sqrt(x * x + y * y + z * z);
               if (diff > 0.0) {
                  e.setCancelled(true);
               }
            } else if (packet instanceof C03PacketPlayer) {
               this.getClass();
               if (packet instanceof C01PacketPing) {
               }

               if (packet instanceof C13PacketPlayerAbilities) {
               }

               if (mc.thePlayer.ticksExisted % 75 == 0) {
                  System.out.println("Sda");
                  float yValue = (float)MathHelper.getRandomDoubleInRange(new Random(), 1.052, 0.5213);
                  this.sendPacketSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                  this.sendPacketSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 11.0, mc.thePlayer.posZ, true));
                  this.sendPacketSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                  e.setCancelled(true);
               }
            } else if (packet instanceof C0FPacketConfirmTransaction) {
               boolean c0f = true;
               boolean c0fMultiply = true;
               if (c0f) {
                  if (!c0fMultiply) {
                     this.packetQueue.add(packet);
                     e.setCancelled(true);
                  } else {
                     for(int i = 0; i < 1; ++i) {
                        this.packetQueue.add(packet);
                     }

                     e.setCancelled(true);
                  }
               }
            }
         }
      }
   }

   public void sendPacketSilent(Packet packet) {
      mc.getNetHandler().getNetworkManager().sendPacket(packet, null);
   }

   @Override
   public void onDisable() {
      if (!mc.isSingleplayer()) {
         if (this.packets != null && this.packets.size() > 0) {
            this.packets.clear();
         }

         if (this.packetQueue != null && this.packetQueue.size() > 0) {
            this.packetQueue.clear();
         }

         this.timer.reset();
         this.timer2.reset();
         this.state = 0;
         this.state2 = 0;
         this.state3 = 0;
         this.stage = 0;
         this.stage2 = 0;
         this.stage3 = 0;
      }
   }

   @Override
   public void onEnable() {
      if (!mc.isSingleplayer()) {
         if (mc.thePlayer != null) {
            mc.thePlayer.ticksExisted = 0;
         }
      }
   }

   public boolean isBlockUnder() {
      for(int i = (int)mc.thePlayer.posY; i >= 0; --i) {
         BlockPos position = new BlockPos(mc.thePlayer.posX, (double)i, mc.thePlayer.posZ);
         if (!(mc.theWorld.getBlockState(position).getBlock() instanceof BlockAir)) {
            return true;
         }
      }

      return false;
   }
}
