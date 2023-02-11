/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.mods.modules.render;

import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.world.EventTick;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;

public class FullBright
        extends Module {
    private float old;

    public FullBright() {
        super("FullBright", ModuleType.Render);

    }

    @Override
    public void onEnable() {
        this.old = this.mc.gameSettings.gammaSetting;
    }

    @EventHandler
    private void onTick(EventTick e) {
        this.mc.gameSettings.gammaSetting = 1.5999999E7f;
    }

    @Override
    public void onDisable() {
        this.mc.gameSettings.gammaSetting = this.old;
    }
}

