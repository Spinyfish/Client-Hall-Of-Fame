package niggerlib.mc.protocol;

import niggerlib.mc.auth.data.GameProfile;
import niggerlib.mc.auth.exception.request.RequestException;
import niggerlib.mc.auth.service.AuthenticationService;
import niggerlib.mc.protocol.data.SubProtocol;
import niggerlib.mc.protocol.packet.handshake.client.HandshakePacket;
import niggerlib.mc.protocol.packet.ingame.client.*;
import niggerlib.mc.protocol.packet.ingame.client.player.*;
import niggerlib.mc.protocol.packet.ingame.client.window.*;
import niggerlib.mc.protocol.packet.ingame.client.world.*;
import niggerlib.mc.protocol.packet.ingame.server.*;
import niggerlib.mc.protocol.packet.ingame.server.entity.*;
import niggerlib.mc.protocol.packet.ingame.server.entity.player.*;
import niggerlib.mc.protocol.packet.ingame.server.entity.spawn.*;
import niggerlib.mc.protocol.packet.ingame.server.scoreboard.ServerDisplayScoreboardPacket;
import niggerlib.mc.protocol.packet.ingame.server.scoreboard.ServerScoreboardObjectivePacket;
import niggerlib.mc.protocol.packet.ingame.server.scoreboard.ServerTeamPacket;
import niggerlib.mc.protocol.packet.ingame.server.scoreboard.ServerUpdateScorePacket;
import niggerlib.mc.protocol.packet.ingame.server.window.*;
import niggerlib.mc.protocol.packet.ingame.server.world.*;
import niggerlib.mc.protocol.packet.login.client.EncryptionResponsePacket;
import niggerlib.mc.protocol.packet.login.client.LoginStartPacket;
import niggerlib.mc.protocol.packet.login.server.EncryptionRequestPacket;
import niggerlib.mc.protocol.packet.login.server.LoginDisconnectPacket;
import niggerlib.mc.protocol.packet.login.server.LoginSetCompressionPacket;
import niggerlib.mc.protocol.packet.login.server.LoginSuccessPacket;
import niggerlib.mc.protocol.packet.status.client.StatusPingPacket;
import niggerlib.mc.protocol.packet.status.client.StatusQueryPacket;
import niggerlib.mc.protocol.packet.status.server.StatusPongPacket;
import niggerlib.mc.protocol.packet.status.server.StatusResponsePacket;
import niggerlib.packetlib.Client;
import niggerlib.packetlib.Server;
import niggerlib.packetlib.Session;
import niggerlib.packetlib.crypt.AESEncryption;
import niggerlib.packetlib.crypt.PacketEncryption;
import niggerlib.packetlib.packet.DefaultPacketHeader;
import niggerlib.packetlib.packet.PacketHeader;
import niggerlib.packetlib.packet.PacketProtocol;

import java.net.Proxy;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.UUID;

public class MinecraftProtocol extends PacketProtocol {
    private SubProtocol subProtocol = SubProtocol.HANDSHAKE;
    private PacketHeader header = new DefaultPacketHeader();
    private AESEncryption encrypt;

    private GameProfile profile;
    private String accessToken = "";

    @SuppressWarnings("unused")
    private MinecraftProtocol() {
    }

    public MinecraftProtocol(SubProtocol subProtocol) {
        if (subProtocol != SubProtocol.LOGIN && subProtocol != SubProtocol.STATUS) {
            throw new IllegalArgumentException("Only login and status modes are permitted.");
        }

        this.subProtocol = subProtocol;
        if (subProtocol == SubProtocol.LOGIN) {
            this.profile = new GameProfile((UUID) null, "Player");
        }
    }

    public MinecraftProtocol(String username) {
        this(SubProtocol.LOGIN);
        this.profile = new GameProfile((UUID) null, username);
    }

    public MinecraftProtocol(String username, String password) throws RequestException {
        this(username, password, false);
    }

    public MinecraftProtocol(String username, String using, boolean token) throws RequestException {
        this(username, using, token, Proxy.NO_PROXY);
    }

    public MinecraftProtocol(String username, String using, boolean token, Proxy authProxy) throws RequestException {
        this(SubProtocol.LOGIN);
        String clientToken = UUID.randomUUID().toString();
        AuthenticationService auth = new AuthenticationService(clientToken, authProxy);
        auth.setUsername(username);
        if (token) {
            auth.setAccessToken(using);
        } else {
            auth.setPassword(using);
        }

        auth.login();
        this.profile = auth.getSelectedProfile();
        this.accessToken = auth.getAccessToken();
    }

