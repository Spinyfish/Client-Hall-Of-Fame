package me.jinthium.straight.impl.modules.visual;

import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.settings.ModeSetting;

public class ImageESP extends Module {

    private final ModeSetting modeSetting = new ModeSetting("Mode", "Bretthax", "Bretthax", "Dream", "Floyd", "Rosa Parks", "Matt Walsh", "Andrew Tate");

    public ImageESP(){
        super("ImageESP", Category.VISUALS);
        this.addSettings(modeSetting);
    }
}
