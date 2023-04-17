package me.levansj01.verus.compat.v1_8_R3;

import me.levansj01.verus.compat.api.*;
import java.lang.reflect.*;
import me.levansj01.verus.compat.*;
import me.levansj01.verus.data.*;
import me.levansj01.verus.util.java.*;
import net.minecraft.server.v1_8_R3.*;
import me.levansj01.verus.compat.v1_8_R3.packets.*;

public class SPacketListener extends VPacketListener implements PacketListenerPlayIn, PacketListenerPlayOut, PacketStatusInListener, PacketStatusOutListener, PacketLoginInListener, PacketLoginOutListener, PacketHandshakingInListener
{
    private static final Method method;
    
    public void process(final Object o) {
        this.processTyped((Packet)o);
    }
    
    public void a(final PacketPlayInSetCreativeSlot packetPlayInSetCreativeSlot) {
        this.handleIn(packetPlayInSetCreativeSlot, new SPacketPlayInSetCreativeSlot());
    }
    
    public void a(final PacketPlayOutWindowData packetPlayOutWindowData) {
    }
    
    public void a(final PacketLoginOutSetCompression packetLoginOutSetCompression) {
    }
    
    public void a(final PacketPlayOutServerDifficulty packetPlayOutServerDifficulty) {
    }
    
    public void a(final PacketPlayInEnchantItem packetPlayInEnchantItem) {
    }
    
    public void a(final PacketLoginOutSuccess packetLoginOutSuccess) {
    }
    
    public void a(final PacketPlayOutHeldItemSlot packetPlayOutHeldItemSlot) {
    }
    
    public void a(final PacketPlayOutKeepAlive packetPlayOutKeepAlive) {
        this.handleOut(packetPlayOutKeepAlive, new SPacketPlayOutKeepAlive());
    }
    
    public SPacketListener(final PlayerData playerData) {
        super(playerData);
    }
    
    public void a(final PacketPlayOutCollect packetPlayOutCollect) {
    }
    
    public void a(final PacketPlayOutWindowItems packetPlayOutWindowItems) {
    }
    
    public void a(final PacketPlayOutCombatEvent packetPlayOutCombatEvent) {
    }
    
    public void a(final PacketPlayOutSpawnEntityLiving packetPlayOutSpawnEntityLiving) {
        this.handleOut(packetPlayOutSpawnEntityLiving, new SPacketPlayOutSpawnEntityLiving());
    }
    
    public void a(final PacketPlayOutBlockChange packetPlayOutBlockChange) {
        this.handleOut(packetPlayOutBlockChange, new SPacketPlayOutBlockChange());
    }
    
    public void a(final PacketPlayOutAttachEntity packetPlayOutAttachEntity) {
        this.handleOut(packetPlayOutAttachEntity, new SPacketPlayOutAttachEntity());
    }
    
    public void a(final PacketPlayOutEntityDestroy packetPlayOutEntityDestroy) {
        this.handleOut(packetPlayOutEntityDestroy, new SPacketPlayOutEntityDestroy());
    }
    
    public void a(final PacketPlayInResourcePackStatus packetPlayInResourcePackStatus) {
    }
    
    public void a(final PacketPlayOutScoreboardObjective packetPlayOutScoreboardObjective) {
    }
    
    public void a(final PacketPlayOutAnimation packetPlayOutAnimation) {
    }
    
    public void a(final PacketPlayOutExperience packetPlayOutExperience) {
    }
    
    public void a(final PacketLoginInEncryptionBegin packetLoginInEncryptionBegin) {
    }
    
    public void a(final PacketPlayInTransaction packetPlayInTransaction) {
        this.handleIn(packetPlayInTransaction, new SPacketPlayInTransaction());
    }
    
    public void a(final PacketPlayOutEntityTeleport packetPlayOutEntityTeleport) {
        this.handleOut(packetPlayOutEntityTeleport, new SPacketPlayOutEntityTeleport());
    }
    
