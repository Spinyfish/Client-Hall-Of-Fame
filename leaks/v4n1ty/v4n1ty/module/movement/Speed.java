package v4n1ty.module.movement;

import de.Hero.settings.Setting;
import net.minecraft.potion.Potion;
import org.lwjgl.input.Keyboard;
import v4n1ty.V4n1ty;
import v4n1ty.module.Category;
import v4n1ty.module.Module;
import v4n1ty.utils.player.MoveUtil;

import java.util.ArrayList;

public class Speed extends Module {
    public Speed() {
        super("Speed", Keyboard.KEY_F, Category.MOVEMENT);
    }

    @Override
    public void setup() {
        ArrayList<String> options = new ArrayList<>();
        options.add("Hypixel");
        options.add("Vulcan");
        options.add("NCP");
        V4n1ty.settingsManager.rSetting(new Setting("Mode", this, "Hypixel", options));
    }

    private int ticks;

    public void onUpdate() {
        if (this.isToggled()){
            if(V4n1ty.settingsManager.getSettingByName("Mode").getValString().equalsIgnoreCase("Hypixel")){
                if(!MoveUtil.isMoving()) {
                    mc.timer.timerSpeed = 1;
                } else {
                    mc.timer.timerSpeed = (float) (1 + Math.random() / 30);
                    if (mc.thePlayer.onGround) {
                        ticks = 0;
                        mc.thePlayer.jump();
                        MoveUtil.strafe(0.3999F);
                    } else {
                        ticks++;
                        mc.thePlayer.motionY -= 0.0008;
                        if (ticks == 1) {
                            mc.thePlayer.motionY -= 0.002;
                        }

                        if (ticks == 8) {
                            mc.thePlayer.motionY -= 0.003;
                        }
                    }
                }
            }

            if(V4n1ty.settingsManager.getSettingByName("Mode").getValString().equalsIgnoreCase("Vulcan")){
                if(mc.thePlayer.onGround) {
                    ticks = 0;
                    MoveUtil.strafe((float) (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.6 : 0.4));
                    mc.thePlayer.jump();
                } else {
                    ticks++;
                }

                switch(ticks) {
                    case 1:
                    case 2:
                        MoveUtil.strafe(MoveUtil.getSpeed());
                        break;

                    case 6:
                        mc.thePlayer.motionY -= 0.035;
                }
            }

            if(V4n1ty.settingsManager.getSettingByName("Mode").getValString().equalsIgnoreCase("NCP")){
                if(mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                    MoveUtil.strafe(0.485F);
                } else {
                    MoveUtil.strafe(MoveUtil.getSpeed());
                }
            }
        }
    }
}
