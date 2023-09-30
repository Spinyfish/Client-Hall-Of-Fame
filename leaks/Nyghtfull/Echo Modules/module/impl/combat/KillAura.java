package dev.echo.module.impl.combat;

import dev.echo.Echo;
import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.game.TickEvent;
import dev.echo.listener.event.impl.input.ClickEvent;
import dev.echo.listener.event.impl.player.*;
import dev.echo.listener.event.impl.render.Render3DEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.impl.movement.Scaffold;
import dev.echo.module.impl.player.Breaker;
import dev.echo.module.impl.render.HUDMod;
import dev.echo.module.settings.impl.BooleanSetting;
import dev.echo.module.settings.impl.ModeSetting;
import dev.echo.module.settings.impl.MultipleBoolSetting;
import dev.echo.module.settings.impl.NumberSetting;
import dev.echo.utils.animations.Animation;
import dev.echo.utils.animations.Direction;
import dev.echo.utils.animations.impl.DecelerateAnimation;
import dev.echo.utils.misc.MathUtils;
import dev.echo.utils.player.RotationUtils;
import dev.echo.utils.player.rotations.KillauraRotationUtil;
import dev.echo.utils.render.ColorUtil;
import dev.echo.utils.render.RenderUtil;
import dev.echo.utils.time.TimerUtil;
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
import org.apache.commons.lang3.RandomUtils;

