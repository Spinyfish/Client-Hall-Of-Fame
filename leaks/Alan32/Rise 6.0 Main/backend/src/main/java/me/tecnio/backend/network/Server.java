package me.tecnio.backend.network;

import lombok.Getter;

import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public final class Server extends Thread {

    private final List<Client> clients = new CopyOnWriteArrayList<>();

    @Override
    public void run() {
        try {
            final ServerSocket server = new ServerSocket(18934);

            while (true) server.accept();

        } catch (final Exception e) {
            System.out.println(e.getMessage());
            System.out.println("shutting down....");
            System.exit(31);
        }
    }
}