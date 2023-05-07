package client.util.player;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

public class PacketUtil {

    public static Minecraft mc = Minecraft.getMinecraft();
    public static double xC;
    public static double yC;
    public static double zC;
    public static double cX;
    public static double cY;
    public static double cZ;
    public static boolean isOnGround;
    public static boolean state;

    public static void interceptC03Pos(double x, double y, double z, boolean onGround) {
        xC = x;
        yC = y;
        zC = z;
        isOnGround = onGround;
    }

    public static void sendPacket(Packet packet) {
        mc.getNetHandler().addToSendQueue(packet);
    }

    public static void sendPacketNoEvent(Packet packet) {
        mc.getNetHandler().getNetworkManager().sendPacket(packet);
    }

    public static void sendPacketSilent(Packet packet) {
        mc.getNetHandler().getNetworkManager().sendPacket(packet);
    }

    public static void reset() {
        zC = 0.0D;
        yC = 0.0D;
        xC = 0.0D;
    }

    public static boolean getState() {
        return state;
    }

    public static void setState(boolean allow) {
        state = allow;
    }

    public static double getX() {
        return cX;
    }

    public static double getY() {
        return cY;
    }

    public static double getZ() {
        return cZ;
    }

    public static void sendPacketWithoutEvent(final Packet<?> packet) {
     //   mc.getNetHandler().addToSendQueueWithoutEvent(packet);
    }

    public static void sendTimes(int amount, Packet<?> packet) {
        for (int i2 = 0; i2 < amount; ++i2) {
            PacketUtil.sendPacketNoEvent(packet);
        }
    }

    public static void send(Packet<?> packet) {
        mc.getNetHandler().addToSendQueue(packet);
    }

    public static void sendC04(double x, double y, double z, boolean ground, boolean silent) {
        if (silent) {
            sendPacketSilent(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, ground));
        } else {
            sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, ground));
        }
    }
}