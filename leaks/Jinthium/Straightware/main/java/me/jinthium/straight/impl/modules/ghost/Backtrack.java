package me.jinthium.straight.impl.modules.ghost;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.event.game.PlayerAttackEvent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.settings.NumberSetting;
import me.jinthium.straight.impl.utils.EvictingList;
import me.jinthium.straight.impl.utils.player.RotationUtils;
import me.jinthium.straight.impl.utils.vector.Vector2f;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.MovingObjectPosition;

public class Backtrack extends Module {

    private final NumberSetting backtrack = new NumberSetting("Amount", 1, 1, 20, 1);
    private EvictingList<Vector2f> previousRotations = new EvictingList<>(1);
    private boolean attacked;
    private int lastSize;

    public Backtrack(){
        super("Backtrack", Category.GHOST);
        this.addSettings(backtrack);
    }

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventEventCallback = event -> {
        if(!event.isPre())
            return;

        if (lastSize != backtrack.getValue().intValue()) {
            previousRotations = new EvictingList<>(backtrack.getValue().intValue());
            lastSize = backtrack.getValue().intValue();
        }

        previousRotations.add(new Vector2f(event.getYaw(), event.getPitch()));

        attacked = false;
    };

    @Callback
    final EventCallback<PacketEvent> packetEventEventCallback = event -> {
        if(event.getPacketState() != PacketEvent.PacketState.SENDING)
            return;

        final Packet<?> packet = event.getPacket();

        if (packet instanceof C0APacketAnimation && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.MISS) {
            for (final Vector2f rotation : previousRotations) {
                final MovingObjectPosition movingObjectPosition = RotationUtils.rayCast(rotation, 3.0D);

                if (movingObjectPosition.entityHit != null && !attacked) {
                    final PlayerAttackEvent e = new PlayerAttackEvent(movingObjectPosition.entityHit);
                    Client.INSTANCE.getPubSubEventBus().publish(e);

                    if (e.isCancelled()) return;
                    mc.playerController.attackEntity(mc.thePlayer, movingObjectPosition.entityHit);
                }
            }
        }
    };

    @Callback
    final EventCallback<PlayerAttackEvent> playerAttackEventEventCallback = event -> {
        if (attacked)
            event.cancel();

        attacked = true;
    };
}
