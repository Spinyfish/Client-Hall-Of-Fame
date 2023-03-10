/*
 * Decompiled with CFR 0.152.
 */
package de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.types;

import java.util.HashMap;

public final class Particle
extends Enum<Particle> {
    public static final /* enum */ Particle EXPLOSION_NORMAL;
    public static final /* enum */ Particle EXPLOSION_LARGE;
    public static final /* enum */ Particle EXPLOSION_HUGE;
    public static final /* enum */ Particle FIREWORKS_SPARK;
    public static final /* enum */ Particle WATER_BUBBLE;
    public static final /* enum */ Particle WATER_SPLASH;
    public static final /* enum */ Particle WATER_WAKE;
    public static final /* enum */ Particle SUSPENDED;
    public static final /* enum */ Particle SUSPENDED_DEPTH;
    public static final /* enum */ Particle CRIT;
    public static final /* enum */ Particle CRIT_MAGIC;
    public static final /* enum */ Particle SMOKE_NORMAL;
    public static final /* enum */ Particle SMOKE_LARGE;
    public static final /* enum */ Particle SPELL;
    public static final /* enum */ Particle SPELL_INSTANT;
    public static final /* enum */ Particle SPELL_MOB;
    public static final /* enum */ Particle SPELL_MOB_AMBIENT;
    public static final /* enum */ Particle SPELL_WITCH;
    public static final /* enum */ Particle DRIP_WATER;
    public static final /* enum */ Particle DRIP_LAVA;
    public static final /* enum */ Particle VILLAGER_ANGRY;
    public static final /* enum */ Particle VILLAGER_HAPPY;
    public static final /* enum */ Particle TOWN_AURA;
    public static final /* enum */ Particle NOTE;
    public static final /* enum */ Particle PORTAL;
    public static final /* enum */ Particle ENCHANTMENT_TABLE;
    public static final /* enum */ Particle FLAME;
    public static final /* enum */ Particle LAVA;
    public static final /* enum */ Particle FOOTSTEP;
    public static final /* enum */ Particle CLOUD;
    public static final /* enum */ Particle REDSTONE;
    public static final /* enum */ Particle SNOWBALL;
    public static final /* enum */ Particle SNOW_SHOVEL;
    public static final /* enum */ Particle SLIME;
    public static final /* enum */ Particle HEART;
    public static final /* enum */ Particle BARRIER;
    public static final /* enum */ Particle ICON_CRACK;
    public static final /* enum */ Particle BLOCK_CRACK;
    public static final /* enum */ Particle BLOCK_DUST;
    public static final /* enum */ Particle WATER_DROP;
    public static final /* enum */ Particle ITEM_TAKE;
    public static final /* enum */ Particle MOB_APPEARANCE;
    public final String name;
    public final int extra;
    private static final HashMap<String, Particle> particleMap;
    private static final /* synthetic */ Particle[] $VALUES;

    public static Particle[] values() {
        return (Particle[])$VALUES.clone();
    }

    public static Particle valueOf(String name) {
        return Enum.valueOf(Particle.class, name);
    }

    private Particle(String name) {
        this(name, 0);
    }

    private Particle(String name, int extra) {
        this.name = name;
        this.extra = extra;
    }

    public static Particle find(String part) {
        return particleMap.get(part);
    }

    public static Particle find(int id) {
        if (id < 0) {
            return null;
        }
        Particle[] values = Particle.values();
        return id >= values.length ? null : values[id];
    }

    static {
        Particle[] particles;
        EXPLOSION_NORMAL = new Particle("explode");
        EXPLOSION_LARGE = new Particle("largeexplode");
        EXPLOSION_HUGE = new Particle("hugeexplosion");
        FIREWORKS_SPARK = new Particle("fireworksSpark");
        WATER_BUBBLE = new Particle("bubble");
        WATER_SPLASH = new Particle("splash");
        WATER_WAKE = new Particle("wake");
        SUSPENDED = new Particle("suspended");
        SUSPENDED_DEPTH = new Particle("depthsuspend");
        CRIT = new Particle("crit");
        CRIT_MAGIC = new Particle("magicCrit");
        SMOKE_NORMAL = new Particle("smoke");
        SMOKE_LARGE = new Particle("largesmoke");
        SPELL = new Particle("spell");
        SPELL_INSTANT = new Particle("instantSpell");
        SPELL_MOB = new Particle("mobSpell");
        SPELL_MOB_AMBIENT = new Particle("mobSpellAmbient");
        SPELL_WITCH = new Particle("witchMagic");
        DRIP_WATER = new Particle("dripWater");
        DRIP_LAVA = new Particle("dripLava");
        VILLAGER_ANGRY = new Particle("angryVillager");
        VILLAGER_HAPPY = new Particle("happyVillager");
        TOWN_AURA = new Particle("townaura");
        NOTE = new Particle("note");
        PORTAL = new Particle("portal");
        ENCHANTMENT_TABLE = new Particle("enchantmenttable");
        FLAME = new Particle("flame");
        LAVA = new Particle("lava");
        FOOTSTEP = new Particle("footstep");
        CLOUD = new Particle("cloud");
        REDSTONE = new Particle("reddust");
        SNOWBALL = new Particle("snowballpoof");
        SNOW_SHOVEL = new Particle("snowshovel");
        SLIME = new Particle("slime");
        HEART = new Particle("heart");
        BARRIER = new Particle("barrier");
        ICON_CRACK = new Particle("iconcrack", 2);
        BLOCK_CRACK = new Particle("blockcrack", 1);
        BLOCK_DUST = new Particle("blockdust", 1);
        WATER_DROP = new Particle("droplet");
        ITEM_TAKE = new Particle("take");
        MOB_APPEARANCE = new Particle("mobappearance");
        $VALUES = new Particle[]{EXPLOSION_NORMAL, EXPLOSION_LARGE, EXPLOSION_HUGE, FIREWORKS_SPARK, WATER_BUBBLE, WATER_SPLASH, WATER_WAKE, SUSPENDED, SUSPENDED_DEPTH, CRIT, CRIT_MAGIC, SMOKE_NORMAL, SMOKE_LARGE, SPELL, SPELL_INSTANT, SPELL_MOB, SPELL_MOB_AMBIENT, SPELL_WITCH, DRIP_WATER, DRIP_LAVA, VILLAGER_ANGRY, VILLAGER_HAPPY, TOWN_AURA, NOTE, PORTAL, ENCHANTMENT_TABLE, FLAME, LAVA, FOOTSTEP, CLOUD, REDSTONE, SNOWBALL, SNOW_SHOVEL, SLIME, HEART, BARRIER, ICON_CRACK, BLOCK_CRACK, BLOCK_DUST, WATER_DROP, ITEM_TAKE, MOB_APPEARANCE};
        particleMap = new HashMap();
        for (Particle particle : particles = Particle.values()) {
            particleMap.put(particle.name, particle);
        }
    }
}

