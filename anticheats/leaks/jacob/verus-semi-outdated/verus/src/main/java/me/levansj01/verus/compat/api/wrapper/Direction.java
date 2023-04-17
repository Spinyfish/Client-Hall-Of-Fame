package me.levansj01.verus.compat.api.wrapper;

public enum Direction {
    UP("UP",1),
    DOWN("DOWN",0),
    EAST("EAST",5),
    NORTH("NORTH",2),
    WEST("WEST",4),
    SOUTH("SOUTH",3);

    Direction(String name, int i) {
    }

    public static Direction byFace(int n) {
        Direction[] values;
        if (n < 0 || n >= (values = values()).length) {
            return null;
        }
        return values[n];
    }
}