    public void a(final PacketPlayInUseEntity packetPlayInUseEntity) {
        this.handleIn(packetPlayInUseEntity, new SPacketPlayInUseEntity());
    }
    
    public void a(final PacketPlayOutPlayerListHeaderFooter packetPlayOutPlayerListHeaderFooter) {
    }
    
    public void a(final PacketPlayOutSpawnEntityExperienceOrb packetPlayOutSpawnEntityExperienceOrb) {
    }
    
    public void a(final PacketPlayOutMapChunkBulk packetPlayOutMapChunkBulk) {
        this.handleOut(packetPlayOutMapChunkBulk, new SPacketPlayOutMapChunkBulk());
    }
    
    public void a(final PacketStatusOutServerInfo packetStatusOutServerInfo) {
    }
    
    public void a(final PacketPlayOutUpdateTime packetPlayOutUpdateTime) {
    }
    
    public void a(final PacketStatusInPing packetStatusInPing) {
    }
    
    public void a(final PacketPlayInSteerVehicle packetPlayInSteerVehicle) {
        this.handleIn(packetPlayInSteerVehicle, new SPacketPlayInSteerVehicle());
    }
    
    public void a(final PacketPlayOutRespawn packetPlayOutRespawn) {
        this.handleOut(packetPlayOutRespawn, new SPacketPlayOutRespawn());
    }
    
    public void a(final PacketPlayOutTitle packetPlayOutTitle) {
    }
    
    public void a(final PacketPlayInChat packetPlayInChat) {
    }
    
    public void a(final PacketStatusOutPong packetStatusOutPong) {
    }
    
    public void a(final PacketStatusInStart packetStatusInStart) {
    }
    
    public void a(final PacketHandshakingInSetProtocol packetHandshakingInSetProtocol) {
    }
    
    public void a(final PacketPlayOutCamera packetPlayOutCamera) {
    }
    
    public void a(final PacketPlayOutOpenSignEditor packetPlayOutOpenSignEditor) {
    }
    
    public void a(final PacketPlayOutMapChunk packetPlayOutMapChunk) {
        this.handleOut(packetPlayOutMapChunk, new SPacketPlayOutMapChunk());
    }
    
    public void a(final PacketPlayOutCloseWindow packetPlayOutCloseWindow) {
    }
    
    public void a(final PacketPlayOutSpawnPosition packetPlayOutSpawnPosition) {
        this.handleOut(packetPlayOutSpawnPosition, new SPacketPlayOutSpawnPosition());
    }
    
    public void a(final PacketPlayOutEntityMetadata packetPlayOutEntityMetadata) {
    }
    
    public void a(final PacketPlayOutStatistic packetPlayOutStatistic) {
    }
    
    public void a(final PacketPlayOutEntityStatus packetPlayOutEntityStatus) {
    }
    
    public void a(final PacketPlayInCloseWindow packetPlayInCloseWindow) {
        this.handleIn(packetPlayInCloseWindow, new SPacketPlayInCloseWindow());
    }
    
    public void a(final PacketPlayOutScoreboardDisplayObjective packetPlayOutScoreboardDisplayObjective) {
    }
    
    public void a(final PacketPlayOutWorldBorder packetPlayOutWorldBorder) {
    }
    
    public void a(final PacketPlayOutNamedEntitySpawn packetPlayOutNamedEntitySpawn) {
        this.handleOut(packetPlayOutNamedEntitySpawn, new SPacketPlayOutNamedEntitySpawn());
    }
    
    public void a(final PacketPlayOutEntity packetPlayOutEntity) {
        this.handleOut(packetPlayOutEntity, new SPacketPlayOutEntity());
    }
    
    public void a(final PacketPlayOutBed packetPlayOutBed) {
    }
    
    public void a(final PacketPlayOutEntityHeadRotation packetPlayOutEntityHeadRotation) {
    }
    
    public void a(final PacketPlayOutMap packetPlayOutMap) {
    }
    
