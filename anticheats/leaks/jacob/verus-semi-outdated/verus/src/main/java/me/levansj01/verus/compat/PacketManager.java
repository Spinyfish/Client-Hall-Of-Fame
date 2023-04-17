package me.levansj01.verus.compat;

import me.levansj01.verus.data.PlayerData;
import org.bukkit.Bukkit;

public abstract class PacketManager {

    private static PacketManager packetManager = null;
    public abstract void kickForcibly(PlayerData playerData);
    public abstract void updatePing(PlayerData playerData);
    public abstract void close(PlayerData playerData);
    public abstract void cancelPacket();
    public abstract boolean inEventLoop(PlayerData playerData);
    public abstract void inject(PlayerData playerData);
    public abstract boolean checkMovement(PlayerData playerData);
    public abstract boolean isTeleporting(PlayerData playerData);
    public abstract void postToMainThread(Runnable task);
    public abstract void uninject(PlayerData playerData);

    public static PacketManager getInstance() {
        PacketManager packetManager;
        if (PacketManager.packetManager == null) {
            packetManager = PacketManager.packetManager = PacketManager.newInstance();
            return packetManager;
        }
        packetManager = PacketManager.packetManager;
        return packetManager;
    }

    public abstract <P> VPacket<P> transform(boolean var1, Object var2, P var3);

    private static PacketManager newInstance() {
        try {
            String string = Bukkit.getServer().getClass().getName().split("\\.")[3];
            return (PacketManager) Class.forName(PacketManager.class.getName().replace(".PacketManager", "." + string + ".PacketManager")).newInstance();
        } catch (Throwable throwable) {
            throw new IllegalArgumentException(throwable);
        }
    }
}