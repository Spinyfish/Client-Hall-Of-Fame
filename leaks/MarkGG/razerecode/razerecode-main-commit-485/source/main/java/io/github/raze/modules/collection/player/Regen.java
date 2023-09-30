package io.github.raze.modules.collection.player;

import io.github.raze.events.collection.motion.EventMotion;
import io.github.raze.events.system.BaseEvent;
import io.github.raze.events.system.SubscribeEvent;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.modules.system.information.ModuleCategory;
import io.github.raze.utilities.collection.visual.ChatUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Regen extends BaseModule {

    public Regen() {
        super("Regen", "Regenerate health faster", ModuleCategory.PLAYER);
    }

    @SubscribeEvent
    private void onMotion(EventMotion eventMotion) {
        if (eventMotion.getState() == BaseEvent.State.PRE) {
            if(mc.thePlayer.getHealth() < mc.thePlayer.getMaxHealth())
                for (int i = 0; i < 10; i++)
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
        }
    }

    public void onEnable() {
        ChatUtil.addChatMessage("Regen needs full hunger to work!", true);
    }

}
