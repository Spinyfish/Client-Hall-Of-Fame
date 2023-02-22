package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityFootStepFX extends EntityFX {
    private static final ResourceLocation FOOTPRINT_TEXTURE = new ResourceLocation("textures/particle/footprint.png");
    private int footstepAge;
    private final int footstepMaxAge;
    private final TextureManager currentFootSteps;

    protected EntityFootStepFX(final TextureManager currentFootStepsIn, final World worldIn, final double xCoordIn, final double yCoordIn, final double zCoordIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0D, 0.0D, 0.0D);
        this.currentFootSteps = currentFootStepsIn;
        this.motionX = this.motionY = this.motionZ = 0.0D;
        this.footstepMaxAge = 200;
    }

    /**
     * Renders the particle
     *
     * @param worldRendererIn The WorldRenderer instance
     */
    public void renderParticle(final WorldRenderer worldRendererIn, final Entity entityIn, final float partialTicks, final float p_180434_4_, final float p_180434_5_, final float p_180434_6_, final float p_180434_7_, final float p_180434_8_) {
        float f = ((float) this.footstepAge + partialTicks) / (float) this.footstepMaxAge;
        f = f * f;
        float f1 = 2.0F - f * 2.0F;

        if (f1 > 1.0F) {
            f1 = 1.0F;
        }

        f1 = f1 * 0.2F;
        GlStateManager.disableLighting();
        final float f2 = 0.125F;
        final float f3 = (float) (this.posX - interpPosX);
        final float f4 = (float) (this.posY - interpPosY);
        final float f5 = (float) (this.posZ - interpPosZ);
        final float f6 = this.worldObj.getLightBrightness(new BlockPos(this));
        this.currentFootSteps.bindTexture(FOOTPRINT_TEXTURE);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        worldRendererIn.begin(7, DefaultVertexFormats.field_181709_i);
        worldRendererIn.pos(f3 - 0.125F, f4, f5 + 0.125F).tex(0.0D, 1.0D).func_181666_a(f6, f6, f6, f1).endVertex();
        worldRendererIn.pos(f3 + 0.125F, f4, f5 + 0.125F).tex(1.0D, 1.0D).func_181666_a(f6, f6, f6, f1).endVertex();
        worldRendererIn.pos(f3 + 0.125F, f4, f5 - 0.125F).tex(1.0D, 0.0D).func_181666_a(f6, f6, f6, f1).endVertex();
        worldRendererIn.pos(f3 - 0.125F, f4, f5 - 0.125F).tex(0.0D, 0.0D).func_181666_a(f6, f6, f6, f1).endVertex();
        Tessellator.getInstance().draw();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate() {
        ++this.footstepAge;

        if (this.footstepAge == this.footstepMaxAge) {
            this.setDead();
        }
    }

    public int getFXLayer() {
        return 3;
    }

    public static class Factory implements IParticleFactory {
        public EntityFX getEntityFX(final int particleID, final World worldIn, final double xCoordIn, final double yCoordIn, final double zCoordIn, final double xSpeedIn, final double ySpeedIn, final double zSpeedIn, final int... p_178902_15_) {
            return new EntityFootStepFX(Minecraft.getMinecraft().getTextureManager(), worldIn, xCoordIn, yCoordIn, zCoordIn);
        }
    }
}
