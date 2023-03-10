/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.event.impl.game.EventUpdate;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.impl.BooleanSetting;
import lodomir.dev.settings.impl.ModeSetting;

public class Animations
extends Module {
    public static ModeSetting mode = new ModeSetting("Mode", "1.7", "1.7", "November", "Smooth", "Stella", "Autumn", "Dortware", "Ethernal", "Exhibition", "ETB", "Flux", "Skid", "Punch", "Sigma", "xd", "Swank", "Swong", "Swaing", "Swing", "Astolfo");
    public static BooleanSetting show = new BooleanSetting("Always Show", true);
    public static BooleanSetting smooth = new BooleanSetting("Smooth", false);

    public Animations() {
        super("Animations", 0, Category.RENDER);
        this.addSetting(mode);
        this.addSetting(show);
        this.addSetting(smooth);
    }

    @Subscribe
    public void onUpdate(EventUpdate event) {
        super.onUpdate(event);
    }
}

