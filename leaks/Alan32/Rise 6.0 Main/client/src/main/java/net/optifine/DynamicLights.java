package net.optifine;

import net.minecraft.block.Block;
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
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.optifine.config.ConnectedParser;
import net.optifine.config.EntityClassLocator;
import net.optifine.config.IObjectLocator;
import net.optifine.config.ItemLocator;
import net.optifine.reflect.ReflectorForge;
import net.optifine.util.PropertiesOrdered;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DynamicLights {
    private static final DynamicLightsMap mapDynamicLights = new DynamicLightsMap();
    private static final Map<Class, Integer> mapEntityLightLevels = new HashMap();
    private static final Map<Item, Integer> mapItemLightLevels = new HashMap();
    private static long timeUpdateMs = 0L;
    private static final double MAX_DIST = 7.5D;
    private static final double MAX_DIST_SQ = 56.25D;
    private static final int LIGHT_LEVEL_MAX = 15;
    private static final int LIGHT_LEVEL_FIRE = 15;
    private static final int LIGHT_LEVEL_BLAZE = 10;
    private static final int LIGHT_LEVEL_MAGMA_CUBE = 8;
    private static final int LIGHT_LEVEL_MAGMA_CUBE_CORE = 13;
    private static final int LIGHT_LEVEL_GLOWSTONE_DUST = 8;
    private static final int LIGHT_LEVEL_PRISMARINE_CRYSTALS = 8;
    private static boolean initialized;

    public static void entityAdded(final Entity entityIn, final RenderGlobal renderGlobal) {
    }

    public static void entityRemoved(final Entity entityIn, final RenderGlobal renderGlobal) {
        synchronized (mapDynamicLights) {
            final DynamicLight dynamiclight = mapDynamicLights.remove(entityIn.getEntityId());

            if (dynamiclight != null) {
                dynamiclight.updateLitChunks(renderGlobal);
            }
        }
    }

    public static void update(final RenderGlobal renderGlobal) {
        final long i = System.currentTimeMillis();

        if (i >= timeUpdateMs + 50L) {
            timeUpdateMs = i;

            if (!initialized) {
                initialize();
            }

            synchronized (mapDynamicLights) {
                updateMapDynamicLights(renderGlobal);

                if (mapDynamicLights.size() > 0) {
                    final List<DynamicLight> list = mapDynamicLights.valueList();

                    for (int j = 0; j < list.size(); ++j) {
                        final DynamicLight dynamiclight = list.get(j);
                        dynamiclight.update(renderGlobal);
                    }
                }
            }
        }
    }

    private static void initialize() {
        initialized = true;
        mapEntityLightLevels.clear();
        mapItemLightLevels.clear();
        final String[] astring = ReflectorForge.getForgeModIds();

        for (int i = 0; i < astring.length; ++i) {
            final String s = astring[i];

            try {
                final ResourceLocation resourcelocation = new ResourceLocation(s, "optifine/dynamic_lights.properties");
                final InputStream inputstream = Config.getResourceStream(resourcelocation);
                loadModConfiguration(inputstream, resourcelocation.toString(), s);
            } catch (final IOException var5) {
            }
        }

        if (mapEntityLightLevels.size() > 0) {
            Config.dbg("DynamicLights entities: " + mapEntityLightLevels.size());
        }

        if (mapItemLightLevels.size() > 0) {
            Config.dbg("DynamicLights items: " + mapItemLightLevels.size());
        }
    }

    private static void loadModConfiguration(final InputStream in, final String path, final String modId) {
        if (in != null) {
            try {
                final Properties properties = new PropertiesOrdered();
                properties.load(in);
                in.close();
                Config.dbg("DynamicLights: Parsing " + path);
                final ConnectedParser connectedparser = new ConnectedParser("DynamicLights");
                loadModLightLevels(properties.getProperty("entities"), mapEntityLightLevels, new EntityClassLocator(), connectedparser, path, modId);
                loadModLightLevels(properties.getProperty("items"), mapItemLightLevels, new ItemLocator(), connectedparser, path, modId);
            } catch (final IOException var5) {
                Config.warn("DynamicLights: Error reading " + path);
            }
        }
    }

    private static void loadModLightLevels(final String prop, final Map mapLightLevels, final IObjectLocator ol, final ConnectedParser cp, final String path, final String modId) {
        if (prop != null) {
            final String[] astring = Config.tokenize(prop, " ");

            for (int i = 0; i < astring.length; ++i) {
                final String s = astring[i];
                final String[] astring1 = Config.tokenize(s, ":");

                if (astring1.length != 2) {
                    cp.warn("Invalid entry: " + s + ", in:" + path);
                } else {
                    final String s1 = astring1[0];
                    final String s2 = astring1[1];
                    final String s3 = modId + ":" + s1;
                    final ResourceLocation resourcelocation = new ResourceLocation(s3);
                    final Object object = ol.getObject(resourcelocation);

                    if (object == null) {
                        cp.warn("Object not found: " + s3);
                    } else {
                        final int j = cp.parseInt(s2, -1);

                        if (j >= 0 && j <= 15) {
                            mapLightLevels.put(object, new Integer(j));
                        } else {
                            cp.warn("Invalid light level: " + s);
                        }
                    }
                }
            }
        }
    }

    private static void updateMapDynamicLights(final RenderGlobal renderGlobal) {
        final World world = renderGlobal.getWorld();

        if (world != null) {
            for (final Entity entity : world.getLoadedEntityList()) {
                final int i = getLightLevel(entity);

                if (i > 0) {
                    final int j = entity.getEntityId();
                    DynamicLight dynamiclight = mapDynamicLights.get(j);

                    if (dynamiclight == null) {
                        dynamiclight = new DynamicLight(entity);
                        mapDynamicLights.put(j, dynamiclight);
                    }
                } else {
                    final int k = entity.getEntityId();
                    final DynamicLight dynamiclight1 = mapDynamicLights.remove(k);

                    if (dynamiclight1 != null) {
                        dynamiclight1.updateLitChunks(renderGlobal);
                    }
                }
            }
        }
    }

    public static int getCombinedLight(final BlockPos pos, int combinedLight) {
        final double d0 = getLightLevel(pos);
        combinedLight = getCombinedLight(d0, combinedLight);
        return combinedLight;
    }

    public static int getCombinedLight(final Entity entity, int combinedLight) {
        final double d0 = getLightLevel(entity);
        combinedLight = getCombinedLight(d0, combinedLight);
        return combinedLight;
    }

    public static int getCombinedLight(final double lightPlayer, int combinedLight) {
        if (lightPlayer > 0.0D) {
            final int i = (int) (lightPlayer * 16.0D);
            final int j = combinedLight & 255;

            if (i > j) {
                combinedLight = combinedLight & -256;
                combinedLight = combinedLight | i;
            }
        }

        return combinedLight;
    }

    public static double getLightLevel(final BlockPos pos) {
        double d0 = 0.0D;

        synchronized (mapDynamicLights) {
            final List<DynamicLight> list = mapDynamicLights.valueList();

            for (int i = 0; i < list.size(); ++i) {
                final DynamicLight dynamiclight = list.get(i);
                int j = dynamiclight.getLastLightLevel();

                if (j > 0) {
                    final double d1 = dynamiclight.getLastPosX();
                    final double d2 = dynamiclight.getLastPosY();
                    final double d3 = dynamiclight.getLastPosZ();
                    final double d4 = (double) pos.getX() - d1;
                    final double d5 = (double) pos.getY() - d2;
                    final double d6 = (double) pos.getZ() - d3;
                    double d7 = d4 * d4 + d5 * d5 + d6 * d6;

                    if (dynamiclight.isUnderwater() && !Config.isClearWater()) {
                        j = Config.limit(j - 2, 0, 15);
                        d7 *= 2.0D;
                    }

                    if (d7 <= 56.25D) {
                        final double d8 = Math.sqrt(d7);
                        final double d9 = 1.0D - d8 / 7.5D;
                        final double d10 = d9 * (double) j;

                        if (d10 > d0) {
                            d0 = d10;
                        }
                    }
                }
            }
        }

        final double d11 = Config.limit(d0, 0.0D, 15.0D);
        return d11;
    }

    public static int getLightLevel(final ItemStack itemStack) {
        if (itemStack == null) {
            return 0;
        } else {
            final Item item = itemStack.getItem();

            if (item instanceof ItemBlock) {
                final ItemBlock itemblock = (ItemBlock) item;
                final Block block = itemblock.getBlock();

                if (block != null) {
                    return block.getLightValue();
                }
            }

            if (item == Items.lava_bucket) {
                return Blocks.lava.getLightValue();
            } else if (item != Items.blaze_rod && item != Items.blaze_powder) {
                if (item == Items.glowstone_dust) {
                    return 8;
                } else if (item == Items.prismarine_crystals) {
                    return 8;
                } else if (item == Items.magma_cream) {
                    return 8;
                } else if (item == Items.nether_star) {
                    return Blocks.beacon.getLightValue() / 2;
                } else {
                    if (!mapItemLightLevels.isEmpty()) {
                        final Integer integer = mapItemLightLevels.get(item);

                        if (integer != null) {
                            return integer.intValue();
                        }
                    }

                    return 0;
                }
            } else {
                return 10;
            }
        }
    }

    public static int getLightLevel(final Entity entity) {
        if (entity == Config.getMinecraft().getRenderViewEntity() && !Config.isDynamicHandLight()) {
            return 0;
        } else {
            if (entity instanceof EntityPlayer) {
                final EntityPlayer entityplayer = (EntityPlayer) entity;

                if (entityplayer.isSpectator()) {
                    return 0;
                }
            }

            if (entity.isBurning()) {
                return 15;
            } else {
                if (!mapEntityLightLevels.isEmpty()) {
                    final Integer integer = mapEntityLightLevels.get(entity.getClass());

                    if (integer != null) {
                        return integer.intValue();
                    }
                }

                if (entity instanceof EntityFireball) {
                    return 15;
                } else if (entity instanceof EntityTNTPrimed) {
                    return 15;
                } else if (entity instanceof EntityBlaze) {
                    final EntityBlaze entityblaze = (EntityBlaze) entity;
                    return entityblaze.func_70845_n() ? 15 : 10;
                } else if (entity instanceof EntityMagmaCube) {
                    final EntityMagmaCube entitymagmacube = (EntityMagmaCube) entity;
                    return (double) entitymagmacube.squishFactor > 0.6D ? 13 : 8;
                } else {
                    if (entity instanceof EntityCreeper) {
                        final EntityCreeper entitycreeper = (EntityCreeper) entity;

                        if ((double) entitycreeper.getCreeperFlashIntensity(0.0F) > 0.001D) {
                            return 15;
                        }
                    }

                    if (entity instanceof EntityLivingBase) {
                        final EntityLivingBase entitylivingbase = (EntityLivingBase) entity;
                        final ItemStack itemstack2 = entitylivingbase.getHeldItem();
                        final int i = getLightLevel(itemstack2);
                        final ItemStack itemstack1 = entitylivingbase.getEquipmentInSlot(4);
                        final int j = getLightLevel(itemstack1);
                        return Math.max(i, j);
                    } else if (entity instanceof EntityItem) {
                        final EntityItem entityitem = (EntityItem) entity;
                        final ItemStack itemstack = getItemStack(entityitem);
                        return getLightLevel(itemstack);
                    } else {
                        return 0;
                    }
                }
            }
        }
    }

    public static void removeLights(final RenderGlobal renderGlobal) {
        synchronized (mapDynamicLights) {
            final List<DynamicLight> list = mapDynamicLights.valueList();

            for (int i = 0; i < list.size(); ++i) {
                final DynamicLight dynamiclight = list.get(i);
                dynamiclight.updateLitChunks(renderGlobal);
            }

            mapDynamicLights.clear();
        }
    }

    public static void clear() {
        synchronized (mapDynamicLights) {
            mapDynamicLights.clear();
        }
    }

    public static int getCount() {
        synchronized (mapDynamicLights) {
            return mapDynamicLights.size();
        }
    }

    public static ItemStack getItemStack(final EntityItem entityItem) {
        final ItemStack itemstack = entityItem.getDataWatcher().getWatchableObjectItemStack(10);
        return itemstack;
    }
}