import java.awt.*;
import java.security.SecureRandom;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public final class KillAura extends Module {

    public static ModeSetting attackMode = new ModeSetting("Attack Mode", "Single", "Single", "Switch", "Multi"),
            blockMode = new ModeSetting("Blocking Mode", "Vanilla", "None", "Vanilla", "Watchdog", "PostAttack", "BlocksMC", "Legit", "Vulcan"),
            rotationMode = new ModeSetting("Rotation Mode", "Normal", "None", "Smooth", "Normal"),
            sortingMode = new ModeSetting("Sorting Mode", "Health", "Health", "Range", "HurtTime", "Armor"),
            attackTiming = new ModeSetting("Attack Timing", "Legit", "Legit", "Pre", "Post", "All"),
            blockTiming = new ModeSetting("Block Timing", "Pre", "Pre", "Post", "All"),
            randomMode = new ModeSetting("Random Mode", "None", "None", "Normal", "Doubled", "Gaussian", "Augustus", "Multipoints");
    public static BooleanSetting fakeAutoblock = new BooleanSetting("Fake AutoBlock", false);
    public static BooleanSetting smartattack = new BooleanSetting("Smart Attack", false); // basically smart hitselect
    public NumberSetting maxTargets = new NumberSetting("Max Targets", 2, 10, 2, 1);
    public NumberSetting minAPS = new NumberSetting("Min APS", 9, 20, 1, 0.1),
            maxAPS = new NumberSetting("Max APS", 12, 20, 1, 0.1);

    public NumberSetting minTurnSpeed = new NumberSetting("Min Turn Speed", 120, 180, 10, 10);
    public NumberSetting maxTurnSpeed = new NumberSetting("Max Turn Speed", 160, 180, 10, 10);
    public NumberSetting randomization = new NumberSetting("Randomization", 0, 5, 0.1, 0.1);

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
            new BooleanSetting("Move Correction", true),
            new BooleanSetting("Through Block", true),
            new BooleanSetting("Ray Trace", false)
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
        super("Kill Aura", Category.COMBAT, "Automatically hits entities for you.");
        this.maxTargets.addParent(attackMode, a -> attackMode.is("Single"));

        this.blockChance.addParent(blockMode, a -> !blockMode.is("None") && !blockMode.is("Fake"));
        this.switchDelay.addParent(attackMode, a -> attackMode.is("Switch"));

        this.silentRotations.addParent(rotationMode, a -> !rotationMode.is("None"));
        this.showRotations.addParent(rotationMode, a -> !rotationMode.is("None"));
        minTurnSpeed.addParent(rotationMode, a -> rotationMode.is("Smooth"));
        maxTurnSpeed.addParent(rotationMode, a -> rotationMode.is("Smooth"));
        this.addSettings(
                attackMode, smartattack, blockMode, fakeAutoblock, rotationMode, sortingMode, attackTiming, blockTiming, randomMode, maxTargets, blockChance, switchDelay,

                minAPS, maxAPS,
                swingRange, attackRange, wallsRange, blockRange, rotationRange,

                silentRotations, showRotations,
                minTurnSpeed, maxTurnSpeed,
                randomization,

                targets, bypass, features, renders,
                reverseSorting
        );

        this.list = new ArrayList<>();
    }

    @Override
    public void onEnable() {

        attackTimer.reset();

        yaw = mc.thePlayer.rotationYaw;
        pitch = mc.thePlayer.rotationPitch;

        lastYaw = mc.thePlayer.rotationYaw;
        lastPitch = mc.thePlayer.rotationPitch;
        super.onEnable();
    }

    @Override
    public void onDisable() {

        target = null;
        renderTarget = null;

        list.clear();

        unblock();
        if (blockMode.is("Vulcan")) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
        }
        blocking = false;
        attacking = false;

        fake = false;

        super.onDisable();
    }

    @Link
    private final Listener<TickEvent> tickEventListener = (event) -> {

        if (mc.theWorld == null || mc.thePlayer == null || !event.isPost()) {
            return;
        }

        updateTargets();

        target = !this.list.isEmpty() ? this.list.get(0) : null;

        if (target == null) {
            return;
        }
        if (target != null) {
            if (fakeAutoblock.isEnabled()) {
                fake = true;
            } else {
                fake = false;
            }
        }
        calculateRotations(target);

        mc.leftClickCounter = 0;

        int chance = (int) Math.round(100 * Math.random());
        if (chance <= blockChance.getValue()) {
            if (blockMode.is("Vulcan")) {
                if (target != null) {
                    block();
                }
            }
        }
    };

    @Link
    private final Listener<MotionEvent> motionEventListener = (event) -> {
        this.setSuffix(attackMode.getMode());

        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }
        if (target == null) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
        }

        attacking = target != null && !Echo.INSTANCE.isEnabled(Scaffold.class);

        if (mc.thePlayer.ticksExisted == 0 && features.getSetting("Auto Disable").isEnabled()) {
            this.toggle();
            return;
        }

        if (mc.currentScreen != null && features.getSetting("Ignore UIs").isEnabled()) {
            return;
        }

        target = !list.isEmpty() ? list.get(0) : null;

        if (target != null) {
            if (!Echo.INSTANCE.isEnabled(Breaker.class)) {
                runRotations(event);
            }
        }

        if (
                (attackTiming.is("Pre") && event.isPost()) ||
                        (attackTiming.is("Post") && event.isPre()) ||
                        attackTiming.is("Legit") ||
                        target == null
        ) {
            return;
        }

        runAttackLoop();
    };

    @Link
    private final Listener<ClickEvent> clickEventListener = (event) -> {
        if (mc.theWorld == null || mc.thePlayer == null || target == null) {
            return;
        }

        if (
                attackTiming.is("Pre") ||
                        attackTiming.is("Post")
        ) {
            return;
        }

        runAttackLoop();
    };

    @Link
    private final Listener<StrafeEvent> strafeEventListener = (event) -> {
        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        if (bypass.getSetting("Move Correction").isEnabled() && target != null) {
            event.setYaw(yaw);
        }
    };

    @Link
    private final Listener<JumpEvent> jumpEventListener = (event) -> {
        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        if (bypass.getSetting("Move Correction").isEnabled() && target != null) {
            event.setYaw(yaw);
        }
    };

    private void calculateRotations(EntityLivingBase target) {
        lastYaw = yaw;
        lastPitch = pitch;
        float[] prevRots = new float[]{lastYaw, lastPitch};
        float[] rotations = new float[]{0, 0};

        switch (rotationMode.getMode()) {
            case "None": {
                rotations = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
                break;
            }
            case "Smooth": {
                rotations = KillauraRotationUtil.getRotations(target, lastYaw, lastPitch);
                break;
            }
            case "Normal": {
                rotations = getGCDRotations(RotationUtils.getRotationsNormal(mc.thePlayer.getPositionVector(), target.getPositionVector()), prevRots);
                break;
            }
            default: {
                break;
            }
        }

        yaw = rotations[0];
        pitch = rotations[1];

        switch (randomMode.getMode()) {
            case "Normal": {
                yaw += (float) (Math.random() * randomization.getValue());
                pitch += (float) (Math.random() * randomization.getValue());
                break;
            }
            case "Doubled": {
                yaw += (float) (Math.random() * randomization.getValue());
                pitch += (float) (Math.random() * randomization.getValue());

                if (mc.thePlayer.ticksExisted % 3 == 0) {
                    yaw += (float) (Math.random() * randomization.getValue());
                    pitch += (float) (Math.random() * randomization.getValue());
                }

                break;
            }
            case "Gaussian": {
                yaw += (float) (ThreadLocalRandom.current().nextGaussian() * randomization.getValue());
                pitch += (float) (ThreadLocalRandom.current().nextGaussian() * randomization.getValue());
                break;
            }
            case "Augustus": {
                final float random1 = nextSecureFloat(-randomization.getValue(), randomization.getValue());
                final float random2 = nextSecureFloat(-randomization.getValue(), randomization.getValue());
                final float random3 = nextSecureFloat(-randomization.getValue(), randomization.getValue());
                final float random4 = nextSecureFloat(-randomization.getValue(), randomization.getValue());
                yaw += nextSecureFloat(Math.min(random1, random2), Math.max(random1, random2));
                pitch += nextSecureFloat(Math.min(random3, random4), Math.max(random3, random4));
                break;
            }
            case "Multipoints": {
                pitch += MathUtils.getRandomInRange(0, randomization.getValue() * 4);
                yaw += (float) (Math.random() * randomization.getValue());
                break;
            }
            default: {
                break;
            }
        }

        float speed = MathUtils.getRandomInRange(
                minTurnSpeed.getValue().floatValue(),
                maxTurnSpeed.getValue().floatValue()
        );

        yaw = KillauraRotationUtil.smoothRotation(lastYaw, yaw, speed);
        pitch = KillauraRotationUtil.smoothRotation(lastPitch, pitch, speed);

        float[] fixedRotations = RotationUtils.getFixedRotations(
                new float[]{yaw, pitch},
                new float[]{lastYaw, lastPitch}
        );

        yaw = fixedRotations[0];
        pitch = fixedRotations[1];
    }

    private void runRotations(MotionEvent event) {
        if (silentRotations.isEnabled()) {
            event.setYaw(yaw);
            event.setPitch(pitch);
        } else {
            mc.thePlayer.rotationYaw = yaw;
            mc.thePlayer.rotationPitch = pitch;
        }

        if (showRotations.isEnabled()) {
            RotationUtils.setVisualRotations(yaw, pitch);
        }
    }

    @Link
    public Listener<SlowDownEvent> slowdownEventListener = (event) -> {
        if (blockMode.getMode().equals("Watchdog")) {
            if (mc.thePlayer.hurtTime > 1) {
                event.setCancelled(true);
            }
        }
    };

    private void runBlocking() {
        int chance = (int) Math.round(100 * Math.random());

        if (target != null && !blockMode.is("None")) {
            unblock();
        }

        if (chance <= blockChance.getValue()) {
            switch (blockMode.getMode()) {
                case "Vanilla": {
                    block();
                    break;
                }
                case "Watchdog": {
                    if (mc.thePlayer.hurtTime > 1) {
                        block();
                    } else {
                        unblock();
                    }
                    break;
                }
                case "BlocksMC": {
                    if (mc.thePlayer.ticksExisted % 3 == 0) {
                        block();
                    } else {
                        unblock();
                    }
                    break;
                }
                case "Legit": {
                    if (mc.thePlayer.prevSwingProgress < mc.thePlayer.swingProgress) {
                        if (mc.thePlayer.ticksExisted % 15 == 0) {
                            KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
                        }
                    }
                }
                break;
                default:
                    break;
            }
        }
    }


    private void runAttackLoop() {
        if (attacking) {
            if (attackTimer.hasTimeElapsed((1000 / currentAPS) +
                    (!smartattack.isEnabled() || mc.thePlayer.hurtTime != 0 || (target instanceof EntityLivingBase && target.hurtTime <= 3) ? 0 : MathUtils.getRandomInRange(490, 525)))) {

                // Attack
                switch (attackMode.getMode()) {
                    case "Single":
                        target = !list.isEmpty() ? list.get(0) : null;

                        if (target != null) {
                            attack(target);
                        }

                        break;
                    case "Switch":
                        if (list.size() >= targetIndex) {
                            targetIndex = 0;
                        }

                        target = !list.isEmpty() ? list.get(targetIndex) : null;

                        if (target != null) {
                            attack(target);
                        }

                        if (switchTimer.hasTimeElapsed(switchDelay.getValue())) {
                            targetIndex++;
                            switchTimer.reset();
                        }

                        break;
                    case "Multi":
                        target = !list.isEmpty() ? list.get(0) : null;
                        list.forEach(this::attack);
                        break;
                }

                currentAPS = RandomUtils.nextDouble(
                        minAPS.getValue(),
                        maxAPS.getValue() + 1
                );

                attackTimer.reset();
            }
        }
    }


    private void attack(EntityLivingBase entity) {
        mc.thePlayer.swingItem();

        if (mc.thePlayer.getDistanceToEntity(entity) <= blockRange.getValue() && blockTiming.is("Pre")) {
            runBlocking();
        }
        if (!mc.thePlayer.canEntityBeSeen(entity) && mc.thePlayer.getDistanceToEntity(entity) > wallsRange.getValue()) {
            return;
        }

        if (bypass.getSetting("Ray Trace").isEnabled() && !RotationUtils.isMouseOver(yaw, pitch, target, attackRange.getValue().floatValue())) {
            return;
        }

        if (mc.thePlayer.getDistanceToEntity(entity) <= attackRange.getValue()) {
            Echo.INSTANCE.getEventProtocol().handleEvent(new AttackEvent(target));
            mc.playerController.attackEntity(mc.thePlayer, entity);
        }

        if (mc.thePlayer.getDistanceToEntity(entity) <= blockRange.getValue() && blockTiming.is("Post")) {
            runBlocking();
        }

    }

    private void updateTargets() {
        this.list = mc.theWorld.loadedEntityList

                .stream()

                .filter(entity -> entity instanceof EntityLivingBase)

                .map(entity -> (EntityLivingBase) entity)

                .filter(livingEntity -> {

                    if (!this.targets.getSetting("Players").isEnabled() && livingEntity instanceof EntityPlayer) {
                        return false;
                    }

                    if (
                            !this.targets.getSetting("Animals").isEnabled() &&
                                    (
                                            livingEntity instanceof EntityAnimal ||
                                                    livingEntity instanceof EntitySquid ||
                                                    livingEntity instanceof EntityVillager
                                    )
                    ) {
                        return false;
                    }

                    if (
                            !this.targets.getSetting("Monsters").isEnabled() &&
                                    (
                                            livingEntity instanceof EntityMob ||
                                                    livingEntity instanceof EntitySlime
                                    )
                    ) {
                        return false;
                    }

                    if (!this.targets.getSetting("Invisible").isEnabled() && livingEntity.isInvisible()) {
                        return false;
                    }

                    if (livingEntity instanceof EntityArmorStand || livingEntity.deathTime != 0 || livingEntity.isDead) {
                        return false;
                    }

                    return livingEntity != mc.thePlayer;
                })

                .filter(entity -> mc.thePlayer.getDistanceToEntity(entity) <= rotationRange.getValue())

                .sorted(Comparator.comparingDouble(entity -> {
                    switch (sortingMode.getMode()) {
                        case "Health":
                            return entity.getHealth();
                        case "Range":
                            return mc.thePlayer.getDistanceToEntity(entity);
                        case "HurtTime":
                            return entity.getHurtTime();
                        case "Armor":
                            return entity.getTotalArmorValue();
                        default:
                            return -1;
                    }
                }))

                .collect(Collectors.toList());

        if (this.reverseSorting.isEnabled()) {
            Collections.reverse(list);
        }

    }

    private void block() {
        if (!canBlock()) {
            return;
        }

        if (!blocking) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
            blocking = true;
        }
    }

    private void unblock() {
        if (canBlock()) {
            if (blocking) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                blocking = false;
            }
        }
    }

    private boolean canBlock() {
        return mc.thePlayer.inventory.getCurrentItem() != null && mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword;
    }

    private final Animation auraESPAnim = new DecelerateAnimation(300, 1);

    @Link
    private final Listener<Render3DEvent> render3DEventListener = (event) -> {
        auraESPAnim.setDirection(target != null ? Direction.FORWARDS : Direction.BACKWARDS);

        if (target != null) {
            renderTarget = target;
        } else {
            renderTarget = null;
        }

        if (auraESPAnim.finished(Direction.BACKWARDS)) {
            renderTarget = null;
        }

        Color color = HUDMod.getClientColors().getFirst();

        if (renderTarget != null) {
            if (renders.getSetting("Box").isEnabled()) {

                RenderUtil.renderBoundingBox(renderTarget, ColorUtil.interpolateColorsBackAndForth(5, 0, HUDMod.getClientColors().getFirst(), HUDMod.getClientColors().getSecond(), false), 0.7f, false);
            }

            if (renders.getSetting("Circle").isEnabled()) {
                RenderUtil.drawCircle(renderTarget, event.getTicks(), .65f, ColorUtil.interpolateColorsBackAndForth(5, 0, HUDMod.getClientColors().getFirst(), HUDMod.getClientColors().getSecond(), false).getRGB(), 7);
            }
            if (renders.getSetting("Tracer").isEnabled()) {
                RenderUtil.drawTracerLine(renderTarget, 4f, Color.BLACK, auraESPAnim.getOutput().floatValue());
                RenderUtil.drawTracerLine(renderTarget, 2.5f, color, auraESPAnim.getOutput().floatValue());
            }
        }
    };

    public static float nextSecureFloat(final double origin, final double bound) {
        if (origin == bound) {
            return (float) origin;
        }
        final SecureRandom secureRandom = new SecureRandom();
        final float difference = (float) (bound - origin);
        return (float) (origin + secureRandom.nextFloat() * difference);
    }

    private double Sens() {
        final float sens = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        final float pow = sens * sens * sens * 8.0F;
        return pow * 0.15D;
    }

    private float[] getGCDRotations(final float[] currentRots, final float[] prevRots) {
        final float yawDif = currentRots[0] - prevRots[0];
        final float pitchDif = currentRots[1] - prevRots[1];
        final double gcd = Sens();

        currentRots[0] -= yawDif % gcd;
        currentRots[1] -= pitchDif % gcd;
        return currentRots;
    }
}