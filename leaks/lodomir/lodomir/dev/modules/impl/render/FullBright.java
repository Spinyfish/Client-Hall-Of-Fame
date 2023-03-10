/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.modules.impl.render;

import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;

public class FullBright
extends Module {
    public float oldGamma;

    public FullBright() {
        super("FullBright", 0, Category.RENDER);
    }

    @Override
    public void onEnable() {
        this.oldGamma = FullBright.mc.gameSettings.gammaSetting;
        FullBright.mc.gameSettings.gammaSetting = 100.0f;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        FullBright.mc.gameSettings.gammaSetting = this.oldGamma;
        super.onDisable();
    }
}

