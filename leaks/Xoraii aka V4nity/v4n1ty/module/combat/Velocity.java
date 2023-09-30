package v4n1ty.module.combat;

import de.Hero.settings.Setting;
import org.lwjgl.input.Keyboard;
import v4n1ty.V4n1ty;
import v4n1ty.module.Category;
import v4n1ty.module.Module;

public class Velocity extends Module {
    public Velocity() {
        super("Velocity", Keyboard.KEY_L, Category.COMBAT);
    }

    @Override
    public void setup() {
        V4n1ty.settingsManager.rSetting(new Setting("Horizontal", this, 0, 0, 100.0, true));
        V4n1ty.settingsManager.rSetting(new Setting("Vertical", this, 100.0, 0, 100.0, true));
    }

    public void onUpdate(){
        if(this.isToggled() && mc.thePlayer.hurtTime == mc.thePlayer.maxHurtTime - 1){
            double h = V4n1ty.settingsManager.getSettingByName("Horizontal").getValDouble() / 100;
            double v = V4n1ty.settingsManager.getSettingByName("Vertical").getValDouble() / 100;
            mc.thePlayer.motionX *= h;
            mc.thePlayer.motionY *= v;
            mc.thePlayer.motionZ *= h;
        }
    }
}
