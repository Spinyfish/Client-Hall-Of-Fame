package com.alan.clients.module.impl.combat;

import com.alan.clients.Client;
import com.alan.clients.api.Rise;
import com.alan.clients.component.impl.player.BadPacketsComponent;
import com.alan.clients.component.impl.player.GUIDetectionComponent;
import com.alan.clients.component.impl.player.RotationComponent;
import com.alan.clients.component.impl.player.SlotComponent;
import com.alan.clients.component.impl.player.rotationcomponent.MovementFix;
import com.alan.clients.component.impl.render.ESPComponent;
import com.alan.clients.component.impl.render.espcomponent.api.ESPColor;
import com.alan.clients.component.impl.render.espcomponent.impl.SigmaRing;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.module.impl.player.Scaffold;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.Priorities;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.input.ClickEvent;
import com.alan.clients.newevent.impl.motion.PostMotionEvent;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.motion.PreUpdateEvent;
import com.alan.clients.newevent.impl.other.AttackEvent;
import com.alan.clients.newevent.impl.other.WorldChangeEvent;
import com.alan.clients.newevent.impl.packet.PacketSendEvent;
import com.alan.clients.newevent.impl.render.MouseOverEvent;
import com.alan.clients.newevent.impl.render.Render2DEvent;
import com.alan.clients.newevent.impl.render.RenderItemEvent;
import com.alan.clients.util.math.MathUtil;
import com.alan.clients.util.packet.PacketUtil;
import com.alan.clients.util.rotation.RotationUtil;
import util.time.StopWatch;
import com.alan.clients.util.vector.Vector2f;
import com.alan.clients.value.impl.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.ScriptEvaluator;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author Alan
 * @since 11/17/2021
 */
@Rise
@ModuleInfo(name = "Kill Aura", description = "module.combat.killaura.description", category = Category.COMBAT)
public final class KillAura extends Module {

    private final ModeValue mode = new ModeValue("Attack Mode", this)
            .add(new SubMode("Single"))
            .add(new SubMode("Multiple"))
            .setDefault("Single");

    private final ModeValue autoBlock = new ModeValue("Auto Block", this)
            .add(new SubMode("None"))
            .add(new SubMode("Fake"))
            .add(new SubMode("Vanilla"))
            .add(new SubMode("NCP"))
            .add(new SubMode("Watchdog"))
            .add(new SubMode("Legit"))
            .add(new SubMode("Intave"))
            .add(new SubMode("Imperfect Vanilla"))
            .add(new SubMode("Vanilla ReBlock"))
            .setDefault("None");

    private final ModeValue clickMode = new ModeValue("Click Delay Mode", this)
            .add(new SubMode("Normal"))
            .add(new SubMode("Hit Select"))
            .add(new SubMode("1.9+"))
            .setDefault("Normal");

    private final NumberValue range = new NumberValue("Range", this, 3, 3, 6, 0.1);
    private final BoundsNumberValue cps = new BoundsNumberValue("CPS", this, 10, 15, 1, 20, 1);
    private final BoundsNumberValue rotationSpeed = new BoundsNumberValue("Rotation speed", this, 5, 10, 0, 10, 1);
    private final ListValue<MovementFix> movementCorrection = new ListValue<>("Movement correction", this);
    private final BooleanValue keepSprint = new BooleanValue("Keep sprint", this, false);
    private final BooleanValue rayCast = new BooleanValue("Ray cast", this, false);

    private final BooleanValue advanced = new BooleanValue("Advanced", this, false);
    private final BooleanValue lookAtTheClosestPoint = new BooleanValue("Look at the closest point on the player", this, true, () -> !advanced.getValue());
    private final BooleanValue subTicks = new BooleanValue("Attack outside ticks", this, false, () -> !advanced.getValue());
    private final StringValue runMovementFixIfNot = new StringValue("Exclude MovementCorrection if", this, "", () -> !advanced.getValue());
    private final ModeValue rotationMode = new ModeValue("Rotation Mode", this, () -> !advanced.getValue())
            .add(new SubMode("Legit/Normal"))
            .add(new SubMode("Autistic AntiCheat"))
            .setDefault("Legit/Normal");
    private final BooleanValue attackWhilstScaffolding = new BooleanValue("Attack whilst Scaffolding", this, false, () -> !advanced.getValue());
    private final BooleanValue noSwing = new BooleanValue("No swing", this, false, () -> !advanced.getValue());
    private final BooleanValue autoDisable = new BooleanValue("Auto disable", this, false, () -> !advanced.getValue());
    private final BooleanValue grimFalse = new BooleanValue("Prevent Grim false positives", this, false, () -> !advanced.getValue());


