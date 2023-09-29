package de.verschwiegener.atero.module.modules.combat;
import org.lwjgl.input.Keyboard;
import de.verschwiegener.atero.module.Category;
import de.verschwiegener.atero.module.Module;



public class AutoClicker extends Module {


    public AutoClicker() {
        super("AutoClicker", "AutoClicker", Keyboard.KEY_NONE, Category.Combat);
    }

    public void onEnable() {

        super.onEnable();
    }

    public void onDisable() {

        super.onDisable();
    }
    public void onUpdate() {
        if (this.isEnabled()) {
              if(mc.thePlayer.ticksExisted % 2 == 0) {
                  mc.clickMouse();
              }
        }
    }
}



