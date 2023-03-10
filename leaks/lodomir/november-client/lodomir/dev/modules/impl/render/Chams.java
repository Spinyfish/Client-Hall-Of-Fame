/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.event.impl.player.EventPreMotion;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.impl.NumberSetting;
import net.minecraft.client.renderer.entity.RendererLivingEntity;

public class Chams
extends Module {
    private final NumberSetting red = new NumberSetting("Red", 0.0, 255.0, 0.0, 1.0);
    private final NumberSetting green = new NumberSetting("Green", 0.0, 255.0, 0.0, 1.0);
    private final NumberSetting blue = new NumberSetting("Blue", 0.0, 255.0, 0.0, 1.0);
    private final NumberSetting alpha = new NumberSetting("Alpha", 0.0, 255.0, 0.0, 1.0);

    public Chams() {
        super("Chams", 0, Category.RENDER);
        this.addSettings(this.red, this.green, this.blue, this.alpha);
    }

    @Override
    @Subscribe
    public void onPreMotion(EventPreMotion event) {
        RendererLivingEntity.chamsRed = (float)this.red.getValue() / 255.0f;
        RendererLivingEntity.chamsGreen = (float)this.green.getValue() / 255.0f;
        RendererLivingEntity.chamsBlue = (float)this.blue.getValue() / 255.0f;
        RendererLivingEntity.chamsAlpha = (float)this.alpha.getValue() / 255.0f;
    }
}

