package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import net.minecraft.server.v1_8_R3.*;
import java.math.*;

public class SPacketPlayInKeepAlive extends VPacketPlayInKeepAlive
{
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayInKeepAlive)o);
    }
    
    public void accept(final PacketPlayInKeepAlive packetPlayInKeepAlive) {
        this.id = BigInteger.valueOf(packetPlayInKeepAlive.a());
    }
}
