package net.minecraft.command.server;

import club.pulsive.client.intent.IRCConnection;
import club.pulsive.impl.util.client.Logger;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import java.util.Objects;

public class CommandIRC extends CommandBase {
    public String getCommandName() {
        return "irc";
    }

    public int getRequiredPermissionLevel() {
        return 0;
    }

    public String getCommandUsage(ICommandSender sender) {
        return "/irc <message|list-online|list-sameserver|list-sameclient>";
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if(args == null) return;
        if(args.length == 1 && args[0].startsWith("list-")) {
            switch(args[0]){
                case "list-online":{
                    Logger.print("Online Global IRC Users: " + IRCConnection.INSTANCE.getGlobalOnline());
                    break;
                }
                case "list-sameserver": {
                    Logger.print("Online Players In Same Server: " + IRCConnection.INSTANCE.getGlobalOnSameServer());
                    break;
                }
                case "list-sameclient": {
                    Logger.print("Online Players In Pulsive: " + IRCConnection.INSTANCE.getProductOnline());
                    break;
                }
            }
        }else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                sb.append(" " + args[i]);
            }
            String message = sb.toString();
            IRCConnection.INSTANCE.sendMessage("intent_chat", message);
        }
    }
}
