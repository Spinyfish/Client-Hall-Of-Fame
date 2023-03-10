package de.datasecs.hydra.shared.protocol.packets;

import io.netty.buffer.ByteBuf;

/**
 * Created with love by DataSecs on 09.04.18
 */
@PacketId(Byte.MIN_VALUE)
public class StandardPacket extends Packet {

    private Object object;

    public StandardPacket() {}

    public StandardPacket(Object object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return object.toString();
    }

    @Override
    public void read(ByteBuf byteBuf) {
        readObject(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        writeObject(byteBuf, object);
    }
}