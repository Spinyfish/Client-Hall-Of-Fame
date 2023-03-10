/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.commands.impl;

import lodomir.dev.commands.Command;
import lodomir.dev.config.cloud.CloudConfig;

public class Cloud
extends Command {
    public Cloud() {
        super("Cloud", "November Client Cloud Manager", ".cloud config <load/list> <name>", "cloud");
    }

    @Override
    public void onCommand(String[] args, String command) {
        if (!args[0].equalsIgnoreCase("cloud") && !args[1].equalsIgnoreCase("config")) {
            return;
        }
        switch (args[3]) {
            case "load": {
                CloudConfig.loadConfig(args[4]);
                break;
            }
            case "list": {
                CloudConfig.getConfigs();
            }
        }
    }
}

