package me.errordev.http.resolver;

import java.net.Socket;

public class ResolvedIP {
    private final String unresolvedIP;
    private String resolvedIP, resolvedHost, resolvedCanonHost;
    private int port;
    private long ping;
    public ResolvedIP(String unresolvedIP, int port) {
        this.unresolvedIP = unresolvedIP;
        try {
            final Socket socket = new Socket(unresolvedIP, port);
            final long prevTime = System.currentTimeMillis();
            if (socket.isConnected()) {
                this.port = socket.getPort();
                this.resolvedIP = socket.getInetAddress().getHostAddress();
                this.resolvedHost = socket.getInetAddress().getHostName();
                this.resolvedCanonHost = socket.getInetAddress().getCanonicalHostName();
                ping = System.currentTimeMillis() - prevTime;
                socket.close();
            }
        } catch (Exception ignore) {}
    }
    public String getUnresolvedIP() {
        return unresolvedIP;
    }
    public String getResolvedCanonHost() {
        return resolvedCanonHost;
    }
    public String getResolvedHost() {
        return resolvedHost;
    }
    public String getResolvedIP() {
        return resolvedIP;
    }
    public long getPing() {
        return ping;
    }
    public int getPort() {
        return port;
    }
}