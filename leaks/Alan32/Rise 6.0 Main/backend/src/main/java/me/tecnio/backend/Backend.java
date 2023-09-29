package me.tecnio.backend;

import com.mongodb.client.MongoClient;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.tecnio.backend.manager.impl.community.CommunityManager;
import me.tecnio.backend.manager.impl.protection.ProtectionManager;
import me.tecnio.backend.network.Server;

import java.io.PrintStream;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Getter
@Slf4j
public enum Backend {

    INSTANCE;

    // Ryuzaki does not want you to know about this one simple trick! Bypass authentication while developing in seconds!
    public static final boolean OFFLINE = true;
    private Server server;
    private Scanner scanner;
    private PrintStream out;
    private MongoClient mongoClient;
    private ProtectionManager protectionManager;
    private CommunityManager communityManager;

    Executor threadPool = Executors.newCachedThreadPool();

    public void init() {
        log.info("Starting up the server...");

        if (OFFLINE) {
            System.out.println("Not authenticating users, offline mode is active");
            System.out.println("------------------------------------------------");
        }

        this.scanner = new Scanner(System.in);
        this.out = System.out;
        this.start();
        this.protectionManager = new ProtectionManager();
        this.communityManager = new CommunityManager();

        log.info("Server's up!");

        while (true) {
            String input = this.scanner.nextLine();

            switch (input.toLowerCase()) {
                case "online":
                    this.out.println(this.server.getClients().size() + " online");
                    break;

                case "online clear":
                    this.server.getClients().clear();
                    break;

                case "restart server":
                    this.start();
                    break;

                case "kick user":
                    input = this.scanner.nextLine();
                    this.out.println("Input the user you would like to kick");

                    String finalInput = input;
                    this.server.getClients().removeIf(client -> client.getUsername().equalsIgnoreCase(finalInput));
                    this.out.println("Kicked");
                    break;

                default:
                    this.out.println("Unknown Command");
                    break;
            }
        }
    }

    public void start() {
        threadPool.execute(() -> (this.server = new Server()).start());
    }
}
