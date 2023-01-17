package com.alan.clients.command.impl;

import com.alan.clients.api.Rise;
import com.alan.clients.command.Command;
import com.alan.clients.Client;

/**
 * @author Alan
 * @since 3/02/2022
 */
@Rise
public final class Panic extends Command {

    public Panic() {
        super("command.panic.description", "panic", "p");
    }

    @Override
    public void execute(final String[] args) {
        Client.INSTANCE.getModuleManager().getAll().stream().filter(module ->
                !module.getModuleInfo().autoEnabled()).forEach(module -> module.setEnabled(false));
    }
}