    private final StopWatch attackStopWatch = new StopWatch();
    private final StopWatch clickStopWatch = new StopWatch();

    private float yaw, pitch, randomYaw, randomPitch;
    private float randomYawDirection, randomPitchDirection;

    private boolean blocking, swing, allowAttack;
    private int expandRange;
    private long nextSwing;

    private List<EntityLivingBase> targets;
    public Entity target;

    public StopWatch subTicksStopWatch = new StopWatch();
    private int attack;

    private int hitTicks;

    public KillAura() {
        for (MovementFix movementFix : MovementFix.values()) {
            movementCorrection.add(movementFix);
        }

        movementCorrection.setDefault(MovementFix.OFF);
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
        this.hitTicks++;

        if (GUIDetectionComponent.inGUI()) {
            return;
        }

        if (target == null || mc.thePlayer.isDead || this.getModule(Scaffold.class).isEnabled()) {
            this.unblock(false);
            target = null;
        }
    };

    @Override
    protected void onEnable() {
        this.resetRotations();
        randomPitchDirection = randomYawDirection = randomYaw = randomPitch = 0;
        this.attack = 0;
    }

    @Override
    public void onDisable() {
        target = null;

        this.unblock(false);
    }

    private void resetRotations() {
        yaw = mc.thePlayer.rotationYaw;
        pitch = mc.thePlayer.rotationPitch;
    }

    @EventLink()
    public final Listener<WorldChangeEvent> onWorldChange = event -> {
        if (this.autoDisable.getValue()) {
            this.toggle();
        }
    };

    @EventLink()
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {

        if (mc.thePlayer.getHealth() <= 0.0) {
            if (this.autoDisable.getValue()) {
                this.toggle();
            }
        }

        if (getModule(Scaffold.class).isEnabled() && !attackWhilstScaffolding.getValue()) {
            return;
        }

        this.attack = Math.max(Math.min(this.attack, this.attack - 2), 0);

        if (mc.thePlayer.ticksExisted % 20 == 0) {
            expandRange = (int) (2 + Math.random() * 3);
        }

        if (GUIDetectionComponent.inGUI()) {
            return;
        }

        /*
         * Getting targets and selecting the nearest one
         */
        targets = Client.INSTANCE.getTargetManager().getTargets(range.getValue().doubleValue() + expandRange);

        if (targets.isEmpty()) {
            this.randomiseTargetRotations();
            target = null;
            return;
        }

        target = targets.get(0);

        if (target == null || mc.thePlayer.isDead) {
            this.randomiseTargetRotations();
            return;
        }

        Color color = /*getTheme().getFirstColor()*/ Color.WHITE;
        ESPComponent.add(new SigmaRing(new ESPColor(color, color, color)));

        if (this.canBlock()) {
            this.preBlock();
        }

        /*
         * Calculating rotations to target
         */
        this.rotations();

        /*
         * Doing the attack
         */
        this.doAttack(targets);

        /*
         * Blocking
         */
        if (this.canBlock()) {
            this.postAttackBlock();
        }
    };

    public void rotations() {
        final double minRotationSpeed = this.rotationSpeed.getValue().doubleValue();
        final double maxRotationSpeed = this.rotationSpeed.getSecondValue().doubleValue();
        final float rotationSpeed = (float) MathUtil.getRandom(minRotationSpeed, maxRotationSpeed);

        switch (rotationMode.getValue().getName()) {
            case "Legit/Normal":
                final Vector2f targetRotations = RotationUtil.calculate(target, lookAtTheClosestPoint.getValue(), range.getValue().doubleValue());

                this.randomiseTargetRotations();

                targetRotations.x += randomYaw;
                targetRotations.y += randomPitch;

                if (rotationSpeed != 0) {
                    RotationComponent.setRotations(targetRotations, rotationSpeed,
                            movementCorrection.getValue() == MovementFix.OFF || shouldRun() ? MovementFix.OFF : movementCorrection.getValue());
                }
                break;

            case "Autistic AntiCheat":
                double speed = rotationSpeed * 10;
                RotationComponent.setRotations(new Vector2f((float) (RotationComponent.rotations.x + speed), 0), speed / 18,
                        movementCorrection.getValue() == MovementFix.OFF || shouldRun() ? MovementFix.OFF : movementCorrection.getValue());
                break;
        }

    }

