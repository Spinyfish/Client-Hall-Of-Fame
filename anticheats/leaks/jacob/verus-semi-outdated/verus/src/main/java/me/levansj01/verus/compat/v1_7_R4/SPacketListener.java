package me.levansj01.verus.compat.v1_7_R4;

import me.levansj01.verus.compat.api.VPacketListener;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayInAbilities;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayInArmAnimation;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayInBlockDig;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayInBlockPlace;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayInClientCommand;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayInCloseWindow;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayInCustomPayload;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayInEntityAction;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayInFlying;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayInHeldItemSlot;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayInKeepAlive;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayInSetCreativeSlot;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayInSteerVehicle;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayInTransaction;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayInUseEntity;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayInWindowClick;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayOutAbilities;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayOutAttachEntity;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayOutEntity;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayOutEntityDestroy;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayOutEntityEffect;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayOutEntityTeleport;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayOutEntityVelocity;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayOutExplosion;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayOutGameStateChange;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayOutKeepAlive;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayOutNamedEntitySpawn;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayOutOpenWindow;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayOutPosition;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayOutRemoveEntityEffect;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayOutRespawn;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayOutSetSlot;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayOutSpawnPosition;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayOutTransaction;
import me.levansj01.verus.compat.v1_7_R4.packets.SPacketPlayOutUpdateAttributes;
import me.levansj01.verus.data.PlayerData;
import net.minecraft.server.v1_7_R4.EnumProtocol;
import net.minecraft.server.v1_7_R4.IChatBaseComponent;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketHandshakingInListener;
import net.minecraft.server.v1_7_R4.PacketHandshakingInSetProtocol;
import net.minecraft.server.v1_7_R4.PacketListener;
import net.minecraft.server.v1_7_R4.PacketLoginInEncryptionBegin;
import net.minecraft.server.v1_7_R4.PacketLoginInListener;
import net.minecraft.server.v1_7_R4.PacketLoginInStart;
import net.minecraft.server.v1_7_R4.PacketLoginOutDisconnect;
import net.minecraft.server.v1_7_R4.PacketLoginOutEncryptionBegin;
import net.minecraft.server.v1_7_R4.PacketLoginOutListener;
import net.minecraft.server.v1_7_R4.PacketLoginOutSuccess;
import net.minecraft.server.v1_7_R4.PacketPlayInAbilities;
import net.minecraft.server.v1_7_R4.PacketPlayInArmAnimation;
import net.minecraft.server.v1_7_R4.PacketPlayInBlockDig;
import net.minecraft.server.v1_7_R4.PacketPlayInBlockPlace;
import net.minecraft.server.v1_7_R4.PacketPlayInChat;
import net.minecraft.server.v1_7_R4.PacketPlayInClientCommand;
import net.minecraft.server.v1_7_R4.PacketPlayInCloseWindow;
import net.minecraft.server.v1_7_R4.PacketPlayInCustomPayload;
import net.minecraft.server.v1_7_R4.PacketPlayInEnchantItem;
import net.minecraft.server.v1_7_R4.PacketPlayInEntityAction;
import net.minecraft.server.v1_7_R4.PacketPlayInFlying;
import net.minecraft.server.v1_7_R4.PacketPlayInHeldItemSlot;
import net.minecraft.server.v1_7_R4.PacketPlayInKeepAlive;
import net.minecraft.server.v1_7_R4.PacketPlayInListener;
import net.minecraft.server.v1_7_R4.PacketPlayInSetCreativeSlot;
import net.minecraft.server.v1_7_R4.PacketPlayInSettings;
import net.minecraft.server.v1_7_R4.PacketPlayInSteerVehicle;
import net.minecraft.server.v1_7_R4.PacketPlayInTabComplete;
import net.minecraft.server.v1_7_R4.PacketPlayInTransaction;
import net.minecraft.server.v1_7_R4.PacketPlayInUpdateSign;
import net.minecraft.server.v1_7_R4.PacketPlayInUseEntity;
import net.minecraft.server.v1_7_R4.PacketPlayInWindowClick;
import net.minecraft.server.v1_7_R4.PacketPlayOutAbilities;
import net.minecraft.server.v1_7_R4.PacketPlayOutAnimation;
import net.minecraft.server.v1_7_R4.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_7_R4.PacketPlayOutBed;
import net.minecraft.server.v1_7_R4.PacketPlayOutBlockAction;
import net.minecraft.server.v1_7_R4.PacketPlayOutBlockBreakAnimation;
import net.minecraft.server.v1_7_R4.PacketPlayOutBlockChange;
import net.minecraft.server.v1_7_R4.PacketPlayOutChat;
import net.minecraft.server.v1_7_R4.PacketPlayOutCloseWindow;
import net.minecraft.server.v1_7_R4.PacketPlayOutCollect;
import net.minecraft.server.v1_7_R4.PacketPlayOutCustomPayload;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntity;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityEffect;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityVelocity;
import net.minecraft.server.v1_7_R4.PacketPlayOutExperience;
import net.minecraft.server.v1_7_R4.PacketPlayOutExplosion;
import net.minecraft.server.v1_7_R4.PacketPlayOutGameStateChange;
import net.minecraft.server.v1_7_R4.PacketPlayOutHeldItemSlot;
import net.minecraft.server.v1_7_R4.PacketPlayOutKeepAlive;
import net.minecraft.server.v1_7_R4.PacketPlayOutKickDisconnect;
import net.minecraft.server.v1_7_R4.PacketPlayOutListener;
import net.minecraft.server.v1_7_R4.PacketPlayOutLogin;
import net.minecraft.server.v1_7_R4.PacketPlayOutMap;
import net.minecraft.server.v1_7_R4.PacketPlayOutMapChunk;
import net.minecraft.server.v1_7_R4.PacketPlayOutMapChunkBulk;
import net.minecraft.server.v1_7_R4.PacketPlayOutMultiBlockChange;
import net.minecraft.server.v1_7_R4.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_7_R4.PacketPlayOutNamedSoundEffect;
import net.minecraft.server.v1_7_R4.PacketPlayOutOpenSignEditor;
import net.minecraft.server.v1_7_R4.PacketPlayOutOpenWindow;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_7_R4.PacketPlayOutPosition;
import net.minecraft.server.v1_7_R4.PacketPlayOutRemoveEntityEffect;
import net.minecraft.server.v1_7_R4.PacketPlayOutRespawn;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardObjective;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_7_R4.PacketPlayOutSetSlot;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntityExperienceOrb;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntityPainting;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntityWeather;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnPosition;
import net.minecraft.server.v1_7_R4.PacketPlayOutStatistic;
import net.minecraft.server.v1_7_R4.PacketPlayOutTabComplete;
import net.minecraft.server.v1_7_R4.PacketPlayOutTileEntityData;
import net.minecraft.server.v1_7_R4.PacketPlayOutTransaction;
import net.minecraft.server.v1_7_R4.PacketPlayOutUpdateAttributes;
import net.minecraft.server.v1_7_R4.PacketPlayOutUpdateHealth;
import net.minecraft.server.v1_7_R4.PacketPlayOutUpdateSign;
import net.minecraft.server.v1_7_R4.PacketPlayOutUpdateTime;
import net.minecraft.server.v1_7_R4.PacketPlayOutWindowData;
import net.minecraft.server.v1_7_R4.PacketPlayOutWindowItems;
import net.minecraft.server.v1_7_R4.PacketPlayOutWorldEvent;
import net.minecraft.server.v1_7_R4.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_7_R4.PacketStatusInListener;
import net.minecraft.server.v1_7_R4.PacketStatusInPing;
import net.minecraft.server.v1_7_R4.PacketStatusInStart;
import net.minecraft.server.v1_7_R4.PacketStatusOutListener;
import net.minecraft.server.v1_7_R4.PacketStatusOutPong;
import net.minecraft.server.v1_7_R4.PacketStatusOutServerInfo;

