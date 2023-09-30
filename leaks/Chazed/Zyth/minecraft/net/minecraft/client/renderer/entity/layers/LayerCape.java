package net.minecraft.client.renderer.entity.layers;

import me.wavelength.baseclient.module.modules.render.Capes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class LayerCape implements LayerRenderer<EntityLivingBase> {

	private final RenderPlayer playerRenderer;


	public LayerCape(RenderPlayer playerRendererIn) {
		this.playerRenderer = playerRendererIn;
	}

	public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
		if (entitylivingbaseIn.hasPlayerInfo() && !entitylivingbaseIn.isInvisible() && entitylivingbaseIn.isWearing(EnumPlayerModelParts.CAPE) && entitylivingbaseIn.getLocationCape() != null) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.playerRenderer.bindTexture(entitylivingbaseIn.getLocationCape());
			if(entitylivingbaseIn == Minecraft.getMinecraft().thePlayer){
				if(Capes.cape == 1)
					this.playerRenderer.bindTexture(new ResourceLocation("Jello.png"));

				if(Capes.cape == 2)
					this.playerRenderer.bindTexture(new ResourceLocation("Hentai.png"));

				if(Capes.cape == 3)
					this.playerRenderer.bindTexture(new ResourceLocation("Hentai1.png"));

				if(Capes.cape == 4)
					this.playerRenderer.bindTexture(new ResourceLocation("Hentai2.png"));

				if(Capes.cape == 5)
					this.playerRenderer.bindTexture(new ResourceLocation("Hantai3.png"));

				if(Capes.cape == 6)
					this.playerRenderer.bindTexture(new ResourceLocation("Hantai4.png"));

				if(Capes.cape == 7)
					this.playerRenderer.bindTexture(new ResourceLocation("Hentai5.png"));

				if(Capes.cape == 8)
					this.playerRenderer.bindTexture(new ResourceLocation("Hentai6.png"));

				if(Capes.cape == 9)
					this.playerRenderer.bindTexture(new ResourceLocation("Hentai7.png"));

				if(Capes.cape == 10)
					this.playerRenderer.bindTexture(new ResourceLocation("moon.png"));

				if(Capes.cape == 11)
					this.playerRenderer.bindTexture(new ResourceLocation("Zyth.png"));

				if(Capes.cape == 12)
					this.playerRenderer.bindTexture(new ResourceLocation("Zyth2.png"));
				
				if(Capes.cape == 13)
					this.playerRenderer.bindTexture(new ResourceLocation("Zyth3.png"));
				
				if(Capes.cape == 14)
					this.playerRenderer.bindTexture(new ResourceLocation("Zyth4.png"));

			}
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 0.0F, 0.125F);
			double d0 = entitylivingbaseIn.prevChasingPosX + (entitylivingbaseIn.chasingPosX - entitylivingbaseIn.prevChasingPosX) * (double) partialTicks - (entitylivingbaseIn.prevPosX + (entitylivingbaseIn.posX - entitylivingbaseIn.prevPosX) * (double) partialTicks);
			double d1 = entitylivingbaseIn.prevChasingPosY + (entitylivingbaseIn.chasingPosY - entitylivingbaseIn.prevChasingPosY) * (double) partialTicks - (entitylivingbaseIn.prevPosY + (entitylivingbaseIn.posY - entitylivingbaseIn.prevPosY) * (double) partialTicks);
			double d2 = entitylivingbaseIn.prevChasingPosZ + (entitylivingbaseIn.chasingPosZ - entitylivingbaseIn.prevChasingPosZ) * (double) partialTicks - (entitylivingbaseIn.prevPosZ + (entitylivingbaseIn.posZ - entitylivingbaseIn.prevPosZ) * (double) partialTicks);
			float f = entitylivingbaseIn.prevRenderYawOffset + (entitylivingbaseIn.renderYawOffset - entitylivingbaseIn.prevRenderYawOffset) * partialTicks;
			double d3 = (double) MathHelper.sin(f * (float) Math.PI / 180.0F);
			double d4 = (double) (-MathHelper.cos(f * (float) Math.PI / 180.0F));
			float f1 = (float) d1 * 10.0F;
			f1 = MathHelper.clamp_float(f1, -6.0F, 32.0F);
			float f2 = (float) (d0 * d3 + d2 * d4) * 100.0F;
			float f3 = (float) (d0 * d4 - d2 * d3) * 100.0F;

			if (f2 < 0.0F) {
				f2 = 0.0F;
			}

			if (f2 > 165.0F) {
				f2 = 165.0F;
			}

			float f4 = entitylivingbaseIn.prevCameraYaw + (entitylivingbaseIn.cameraYaw - entitylivingbaseIn.prevCameraYaw) * partialTicks;
			f1 = f1 + MathHelper.sin((entitylivingbaseIn.prevDistanceWalkedModified + (entitylivingbaseIn.distanceWalkedModified - entitylivingbaseIn.prevDistanceWalkedModified) * partialTicks) * 6.0F) * 32.0F * f4;

			if (entitylivingbaseIn.isSneaking()) {
				f1 += 25.0F;
				GlStateManager.translate(0.0F, 0.142F, -0.0178F);
			}

			GlStateManager.rotate(6.0F + f2 / 2.0F + f1, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(f3 / 2.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(-f3 / 2.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			this.playerRenderer.getMainModel().renderCape(0.0625F);
			GlStateManager.popMatrix();
		}
	}

	public boolean shouldCombineTextures() {
		return false;
	}

	public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
		this.doRenderLayer((AbstractClientPlayer) entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale);
	}

}