package com.alan.clients.launcher;

import com.alan.clients.Client;
import com.alan.clients.launcher.procedure.LaunchProcedure;
import com.alan.clients.launcher.procedure.ProcedureFactory;

public final class Launcher {

    public static final String BASE = "http://localhost:3000";

    public void launch() {
        final LaunchProcedure procedure = new ProcedureFactory()
                .setDeveloper(Client.DEVELOPMENT_SWITCH)
                .build();

        procedure.launch();
    }
}
