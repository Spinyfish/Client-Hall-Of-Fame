package me.eldodebug.soar.hooks;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.gui.GuiEditHUD;
import me.eldodebug.soar.management.events.impl.EventRender2D;
import me.eldodebug.soar.management.events.impl.EventRenderDamageTint;
import me.eldodebug.soar.management.events.impl.EventRenderShadow;
import me.eldodebug.soar.management.mods.impl.HUDMod;
import me.eldodebug.soar.management.mods.impl.MenuBlurMod;
import me.eldodebug.soar.ui.notification.NotificationManager;
import me.eldodebug.soar.utils.GlUtils;
import me.eldodebug.soar.utils.animation.simple.SimpleAnimation;
import me.eldodebug.soar.utils.color.ColorUtils;
import me.eldodebug.soar.utils.render.RoundedUtils;
import me.eldodebug.soar.utils.render.StencilUtils;
import me.eldodebug.soar.utils.shader.BlurUtils;
import me.eldodebug.soar.utils.shader.GaussianBlur;
import me.eldodebug.soar.utils.shader.ShadowUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;

public class GuiIngameHook {

	private static Framebuffer shadowFramebuffer = new Framebuffer(1, 1, false);
	private static SimpleAnimation opacityAnimation = new SimpleAnimation(0.0F);
	
	public static void renderGameOverlay(float partialTicks) {
		
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		
		opacityAnimation.setAnimation(Minecraft.getMinecraft().currentScreen instanceof GuiEditHUD ? 220 : 0, 16);
		
		RoundedUtils.drawRound(0, sr.getScaledHeight() / 2, sr.getScaledWidth(), 1, 0, ColorUtils.getBackgroundColor(4, (int) opacityAnimation.getValue()));
		RoundedUtils.drawRound(sr.getScaledWidth() / 2, 0, 1, sr.getScaledHeight(), 0, ColorUtils.getBackgroundColor(4, (int) opacityAnimation.getValue()));
		
		EventRenderDamageTint event3 = new EventRenderDamageTint();
		event3.call();
		
		if((!Soar.instance.settingsManager.getSettingByClass(HUDMod.class, "Hide Debug Menu").getValBoolean()) || (Soar.instance.settingsManager.getSettingByClass(HUDMod.class, "Hide Debug Menu").getValBoolean() && !Minecraft.getMinecraft().gameSettings.showDebugInfo)) {
			EventRender2D event = new EventRender2D(partialTicks);
			EventRenderShadow event2 = new EventRenderShadow(partialTicks);
			
	        shadowFramebuffer = GlUtils.createFrameBuffer(shadowFramebuffer);

	        shadowFramebuffer.framebufferClear();
	        shadowFramebuffer.bindFramebuffer(true);
	        event2.call();
	        shadowFramebuffer.unbindFramebuffer();

	        ShadowUtils.renderShadow(shadowFramebuffer.framebufferTexture, 8, 2);
	        
	        StencilUtils.initStencilToWrite();
	        if(Soar.instance.settingsManager.getSettingByClass(HUDMod.class, "Blur").getValBoolean()) {
	            event2.call();
	        }
	        
	        StencilUtils.readStencilBuffer(1);
	        GaussianBlur.renderBlur(Soar.instance.settingsManager.getSettingByClass(HUDMod.class, "Blur Radius").getValInt());
	        StencilUtils.uninitStencilBuffer();
	        
	        event.call();
		}
		
		if(Minecraft.getMinecraft().currentScreen instanceof Gui) {
	        if(Soar.instance.modManager.getModByClass(MenuBlurMod.class).isToggled()) {
	        	BlurUtils.drawBlurScreen();
	        }	
		}
		
		NotificationManager.render();
        
        GlStateManager.enableBlend();
	}
}
