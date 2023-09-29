package net.minecraft.network.login.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginServer;

import java.io.IOException;

public class C00PacketLoginStart implements Packet<INetHandlerLoginServer> {
    private GameProfile profile;

    public C00PacketLoginStart() {
    }

    public C00PacketLoginStart(final GameProfile profileIn) {
        this.profile = profileIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.profile = new GameProfile(null, buf.readStringFromBuffer(16));
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeString(this.profile.getName());
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(final INetHandlerLoginServer handler) {
        handler.processLoginStart(this);
    }

    public GameProfile getProfile() {
        return this.profile;
    }
}
