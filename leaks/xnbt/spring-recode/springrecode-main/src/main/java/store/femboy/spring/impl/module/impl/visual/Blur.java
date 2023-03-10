package store.femboy.spring.impl.module.impl.visual;

import org.lwjgl.input.Keyboard;
import store.femboy.spring.impl.module.Category;
import store.femboy.spring.impl.module.Module;
import store.femboy.spring.impl.module.settings.impl.NumberSettings;

public class Blur extends Module {

    public static NumberSettings radius;

    public Blur() {
        super("Blur", "Blurs shit", Keyboard.KEY_NONE, Category.VISUAL);
        radius = new NumberSettings("Radius", 15f, 0f, 25f, 1f);

        addSettings(radius);
    }
}
