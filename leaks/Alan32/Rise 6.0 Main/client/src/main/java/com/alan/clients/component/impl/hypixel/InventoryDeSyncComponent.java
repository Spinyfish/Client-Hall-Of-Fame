package com.alan.clients.component.impl.hypixel;

import com.alan.clients.component.Component;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.packet.PacketReceiveEvent;
import com.alan.clients.util.chat.ChatUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S2DPacketOpenWindow;

public class InventoryDeSyncComponent extends Component {

    private static boolean active, deSynced;

    @EventLink
    public final Listener<PacketReceiveEvent> onPacketReceive = event -> {
        Packet<?> p = event.getPacket();

        if (p instanceof S2DPacketOpenWindow) {
            if (active) {
                event.setCancelled();
                deSynced = true;
                active = false;
            }
        }
    };

    public static void setActive(String command) {
        if (active || deSynced || mc.currentScreen != null) {
            return;
        }

        ChatUtil.send(command);
        active = true;
    }

    public static boolean isDeSynced() {
        return deSynced;
    }
}
