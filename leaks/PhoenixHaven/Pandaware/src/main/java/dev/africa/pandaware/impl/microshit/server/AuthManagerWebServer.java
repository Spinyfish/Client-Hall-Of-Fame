package dev.africa.pandaware.impl.microshit.server;

import com.sun.net.httpserver.HttpServer;
import dev.africa.pandaware.utils.math.random.RandomUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class AuthManagerWebServer {
    public static ConcurrentHashMap<UUID, AuthInfo> authCache = new ConcurrentHashMap<>();

    public static final int THREADS = 50;

    public static String CLIENT_ID = "";
    public static String CLIENT_SECRET = "";
    public static String REDIRECT_URI = "";

    public static final int TOKEN_STORE_TIME_MS = 30 * 1000;
    public static final int WEB_PORT = RandomUtils.nextInt(3333, 8888);

    public boolean serverRunning;
    private final ExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREADS);
    private HttpServer server;

    public void start() throws IOException {
        CLIENT_ID = "af463f20-a01b-4382-8899-834df1494dae";
        CLIENT_SECRET = "8e1.Q_VGP1WO88Cj00j48yXoN5sl9_.6dB";
        REDIRECT_URI = String.format("http://localhost:%s/auth", WEB_PORT);

        this.executorService.execute(() -> {
            try {
                server = HttpServer.create(new InetSocketAddress("0.0.0.0", WEB_PORT), 0);
                server.createContext("/auth", new OAuthHandler());
                server.createContext("/get", new CachedTokenHandler());
                server.setExecutor(threadPoolExecutor);
                server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        this.serverRunning = true;
    }

    public void shutdown() {
        this.serverRunning = false;
        this.server.stop(1);
        this.threadPoolExecutor.shutdownNow();
        this.executorService.shutdownNow();
    }

    static class AuthInfo {
        public long time;
        public String info;
        public String addr;

        public AuthInfo(long a, String b, String c) {
            time = a;
            info = b;
            addr = c;
        }
    }
}
