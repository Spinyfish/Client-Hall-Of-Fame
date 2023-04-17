package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import java.lang.reflect.*;
import net.minecraft.server.v1_8_R3.*;
import me.levansj01.verus.util.java.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.craftbukkit.v1_8_R3.*;
import java.lang.ref.*;

public class SPacketPlayInUseEntity extends VPacketPlayInUseEntity
{
    private static final Field id_field;
    private boolean fetchedEntity;
    
    static {
        id_field = SafeReflection.access(PacketPlayInUseEntity.class, new String[] { "a", "id" });
    }
    
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayInUseEntity)o);
    }
    
    @Override
    public Entity getEntity(final World world) {
        if (!this.fetchedEntity) {
            final net.minecraft.server.v1_8_R3.Entity a = ((CraftWorld)world).getHandle().a(this.id);
            if (a == null) {
                this.entity = new WeakReference(null);
            }
            else {
                this.entity = new WeakReference(a.getBukkitEntity());
            }
            this.fetchedEntity = true;
        }
        return (this.entity == null) ? null : ((Entity)this.entity.get());
    }
    
    public void accept(final PacketPlayInUseEntity packetPlayInUseEntity) {
        this.id = (int)SafeReflection.fetch(SPacketPlayInUseEntity.id_field, packetPlayInUseEntity);
        this.action = EntityUseAction.values()[packetPlayInUseEntity.a().ordinal()];
        if (this.action == EntityUseAction.INTERACT_AT) {
            this.bodyX = packetPlayInUseEntity.b().a;
            this.bodyY = packetPlayInUseEntity.b().b;
            this.bodyZ = packetPlayInUseEntity.b().c;
        }
        else {
            final double bodyX = 0.0;
            this.bodyZ = bodyX;
            this.bodyY = bodyX;
            this.bodyX = bodyX;
        }
        this.fetchedEntity = false;
    }
}
