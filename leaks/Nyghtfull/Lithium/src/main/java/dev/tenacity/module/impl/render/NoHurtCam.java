package dev.tenacity.module.impl.render;

import dev.tenacity.event.impl.render.HurtCamEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;

public class NoHurtCam extends Module {

    public NoHurtCam() {
        super("NoHurtCam", Category.RENDER, "removes shaking after being hit");
    }

    @Override
    public void onHurtCamEvent(HurtCamEvent e) {
        e.cancel();
    }

}
