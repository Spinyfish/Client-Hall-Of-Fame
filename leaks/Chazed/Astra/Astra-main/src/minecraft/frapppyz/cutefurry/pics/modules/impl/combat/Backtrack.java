package frapppyz.cutefurry.pics.modules.impl.combat;

import frapppyz.cutefurry.pics.event.Event;
import frapppyz.cutefurry.pics.event.impl.ReceivePacket;
import frapppyz.cutefurry.pics.event.impl.SendPacket;
import frapppyz.cutefurry.pics.event.impl.Update;
import frapppyz.cutefurry.pics.modules.Category;
import frapppyz.cutefurry.pics.modules.Mod;
import frapppyz.cutefurry.pics.modules.settings.impl.Mode;
import frapppyz.cutefurry.pics.util.PacketUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S14PacketEntity;

import java.util.ArrayList;

public class Backtrack extends Mod {

    private Mode mode = new Mode("Mode", "Pulse", "Pulse", "Delay", "Minemen");
    private ArrayList<Packet> packets = new ArrayList<>();
    private ArrayList<Packet> sendingpackets = new ArrayList<>();
    public Backtrack() {
        super("Backtrack", "Back the track", 0, Category.COMBAT);
        addSettings(mode);
    }

    public void onEvent(Event e){
        if(e instanceof Update){
            if(mode.is("Pulse")){
                if(Killaura.entity == null || mc.thePlayer.ticksExisted % 12 == 0 && !packets.isEmpty()){
                    packets.forEach(PacketUtil::receivePacket);
                    packets.clear();
                }
            }

            if(mode.is("Minemen")){
                if((Killaura.entity == null) || mc.thePlayer.ticksExisted % 12 == 0 && !packets.isEmpty()){
                    packets.forEach(PacketUtil::receivePacket);
                    packets.clear();
                }
                if(Killaura.entity == null || mc.thePlayer.ticksExisted % 5 == 0 && !sendingpackets.isEmpty()){
                    sendingpackets.forEach(PacketUtil::sendPacketNoEvent);
                    sendingpackets.clear();
                }
            }
        }
        if(e instanceof SendPacket){
            if(mode.is("Minemen") && Killaura.entity != null){
                e.setCancelled(true);
                sendingpackets.add(((SendPacket) e).getPacket());
            }
        }

        if(e instanceof ReceivePacket){
            if(((ReceivePacket) e).getPacket() instanceof S14PacketEntity && Killaura.entity != null){
                if(mode.is("Pulse") || mode.is("Minemen")){
                    e.setCancelled(true);
                    packets.add(((ReceivePacket) e).getPacket());
                }

                if(mode.is("Delay")){
                    e.setCancelled(true);
                    PacketUtil.receiveDelayedPacket(((ReceivePacket) e).getPacket(), 350);
                }
            }
        }
    }
}
