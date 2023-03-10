/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.modules.impl.render;

import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.impl.BooleanSetting;
import lodomir.dev.settings.impl.NumberSetting;
import net.minecraft.client.gui.ScaledResolution;

public class Scoreboard
extends Module {
    private static ScaledResolution sr = new ScaledResolution(mc);
    public static NumberSetting x = new NumberSetting("PosX", 0.0, sr.getScaledWidth() / 2, 1920.0, 1.0);
    public static NumberSetting y = new NumberSetting("PosY", 0.0, sr.getScaledHeight() / 2, 500.0, 1.0);
    public static BooleanSetting font = new BooleanSetting("Custom Font", false);
    public static BooleanSetting ip = new BooleanSetting("Custom Server IP", false);
    public static BooleanSetting hide = new BooleanSetting("Hide Scoreboard", false);

    public Scoreboard() {
        super("Scoreboard", 0, Category.RENDER);
        this.addSettings(font, ip, hide);
    }
}

