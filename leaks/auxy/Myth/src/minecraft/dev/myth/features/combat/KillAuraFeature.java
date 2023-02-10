/**
 * @project Myth
 * @author CodeMan
 * @at 05.08.22, 22:01
 */
package dev.myth.features.combat;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.event.EventState;
import dev.myth.api.feature.Feature;
import dev.myth.api.logger.Logger;
import dev.myth.api.utils.PacketUtil;
import dev.myth.api.utils.inventory.InventoryUtils;
import dev.myth.api.utils.rotation.RotationUtil;
import dev.myth.api.utils.ScoreboardUtil;
import dev.myth.api.utils.StringUtil;
import dev.myth.api.utils.TeamUtil;
import dev.myth.api.utils.math.MathUtil;
import dev.myth.events.MoveFlyingEvent;
import dev.myth.events.PacketEvent;
import dev.myth.events.Render3DEvent;
import dev.myth.events.UpdateEvent;
import dev.myth.features.movement.NoSlowFeature;
import dev.myth.features.player.ScaffoldFeature;
import dev.myth.main.ClientMain;
import dev.myth.managers.FeatureManager;
import dev.myth.managers.FriendManager;
import dev.myth.settings.BooleanSetting;
import dev.myth.settings.EnumSetting;
import dev.myth.settings.ListSetting;
import dev.myth.settings.NumberSetting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.*;
import viamcp.ViaMCP;

import java.util.*;

@Feature.Info(
        name = "KillAura",
        description = "Attacks all entities in a radius",
        category = Feature.Category.COMBAT
)
public class KillAuraFeature extends Feature {

