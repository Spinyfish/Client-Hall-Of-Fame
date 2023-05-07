package client.event.impl.input;

import client.event.Event;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
public class MoveInputEvent implements Event {
    public float moveForward;
    public float moveStrafe;
    public boolean jump;
    public boolean sneak;
    public double sneakMultiplier;
}
