package com.alan.clients.command.impl;

import com.alan.clients.api.Rise;
import com.alan.clients.command.Command;
import com.alan.clients.Client;
import com.alan.clients.module.api.DevelopmentFeature;
import com.alan.clients.util.chat.ChatUtil;

/**
 * @author Alan
 * @since 10/19/2021
 */
@Rise
@DevelopmentFeature
public final class DeveloperReload extends Command {

    public DeveloperReload() {
        super("Reloads the client", "developerreload", "dr");
    }

    @Override
    public void execute(final String[] args) {
        Client.INSTANCE.terminate();
        Client.INSTANCE.initRise();
        ChatUtil.display("Reloaded Rise");
    }
}