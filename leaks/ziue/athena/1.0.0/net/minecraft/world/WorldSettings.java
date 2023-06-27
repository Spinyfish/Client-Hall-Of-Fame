package net.minecraft.world;

import net.minecraft.world.storage.*;
import net.minecraft.entity.player.*;

public final class WorldSettings
{
    private final long seed;
    private final GameType theGameType;
    private final boolean mapFeaturesEnabled;
    private final boolean hardcoreEnabled;
    private final WorldType terrainType;
    private boolean commandsAllowed;
    private boolean bonusChestEnabled;
    private String worldName;
    
    public WorldSettings(final long seedIn, final GameType gameType, final boolean enableMapFeatures, final boolean hardcoreMode, final WorldType worldTypeIn) {
        this.worldName = "";
        this.seed = seedIn;
        this.theGameType = gameType;
        this.mapFeaturesEnabled = enableMapFeatures;
        this.hardcoreEnabled = hardcoreMode;
        this.terrainType = worldTypeIn;
    }
    
    public WorldSettings(final WorldInfo info) {
        this(info.getSeed(), info.getGameType(), info.isMapFeaturesEnabled(), info.isHardcoreModeEnabled(), info.getTerrainType());
    }
    
    public WorldSettings enableBonusChest() {
        this.bonusChestEnabled = true;
        return this;
    }
    
    public WorldSettings enableCommands() {
        this.commandsAllowed = true;
        return this;
    }
    
    public WorldSettings setWorldName(final String name) {
        this.worldName = name;
        return this;
    }
    
    public boolean isBonusChestEnabled() {
        return this.bonusChestEnabled;
    }
    
    public long getSeed() {
        return this.seed;
    }
    
    public GameType getGameType() {
        return this.theGameType;
    }
    
    public boolean getHardcoreEnabled() {
        return this.hardcoreEnabled;
    }
    
    public boolean isMapFeaturesEnabled() {
        return this.mapFeaturesEnabled;
    }
    
    public WorldType getTerrainType() {
        return this.terrainType;
    }
    
    public boolean areCommandsAllowed() {
        return this.commandsAllowed;
    }
    
    public static GameType getGameTypeById(final int id) {
        return GameType.getByID(id);
    }
    
    public String getWorldName() {
        return this.worldName;
    }
    
    public enum GameType
    {
        NOT_SET(-1, ""), 
        SURVIVAL(0, "survival"), 
        CREATIVE(1, "creative"), 
        ADVENTURE(2, "adventure"), 
        SPECTATOR(3, "spectator");
        
        int id;
        String name;
        
        private GameType(final int typeId, final String nameIn) {
            this.id = typeId;
            this.name = nameIn;
        }
        
        public int getID() {
            return this.id;
        }
        
        public String getName() {
            return this.name;
        }
        
        public void configurePlayerCapabilities(final PlayerCapabilities capabilities) {
            if (this == GameType.CREATIVE) {
                capabilities.allowFlying = true;
                capabilities.isCreativeMode = true;
                capabilities.disableDamage = true;
            }
            else if (this == GameType.SPECTATOR) {
                capabilities.allowFlying = true;
                capabilities.isCreativeMode = false;
                capabilities.disableDamage = true;
                capabilities.isFlying = true;
            }
            else {
                capabilities.allowFlying = false;
                capabilities.isCreativeMode = false;
                capabilities.disableDamage = false;
                capabilities.isFlying = false;
            }
            capabilities.allowEdit = !this.isAdventure();
        }
        
        public boolean isAdventure() {
            return this == GameType.ADVENTURE || this == GameType.SPECTATOR;
        }
        
        public boolean isCreative() {
            return this == GameType.CREATIVE;
        }
        
        public boolean isSurvivalOrAdventure() {
            return this == GameType.SURVIVAL || this == GameType.ADVENTURE;
        }
        
        public static GameType getByID(final int idIn) {
            for (final GameType worldsettings$gametype : values()) {
                if (worldsettings$gametype.id == idIn) {
                    return worldsettings$gametype;
                }
            }
            return GameType.SURVIVAL;
        }
        
        public static GameType getByName(final String p_77142_0_) {
            for (final GameType worldsettings$gametype : values()) {
                if (worldsettings$gametype.name.equals(p_77142_0_)) {
                    return worldsettings$gametype;
                }
            }
            return GameType.SURVIVAL;
        }
    }
}
