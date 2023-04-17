package me.levansj01.verus.compat;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.data.state.Releasable;

public abstract class VPacket<P> implements Consumer<Object>, Releasable {

    public static final AtomicInteger PACKET_COUNT = new AtomicInteger();
    public abstract int ordinal();
    public abstract void handle(PacketHandler paramPacketHandler);

    public static int count() {
        return PACKET_COUNT.getAndIncrement();
    }
}