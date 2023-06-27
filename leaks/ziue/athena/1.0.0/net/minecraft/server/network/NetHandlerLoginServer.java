package net.minecraft.server.network;

import net.minecraft.network.login.*;
import java.util.concurrent.atomic.*;
import net.minecraft.server.*;
import com.mojang.authlib.*;
import javax.crypto.*;
import net.minecraft.entity.player.*;
import net.minecraft.network.*;
import io.netty.channel.*;
import io.netty.util.concurrent.*;
import org.apache.commons.lang3.*;
import net.minecraft.network.login.server.*;
import net.minecraft.network.login.client.*;
import net.minecraft.util.*;
import java.math.*;
import java.util.*;
import com.mojang.authlib.exceptions.*;
import java.security.*;
import com.google.common.base.*;
import org.apache.logging.log4j.*;

public class NetHandlerLoginServer implements INetHandlerLoginServer, ITickable
{
    private static final AtomicInteger AUTHENTICATOR_THREAD_ID;
    private static final Logger logger;
    private static final Random RANDOM;
    private final byte[] verifyToken;
    private final MinecraftServer server;
    public final NetworkManager networkManager;
    private LoginState currentLoginState;
    private int connectionTimer;
    private GameProfile loginGameProfile;
    private String serverId;
    private SecretKey secretKey;
    private EntityPlayerMP field_181025_l;
    
    public NetHandlerLoginServer(final MinecraftServer p_i45298_1_, final NetworkManager p_i45298_2_) {
        this.verifyToken = new byte[4];
        this.currentLoginState = LoginState.HELLO;
        this.serverId = "";
        this.server = p_i45298_1_;
        this.networkManager = p_i45298_2_;
        NetHandlerLoginServer.RANDOM.nextBytes(this.verifyToken);
    }
    
    @Override
    public void update() {
        if (this.currentLoginState == LoginState.READY_TO_ACCEPT) {
            this.tryAcceptPlayer();
        }
        else if (this.currentLoginState == LoginState.DELAY_ACCEPT) {
            final EntityPlayerMP entityplayermp = this.server.getConfigurationManager().getPlayerByUUID(this.loginGameProfile.getId());
            if (entityplayermp == null) {
                this.currentLoginState = LoginState.READY_TO_ACCEPT;
                this.server.getConfigurationManager().initializeConnectionToPlayer(this.networkManager, this.field_181025_l);
                this.field_181025_l = null;
            }
        }
        if (this.connectionTimer++ == 600) {
            this.closeConnection("Took too long to log in");
        }
    }
    
    public void closeConnection(final String reason) {
        try {
            NetHandlerLoginServer.logger.info("Disconnecting " + this.getConnectionInfo() + ": " + reason);
            final ChatComponentText chatcomponenttext = new ChatComponentText(reason);
            this.networkManager.sendPacket(new S00PacketDisconnect(chatcomponenttext));
            this.networkManager.closeChannel(chatcomponenttext);
        }
        catch (Exception exception) {
            NetHandlerLoginServer.logger.error("Error whilst disconnecting player", (Throwable)exception);
        }
    }
    
