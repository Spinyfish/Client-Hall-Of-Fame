/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StringUtils;

public class ChatComponentScore
extends ChatComponentStyle {
    private final String name;
    private final String objective;
    private String value = "";

    public ChatComponentScore(String nameIn, String objectiveIn) {
        this.name = nameIn;
        this.objective = objectiveIn;
    }

    public String getName() {
        return this.name;
    }

    public String getObjective() {
        return this.objective;
    }

    public void setValue(String valueIn) {
        this.value = valueIn;
    }

    @Override
    public String getUnformattedTextForChat() {
        MinecraftServer minecraftserver = MinecraftServer.getServer();
        if (minecraftserver != null && minecraftserver.isAnvilFileSet() && StringUtils.isNullOrEmpty(this.value)) {
            ScoreObjective scoreobjective;
            Scoreboard scoreboard = minecraftserver.worldServerForDimension(0).getScoreboard();
            if (scoreboard.entityHasObjective(this.name, scoreobjective = scoreboard.getObjective(this.objective))) {
                Score score = scoreboard.getValueFromObjective(this.name, scoreobjective);
                this.setValue(String.format("%d", score.getScorePoints()));
            } else {
                this.value = "";
            }
        }
        return this.value;
    }

    @Override
    public ChatComponentScore createCopy() {
        ChatComponentScore chatcomponentscore = new ChatComponentScore(this.name, this.objective);
        chatcomponentscore.setValue(this.value);
        chatcomponentscore.setChatStyle(this.getChatStyle().createShallowCopy());
        for (IChatComponent ichatcomponent : this.getSiblings()) {
            chatcomponentscore.appendSibling(ichatcomponent.createCopy());
        }
        return chatcomponentscore;
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (!(p_equals_1_ instanceof ChatComponentScore)) {
            return false;
        }
        ChatComponentScore chatcomponentscore = (ChatComponentScore)p_equals_1_;
        return this.name.equals(chatcomponentscore.name) && this.objective.equals(chatcomponentscore.objective) && super.equals(p_equals_1_);
    }

    @Override
    public String toString() {
        return "ScoreComponent{name='" + this.name + '\'' + "objective='" + this.objective + '\'' + ", siblings=" + this.siblings + ", style=" + this.getChatStyle() + '}';
    }
}

