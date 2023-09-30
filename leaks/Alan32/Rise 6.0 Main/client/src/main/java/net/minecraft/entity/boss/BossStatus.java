package net.minecraft.entity.boss;

public final class BossStatus {
    public static float healthScale;
    public static int statusBarTime;
    public static String bossName;
    public static boolean hasColorModifier;

    public static void setBossStatus(final IBossDisplayData displayData, final boolean hasColorModifierIn) {
        healthScale = displayData.getHealth() / displayData.getMaxHealth();
        statusBarTime = 100;
        bossName = displayData.getDisplayName().getFormattedText();
        hasColorModifier = hasColorModifierIn;
    }
}
