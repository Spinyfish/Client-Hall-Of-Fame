/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.service.module.Category
 *  vip.astroline.client.service.module.Module
 *  vip.astroline.client.service.module.value.BooleanValue
 *  vip.astroline.client.service.module.value.FloatValue
 *  vip.astroline.client.service.module.value.ModeValue
 */
package vip.astroline.client.service.module.impl.render;

import vip.astroline.client.service.module.Category;
import vip.astroline.client.service.module.Module;
import vip.astroline.client.service.module.value.BooleanValue;
import vip.astroline.client.service.module.value.FloatValue;
import vip.astroline.client.service.module.value.ModeValue;

public class Animation
extends Module {
    public static ModeValue swordAnimation = new ModeValue("Animation", "Sword Animation", "1.7", new String[]{"1.7", "Slide", "Sigma", "Push", "Push1", "Fixed", "Reverse", "Reverse1", "Vanilla", "Strange", "Spin", "Screw", "Poke", "Swong", "Exhi 1.7", "Exhi Swang", "Exhi Swong", "Exhi Swank", "E"});
    public static FloatValue x = new FloatValue("Animation", "Blocking X", 0.0f, -2.5f, 2.5f, 0.1f);
    public static FloatValue y = new FloatValue("Animation", "Blocking Y", 0.0f, -2.5f, 2.5f, 0.1f);
    public static FloatValue z = new FloatValue("Animation", "Blocking Z", 0.0f, -2.5f, 2.5f, 0.1f);
    public static FloatValue xArm = new FloatValue("Animation", "Arm X", 0.0f, -2.5f, 2.5f, 0.1f);
    public static FloatValue yArm = new FloatValue("Animation", "Arm Y", 0.0f, -2.5f, 2.5f, 0.1f);
    public static FloatValue zArm = new FloatValue("Animation", "Arm Z", 0.0f, -2.5f, 2.5f, 0.1f);
    public static FloatValue swingSpeed = new FloatValue("Animation", "Swing Slowdown", 1.0f, 0.5f, 3.0f, 0.1f);
    public static BooleanValue swingAnimation = new BooleanValue("Animation", "Swing Animation", Boolean.valueOf(true));

    public Animation() {
        super("Animation", Category.Render, 0, false);
    }
}
