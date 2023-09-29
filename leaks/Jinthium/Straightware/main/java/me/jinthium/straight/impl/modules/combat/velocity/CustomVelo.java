package me.jinthium.straight.impl.modules.combat.velocity;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.modules.combat.Test;
import me.jinthium.straight.impl.modules.combat.Velocity;
import me.jinthium.straight.impl.settings.NumberSetting;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

@ModeInfo(name = "Custom", parent = Velocity.class)
public class CustomVelo extends ModuleMode<Velocity> {

    private final NumberSetting horizontal = new NumberSetting("Horizontal", 100, 0, 100, 1);
    private final NumberSetting vertical = new NumberSetting("Vertical", 100, 0, 100, 1);

    public CustomVelo() {

        this.registerSettings(horizontal, vertical);
    }

    @Callback
    final EventCallback<PacketEvent> packetEventCallback = event -> {
        if(event.getPacketState() == PacketEvent.PacketState.RECEIVING){
            float horizontal = this.horizontal.getValue().floatValue();
            float vertical = this.vertical.getValue().floatValue();
            if (event.getPacket() instanceof S12PacketEntityVelocity s12) {

                if (s12.getEntityID() == mc.thePlayer.getEntityId()) {
                    if (horizontal == 0) {
                        event.setCancelled(true);

                        if (vertical != 0) {
                            mc.thePlayer.motionY = s12.getMotionY() / 8000.0D;
                        }
                        return;
                    }

                    s12.motionX *= horizontal / 100;
                    s12.motionY *= vertical / 100;
                    s12.motionZ *= horizontal / 100;

                    event.setPacket(s12);

                }
            }
            if (event.getPacket() instanceof S27PacketExplosion s27) {

                if (horizontal == 0 && vertical == 0) {
                    event.setCancelled(true);
                    return;
                }

                s27.posX *= horizontal / 100;
                s27.posY *= vertical / 100;
                s27.posZ *= horizontal / 100;

                event.setPacket(s27);
            }
        }
    };
}
