package dev.elysium.client.mods.impl.render;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.BooleanSetting;
import dev.elysium.base.mods.settings.NumberSetting;
import dev.elysium.client.Elysium;
import dev.elysium.client.events.EventChat;
import dev.elysium.client.events.EventUpdate;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class Freelook extends Mod {
    public NumberSetting sensX = new NumberSetting("Sensitivity X",0.2,2,1,0.05,this);
    public NumberSetting sensY = new NumberSetting("Sensitivity Y",0.2,2,1,0.05,this);
    public BooleanSetting invertX = new BooleanSetting("Invert X",false, this);
    public BooleanSetting invertY = new BooleanSetting("Invert Y",false, this);
    public BooleanSetting pitchLimit = new BooleanSetting("Pitch limit",true, this);

    public Freelook() {
        super("Freelook","360 view around your player", Category.RENDER);
    }

    public static float xRot = 0;
    public static float yRot = 0;

    public static boolean isFreelooking = true;

    public void onEnable() {
        xRot = mc.thePlayer.rotationYaw + (mc.gameSettings.thirdPersonView > 1 ? 0 : 180);
        yRot = mc.thePlayer.rotationPitch * (mc.gameSettings.thirdPersonView > 1 ? -1 : 1);
        mc.gameSettings.thirdPersonView = 1;
    }

    public void updateRot(float x, float y) {
        xRot += (x * (invertX.isEnabled() ? -1 : 1) * sensX.getValue());
        yRot += (y * (invertY.isEnabled() ? -1 : 1) * sensY.getValue());
        if(pitchLimit.isEnabled()) {
            if(yRot > 90) yRot = 90;
            if(yRot < -90) yRot = -90;
        }
    }

    public void onDisable() {
        mc.gameSettings.thirdPersonView = 0;
    }

    @EventTarget
    public void onEvent(EventUpdate e) {
        //isFreelooking = false;
        if(!Keyboard.isKeyDown(this.getKey()))
            this.toggle();
    }
}
