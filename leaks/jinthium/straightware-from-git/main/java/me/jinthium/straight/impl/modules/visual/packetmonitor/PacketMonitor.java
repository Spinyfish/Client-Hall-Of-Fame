package me.jinthium.straight.impl.modules.visual.packetmonitor;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.concurrent.CopyOnWriteArrayList;

class PacketMonitor {
    private final CopyOnWriteArrayList<Integer> packets = new CopyOnWriteArrayList<>();

    private int packetCounter;

    private long lastReset;

    void increment() {
        this.packetCounter++;
    }

    public Collection<Integer> getPacketRecord() {
        return this.packets;
    }

    public void update(final int maxSamples) {
        if (System.currentTimeMillis() - this.lastReset > 1000L) {
            this.packets.add(this.packetCounter);
            this.lastReset = System.currentTimeMillis();
            this.packetCounter = 0;

            if (this.packets.size() > maxSamples) {
                this.packets.remove(packets.size() - 1);
            }
        }
    }

    public void reset() {
        this.packets.clear();
    }
}