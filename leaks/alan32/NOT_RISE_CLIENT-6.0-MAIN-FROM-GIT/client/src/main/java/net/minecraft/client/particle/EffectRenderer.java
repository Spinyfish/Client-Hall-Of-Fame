package net.minecraft.client.particle;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.src.Config;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.optifine.reflect.Reflector;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EffectRenderer {
    private static final ResourceLocation particleTextures = new ResourceLocation("textures/particle/particles.png");

    /**
     * Reference to the World object.
     */
    protected World worldObj;
    private List<EntityFX>[][] fxLayers = new List[4][];
    private final List<EntityParticleEmitter> particleEmitters = Lists.newArrayList();
    private final TextureManager renderer;

    /**
     * RNG.
     */
    private final Random rand = new Random();
    private final Map<Integer, IParticleFactory> particleTypes = Maps.newHashMap();

    public EffectRenderer(final World worldIn, final TextureManager rendererIn) {
        this.worldObj = worldIn;
        this.renderer = rendererIn;

        for (int i = 0; i < 4; ++i) {
            this.fxLayers[i] = new List[2];

            for (int j = 0; j < 2; ++j) {
                this.fxLayers[i][j] = Lists.newArrayList();
            }
        }

        this.registerVanillaParticles();
    }

    private void registerVanillaParticles() {
        this.registerParticle(EnumParticleTypes.EXPLOSION_NORMAL.getParticleID(), new EntityExplodeFX.Factory());
        this.registerParticle(EnumParticleTypes.WATER_BUBBLE.getParticleID(), new EntityBubbleFX.Factory());
        this.registerParticle(EnumParticleTypes.WATER_SPLASH.getParticleID(), new EntitySplashFX.Factory());
        this.registerParticle(EnumParticleTypes.WATER_WAKE.getParticleID(), new EntityFishWakeFX.Factory());
        this.registerParticle(EnumParticleTypes.WATER_DROP.getParticleID(), new EntityRainFX.Factory());
        this.registerParticle(EnumParticleTypes.SUSPENDED.getParticleID(), new EntitySuspendFX.Factory());
        this.registerParticle(EnumParticleTypes.SUSPENDED_DEPTH.getParticleID(), new EntityAuraFX.Factory());
        this.registerParticle(EnumParticleTypes.CRIT.getParticleID(), new EntityCrit2FX.Factory());
        this.registerParticle(EnumParticleTypes.CRIT_MAGIC.getParticleID(), new EntityCrit2FX.MagicFactory());
        this.registerParticle(EnumParticleTypes.SMOKE_NORMAL.getParticleID(), new EntitySmokeFX.Factory());
        this.registerParticle(EnumParticleTypes.SMOKE_LARGE.getParticleID(), new EntityCritFX.Factory());
        this.registerParticle(EnumParticleTypes.SPELL.getParticleID(), new EntitySpellParticleFX.Factory());
        this.registerParticle(EnumParticleTypes.SPELL_INSTANT.getParticleID(), new EntitySpellParticleFX.InstantFactory());
        this.registerParticle(EnumParticleTypes.SPELL_MOB.getParticleID(), new EntitySpellParticleFX.MobFactory());
        this.registerParticle(EnumParticleTypes.SPELL_MOB_AMBIENT.getParticleID(), new EntitySpellParticleFX.AmbientMobFactory());
        this.registerParticle(EnumParticleTypes.SPELL_WITCH.getParticleID(), new EntitySpellParticleFX.WitchFactory());
        this.registerParticle(EnumParticleTypes.DRIP_WATER.getParticleID(), new EntityDropParticleFX.WaterFactory());
        this.registerParticle(EnumParticleTypes.DRIP_LAVA.getParticleID(), new EntityDropParticleFX.LavaFactory());
        this.registerParticle(EnumParticleTypes.VILLAGER_ANGRY.getParticleID(), new EntityHeartFX.AngryVillagerFactory());
        this.registerParticle(EnumParticleTypes.VILLAGER_HAPPY.getParticleID(), new EntityAuraFX.HappyVillagerFactory());
        this.registerParticle(EnumParticleTypes.TOWN_AURA.getParticleID(), new EntityAuraFX.Factory());
        this.registerParticle(EnumParticleTypes.NOTE.getParticleID(), new EntityNoteFX.Factory());
        this.registerParticle(EnumParticleTypes.PORTAL.getParticleID(), new EntityPortalFX.Factory());
        this.registerParticle(EnumParticleTypes.ENCHANTMENT_TABLE.getParticleID(), new EntityEnchantmentTableParticleFX.EnchantmentTable());
        this.registerParticle(EnumParticleTypes.FLAME.getParticleID(), new EntityFlameFX.Factory());
        this.registerParticle(EnumParticleTypes.LAVA.getParticleID(), new EntityLavaFX.Factory());
        this.registerParticle(EnumParticleTypes.FOOTSTEP.getParticleID(), new EntityFootStepFX.Factory());
        this.registerParticle(EnumParticleTypes.CLOUD.getParticleID(), new EntityCloudFX.Factory());
        this.registerParticle(EnumParticleTypes.REDSTONE.getParticleID(), new EntityReddustFX.Factory());
        this.registerParticle(EnumParticleTypes.SNOWBALL.getParticleID(), new EntityBreakingFX.SnowballFactory());
        this.registerParticle(EnumParticleTypes.SNOW_SHOVEL.getParticleID(), new EntitySnowShovelFX.Factory());
        this.registerParticle(EnumParticleTypes.SLIME.getParticleID(), new EntityBreakingFX.SlimeFactory());
        this.registerParticle(EnumParticleTypes.HEART.getParticleID(), new EntityHeartFX.Factory());
        this.registerParticle(EnumParticleTypes.BARRIER.getParticleID(), new Barrier.Factory());
        this.registerParticle(EnumParticleTypes.ITEM_CRACK.getParticleID(), new EntityBreakingFX.Factory());
        this.registerParticle(EnumParticleTypes.BLOCK_CRACK.getParticleID(), new EntityDiggingFX.Factory());
        this.registerParticle(EnumParticleTypes.BLOCK_DUST.getParticleID(), new EntityBlockDustFX.Factory());
        this.registerParticle(EnumParticleTypes.EXPLOSION_HUGE.getParticleID(), new EntityHugeExplodeFX.Factory());
        this.registerParticle(EnumParticleTypes.EXPLOSION_LARGE.getParticleID(), new EntityLargeExplodeFX.Factory());
        this.registerParticle(EnumParticleTypes.FIREWORKS_SPARK.getParticleID(), new EntityFirework.Factory());
        this.registerParticle(EnumParticleTypes.MOB_APPEARANCE.getParticleID(), new MobAppearance.Factory());
    }

    public void registerParticle(final int id, final IParticleFactory particleFactory) {
        this.particleTypes.put(Integer.valueOf(id), particleFactory);
    }

    public void emitParticleAtEntity(final Entity entityIn, final EnumParticleTypes particleTypes) {
        this.particleEmitters.add(new EntityParticleEmitter(this.worldObj, entityIn, particleTypes));
    }

    /**
     * Spawns the relevant particle according to the particle id.
     *
     * @param particleId The id of the particle
     */
    public EntityFX spawnEffectParticle(final int particleId, final double p_178927_2_, final double p_178927_4_, final double p_178927_6_, final double p_178927_8_, final double p_178927_10_, final double p_178927_12_, final int... p_178927_14_) {
        final IParticleFactory iparticlefactory = this.particleTypes.get(Integer.valueOf(particleId));

        if (iparticlefactory != null) {
            final EntityFX entityfx = iparticlefactory.getEntityFX(particleId, this.worldObj, p_178927_2_, p_178927_4_, p_178927_6_, p_178927_8_, p_178927_10_, p_178927_12_, p_178927_14_);

            if (entityfx != null) {
                this.addEffect(entityfx);
                return entityfx;
            }
        }

        return null;
    }

    public void addEffect(final EntityFX effect) {
        if (effect != null) {
            if (!(effect instanceof EntityFirework.SparkFX) || Config.isFireworkParticles()) {
                final int i = effect.getFXLayer();
                final int j = effect.getAlpha() != 1.0F ? 0 : 1;

                if (this.fxLayers[i][j].size() >= 4000) {
                    try {
                        this.fxLayers[i][j].remove(0);
                    } catch (NullPointerException nullPointerException) {
                        System.out.println("Effectrenderer 140");
                    }
                }

                this.fxLayers[i][j].add(effect);
            }
        }
    }

    public void updateEffects() {
        try {
            for (int i = 0; i < 4; ++i) {
                this.updateEffectLayer(i);
            }

            final List<EntityParticleEmitter> list = Lists.newArrayList();

            for (final EntityParticleEmitter entityparticleemitter : this.particleEmitters) {
                entityparticleemitter.onUpdate();

                if (entityparticleemitter.isDead) {
                    list.add(entityparticleemitter);
                }
            }

            this.particleEmitters.removeAll(list);
        } catch (Exception exception) {
            this.particleEmitters.clear();
            fxLayers = new List[4][];

            for (int i = 0; i < 4; ++i) {
                this.fxLayers[i] = new List[2];

                for (int j = 0; j < 2; ++j) {
                    this.fxLayers[i][j] = Lists.newArrayList();
                }
            }

            this.registerVanillaParticles();
        }
    }

    private void updateEffectLayer(final int p_178922_1_) {
        for (int i = 0; i < 2; ++i) {
            this.updateEffectAlphaLayer(this.fxLayers[p_178922_1_][i]);
        }
    }

    private void updateEffectAlphaLayer(final List<EntityFX> entitiesFX) {
        final List<EntityFX> list = Lists.newArrayList();
        final long i = System.currentTimeMillis();
        int j = entitiesFX.size();

        for (final EntityFX entityfx : entitiesFX) {
            this.tickParticle(entityfx);

            if (entityfx.isDead) {
                list.add(entityfx);
            }

            --j;

            if (System.currentTimeMillis() > i + 20L) {
                break;
            }
        }

        if (j > 0) {
            int l = j;

            for (final Iterator iterator = entitiesFX.iterator(); iterator.hasNext() && l > 0; --l) {
                final EntityFX entityfx1 = (EntityFX) iterator.next();
                entityfx1.setDead();
                iterator.remove();
            }
        }

        entitiesFX.removeAll(list);
    }

    private void tickParticle(final EntityFX p_178923_1_) {
        p_178923_1_.onUpdate();
    }

    /**
     * Renders all current particles. Args player, partialTickTime
     */
    public void renderParticles(final Entity entityIn, final float partialTicks) {
        try {
            final float f = ActiveRenderInfo.getRotationX();
            final float f1 = ActiveRenderInfo.getRotationZ();
            final float f2 = ActiveRenderInfo.getRotationYZ();
            final float f3 = ActiveRenderInfo.getRotationXY();
            final float f4 = ActiveRenderInfo.getRotationXZ();
            EntityFX.interpPosX = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double) partialTicks;
            EntityFX.interpPosY = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double) partialTicks;
            EntityFX.interpPosZ = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double) partialTicks;
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.alphaFunc(516, 0.003921569F);

            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 2; ++j) {

                    if (!this.fxLayers[i][j].isEmpty()) {
                        switch (j) {
                            case 0:
                                GlStateManager.depthMask(false);
                                break;

                            case 1:
                                GlStateManager.depthMask(true);
                        }

                        switch (i) {
                            case 0:
                            default:
                                this.renderer.bindTexture(particleTextures);
                                break;

                            case 1:
                                this.renderer.bindTexture(TextureMap.locationBlocksTexture);
                        }

                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                        final Tessellator tessellator = Tessellator.getInstance();
                        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                        worldrenderer.begin(7, DefaultVertexFormats.field_181704_d);

                        for (int k = 0; k < this.fxLayers[i][j].size(); ++k) {
                            final EntityFX entityfx = this.fxLayers[i][j].get(k);

                            if (entityfx == null) continue;

                            entityfx.renderParticle(worldrenderer, entityIn, partialTicks, f, f4, f1, f2, f3);
                        }

                        tessellator.draw();
                    }
                }
            }

            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
            GlStateManager.alphaFunc(516, 0.1F);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void renderLitParticles(final Entity entityIn, final float p_78872_2_) {

        final float f1 = MathHelper.cos(entityIn.rotationYaw * 0.017453292F);
        final float f2 = MathHelper.sin(entityIn.rotationYaw * 0.017453292F);
        final float f3 = -f2 * MathHelper.sin(entityIn.rotationPitch * 0.017453292F);
        final float f4 = f1 * MathHelper.sin(entityIn.rotationPitch * 0.017453292F);
        final float f5 = MathHelper.cos(entityIn.rotationPitch * 0.017453292F);

        for (int i = 0; i < 2; ++i) {
            final List<EntityFX> list = this.fxLayers[3][i];

            if (!list.isEmpty()) {
                final Tessellator tessellator = Tessellator.getInstance();
                final WorldRenderer worldrenderer = tessellator.getWorldRenderer();

                for (final EntityFX entityfx : list) {
                    entityfx.renderParticle(worldrenderer, entityIn, p_78872_2_, f1, f5, f2, f3, f4);
                }
            }
        }
    }

    public void clearEffects(final World worldIn) {
        this.worldObj = worldIn;

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 2; ++j) {
                this.fxLayers[i][j].clear();
            }
        }

        this.particleEmitters.clear();
    }

    public void addBlockDestroyEffects(final BlockPos pos, IBlockState state) {
        final boolean flag;

        if (Reflector.ForgeBlock_addDestroyEffects.exists() && Reflector.ForgeBlock_isAir.exists()) {
            final Block block = state.getBlock();
            flag = !Reflector.callBoolean(block, Reflector.ForgeBlock_isAir, this.worldObj, pos) && !Reflector.callBoolean(block, Reflector.ForgeBlock_addDestroyEffects, this.worldObj, pos, this);
        } else {
            flag = state.getBlock().getMaterial() != Material.air;
        }

        if (flag) {
            state = state.getBlock().getActualState(state, this.worldObj, pos);
            final int l = 4;

            for (int i = 0; i < l; ++i) {
                for (int j = 0; j < l; ++j) {
                    for (int k = 0; k < l; ++k) {
                        final double d0 = (double) pos.getX() + ((double) i + 0.5D) / (double) l;
                        final double d1 = (double) pos.getY() + ((double) j + 0.5D) / (double) l;
                        final double d2 = (double) pos.getZ() + ((double) k + 0.5D) / (double) l;
                        this.addEffect((new EntityDiggingFX(this.worldObj, d0, d1, d2, d0 - (double) pos.getX() - 0.5D, d1 - (double) pos.getY() - 0.5D, d2 - (double) pos.getZ() - 0.5D, state)).func_174846_a(pos));
                    }
                }
            }
        }
    }

    /**
     * Adds block hit particles for the specified block
     *
     * @param pos  The block's coordinates
     * @param side The side the block was hit from
     */
    public void addBlockHitEffects(final BlockPos pos, final EnumFacing side) {
        final IBlockState iblockstate = this.worldObj.getBlockState(pos);
        final Block block = iblockstate.getBlock();

        if (block.getRenderType() != -1) {
            final int i = pos.getX();
            final int j = pos.getY();
            final int k = pos.getZ();
            final float f = 0.1F;
            double d0 = (double) i + this.rand.nextDouble() * (block.getBlockBoundsMaxX() - block.getBlockBoundsMinX() - (double) (f * 2.0F)) + (double) f + block.getBlockBoundsMinX();
            double d1 = (double) j + this.rand.nextDouble() * (block.getBlockBoundsMaxY() - block.getBlockBoundsMinY() - (double) (f * 2.0F)) + (double) f + block.getBlockBoundsMinY();
            double d2 = (double) k + this.rand.nextDouble() * (block.getBlockBoundsMaxZ() - block.getBlockBoundsMinZ() - (double) (f * 2.0F)) + (double) f + block.getBlockBoundsMinZ();

            if (side == EnumFacing.DOWN) {
                d1 = (double) j + block.getBlockBoundsMinY() - (double) f;
            }

            if (side == EnumFacing.UP) {
                d1 = (double) j + block.getBlockBoundsMaxY() + (double) f;
            }

            if (side == EnumFacing.NORTH) {
                d2 = (double) k + block.getBlockBoundsMinZ() - (double) f;
            }

            if (side == EnumFacing.SOUTH) {
                d2 = (double) k + block.getBlockBoundsMaxZ() + (double) f;
            }

            if (side == EnumFacing.WEST) {
                d0 = (double) i + block.getBlockBoundsMinX() - (double) f;
            }

            if (side == EnumFacing.EAST) {
                d0 = (double) i + block.getBlockBoundsMaxX() + (double) f;
            }

            this.addEffect((new EntityDiggingFX(this.worldObj, d0, d1, d2, 0.0D, 0.0D, 0.0D, iblockstate)).func_174846_a(pos).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));
        }
    }

    public void moveToAlphaLayer(final EntityFX effect) {
        this.moveToLayer(effect, 1, 0);
    }

    public void moveToNoAlphaLayer(final EntityFX effect) {
        this.moveToLayer(effect, 0, 1);
    }

    private void moveToLayer(final EntityFX effect, final int p_178924_2_, final int p_178924_3_) {
        for (int i = 0; i < 4; ++i) {
            if (this.fxLayers[i][p_178924_2_].contains(effect)) {
                this.fxLayers[i][p_178924_2_].remove(effect);
                this.fxLayers[i][p_178924_3_].add(effect);
            }
        }
    }

    public String getStatistics() {
        int i = 0;

        try {
            for (int j = 0; j < 4; ++j) {
                for (int k = 0; k < 2; ++k) {
                    i += this.fxLayers[j][k].size();
                }
            }
        } catch (NullPointerException exception) {
            return "" + i;
        }

        return "" + i;
    }

    public void addBlockHitEffects(final BlockPos p_addBlockHitEffects_1_, final MovingObjectPosition p_addBlockHitEffects_2_) {
        final IBlockState iblockstate = this.worldObj.getBlockState(p_addBlockHitEffects_1_);

        if (iblockstate != null) {
            final boolean flag = Reflector.callBoolean(iblockstate.getBlock(), Reflector.ForgeBlock_addHitEffects, this.worldObj, p_addBlockHitEffects_2_, this);

            if (iblockstate != null && !flag) {
                this.addBlockHitEffects(p_addBlockHitEffects_1_, p_addBlockHitEffects_2_.sideHit);
            }
        }
    }
}