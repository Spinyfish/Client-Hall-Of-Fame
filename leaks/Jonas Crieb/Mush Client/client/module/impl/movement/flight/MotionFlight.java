package client.module.impl.movement.flight;

import client.event.Listener;
import client.event.annotations.EventLink;
import client.event.impl.motion.MotionEvent;
import client.event.impl.motion.StrafeEvent;
import client.module.impl.movement.Flight;
import client.util.chat.ChatUtil;
import client.util.player.MoveUtil;
import client.util.player.PlayerUtil;
import client.value.Mode;
import client.value.impl.NumberValue;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovementInput;

public class MotionFlight extends Mode<Flight> {

    private final NumberValue speed = new NumberValue("Speed", this, 1, 0.1, 9.5, 0.1);
int jew;
    public MotionFlight(String name, Flight parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<MotionEvent> onMotion = event -> {
        if (!MoveUtil.isMoving()) {
            MoveUtil.stop();
        }

        if (mc.thePlayer.onGround){

        }else {

                //  mc.thePlayer.chasingPosY = mc.thePlayer.lastReportedPosY;
              //  mc.thePlayer.prevPosY = mc.thePlayer.renderOffsetY;
                //     mc.thePlayer.prevChasingPosY = mc.thePlayer.lastReportedPosY;
            if (mc.thePlayer.ticksExisted % 4 == 0) {

                mc.thePlayer.motionY = -0.23f;
            } else{
                  mc.thePlayer.chasingPosY = mc.thePlayer.lastReportedPosY;
                  mc.thePlayer.prevPosY = mc.thePlayer.renderOffsetY;
                mc.timer.timerSpeed = 1.3f;
                mc.thePlayer.motionY = +0.001f;
            }
                //mc.thePlayer.motionY = mc.gameSettings.keyBindJump.isKeyDown() ? 1F : mc.gameSettings.keyBindSneak.isKeyDown() ? -1F : 0.0;

        }


        /*
        if (mc.thePlayer.onGround){
            mc.thePlayer.jump();

        }else {
            MoveUtil.strafe(1.89999);
            mc.thePlayer.motionY = mc.gameSettings.keyBindJump.isKeyDown() ? 1F : mc.gameSettings.keyBindSneak.isKeyDown() ? -1F : 0.0;

        }

         */

   //     ChatUtil.display(mc.thePlayer.prevPosY = mc.thePlayer.renderOffsetY);


    };
}
