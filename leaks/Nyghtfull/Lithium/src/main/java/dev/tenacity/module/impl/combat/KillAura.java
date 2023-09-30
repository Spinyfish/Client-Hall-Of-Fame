package dev.tenacity.module.impl.combat;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.game.world.TickEvent;
import dev.tenacity.event.impl.player.input.AttackEvent;
import dev.tenacity.event.impl.player.input.LegitClickEvent;
import dev.tenacity.event.impl.player.input.MoveInputEvent;
import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.event.impl.player.movement.SlowdownEvent;
import dev.tenacity.event.impl.player.movement.correction.JumpEvent;
import dev.tenacity.event.impl.player.movement.correction.StrafeEvent;
import dev.tenacity.event.impl.render.Render2DEvent;
import dev.tenacity.event.impl.render.Render3DEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.impl.movement.Scaffold;
import dev.tenacity.module.impl.render.HUDMod;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.module.settings.impl.MultipleBoolSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.misc.MathUtils;
import dev.tenacity.utils.misc.Random;
import dev.tenacity.utils.player.rotations.KillauraRotationUtil;
import dev.tenacity.utils.player.MovementUtils;
import dev.tenacity.utils.player.RotationUtils;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.time.TimerUtil;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPosition;
import net.minecraft.util.EnumFacing;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public final class KillAura extends Module {

    public static ModeSetting attackMode = new ModeSetting("Attack Mode", "Single", "Single", "Switch", "Multi"),
            blockMode = new ModeSetting("Blocking Mode", "Vanilla", "None", "Vanilla", "Watchdog", "PostAttack", "BlocksMC"),
            rotationMode = new ModeSetting("Rotation Mode", "Normal", "None", "Normal"),
            sortingMode = new ModeSetting("Sorting Mode", "Health", "Health", "Range", "HurtTime", "Armor"),
            attackTiming = new ModeSetting("Attack Timing", "Pre", "Pre", "Post", "Legit", "HvH", "All"),
            randomMode = new ModeSetting("Random Mode", "None", "None", "Normal", "Doubled", "Gaussian");

    public BooleanSetting blockInteract = new BooleanSetting("Block Interact", false);
    public static BooleanSetting fakeAutoblock = new BooleanSetting("Fake AutoBlock", false);

    public NumberSetting maxTargets = new NumberSetting("Max Targets", 2, 10, 2, 1);
    public NumberSetting minAPS = new NumberSetting("Min APS", 9, 20, 1, 0.1),
            maxAPS = new NumberSetting("Max APS", 12, 20, 1, 0.1);

    public NumberSetting minTurnSpeed = new NumberSetting("Min Turn Speed", 120, 180, 10, 10);
    public NumberSetting maxTurnSpeed = new NumberSetting("Max Turn Speed", 160, 180, 10, 10);
    public NumberSetting randomization = new NumberSetting("Randomization", 0, 3, 0.1, 0.1);

    public NumberSetting swingRange = new NumberSetting("Swing Range", 3, 10, 3, 0.1),
            attackRange = new NumberSetting("Attack Range", 3, 10, 3, 0.1),
            wallsRange = new NumberSetting("Walls Range", 0.5, 10, 0.5, 0.1),
            blockRange = new NumberSetting("Block Range", 3, 10, 3, 0.1),
            rotationRange = new NumberSetting("Rotation Range", 3, 10, 3, 0.1);

    public NumberSetting blockChance = new NumberSetting("Block Chance", 100, 100, 0, 1);
    public NumberSetting switchDelay = new NumberSetting("Switch Delay", 350, 5000, 50, 50);

    public BooleanSetting silentRotations = new BooleanSetting("Silent Rotations", true),
            showRotations = new BooleanSetting("Show Rotations", true);

    public MultipleBoolSetting targets = new MultipleBoolSetting(
            "Targets",
            new BooleanSetting("Players", true),
            new BooleanSetting("Animals", false),
            new BooleanSetting("Monsters", false),
            new BooleanSetting("Invisible", false)

    );

    public MultipleBoolSetting bypass = new MultipleBoolSetting(
            "Bypass",
            new BooleanSetting("Movement Correction", true),
            new BooleanSetting("Through Walls", true),
            new BooleanSetting("Ray Tracing", false)

    );

    public MultipleBoolSetting features = new MultipleBoolSetting(
            "Features",
            new BooleanSetting("Auto Disable", true),
            new BooleanSetting("Ignore UIs", true)
    );

    public MultipleBoolSetting renders = new MultipleBoolSetting(
            "Renders",
            new BooleanSetting("Circle", true),
            new BooleanSetting("Box", false),
            new BooleanSetting("Tracer", false)
    );

    public BooleanSetting reverseSorting = new BooleanSetting("Reverse Sorting", false);

    public static EntityLivingBase target, renderTarget;
    public List<EntityLivingBase> list;

    private int targetIndex = 0;

    private float yaw, pitch, lastYaw, lastPitch;

    public static boolean fake, blocking, attacking;

    private double currentAPS = 2;

    private final TimerUtil attackTimer = new TimerUtil();
    private final TimerUtil switchTimer = new TimerUtil();


    public KillAura() {
        super("KillAura", Category.COMBAT, "Automatically hits entities for you.");

        this.blockInteract.addParent(blockMode, a -> !blockMode.is("None") && !blockMode.is("Fake"));
        this.maxTargets.addParent(attackMode, a -> attackMode.is("Single"));

        this.blockChance.addParent(blockMode, a -> !blockMode.is("None") && !blockMode.is("Fake"));
        this.switchDelay.addParent(rotationMode, a -> rotationMode.is("Switch"));

        this.silentRotations.addParent(rotationMode, a -> !rotationMode.is("None"));
        this.showRotations.addParent(rotationMode, a -> !rotationMode.is("None"));

        this.addSettings(
                attackMode, blockMode, rotationMode, sortingMode, attackTiming, randomMode,
                blockInteract, maxTargets, blockChance, switchDelay,

                minAPS, maxAPS,
                swingRange, attackRange, wallsRange, blockRange, rotationRange,

                silentRotations, showRotations,
                minTurnSpeed, maxTurnSpeed,
                randomization,

                targets, bypass, features, fakeAutoblock, renders,
                reverseSorting
        );

        this.list = new ArrayList<>();
    }

    @Override
    public void onEnable() {
        // Code removed. Made by Liticane.

        super.onEnable();
    }

    @Override
    public void onDisable() {
        // Code removed. Made by Liticane.

        super.onDisable();
    }

    @Override
    public void onTickEvent(TickEvent event) {

        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        // Code removed. Made by Liticane.

        super.onTickEvent(event);
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        this.setSuffix(attackMode.getMode());

        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        // Code removed. Made by Liticane.

        super.onMotionEvent(event);
    }

    @Override
    public void onLegitClickEvent(LegitClickEvent event) {

        if (target == null) {
            return;
        }

        // Code removed. Made by Liticane.

        super.onLegitClickEvent(event);
    }

    @Override
    public void onRender2DEvent(Render2DEvent event) {

        if (target == null) {
            return;
        }

        // Code removed. Made by Liticane.

        super.onRender2DEvent(event);
    }

    @Override
    public void onMoveInputEvent(MoveInputEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        // Code removed. Made by Liticane.
    }

    @Override
    public void onStrafeEvent(StrafeEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        // Code removed. Made by Liticane.
    }

    @Override
    public void onJumpFixEvent(JumpEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        // Code removed. Made by Liticane.
    }

    private void calculateRotations(EntityLivingBase target) {
        lastYaw = yaw;
        lastPitch = pitch;

        float[] rotations = new float[] {0, 0};

        switch (rotationMode.getMode()) {
            case "None": {
                rotations = new float[] { mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch };
                break;
            }
            case "Normal": {
                rotations = KillauraRotationUtil.getRotations(target, lastYaw, lastPitch);
                break;
            }
            default: {
                break;
            }
        }

        yaw = rotations[0];
        pitch = rotations[1];

        switch (randomMode.getMode()) {
            // Code removed. Made by Liticane.
        }

        // Code removed. Made by Liticane.
    }

    private void runRotations(MotionEvent event) {
        if (silentRotations.isEnabled()) {
            event.setYaw(yaw);
            event.setPitch(pitch);
        } else {
            mc.thePlayer.rotationYaw = yaw;
            mc.thePlayer.rotationPitch = pitch;
        }

        if (showRotations.isEnabled())
            RotationUtils.setVisualRotations(yaw, pitch);
    }

    @Override
    public void onSlowDownEvent(SlowdownEvent event) {
        if (blockMode.getMode().equals("Watchdog")) {
            // Code removed. Made by Liticane.
        }
    }

    private void runPreBlocking() {
        boolean shouldInteract = blockInteract.isEnabled();

        int chance = (int) Math.round(100 * Math.random());

        if (target != null && !blockMode.is("None")) {
            unblock();
        }

        if (chance <= blockChance.getValue()) {
            switch (blockMode.getMode()) {
                case "Vanilla": {
                    block(true);
                    break;
                }
                case "Watchdog": {
                    // Code removed. Made by Liticane.
                    break;
                }
                case "BlocksMC": {
                    // Code removed. Made by Liticane.
                    break;
                }
                default:
                    break;
            }
        }
    }

    private void runAttackLoop() {
        if (attacking) {
            if (attackTimer.hasTimeElapsed(1000 / currentAPS)) {

                // Attack
                switch (attackMode.getMode()) {
                    case "Single":
                        target = (list.size() > 0) ? list.get(0) : null;

                        if (target != null)
                            attack(target);

                        break;
                    case "Switch":
                        if (list.size() >= targetIndex)
                            targetIndex = 0;

                        target = (list.size() > 0) ? list.get(targetIndex) : null;

                        if (target != null)
                            attack(target);

                        if (switchTimer.hasTimeElapsed(switchDelay.getValue())) {
                            targetIndex++;

                            switchTimer.reset();
                        }

                        break;
                    case "Multi":
                        target = (list.size() > 0) ? list.get(0) : null;
                        list.forEach(this::attack);
                        break;
                }

                currentAPS = Random.nextDouble(
                        minAPS.getValue(),
                        maxAPS.getValue()
                );

                attackTimer.reset();
            }
        }
    }

    private void runPostBlocking() {
        boolean shouldInteract = blockInteract.isEnabled();

        int chance = (int) Math.round(100 * Math.random());

        if (target != null && !blockMode.is("None")) {
            unblock();
        }

        if (chance <= blockChance.getValue()) {
            if (blockMode.getMode().equals("PostAttack")) {
                block(shouldInteract);
            }

        }
    }

    private void attack(EntityLivingBase entity) {
        if (mc.thePlayer.getDistanceToEntity(entity) <= swingRange.getValue() && ViaLoadingBase.getInstance().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            mc.thePlayer.swingItem();
        }

        if (mc.thePlayer.getDistanceToEntity(entity) <= blockRange.getValue()) {
            runPreBlocking();
        }

        if (!mc.thePlayer.canEntityBeSeen(entity) && mc.thePlayer.getDistanceToEntity(entity) > wallsRange.getValue())
            return;

        if (bypass.getSetting("Ray Tracing").isEnabled() && !RotationUtils.isMouseOver(yaw, pitch, target, attackRange.getValue().floatValue()))
            return;

        if (mc.thePlayer.getDistanceToEntity(entity) <= attackRange.getValue()) {
            Tenacity.INSTANCE.getEventProtocol().handleEvent(new AttackEvent(target));

            mc.playerController.attackEntity(mc.thePlayer, entity);
            mc.thePlayer.onCriticalHit(entity);
        }

        if (mc.thePlayer.getDistanceToEntity(entity) <= swingRange.getValue() && ViaLoadingBase.getInstance().getTargetVersion().isNewerThan(ProtocolVersion.v1_8)) {
            mc.thePlayer.swingItem();
        }

        if (mc.thePlayer.getDistanceToEntity(entity) <= blockRange.getValue()) {
            runPostBlocking();
        }
    }

    private void updateTargets() {
        // Code removed. Made by Liticane.
    }

    private void block(boolean interact) {
        if (!canBlock()) {
            return;
        }

        if (!blocking) {
            if (interact && target != null && mc.objectMouseOver.entityHit == target) {
                mc.playerController.interactWithEntitySendPacket(mc.thePlayer, target);
            }

            // Code removed. Made by Liticane.

            mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
            blocking = true;
        }
    }

    private void unblock() {
        if (blocking) {
            if (!mc.gameSettings.keyBindUseItem.isKeyDown()) {
                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPosition.ORIGIN, EnumFacing.DOWN));
            } else {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
            }
            blocking = false;
        }
    }

    private boolean canBlock() {
        return false; // Code removed. Made by Liticane.
    }

    @Override
    public void onRender3DEvent(Render3DEvent event) {
        // Code removed. Made by Liticane.
    }
}