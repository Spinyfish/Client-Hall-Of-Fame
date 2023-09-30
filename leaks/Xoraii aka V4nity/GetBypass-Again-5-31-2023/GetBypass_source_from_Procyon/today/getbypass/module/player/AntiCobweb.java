// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.module.player;

import today.getbypass.module.Category;
import today.getbypass.module.Module;

public class AntiCobweb extends Module
{
    public AntiCobweb() {
        super("AntiCobweb", 0, "No slow in cobweb", Category.PLAYER);
    }
    
    @Override
    public void onUpdate() {
        if (this.isToggled()) {
            this.mc.thePlayer.isInWeb = false;
        }
    }
}
