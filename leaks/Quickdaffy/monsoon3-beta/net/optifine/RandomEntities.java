/*
 * Decompiled with CFR 0.152.
 */
package net.optifine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
import net.optifine.IRandomEntity;
import net.optifine.RandomEntity;
import net.optifine.RandomEntityProperties;
import net.optifine.RandomTileEntity;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorRaw;
import net.optifine.util.IntegratedServerUtils;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.ResUtils;
import net.optifine.util.StrUtils;

public class RandomEntities {
    private static Map<String, RandomEntityProperties> mapProperties = new HashMap<String, RandomEntityProperties>();
    private static boolean active = false;
    private static RenderGlobal renderGlobal;
    private static RandomEntity randomEntity;
    private static TileEntityRendererDispatcher tileEntityRendererDispatcher;
    private static RandomTileEntity randomTileEntity;
    private static boolean working;
    public static final String SUFFIX_PNG = ".png";
    public static final String SUFFIX_PROPERTIES = ".properties";
    public static final String PREFIX_TEXTURES_ENTITY = "textures/entity/";
    public static final String PREFIX_TEXTURES_PAINTING = "textures/painting/";
    public static final String PREFIX_TEXTURES = "textures/";
    public static final String PREFIX_OPTIFINE_RANDOM = "optifine/random/";
    public static final String PREFIX_MCPATCHER_MOB = "mcpatcher/mob/";
    private static final String[] DEPENDANT_SUFFIXES;
    private static final String PREFIX_DYNAMIC_TEXTURE_HORSE = "horse/";
    private static final String[] HORSE_TEXTURES;
    private static final String[] HORSE_TEXTURES_ABBR;

    public static void entityLoaded(Entity entity, World world) {
        if (world != null) {
            DataWatcher datawatcher = entity.getDataWatcher();
            datawatcher.spawnPosition = entity.getPosition();
            datawatcher.spawnBiome = world.getBiomeGenForCoords(datawatcher.spawnPosition);
            UUID uuid = entity.getUniqueID();
            if (entity instanceof EntityVillager) {
                RandomEntities.updateEntityVillager(uuid, (EntityVillager)entity);
            }
        }
    }

    public static void entityUnloaded(Entity entity, World world) {
    }

    private static void updateEntityVillager(UUID uuid, EntityVillager ev) {
        Entity entity = IntegratedServerUtils.getEntity(uuid);
        if (entity instanceof EntityVillager) {
            EntityVillager entityvillager = (EntityVillager)entity;
            int i = entityvillager.getProfession();
            ev.setProfession(i);
            int j = Reflector.getFieldValueInt(entityvillager, Reflector.EntityVillager_careerId, 0);
            Reflector.setFieldValueInt(ev, Reflector.EntityVillager_careerId, j);
            int k = Reflector.getFieldValueInt(entityvillager, Reflector.EntityVillager_careerLevel, 0);
            Reflector.setFieldValueInt(ev, Reflector.EntityVillager_careerLevel, k);
        }
    }

