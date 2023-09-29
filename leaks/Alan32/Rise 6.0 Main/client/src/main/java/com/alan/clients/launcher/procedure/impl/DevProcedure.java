package com.alan.clients.launcher.procedure.impl;

import com.alan.clients.launcher.procedure.LaunchProcedure;
import lombok.SneakyThrows;

public final class DevProcedure implements LaunchProcedure {

    @Override
    @SneakyThrows
    public void launch() {
        // comment out before release
        main: {
            final Object object = Class.forName("com.alan.clients.Client").getEnumConstants()[0];
            if (object == null) break main;

            object.getClass()
                    .getMethod("init")
                    .invoke(object);
        }

        Class.forName("net.minecraft.viamcp.ViaMCP")
                .getMethod("staticInit")
                .invoke(null);
    }
}
