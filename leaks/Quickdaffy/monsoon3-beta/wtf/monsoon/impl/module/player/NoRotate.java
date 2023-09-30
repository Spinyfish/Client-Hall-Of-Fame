/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.impl.event.EventPacket;

public class NoRotate
extends Module {
    private final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.CANCEL).describedBy("The mode to use.");
    @EventLink
    public final Listener<EventPacket> eventPreMotionListener = e -> {
        if (e.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook)e.getPacket();
            if (!this.mc.getNetHandler().doneLoadingTerrain) {
                return;
            }
            switch (this.mode.getValue()) {
                case CANCEL: {
                    e.setCancelled(true);
                    PacketUtil.sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch(), this.mc.thePlayer.onGround));
                    this.mc.thePlayer.setPosition(packet.getX(), packet.getY(), packet.getZ());
                    break;
                }
                case EDIT: {
                    packet.setYaw(this.mc.thePlayer.rotationYaw);
                    packet.setPitch(this.mc.thePlayer.rotationPitch);
                    this.mc.thePlayer.setPosition(packet.getX(), packet.getY(), packet.getZ());
                }
            }
        }
    };

    public NoRotate() {
        super("No Rotate", "Cancel incoming rotation packets.", Category.PLAYER);
    }

    private static enum Mode {
        CANCEL,
        EDIT;

    }
}

