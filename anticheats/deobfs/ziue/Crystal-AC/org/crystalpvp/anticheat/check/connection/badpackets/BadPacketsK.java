package org.crystalpvp.anticheat.check.connection.badpackets;

import org.crystalpvp.anticheat.api.checks.PacketCheck;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.data.player.PlayerData;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class BadPacketsK extends PacketCheck {

    public BadPacketsK(PlayerData playerData) {
        super(playerData, "Packets (K)");
    }

    @Override
    public void handleCheck(Player player, Packet packet) {
        if (ConfigurationManager.isEnabled("BadPacketsK") == false) {
            return;
        }
        double vl = playerData.getCheckVl(this);

        if (packet instanceof PacketPlayInUseEntity) {
            final PacketPlayInUseEntity useEntity = (PacketPlayInUseEntity) packet;
            if (useEntity.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT_AT) {
                final Entity targetEntity = useEntity.a(((CraftPlayer) player).getHandle().getWorld());
                if (targetEntity instanceof EntityPlayer) {
                    final Vec3D vec3D = useEntity.b();
                    if ((Math.abs(vec3D.a) > 0.41 || Math.abs(vec3D.b) > 1.91 || Math.abs(vec3D.c) > 0.41)) {
                        alert(AlertType.RELEASE, player, String.format("VL %.1f/%s.", ++vl , ConfigurationManager.alertVl(getClass().getSimpleName())), true);
                        if (!playerData.isBanned() && ConfigurationManager.isAutoBanning("BadPacketsK") && vl >= ConfigurationManager.banVL("BadPacketsK")) {
                            punish(player);
                        }
                    }
                }
            }
        }
        playerData.setCheckVl(vl, this);
    }
}
