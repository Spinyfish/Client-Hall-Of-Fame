package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import net.minecraft.server.v1_8_R3.*;

public class SPacketPlayInCustomPayload extends VPacketPlayInCustomPayload
{
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayInCustomPayload)o);
    }
    
    public void accept(final PacketPlayInCustomPayload packetPlayInCustomPayload) {
        this.channel = packetPlayInCustomPayload.a();
        this.size = packetPlayInCustomPayload.b().capacity();
    }
}
