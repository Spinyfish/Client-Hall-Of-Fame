package de.fanta.command.impl;

import de.fanta.Client;
import de.fanta.command.Command;
import de.fanta.utils.ChatUtil;

public class Help extends Command {
    public Help() {
        super("help");
    }
   
    @Override
    public void execute(String[] args) {
     
    	 for(Command c : Client.INSTANCE.commandManager.commands){
    		 ChatUtil.sendChatMessageWithPrefix(""+ c.getName());
         }
    }
}