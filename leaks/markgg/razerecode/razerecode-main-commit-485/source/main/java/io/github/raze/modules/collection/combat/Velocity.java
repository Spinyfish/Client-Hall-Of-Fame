package io.github.raze.modules.collection.combat;

import io.github.raze.Raze;
import io.github.raze.events.collection.network.EventPacketReceive;
import io.github.raze.events.system.SubscribeEvent;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.modules.system.information.ModuleCategory;
import io.github.raze.settings.collection.ArraySetting;
import io.github.raze.settings.collection.BooleanSetting;
import io.github.raze.settings.collection.NumberSetting;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

public class Velocity extends BaseModule {

    public ArraySetting mode;
    public NumberSetting horizontal, vertical;
    public BooleanSetting explosions;

    public Velocity() {
        super("Velocity", "Changes your knockback.", ModuleCategory.COMBAT);

        Raze.INSTANCE.MANAGER_REGISTRY.SETTING_REGISTRY.register(

                mode = new ArraySetting(this, "Mode", "Packet", "Packet", "Cancel", "Motion"),

                horizontal = new NumberSetting(this, "Horizontal", 0, 100, 0),
                vertical = new NumberSetting(this, "Vertical", 0, 100, 0),

                explosions = new BooleanSetting(this, "Explosion Velocity", false)

        );

    }

    @SubscribeEvent
    private void onPacketReceived(EventPacketReceive event) {
        if (event.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();

            switch (mode.get()) {
                case "Packet":
                    packet.setMotionX(horizontal.get().intValue() / 100);
                    packet.setMotionY(vertical.get().intValue() / 100);
                    packet.setMotionZ(horizontal.get().intValue() / 100);
                    break;

                case "Cancel":
                    event.setCancelled(true);
                    break;

                case "Motion":
                    mc.thePlayer.motionX = mc.thePlayer.motionX / horizontal.get().intValue() / 100;
                    mc.thePlayer.motionY = mc.thePlayer.motionX / vertical.get().intValue() / 100;
                    mc.thePlayer.motionZ = mc.thePlayer.motionX / horizontal.get().intValue() / 100;
                break;
            }
        }
        if(event.getPacket() instanceof S27PacketExplosion) {
            S27PacketExplosion packet = (S27PacketExplosion) event.getPacket();
            if(explosions.get()) {
                switch (mode.get()) {
                    case "Packet":
                        packet.setMotionX(horizontal.get().intValue() / 100);
                        packet.setMotionY(vertical.get().intValue() / 100);
                        packet.setMotionZ(horizontal.get().intValue() / 100);
                        break;

                    case "Cancel":
                        event.setCancelled(true);
                        break;

                    case "Motion":
                        mc.thePlayer.motionX = mc.thePlayer.motionX / horizontal.get().intValue() / 100;
                        mc.thePlayer.motionY = mc.thePlayer.motionX / vertical.get().intValue() / 100;
                        mc.thePlayer.motionZ = mc.thePlayer.motionX / horizontal.get().intValue() / 100;
                        break;
                }
            }
        }
    }


}
