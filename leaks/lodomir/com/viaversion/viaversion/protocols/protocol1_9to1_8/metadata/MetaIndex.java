/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.protocols.protocol1_9to1_8.metadata;

import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_8;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_9;
import com.viaversion.viaversion.util.Pair;
import java.util.HashMap;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.Nullable;

public enum MetaIndex {
    ENTITY_STATUS(Entity1_10Types.EntityType.ENTITY, 0, MetaType1_8.Byte, MetaType1_9.Byte),
    ENTITY_AIR(Entity1_10Types.EntityType.ENTITY, 1, MetaType1_8.Short, MetaType1_9.VarInt),
    ENTITY_NAMETAG(Entity1_10Types.EntityType.ENTITY, 2, MetaType1_8.String, MetaType1_9.String),
    ENTITY_ALWAYS_SHOW_NAMETAG(Entity1_10Types.EntityType.ENTITY, 3, MetaType1_8.Byte, MetaType1_9.Boolean),
    ENTITY_SILENT(Entity1_10Types.EntityType.ENTITY, 4, MetaType1_8.Byte, MetaType1_9.Boolean),
    LIVINGENTITY_HEALTH(Entity1_10Types.EntityType.ENTITY_LIVING, 6, MetaType1_8.Float, MetaType1_9.Float),
    LIVINGENTITY_POTION_EFFECT_COLOR(Entity1_10Types.EntityType.ENTITY_LIVING, 7, MetaType1_8.Int, MetaType1_9.VarInt),
    LIVINGENTITY_IS_POTION_AMBIENT(Entity1_10Types.EntityType.ENTITY_LIVING, 8, MetaType1_8.Byte, MetaType1_9.Boolean),
    LIVINGENTITY_NUMBER_OF_ARROWS_IN(Entity1_10Types.EntityType.ENTITY_LIVING, 9, MetaType1_8.Byte, MetaType1_9.VarInt),
    LIVINGENTITY_NO_AI(Entity1_10Types.EntityType.ENTITY_LIVING, 15, MetaType1_8.Byte, 10, MetaType1_9.Byte),
    AGEABLE_AGE(Entity1_10Types.EntityType.ENTITY_AGEABLE, 12, MetaType1_8.Byte, 11, MetaType1_9.Boolean),
    STAND_INFO(Entity1_10Types.EntityType.ARMOR_STAND, 10, MetaType1_8.Byte, MetaType1_9.Byte),
    STAND_HEAD_POS(Entity1_10Types.EntityType.ARMOR_STAND, 11, MetaType1_8.Rotation, MetaType1_9.Vector3F),
    STAND_BODY_POS(Entity1_10Types.EntityType.ARMOR_STAND, 12, MetaType1_8.Rotation, MetaType1_9.Vector3F),
    STAND_LA_POS(Entity1_10Types.EntityType.ARMOR_STAND, 13, MetaType1_8.Rotation, MetaType1_9.Vector3F),
    STAND_RA_POS(Entity1_10Types.EntityType.ARMOR_STAND, 14, MetaType1_8.Rotation, MetaType1_9.Vector3F),
    STAND_LL_POS(Entity1_10Types.EntityType.ARMOR_STAND, 15, MetaType1_8.Rotation, MetaType1_9.Vector3F),
    STAND_RL_POS(Entity1_10Types.EntityType.ARMOR_STAND, 16, MetaType1_8.Rotation, MetaType1_9.Vector3F),
    PLAYER_SKIN_FLAGS(Entity1_10Types.EntityType.ENTITY_HUMAN, 10, MetaType1_8.Byte, 12, MetaType1_9.Byte),
    PLAYER_HUMAN_BYTE(Entity1_10Types.EntityType.ENTITY_HUMAN, 16, MetaType1_8.Byte, null),
    PLAYER_ADDITIONAL_HEARTS(Entity1_10Types.EntityType.ENTITY_HUMAN, 17, MetaType1_8.Float, 10, MetaType1_9.Float),
    PLAYER_SCORE(Entity1_10Types.EntityType.ENTITY_HUMAN, 18, MetaType1_8.Int, 11, MetaType1_9.VarInt),
    PLAYER_HAND(Entity1_10Types.EntityType.ENTITY_HUMAN, -1, MetaType1_8.NonExistent, 5, MetaType1_9.Byte),
    SOMETHING_ANTICHEAT_PLUGINS_FOR_SOME_REASON_USE(Entity1_10Types.EntityType.ENTITY_HUMAN, 11, MetaType1_8.Byte, null),
    HORSE_INFO(Entity1_10Types.EntityType.HORSE, 16, MetaType1_8.Int, 12, MetaType1_9.Byte),
    HORSE_TYPE(Entity1_10Types.EntityType.HORSE, 19, MetaType1_8.Byte, 13, MetaType1_9.VarInt),
    HORSE_SUBTYPE(Entity1_10Types.EntityType.HORSE, 20, MetaType1_8.Int, 14, MetaType1_9.VarInt),
    HORSE_OWNER(Entity1_10Types.EntityType.HORSE, 21, MetaType1_8.String, 15, MetaType1_9.OptUUID),
    HORSE_ARMOR(Entity1_10Types.EntityType.HORSE, 22, MetaType1_8.Int, 16, MetaType1_9.VarInt),
    BAT_ISHANGING(Entity1_10Types.EntityType.BAT, 16, MetaType1_8.Byte, 11, MetaType1_9.Byte),
    TAMING_INFO(Entity1_10Types.EntityType.ENTITY_TAMEABLE_ANIMAL, 16, MetaType1_8.Byte, 12, MetaType1_9.Byte),
    TAMING_OWNER(Entity1_10Types.EntityType.ENTITY_TAMEABLE_ANIMAL, 17, MetaType1_8.String, 13, MetaType1_9.OptUUID),
    OCELOT_TYPE(Entity1_10Types.EntityType.OCELOT, 18, MetaType1_8.Byte, 14, MetaType1_9.VarInt),
    WOLF_HEALTH(Entity1_10Types.EntityType.WOLF, 18, MetaType1_8.Float, 14, MetaType1_9.Float),
    WOLF_BEGGING(Entity1_10Types.EntityType.WOLF, 19, MetaType1_8.Byte, 15, MetaType1_9.Boolean),
    WOLF_COLLAR(Entity1_10Types.EntityType.WOLF, 20, MetaType1_8.Byte, 16, MetaType1_9.VarInt),
    PIG_SADDLE(Entity1_10Types.EntityType.PIG, 16, MetaType1_8.Byte, 12, MetaType1_9.Boolean),
    RABBIT_TYPE(Entity1_10Types.EntityType.RABBIT, 18, MetaType1_8.Byte, 12, MetaType1_9.VarInt),
    SHEEP_COLOR(Entity1_10Types.EntityType.SHEEP, 16, MetaType1_8.Byte, 12, MetaType1_9.Byte),
    VILLAGER_PROFESSION(Entity1_10Types.EntityType.VILLAGER, 16, MetaType1_8.Int, 12, MetaType1_9.VarInt),
    ENDERMAN_BLOCKSTATE(Entity1_10Types.EntityType.ENDERMAN, 16, MetaType1_8.Short, 11, MetaType1_9.BlockID),
    ENDERMAN_BLOCKDATA(Entity1_10Types.EntityType.ENDERMAN, 17, MetaType1_8.Byte, null),
    ENDERMAN_ISSCREAMING(Entity1_10Types.EntityType.ENDERMAN, 18, MetaType1_8.Byte, 12, MetaType1_9.Boolean),
    ZOMBIE_ISCHILD(Entity1_10Types.EntityType.ZOMBIE, 12, MetaType1_8.Byte, 11, MetaType1_9.Boolean),
    ZOMBIE_ISVILLAGER(Entity1_10Types.EntityType.ZOMBIE, 13, MetaType1_8.Byte, 12, MetaType1_9.VarInt),
    ZOMBIE_ISCONVERTING(Entity1_10Types.EntityType.ZOMBIE, 14, MetaType1_8.Byte, 13, MetaType1_9.Boolean),
    BLAZE_ONFIRE(Entity1_10Types.EntityType.BLAZE, 16, MetaType1_8.Byte, 11, MetaType1_9.Byte),
    SPIDER_CIMBING(Entity1_10Types.EntityType.SPIDER, 16, MetaType1_8.Byte, 11, MetaType1_9.Byte),
    CREEPER_FUSE(Entity1_10Types.EntityType.CREEPER, 16, MetaType1_8.Byte, 11, MetaType1_9.VarInt),
    CREEPER_ISPOWERED(Entity1_10Types.EntityType.CREEPER, 17, MetaType1_8.Byte, 12, MetaType1_9.Boolean),
    CREEPER_ISIGNITED(Entity1_10Types.EntityType.CREEPER, 18, MetaType1_8.Byte, 13, MetaType1_9.Boolean),
    GHAST_ISATTACKING(Entity1_10Types.EntityType.GHAST, 16, MetaType1_8.Byte, 11, MetaType1_9.Boolean),
    SLIME_SIZE(Entity1_10Types.EntityType.SLIME, 16, MetaType1_8.Byte, 11, MetaType1_9.VarInt),
    SKELETON_TYPE(Entity1_10Types.EntityType.SKELETON, 13, MetaType1_8.Byte, 11, MetaType1_9.VarInt),
    WITCH_AGGRO(Entity1_10Types.EntityType.WITCH, 21, MetaType1_8.Byte, 11, MetaType1_9.Boolean),
    IRON_PLAYERMADE(Entity1_10Types.EntityType.IRON_GOLEM, 16, MetaType1_8.Byte, 11, MetaType1_9.Byte),
    WITHER_TARGET1(Entity1_10Types.EntityType.WITHER, 17, MetaType1_8.Int, 11, MetaType1_9.VarInt),
    WITHER_TARGET2(Entity1_10Types.EntityType.WITHER, 18, MetaType1_8.Int, 12, MetaType1_9.VarInt),
    WITHER_TARGET3(Entity1_10Types.EntityType.WITHER, 19, MetaType1_8.Int, 13, MetaType1_9.VarInt),
    WITHER_INVULN_TIME(Entity1_10Types.EntityType.WITHER, 20, MetaType1_8.Int, 14, MetaType1_9.VarInt),
    WITHER_PROPERTIES(Entity1_10Types.EntityType.WITHER, 10, MetaType1_8.Byte, null),
    WITHER_UNKNOWN(Entity1_10Types.EntityType.WITHER, 11, MetaType1_8.NonExistent, null),
    WITHERSKULL_INVULN(Entity1_10Types.EntityType.WITHER_SKULL, 10, MetaType1_8.Byte, 5, MetaType1_9.Boolean),
    GUARDIAN_INFO(Entity1_10Types.EntityType.GUARDIAN, 16, MetaType1_8.Int, 11, MetaType1_9.Byte),
    GUARDIAN_TARGET(Entity1_10Types.EntityType.GUARDIAN, 17, MetaType1_8.Int, 12, MetaType1_9.VarInt),
    BOAT_SINCEHIT(Entity1_10Types.EntityType.BOAT, 17, MetaType1_8.Int, 5, MetaType1_9.VarInt),
    BOAT_FORWARDDIR(Entity1_10Types.EntityType.BOAT, 18, MetaType1_8.Int, 6, MetaType1_9.VarInt),
    BOAT_DMGTAKEN(Entity1_10Types.EntityType.BOAT, 19, MetaType1_8.Float, 7, MetaType1_9.Float),
    MINECART_SHAKINGPOWER(Entity1_10Types.EntityType.MINECART_ABSTRACT, 17, MetaType1_8.Int, 5, MetaType1_9.VarInt),
    MINECART_SHAKINGDIRECTION(Entity1_10Types.EntityType.MINECART_ABSTRACT, 18, MetaType1_8.Int, 6, MetaType1_9.VarInt),
    MINECART_DAMAGETAKEN(Entity1_10Types.EntityType.MINECART_ABSTRACT, 19, MetaType1_8.Float, 7, MetaType1_9.Float),
    MINECART_BLOCK(Entity1_10Types.EntityType.MINECART_ABSTRACT, 20, MetaType1_8.Int, 8, MetaType1_9.VarInt),
    MINECART_BLOCK_Y(Entity1_10Types.EntityType.MINECART_ABSTRACT, 21, MetaType1_8.Int, 9, MetaType1_9.VarInt),
    MINECART_SHOWBLOCK(Entity1_10Types.EntityType.MINECART_ABSTRACT, 22, MetaType1_8.Byte, 10, MetaType1_9.Boolean),
    MINECART_COMMANDBLOCK_COMMAND(Entity1_10Types.EntityType.MINECART_ABSTRACT, 23, MetaType1_8.String, 11, MetaType1_9.String),
    MINECART_COMMANDBLOCK_OUTPUT(Entity1_10Types.EntityType.MINECART_ABSTRACT, 24, MetaType1_8.String, 12, MetaType1_9.Chat),
    FURNACECART_ISPOWERED(Entity1_10Types.EntityType.MINECART_ABSTRACT, 16, MetaType1_8.Byte, 11, MetaType1_9.Boolean),
    ITEM_ITEM(Entity1_10Types.EntityType.DROPPED_ITEM, 10, MetaType1_8.Slot, 5, MetaType1_9.Slot),
    ARROW_ISCRIT(Entity1_10Types.EntityType.ARROW, 16, MetaType1_8.Byte, 5, MetaType1_9.Byte),
    FIREWORK_INFO(Entity1_10Types.EntityType.FIREWORK, 8, MetaType1_8.Slot, 5, MetaType1_9.Slot),
    ITEMFRAME_ITEM(Entity1_10Types.EntityType.ITEM_FRAME, 8, MetaType1_8.Slot, 5, MetaType1_9.Slot),
    ITEMFRAME_ROTATION(Entity1_10Types.EntityType.ITEM_FRAME, 9, MetaType1_8.Byte, 6, MetaType1_9.VarInt),
    ENDERCRYSTAL_HEALTH(Entity1_10Types.EntityType.ENDER_CRYSTAL, 8, MetaType1_8.Int, null),
    ENDERDRAGON_UNKNOWN(Entity1_10Types.EntityType.ENDER_DRAGON, 5, MetaType1_8.Byte, null),
    ENDERDRAGON_NAME(Entity1_10Types.EntityType.ENDER_DRAGON, 10, MetaType1_8.String, null),
    ENDERDRAGON_FLAG(Entity1_10Types.EntityType.ENDER_DRAGON, 15, MetaType1_8.Byte, null),
    ENDERDRAGON_PHASE(Entity1_10Types.EntityType.ENDER_DRAGON, 11, MetaType1_8.Byte, MetaType1_9.VarInt);