public class SPacketListener
extends VPacketListener
implements PacketPlayInListener,
PacketPlayOutListener,
PacketStatusInListener,
PacketStatusOutListener,
PacketLoginInListener,
PacketLoginOutListener,
PacketHandshakingInListener {
    public void a(PacketPlayOutKickDisconnect packetPlayOutKickDisconnect) {
    }

    public void a(PacketLoginOutEncryptionBegin packetLoginOutEncryptionBegin) {
    }

    public void a(PacketPlayInTransaction packetPlayInTransaction) {
        this.handleIn(packetPlayInTransaction, new SPacketPlayInTransaction());
    }

    public void a(PacketPlayOutWorldParticles packetPlayOutWorldParticles) {
    }

    public void a(PacketPlayOutMap packetPlayOutMap) {
    }

    public void a(PacketPlayInBlockPlace packetPlayInBlockPlace) {
        this.handleIn(packetPlayInBlockPlace, new SPacketPlayInBlockPlace());
    }

    public void a(PacketPlayOutEntityStatus packetPlayOutEntityStatus) {
    }

    public void a(PacketPlayOutGameStateChange packetPlayOutGameStateChange) {
        this.handleOut(packetPlayOutGameStateChange, new SPacketPlayOutGameStateChange());
    }

    public void a(PacketPlayInCustomPayload packetPlayInCustomPayload) {
        this.handleIn(packetPlayInCustomPayload, new SPacketPlayInCustomPayload());
    }

    public void a(PacketPlayOutCloseWindow packetPlayOutCloseWindow) {
    }

    public void a(PacketPlayOutUpdateHealth packetPlayOutUpdateHealth) {
    }

    public void a(PacketPlayInEnchantItem packetPlayInEnchantItem) {
    }

    public void a(PacketPlayOutHeldItemSlot packetPlayOutHeldItemSlot) {
    }

    public void a(PacketStatusInPing packetStatusInPing) {
    }

    public void a(PacketStatusInStart packetStatusInStart) {
    }

    public void a(PacketLoginOutSuccess packetLoginOutSuccess) {
    }

    public void a(PacketPlayOutBed packetPlayOutBed) {
    }

    public void a(PacketPlayOutLogin packetPlayOutLogin) {
    }

    public void a(PacketPlayOutTransaction packetPlayOutTransaction) {
        this.handleOut(packetPlayOutTransaction, new SPacketPlayOutTransaction());
    }

    public void a(PacketPlayInHeldItemSlot packetPlayInHeldItemSlot) {
        this.handleIn(packetPlayInHeldItemSlot, new SPacketPlayInHeldItemSlot());
    }

    public void a(PacketPlayOutEntityDestroy packetPlayOutEntityDestroy) {
        this.handleOut(packetPlayOutEntityDestroy, new SPacketPlayOutEntityDestroy());
    }

    public void a(PacketPlayOutTabComplete packetPlayOutTabComplete) {
    }

    public void a(PacketPlayOutScoreboardScore packetPlayOutScoreboardScore) {
    }

    public void a(PacketPlayOutAbilities packetPlayOutAbilities) {
        this.handleOut(packetPlayOutAbilities, new SPacketPlayOutAbilities());
    }

    public void a(IChatBaseComponent iChatBaseComponent) {
    }

    public void a(PacketPlayOutEntityEffect packetPlayOutEntityEffect) {
        this.handleOut(packetPlayOutEntityEffect, new SPacketPlayOutEntityEffect());
    }

    public void a(PacketPlayOutScoreboardTeam packetPlayOutScoreboardTeam) {
    }

    public void a(PacketPlayInBlockDig packetPlayInBlockDig) {
        this.handleIn(packetPlayInBlockDig, new SPacketPlayInBlockDig());
    }

    public void a(PacketPlayInFlying packetPlayInFlying) {
        this.handleIn(packetPlayInFlying, new SPacketPlayInFlying());
    }

    public void a(PacketHandshakingInSetProtocol packetHandshakingInSetProtocol) {
    }

    public void a(PacketPlayOutWindowItems packetPlayOutWindowItems) {
    }

    public void a(PacketPlayOutAttachEntity packetPlayOutAttachEntity) {
        this.handleOut(packetPlayOutAttachEntity, new SPacketPlayOutAttachEntity());
    }

    public void a(PacketPlayOutEntityMetadata packetPlayOutEntityMetadata) {
    }

    public void a(PacketPlayInSettings packetPlayInSettings) {
    }

    public void a(PacketPlayOutOpenSignEditor packetPlayOutOpenSignEditor) {
    }

    public void a(PacketPlayInCloseWindow packetPlayInCloseWindow) {
        this.handleIn(packetPlayInCloseWindow, new SPacketPlayInCloseWindow());
    }

    public SPacketListener(PlayerData playerData) {
        super(playerData);
    }

    public void a(PacketPlayOutOpenWindow packetPlayOutOpenWindow) {
        this.handleOut(packetPlayOutOpenWindow, new SPacketPlayOutOpenWindow());
    }

    public void a(PacketPlayOutCustomPayload packetPlayOutCustomPayload) {
    }

    public void a(PacketPlayOutPlayerInfo packetPlayOutPlayerInfo) {
    }

    public void a(PacketPlayOutEntityEquipment packetPlayOutEntityEquipment) {
    }

    public void a(PacketPlayOutTileEntityData packetPlayOutTileEntityData) {
    }

    public void a(PacketPlayInUpdateSign packetPlayInUpdateSign) {
    }

    public void a(PacketPlayInUseEntity packetPlayInUseEntity) {
        this.handleIn(packetPlayInUseEntity, new SPacketPlayInUseEntity());
    }

    public void a(PacketPlayOutMapChunkBulk packetPlayOutMapChunkBulk) {
    }

    public void a(PacketPlayInChat packetPlayInChat) {
    }

    public void a(PacketPlayOutUpdateSign packetPlayOutUpdateSign) {
    }

    public void a(PacketPlayInClientCommand packetPlayInClientCommand) {
        this.handleIn(packetPlayInClientCommand, new SPacketPlayInClientCommand());
    }

    public void a(PacketPlayOutKeepAlive packetPlayOutKeepAlive) {
        this.handleOut(packetPlayOutKeepAlive, new SPacketPlayOutKeepAlive());
    }

    public void a(PacketPlayInSteerVehicle packetPlayInSteerVehicle) {
        this.handleIn(packetPlayInSteerVehicle, new SPacketPlayInSteerVehicle());
    }

    public void a(PacketPlayOutPosition packetPlayOutPosition) {
        this.handleOut(packetPlayOutPosition, new SPacketPlayOutPosition());
    }

    public void a(PacketPlayOutWorldEvent packetPlayOutWorldEvent) {
    }

    public void a(PacketPlayOutMultiBlockChange packetPlayOutMultiBlockChange) {
    }

    public void a(PacketPlayOutChat packetPlayOutChat) {
    }

    public void a(PacketPlayOutSpawnPosition packetPlayOutSpawnPosition) {
        this.handleOut(packetPlayOutSpawnPosition, new SPacketPlayOutSpawnPosition());
    }

    public void a(PacketLoginInStart packetLoginInStart) {
    }

    public void a(PacketLoginInEncryptionBegin packetLoginInEncryptionBegin) {
    }

    public void a(EnumProtocol enumProtocol, EnumProtocol enumProtocol2) {
    }

    public void a(PacketPlayInSetCreativeSlot packetPlayInSetCreativeSlot) {
        this.handleIn(packetPlayInSetCreativeSlot, new SPacketPlayInSetCreativeSlot());
    }

    public void a(PacketPlayOutSpawnEntityWeather packetPlayOutSpawnEntityWeather) {
    }

    public void a(PacketPlayOutExperience packetPlayOutExperience) {
    }

    public void a(PacketPlayOutScoreboardDisplayObjective packetPlayOutScoreboardDisplayObjective) {
    }

    public void a(PacketPlayOutBlockAction packetPlayOutBlockAction) {
    }

    public void a(PacketPlayOutWindowData packetPlayOutWindowData) {
    }

    public void a(PacketPlayOutMapChunk packetPlayOutMapChunk) {
    }

    public void a(PacketPlayInArmAnimation packetPlayInArmAnimation) {
        this.handleIn(packetPlayInArmAnimation, new SPacketPlayInArmAnimation());
    }

    public void a(PacketPlayOutBlockChange packetPlayOutBlockChange) {
    }

    public void a(PacketPlayOutSetSlot packetPlayOutSetSlot) {
        this.handleOut(packetPlayOutSetSlot, new SPacketPlayOutSetSlot());
    }

    public void a(PacketPlayOutNamedEntitySpawn packetPlayOutNamedEntitySpawn) {
        this.handleOut(packetPlayOutNamedEntitySpawn, new SPacketPlayOutNamedEntitySpawn());
    }

    public void a(PacketPlayInKeepAlive packetPlayInKeepAlive) {
        this.handleIn(packetPlayInKeepAlive, new SPacketPlayInKeepAlive());
    }

    public void a(PacketPlayOutStatistic packetPlayOutStatistic) {
    }

    public void a(PacketPlayInAbilities packetPlayInAbilities) {
        this.handleIn(packetPlayInAbilities, new SPacketPlayInAbilities());
    }

    public void a(PacketLoginOutDisconnect packetLoginOutDisconnect) {
    }

    public void a() {
    }

    public void a(PacketPlayOutExplosion packetPlayOutExplosion) {
        this.handleOut(packetPlayOutExplosion, new SPacketPlayOutExplosion());
    }

    public void a(PacketStatusOutServerInfo packetStatusOutServerInfo) {
    }

    public void a(PacketPlayOutEntity packetPlayOutEntity) {
        this.handleOut(packetPlayOutEntity, new SPacketPlayOutEntity());
    }

    public void a(PacketPlayOutAnimation packetPlayOutAnimation) {
    }

    public void a(PacketPlayOutEntityVelocity packetPlayOutEntityVelocity) {
        this.handleOut(packetPlayOutEntityVelocity, new SPacketPlayOutEntityVelocity());
    }

    public void a(PacketPlayOutUpdateTime packetPlayOutUpdateTime) {
    }

    public void a(PacketPlayOutSpawnEntityExperienceOrb packetPlayOutSpawnEntityExperienceOrb) {
    }

    public void a(PacketPlayOutNamedSoundEffect packetPlayOutNamedSoundEffect) {
    }

    public void a(PacketStatusOutPong packetStatusOutPong) {
    }

    @Override
    public void process(Object object) {
        ((Packet)object).handle((PacketListener)this);
    }

    public void a(PacketPlayOutBlockBreakAnimation packetPlayOutBlockBreakAnimation) {
    }

    public void a(PacketPlayOutRemoveEntityEffect packetPlayOutRemoveEntityEffect) {
        this.handleOut(packetPlayOutRemoveEntityEffect, new SPacketPlayOutRemoveEntityEffect());
    }

    public void a(PacketPlayOutCollect packetPlayOutCollect) {
    }

    public void a(PacketPlayOutUpdateAttributes packetPlayOutUpdateAttributes) {
        this.handleOut(packetPlayOutUpdateAttributes, new SPacketPlayOutUpdateAttributes());
    }

    public void a(PacketPlayOutScoreboardObjective packetPlayOutScoreboardObjective) {
    }

    public void a(PacketPlayOutEntityHeadRotation packetPlayOutEntityHeadRotation) {
    }

    public void a(PacketPlayOutSpawnEntity packetPlayOutSpawnEntity) {
    }

    public void a(PacketPlayInEntityAction packetPlayInEntityAction) {
        this.handleIn(packetPlayInEntityAction, new SPacketPlayInEntityAction());
    }

    public void a(PacketPlayInTabComplete packetPlayInTabComplete) {
    }

    public void a(PacketPlayOutEntityTeleport packetPlayOutEntityTeleport) {
        this.handleOut(packetPlayOutEntityTeleport, new SPacketPlayOutEntityTeleport());
    }

    public void a(PacketPlayOutSpawnEntityLiving packetPlayOutSpawnEntityLiving) {
    }

    public void a(PacketPlayOutRespawn packetPlayOutRespawn) {
        this.handleOut(packetPlayOutRespawn, new SPacketPlayOutRespawn());
    }

    public void a(PacketPlayInWindowClick packetPlayInWindowClick) {
        this.handleIn(packetPlayInWindowClick, new SPacketPlayInWindowClick());
    }

    public void a(PacketPlayOutSpawnEntityPainting packetPlayOutSpawnEntityPainting) {
    }
}

