package store.femboy.spring.impl.module.impl.visual.clickgui;

import org.lwjgl.input.Keyboard;
import store.femboy.spring.impl.module.Category;
import store.femboy.spring.impl.module.Module;
import store.femboy.spring.impl.module.impl.visual.clickgui.impl.ClickGuiScreen;
import store.femboy.spring.impl.module.impl.visual.clickgui.newver.ClickGuiScreenNew;
import store.femboy.spring.impl.module.settings.impl.ModeSetting;

public final class ClickGui extends Module {

    public static ModeSetting clickGuiAnimationDirection = new ModeSetting("Animation Direction", "Up", "Up", "Down", "Left", "Right", "Left + Down", "Left + Up", "Right + Down", "Right + Up");
    public final ModeSetting mode = new ModeSetting("Mode", "New", "New", "Old");
    public static ModeSetting femboyMode = new ModeSetting("Femboy Mode", "Astolfo 1", "None", "Astolfo 1", "Astolfo 2", "Astolfo 3", "Astolfo 4");

    public ClickGui() {
        super("ClickGUI", "Click to enable module, right click to show settings", Keyboard.KEY_RSHIFT, Category.CLIENT);
        addSettings(mode, clickGuiAnimationDirection, femboyMode);
    }

    @Override
    public void onEnable() {

        switch (mode.getMode()){
            case "New":
                mc.displayGuiScreen(new ClickGuiScreenNew());
                break;
            case "Old":
                mc.displayGuiScreen(new ClickGuiScreen());
                break;

        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
