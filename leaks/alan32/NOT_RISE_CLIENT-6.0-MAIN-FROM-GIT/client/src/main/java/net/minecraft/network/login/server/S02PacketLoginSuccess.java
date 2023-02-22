package net.minecraft.network.login.server;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;

import java.io.IOException;
import java.util.UUID;

public class S02PacketLoginSuccess implements Packet<INetHandlerLoginClient> {
    private GameProfile profile;

    public S02PacketLoginSuccess() {
    }

    public S02PacketLoginSuccess(final GameProfile profileIn) {
        this.profile = profileIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(final PacketBuffer buf) throws IOException {
        final String s = buf.readStringFromBuffer(36);
        final String s1 = buf.readStringFromBuffer(16);
        final UUID uuid = UUID.fromString(s);
        this.profile = new GameProfile(uuid, s1);
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(final PacketBuffer buf) throws IOException {
        final UUID uuid = this.profile.getId();
        buf.writeString(uuid == null ? "" : uuid.toString());
        buf.writeString(this.profile.getName());
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final INetHandlerLoginClient handler) {
        handler.handleLoginSuccess(this);
    }

    public GameProfile getProfile() {
        return this.profile;
    }
}
