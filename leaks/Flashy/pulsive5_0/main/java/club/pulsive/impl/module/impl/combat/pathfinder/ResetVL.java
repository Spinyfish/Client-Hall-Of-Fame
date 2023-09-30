package club.pulsive.impl.module.impl.combat.pathfinder;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.impl.event.player.PlayerMotionEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.util.client.Logger;

@ModuleInfo(name = "ResetVL", renderName = "Reset VL", category = Category.MISC)
public class ResetVL extends Module {
    private int amountJumped;
    private double y;

    @EventHandler
    private final Listener<PlayerMotionEvent> playerMotionEventListener = event -> {
        if(mc.thePlayer.onGround) {
            if(amountJumped <= 25) {
                mc.thePlayer.motionY = 0.11;
                amountJumped++;
            }
        }
        if(amountJumped <= 25) {
            mc.thePlayer.posY = y;
            mc.timer.timerSpeed = 2.25f;
        }else{
            mc.timer.timerSpeed = 1;
            Logger.print("Player VL Should Have Been Reset!");
            toggle();
        }
    };

    @Override
    public void onEnable() {
        super.onEnable();
        amountJumped = 0;
        y = mc.thePlayer.posY;
    }
}
