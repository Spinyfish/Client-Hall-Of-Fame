package net.minecraft.client.renderer.entity.layers;

import java.awt.image.BufferedImage;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.cape.GIF;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.MathHelper;

public class LayerCape implements LayerRenderer {
    private final RenderPlayer playerRenderer;
    Minecraft mc = Minecraft.getMinecraft();

    public LayerCape(final RenderPlayer playerRendererIn) {
	playerRenderer = playerRendererIn;
    }

    // RenderCape
    public void doRenderLayer(final AbstractClientPlayer entitylivingbaseIn, final float p_177141_2_,
	    final float p_177141_3_, final float partialTicks, final float p_177141_5_, final float p_177141_6_,
	    final float p_177141_7_, final float scale) {

	if(Management.instance.friendmgr.isFriend(entitylivingbaseIn.getName()) || entitylivingbaseIn == mc.thePlayer){
	    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GIF gif =  Management.instance.GIFmgr.getGIFByName(Management.instance.settingsmgr.getSettingByName("CapeManager").getItemByName("Cape").getCurrent());
	    mc.getTextureManager().bindTexture(gif.getNext().getLocation());
	    GlStateManager.pushMatrix();
	    GlStateManager.translate(0.0F, 0.0F, 0.125F);
	    final double d0 = entitylivingbaseIn.prevChasingPosX
		    + (entitylivingbaseIn.chasingPosX - entitylivingbaseIn.prevChasingPosX) * partialTicks
		    - (entitylivingbaseIn.prevPosX
			    + (entitylivingbaseIn.posX - entitylivingbaseIn.prevPosX) * partialTicks);
	    final double d1 = entitylivingbaseIn.prevChasingPosY
		    + (entitylivingbaseIn.chasingPosY - entitylivingbaseIn.prevChasingPosY) * partialTicks
		    - (entitylivingbaseIn.prevPosY
			    + (entitylivingbaseIn.posY - entitylivingbaseIn.prevPosY) * partialTicks);
	    final double d2 = entitylivingbaseIn.prevChasingPosZ
		    + (entitylivingbaseIn.chasingPosZ - entitylivingbaseIn.prevChasingPosZ) * partialTicks
		    - (entitylivingbaseIn.prevPosZ
			    + (entitylivingbaseIn.posZ - entitylivingbaseIn.prevPosZ) * partialTicks);
	    final float f = entitylivingbaseIn.prevRenderYawOffset
		    + (entitylivingbaseIn.renderYawOffset - entitylivingbaseIn.prevRenderYawOffset) * partialTicks;
	    final double d3 = MathHelper.sin(f * (float) Math.PI / 180.0F);
	    final double d4 = -MathHelper.cos(f * (float) Math.PI / 180.0F);
	    float f1 = (float) d1 * 10.0F;
	    f1 = MathHelper.clamp_float(f1, -6.0F, 32.0F);
	    float f2 = (float) (d0 * d3 + d2 * d4) * 100.0F;
	    final float f3 = (float) (d0 * d4 - d2 * d3) * 100.0F;

	    if (f2 < 0.0F) {
		f2 = 0.0F;
	    }

	    if (f2 > 165.0F) {
		f2 = 165.0F;
	    }

	    final float f4 = entitylivingbaseIn.prevCameraYaw
		    + (entitylivingbaseIn.cameraYaw - entitylivingbaseIn.prevCameraYaw) * partialTicks;
	    f1 = f1 + MathHelper.sin((entitylivingbaseIn.prevDistanceWalkedModified
		    + (entitylivingbaseIn.distanceWalkedModified - entitylivingbaseIn.prevDistanceWalkedModified)
			    * partialTicks)
		    * 6.0F) * 32.0F * f4;

	    if (entitylivingbaseIn.isSneaking()) {
		f1 += 25.0F;
		GlStateManager.translate(0.0F, 0.142F, -0.0178F);
	    }

	    GlStateManager.rotate(6.0F + f2 / 2.0F + f1, 1.0F, 0.0F, 0.0F);
	    GlStateManager.rotate(f3 / 2.0F, 0.0F, 0.0F, 1.0F);
	    GlStateManager.rotate(-f3 / 2.0F, 0.0F, 1.0F, 0.0F);
	    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
	    final BufferedImage image = gif.getNext().getImage();
	    final float width = image.getWidth() * (12.0F / image.getWidth());
	    final float height = image.getHeight() * (18.0F / image.getHeight());
	    playerRenderer.getMainModel().renderCustomCape(0.0625F, (int) width, (int) height);
	    GlStateManager.popMatrix();
	}else if (entitylivingbaseIn.hasPlayerInfo() && !entitylivingbaseIn.isInvisible()
		&& entitylivingbaseIn.isWearing(EnumPlayerModelParts.CAPE)
		&& entitylivingbaseIn.getLocationCape() != null) {
	    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	    playerRenderer.bindTexture(entitylivingbaseIn.getLocationCape());
	    GlStateManager.pushMatrix();
	    GlStateManager.translate(0.0F, 0.0F, 0.125F);
	    final double d0 = entitylivingbaseIn.prevChasingPosX
		    + (entitylivingbaseIn.chasingPosX - entitylivingbaseIn.prevChasingPosX) * partialTicks
		    - (entitylivingbaseIn.prevPosX
			    + (entitylivingbaseIn.posX - entitylivingbaseIn.prevPosX) * partialTicks);
	    final double d1 = entitylivingbaseIn.prevChasingPosY
		    + (entitylivingbaseIn.chasingPosY - entitylivingbaseIn.prevChasingPosY) * partialTicks
		    - (entitylivingbaseIn.prevPosY
			    + (entitylivingbaseIn.posY - entitylivingbaseIn.prevPosY) * partialTicks);
	    final double d2 = entitylivingbaseIn.prevChasingPosZ
		    + (entitylivingbaseIn.chasingPosZ - entitylivingbaseIn.prevChasingPosZ) * partialTicks
		    - (entitylivingbaseIn.prevPosZ
			    + (entitylivingbaseIn.posZ - entitylivingbaseIn.prevPosZ) * partialTicks);
	    final float f = entitylivingbaseIn.prevRenderYawOffset
		    + (entitylivingbaseIn.renderYawOffset - entitylivingbaseIn.prevRenderYawOffset) * partialTicks;
	    final double d3 = MathHelper.sin(f * (float) Math.PI / 180.0F);
	    final double d4 = -MathHelper.cos(f * (float) Math.PI / 180.0F);
	    float f1 = (float) d1 * 10.0F;
	    f1 = MathHelper.clamp_float(f1, -6.0F, 32.0F);
	    float f2 = (float) (d0 * d3 + d2 * d4) * 100.0F;
	    final float f3 = (float) (d0 * d4 - d2 * d3) * 100.0F;

	    if (f2 < 0.0F) {
		f2 = 0.0F;
	    }

	    final float f4 = entitylivingbaseIn.prevCameraYaw
		    + (entitylivingbaseIn.cameraYaw - entitylivingbaseIn.prevCameraYaw) * partialTicks;
	    f1 = f1 + MathHelper.sin((entitylivingbaseIn.prevDistanceWalkedModified
		    + (entitylivingbaseIn.distanceWalkedModified - entitylivingbaseIn.prevDistanceWalkedModified)
			    * partialTicks)
		    * 6.0F) * 32.0F * f4;

	    if (entitylivingbaseIn.isSneaking()) {
		f1 += 25.0F;
	    }

	    GlStateManager.rotate(6.0F + f2 / 2.0F + f1, 1.0F, 0.0F, 0.0F);
	    GlStateManager.rotate(f3 / 2.0F, 0.0F, 0.0F, 1.0F);
	    GlStateManager.rotate(-f3 / 2.0F, 0.0F, 1.0F, 0.0F);
	    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
	    playerRenderer.getMainModel().renderCape(0.0625F);
	    GlStateManager.popMatrix();
	}
    }

    @Override
    public void doRenderLayer(final EntityLivingBase entitylivingbaseIn, final float p_177141_2_,
	    final float p_177141_3_, final float partialTicks, final float p_177141_5_, final float p_177141_6_,
	    final float p_177141_7_, final float scale) {
	this.doRenderLayer((AbstractClientPlayer) entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks,
		p_177141_5_, p_177141_6_, p_177141_7_, scale);
    }

    @Override
    public boolean shouldCombineTextures() {
	return false;
    }
}
