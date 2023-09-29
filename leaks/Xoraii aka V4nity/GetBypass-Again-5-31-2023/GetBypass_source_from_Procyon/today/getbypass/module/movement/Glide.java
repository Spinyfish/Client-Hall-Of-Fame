// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.module.movement;

import today.getbypass.module.Category;
import today.getbypass.utils.Timer;
import today.getbypass.module.Module;

public class Glide extends Module
{
    public Timer timer;
    
    public Glide() {
        super("Glide", 0, "Glides you in the air!", Category.MOVEMENT);
        this.timer = new Timer();
    }
    
    @Override
    public void onUpdate() {
        if (this.isToggled()) {
            this.mc.thePlayer.motionY = (this.timer.hasTimeElapsed(100L, true) ? -0.1550000011920929 : -0.10000000149011612);
        }
    }
}
