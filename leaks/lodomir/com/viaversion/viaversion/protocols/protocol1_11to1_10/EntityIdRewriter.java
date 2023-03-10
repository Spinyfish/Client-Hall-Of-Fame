/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 */
package com.viaversion.viaversion.protocols.protocol1_11to1_10;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;

public class EntityIdRewriter {
    private static final BiMap<String, String> oldToNewNames = HashBiMap.create();

    public static void toClient(CompoundTag tag) {
        EntityIdRewriter.toClient(tag, false);
    }

    public static void toClient(CompoundTag tag, boolean backwards) {
        Object idTag = tag.get("id");
        if (idTag instanceof StringTag) {
            String newName;
            StringTag id = (StringTag)idTag;
            String string = newName = backwards ? (String)oldToNewNames.inverse().get((Object)id.getValue()) : (String)oldToNewNames.get((Object)id.getValue());
            if (newName != null) {
                id.setValue(newName);
            }
        }
    }

    public static void toClientSpawner(CompoundTag tag) {
        EntityIdRewriter.toClientSpawner(tag, false);
    }

    public static void toClientSpawner(CompoundTag tag, boolean backwards) {
        if (tag == null) {
            return;
        }
        Object spawnDataTag = tag.get("SpawnData");
        if (spawnDataTag != null) {
            EntityIdRewriter.toClient((CompoundTag)spawnDataTag, backwards);
        }
    }

    public static void toClientItem(Item item) {
        EntityIdRewriter.toClientItem(item, false);
    }

    public static void toClientItem(Item item, boolean backwards) {
        if (EntityIdRewriter.hasEntityTag(item)) {
            EntityIdRewriter.toClient((CompoundTag)item.tag().get("EntityTag"), backwards);
        }
        if (item != null && item.amount() <= 0) {
            item.setAmount(1);
        }
    }

    public static void toServerItem(Item item) {
        EntityIdRewriter.toServerItem(item, false);
    }

    public static void toServerItem(Item item, boolean backwards) {
        if (!EntityIdRewriter.hasEntityTag(item)) {
            return;
        }
        CompoundTag entityTag = (CompoundTag)item.tag().get("EntityTag");
        Object idTag = entityTag.get("id");
        if (idTag instanceof StringTag) {
            String newName;
            StringTag id = (StringTag)idTag;
            String string = newName = backwards ? (String)oldToNewNames.get((Object)id.getValue()) : (String)oldToNewNames.inverse().get((Object)id.getValue());
            if (newName != null) {
                id.setValue(newName);
            }
        }
    }

    private static boolean hasEntityTag(Item item) {
        if (item == null || item.identifier() != 383) {
            return false;
        }
        CompoundTag tag = item.tag();
        if (tag == null) {
            return false;
        }
        Object entityTag = tag.get("EntityTag");
        return entityTag instanceof CompoundTag && ((CompoundTag)entityTag).get("id") instanceof StringTag;
    }

