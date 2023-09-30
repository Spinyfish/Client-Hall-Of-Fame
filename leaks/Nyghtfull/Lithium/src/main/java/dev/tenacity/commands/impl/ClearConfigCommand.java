package dev.tenacity.commands.impl;

import dev.tenacity.Tenacity;
import dev.tenacity.commands.Command;
import dev.tenacity.module.Module;

public class ClearConfigCommand extends Command {

    public ClearConfigCommand(){
        super("clearconfig", "Turns off all enabled modules", ".clearconfig");
    }

    @Override
    public void execute(String[] args) {
        Tenacity.INSTANCE.getModuleCollection().getModules().stream().filter(Module::isEnabled).forEach(Module::toggle);
    }
}
