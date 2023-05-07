package client.command.impl;

import client.command.Command;
import client.util.chat.ChatUtil;
import client.util.player.PlayerUtil;
import net.minecraft.client.gui.GuiScreen;

public final class Name extends Command {

    public Name() {
        super("Copies and displays your username", "name", "ign", "username", "nick", "nickname");
    }

    @Override
    public void execute(final String[] args) {
        if (args.length == 1) {
            final String name = PlayerUtil.name();

            GuiScreen.setClipboardString(name);
            ChatUtil.display("Copied your username to clipboard. (%s)", name);
        } else {
            error();
        }
    }
}
