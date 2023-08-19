package cc.swift.module.impl.movement;

import cc.swift.events.BlockAABBEvent;
import cc.swift.events.UpdateWalkingPlayerEvent;
import cc.swift.module.Module;
import cc.swift.util.ChatUtil;
import cc.swift.util.player.MovementUtil;
import cc.swift.value.impl.ModeValue;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import net.minecraft.block.BlockLiquid;
import net.minecraft.util.AxisAlignedBB;

public class JesusModule extends Module {

    public final ModeValue<Mode> mode = new ModeValue<>("Mode", Mode.values());

    public JesusModule(){
        super("Jesus",Category.MOVEMENT);
        registerValues(mode);
    }

    boolean inWater;

    @Handler
    public final Listener<BlockAABBEvent> blockAABBEventListener = event -> {
        switch(mode.getValue()) {
            case NORMAL:
            if (event.getBlock() instanceof BlockLiquid) {

                final double x = event.getBlockPos().getX(), y = event.getBlockPos().getY(), z = event.getBlockPos().getZ();

                event.setBoundingBox(AxisAlignedBB.fromBounds(-15, -1, -15, 15, 1, 15).offset(x, y, z));
            }
            break;
        }
    };

    @Handler
    public final Listener<UpdateWalkingPlayerEvent> updateEventListener = event -> {
        switch(mode.getValue()){
            case HYPIXEL:
                if(mc.thePlayer.isInWater() && !mc.gameSettings.keyBindJump.isKeyDown() && !mc.thePlayer.isSneaking()){
                    mc.thePlayer.motionY = 0.1;
                    if(MovementUtil.isMoving())
                        MovementUtil.setSpeed(0.1);
                }
                break;
            case HYPIXELGROUND:
                if(mc.thePlayer.isInWater() && mc.thePlayer.onGround && MovementUtil.isMoving()){
                    MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed() * 0.87);
                }
        }
    };


    enum Mode {
        NORMAL, HYPIXEL, HYPIXELGROUND;
    }

}
