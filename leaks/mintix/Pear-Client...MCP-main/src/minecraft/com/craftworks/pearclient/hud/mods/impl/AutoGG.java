package com.craftworks.pearclient.hud.mods.impl;

import java.util.Arrays;

import com.craftworks.pearclient.hud.mods.HudMod;

import net.minecraft.client.Minecraft;
import net.minecraft.util.IChatComponent;

public class AutoGG extends HudMod {
    public static final AutoGG instance = new AutoGG();
    private long lastTrigger;

    public AutoGG() {
        super("AutoGG", "GG Bro", 0, 0);
    }

    public void onChat(IChatComponent message) {
        if (this.isToggled() && System.currentTimeMillis() > this.lastTrigger + 1000L && Arrays.asList(getHypixelTrigger().split("\n")).stream().anyMatch((trigger) -> {
            return message.getUnformattedText().contains(trigger);
        })) {
            Minecraft.getMinecraft().thePlayer.sendChatMessage("gg");
            Minecraft.getMinecraft().ingameGUI.getChatGUI().addToSentMessages("gg");
            this.lastTrigger = System.currentTimeMillis();
        }

    }

    public static String getHypixelTrigger() {
        return "1st Killer - \n1st Place - \nWinner: \n - Damage Dealt - \nWinning Team -\n1st - \nWinners: \nWinner: \nWinning Team: \n won the game!\nTop Seeker: \n1st Place: \nLast team standing!\nWinner #1 (\nTop Survivors\nWinners - \nSumo Duel - \n";
    }
}
