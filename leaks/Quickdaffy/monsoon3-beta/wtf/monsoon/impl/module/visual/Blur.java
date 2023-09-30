/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.visual;

import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;

public class Blur
extends Module {
    public final Setting<Boolean> chat = new Setting<Boolean>("Chat", false).describedBy("Blurs the chat GUI screen");
    public final Setting<Float> guiBlurStrength = new Setting<Float>("Intensity", Float.valueOf(6.0f)).minimum(Float.valueOf(2.0f)).maximum(Float.valueOf(10.0f)).incrementation(Float.valueOf(0.5f)).describedBy("Strength of  the blur");

    public Blur() {
        super("Blur", "Blurs GUIs", Category.VISUAL);
    }
}

