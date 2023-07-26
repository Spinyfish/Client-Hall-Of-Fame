package dev.hera.client;

import dev.hera.client.events.impl.EventRenderHUD;
import dev.hera.client.mods.ModManager;

public class Client {

    private static Client instance;
    public String name = "Hera";
    public String version = "b1";
    private ModManager modManager;

    public static Client getInstance() {
        return instance;
    }

    public ModManager getModManager(){
        return modManager;
    }

    public Client() {
        instance = this;
        modManager = new ModManager();
    }
}