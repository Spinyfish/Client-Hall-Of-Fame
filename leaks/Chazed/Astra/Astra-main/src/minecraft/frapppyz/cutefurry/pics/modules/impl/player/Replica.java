package frapppyz.cutefurry.pics.modules.impl.player;

import frapppyz.cutefurry.pics.event.Event;
import frapppyz.cutefurry.pics.event.impl.SendPacket;
import frapppyz.cutefurry.pics.event.impl.Update;
import frapppyz.cutefurry.pics.modules.Category;
import frapppyz.cutefurry.pics.modules.Mod;
import frapppyz.cutefurry.pics.util.PacketUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;

import java.util.ArrayList;

public class Replica extends Mod {
    private Packet p;
    private ArrayList<Packet> packets = new ArrayList<>();
    public Replica() {
        super("Blink", "Blinks lol.", 0, Category.PLAYER);
    }

    public void onDisable(){
        mc.timer.timerSpeed = 1;
        packets.forEach(PacketUtil::sendPacketNoEvent);
        packets.clear();
    }

    public void onEvent(Event e){
        if(e instanceof SendPacket){
                e.setCancelled(true);
                packets.add(((SendPacket) e).getPacket());
        }
    }
}
