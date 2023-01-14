package com.srt.module.visuals;

import org.lwjgl.opengl.GL11;

import com.srt.events.Event;
import com.srt.events.listeners.EventRender2D;
import com.srt.module.ModuleBase;
import com.thunderware.utils.RenderUtils;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class Radar extends ModuleBase {

	public Radar() {
		super("Radar", 0, Category.VISUALS);
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventRender2D) {
			GlStateManager.pushMatrix();
			int x = 4;
			int y = 46;
			int size = 70;
			Gui.drawRect(x, y, x + size, y + size, 0xFF121212);
			for(Entity ent : mc.theWorld.loadedEntityList) {
				if(ent instanceof EntityLivingBase) {
					double posX = mc.thePlayer.posX - ent.posX;
					double posZ = mc.thePlayer.posZ - ent.posZ;
					if(posX <= size /2 && posX >= -size /2 && posZ <= size /2 && posZ >= -size /2)
						if(ent == mc.thePlayer) {
							Gui.drawRect(posX - 1 + x + (size / 2), posZ - 1 + y + (size / 2), posX + 1 + x + (size / 2), posZ + 1 + y + (size / 2), 0xFF00FF00);
						}
						else
							if(!ent.isInvisible() && ent instanceof EntityPlayer)
								Gui.drawRect(posX - 0.5 + x + (size / 2), posZ - 0.5 + y + (size / 2), posX + 0.5 + x + (size / 2), posZ + 0.5 + y + (size / 2), 0xFFFF0000);
				}
			}
			GlStateManager.popMatrix();
		}
	}

}
