package client.module.impl.movement.speed;

import client.event.Listener;
import client.event.annotations.EventLink;
import client.event.impl.input.MoveInputEvent;
import client.event.impl.motion.MotionEvent;
import client.event.impl.motion.StrafeEvent;
import client.module.impl.movement.Flight;
import client.module.impl.movement.Speed;
import client.util.chat.ChatUtil;
import client.util.player.MoveUtil;
import client.util.player.PlayerUtil;
import client.value.Mode;
import client.value.impl.NumberValue;
import net.minecraft.block.BlockAir;
import net.minecraft.potion.Potion;

public class MushMCSpeed extends Mode<Speed> {

    public MushMCSpeed(String name, Speed parent) {
        super(name, parent);
    }
    @EventLink()
    public final Listener<MoveInputEvent> onMove = event -> {
        event.setJump(false);
    };

    @EventLink()
    public final Listener<MotionEvent> onMotion = event -> {



        if (mc.thePlayer.onGround){
            mc.timer.timerSpeed = 1.0f;
            mc.thePlayer.jump();
        } else {

            MoveUtil.strafe(0.58);
        }






    };
}
