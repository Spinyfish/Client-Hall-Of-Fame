package dev.tenacity.module.impl.combat;

import de.florianmichael.viamcp.fixes.AttackOrder;
import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.game.world.TickEvent;
import dev.tenacity.event.impl.game.world.WorldEvent;
import dev.tenacity.event.impl.player.input.AttackEvent;
import dev.tenacity.event.impl.player.input.LegitClickEvent;
import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.misc.MathUtils;
import dev.tenacity.utils.player.RotationUtils;
import dev.tenacity.utils.player.rotations.KillauraRotationUtil;
import dev.tenacity.utils.time.TimerUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Comparator;
import java.util.Random;

public final class AutoPlay extends Module {


    private final NumberSetting minCPS = new NumberSetting("Min CPS", 10, 20, 1, 1);
    private final NumberSetting maxCPS = new NumberSetting("Max CPS", 10, 20, 1, 1);
    private final NumberSetting reach = new NumberSetting("Reach", 4, 6, 3, 0.1);

    private final NumberSetting strafeDelay = new NumberSetting("Strafe Delay", 1000, 5000, 0, 10);
    private final NumberSetting timeStrafing = new NumberSetting("Time Strafing", 1000, 5000, 0, 10);

    private EntityLivingBase target;

    private final TimerUtil attackTimer = new TimerUtil();
    private final TimerUtil strafeTimer = new TimerUtil();
    private double currentCPS = 10;


    public float[] rotations, lastRotations;

    public AutoPlay() {
        super("AutoPlay", Category.COMBAT, "Automatically finds the nearest player and attempts to kill them");
        this.addSettings(minCPS, maxCPS, reach, strafeDelay, timeStrafing);
    }

    /* Minemen Shit */
    private static int minemen_ticks = 0;


    @Override
    public void onEnable() {
        this.rotations = new float[] {
                mc.thePlayer.rotationYaw,
                mc.thePlayer.rotationPitch
        };

        this.lastRotations = new float[] {
                mc.thePlayer.rotationYaw,
                mc.thePlayer.rotationPitch
        };

        super.onEnable();
    }

    @Override
    public void onTickEvent(TickEvent event) {
        ++minemen_ticks;
        super.onTickEvent(event);
    }

    @Override
    public void onMotionEvent(MotionEvent event) {

        this.target = mc.theWorld.getLoadedEntityList().stream()
                .filter(entity -> entity instanceof EntityPlayer && entity != mc.thePlayer).map(entity -> (EntityPlayer) entity)
                .min(Comparator.comparingDouble(entity -> mc.thePlayer.getDistanceToEntity(entity))).orElse(null);

        if (this.target == null) {
            return;
        }

        if (minCPS.getValue() > maxCPS.getValue()) {
            minCPS.setValue(minCPS.getValue() - 1);
        }

        if (mc.thePlayer.hurtTime == 0) {
            mc.gameSettings.keyBindForward.pressed = mc.thePlayer.getDistanceToEntity(target) > reach.getValue();
        } else {
            mc.gameSettings.keyBindForward.pressed = false;
            mc.gameSettings.keyBindBack.pressed = true;
        }

        mc.gameSettings.keyBindJump.pressed = mc.thePlayer.isCollidedHorizontally || mc.thePlayer.isInWater();

        double delay = strafeDelay.getValue();
        double strafing = timeStrafing.getValue();

        if (strafeTimer.hasTimeElapsed(delay)) {
            boolean direction = new Random().nextBoolean();

            if (!strafeTimer.hasTimeElapsed(delay + strafing)) {
                if (direction) {
                    mc.gameSettings.keyBindLeft.pressed = false;
                    mc.gameSettings.keyBindRight.pressed = true;
                } else {
                    mc.gameSettings.keyBindLeft.pressed = true;
                    mc.gameSettings.keyBindRight.pressed = false;
                }
            } else {
                strafeTimer.reset();
            }
        }

        lastRotations = rotations;

        final float[] rotations = KillauraRotationUtil.getRotations(
                target,
                event.getYaw(),
                event.getPitch()
        );

        float[] fixedRotations = RotationUtils.getFixedRotations(
                rotations,
                lastRotations
        );

        mc.thePlayer.rotationYaw = fixedRotations[0];
        mc.thePlayer.rotationPitch = fixedRotations[1];
    }

    @Override
    public void onLegitClickEvent(LegitClickEvent event) {

        if (attackTimer.hasTimeElapsed(1000 / currentCPS)) {
            AttackEvent attackEvent = new AttackEvent(target);
            Tenacity.INSTANCE.getEventProtocol().handleEvent(attackEvent);

            if (!attackEvent.isCancelled() && mc.thePlayer.getDistanceToEntity(target) <= (minemen_ticks % 2 == 0 ? 6.0 : reach.getValue())) {
                AttackOrder.sendFixedAttack(mc.thePlayer, target);
            }

            currentCPS = MathUtils.getRandomInRange(minCPS.getValue(), maxCPS.getValue());
            attackTimer.reset();
        }

        super.onLegitClickEvent(event);
    }

    @Override
    public void onDisable() {

        this.rotations = new float[] { 0.0F, 0.0F };
        this.lastRotations = new float[]  {0.0F, 0.0F };

        // Main
        mc.gameSettings.keyBindForward.pressed = false;
        mc.gameSettings.keyBindJump.pressed = false;

        // Strafe
        mc.gameSettings.keyBindLeft.pressed = false;
        mc.gameSettings.keyBindRight.pressed = false;

        target = null;

        super.onDisable();
    }

    @Override
    public void onWorldEvent(WorldEvent event) {

        if (event instanceof WorldEvent.Load) {
            minemen_ticks = 0;
        }

        super.onWorldEvent(event);
    }
}