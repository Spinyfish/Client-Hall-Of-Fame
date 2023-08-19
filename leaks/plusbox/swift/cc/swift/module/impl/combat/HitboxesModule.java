package cc.swift.module.impl.combat;

import cc.swift.events.UpdateEvent;
import cc.swift.module.Module;
import cc.swift.value.impl.DoubleValue;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import org.lwjgl.input.Keyboard;

public final class HitboxesModule extends Module {
    public final DoubleValue hitboxSize = new DoubleValue("Hitbox Size", 0.2, 0.125, 1, 0.025);

    public HitboxesModule() {
        super("HitBoxes", Category.COMBAT);
        this.registerValues(this.hitboxSize);
    }
}