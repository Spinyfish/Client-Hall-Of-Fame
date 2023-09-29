package de.fanta.command.impl;

import de.fanta.Client;
import de.fanta.command.Command;

public class Global extends Command {
    public Global() {
        super("c");
    }
   
    @Override
    public void execute(String[] args) {
     
    	Client.INSTANCE.ircClient.sendChatMessage(String.join(" ", args));

    }
}