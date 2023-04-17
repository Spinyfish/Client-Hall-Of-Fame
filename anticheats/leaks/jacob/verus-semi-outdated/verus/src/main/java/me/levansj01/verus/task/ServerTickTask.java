package me.levansj01.verus.task;

import java.util.concurrent.atomic.AtomicBoolean;

import me.levansj01.launcher.VerusLauncher;
import me.levansj01.verus.compat.NMSManager;
import me.levansj01.verus.data.PlayerData;
import me.levansj01.verus.data.manager.DataManager;
import me.levansj01.verus.storage.StorageEngine;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public class ServerTickTask implements Runnable {
    private BukkitTask bukkitTask;
    public static ServerTickTask instance;
    private final AtomicBoolean cancel = new AtomicBoolean();
    private Runnable endTask;
    private long lastTick,
            tick,
            lastTransaction;

    public long getTick() {
        return this.tick;
    }

    public void sendTransactions() {
        if (!this.cancel.get()) {
            DataManager.getInstance().getPlayers().forEach(PlayerData::handleTransaction);
        }
    }

    public Runnable getEndTask() {
        return this.endTask;
    }

    public boolean isLagging(long l) {
        long lsat = l - this.tick;
        if (lsat <= (7784134498468094486L ^ 7784134498468094200L)) {
            lsat = this.tick - this.lastTick;
            return lsat > (8144936410824535163L ^ 8144936410824535701L);
        }
        return true;
    }

    public AtomicBoolean getCancel() {
        return this.cancel;
    }

    public BukkitTask getBukkitTask() {
        return this.bukkitTask;
    }

    public void run() {
        this.lastTick = this.tick;
        this.tick = System.currentTimeMillis();
        if (!StorageEngine.getInstance().getVerusConfig().isMoreTransactions()) {
            if (this.tick - this.lastTransaction < 250) {
                return;
            }
        }
        this.lastTransaction = this.tick;
        this.sendTransactions();
    }

    public long getLastTransaction() {
        return this.lastTransaction;
    }

    public void schedule() {
        this.bukkitTask = Bukkit.getScheduler().runTaskTimer(VerusLauncher.getPlugin(), this, 1, -1284262778401105111L ^ -1284262778401105112L);
        if (StorageEngine.getInstance().getVerusConfig().isMoreTransactions()) {
            NMSManager nmsManager = NMSManager.getInstance();
            nmsManager.postToMainThread(() -> {
                if (!this.cancel.get()) {
                    this.endTask = nmsManager.scheduleEnd(this::sendTransactions);
                }
            });
        }
    }

    public static ServerTickTask getInstance() {
        ServerTickTask serverTickTask;
        if (instance == null) {
            serverTickTask = instance = new ServerTickTask();
        } else {
            serverTickTask = instance;
        }
        return serverTickTask;
    }

    public void cancel() {
        this.cancel.set(true);
        if (this.bukkitTask != null) {
            this.bukkitTask.cancel();
        }
        if (this.endTask != null) {
            NMSManager.getInstance().postToMainThread(this.endTask);
        }
    }

    public long getLastTick() {
        return this.lastTick;
    }
}
