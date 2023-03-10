/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.configuration;

import com.viaversion.viaversion.api.minecraft.WorldIdentifiers;
import com.viaversion.viaversion.api.protocol.version.BlockedProtocolVersions;
import com.viaversion.viaversion.libs.fastutil.ints.IntSet;
import com.viaversion.viaversion.libs.gson.JsonElement;

public interface ViaVersionConfig {
    public boolean isCheckForUpdates();

    public void setCheckForUpdates(boolean var1);

    public boolean isPreventCollision();

    public boolean isNewEffectIndicator();

    public boolean isShowNewDeathMessages();

    public boolean isSuppressMetadataErrors();

    public boolean isShieldBlocking();

    public boolean isNoDelayShieldBlocking();

    public boolean isShowShieldWhenSwordInHand();

    public boolean isHologramPatch();

    public boolean isPistonAnimationPatch();

    public boolean isBossbarPatch();

    public boolean isBossbarAntiflicker();

    public double getHologramYOffset();

    public boolean isAutoTeam();

    public int getMaxPPS();

    public String getMaxPPSKickMessage();

    public int getTrackingPeriod();

    public int getWarningPPS();

    public int getMaxWarnings();

    public String getMaxWarningsKickMessage();

    public boolean isAntiXRay();

    public boolean isSendSupportedVersions();

    public boolean isSimulatePlayerTick();

    public boolean isItemCache();

    public boolean isNMSPlayerTicking();

    public boolean isReplacePistons();

    public int getPistonReplacementId();

    public boolean isChunkBorderFix();

    public boolean isForceJsonTransform();

    public boolean is1_12NBTArrayFix();

    public boolean is1_13TeamColourFix();

    public boolean is1_12QuickMoveActionFix();

    public BlockedProtocolVersions blockedProtocolVersions();

    @Deprecated
    default public IntSet getBlockedProtocols() {
        return this.blockedProtocolVersions().singleBlockedVersions();
    }

    public String getBlockedDisconnectMsg();

    public String getReloadDisconnectMsg();

    public boolean isSuppressConversionWarnings();

    public boolean isDisable1_13AutoComplete();

    public boolean isMinimizeCooldown();

    public boolean isServersideBlockConnections();

    public String getBlockConnectionMethod();

    public boolean isReduceBlockStorageMemory();

    public boolean isStemWhenBlockAbove();

    public boolean isVineClimbFix();

    public boolean isSnowCollisionFix();

    public boolean isInfestedBlocksFix();

    public int get1_13TabCompleteDelay();

    public boolean isTruncate1_14Books();

    public boolean isLeftHandedHandling();

    public boolean is1_9HitboxFix();

    public boolean is1_14HitboxFix();

    public boolean isNonFullBlockLightFix();

    public boolean is1_14HealthNaNFix();

    public boolean is1_15InstantRespawn();

    public boolean isIgnoreLong1_16ChannelNames();

    public boolean isForcedUse1_17ResourcePack();

    public JsonElement get1_17ResourcePackPrompt();

    public WorldIdentifiers get1_16WorldNamesMap();

    public boolean cache1_17Light();
}