    static {
        oldToNewNames.put((Object)"AreaEffectCloud", (Object)"minecraft:area_effect_cloud");
        oldToNewNames.put((Object)"ArmorStand", (Object)"minecraft:armor_stand");
        oldToNewNames.put((Object)"Arrow", (Object)"minecraft:arrow");
        oldToNewNames.put((Object)"Bat", (Object)"minecraft:bat");
        oldToNewNames.put((Object)"Blaze", (Object)"minecraft:blaze");
        oldToNewNames.put((Object)"Boat", (Object)"minecraft:boat");
        oldToNewNames.put((Object)"CaveSpider", (Object)"minecraft:cave_spider");
        oldToNewNames.put((Object)"Chicken", (Object)"minecraft:chicken");
        oldToNewNames.put((Object)"Cow", (Object)"minecraft:cow");
        oldToNewNames.put((Object)"Creeper", (Object)"minecraft:creeper");
        oldToNewNames.put((Object)"Donkey", (Object)"minecraft:donkey");
        oldToNewNames.put((Object)"DragonFireball", (Object)"minecraft:dragon_fireball");
        oldToNewNames.put((Object)"ElderGuardian", (Object)"minecraft:elder_guardian");
        oldToNewNames.put((Object)"EnderCrystal", (Object)"minecraft:ender_crystal");
        oldToNewNames.put((Object)"EnderDragon", (Object)"minecraft:ender_dragon");
        oldToNewNames.put((Object)"Enderman", (Object)"minecraft:enderman");
        oldToNewNames.put((Object)"Endermite", (Object)"minecraft:endermite");
        oldToNewNames.put((Object)"EntityHorse", (Object)"minecraft:horse");
        oldToNewNames.put((Object)"EyeOfEnderSignal", (Object)"minecraft:eye_of_ender_signal");
        oldToNewNames.put((Object)"FallingSand", (Object)"minecraft:falling_block");
        oldToNewNames.put((Object)"Fireball", (Object)"minecraft:fireball");
        oldToNewNames.put((Object)"FireworksRocketEntity", (Object)"minecraft:fireworks_rocket");
        oldToNewNames.put((Object)"Ghast", (Object)"minecraft:ghast");
        oldToNewNames.put((Object)"Giant", (Object)"minecraft:giant");
        oldToNewNames.put((Object)"Guardian", (Object)"minecraft:guardian");
        oldToNewNames.put((Object)"Husk", (Object)"minecraft:husk");
        oldToNewNames.put((Object)"Item", (Object)"minecraft:item");
        oldToNewNames.put((Object)"ItemFrame", (Object)"minecraft:item_frame");
        oldToNewNames.put((Object)"LavaSlime", (Object)"minecraft:magma_cube");
        oldToNewNames.put((Object)"LeashKnot", (Object)"minecraft:leash_knot");
        oldToNewNames.put((Object)"MinecartChest", (Object)"minecraft:chest_minecart");
        oldToNewNames.put((Object)"MinecartCommandBlock", (Object)"minecraft:commandblock_minecart");
        oldToNewNames.put((Object)"MinecartFurnace", (Object)"minecraft:furnace_minecart");
        oldToNewNames.put((Object)"MinecartHopper", (Object)"minecraft:hopper_minecart");
        oldToNewNames.put((Object)"MinecartRideable", (Object)"minecraft:minecart");
        oldToNewNames.put((Object)"MinecartSpawner", (Object)"minecraft:spawner_minecart");
        oldToNewNames.put((Object)"MinecartTNT", (Object)"minecraft:tnt_minecart");
        oldToNewNames.put((Object)"Mule", (Object)"minecraft:mule");
        oldToNewNames.put((Object)"MushroomCow", (Object)"minecraft:mooshroom");
        oldToNewNames.put((Object)"Ozelot", (Object)"minecraft:ocelot");
        oldToNewNames.put((Object)"Painting", (Object)"minecraft:painting");
        oldToNewNames.put((Object)"Pig", (Object)"minecraft:pig");
        oldToNewNames.put((Object)"PigZombie", (Object)"minecraft:zombie_pigman");
        oldToNewNames.put((Object)"PolarBear", (Object)"minecraft:polar_bear");
        oldToNewNames.put((Object)"PrimedTnt", (Object)"minecraft:tnt");
        oldToNewNames.put((Object)"Rabbit", (Object)"minecraft:rabbit");
        oldToNewNames.put((Object)"Sheep", (Object)"minecraft:sheep");
        oldToNewNames.put((Object)"Shulker", (Object)"minecraft:shulker");
        oldToNewNames.put((Object)"ShulkerBullet", (Object)"minecraft:shulker_bullet");
        oldToNewNames.put((Object)"Silverfish", (Object)"minecraft:silverfish");
        oldToNewNames.put((Object)"Skeleton", (Object)"minecraft:skeleton");
        oldToNewNames.put((Object)"SkeletonHorse", (Object)"minecraft:skeleton_horse");
        oldToNewNames.put((Object)"Slime", (Object)"minecraft:slime");
        oldToNewNames.put((Object)"SmallFireball", (Object)"minecraft:small_fireball");
        oldToNewNames.put((Object)"Snowball", (Object)"minecraft:snowball");
        oldToNewNames.put((Object)"SnowMan", (Object)"minecraft:snowman");
        oldToNewNames.put((Object)"SpectralArrow", (Object)"minecraft:spectral_arrow");
        oldToNewNames.put((Object)"Spider", (Object)"minecraft:spider");
        oldToNewNames.put((Object)"Squid", (Object)"minecraft:squid");
        oldToNewNames.put((Object)"Stray", (Object)"minecraft:stray");
        oldToNewNames.put((Object)"ThrownEgg", (Object)"minecraft:egg");
        oldToNewNames.put((Object)"ThrownEnderpearl", (Object)"minecraft:ender_pearl");
        oldToNewNames.put((Object)"ThrownExpBottle", (Object)"minecraft:xp_bottle");
        oldToNewNames.put((Object)"ThrownPotion", (Object)"minecraft:potion");
        oldToNewNames.put((Object)"Villager", (Object)"minecraft:villager");
        oldToNewNames.put((Object)"VillagerGolem", (Object)"minecraft:villager_golem");
        oldToNewNames.put((Object)"Witch", (Object)"minecraft:witch");
        oldToNewNames.put((Object)"WitherBoss", (Object)"minecraft:wither");
        oldToNewNames.put((Object)"WitherSkeleton", (Object)"minecraft:wither_skeleton");
        oldToNewNames.put((Object)"WitherSkull", (Object)"minecraft:wither_skull");
        oldToNewNames.put((Object)"Wolf", (Object)"minecraft:wolf");
        oldToNewNames.put((Object)"XPOrb", (Object)"minecraft:xp_orb");
        oldToNewNames.put((Object)"Zombie", (Object)"minecraft:zombie");
        oldToNewNames.put((Object)"ZombieHorse", (Object)"minecraft:zombie_horse");
        oldToNewNames.put((Object)"ZombieVillager", (Object)"minecraft:zombie_villager");
    }
}

