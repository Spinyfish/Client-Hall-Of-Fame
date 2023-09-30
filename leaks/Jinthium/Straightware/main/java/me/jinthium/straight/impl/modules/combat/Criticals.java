package me.jinthium.straight.impl.modules.combat;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.modules.combat.criticals.EditCrits;
import me.jinthium.straight.impl.modules.combat.criticals.NoGroundCrits;
import me.jinthium.straight.impl.modules.combat.criticals.PacketCrits;
import me.jinthium.straight.impl.modules.combat.criticals.WatchdogCrits;
import me.jinthium.straight.impl.settings.ModeSetting;
import me.jinthium.straight.impl.settings.NumberSetting;
import me.jinthium.straight.impl.utils.misc.TimerUtil;
import me.jinthium.straight.impl.utils.network.PacketUtil;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;

public class Criticals extends Module {

    public Criticals(){
        super("Criticals", Category.COMBAT);
        this.registerModes(
                new WatchdogCrits(),
                new EditCrits(),
                new PacketCrits(),
                new NoGroundCrits()
        );
    }

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventCallback = event ->  {
        this.setSuffix(this.getCurrentMode().getInformationSuffix());
    };
}
