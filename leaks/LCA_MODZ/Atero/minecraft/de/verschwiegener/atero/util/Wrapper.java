package de.verschwiegener.atero.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;

public interface Wrapper {
    
    public static final Minecraft mc = Minecraft.getMinecraft();
    public static final RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();

}
