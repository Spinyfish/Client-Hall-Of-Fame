package cc.swift.module.impl.player;

import cc.swift.events.EventState;
import cc.swift.events.UpdateWalkingPlayerEvent;
import cc.swift.module.Module;
import cc.swift.util.player.MovementUtil;
import cc.swift.value.impl.BooleanValue;
import cc.swift.value.impl.ModeValue;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

public final class NoFallModule extends Module {

    public final ModeValue<NoFallMode> mode = new ModeValue<>("Mode", NoFallMode.values());
    public final BooleanValue moving = new BooleanValue("Moving", false).setDependency(() -> this.mode.getValue() == NoFallMode.PACKET);
    public final BooleanValue rotating = new BooleanValue("Rotating", false).setDependency(() -> this.mode.getValue() == NoFallMode.PACKET);

    private double lastFallDistance;

    public NoFallModule() {
        super("NoFall", Category.PLAYER);
        this.registerValues(this.mode, this.moving, this.rotating);
    }

    @Handler
    public final Listener<UpdateWalkingPlayerEvent> updateWalkingPlayerEventListener = event -> {
        if (event.getState() != EventState.PRE) return;

        if (MovementUtil.isOnGround())
            this.lastFallDistance = 0;

        switch (mode.getValue()) {
            case SPOOF:
                if (mc.thePlayer.fallDistance - this.lastFallDistance >= 3.0) {
                    this.lastFallDistance = mc.thePlayer.fallDistance;
                    event.setOnGround(true);
                }
                break;
            case PACKET:
                if (mc.thePlayer.fallDistance - this.lastFallDistance >= 3.0) {
                    this.lastFallDistance = mc.thePlayer.fallDistance;
                    Packet<?> packet;
                    if (moving.getValue() && rotating.getValue())
                        packet = new C03PacketPlayer.C06PacketPlayerPosLook(event.getX(), event.getY(), event.getZ(), event.getYaw(), event.getPitch(), true);
                    else if (moving.getValue())
                        packet = new C03PacketPlayer.C04PacketPlayerPosition(event.getX(), event.getY(), event.getZ(), true);
                    else if (rotating.getValue())
                        packet = new C03PacketPlayer.C05PacketPlayerLook(event.getYaw(), event.getPitch(), true);
                    else
                        packet = new C03PacketPlayer(true);

                    mc.thePlayer.sendQueue.addToSendQueue(packet);
                }
                break;
        }

    };

    enum NoFallMode {
        SPOOF, PACKET
    }
}
