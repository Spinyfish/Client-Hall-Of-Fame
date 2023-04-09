package niggerlib.mc.protocol.packet;

import niggerlib.mc.protocol.util.ObjectUtil;
import niggerlib.packetlib.packet.Packet;

public abstract class MinecraftPacket implements Packet {
    @Override
    public boolean isPriority() {
        return false;
    }

    @Override
    public String toString() {
        return ObjectUtil.toString(this);
    }
}
