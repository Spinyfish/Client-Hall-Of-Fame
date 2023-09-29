package me.jinthium.straight.impl.modules.movement.flight;

import best.azura.irc.utils.Wrapper;
import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.event.movement.PlayerMoveUpdateEvent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.modules.movement.Flight;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

@ModeInfo(name = "Watchdog", parent = Flight.class)
public class WatchdogFly extends ModuleMode<Flight> {

    private double veloY;
    private int tick;
    private boolean speedUp;

    @Override
    public void onEnable() {
        veloY = 0;
        tick = 0;
        speedUp = false;
        super.onEnable();
    }

    @Callback
    final EventCallback<PacketEvent> packetEventEventCallback = event -> {
        if(event.getPacketState() == PacketEvent.PacketState.RECEIVING){
            if(event.getPacket() instanceof S12PacketEntityVelocity s12){
                if(s12.getEntityID() != mc.thePlayer.getEntityId())
                    return;

                veloY = s12.motionY / 8000.D;
                event.cancel();
            }
        }
    };

    @Callback
    final EventCallback<PlayerMoveUpdateEvent> playerMoveUpdateEventEventCallback = event -> {
        if(!speedUp) {
            event.cancel();
            mc.thePlayer.motionX *= mc.thePlayer.motionZ *= 0;
        }
//        mc.thePlayer.motionX *= mc.thePlayer.motionZ *= 0;
//        if(speedUp) {
//            event.setFriction(2);
//            speedUp = false;
//        }
    };

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventEventCallback = event -> {
        if(event.isPre()){
            if(tick > 5)
                mc.thePlayer.motionY += 0.028;
            if(mc.thePlayer.onGround && tick < 4){
                mc.thePlayer.jump();
                tick++;
                event.setOnGround(false);
            }

            if(tick == 4) {
                mc.thePlayer.motionY = 0.42F;
                        MovementUtil.strafe(MovementUtil.getAllowedHorizontalDistance() * 1.56);
                event.setOnGround(false);
                tick++;
                speedUp = true;
            }

            if(tick == 5 && mc.thePlayer.motionY > 0.2 && mc.thePlayer.motionY < .25){
                Wrapper.sendMessage("help");
                event.setOnGround(true);
                tick++;
            }


            if(veloY > 0 && tick == 6){
                mc.thePlayer.motionY = veloY;
                tick++;
            }

            if(tick == 7){
                tick++;
            }
            MovementUtil.strafe(MovementUtil.getAllowedHorizontalDistance());
        }
    };

}
