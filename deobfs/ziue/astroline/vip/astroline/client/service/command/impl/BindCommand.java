/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Keyboard
 *  vip.astroline.client.Astroline
 *  vip.astroline.client.service.command.Command
 *  vip.astroline.client.service.module.Module
 */
package vip.astroline.client.service.command.impl;

import java.util.Iterator;
import org.lwjgl.input.Keyboard;
import vip.astroline.client.Astroline;
import vip.astroline.client.service.command.Command;
import vip.astroline.client.service.module.Module;

public class BindCommand
extends Command {
    public BindCommand(String[] names, String description) {
        super(names, description);
    }

    public String getDescription() {
        return "Bind a module";
    }

    public String getSyntax() {
        return ".bind add [MOD] [KEY] | .bind del [MOD] | .bind clear";
    }

    public String executeCommand(String line, String[] args) {
        if (args[0].equalsIgnoreCase("")) {
            Astroline.INSTANCE.tellPlayer(this.getSyntax());
        }
        if (args[0].equalsIgnoreCase("add")) {
            args[2] = args[2].toUpperCase();
            int key = Keyboard.getKeyIndex((String)args[2]);
            for (Module module : Astroline.INSTANCE.moduleManager.getModules()) {
                if (!args[1].equalsIgnoreCase(module.getName())) continue;
                module.setKey(Keyboard.getKeyIndex((String)Keyboard.getKeyName((int)key)));
                Astroline.INSTANCE.tellPlayer("Successfully bound " + module.getName() + " to " + Keyboard.getKeyName((int)key));
            }
        }
        if (args[0].equalsIgnoreCase("del")) {
            args[2] = args[2].toUpperCase();
            for (Module module : Astroline.INSTANCE.moduleManager.getModules()) {
                if (!module.getName().equalsIgnoreCase(args[1])) continue;
                module.setKey(0);
                Astroline.INSTANCE.tellPlayer("Successfully unbound " + module.getName());
            }
        }
        if (!args[0].equalsIgnoreCase("clear")) return line;
        Iterator iterator = Astroline.INSTANCE.moduleManager.getModules().iterator();
        while (iterator.hasNext()) {
            Module module;
            module = (Module)iterator.next();
            module.setKey(0);
            Astroline.INSTANCE.tellPlayer("Successfully unbound all modules");
        }
        return line;
    }
}