    public MinecraftProtocol(GameProfile profile, String accessToken) {
        this(SubProtocol.LOGIN);
        this.profile = profile;
        this.accessToken = accessToken;
    }

    public GameProfile getProfile() {
        return this.profile;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    @Override
    public String getSRVRecordPrefix() {
        return "_minecraft";
    }

    @Override
    public PacketHeader getPacketHeader() {
        return this.header;
    }

    @Override
    public PacketEncryption getEncryption() {
        return this.encrypt;
    }

    @Override
    public void newClientSession(Client client, Session session) {
        if (this.profile != null) {
            session.setFlag(MinecraftConstants.PROFILE_KEY, this.profile);
            session.setFlag(MinecraftConstants.ACCESS_TOKEN_KEY, this.accessToken);
        }

        this.setSubProtocol(this.subProtocol, true, session);
        session.addListener(new ClientListener());
    }

    @Override
    public void newServerSession(Server server, Session session) {
        this.setSubProtocol(SubProtocol.HANDSHAKE, false, session);
        session.addListener(new ServerListener());
    }

    protected void enableEncryption(Key key) {
        try {
            this.encrypt = new AESEncryption(key);
        } catch (GeneralSecurityException e) {
            throw new Error("Failed to enable protocol encryption.", e);
        }
    }

    public SubProtocol getSubProtocol() {
        return this.subProtocol;
    }

    protected void setSubProtocol(SubProtocol subProtocol, boolean client, Session session) {
        this.clearPackets();
        switch (subProtocol) {
            case HANDSHAKE:
                if (client) {
                    this.initClientHandshake(session);
                } else {
                    this.initServerHandshake(session);
                }

                break;
            case LOGIN:
                if (client) {
                    this.initClientLogin(session);
                } else {
                    this.initServerLogin(session);
                }

                break;
            case GAME:
                if (client) {
                    this.initClientGame(session);
                } else {
                    this.initServerGame(session);
                }

                break;
            case STATUS:
                if (client) {
                    this.initClientStatus(session);
                } else {
                    this.initServerStatus(session);
                }

                break;
        }

        this.subProtocol = subProtocol;
    }

    private void initClientHandshake(Session session) {
        this.registerOutgoing(0, HandshakePacket.class);
    }

    private void initServerHandshake(Session session) {
        this.registerIncoming(0, HandshakePacket.class);
    }

    private void initClientLogin(Session session) {
        this.registerIncoming(0x00, LoginDisconnectPacket.class);
        this.registerIncoming(0x01, EncryptionRequestPacket.class);
        this.registerIncoming(0x02, LoginSuccessPacket.class);
        this.registerIncoming(0x03, LoginSetCompressionPacket.class);

        this.registerOutgoing(0x00, LoginStartPacket.class);
        this.registerOutgoing(0x01, EncryptionResponsePacket.class);
    }

    private void initServerLogin(Session session) {
        this.registerIncoming(0x00, LoginStartPacket.class);
        this.registerIncoming(0x01, EncryptionResponsePacket.class);

        this.registerOutgoing(0x00, LoginDisconnectPacket.class);
        this.registerOutgoing(0x01, EncryptionRequestPacket.class);
        this.registerOutgoing(0x02, LoginSuccessPacket.class);
        this.registerOutgoing(0x03, LoginSetCompressionPacket.class);
    }

    private void initClientGame(Session session) {
        this.registerIncoming(0x00, ServerSpawnObjectPacket.class);
        this.registerIncoming(0x01, ServerSpawnExpOrbPacket.class);
        this.registerIncoming(0x02, ServerSpawnGlobalEntityPacket.class);
        this.registerIncoming(0x03, ServerSpawnMobPacket.class);
        this.registerIncoming(0x04, ServerSpawnPaintingPacket.class);
        this.registerIncoming(0x05, ServerSpawnPlayerPacket.class);
        this.registerIncoming(0x06, ServerEntityAnimationPacket.class);
        this.registerIncoming(0x07, ServerStatisticsPacket.class);
        this.registerIncoming(0x08, ServerBlockBreakAnimPacket.class);
        this.registerIncoming(0x09, ServerUpdateTileEntityPacket.class);
        this.registerIncoming(0x0A, ServerBlockValuePacket.class);
        this.registerIncoming(0x0B, ServerBlockChangePacket.class);
        this.registerIncoming(0x0C, ServerBossBarPacket.class);
        this.registerIncoming(0x0D, ServerDifficultyPacket.class);
        this.registerIncoming(0x0E, ServerTabCompletePacket.class);
        this.registerIncoming(0x0F, ServerChatPacket.class);
        this.registerIncoming(0x10, ServerMultiBlockChangePacket.class);
        this.registerIncoming(0x11, ServerConfirmTransactionPacket.class);
        this.registerIncoming(0x12, ServerCloseWindowPacket.class);
        this.registerIncoming(0x13, ServerOpenWindowPacket.class);
        this.registerIncoming(0x14, ServerWindowItemsPacket.class);
        this.registerIncoming(0x15, ServerWindowPropertyPacket.class);
        this.registerIncoming(0x16, ServerSetSlotPacket.class);
        this.registerIncoming(0x17, ServerSetCooldownPacket.class);
        this.registerIncoming(0x18, ServerPluginMessagePacket.class);
        this.registerIncoming(0x19, ServerPlaySoundPacket.class);
        this.registerIncoming(0x1A, ServerDisconnectPacket.class);
        this.registerIncoming(0x1B, ServerEntityStatusPacket.class);
        this.registerIncoming(0x1C, ServerExplosionPacket.class);
        this.registerIncoming(0x1D, ServerUnloadChunkPacket.class);
        this.registerIncoming(0x1E, ServerNotifyClientPacket.class);
        this.registerIncoming(0x1F, ServerKeepAlivePacket.class);
        this.registerIncoming(0x20, ServerChunkDataPacket.class);
        this.registerIncoming(0x21, ServerPlayEffectPacket.class);
        this.registerIncoming(0x22, ServerSpawnParticlePacket.class);
        this.registerIncoming(0x23, ServerJoinGamePacket.class);
        this.registerIncoming(0x24, ServerMapDataPacket.class);
        this.registerIncoming(0x25, ServerEntityMovementPacket.class);
        this.registerIncoming(0x26, ServerEntityPositionPacket.class);
        this.registerIncoming(0x27, ServerEntityPositionRotationPacket.class);
        this.registerIncoming(0x28, ServerEntityRotationPacket.class);
        this.registerIncoming(0x29, ServerVehicleMovePacket.class);
        this.registerIncoming(0x2A, ServerOpenTileEntityEditorPacket.class);
        this.registerIncoming(0x2B, ServerPreparedCraftingGridPacket.class);
        this.registerIncoming(0x2C, ServerPlayerAbilitiesPacket.class);
        this.registerIncoming(0x2D, ServerCombatPacket.class);
        this.registerIncoming(0x2E, ServerPlayerListEntryPacket.class);
        this.registerIncoming(0x2F, ServerPlayerPositionRotationPacket.class);
        this.registerIncoming(0x30, ServerPlayerUseBedPacket.class);
        this.registerIncoming(0x31, ServerUnlockRecipesPacket.class);
        this.registerIncoming(0x32, ServerEntityDestroyPacket.class);
        this.registerIncoming(0x33, ServerEntityRemoveEffectPacket.class);
        this.registerIncoming(0x34, ServerResourcePackSendPacket.class);
        this.registerIncoming(0x35, ServerRespawnPacket.class);
        this.registerIncoming(0x36, ServerEntityHeadLookPacket.class);
        this.registerIncoming(0x37, ServerAdvancementTabPacket.class);
        this.registerIncoming(0x38, ServerWorldBorderPacket.class);
        this.registerIncoming(0x39, ServerSwitchCameraPacket.class);
        this.registerIncoming(0x3A, ServerPlayerChangeHeldItemPacket.class);
        this.registerIncoming(0x3B, ServerDisplayScoreboardPacket.class);
        this.registerIncoming(0x3C, ServerEntityMetadataPacket.class);
        this.registerIncoming(0x3D, ServerEntityAttachPacket.class);
        this.registerIncoming(0x3E, ServerEntityVelocityPacket.class);
        this.registerIncoming(0x3F, ServerEntityEquipmentPacket.class);
        this.registerIncoming(0x40, ServerPlayerSetExperiencePacket.class);
        this.registerIncoming(0x41, ServerPlayerHealthPacket.class);
        this.registerIncoming(0x42, ServerScoreboardObjectivePacket.class);
        this.registerIncoming(0x43, ServerEntitySetPassengersPacket.class);
        this.registerIncoming(0x44, ServerTeamPacket.class);
        this.registerIncoming(0x45, ServerUpdateScorePacket.class);
        this.registerIncoming(0x46, ServerSpawnPositionPacket.class);
        this.registerIncoming(0x47, ServerUpdateTimePacket.class);
        this.registerIncoming(0x48, ServerTitlePacket.class);
        this.registerIncoming(0x49, ServerPlayBuiltinSoundPacket.class);
        this.registerIncoming(0x4A, ServerPlayerListDataPacket.class);
        this.registerIncoming(0x4B, ServerEntityCollectItemPacket.class);
        this.registerIncoming(0x4C, ServerEntityTeleportPacket.class);
        this.registerIncoming(0x4D, ServerAdvancementsPacket.class);
        this.registerIncoming(0x4E, ServerEntityPropertiesPacket.class);
        this.registerIncoming(0x4F, ServerEntityEffectPacket.class);

        this.registerOutgoing(0x00, ClientTeleportConfirmPacket.class);
        this.registerOutgoing(0x01, ClientTabCompletePacket.class);
        this.registerOutgoing(0x02, ClientChatPacket.class);
        this.registerOutgoing(0x03, ClientRequestPacket.class);
        this.registerOutgoing(0x04, ClientSettingsPacket.class);
        this.registerOutgoing(0x05, ClientConfirmTransactionPacket.class);
        this.registerOutgoing(0x06, ClientEnchantItemPacket.class);
        this.registerOutgoing(0x07, ClientWindowActionPacket.class);
        this.registerOutgoing(0x08, ClientCloseWindowPacket.class);
        this.registerOutgoing(0x09, ClientPluginMessagePacket.class);
        this.registerOutgoing(0x0A, ClientPlayerInteractEntityPacket.class);
        this.registerOutgoing(0x0B, ClientKeepAlivePacket.class);
        this.registerOutgoing(0x0C, ClientPlayerMovementPacket.class);
        this.registerOutgoing(0x0D, ClientPlayerPositionPacket.class);
        this.registerOutgoing(0x0E, ClientPlayerPositionRotationPacket.class);
        this.registerOutgoing(0x0F, ClientPlayerRotationPacket.class);
        this.registerOutgoing(0x10, ClientVehicleMovePacket.class);
        this.registerOutgoing(0x11, ClientSteerBoatPacket.class);
        this.registerOutgoing(0x12, ClientPrepareCraftingGridPacket.class);
        this.registerOutgoing(0x13, ClientPlayerAbilitiesPacket.class);
        this.registerOutgoing(0x14, ClientPlayerActionPacket.class);
        this.registerOutgoing(0x15, ClientPlayerStatePacket.class);
        this.registerOutgoing(0x16, ClientSteerVehiclePacket.class);
        this.registerOutgoing(0x17, ClientCraftingBookDataPacket.class);
        this.registerOutgoing(0x18, ClientResourcePackStatusPacket.class);
        this.registerOutgoing(0x19, ClientAdvancementTabPacket.class);
        this.registerOutgoing(0x1A, ClientPlayerChangeHeldItemPacket.class);
        this.registerOutgoing(0x1B, ClientCreativeInventoryActionPacket.class);
        this.registerOutgoing(0x1C, ClientUpdateSignPacket.class);
        this.registerOutgoing(0x1D, ClientPlayerSwingArmPacket.class);
        this.registerOutgoing(0x1E, ClientSpectatePacket.class);
        this.registerOutgoing(0x1F, ClientPlayerPlaceBlockPacket.class);
        this.registerOutgoing(0x20, ClientPlayerUseItemPacket.class);
    }

    private void initServerGame(Session session) {
        this.registerIncoming(0x00, ClientTeleportConfirmPacket.class);
        this.registerIncoming(0x01, ClientTabCompletePacket.class);
        this.registerIncoming(0x02, ClientChatPacket.class);
        this.registerIncoming(0x03, ClientRequestPacket.class);
        this.registerIncoming(0x04, ClientSettingsPacket.class);
        this.registerIncoming(0x05, ClientConfirmTransactionPacket.class);
        this.registerIncoming(0x06, ClientEnchantItemPacket.class);
        this.registerIncoming(0x07, ClientWindowActionPacket.class);
        this.registerIncoming(0x08, ClientCloseWindowPacket.class);
        this.registerIncoming(0x09, ClientPluginMessagePacket.class);
        this.registerIncoming(0x0A, ClientPlayerInteractEntityPacket.class);
        this.registerIncoming(0x0B, ClientKeepAlivePacket.class);
        this.registerIncoming(0x0C, ClientPlayerMovementPacket.class);
        this.registerIncoming(0x0D, ClientPlayerPositionPacket.class);
        this.registerIncoming(0x0E, ClientPlayerPositionRotationPacket.class);
        this.registerIncoming(0x0F, ClientPlayerRotationPacket.class);
        this.registerIncoming(0x10, ClientVehicleMovePacket.class);
        this.registerIncoming(0x11, ClientSteerBoatPacket.class);
        this.registerIncoming(0x12, ClientPrepareCraftingGridPacket.class);
        this.registerIncoming(0x13, ClientPlayerAbilitiesPacket.class);
        this.registerIncoming(0x14, ClientPlayerActionPacket.class);
        this.registerIncoming(0x15, ClientPlayerStatePacket.class);
        this.registerIncoming(0x16, ClientSteerVehiclePacket.class);
        this.registerIncoming(0x17, ClientCraftingBookDataPacket.class);
        this.registerIncoming(0x18, ClientResourcePackStatusPacket.class);
        this.registerIncoming(0x19, ClientAdvancementTabPacket.class);
        this.registerIncoming(0x1A, ClientPlayerChangeHeldItemPacket.class);
        this.registerIncoming(0x1B, ClientCreativeInventoryActionPacket.class);
        this.registerIncoming(0x1C, ClientUpdateSignPacket.class);
        this.registerIncoming(0x1D, ClientPlayerSwingArmPacket.class);
        this.registerIncoming(0x1E, ClientSpectatePacket.class);
        this.registerIncoming(0x1F, ClientPlayerPlaceBlockPacket.class);
        this.registerIncoming(0x20, ClientPlayerUseItemPacket.class);

        this.registerOutgoing(0x00, ServerSpawnObjectPacket.class);
        this.registerOutgoing(0x01, ServerSpawnExpOrbPacket.class);
        this.registerOutgoing(0x02, ServerSpawnGlobalEntityPacket.class);
        this.registerOutgoing(0x03, ServerSpawnMobPacket.class);
        this.registerOutgoing(0x04, ServerSpawnPaintingPacket.class);
        this.registerOutgoing(0x05, ServerSpawnPlayerPacket.class);
        this.registerOutgoing(0x06, ServerEntityAnimationPacket.class);
        this.registerOutgoing(0x07, ServerStatisticsPacket.class);
        this.registerOutgoing(0x08, ServerBlockBreakAnimPacket.class);
        this.registerOutgoing(0x09, ServerUpdateTileEntityPacket.class);
        this.registerOutgoing(0x0A, ServerBlockValuePacket.class);
        this.registerOutgoing(0x0B, ServerBlockChangePacket.class);
        this.registerOutgoing(0x0C, ServerBossBarPacket.class);
        this.registerOutgoing(0x0D, ServerDifficultyPacket.class);
        this.registerOutgoing(0x0E, ServerTabCompletePacket.class);
        this.registerOutgoing(0x0F, ServerChatPacket.class);
        this.registerOutgoing(0x10, ServerMultiBlockChangePacket.class);
        this.registerOutgoing(0x11, ServerConfirmTransactionPacket.class);
        this.registerOutgoing(0x12, ServerCloseWindowPacket.class);
        this.registerOutgoing(0x13, ServerOpenWindowPacket.class);
        this.registerOutgoing(0x14, ServerWindowItemsPacket.class);
        this.registerOutgoing(0x15, ServerWindowPropertyPacket.class);
        this.registerOutgoing(0x16, ServerSetSlotPacket.class);
        this.registerOutgoing(0x17, ServerSetCooldownPacket.class);
        this.registerOutgoing(0x18, ServerPluginMessagePacket.class);
        this.registerOutgoing(0x19, ServerPlaySoundPacket.class);
        this.registerOutgoing(0x1A, ServerDisconnectPacket.class);
        this.registerOutgoing(0x1B, ServerEntityStatusPacket.class);
        this.registerOutgoing(0x1C, ServerExplosionPacket.class);
        this.registerOutgoing(0x1D, ServerUnloadChunkPacket.class);
        this.registerOutgoing(0x1E, ServerNotifyClientPacket.class);
        this.registerOutgoing(0x1F, ServerKeepAlivePacket.class);
        this.registerOutgoing(0x20, ServerChunkDataPacket.class);
        this.registerOutgoing(0x21, ServerPlayEffectPacket.class);
        this.registerOutgoing(0x22, ServerSpawnParticlePacket.class);
        this.registerOutgoing(0x23, ServerJoinGamePacket.class);
        this.registerOutgoing(0x24, ServerMapDataPacket.class);
        this.registerOutgoing(0x25, ServerEntityMovementPacket.class);
        this.registerOutgoing(0x26, ServerEntityPositionPacket.class);
        this.registerOutgoing(0x27, ServerEntityPositionRotationPacket.class);
        this.registerOutgoing(0x28, ServerEntityRotationPacket.class);
        this.registerOutgoing(0x29, ServerVehicleMovePacket.class);
        this.registerOutgoing(0x2A, ServerOpenTileEntityEditorPacket.class);
        this.registerOutgoing(0x2B, ServerPreparedCraftingGridPacket.class);
        this.registerOutgoing(0x2C, ServerPlayerAbilitiesPacket.class);
        this.registerOutgoing(0x2D, ServerCombatPacket.class);
        this.registerOutgoing(0x2E, ServerPlayerListEntryPacket.class);
        this.registerOutgoing(0x2F, ServerPlayerPositionRotationPacket.class);
        this.registerOutgoing(0x30, ServerPlayerUseBedPacket.class);
        this.registerOutgoing(0x31, ServerUnlockRecipesPacket.class);
        this.registerOutgoing(0x32, ServerEntityDestroyPacket.class);
        this.registerOutgoing(0x33, ServerEntityRemoveEffectPacket.class);
        this.registerOutgoing(0x34, ServerResourcePackSendPacket.class);
        this.registerOutgoing(0x35, ServerRespawnPacket.class);
        this.registerOutgoing(0x36, ServerEntityHeadLookPacket.class);
        this.registerOutgoing(0x37, ServerAdvancementTabPacket.class);
        this.registerOutgoing(0x38, ServerWorldBorderPacket.class);
        this.registerOutgoing(0x39, ServerSwitchCameraPacket.class);
        this.registerOutgoing(0x3A, ServerPlayerChangeHeldItemPacket.class);
        this.registerOutgoing(0x3B, ServerDisplayScoreboardPacket.class);
        this.registerOutgoing(0x3C, ServerEntityMetadataPacket.class);
        this.registerOutgoing(0x3D, ServerEntityAttachPacket.class);
        this.registerOutgoing(0x3E, ServerEntityVelocityPacket.class);
        this.registerOutgoing(0x3F, ServerEntityEquipmentPacket.class);
        this.registerOutgoing(0x40, ServerPlayerSetExperiencePacket.class);
        this.registerOutgoing(0x41, ServerPlayerHealthPacket.class);
        this.registerOutgoing(0x42, ServerScoreboardObjectivePacket.class);
        this.registerOutgoing(0x43, ServerEntitySetPassengersPacket.class);
        this.registerOutgoing(0x44, ServerTeamPacket.class);
        this.registerOutgoing(0x45, ServerUpdateScorePacket.class);
        this.registerOutgoing(0x46, ServerSpawnPositionPacket.class);
        this.registerOutgoing(0x47, ServerUpdateTimePacket.class);
        this.registerOutgoing(0x48, ServerTitlePacket.class);
        this.registerOutgoing(0x49, ServerPlayBuiltinSoundPacket.class);
        this.registerOutgoing(0x4A, ServerPlayerListDataPacket.class);
        this.registerOutgoing(0x4B, ServerEntityCollectItemPacket.class);
        this.registerOutgoing(0x4C, ServerEntityTeleportPacket.class);
        this.registerOutgoing(0x4D, ServerAdvancementsPacket.class);
        this.registerOutgoing(0x4E, ServerEntityPropertiesPacket.class);
        this.registerOutgoing(0x4F, ServerEntityEffectPacket.class);
    }

    private void initClientStatus(Session session) {
        this.registerIncoming(0x00, StatusResponsePacket.class);
        this.registerIncoming(0x01, StatusPongPacket.class);

        this.registerOutgoing(0x00, StatusQueryPacket.class);
        this.registerOutgoing(0x01, StatusPingPacket.class);
    }

    private void initServerStatus(Session session) {
        this.registerIncoming(0x00, StatusQueryPacket.class);
        this.registerIncoming(0x01, StatusPingPacket.class);

        this.registerOutgoing(0x00, StatusResponsePacket.class);
        this.registerOutgoing(0x01, StatusPongPacket.class);
    }
}
