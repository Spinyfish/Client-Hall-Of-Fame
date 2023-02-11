package Reality.Realii.commands.commands;

import Reality.Realii.commands.Command;
import Reality.Realii.managers.IRC.IRCClient;
import Reality.server.packets.client.C01PacketChat;
import Reality.server.packets.client.C02PacketCommand;

public class IRC extends Command {
    public IRC() {
        super("irc", new String[]{"i"}, "<content>", "chat with other people are that using Reality client");
    }

    @Override
    public String execute(String[] args) {
        String t = "";
        for (String s : args) {
            t = t + s + " ";
        }
        if (t.startsWith("/")) {
            IRCClient.addPacket(IRCClient.writer, new C02PacketCommand(t));
        } else {
            IRCClient.addPacket(IRCClient.writer, new C01PacketChat(t));
        }
        return null;
    }
}
