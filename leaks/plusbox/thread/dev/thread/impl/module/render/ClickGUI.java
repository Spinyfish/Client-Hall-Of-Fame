package dev.thread.impl.module.render;

import dev.thread.api.module.Module;
import dev.thread.api.module.ModuleCategory;
import dev.thread.api.setting.Setting;
import dev.thread.impl.gui.clickgui.thread.ThreadClickGUI;
import org.lwjglx.input.Keyboard;

public class ClickGUI extends Module {
    private final Setting<Mode> mode = new Setting<>("Mode", "ClickGUI mode", Mode.THREAD);

    public ClickGUI() {
        super("ClickGUI", "ClackGUI", ModuleCategory.RENDER);
        getKey().getValue().setKey(Keyboard.KEY_RSHIFT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        switch(mode.getValue()) {
            case THREAD:
                mc.displayGuiScreen(new ThreadClickGUI());
                break;
        }
    }

    public enum Mode {
        THREAD
    }
}
