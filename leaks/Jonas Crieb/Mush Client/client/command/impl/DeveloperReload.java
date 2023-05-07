package client.command.impl;

import client.Client;
import client.command.Command;
import client.module.DevelopmentFeature;
import client.util.chat.ChatUtil;

@DevelopmentFeature
public final class DeveloperReload extends Command {
    public DeveloperReload() {
        super("Reloads the client", "developerreload", "dr");
    }

    @Override
    public void execute(String[] args) {
        Client.INSTANCE.init();
        ChatUtil.display("Reloaded " + Client.NAME);
    }
}
