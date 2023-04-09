package tech.dort.dortware.impl.utils.pathfinding;

public class Path {

    private final double startX, startY, startZ;
    private final double endX, endY, endZ;

    public Path(double startX, double startY, double startZ, double endX, double endY, double endZ) {
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
        this.endX = endX;
        this.endY = endY;
        this.endZ = endZ;
    }

    public double getStartX() {
        return startX;
    }

    public double getStartY() {
        return startY;
    }

    public double getStartZ() {
        return startZ;
    }

    public double getEndX() {
        return endX;
    }

    public double getEndY() {
        return endY;
    }

    public double getEndZ() {
        return endZ;
    }
}