    private static final HashMap<Pair<Entity1_10Types.EntityType, Integer>, MetaIndex> metadataRewrites;
    private final Entity1_10Types.EntityType clazz;
    private final int newIndex;
    private final MetaType1_9 newType;
    private final MetaType1_8 oldType;
    private final int index;

    private MetaIndex(Entity1_10Types.EntityType type, @Nullable int index, MetaType1_8 oldType, MetaType1_9 newType) {
        this.clazz = type;
        this.index = index;
        this.newIndex = index;
        this.oldType = oldType;
        this.newType = newType;
    }

    private MetaIndex(Entity1_10Types.EntityType type, int index, @Nullable MetaType1_8 oldType, int newIndex, MetaType1_9 newType) {
        this.clazz = type;
        this.index = index;
        this.oldType = oldType;
        this.newIndex = newIndex;
        this.newType = newType;
    }

    public Entity1_10Types.EntityType getClazz() {
        return this.clazz;
    }

    public int getNewIndex() {
        return this.newIndex;
    }

    public @Nullable MetaType1_9 getNewType() {
        return this.newType;
    }

    public MetaType1_8 getOldType() {
        return this.oldType;
    }

    public int getIndex() {
        return this.index;
    }

    private static Optional<MetaIndex> getIndex(EntityType type, int index) {
        Pair<EntityType, Integer> pair = new Pair<EntityType, Integer>(type, index);
        return Optional.ofNullable(metadataRewrites.get(pair));
    }

    public static MetaIndex searchIndex(EntityType type, int index) {
        EntityType currentType = type;
        do {
            Optional<MetaIndex> optMeta;
            if (!(optMeta = MetaIndex.getIndex(currentType, index)).isPresent()) continue;
            return optMeta.get();
        } while ((currentType = currentType.getParent()) != null);
        return null;
    }

    static {
        metadataRewrites = new HashMap();
        for (MetaIndex index : MetaIndex.values()) {
            metadataRewrites.put(new Pair<Entity1_10Types.EntityType, Integer>(index.clazz, index.index), index);
        }
    }
}

