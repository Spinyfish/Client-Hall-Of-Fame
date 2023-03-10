package store.femboy.spring.impl.module.impl.player;

import org.lwjgl.input.Keyboard;
import store.femboy.spring.impl.module.Category;
import store.femboy.spring.impl.module.Module;

public final class Fastplace extends Module {
    public Fastplace() {
        super("Fastplace", "place block fast", Keyboard.KEY_O, Category.PLAYER);
    }

    @Override
    public void onEnable() {
        mc.rightClickDelayTimer = 0;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.rightClickDelayTimer = 6;
        super.onDisable();
    }
}
