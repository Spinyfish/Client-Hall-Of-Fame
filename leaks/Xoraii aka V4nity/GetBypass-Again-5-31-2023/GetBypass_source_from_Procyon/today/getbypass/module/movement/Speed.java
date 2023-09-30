// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.module.movement;

import today.getbypass.utils.MoveUtil;
import today.getbypass.module.Category;
import today.getbypass.module.Module;

public class Speed extends Module
{
    public Speed() {
        super("Speed", 0, "Move quickly", Category.MOVEMENT);
    }
    
    @Override
    public void onUpdate() {
        if (this.isToggled()) {
            if (this.mc.thePlayer.fallDistance > 1.7) {
                this.mc.timer.timerSpeed = 1.17f;
            }
            else {
                this.mc.timer.timerSpeed = 1.0f;
            }
            if (this.mc.thePlayer.onGround) {
                if (MoveUtil.isMoving()) {
                    this.mc.thePlayer.jump();
                }
                else {
                    this.mc.thePlayer.motionX = 0.0;
                    this.mc.thePlayer.motionZ = 0.0;
                }
            }
        }
    }
}
