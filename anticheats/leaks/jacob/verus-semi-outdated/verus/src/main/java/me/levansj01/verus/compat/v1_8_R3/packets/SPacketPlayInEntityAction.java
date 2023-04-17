package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import net.minecraft.server.v1_8_R3.*;

public class SPacketPlayInEntityAction extends VPacketPlayInEntityAction
{
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayInEntityAction)o);
    }
    
    public void accept(final PacketPlayInEntityAction packetPlayInEntityAction) {
        this.action = PlayerAction.values()[packetPlayInEntityAction.b().ordinal()];
        this.value = packetPlayInEntityAction.c();
    }
}
