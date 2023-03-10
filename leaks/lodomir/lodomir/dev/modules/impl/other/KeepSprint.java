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
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class KeepSprint
extends Module {
    public KeepSprint() {
        super("KeepSprint", 0, Category.MOVEMENT);
    }

    @Subscribe
    public void onGetPackets(EventGetPackets event) {
        Packet packet = event.packets;
        if (packet instanceof C0BPacketEntityAction && ((C0BPacketEntityAction)packet).getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
            event.setCancelled(true);
        }
        super.onGetPackets(event);
    }
}

