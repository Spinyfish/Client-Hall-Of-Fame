package me.eldodebug.soar.management.mods.impl;

import java.awt.Color;
import java.io.IOException;

import org.lwjgl.opengl.GL11;

import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventRender2D;
import me.eldodebug.soar.management.events.impl.EventRenderShadow;
import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;
import me.eldodebug.soar.ui.minimap.XaeroMinimap;
import me.eldodebug.soar.ui.minimap.animation.MinimapAnimation;
import me.eldodebug.soar.ui.minimap.interfaces.InterfaceHandler;
import me.eldodebug.soar.utils.ClientUtils;
import me.eldodebug.soar.utils.interfaces.IMixinMinecraft;
import me.eldodebug.soar.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;

public class MinimapMod extends Mod{

	private XaeroMinimap minimap = new XaeroMinimap();
	
	public MinimapMod() {
		super("Minimap", "Display minimap", ModCategory.HUD);
	}

	@EventTarget
	public void onRender2D(EventRender2D event) {
        Minecraft.getMinecraft().entityRenderer.setupOverlayRendering();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        InterfaceHandler.drawInterfaces(((IMixinMinecraft)Minecraft.getMinecraft()).getTimer().renderPartialTicks);
        MinimapAnimation.tick();
        
        this.setWidth(75);
        this.setHeight(75);
	}
	
	@EventTarget
	public void onRenderShadow(EventRenderShadow event) {
		RenderUtils.drawRect(this.getX(), this.getY(), 75, 75, Color.BLACK.getRGB());
	}
	
	@Override
	public void onEnable() {
		
		super.onEnable();
		
		if(!ClientUtils.loadedMinimap) {
			
			ClientUtils.loadedMinimap = true;
			
			try {
				minimap.load();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
