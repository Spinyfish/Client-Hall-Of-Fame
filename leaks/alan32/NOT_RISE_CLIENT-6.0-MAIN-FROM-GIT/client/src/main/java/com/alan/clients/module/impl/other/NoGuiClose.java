package com.alan.clients.module.impl.other;

import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.packet.PacketReceiveEvent;
import com.alan.clients.value.impl.BooleanValue;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S2EPacketCloseWindow;

/**
 * @author Alan Jr.
 * @since 9/17/2022
 */
@Rise
@ModuleInfo(name = "module.other.noguiclose.name", category = Category.OTHER, description = "module.other.noguiclose.description")
public final class NoGuiClose extends Module {
    private final BooleanValue chatonly = new BooleanValue("Chat Only", this, false);

    @EventLink()
    public final Listener<PacketReceiveEvent> onPacketReceive = event -> {
        final Packet<?> packet = event.getPacket();
        if (event.getPacket() instanceof S2EPacketCloseWindow && (mc.currentScreen instanceof GuiChat || !chatonly.getValue())) {
            event.setCancelled(true);
        }
    };
}

