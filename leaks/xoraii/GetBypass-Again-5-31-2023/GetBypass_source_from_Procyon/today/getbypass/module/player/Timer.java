// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.module.player;

import today.getbypass.module.Category;
import today.getbypass.module.Module;

public final class Timer extends Module
{
    @Override
    public void onUpdate() {
        if (this.isToggled()) {
            this.mc.timer.timerSpeed = 1.2f;
        }
    }
    
    @Override
    public void onDisable() {
        this.mc.timer.timerSpeed = 1.0f;
        super.onDisable();
    }
    
    public Timer() {
        super("Timer", 0, "Changes game speed", Category.PLAYER);
    }
}
