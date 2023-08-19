package cc.swift.commands;

import cc.swift.util.IMinecraft;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class Command implements IMinecraft {
    public final String name, description;
    public final String[] aliases;

    public abstract void onCommand(String[] args);
}
