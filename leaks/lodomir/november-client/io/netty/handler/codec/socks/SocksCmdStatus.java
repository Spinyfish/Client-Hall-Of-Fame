/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.socks;

public enum SocksCmdStatus {
    SUCCESS(0),
    FAILURE(1),
    FORBIDDEN(2),
    NETWORK_UNREACHABLE(3),
    HOST_UNREACHABLE(4),
    REFUSED(5),
    TTL_EXPIRED(6),
    COMMAND_NOT_SUPPORTED(7),
    ADDRESS_NOT_SUPPORTED(8),
    UNASSIGNED(-1);

    private final byte b;

    private SocksCmdStatus(byte b) {
        this.b = b;
    }

    @Deprecated
    public static SocksCmdStatus fromByte(byte b) {
        return SocksCmdStatus.valueOf(b);
    }

    public static SocksCmdStatus valueOf(byte b) {
        for (SocksCmdStatus code : SocksCmdStatus.values()) {
            if (code.b != b) continue;
            return code;
        }
        return UNASSIGNED;
    }

    public byte byteValue() {
        return this.b;
    }
}

