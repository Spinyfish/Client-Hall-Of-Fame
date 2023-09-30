package io.github.raze.commands.system;

import io.github.raze.utilities.system.BaseUtility;

import java.util.Arrays;
import java.util.List;

public abstract class BaseCommand implements BaseUtility {

    public String name, description, syntax;
    public List<String> aliases;

    public BaseCommand(String name, String description, String syntax, String... aliases) {
        this.name = name;
        this.description = description;
        this.syntax = syntax;
        this.aliases = Arrays.asList(aliases);
    }

    public abstract String onCommand(String[] args, String command);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSyntax() {
        return syntax;
    }

    public List<String> getAliases() {
        return aliases;
    }
}
