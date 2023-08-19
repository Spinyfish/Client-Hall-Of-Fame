/**
 * @project hakarware
 * @author CodeMan
 * @at 24.07.23, 23:17
 */

package cc.swift.module.impl.combat;

import cc.swift.Swift;
import cc.swift.events.EventState;
import cc.swift.events.PacketEvent;
import cc.swift.events.UpdateEvent;
import cc.swift.module.Module;
import cc.swift.module.impl.movement.LongJumpModule;
import cc.swift.module.impl.movement.SpeedModule;
import cc.swift.value.impl.BooleanValue;
import cc.swift.value.impl.DoubleValue;
import cc.swift.value.impl.ModeValue;
import com.sun.org.apache.xpath.internal.operations.Mod;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.EnumFacing;


public final class VelocityModule extends Module {
    public final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.values());

    public final BooleanValue experimental = new BooleanValue("Experimental", false).setDependency(() -> this.mode.getValue() == Mode.HYPIXEL);

    public final DoubleValue horizontal = new DoubleValue("Horizontal", 0d, 0, 100, 1).setDependency(() -> this.mode.getValue() == Mode.PERCENT);
    public final DoubleValue vertical = new DoubleValue("Vertical", 0d, 0, 100, 1).setDependency(() -> this.mode.getValue() == Mode.PERCENT);

    private boolean tookDamage;

    private int ticksSinceVelocity;

    public VelocityModule() {
        super("Velocity", Module.Category.COMBAT);
        this.registerValues(this.mode, this.experimental, this.horizontal, this.vertical);
    }

    @Handler
    public final Listener<PacketEvent> packetEventListener = event -> {
        if (event.getDirection() != EventState.RECEIVE || event.getState() != EventState.PRE) return;

        if (event.getPacket() instanceof S19PacketEntityStatus) {
            S19PacketEntityStatus packet = event.getPacket();
            if (packet.getOpCode() == 2) {
                tookDamage = true;
            }
        }

        if (!((event.getPacket() instanceof S12PacketEntityVelocity))) return;
        S12PacketEntityVelocity packet = event.getPacket();
        if (packet.getEntityID() != mc.thePlayer.getEntityId()) return;

        if (!tookDamage) return;
        tookDamage = false;

        ticksSinceVelocity = 0;

        switch (mode.getValue()) {
            case GRIM:
            case CANCEL:
                event.setCancelled(true);
                break;

            case HYPIXEL:
                if(Swift.INSTANCE.getModuleManager().getModule(LongJumpModule.class).isEnabled()) return;


                event.setCancelled(true);

                if (this.experimental.getValue()) {
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.motionY = 0.01;
                        mc.thePlayer.onGround = false;
                    } else {
                        mc.thePlayer.motionY += 0.03;
                    }
                } else {
                    if (!mc.thePlayer.onGround)
                        mc.thePlayer.motionY += 0.01;
                    else
                        mc.thePlayer.motionY = packet.getMotionY() / 8000.0D;
                }

                break;

            case PERCENT:
                if(horizontal.getValue() == 0){
                    event.setCancelled(true);

                    if (vertical.getValue() != 0) {
                        mc.thePlayer.motionY = (packet.getMotionY() / 8000.0D) * (vertical.getValue() / 100);
                    }
                    return;
                }
                packet.setMotionX((int) (packet.getMotionX() * (horizontal.getValue() / 100)));
                packet.setMotionZ((int) (packet.getMotionZ() * (horizontal.getValue() / 100)));
                packet.setMotionY((int) (packet.getMotionY() * (vertical.getValue() / 100)));
                break;
        }

    };

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {
        if (this.mode.getValue() == Mode.GRIM) {
            if (ticksSinceVelocity == 0) {
                mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, mc.thePlayer.getPosition(), EnumFacing.UP));
            }
        }

        ticksSinceVelocity++;
    };

    enum Mode {
        CANCEL, PERCENT, HYPIXEL, GRIM
    }
}
