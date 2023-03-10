/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.play;

import net.minecraft.network.INetHandler;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.network.play.server.S41PacketServerDifficulty;
import net.minecraft.network.play.server.S42PacketCombatEvent;
import net.minecraft.network.play.server.S43PacketCamera;
import net.minecraft.network.play.server.S44PacketWorldBorder;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.network.play.server.S46PacketSetCompressionLevel;
import net.minecraft.network.play.server.S47PacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import net.minecraft.network.play.server.S49PacketUpdateEntityNBT;

public interface INetHandlerPlayClient
extends INetHandler {
    public void handleSpawnObject(S0EPacketSpawnObject var1);

    public void handleSpawnExperienceOrb(S11PacketSpawnExperienceOrb var1);

    public void handleSpawnGlobalEntity(S2CPacketSpawnGlobalEntity var1);

    public void handleSpawnMob(S0FPacketSpawnMob var1);

    public void handleScoreboardObjective(S3BPacketScoreboardObjective var1);

    public void handleSpawnPainting(S10PacketSpawnPainting var1);

    public void handleSpawnPlayer(S0CPacketSpawnPlayer var1);

    public void handleAnimation(S0BPacketAnimation var1);

    public void handleStatistics(S37PacketStatistics var1);

    public void handleBlockBreakAnim(S25PacketBlockBreakAnim var1);

    public void handleSignEditorOpen(S36PacketSignEditorOpen var1);

    public void handleUpdateTileEntity(S35PacketUpdateTileEntity var1);

    public void handleBlockAction(S24PacketBlockAction var1);

    public void handleBlockChange(S23PacketBlockChange var1);

    public void handleChat(S02PacketChat var1);

    public void handleTabComplete(S3APacketTabComplete var1);

    public void handleMultiBlockChange(S22PacketMultiBlockChange var1);

    public void handleMaps(S34PacketMaps var1);

    public void handleConfirmTransaction(S32PacketConfirmTransaction var1);

    public void handleCloseWindow(S2EPacketCloseWindow var1);

    public void handleWindowItems(S30PacketWindowItems var1);

    public void handleOpenWindow(S2DPacketOpenWindow var1);

    public void handleWindowProperty(S31PacketWindowProperty var1);

    public void handleSetSlot(S2FPacketSetSlot var1);

    public void handleCustomPayload(S3FPacketCustomPayload var1);

    public void handleDisconnect(S40PacketDisconnect var1);

    public void handleUseBed(S0APacketUseBed var1);

    public void handleEntityStatus(S19PacketEntityStatus var1);

    public void handleEntityAttach(S1BPacketEntityAttach var1);

    public void handleExplosion(S27PacketExplosion var1);

    public void handleChangeGameState(S2BPacketChangeGameState var1);

    public void handleKeepAlive(S00PacketKeepAlive var1);

    public void handleChunkData(S21PacketChunkData var1);

    public void handleMapChunkBulk(S26PacketMapChunkBulk var1);

    public void handleEffect(S28PacketEffect var1);

    public void handleJoinGame(S01PacketJoinGame var1);

    public void handleEntityMovement(S14PacketEntity var1);

    public void handlePlayerPosLook(S08PacketPlayerPosLook var1);

    public void handleParticles(S2APacketParticles var1);

    public void handlePlayerAbilities(S39PacketPlayerAbilities var1);

    public void handlePlayerListItem(S38PacketPlayerListItem var1);

    public void handleDestroyEntities(S13PacketDestroyEntities var1);

    public void handleRemoveEntityEffect(S1EPacketRemoveEntityEffect var1);

    public void handleRespawn(S07PacketRespawn var1);

    public void handleEntityHeadLook(S19PacketEntityHeadLook var1);

    public void handleHeldItemChange(S09PacketHeldItemChange var1);

    public void handleDisplayScoreboard(S3DPacketDisplayScoreboard var1);

    public void handleEntityMetadata(S1CPacketEntityMetadata var1);

    public void handleEntityVelocity(S12PacketEntityVelocity var1);

    public void handleEntityEquipment(S04PacketEntityEquipment var1);

    public void handleSetExperience(S1FPacketSetExperience var1);

    public void handleUpdateHealth(S06PacketUpdateHealth var1);

    public void handleTeams(S3EPacketTeams var1);

    public void handleUpdateScore(S3CPacketUpdateScore var1);

    public void handleSpawnPosition(S05PacketSpawnPosition var1);

    public void handleTimeUpdate(S03PacketTimeUpdate var1);

    public void handleUpdateSign(S33PacketUpdateSign var1);

    public void handleSoundEffect(S29PacketSoundEffect var1);

    public void handleCollectItem(S0DPacketCollectItem var1);

    public void handleEntityTeleport(S18PacketEntityTeleport var1);

    public void handleEntityProperties(S20PacketEntityProperties var1);

    public void handleEntityEffect(S1DPacketEntityEffect var1);

    public void handleCombatEvent(S42PacketCombatEvent var1);

    public void handleServerDifficulty(S41PacketServerDifficulty var1);

    public void handleCamera(S43PacketCamera var1);

    public void handleWorldBorder(S44PacketWorldBorder var1);

    public void handleTitle(S45PacketTitle var1);

    public void handleSetCompressionLevel(S46PacketSetCompressionLevel var1);

    public void handlePlayerListHeaderFooter(S47PacketPlayerListHeaderFooter var1);

    public void handleResourcePack(S48PacketResourcePackSend var1);

    public void handleEntityNBT(S49PacketUpdateEntityNBT var1);
}

