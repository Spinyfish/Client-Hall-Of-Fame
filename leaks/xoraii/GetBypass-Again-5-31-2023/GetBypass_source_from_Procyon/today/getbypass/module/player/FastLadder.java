// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.module.player;

import org.lwjgl.input.Keyboard;
import today.getbypass.utils.Invoker;
import today.getbypass.module.Category;
import today.getbypass.module.Module;

public class FastLadder extends Module
{
    private int ticks;
    
    public FastLadder() {
        super("FastLadder", 0, "Move faster with a ladder", Category.PLAYER);
        this.ticks = 0;
    }
    
    @Override
    public void onUpdate() {
        if (this.isToggled()) {
            ++this.ticks;
            if (Invoker.isOnLadder() && Keyboard.isKeyDown(Invoker.getForwardCode())) {
                Invoker.setMotionY(0.25);
            }
            else if (Invoker.isOnLadder() && !Keyboard.isKeyDown(Invoker.getForwardCode())) {
                Invoker.setMotionY(-0.25);
            }
        }
    }
}
