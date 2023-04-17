package club.mineman.antigamingchair.check.impl.range;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.check.checks.PacketCheck;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.location.CustomLocation;
import club.mineman.antigamingchair.util.MathUtil;
import club.mineman.antigamingchair.util.dummy.DummyPlayer;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class RangeC extends PacketCheck {
    private boolean entitySpawned;
    private int fakeEntityId;

    public RangeC(final AntiGamingChair plugin, final PlayerData playerData) {
        super(plugin, playerData);
        this.entitySpawned = false;
        this.fakeEntityId = 0;
    }

    @Override
    public void handleCheck(final Player player, final Packet packet) {
        if (!this.entitySpawned) {
            return;
        }
        if (packet instanceof PacketPlayInUseEntity) {
            if (((PacketPlayInUseEntity) packet).a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
                final Entity entity = ((PacketPlayInUseEntity) packet).a(((CraftPlayer) player).getHandle().getWorld());
                if (entity != null && entity instanceof EntityPlayer && entity.getId() == this.fakeEntityId) {
                    System.out.println("GGGGGGGGGANG GANG NIGGER WHY YOU BE HITTING OUT OF REAAAAACH RANGE?!?");
                }
            }
        } else if (packet instanceof PacketPlayInFlying) {
            final PacketPlayInFlying flying = (PacketPlayInFlying) packet;
            if (!flying.h()) {
                return;
            }
            final double x = flying.a();
            final double y = flying.b();
            final double z = flying.c();
            final float yaw = flying.d();
            final float pitch = flying.e();
            final CustomLocation location = MathUtil.getLocationInFrontOfLocation(x, y, z, yaw, pitch, 3.3);
            final DummyPlayer dummyPlayer = this.getFakePlayer(player);
            if (dummyPlayer == null) {
                return;
            }
            dummyPlayer.setPosition(location.getX(), location.getY(), location.getZ());
        }
    }

    public void spawnEntity(final Player player) {
        final DummyPlayer dummyPlayer = new DummyPlayer(((CraftPlayer) player).getHandle(), "nigger_420");
        dummyPlayer.setInvisible(true);
        final CustomLocation location = MathUtil.getLocationInFrontOfPlayer(player, 3.3);
        dummyPlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
        dummyPlayer.setPosition(location.getX(), location.getY(), location.getZ());
        final PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(dummyPlayer);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(spawn);
        this.fakeEntityId = dummyPlayer.getId();
        this.entitySpawned = true;
    }

    public void destroyEntity(final Player player) {
        final DummyPlayer dummyPlayer = this.getFakePlayer(player);
        if (dummyPlayer == null) {
            return;
        }
        final PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(dummyPlayer.getId());
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(destroy);
        dummyPlayer.getBukkitEntity().remove();
        this.fakeEntityId = 0;
        this.entitySpawned = false;
    }

    private DummyPlayer getFakePlayer(final Player player) {
        if (!this.entitySpawned || this.fakeEntityId == 0) {
            return null;
        }
        for (final Entity entity : ((CraftPlayer) player).getHandle().getWorld().entityList) {
            if (entity.getId() == this.fakeEntityId) {
                return (DummyPlayer) entity;
            }
        }
        return null;
    }
}
