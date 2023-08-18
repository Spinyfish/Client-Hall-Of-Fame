package me.jinthium.straight.impl.modules.combat.criticals;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.modules.combat.Criticals;
import me.jinthium.straight.impl.settings.NumberSetting;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.misc.TimerUtil;
import net.minecraft.network.play.client.C0APacketAnimation;

@ModeInfo(name = "Edit", parent = Criticals.class)
public class EditCrits extends ModuleMode<Criticals> {

    private final NumberSetting delay = new NumberSetting("Delay", 500, 0, 1000, 50);
    private final TimerUtil timerUtil = new TimerUtil();
    private boolean attacked;
    private int ticks;
    private final double[] editOffsets = new double[]{0.0005D, 0.0001D};

    public EditCrits(){

        this.registerSettings(delay);
    }

    @Callback
    final EventCallback<PacketEvent> packetEventCallback = event -> {
        if(event.getPacketState() == PacketEvent.PacketState.SENDING){
            if(event.getPacket() instanceof C0APacketAnimation){
                if (mc.thePlayer.onGround && !mc.thePlayer.isOnLadder() && timerUtil.hasTimeElapsed(delay.getValue().longValue(), true))
                    attacked = true;
            }
        }
    };

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventCallback = event -> {
        if(event.isPre()){
            if (mc.thePlayer.onGround && attacked) {
                ticks++;

                switch (ticks) {
                    case 1 -> event.setPosY(event.getPosY() + editOffsets[0]);
                    case 2 -> {
                        event.setPosY(event.getPosY() + editOffsets[1]);
                        attacked = false;
                    }
                }

                event.setOnGround(false);
            } else {
                attacked = false;
                ticks = 0;
            }
        }
    };
}
