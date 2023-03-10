/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_9to1_8;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntOpenHashMap;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ItemRewriter {
    private static final Map<String, Integer> ENTTIY_NAME_TO_ID = new HashMap<String, Integer>();
    private static final Map<Integer, String> ENTTIY_ID_TO_NAME = new HashMap<Integer, String>();
    private static final Map<String, Integer> POTION_NAME_TO_ID = new HashMap<String, Integer>();
    private static final Map<Integer, String> POTION_ID_TO_NAME = new HashMap<Integer, String>();
    private static final Int2IntMap POTION_INDEX = new Int2IntOpenHashMap(36, 0.99f);

    public static void toServer(Item item) {
        if (item != null) {
            String potionName;
            StringTag potion;
            int data;
            CompoundTag tag;
            if (item.identifier() == 383 && item.data() == 0) {
                tag = item.tag();
                data = 0;
                if (tag != null && tag.get("EntityTag") instanceof CompoundTag) {
                    StringTag id;
                    CompoundTag entityTag = (CompoundTag)tag.get("EntityTag");
                    if (entityTag.get("id") instanceof StringTag && ENTTIY_NAME_TO_ID.containsKey((id = (StringTag)entityTag.get("id")).getValue())) {
                        data = ENTTIY_NAME_TO_ID.get(id.getValue());
                    }
                    tag.remove("EntityTag");
                }
                item.setTag(tag);
                item.setData((short)data);
            }
            if (item.identifier() == 373) {
                tag = item.tag();
                data = 0;
                if (tag != null && tag.get("Potion") instanceof StringTag) {
                    potion = (StringTag)tag.get("Potion");
                    potionName = potion.getValue().replace("minecraft:", "");
                    if (POTION_NAME_TO_ID.containsKey(potionName)) {
                        data = POTION_NAME_TO_ID.get(potionName);
                    }
                    tag.remove("Potion");
                }
                item.setTag(tag);
                item.setData((short)data);
            }
            if (item.identifier() == 438) {
                tag = item.tag();
                data = 0;
                item.setIdentifier(373);
                if (tag != null && tag.get("Potion") instanceof StringTag) {
                    potion = (StringTag)tag.get("Potion");
                    potionName = potion.getValue().replace("minecraft:", "");
                    if (POTION_NAME_TO_ID.containsKey(potionName)) {
                        data = POTION_NAME_TO_ID.get(potionName) + 8192;
                    }
                    tag.remove("Potion");
                }
                item.setTag(tag);
                item.setData((short)data);
            }
            boolean newItem = item.identifier() >= 198 && item.identifier() <= 212;
            newItem |= item.identifier() == 397 && item.data() == 5;
            if (newItem |= item.identifier() >= 432 && item.identifier() <= 448) {
                item.setIdentifier(1);
                item.setData((short)0);
            }
        }
    }

    public static void rewriteBookToServer(Item item) {
        int id = item.identifier();
        if (id != 387) {
            return;
        }
        CompoundTag tag = item.tag();
        ListTag pages = (ListTag)tag.get("pages");
        if (pages == null) {
            return;
        }
        for (int i = 0; i < pages.size(); ++i) {
            Object pageTag = pages.get(i);
            if (!(pageTag instanceof StringTag)) continue;
            StringTag stag = (StringTag)pageTag;
            String value = stag.getValue();
            value = value.replaceAll(" ", "").isEmpty() ? "\"" + ItemRewriter.fixBookSpaceChars(value) + "\"" : ItemRewriter.fixBookSpaceChars(value);
            stag.setValue(value);
        }
    }

    private static String fixBookSpaceChars(String str) {
        if (!str.startsWith(" ")) {
            return str;
        }
        str = "\u00a7r" + str;
        return str;
    }

    public static void toClient(Item item) {
        if (item != null) {
            CompoundTag tag;
            if (item.identifier() == 383 && item.data() != 0) {
                tag = item.tag();
                if (tag == null) {
                    tag = new CompoundTag();
                }
                CompoundTag entityTag = new CompoundTag();
                String entityName = ENTTIY_ID_TO_NAME.get(item.data());
                if (entityName != null) {
                    StringTag id = new StringTag(entityName);
                    entityTag.put("id", id);
                    tag.put("EntityTag", entityTag);
                }
                item.setTag(tag);
                item.setData((short)0);
            }
            if (item.identifier() == 373) {
                tag = item.tag();
                if (tag == null) {
                    tag = new CompoundTag();
                }
                if (item.data() >= 16384) {
                    item.setIdentifier(438);
                    item.setData((short)(item.data() - 8192));
                }
                String name = ItemRewriter.potionNameFromDamage(item.data());
                StringTag potion = new StringTag("minecraft:" + name);
                tag.put("Potion", potion);
                item.setTag(tag);
                item.setData((short)0);
            }
            if (item.identifier() == 387) {
                ListTag pages;
                tag = item.tag();
                if (tag == null) {
                    tag = new CompoundTag();
                }
                if ((pages = (ListTag)tag.get("pages")) == null) {
                    pages = new ListTag(Collections.singletonList(new StringTag(Protocol1_9To1_8.fixJson("").toString())));
                    tag.put("pages", pages);
                    item.setTag(tag);
                    return;
                }
                for (int i = 0; i < pages.size(); ++i) {
                    if (!(pages.get(i) instanceof StringTag)) continue;
                    StringTag page = (StringTag)pages.get(i);
                    page.setValue(Protocol1_9To1_8.fixJson(page.getValue()).toString());
                }
                item.setTag(tag);
            }
        }
    }

    public static String potionNameFromDamage(short damage) {
        String id;
        String cached = POTION_ID_TO_NAME.get(damage);
        if (cached != null) {
            return cached;
        }
        if (damage == 0) {
            return "water";
        }
        int effect = damage & 0xF;
        int name = damage & 0x3F;
        boolean enhanced = (damage & 0x20) > 0;
        boolean extended = (damage & 0x40) > 0;
        boolean canEnhance = true;
        boolean canExtend = true;
        block0 : switch (effect) {
            case 1: {
                id = "regeneration";
                break;
            }
            case 2: {
                id = "swiftness";
                break;
            }
            case 3: {
                id = "fire_resistance";
                canEnhance = false;
                break;
            }
            case 4: {
                id = "poison";
                break;
            }
            case 5: {
                id = "healing";
                canExtend = false;
                break;
            }
            case 6: {
                id = "night_vision";
                canEnhance = false;
                break;
            }
            case 8: {
                id = "weakness";
                canEnhance = false;
                break;
            }
            case 9: {
                id = "strength";
                break;
            }
            case 10: {
                id = "slowness";
                canEnhance = false;
                break;
            }
            case 11: {
                id = "leaping";
                break;
            }
            case 12: {
                id = "harming";
                canExtend = false;
                break;
            }
            case 13: {
                id = "water_breathing";
                canEnhance = false;
                break;
            }
            case 14: {
                id = "invisibility";
                canEnhance = false;
                break;
            }
            default: {
                canEnhance = false;
                canExtend = false;
                switch (name) {
                    case 0: {
                        id = "mundane";
                        break block0;
                    }
                    case 16: {
                        id = "awkward";
                        break block0;
                    }
                    case 32: {
                        id = "thick";
                        break block0;
                    }
                }
                id = "empty";
            }
        }
        if (effect > 0) {
            if (canEnhance && enhanced) {
                id = "strong_" + id;
            } else if (canExtend && extended) {
                id = "long_" + id;
            }
        }
        return id;
    }

    public static int getNewEffectID(int oldID) {
        int index;
        if (oldID >= 16384) {
            oldID -= 8192;
        }
        if ((index = POTION_INDEX.get(oldID)) != -1) {
            return index;
        }
        index = POTION_INDEX.get(oldID = POTION_NAME_TO_ID.get(ItemRewriter.potionNameFromDamage((short)oldID)).intValue());
        return index != -1 ? index : 0;
    }

    private static void registerEntity(int id, String name) {
        ENTTIY_ID_TO_NAME.put(id, name);
        ENTTIY_NAME_TO_ID.put(name, id);
    }

    private static void registerPotion(int id, String name) {
        POTION_INDEX.put(id, POTION_ID_TO_NAME.size());
        POTION_ID_TO_NAME.put(id, name);
        POTION_NAME_TO_ID.put(name, id);
    }

    static {
        ItemRewriter.registerEntity(1, "Item");
        ItemRewriter.registerEntity(2, "XPOrb");
        ItemRewriter.registerEntity(7, "ThrownEgg");
        ItemRewriter.registerEntity(8, "LeashKnot");
        ItemRewriter.registerEntity(9, "Painting");
        ItemRewriter.registerEntity(10, "Arrow");
        ItemRewriter.registerEntity(11, "Snowball");
        ItemRewriter.registerEntity(12, "Fireball");
        ItemRewriter.registerEntity(13, "SmallFireball");
        ItemRewriter.registerEntity(14, "ThrownEnderpearl");
        ItemRewriter.registerEntity(15, "EyeOfEnderSignal");
        ItemRewriter.registerEntity(16, "ThrownPotion");
        ItemRewriter.registerEntity(17, "ThrownExpBottle");
        ItemRewriter.registerEntity(18, "ItemFrame");
        ItemRewriter.registerEntity(19, "WitherSkull");
        ItemRewriter.registerEntity(20, "PrimedTnt");
        ItemRewriter.registerEntity(21, "FallingSand");
        ItemRewriter.registerEntity(22, "FireworksRocketEntity");
        ItemRewriter.registerEntity(30, "ArmorStand");
        ItemRewriter.registerEntity(40, "MinecartCommandBlock");
        ItemRewriter.registerEntity(41, "Boat");
        ItemRewriter.registerEntity(42, "MinecartRideable");
        ItemRewriter.registerEntity(43, "MinecartChest");
        ItemRewriter.registerEntity(44, "MinecartFurnace");
        ItemRewriter.registerEntity(45, "MinecartTNT");
        ItemRewriter.registerEntity(46, "MinecartHopper");
        ItemRewriter.registerEntity(47, "MinecartSpawner");
        ItemRewriter.registerEntity(48, "Mob");
        ItemRewriter.registerEntity(49, "Monster");
        ItemRewriter.registerEntity(50, "Creeper");
        ItemRewriter.registerEntity(51, "Skeleton");
        ItemRewriter.registerEntity(52, "Spider");
        ItemRewriter.registerEntity(53, "Giant");
        ItemRewriter.registerEntity(54, "Zombie");
        ItemRewriter.registerEntity(55, "Slime");
        ItemRewriter.registerEntity(56, "Ghast");
        ItemRewriter.registerEntity(57, "PigZombie");
        ItemRewriter.registerEntity(58, "Enderman");
        ItemRewriter.registerEntity(59, "CaveSpider");
        ItemRewriter.registerEntity(60, "Silverfish");
        ItemRewriter.registerEntity(61, "Blaze");
        ItemRewriter.registerEntity(62, "LavaSlime");
        ItemRewriter.registerEntity(63, "EnderDragon");
        ItemRewriter.registerEntity(64, "WitherBoss");
        ItemRewriter.registerEntity(65, "Bat");
        ItemRewriter.registerEntity(66, "Witch");
        ItemRewriter.registerEntity(67, "Endermite");
        ItemRewriter.registerEntity(68, "Guardian");
        ItemRewriter.registerEntity(90, "Pig");
        ItemRewriter.registerEntity(91, "Sheep");
        ItemRewriter.registerEntity(92, "Cow");
        ItemRewriter.registerEntity(93, "Chicken");
        ItemRewriter.registerEntity(94, "Squid");
        ItemRewriter.registerEntity(95, "Wolf");
        ItemRewriter.registerEntity(96, "MushroomCow");
        ItemRewriter.registerEntity(97, "SnowMan");
        ItemRewriter.registerEntity(98, "Ozelot");
        ItemRewriter.registerEntity(99, "VillagerGolem");
        ItemRewriter.registerEntity(100, "EntityHorse");
        ItemRewriter.registerEntity(101, "Rabbit");
        ItemRewriter.registerEntity(120, "Villager");
        ItemRewriter.registerEntity(200, "EnderCrystal");
        ItemRewriter.registerPotion(-1, "empty");
        ItemRewriter.registerPotion(0, "water");
        ItemRewriter.registerPotion(64, "mundane");
        ItemRewriter.registerPotion(32, "thick");
        ItemRewriter.registerPotion(16, "awkward");
        ItemRewriter.registerPotion(8198, "night_vision");
        ItemRewriter.registerPotion(8262, "long_night_vision");
        ItemRewriter.registerPotion(8206, "invisibility");
        ItemRewriter.registerPotion(8270, "long_invisibility");
        ItemRewriter.registerPotion(8203, "leaping");
        ItemRewriter.registerPotion(8267, "long_leaping");
        ItemRewriter.registerPotion(8235, "strong_leaping");
        ItemRewriter.registerPotion(8195, "fire_resistance");
        ItemRewriter.registerPotion(8259, "long_fire_resistance");
        ItemRewriter.registerPotion(8194, "swiftness");
        ItemRewriter.registerPotion(8258, "long_swiftness");
        ItemRewriter.registerPotion(8226, "strong_swiftness");
        ItemRewriter.registerPotion(8202, "slowness");
        ItemRewriter.registerPotion(8266, "long_slowness");
        ItemRewriter.registerPotion(8205, "water_breathing");
        ItemRewriter.registerPotion(8269, "long_water_breathing");
        ItemRewriter.registerPotion(8261, "healing");
        ItemRewriter.registerPotion(8229, "strong_healing");
        ItemRewriter.registerPotion(8204, "harming");
        ItemRewriter.registerPotion(8236, "strong_harming");
        ItemRewriter.registerPotion(8196, "poison");
        ItemRewriter.registerPotion(8260, "long_poison");
        ItemRewriter.registerPotion(8228, "strong_poison");
        ItemRewriter.registerPotion(8193, "regeneration");
        ItemRewriter.registerPotion(8257, "long_regeneration");
        ItemRewriter.registerPotion(8225, "strong_regeneration");
        ItemRewriter.registerPotion(8201, "strength");
        ItemRewriter.registerPotion(8265, "long_strength");
        ItemRewriter.registerPotion(8233, "strong_strength");
        ItemRewriter.registerPotion(8200, "weakness");
        ItemRewriter.registerPotion(8264, "long_weakness");
    }
}

