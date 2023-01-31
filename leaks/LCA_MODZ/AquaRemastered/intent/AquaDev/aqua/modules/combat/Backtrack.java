package intent.AquaDev.aqua.modules.combat;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventPacket;
import events.listeners.EventRenderNameTags;
import events.listeners.EventTick;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.utils.RenderUtil;
import intent.AquaDev.aqua.utils.TimeUtil;
import java.util.ArrayList;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.ThreadQuickExitException;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class Backtrack extends Module {
   private final ArrayList<Packet> packets = new ArrayList<>();
   private EntityLivingBase entity = null;
   public static EntityPlayer target = null;
   private boolean blockPackets;
   private INetHandler packetListener = null;
   private WorldClient lastWorld;
   private final TimeUtil timeUtil = new TimeUtil();

   public Backtrack() {
      super("Backtrack", Module.Type.Combat, "Backtrack", 0, Category.Combat);
      Aqua.setmgr.register(new Setting("Range", this, 6.0, 3.0, 6.0, false));
      Aqua.setmgr.register(new Setting("BacktrackMS", this, 1000.0, 50.0, 2000.0, false));
   }

   @Override
   public void onEnable() {
      this.blockPackets = false;
      this.packets.clear();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.packets.clear();
      super.onDisable();
   }

   @Override
   public void onEvent(Event event) {
      if (event instanceof EventRenderNameTags) {
         this.render(Killaura.target);
      }

      if (event instanceof EventPacket) {
         EventPacket eventPacket = (EventPacket)event;
         if (eventPacket.getDirection() != EnumPacketDirection.CLIENTBOUND) {
            return;
         }

         this.packetListener = eventPacket.getNetHandler();
         Packet p = EventPacket.getPacket();
         if (p instanceof S14PacketEntity) {
            S14PacketEntity packet = (S14PacketEntity)EventPacket.getPacket();
            Entity entity1 = mc.theWorld.getEntityByID(packet.getEntityId());
            if (entity1 instanceof EntityLivingBase) {
               EntityLivingBase entityLivingBase = (EntityLivingBase)entity1;
               entityLivingBase.realPosX += packet.func_149062_c();
               entityLivingBase.realPosY += packet.func_149061_d();
               entityLivingBase.realPosZ += packet.func_149064_e();
            }
         }

         if (p instanceof S18PacketEntityTeleport) {
            S18PacketEntityTeleport packet = (S18PacketEntityTeleport)EventPacket.getPacket();
            Entity entity1 = mc.theWorld.getEntityByID(packet.getEntityId());
            if (entity1 instanceof EntityLivingBase) {
               EntityLivingBase entityLivingBase = (EntityLivingBase)entity1;
               entityLivingBase.realPosX = packet.getX();
               entityLivingBase.realPosY = packet.getY();
               entityLivingBase.realPosZ = packet.getZ();
            }
         }

         this.entity = target;
         if (this.entity == null) {
            this.resetPackets(eventPacket.getNetHandler());
            return;
         }

         if (mc.theWorld != null && mc.thePlayer != null) {
            if (this.lastWorld != mc.theWorld) {
               this.resetPackets(eventPacket.getNetHandler());
               this.lastWorld = mc.theWorld;
               return;
            }

            this.addPackets(p, eventPacket);
         }

         this.lastWorld = mc.theWorld;
      }

      if (event instanceof EventTick) {
         if (Aqua.moduleManager.getModuleByName("Killaura").isToggled()) {
            target = this.searchTargets();
         } else {
            target = null;
         }

         if (mc.thePlayer != null && this.packetListener != null && mc.theWorld != null) {
            if (this.entity == null) {
               this.resetPackets(this.packetListener);
               return;
            }

            double d0 = (double)this.entity.realPosX / 32.0;
            double d1 = (double)this.entity.realPosY / 32.0;
            double d2 = (double)this.entity.realPosZ / 32.0;
            double d3 = (double)this.entity.serverPosX / 32.0;
            double d4 = (double)this.entity.serverPosY / 32.0;
            double d5 = (double)this.entity.serverPosZ / 32.0;
            AxisAlignedBB alignedBB = new AxisAlignedBB(
               d3 - (double)this.entity.width,
               d4,
               d5 - (double)this.entity.width,
               d3 + (double)this.entity.width,
               d4 + (double)this.entity.height,
               d5 + (double)this.entity.width
            );
            Vec3 positionEyes = mc.thePlayer.getPositionEyes(mc.timer.renderPartialTicks);
            double currentX = MathHelper.clamp_double(positionEyes.xCoord, alignedBB.minX, alignedBB.maxX);
            double currentY = MathHelper.clamp_double(positionEyes.yCoord, alignedBB.minY, alignedBB.maxY);
            double currentZ = MathHelper.clamp_double(positionEyes.zCoord, alignedBB.minZ, alignedBB.maxZ);
            AxisAlignedBB alignedBB2 = new AxisAlignedBB(
               d0 - (double)this.entity.width,
               d1,
               d2 - (double)this.entity.width,
               d0 + (double)this.entity.width,
               d1 + (double)this.entity.height,
               d2 + (double)this.entity.width
            );
            double realX = MathHelper.clamp_double(positionEyes.xCoord, alignedBB2.minX, alignedBB2.maxX);
            double realY = MathHelper.clamp_double(positionEyes.yCoord, alignedBB2.minY, alignedBB2.maxY);
            double realZ = MathHelper.clamp_double(positionEyes.zCoord, alignedBB2.minZ, alignedBB2.maxZ);
            double distance = 6.0;
            if (!mc.thePlayer.canEntityBeSeen(this.entity)) {
               distance = 3.0;
            }

            double bestX = MathHelper.clamp_double(positionEyes.xCoord, this.entity.getEntityBoundingBox().minX, this.entity.getEntityBoundingBox().maxX);
            double bestY = MathHelper.clamp_double(positionEyes.yCoord, this.entity.getEntityBoundingBox().minY, this.entity.getEntityBoundingBox().maxY);
            double bestZ = MathHelper.clamp_double(positionEyes.zCoord, this.entity.getEntityBoundingBox().minZ, this.entity.getEntityBoundingBox().maxZ);
            boolean b = false;
            if (positionEyes.distanceTo(new Vec3(bestX, bestY, bestZ)) > 2.98) {
               b = true;
            }

            float delayMS = (float)Aqua.setmgr.getSetting("BacktrackBacktrackMS").getCurrentNumber();
            if (!b
               || !(positionEyes.distanceTo(new Vec3(realX, realY, realZ)) > positionEyes.distanceTo(new Vec3(currentX, currentY, currentZ)) + 0.05)
               || !(mc.thePlayer.getDistance(d0, d1, d2) < distance)
               || this.timeUtil.hasReached((long)delayMS)) {
               this.resetPackets(this.packetListener);
               this.timeUtil.reset();
            }
         }
      }
   }

   private void resetPackets(INetHandler netHandler) {
      if (this.packets.size() > 0) {
         for(; this.packets.size() != 0; this.packets.remove(this.packets.get(0))) {
            try {
               this.packets.get(0).processPacket(netHandler);
            } catch (ThreadQuickExitException var3) {
            }
         }
      }
   }

   private void addPackets(Packet packet, EventPacket eventPacket) {
      synchronized(this.packets) {
         if (this.blockPacket(packet)) {
            this.packets.add(packet);
            eventPacket.setCancelled(true);
         }
      }
   }

   private boolean blockPacket(Packet packet) {
      if (packet instanceof S03PacketTimeUpdate) {
         return true;
      } else if (packet instanceof S00PacketKeepAlive) {
         return true;
      } else if (packet instanceof C00PacketKeepAlive) {
         return true;
      } else if (packet instanceof C0CPacketInput) {
         return true;
      } else if (packet instanceof S12PacketEntityVelocity) {
         return true;
      } else if (packet instanceof C01PacketEncryptionResponse) {
         return true;
      } else if (packet instanceof S27PacketExplosion) {
         return true;
      } else {
         return packet instanceof S32PacketConfirmTransaction
            || packet instanceof S14PacketEntity
            || packet instanceof S19PacketEntityHeadLook
            || packet instanceof S18PacketEntityTeleport
            || packet instanceof S0FPacketSpawnMob
            || packet instanceof S08PacketPlayerPosLook;
      }
   }

   public EntityPlayer searchTargets() {
      float range = (float)Aqua.setmgr.getSetting("BacktrackRange").getCurrentNumber();
      EntityPlayer player = null;
      double closestDist = 100000.0;

      for(Entity o : mc.theWorld.loadedEntityList) {
         if (!o.getName().equals(mc.thePlayer.getName()) && o instanceof EntityPlayer && mc.thePlayer.getDistanceToEntity(o) < range) {
            double dist = (double)mc.thePlayer.getDistanceToEntity(o);
            if (dist < closestDist) {
               closestDist = dist;
               player = (EntityPlayer)o;
            }
         }
      }

      return player;
   }

   private void render(EntityLivingBase entity) {
      float red = 0.0F;
      float green = 1.1333333F;
      float blue = 0.0F;
      float lineWidth = 3.0F;
      float alpha = 0.03137255F;
      if (mc.thePlayer.getDistanceToEntity(entity) > 1.0F) {
         double d0 = (double)(1.0F - mc.thePlayer.getDistanceToEntity(entity) / 20.0F);
         if (d0 < 0.3) {
            d0 = 0.3;
         }

         lineWidth = (float)((double)lineWidth * d0);
      }

      RenderUtil.drawEntityServerESP(entity, red, green, blue, alpha, 1.0F, 1.0F);
   }
}
