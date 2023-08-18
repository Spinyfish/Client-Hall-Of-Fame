package me.jinthium.straight.impl.modules.combat;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.modules.combat.velocity.CancelVelo;
import me.jinthium.straight.impl.modules.combat.velocity.CustomVelo;
import me.jinthium.straight.impl.modules.combat.velocity.ReverseVelo;
import me.jinthium.straight.impl.modules.combat.velocity.WatchdogVelo;
import me.jinthium.straight.impl.settings.ModeSetting;
import me.jinthium.straight.impl.settings.NumberSetting;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

public class Velocity extends Module {

    public Velocity(){
        super("Velocity", Category.COMBAT);
        this.registerModes(
                new CancelVelo(),
                new CustomVelo(),
                new ReverseVelo(),
                new WatchdogVelo()
        );
    }

    @Callback
    final EventCallback<PacketEvent> packetEventCallback = event -> {
        this.setSuffix(this.getCurrentMode().getInformationSuffix());
    };
}
