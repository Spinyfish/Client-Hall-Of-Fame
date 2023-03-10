/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.modules.impl.render;

import lodomir.dev.November;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.impl.ModeSetting;

public class ClickGUI
extends Module {
    public static ModeSetting mode = new ModeSetting("Mode", "Dropdown", "Dropdown", "Neverlose");

    public ClickGUI() {
        super("ClickGUI", 54, Category.RENDER);
        this.addSetting(mode);
    }

    @Override
    public void onEnable() {
        switch (mode.getMode()) {
            case "Dropdown": {
                mc.displayGuiScreen(November.INSTANCE.clickui);
                this.setEnabled(false);
                break;
            }
            case "Neverlose": {
                mc.displayGuiScreen(November.INSTANCE.clickgui);
                this.setEnabled(false);
            }
        }
        super.onEnable();
    }
}

