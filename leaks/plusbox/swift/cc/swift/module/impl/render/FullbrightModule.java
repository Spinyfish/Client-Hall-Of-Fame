/**
 * @project hakarware
 * @author CodeMan
 * @at 24.07.23, 19:31
 */

package cc.swift.module.impl.render;

import cc.swift.events.UpdateEvent;
import cc.swift.module.Module;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;

public final class FullbrightModule extends Module {

    private float oldGamma;

    public FullbrightModule() {
        super("Fullbright", Category.RENDER);
    }

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {
        mc.gameSettings.gammaSetting = 1000;
    };

    @Override
    public void onEnable() {
        oldGamma = mc.gameSettings.gammaSetting;
    }

    @Override
    public void onDisable() {
        mc.gameSettings.gammaSetting = oldGamma;
    }
}