    public void a(final PacketPlayOutSetSlot packetPlayOutSetSlot) {
        this.handleOut(packetPlayOutSetSlot, new SPacketPlayOutSetSlot());
    }
    
    public void a(final PacketPlayOutScoreboardScore packetPlayOutScoreboardScore) {
    }
    
    public void a(final PacketPlayOutResourcePackSend packetPlayOutResourcePackSend) {
    }
    
    public void a(final PacketLoginInStart packetLoginInStart) {
    }
    
    public void a(final PacketPlayInAbilities packetPlayInAbilities) {
        this.handleIn(packetPlayInAbilities, new SPacketPlayInAbilities());
    }
    
    public void a(final PacketPlayOutUpdateAttributes packetPlayOutUpdateAttributes) {
        this.handleOut(packetPlayOutUpdateAttributes, new SPacketPlayOutUpdateAttributes());
    }
    
    public void a(final PacketPlayInClientCommand packetPlayInClientCommand) {
        this.handleIn(packetPlayInClientCommand, new SPacketPlayInClientCommand());
    }
    
    public void a(final PacketPlayOutKickDisconnect packetPlayOutKickDisconnect) {
    }
    
    public void a(final PacketPlayOutTransaction packetPlayOutTransaction) {
        this.handleOut(packetPlayOutTransaction, new SPacketPlayOutTransaction());
    }
    
    public void a(final PacketPlayOutLogin packetPlayOutLogin) {
    }
    
    public void a(final IChatBaseComponent chatBaseComponent) {
    }
    
    public void a(final PacketPlayOutSpawnEntityWeather packetPlayOutSpawnEntityWeather) {
    }
    
    public void a(final PacketPlayInKeepAlive packetPlayInKeepAlive) {
        this.handleIn(packetPlayInKeepAlive, new SPacketPlayInKeepAlive());
    }
    
    static {
        method = SafeReflection.access(new String[] { "net.minecraft.server.v1_8_R3.Packet" }, "a", PacketListener.class);
    }
    
    public void a(final PacketLoginOutEncryptionBegin packetLoginOutEncryptionBegin) {
    }
    
    public void a(final PacketPlayOutMultiBlockChange packetPlayOutMultiBlockChange) {
        this.handleOut(packetPlayOutMultiBlockChange, new SPacketPlayOutMultiBlockChange());
    }
    
    public void a(final PacketLoginOutDisconnect packetLoginOutDisconnect) {
    }
    
    public void a(final PacketPlayOutWorldEvent packetPlayOutWorldEvent) {
    }
    
    public void a(final PacketPlayOutAbilities packetPlayOutAbilities) {
        this.handleOut(packetPlayOutAbilities, new SPacketPlayOutAbilities());
    }
    
    public void a(final PacketPlayOutBlockBreakAnimation packetPlayOutBlockBreakAnimation) {
    }
    
    public void a(final PacketPlayInFlying packetPlayInFlying) {
        this.handleIn(packetPlayInFlying, new SPacketPlayInFlying());
    }
    
    public void a(final PacketPlayOutBlockAction packetPlayOutBlockAction) {
    }
    
    public void a(final PacketPlayOutEntityEffect packetPlayOutEntityEffect) {
        this.handleOut(packetPlayOutEntityEffect, new SPacketPlayOutEntityEffect());
    }
    
    public void a(final PacketPlayOutRemoveEntityEffect packetPlayOutRemoveEntityEffect) {
        this.handleOut(packetPlayOutRemoveEntityEffect, new SPacketPlayOutRemoveEntityEffect());
    }
    
    public void a(final PacketPlayInBlockDig packetPlayInBlockDig) {
        this.handleIn(packetPlayInBlockDig, new SPacketPlayInBlockDig());
    }
    
    public void a(final PacketPlayInWindowClick packetPlayInWindowClick) {
        this.handleIn(packetPlayInWindowClick, new SPacketPlayInWindowClick());
    }
    
    public void a(final PacketPlayInHeldItemSlot packetPlayInHeldItemSlot) {
        this.handleIn(packetPlayInHeldItemSlot, new SPacketPlayInHeldItemSlot());
    }
    