    public boolean shouldRun() {
        // If you're Tecnio don't scroll down
        String userEnteredCode = runMovementFixIfNot.getValue();

        // Legit no one can bypass this to make a rce
        if (userEnteredCode.length() > 60 || userEnteredCode.length() <= 1 || userEnteredCode.contains(";") || userEnteredCode.contains(".")) {
            return false;
        }

        // I don't think you could write something more scuffed if you tried
        String script =
                // Don't kill me please
                "" +
                        "boolean onGround = " + mc.thePlayer.onGround + ";" +
                        "boolean ground = onGround;" +

                        "int ticksOnGround = " + mc.thePlayer.onGroundTicks + ";" +
                        "int onGroundTicks = ticksOnGround;" +
                        "int groundTicks = ticksOnGround;" +

                        "int ticksInAir = " + mc.thePlayer.offGroundTicks + ";" +
                        "int airTicks = ticksInAir;" +
                        "int ticksOffGround = ticksInAir;" +

                        "int ticksSinceVelocity = " + mc.thePlayer.ticksSinceVelocity + ";" +
                        "int velocityTicks = ticksSinceVelocity;" +

                        "boolean runIf = " + userEnteredCode + ";" +

                        "System.out.println(runIf);";

        ScriptEvaluator scriptEvaluator = new ScriptEvaluator();

        // Preserve current console which contains.
        PrintStream previousConsole = System.out;

        // Set the standard output to use newConsole.
        ByteArrayOutputStream newConsole = new ByteArrayOutputStream();
        System.setOut(new PrintStream(newConsole));

        try {
            scriptEvaluator.cook(script);
            scriptEvaluator.evaluate(new Object[0]);
        } catch (CompileException | InvocationTargetException e) {
            return false;
        }

        boolean result = newConsole.toString().contains("true");

        System.setOut(previousConsole);

        return result;
    }

    /*
     * Randomising rotation target to simulate legit players
     */
    private void randomiseTargetRotations() {
        randomYaw = (float) (Math.random() - 0.5f);
        randomPitch = (float) (Math.random() - 0.5f);
    }

    @EventLink
    public final Listener<MouseOverEvent> onMouseOver = event -> {
        event.setRange(event.getRange() + range.getValue().doubleValue() - 3);
    };

    @EventLink
    public final Listener<PostMotionEvent> onPostMotion = event -> {
        if (target != null && this.canBlock()) {
            this.postBlock();
        }
    };

