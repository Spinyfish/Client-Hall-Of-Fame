/**
 * @project hakarware
 * @author CodeMan
 * @at 27.07.23, 18:42
 */

package cc.swift.module.impl.player;

import cc.swift.events.EventState;
import cc.swift.events.PacketEvent;
import cc.swift.events.UpdateEvent;
import cc.swift.module.Module;
import cc.swift.value.impl.DoubleValue;
import cc.swift.value.impl.ModeValue;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public final class FastBreakModule extends Module {

    public final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.values());

    public final DoubleValue percentage = new DoubleValue("Percentage", 70D, 0, 100, 1).setDependency(() -> this.mode.getValue() == Mode.VANILLA);

    private BlockPos blockPos;
    private EnumFacing enumFacing;

    public FastBreakModule() {
        super("FastBreak", Category.PLAYER);
        this.registerValues(this.mode, this.percentage);
    }

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {
        if (this.mode.getValue() != Mode.VANILLA) return;

        if (mc.playerController.getCurBlockDamageMP() > this.percentage.getValue() / 100D) {
            mc.playerController.setCurBlockDamageMP(1);
            mc.playerController.onPlayerDamageBlock(this.blockPos, this.enumFacing);
        }
    };

    @Handler
    public final Listener<PacketEvent> packetEventListener = event -> {
        if (event.getDirection() != EventState.SEND || event.getState() != EventState.PRE) return;

        if (event.getPacket() instanceof C07PacketPlayerDigging) {
            C07PacketPlayerDigging packet = event.getPacket();
            if (packet.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                this.blockPos = packet.getPosition();
                this.enumFacing = packet.getFacing();
            }
        }
    };

    public enum Mode {
        VANILLA
    }
}
