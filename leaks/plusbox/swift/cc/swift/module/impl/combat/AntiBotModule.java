package cc.swift.module.impl.combat;

import cc.swift.events.EventState;
import cc.swift.events.PacketEvent;
import cc.swift.module.Module;
import cc.swift.value.impl.DoubleValue;
import cc.swift.value.impl.ModeValue;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S14PacketEntity;

import java.util.ArrayList;

public class AntiBotModule extends Module {
    private final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.values());
    private final DoubleValue ticks = new DoubleValue("Ticks", 100D, 50, 2000, 50).setDependency(() -> mode.getValue() == Mode.TICKS);
    private final ArrayList<Entity> legitPlayers = new ArrayList<>();
    public AntiBotModule() {
        super("AntiBot", Category.COMBAT);

        this.registerValues(mode, ticks);
    }

    public boolean isBot(Entity entity) {
        if (!this.isEnabled()) return false;
        //ik this is stupid bc it'll call the method more than it needs to but whatever i'm lazy rn lmfao if someone has an issue you can fix it yourself or remind me XD
        if (!(entity instanceof EntityPlayer))
            return false;

        switch (mode.getValue()) {
            case TICKS:
                if (entity.ticksExisted < ticks.getValue().intValue()) {
                    return true;
                }
                break;
            case PACKET:
                return !legitPlayers.contains(entity);
        }

        return false;
    }

    @Handler
    Listener<PacketEvent> packetEventListener = event -> {
        if (event.getDirection() != EventState.RECEIVE) return;
        if (event.getPacket() instanceof S14PacketEntity.S17PacketEntityLookMove) {
            if (((S14PacketEntity) event.getPacket()).getOnGround()) {
                legitPlayers.add(((S14PacketEntity) event.getPacket()).getEntity(mc.theWorld));
            }
        }
    };

    public enum Mode {
        TICKS, PACKET
    }
}