    public void tryAcceptPlayer() {
        if (!this.loginGameProfile.isComplete()) {
            this.loginGameProfile = this.getOfflineProfile(this.loginGameProfile);
        }
        final String s = this.server.getConfigurationManager().allowUserToConnect(this.networkManager.getRemoteAddress(), this.loginGameProfile);
        if (s != null) {
            this.closeConnection(s);
        }
        else {
            this.currentLoginState = LoginState.ACCEPTED;
            if (this.server.getNetworkCompressionTreshold() >= 0 && !this.networkManager.isLocalChannel()) {
                this.networkManager.sendPacket(new S03PacketEnableCompression(this.server.getNetworkCompressionTreshold()), (GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener() {
                    public void operationComplete(final ChannelFuture p_operationComplete_1_) throws Exception {
                        NetHandlerLoginServer.this.networkManager.setCompressionTreshold(NetHandlerLoginServer.this.server.getNetworkCompressionTreshold());
                    }
                }, (GenericFutureListener<? extends Future<? super Void>>[])new GenericFutureListener[0]);
            }
            this.networkManager.sendPacket(new S02PacketLoginSuccess(this.loginGameProfile));
            final EntityPlayerMP entityplayermp = this.server.getConfigurationManager().getPlayerByUUID(this.loginGameProfile.getId());
            if (entityplayermp != null) {
                this.currentLoginState = LoginState.DELAY_ACCEPT;
                this.field_181025_l = this.server.getConfigurationManager().createPlayerForUser(this.loginGameProfile);
            }
            else {
                this.server.getConfigurationManager().initializeConnectionToPlayer(this.networkManager, this.server.getConfigurationManager().createPlayerForUser(this.loginGameProfile));
            }
        }
    }
    
    @Override
    public void onDisconnect(final IChatComponent reason) {
        NetHandlerLoginServer.logger.info(this.getConnectionInfo() + " lost connection: " + reason.getUnformattedText());
    }
    
    public String getConnectionInfo() {
        return (this.loginGameProfile != null) ? (this.loginGameProfile.toString() + " (" + this.networkManager.getRemoteAddress().toString() + ")") : String.valueOf(this.networkManager.getRemoteAddress());
    }
    
    @Override
    public void processLoginStart(final C00PacketLoginStart packetIn) {
        Validate.validState(this.currentLoginState == LoginState.HELLO, "Unexpected hello packet", new Object[0]);
        this.loginGameProfile = packetIn.getProfile();
        if (this.server.isServerInOnlineMode() && !this.networkManager.isLocalChannel()) {
            this.currentLoginState = LoginState.KEY;
            this.networkManager.sendPacket(new S01PacketEncryptionRequest(this.serverId, this.server.getKeyPair().getPublic(), this.verifyToken));
        }
        else {
            this.currentLoginState = LoginState.READY_TO_ACCEPT;
        }
    }
    
    @Override
    public void processEncryptionResponse(final C01PacketEncryptionResponse packetIn) {
        Validate.validState(this.currentLoginState == LoginState.KEY, "Unexpected key packet", new Object[0]);
        final PrivateKey privatekey = this.server.getKeyPair().getPrivate();
        if (!Arrays.equals(this.verifyToken, packetIn.getVerifyToken(privatekey))) {
            throw new IllegalStateException("Invalid nonce!");
        }
        this.secretKey = packetIn.getSecretKey(privatekey);
        this.currentLoginState = LoginState.AUTHENTICATING;
        this.networkManager.enableEncryption(this.secretKey);
        new Thread("User Authenticator #" + NetHandlerLoginServer.AUTHENTICATOR_THREAD_ID.incrementAndGet()) {
            @Override
            public void run() {
                final GameProfile gameprofile = NetHandlerLoginServer.this.loginGameProfile;
                try {
                    final String s = new BigInteger(CryptManager.getServerIdHash(NetHandlerLoginServer.this.serverId, NetHandlerLoginServer.this.server.getKeyPair().getPublic(), NetHandlerLoginServer.this.secretKey)).toString(16);
                    NetHandlerLoginServer.this.loginGameProfile = NetHandlerLoginServer.this.server.getMinecraftSessionService().hasJoinedServer(new GameProfile((UUID)null, gameprofile.getName()), s);
                    if (NetHandlerLoginServer.this.loginGameProfile != null) {
                        NetHandlerLoginServer.logger.info("UUID of player " + NetHandlerLoginServer.this.loginGameProfile.getName() + " is " + NetHandlerLoginServer.this.loginGameProfile.getId());
                        NetHandlerLoginServer.this.currentLoginState = LoginState.READY_TO_ACCEPT;
                    }
                    else if (NetHandlerLoginServer.this.server.isSinglePlayer()) {
                        NetHandlerLoginServer.logger.warn("Failed to verify username but will let them in anyway!");
                        NetHandlerLoginServer.this.loginGameProfile = NetHandlerLoginServer.this.getOfflineProfile(gameprofile);
                        NetHandlerLoginServer.this.currentLoginState = LoginState.READY_TO_ACCEPT;
                    }
                    else {
                        NetHandlerLoginServer.this.closeConnection("Failed to verify username!");
                        NetHandlerLoginServer.logger.error("Username '" + NetHandlerLoginServer.this.loginGameProfile.getName() + "' tried to join with an invalid session");
                    }
                }
                catch (AuthenticationUnavailableException var3) {
                    if (NetHandlerLoginServer.this.server.isSinglePlayer()) {
                        NetHandlerLoginServer.logger.warn("Authentication servers are down but will let them in anyway!");
                        NetHandlerLoginServer.this.loginGameProfile = NetHandlerLoginServer.this.getOfflineProfile(gameprofile);
                        NetHandlerLoginServer.this.currentLoginState = LoginState.READY_TO_ACCEPT;
                    }
                    else {
                        NetHandlerLoginServer.this.closeConnection("Authentication servers are down. Please try again later, sorry!");
                        NetHandlerLoginServer.logger.error("Couldn't verify username because servers are unavailable");
                    }
                }
            }
        }.start();
    }
    
    protected GameProfile getOfflineProfile(final GameProfile original) {
        final UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + original.getName()).getBytes(Charsets.UTF_8));
        return new GameProfile(uuid, original.getName());
    }
    
    static {
        AUTHENTICATOR_THREAD_ID = new AtomicInteger(0);
        logger = LogManager.getLogger();
        RANDOM = new Random();
    }
    
    enum LoginState
    {
        HELLO, 
        KEY, 
        AUTHENTICATING, 
        READY_TO_ACCEPT, 
        DELAY_ACCEPT, 
        ACCEPTED;
    }
}
