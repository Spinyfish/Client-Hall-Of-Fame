package me.tecnio.backend.network;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.tecnio.backend.Backend;
import me.tecnio.backend.handler.ClientPacketHandler;
import packet.Packet;
import util.Communication;
import util.time.StopWatch;

import java.net.Socket;
import java.util.UUID;

@Getter
@Setter
@Slf4j
public final class Client extends Thread {

    private final Socket socket;

    private String ip, pcName, username, os, userId;
    private boolean ssh, connected = true, authenticated, debug = true;

    private final UUID uuid = UUID.randomUUID();

    private final ClientPacketHandler handler;
    private final Communication communication;

    private long lastMessage;
    public boolean loggedIn;
    public StopWatch timeSinceMessagesPopulated = new StopWatch();
    public StopWatch timeSinceKeepAlive = new StopWatch();

    @SneakyThrows
    public Client(final Socket socket) {
        this.socket = socket;

        this.ip = socket.getInetAddress().getHostAddress();
        this.username = "not-resolved";
        this.pcName = "not-resolved";

        this.handler = new ClientPacketHandler(this);
        this.communication = new Communication(socket, true);

        this.lastMessage = System.currentTimeMillis();

        this.setName("thread-" + this.socket.getInetAddress().getHostAddress() + "-" + currentThread().getId());
        this.start();
    }

    @Override
    public void run() {
        try {
            debug("Incoming connection from " + this.ip);

            while (this.socket.isConnected()) {
                final Packet packet = this.communication.read();

                if (packet != null) {
                    packet.process(this.handler);
                } else {
                    debug("Failed Packet is null " + this.ip);
                }

                if (this.socket.getInputStream().read() == -1) {
                    debug("Failed InputStreamRead == -1 " + this.ip);
                    break;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        this.connected = false;
        Backend.INSTANCE.getServer().getClients().remove(this);

        debug("Connection by " + this.ip + " has been closed");
    }

    public void debug(Object obj) {
        if (debug)
            System.out.println("[Backend Debug] > " + obj);
    }
}