    private void doAttack(final List<EntityLivingBase> targets) {
        String autoBlock = this.autoBlock.getValue().getName();
        if (BadPacketsComponent.bad(false, true, true, true, true) &&
                (autoBlock.equals("Fake") || autoBlock.equals("None") ||
                        autoBlock.equals("Imperfect Vanilla") || autoBlock.equals("Vanilla ReBlock"))) {
            return;
        }

        double delay = -1;
        boolean flag = false;

        switch (clickMode.getValue().getName()) {
            case "Hit Select": {
                delay = 9;
                flag = target.hurtResistantTime <= 10;
                break;
            }

            case "1.9+": {
                double speed = 4;

                if (mc.thePlayer.getHeldItem() != null) {
                    final Item item = mc.thePlayer.getHeldItem().getItem();

                    if (item instanceof ItemSword) {
                        speed = 1.6;
                    } else if (item instanceof ItemSpade) {
                        speed = 1;
                    } else if (item instanceof ItemPickaxe) {
                        speed = 1.2;
                    } else if (item instanceof ItemAxe) {
                        switch (((ItemAxe) item).getToolMaterial()) {
                            case WOOD:
                            case STONE:
                                speed = 0.8;
                                break;

                            case IRON:
                                speed = 0.9;
                                break;

                            default:
                                speed = 1;
                                break;
                        }
                    } else if (item instanceof ItemHoe) {
                        switch (((ItemHoe) item).getToolMaterial()) {
                            case WOOD:
                            case GOLD:
                                speed = 1;
                                break;

                            case STONE:
                                speed = 2;
                                break;

                            case IRON:
                                speed = 3;
                                break;
                        }
                    }
                }

                delay = 1 / speed * 20 - 1;
                break;
            }
        }

        if (attackStopWatch.finished(this.nextSwing) && (!grimFalse.getValue() || !(mc.thePlayer.ticksSprint <= 1 && mc.thePlayer.isSprinting())) && !BadPacketsComponent.bad(false, true, true, false, true) && target != null && (clickStopWatch.finished((long) (delay * 50)) || flag)) {
            final long clicks = (long) (Math.round(MathUtil.getRandom(this.cps.getValue().intValue(), this.cps.getSecondValue().intValue())) * 1.5);
            this.nextSwing = 1000 / clicks;

            if (Math.sin(nextSwing) + 1 > Math.random() || attackStopWatch.finished(this.nextSwing + 500) || Math.random() > 0.5) {
                this.allowAttack = true;

                if (this.canBlock()) {
                    this.attackBlock();
                }

                if (this.allowAttack) {
                    /*
                     * Attacking target
                     */
                    final double range = this.range.getValue().doubleValue();
                    final MovingObjectPosition movingObjectPosition = mc.objectMouseOver;

                    switch (this.mode.getValue().getName()) {
                        case "Single": {
                            if ((mc.thePlayer.getDistanceToEntity(target) <= range && !rayCast.getValue()) ||
                                    (rayCast.getValue() && movingObjectPosition != null && movingObjectPosition.entityHit == target)) {
                                this.attack(target);
                            } else if (movingObjectPosition != null && movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                                this.attack(movingObjectPosition.entityHit);
                            } else {
                                switch (clickMode.getValue().getName()) {
                                    case "Normal":
                                    case "Hit Select":
                                        PacketUtil.send(new C0APacketAnimation());
                                        this.clickStopWatch.reset();
                                        this.hitTicks = 0;
                                        break;
                                }
                            }
                            break;
                        }

                        case "Multiple": {
                            targets.removeIf(target -> mc.thePlayer.getDistanceToEntity(target) > range);

                            if (!targets.isEmpty()) {
                                targets.forEach(this::attack);
                            }
                            break;
                        }
                    }
                }

                this.attackStopWatch.reset();
            }
        }
    }

    private void attack(final Entity target) {
        this.attack = Math.min(Math.max(this.attack, this.attack + 2), 5);

        Client.INSTANCE.getEventBus().handle(new ClickEvent());
        if (!this.noSwing.getValue()) mc.thePlayer.swingItem();

        final AttackEvent event = new AttackEvent(target);
        Client.INSTANCE.getEventBus().handle(event);

        if (!event.isCancelled()) {
            if (this.keepSprint.getValue()) {
                mc.playerController.syncCurrentPlayItem();

                PacketUtil.send(new C02PacketUseEntity(event.getTarget(), C02PacketUseEntity.Action.ATTACK));

                if (mc.thePlayer.fallDistance > 0 && !mc.thePlayer.onGround && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWater() && !mc.thePlayer.isPotionActive(Potion.blindness) && mc.thePlayer.ridingEntity == null) {
                    mc.thePlayer.onCriticalHit(target);
                }
            } else {
                mc.playerController.attackEntity(mc.thePlayer, target);
            }
        }

        this.clickStopWatch.reset();
        this.hitTicks = 0;
    }

    private void block(final boolean check, final boolean interact) {
        if (!blocking || !check) {
            if (interact && target != null) {
                mc.playerController.interactWithEntitySendPacket(mc.thePlayer, target);
            }

            PacketUtil.send(new C08PacketPlayerBlockPlacement(SlotComponent.getItemStack()));
            blocking = true;
        }
    }

    private void unblock(final boolean swingCheck) {
        if (blocking && (!swingCheck || !swing)) {
            if (!mc.gameSettings.keyBindUseItem.isKeyDown()) {
                PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            } else {
                mc.gameSettings.keyBindUseItem.setPressed(false);
            }
            blocking = false;
        }
    }

