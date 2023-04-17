package me.levansj01.verus.type;

import lombok.Getter;
import me.levansj01.launcher.VerusLauncher;

public abstract class Loader {
    @Getter
    public static String recentlogsCommand = "recentlogs",
            manualbanCommand = "manualban",
            logsCommand = "logs",
            username = VerusLauncher.getPlugin().getConfig().getString("launcher.username", ""),
            alertsCommand = "alerts",
            checklogsCommand = "checklogs",
            pingCommand = "ping";

    public abstract void load();
}