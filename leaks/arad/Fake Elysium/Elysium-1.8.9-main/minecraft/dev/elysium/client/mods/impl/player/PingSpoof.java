package dev.elysium.client.mods.impl.player;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.NumberSetting;
import dev.elysium.client.Elysium;
import dev.elysium.client.events.EventPacket;
import dev.elysium.client.events.EventUpdate;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;

public class PingSpoof extends Mod {
    public HashMap<Packet, Long> packetmap = new HashMap<Packet, Long>();
    public List<Object []> removelist = new ArrayList<>();
    public NumberSetting delay = new NumberSetting("Ping Delay",0,5000,750,1,this);

    public PingSpoof() {
        super("PingSpoof","Spoof your ping", Category.PLAYER);
    }

    @EventTarget
    public void onEventUpdate(EventUpdate e) {
        if(mc.thePlayer.ticksExisted <= 1)
            packetmap.clear();

        for (Packet p : packetmap.keySet()) {
            long timestamp = packetmap.get(p);
            if (timestamp < System.currentTimeMillis() - delay.getValue()) {
                mc.thePlayer.sendQueue.addToSilentSendQueue(p);
                removelist.add(new Object[]{p, timestamp});
            }
        }

        for (Object[] o : removelist) {
            packetmap.remove(o[0], o[1]);
        }
    }

    @EventTarget
    public void onEventPacket(EventPacket e) {
        Packet p = e.getPacket();
        if(mc.thePlayer.ticksExisted > 1 && (p instanceof C00PacketKeepAlive || p instanceof C0FPacketConfirmTransaction)) {
            packetmap.put(p,System.currentTimeMillis());
            e.setCancelled(true);
        }
    }
}
