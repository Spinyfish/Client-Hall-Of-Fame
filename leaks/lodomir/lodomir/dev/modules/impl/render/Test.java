/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.November;
import lodomir.dev.event.impl.render.EventRender2D;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.utils.render.RenderUtils;
import lodomir.dev.utils.render.animations.Animate;
import lodomir.dev.utils.render.animations.Easing;

public class Test
extends Module {
    public Test() {
        super("Test", 0, Category.OTHER);
    }

    @Override
    @Subscribe
    public void on2D(EventRender2D event) {
        November.Log(String.valueOf(Test.mc.thePlayer.rotationPitch));
        Animate anim = new Animate();
        anim.setEase(Easing.LINEAR).setMin(10.0f).setMax(500.0f).setSpeed(100.0f).setReversed(false).update();
        RenderUtils.drawRect(anim.getValue(), anim.getValue(), 20.0, 20.0, -1);
        if (!this.isEnabled()) {
            anim.reset();
        }
    }
}

