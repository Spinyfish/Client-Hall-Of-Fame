package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.EnumDifficulty;

import java.io.IOException;

public class S41PacketServerDifficulty implements Packet<INetHandlerPlayClient> {
    private EnumDifficulty difficulty;
    private boolean difficultyLocked;

    public S41PacketServerDifficulty() {
    }

    public S41PacketServerDifficulty(final EnumDifficulty difficultyIn, final boolean lockedIn) {
        this.difficulty = difficultyIn;
        this.difficultyLocked = lockedIn;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final INetHandlerPlayClient handler) {
        handler.handleServerDifficulty(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.difficulty = EnumDifficulty.getDifficultyEnum(buf.readUnsignedByte());
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeByte(this.difficulty.getDifficultyId());
    }

    public boolean isDifficultyLocked() {
        return this.difficultyLocked;
    }

    public EnumDifficulty getDifficulty() {
        return this.difficulty;
    }
}
