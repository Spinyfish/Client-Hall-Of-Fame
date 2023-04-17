package me.levansj01.verus.util.location;

public interface ILocationGround extends ILocation
{
    boolean isGround();
    
    default boolean isAir() {
        return !this.isGround();
    }
}
