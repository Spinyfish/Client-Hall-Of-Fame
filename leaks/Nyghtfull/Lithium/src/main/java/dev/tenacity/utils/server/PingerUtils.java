package dev.tenacity.utils.server;

import dev.tenacity.Tenacity;
import dev.tenacity.event.ListenerAdapter;
import dev.tenacity.event.impl.game.world.TickEvent;
import dev.tenacity.utils.Utils;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.network.OldServerPinger;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class PingerUtils extends ListenerAdapter implements Utils {

    public static long SERVER_UPDATE_TIME = 30000;

    private final OldServerPinger serverPinger;
    private final Map<String, Long> serverUpdateTime;
    private final Map<String, Boolean> serverUpdateStatus;

    @Getter
    private Long serverPing;

    @Override
    public void onTickEvent(TickEvent event) {
        updateManually(Minecraft.getMinecraft().getCurrentServerData());
    }

    public PingerUtils() {
        this.serverPinger = new OldServerPinger();
        this.serverUpdateTime = new HashMap<>();
        this.serverUpdateStatus = new HashMap<>();
        this.serverPing = null;
        Tenacity.INSTANCE.getEventProtocol().register(this);
    }

    public static String getPing() {
        int latency = 0;
        if (!mc.isSingleplayer()) {
            NetworkPlayerInfo info = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID());
            if (info != null) latency = info.getResponseTime();

            if (ServerUtils.isOnHypixel() && latency == 1) {
                int temp = Tenacity.INSTANCE.getPingerUtils().getServerPing().intValue();
                if (temp != -1) {
                    latency = temp;
                }
            }
        }
        return latency == 0 ? "?" : String.valueOf(latency);
    }

    public void updateManually(ServerData server) {
        if (server != null) {
            Long updateTime = serverUpdateTime.get(server.serverIP);
            if ((updateTime == null || updateTime + SERVER_UPDATE_TIME <= System.currentTimeMillis()) && !serverUpdateStatus.getOrDefault(server.serverIP, false)) {
                serverUpdateStatus.put(server.serverIP, true);

                new Thread(() -> {
                    try {
                        serverPinger.ping(server);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }

                    serverUpdateStatus.put(server.serverIP, false);
                    serverUpdateTime.put(server.serverIP, System.currentTimeMillis());
                }).start();
            }

            if (!ServerUtils.isOnHypixel() || server.pingToServer != 1) {
                serverPing = server.pingToServer;
            }
        }
    }

}
