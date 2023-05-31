// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.module.player;

import today.getbypass.module.Category;
import today.getbypass.module.Module;

public class FastPlace extends Module
{
    public FastPlace() {
        super("FastPlace", 0, "Quickly place blocks", Category.PLAYER);
    }
    
    @Override
    public void onUpdate() {
        if (this.isToggled()) {
            this.mc.rightClickDelayTimer = 0;
        }
    }
}
