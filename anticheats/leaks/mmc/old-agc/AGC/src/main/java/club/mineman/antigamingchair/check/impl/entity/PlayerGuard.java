package club.mineman.antigamingchair.check.impl.entity;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.check.checks.PacketCheck;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.util.dummy.DummyPlayer;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerGuard extends PacketCheck {
    private final Map<UUID, Set<Integer>> playerGuardMap;

    public PlayerGuard(final AntiGamingChair plugin, final PlayerData playerData) {
        super(plugin, playerData);
        this.playerGuardMap = new HashMap<UUID, Set<Integer>>();
    }

    @Override
    public void handleCheck(final Player player, final Packet packet) {
        if (packet instanceof PacketPlayInUseEntity) {
            final PacketPlayInUseEntity useEntity = (PacketPlayInUseEntity) packet;
            if (useEntity.a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
                final Entity entity = useEntity.a(((CraftPlayer) player).getHandle().world);
                if (entity == null) {
                    return;
                }
                final boolean b = entity instanceof EntityPlayer;
            }
        }
    }

    public void handleOutboundPacket(final Player player, final Packet packet) {
        if (packet instanceof PacketPlayOutEntity.PacketPlayOutRelEntityMove || packet instanceof PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook) {
            final Entity targetEntity = ((CraftPlayer) player).getHandle().getWorld().a(((PacketPlayOutEntity) packet).getA());
            if (targetEntity instanceof EntityPlayer) {
                final Set<Integer> playerGuard = this.playerGuardMap.get(targetEntity.getUniqueID());
                if (playerGuard == null) {
                    this.createPlayerGuard(player, targetEntity);
                    return;
                }
                final PacketPlayOutEntity movePacket = (PacketPlayOutEntity) packet;
                final Iterator<Integer> iterator = playerGuard.iterator();
                while (iterator.hasNext()) {
                    final int id = iterator.next();
                    for (final Entity entity : ((CraftPlayer) player).getHandle().getWorld().entityList) {
                        if (entity.getBukkitEntity().getEntityId() == id) {
                            final PacketPlayOutEntity.PacketPlayOutRelEntityMove move = new PacketPlayOutEntity.PacketPlayOutRelEntityMove(id, movePacket.getB(), movePacket.getC(), movePacket.getD(), false);
                            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(move);
                            break;
                        }
                    }
                }
            }
        } else {
            final boolean b = packet instanceof PacketPlayOutNamedEntitySpawn;
        }
    }

    private void createPlayerGuard(final Player player, final Entity entity) {
        final Set<Integer> playerGuard = new HashSet<Integer>();
        for (int i = 0; i < 6; ++i) {
            final DummyPlayer entityPlayer = new DummyPlayer(entity, "nigger_" + i);
            entityPlayer.setInvisible(true);
            final PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(entityPlayer);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(spawn);
            playerGuard.add(entityPlayer.getId());
        }
        this.playerGuardMap.put(entity.getUniqueID(), playerGuard);
    }
}
