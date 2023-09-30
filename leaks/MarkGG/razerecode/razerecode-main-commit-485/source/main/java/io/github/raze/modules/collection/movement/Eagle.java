package io.github.raze.modules.collection.movement;

import io.github.raze.events.collection.motion.EventMotion;
import io.github.raze.events.system.BaseEvent;
import io.github.raze.events.system.SubscribeEvent;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.modules.system.information.ModuleCategory;
import net.minecraft.block.BlockAir;
import net.minecraft.util.BlockPosition;

public class Eagle extends BaseModule {


    public Eagle() {
        super("Eagle", "Makes you sneak bridge.", ModuleCategory.MOVEMENT);
    }

    @SubscribeEvent
    private void onMotion(EventMotion eventMotion) {
        if (eventMotion.getState() == BaseEvent.State.PRE) {
            mc.gameSettings.keyBindSneak.pressed = mc.theWorld.getBlockState(
                    new BlockPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY - 1.0,
                            mc.thePlayer.posZ
                    )
            ).getBlock() instanceof BlockAir &&
                    mc.thePlayer.onGround;
        }
    }

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindSneak.pressed = false;
        super.onDisable();
    }
}