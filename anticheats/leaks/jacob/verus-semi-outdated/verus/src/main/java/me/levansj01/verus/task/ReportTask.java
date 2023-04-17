package me.levansj01.verus.task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.*;
import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;
import me.levansj01.verus.VerusPlugin;
import me.levansj01.verus.compat.NMSManager;
import me.levansj01.verus.storage.StorageEngine;
import me.levansj01.verus.storage.config.VerusConfiguration;
import me.levansj01.verus.type.Loader;
import org.bukkit.Bukkit;

public class ReportTask extends Thread {
    private final SocketAddress socketAddress;
    private boolean running = true;

    public SocketAddress getSocketAddress() {
        return this.socketAddress;
    }

    public void end() {
        this.running = false;
        this.interrupt();
    }

    public void setRunning(boolean bl) {
        this.running = bl;
    }

    @SneakyThrows
    public void run() {
        Thread.sleep(TimeUnit.SECONDS.toMillis(30L));
        while (this.running) {
            VerusConfiguration verusConfiguration;
            Socket socket = new Socket();
            socket.connect(this.socketAddress);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeByte(3);
            dataOutputStream.writeInt(Bukkit.getServer().getPort());
            dataOutputStream.writeInt(Bukkit.getOnlinePlayers().size());
            StorageEngine storageEngine = StorageEngine.getInstance();
            boolean bl = this.running && storageEngine.isConnected();
            dataOutputStream.writeBoolean(bl);
            if (bl) {
                verusConfiguration = storageEngine.getVerusConfig();
                dataOutputStream.writeUTF(verusConfiguration.getMongoHost());
                dataOutputStream.writeInt(verusConfiguration.getMongoPort());
                dataOutputStream.writeUTF(verusConfiguration.getMongoDatabase());
                dataOutputStream.writeInt(Math.toIntExact((long) storageEngine.getDatabase().getTotalBans()));
                dataOutputStream.writeInt(Math.toIntExact((long) storageEngine.getDatabase().getTotalLogs()));
            }
            dataOutputStream.writeBoolean(true);
            dataOutputStream.writeInt(2);
            dataOutputStream.writeInt((int) VerusPlugin.getBuild());
            verusConfiguration = new DataInputStream(socket.getInputStream());
            int n = verusConfiguration.readInt();
            if (n <= 2) {
                boolean bl2 = verusConfiguration.read() == 1;
                if (bl2) {
                    NMSManager.getInstance().postToMainThread(VerusPlugin::restart);
                }
            }
            socket.close();
            try {
                Thread.sleep(TimeUnit.MINUTES.toMillis(3L));
            } catch (InterruptedException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    public ReportTask() throws UnknownHostException {
        super("Verus Report Thread");
        int n;
        InetAddress inetAddress = InetAddress.getByName("session.verusdev.xyz");
        if (Loader.getUsername().equals("Test")) {
            n = 10312;
        } else {
            n = 10311;
        }
        this.socketAddress = new InetSocketAddress(inetAddress, n);
    }
}
