package com.alan.clients.module.impl.movement.speed;

import com.alan.clients.component.impl.player.BadPacketsComponent;
import com.alan.clients.component.impl.player.SlotComponent;
import com.alan.clients.module.impl.movement.Speed;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.input.MoveInputEvent;
import com.alan.clients.newevent.impl.motion.StrafeEvent;
import com.alan.clients.util.chat.ChatUtil;
import com.alan.clients.util.packet.PacketUtil;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.util.player.PlayerUtil;
import com.alan.clients.util.player.SlotUtil;
import com.alan.clients.value.impl.ModeValue;
import com.alan.clients.value.Mode;
import com.alan.clients.value.impl.SubMode;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public final class VulcanSpeed extends Mode<Speed> {

    ModeValue mode = new ModeValue("Mode", this)
            .add(new SubMode("BHop"))
            .add(new SubMode("Funny"))
            .setDefault("BHop");

    private int lastRightClick;

    public VulcanSpeed(String name, Speed parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<StrafeEvent> onStrafe = event -> {

        if (!MoveUtil.isMoving()) {
            return;
        }

        switch (mode.getValue().getName()) {
            case "BHop":
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
                break;

            case "Funny":
                int blockSlot = SlotUtil.findBlock();

                if (blockSlot == -1) {
                    ChatUtil.display("This speed requires a block to be in your HotBar.");
                    return;
                }

                if (!BadPacketsComponent.bad(false,true,false,false,false)) {
                    SlotComponent.setSlot(blockSlot, false);
                }

                int speed = mc.thePlayer.isPotionActive(Potion.moveSpeed) ? mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).amplifier + 1 : 0;

                if (!BadPacketsComponent.bad(false, true, false, false, false) && lastRightClick < mc.thePlayer.ticksExisted) {
                    lastRightClick = mc.thePlayer.ticksExisted + 2;
                    PacketUtil.send(new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer).down(), EnumFacing.UP.getIndex(), SlotComponent.getItemStack(), 0.0F, 1.0F, 0.0F));
                }

                switch (mc.thePlayer.offGroundTicks) {
                    case 0:
                        switch (speed) {
                            case 0:
                                MoveUtil.strafe(0.55);
                                break;
                            case 1:
                                MoveUtil.strafe(0.65 - 0.11);
                                break;
                            default:
                                MoveUtil.strafe(0.85 - 0.18);
                                break;
                        }

                        mc.thePlayer.motionY = 0.2f;
                        break;

                    case 1:
                        mc.thePlayer.motionY = MoveUtil.HEAD_HITTER_MOTION;

                        switch (speed) {
                            case 0:
                                MoveUtil.strafe(0.45 - 0.02);
                                break;
                            case 1:
                                MoveUtil.strafe(0.6 - 0.11);
                                break;
                            default:
                                MoveUtil.strafe(0.75 - 0.18);
                                break;
                        }
                        break;

                    case 2:
                        switch (speed) {
                            case 0:
                                MoveUtil.strafe(0.4 - 0.03);
                                break;
                            case 1:
                                MoveUtil.strafe(0.55 - 0.11);
                                break;
                            default:
                                MoveUtil.strafe(0.65 - 0.18);
                                break;
                        }
                        break;
                }
                break;
        }
    };


    @EventLink()
    public final Listener<MoveInputEvent> onMove = event -> {
        event.setJump(false);
    };
}
