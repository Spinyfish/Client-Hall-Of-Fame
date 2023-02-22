package com.alan.clients.launcher.procedure.impl;

import com.alan.clients.launcher.auth.Authenticator;
import com.alan.clients.launcher.procedure.LaunchProcedure;
import lombok.SneakyThrows;

public final class ReleaseProcedure implements LaunchProcedure {

    @Override
    @SneakyThrows
    public void launch() {
        if (!Authenticator.isValid()) return;

        // TODO: 30.07.2022 finish
    }
}
