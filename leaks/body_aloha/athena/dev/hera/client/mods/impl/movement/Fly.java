package dev.hera.client.mods.impl.movement;

import dev.hera.client.events.impl.EventTick;
import dev.hera.client.events.types.EventTarget;
import dev.hera.client.mods.Category;
import dev.hera.client.mods.Mod;
import dev.hera.client.mods.ModInfo;
import dev.hera.client.utils.MovementUtils;
import org.lwjgl.input.Keyboard;

@ModInfo(
        name = "Fly",
        category = Category.MOVEMENT,
        description = "Pretty self explanatory",
        keyCode = Keyboard.KEY_F
)
public class Fly extends Mod {

    @Override
    public void onEnable(){

    }

    @Override
    public void onDisable(){

    }

    @EventTarget
    public void onTick(EventTick e){
        mc.thePlayer.motionX = mc.thePlayer.motionY = mc.thePlayer.motionZ = 0;
        MovementUtils.strafe(1f);
    }

}