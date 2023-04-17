package club.mineman.antigamingchair.packet;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.event.PlayerUpdatePositionEvent;
import club.mineman.antigamingchair.event.PlayerUpdateRotationEvent;
import gg.ragemc.spigot.handler.MovementHandler;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class CustomMoveHandler implements MovementHandler {
    private final AntiGamingChair plugin;

    public CustomMoveHandler(final AntiGamingChair plugin) {
        this.plugin = plugin;
    }

    public void handleUpdateLocation(final Player arg0, final Location arg1, final Location arg2, final PacketPlayInFlying arg3) {
        final PlayerUpdatePositionEvent event = new PlayerUpdatePositionEvent(arg0, arg2, arg2);
        if (!event.isCancelled()) {
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    public void handleUpdateRotation(final Player arg0, final Location arg1, final Location arg2, final PacketPlayInFlying arg3) {
        final PlayerUpdateRotationEvent event = new PlayerUpdateRotationEvent(arg0, arg2, arg2);
        if (!event.isCancelled()) {
            Bukkit.getPluginManager().callEvent(event);
        }
    }
}
