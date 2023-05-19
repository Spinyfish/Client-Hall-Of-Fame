package club.marsh.bloom.impl.utils.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;

public class ClipUtils {
	static Minecraft mc = Minecraft.getMinecraft();
    public static void clip(double v,double h) {
        float yaw = mc.thePlayer.rotationYaw;
        mc.thePlayer.setPosition(mc.thePlayer.posX + -Math.sin(yaw) * h,mc.thePlayer.posY+v,mc.thePlayer.posZ + Math.cos(yaw) * h);
    }

    public static void clip2(double v,double h,boolean ground, float yaw) {
        mc.getNetHandler().addToSendQueueSilent(new C04PacketPlayerPosition(mc.thePlayer.posX + -Math.sin(yaw) * h,mc.thePlayer.posY+v,mc.thePlayer.posZ + Math.cos(yaw) * h,ground));
    }

    public static void clip2(double v, double h, boolean ground) {
        clip2(v,h,ground,mc.thePlayer.rotationYaw);
    }
}
