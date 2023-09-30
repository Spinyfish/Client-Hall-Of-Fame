package net.minecraft.stats;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;

public class Achievement extends StatBase {
    /**
     * Is the column (related to center of achievement gui, in 24 pixels unit) that the achievement will be displayed.
     */
    public final int displayColumn;

    /**
     * Is the row (related to center of achievement gui, in 24 pixels unit) that the achievement will be displayed.
     */
    public final int displayRow;

    /**
     * Holds the parent achievement, that must be taken before this achievement is avaiable.
     */
    public final Achievement parentAchievement;

    /**
     * Holds the description of the achievement, ready to be formatted and/or displayed.
     */
    private final String achievementDescription;

    /**
     * Holds a string formatter for the achievement, some of then needs extra dynamic info - like the key used to open
     * the inventory.
     */
    private IStatStringFormat statStringFormatter;

    /**
     * Holds the ItemStack that will be used to draw the achievement into the GUI.
     */
    public final ItemStack theItemStack;

    /**
     * Special achievements have a 'spiked' (on normal texture pack) frame, special achievements are the hardest ones to
     * achieve.
     */
    private boolean isSpecial;

    public Achievement(final String p_i46327_1_, final String p_i46327_2_, final int column, final int row, final Item p_i46327_5_, final Achievement parent) {
        this(p_i46327_1_, p_i46327_2_, column, row, new ItemStack(p_i46327_5_), parent);
    }

    public Achievement(final String p_i45301_1_, final String p_i45301_2_, final int column, final int row, final Block p_i45301_5_, final Achievement parent) {
        this(p_i45301_1_, p_i45301_2_, column, row, new ItemStack(p_i45301_5_), parent);
    }

    public Achievement(final String p_i45302_1_, final String p_i45302_2_, final int column, final int row, final ItemStack p_i45302_5_, final Achievement parent) {
        super(p_i45302_1_, new ChatComponentTranslation("achievement." + p_i45302_2_));
        this.theItemStack = p_i45302_5_;
        this.achievementDescription = "achievement." + p_i45302_2_ + ".desc";
        this.displayColumn = column;
        this.displayRow = row;

        if (column < AchievementList.minDisplayColumn) {
            AchievementList.minDisplayColumn = column;
        }

        if (row < AchievementList.minDisplayRow) {
            AchievementList.minDisplayRow = row;
        }

        if (column > AchievementList.maxDisplayColumn) {
            AchievementList.maxDisplayColumn = column;
        }

        if (row > AchievementList.maxDisplayRow) {
            AchievementList.maxDisplayRow = row;
        }

        this.parentAchievement = parent;
    }

    /**
     * Initializes the current stat as independent (i.e., lacking prerequisites for being updated) and returns the
     * current instance.
     */
    public Achievement initIndependentStat() {
        this.isIndependent = true;
        return this;
    }

    /**
     * Special achievements have a 'spiked' (on normal texture pack) frame, special achievements are the hardest ones to
     * achieve.
     */
    public Achievement setSpecial() {
        this.isSpecial = true;
        return this;
    }

    /**
     * Register the stat into StatList.
     */
    public Achievement registerStat() {
        super.registerStat();
        AchievementList.achievementList.add(this);
        return this;
    }

    /**
     * Returns whether or not the StatBase-derived class is a statistic (running counter) or an achievement (one-shot).
     */
    public boolean isAchievement() {
        return true;
    }

    public IChatComponent getStatName() {
        final IChatComponent ichatcomponent = super.getStatName();
        ichatcomponent.getChatStyle().setColor(this.getSpecial() ? EnumChatFormatting.DARK_PURPLE : EnumChatFormatting.GREEN);
        return ichatcomponent;
    }

    public Achievement func_150953_b(final Class<? extends IJsonSerializable> p_150953_1_) {
        return (Achievement) super.func_150953_b(p_150953_1_);
    }

    /**
     * Returns the fully description of the achievement - ready to be displayed on screen.
     */
    public String getDescription() {
        return this.statStringFormatter != null ? this.statStringFormatter.formatString(StatCollector.translateToLocal(this.achievementDescription)) : StatCollector.translateToLocal(this.achievementDescription);
    }

    /**
     * Defines a string formatter for the achievement.
     */
    public Achievement setStatStringFormatter(final IStatStringFormat p_75988_1_) {
        this.statStringFormatter = p_75988_1_;
        return this;
    }

    /**
     * Special achievements have a 'spiked' (on normal texture pack) frame, special achievements are the hardest ones to
     * achieve.
     */
    public boolean getSpecial() {
        return this.isSpecial;
    }
}
