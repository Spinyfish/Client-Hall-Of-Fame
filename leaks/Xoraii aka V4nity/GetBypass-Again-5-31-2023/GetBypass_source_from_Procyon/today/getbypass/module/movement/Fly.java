// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.module.movement;

import today.getbypass.utils.MoveUtil;
import today.getbypass.module.Category;
import today.getbypass.module.Module;

public class Fly extends Module
{
    private float flySpeed;
    private float timerSpeed;
    
    public Fly() {
        super("Fly", 0, "Fly, without an elytra!", Category.MOVEMENT);
        this.flySpeed = 1.2f;
        this.timerSpeed = 1.0f;
    }
    
    @Override
    public void onUpdate() {
        if (this.isToggled()) {
            this.mc.thePlayer.onGround = true;
            this.mc.thePlayer.motionY = 0.0;
            this.mc.timer.timerSpeed = 1.0f;
            MoveUtil.setSpeed(1.2000000476837158);
        }
    }
}
