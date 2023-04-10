package crispy.features.hacks.impl.combat;

import crispy.features.event.Event;
import crispy.features.event.impl.player.EventPacket;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.util.player.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.superblaubeere27.valuesystem.ModeValue;
import net.superblaubeere27.valuesystem.NumberValue;

import java.util.ArrayList;

@HackInfo(name = "Velocity", category = Category.COMBAT)
public class Velocity extends Hack {
    ModeValue mode = new ModeValue("Velocity Mode", "Null", "Null", "Delayed", "Burst", "Custom", "Mineman");
    NumberValue<Long> delay = new NumberValue<Long>("Delay", 100L, 10L, 1000L, () -> mode.getMode().equalsIgnoreCase("Delayed"));

    NumberValue<Double> horizontal = new NumberValue<Double>("Horizontal", 0.5D, 0D, 1D, () -> mode.getMode().equalsIgnoreCase("Custom"));
    NumberValue<Double> vertical = new NumberValue<Double>("Vertical", 1D, 0D, 1D, () -> mode.getMode().equalsIgnoreCase("Custom"));
    private final ArrayList<Packet> packets = new ArrayList<>();

    private int ticks;
    @Override
    public void onDisable() {
        if (Minecraft.theWorld != null) {
            tacticalNuke();
        }
        super.onDisable();
    }

    public void tacticalNuke() {
        if (!packets.isEmpty()) {
            packets.forEach(packets -> packets.processPacket(mc.getNetHandler()));
            packets.clear();
        }


    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventPacket) {
            Packet packet = ((EventPacket) e).getPacket();

            if (packet instanceof S12PacketEntityVelocity) {
                S12PacketEntityVelocity s12PacketEntityVelocity = (S12PacketEntityVelocity) packet;
                if (((S12PacketEntityVelocity) packet).getEntityID() == mc.thePlayer.getEntityId()) {

                    if (mode.getMode().equalsIgnoreCase("Null")) {
                        e.setCancelled(true);
                    } else if (mode.getMode().equalsIgnoreCase("Delayed")) {
                        e.setCancelled(true);
                        new Thread(() -> {
                            try {
                                Thread.sleep(delay.getObject());
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                            }
                            packet.processPacket(mc.getNetHandler());
                        }).start();
                    } else if (mode.getMode().equalsIgnoreCase("Burst")) {
                        e.setCancelled(true);
                        packets.add(((EventPacket) e).getPacket());
                    }
                    if (mode.getMode().equalsIgnoreCase("Custom")) {
                        ((S12PacketEntityVelocity) packet).motionZ = (int) (((S12PacketEntityVelocity) packet).getMotionZ() * horizontal.getObject());
                        ((S12PacketEntityVelocity) packet).motionY = (int) (((S12PacketEntityVelocity) packet).getMotionY() * vertical.getObject());
                        ((S12PacketEntityVelocity) packet).motionX = (int) (((S12PacketEntityVelocity) packet).getMotionX() * horizontal.getObject());

                    } else if (mode.getMode().equalsIgnoreCase("Mineman")) {
                        ticks++;
                        e.setCancelled(true);
                        if (ticks > 2) {
                            e.setCancelled(false);
                            ticks = 0;
                        }
                    }


                }
            }
            if(packet instanceof S27PacketExplosion) {
                if(mode.getMode().equalsIgnoreCase("Null")) {
                    e.setCancelled(true);
                }
            }
        } else if (e instanceof EventUpdate) {
            setDisplayName(getName() + " \2477" + mode.getMode());
            if (mode.getMode().equalsIgnoreCase("Burst") && PlayerUtil.isMoving2()) {
                tacticalNuke();
            }
        }
    }
}
