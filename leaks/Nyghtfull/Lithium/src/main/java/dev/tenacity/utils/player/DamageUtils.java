package dev.tenacity.utils.player;

import dev.tenacity.utils.Utils;
import dev.tenacity.utils.server.PacketUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

public class DamageUtils implements Utils {

    public static void damage(DamageType type) {
        if (mc.thePlayer == null) return;
        double x = mc.thePlayer.posX, y = mc.thePlayer.posY, z = mc.thePlayer.posZ;
        switch (type) {
            case WATCHDOGUP:
                for(int i = 0; i < 49; i++) {
                    PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625, mc.thePlayer.posZ, false));
                    PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                }
                PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                break;
            case WATCHDOGDOWN:
                for(int i = 0; i < 49; i++) {
                    PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.0625, mc.thePlayer.posZ, false));
                    PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                }
                PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                break;
            case NCP:
                for (int i = 0; i <= MovementUtils.getFallDistance() / 0.0625; i++) {
                    send(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.0625, z, false));
                    send(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
                }
                send(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, true));
                break;
            case VANILLA:
                send(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 3.01, z, false));
                send(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
                send(new C03PacketPlayer(true));
                break;
            case VERUS:
                for (int i = 0; i < 3; i++) {
                    for (double offset : new double[]{0.41999998688698, 0.7531999805212, 1.00133597911214, 1.16610926093821,
                            1.24918707874468, 1.1707870772188, 1.0155550727022, 0.78502770378924, 0.4807108763317, 0.10408037809304}) {
                        send(new C03PacketPlayer.C04PacketPlayerPosition(x, y + offset, z, false));
                    }
                    send(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
                }
                send(new C03PacketPlayer(true));
            case SUFFOCATE:
                send(new C03PacketPlayer.C04PacketPlayerPosition(x, y - 2, z, false));
                break;
        }
    }

    public enum DamageType {
        WATCHDOGUP, WATCHDOGDOWN, NCP, VANILLA, VERUS, SUFFOCATE
    }

    private static void send(Packet<?> packet) {
        PacketUtils.sendPacketNoEvent(packet);
    }

}
