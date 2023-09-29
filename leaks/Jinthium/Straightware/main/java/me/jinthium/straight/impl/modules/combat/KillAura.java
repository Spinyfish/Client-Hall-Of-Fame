package me.jinthium.straight.impl.modules.combat;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.event.game.PlayerAttackEvent;
import me.jinthium.straight.impl.event.game.UpdateLookEvent;
import me.jinthium.straight.impl.event.movement.JumpFixEvent;
import me.jinthium.straight.impl.event.movement.PlayerMoveUpdateEvent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.modules.movement.NoSlowDown;
import me.jinthium.straight.impl.modules.player.Scaffold;
import me.jinthium.straight.impl.settings.BooleanSetting;
import me.jinthium.straight.impl.settings.ModeSetting;
import me.jinthium.straight.impl.settings.MultiBoolSetting;
import me.jinthium.straight.impl.settings.NumberSetting;
import me.jinthium.straight.impl.utils.ChatUtil;
import me.jinthium.straight.impl.utils.entity.EntityValidator;
import me.jinthium.straight.impl.utils.entity.impl.*;
import me.jinthium.straight.impl.utils.math.MathUtils;
import me.jinthium.straight.impl.utils.misc.TimerUtil;
import me.jinthium.straight.impl.utils.network.PacketUtil;
import me.jinthium.straight.impl.utils.player.PlayerUtil;
import me.jinthium.straight.impl.utils.player.RotationUtils;
import me.jinthium.straight.impl.utils.vector.Vector2f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.*;
import net.minecraft.util.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class KillAura extends Module {


    private final ModeSetting modeProperty = new ModeSetting("Mode", "Single", "Single", "Switch", "Multi");

    private final NumberSetting switchDelayProperty = new NumberSetting("Switch Delay", 50, 1, 1000, 20);

    private final ModeSetting sortProperty = new ModeSetting("Sort by", "Health", "Health", "Armor", "FOV", "Hurttime", "Distance");
    public boolean crits;
    private final NumberSetting minAPSProperty = new NumberSetting("Min APS", 11, 1, 20, 1);
    private final NumberSetting maxAPSProperty = new NumberSetting("Max APS", 14, 1, 20, 1);

    private final ModeSetting attackModeProperty = new ModeSetting("Attack In", "Post", "Pre", "Post");

    private final ModeSetting blockModeProperty = new ModeSetting("Block Mode", "Watchdog", "Watchdog", "Verus", "Vanilla", "AstralMC", "Fake");
    public final ModeSetting rotationsProperty = new ModeSetting("Rotations", "Snap", "Off", "Snap", "Smooth");
    private final BooleanSetting onlyAttackWhenLookingAtEntityLongNameMethod = new BooleanSetting("Attack When Looking", false);

    private final NumberSetting rangeProperty = new NumberSetting("Range", 4.2, 1, 7, 0.1);
    public final NumberSetting maxTargets = new NumberSetting("Max Targets", 4, 1, 10, 1);
    private final NumberSetting blockRangeProperty = new NumberSetting("Block Range", 4.2, 1, 10, 0.1);

    private final MultiBoolSetting targetsProperty = new MultiBoolSetting("Targets",
            new BooleanSetting("Players", true),
            new BooleanSetting("Mobs", false),
            new BooleanSetting("Animals", false),
            new BooleanSetting("Invis", false),
            new BooleanSetting("Dead Father Figure", false),
            new BooleanSetting("Teams", true),
            new BooleanSetting("Friends", false));

    public final BooleanSetting lockViewProperty = new BooleanSetting("Lock View", false);
    private final BooleanSetting autoBlockProperty = new BooleanSetting("Autoblock", true);
    private final BooleanSetting noSwingProperty = new BooleanSetting("No Swing", false);
    private final BooleanSetting keepSprintProperty = new BooleanSetting("Keep Sprint", true);
    private final TimerUtil switchTimer = new TimerUtil();
    private final TimerUtil attackTimer = new TimerUtil();
    private final TimerUtil blockTimer = new TimerUtil();
    public float[] rotationStore;
    public boolean block, blockAnimation, changeTarget;
    private boolean rotating;
    public static CopyOnWriteArrayList<EntityLivingBase> bots = new CopyOnWriteArrayList<>();
    private int targetIndex;

    public final List<EntityLivingBase> targets = new ArrayList<>();
    public final List<EntityLivingBase> multiTargets = new ArrayList<>();
    public EntityValidator entityValidator = new EntityValidator(), blockValidator = new EntityValidator();
    public EntityLivingBase target;

    public KillAura() {
        super("Killaura", Category.COMBAT);
        switchDelayProperty.addParent(modeProperty, r -> modeProperty.is("Switch"));
        sortProperty.addParent(modeProperty, r -> modeProperty.is("Single"));
        maxTargets.addParent(modeProperty, r -> modeProperty.is("Multi"));
        this.addSettings(modeProperty, switchDelayProperty, sortProperty, minAPSProperty, maxAPSProperty,
                attackModeProperty, blockModeProperty, rotationsProperty, onlyAttackWhenLookingAtEntityLongNameMethod,
                rangeProperty, maxTargets, blockRangeProperty, targetsProperty, lockViewProperty, autoBlockProperty, noSwingProperty, keepSprintProperty);
    }

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventCallback = event -> {
        if (mc.thePlayer.ticksExisted < 5 || Client.INSTANCE.getModuleManager().getModule(Scaffold.class).isEnabled())
            return;
        this.setSuffix(modeProperty.getMode());
        entityValidator = new EntityValidator();
        blockValidator = new EntityValidator();
        final AliveCheck aliveCheck = new AliveCheck();
        final EntityCheck entityCheck = new EntityCheck(targetsProperty.isEnabled("Players"), targetsProperty.isEnabled("Animals"),
                targetsProperty.isEnabled("Mobs"), targetsProperty.isEnabled("Invis"));
        final TeamsCheck teamsCheck = new TeamsCheck(targetsProperty.isEnabled("Teams"));
        entityValidator.add(aliveCheck);
        entityValidator.add(new ConstantDistanceCheck(blockRangeProperty.getValue().floatValue()));
        entityValidator.add(entityCheck);
        entityValidator.add(teamsCheck);
        if (!targetsProperty.isEnabled("Friends"))
            entityValidator.add(new FriendCheck());

        blockValidator.add(aliveCheck);
        blockValidator.add(new ConstantDistanceCheck(blockRangeProperty.getValue().floatValue()));
        blockValidator.add(entityCheck);
        blockValidator.add(teamsCheck);

        if (!targetsProperty.isEnabled("Friends"))
            blockValidator.add(new FriendCheck());
        if (mc.thePlayer.ticksExisted <= 5) {
            if (this.isEnabled()) {
                this.toggle();
            }
            return;
        }

        updateTargets();

//        if (!event.isUpdate() && block && mc.thePlayer.swingProgressInt == -1 && blockModeProperty.is("Watchdog")) {
//            mc.thePlayer.addChatComponentMessage(new ChatComponentText("Unblocked"));
//            PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
//            block = false;
//            blockTimer.reset();
//        }
//
//        if(event.isPost() && block && (target == null || (!isEntityNearbyAttack() && isEntityNearby())) && blockModeProperty.is("Watchdog")) {
//            PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
//            block = false;
//        }

        if (event.isPre()) {
            if (target == null) {
                if (block && !blockModeProperty.is("Watchdog"))
                    unblock();

                if (block && blockModeProperty.is("Watchdog")) {
                    PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem + 1));
                    PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                    block = false;
                }

                blockAnimation = false;
                rotating = false;
                multiTargets.clear();
            }

            if (block && !blockModeProperty.is("Watchdog")) {
                unblock();
            }
        }

        //Do mode selecting hsit
        if (!modeProperty.is("Multi")) {
            target = getTarget();
            if (event.isPre()) {
                if (isEntityNearby()) {
                    final Vec3 hitOrigin = RotationUtils.getHitOrigin(mc.thePlayer);
                    final Vec3 attackHitVec = getAttackHitVec(hitOrigin, target);
                    if (doesRotations() && attackHitVec != null) {
                        final float[] rotations = RotationUtils.getRotations(
                                new float[]{lockViewProperty.isEnabled() ? mc.thePlayer.rotationYaw : event.getPrevYaw(),
                                        lockViewProperty.isEnabled() ? mc.thePlayer.rotationPitch : event.getPrevPitch()},
                                rotationsProperty.is("Snap") ? 0.0F : 17.5f,
                                hitOrigin,
                                attackHitVec);
                        // if (!isBypassRotations()) rotationStore = new float[]{event.getPrevYaw(), event.getPrevPitch()};
                        setServerSideRotations(rotationStore = rotations, event, lockViewProperty.isEnabled());
                    }
                }
            }

        } else {
            final Vec3 hitOrigin = RotationUtils.getHitOrigin(mc.thePlayer);
            final boolean doRots = doesRotations();
            final List<float[]> rotationsToEntities = new ArrayList<>();
            if (!isEntityNearby())
                target = null;


            targets.stream().limit(maxTargets.getValue().intValue()).forEach(entity -> {
                target = entity;
                final Vec3 attackHitVec = getAttackHitVec(hitOrigin, target);
                if (attackHitVec != null && isEntityNearby() && event.isPre()) {
                    if (doRots) {
                        // Calculate rotations (with max yaw/pitch change & GCD) to best attack hit vec
                        final float[] rotations = RotationUtils.getRotations(
                                new float[]{lockViewProperty.isEnabled() ? mc.thePlayer.rotationYaw : event.getPrevYaw(),
                                        lockViewProperty.isEnabled() ? mc.thePlayer.rotationPitch : event.getPrevPitch()},
                                rotationsProperty.is("Snap") ? 0.0F : 17.5f,
                                hitOrigin,
                                attackHitVec);

                        rotationsToEntities.add(rotations);
                    }
                    multiTargets.add(target);
                }
            });
            if (event.isPre()) {
                if (doesRotations() && isEntityNearby()) {
//                    int index = 0;
//                    if(index == multiTargets.size())
//                        index = 0;
                }
            }
        }


        if (isEntityNearby()) {
            final Vec3 origin = RotationUtils.getHitOrigin(this.mc.thePlayer);
            final MovingObjectPosition intercept = RotationUtils.calculateIntercept(
                    RotationUtils.getHittableBoundingBox(target, 0.1),
                    origin,
                    event.getYaw(),
                    event.getPitch(),
                    rangeProperty.getValue().floatValue());

            if (event.isPre()) {
                if (isEntityNearbyAttack() && (intercept != null || !onlyAttackWhenLookingAtEntityLongNameMethod.isEnabled())) {
                    if (attackModeProperty.is("Pre")) {
                        attack(target);
                    }
                }
            } else if (event.isPost()) {
                if (isEntityNearbyAttack() && (intercept != null || !onlyAttackWhenLookingAtEntityLongNameMethod.isEnabled())) {
                    if (attackModeProperty.is("Post")) {
                        attack(target);
                    }
                }

                boolean canBlock = autoBlockProperty.isEnabled() && mc.thePlayer.getHeldItem() != null;
                if (canBlock && !block) {
                    block();
                }
            }
        }
    };

    @Override
    public void onEnable() {
        switchTimer.reset();
        attackTimer.reset();
        blockTimer.reset();
        blockAnimation = false;
        targets.clear();
        multiTargets.clear();
        crits = false;
        changeTarget = false;
        block = false;
        this.rotating = false;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (block) {
            PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            block = false;
        }
        targetIndex = 0;
        blockAnimation = false;
        target = null;
        targets.clear();
    }

    public Vec3 getAttackHitVec(final Vec3 hitOrigin, final EntityLivingBase entity) {
        final AxisAlignedBB boundingBox = RotationUtils.getHittableBoundingBox(entity, .1f);
        // Get optimal attack hit vec
        return RotationUtils.getAttackHitVec(mc, hitOrigin, boundingBox,
                RotationUtils.getClosestPoint(hitOrigin, entity.getEntityBoundingBox()),
                true, 5);
    }

    public boolean isMulti() {
        return !multiTargets.isEmpty() && isEnabled();
    }

    public EntityLivingBase getTarget() {
        if (targets.isEmpty()) {
            return null;
        }

        for (EntityLivingBase bot : KillAura.bots) {
            if (targets.contains(bot))
                return null;
        }

        if (modeProperty.is("Single")) {
            return targets.get(0);
        }

        final int size = targets.size();

        if (size >= targetIndex && changeTarget) {
            targetIndex++;
            changeTarget = false;
        }


        if (switchTimer.hasTimeElapsed(switchDelayProperty.getValue().longValue())) {
            changeTarget = true;
            switchTimer.reset();
        }

        if (size <= targetIndex) {
            targetIndex = 0;
        }

        return targets.get(targetIndex);
    }

    private void updateTargets() {
        targets.clear();

        final List<Entity> entities = mc.theWorld.loadedEntityList;

        for (final Entity entity : entities) {
            if (entity instanceof EntityLivingBase entityLivingBase) {
                if (entityValidator.validate(entityLivingBase) && mc.theWorld.loadedEntityList.contains(entityLivingBase) && !bots.contains(entityLivingBase)) {
                    this.targets.add(entityLivingBase);
                }
            }
        }

        switch (sortProperty.getMode()) {
            case "Health" -> targets.sort(new HealthSorter());
            case "Armor" -> targets.sort(new ArmorSorter());
            case "FOV" -> targets.sort(new FovSorter());
            case "Hurttime" -> targets.sort(new HurtTimeSorter());
            case "Distance" -> targets.sort(new DistanceSorter());
        }
    }

    public boolean isEntityNearby() {
        final List<Entity> loadedEntityList = mc.theWorld.loadedEntityList;
        for (final Entity entity : loadedEntityList) {
            if (blockValidator.validate(entity)) {
                return true;
            }
        }
        return false;
    }

    public boolean isEntityNearbyAttack() {
        return mc.thePlayer.getDistanceToEntity(getTarget()) <= rangeProperty.getValue();
    }

    public void setServerSideRotations(float[] rotations,
                                       final PlayerUpdateEvent event,
                                       final boolean lockView) {
        if (rotations == null) return;

        if (rotations[1] < 0.1 && rotations[1] > 0) {
            rotations[1] = -1;
        }

        // Set event yaw (rotations yaw)
        RotationUtils.setRotations(event, rotations, rotationsProperty.is("Snap") ? 100 : 100 - 17.5f);

        // Do lock view aim
        if (lockView) {
            mc.thePlayer.rotationYaw = rotations[0];
            mc.thePlayer.rotationPitch = rotations[1];
        }
        rotating = true;
    }

    private void attack(EntityLivingBase entity) {
        int min = minAPSProperty.getValue().intValue();
        int max = maxAPSProperty.getValue().intValue();
        int cps;

        if (min == max) cps = min;
        else cps = MathUtils.getRandomInRange(min, max);


//        if (attackTimer.hasTimeElapsed(1000 / MathUtils.getRandomInRange(min, max))) {
        //mc.playerController.syncCurrentPlayItem();
//            if (block) {
//                PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem + 1));
//                PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
//                block = false;
//            }
        if (isMulti()) {
            multiTargets.forEach(entityLivingBase -> {
                if (attackTimer.hasTimeElapsed((long) 1000 / multiTargets.size() * 2)) {
                    // mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.0001, mc.thePlayer.posZ);
                    if (noSwingProperty.isEnabled()) {
                        PacketUtil.sendPacket(new C0APacketAnimation());
                    } else mc.thePlayer.swingItem();

                    final PlayerAttackEvent event = new PlayerAttackEvent(entityLivingBase);
                    Client.INSTANCE.getPubSubEventBus().publish(event);

                    if (!event.isCancelled()) {
                        if (keepSprintProperty.isEnabled()) {
                            PacketUtil.sendPacket(new C02PacketUseEntity(entityLivingBase, C02PacketUseEntity.Action.ATTACK));
                        } else {
                            mc.playerController.attackEntity(mc.thePlayer, entityLivingBase);
                        }
                    }
                    attackTimer.reset();
                    // blockTimer.reset();
                }
            });
        } else {
            if (attackTimer.hasTimeElapsed(1000 / MathUtils.getRandomInRange(min, max))) {
                if (noSwingProperty.isEnabled()) {
                    PacketUtil.sendPacket(new C0APacketAnimation());
                } else mc.thePlayer.swingItem();

                final PlayerAttackEvent event = new PlayerAttackEvent(entity);
                Client.INSTANCE.getPubSubEventBus().publish(event);

                if (!event.isCancelled()) {
                    if (keepSprintProperty.isEnabled()) {
                        PacketUtil.sendPacket(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
                    } else {
                        mc.playerController.attackEntity(mc.thePlayer, entity);
                    }
                }
                attackTimer.reset();
                // blockTimer.reset();
            }
        }
    }


    public boolean doesRotations() {
        return !rotationsProperty.is("Off");
    }

    private void block() {
        switch (blockModeProperty.getMode()) {
            case "Watchdog" -> {
                if(!Client.INSTANCE.getModuleManager().getModule(NoSlowDown.class).isEnabled()){
                    ChatUtil.print("Enable noslow lol");
                }
            }
            case "Verus" -> {
                if (isEntityNearbyAttack() && PlayerUtil.isHoldingSword()) {
                    PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                    block = true;
                }
            }
            case "AstralMC", "Vanilla" -> {
                if (isEntityNearbyAttack() && PlayerUtil.isHoldingSword()) {
                    PacketUtil.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                    block = true;
                }
            }
        }
        blockAnimation = true;
    }

    private void unblock() {
        switch (blockModeProperty.getMode()) {
            case "Verus", "Vanilla" -> {
                PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                block = false;
            }
            case "Watchdog" -> {
//                if(target == null)
//                    PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
//                else if (mc.thePlayer.getDistanceToEntity(target) > rangeProperty.getValue().floatValue())
//                    PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));

//                block = false;
            }
            case "Fake" -> block = false;
        }
    }


    public float getRange() {
        return blockRangeProperty.getValue().floatValue();
    }


    private final static class HealthSorter implements Comparator<EntityLivingBase> {
        public int compare(EntityLivingBase o1, EntityLivingBase o2) {
            return Double.compare(PlayerUtil.getEffectiveHealth(o1), PlayerUtil.getEffectiveHealth(o2));
        }
    }

    private final static class ArmorSorter implements Comparator<EntityLivingBase> {
        public int compare(EntityLivingBase o1, EntityLivingBase o2) {
            return Double.compare(o1.getTotalArmorValue(), o2.getTotalArmorValue());
        }
    }

    private final static class FovSorter implements Comparator<EntityLivingBase> {
        public int compare(EntityLivingBase o1, EntityLivingBase o2) {
            return Double.compare(o1.getPositionVector().subtract(mc.thePlayer.getPositionEyes(mc.timer.renderPartialTicks)).angle(mc.thePlayer.getLookVec()), o2.getPositionVector().subtract(mc.thePlayer.getPositionEyes(mc.timer.renderPartialTicks)).angle(mc.thePlayer.getLookVec()));
        }
    }

    private final static class HurtTimeSorter implements Comparator<EntityLivingBase> {
        public int compare(EntityLivingBase o1, EntityLivingBase o2) {
            return Double.compare(20 - o1.hurtResistantTime, 20 - o2.hurtResistantTime);
        }
    }

    private final static class DistanceSorter implements Comparator<EntityLivingBase> {
        public int compare(EntityLivingBase o1, EntityLivingBase o2) {
            return Double.compare(mc.thePlayer.getDistanceToEntity(o1), mc.thePlayer.getDistanceToEntity(o2));
        }
    }
//    public MultiSelectEnumSetting<PlayerUtil.TARGETS> targetsProperty() {
//        return targetsProperty;
//    }
}