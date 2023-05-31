package de.lirium.impl.module.impl.combat;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.setting.Dependency;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.ComboBox;
import de.lirium.base.setting.impl.SliderSetting;
import de.lirium.impl.events.PacketEvent;
import de.lirium.impl.module.ModuleFeature;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;

@ModuleFeature.Info(name = "Velocity", description = "Take less velocity", category = ModuleFeature.Category.COMBAT)
public class VelocityFeature extends ModuleFeature {

    @Value(name = "Mode")
    private final ComboBox<String> mode = new ComboBox<>("Cancel", new String[]{"Packet"});

    @Value(name = "Horizontal")
    private final SliderSetting<Integer> horizontal = new SliderSetting<>(100, -100, 100, new Dependency<>(mode, "Packet"));
    @Value(name = "Vertical")
    private final SliderSetting<Integer> vertical = new SliderSetting<>(100, -100, 100, new Dependency<>(mode, "Packet"));

    @EventHandler
    public final Listener<PacketEvent> packetEvent = e -> {
        switch (mode.getValue()) {
            case "Packet":
                if (e.packet instanceof SPacketEntityVelocity) {
                    final SPacketEntityVelocity velocity = (SPacketEntityVelocity) e.packet;
                    if (velocity.getEntityID() == getPlayer().getEntityId()) {
                        double motionX = getPlayer().motionX, motionY = getPlayer().motionY = getPlayer().motionY, motionZ = getPlayer().motionZ;
                        if (horizontal.getValue() != 0) {
                            motionX = (double) velocity.getMotionX() / 8000.0D * horizontal.getValue() / 100;
                            motionZ = (double) velocity.getMotionZ() / 8000.0D * horizontal.getValue() / 100;
                        }
                        if (vertical.getValue() != 0) {
                            motionY = (double) velocity.getMotionY() / 8000.0D * vertical.getValue() / 100;
                        }
                        getPlayer().setVelocity(motionX, motionY, motionZ);
                        e.setCancelled(true);
                    }
                }
                break;
            case "Cancel":
                if (e.packet instanceof SPacketEntityVelocity) {
                    final SPacketEntityVelocity velocity = (SPacketEntityVelocity) e.packet;
                    if (velocity.getEntityID() == getPlayer().getEntityId()) {
                        e.setCancelled(true);
                    }
                }
                if (e.packet instanceof SPacketExplosion) {
                    e.setCancelled(true);
                }
                break;
        }
    };
}
