package niggerlib.mc.protocol.packet.ingame.server.window;

import niggerlib.mc.protocol.data.MagicValues;
import niggerlib.mc.protocol.data.game.window.property.WindowProperty;
import niggerlib.mc.protocol.packet.MinecraftPacket;
import niggerlib.packetlib.io.NetInput;
import niggerlib.packetlib.io.NetOutput;

import java.io.IOException;

public class ServerWindowPropertyPacket extends MinecraftPacket {
    private int windowId;
    private int property;
    private int value;

    @SuppressWarnings("unused")
    private ServerWindowPropertyPacket() {
    }

    public ServerWindowPropertyPacket(int windowId, int property, int value) {
        this.windowId = windowId;
        this.property = property;
        this.value = value;
    }

    public <T extends Enum<T> & WindowProperty> ServerWindowPropertyPacket(int windowId, T property, int value) {
        this.windowId = windowId;
        this.property = MagicValues.value(Integer.class, property);
        this.value = value;
    }

    public int getWindowId() {
        return this.windowId;
    }

    public int getRawProperty() {
        return this.property;
    }

    public <T extends Enum<T> & WindowProperty> T getProperty(Class<T> type) {
        return MagicValues.key(type, this.value);
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.windowId = in.readUnsignedByte();
        this.property = in.readShort();
        this.value = in.readShort();
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeByte(this.windowId);
        out.writeShort(this.property);
        out.writeShort(this.value);
    }
}
