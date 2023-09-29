// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.module.movement;

import today.getbypass.module.Category;
import today.getbypass.module.Module;

public final class Spider extends Module
{
    public Spider() {
        super("Spider", 0, "Climbs you up walls like a spider", Category.MOVEMENT);
    }
    
    @Override
    public void onUpdate() {
        if (this.isToggled() && this.mc.thePlayer.isCollidedHorizontally && this.mc.thePlayer.ticksExisted % 3 == 0) {
            this.mc.thePlayer.motionY = 0.41999998688697815;
        }
    }
}
