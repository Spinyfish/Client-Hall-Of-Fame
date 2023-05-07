package client.module.impl.combat;

import client.event.Listener;
import client.event.annotations.EventLink;

import client.event.impl.motion.MotionEvent;
import client.event.impl.packet.PacketReceiveEvent;

import client.event.impl.packet.PacketSendEvent;
import client.module.Category;
import client.module.Module;
import client.module.ModuleInfo;
import client.util.chat.ChatUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

@ModuleInfo(name = "AntiVelocity", description = "", category = Category.EXPLOIT)
public class AntiVelocity extends Module {

    @Override
    protected void onEnable() {
        super.onEnable();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
    }
    @EventLink()
    public final Listener<PacketReceiveEvent> gay = event -> {
        if (event.getPacket() instanceof S12PacketEntityVelocity){
            event.setCancelled(true);
        }
        if (event.getPacket() instanceof S27PacketExplosion){
            event.setCancelled(true);
        }


    };
}
