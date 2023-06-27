package net.minecraft.world;

public enum EnumDifficulty
{
    PEACEFUL(0, "options.difficulty.peaceful"), 
    EASY(1, "options.difficulty.easy"), 
    NORMAL(2, "options.difficulty.normal"), 
    HARD(3, "options.difficulty.hard");
    
    private static final EnumDifficulty[] difficultyEnums;
    private final int difficultyId;
    private final String difficultyResourceKey;
    
    private EnumDifficulty(final int difficultyIdIn, final String difficultyResourceKeyIn) {
        this.difficultyId = difficultyIdIn;
        this.difficultyResourceKey = difficultyResourceKeyIn;
    }
    
    public int getDifficultyId() {
        return this.difficultyId;
    }
    
    public static EnumDifficulty getDifficultyEnum(final int p_151523_0_) {
        return EnumDifficulty.difficultyEnums[p_151523_0_ % EnumDifficulty.difficultyEnums.length];
    }
    
    public String getDifficultyResourceKey() {
        return this.difficultyResourceKey;
    }
    
    static {
        difficultyEnums = new EnumDifficulty[values().length];
        for (final EnumDifficulty enumdifficulty : values()) {
            EnumDifficulty.difficultyEnums[enumdifficulty.difficultyId] = enumdifficulty;
        }
    }
}
