package Reality.Realii.mods.modules.combat;

import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventPacketRecieve;
import Reality.Realii.event.value.Numbers;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;


public class AntiKb
extends Module {
    private Numbers<Number> yt = new Numbers<Number>("y", "y", 0.0, 0.0, 100.0, 5.0);
    private Numbers<Number> vertical = new Numbers<Number>("vertical", "vertical", 0.0, 0.0, 100.0, 5.0);
    private Numbers<Number> horizontal = new Numbers<Number>("horizontal", "horizontal", 0.0, 0.0, 100.0, 5.0);

    public AntiKb() {
        super("AntiKb", ModuleType.Combat);
        this.addValues(yt , horizontal, vertical);
    }

    @EventHandler
    private void onPacket(EventPacketRecieve e) {
        if (e.getPacket() instanceof S12PacketEntityVelocity || e.getPacket() instanceof S27PacketExplosion) {

                S12PacketEntityVelocity packet = (S12PacketEntityVelocity)e.getPacket();
                packet.motionX = (int)(this.vertical.getValue().floatValue());
                packet.motionY = (int)(this.vertical.getValue().floatValue());
                packet.motionZ = (int)(this.horizontal.getValue().floatValue());
            }
        }
    }


