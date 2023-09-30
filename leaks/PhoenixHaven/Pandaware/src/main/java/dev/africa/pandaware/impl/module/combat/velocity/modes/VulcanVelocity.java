package dev.africa.pandaware.impl.module.combat.velocity.modes;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.module.combat.velocity.VelocityModule;
import dev.africa.pandaware.impl.setting.NumberSetting;
import lombok.var;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

public class VulcanVelocity extends ModuleMode<VelocityModule> {
    private final NumberSetting horizontal = new NumberSetting("Horizontal", 100, 0,
            100, 0.01);
    private final NumberSetting vertical = new NumberSetting("Vertical", 100, 0,
            100, 0.01);
    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (mc.thePlayer != null) {
            if (event.getPacket() instanceof S12PacketEntityVelocity && ((S12PacketEntityVelocity) event.getPacket())
                    .getEntityID() == mc.thePlayer.getEntityId()) {
                var packet = (S12PacketEntityVelocity) event.getPacket();

                packet.setMotionX((int) (packet.getMotionX() * (this.horizontal.getValue().floatValue() / 100f)));
                packet.setMotionY((int) (packet.getMotionY() * (this.vertical.getValue().floatValue() / 100f)));
                packet.setMotionZ((int) (packet.getMotionZ() * (this.horizontal.getValue().floatValue() / 100f)));

                if (this.horizontal.getValue().doubleValue() <= 0.0 && this.vertical.getValue().doubleValue() <= 0.0) {
                    event.cancel();
                }
            }

            if (event.getPacket() instanceof S27PacketExplosion) {
                var packet = (S27PacketExplosion) event.getPacket();

                packet.setField_149152_f((int) (packet.getField_149152_f()
                        * (this.horizontal.getValue().floatValue() / 100f)));
                packet.setField_149153_g((int) (packet.getField_149153_g()
                        * (this.vertical.getValue().floatValue() / 100f)));
                packet.setField_149159_h((int) (packet.getField_149159_h()
                        * (this.horizontal.getValue().floatValue() / 100f)));

                if (this.horizontal.getValue().doubleValue() <= 0.0 && this.vertical.getValue().doubleValue() <= 0.0) {
                    event.cancel();
                }
            }

            if (event.getPacket() instanceof C0FPacketConfirmTransaction && mc.thePlayer.hurtTime > 0) event.cancel();
        }
    };


    public VulcanVelocity(String name, VelocityModule parent) {
        super(name, parent);
        this.registerSettings(
                this.horizontal,
                this.vertical
        );
    }
}
