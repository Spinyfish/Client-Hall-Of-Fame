package me.levansj01.verus.compat.v1_7_R4.packets;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import me.levansj01.verus.compat.packets.VPacketPlayInUseEntity;
import me.levansj01.verus.util.java.SafeReflection;
import net.minecraft.server.v1_7_R4.Entity;
import net.minecraft.server.v1_7_R4.PacketPlayInUseEntity;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;

public class SPacketPlayInUseEntity
extends VPacketPlayInUseEntity {
    private boolean fetchedEntity;
    private static final Field id_field = SafeReflection.access(PacketPlayInUseEntity.class, "a");

    public void accept(PacketPlayInUseEntity packetPlayInUseEntity) {
        this.id = (Integer)SafeReflection.fetch(id_field, packetPlayInUseEntity);
        this.action = packetPlayInUseEntity.c() != null ? VPacketPlayInUseEntity.EntityUseAction.values()[packetPlayInUseEntity.c().ordinal()] : VPacketPlayInUseEntity.EntityUseAction.INTERACT_AT;
        this.fetchedEntity = false;
    }

    @Override
    public org.bukkit.entity.Entity getEntity(World world) {
        if (!this.fetchedEntity) {
            Entity entity = ((CraftWorld)world).getHandle().getEntity(this.id);
            this.entity = entity == null ? new WeakReference<Object>(null) : new WeakReference<CraftEntity>(entity.getBukkitEntity());
            this.fetchedEntity = true;
        }
        return this.entity == null ? null : (org.bukkit.entity.Entity)this.entity.get();
    }
}

