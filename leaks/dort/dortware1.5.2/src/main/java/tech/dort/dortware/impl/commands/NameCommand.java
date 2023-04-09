package tech.dort.dortware.impl.commands;

import tech.dort.dortware.api.command.Command;
import tech.dort.dortware.api.command.CommandData;
import tech.dort.dortware.impl.utils.player.ChatUtil;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class NameCommand extends Command {

    public NameCommand() {
        super(new CommandData("name"));
    }

    @Override
    public void run(String command, String... args) {
        try {
            if ("name".equalsIgnoreCase(command)) {
                StringSelection selection = new StringSelection(mc.thePlayer.getName());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
                ChatUtil.displayChatMessage("Copied your name to the clipboard!");
            }
        } catch (Exception exception) {
            ChatUtil.displayChatMessage("An unknown error occurred.");
        }
    }
}
