package de.lirium.impl.module.impl.ui;

import de.lirium.Client;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.CheckBox;
import de.lirium.impl.module.ModuleFeature;
import org.lwjgl.input.Keyboard;

@ModuleFeature.Info(name = "Click GUI", description = "Modules interface", keyBind = Keyboard.KEY_RSHIFT, category = ModuleFeature.Category.UI)
public class ClickGuiFeature extends ModuleFeature {

    @Value(name = "Fade")
    private final CheckBox fade = new CheckBox(true);

    @Override
    public void onEnable() {
        Client.INSTANCE.getClickGUI().fade = fade.getValue();
        mc.displayGuiScreen(Client.INSTANCE.getClickGUI());

        setEnabled(false);
    }
}
