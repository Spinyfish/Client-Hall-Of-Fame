/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import io.netty.util.internal.ThreadLocalRandom;
import lodomir.dev.event.impl.game.EventUpdate;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.impl.BooleanSetting;
import lodomir.dev.settings.impl.NumberSetting;
import lodomir.dev.utils.math.TimeUtils;

public class AutoClicker
extends Module {
    public NumberSetting minCps = new NumberSetting("Min Cps", 1.0, 20.0, 10.0, 0.5);
    public NumberSetting maxCps = new NumberSetting("Max Cps", 1.0, 20.0, 10.0, 0.5);
    public BooleanSetting breakBlocks = new BooleanSetting("Break Blocks", true);
    public BooleanSetting swordOnly = new BooleanSetting("Sword Only", false);
    public TimeUtils timer = new TimeUtils();

    public AutoClicker() {
        super("AutoClicker", 0, Category.COMBAT);
        this.addSettings(this.minCps, this.maxCps, this.breakBlocks, this.swordOnly);
    }

    @Subscribe
    public void onUpdate(EventUpdate event) {
        if (AutoClicker.mc.gameSettings.keyBindAttack.isKeyDown() && !AutoClicker.mc.thePlayer.isUsingItem()) {
            if (this.timer.hasReached(1000L / this.getRandomCPS(this.minCps.getValueInt(), this.maxCps.getValueInt())) && AutoClicker.mc.thePlayer.ticksExisted % 5 != 0 && AutoClicker.mc.thePlayer.ticksExisted % 17 != 0) {
                AutoClicker.mc.leftClickCounter = 0;
                mc.clickMouse();
                this.timer.reset();
            }
        } else {
            this.timer.reset();
        }
    }

    public long getRandomCPS(long minimum, long maximum) {
        if (maximum > minimum) {
            return minimum + ThreadLocalRandom.current().nextLong(0L, maximum -= minimum);
        }
        if (minimum > maximum) {
            return maximum + ThreadLocalRandom.current().nextLong(0L, minimum -= maximum);
        }
        return minimum;
    }
}

