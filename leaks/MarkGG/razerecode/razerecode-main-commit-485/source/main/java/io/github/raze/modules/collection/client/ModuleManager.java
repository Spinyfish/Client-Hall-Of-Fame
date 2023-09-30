package io.github.raze.modules.collection.client;

import io.github.raze.Raze;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.modules.system.information.ModuleCategory;
import org.lwjgl.input.Keyboard;

public class ModuleManager extends BaseModule {

    public ModuleManager() {
        super("ModuleManager", "Manage Modules in a UI.", ModuleCategory.CLIENT, Keyboard.KEY_RSHIFT);
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(Raze.INSTANCE.SCREEN_REGISTRY.uiModuleManager);
        super.onEnable();
    }
}
