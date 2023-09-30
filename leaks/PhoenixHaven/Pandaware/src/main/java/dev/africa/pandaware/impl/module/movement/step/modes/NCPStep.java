package dev.africa.pandaware.impl.module.movement.step.modes;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.event.player.StepEvent;
import dev.africa.pandaware.impl.module.movement.speed.SpeedModule;
import dev.africa.pandaware.impl.module.movement.step.StepModule;
import dev.africa.pandaware.utils.math.apache.ApacheMath;
import dev.africa.pandaware.utils.player.block.BlockUtils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NCPStep extends ModuleMode<StepModule> {
    private final Map<Float, float[]> offsets = new HashMap<>();
    private long lastPacket, lastStep;
    private boolean stepping;

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {

        if (this.offsets.isEmpty()) {
            this.offsets.put(1.0F, new float[]{0.41999998688698f, 0.753f});
            this.offsets.put(1.5F, new float[]{0.41999998688698f, 0.75f, 1, 1.16f});
            this.offsets.put(2.0F, new float[]{0.41999998688698f, 0.78f, 0.63f, 0.51f, 0.9f, 1.21f, 1.45f, 1.43f});
        }

        if (stepping && (System.currentTimeMillis() - lastStep) > 50L) {
            stepping = false;
            mc.timer.timerSpeed = 1f;
        }
    };

    @EventHandler
    EventCallback<StepEvent> onStep = event -> {
        if (mc.thePlayer == null || mc.theWorld == null || !mc.thePlayer.onGround ||
                Client.getInstance().getModuleManager().getByClass(SpeedModule.class).getData().isEnabled()) return;

        if (Objects.requireNonNull(BlockUtils.getBlockAtPos(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY
                - 1, mc.thePlayer.posZ))).getUnlocalizedName().contains("air")) return;

        if (!mc.thePlayer.isCollidedHorizontally || mc.thePlayer.isInWater() || mc.thePlayer.isInLava()
                || mc.thePlayer.isOnLadder()) return;

        float stepHeight;
        if ((stepHeight = this.getNeededStepHeight()) > this.getParent().getStepHeight().getValue().floatValue()) return;

        if ((stepHeight == 1 && mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer,
                mc.thePlayer.getEntityBoundingBox().offset(mc.thePlayer.motionX, 0.6, mc.thePlayer.motionZ)).isEmpty())
                || !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(
                mc.thePlayer.motionX, stepHeight + 0.01, mc.thePlayer.motionZ)).isEmpty()) return;

        float timer = parent.getStepTimer().getValue().floatValue();

        if (stepHeight >= 1) {
            timer -= ApacheMath.abs(1 - stepHeight) * (timer * .60f);
        } else {
            timer -= 0;
        }


        if (this.getParent().getDifStepTimer().getValue()) {
            if (this.getNeededStepHeight() == 1f) {
                mc.timer.timerSpeed = this.getParent().getStepTimer10().getValue().floatValue();
            } else if (this.getNeededStepHeight() == 1.5f) {
                mc.timer.timerSpeed = this.getParent().getStepTimer15().getValue().floatValue();
            } else if (this.getNeededStepHeight() == 2f) {
                mc.timer.timerSpeed = this.getParent().getStepTimer20().getValue().floatValue();
            }
        } else {
            mc.timer.timerSpeed = timer;
        }

        event.setStepHeight(stepHeight);

        if ((System.currentTimeMillis() - lastPacket) > (stepHeight > 1.00 ? 355L : 100L)) {
            offsets.forEach((a, b) -> {
                if (a == stepHeight) {
                    for (double ab : b) {
                        mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer
                                .C04PacketPlayerPosition(mc.thePlayer.posX,
                                mc.thePlayer.posY + ab, mc.thePlayer.posZ, true));
                    }
                    lastPacket = System.currentTimeMillis();
                }
            });
        }

        stepping = true;
        lastStep = System.currentTimeMillis();
    };

    private float getNeededStepHeight() {
        if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer
                .getEntityBoundingBox().offset(mc.thePlayer.motionX, 1.01, mc.thePlayer.motionZ)).size() == 0)
            return 1.0F;

        if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox()
                .offset(mc.thePlayer.motionX, 1.51, mc.thePlayer.motionZ)).size() == 0)
            return 1.5F;

        return (float) 2D;
    }

    public NCPStep(String name, StepModule parent) {
        super(name, parent);
    }

    public void onEnable() {
        this.stepping = false;
        this.offsets.clear();
    }
}
