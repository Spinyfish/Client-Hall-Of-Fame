/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.EntityRenderer
 *  net.minecraft.entity.player.EntityPlayer
 *  vip.astroline.client.service.module.Category
 *  vip.astroline.client.service.module.Module
 */
package vip.astroline.client.service.module.impl.render;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.player.EntityPlayer;
import vip.astroline.client.service.module.Category;
import vip.astroline.client.service.module.Module;

public class LSD
extends Module {
    public LSD() {
        super("LSD", Category.Render, 0, false);
    }

    public void onEnable() {
        if (!(mc.getRenderViewEntity() instanceof EntityPlayer)) {
            this.onEnable();
            return;
        }
        if (LSD.mc.entityRenderer.theShaderGroup != null) {
            LSD.mc.entityRenderer.theShaderGroup.deleteShaderGroup();
        }
        LSD.mc.entityRenderer.shaderIndex = 19;
        LSD.mc.entityRenderer.loadShader(EntityRenderer.shaderResourceLocations[19]);
    }

    public void onDisable() {
        if (LSD.mc.entityRenderer.theShaderGroup == null) return;
        LSD.mc.entityRenderer.theShaderGroup.deleteShaderGroup();
        LSD.mc.entityRenderer.theShaderGroup = null;
    }
}
