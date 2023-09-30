package club.pulsive.impl.module.impl.combat.pathfinder.jinfinder;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.Vec3;

import java.util.ArrayList;

@Getter
@Setter
public class Path {
    private ArrayList<Vec3> points;

    //Used to find if all points this path tries to create, don't follow the rules function, it will terminate itself
    private boolean beingUsed;

    private double distanceFromDestination;

    public Path(final ArrayList<Vec3> points, final Vec3 destination, final Prioritise prioritise) {
        this.points = points;

        final Vec3 newestPoint = points.get(points.size() - 1);
        final double xDistance = Math.abs(newestPoint.xCoord - destination.xCoord);
        final double yDistance = Math.abs(newestPoint.yCoord - destination.yCoord);
        final double zDistance = Math.abs(newestPoint.zCoord - destination.zCoord);
        distanceFromDestination = xDistance + yDistance + zDistance;
        switch (prioritise) {
            case FASTEST_PATH:
                if (xDistance == zDistance) {
                    distanceFromDestination--;
                }
                if (yDistance == xDistance) {
                    distanceFromDestination--;
                }
                if (zDistance == yDistance) {
                    distanceFromDestination--;
                }
                break;
        }
    }
}
