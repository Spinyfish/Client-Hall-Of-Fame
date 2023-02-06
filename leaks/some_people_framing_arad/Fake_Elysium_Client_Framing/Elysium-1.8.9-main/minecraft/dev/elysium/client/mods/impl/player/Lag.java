package dev.elysium.client.mods.impl.player;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.NumberSetting;
import dev.elysium.client.Elysium;
import dev.elysium.client.events.EventPacket;
import dev.elysium.client.events.EventRenderHUD;
import dev.elysium.client.events.EventUpdate;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Lag extends Mod {
    public HashMap<Packet, Long> packetmap = new HashMap<Packet, Long>();
    public List<Object []> removelist = new ArrayList<>();
    public NumberSetting delay = new NumberSetting("Delay (MS)",0,5000,750,1,this);

    public Lag() {
        super("Lag","Delays outgoing packets", Category.PLAYER);
    }

    public void onEnable() {
        packetmap.clear();
        removelist.clear();
    }

    public void onDisable() {
        for(Packet p : packetmap.keySet())
            mc.thePlayer.sendQueue.addToSilentSendQueue(p);
        packetmap.clear();
        removelist.clear();
    }

    @EventTarget
    public void onEventRenderHUD(EventRenderHUD e) {
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
        if(e.isIncoming() || mc.thePlayer.ticksExisted < 5 || (e.packet instanceof C0FPacketConfirmTransaction)) return;
        packetmap.put(e.getPacket(),System.currentTimeMillis());
        e.setCancelled(true);
    }
}