    public void a(final PacketPlayOutEntityVelocity packetPlayOutEntityVelocity) {
        this.handleOut(packetPlayOutEntityVelocity, new SPacketPlayOutEntityVelocity());
    }
    
    public void a(final PacketPlayOutUpdateHealth packetPlayOutUpdateHealth) {
    }
    
    public void a(final PacketPlayOutGameStateChange packetPlayOutGameStateChange) {
        this.handleOut(packetPlayOutGameStateChange, new SPacketPlayOutGameStateChange());
    }
    
    public void a(final PacketPlayOutSpawnEntity packetPlayOutSpawnEntity) {
        this.handleOut(packetPlayOutSpawnEntity, new SPacketPlayOutSpawnEntity());
    }
    
    public void a(final PacketPlayOutNamedSoundEffect packetPlayOutNamedSoundEffect) {
    }
    
    public void a(final PacketPlayOutPosition packetPlayOutPosition) {
        this.handleOut(packetPlayOutPosition, new SPacketPlayOutPosition());
    }
    
    public void a(final PacketPlayOutUpdateEntityNBT packetPlayOutUpdateEntityNBT) {
    }
    
    public void a(final PacketPlayOutWorldParticles packetPlayOutWorldParticles) {
    }
    
    public void a(final PacketPlayOutTileEntityData packetPlayOutTileEntityData) {
    }
    
    public void a(final PacketPlayOutSetCompression packetPlayOutSetCompression) {
    }
    
    public void a(final PacketPlayInCustomPayload packetPlayInCustomPayload) {
        this.handleIn(packetPlayInCustomPayload, new SPacketPlayInCustomPayload());
    }
    
    public void a(final PacketPlayOutTabComplete packetPlayOutTabComplete) {
    }
    
    public void a(final PacketPlayInBlockPlace packetPlayInBlockPlace) {
        this.handleIn(packetPlayInBlockPlace, new SPacketPlayInBlockPlace());
    }
    
    public void a(final PacketPlayOutOpenWindow packetPlayOutOpenWindow) {
        this.handleOut(packetPlayOutOpenWindow, new SPacketPlayOutOpenWindow());
    }
    
    public void a(final PacketPlayInArmAnimation packetPlayInArmAnimation) {
        this.handleIn(packetPlayInArmAnimation, new SPacketPlayInArmAnimation());
    }
    
    public void processTyped(final Packet packet) {
        packet.a((PacketListener)this);
    }
    
    public void a(final PacketPlayOutCustomPayload packetPlayOutCustomPayload) {
    }
    
    public void a(final PacketPlayOutUpdateSign packetPlayOutUpdateSign) {
    }
    
    public void a(final PacketPlayInSettings packetPlayInSettings) {
    }
    
    public void a(final PacketPlayInUpdateSign packetPlayInUpdateSign) {
    }
    
    public void a(final PacketPlayInEntityAction packetPlayInEntityAction) {
        this.handleIn(packetPlayInEntityAction, new SPacketPlayInEntityAction());
    }
    
    public void a(final PacketPlayOutPlayerInfo packetPlayOutPlayerInfo) {
    }
    
    public void a(final PacketPlayOutEntityEquipment packetPlayOutEntityEquipment) {
    }
    
    public void a(final PacketPlayInTabComplete packetPlayInTabComplete) {
    }
    
    public void a(final PacketPlayOutScoreboardTeam packetPlayOutScoreboardTeam) {
    }
    
    public void a(final PacketPlayOutSpawnEntityPainting packetPlayOutSpawnEntityPainting) {
    }
    
    public void a(final PacketPlayOutChat packetPlayOutChat) {
    }
    
    public void a(final PacketPlayInSpectate packetPlayInSpectate) {
    }
    
    public void a(final PacketPlayOutExplosion packetPlayOutExplosion) {
        this.handleOut(packetPlayOutExplosion, new SPacketPlayOutExplosion());
    }
}
