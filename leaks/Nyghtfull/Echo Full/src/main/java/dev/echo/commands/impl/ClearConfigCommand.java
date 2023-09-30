package dev.echo.commands.impl;

import dev.echo.Echo;
import dev.echo.commands.Command;
import dev.echo.module.Module;

public class ClearConfigCommand extends Command {

    public ClearConfigCommand(){
        super("clearconfig", "Turns off all enabled modules", ".clearconfig");
    }

    @Override
    public void execute(String[] args) {
        Echo.INSTANCE.getModuleCollection().getModules().stream().filter(Module::isEnabled).forEach(Module::toggle);
    }
}