    @EventLink(value = Priorities.HIGH)
    public final Listener<RenderItemEvent> onRenderItem = event -> {
        if (target != null && !autoBlock.getValue().getName().equals("None") && this.canBlock()) {
            event.setEnumAction(EnumAction.BLOCK);
            event.setUseItem(true);
        }
    };

    @EventLink()
    public final Listener<PacketSendEvent> onPacketSend = event -> {
        final Packet<?> packet = event.getPacket();

        if (packet instanceof C0APacketAnimation) {
            swing = true;
        } else if (packet instanceof C03PacketPlayer) {
            swing = false;
        }

        this.packetBlock(event);
    };

    public void packetBlock(final PacketSendEvent event) {
        final Packet<?> packet = event.getPacket();

        switch (autoBlock.getValue().getName()) {
            case "Intave":
                if (packet instanceof C03PacketPlayer) {
                    event.setCancelled(true);
                    this.unblock(false);
                    PacketUtil.sendNoEvent(packet);
                    this.block(false, true);
                }
                break;

            case "Fake":
            case "None":
                if (SlotComponent.getItemStack() == null || !(SlotComponent.getItemStack().getItem() instanceof ItemSword)) {
                    return;
                }

                if (packet instanceof C08PacketPlayerBlockPlacement) {
                    final C08PacketPlayerBlockPlacement wrapper = (C08PacketPlayerBlockPlacement) packet;

                    if (wrapper.getPlacedBlockDirection() == 255) {
                        event.setCancelled(true);
                    }
                } else if (packet instanceof C07PacketPlayerDigging) {
                    C07PacketPlayerDigging wrapper = ((C07PacketPlayerDigging) packet);

                    if (wrapper.getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM) {
                        event.setCancelled(true);
                    }
                }
                break;
        }
    }

    private void attackBlock() {
        if ("Legit".equals(autoBlock.getValue().getName())) {
            if (mc.gameSettings.keyBindUseItem.isKeyDown()) {
                mc.gameSettings.keyBindUseItem.setPressed(false);
            }


            this.allowAttack = !BadPacketsComponent.bad(false, false, false, true, false);
        }
    }

    private void postAttackBlock() {
        switch (autoBlock.getValue().getName()) {
            case "Legit":
                if (this.hitTicks == 1) {
                    mc.gameSettings.keyBindUseItem.setPressed(true);
                    this.blocking = true;
                }
                break;

            case "Intave":
                this.block(false, false);
                break;

            case "Vanilla":
                if (this.hitTicks != 0) {
                    this.block(false, true);
                }
                break;

            case "Imperfect Vanilla":
                if (this.hitTicks == 1 && mc.thePlayer.isSwingInProgress && Math.random() > 0.1) {
                    this.block(false, true);
                }
                break;

            case "Vanilla ReBlock":
                if (this.hitTicks == 1 || !this.blocking) {
                    this.block(false, true);
                }
                break;

            case "Watchdog":
                if ((this.hitTicks == 1 || !this.blocking) &&
                        (mc.thePlayer.isPotionActive(Potion.moveSpeed) ||
                                (mc.thePlayer.ticksSinceVelocity <= 15 && mc.thePlayer.ticksSinceVelocity > 6))) {
                    this.block(false, true);
                }

                if (mc.thePlayer.ticksSinceVelocity > 16 && !mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                    this.unblock(true);
                }
                break;
        }
    }

    private void preBlock() {
        switch (autoBlock.getValue().getName()) {
            case "NCP":
            case "Intave":
                this.unblock(false);
                break;
        }
    }

    private void postBlock() {
        switch (autoBlock.getValue().getName()) {
            case "NCP":
                this.block(true, false);
                break;
        }
    }

    private boolean canBlock() {
        return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    @EventLink()
    public final Listener<Render2DEvent> onRender2D = event -> {
        if (this.subTicks.getValue() && this.attack <= 5 && target != null && this.subTicksStopWatch.finished(10)) {
            this.subTicksStopWatch.reset();

            /*
             * Getting targets and selecting the nearest one
             */
            targets = Client.INSTANCE.getTargetManager().getTargets(range.getValue().doubleValue() + expandRange);

            if (targets.isEmpty()) {
                this.randomiseTargetRotations();
                target = null;
                return;
            }

            this.doAttack(targets);
        }
    };
}