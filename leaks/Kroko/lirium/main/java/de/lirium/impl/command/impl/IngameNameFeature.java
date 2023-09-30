package de.lirium.impl.command.impl;

import de.lirium.impl.command.CommandFeature;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

@CommandFeature.Info(name = "ingamename", alias = "ign")
public class IngameNameFeature extends CommandFeature {
    @Override
    public boolean execute(String[] args) {
        GuiScreen.setClipboardString(getPlayer().getName());
        sendMessage("§aCopied your name into clipboard!");
        return true;
    }
}
