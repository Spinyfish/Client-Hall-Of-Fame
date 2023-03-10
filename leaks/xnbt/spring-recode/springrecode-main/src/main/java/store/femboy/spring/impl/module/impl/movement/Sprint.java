package store.femboy.spring.impl.module.impl.movement;

import best.azura.eventbus.core.EventPriority;
import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import org.lwjgl.input.Keyboard;
import store.femboy.spring.impl.event.impl.EventMotion;
import store.femboy.spring.impl.module.Category;
import store.femboy.spring.impl.module.Module;
import store.femboy.spring.impl.util.MoveUtil;

public final class Sprint extends Module {
    public Sprint() {
        super("Sprint", "Makes u run automatically", Keyboard.KEY_N, Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        System.out.println("Sprint enabled");
        super.onEnable();
    }

    @Override
    public void onDisable() {
        System.out.println("Sprint disabled");
        super.onDisable();
    }

    @EventHandler(EventPriority.LOWEST)
    public final Listener<EventMotion> onUpdate = e -> {
        if (MoveUtil.isMoving() && !mc.thePlayer.isCollidedHorizontally) {
            mc.thePlayer.setSprinting(true);
        }
    };


}
