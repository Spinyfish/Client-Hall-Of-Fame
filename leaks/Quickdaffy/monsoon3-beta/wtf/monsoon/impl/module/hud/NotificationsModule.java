/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.hud;

import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;

public class NotificationsModule
extends Module {
    public final Setting<Float> barOpacity = new Setting<Float>("BarOpacity", Float.valueOf(1.0f)).minimum(Float.valueOf(0.0f)).maximum(Float.valueOf(1.0f)).incrementation(Float.valueOf(0.01f)).describedBy("The opacity of the time bar");
    public final Setting<Float> barDarken = new Setting<Float>("BarDarken", Float.valueOf(0.65f)).minimum(Float.valueOf(0.0f)).maximum(Float.valueOf(1.0f)).incrementation(Float.valueOf(0.01f)).describedBy("How much to darken the bar by");
    public final Setting<FlagAlertMode> flagAlert = new Setting<FlagAlertMode>("Flag Alert Mode", FlagAlertMode.NOTIFICATION).describedBy("How to alert you to a flag.");

    public NotificationsModule() {
        super("Notifications", "Renders notifications on screen", Category.HUD);
    }

    public static enum FlagAlertMode {
        NOTIFICATION,
        INDICATOR;

    }
}

