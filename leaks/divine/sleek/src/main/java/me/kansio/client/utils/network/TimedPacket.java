package me.kansio.client.utils.network;

import net.minecraft.network.Packet;

/**
 * @author Moshi
 */
public class TimedPacket {

    private Packet<?> packet;
    private long time;

    public TimedPacket(Packet<?> packet, long time) {
        this.time = time;
        this.packet = packet;
    }

    public long postAddTime() {
        return System.currentTimeMillis() - time;
    }

    public void send() {
        PacketUtil.sendPacket(this.getPacket());
    }

    public void sendSilent() {
        PacketUtil.sendPacketNoEvent(this.getPacket());
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }
}