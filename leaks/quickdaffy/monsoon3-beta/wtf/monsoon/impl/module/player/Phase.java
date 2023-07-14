/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.impl.event.EventPreMotion;

public class Phase
extends Module {
    private final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.VCLIP).describedBy("The mode of the scaffold.");
    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        switch (this.mode.getValue()) {
            case PACKET: {
                double strength = 0.6;
                this.mc.thePlayer.stepHeight = 0.0f;
                double mx = Math.cos(Math.toRadians(this.mc.thePlayer.rotationYaw + 90.0f));
                double mz = Math.sin(Math.toRadians(this.mc.thePlayer.rotationYaw + 90.0f));
                double x = (double)this.mc.thePlayer.movementInput.moveForward * strength * mx + (double)this.mc.thePlayer.movementInput.moveStrafe * strength * mz;
                double z = (double)this.mc.thePlayer.movementInput.moveForward * strength * mz - (double)this.mc.thePlayer.movementInput.moveStrafe * strength * mx;
                if (!this.mc.thePlayer.isCollidedHorizontally || this.mc.thePlayer.isOnLadder()) break;
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX + x, this.mc.thePlayer.posY, this.mc.thePlayer.posZ + z, false));
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 3.0, this.mc.thePlayer.posZ, false));
                this.mc.thePlayer.setPosition(this.mc.thePlayer.posX + x, this.mc.thePlayer.posY, this.mc.thePlayer.posZ + z);
                break;
            }
            case PACKETLESS: {
                this.mc.getTimer().timerSpeed = 1.0f;
                if (!this.mc.thePlayer.isCollidedHorizontally || !this.player.isMoving() || !this.mc.thePlayer.onGround) break;
                double strength = 0.545;
                this.mc.thePlayer.stepHeight = 0.0f;
                double mx = Math.cos(Math.toRadians(this.mc.thePlayer.rotationYaw + 90.0f));
                double mz = Math.sin(Math.toRadians(this.mc.thePlayer.rotationYaw + 90.0f));
                double x = (double)this.mc.thePlayer.movementInput.moveForward * strength * mx + (double)this.mc.thePlayer.movementInput.moveStrafe * strength * mz;
                double z = (double)this.mc.thePlayer.movementInput.moveForward * strength * mz - (double)this.mc.thePlayer.movementInput.moveStrafe * strength * mx;
                this.mc.thePlayer.setPosition(this.mc.thePlayer.posX + x, this.mc.thePlayer.posY - 0.07, this.mc.thePlayer.posZ + z);
                break;
            }
            case VCLIP: {
                this.mc.thePlayer.setPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY - 4.0, this.mc.thePlayer.posZ);
                this.toggle();
            }
        }
    };

    public Phase() {
        super("Phase", "Phase through blocks.", Category.PLAYER);
    }

    private static enum Mode {
        VCLIP,
        PACKET,
        PACKETLESS;

    }
}

