// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.module.combat;

import org.lwjgl.input.Mouse;
import today.getbypass.module.Category;
import today.getbypass.module.Module;

public final class AutoClicker extends Module
{
    public AutoClicker() {
        super("AutoClicker", 0, "Click for you", Category.COMBAT);
    }
    
    @Override
    public void onUpdate() {
        if (this.isToggled() && Mouse.isButtonDown(0)) {
            this.mc.clickMouse();
        }
    }
}
