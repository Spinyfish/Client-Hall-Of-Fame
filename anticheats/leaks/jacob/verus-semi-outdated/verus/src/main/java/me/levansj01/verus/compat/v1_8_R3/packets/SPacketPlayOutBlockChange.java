package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import java.lang.reflect.*;
import me.levansj01.verus.util.java.*;
import me.levansj01.verus.compat.v1_8_R3.*;
import net.minecraft.server.v1_8_R3.*;

public class SPacketPlayOutBlockChange extends VPacketPlayOutBlockChange
{
    private static final Field a_field;
    private static final Field block_field;
    
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayOutBlockChange)o);
    }
    
    public void accept(final PacketPlayOutBlockChange packetPlayOutBlockChange) {
        final BlockPosition blockPosition = (BlockPosition)SafeReflection.fetch(SPacketPlayOutBlockChange.a_field, packetPlayOutBlockChange);
        this.x = blockPosition.getX();
        this.y = blockPosition.getY();
        this.z = blockPosition.getZ();
        this.data = NMSManager.getInstance().toLazy((IBlockData)SafeReflection.fetch(SPacketPlayOutBlockChange.block_field, packetPlayOutBlockChange));
    }
    
    static {
        a_field = SafeReflection.access(PacketPlayOutBlockChange.class, "a");
        block_field = SafeReflection.access(PacketPlayOutBlockChange.class, "block");
    }
}
