package club.pulsive.impl.module.impl.movement;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.impl.event.player.PlayerMotionEvent;
import club.pulsive.impl.event.player.PlayerMoveEvent;
import club.pulsive.impl.event.player.PlayerStrafeEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.util.math.apache.ApacheMath;
import club.pulsive.impl.util.network.PacketUtil;
import club.pulsive.impl.util.player.MovementUtil;
import club.pulsive.impl.util.player.PlayerUtil;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;
import org.apache.commons.lang3.RandomUtils;

@ModuleInfo(name = "LongJump",  description = "Long jump module", category = Category.MOVEMENT)
public class LongJump extends Module {


    private double lastDistance, moveSpeed;
    private int ticks;
    private boolean wasOnGround;

    @EventHandler
    private final Listener<PlayerMotionEvent> playerMotionEventListener = event -> {
        if(wasOnGround){
            ticks++;
        }

        if(MovementUtil.isMathGround())
            event.setGround(false);

        if(MovementUtil.isMathGround() && wasOnGround && ticks > 0){
            this.toggle();
        }else if(mc.thePlayer.onGround){
            wasOnGround = true;
        }

        if(event.isPre()){
            lastDistance = MovementUtil.getLastDistance();
        }
    };

    @EventHandler
    private final Listener<PlayerMoveEvent> playerMoveEventListener = event -> {
        if(MovementUtil.isMoving()){
            double value = 1;
            for (int i = 0; i < RandomUtils.nextInt(4, 7); i++) {
                value *= ApacheMath.random();
            }
            int stage = 0;

//            if(wasOnGround && MovementUtil.isMathGround())
//                moveSpeed = MovementUtil.getBaseMoveSpeed() * 10;

            if (mc.thePlayer.onGround) {
                this.moveSpeed = MovementUtil.getBaseMoveSpeed() * 2.149D;
                event.setY(mc.thePlayer.motionY = 0.42F + (mc.thePlayer.isPotionActive(Potion.jump)
                        ? MovementUtil.getJumpBoostMotion() * 1.1 : 0));
            }

            if (!mc.thePlayer.onGround) {
                this.moveSpeed = this.lastDistance * 0.915;
            }

            if (mc.thePlayer.fallDistance > 0 && mc.thePlayer.fallDistance < 0.3 && !mc.thePlayer.isPotionActive(Potion.jump)) {
                mc.thePlayer.motionY = 1E-3 + value *
                        (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 1E-6 : 1E-4);
            }

           MovementUtil.setSpeed(event, ApacheMath.max(MovementUtil.getBaseMoveSpeed(), this.moveSpeed));
        }
    };


    @Override
    public void onEnable() {
        super.onEnable();
        lastDistance = MovementUtil.getLastDistance();
        wasOnGround = false;
        ticks = 0;
        moveSpeed = MovementUtil.getBaseMoveSpeed() * 5;
    }
}
