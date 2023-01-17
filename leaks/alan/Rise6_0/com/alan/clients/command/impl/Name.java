package com.alan.clients.command.impl;

import com.alan.clients.command.Command;
import com.alan.clients.util.chat.ChatUtil;
import com.alan.clients.util.player.PlayerUtil;
import net.minecraft.client.gui.GuiScreen;

/**
 * @author Patrick
 * @since 10/19/2021
 */
public final class Name extends Command {

    public Name() {
        super("command.name.description", "name", "ign", "username", "nick", "nickname");
    }

    @Override
    public void execute(final String[] args) {
        if (args.length == 1) {
            final String name = PlayerUtil.name();

            GuiScreen.setClipboardString(name);
            ChatUtil.display("command.name.copied", name);
        } else {
            error();
        }
    }
}
