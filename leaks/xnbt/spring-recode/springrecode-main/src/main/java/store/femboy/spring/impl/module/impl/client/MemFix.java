package store.femboy.spring.impl.module.impl.client;

import org.lwjgl.input.Keyboard;
import store.femboy.spring.impl.module.Category;
import store.femboy.spring.impl.module.Module;

public class MemFix extends Module {
    public MemFix() {
        super("MemoryFix", "Fixes memory leaks and shit", Keyboard.KEY_NONE, Category.CLIENT);
    }
}
