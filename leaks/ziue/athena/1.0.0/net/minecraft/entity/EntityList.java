package net.minecraft.entity;

import net.minecraft.world.*;
import net.minecraft.nbt.*;
import java.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.effect.*;
import org.apache.logging.log4j.*;
import com.google.common.collect.*;
import net.minecraft.entity.projectile.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.boss.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.item.*;
import net.minecraft.stats.*;

public class EntityList
{
    private static final Logger logger;
    private static final Map<String, Class<? extends Entity>> stringToClassMapping;
    private static final Map<Class<? extends Entity>, String> classToStringMapping;
    private static final Map<Integer, Class<? extends Entity>> idToClassMapping;
    private static final Map<Class<? extends Entity>, Integer> classToIDMapping;
    private static final Map<String, Integer> stringToIDMapping;
    public static final Map<Integer, EntityEggInfo> entityEggs;
    
    private static void addMapping(final Class<? extends Entity> entityClass, final String entityName, final int id) {
        if (EntityList.stringToClassMapping.containsKey(entityName)) {
            throw new IllegalArgumentException("ID is already registered: " + entityName);
        }
        if (EntityList.idToClassMapping.containsKey(id)) {
            throw new IllegalArgumentException("ID is already registered: " + id);
        }
        if (id == 0) {
            throw new IllegalArgumentException("Cannot register to reserved id: " + id);
        }
        if (entityClass == null) {
            throw new IllegalArgumentException("Cannot register null clazz for id: " + id);
        }
        EntityList.stringToClassMapping.put(entityName, entityClass);
        EntityList.classToStringMapping.put(entityClass, entityName);
        EntityList.idToClassMapping.put(id, entityClass);
        EntityList.classToIDMapping.put(entityClass, id);
        EntityList.stringToIDMapping.put(entityName, id);
    }
    
    private static void addMapping(final Class<? extends Entity> entityClass, final String entityName, final int entityID, final int baseColor, final int spotColor) {
        addMapping(entityClass, entityName, entityID);
        EntityList.entityEggs.put(entityID, new EntityEggInfo(entityID, baseColor, spotColor));
    }
    
