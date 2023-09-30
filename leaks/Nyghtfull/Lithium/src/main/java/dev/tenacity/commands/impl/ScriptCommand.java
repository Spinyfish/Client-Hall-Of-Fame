package dev.tenacity.commands.impl;

import dev.tenacity.Tenacity;
import dev.tenacity.commands.Command;

public final class ScriptCommand extends Command {

    public ScriptCommand() {
        super("scriptreload", "Reloads all scripts", ".scriptreload");
    }

    @Override
    public void execute(String[] args) {
    }

}