    public final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.SINGLE);
    public final NumberSetting switchDelay = new NumberSetting("Switch Delay", 200, 0, 1000, 50)
            .addDependency(() -> mode.getValue() == Mode.SWITCH).setSuffix("ms");
    public final NumberSetting switchAmount = new NumberSetting("Targets", 1, 1, 10, 1)
            .addDependency(() -> mode.getValue() == Mode.SWITCH);
    public final EnumSetting<RotationMode> rotationMode = new EnumSetting<>("Rotation Mode", RotationMode.NORMAL);
    public final EnumSetting<BlockMode> blockMode = new EnumSetting<>("Block Mode", BlockMode.FAKE);
    public final EnumSetting<AttackTiming> attackTiming = new EnumSetting<>("Attack Timing", AttackTiming.PRE);
    public final EnumSetting<AttackMode> attackMode = new EnumSetting<>("Attack Mode", AttackMode.CPS);
    public final EnumSetting<SortMode> sortMode = new EnumSetting<>("Sort Mode", SortMode.DISTANCE);
    public final ListSetting<BypassSettings> bypassSetting = new ListSetting<>("Bypass Settings", BypassSettings.STOP_SPRINTING);
    public final BooleanSetting onlyHittable = new BooleanSetting("Only Attack Hittable", true);
    public final ListSetting<Bones> targetBones = new ListSetting<>("Target Bones", Bones.HEAD);
    public final ListSetting<Targets> targetSetting = new ListSetting<>("Target", Targets.PLAYER);
    public final ListSetting<Checks> checksSetting = new ListSetting<>("Checks", Checks.ALIVE);
    public final BooleanSetting lockView = new BooleanSetting("Lock View", false)
            .addDependency(() -> rotationMode.getValue() == RotationMode.NORMAL);
    public final BooleanSetting lagComp = new BooleanSetting("Prediction", false)
            .addDependency(() -> rotationMode.getValue() != RotationMode.OFF);
    public final NumberSetting lagCompTicks = new NumberSetting("Predict Ticks", 1, 1, 10, 1)
            .addDependency(() -> lagComp.getValue() && rotationMode.getValue() != RotationMode.OFF).setSuffix("t");
    public final NumberSetting range = new NumberSetting("Range", 3, 3, 6, 0.1)
            .setSuffix("m");
    public final NumberSetting minCPS = new NumberSetting("Min Cps", 10, 1, 20, 0.1)
            .addDependency(() -> attackMode.getValue() == AttackMode.CPS);
    public final NumberSetting maxCPS = new NumberSetting("Max Cps", 10, 1, 20, 0.1)
            .addDependency(() -> attackMode.getValue() == AttackMode.CPS);
    public final NumberSetting randomization = new NumberSetting("Randomization", 0, 0, 10, 0.1)
            .addValueAlias(0, "None").addDependency(() -> rotationMode.getValue() == RotationMode.NORMAL);
    public final NumberSetting maxAngleChange = new NumberSetting("Max Angle Change", 30, 1, 180, 0.25)
            .addValueAlias(180, "Instant").addDependency(() -> rotationMode.getValue() == RotationMode.NORMAL);
    public final NumberSetting hitboxExpand = new NumberSetting("Expand Hitbox", 0, 0, 1, 0.01);

    private final HashMap<EntityLivingBase, EntityData> entityDataMap = new HashMap<>();

    public EntityLivingBase target;
    private ArrayList<EntityLivingBase> targets = new ArrayList<>();
    private long lastAttack, lastSwitch;
    private boolean isBlocking, rotateBack;
    private float yaw, pitch;
    private int index, ticksBlocked, lastSlot;

    private AntiBotFeature antiBotFeature;

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {
        if (event.getState() == EventState.PRE)
            entityDataMap.clear();

        if (ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(ScaffoldFeature.class).isEnabled()) {
            target = null;
            return;
        }
        switch (event.getState()) {
            case PRE: {

                if (!canBlock()) {
                    isBlocking = false;
                    ticksBlocked = 0;
                }

                if ((blockMode.is(BlockMode.NONE) || blockMode.is(BlockMode.FAKE)) || target == null) {
                    if (isBlocking) {
                        MC.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        isBlocking = false;
                    }
                }

                targets = MC.theWorld.loadedEntityList.stream().filter(this::canAttackEntity)
                        .map(entity -> (EntityLivingBase) entity)
//                        .filter(entityLivingBase -> getEntityData(entityLivingBase).hittable)
                        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

                EntityLivingBase lastTarget = target;

                if (targets.isEmpty()) {
                    target = null;
                } else {
                    targets.sort(sortMode.getValue().comparator);
                    if (mode.is(Mode.TICK)) {
                        if (index >= targets.size()) index = 0;
                        target = targets.get(index);
                    } else if (mode.is(Mode.SWITCH)) {
                        if (System.currentTimeMillis() - lastSwitch >= switchDelay.getValue()) {
                            index++;
                            if (index >= targets.size() || index > switchAmount.getValue()) index = 0;
                            target = targets.get(index);
                            lastSwitch = System.currentTimeMillis();
//                            doLog(index + " " + target.getName());
                        }
                    } else {
                        target = targets.get(0);
                    }
                }

                if (blockMode.is(BlockMode.NORMAL) || blockMode.is(BlockMode.INTERACT)) {
                    if (isBlocking) {
                        MC.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        isBlocking = false;
                    }
                }

                if (target != null) rotateBack = false;
                if (target == null && lastTarget != null) {
                    if (yaw != getYaw() && pitch != getPitch()) {
                        if (!rotateBack) {
//                            doLog("Starting to rotate back on tick " + MC.thePlayer.ticksExisted);
                            rotateBack = true;
                        }
                    }
                }
                if (rotateBack) {
//                    doLog("Rotating back on tick " + MC.thePlayer.ticksExisted);
                    RotationUtil.doRotation(event, new float[]{getYaw(), getPitch()}, maxAngleChange.getValue().floatValue(), !lockView.getValue());
                    yaw = event.getYaw();
                    pitch = event.getPitch();
                    if (Math.round(MathHelper.wrapAngleTo180_float(yaw)) == Math.round(MathHelper.wrapAngleTo180_float(getYaw())) && Math.round(pitch) == Math.round(getPitch())) {
//                        doLog("Done rotating back on tick " + MC.thePlayer.ticksExisted);
                        rotateBack = false;
                    }
                }

                if (target == null) return;

                if (attackMode.is(AttackMode.SMART)) {
                    if (target != lastTarget) {
                        lastAttack = 0;
                    }
                }

                switch (rotationMode.getValue()) {
                    case NORMAL: {
                        float[] rotations = getEntityData(target).rotations;
                        yaw = rotations[0];
                        pitch = rotations[1];

                        if (randomization.getValue() > 0) {
                            yaw += MathUtil.getRandomValue(-randomization.getValue(), randomization.getValue());
                            pitch += MathUtil.getRandomValue(-randomization.getValue(), randomization.getValue());
                        }

                        RotationUtil.doRotation(event, new float[]{yaw, pitch}, (float) (maxAngleChange.getValue().floatValue() + Math.random() * 10), !lockView.getValue());
                        break;
                    }
                    case REDUCE: {
                        float[] rotations = getEntityData(target).rotations;

//                        Vec3 vec3 = MC.thePlayer.getPositionEyes(1F);
//                        Vec3 vec31 = RotationUtil.getVectorForRotation(pitch, yaw);
//                        Vec3 vec32 = vec3.addVector(vec31.xCoord * 6, vec31.yCoord * 6, vec31.zCoord * 6);
//                        AxisAlignedBB targetBB = target.getEntityBoundingBox().expand(0.4, 0, 0.4);

//                        if (targetBB.calculateIntercept(vec3, vec32) == null) {
                        if (!getEntityData(target).hittable) yaw = rotations[0];
//                        }

                        if (getPlayer().getEntityBoundingBox().intersectsWith(target.getEntityBoundingBox())) {
                            pitch = 90;
                        } else {
                            pitch = rotations[1];
                        }

                        RotationUtil.doRotation(event, new float[]{yaw, pitch}, maxAngleChange.getValue().floatValue(), true);
                        break;
                    }
                }

                yaw = event.getYaw();
                pitch = event.getPitch();

                if (event.getState() == attackTiming.getValue().state) performAttack();

                if (blockMode.is(BlockMode.BLOCKSMC)) {
                    if (!isBlocking && canBlock()) {
//                        sendPacket(new C02PacketUseEntity(target, target.getPositionVector()));
//                        sendPacket(new C02PacketUseEntity(target, C02PacketUseEntity.Action.INTERACT));
//                        MC.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, MC.thePlayer.getHeldItem(), 0, 0, 0));
//                        isBlocking = true;
//                        doLog("Blocking");
                    }
                }

                if (blockMode.is(BlockMode.BLOCKSMC) && canBlock()) {
                    sendPacket(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, MC.thePlayer.getHeldItem(), 0, 0, 0));
                    isBlocking = true;
                }

                break;
            }
            case POST: {

                boolean canBlock = canBlock();

                if (!canBlock || target == null) {
                    ticksBlocked = 0;
                }

                if (target == null) return;

                if (event.getState() == attackTiming.getValue().state) performAttack();

                if (blockMode.is(BlockMode.LEGIT)) {
                }

                if (blockMode.is(BlockMode.LATEST_NCP)) {
                    if (canBlock) {
                        if (getPlayer().ticksExisted % 2 == 0) {
                            int toSlot = getPlayer().inventory.currentItem + 1;
                            if (toSlot > 8) toSlot = 0;
                            sendPacket(new C09PacketHeldItemChange(toSlot));
                            sendPacket(new C09PacketHeldItemChange(MC.thePlayer.inventory.currentItem));
                            MC.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, MC.thePlayer.getHeldItem(), 0, 0, 0));
                            isBlocking = true;
                        }
                    }
                }

                if ((blockMode.is(BlockMode.NORMAL) || blockMode.is(BlockMode.INTERACT)) && canBlock()) {
                    if (!isBlocking) {
                        if (blockMode.is(BlockMode.INTERACT)) {
                            sendPacket(new C02PacketUseEntity(target, target.getPositionVector()));
                            sendPacket(new C02PacketUseEntity(target, C02PacketUseEntity.Action.INTERACT));
                        }
                        MC.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, MC.thePlayer.getHeldItem(), 0, 0, 0));
                        isBlocking = true;
                    }
                }

                if (blockMode.is(BlockMode.WATCHDOG)) {
                    if (canBlock) {
                        if (ticksBlocked >= 4 && isBlocking) {
                            MC.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                            isBlocking = false;
                            ticksBlocked = 0;
                        } else if (ticksBlocked == 1 && !isBlocking) {
                            MC.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, MC.thePlayer.getHeldItem(), 0, 0, 0));
//                            MC.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(MC.thePlayer.getHeldItem()));
                            isBlocking = true;
                        }
                        ticksBlocked++;
                    } else {
                        ticksBlocked = 0;
                    }
                }

                break;
            }
        }
    };

    @Handler
    public final Listener<PacketEvent> packetEventListener = event -> {
        if (event.getState() == EventState.SENDING) {
            if (event.getPacket() instanceof C09PacketHeldItemChange)
                isBlocking = false;
        } else {
            if (!(event.getPacket() instanceof S30PacketWindowItems || event.getPacket() instanceof S2FPacketSetSlot))
                return;

            if (blockMode.is(BlockMode.BLOCKSMC) || blockMode.is(BlockMode.ZONECRAFT)) {

                PacketUtil.sillyS30(event);

                if (blockMode.is(BlockMode.ZONECRAFT))
                    PacketUtil.sillyS2F(event);
            }
        }
    };

    @Handler
    public final Listener<MoveFlyingEvent> moveFlyingEventListener = event -> {
        if (target != null && bypassSetting.is(BypassSettings.MOVE_FIX)) {
            float[] vals = RotationUtil.getSilentMovementValues(yaw);
            event.setYaw(yaw);
            event.setForward(vals[0]);
            event.setStrafe(vals[1]);

            if(vals[0] <= 0) {
                getPlayer().setSprinting(false);
            }
        }
    };

    @Override
    public void onDisable() {
        super.onDisable();

        target = null;

        if (blockMode.is(BlockMode.NORMAL) || blockMode.is(BlockMode.INTERACT) || blockMode.is(BlockMode.WATCHDOG) || blockMode.is(BlockMode.BLOCKSMC) || blockMode.is(BlockMode.LATEST_NCP)) {
            if (isBlocking) {
                MC.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                isBlocking = false;
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        index = 0;
        target = null;
        lastAttack = 0;
        lastSwitch = 0;
        ticksBlocked = 0;

        if (getPlayer() == null) {
            toggle();
        }
    }

    @Handler
    public final Listener<Render3DEvent> render3DEventListener = event -> {
        if (target != null && target.isEntityAlive()) {
//            RenderUtil.drawBoundingBox(target.getEntityBoundingBox().expand(0.2, 0.2, 0.2), new Color(146, 224, 155, 120), true, true, 0);
        }
    };

    public void performAttack() {
        boolean shouldAttack = true;
        if (onlyHittable.getValue() && !getEntityData(target).hittable) shouldAttack = false;
        if (getPlayer().getDistanceToEntity(target) > range.getValue()) shouldAttack = false;

        if (blockMode.is(BlockMode.ROD)) {

            int rodSlot = InventoryUtils.getSlot(Items.fishing_rod);

            if (lastSlot == rodSlot) lastSlot = InventoryUtils.getSlot(Items.diamond_sword);

            if (MC.thePlayer.inventory.currentItem == rodSlot) {
                if (System.currentTimeMillis() - lastAttack > 100) MC.thePlayer.inventory.currentItem = lastSlot;
                return;
            }

            if (rodSlot != -1 && (System.currentTimeMillis() - lastAttack > 100 && target.hurtTime == 0)) {

                if (MC.thePlayer.inventory.currentItem != rodSlot) lastSlot = MC.thePlayer.inventory.currentItem;
                MC.thePlayer.inventory.currentItem = rodSlot;

                MC.playerController.syncCurrentPlayItem();
                MC.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, MC.thePlayer.getHeldItem(), 0, 0, 0));

//            MC.thePlayer.inventory.currentItem = lastSlot;

                lastAttack = System.currentTimeMillis();
                return;
            }
        }

        long time = (long) (1000L / getCPS());
        shouldAttack = System.currentTimeMillis() - lastAttack >= time && shouldAttack;
        if (attackMode.is(AttackMode.SMART)) {
            shouldAttack = target.hurtTime == 0 && shouldAttack;
        }
        if (shouldAttack) {
            if (blockMode.is(BlockMode.ZONECRAFT)) {
                if (isBlocking) {
                    MC.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    isBlocking = false;
                }
            }

            if (mode.is(Mode.MULTI)) {
                for (EntityLivingBase entityLivingBase : targets) {
                    doAttack(entityLivingBase);
                }
            } else {
                doAttack(target);
            }
            lastAttack = System.currentTimeMillis();
            if (mode.is(Mode.TICK)) index++;
        } else if (blockMode.is(BlockMode.ZONECRAFT) && canBlock()) {
            sendPacket(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, MC.thePlayer.getHeldItem(), 0, 0, 0));
            isBlocking = true;
        }
    }

    public void doAttack(EntityLivingBase target) {
        if (ViaMCP.getInstance().getVersion() <= ProtocolVersion.v1_8.getVersion())
            MC.thePlayer.swingItem();

        MC.playerController.syncCurrentPlayItem();
        MC.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));

        if (bypassSetting.is(BypassSettings.STOP_SPRINTING)) {
//            MC.thePlayer.setSprinting(false);
//            MC.thePlayer.motionX *= 0.6D;
//            MC.thePlayer.motionZ *= 0.6D;
            MC.thePlayer.attackTargetEntityWithCurrentItem(target);
        } else {
            if (getPlayer().fallDistance > 0 && !getPlayer().onGround && !getPlayer().isOnLadder() && !getPlayer().isInWater() && !getPlayer().isPotionActive(Potion.blindness) && getPlayer().ridingEntity == null) {
                getPlayer().onCriticalHit(target);
            }
        }


        if (ViaMCP.getInstance().getVersion() > ProtocolVersion.v1_8.getVersion())
            MC.thePlayer.swingItem();
    }

    public double getCPS() {
        return MathUtil.getRandomValue(minCPS.getValue(), maxCPS.getValue());
    }

    public Vec3 getClosestBone(EntityLivingBase target) {
        Vec3 vec3 = null;
        if (target instanceof EntityPlayer) {
            Vec3 player = MC.thePlayer.getPositionEyes(1);
            for (Bones bone : targetBones.getValue()) {
                Vec3 vec = new Vec3(target.posX, target.posY + bone.height, target.posZ);
                if (vec3 == null || player.distanceTo(vec) <= player.distanceTo(vec3)) {
                    vec3 = vec;
                }
            }
        }
        if (vec3 == null) {
            vec3 = target.getPositionEyes(1);
        }
        return vec3;
    }

    public boolean canAttackEntity(Entity entity) {
        if (checksSetting.is(Checks.ALIVE) && !entity.isEntityAlive()) return false;
        if (checksSetting.is(Checks.INVISIBLE) && entity.isInvisible()) return false;
        if (entity instanceof EntityOtherPlayerMP) {
            if (antiBotFeature == null)
                antiBotFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(AntiBotFeature.class);
            if (antiBotFeature.isBot((EntityLivingBase) entity)) return false;

            FriendManager friendManager = ClientMain.INSTANCE.manager.getManager(FriendManager.class);
            if (friendManager.isFriend(entity.getName())) return false;

            if (!targetSetting.is(Targets.PLAYER)) return false;
            if (checksSetting.is(Checks.TEAM) && TeamUtil.isOnSameTeamName((EntityPlayer) entity)/*((EntityPlayer) entity).isOnSameTeam(MC.thePlayer)*/)
                return false;
            String scoreboard = StringUtil.removeFormatting(ScoreboardUtil.getScoreboardTitle());
            if (scoreboard.contains("THE") && scoreboard.contains("PIT")) {
                Collection<Score> scores = ScoreboardUtil.getScoreCollection();

                boolean active = false, isTDM = false;

                for (Score score : scores) {
                    ScorePlayerTeam scoreplayerteam = ScoreboardUtil.getScoreboard().getPlayersTeam(score.getPlayerName());
                    String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam, score.getPlayerName());
                    if (s1.contains("Remaining: ")) {
                        active = true;
                    }
                    if (s1.contains("Event: ") && (s1.contains("DEATHMATCH") || s1.contains("TDM"))) {
                        isTDM = true;
                    }
                }

                if (active && isTDM) {
                    if (TeamUtil.isOnSameTeamName((EntityPlayer) entity)) return false;
                }
            }
        } else if (entity instanceof EntityMob || entity instanceof EntityWaterMob) {
            if (!targetSetting.is(Targets.MOB)) return false;
        } else if (entity instanceof EntityAnimal) {
            if (!targetSetting.is(Targets.ANIMAL)) return false;
        } else {
            return false;
        }

        return getEntityData((EntityLivingBase) entity).distance <= range.getValue();
    }

    public boolean canBlock() {
        return getPlayer().getCurrentEquippedItem() != null && getPlayer().getCurrentEquippedItem().getItem() instanceof ItemSword && !getPlayer().isUsingItem();
    }

    public boolean shouldRenderBlocking() {
        return isBlocking || ((blockMode.is(BlockMode.FAKE) || blockMode.is(BlockMode.WATCHDOG) || blockMode.is(BlockMode.ZONECRAFT)) && target != null && canBlock());
    }

    @Override
    public String getSuffix() {
        return mode.getValue().toString();
    }

    public EntityData getEntityData(EntityLivingBase entity) {
        EntityData entityData = entityDataMap.getOrDefault(entity, null);
        if (entityData == null) {
            Vec3 closestBone = getClosestBone(entity);
            int lagCompTicks = this.lagCompTicks.getValue().intValue();

            Vec3 player = MC.thePlayer.getPositionEyes(1);
            Vec3 vec31 = RotationUtil.getVectorForRotation(pitch, yaw);

            double range = this.range.getValue();

            Vec3 vec32 = player.addVector(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range);

            double expand = hitboxExpand.getValue();

            AxisAlignedBB targetBB = entity.getEntityBoundingBox()
                    .expand(expand, expand, expand);

            if (lagComp.getValue()) {
                closestBone = closestBone.addVector(
                        (entity.posX - entity.prevPosX) * lagCompTicks,
                        (entity.posY - entity.prevPosY) * lagCompTicks,
                        (entity.posZ - entity.prevPosZ) * lagCompTicks);
                targetBB = targetBB.offset(
                        (entity.posX - entity.prevPosX) * lagCompTicks,
                        (entity.posY - entity.prevPosY) * lagCompTicks,
                        (entity.posZ - entity.prevPosZ) * lagCompTicks);
            }

            boolean hittable = targetBB.calculateIntercept(player, vec32) != null;

            entityData = new EntityData(closestBone.distanceTo(player), RotationUtil.getRotationsToVector(closestBone, player), hittable, targetBB);
            entityDataMap.put(entity, entityData);
        }
        return entityData;
    }

    public static class EntityData {
        public double distance;
        public float[] rotations;
        public boolean hittable;
        public AxisAlignedBB bb;

        public EntityData(double distance, float[] rotations, boolean hittable, AxisAlignedBB bb) {
            this.distance = distance;
            this.rotations = rotations;
            this.hittable = hittable;
            this.bb = bb;
        }
    }

    public enum Mode {
        SINGLE("Single"),
        SWITCH("Switch"),
        TICK("Tick"),
        MULTI("Multi");

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum RotationMode {
        OFF("Off"),
        NORMAL("Normal"),
        REDUCE("Reduce");

        private final String name;

        RotationMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum BlockMode {
        NONE("None"),
        FAKE("Fake"),
        ROD("Rod"),
        LEGIT("Legit"),
        WATCHDOG("Watchdog"),
        NORMAL("Normal"),
        LATEST_NCP("Latest NCP"),
        INTERACT("Interact"),
        BLOCKSMC("BlocksMC"),
        ZONECRAFT("Zonecraft");

        private final String name;

        BlockMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum AttackTiming {
        PRE("Pre", EventState.PRE),
        POST("Post", EventState.POST);

        private final String name;
        private final EventState state;

        AttackTiming(String name, EventState state) {
            this.name = name;
            this.state = state;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum Targets {
        PLAYER("Players"),
        MOB("Mobs"),
        ANIMAL("Animals");

        private final String name;

        Targets(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum AttackMode {
        CPS("CPS"),
        SMART("Smart");

        private final String name;

        AttackMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum Checks {
        ALIVE("Alive"),
        TEAM("Team"),
        INVISIBLE("Invisible");

        private final String name;

        Checks(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum SortMode {
        DISTANCE("Distance", (o1, o2) -> {
            final KillAuraFeature aura = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(KillAuraFeature.class);
            return Double.compare(aura.getEntityData(o1).distance, aura.getEntityData(o2).distance);
        }),
        HEALTH("Health", Comparator.comparingDouble(EntityLivingBase::getHealth)),
        HURT_TIME("Hurt Time", (o1, o2) -> Integer.compare(20 - o2.hurtResistantTime, 20 - o1.hurtResistantTime));

        private final String name;
        private final Comparator<EntityLivingBase> comparator;

        SortMode(String name, Comparator<EntityLivingBase> comparator) {
            this.name = name;
            this.comparator = comparator;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum BypassSettings {
        STOP_SPRINTING("Stop Sprinting"),
        MOVE_FIX("Move Fix");

        private final String name;

        BypassSettings(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum Bones {
        HEAD("Head", 1.6),
        CHEST("Chest", 1.2),
        PELVIS("Pelvis", 0.8),
        FEET("Feet", 0.2);

        private final String name;
        private final double height;

        Bones(String name, double height) {
            this.name = name;
            this.height = height;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
