package dev.africa.pandaware.impl.module.combat.velocity.modes;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.event.player.UpdateEvent;
import dev.africa.pandaware.impl.module.combat.velocity.VelocityModule;
import dev.africa.pandaware.impl.setting.NumberSetting;
import lombok.var;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class TickVelocity extends ModuleMode<VelocityModule> {
    private final NumberSetting horizonal = new NumberSetting("Horizontal", 100, 0,
            100, 0.01);
    private final NumberSetting vertical = new NumberSetting("Vertical", 100, 0,
            100, 0.01);

    private final NumberSetting landingTick = new NumberSetting("Landing tick", 20, 0,
            2, 1);

    public TickVelocity(String name, VelocityModule parent) {
        super(name, parent);

        this.registerSettings(
                this.horizonal,
                this.vertical,
                this.landingTick
        );
    }

    private int ticks;

    @Override
    public void onEnable() {
        this.ticks = 21;
    }

    @EventHandler
    EventCallback<UpdateEvent> onUpdate = event -> {
        if (this.ticks == this.landingTick.getValue().intValue()) {
            mc.thePlayer.motionX *= (this.horizonal.getValue().floatValue() / 100f);
            mc.thePlayer.motionY *= (this.vertical.getValue().floatValue() / 100f);
            mc.thePlayer.motionZ *= (this.horizonal.getValue().floatValue() / 100f);
        }

        this.ticks += this.ticks <= 20 ? 1 : 0;
    };

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (mc.thePlayer != null) {
            if (event.getPacket() instanceof S12PacketEntityVelocity) {
                var velocity = (S12PacketEntityVelocity) event.getPacket();
                if (velocity.getEntityID() == mc.thePlayer.getEntityId() && (velocity.getMotionX() > 0.1 || velocity.getMotionZ() > 0.1)) {
                    this.ticks = 0;
                }
            }
        }
    };
}
