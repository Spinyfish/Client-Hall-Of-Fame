/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.ghost;

import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;

public class HitBox
extends Module {
    public Setting<Float> expand = new Setting<Float>("Expand", Float.valueOf(1.0f)).minimum(Float.valueOf(0.1f)).maximum(Float.valueOf(3.0f)).incrementation(Float.valueOf(0.1f)).describedBy("The multiplication factor of the entity's hitbox");

    public HitBox() {
        super("Hit Box", "Expands the entity hitboxes", Category.GHOST);
    }
}

