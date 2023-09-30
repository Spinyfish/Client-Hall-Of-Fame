/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.visual;

import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;

public class NoRender
extends Module {
    public final Setting<Boolean> fireOverlay = new Setting<Boolean>("Fire Overlay", true).describedBy("Don't render the fire overlay.");
    public final Setting<Boolean> hurtCam = new Setting<Boolean>("Hurtcam", true).describedBy("Don't render the hurtcam.");

    public NoRender() {
        super("No Render", "Exclude the rendering of some things.", Category.VISUAL);
    }
}

