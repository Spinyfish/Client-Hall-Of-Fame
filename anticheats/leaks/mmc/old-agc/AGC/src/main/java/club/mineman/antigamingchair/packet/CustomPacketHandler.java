package club.mineman.antigamingchair.packet;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.check.ICheck;
import club.mineman.antigamingchair.client.ClientType;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.event.player.PlayerAlertEvent;
import club.mineman.antigamingchair.event.player.PlayerBanWaveEvent;
import club.mineman.antigamingchair.event.player.PlayerAlertEvent.AlertType;
import club.mineman.antigamingchair.location.CustomLocation;
import com.google.common.base.Charsets;
import gg.ragemc.spigot.handler.PacketHandler;
import io.netty.util.IllegalReferenceCountException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class CustomPacketHandler implements PacketHandler
{
    private final AntiGamingChair plugin;

    public void handleReceivedPacket(final PlayerConnection playerConnection, final Packet packet) {
        try {
            final Player player = playerConnection.getPlayer();
            final PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);
            if (playerData == null) {
                System.out.println("Player data null for " + player.getName());
                return;
            }
            if (playerData.isSniffing()) {
                this.handleSniffedPacket(packet, playerData);
            }
            final String simpleName = packet.getClass().getSimpleName();
            Label_0603: {
                switch (simpleName) {
                    case "PacketPlayInFlying": {
                        break;
                    }
                    case "PacketPlayInPosition": {
                        break;
                    }
                    case "PacketPlayInLook": {
                        break;
                    }
                    case "PacketPlayInCustomPayload": {
                        if (!playerData.getClient().isHacked()) {
                            this.handleCustomPayload(packet, playerData, player);
                        }
                        break Label_0603;
                    }
                    case "PacketPlayInCloseWindow": {
                        playerData.setInventoryOpen(false);
                        break Label_0603;
                    }
                    case "PacketPlayInPositionLook": {
                        break;
                    }
                    case "PacketPlayInBlockPlace": {
                        playerData.setPlacing(true);
                        break Label_0603;
                    }
                    case "PacketPlayInEntityAction": {
                        final PacketPlayInEntityAction.EnumPlayerAction actionType = ((PacketPlayInEntityAction)packet).b();
                        if (actionType == PacketPlayInEntityAction.EnumPlayerAction.START_SPRINTING) {
                            playerData.setSprinting(true);
                            break Label_0603;
                        }
                        if (actionType == PacketPlayInEntityAction.EnumPlayerAction.STOP_SPRINTING) {
                            playerData.setSprinting(false);
                        }
                        break Label_0603;
                    }
                    case "PacketPlayInClientCommand": {
                        if (((PacketPlayInClientCommand)packet).a() == PacketPlayInClientCommand.EnumClientCommand.OPEN_INVENTORY_ACHIEVEMENT) {
                            playerData.setInventoryOpen(true);
                        }
                        break Label_0603;
                    }
                    case "PacketPlayInKeepAlive": {
                        this.handleKeepAlive((PacketPlayInKeepAlive)packet, playerData, player);
                        break Label_0603;
                    }
                    case "PacketPlayInBlockDig": {
                        final PacketPlayInBlockDig.EnumPlayerDigType digType = ((PacketPlayInBlockDig)packet).c();
                        if (digType == PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK) {
                            playerData.setDigging(true);
                            break Label_0603;
                        }
                        if (digType == PacketPlayInBlockDig.EnumPlayerDigType.ABORT_DESTROY_BLOCK || digType == PacketPlayInBlockDig.EnumPlayerDigType.STOP_DESTROY_BLOCK) {
                            playerData.setDigging(false);
                        }
                        break Label_0603;
                    }
                    case "PacketPlayInUseEntity": {
                        playerData.setLastAttackPacket(System.currentTimeMillis());
                        if (playerData.isSendingVape()) {
                            player.sendMessage("§8 §8 §1 §3 §3 §7 §8 §r");
                            playerData.setSendingVape(false);
                        }
                        if (!playerData.isAttackedSinceVelocity()) {
                            playerData.setVelocityX(playerData.getVelocityX() * 0.6);
                            playerData.setVelocityZ(playerData.getVelocityZ() * 0.6);
                            playerData.setAttackedSinceVelocity(true);
                        }
                        break Label_0603;
                    }
                    default:
                        break Label_0603;
                }
                this.handleFlyPacket((PacketPlayInFlying)packet, playerData);
            }
            for (final Class checkClass : PlayerData.CHECKS) {
                final ICheck check = playerData.getCheck(checkClass);
                if (check.getType() == Packet.class) {
                    check.handleCheck(playerConnection.getPlayer(), packet);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleSentPacket(final PlayerConnection playerConnection, final Packet packet) {
        try {
            final Player player = playerConnection.getPlayer();
            final PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);
            final String simpleName = packet.getClass().getSimpleName();
            switch (simpleName) {
                case "PacketPlayOutRelEntityMoveLook": {
                    this.handleEntityPacket((PacketPlayOutEntity)packet, playerData, player);
                    break;
                }
                case "PacketPlayOutEntityVelocity": {
                    this.handleVelocityOut((PacketPlayOutEntityVelocity)packet, playerData, player);
                    return;
                }
                case "PacketPlayOutPosition": {
                    final PacketPlayOutPosition position = (PacketPlayOutPosition)packet;
                    if (position.getE() > 90.0f) {
                        position.setE(90.0f);
                    }
                    else if (position.getE() < -90.0f) {
                        position.setE(-90.0f);
                    }
                    playerData.setVelocityY(0.0);
                    playerData.setVelocityX(0.0);
                    playerData.setVelocityZ(0.0);
                    playerData.setAttackedSinceVelocity(false);
                    playerData.addTeleportLocation(new CustomLocation(position.getA(), position.getB(), position.getC(), position.getD(), position.getE()));
                    return;
                }
                case "PacketPlayOutExplosion": {
                    this.handleExplosionPacket((PacketPlayOutExplosion)packet, playerData);
                    return;
                }
                case "PacketPlayOutEntityTeleport": {
                    this.handleTeleportPacket((PacketPlayOutEntityTeleport)packet, playerData, player);
                    return;
                }
                case "PacketPlayOutEntityLook": {
                    this.handleEntityPacket((PacketPlayOutEntity)packet, playerData, player);
                    break;
                }
                case "PacketPlayOutRelEntityMove": {
                    this.handleEntityPacket((PacketPlayOutEntity)packet, playerData, player);
                    break;
                }
                case "PacketPlayOutKeepAlive": {
                    playerData.addKeepAliveTime(((PacketPlayOutKeepAlive)packet).getA());
                    return;
                }
                case "PacketPlayOutEntity": {
                    this.handleEntityPacket((PacketPlayOutEntity)packet, playerData, player);
                    break;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleSniffedPacket(final Packet packet, final PlayerData playerData) {
        try {
            final StringBuilder builder = new StringBuilder();
            builder.append(packet.getClass().getSimpleName());
            builder.append(" (timestamp = ");
            builder.append(System.currentTimeMillis());
            builder.append(", ");
            final List<Field> fieldsList = new ArrayList<>();
            fieldsList.addAll(Arrays.asList(packet.getClass().getDeclaredFields()));
            fieldsList.addAll(Arrays.asList(packet.getClass().getSuperclass().getDeclaredFields()));
            for (int i = 0; i < fieldsList.size(); ++i) {
                final Field field = fieldsList.get(i);
                field.setAccessible(true);
                builder.append(field.getName());
                builder.append(" = ");
                builder.append(field.get(packet));
                if (i != fieldsList.size() - 1) {
                    builder.append(", ");
                }
            }
            builder.append(")");
            playerData.getSniffedPacketBuilder().append(builder.toString());
            playerData.getSniffedPacketBuilder().append("\n");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleCustomPayload(final Packet packet, final PlayerData playerData, final Player player) {
        final String pluginMessage = ((PacketPlayInCustomPayload)packet).a();

        Bukkit.getConsoleSender().sendMessage(player.getName() + " \n " + pluginMessage);
        ClientType type = null;
        switch (pluginMessage) {
            case "LOLIMAHCKER": {
                type = ClientType.HACKED_CLIENT_A;
                break;
            }
            case "cock": {
                type = ClientType.HACKED_CLIENT_B;

                break;
            }
            case "customGuiOpenBspkrs": {
                type = ClientType.HACKED_CLIENT_C;
                break;
            }
            case "0SO1Lk2KASxzsd": {
                type = ClientType.HACKED_CLIENT_D;
                break;
            }
            case "lmaohax": {
                type = ClientType.HACKED_CLIENT_E;
                break;
            }
            case "MCnetHandler": {
                type = ClientType.HACKED_CLIENT_F;
                break;
            }
            case "OCMC": {
                type = ClientType.OC_MC;
                break;
            }
            case "REGISTER": {
                try {
                    final String registerType = ((PacketPlayInCustomPayload)packet).b().toString(Charsets.UTF_8);
                    if (registerType.contains("FORGE") && !registerType.contains("BungeeCord")) {
                        type = ClientType.FORGE;
                        break;
                    }
                    if (registerType.contains("CB-Client")) {
                        type = ClientType.CHEAT_BREAKER;
                        break;
                    }
                    if (registerType.equalsIgnoreCase("CC")) {
                        type = ClientType.COSMIC_CLIENT;
                        break;
                    }
                }
                catch (IllegalReferenceCountException e2) {
                    return;
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
        if(type == null){
            type = ClientType.VANILLA;
        }
        playerData.setClient(type);
        if (type.isHacked()) {
            final PlayerAlertEvent event = new PlayerAlertEvent(PlayerAlertEvent.AlertType.RELEASE, player, "is using " + type.getName() + ".");
            this.plugin.getServer().getPluginManager().callEvent(event);
            if (!playerData.isBanWave()) {
                final PlayerBanWaveEvent banWaveEvent = new PlayerBanWaveEvent(player, type.getName());
                this.plugin.getServer().getPluginManager().callEvent(banWaveEvent);
                playerData.setBanWave(true);
            }
        }
    }

    private void handleFlyPacket(final PacketPlayInFlying flying, final PlayerData playerData) {
        final CustomLocation customLocation = new CustomLocation(flying.a(), flying.b(), flying.c(), flying.d(), flying.e());
        final CustomLocation lastLocation = playerData.getLastMovePacket();
        if (lastLocation != null) {
            if (!flying.g()) {
                customLocation.setX(lastLocation.getX());
                customLocation.setY(lastLocation.getY());
                customLocation.setZ(lastLocation.getZ());
            }
            if (!flying.h()) {
                customLocation.setYaw(lastLocation.getYaw());
                customLocation.setPitch(lastLocation.getPitch());
            }
            if (System.currentTimeMillis() - lastLocation.getTimestamp() > 110L) {
                playerData.setLastDelayedMovePacket(System.currentTimeMillis());
            }
        }
        playerData.setLastMovePacket(customLocation);
        playerData.setPlacing(false);
        playerData.setAllowTeleport(false);
        if (flying instanceof PacketPlayInFlying.PacketPlayInPositionLook && playerData.allowTeleport(customLocation)) {
            playerData.setAllowTeleport(true);
        }
    }

    private void handleKeepAlive(final PacketPlayInKeepAlive packet, final PlayerData playerData, final Player player) {
        final int id = packet.a();
        if (playerData.keepAliveExists(id)) {
            playerData.setPing(System.currentTimeMillis() - playerData.getKeepAliveTime(id));
        }
        else if (id != 0) {
            final PlayerAlertEvent alertEvent = new PlayerAlertEvent(PlayerAlertEvent.AlertType.RELEASE, player, "sent an illegal keep alive packet.");
            this.plugin.getServer().getPluginManager().callEvent(alertEvent);
        }
    }

    private void handleVelocityOut(final PacketPlayOutEntityVelocity entityVelocity, final PlayerData playerData, final Player player) {
        if (entityVelocity.getA() == player.getEntityId()) {
            final double x = Math.abs(entityVelocity.getB() / 8000.0);
            final double y = entityVelocity.getC() / 8000.0;
            final double z = Math.abs(entityVelocity.getD() / 8000.0);
            if (x > 0.0 || z > 0.0) {
                playerData.setVelocityH((int)(((x + z) / 2.0 + 2.0) * 15.0));
            }
            if (y > 0.0) {
                playerData.setVelocityV((int)(Math.pow(y + 2.0, 2.0) * 5.0));
                if (playerData.isOnGround() && player.getLocation().getY() % 1.0 == 0.0) {
                    playerData.setVelocityX(x);
                    playerData.setVelocityY(y);
                    playerData.setVelocityZ(z);
                    playerData.setLastVelocity(System.currentTimeMillis());
                    playerData.setAttackedSinceVelocity(false);
                }
            }
        }
    }

    private void handleExplosionPacket(final PacketPlayOutExplosion explosion, final PlayerData playerData) {
        final float x = Math.abs(explosion.getF());
        final float y = explosion.getG();
        final float z = Math.abs(explosion.getH());
        if (x > 0.0f || z > 0.0f) {
            playerData.setVelocityH((int)(((x + z) / 2.0f + 2.0f) * 15.0f));
        }
        if (y > 0.0f) {
            playerData.setVelocityV((int)(Math.pow(y + 2.0f, 2.0) * 5.0));
        }
    }

    private void handleEntityPacket(final PacketPlayOutEntity entity, final PlayerData playerData, final Player player) {
        final Entity targetEntity = ((CraftPlayer)player).getHandle().getWorld().a(entity.getA());
        if (targetEntity instanceof EntityPlayer) {
            final Player target = (Player)targetEntity.getBukkitEntity();
            final CustomLocation customLocation = playerData.getLastPlayerPacket(target.getUniqueId(), 1);
            if (customLocation != null) {
                final double x = entity.getB() / 32.0;
                final double y = entity.getC() / 32.0;
                final double z = entity.getD() / 32.0;
                float yaw = entity.getE() * 360.0f / 256.0f;
                float pitch = entity.getF() * 360.0f / 256.0f;
                if (!entity.isH()) {
                    yaw = customLocation.getYaw();
                    pitch = customLocation.getPitch();
                }
                playerData.addPlayerPacket(target.getUniqueId(), new CustomLocation(customLocation.getX() + x, customLocation.getY() + y, customLocation.getZ() + z, yaw, pitch));
            }
        }
    }

    private void handleTeleportPacket(final PacketPlayOutEntityTeleport entityTeleport, final PlayerData playerData, final Player player) {
        final Entity targetEntity = ((CraftPlayer)player).getHandle().getWorld().a(entityTeleport.getA());
        if (targetEntity instanceof EntityPlayer) {
            final Player target = (Player)targetEntity.getBukkitEntity();
            final double x = entityTeleport.getB() / 32.0;
            final double y = entityTeleport.getC() / 32.0;
            final double z = entityTeleport.getD() / 32.0;
            final float yaw = entityTeleport.getE() * 360.0f / 256.0f;
            final float pitch = entityTeleport.getF() * 360.0f / 256.0f;
            playerData.addPlayerPacket(target.getUniqueId(), new CustomLocation(x, y, z, yaw, pitch));
        }
    }

    public CustomPacketHandler(final AntiGamingChair plugin) {
        this.plugin = plugin;
    }
}