    public static Entity createEntityByName(final String entityName, final World worldIn) {
        Entity entity = null;
        try {
            final Class<? extends Entity> oclass = EntityList.stringToClassMapping.get(entityName);
            if (oclass != null) {
                entity = (Entity)oclass.getConstructor(World.class).newInstance(worldIn);
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return entity;
    }
    
    public static Entity createEntityFromNBT(final NBTTagCompound nbt, final World worldIn) {
        Entity entity = null;
        if ("Minecart".equals(nbt.getString("id"))) {
            nbt.setString("id", EntityMinecart.EnumMinecartType.byNetworkID(nbt.getInteger("Type")).getName());
            nbt.removeTag("Type");
        }
        try {
            final Class<? extends Entity> oclass = EntityList.stringToClassMapping.get(nbt.getString("id"));
            if (oclass != null) {
                entity = (Entity)oclass.getConstructor(World.class).newInstance(worldIn);
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        if (entity != null) {
            entity.readFromNBT(nbt);
        }
        else {
            EntityList.logger.warn("Skipping Entity with id " + nbt.getString("id"));
        }
        return entity;
    }
    
    public static Entity createEntityByID(final int entityID, final World worldIn) {
        Entity entity = null;
        try {
            final Class<? extends Entity> oclass = getClassFromID(entityID);
            if (oclass != null) {
                entity = (Entity)oclass.getConstructor(World.class).newInstance(worldIn);
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        if (entity == null) {
            EntityList.logger.warn("Skipping Entity with id " + entityID);
        }
        return entity;
    }
    
    public static int getEntityID(final Entity entityIn) {
        final Integer integer = EntityList.classToIDMapping.get(entityIn.getClass());
        return (integer == null) ? 0 : integer;
    }
    
    public static Class<? extends Entity> getClassFromID(final int entityID) {
        return EntityList.idToClassMapping.get(entityID);
    }
    
    public static String getEntityString(final Entity entityIn) {
        return EntityList.classToStringMapping.get(entityIn.getClass());
    }
    
    public static int getIDFromString(final String entityName) {
        final Integer integer = EntityList.stringToIDMapping.get(entityName);
        return (integer == null) ? 90 : integer;
    }
    
    public static String getStringFromID(final int entityID) {
        return EntityList.classToStringMapping.get(getClassFromID(entityID));
    }
    
    public static void func_151514_a() {
    }
    
    public static List<String> getEntityNameList() {
        final Set<String> set = EntityList.stringToClassMapping.keySet();
        final List<String> list = (List<String>)Lists.newArrayList();
        for (final String s : set) {
            final Class<? extends Entity> oclass = EntityList.stringToClassMapping.get(s);
            if ((oclass.getModifiers() & 0x400) != 0x400) {
                list.add(s);
            }
        }
        list.add("LightningBolt");
        return list;
    }
    
    public static boolean isStringEntityName(final Entity entityIn, final String entityName) {
        String s = getEntityString(entityIn);
        if (s == null && entityIn instanceof EntityPlayer) {
            s = "Player";
        }
        else if (s == null && entityIn instanceof EntityLightningBolt) {
            s = "LightningBolt";
        }
        return entityName.equals(s);
    }
    
    public static boolean isStringValidEntityName(final String entityName) {
        return "Player".equals(entityName) || getEntityNameList().contains(entityName);
    }
    
    static {
        logger = LogManager.getLogger();
        stringToClassMapping = Maps.newHashMap();
        classToStringMapping = Maps.newHashMap();
        idToClassMapping = Maps.newHashMap();
        classToIDMapping = Maps.newHashMap();
        stringToIDMapping = Maps.newHashMap();
        entityEggs = Maps.newLinkedHashMap();
        addMapping(EntityItem.class, "Item", 1);
        addMapping(EntityXPOrb.class, "XPOrb", 2);
        addMapping(EntityEgg.class, "ThrownEgg", 7);
        addMapping(EntityLeashKnot.class, "LeashKnot", 8);
        addMapping(EntityPainting.class, "Painting", 9);
        addMapping(EntityArrow.class, "Arrow", 10);
        addMapping(EntitySnowball.class, "Snowball", 11);
        addMapping(EntityLargeFireball.class, "Fireball", 12);
        addMapping(EntitySmallFireball.class, "SmallFireball", 13);
        addMapping(EntityEnderPearl.class, "ThrownEnderpearl", 14);
        addMapping(EntityEnderEye.class, "EyeOfEnderSignal", 15);
        addMapping(EntityPotion.class, "ThrownPotion", 16);
        addMapping(EntityExpBottle.class, "ThrownExpBottle", 17);
        addMapping(EntityItemFrame.class, "ItemFrame", 18);
        addMapping(EntityWitherSkull.class, "WitherSkull", 19);
        addMapping(EntityTNTPrimed.class, "PrimedTnt", 20);
        addMapping(EntityFallingBlock.class, "FallingSand", 21);
        addMapping(EntityFireworkRocket.class, "FireworksRocketEntity", 22);
        addMapping(EntityArmorStand.class, "ArmorStand", 30);
        addMapping(EntityBoat.class, "Boat", 41);
        addMapping(EntityMinecartEmpty.class, EntityMinecart.EnumMinecartType.RIDEABLE.getName(), 42);
        addMapping(EntityMinecartChest.class, EntityMinecart.EnumMinecartType.CHEST.getName(), 43);
        addMapping(EntityMinecartFurnace.class, EntityMinecart.EnumMinecartType.FURNACE.getName(), 44);
        addMapping(EntityMinecartTNT.class, EntityMinecart.EnumMinecartType.TNT.getName(), 45);
        addMapping(EntityMinecartHopper.class, EntityMinecart.EnumMinecartType.HOPPER.getName(), 46);
        addMapping(EntityMinecartMobSpawner.class, EntityMinecart.EnumMinecartType.SPAWNER.getName(), 47);
        addMapping(EntityMinecartCommandBlock.class, EntityMinecart.EnumMinecartType.COMMAND_BLOCK.getName(), 40);
        addMapping(EntityLiving.class, "Mob", 48);
        addMapping(EntityMob.class, "Monster", 49);
        addMapping(EntityCreeper.class, "Creeper", 50, 894731, 0);
        addMapping(EntitySkeleton.class, "Skeleton", 51, 12698049, 4802889);
        addMapping(EntitySpider.class, "Spider", 52, 3419431, 11013646);
        addMapping(EntityGiantZombie.class, "Giant", 53);
        addMapping(EntityZombie.class, "Zombie", 54, 44975, 7969893);
        addMapping(EntitySlime.class, "Slime", 55, 5349438, 8306542);
        addMapping(EntityGhast.class, "Ghast", 56, 16382457, 12369084);
        addMapping(EntityPigZombie.class, "PigZombie", 57, 15373203, 5009705);
        addMapping(EntityEnderman.class, "Enderman", 58, 1447446, 0);
        addMapping(EntityCaveSpider.class, "CaveSpider", 59, 803406, 11013646);
        addMapping(EntitySilverfish.class, "Silverfish", 60, 7237230, 3158064);
        addMapping(EntityBlaze.class, "Blaze", 61, 16167425, 16775294);
        addMapping(EntityMagmaCube.class, "LavaSlime", 62, 3407872, 16579584);
        addMapping(EntityDragon.class, "EnderDragon", 63);
        addMapping(EntityWither.class, "WitherBoss", 64);
        addMapping(EntityBat.class, "Bat", 65, 4996656, 986895);
        addMapping(EntityWitch.class, "Witch", 66, 3407872, 5349438);
        addMapping(EntityEndermite.class, "Endermite", 67, 1447446, 7237230);
        addMapping(EntityGuardian.class, "Guardian", 68, 5931634, 15826224);
        addMapping(EntityPig.class, "Pig", 90, 15771042, 14377823);
        addMapping(EntitySheep.class, "Sheep", 91, 15198183, 16758197);
        addMapping(EntityCow.class, "Cow", 92, 4470310, 10592673);
        addMapping(EntityChicken.class, "Chicken", 93, 10592673, 16711680);
        addMapping(EntitySquid.class, "Squid", 94, 2243405, 7375001);
        addMapping(EntityWolf.class, "Wolf", 95, 14144467, 13545366);
        addMapping(EntityMooshroom.class, "MushroomCow", 96, 10489616, 12040119);
        addMapping(EntitySnowman.class, "SnowMan", 97);
        addMapping(EntityOcelot.class, "Ozelot", 98, 15720061, 5653556);
        addMapping(EntityIronGolem.class, "VillagerGolem", 99);
        addMapping(EntityHorse.class, "EntityHorse", 100, 12623485, 15656192);
        addMapping(EntityRabbit.class, "Rabbit", 101, 10051392, 7555121);
        addMapping(EntityVillager.class, "Villager", 120, 5651507, 12422002);
        addMapping(EntityEnderCrystal.class, "EnderCrystal", 200);
    }
    
    public static class EntityEggInfo
    {
        public final int spawnedID;
        public final int primaryColor;
        public final int secondaryColor;
        public final StatBase field_151512_d;
        public final StatBase field_151513_e;
        
        public EntityEggInfo(final int id, final int baseColor, final int spotColor) {
            this.spawnedID = id;
            this.primaryColor = baseColor;
            this.secondaryColor = spotColor;
            this.field_151512_d = StatList.getStatKillEntity(this);
            this.field_151513_e = StatList.getStatEntityKilledBy(this);
        }
    }
}
