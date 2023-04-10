package crispy.util.fbi.check.impl.badpackets;

import crispy.Crispy;
import crispy.util.fbi.check.Check;
import crispy.util.fbi.check.CheckInfo;
import crispy.util.fbi.data.Data;
import net.minecraft.entity.player.EntityPlayer;

@CheckInfo(name = "Invalid Rotations", type = "A")
public class BadPackets extends Check {
    private double prevPosX;
    private double prevPosZ;
    private double vl;
    private float prevYaw;

    @Override
    public void run(Data data) {
        EntityPlayer pd = data.getPlayer();
        double posX = data.getPlayer().posX;
        double posZ = data.getPlayer().posZ;
        double horizontalDistance = Math.hypot(posX - prevPosX, posZ - prevPosZ);
        float yaw = data.getPlayer().getRotationYawHead();
        float yawDelta = Math.abs(yaw - prevYaw);
        if (horizontalDistance > 0.3 && yawDelta > 280) {

        }


        prevPosX = posX;
        prevPosZ = posZ;
        prevYaw = yaw;
    }
}
