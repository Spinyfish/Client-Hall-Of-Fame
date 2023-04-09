package tech.dort.dortware.impl.modules.render;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.client.C02PacketUseEntity;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.impl.events.PacketEvent;

/**
 * @author Auth
 */

public class MoreParticles extends Module {

    private final NumberValue amount = new NumberValue("Amount", this, 3, 1, 10, true);

    public MoreParticles(ModuleData moduleData) {
        super(moduleData);
        register(amount);
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        switch (event.getPacketDirection()) {
            case INBOUND:
                break;
            case OUTBOUND:
                if (event.getPacket() instanceof C02PacketUseEntity) {
                    C02PacketUseEntity packetUseEntity = event.getPacket();
                    if (packetUseEntity.getAction() == C02PacketUseEntity.Action.ATTACK) {
                        for (int i = 0; i < amount.getValue(); i++) {
                            mc.thePlayer.onCriticalHit(packetUseEntity.getEntityFromWorld(mc.theWorld));
                        }
                    }
                }
                break;
        }
    }
}