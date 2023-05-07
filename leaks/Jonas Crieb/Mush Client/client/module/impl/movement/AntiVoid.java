package client.module.impl.movement;

import client.event.Listener;
import client.event.annotations.EventLink;

import client.event.impl.motion.MotionEvent;


import client.module.Category;
import client.module.Module;
import client.module.ModuleInfo;


import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

@ModuleInfo(name = "AntiVoid", description = "", category = Category.EXPLOIT)
public class AntiVoid extends Module {


    @Override
    protected void onEnable() {
        super.onEnable();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
    }

    @EventLink()
    public final Listener<MotionEvent> event = event -> {
        if (mc.thePlayer.fallDistance > 2.5) {
            if (mc.thePlayer.posY < 0) {
                event.setPosY(4.42f);

            }

        }
    };
}



