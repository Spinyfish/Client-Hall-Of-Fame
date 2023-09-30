package dev.echo.commands.impl;

import dev.echo.commands.Command;

public final class ScriptCommand extends Command {

    public ScriptCommand() {
        super("scriptreload", "Reloads all scripts", ".scriptreload");
    }

    @Override
    public void execute(String[] args) {
    }

}
