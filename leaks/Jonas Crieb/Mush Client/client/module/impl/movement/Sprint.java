package client.module.impl.movement;

import client.Client;
import client.event.Listener;
import client.event.annotations.EventLink;

import client.event.impl.motion.MotionEvent;
import client.module.Category;
import client.module.Module;

import client.module.ModuleInfo;
import client.module.impl.player.Scaffold;
import client.util.player.MoveUtil;

@ModuleInfo(name = "Sprint", description = "", category = Category.MOVEMENT, autoEnabled = true)
public class Sprint extends Module {
    @EventLink
    public final Listener<MotionEvent> onMotion = event -> {

        if ((mc.thePlayer.moveForward != 0.0F || mc.thePlayer.moveStrafing != 0.0F) && !mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isSneaking() && !mc.thePlayer.isUsingItem())
            if (((Scaffold)Client.INSTANCE.getModuleManager().get(Scaffold.class)).isEnabled()) {
                mc.thePlayer.setSprinting(false);
            } else {

                mc.thePlayer.setSprinting(true);
            }
    };
}
