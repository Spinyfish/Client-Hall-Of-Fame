/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.other;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.event.impl.network.EventGetPacket;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class NoRotate
extends Module {
    private float yaw;
    private float pitch;

    public NoRotate() {
        super("NoRotate", 0, Category.OTHER);
    }

    @Override
    @Subscribe
    public void onGetPacket(EventGetPacket event) {
        Packet packet = event.getPacket();
        if (packet instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook s08 = (S08PacketPlayerPosLook)packet;
            this.yaw = s08.yaw;
            this.pitch = s08.pitch;
            s08.yaw = NoRotate.mc.thePlayer.rotationYaw;
            s08.pitch = NoRotate.mc.thePlayer.rotationPitch;
        }
    }
}

