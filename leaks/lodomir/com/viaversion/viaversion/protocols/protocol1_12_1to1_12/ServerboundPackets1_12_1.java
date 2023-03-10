/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_12_1to1_12;

import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;

public enum ServerboundPackets1_12_1 implements ServerboundPacketType
{
    TELEPORT_CONFIRM,
    TAB_COMPLETE,
    CHAT_MESSAGE,
    CLIENT_STATUS,
    CLIENT_SETTINGS,
    WINDOW_CONFIRMATION,
    CLICK_WINDOW_BUTTON,
    CLICK_WINDOW,
    CLOSE_WINDOW,
    PLUGIN_MESSAGE,
    INTERACT_ENTITY,
    KEEP_ALIVE,
    PLAYER_MOVEMENT,
    PLAYER_POSITION,
    PLAYER_POSITION_AND_ROTATION,
    PLAYER_ROTATION,
    VEHICLE_MOVE,
    STEER_BOAT,
    CRAFT_RECIPE_REQUEST,
    PLAYER_ABILITIES,
    PLAYER_DIGGING,
    ENTITY_ACTION,
    STEER_VEHICLE,
    RECIPE_BOOK_DATA,
    RESOURCE_PACK_STATUS,
    ADVANCEMENT_TAB,
    HELD_ITEM_CHANGE,
    CREATIVE_INVENTORY_ACTION,
    UPDATE_SIGN,
    ANIMATION,
    SPECTATE,
    PLAYER_BLOCK_PLACEMENT,
    USE_ITEM;


    @Override
    public int getId() {
        return this.ordinal();
    }

    @Override
    public String getName() {
        return this.name();
    }
}

