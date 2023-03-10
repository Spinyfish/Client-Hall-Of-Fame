package store.femboy.spring.impl.module.impl.visual;

import org.lwjgl.input.Keyboard;
import store.femboy.spring.impl.module.Category;
import store.femboy.spring.impl.module.Module;

public final class Fullbright extends Module {
    public Fullbright() {
        super("Fullbright", "Makes game bright", Keyboard.KEY_P, Category.VISUAL);
    }

    public float gamma;

    @Override
    public void onEnable(){
        gamma = mc.gameSettings.gammaSetting;
        mc.gameSettings.gammaSetting = 10000f;
        super.onEnable();
    }

    @Override
    public void onDisable(){
        mc.gameSettings.gammaSetting = gamma;
        super.onDisable();
    }

}
