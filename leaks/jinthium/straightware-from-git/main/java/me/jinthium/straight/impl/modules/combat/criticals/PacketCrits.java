package me.jinthium.straight.impl.modules.combat.criticals;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.modules.combat.Criticals;
import me.jinthium.straight.impl.settings.NumberSetting;
import me.jinthium.straight.impl.settings.mode.ModeInfo;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.misc.TimerUtil;
import me.jinthium.straight.impl.utils.network.PacketUtil;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;

@ModeInfo(name = "Packet", parent = Criticals.class)
public class PacketCrits extends ModuleMode<Criticals> {

    private final NumberSetting delay = new NumberSetting("Delay", 500, 0, 1000, 50);
    private final TimerUtil timerUtil = new TimerUtil();
    private final double[] packetOffsets = new double[]{0.0625, 0};

    public PacketCrits() {

        this.registerSettings(delay);
    }

    @Callback
    final EventCallback<PacketEvent> packetEventCallback = event -> {
        if(event.getPacketState() == PacketEvent.PacketState.SENDING){
            if(event.getPacket() instanceof C0APacketAnimation){
                if(timerUtil.hasTimeElapsed(delay.getValue().longValue(), true) && mc.thePlayer.onGroundTicks > 2) {
                    for (final double offset : packetOffsets)
                        PacketUtil.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offset, mc.thePlayer.posZ, false));
                }
            }
        }
    };
}
