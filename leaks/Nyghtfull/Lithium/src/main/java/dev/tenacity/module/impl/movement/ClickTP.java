package dev.tenacity.module.impl.movement;

import dev.tenacity.event.impl.game.world.TickEvent;
import dev.tenacity.event.impl.network.PacketReceiveEvent;
import dev.tenacity.event.impl.network.PacketSendEvent;
import dev.tenacity.event.impl.player.BoundingBoxEvent;
import dev.tenacity.event.impl.player.UpdateEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.ModeSetting;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Mouse;

public class ClickTP extends Module {

    private boolean hasclicked;
    public static final ModeSetting mode = new ModeSetting("Mode", "Vannila","Cock", "Vannila");
    public ClickTP() {
        super("ClickTP", Category.MOVEMENT, "Tp`s your where u click");
        addSettings(mode);
    }
    private boolean isBlockUnder() {
        if (mc.thePlayer.posY < 0) return false;
        for (int offset = 0; offset < (int) mc.thePlayer.posY + 2; offset += 2) {
            AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0, -offset, 0);
            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDisable() {
        hasclicked = false;
        super.onDisable();
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent e) {
        if (mode.is("Cock")) {
           // if(hasclicked) {
                if (e.getPacket() instanceof S08PacketPlayerPosLook) {

                   // if(mc.thePlayer != null) {
                       e.cancel();
                   // }
                }
           // }
        }
        super.onPacketReceiveEvent(e);
    }
    @Override
    public void onPacketSendEvent(PacketSendEvent event) {
        if(mode.is("Cock")) {
          //  if(hasclicked) {
            if(!hasclicked) {
                if (event.getPacket() instanceof C00PacketKeepAlive || event.getPacket() instanceof C01PacketChatMessage || event.getPacket() instanceof C02PacketUseEntity || event.getPacket() instanceof C03PacketPlayer || event.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition || event.getPacket() instanceof C07PacketPlayerDigging || event.getPacket() instanceof C08PacketPlayerBlockPlacement || event.getPacket() instanceof C07PacketPlayerDigging || event.getPacket() instanceof C07PacketPlayerDigging) {

                  //  event.cancel();


                }
            }
        }
        super.onPacketSendEvent(event);
    }

    @Override
    public void onBoundingBoxEvent(BoundingBoxEvent event) {
        if(mode.is("Cock") && hasclicked) {
            final AxisAlignedBB axisAlignedBB = AxisAlignedBB.fromBounds(-5, -1, -5, 5, 1, 5).offset(event.getBlockPos().getX(), event.getBlockPos().getY(), event.getBlockPos().getZ());
            event.setBoundingBox(axisAlignedBB);
        }
        super.onBoundingBoxEvent(event);
    }
    @Override
    public void onTickEvent(TickEvent event) {
        if(mode.is("Cock") && hasclicked) {

        }
    }




    @Override
    public void onUpdateEvent(UpdateEvent e) {
        if(mode.is("Cock") && hasclicked) {
            if(isBlockUnder()&& mc.thePlayer.onGround) {
                if(mc.thePlayer.ticksExisted % 3 == 0) {
                  //  mc.thePlayer.jump();
                }
            }
        }

        MovingObjectPosition ray = this.rayTrace(500.0);
        if (ray == null) {
            return;
        }
        if (Mouse.isButtonDown((int)1)) {
            hasclicked = true;
            double x_new = (double)ray.getBlockPos().getX() + 0.5;
            double y_new = ray.getBlockPos().getY() + 1;
            double z_new = (double)ray.getBlockPos().getZ() + 0.5;
            double distance = this.mc.thePlayer.getDistance(x_new, y_new, z_new);
            double d = 0.0;
            while (d < distance) {
                this.setPos(this.mc.thePlayer.posX + (x_new - (double)this.mc.thePlayer.getHorizontalFacing().getFrontOffsetX() - this.mc.thePlayer.posX) * d / distance, this.mc.thePlayer.posY + (y_new - this.mc.thePlayer.posY) * d / distance, this.mc.thePlayer.posZ + (z_new - (double)this.mc.thePlayer.getHorizontalFacing().getFrontOffsetZ() - this.mc.thePlayer.posZ) * d / distance);
                d += 2.0;
            }
            this.setPos(x_new, y_new, z_new);
            this.mc.renderGlobal.loadRenderers();
        }
        super.onUpdateEvent(e);
    }

    public MovingObjectPosition rayTrace(double blockReachDistance) {
        Vec3 vec3 = this.mc.thePlayer.getPositionEyes(1.0f);
        Vec3 vec4 = this.mc.thePlayer.getLookVec();
        Vec3 vec5 = vec3.addVector(vec4.xCoord * blockReachDistance, vec4.yCoord * blockReachDistance, vec4.zCoord * blockReachDistance);
        return this.mc.theWorld.rayTraceBlocks(vec3, vec5, !this.mc.thePlayer.isInWater(), false, false);
    }

    public void setPos(double x, double y, double z) {
        this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, true));
        this.mc.thePlayer.setPosition(x, y, z);
    }




}
