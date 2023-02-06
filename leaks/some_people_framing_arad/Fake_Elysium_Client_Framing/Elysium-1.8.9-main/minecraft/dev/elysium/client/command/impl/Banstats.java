package dev.elysium.client.command.impl;

import dev.elysium.client.Elysium;
import dev.elysium.client.command.Command;
import dev.elysium.client.utils.api.Hypixel;

public class Banstats extends Command {
    public Banstats() {
        super("Banstats","Shows hypixel ban stats","banstats","bans");
    }


    @Override
    public void onCommand(String[] args, String command) {
        new Thread(new Runnable() {
            public void run() {
                int[] banStats = Hypixel.getBanStats();
                Elysium.getInstance().addChatMessage("Ban Stats");
                Elysium.getInstance().addChatMessage("Since Last Check |  Watchdog : " + banStats[0] + " - Staff : " + banStats[1]);
                Elysium.getInstance().addChatMessage("Since Session Start |  Watchdog : " + Hypixel.bansSinceStart[0] + " - Staff : " + Hypixel.bansSinceStart[1]);
            }
        }).start();
    }
}
