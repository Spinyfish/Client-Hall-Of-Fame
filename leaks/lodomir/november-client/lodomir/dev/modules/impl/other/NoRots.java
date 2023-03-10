/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.other;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.event.impl.EventGetPackets;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class NoRots
extends Module {
    public NoRots() {
        super("NoRot", 0, Category.OTHER);
    }

    @Subscribe
    public void onGetPackets(EventGetPackets event) {
        if (!NoRots.mc.getNetHandler().doneLoadingTerrain) {
            return;
        }
        if (event.getPackets() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook)event.getPackets();
            packet.setYaw(NoRots.mc.thePlayer.rotationYaw);
            packet.setPitch(NoRots.mc.thePlayer.rotationPitch);
        }
        event.setCancelled(true);
    }
}

