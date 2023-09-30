package io.github.raze.modules.collection.combat;

import io.github.raze.Raze;
import io.github.raze.events.collection.motion.EventMotion;
import io.github.raze.events.system.SubscribeEvent;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.modules.system.information.ModuleCategory;
import io.github.raze.settings.collection.ArraySetting;
import io.github.raze.settings.collection.BooleanSetting;
import io.github.raze.settings.collection.NumberSetting;
import io.github.raze.utilities.collection.math.RandomUtil;
import io.github.raze.utilities.collection.math.RotationUtil;
import io.github.raze.utilities.collection.math.TimeUtil;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMonster;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPosition;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Aura extends BaseModule {

    public ArraySetting attack, autoblock, sorting, rotation;

    public NumberSetting minAPS, maxAPS;
    public NumberSetting attackRange, rotationRange;
    public NumberSetting minSpeed, maxSpeed;

    public NumberSetting random, predict;

    public BooleanSetting interact, silent, raytrace, keepSprint;

    public BooleanSetting players;
    public BooleanSetting animals;
    public BooleanSetting monsters;

    // Utilities
    public TimeUtil stopwatch;

    // Variables
    public float yaw, pitch, lastYaw, lastPitch;
    public double currentAPS, currentSpeed;

    public EntityLivingBase target;
    public List<EntityLivingBase> targets;

    public boolean blocking;

    public Aura() {
        super("Aura", "Automatically attacks entities.", ModuleCategory.COMBAT);

        Raze.INSTANCE.MANAGER_REGISTRY.SETTING_REGISTRY.register(

                attack = new ArraySetting(this, "Attack", "Single", "Single"),
                autoblock = new ArraySetting(this, "AutoBlock", "None", "None", "Fake", "Vanilla", "Swing", "Damage"),
                sorting = new ArraySetting(this, "Sorting", "Health", "Health", "Distance"),
                rotation = new ArraySetting(this, "Rotation", "Normal", "None", "Normal", "Smooth"),

                minAPS = new NumberSetting(this, "Min APS", 0, 20, 10) {
                    @Override
                    public void onChange(Number old, Number current) {

                        double a = minAPS.get().doubleValue();
                        double b = maxAPS.get().doubleValue();

                        if (a > b) {
                            minAPS.set(b);
                        }

                        super.onChange(old, current);
                    }
                },

                maxAPS = new NumberSetting(this, "Max APS", 0, 20, 15) {
                    @Override
                    public void onChange(Number old, Number current) {

                        double a = minAPS.get().doubleValue();
                        double b = maxAPS.get().doubleValue();

                        if (b < a) {
                            maxAPS.set(a);
                        }

                        super.onChange(old, current);
                    }
                },

                attackRange = new NumberSetting(this, "Attack Range", 3, 6, 3) {
                    @Override
                    public void onChange(Number old, Number current) {

                        double a = attackRange.get().doubleValue();
                        double b = rotationRange.get().doubleValue();

                        if (a > b) {
                            attackRange.set(b);
                        }

                        super.onChange(old, current);
                    }
                },

                rotationRange = new NumberSetting(this, "Rotation Range", 3, 6, 3.2) {
                    @Override
                    public void onChange(Number old, Number current) {

                        double a = attackRange.get().doubleValue();
                        double b = rotationRange.get().doubleValue();

                        if (b > a) {
                            rotationRange.set(a);
                        }

                        super.onChange(old, current);
                    }
                },

                minSpeed = new NumberSetting(this, "Min Turn Speed", 0, 180, 160) {
                    @Override
                    public void onChange(Number old, Number current) {

                        double a = minSpeed.get().doubleValue();
                        double b = maxSpeed.get().doubleValue();

                        if (a > b) {
                            minSpeed.set(b);
                        }

                        super.onChange(old, current);
                    }
                },
                maxSpeed = new NumberSetting(this, "Max Turn Speed", 0, 180, 180) {
                    @Override
                    public void onChange(Number old, Number current) {

                        double a = minSpeed.get().doubleValue();
                        double b = maxSpeed.get().doubleValue();

                        if (b < a) {
                            maxSpeed.set(a);
                        }

                        super.onChange(old, current);
                    }
                },

                random = new NumberSetting(this, "Random", 0, 5, 0.5),
                predict = new NumberSetting(this, "Predict", 0, 5, 1.2),

                interact = new BooleanSetting(this, "Interact", false),
                silent = new BooleanSetting(this, "Silent", true),
                raytrace = new BooleanSetting(this, "Raytrace", true),
                keepSprint = new BooleanSetting(this, "KeepSprint", true),

                players = new BooleanSetting(this, "Players", true),
                animals = new BooleanSetting(this, "Animals", true),
                monsters = new BooleanSetting(this, "Monsters", true)

        );

        stopwatch = new TimeUtil();

        currentAPS   = RandomUtil.randomNumber(minAPS.get().doubleValue(),   maxAPS.get().doubleValue()  );
        currentSpeed = RandomUtil.randomNumber(minSpeed.get().doubleValue(), maxSpeed.get().doubleValue());

        target = null;
        targets = new ArrayList<>();
    }

    @SubscribeEvent
    public void onMotion(EventMotion event) {

        boolean shouldInteract = interact.get();

        switch (event.getState()) {
            case PRE: {

                updateTargets();
                updateRotations();

                if (target == null) {
                    lastYaw   = mc.thePlayer.rotationYaw;
                    lastPitch = mc.thePlayer.rotationPitch;

                    return;
                }

                currentSpeed = RandomUtil.randomNumber(
                        minSpeed.get().doubleValue(),
                        maxSpeed.get().doubleValue()
                );

                if (silent.get()) {
                    event.setYaw(yaw);
                    event.setPitch(pitch);
                } else {
                    mc.thePlayer.rotationYaw = yaw;
                    mc.thePlayer.rotationPitch = pitch;
                }

                mc.thePlayer.rotationYawHead = yaw;
                mc.thePlayer.renderYawOffset = pitch;

                switch (autoblock.get()) {
                    default: {
                        unblock();
                        break;
                    }
                }

                break;
            }

            case POST: {

                long milliseconds = (long) (1000 / currentAPS);

                if (stopwatch.elapsed(milliseconds, true)) {
                    attack(target, keepSprint.get());

                    currentAPS = RandomUtil.randomNumber(
                            minAPS.get().doubleValue(),
                            maxAPS.get().doubleValue()
                    );
                }

                switch (autoblock.get()) {
                    case "Vanilla": {
                        block(shouldInteract);
                        break;
                    }
                    case "Swing": {
                        if (mc.thePlayer.swingProgressInt == 1) {
                            unblock();
                        } else if (mc.thePlayer.swingProgressInt == 2) {
                            block(shouldInteract);
                        }
                        break;
                    }
                    case "Damage": {
                        if (mc.thePlayer.hurtTime > 8) {
                            block(shouldInteract);
                        } else {
                            unblock();
                        }
                    }
                    default: {
                        break;
                    }
                }

                break;
            }
        }

    }

    @Override
    public void onEnable() {

        yaw = mc.thePlayer.rotationYaw;
        pitch = mc.thePlayer.rotationPitch;

        lastYaw = mc.thePlayer.rotationYaw;
        lastPitch = mc.thePlayer.rotationPitch;

        super.onEnable();
    }

    @Override
    public void onDisable() {

        stopwatch.reset();

        super.onDisable();
    }

    public void attack(EntityLivingBase entity, boolean exhaustion) {
        mc.thePlayer.swingItem();

        if (mc.thePlayer.getDistanceToEntity(entity) > attackRange.get().doubleValue()) {
            return;
        }

        if (raytrace.get() && !RotationUtil.raytrace(yaw, pitch, attackRange.get().doubleValue(), target)) {
            return;
        }

        if (exhaustion) {
            mc.playerController.attackEntity(mc.thePlayer, entity);
        } else {
            mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
        }
    }

    private void updateRotations() {

        lastYaw   = yaw;
        lastPitch = pitch;

        float[] destination = RotationUtil.getRotationsToEntity(
                target,
                predict.get().doubleValue(),
                random.get().doubleValue()
        );

        yaw   = destination[0];
        pitch = destination[1];

        switch (rotation.get()) {
            case "None": {
                yaw   = mc.thePlayer.rotationYaw;
                pitch = mc.thePlayer.rotationPitch;
                break;
            }
            case "Normal": {
                yaw   = destination[0];
                pitch = destination[1];
                break;
            }
            case "Smooth": {
                float yawDiff   = RotationUtil.getAngleDifference(destination[0], lastYaw);
                float pitchDiff = RotationUtil.getAngleDifference(destination[1], lastPitch);

                yaw   = (float) (lastYaw + (yawDiff > currentSpeed ? currentSpeed : Math.max(yawDiff, -currentSpeed)));
                pitch = (float) (lastPitch + (pitchDiff > currentSpeed ? currentSpeed : Math.max(pitchDiff, -currentSpeed)));

                break;
            }
        }

        float[] fixedRotations = RotationUtil.getFixedRotations(
                new float[] { yaw, pitch },
                new float[] { lastYaw, lastPitch }
        );

        yaw   = fixedRotations[0];
        pitch = fixedRotations[1];
    }

    private List<EntityLivingBase> getTargets() {

        List<EntityLivingBase> entities = mc.theWorld.loadedEntityList

                .stream()

                .filter(entity -> (entity instanceof EntityLivingBase))

                .map(entity -> (EntityLivingBase) entity)

                .filter(entity -> {
                    if (entity.deathTime != 0) {
                        return false;
                    }

                    if (entity instanceof EntityPlayer && !players.get()) {
                        return false;
                    }

                    if (
                            entity instanceof EntityAnimal ||
                            entity instanceof EntityVillager ||
                            entity instanceof EntitySquid &&
                            !animals.get()
                    ) {
                        return false;
                    }

                    if (
                            entity instanceof EntityMonster ||
                            entity instanceof EntitySlime &&
                            !monsters.get()
                    ) {
                        return false;
                    }

                    return (entity != mc.thePlayer);
                })

                .filter(entity -> (mc.thePlayer.getDistanceToEntity(entity) < rotationRange.get().doubleValue()))

                .collect(Collectors.toList());

        switch (sorting.get()) {
            case "Health": {
                entities.sort(Comparator.comparingDouble(EntityLivingBase::getHealth));
                break;
            }
            case "Distance": {
                entities.sort(Comparator.comparingDouble(entity -> mc.thePlayer.getDistanceSqToEntity(entity)));
                break;
            }
        }

        return entities;
    }

    private EntityLivingBase getTarget() {
        EntityLivingBase entity;

        switch (attack.get()) {
            case "Single": {
                entity = (targets.size() > 0) ? targets.get(0) : null;
                break;
            }
            case "Switch": {
                entity = null;
                break;
            }
            default: {
                entity = null;
                break;
            }
        }

        return entity;
    }

    private void updateTargets() {
        targets = getTargets();
        target = getTarget();
    }

    private void block(boolean interact) {
        if (!canBlock()) {
            return;
        }

        if (!blocking) {
            if (interact && target != null && mc.objectMouseOver.entityHit == target) {
                mc.playerController.interactWithEntitySendPacket(mc.thePlayer, target);
            }

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
        return mc.thePlayer.inventory.getCurrentItem() != null && mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword;
    }
}