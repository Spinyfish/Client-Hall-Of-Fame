package net.minecraft.network.play.client;

import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

import java.io.IOException;

public class C0BPacketEntityAction implements Packet<INetHandlerPlayServer> {
    private int entityID;
    private C0BPacketEntityAction.Action action;
    private int auxData;

    public C0BPacketEntityAction() {
    }

    public C0BPacketEntityAction(final Entity entity, final C0BPacketEntityAction.Action action) {
        this(entity, action, 0);
    }

    public C0BPacketEntityAction(final Entity entity, final C0BPacketEntityAction.Action action, final int auxData) {
        this.entityID = entity.getEntityId();
        this.action = action;
        this.auxData = auxData;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.entityID = buf.readVarIntFromBuffer();
        this.action = buf.readEnumValue(Action.class);
        this.auxData = buf.readVarIntFromBuffer();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeVarIntToBuffer(this.entityID);
        buf.writeEnumValue(this.action);
        buf.writeVarIntToBuffer(this.auxData);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final INetHandlerPlayServer handler) {
        handler.processEntityAction(this);
    }

    public C0BPacketEntityAction.Action getAction() {
        return this.action;
    }

    public int getAuxData() {
        return this.auxData;
    }

    public enum Action {
        START_SNEAKING,
        STOP_SNEAKING,
        STOP_SLEEPING,
        START_SPRINTING,
        STOP_SPRINTING,
        RIDING_JUMP,
        OPEN_INVENTORY
    }
}
