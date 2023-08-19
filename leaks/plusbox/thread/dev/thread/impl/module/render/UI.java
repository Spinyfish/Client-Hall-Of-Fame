package dev.thread.impl.module.render;

import dev.thread.api.module.Module;
import dev.thread.api.module.ModuleCategory;
import dev.thread.api.setting.Setting;

import java.awt.*;

public class UI extends Module {
    private final Setting<Boolean> test = new Setting<>("Test", "A test setting", false);

    public static final Color ONE = new Color(237, 0, 246);
    public static final Color TWO = new Color(138, 0, 255);

    public UI() {
        super("UI", "Thread UI.", ModuleCategory.RENDER);
        getSettings().add(test);
    }
}
