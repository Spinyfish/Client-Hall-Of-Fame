package client.module.impl.movement.speed;

import client.event.Listener;
import client.event.annotations.EventLink;
import client.event.impl.input.MoveInputEvent;
import client.event.impl.motion.MotionEvent;
import client.event.impl.motion.StrafeEvent;
import client.module.impl.movement.Flight;
import client.module.impl.movement.Speed;
import client.util.player.MoveUtil;
import client.util.player.PlayerUtil;
import client.value.Mode;
import client.value.impl.NumberValue;
import net.minecraft.block.BlockAir;
import net.minecraft.potion.Potion;

public class Vulcan1Speed extends Mode<Speed> {

    public Vulcan1Speed(String name, Speed parent) {
        super(name, parent);
    }
    @EventLink()
    public final Listener<MoveInputEvent> onMove = event -> {
        event.setJump(false);
    };
    @EventLink()

    public final Listener<StrafeEvent> onStrafe = event -> {
        if (!MoveUtil.isMoving()) {
            return;
        }

        switch (mc.thePlayer.offGroundTicks) {
                case 0:
                    mc.thePlayer.jump();

                    if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                        MoveUtil.strafe(0.6);
                    } else {
                        MoveUtil.strafe(0.485);
                    }
                    break;

                case 9:
                    if (!(PlayerUtil.blockRelativeToPlayer(0, mc.thePlayer.motionY,
                            0) instanceof BlockAir)) {
                        MoveUtil.strafe();
                    }
                    break;

                case 2:
                case 1:
                    MoveUtil.strafe();
                    break;

                case 5:
                    mc.thePlayer.motionY = MoveUtil.predictedMotion(mc.thePlayer.motionY, 2);
                    break;
        }
    };
    @EventLink()
    public final Listener<MotionEvent> onMotion = event -> {
/*
        MoveUtil.stop();

        if (mc.thePlayer.onGround){
            MoveUtil.strafe(0.7241593749554431f);
            mc.thePlayer.motionY = 0.4141593749554431f;
        } else {
            MoveUtil.strafe(0.6189);
        }

 */




    };
}
