package net.minecraft.client.renderer.entity;

import com.alan.clients.util.font.impl.minecraft.FontRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.src.Config;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.shaders.Shaders;
import org.lwjgl.opengl.GL11;

public abstract class Render<T extends Entity> implements IEntityRenderer {
    private static final ResourceLocation shadowTextures = new ResourceLocation("textures/misc/shadow.png");
    protected final RenderManager renderManager;
    public float shadowSize;

    /**
     * Determines the darkness of the object's shadow. Higher value makes a darker shadow.
     */
    protected float shadowOpaque = 1.0F;
    private Class entityClass = null;
    private ResourceLocation locationTextureCustom = null;

    protected Render(final RenderManager renderManager) {
        this.renderManager = renderManager;
    }

    public boolean shouldRender(final T livingEntity, final ICamera camera, final double camX, final double camY, final double camZ) {
        AxisAlignedBB axisalignedbb = livingEntity.getEntityBoundingBox();

        if (axisalignedbb.func_181656_b() || axisalignedbb.getAverageEdgeLength() == 0.0D) {
            axisalignedbb = new AxisAlignedBB(livingEntity.posX - 2.0D, livingEntity.posY - 2.0D, livingEntity.posZ - 2.0D, livingEntity.posX + 2.0D, livingEntity.posY + 2.0D, livingEntity.posZ + 2.0D);
        }

        return livingEntity.isInRangeToRender3d(camX, camY, camZ) && (livingEntity.ignoreFrustumCheck || camera.isBoundingBoxInFrustum(axisalignedbb));
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity>) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doe
     *
     * @param entityYaw The yaw rotation of the passed entity
     */
    public void doRender(final T entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks) {
        if (entity.renderNameTag) this.renderName(entity, x, y, z);
    }

    protected void renderName(final T entity, final double x, final double y, final double z) {
        if (this.canRenderName(entity)) {
            this.renderLivingLabel(entity, entity.getDisplayName().getFormattedText(), x, y, z);
        }
    }

    protected boolean canRenderName(final T entity) {
        return entity.getAlwaysRenderNameTagForRender() && entity.hasCustomName();
    }

    protected void renderOffsetLivingLabel(final T entityIn, final double x, final double y, final double z, final String str, final float p_177069_9_, final double p_177069_10_) {
        this.renderLivingLabel(entityIn, str, x, y, z);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected abstract ResourceLocation getEntityTexture(T entity);

    protected boolean bindEntityTexture(final T entity) {
        ResourceLocation resourcelocation = this.getEntityTexture(entity);

        if (this.locationTextureCustom != null) {
            resourcelocation = this.locationTextureCustom;
        }

        if (resourcelocation == null) {
            return false;
        } else {
            this.bindTexture(resourcelocation);
            return true;
        }
    }

    public void bindTexture(final ResourceLocation location) {
        this.renderManager.renderEngine.bindTexture(location);
    }

    /**
     * Renders fire on top of the entity. Args: entity, x, y, z, partialTickTime
     */
    private void renderEntityOnFire(final Entity entity, final double x, final double y, final double z, final float partialTicks) {
        GlStateManager.disableLighting();
        final TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();
        final TextureAtlasSprite textureatlassprite = texturemap.getAtlasSprite("minecraft:blocks/fire_layer_0");
        final TextureAtlasSprite textureatlassprite1 = texturemap.getAtlasSprite("minecraft:blocks/fire_layer_1");
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);
        final float f = entity.width * 1.4F;
        GlStateManager.scale(f, f, f);
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        float f1 = 0.5F;
        final float f2 = 0.0F;
        float f3 = entity.height / f;
        float f4 = (float) (entity.posY - entity.getEntityBoundingBox().minY);
        GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(0.0F, 0.0F, -0.3F + (float) ((int) f3) * 0.02F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        float f5 = 0.0F;
        int i = 0;
        final boolean flag = Config.isMultiTexture();

        if (flag) {
            worldrenderer.setBlockLayer(EnumWorldBlockLayer.SOLID);
        }

        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);

        while (f3 > 0.0F) {
            final TextureAtlasSprite textureatlassprite2 = i % 2 == 0 ? textureatlassprite : textureatlassprite1;
            worldrenderer.setSprite(textureatlassprite2);
            this.bindTexture(TextureMap.locationBlocksTexture);
            float f6 = textureatlassprite2.getMinU();
            final float f7 = textureatlassprite2.getMinV();
            float f8 = textureatlassprite2.getMaxU();
            final float f9 = textureatlassprite2.getMaxV();

            if (i / 2 % 2 == 0) {
                final float f10 = f8;
                f8 = f6;
                f6 = f10;
            }

            worldrenderer.pos(f1 - f2, 0.0F - f4, f5).tex(f8, f9).endVertex();
            worldrenderer.pos(-f1 - f2, 0.0F - f4, f5).tex(f6, f9).endVertex();
            worldrenderer.pos(-f1 - f2, 1.4F - f4, f5).tex(f6, f7).endVertex();
            worldrenderer.pos(f1 - f2, 1.4F - f4, f5).tex(f8, f7).endVertex();
            f3 -= 0.45F;
            f4 -= 0.45F;
            f1 *= 0.9F;
            f5 += 0.03F;
            ++i;
        }

        tessellator.draw();

        if (flag) {
            worldrenderer.setBlockLayer(null);
            GlStateManager.bindCurrentTexture();
        }

        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
    }

