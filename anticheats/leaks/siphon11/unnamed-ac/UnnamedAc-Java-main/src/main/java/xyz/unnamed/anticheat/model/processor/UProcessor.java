package xyz.unnamed.anticheat.model.processor;

import org.atteo.classindex.IndexSubclasses;
import xyz.unnamed.anticheat.model.packet.UPacket;

/**
 * Copyright (c) 2022 - Tranquil, LLC.
 *
 * @author incognito@tranquil.cc
 * @date 4/8/2023
 */

@IndexSubclasses
public interface UProcessor {
    /***
     * Updates the processor with information from packet
     * @param packet Packet to update with
     */
    void update(UPacket packet);
}
