package net.minecraft.network.login.server;

import net.minecraft.network.login.*;
import java.security.*;
import net.minecraft.util.*;
import java.io.*;
import net.minecraft.network.*;

public class S01PacketEncryptionRequest implements Packet<INetHandlerLoginClient>
{
    private String hashedServerId;
    private PublicKey publicKey;
    private byte[] verifyToken;
    
    public S01PacketEncryptionRequest() {
    }
    
    public S01PacketEncryptionRequest(final String serverId, final PublicKey key, final byte[] verifyToken) {
        this.hashedServerId = serverId;
        this.publicKey = key;
        this.verifyToken = verifyToken;
    }
    
    @Override
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.hashedServerId = buf.readStringFromBuffer(20);
        this.publicKey = CryptManager.decodePublicKey(buf.readByteArray());
        this.verifyToken = buf.readByteArray();
    }
    
    @Override
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeString(this.hashedServerId);
        buf.writeByteArray(this.publicKey.getEncoded());
        buf.writeByteArray(this.verifyToken);
    }
    
    @Override
    public void processPacket(final INetHandlerLoginClient handler) {
        handler.handleEncryptionRequest(this);
    }
    
    public String getServerId() {
        return this.hashedServerId;
    }
    
    public PublicKey getPublicKey() {
        return this.publicKey;
    }
    
    public byte[] getVerifyToken() {
        return this.verifyToken;
    }
}
