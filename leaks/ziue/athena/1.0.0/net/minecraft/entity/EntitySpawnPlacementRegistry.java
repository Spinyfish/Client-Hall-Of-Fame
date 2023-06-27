package net.minecraft.entity;

import java.util.*;
import com.google.common.collect.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.boss.*;
import net.minecraft.entity.monster.*;

public class EntitySpawnPlacementRegistry
{
    private static final HashMap<Class, EntityLiving.SpawnPlacementType> ENTITY_PLACEMENTS;
    
    public static EntityLiving.SpawnPlacementType getPlacementForEntity(final Class entityClass) {
        return EntitySpawnPlacementRegistry.ENTITY_PLACEMENTS.get(entityClass);
    }
    
    static {
        (ENTITY_PLACEMENTS = Maps.newHashMap()).put(EntityBat.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        EntitySpawnPlacementRegistry.ENTITY_PLACEMENTS.put(EntityChicken.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        EntitySpawnPlacementRegistry.ENTITY_PLACEMENTS.put(EntityCow.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        EntitySpawnPlacementRegistry.ENTITY_PLACEMENTS.put(EntityHorse.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        EntitySpawnPlacementRegistry.ENTITY_PLACEMENTS.put(EntityMooshroom.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        EntitySpawnPlacementRegistry.ENTITY_PLACEMENTS.put(EntityOcelot.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        EntitySpawnPlacementRegistry.ENTITY_PLACEMENTS.put(EntityPig.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        EntitySpawnPlacementRegistry.ENTITY_PLACEMENTS.put(EntityRabbit.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        EntitySpawnPlacementRegistry.ENTITY_PLACEMENTS.put(EntitySheep.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        EntitySpawnPlacementRegistry.ENTITY_PLACEMENTS.put(EntitySnowman.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        EntitySpawnPlacementRegistry.ENTITY_PLACEMENTS.put(EntitySquid.class, EntityLiving.SpawnPlacementType.IN_WATER);
        EntitySpawnPlacementRegistry.ENTITY_PLACEMENTS.put(EntityIronGolem.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        EntitySpawnPlacementRegistry.ENTITY_PLACEMENTS.put(EntityWolf.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        EntitySpawnPlacementRegistry.ENTITY_PLACEMENTS.put(EntityVillager.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        EntitySpawnPlacementRegistry.ENTITY_PLACEMENTS.put(EntityDragon.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        EntitySpawnPlacementRegistry.ENTITY_PLACEMENTS.put(EntityWither.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        EntitySpawnPlacementRegistry.ENTITY_PLACEMENTS.put(EntityBlaze.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        EntitySpawnPlacementRegistry.ENTITY_PLACEMENTS.put(EntityCaveSpider.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        EntitySpawnPlacementRegistry.ENTITY_PLACEMENTS.put(EntityCreeper.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        EntitySpawnPlacementRegistry.ENTITY_PLACEMENTS.put(EntityEnderman.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        EntitySpawnPlacementRegistry.ENTITY_PLACEMENTS.put(EntityEndermite.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        EntitySpawnPlacementRegistry.ENTITY_PLACEMENTS.put(EntityGhast.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        EntitySpawnPlacementRegistry.ENTITY_PLACEMENTS.put(EntityGiantZombie.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        EntitySpawnPlacementRegistry.ENTITY_PLACEMENTS.put(EntityGuardian.class, EntityLiving.SpawnPlacementType.IN_WATER);
        EntitySpawnPlacementRegistry.ENTITY_PLACEMENTS.put(EntityMagmaCube.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        EntitySpawnPlacementRegistry.ENTITY_PLACEMENTS.put(EntityPigZombie.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        EntitySpawnPlacementRegistry.ENTITY_PLACEMENTS.put(EntitySilverfish.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        EntitySpawnPlacementRegistry.ENTITY_PLACEMENTS.put(EntitySkeleton.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        EntitySpawnPlacementRegistry.ENTITY_PLACEMENTS.put(EntitySlime.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        EntitySpawnPlacementRegistry.ENTITY_PLACEMENTS.put(EntitySpider.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        EntitySpawnPlacementRegistry.ENTITY_PLACEMENTS.put(EntityWitch.class, EntityLiving.SpawnPlacementType.ON_GROUND);
        EntitySpawnPlacementRegistry.ENTITY_PLACEMENTS.put(EntityZombie.class, EntityLiving.SpawnPlacementType.ON_GROUND);
    }
}
