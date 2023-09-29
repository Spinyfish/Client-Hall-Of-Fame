// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.module.player;

import today.getbypass.module.Category;
import today.getbypass.utils.Timer2;
import today.getbypass.module.Module;

public class AntiAFK extends Module
{
    public Timer2 time;
    
    public AntiAFK() {
        super("AntiAFK", 0, "Anti kick AFK", Category.PLAYER);
        this.time = new Timer2();
    }
    
    @Override
    public void onUpdate() {
        if (this.isToggled() && Timer2.hasReached(10000L)) {
            this.mc.thePlayer.jump();
            Timer2.reset();
        }
    }
}