    public static void worldChanged(World oldWorld, World newWorld) {
        if (newWorld != null) {
            List<Entity> list = newWorld.getLoadedEntityList();
            for (int i = 0; i < list.size(); ++i) {
                Entity entity = list.get(i);
                RandomEntities.entityLoaded(entity, newWorld);
            }
        }
        randomEntity.setEntity(null);
        randomTileEntity.setTileEntity(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ResourceLocation getTextureLocation(ResourceLocation loc) {
        ResourceLocation name;
        if (!active) {
            return loc;
        }
        if (working) {
            return loc;
        }
        try {
            working = true;
            IRandomEntity irandomentity = RandomEntities.getRandomEntityRendered();
            if (irandomentity != null) {
                ResourceLocation resourcelocation1;
                String s = loc.getResourcePath();
                if (s.startsWith(PREFIX_DYNAMIC_TEXTURE_HORSE)) {
                    s = RandomEntities.getHorseTexturePath(s, PREFIX_DYNAMIC_TEXTURE_HORSE.length());
                }
                if (!s.startsWith(PREFIX_TEXTURES_ENTITY) && !s.startsWith(PREFIX_TEXTURES_PAINTING)) {
                    ResourceLocation resourcelocation2;
                    ResourceLocation resourceLocation = resourcelocation2 = loc;
                    return resourceLocation;
                }
                RandomEntityProperties randomentityproperties = mapProperties.get(s);
                if (randomentityproperties == null) {
                    ResourceLocation resourcelocation3;
                    ResourceLocation resourceLocation = resourcelocation3 = loc;
                    return resourceLocation;
                }
                ResourceLocation resourceLocation = resourcelocation1 = randomentityproperties.getTextureLocation(loc, irandomentity);
                return resourceLocation;
            }
            name = loc;
        }
        finally {
            working = false;
        }
        return name;
    }

    private static String getHorseTexturePath(String path, int pos) {
        if (HORSE_TEXTURES != null && HORSE_TEXTURES_ABBR != null) {
            for (int i = 0; i < HORSE_TEXTURES_ABBR.length; ++i) {
                String s = HORSE_TEXTURES_ABBR[i];
                if (!path.startsWith(s, pos)) continue;
                return HORSE_TEXTURES[i];
            }
            return path;
        }
        return path;
    }

    private static IRandomEntity getRandomEntityRendered() {
        TileEntity tileentity;
        if (RandomEntities.renderGlobal.renderedEntity != null) {
            randomEntity.setEntity(RandomEntities.renderGlobal.renderedEntity);
            return randomEntity;
        }
        if (RandomEntities.tileEntityRendererDispatcher.tileEntityRendered != null && (tileentity = RandomEntities.tileEntityRendererDispatcher.tileEntityRendered).getWorld() != null) {
            randomTileEntity.setTileEntity(tileentity);
            return randomTileEntity;
        }
        return null;
    }

    private static RandomEntityProperties makeProperties(ResourceLocation loc, boolean mcpatcher) {
        RandomEntityProperties randomentityproperties;
        String s = loc.getResourcePath();
        ResourceLocation resourcelocation = RandomEntities.getLocationProperties(loc, mcpatcher);
        if (resourcelocation != null && (randomentityproperties = RandomEntities.parseProperties(resourcelocation, loc)) != null) {
            return randomentityproperties;
        }
        ResourceLocation[] aresourcelocation = RandomEntities.getLocationsVariants(loc, mcpatcher);
        return aresourcelocation == null ? null : new RandomEntityProperties(s, aresourcelocation);
    }

    private static RandomEntityProperties parseProperties(ResourceLocation propLoc, ResourceLocation resLoc) {
        try {
            String s = propLoc.getResourcePath();
            RandomEntities.dbg(resLoc.getResourcePath() + ", properties: " + s);
            InputStream inputstream = Config.getResourceStream(propLoc);
            if (inputstream == null) {
                RandomEntities.warn("Properties not found: " + s);
                return null;
            }
            PropertiesOrdered properties = new PropertiesOrdered();
            properties.load(inputstream);
            inputstream.close();
            RandomEntityProperties randomentityproperties = new RandomEntityProperties(properties, s, resLoc);
            return !randomentityproperties.isValid(s) ? null : randomentityproperties;
        }
        catch (FileNotFoundException var6) {
            RandomEntities.warn("File not found: " + resLoc.getResourcePath());
            return null;
        }
        catch (IOException ioexception) {
            ioexception.printStackTrace();
            return null;
        }
    }

    private static ResourceLocation getLocationProperties(ResourceLocation loc, boolean mcpatcher) {
        String s1;
        String s2;
        String s3;
        ResourceLocation resourcelocation = RandomEntities.getLocationRandom(loc, mcpatcher);
        if (resourcelocation == null) {
            return null;
        }
        String s = resourcelocation.getResourceDomain();
        ResourceLocation resourcelocation1 = new ResourceLocation(s, s3 = (s2 = StrUtils.removeSuffix(s1 = resourcelocation.getResourcePath(), SUFFIX_PNG)) + SUFFIX_PROPERTIES);
        if (Config.hasResource(resourcelocation1)) {
            return resourcelocation1;
        }
        String s4 = RandomEntities.getParentTexturePath(s2);
        if (s4 == null) {
            return null;
        }
        ResourceLocation resourcelocation2 = new ResourceLocation(s, s4 + SUFFIX_PROPERTIES);
        return Config.hasResource(resourcelocation2) ? resourcelocation2 : null;
    }

    protected static ResourceLocation getLocationRandom(ResourceLocation loc, boolean mcpatcher) {
        String s = loc.getResourceDomain();
        String s1 = loc.getResourcePath();
        String s2 = PREFIX_TEXTURES;
        String s3 = PREFIX_OPTIFINE_RANDOM;
        if (mcpatcher) {
            s2 = PREFIX_TEXTURES_ENTITY;
            s3 = PREFIX_MCPATCHER_MOB;
        }
        if (!s1.startsWith(s2)) {
            return null;
        }
        String s4 = StrUtils.replacePrefix(s1, s2, s3);
        return new ResourceLocation(s, s4);
    }

    private static String getPathBase(String pathRandom) {
        return pathRandom.startsWith(PREFIX_OPTIFINE_RANDOM) ? StrUtils.replacePrefix(pathRandom, PREFIX_OPTIFINE_RANDOM, PREFIX_TEXTURES) : (pathRandom.startsWith(PREFIX_MCPATCHER_MOB) ? StrUtils.replacePrefix(pathRandom, PREFIX_MCPATCHER_MOB, PREFIX_TEXTURES_ENTITY) : null);
    }

    protected static ResourceLocation getLocationIndexed(ResourceLocation loc, int index) {
        if (loc == null) {
            return null;
        }
        String s = loc.getResourcePath();
        int i = s.lastIndexOf(46);
        if (i < 0) {
            return null;
        }
        String s1 = s.substring(0, i);
        String s2 = s.substring(i);
        String s3 = s1 + index + s2;
        ResourceLocation resourcelocation = new ResourceLocation(loc.getResourceDomain(), s3);
        return resourcelocation;
    }

    private static String getParentTexturePath(String path) {
        for (int i = 0; i < DEPENDANT_SUFFIXES.length; ++i) {
            String s = DEPENDANT_SUFFIXES[i];
            if (!path.endsWith(s)) continue;
            String s1 = StrUtils.removeSuffix(path, s);
            return s1;
        }
        return null;
    }

    private static ResourceLocation[] getLocationsVariants(ResourceLocation loc, boolean mcpatcher) {
        ArrayList<ResourceLocation> list = new ArrayList<ResourceLocation>();
        list.add(loc);
        ResourceLocation resourcelocation = RandomEntities.getLocationRandom(loc, mcpatcher);
        if (resourcelocation == null) {
            return null;
        }
        for (int i = 1; i < list.size() + 10; ++i) {
            int j = i + 1;
            ResourceLocation resourcelocation1 = RandomEntities.getLocationIndexed(resourcelocation, j);
            if (!Config.hasResource(resourcelocation1)) continue;
            list.add(resourcelocation1);
        }
        if (list.size() <= 1) {
            return null;
        }
        ResourceLocation[] aresourcelocation = list.toArray(new ResourceLocation[list.size()]);
        RandomEntities.dbg(loc.getResourcePath() + ", variants: " + aresourcelocation.length);
        return aresourcelocation;
    }

    public static void update() {
        mapProperties.clear();
        active = false;
        if (Config.isRandomEntities()) {
            RandomEntities.initialize();
        }
    }

    private static void initialize() {
        renderGlobal = Config.getRenderGlobal();
        tileEntityRendererDispatcher = TileEntityRendererDispatcher.instance;
        String[] astring = new String[]{PREFIX_OPTIFINE_RANDOM, PREFIX_MCPATCHER_MOB};
        String[] astring1 = new String[]{SUFFIX_PNG, SUFFIX_PROPERTIES};
        String[] astring2 = ResUtils.collectFiles(astring, astring1);
        HashSet<String> set = new HashSet<String>();
        for (int i = 0; i < astring2.length; ++i) {
            RandomEntityProperties randomentityproperties;
            String s = astring2[i];
            s = StrUtils.removeSuffix(s, astring1);
            s = StrUtils.trimTrailing(s, "0123456789");
            String s1 = RandomEntities.getPathBase(s = s + SUFFIX_PNG);
            if (set.contains(s1)) continue;
            set.add(s1);
            ResourceLocation resourcelocation = new ResourceLocation(s1);
            if (!Config.hasResource(resourcelocation) || (randomentityproperties = mapProperties.get(s1)) != null) continue;
            randomentityproperties = RandomEntities.makeProperties(resourcelocation, false);
            if (randomentityproperties == null) {
                randomentityproperties = RandomEntities.makeProperties(resourcelocation, true);
            }
            if (randomentityproperties == null) continue;
            mapProperties.put(s1, randomentityproperties);
        }
        active = !mapProperties.isEmpty();
    }

    public static void dbg(String str) {
        Config.dbg("RandomEntities: " + str);
    }

    public static void warn(String str) {
        Config.warn("RandomEntities: " + str);
    }

    static {
        randomEntity = new RandomEntity();
        randomTileEntity = new RandomTileEntity();
        working = false;
        DEPENDANT_SUFFIXES = new String[]{"_armor", "_eyes", "_exploding", "_shooting", "_fur", "_eyes", "_invulnerable", "_angry", "_tame", "_collar"};
        HORSE_TEXTURES = (String[])ReflectorRaw.getFieldValue(null, EntityHorse.class, String[].class, 0);
        HORSE_TEXTURES_ABBR = (String[])ReflectorRaw.getFieldValue(null, EntityHorse.class, String[].class, 1);
    }
}

