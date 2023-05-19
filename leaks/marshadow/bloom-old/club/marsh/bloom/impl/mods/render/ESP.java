package club.marsh.bloom.impl.mods.render;

import club.marsh.bloom.api.value.BooleanValue;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.events.Render3DEvent;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.impl.mods.combat.KillAura;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;

import java.awt.*;

public class ESP extends Module {
	EntityLivingBase cachedKillauraTarget = null;
	RenderManager renderManager = mc.getRenderManager();

	BooleanValue healthBars = new BooleanValue("Health Bars", true, () -> true);
	BooleanValue box = new BooleanValue("Box", true, () -> true);

	public ESP() {
		super("ESP",Keyboard.KEY_NONE,Category.VISUAL);
	}

	@Subscribe
	public void onRender3D(Render3DEvent e) {
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_DST_ALPHA);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glLineWidth(5.0f);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);

		mc.theWorld.loadedEntityList.stream().filter((entity) -> (entity instanceof EntityLivingBase && entity != mc.thePlayer)).forEach((target) -> {
			if (cachedKillauraTarget != null && cachedKillauraTarget != target || cachedKillauraTarget == null) {
				Color color = Color.getHSBColor(Math.max(0.0F, Math.min(((EntityLivingBase) target).getHealth(), ((EntityLivingBase) target).getMaxHealth()) / ((EntityLivingBase) target).getMaxHealth()) / 3.0F, 1.0F, 0.75F);
				AxisAlignedBB bb = new AxisAlignedBB(target.boundingBox.minX - target.posX + target.posX - renderManager.renderPosX, target.boundingBox.minY - target.posY + target.posY - renderManager.renderPosY, target.boundingBox.minZ - target.posZ + target.posZ - renderManager.renderPosZ, target.boundingBox.maxX - target.posX + target.posX - renderManager.renderPosX, target.boundingBox.maxY - target.posY + target.posY - renderManager.renderPosY, target.boundingBox.maxZ - target.posZ + target.posZ - renderManager.renderPosZ);
				if (box.isOn()) {
					RenderGlobal.drawOutlinedBoundingBox(bb, color.getRed(), color.getGreen(), color.getBlue(), 255);
				}
				if (healthBars.isOn()) {
					EntityLivingBase entityLivingBase = (EntityLivingBase) (target);
					double radians = Math.toRadians(target.rotationYaw+90),
							originalYSize = bb.maxY-bb.minY;
					AxisAlignedBB healthBox = bb.offset(1 * -Math.sin(radians), 0, 1 * Math.cos(radians)).contract(0.3,0,0);
					healthBox.maxY = healthBox.minY;
					healthBox.maxY += (entityLivingBase.getHealth() / entityLivingBase.getMaxHealth()) * originalYSize;
					RenderGlobal.drawOutlinedBoundingBox(healthBox, color.getRed(),color.getGreen(),color.getBlue(), 255);
					//RenderUtil.drawFilledBoundingBox(healthBox, color);
					//RenderUtil.drawBoundingBoxIn2D(healthBox,false,Color.white);
					//RenderUtil.drawBoundingBoxIn2D(healthBox,true,color);
				}
			}
		});
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
	}

	@Subscribe
	public void onUpdate(UpdateEvent e) {
		cachedKillauraTarget = KillAura.target;
	}
}
