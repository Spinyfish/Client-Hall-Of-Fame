package dev.africa.pandaware.impl.module.combat.velocity.modes;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.module.combat.velocity.VelocityModule;
import dev.africa.pandaware.impl.setting.EnumSetting;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.utils.player.MovementUtils;
import lombok.AllArgsConstructor;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class ReverseVelocity extends ModuleMode<VelocityModule> {
    private final EnumSetting<ReverseMode> mode = new EnumSetting<>("Mode", ReverseMode.INSTANT);
    private final NumberSetting reverse = new NumberSetting("Reverse", 1, 0, 0.2, 0.1);

    public ReverseVelocity(String name, VelocityModule parent) {
        super(name, parent);

        this.registerSettings(
                this.mode,
                this.reverse
        );
    }

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (mc.thePlayer != null) {
            if (event.getPacket() instanceof S12PacketEntityVelocity && ((S12PacketEntityVelocity) event.getPacket())
                    .getEntityID() == mc.thePlayer.getEntityId()) {
                switch (this.mode.getValue()) {
                    case INSTANT:
                        if (mc.thePlayer.hurtTime > 0) {
                            MovementUtils.strafe(MovementUtils.getSpeed() * this.reverse.getValue().floatValue());
                        }
                        break;

                    case SMOOTH:
                        if (mc.thePlayer.onGround && mc.thePlayer.hurtTime > 0) {
                            mc.thePlayer.speedInAir = this.reverse.getValue().floatValue() * 0.1f;
                        } else {
                            mc.thePlayer.speedInAir = 0.02f;
                        }
                        break;
                }
            }
        }
    };

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (!(mc.thePlayer.hurtTime > 0)) {
            mc.thePlayer.speedInAir = 0.02f;
        }
    };

    @AllArgsConstructor
    public enum ReverseMode {
        INSTANT("Instant"),
        SMOOTH("Smooth");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }
}
