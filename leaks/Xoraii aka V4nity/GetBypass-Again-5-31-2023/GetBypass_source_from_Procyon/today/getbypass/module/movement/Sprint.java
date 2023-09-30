// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.module.movement;

import net.minecraft.client.settings.KeyBinding;
import today.getbypass.module.Category;
import today.getbypass.module.Module;

public class Sprint extends Module
{
    public Sprint() {
        super("Sprint", 0, "Automatically sprints for you", Category.MOVEMENT);
    }
    
    @Override
    public void onUpdate() {
        if (this.isToggled()) {
            KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSprint.getKeyCode(), true);
            super.onDisable();
        }
    }
    
    @Override
    public void onDisable() {
        KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSprint.getKeyCode(), false);
        super.onDisable();
    }
}
