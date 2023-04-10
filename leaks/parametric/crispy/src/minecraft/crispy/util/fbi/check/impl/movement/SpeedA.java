package crispy.util.fbi.check.impl.movement;

import crispy.Crispy;
import crispy.util.fbi.check.Check;
import crispy.util.fbi.check.CheckInfo;
import crispy.util.fbi.data.Data;


@CheckInfo(name = "Speed", type = "A")
public class SpeedA extends Check {
    private double prevPosX;
    private double prevPosZ;
    private double vl;

    @Override
    public void run(Data data) {

        double posX = data.getPlayer().posX;
        double posZ = data.getPlayer().posZ;
        double horizontalDistance = Math.hypot(posX - prevPosX, posZ - prevPosZ);
        if (horizontalDistance >= 0.5 && horizontalDistance <= 100 && data.getPlayer().hurtTime == 0) {
            vl++;
            if (vl > 8) {
                alert(data);
            }
        } else {
            if (horizontalDistance >= 0.1) {
                if (vl > 0) {
                    vl--;
                }
            }
        }

        prevPosX = posX;
        prevPosZ = posZ;
    }

}