    /**
     * Renders the entity shadows at the position, shadow alpha and partialTickTime. Args: entity, x, y, z, shadowAlpha,
     * partialTickTime
     */
    private void renderShadow(final Entity entityIn, final double x, final double y, final double z, final float shadowAlpha, final float partialTicks) {
        if (!Config.isShaders() || !Shaders.shouldSkipDefaultShadow) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            this.renderManager.renderEngine.bindTexture(shadowTextures);
            final World world = this.getWorldFromRenderManager();
            GlStateManager.depthMask(false);
            float f = this.shadowSize;

            if (entityIn instanceof EntityLiving) {
                final EntityLiving entityliving = (EntityLiving) entityIn;
                f *= entityliving.getRenderSizeModifier();

                if (entityliving.isChild()) {
                    f *= 0.5F;
                }
            }

            final double d5 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double) partialTicks;
            final double d0 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double) partialTicks;
            final double d1 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double) partialTicks;
            final int i = MathHelper.floor_double(d5 - (double) f);
            final int j = MathHelper.floor_double(d5 + (double) f);
            final int k = MathHelper.floor_double(d0 - (double) f);
            final int l = MathHelper.floor_double(d0);
            final int i1 = MathHelper.floor_double(d1 - (double) f);
            final int j1 = MathHelper.floor_double(d1 + (double) f);
            final double d2 = x - d5;
            final double d3 = y - d0;
            final double d4 = z - d1;
            final Tessellator tessellator = Tessellator.getInstance();
            final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            worldrenderer.begin(7, DefaultVertexFormats.field_181709_i);

            for (final BlockPos blockpos : BlockPos.getAllInBoxMutable(new BlockPos(i, k, i1), new BlockPos(j, l, j1))) {
                final Block block = world.getBlockState(blockpos.down()).getBlock();

                if (block.getRenderType() != -1 && world.getLightFromNeighbors(blockpos) > 3) {
                    this.func_180549_a(block, x, y, z, blockpos, shadowAlpha, f, d2, d3, d4);
                }
            }

            tessellator.draw();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
        }
    }

    /**
     * Returns the render bus's world object
     */
    private World getWorldFromRenderManager() {
        return this.renderManager.worldObj;
    }

    private void func_180549_a(final Block blockIn, final double p_180549_2_, final double p_180549_4_, final double p_180549_6_, final BlockPos pos, final float p_180549_9_, final float p_180549_10_, final double p_180549_11_, final double p_180549_13_, final double p_180549_15_) {
        if (blockIn.isFullCube()) {
            final Tessellator tessellator = Tessellator.getInstance();
            final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            double d0 = ((double) p_180549_9_ - (p_180549_4_ - ((double) pos.getY() + p_180549_13_)) / 2.0D) * 0.5D * (double) this.getWorldFromRenderManager().getLightBrightness(pos);

            if (d0 >= 0.0D) {
                if (d0 > 1.0D) {
                    d0 = 1.0D;
                }

                final double d1 = (double) pos.getX() + blockIn.getBlockBoundsMinX() + p_180549_11_;
                final double d2 = (double) pos.getX() + blockIn.getBlockBoundsMaxX() + p_180549_11_;
                final double d3 = (double) pos.getY() + blockIn.getBlockBoundsMinY() + p_180549_13_ + 0.015625D;
                final double d4 = (double) pos.getZ() + blockIn.getBlockBoundsMinZ() + p_180549_15_;
                final double d5 = (double) pos.getZ() + blockIn.getBlockBoundsMaxZ() + p_180549_15_;
                final float f = (float) ((p_180549_2_ - d1) / 2.0D / (double) p_180549_10_ + 0.5D);
                final float f1 = (float) ((p_180549_2_ - d2) / 2.0D / (double) p_180549_10_ + 0.5D);
                final float f2 = (float) ((p_180549_6_ - d4) / 2.0D / (double) p_180549_10_ + 0.5D);
                final float f3 = (float) ((p_180549_6_ - d5) / 2.0D / (double) p_180549_10_ + 0.5D);
                worldrenderer.pos(d1, d3, d4).tex(f, f2).func_181666_a(1.0F, 1.0F, 1.0F, (float) d0).endVertex();
                worldrenderer.pos(d1, d3, d5).tex(f, f3).func_181666_a(1.0F, 1.0F, 1.0F, (float) d0).endVertex();
                worldrenderer.pos(d2, d3, d5).tex(f1, f3).func_181666_a(1.0F, 1.0F, 1.0F, (float) d0).endVertex();
                worldrenderer.pos(d2, d3, d4).tex(f1, f2).func_181666_a(1.0F, 1.0F, 1.0F, (float) d0).endVertex();
            }
        }
    }

    /**
     * Renders a white box with the bounds of the AABB translated by the offset. Args: aabb, x, y, z
     */
    public static void renderOffsetAABB(final AxisAlignedBB boundingBox, final double x, final double y, final double z) {
        GlStateManager.disableTexture2D();
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        worldrenderer.setTranslation(x, y, z);
        worldrenderer.begin(7, DefaultVertexFormats.field_181708_h);
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).func_181663_c(0.0F, 0.0F, -1.0F).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).func_181663_c(0.0F, 0.0F, -1.0F).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).func_181663_c(0.0F, 0.0F, -1.0F).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).func_181663_c(0.0F, 0.0F, -1.0F).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).func_181663_c(0.0F, 0.0F, 1.0F).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).func_181663_c(0.0F, 0.0F, 1.0F).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).func_181663_c(0.0F, 0.0F, 1.0F).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).func_181663_c(0.0F, 0.0F, 1.0F).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).func_181663_c(0.0F, -1.0F, 0.0F).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).func_181663_c(0.0F, -1.0F, 0.0F).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).func_181663_c(0.0F, -1.0F, 0.0F).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).func_181663_c(0.0F, -1.0F, 0.0F).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).func_181663_c(0.0F, 1.0F, 0.0F).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).func_181663_c(0.0F, 1.0F, 0.0F).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).func_181663_c(0.0F, 1.0F, 0.0F).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).func_181663_c(0.0F, 1.0F, 0.0F).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).func_181663_c(-1.0F, 0.0F, 0.0F).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).func_181663_c(-1.0F, 0.0F, 0.0F).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).func_181663_c(-1.0F, 0.0F, 0.0F).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).func_181663_c(-1.0F, 0.0F, 0.0F).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).func_181663_c(1.0F, 0.0F, 0.0F).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).func_181663_c(1.0F, 0.0F, 0.0F).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).func_181663_c(1.0F, 0.0F, 0.0F).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).func_181663_c(1.0F, 0.0F, 0.0F).endVertex();
        tessellator.draw();
        worldrenderer.setTranslation(0.0D, 0.0D, 0.0D);
        GlStateManager.enableTexture2D();
    }

    /**
     * Renders the entity's shadow and fire (if its on fire). Args: entity, x, y, z, yaw, partialTickTime
     */
    public void doRenderShadowAndFire(final Entity entityIn, final double x, final double y, final double z, final float yaw, final float partialTicks) {
        if (this.renderManager.options != null) {
            if (this.renderManager.options.field_181151_V && this.shadowSize > 0.0F && !entityIn.isInvisible() && this.renderManager.isRenderShadow()) {
                final double d0 = this.renderManager.getDistanceToCamera(entityIn.posX, entityIn.posY, entityIn.posZ);
                final float f = (float) ((1.0D - d0 / 256.0D) * (double) this.shadowOpaque);

                if (f > 0.0F) {
                    this.renderShadow(entityIn, x, y, z, f, partialTicks);
                }
            }

            if (entityIn.canRenderOnFire() && (!(entityIn instanceof EntityPlayer) || !((EntityPlayer) entityIn).isSpectator())) {
                this.renderEntityOnFire(entityIn, x, y, z, partialTicks);
            }
        }
    }

    /**
     * Returns the font renderer from the set render bus
     */
    public FontRenderer getFontRendererFromRenderManager() {
        return this.renderManager.getFontRenderer();
    }

    /**
     * Renders an entity's name above its head
     */
    protected void renderLivingLabel(final T entityIn, final String str, final double x, final double y, final double z) {
        final double d0 = entityIn.getDistanceSqToEntity(this.renderManager.livingPlayer);

        if (d0 <= (double) (25 * 25)) {
            final FontRenderer fontrenderer = this.getFontRendererFromRenderManager();
            final float f = 1.6F;
            final float f1 = 0.016666668F * f;
            GlStateManager.pushMatrix();
            GlStateManager.translate((float) x + 0.0F, (float) y + entityIn.height + 0.5F, (float) z);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(this.renderManager.playerViewX, Minecraft.getMinecraft().gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F, 0.0F, 0.0F); // The added else if fixes a Minecraft rotation bug in second person
            GlStateManager.scale(-f1, -f1, f1);
            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            final Tessellator tessellator = Tessellator.getInstance();
            final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            final int width = fontrenderer.width(str);
            final int j = width / 2;
            GlStateManager.disableTexture2D();
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            worldrenderer.pos(-j - 1, -1, 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            worldrenderer.pos(-j - 1, 8, 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            worldrenderer.pos(j + 1, 8, 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            worldrenderer.pos(j + 1, -1, 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();
            fontrenderer.drawString(str, -width / 2, 0, 553648127);
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            fontrenderer.drawString(str, -width / 2, 0, -1);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }

    public RenderManager getRenderManager() {
        return this.renderManager;
    }

    public boolean isMultipass() {
        return false;
    }

    public void renderMultipass(final T p_renderMultipass_1_, final double p_renderMultipass_2_, final double p_renderMultipass_4_, final double p_renderMultipass_6_, final float p_renderMultipass_8_, final float p_renderMultipass_9_) {
    }

    public Class getEntityClass() {
        return this.entityClass;
    }

    public void setEntityClass(final Class p_setEntityClass_1_) {
        this.entityClass = p_setEntityClass_1_;
    }

    public ResourceLocation getLocationTextureCustom() {
        return this.locationTextureCustom;
    }

    public void setLocationTextureCustom(final ResourceLocation p_setLocationTextureCustom_1_) {
        this.locationTextureCustom = p_setLocationTextureCustom_1_;
    }

    public static void setModelBipedMain(final RenderBiped p_setModelBipedMain_0_, final ModelBiped p_setModelBipedMain_1_) {
        p_setModelBipedMain_0_.modelBipedMain = p_setModelBipedMain_1_;
    }
}
