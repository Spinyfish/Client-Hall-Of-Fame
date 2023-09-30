/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package Reality.Realii.utils.misc.proxy;

import Reality.Realii.utils.cheats.player.Helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.util.ChatComponentText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TransparentProxy {
    private final int listenerPort;
    private Logger logger = LogManager.getLogger((String)"AC-Proxy");
    private ServerSocket listener;
    private Socket client;
    private Socket server;
    private Exception failReason;
    public boolean failed = false;
    private boolean ready = false;
    private boolean running = true;

    public TransparentProxy(int listenerPort) {
        this.listenerPort = listenerPort;
    }

    public void start(String proxy, int proxyPort, String ip, int port) throws Exception {
        this.failed = false;
        this.ready = false;
        if (this.running) {
            this.stop();
        }
        try {
            this.logger.info("Starting Listener...");
            this.listener = new ServerSocket(this.listenerPort, 1);
            this.listener.setReuseAddress(true);
            this.logger.info("Listener started!");
            new Thread(() -> {
                try {
                    this.logger.info("Waiting for connection...");
                    this.ready = true;
                    this.client = this.listener.accept();
                    this.client.setTcpNoDelay(true);
                    this.logger.info("Connection incoming!");
                    this.running = true;
                    this.logger.info("Connecting you via Proxy...");
                    this.server = ProxySocket.connectOverProxy(proxy, proxyPort, ip, port);
                    this.logger.info("Connected!");
                    new Thread(() -> {
                        try {
                            byte[] buffer = new byte[4096];
                            InputStream in = this.client.getInputStream();
                            OutputStream out = this.server.getOutputStream();
                            while (this.running) {
                                int read = in.read(buffer);
                                if (read <= 0) {
                                    throw new IOException();
                                }
                                out.write(buffer, 0, read);
                            }
                        }
                        catch (Exception e) {
                            this.failReason = e;
                            this.failed = true;
                        }
                    }, "AC-Proxy-Send").start();
                    new Thread(() -> {
                        try {
                            byte[] buffer = new byte[4096];
                            InputStream in = this.server.getInputStream();
                            OutputStream out = this.client.getOutputStream();
                            while (this.running) {
                                int read = in.read(buffer);
                                if (read <= 0) {
                                    throw new IOException();
                                }
                                out.write(buffer, 0, read);
                            }
                        }
                        catch (Exception e) {
                            this.failReason = e;
                            this.failed = true;
                        }
                    }, "AC-Proxy-Recv").start();
                    this.logger.info("Proxy started!");
                }
                catch (Exception e) {
                    e.printStackTrace();
                    this.failReason = e;
                    this.failed = true;
                    if (Helper.mc.currentScreen != null && Helper.mc.currentScreen instanceof GuiConnecting && ((GuiConnecting)Helper.mc.currentScreen).networkManager != null) {
                        ((GuiConnecting)Helper.mc.currentScreen).networkManager.closeChannel(new ChatComponentText("Proxy failure"));
                    }
                    try {
                        this.client.close();
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                    try {
                        this.server.close();
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                }
            }, "AC-Proxy").start();
        }
        catch (IOException e) {
            this.failReason = e;
            this.failed = true;
        }
    }

    public void stop() {
        if (this.listener != null) {
            try {
                this.listener.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        this.listener = null;
        this.running = false;
    }

    public boolean isRunning() {
        return this.running;
    }

    public boolean isReady() {
        return this.ready;
    }

    public boolean hasFailed() {
        return this.failed;
    }

    public Exception getFailReason() {
        return this.failReason;
    }
}

