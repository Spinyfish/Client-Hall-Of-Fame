package dev.elysium.client.mods.impl.player;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.ModeSetting;
import dev.elysium.client.Elysium;
import dev.elysium.client.events.EventPacket;
import dev.elysium.client.events.EventUpdate;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

public class NoFall extends Mod {

    public ModeSetting mode = new ModeSetting("Mode",this,"Vanilla","Edit","Collide","Glide");

    public NoFall() {
        super("No Fall","Reduces/Nullifies fall damage", Category.PLAYER);
    }

    @EventTarget
    public void onEventPacket(EventPacket e) {
        if(!mode.is("Edit")) return;

        Packet p = e.getPacket();

        if(p instanceof C03PacketPlayer && mc.thePlayer.fallDistance > 2) {
            C03PacketPlayer c3 = (C03PacketPlayer)p;
            c3.setOnGround(true);
        }
    }

    @EventTarget
    public void onEventUpdate(EventUpdate e) {
        switch(mode.getMode()) {
            case "Vanilla":
                if(mc.thePlayer.fallDistance > 2)
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                break;
        }
    }

}
