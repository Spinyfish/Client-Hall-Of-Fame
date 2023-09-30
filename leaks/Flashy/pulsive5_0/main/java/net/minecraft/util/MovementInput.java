package net.minecraft.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovementInput
{
    public float moveStrafe;
    public float moveForward;
    public boolean jump;
    public boolean sneak;

    public void updatePlayerMoveState()
    {
    }
}
