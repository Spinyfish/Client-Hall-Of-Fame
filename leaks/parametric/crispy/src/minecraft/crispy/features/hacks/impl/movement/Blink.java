package crispy.features.hacks.impl.movement;

import crispy.features.event.Event;
import crispy.features.event.EventDirection;
import crispy.features.event.impl.player.EventPacket;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.superblaubeere27.valuesystem.BooleanValue;

import java.util.ArrayList;
import java.util.Collections;

@HackInfo(name = "Blink", category = Category.MOVEMENT)
public class Blink extends Hack {
    private final ArrayList<Packet> packets = new ArrayList<>();
    BooleanValue reverse = new BooleanValue("Reverse", true);
    BooleanValue cancel = new BooleanValue("Cancel Incoming", false);

    @Override
    public void onDisable() {
        if(Minecraft.theWorld != null) {
            blink();
        }

        super.onDisable();
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventPacket) {

            EventPacket event = (EventPacket) e;
            if(event.getDirection() == EventDirection.Outgoing) {
                if(Minecraft.theWorld != null) {
                    if(!reverse.getObject()) {
                        packets.add(((EventPacket) e).getPacket());
                        e.setCancelled(true);
                    } else {
                        if(event.getPacket() instanceof C03PacketPlayer) {
                            packets.add(((EventPacket) e).getPacket());
                        }
                    }

                }
            }
            if(event.getDirection() == EventDirection.Incoming) {
                if(Minecraft.theWorld != null && cancel.getObject()) {
                    if(!(event.getPacket() instanceof S00PacketKeepAlive)) {
                        if(!reverse.getObject()) {
                            e.setCancelled(true);
                        }
                    }
                } else if(Minecraft.theWorld != null) {
                    if(event.getPacket() instanceof S12PacketEntityVelocity) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
    public void blink() {
        if(!reverse.getObject()) {
            packets.forEach(packets -> mc.thePlayer.sendQueue.addToSendNoEvent(packets));
        } else {
            Collections.reverse(packets);
            packets.forEach(packets -> {
                if(packets instanceof C03PacketPlayer) {
                    mc.thePlayer.sendQueue.addToSendNoEvent(packets);
                }
            });

        }

        packets.clear();
    }
}
