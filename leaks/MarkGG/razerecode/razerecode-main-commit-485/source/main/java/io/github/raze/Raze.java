package io.github.raze;

import io.github.raze.registry.system.themes.ThemeRegistry;
import io.github.raze.utilities.collection.configs.PrefixUtil;
import io.github.raze.utilities.collection.discord.DiscordUtil;
import io.github.raze.registry.collection.ManagerRegistry;
import io.github.raze.registry.collection.ScreenRegistry;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.Display;

import java.io.File;

public enum Raze {

    INSTANCE("Raze", "1.1", PrefixUtil.getPrefixFromFile() );

    public String name, version, prefix;

    Raze(String name, String version, String prefix) {
        this.name = name;
        this.version = version;
        this.prefix = prefix;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getPrefix() {
        return prefix;
    }

    public ManagerRegistry MANAGER_REGISTRY;
    public ScreenRegistry SCREEN_REGISTRY;

    public void startup() {
        Display.setTitle("Raze Recoded");
        MANAGER_REGISTRY = new ManagerRegistry();

        MANAGER_REGISTRY.MODULE_REGISTRY.bootstrap();
        MANAGER_REGISTRY.COMMAND_REGISTRY.bootstrap();
        MANAGER_REGISTRY.THEME_REGISTRY.bootstrap();

        SCREEN_REGISTRY = new ScreenRegistry();

        DiscordUtil.bootstrap();

        MANAGER_REGISTRY.EVENT_REGISTRY.register(MANAGER_REGISTRY.MODULE_REGISTRY);
        MANAGER_REGISTRY.EVENT_REGISTRY.register(MANAGER_REGISTRY.COMMAND_REGISTRY);
        MANAGER_REGISTRY.EVENT_REGISTRY.register(MANAGER_REGISTRY.THEME_REGISTRY);

        File directory = new File("raze");
        if (!directory.exists())
            directory.mkdirs();
    }

    public void shutdown() {
        MANAGER_REGISTRY.EVENT_REGISTRY.shutdown();
    }

}
