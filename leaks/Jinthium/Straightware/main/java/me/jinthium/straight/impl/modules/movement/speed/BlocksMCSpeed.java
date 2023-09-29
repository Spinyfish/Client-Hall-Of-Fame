package me.jinthium.straight.impl.modules.movement.speed;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.event.game.TeleportEvent;
import me.jinthium.straight.impl.event.movement.PlayerMoveUpdateEvent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.modules.movement.Speed;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.network.PacketUtil;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;

@ModeInfo(name = "BlocksMC", parent = Speed.class)
public class BlocksMCSpeed extends ModuleMode<Speed> {
    private boolean reset;
    private double speed;

    @Callback
    final EventCallback<PlayerMoveUpdateEvent> playerMoveUpdateEventEventCallback = event -> {
        final double base = MovementUtil.getAllowedHorizontalDistance();
        final boolean potionActive = mc.thePlayer.isPotionActive(Potion.moveSpeed);

        if (mc.thePlayer.isMoving()) {
            switch (mc.thePlayer.offGroundTicks) {
                case 0 -> {
                    mc.thePlayer.motionY = MovementUtil.getJumpHeight(0.42f);
                    speed = base * 2.15;
                }
                case 1 -> speed -= 0.8 * (speed - base);
                default -> speed -= speed / MovementUtil.BUNNY_FRICTION;
            }

            reset = false;
        } else if (!reset) {
            speed = 0;

            reset = true;
            speed = MovementUtil.getAllowedHorizontalDistance();
        }

        if (mc.thePlayer.isCollidedHorizontally) {
            speed = MovementUtil.getAllowedHorizontalDistance();
        }

        event.setSpeed((float) Math.max(speed, base), (float) (Math.random() / 2000));
    };

    @Callback
    final EventCallback<TeleportEvent> teleportEventEventCallback = event -> speed = 0;

    @Override
    public void onDisable() {
        speed = 0;
        super.onDisable();
    }

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventEventCallback = event -> {
        if(event.isPre()){
            if(!mc.thePlayer.isMoving()){
                event.setPosX(event.getPosX() + (Math.random() - 0.5) / 100);
                event.setPosZ(event.getPosZ() + (Math.random() - 0.5) / 100);
            }
            PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
        }
    };

    @Callback
    final EventCallback<PacketEvent> packetEventEventCallback = event -> {
        if(event.getPacketState() == PacketEvent.PacketState.SENDING && event.getPacket() instanceof C0BPacketEntityAction)
            event.cancel();
    };
}
