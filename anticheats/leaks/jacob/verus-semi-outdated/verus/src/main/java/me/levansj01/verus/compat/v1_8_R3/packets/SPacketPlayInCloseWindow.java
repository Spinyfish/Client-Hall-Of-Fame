package me.levansj01.verus.compat.v1_8_R3.packets;

import me.levansj01.verus.compat.packets.*;
import java.lang.reflect.*;
import net.minecraft.server.v1_8_R3.*;
import me.levansj01.verus.util.java.*;

public class SPacketPlayInCloseWindow extends VPacketPlayInCloseWindow
{
    private static final Field window_field;
    
    static {
        window_field = SafeReflection.access(PacketPlayInCloseWindow.class, "id");
    }
    
    public void accept(final PacketPlayInCloseWindow packetPlayInCloseWindow) {
        this.window = (int)SafeReflection.fetch(SPacketPlayInCloseWindow.window_field, packetPlayInCloseWindow);
    }
    
    @Override
    public void accept(final Object o) {
        this.accept((PacketPlayInCloseWindow)o);
    }
}
