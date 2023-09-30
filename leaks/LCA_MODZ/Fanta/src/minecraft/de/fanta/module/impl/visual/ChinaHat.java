package de.fanta.module.impl.visual;

import java.awt.Color;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.GLU;

import de.fanta.events.Event;
import de.fanta.events.listeners.EventRender3D;
import de.fanta.module.Module;
import de.fanta.utils.ColorUtils;
import de.fanta.utils.RenderUtil;
import de.fanta.utils.RotationUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.BlockPos;

public class ChinaHat extends Module{

	public ChinaHat() {
		super("China Hat", 0, Type.Visual, Color.green);
	}

	@Override
	public void onEvent(Event event) {
		if(mc.gameSettings.thirdPersonView == 0) return;
		if(event instanceof EventRender3D) {
			float radius = 0.75F;
			float x = (float) mc.thePlayer.posX, z = (float) mc.thePlayer.posZ, y = (float) mc.thePlayer.posY;
			for(int i = 0; i < 360; i++) {
				float dX = radius*(float) Math.cos(Math.toRadians(i));
				float dZ = radius*(float) Math.sin(Math.toRadians(i));
				int radians = (int) i;
				float minus = mc.thePlayer.isSneaking() ? 0.2F : 0;
				GlStateManager.pushMatrix();
				RenderUtil.draw3dLine(0, 2.25, 0, dX, 1.9, dZ, ColorUtils.getColorAlpha(ColorUtils.rainBowEffectWithOffset(radians, 3600), 110));				
				GlStateManager.popMatrix();
			}
		}
	}
}
