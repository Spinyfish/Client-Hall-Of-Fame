package me.levansj01.verus.compat.v1_7_R4.packets;

import me.levansj01.verus.compat.packets.VPacketPlayInClientCommand;
import net.minecraft.server.v1_7_R4.PacketPlayInClientCommand;

public class SPacketPlayInClientCommand
extends VPacketPlayInClientCommand {
    public void accept(PacketPlayInClientCommand packetPlayInClientCommand) {
        this.command = VPacketPlayInClientCommand.ClientCommand.values()[packetPlayInClientCommand.c().ordinal()];
    }
}

