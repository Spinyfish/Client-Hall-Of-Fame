package me.wavelength.baseclient.module.modules.combat;

import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;

public class Reach extends Module {
    public Reach() {
        super("Reach", "biggerHitRange.", 0, Category.COMBAT);
    }

    public void onUpdate(UpdateEvent event) {

        moduleSettings.addDefault("range", 6.5D);

    }
}