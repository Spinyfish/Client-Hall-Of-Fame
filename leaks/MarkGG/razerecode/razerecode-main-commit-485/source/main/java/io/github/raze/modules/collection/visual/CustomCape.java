package io.github.raze.modules.collection.visual;

import io.github.raze.Raze;
import io.github.raze.events.collection.motion.EventMotion;
import io.github.raze.events.system.SubscribeEvent;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.modules.system.information.ModuleCategory;
import io.github.raze.settings.collection.ArraySetting;
import net.minecraft.util.ResourceLocation;

public class CustomCape extends BaseModule {

    public ArraySetting mode;

    public CustomCape() {
        super("CustomCape", "Equip a custom cape.", ModuleCategory.VISUAL);

        Raze.INSTANCE.MANAGER_REGISTRY.SETTING_REGISTRY.register(
                mode = new ArraySetting(this, "Mode", "Classic", "Classic", "Pig")
        );
    }

    @SubscribeEvent
    private void onMotion(EventMotion eventMotion) {
        mc.thePlayer.setLocationOfCape(new ResourceLocation("raze/capes/" + mode.get() + ".png"));
    }

    public void onDisable() {
        if(mc.thePlayer == null)
            return;
        mc.thePlayer.setLocationOfCape(null);
    }

}
