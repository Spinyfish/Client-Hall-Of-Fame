/**
 * @project Myth
 * @author CodeMan
 * @at 07.08.22, 23:11
 */
package dev.myth.features.combat;

import dev.codeman.eventbus.EventPriority;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.event.EventState;
import dev.myth.api.feature.Feature;
import dev.myth.events.PacketEvent;
import dev.myth.events.UpdateEvent;
import dev.myth.settings.EnumSetting;
import dev.myth.settings.NumberSetting;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

@Feature.Info(
        name = "Velocity",
        description = "Allows you avoid taking velocity",
        category = Feature.Category.COMBAT
)
public class VelocityFeature extends Feature {

    /** modes */
    public final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.BASIC);

    public final NumberSetting horizontal = new NumberSetting("Horizontal", 0, 0, 100, 1).setSuffix("%");
    public final NumberSetting vertical = new NumberSetting("Vertical", 0, 0, 100, 1).setSuffix("%");

    /** values */
    public boolean velocity;

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {

    };


    @Handler(EventPriority.LOWEST)
    public final Listener<PacketEvent> packetEventListener = event -> {

        switch (mode.getValue()) {
            case ZONECRAFT:
            case BASIC:
                if(event.getState() == EventState.RECEIVING) {
                    if(event.getPacket() instanceof S12PacketEntityVelocity) {
                        S12PacketEntityVelocity packet = event.getPacket();

                        if(packet.getEntityID() == getPlayer().getEntityId()) {
                            if (horizontal.getValue() == 0 && vertical.getValue() == 0) {
                                event.setCancelled(true);
                                return;
                            }

                            packet.setMotionX((int) (packet.getMotionX() * horizontal.getValue() / 100));
                            packet.setMotionZ((int) (packet.getMotionZ() * horizontal.getValue() / 100));
                            packet.setMotionY((int) (packet.getMotionY() * vertical.getValue() / 100));
                        }
                    }
                    if (event.getPacket() instanceof S27PacketExplosion) {
                        S27PacketExplosion packet = event.getPacket();
                        if (horizontal.getValue() == 0 && vertical.getValue() == 0) {
                            event.setCancelled(true);
                            return;
                        }
                        packet.setMotionX((int) (packet.getMotionX() * horizontal.getValue() / 100));
                        packet.setMotionZ((int) (packet.getMotionZ() * horizontal.getValue() / 100));
                        packet.setMotionY((int) (packet.getMotionY() * vertical.getValue() / 100));
                    }
                } else {
                    if(!mode.is(Mode.ZONECRAFT)) return;
                    if(getPlayer() == null) return;
                    if (getPlayer().hurtTime > 0 && event.getPacket() instanceof C0FPacketConfirmTransaction) {
                        event.setCancelled(true);
                    }
                }

                break;

        }
    };

    @Override
    public String getSuffix() {
        return !mode.is(Mode.BASIC) ? mode.getValue().toString() : horizontal.getValueDisplayString() + " " + vertical.getValueDisplayString();
    }

    public enum Mode {
        BASIC("Basic"),
        ZONECRAFT("Zonecraft");

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
