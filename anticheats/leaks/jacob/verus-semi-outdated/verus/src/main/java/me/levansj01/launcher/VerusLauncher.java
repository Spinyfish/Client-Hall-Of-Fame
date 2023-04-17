package me.levansj01.launcher;

import me.levansj01.verus.VerusPlugin;
import org.bukkit.plugin.java.JavaPlugin;

public class VerusLauncher extends JavaPlugin {

    private static VerusLauncher plugin;

    @Override
    public void onEnable() {
        plugin = this;
        try {
            final VerusPlugin verusPlugin = new VerusPlugin();
            verusPlugin.launch(this);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static VerusLauncher getPlugin() {
        return plugin;
    }
}