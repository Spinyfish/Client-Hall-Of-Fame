/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_9to1_8;

import java.util.HashMap;
import java.util.Map;

public enum ArmorType {
    LEATHER_HELMET(1, 298, "minecraft:leather_helmet"),
    LEATHER_CHESTPLATE(3, 299, "minecraft:leather_chestplate"),
    LEATHER_LEGGINGS(2, 300, "minecraft:leather_leggings"),
    LEATHER_BOOTS(1, 301, "minecraft:leather_boots"),
    CHAINMAIL_HELMET(2, 302, "minecraft:chainmail_helmet"),
    CHAINMAIL_CHESTPLATE(5, 303, "minecraft:chainmail_chestplate"),
    CHAINMAIL_LEGGINGS(4, 304, "minecraft:chainmail_leggings"),
    CHAINMAIL_BOOTS(1, 305, "minecraft:chainmail_boots"),
    IRON_HELMET(2, 306, "minecraft:iron_helmet"),
    IRON_CHESTPLATE(6, 307, "minecraft:iron_chestplate"),
    IRON_LEGGINGS(5, 308, "minecraft:iron_leggings"),
    IRON_BOOTS(2, 309, "minecraft:iron_boots"),
    DIAMOND_HELMET(3, 310, "minecraft:diamond_helmet"),
    DIAMOND_CHESTPLATE(8, 311, "minecraft:diamond_chestplate"),
    DIAMOND_LEGGINGS(6, 312, "minecraft:diamond_leggings"),
    DIAMOND_BOOTS(3, 313, "minecraft:diamond_boots"),
    GOLD_HELMET(2, 314, "minecraft:gold_helmet"),
    GOLD_CHESTPLATE(5, 315, "minecraft:gold_chestplate"),
    GOLD_LEGGINGS(3, 316, "minecraft:gold_leggings"),
    GOLD_BOOTS(1, 317, "minecraft:gold_boots"),
    NONE(0, 0, "none");

    private static final Map<Integer, ArmorType> armor;
    private final int armorPoints;
    private final int id;
    private final String type;

    private ArmorType(int armorPoints, int id, String type) {
        this.armorPoints = armorPoints;
        this.id = id;
        this.type = type;
    }

    public int getArmorPoints() {
        return this.armorPoints;
    }

    public String getType() {
        return this.type;
    }

    public static ArmorType findById(int id) {
        ArmorType type = armor.get(id);
        return type == null ? NONE : type;
    }

    public static ArmorType findByType(String type) {
        for (ArmorType a : ArmorType.values()) {
            if (!a.getType().equals(type)) continue;
            return a;
        }
        return NONE;
    }

    public static boolean isArmor(int id) {
        return armor.containsKey(id);
    }

    public static boolean isArmor(String type) {
        for (ArmorType a : ArmorType.values()) {
            if (!a.getType().equals(type)) continue;
            return true;
        }
        return false;
    }

    public int getId() {
        return this.id;
    }

    static {
        armor = new HashMap<Integer, ArmorType>();
        for (ArmorType a : ArmorType.values()) {
            armor.put(a.getId(), a);
        }
    }
}

