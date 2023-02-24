package me.kansio.client.commands.impl;

import me.kansio.client.commands.Command;
import me.kansio.client.commands.CommandData;
import me.kansio.client.utils.chat.ChatUtil;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

@CommandData(
        name = "name",
        description = "Copies the current account name"
)
public class NameCommand extends Command {

    @Override
    public void run(String[] args) {
        final String name = mc.thePlayer.getName();
        ChatUtil.log("Your Name Is: " + name + ", And Has Been Copied To Your Clipboard");
        StringSelection namefinal = new StringSelection(name);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(namefinal, namefinal);
    }
}


