package xyz.unnamed.anticheat;

import lombok.Getter;

import org.bukkit.plugin.java.JavaPlugin;

import xyz.unnamed.anticheat.service.PlayerDataService;
import xyz.unnamed.anticheat.service.PlayerService;
import xyz.unnamed.anticheat.service.packet.PacketService;
import xyz.unnamed.anticheat.utilities.registry.ServiceRegistry;
import xyz.unnamed.anticheat.utilities.registry.ServiceRegistryImpl;

/**
 * Copyright (c) 2022 - Tranquil, LLC.
 *
 * @author incognito@tranquil.cc
 * @date 4/8/2023
 */

@Getter
public class Anticheat extends JavaPlugin {

    @Getter
    private static Anticheat instance;

    private final ServiceRegistry registry = new ServiceRegistryImpl();
    @Override
    public void onEnable() {
        instance = this;

        install(PlayerService.class, new PlayerService());
        install(PacketService.class, new PacketService());
        install(PlayerDataService.class, new PlayerDataService());
    }

    @Override
    public void onDisable() {
        get(PlayerService.class).unregister();
    }


    public static Anticheat get() {
        return instance;
    }

    public static <T> T install(Class<T> key, T service) {
        return instance.registry.put(key, service);
    }

    public static <T> T get(Class<T> tClass) {
        return instance.registry.get(tClass);
    }
}
