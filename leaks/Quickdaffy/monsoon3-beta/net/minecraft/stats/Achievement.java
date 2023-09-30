/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.stats;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.IStatStringFormat;
import net.minecraft.stats.StatBase;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IJsonSerializable;
import net.minecraft.util.StatCollector;

public class Achievement
extends StatBase {
    public final int displayColumn;
    public final int displayRow;
    public final Achievement parentAchievement;
    private final String achievementDescription;
    private IStatStringFormat statStringFormatter;
    public final ItemStack theItemStack;
    private boolean isSpecial;

    public Achievement(String p_i46327_1_, String p_i46327_2_, int column, int row, Item p_i46327_5_, Achievement parent) {
        this(p_i46327_1_, p_i46327_2_, column, row, new ItemStack(p_i46327_5_), parent);
    }

    public Achievement(String p_i45301_1_, String p_i45301_2_, int column, int row, Block p_i45301_5_, Achievement parent) {
        this(p_i45301_1_, p_i45301_2_, column, row, new ItemStack(p_i45301_5_), parent);
    }

    public Achievement(String p_i45302_1_, String p_i45302_2_, int column, int row, ItemStack p_i45302_5_, Achievement parent) {
        super(p_i45302_1_, new ChatComponentTranslation("achievement." + p_i45302_2_, new Object[0]));
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

    @Override
    public Achievement initIndependentStat() {
        this.isIndependent = true;
        return this;
    }

    public Achievement setSpecial() {
        this.isSpecial = true;
        return this;
    }

    @Override
    public Achievement registerStat() {
        super.registerStat();
        AchievementList.achievementList.add(this);
        return this;
    }

    @Override
    public boolean isAchievement() {
        return true;
    }

    @Override
    public IChatComponent getStatName() {
        IChatComponent ichatcomponent = super.getStatName();
        ichatcomponent.getChatStyle().setColor(this.getSpecial() ? EnumChatFormatting.DARK_PURPLE : EnumChatFormatting.GREEN);
        return ichatcomponent;
    }

    @Override
    public Achievement func_150953_b(Class<? extends IJsonSerializable> p_150953_1_) {
        return (Achievement)super.func_150953_b(p_150953_1_);
    }

    public String getDescription() {
        return this.statStringFormatter != null ? this.statStringFormatter.formatString(StatCollector.translateToLocal(this.achievementDescription)) : StatCollector.translateToLocal(this.achievementDescription);
    }

    public Achievement setStatStringFormatter(IStatStringFormat p_75988_1_) {
        this.statStringFormatter = p_75988_1_;
        return this;
    }

    public boolean getSpecial() {
        return this.isSpecial;
    }
}

