package de.fanta.command.impl;


import java.nio.ByteBuffer;

import de.fanta.Client;
import de.fanta.command.Command;
import de.fanta.utils.ChatUtil;

public class Report extends Command {
    public Report() {
        super("r");
    }
   
    @Override
    public void execute(String[] args) {
    	sendIRCPacket("report_" + args[0], -1);
    	ChatUtil.sendChatMessage("Reportet");
    }
    public void sendIRCPacket(String name, int id) {
        if (Client.INSTANCE.ircClient == null) {
            System.err.println("Custom IRC Packet couldn't be sent, because IRC is null!");
            return;
        }
        ByteBuffer buffer = ByteBuffer.allocate(64);
        buffer.putInt(id);
        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        Client.INSTANCE.ircClient.sendCustomData(name, data);
    }
}