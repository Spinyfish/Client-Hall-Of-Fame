// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.module.player;

import today.getbypass.module.Category;
import today.getbypass.module.Module;

public class AutoRespawn extends Module
{
    public AutoRespawn() {
        super("AutoRespawn", 0, "Autom respawn on death", Category.PLAYER);
    }
    
    @Override
    public void onUpdate() {
        if (this.isToggled() && this.mc.thePlayer.isDead) {
            this.mc.thePlayer.respawnPlayer();
        }
    }
}
