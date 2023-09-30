package net.minecraft.network.play.server;

import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

import java.io.IOException;

public class S19PacketEntityStatus implements Packet<INetHandlerPlayClient> {
    private int entityId;
    private byte logicOpcode;

    public S19PacketEntityStatus() {
    }

    public S19PacketEntityStatus(final Entity entityIn, final byte opCodeIn) {
        this.entityId = entityIn.getEntityId();
        this.logicOpcode = opCodeIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.entityId = buf.readInt();
        this.logicOpcode = buf.readByte();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeInt(this.entityId);
        buf.writeByte(this.logicOpcode);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final INetHandlerPlayClient handler) {
        handler.handleEntityStatus(this);
    }

    public Entity getEntity(final World worldIn) {
        return worldIn.getEntityByID(this.entityId);
    }

    public byte getOpCode() {
        return this.logicOpcode;
    }
}
