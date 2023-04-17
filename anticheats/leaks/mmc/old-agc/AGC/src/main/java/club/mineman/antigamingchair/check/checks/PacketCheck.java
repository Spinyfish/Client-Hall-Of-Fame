package club.mineman.antigamingchair.check.checks;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.check.AbstractCheck;
import club.mineman.antigamingchair.data.PlayerData;
import net.minecraft.server.v1_8_R3.Packet;

public abstract class PacketCheck extends AbstractCheck<Packet> {
    public PacketCheck(final AntiGamingChair plugin, final PlayerData playerData) {
        super(plugin, playerData, Packet.class);
    }
}
