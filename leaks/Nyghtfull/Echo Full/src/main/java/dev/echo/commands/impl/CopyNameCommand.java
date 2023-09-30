package dev.echo.commands.impl;

import dev.echo.commands.Command;
import dev.echo.utils.misc.IOUtils;

public class CopyNameCommand extends Command {
    public CopyNameCommand() {
        super("name", "copies your name to the clipboard", ".name");
    }

    @Override
    public void execute(String[] args) {
        IOUtils.copy(mc.thePlayer.getName());
        sendChatWithInfo("Copied your name to the clipboard");
    }
}
