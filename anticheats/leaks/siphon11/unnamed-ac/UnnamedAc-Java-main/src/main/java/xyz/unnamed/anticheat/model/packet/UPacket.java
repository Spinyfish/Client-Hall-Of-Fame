package xyz.unnamed.anticheat.model.packet;

import org.atteo.classindex.IndexSubclasses;
import xyz.unnamed.anticheat.model.PlayerData;
import xyz.unnamed.anticheat.model.packet.nms.UWrappedPacketDataSerializer;

/**
 * Copyright (c) 2022 - Tranquil, LLC.
 *
 * @author incognito@tranquil.cc
 * @date 4/8/2023
 */

@IndexSubclasses
public interface UPacket {
    /***
     * Applies packet-unique updates to given PlayerData
     * @param playerData PlayerData object to update (corresponding to player receiving/sending the packet)
     */
    void handle(PlayerData playerData);

    /***
     * Reads packet data from serialized object
     * @param packetDataSerializer Serializer object for given NMS Packet
     */
    void read(UWrappedPacketDataSerializer packetDataSerializer);
}
