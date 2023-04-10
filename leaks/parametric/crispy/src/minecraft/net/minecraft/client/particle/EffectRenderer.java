package net.minecraft.client.particle;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.src.Config;
import net.minecraft.src.Reflector;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EffectRenderer
{
    private static final ResourceLocation particleTextures = new ResourceLocation("textures/particle/particles.png");

    /** Reference to the World object. */
    protected World worldObj;
    private List[][] fxLayers = new List[4][];
    private List particleEmitters = Lists.newArrayList();
    private TextureManager renderer;

    /** RNG. */
    private Random rand = new Random();
    private Map particleTypes = Maps.newHashMap();

    public EffectRenderer(World worldIn, TextureManager rendererIn)
    {
        this.worldObj = worldIn;
        this.renderer = rendererIn;

        for (int var3 = 0; var3 < 4; ++var3)
        {
            this.fxLayers[var3] = new List[2];

            for (int var4 = 0; var4 < 2; ++var4)
            {
                this.fxLayers[var3][var4] = Lists.newArrayList();
            }
        }

        this.registerVanillaParticles();
    }

    private void registerVanillaParticles()
    {
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
        this.registerParticle(EnumParticleTypes.FIREWORKS_SPARK.getParticleID(), new EntityFireworkStarterFX_Factory());
        this.registerParticle(EnumParticleTypes.MOB_APPEARANCE.getParticleID(), new MobAppearance.Factory());
    }

    public void registerParticle(int id, IParticleFactory particleFactory)
    {
        this.particleTypes.put(Integer.valueOf(id), particleFactory);
    }

    public void emitParticleAtEntity(Entity entityIn, EnumParticleTypes particleTypes)
    {
        this.particleEmitters.add(new EntityParticleEmitter(this.worldObj, entityIn, particleTypes));
    }

    /**
     * Spawns the relevant particle according to the particle id.
     *  
     * @param particleId The id of the particle
     */
    public EntityFX spawnEffectParticle(int particleId, double p_178927_2_, double p_178927_4_, double p_178927_6_, double p_178927_8_, double p_178927_10_, double p_178927_12_, int ... p_178927_14_)
    {
        IParticleFactory var15 = (IParticleFactory)this.particleTypes.get(Integer.valueOf(particleId));

        if (var15 != null)
        {
            EntityFX var16 = var15.getEntityFX(particleId, this.worldObj, p_178927_2_, p_178927_4_, p_178927_6_, p_178927_8_, p_178927_10_, p_178927_12_, p_178927_14_);

            if (var16 != null)
            {
                this.addEffect(var16);
                return var16;
            }
        }

        return null;
    }

    public void addEffect(EntityFX effect)
    {
        if (effect != null)
        {
            if (!(effect instanceof EntityFireworkSparkFX) || Config.isFireworkParticles())
            {
                int var2 = effect.getFXLayer();
                int var3 = effect.getAlpha() != 1.0F ? 0 : 1;

                if (this.fxLayers[var2][var3].size() >= 4000)
                {
                    this.fxLayers[var2][var3].remove(0);
                }

                this.fxLayers[var2][var3].add(effect);
            }
        }
    }

    public void updateEffects()
    {
        for (int var4 = 0; var4 < 4; ++var4)
        {
            this.updateEffectLayer(var4);
        }

        ArrayList var41 = Lists.newArrayList();
        Iterator var2 = this.particleEmitters.iterator();

        while (var2.hasNext())
        {
            EntityParticleEmitter var3 = (EntityParticleEmitter)var2.next();
            var3.onUpdate();

            if (var3.isDead)
            {
                var41.add(var3);
            }
        }

        this.particleEmitters.removeAll(var41);
    }

    private void updateEffectLayer(int p_178922_1_)
    {
        for (int var2 = 0; var2 < 2; ++var2)
        {
            this.updateEffectAlphaLayer(this.fxLayers[p_178922_1_][var2]);
        }
    }

    private void updateEffectAlphaLayer(List p_178925_1_)
    {
        ArrayList var2 = Lists.newArrayList();

        for (int var3 = 0; var3 < p_178925_1_.size(); ++var3)
        {
            EntityFX var4 = (EntityFX)p_178925_1_.get(var3);
            this.tickParticle(var4);

            if (var4.isDead)
            {
                var2.add(var4);
            }
        }

        p_178925_1_.removeAll(var2);
    }

    private void tickParticle(final EntityFX p_178923_1_)
    {
        try
        {
            p_178923_1_.onUpdate();
        }
        catch (Throwable var6)
        {
            CrashReport var3 = CrashReport.makeCrashReport(var6, "Ticking Particle");
            CrashReportCategory var4 = var3.makeCategory("Particle being ticked");
            final int var5 = p_178923_1_.getFXLayer();
            var4.addCrashSectionCallable("Particle", new Callable()
            {
                public String call()
                {
                    return p_178923_1_.toString();
                }
            });
            var4.addCrashSectionCallable("Particle Type", new Callable()
            {
                public String call()
                {
                    return var5 == 0 ? "MISC_TEXTURE" : (var5 == 1 ? "TERRAIN_TEXTURE" : (var5 == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + var5));
                }
            });
            throw new ReportedException(var3);
        }
    }

    /**
     * Renders all current particles. Args player, partialTickTime
     */
    public void renderParticles(Entity entityIn, float partialTicks)
    {
        float var3 = ActiveRenderInfo.getRotationX();
        float var4 = ActiveRenderInfo.getRotationZ();
        float var5 = ActiveRenderInfo.getRotationYZ();
        float var6 = ActiveRenderInfo.getRotationXY();
        float var7 = ActiveRenderInfo.getRotationXZ();
        EntityFX.interpPosX = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double)partialTicks;
        EntityFX.interpPosY = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double)partialTicks;
        EntityFX.interpPosZ = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double)partialTicks;
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.alphaFunc(516, 0.003921569F);

        for (int var8_nf = 0; var8_nf < 3; ++var8_nf)
        {
            final int var8 = var8_nf;

            for (int var9 = 0; var9 < 2; ++var9)
            {
                if (!this.fxLayers[var8][var9].isEmpty())
                {
                    switch (var9)
                    {
                        case 0:
                            GlStateManager.depthMask(false);
                            break;

                        case 1:
                            GlStateManager.depthMask(true);
                    }

                    switch (var8)
                    {
                        case 0:
                        default:
                            this.renderer.bindTexture(particleTextures);
                            break;

                        case 1:
                            this.renderer.bindTexture(TextureMap.locationBlocksTexture);
                    }

                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    Tessellator var10 = Tessellator.getInstance();
                    WorldRenderer var11 = var10.getWorldRenderer();
                    var11.startDrawingQuads();

                    for (int var12 = 0; var12 < this.fxLayers[var8][var9].size(); ++var12)
                    {
                        final EntityFX var13 = (EntityFX)this.fxLayers[var8][var9].get(var12);
                        var11.setBrightness(var13.getBrightnessForRender(partialTicks));

                        try
                        {
                            var13.renderParticle(var11, entityIn, partialTicks, var3, var7, var4, var5, var6);
                        }
                        catch (Throwable var18)
                        {
                            CrashReport var15 = CrashReport.makeCrashReport(var18, "Rendering Particle");
                            CrashReportCategory var16 = var15.makeCategory("Particle being rendered");
                            var16.addCrashSectionCallable("Particle", new Callable()
                            {
                                public String call()
                                {
                                    return var13.toString();
                                }
                            });
                            var16.addCrashSectionCallable("Particle Type", new Callable()
                            {
                                public String call()
                                {
                                    return var8 == 0 ? "MISC_TEXTURE" : (var8 == 1 ? "TERRAIN_TEXTURE" : (var8 == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + var8));
                                }
                            });
                            throw new ReportedException(var15);
                        }
                    }

                    var10.draw();
                }
            }
        }

        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1F);
    }

    public void renderLitParticles(Entity entityIn, float p_78872_2_)
    {
        float var3 = 0.017453292F;
        float var4 = MathHelper.cos(entityIn.rotationYaw * 0.017453292F);
        float var5 = MathHelper.sin(entityIn.rotationYaw * 0.017453292F);
        float var6 = -var5 * MathHelper.sin(entityIn.rotationPitch * 0.017453292F);
        float var7 = var4 * MathHelper.sin(entityIn.rotationPitch * 0.017453292F);
        float var8 = MathHelper.cos(entityIn.rotationPitch * 0.017453292F);

        for (int var9 = 0; var9 < 2; ++var9)
        {
            List var10 = this.fxLayers[3][var9];

            if (!var10.isEmpty())
            {
                Tessellator var11 = Tessellator.getInstance();
                WorldRenderer var12 = var11.getWorldRenderer();

                for (int var13 = 0; var13 < var10.size(); ++var13)
                {
                    EntityFX var14 = (EntityFX)var10.get(var13);
                    var12.setBrightness(var14.getBrightnessForRender(p_78872_2_));
                    var14.renderParticle(var12, entityIn, p_78872_2_, var4, var8, var5, var6, var7);
                }
            }
        }
    }

    public void clearEffects(World worldIn)
    {
        this.worldObj = worldIn;

        for (int var2 = 0; var2 < 4; ++var2)
        {
            for (int var3 = 0; var3 < 2; ++var3)
            {
                this.fxLayers[var2][var3].clear();
            }
        }

        this.particleEmitters.clear();
    }

    public void addBlockDestroyEffects(BlockPos pos, IBlockState state)
    {
        boolean notAir;

        if (Reflector.ForgeBlock_addDestroyEffects.exists() && Reflector.ForgeBlock_isAir.exists())
        {
            Block var3 = state.getBlock();
            Reflector.callBoolean(var3, Reflector.ForgeBlock_isAir, new Object[] {this.worldObj, pos});
            notAir = !Reflector.callBoolean(var3, Reflector.ForgeBlock_isAir, new Object[] {this.worldObj, pos}) && !Reflector.callBoolean(var3, Reflector.ForgeBlock_addDestroyEffects, new Object[] {this.worldObj, pos, this});
        }
        else
        {
            notAir = state.getBlock().getMaterial() != Material.air;
        }

        if (notAir)
        {
            state = state.getBlock().getActualState(state, this.worldObj, pos);
            byte var14 = 4;

            for (int var4 = 0; var4 < var14; ++var4)
            {
                for (int var5 = 0; var5 < var14; ++var5)
                {
                    for (int var6 = 0; var6 < var14; ++var6)
                    {
                        double var7 = (double)pos.getX() + ((double)var4 + 0.5D) / (double)var14;
                        double var9 = (double)pos.getY() + ((double)var5 + 0.5D) / (double)var14;
                        double var11 = (double)pos.getZ() + ((double)var6 + 0.5D) / (double)var14;
                        this.addEffect((new EntityDiggingFX(this.worldObj, var7, var9, var11, var7 - (double)pos.getX() - 0.5D, var9 - (double)pos.getY() - 0.5D, var11 - (double)pos.getZ() - 0.5D, state)).func_174846_a(pos));
                    }
                }
            }
        }
    }

    /**
     * Adds block hit particles for the specified block
     *  
     * @param pos The block's coordinates
     * @param side The side the block was hit from
     */
    public void addBlockHitEffects(BlockPos pos, EnumFacing side)
    {
        IBlockState var3 = this.worldObj.getBlockState(pos);
        Block var4 = var3.getBlock();

        if (var4.getRenderType() != -1)
        {
            int var5 = pos.getX();
            int var6 = pos.getY();
            int var7 = pos.getZ();
            float var8 = 0.1F;
            double var9 = (double)var5 + this.rand.nextDouble() * (var4.getBlockBoundsMaxX() - var4.getBlockBoundsMinX() - (double)(var8 * 2.0F)) + (double)var8 + var4.getBlockBoundsMinX();
            double var11 = (double)var6 + this.rand.nextDouble() * (var4.getBlockBoundsMaxY() - var4.getBlockBoundsMinY() - (double)(var8 * 2.0F)) + (double)var8 + var4.getBlockBoundsMinY();
            double var13 = (double)var7 + this.rand.nextDouble() * (var4.getBlockBoundsMaxZ() - var4.getBlockBoundsMinZ() - (double)(var8 * 2.0F)) + (double)var8 + var4.getBlockBoundsMinZ();

            if (side == EnumFacing.DOWN)
            {
                var11 = (double)var6 + var4.getBlockBoundsMinY() - (double)var8;
            }

            if (side == EnumFacing.UP)
            {
                var11 = (double)var6 + var4.getBlockBoundsMaxY() + (double)var8;
            }

            if (side == EnumFacing.NORTH)
            {
                var13 = (double)var7 + var4.getBlockBoundsMinZ() - (double)var8;
            }

            if (side == EnumFacing.SOUTH)
            {
                var13 = (double)var7 + var4.getBlockBoundsMaxZ() + (double)var8;
            }

            if (side == EnumFacing.WEST)
            {
                var9 = (double)var5 + var4.getBlockBoundsMinX() - (double)var8;
            }

            if (side == EnumFacing.EAST)
            {
                var9 = (double)var5 + var4.getBlockBoundsMaxX() + (double)var8;
            }

            this.addEffect((new EntityDiggingFX(this.worldObj, var9, var11, var13, 0.0D, 0.0D, 0.0D, var3)).func_174846_a(pos).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));
        }
    }

    public void moveToAlphaLayer(EntityFX effect)
    {
        this.moveToLayer(effect, 1, 0);
    }

    public void moveToNoAlphaLayer(EntityFX effect)
    {
        this.moveToLayer(effect, 0, 1);
    }

    private void moveToLayer(EntityFX effect, int p_178924_2_, int p_178924_3_)
    {
        for (int var4 = 0; var4 < 4; ++var4)
        {
            if (this.fxLayers[var4][p_178924_2_].contains(effect))
            {
                this.fxLayers[var4][p_178924_2_].remove(effect);
                this.fxLayers[var4][p_178924_3_].add(effect);
            }
        }
    }

    public String getStatistics()
    {
        int var1 = 0;

        for (int var2 = 0; var2 < 4; ++var2)
        {
            for (int var3 = 0; var3 < 2; ++var3)
            {
                var1 += this.fxLayers[var2][var3].size();
            }
        }

        return "" + var1;
    }

    public void addBlockHitEffects(BlockPos pos, MovingObjectPosition target)
    {
        Block block = this.worldObj.getBlockState(pos).getBlock();
        boolean blockAddHitEffects = Reflector.callBoolean(block, Reflector.ForgeBlock_addHitEffects, new Object[] {this.worldObj, target, this});

        if (block != null && !blockAddHitEffects)
        {
            this.addBlockHitEffects(pos, target.sideHit);
        }
    }
}
