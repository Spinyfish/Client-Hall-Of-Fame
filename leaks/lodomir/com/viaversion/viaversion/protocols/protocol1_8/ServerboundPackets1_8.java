/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_8;

import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;

public enum ServerboundPackets1_8 implements ServerboundPacketType
{
    KEEP_ALIVE,
    CHAT_MESSAGE,
    INTERACT_ENTITY,
    PLAYER_MOVEMENT,
    PLAYER_POSITION,
    PLAYER_ROTATION,
    PLAYER_POSITION_AND_ROTATION,
    PLAYER_DIGGING,
    PLAYER_BLOCK_PLACEMENT,
    HELD_ITEM_CHANGE,
    ANIMATION,
    ENTITY_ACTION,
    STEER_VEHICLE,
    CLOSE_WINDOW,
    CLICK_WINDOW,
    WINDOW_CONFIRMATION,
    CREATIVE_INVENTORY_ACTION,
    CLICK_WINDOW_BUTTON,
    UPDATE_SIGN,
    PLAYER_ABILITIES,
    TAB_COMPLETE,
    CLIENT_SETTINGS,
    CLIENT_STATUS,
    PLUGIN_MESSAGE,
    SPECTATE,
    RESOURCE_PACK_STATUS;


    @Override
    public int getId() {
        return this.ordinal();
    }

    @Override
    public String getName() {
        return this.name();
    }
}

