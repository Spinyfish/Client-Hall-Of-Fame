package net.minecraft.client.renderer.entity;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ResourceLocation;

public class RenderPlayer extends RendererLivingEntity<AbstractClientPlayer> {
    /**
     * this field is used to indicate the 3-pixel wide arms
     */
    private final boolean smallArms;

    public RenderPlayer(final RenderManager renderManager) {
        this(renderManager, false);
    }

    public RenderPlayer(final RenderManager renderManager, final boolean useSmallArms) {
        super(renderManager, new ModelPlayer(0.0F, useSmallArms), 0.5F);
        this.smallArms = useSmallArms;
        this.addLayer(new LayerBipedArmor(this));
        this.addLayer(new LayerHeldItem(this));
        this.addLayer(new LayerArrow(this));
        this.addLayer(new LayerDeadmau5Head(this));
        this.addLayer(new LayerCape(this));
        this.addLayer(new LayerCustomHead(this.getMainModel().bipedHead));
    }

    public ModelPlayer getMainModel() {
        return (ModelPlayer) super.getMainModel();
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity>) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doe
     *
     * @param entityYaw The yaw rotation of the passed entity
     */
    private boolean setVisibilities = false;
    public void doRender(final AbstractClientPlayer entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks) {
        if (!entity.isUser() || this.renderManager.livingPlayer == entity) {
            double d0 = y;

            if (entity.isSneaking() && !(entity instanceof EntityPlayerSP)) {
                d0 = y - 0.125D;
            }

            this.setModelVisibilities(entity);
            super.doRender(entity, x, d0, z, entityYaw, partialTicks);
        }
    }

    private void setModelVisibilities(final AbstractClientPlayer clientPlayer) {
        if (setVisibilities) {
            return;
        }

        setVisibilities = true;

        final ModelPlayer modelplayer = this.getMainModel();

        if (clientPlayer.isSpectator()) {
            modelplayer.setInvisible(false);
            modelplayer.bipedHead.showModel = true;
            modelplayer.bipedHeadwear.showModel = true;
        } else {
            final ItemStack itemstack = clientPlayer.inventory.getCurrentItem();
            modelplayer.setInvisible(true);
            modelplayer.bipedHeadwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.HAT);
            modelplayer.bipedBodyWear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.JACKET);
            modelplayer.bipedLeftLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG);
            modelplayer.bipedRightLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG);
            modelplayer.bipedLeftArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_SLEEVE);
            modelplayer.bipedRightArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE);
            modelplayer.heldItemLeft = 0;
            modelplayer.aimedBow = false;
            modelplayer.isSneak = clientPlayer.isSneaking();

            if (itemstack == null) {
                modelplayer.heldItemRight = 0;
            } else {
                modelplayer.heldItemRight = 1;

                if (clientPlayer.getItemInUseCount() > 0) {
                    final EnumAction enumaction = itemstack.getItemUseAction();

                    if (enumaction == EnumAction.BLOCK) {
                        modelplayer.heldItemRight = 3;
                    } else if (enumaction == EnumAction.BOW) {
                        modelplayer.aimedBow = true;
                    }
                }
            }
        }
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(final AbstractClientPlayer entity) {
        return entity.getLocationSkin();
    }

    public void transformHeldFull3DItemLayer() {
        GlStateManager.translate(0.0F, 0.1875F, 0.0F);
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(final AbstractClientPlayer entitylivingbaseIn, final float partialTickTime) {
        final float f = 0.9375F;
        GlStateManager.scale(f, f, f);
    }

    protected void renderOffsetLivingLabel(final AbstractClientPlayer entityIn, final double x, double y, final double z, final String str, final float p_177069_9_, final double p_177069_10_) {
        if (p_177069_10_ < 100.0D) {
            final Scoreboard scoreboard = entityIn.getWorldScoreboard();
            final ScoreObjective scoreobjective = scoreboard.getObjectiveInDisplaySlot(2);

            if (scoreobjective != null) {
                final Score score = scoreboard.getValueFromObjective(entityIn.getCommandSenderName(), scoreobjective);
                this.renderLivingLabel(entityIn, score.getScorePoints() + " " + scoreobjective.getDisplayName(), x, y, z);
                y += (float) this.getFontRendererFromRenderManager().FONT_HEIGHT * 1.15F * p_177069_9_;
            }
        }

        super.renderOffsetLivingLabel(entityIn, x, y, z, str, p_177069_9_, p_177069_10_);
    }

    public void renderRightArm(final AbstractClientPlayer clientPlayer) {
        final float f = 1.0F;
        GlStateManager.color(f, f, f);
        final ModelPlayer modelplayer = this.getMainModel();
        this.setModelVisibilities(clientPlayer);
        modelplayer.swingProgress = 0.0F;
        modelplayer.isSneak = false;
        modelplayer.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
        modelplayer.renderRightArm();
    }

    public void renderLeftArm(final AbstractClientPlayer clientPlayer) {
        final float f = 1.0F;
        GlStateManager.color(f, f, f);
        final ModelPlayer modelplayer = this.getMainModel();
        this.setModelVisibilities(clientPlayer);
        modelplayer.isSneak = false;
        modelplayer.swingProgress = 0.0F;
        modelplayer.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
        modelplayer.renderLeftArm();
    }

    /**
     * Sets a simple glTranslate on a LivingEntity.
     */
    protected void renderLivingAt(final AbstractClientPlayer entityLivingBaseIn, final double x, final double y, final double z) {
        if (entityLivingBaseIn.isEntityAlive() && entityLivingBaseIn.isPlayerSleeping()) {
            super.renderLivingAt(entityLivingBaseIn, x + (double) entityLivingBaseIn.renderOffsetX, y + (double) entityLivingBaseIn.renderOffsetY, z + (double) entityLivingBaseIn.renderOffsetZ);
        } else {
            super.renderLivingAt(entityLivingBaseIn, x, y, z);
        }
    }

    protected void rotateCorpse(final AbstractClientPlayer bat, final float p_77043_2_, final float p_77043_3_, final float partialTicks) {
        if (bat.isEntityAlive() && bat.isPlayerSleeping()) {
            GlStateManager.rotate(bat.getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(this.getDeathMaxRotation(bat), 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
        } else {
            super.rotateCorpse(bat, p_77043_2_, p_77043_3_, partialTicks);
        }
    }
}
