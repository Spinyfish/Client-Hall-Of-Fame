package me.eldodebug.soar.management.mods.impl;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventRenderHitbox;
import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class HitboxMod extends Mod {

	private Color boundingBoxColor = Color.WHITE;
	private Color eyeHeightColor = Color.RED;
	private Color lookVectorColor = Color.BLUE;
	
	public HitboxMod() {
		super("Hitbox", "Shows range of attack and viewpoint", ModCategory.RENDER);
	}
	
	@Override
	public void setup() {
		this.addBooleanSetting("Bounding box", this, true);
		this.addBooleanSetting("Eye Height", this, true);
		this.addBooleanSetting("Look Vector", this, true);
		this.addSliderSetting("Line Width", this, 2, 1, 5, true);
	}
	
	@EventTarget
	public void onRenderHitbox(EventRenderHitbox event) {
		
		boolean boundingBox = Soar.instance.settingsManager.getSettingByName(this, "Bounding box").getValBoolean();
		boolean eyeHeight = Soar.instance.settingsManager.getSettingByName(this, "Eye Height").getValBoolean();
		boolean lookVector = Soar.instance.settingsManager.getSettingByName(this, "Look Vector").getValBoolean();
		int lineWidth = Soar.instance.settingsManager.getSettingByName(this, "Line Width").getValInt();
		
		float half = event.getEntity().width / 2.0F;
		
		event.setCancelled(true);
		
		GlStateManager.depthMask(false);
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.enableBlend();
		GL11.glLineWidth(lineWidth);
		
		if(boundingBox) {
			AxisAlignedBB box = event.getEntity().getEntityBoundingBox();
			AxisAlignedBB offsetBox = new AxisAlignedBB(box.minX - event.getEntity().posX + event.getX(),
					box.minY - event.getEntity().posY + event.getY(), box.minZ - event.getEntity().posZ + event.getZ(),
					box.maxX - event.getEntity().posX + event.getX(), box.maxY - event.getEntity().posY + event.getY(),
					box.maxZ - event.getEntity().posZ + event.getZ());
			
			RenderGlobal.drawOutlinedBoundingBox(offsetBox, boundingBoxColor.getRed(), boundingBoxColor.getGreen(), boundingBoxColor.getBlue(), boundingBoxColor.getAlpha());
		}
		
		if(eyeHeight && event.getEntity() instanceof EntityLivingBase) {
			RenderGlobal.drawOutlinedBoundingBox(
					new AxisAlignedBB(event.getX() - half, event.getY() + event.getEntity().getEyeHeight() - 0.009999999776482582D,
							event.getZ() - half, event.getX() + half,
							event.getY() + event.getEntity().getEyeHeight() + 0.009999999776482582D, event.getZ() + half),
					eyeHeightColor.getRed(), eyeHeightColor.getGreen(), eyeHeightColor.getBlue(),
					eyeHeightColor.getAlpha());
		}
		
		if(lookVector) {
			
			Tessellator tessellator = Tessellator.getInstance();
			WorldRenderer worldrenderer = tessellator.getWorldRenderer();
			
			Vec3 look = event.getEntity().getLook(event.getPartialTicks());
			worldrenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
			worldrenderer.pos(event.getX(), event.getY() + event.getEntity().getEyeHeight(), event.getZ()).color(0, 0, 255, 255)
					.endVertex();
			worldrenderer.pos(event.getX() + look.xCoord * 2,
					event.getY() + event.getEntity().getEyeHeight() + look.yCoord * 2, event.getZ() + look.zCoord * 2)
					.color(lookVectorColor.getRed(), lookVectorColor.getGreen(), lookVectorColor.getBlue(), lookVectorColor.getAlpha()).endVertex();
			tessellator.draw();
		}
		
		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		
		if(mc.getRenderManager() != null) {
			mc.getRenderManager().setDebugBoundingBox(true);
		}
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		
		if(mc.getRenderManager() != null) {
			mc.getRenderManager().setDebugBoundingBox(false);
		}
	}
}
