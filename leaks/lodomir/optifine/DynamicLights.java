/*
 * Decompiled with CFR 0.152.
 */
package optifine;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import optifine.Config;
import optifine.DynamicLight;
import optifine.IntegerCache;

public class DynamicLights {
    private static Map<Integer, DynamicLight> mapDynamicLights = new HashMap<Integer, DynamicLight>();
    private static long timeUpdateMs = 0L;
    private static final double MAX_DIST = 7.5;
    private static final double MAX_DIST_SQ = 56.25;
    private static final int LIGHT_LEVEL_MAX = 15;
    private static final int LIGHT_LEVEL_FIRE = 15;
    private static final int LIGHT_LEVEL_BLAZE = 10;
    private static final int LIGHT_LEVEL_MAGMA_CUBE = 8;
    private static final int LIGHT_LEVEL_MAGMA_CUBE_CORE = 13;
    private static final int LIGHT_LEVEL_GLOWSTONE_DUST = 8;
    private static final int LIGHT_LEVEL_PRISMARINE_CRYSTALS = 8;

    public static void entityAdded(Entity p_entityAdded_0_, RenderGlobal p_entityAdded_1_) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void entityRemoved(Entity p_entityRemoved_0_, RenderGlobal p_entityRemoved_1_) {
        Map<Integer, DynamicLight> map = mapDynamicLights;
        synchronized (map) {
            DynamicLight dynamiclight = mapDynamicLights.remove(IntegerCache.valueOf(p_entityRemoved_0_.getEntityId()));
            if (dynamiclight != null) {
                dynamiclight.updateLitChunks(p_entityRemoved_1_);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void update(RenderGlobal p_update_0_) {
        long i = System.currentTimeMillis();
        if (i >= timeUpdateMs + 50L) {
            timeUpdateMs = i;
            Map<Integer, DynamicLight> map = mapDynamicLights;
            synchronized (map) {
                DynamicLights.updateMapDynamicLights(p_update_0_);
                if (mapDynamicLights.size() > 0) {
                    for (DynamicLight dynamiclight : mapDynamicLights.values()) {
                        dynamiclight.update(p_update_0_);
                    }
                }
            }
        }
    }

    private static void updateMapDynamicLights(RenderGlobal p_updateMapDynamicLights_0_) {
        WorldClient world = p_updateMapDynamicLights_0_.getWorld();
        if (world != null) {
            for (Entity entity : world.getLoadedEntityList()) {
                int i = DynamicLights.getLightLevel(entity);
                if (i > 0) {
                    Integer integer = IntegerCache.valueOf(entity.getEntityId());
                    DynamicLight dynamiclight = mapDynamicLights.get(integer);
                    if (dynamiclight != null) continue;
                    dynamiclight = new DynamicLight(entity);
                    mapDynamicLights.put(integer, dynamiclight);
                    continue;
                }
                Integer integer1 = IntegerCache.valueOf(entity.getEntityId());
                DynamicLight dynamiclight1 = mapDynamicLights.remove(integer1);
                if (dynamiclight1 == null) continue;
                dynamiclight1.updateLitChunks(p_updateMapDynamicLights_0_);
            }
        }
    }

    public static int getCombinedLight(BlockPos p_getCombinedLight_0_, int p_getCombinedLight_1_) {
        double d0 = DynamicLights.getLightLevel(p_getCombinedLight_0_);
        p_getCombinedLight_1_ = DynamicLights.getCombinedLight(d0, p_getCombinedLight_1_);
        return p_getCombinedLight_1_;
    }

    public static int getCombinedLight(Entity p_getCombinedLight_0_, int p_getCombinedLight_1_) {
        double d0 = DynamicLights.getLightLevel(p_getCombinedLight_0_);
        p_getCombinedLight_1_ = DynamicLights.getCombinedLight(d0, p_getCombinedLight_1_);
        return p_getCombinedLight_1_;
    }

    public static int getCombinedLight(double p_getCombinedLight_0_, int p_getCombinedLight_2_) {
        int j;
        int i;
        if (p_getCombinedLight_0_ > 0.0 && (i = (int)(p_getCombinedLight_0_ * 16.0)) > (j = p_getCombinedLight_2_ & 0xFF)) {
            p_getCombinedLight_2_ &= 0xFFFFFF00;
            p_getCombinedLight_2_ |= i;
        }
        return p_getCombinedLight_2_;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static double getLightLevel(BlockPos p_getLightLevel_0_) {
        double d0 = 0.0;
        Map<Integer, DynamicLight> map = mapDynamicLights;
        synchronized (map) {
            for (DynamicLight dynamiclight : mapDynamicLights.values()) {
                double d8;
                double d9;
                double d10;
                int i = dynamiclight.getLastLightLevel();
                if (i <= 0) continue;
                double d1 = dynamiclight.getLastPosX();
                double d2 = dynamiclight.getLastPosY();
                double d3 = dynamiclight.getLastPosZ();
                double d4 = (double)p_getLightLevel_0_.getX() - d1;
                double d5 = (double)p_getLightLevel_0_.getY() - d2;
                double d6 = (double)p_getLightLevel_0_.getZ() - d3;
                double d7 = d4 * d4 + d5 * d5 + d6 * d6;
                if (dynamiclight.isUnderwater() && !Config.isClearWater()) {
                    i = Config.limit(i - 2, 0, 15);
                    d7 *= 2.0;
                }
                if (!(d7 <= 56.25) || !((d10 = (d9 = 1.0 - (d8 = Math.sqrt(d7)) / 7.5) * (double)i) > d0)) continue;
                d0 = d10;
            }
        }
        double d11 = Config.limit(d0, 0.0, 15.0);
        return d11;
    }

    public static int getLightLevel(ItemStack p_getLightLevel_0_) {
        ItemBlock itemblock;
        Block block;
        if (p_getLightLevel_0_ == null) {
            return 0;
        }
        Item item = p_getLightLevel_0_.getItem();
        if (item instanceof ItemBlock && (block = (itemblock = (ItemBlock)item).getBlock()) != null) {
            return block.getLightValue();
        }
        return item == Items.lava_bucket ? Blocks.lava.getLightValue() : (item != Items.blaze_rod && item != Items.blaze_powder ? (item == Items.glowstone_dust ? 8 : (item == Items.prismarine_crystals ? 8 : (item == Items.magma_cream ? 8 : (item == Items.nether_star ? Blocks.beacon.getLightValue() / 2 : 0)))) : 10);
    }

    public static int getLightLevel(Entity p_getLightLevel_0_) {
        EntityCreeper entitycreeper;
        EntityPlayer entityplayer;
        if (p_getLightLevel_0_ == Config.getMinecraft().getRenderViewEntity() && !Config.isDynamicHandLight()) {
            return 0;
        }
        if (p_getLightLevel_0_ instanceof EntityPlayer && (entityplayer = (EntityPlayer)p_getLightLevel_0_).isSpectator()) {
            return 0;
        }
        if (p_getLightLevel_0_.isBurning()) {
            return 15;
        }
        if (p_getLightLevel_0_ instanceof EntityFireball) {
            return 15;
        }
        if (p_getLightLevel_0_ instanceof EntityTNTPrimed) {
            return 15;
        }
        if (p_getLightLevel_0_ instanceof EntityBlaze) {
            EntityBlaze entityblaze = (EntityBlaze)p_getLightLevel_0_;
            return entityblaze.func_70845_n() ? 15 : 10;
        }
        if (p_getLightLevel_0_ instanceof EntityMagmaCube) {
            EntityMagmaCube entitymagmacube = (EntityMagmaCube)p_getLightLevel_0_;
            return (double)entitymagmacube.squishFactor > 0.6 ? 13 : 8;
        }
        if (p_getLightLevel_0_ instanceof EntityCreeper && (double)(entitycreeper = (EntityCreeper)p_getLightLevel_0_).getCreeperFlashIntensity(0.0f) > 0.001) {
            return 15;
        }
        if (p_getLightLevel_0_ instanceof EntityLivingBase) {
            EntityLivingBase entitylivingbase = (EntityLivingBase)p_getLightLevel_0_;
            ItemStack itemstack2 = entitylivingbase.getHeldItem();
            int i = DynamicLights.getLightLevel(itemstack2);
            ItemStack itemstack1 = entitylivingbase.getEquipmentInSlot(4);
            int j = DynamicLights.getLightLevel(itemstack1);
            return Math.max(i, j);
        }
        if (p_getLightLevel_0_ instanceof EntityItem) {
            EntityItem entityitem = (EntityItem)p_getLightLevel_0_;
            ItemStack itemstack = DynamicLights.getItemStack(entityitem);
            return DynamicLights.getLightLevel(itemstack);
        }
        return 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void removeLights(RenderGlobal p_removeLights_0_) {
        Map<Integer, DynamicLight> map = mapDynamicLights;
        synchronized (map) {
            Collection<DynamicLight> collection = mapDynamicLights.values();
            Iterator<DynamicLight> iterator = collection.iterator();
            while (iterator.hasNext()) {
                DynamicLight dynamiclight = iterator.next();
                iterator.remove();
                dynamiclight.updateLitChunks(p_removeLights_0_);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void clear() {
        Map<Integer, DynamicLight> map = mapDynamicLights;
        synchronized (map) {
            mapDynamicLights.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static int getCount() {
        Map<Integer, DynamicLight> map = mapDynamicLights;
        synchronized (map) {
            return mapDynamicLights.size();
        }
    }

    public static ItemStack getItemStack(EntityItem p_getItemStack_0_) {
        ItemStack itemstack = p_getItemStack_0_.getDataWatcher().getWatchableObjectItemStack(10);
        return itemstack;
    }
}

