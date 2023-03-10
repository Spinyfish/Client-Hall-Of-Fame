/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.commands.impl;

import lodomir.dev.commands.Command;
import lodomir.dev.config.ConfigHandler;

public class Config
extends Command {
    private final ConfigHandler config = new ConfigHandler();

    public Config() {
        super("Config", "Modifies client configuration", ".config <save/load/list/delete> <name>", "cfg");
    }

    @Override
    public void onCommand(String[] args, String command) {
        switch (args[0].toLowerCase()) {
            case "save": {
                this.config.save(args[1]);
                break;
            }
            case "load": {
                this.config.load(args[1]);
                break;
            }
            case "list": {
                this.config.list();
                break;
            }
            case "delete": {
                this.config.delete(args[1]);
            }
        }
    }
}

