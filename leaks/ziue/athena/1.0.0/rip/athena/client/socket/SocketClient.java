package rip.athena.client.socket;

import co.gongzh.procbridge.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;

public class SocketClient
{
    public static final Client client;
    private static final Map<String, Boolean> userCache;
    private static Map<String, String> rankCache;
    private static Map<String, Long> cacheTime;
    private static long cacheExpirationTime;
    private static String currentUsername;
    
    public static boolean isClientRunning() {
        try {
            final ServerSocket serverSocket = new ServerSocket(45376);
            serverSocket.close();
            return false;
        }
        catch (Exception e) {
            return e instanceof SocketException;
        }
    }
    
    public static Object sendRequest(final String command, final String payload) {
        return SocketClient.client.request(command, payload);
    }
    
    public static String getRank(final String username) {
        if (SocketClient.rankCache.containsKey(username) && isCacheValid(username)) {
            return SocketClient.rankCache.get(username);
        }
        final String rank;
        final Thread thread = new Thread(() -> {
            rank = SocketClient.client.request("getRank", username).toString();
            SocketClient.rankCache.put(username, rank);
            SocketClient.cacheTime.put(username, System.currentTimeMillis());
            return;
        });
        thread.start();
        try {
            thread.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        return SocketClient.rankCache.get(username);
    }
    
    public static CompletableFuture<String> getRankAsync(final String username) {
        if (SocketClient.rankCache.containsKey(username) && isCacheValid(username)) {
            return CompletableFuture.completedFuture(SocketClient.rankCache.get(username));
        }
        final String rank;
        return CompletableFuture.supplyAsync(() -> {
            rank = SocketClient.client.request("getRank", username).toString();
            SocketClient.rankCache.put(username, rank);
            SocketClient.cacheTime.put(username, System.currentTimeMillis());
            return rank;
        });
    }
    
    public static CompletableFuture<Boolean> isUserAsync(final String username) {
        if (SocketClient.userCache.containsKey(username)) {
            return CompletableFuture.completedFuture(SocketClient.userCache.get(username));
        }
        final String[] args;
        final boolean isUser;
        return CompletableFuture.supplyAsync(() -> {
            args = SocketClient.client.request("isUser", username).toString().split(":");
            isUser = args[0].equals("true");
            SocketClient.userCache.put(username, isUser);
            return Boolean.valueOf(isUser);
        });
    }
    
    private static boolean isCacheValid(final String username) {
        final long currentTime = System.currentTimeMillis();
        final long lastCacheTime = SocketClient.cacheTime.get(username);
        return currentTime - lastCacheTime <= SocketClient.cacheExpirationTime;
    }
    
    public static boolean isUser(final String username) {
        if (SocketClient.userCache.containsKey(username)) {
            return SocketClient.userCache.get(username);
        }
        final String[] args;
        final boolean isUser;
        final Thread thread = new Thread(() -> {
            args = SocketClient.client.request("isUser", username).toString().split(":");
            isUser = args[0].equals("true");
            SocketClient.userCache.put(username, isUser);
            return;
        });
        thread.start();
        try {
            thread.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        return SocketClient.userCache.get(username);
    }
    
    public static String getCurrentUsername() {
        return SocketClient.currentUsername;
    }
    
    public static void setCurrentUsername(final String username) {
        SocketClient.currentUsername = username;
    }
    
    static {
        client = new Client("141.145.209.142", 45376);
        userCache = new HashMap<String, Boolean>();
        SocketClient.rankCache = new HashMap<String, String>();
        SocketClient.cacheTime = new HashMap<String, Long>();
        SocketClient.cacheExpirationTime = 300000L;
        SocketClient.currentUsername = "";
    }
}
