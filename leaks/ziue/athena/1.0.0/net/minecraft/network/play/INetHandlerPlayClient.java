package net.minecraft.network.play;

import net.minecraft.network.*;
import net.minecraft.network.play.server.*;

public interface INetHandlerPlayClient extends INetHandler
{
    void handleSpawnObject(final S0EPacketSpawnObject p0);
    
    void handleSpawnExperienceOrb(final S11PacketSpawnExperienceOrb p0);
    
    void handleSpawnGlobalEntity(final S2CPacketSpawnGlobalEntity p0);
    
    void handleSpawnMob(final S0FPacketSpawnMob p0);
    
    void handleScoreboardObjective(final S3BPacketScoreboardObjective p0);
    
    void handleSpawnPainting(final S10PacketSpawnPainting p0);
    
    void handleSpawnPlayer(final S0CPacketSpawnPlayer p0);
    
    void handleAnimation(final S0BPacketAnimation p0);
    
    void handleStatistics(final S37PacketStatistics p0);
    
    void handleBlockBreakAnim(final S25PacketBlockBreakAnim p0);
    
    void handleSignEditorOpen(final S36PacketSignEditorOpen p0);
    
    void handleUpdateTileEntity(final S35PacketUpdateTileEntity p0);
    
    void handleBlockAction(final S24PacketBlockAction p0);
    
    void handleBlockChange(final S23PacketBlockChange p0);
    
    void handleChat(final S02PacketChat p0);
    
    void handleTabComplete(final S3APacketTabComplete p0);
    
    void handleMultiBlockChange(final S22PacketMultiBlockChange p0);
    
    void handleMaps(final S34PacketMaps p0);
    
    void handleConfirmTransaction(final S32PacketConfirmTransaction p0);
    
    void handleCloseWindow(final S2EPacketCloseWindow p0);
    
    void handleWindowItems(final S30PacketWindowItems p0);
    
    void handleOpenWindow(final S2DPacketOpenWindow p0);
    
    void handleWindowProperty(final S31PacketWindowProperty p0);
    
    void handleSetSlot(final S2FPacketSetSlot p0);
    
    void handleCustomPayload(final S3FPacketCustomPayload p0);
    
    void handleDisconnect(final S40PacketDisconnect p0);
    
    void handleUseBed(final S0APacketUseBed p0);
    
    void handleEntityStatus(final S19PacketEntityStatus p0);
    
    void handleEntityAttach(final S1BPacketEntityAttach p0);
    
    void handleExplosion(final S27PacketExplosion p0);
    
    void handleChangeGameState(final S2BPacketChangeGameState p0);
    
    void handleKeepAlive(final S00PacketKeepAlive p0);
    
    void handleChunkData(final S21PacketChunkData p0);
    
    void handleMapChunkBulk(final S26PacketMapChunkBulk p0);
    
    void handleEffect(final S28PacketEffect p0);
    
    void handleJoinGame(final S01PacketJoinGame p0);
    
    void handleEntityMovement(final S14PacketEntity p0);
    
    void handlePlayerPosLook(final S08PacketPlayerPosLook p0);
    
    void handleParticles(final S2APacketParticles p0);
    
    void handlePlayerAbilities(final S39PacketPlayerAbilities p0);
    
    void handlePlayerListItem(final S38PacketPlayerListItem p0);
    
    void handleDestroyEntities(final S13PacketDestroyEntities p0);
    
    void handleRemoveEntityEffect(final S1EPacketRemoveEntityEffect p0);
    
    void handleRespawn(final S07PacketRespawn p0);
    
    void handleEntityHeadLook(final S19PacketEntityHeadLook p0);
    
    void handleHeldItemChange(final S09PacketHeldItemChange p0);
    
    void handleDisplayScoreboard(final S3DPacketDisplayScoreboard p0);
    
    void handleEntityMetadata(final S1CPacketEntityMetadata p0);
    
    void handleEntityVelocity(final S12PacketEntityVelocity p0);
    
    void handleEntityEquipment(final S04PacketEntityEquipment p0);
    
    void handleSetExperience(final S1FPacketSetExperience p0);
    
    void handleUpdateHealth(final S06PacketUpdateHealth p0);
    
    void handleTeams(final S3EPacketTeams p0);
    
    void handleUpdateScore(final S3CPacketUpdateScore p0);
    
    void handleSpawnPosition(final S05PacketSpawnPosition p0);
    
    void handleTimeUpdate(final S03PacketTimeUpdate p0);
    
    void handleUpdateSign(final S33PacketUpdateSign p0);
    
    void handleSoundEffect(final S29PacketSoundEffect p0);
    
    void handleCollectItem(final S0DPacketCollectItem p0);
    
    void handleEntityTeleport(final S18PacketEntityTeleport p0);
    
    void handleEntityProperties(final S20PacketEntityProperties p0);
    
    void handleEntityEffect(final S1DPacketEntityEffect p0);
    
    void handleCombatEvent(final S42PacketCombatEvent p0);
    
    void handleServerDifficulty(final S41PacketServerDifficulty p0);
    
    void handleCamera(final S43PacketCamera p0);
    
    void handleWorldBorder(final S44PacketWorldBorder p0);
    
    void handleTitle(final S45PacketTitle p0);
    
    void handleSetCompressionLevel(final S46PacketSetCompressionLevel p0);
    
    void handlePlayerListHeaderFooter(final S47PacketPlayerListHeaderFooter p0);
    
    void handleResourcePack(final S48PacketResourcePackSend p0);
    
    void handleEntityNBT(final S49PacketUpdateEntityNBT p0);
}
