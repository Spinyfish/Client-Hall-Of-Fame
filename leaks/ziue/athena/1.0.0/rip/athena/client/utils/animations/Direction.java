package rip.athena.client.utils.animations;

public enum Direction
{
    FORWARDS, 
    BACKWARDS;
    
    public Direction opposite() {
        if (this == Direction.FORWARDS) {
            return Direction.BACKWARDS;
        }
        return Direction.FORWARDS;
    }
    
    public boolean forwards() {
        return this == Direction.FORWARDS;
    }
    
    public boolean backwards() {
        return this == Direction.BACKWARDS;
    }
}
