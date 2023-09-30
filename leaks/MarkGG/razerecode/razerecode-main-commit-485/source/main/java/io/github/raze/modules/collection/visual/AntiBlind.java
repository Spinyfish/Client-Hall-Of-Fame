package io.github.raze.modules.collection.visual;

import io.github.raze.events.collection.motion.EventMotion;
import io.github.raze.events.system.SubscribeEvent;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.modules.system.information.ModuleCategory;
import net.minecraft.potion.Potion;

public class AntiBlind extends BaseModule {

    public AntiBlind() {
        super("AntiBlind", "Disable blindness effects", ModuleCategory.VISUAL);
    }

    @SubscribeEvent
    private void onMotion(EventMotion eventMotion) {
        if (mc.thePlayer.getActivePotionEffect(Potion.blindness) != null)
            mc.thePlayer.removePotionEffect(Potion.blindness.getId());
        if (mc.thePlayer.getActivePotionEffect(Potion.confusion) != null)
            mc.thePlayer.removePotionEffect(Potion.confusion.getId());
    }

}
