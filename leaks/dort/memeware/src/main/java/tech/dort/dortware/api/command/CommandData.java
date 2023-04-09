package tech.dort.dortware.api.command;

import tech.dort.dortware.api.module.Module;

public class CommandData {

    private final String name;
    private final String[] aliases;

    public CommandData(String name, String... aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    public CommandData(Module module) {
        this(module.getModuleData().getName());
    }

    public String getName() {
        return name;
    }

    public String[] getAliases() {
        return aliases;
    }
}
