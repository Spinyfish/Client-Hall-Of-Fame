/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.configuration;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.configuration.ViaVersionConfig;
import com.viaversion.viaversion.api.minecraft.WorldIdentifiers;
import com.viaversion.viaversion.api.protocol.version.BlockedProtocolVersions;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.libs.fastutil.ints.IntOpenHashSet;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.protocol.BlockedProtocolVersionsImpl;
import com.viaversion.viaversion.util.Config;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractViaConfig
extends Config
implements ViaVersionConfig {
    private boolean checkForUpdates;
    private boolean preventCollision;
    private boolean useNewEffectIndicator;
    private boolean useNewDeathmessages;
    private boolean suppressMetadataErrors;
    private boolean shieldBlocking;
    private boolean noDelayShieldBlocking;
    private boolean showShieldWhenSwordInHand;
    private boolean hologramPatch;
    private boolean pistonAnimationPatch;
    private boolean bossbarPatch;
    private boolean bossbarAntiFlicker;
    private double hologramOffset;
    private int maxPPS;
    private String maxPPSKickMessage;
    private int trackingPeriod;
    private int warningPPS;
    private int maxPPSWarnings;
    private String maxPPSWarningsKickMessage;
    private boolean sendSupportedVersions;
    private boolean simulatePlayerTick;
    private boolean itemCache;
    private boolean nmsPlayerTicking;
    private boolean replacePistons;
    private int pistonReplacementId;
    private boolean chunkBorderFix;
    private boolean autoTeam;
    private boolean forceJsonTransform;
    private boolean nbtArrayFix;
    private BlockedProtocolVersions blockedProtocolVersions;
    private String blockedDisconnectMessage;
    private String reloadDisconnectMessage;
    private boolean suppressConversionWarnings;
    private boolean disable1_13TabComplete;
    private boolean minimizeCooldown;
    private boolean teamColourFix;
    private boolean serversideBlockConnections;
    private boolean reduceBlockStorageMemory;
    private boolean flowerStemWhenBlockAbove;
    private boolean vineClimbFix;
    private boolean snowCollisionFix;
    private boolean infestedBlocksFix;
    private int tabCompleteDelay;
    private boolean truncate1_14Books;
    private boolean leftHandedHandling;
    private boolean fullBlockLightFix;
    private boolean healthNaNFix;
    private boolean instantRespawn;
    private boolean ignoreLongChannelNames;
    private boolean forcedUse1_17ResourcePack;
    private JsonElement resourcePack1_17PromptMessage;
    private WorldIdentifiers map1_16WorldNames;
    private boolean cache1_17Light;

    protected AbstractViaConfig(File configFile) {
        super(configFile);
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        this.loadFields();
    }

    protected void loadFields() {
        this.checkForUpdates = this.getBoolean("checkforupdates", true);
        this.preventCollision = this.getBoolean("prevent-collision", true);
        this.useNewEffectIndicator = this.getBoolean("use-new-effect-indicator", true);
        this.useNewDeathmessages = this.getBoolean("use-new-deathmessages", true);
        this.suppressMetadataErrors = this.getBoolean("suppress-metadata-errors", false);
        this.shieldBlocking = this.getBoolean("shield-blocking", true);
        this.noDelayShieldBlocking = this.getBoolean("no-delay-shield-blocking", false);
        this.showShieldWhenSwordInHand = this.getBoolean("show-shield-when-sword-in-hand", false);
        this.hologramPatch = this.getBoolean("hologram-patch", false);
        this.pistonAnimationPatch = this.getBoolean("piston-animation-patch", false);
        this.bossbarPatch = this.getBoolean("bossbar-patch", true);
        this.bossbarAntiFlicker = this.getBoolean("bossbar-anti-flicker", false);
        this.hologramOffset = this.getDouble("hologram-y", -0.96);
        this.maxPPS = this.getInt("max-pps", 800);
        this.maxPPSKickMessage = this.getString("max-pps-kick-msg", "Sending packets too fast? lag?");
        this.trackingPeriod = this.getInt("tracking-period", 6);
        this.warningPPS = this.getInt("tracking-warning-pps", 120);
        this.maxPPSWarnings = this.getInt("tracking-max-warnings", 3);
        this.maxPPSWarningsKickMessage = this.getString("tracking-max-kick-msg", "You are sending too many packets, :(");
        this.sendSupportedVersions = this.getBoolean("send-supported-versions", false);
        this.simulatePlayerTick = this.getBoolean("simulate-pt", true);
        this.itemCache = this.getBoolean("item-cache", true);
        this.nmsPlayerTicking = this.getBoolean("nms-player-ticking", true);
        this.replacePistons = this.getBoolean("replace-pistons", false);
        this.pistonReplacementId = this.getInt("replacement-piston-id", 0);
        this.chunkBorderFix = this.getBoolean("chunk-border-fix", false);
        this.autoTeam = this.getBoolean("auto-team", true);
        this.forceJsonTransform = this.getBoolean("force-json-transform", false);
        this.nbtArrayFix = this.getBoolean("chat-nbt-fix", true);
        this.blockedProtocolVersions = this.loadBlockedProtocolVersions();
        this.blockedDisconnectMessage = this.getString("block-disconnect-msg", "You are using an unsupported Minecraft version!");
        this.reloadDisconnectMessage = this.getString("reload-disconnect-msg", "Server reload, please rejoin!");
        this.minimizeCooldown = this.getBoolean("minimize-cooldown", true);
        this.teamColourFix = this.getBoolean("team-colour-fix", true);
        this.suppressConversionWarnings = this.getBoolean("suppress-conversion-warnings", false);
        this.disable1_13TabComplete = this.getBoolean("disable-1_13-auto-complete", false);
        this.serversideBlockConnections = this.getBoolean("serverside-blockconnections", true);
        this.reduceBlockStorageMemory = this.getBoolean("reduce-blockstorage-memory", false);
        this.flowerStemWhenBlockAbove = this.getBoolean("flowerstem-when-block-above", false);
        this.vineClimbFix = this.getBoolean("vine-climb-fix", false);
        this.snowCollisionFix = this.getBoolean("fix-low-snow-collision", false);
        this.infestedBlocksFix = this.getBoolean("fix-infested-block-breaking", true);
        this.tabCompleteDelay = this.getInt("1_13-tab-complete-delay", 0);
        this.truncate1_14Books = this.getBoolean("truncate-1_14-books", false);
        this.leftHandedHandling = this.getBoolean("left-handed-handling", true);
        this.fullBlockLightFix = this.getBoolean("fix-non-full-blocklight", false);
        this.healthNaNFix = this.getBoolean("fix-1_14-health-nan", true);
        this.instantRespawn = this.getBoolean("use-1_15-instant-respawn", false);
        this.ignoreLongChannelNames = this.getBoolean("ignore-long-1_16-channel-names", true);
        this.forcedUse1_17ResourcePack = this.getBoolean("forced-use-1_17-resource-pack", false);
        this.resourcePack1_17PromptMessage = this.getSerializedComponent("resource-pack-1_17-prompt");
        Map worlds = this.get("map-1_16-world-names", Map.class, new HashMap());
        this.map1_16WorldNames = new WorldIdentifiers(worlds.getOrDefault("overworld", "minecraft:overworld"), worlds.getOrDefault("nether", "minecraft:the_nether"), worlds.getOrDefault("end", "minecraft:the_end"));
        this.cache1_17Light = this.getBoolean("cache-1_17-light", true);
    }

    private BlockedProtocolVersions loadBlockedProtocolVersions() {
        IntOpenHashSet blockedProtocols = new IntOpenHashSet(this.getIntegerList("block-protocols"));
        int lowerBound = -1;
        int upperBound = -1;
        for (String s : this.getStringList("block-versions")) {
            ProtocolVersion protocolVersion;
            if (s.isEmpty()) continue;
            char c = s.charAt(0);
            if (c == '<' || c == '>') {
                protocolVersion = this.protocolVersion(s.substring(1));
                if (protocolVersion == null) continue;
                if (c == '<') {
                    if (lowerBound != -1) {
                        Via.getPlatform().getLogger().warning("Already set lower bound " + lowerBound + " overridden by " + protocolVersion.getName());
                    }
                    lowerBound = protocolVersion.getVersion();
                    continue;
                }
                if (upperBound != -1) {
                    Via.getPlatform().getLogger().warning("Already set upper bound " + upperBound + " overridden by " + protocolVersion.getName());
                }
                upperBound = protocolVersion.getVersion();
                continue;
            }
            protocolVersion = this.protocolVersion(s);
            if (protocolVersion == null || blockedProtocols.add(protocolVersion.getVersion())) continue;
            Via.getPlatform().getLogger().warning("Duplicated blocked protocol version " + protocolVersion.getName() + "/" + protocolVersion.getVersion());
        }
        if (lowerBound != -1 || upperBound != -1) {
            int finalLowerBound = lowerBound;
            int finalUpperBound = upperBound;
            blockedProtocols.removeIf(version -> {
                if (finalLowerBound != -1 && version < finalLowerBound || finalUpperBound != -1 && version > finalUpperBound) {
                    ProtocolVersion protocolVersion = ProtocolVersion.getProtocol(version);
                    Via.getPlatform().getLogger().warning("Blocked protocol version " + protocolVersion.getName() + "/" + protocolVersion.getVersion() + " already covered by upper or lower bound");
                    return true;
                }
                return false;
            });
        }
        return new BlockedProtocolVersionsImpl(blockedProtocols, lowerBound, upperBound);
    }

    private @Nullable ProtocolVersion protocolVersion(String s) {
        ProtocolVersion protocolVersion = ProtocolVersion.getClosest(s);
        if (protocolVersion == null) {
            Via.getPlatform().getLogger().warning("Unknown protocol version in block-versions: " + s);
            return null;
        }
        return protocolVersion;
    }

    @Override
    public boolean isCheckForUpdates() {
        return this.checkForUpdates;
    }

    @Override
    public void setCheckForUpdates(boolean checkForUpdates) {
        this.checkForUpdates = checkForUpdates;
        this.set("checkforupdates", checkForUpdates);
    }

    @Override
    public boolean isPreventCollision() {
        return this.preventCollision;
    }

    @Override
    public boolean isNewEffectIndicator() {
        return this.useNewEffectIndicator;
    }

    @Override
    public boolean isShowNewDeathMessages() {
        return this.useNewDeathmessages;
    }

    @Override
    public boolean isSuppressMetadataErrors() {
        return this.suppressMetadataErrors;
    }

    @Override
    public boolean isShieldBlocking() {
        return this.shieldBlocking;
    }

    @Override
    public boolean isNoDelayShieldBlocking() {
        return this.noDelayShieldBlocking;
    }

    @Override
    public boolean isShowShieldWhenSwordInHand() {
        return this.showShieldWhenSwordInHand;
    }

    @Override
    public boolean isHologramPatch() {
        return this.hologramPatch;
    }

    @Override
    public boolean isPistonAnimationPatch() {
        return this.pistonAnimationPatch;
    }

    @Override
    public boolean isBossbarPatch() {
        return this.bossbarPatch;
    }

    @Override
    public boolean isBossbarAntiflicker() {
        return this.bossbarAntiFlicker;
    }

    @Override
    public double getHologramYOffset() {
        return this.hologramOffset;
    }

    @Override
    public int getMaxPPS() {
        return this.maxPPS;
    }

    @Override
    public String getMaxPPSKickMessage() {
        return this.maxPPSKickMessage;
    }

    @Override
    public int getTrackingPeriod() {
        return this.trackingPeriod;
    }

    @Override
    public int getWarningPPS() {
        return this.warningPPS;
    }

    @Override
    public int getMaxWarnings() {
        return this.maxPPSWarnings;
    }

    @Override
    public String getMaxWarningsKickMessage() {
        return this.maxPPSWarningsKickMessage;
    }

    @Override
    public boolean isAntiXRay() {
        return false;
    }

    @Override
    public boolean isSendSupportedVersions() {
        return this.sendSupportedVersions;
    }

    @Override
    public boolean isSimulatePlayerTick() {
        return this.simulatePlayerTick;
    }

    @Override
    public boolean isItemCache() {
        return this.itemCache;
    }

    @Override
    public boolean isNMSPlayerTicking() {
        return this.nmsPlayerTicking;
    }

    @Override
    public boolean isReplacePistons() {
        return this.replacePistons;
    }

    @Override
    public int getPistonReplacementId() {
        return this.pistonReplacementId;
    }

    @Override
    public boolean isChunkBorderFix() {
        return this.chunkBorderFix;
    }

    @Override
    public boolean isAutoTeam() {
        return this.preventCollision && this.autoTeam;
    }

    @Override
    public boolean isForceJsonTransform() {
        return this.forceJsonTransform;
    }

    @Override
    public boolean is1_12NBTArrayFix() {
        return this.nbtArrayFix;
    }

    @Override
    public boolean is1_12QuickMoveActionFix() {
        return false;
    }

    @Override
    public BlockedProtocolVersions blockedProtocolVersions() {
        return this.blockedProtocolVersions;
    }

    @Override
    public String getBlockedDisconnectMsg() {
        return this.blockedDisconnectMessage;
    }

    @Override
    public String getReloadDisconnectMsg() {
        return this.reloadDisconnectMessage;
    }

    @Override
    public boolean isMinimizeCooldown() {
        return this.minimizeCooldown;
    }

    @Override
    public boolean is1_13TeamColourFix() {
        return this.teamColourFix;
    }

    @Override
    public boolean isSuppressConversionWarnings() {
        return this.suppressConversionWarnings;
    }

    @Override
    public boolean isDisable1_13AutoComplete() {
        return this.disable1_13TabComplete;
    }

    @Override
    public boolean isServersideBlockConnections() {
        return this.serversideBlockConnections;
    }

    @Override
    public String getBlockConnectionMethod() {
        return "packet";
    }

    @Override
    public boolean isReduceBlockStorageMemory() {
        return this.reduceBlockStorageMemory;
    }

    @Override
    public boolean isStemWhenBlockAbove() {
        return this.flowerStemWhenBlockAbove;
    }

    @Override
    public boolean isVineClimbFix() {
        return this.vineClimbFix;
    }

    @Override
    public boolean isSnowCollisionFix() {
        return this.snowCollisionFix;
    }

    @Override
    public boolean isInfestedBlocksFix() {
        return this.infestedBlocksFix;
    }

    @Override
    public int get1_13TabCompleteDelay() {
        return this.tabCompleteDelay;
    }

    @Override
    public boolean isTruncate1_14Books() {
        return this.truncate1_14Books;
    }

    @Override
    public boolean isLeftHandedHandling() {
        return this.leftHandedHandling;
    }

    @Override
    public boolean is1_9HitboxFix() {
        return false;
    }

    @Override
    public boolean is1_14HitboxFix() {
        return false;
    }

    @Override
    public boolean isNonFullBlockLightFix() {
        return this.fullBlockLightFix;
    }

    @Override
    public boolean is1_14HealthNaNFix() {
        return this.healthNaNFix;
    }

    @Override
    public boolean is1_15InstantRespawn() {
        return this.instantRespawn;
    }

    @Override
    public boolean isIgnoreLong1_16ChannelNames() {
        return this.ignoreLongChannelNames;
    }

    @Override
    public boolean isForcedUse1_17ResourcePack() {
        return this.forcedUse1_17ResourcePack;
    }

    @Override
    public JsonElement get1_17ResourcePackPrompt() {
        return this.resourcePack1_17PromptMessage;
    }

    @Override
    public WorldIdentifiers get1_16WorldNamesMap() {
        return this.map1_16WorldNames;
    }

    @Override
    public boolean cache1_17Light() {
        return this.cache1_17Light;
    }
}

