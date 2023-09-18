package v4n1ty.module.movement;

import net.minecraft.potion.Potion;
import org.lwjgl.input.Keyboard;
import v4n1ty.module.Module;
import v4n1ty.module.Category;

public class Sprint extends Module {

    public Sprint() {
        super("Sprint", Keyboard.KEY_N, Category.MOVEMENT);
    }

    public void onUpdate() {
        if(this.isToggled()) {
            if(mc.thePlayer.onGround && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWater() && !mc.thePlayer.isInLava() && !mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isSneaking() && !(mc.thePlayer.getFoodStats().getFoodLevel() < 6 && !mc.thePlayer.isRiding()) && !mc.thePlayer.isPotionActive(Potion.blindness)) {
                mc.thePlayer.setSprinting(true);
            }
        }
    }

    @Override
    public void onDisable() {
        mc.thePlayer.setSprinting(false);
        super.onDisable();
    }

}
