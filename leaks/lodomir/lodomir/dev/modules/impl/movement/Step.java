/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.event.impl.game.EventUpdate;
import lodomir.dev.event.impl.player.EventMove;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.impl.ModeSetting;
import lodomir.dev.settings.impl.NumberSetting;
import lodomir.dev.utils.math.TimeUtils;
import lodomir.dev.utils.player.MovementUtils;

public class Step
extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla");
    private final NumberSetting height = new NumberSetting("Height", 0.5, 2.5, 1.5, 0.5);
    private TimeUtils time = new TimeUtils();

    public Step() {
        super("Step", 0, Category.MOVEMENT);
        this.addSettings(this.mode, this.height);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Subscribe
    public void onUpdate(EventUpdate event) {
        this.setSuffix(String.valueOf(this.height.getValue()));
    }

    @Override
    @Subscribe
    public void onMove(EventMove event) {
        switch (this.mode.getMode()) {
            case "Vanilla": {
                if (!MovementUtils.canStep(this.height.getValue()) || !MovementUtils.isMoving()) break;
                event.setY(this.height.getValue());
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}

