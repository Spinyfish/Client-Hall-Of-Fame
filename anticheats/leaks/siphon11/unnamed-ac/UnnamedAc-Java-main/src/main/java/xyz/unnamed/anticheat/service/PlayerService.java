package xyz.unnamed.anticheat.service;

import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.DedicatedServer;
import net.minecraft.server.v1_8_R3.MethodProfiler;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PlayerList;
import sun.misc.Unsafe;
import xyz.unnamed.anticheat.Anticheat;
import xyz.unnamed.anticheat.model.server.AnticheatMethodProfiler;
import xyz.unnamed.anticheat.model.server.AnticheatPlayerList;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Copyright (c) 2022 - Tranquil, LLC.
 *
 * @author incognito@tranquil.cc
 * @date 4/8/2023
 */

@RequiredArgsConstructor
public class PlayerService {
    private PlayerList originalPlayerList;
    private MethodProfiler originalProfiler;

    private Unsafe getUnsafe() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);

            Unsafe unsafe = (Unsafe) field.get(null);
            field.setAccessible(false);

            return unsafe;

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    private AnticheatPlayerList installPlayerListHook(MinecraftServer server, Unsafe unsafe) throws InstantiationException, IllegalAccessException, NoSuchFieldException {
        AnticheatPlayerList playerList = (AnticheatPlayerList) unsafe.allocateInstance(AnticheatPlayerList.class);

        for (Field f : PlayerList.class.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers()))
                continue;

            f.setAccessible(true);
            Object o = f.get(originalPlayerList);
            f.set(playerList, o);
            f.setAccessible(false);
        }

        server.a(playerList);
        return playerList;
    }


    private void installMethodProfilerHook(MinecraftServer server, Unsafe unsafe, AnticheatPlayerList pl) throws InstantiationException, NoSuchFieldException, IllegalAccessException {
        AnticheatMethodProfiler profiler = (AnticheatMethodProfiler) unsafe.allocateInstance(AnticheatMethodProfiler.class);
        profiler.a = server.methodProfiler.a;
        profiler.playerList = pl;
        Field f = MinecraftServer.class.getDeclaredField("methodProfiler");

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
        f.set(server, profiler);
    }

    private void uninstallMethodProfilerHook(MinecraftServer server) throws IllegalAccessException, NoSuchFieldException {
        Field f = MinecraftServer.class.getDeclaredField("methodProfiler");
        f.set(server, originalProfiler);

        originalProfiler = null;
    }

    public void register() {
        DedicatedServer server = (DedicatedServer) MinecraftServer.getServer();
        originalPlayerList = server.aP();
        originalProfiler = server.methodProfiler;

        Unsafe unsafe = getUnsafe();
        if (unsafe == null)
            return;

        try {
            AnticheatPlayerList pl = installPlayerListHook(server, unsafe);
            installMethodProfilerHook(server, unsafe, pl);
        } catch (InstantiationException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        Anticheat.get().getServer().getConsoleSender().sendMessage("Successfully initialised PlayerList.");
    }

    public void unregister() {
        DedicatedServer server = (DedicatedServer) MinecraftServer.getServer();

        server.a(originalPlayerList);
        originalPlayerList = null;

        try {
            uninstallMethodProfilerHook(server);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        Anticheat.get().getServer().getConsoleSender().sendMessage("Successfully unregistered PlayerList.");
    }
}
