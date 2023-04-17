package me.levansj01.verus.compat.v1_7_R4.packets;

import java.lang.reflect.Field;
import me.levansj01.verus.compat.packets.VPacketPlayInCloseWindow;
import me.levansj01.verus.util.java.SafeReflection;
import net.minecraft.server.v1_7_R4.PacketPlayInCloseWindow;

public class SPacketPlayInCloseWindow
extends VPacketPlayInCloseWindow {
    private static final Field window_field = SafeReflection.access(PacketPlayInCloseWindow.class, "a");

    public void accept(PacketPlayInCloseWindow packetPlayInCloseWindow) {
        this.window = (Integer)SafeReflection.fetch(window_field, packetPlayInCloseWindow);
    }
}

