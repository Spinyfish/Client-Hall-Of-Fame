package net.minecraft.client.network;

import net.minecraft.network.login.*;
import net.minecraft.client.*;
import com.mojang.authlib.*;
import java.math.*;
import net.minecraft.util.*;
import com.mojang.authlib.exceptions.*;
import net.minecraft.network.login.client.*;
import io.netty.util.concurrent.*;
import javax.crypto.*;
import java.security.*;
import com.mojang.authlib.minecraft.*;
import net.minecraft.network.*;
import net.minecraft.client.gui.*;
import net.minecraft.network.login.server.*;
import org.apache.logging.log4j.*;

public class NetHandlerLoginClient implements INetHandlerLoginClient
{
    private static final Logger logger;
    private final Minecraft mc;
    private final GuiScreen previousGuiScreen;
    private final NetworkManager networkManager;
    private GameProfile gameProfile;
    
    public NetHandlerLoginClient(final NetworkManager p_i45059_1_, final Minecraft mcIn, final GuiScreen p_i45059_3_) {
        this.networkManager = p_i45059_1_;
        this.mc = mcIn;
        this.previousGuiScreen = p_i45059_3_;
    }
    
    @Override
    public void handleEncryptionRequest(final S01PacketEncryptionRequest packetIn) {
        final SecretKey secretkey = CryptManager.createNewSharedKey();
        final String s = packetIn.getServerId();
        final PublicKey publickey = packetIn.getPublicKey();
        final String s2 = new BigInteger(CryptManager.getServerIdHash(s, publickey, secretkey)).toString(16);
        if (this.mc.getCurrentServerData() != null && this.mc.getCurrentServerData().func_181041_d()) {
            try {
                this.getSessionService().joinServer(this.mc.getSession().getProfile(), this.mc.getSession().getToken(), s2);
            }
            catch (AuthenticationException var10) {
                NetHandlerLoginClient.logger.warn("Couldn't connect to auth servers but will continue to join LAN");
            }
        }
        else {
            try {
                this.getSessionService().joinServer(this.mc.getSession().getProfile(), this.mc.getSession().getToken(), s2);
            }
            catch (AuthenticationUnavailableException var11) {
                this.networkManager.closeChannel(new ChatComponentTranslation("disconnect.loginFailedInfo", new Object[] { new ChatComponentTranslation("disconnect.loginFailedInfo.serversUnavailable", new Object[0]) }));
                return;
            }
            catch (InvalidCredentialsException var12) {
                this.networkManager.closeChannel(new ChatComponentTranslation("disconnect.loginFailedInfo", new Object[] { new ChatComponentTranslation("disconnect.loginFailedInfo.invalidSession", new Object[0]) }));
                return;
            }
            catch (AuthenticationException authenticationexception) {
                this.networkManager.closeChannel(new ChatComponentTranslation("disconnect.loginFailedInfo", new Object[] { authenticationexception.getMessage() }));
                return;
            }
        }
        this.networkManager.sendPacket(new C01PacketEncryptionResponse(secretkey, publickey, packetIn.getVerifyToken()), (GenericFutureListener<? extends Future<? super Void>>)new GenericFutureListener<Future<? super Void>>() {
            public void operationComplete(final Future<? super Void> p_operationComplete_1_) throws Exception {
                NetHandlerLoginClient.this.networkManager.enableEncryption(secretkey);
            }
        }, (GenericFutureListener<? extends Future<? super Void>>[])new GenericFutureListener[0]);
    }
    
    private MinecraftSessionService getSessionService() {
        return this.mc.getSessionService();
    }
    
    @Override
    public void handleLoginSuccess(final S02PacketLoginSuccess packetIn) {
        this.gameProfile = packetIn.getProfile();
        this.networkManager.setConnectionState(EnumConnectionState.PLAY);
        this.networkManager.setNetHandler(new NetHandlerPlayClient(this.mc, this.previousGuiScreen, this.networkManager, this.gameProfile));
    }
    
    @Override
    public void onDisconnect(final IChatComponent reason) {
        this.mc.displayGuiScreen(new GuiDisconnected(this.previousGuiScreen, "connect.failed", reason));
    }
    
    @Override
    public void handleDisconnect(final S00PacketDisconnect packetIn) {
        this.networkManager.closeChannel(packetIn.func_149603_c());
    }
    
    @Override
    public void handleEnableCompression(final S03PacketEnableCompression packetIn) {
        if (!this.networkManager.isLocalChannel()) {
            this.networkManager.setCompressionTreshold(packetIn.getCompressionTreshold());
        }
    }
    
    static {
        logger = LogManager.getLogger();
    }
}
