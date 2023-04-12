package xyz.unnamed.anticheat.model.packet.transaction;

import lombok.Getter;
import lombok.Setter;
import xyz.unnamed.anticheat.model.packet.UPacket;

import java.util.LinkedList;

/**
 * Copyright (c) 2022 - Tranquil, LLC.
 *
 * @author incognito@tranquil.cc
 * @date 4/8/2023
 */

@Getter
@Setter
public class TransactionInformation {
    public enum TransactionTickStatus {
        TICK_START,
        TICK_END
    }

    private TransactionTickStatus status;

    private final LinkedList<UPacket> outboundPackets = new LinkedList<>();

    public TransactionInformation() {
        this.status = TransactionTickStatus.TICK_START;
    }
}
