// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.module.combat;

import today.getbypass.module.Category;
import today.getbypass.module.Module;

public class NoHitDelay extends Module
{
    public NoHitDelay() {
        super("NoHitDelay", 0, "Adds 1.7 pvp system", Category.COMBAT);
    }
    
    @Override
    public void onUpdate() {
        if (this.isToggled() && this.mc.theWorld != null && this.mc.thePlayer != null) {
            if (!this.mc.inGameHasFocus) {
                return;
            }
            this.mc.leftClickCounter = 0;
        }
    }
}
