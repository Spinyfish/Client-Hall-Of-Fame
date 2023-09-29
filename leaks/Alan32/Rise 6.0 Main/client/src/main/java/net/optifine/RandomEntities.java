package net.optifine;

import com.alan.clients.Client;
import com.alan.clients.newevent.impl.other.WorldChangeEvent;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorRaw;
import net.optifine.util.IntegratedServerUtils;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.ResUtils;
import net.optifine.util.StrUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RandomEntities {
    private static final Map<String, RandomEntityProperties> mapProperties = new HashMap();
    private static boolean active = false;
    private static RenderGlobal renderGlobal;
    private static final RandomEntity randomEntity = new RandomEntity();
    private static TileEntityRendererDispatcher tileEntityRendererDispatcher;
    private static final RandomTileEntity randomTileEntity = new RandomTileEntity();
    private static boolean working = false;
    public static final String SUFFIX_PNG = ".png";
    public static final String SUFFIX_PROPERTIES = ".properties";
    public static final String PREFIX_TEXTURES_ENTITY = "textures/entity/";
    public static final String PREFIX_TEXTURES_PAINTING = "textures/painting/";
    public static final String PREFIX_TEXTURES = "textures/";
    public static final String PREFIX_OPTIFINE_RANDOM = "optifine/random/";
    public static final String PREFIX_MCPATCHER_MOB = "mcpatcher/mob/";
    private static final String[] DEPENDANT_SUFFIXES = new String[]{"_armor", "_eyes", "_exploding", "_shooting", "_fur", "_eyes", "_invulnerable", "_angry", "_tame", "_collar"};
    private static final String PREFIX_DYNAMIC_TEXTURE_HORSE = "horse/";
    private static final String[] HORSE_TEXTURES = (String[]) ReflectorRaw.getFieldValue(null, EntityHorse.class, String[].class, 0);
    private static final String[] HORSE_TEXTURES_ABBR = (String[]) ReflectorRaw.getFieldValue(null, EntityHorse.class, String[].class, 1);

    public static void entityLoaded(final Entity entity, final World world) {
        if (world != null) {
            final DataWatcher datawatcher = entity.getDataWatcher();
            datawatcher.spawnPosition = entity.getPosition();
            datawatcher.spawnBiome = world.getBiomeGenForCoords(datawatcher.spawnPosition);
            final UUID uuid = entity.getUniqueID();

            if (entity instanceof EntityVillager) {
                updateEntityVillager(uuid, (EntityVillager) entity);
            }
        }
    }

    public static void entityUnloaded(final Entity entity, final World world) {
    }

    private static void updateEntityVillager(final UUID uuid, final EntityVillager ev) {
        final Entity entity = IntegratedServerUtils.getEntity(uuid);

        if (entity instanceof EntityVillager) {
            final EntityVillager entityvillager = (EntityVillager) entity;
            final int i = entityvillager.getProfession();
            ev.setProfession(i);
            final int j = Reflector.getFieldValueInt(entityvillager, Reflector.EntityVillager_careerId, 0);
            Reflector.setFieldValueInt(ev, Reflector.EntityVillager_careerId, j);
            final int k = Reflector.getFieldValueInt(entityvillager, Reflector.EntityVillager_careerLevel, 0);
            Reflector.setFieldValueInt(ev, Reflector.EntityVillager_careerLevel, k);
        }
    }

    public static void worldChanged(final World oldWorld, final World newWorld) {
        Client.INSTANCE.getEventBus().handle(new WorldChangeEvent());

        if (newWorld != null) {
            final ConcurrentLinkedQueue list = newWorld.getLoadedEntityList();

            for (final Object o : list) {
                final Entity entity = (Entity) o;
                entityLoaded(entity, newWorld);
            }
        }

        randomEntity.setEntity(null);
        randomTileEntity.setTileEntity(null);
    }

    public static ResourceLocation getTextureLocation(final ResourceLocation loc) {
        if (!active) {
            return loc;
        } else if (working) {
            return loc;
        } else {
            ResourceLocation name;

            try {
                working = true;
                final IRandomEntity irandomentity = getRandomEntityRendered();

                if (irandomentity != null) {
                    String s = loc.getResourcePath();

                    if (s.startsWith("horse/")) {
                        s = getHorseTexturePath(s, "horse/".length());
                    }

                    if (!s.startsWith("textures/entity/") && !s.startsWith("textures/painting/")) {
                        final ResourceLocation resourcelocation2 = loc;
                        return resourcelocation2;
                    }

                    final RandomEntityProperties randomentityproperties = mapProperties.get(s);

                    if (randomentityproperties == null) {
                        final ResourceLocation resourcelocation3 = loc;
                        return resourcelocation3;
                    }

                    final ResourceLocation resourcelocation1 = randomentityproperties.getTextureLocation(loc, irandomentity);
                    return resourcelocation1;
                }

                name = loc;
            } catch (final Exception ignored) {
                return name = loc;
            } finally {
                working = false;
            }

            return name;
        }
    }

    private static String getHorseTexturePath(final String path, final int pos) {
        if (HORSE_TEXTURES != null && HORSE_TEXTURES_ABBR != null) {
            for (int i = 0; i < HORSE_TEXTURES_ABBR.length; ++i) {
                final String s = HORSE_TEXTURES_ABBR[i];

                if (path.startsWith(s, pos)) {
                    return HORSE_TEXTURES[i];
                }
            }

            return path;
        } else {
            return path;
        }
    }

    private static IRandomEntity getRandomEntityRendered() {
        if (renderGlobal.renderedEntity != null) {
            randomEntity.setEntity(renderGlobal.renderedEntity);
            return randomEntity;
        } else {
            if (tileEntityRendererDispatcher.tileEntityRendered != null) {
                final TileEntity tileentity = tileEntityRendererDispatcher.tileEntityRendered;

                if (tileentity.getWorld() != null) {
                    randomTileEntity.setTileEntity(tileentity);
                    return randomTileEntity;
                }
            }

            return null;
        }
    }

    private static RandomEntityProperties makeProperties(final ResourceLocation loc, final boolean mcpatcher) {
        final String s = loc.getResourcePath();
        final ResourceLocation resourcelocation = getLocationProperties(loc, mcpatcher);

        if (resourcelocation != null) {
            final RandomEntityProperties randomentityproperties = parseProperties(resourcelocation, loc);

            if (randomentityproperties != null) {
                return randomentityproperties;
            }
        }

        final ResourceLocation[] aresourcelocation = getLocationsVariants(loc, mcpatcher);
        return aresourcelocation == null ? null : new RandomEntityProperties(s, aresourcelocation);
    }

    private static RandomEntityProperties parseProperties(final ResourceLocation propLoc, final ResourceLocation resLoc) {
        try {
            final String s = propLoc.getResourcePath();
            dbg(resLoc.getResourcePath() + ", properties: " + s);
            final InputStream inputstream = Config.getResourceStream(propLoc);

            if (inputstream == null) {
                warn("Properties not found: " + s);
                return null;
            } else {
                final Properties properties = new PropertiesOrdered();
                properties.load(inputstream);
                inputstream.close();
                final RandomEntityProperties randomentityproperties = new RandomEntityProperties(properties, s, resLoc);
                return !randomentityproperties.isValid(s) ? null : randomentityproperties;
            }
        } catch (final FileNotFoundException var6) {
            warn("File not found: " + resLoc.getResourcePath());
            return null;
        } catch (final IOException ioexception) {
            ioexception.printStackTrace();
            return null;
        }
    }

    private static ResourceLocation getLocationProperties(final ResourceLocation loc, final boolean mcpatcher) {
        final ResourceLocation resourcelocation = getLocationRandom(loc, mcpatcher);

        if (resourcelocation == null) {
            return null;
        } else {
            final String s = resourcelocation.getResourceDomain();
            final String s1 = resourcelocation.getResourcePath();
            final String s2 = StrUtils.removeSuffix(s1, ".png");
            final String s3 = s2 + ".properties";
            final ResourceLocation resourcelocation1 = new ResourceLocation(s, s3);

            if (Config.hasResource(resourcelocation1)) {
                return resourcelocation1;
            } else {
                final String s4 = getParentTexturePath(s2);

                if (s4 == null) {
                    return null;
                } else {
                    final ResourceLocation resourcelocation2 = new ResourceLocation(s, s4 + ".properties");
                    return Config.hasResource(resourcelocation2) ? resourcelocation2 : null;
                }
            }
        }
    }

    protected static ResourceLocation getLocationRandom(final ResourceLocation loc, final boolean mcpatcher) {
        final String s = loc.getResourceDomain();
        final String s1 = loc.getResourcePath();
        String s2 = "textures/";
        String s3 = "optifine/random/";

        if (mcpatcher) {
            s2 = "textures/entity/";
            s3 = "mcpatcher/mob/";
        }

        if (!s1.startsWith(s2)) {
            return null;
        } else {
            final String s4 = StrUtils.replacePrefix(s1, s2, s3);
            return new ResourceLocation(s, s4);
        }
    }

    private static String getPathBase(final String pathRandom) {
        return pathRandom.startsWith("optifine/random/") ? StrUtils.replacePrefix(pathRandom, "optifine/random/", "textures/") : (pathRandom.startsWith("mcpatcher/mob/") ? StrUtils.replacePrefix(pathRandom, "mcpatcher/mob/", "textures/entity/") : null);
    }

    protected static ResourceLocation getLocationIndexed(final ResourceLocation loc, final int index) {
        if (loc == null) {
            return null;
        } else {
            final String s = loc.getResourcePath();
            final int i = s.lastIndexOf(46);

            if (i < 0) {
                return null;
            } else {
                final String s1 = s.substring(0, i);
                final String s2 = s.substring(i);
                final String s3 = s1 + index + s2;
                final ResourceLocation resourcelocation = new ResourceLocation(loc.getResourceDomain(), s3);
                return resourcelocation;
            }
        }
    }

    private static String getParentTexturePath(final String path) {
        for (int i = 0; i < DEPENDANT_SUFFIXES.length; ++i) {
            final String s = DEPENDANT_SUFFIXES[i];

            if (path.endsWith(s)) {
                final String s1 = StrUtils.removeSuffix(path, s);
                return s1;
            }
        }

        return null;
    }

    private static ResourceLocation[] getLocationsVariants(final ResourceLocation loc, final boolean mcpatcher) {
        final List list = new ArrayList();
        list.add(loc);
        final ResourceLocation resourcelocation = getLocationRandom(loc, mcpatcher);

        if (resourcelocation == null) {
            return null;
        } else {
            for (int i = 1; i < list.size() + 10; ++i) {
                final int j = i + 1;
                final ResourceLocation resourcelocation1 = getLocationIndexed(resourcelocation, j);

                if (Config.hasResource(resourcelocation1)) {
                    list.add(resourcelocation1);
                }
            }

            if (list.size() <= 1) {
                return null;
            } else {
                final ResourceLocation[] aresourcelocation = (ResourceLocation[]) list.toArray(new ResourceLocation[list.size()]);
                dbg(loc.getResourcePath() + ", variants: " + aresourcelocation.length);
                return aresourcelocation;
            }
        }
    }

    public static void update() {
        mapProperties.clear();
        active = false;

        if (Config.isRandomEntities()) {
            initialize();
        }
    }

    private static void initialize() {
        renderGlobal = Config.getRenderGlobal();
        tileEntityRendererDispatcher = TileEntityRendererDispatcher.instance;
        final String[] astring = new String[]{"optifine/random/", "mcpatcher/mob/"};
        final String[] astring1 = new String[]{".png", ".properties"};
        final String[] astring2 = ResUtils.collectFiles(astring, astring1);
        final Set set = new HashSet();

        for (int i = 0; i < astring2.length; ++i) {
            String s = astring2[i];
            s = StrUtils.removeSuffix(s, astring1);
            s = StrUtils.trimTrailing(s, "0123456789");
            s = s + ".png";
            final String s1 = getPathBase(s);

            if (!set.contains(s1)) {
                set.add(s1);
                final ResourceLocation resourcelocation = new ResourceLocation(s1);

                if (Config.hasResource(resourcelocation)) {
                    RandomEntityProperties randomentityproperties = mapProperties.get(s1);

                    if (randomentityproperties == null) {
                        randomentityproperties = makeProperties(resourcelocation, false);

                        if (randomentityproperties == null) {
                            randomentityproperties = makeProperties(resourcelocation, true);
                        }

                        if (randomentityproperties != null) {
                            mapProperties.put(s1, randomentityproperties);
                        }
                    }
                }
            }
        }

        active = !mapProperties.isEmpty();
    }

    public static void dbg(final String str) {
        Config.dbg("RandomEntities: " + str);
    }

    public static void warn(final String str) {
        Config.warn("RandomEntities: " + str);
    }
}
