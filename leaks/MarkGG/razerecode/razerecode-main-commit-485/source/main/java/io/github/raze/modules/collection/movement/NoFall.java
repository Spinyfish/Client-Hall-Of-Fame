package io.github.raze.modules.collection.movement;

import io.github.raze.Raze;
import io.github.raze.events.collection.motion.EventMotion;
import io.github.raze.events.system.BaseEvent;
import io.github.raze.events.system.SubscribeEvent;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.modules.system.information.ModuleCategory;
import io.github.raze.settings.collection.ArraySetting;
import net.minecraft.network.play.client.C03PacketPlayer;

public class NoFall extends BaseModule {

    public ArraySetting mode;

    public NoFall() {
        super("NoFall", "Removes fall damage.", ModuleCategory.MOVEMENT);

        Raze.INSTANCE.MANAGER_REGISTRY.SETTING_REGISTRY.register(

                mode = new ArraySetting(this, "Mode", "Ground", "Ground", "Packet", "AAC", "Verus")

        );

    }

    @SubscribeEvent
    private void onMotion(EventMotion eventMotion) {
        if (eventMotion.getState() == BaseEvent.State.PRE) {
            switch (mode.get()) {
                case "Ground":
                    mc.getNetHandler().addToSendQueueSilent(
                            new C03PacketPlayer.C06PacketPlayerPosLook(
                                    mc.thePlayer.posX,
                                    mc.thePlayer.posY,
                                    mc.thePlayer.posZ,
                                    mc.thePlayer.rotationYaw,
                                    mc.thePlayer.rotationPitch,
                                    true
                            )
                    );
                    break;

                case "Packet":
                    if (mc.thePlayer.fallDistance > 2.0F)
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                    break;

                case "AAC":
                    if (mc.thePlayer.fallDistance >= 3) {

                        mc.timer.timerSpeed = 0.1F;
                        mc.thePlayer.motionY -= 0.965F;

                        mc.thePlayer.sendQueue.addToSendQueue(
                                new C03PacketPlayer.C04PacketPlayerPosition(
                                        mc.thePlayer.posX,
                                        mc.thePlayer.posY,
                                        mc.thePlayer.posZ,
                                        true
                                )
                        );

                    } else
                        mc.timer.timerSpeed = 1.0F;
                    break;

                case "Verus":
                    if(mc.thePlayer.fallDistance - mc.thePlayer.motionY > 3) {
                        mc.thePlayer.motionY = 0.0;
                        mc.thePlayer.fallDistance = 0.0f;
                        mc.thePlayer.motionX *= 0.6;
                        mc.thePlayer.motionZ *= 0.6;
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                    }
                    break;
            }
        }
    }

}