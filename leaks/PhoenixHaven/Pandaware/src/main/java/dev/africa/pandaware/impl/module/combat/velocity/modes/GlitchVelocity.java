package dev.africa.pandaware.impl.module.combat.velocity.modes;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.module.combat.velocity.VelocityModule;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.utils.math.MathUtils;
import dev.africa.pandaware.utils.player.PlayerUtils;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;

public class GlitchVelocity extends ModuleMode<VelocityModule> {
    private final BooleanSetting poison = new BooleanSetting("Return on poison", false);

    public GlitchVelocity(String name, VelocityModule parent) {
        super(name, parent);
    }

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (mc.thePlayer != null) {
            if (mc.thePlayer.isPotionActive(Potion.poison) || mc.thePlayer.getGroundTicks() <= Math.round((PlayerUtils.getPing(mc.thePlayer) / 50) + 1)) return;
            if (event.getPacket() instanceof S12PacketEntityVelocity && ((S12PacketEntityVelocity) event.getPacket())
                    .getEntityID() == mc.thePlayer.getEntityId()) {
                event.cancel();
                mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX, mc.thePlayer.posY - 0.1, mc.thePlayer.posZ);
            }
        }
    };
}
