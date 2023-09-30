package dev.tenacity.module.impl.movement;

import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.event.impl.player.StepConfirmEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.time.TimerUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

@SuppressWarnings("unused")
public final class Step extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "NCP", "Full Jump Packets");

    private final NumberSetting height = new NumberSetting("Height", 1, 10, 1, 0.5);

    private final NumberSetting timer = new NumberSetting("Timer", 1, 2, 0.1, 0.1);

    private boolean hasStepped;

    private final TimerUtil timerUtil = new TimerUtil();

    public static boolean isStepping;

    public Step() {
        super("Step", Category.MOVEMENT, "step up blocks");
        this.addSettings(mode, height, timer);
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        setSuffix(mode.getMode());
        if(mc.thePlayer.onGround) {
            if(mc.thePlayer.stepHeight != height.getValue().floatValue()) mc.thePlayer.stepHeight = height.getValue().floatValue();
        } else {
            if(mc.thePlayer.stepHeight != 0.625f) mc.thePlayer.stepHeight = 0.625f;
        }
        if(timerUtil.hasTimeElapsed(20) && hasStepped) {
            mc.timer.timerSpeed = 1;
            hasStepped = false;
            isStepping = false;
        }
    }

    @Override
    public void onStepConfirmEvent(StepConfirmEvent event) {
        double diffY = mc.thePlayer.getEntityBoundingBox().minY - mc.thePlayer.posY;
        if(diffY > 0.625f && diffY <= 1.5f && mc.thePlayer.onGround) {
            mc.timer.timerSpeed = timer.getValue().floatValue();
            timerUtil.reset();
            hasStepped = true;
            isStepping = true;
            switch(mode.getMode()) {
                case "NCP":

                    for (double offset : new double[]{0.41999998688698, 0.7531999805212, 1.00133597911214, 1.16610926093821, 1.24918707874468, 1.24918707874468, 1.1707870772188, 1.0155550727022})
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offset, mc.thePlayer.posZ, true));
                    break;
                case "Full Jump Packets":
                    for (double offset : new double[]{0.41999998688698, 0.7531999805212, 1.00133597911214, 1.16610926093821, 1.24918707874468, 1.24918707874468, 1.1707870772188, 1.0155550727022})
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offset, mc.thePlayer.posZ, false));
                    break;
            }
        }
    }

    @Override
    public void onDisable() {
        mc.thePlayer.stepHeight = 0.625f;
        super.onDisable();
    }

}
