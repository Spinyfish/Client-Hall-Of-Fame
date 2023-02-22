package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityLargeExplodeFX extends EntityFX {
    private static final ResourceLocation EXPLOSION_TEXTURE = new ResourceLocation("textures/entity/explosion.png");
    private static final VertexFormat field_181549_az = (new VertexFormat()).func_181721_a(DefaultVertexFormats.field_181713_m).func_181721_a(DefaultVertexFormats.field_181715_o).func_181721_a(DefaultVertexFormats.field_181714_n).func_181721_a(DefaultVertexFormats.field_181716_p).func_181721_a(DefaultVertexFormats.field_181717_q).func_181721_a(DefaultVertexFormats.field_181718_r);
    private int field_70581_a;
    private final int field_70584_aq;

    /**
     * The Rendering Engine.
     */
    private final TextureManager theRenderEngine;
    private final float field_70582_as;

    protected EntityLargeExplodeFX(final TextureManager renderEngine, final World worldIn, final double xCoordIn, final double yCoordIn, final double zCoordIn, final double p_i1213_9_, final double p_i1213_11_, final double p_i1213_13_) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0D, 0.0D, 0.0D);
        this.theRenderEngine = renderEngine;
        this.field_70584_aq = 6 + this.rand.nextInt(4);
        this.particleRed = this.particleGreen = this.particleBlue = this.rand.nextFloat() * 0.6F + 0.4F;
        this.field_70582_as = 1.0F - (float) p_i1213_9_ * 0.5F;
    }

    /**
     * Renders the particle
     *
     * @param worldRendererIn The WorldRenderer instance
     */
    public void renderParticle(final WorldRenderer worldRendererIn, final Entity entityIn, final float partialTicks, final float p_180434_4_, final float p_180434_5_, final float p_180434_6_, final float p_180434_7_, final float p_180434_8_) {
        final int i = (int) (((float) this.field_70581_a + partialTicks) * 15.0F / (float) this.field_70584_aq);

        if (i <= 15) {
            this.theRenderEngine.bindTexture(EXPLOSION_TEXTURE);
            final float f = (float) (i % 4) / 4.0F;
            final float f1 = f + 0.24975F;
            final float f2 = (float) (i / 4) / 4.0F;
            final float f3 = f2 + 0.24975F;
            final float f4 = 2.0F * this.field_70582_as;
            final float f5 = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
            final float f6 = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
            final float f7 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableLighting();
            RenderHelper.disableStandardItemLighting();
            worldRendererIn.begin(7, field_181549_az);
            worldRendererIn.pos(f5 - p_180434_4_ * f4 - p_180434_7_ * f4, f6 - p_180434_5_ * f4, f7 - p_180434_6_ * f4 - p_180434_8_ * f4).tex(f1, f3).func_181666_a(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).func_181671_a(0, 240).func_181663_c(0.0F, 1.0F, 0.0F).endVertex();
            worldRendererIn.pos(f5 - p_180434_4_ * f4 + p_180434_7_ * f4, f6 + p_180434_5_ * f4, f7 - p_180434_6_ * f4 + p_180434_8_ * f4).tex(f1, f2).func_181666_a(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).func_181671_a(0, 240).func_181663_c(0.0F, 1.0F, 0.0F).endVertex();
            worldRendererIn.pos(f5 + p_180434_4_ * f4 + p_180434_7_ * f4, f6 + p_180434_5_ * f4, f7 + p_180434_6_ * f4 + p_180434_8_ * f4).tex(f, f2).func_181666_a(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).func_181671_a(0, 240).func_181663_c(0.0F, 1.0F, 0.0F).endVertex();
            worldRendererIn.pos(f5 + p_180434_4_ * f4 - p_180434_7_ * f4, f6 - p_180434_5_ * f4, f7 + p_180434_6_ * f4 - p_180434_8_ * f4).tex(f, f3).func_181666_a(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).func_181671_a(0, 240).func_181663_c(0.0F, 1.0F, 0.0F).endVertex();
            Tessellator.getInstance().draw();
            GlStateManager.enableLighting();
        }
    }

    public int getBrightnessForRender(final float partialTicks) {
        return 61680;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        ++this.field_70581_a;

        if (this.field_70581_a == this.field_70584_aq) {
            this.setDead();
        }
    }

    public int getFXLayer() {
        return 3;
    }

    public static class Factory implements IParticleFactory {
        public EntityFX getEntityFX(final int particleID, final World worldIn, final double xCoordIn, final double yCoordIn, final double zCoordIn, final double xSpeedIn, final double ySpeedIn, final double zSpeedIn, final int... p_178902_15_) {
            return new EntityLargeExplodeFX(Minecraft.getMinecraft().getTextureManager(), worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        }
    }
}
