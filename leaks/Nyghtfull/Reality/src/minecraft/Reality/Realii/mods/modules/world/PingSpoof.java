/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.mods.modules.world;

import libraries.optifine.MathUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventPacketSend;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.utils.cheats.world.TimerUtil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PingSpoof
extends Module {
    private List<Packet> packetList = new CopyOnWriteArrayList<Packet>();
    private TimerUtil timer = new TimerUtil();

    public PingSpoof(){
        super("PingSpoof", ModuleType.Misc);
    }

    @EventHandler
    private void onPacketSend(EventPacketSend e) {
        if (e.getPacket() instanceof C00PacketKeepAlive && this.mc.thePlayer.isEntityAlive()) {
            this.packetList.add(e.getPacket());
            e.setCancelled(true);
        }
        if (this.timer.hasReached(750.0)) {
            if (!this.packetList.isEmpty()) {
                int i = 0;
                double totalPackets = MathUtils.getIncremental(Math.random() * 10.0, 1.0);
                for (Packet packet : this.packetList) {
                    if ((double)i >= totalPackets) continue;
                    ++i;
                    this.mc.getNetHandler().getNetworkManager().sendPacket(packet);
                    this.packetList.remove(packet);
                }
            }
            this.mc.getNetHandler().getNetworkManager().sendPacket(new C00PacketKeepAlive(10000));
            this.timer.reset();
        }
    }
}

