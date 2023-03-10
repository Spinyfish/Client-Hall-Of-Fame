/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.WorldInfo;

public class DerivedWorldInfo
extends WorldInfo {
    private final WorldInfo theWorldInfo;

    public DerivedWorldInfo(WorldInfo p_i2145_1_) {
        this.theWorldInfo = p_i2145_1_;
    }

    @Override
    public NBTTagCompound getNBTTagCompound() {
        return this.theWorldInfo.getNBTTagCompound();
    }

    @Override
    public NBTTagCompound cloneNBTCompound(NBTTagCompound nbt) {
        return this.theWorldInfo.cloneNBTCompound(nbt);
    }

    @Override
    public long getSeed() {
        return this.theWorldInfo.getSeed();
    }

    @Override
    public int getSpawnX() {
        return this.theWorldInfo.getSpawnX();
    }

    @Override
    public int getSpawnY() {
        return this.theWorldInfo.getSpawnY();
    }

    @Override
    public int getSpawnZ() {
        return this.theWorldInfo.getSpawnZ();
    }

    @Override
    public long getWorldTotalTime() {
        return this.theWorldInfo.getWorldTotalTime();
    }

    @Override
    public long getWorldTime() {
        return this.theWorldInfo.getWorldTime();
    }

    @Override
    public long getSizeOnDisk() {
        return this.theWorldInfo.getSizeOnDisk();
    }

    @Override
    public NBTTagCompound getPlayerNBTTagCompound() {
        return this.theWorldInfo.getPlayerNBTTagCompound();
    }

    @Override
    public String getWorldName() {
        return this.theWorldInfo.getWorldName();
    }

    @Override
    public int getSaveVersion() {
        return this.theWorldInfo.getSaveVersion();
    }

    @Override
    public long getLastTimePlayed() {
        return this.theWorldInfo.getLastTimePlayed();
    }

    @Override
    public boolean isThundering() {
        return this.theWorldInfo.isThundering();
    }

    @Override
    public int getThunderTime() {
        return this.theWorldInfo.getThunderTime();
    }

    @Override
    public boolean isRaining() {
        return this.theWorldInfo.isRaining();
    }

    @Override
    public int getRainTime() {
        return this.theWorldInfo.getRainTime();
    }

    @Override
    public WorldSettings.GameType getGameType() {
        return this.theWorldInfo.getGameType();
    }

    @Override
    public void setSpawnX(int x) {
    }

    @Override
    public void setSpawnY(int y) {
    }

    @Override
    public void setSpawnZ(int z) {
    }

    @Override
    public void setWorldTotalTime(long time) {
    }

    @Override
    public void setWorldTime(long time) {
    }

    @Override
    public void setSpawn(BlockPos spawnPoint) {
    }

    @Override
    public void setWorldName(String worldName) {
    }

    @Override
    public void setSaveVersion(int version) {
    }

    @Override
    public void setThundering(boolean thunderingIn) {
    }

    @Override
    public void setThunderTime(int time) {
    }

    @Override
    public void setRaining(boolean isRaining) {
    }

    @Override
    public void setRainTime(int time) {
    }

    @Override
    public boolean isMapFeaturesEnabled() {
        return this.theWorldInfo.isMapFeaturesEnabled();
    }

    @Override
    public boolean isHardcoreModeEnabled() {
        return this.theWorldInfo.isHardcoreModeEnabled();
    }

    @Override
    public WorldType getTerrainType() {
        return this.theWorldInfo.getTerrainType();
    }

    @Override
    public void setTerrainType(WorldType type) {
    }

    @Override
    public boolean areCommandsAllowed() {
        return this.theWorldInfo.areCommandsAllowed();
    }

    @Override
    public void setAllowCommands(boolean allow) {
    }

    @Override
    public boolean isInitialized() {
        return this.theWorldInfo.isInitialized();
    }

    @Override
    public void setServerInitialized(boolean initializedIn) {
    }

    @Override
    public GameRules getGameRulesInstance() {
        return this.theWorldInfo.getGameRulesInstance();
    }

    @Override
    public EnumDifficulty getDifficulty() {
        return this.theWorldInfo.getDifficulty();
    }

    @Override
    public void setDifficulty(EnumDifficulty newDifficulty) {
    }

    @Override
    public boolean isDifficultyLocked() {
        return this.theWorldInfo.isDifficultyLocked();
    }

    @Override
    public void setDifficultyLocked(boolean locked) {
    }
}

