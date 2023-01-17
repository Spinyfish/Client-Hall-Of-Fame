package com.alan.clients.module.impl.movement;

import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.Priorities;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.StrafeEvent;
import com.alan.clients.value.impl.BooleanValue;
import com.alan.clients.util.player.MoveUtil;

/**
 * @author Auth
 * @since 20/10/2021
 */
@ModuleInfo(name = "Sprint", description = "module.movement.sprint.description", category = Category.MOVEMENT)
public class Sprint extends Module {
    private final BooleanValue legit = new BooleanValue("Legit", this, true);

    @EventLink(value = Priorities.LOW)
    public final Listener<StrafeEvent> onStrafe = event -> {

        mc.gameSettings.keyBindSprint.setPressed(true);

        if (mc.thePlayer.omniSprint && MoveUtil.isMoving() && !legit.getValue()) {
            mc.thePlayer.setSprinting(true);
        }

        mc.thePlayer.omniSprint = !legit.getValue() && MoveUtil.isMoving() && !mc.thePlayer.isCollidedHorizontally &&
                !mc.thePlayer.isSneaking() && !mc.thePlayer.isUsingItem();
    };

    @Override
    public void onDisable() {
        mc.thePlayer.setSprinting(mc.gameSettings.keyBindSprint.isKeyDown());
        mc.thePlayer.omniSprint = false;
    }
}