/**
 * @project hakarware
 * @author CodeMan
 * @at 24.07.23, 20:44
 */

package cc.swift.module.impl.combat;

import cc.swift.Swift;
import cc.swift.events.*;
import cc.swift.module.Module;
import cc.swift.module.impl.combat.KillAuraModule.AutoBlockMode;
import cc.swift.module.impl.combat.KillAuraModule.KeepSprintMode;
import cc.swift.module.impl.combat.KillAuraModule.RotationMode;
import cc.swift.module.impl.combat.KillAuraModule.SortingMode;
import cc.swift.module.impl.combat.KillAuraModule.TargetMode;
import cc.swift.module.impl.player.NukerModule;
import cc.swift.util.RotationHandler;
import cc.swift.util.player.RotationUtil;
import cc.swift.value.impl.BooleanValue;
import cc.swift.value.impl.DoubleValue;
import cc.swift.value.impl.ModeValue;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

public final class KillAuraModule extends Module {

    public final DoubleValue cps = new DoubleValue("CPS", 10D, 1, 20, 0.1);
    public final DoubleValue range = new DoubleValue("Range", 3D, 3, 6, 0.1);
    public final ModeValue<TargetMode> targetMode = new ModeValue<>("Mode", TargetMode.values());
    public final DoubleValue preRange = new DoubleValue("Pre Range", 3D, 0, 10, 0.1);
    public final ModeValue<SortingMode> sortMode = new ModeValue<>("Sorting", SortingMode.values());
    
    public final ModeValue<SortingMode> secondarySortMode = new ModeValue<>("Secondary Sorting", SortingMode.DISTANCE, SortingMode.HEALTH, SortingMode.FOV).setDependency(() -> sortMode.getValue() == SortingMode.HURTTIME);
    public final ModeValue<RotationMode> rotationMode = new ModeValue<>("Rotations", RotationMode.values());
    public final ModeValue<AutoBlockMode> autoBlockMode = new ModeValue<>("AutoBlock", AutoBlockMode.values());
    public final ModeValue<KeepSprintMode> keepSprint = new ModeValue<>("Keep Sprint", KeepSprintMode.values());
    public final BooleanValue raycast = new BooleanValue("Raycast", false);
    public final BooleanValue hvh = new BooleanValue("HvH", false);
    public final BooleanValue teamCheck = new BooleanValue("Team Check", true);
    public final BooleanValue switchTargets = new BooleanValue("Switch", false);

    public EntityLivingBase target;
    private EntityLivingBase lastTarget;
    private long nextAttack;

    private boolean blocking;

    private NukerModule nukerModule;
    private AntiBotModule antiBotModule;

    public KillAuraModule() {
        super("KillAura", Category.COMBAT);
        this.registerValues(this.cps, this.range, this.preRange, this.targetMode,  this.rotationMode, this.sortMode, this.secondarySortMode, this.autoBlockMode, this.keepSprint, this.raycast, this.hvh, this.teamCheck);
    }

    @Handler
    public final Listener<RotationEvent> rotationEventListener = event -> {
        if (target == null) return;
        Vec3 eyesPos = RotationUtil.getEyePosition();
        Vec3 targetPos = RotationUtil.getBestVector(this.target.getEntityBoundingBox(), eyesPos);

        if (this.rotationMode.getValue() == RotationMode.NORMAL && !nukerModule.rotating) {
            float[] rotations = RotationUtil.getRotationsToVector(eyesPos, targetPos);
            RotationUtil.fixGCD(rotations, new float[]{Swift.INSTANCE.getRotationHandler().getLastYaw(), Swift.INSTANCE.getRotationHandler().getLastPitch()});
            event.setYaw(rotations[0]);
            event.setPitch(rotations[1]);
        }
    };

    @Handler
    public final Listener<UpdateWalkingPlayerEvent> updateWalkingPlayerEventListener = event -> {
        if (nukerModule == null) {
            nukerModule = Swift.INSTANCE.getModuleManager().getModule(NukerModule.class);
        }

        if (antiBotModule == null) {
            antiBotModule = Swift.INSTANCE.getModuleManager().getModule(AntiBotModule.class);
        }

        if (event.getState() == EventState.PRE) {
            this.target = null;

            ArrayList<EntityLivingBase> entityLivingBases = new ArrayList<>();

            for (Entity entity : mc.theWorld.loadedEntityList) {
                if (!(entity instanceof EntityLivingBase)) continue;
                if (entity == mc.thePlayer || entity instanceof EntityArmorStand) continue;

                if (entity instanceof EntityVillager) continue;

                EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                if (entityLivingBase.deathTime > 0) continue;

                if (entity instanceof EntityPlayer) {
                    EntityPlayer entityPlayer = (EntityPlayer) entity;
                    if (entityPlayer.getName().startsWith("§")) continue;
                    if (antiBotModule.isBot(entityPlayer)) continue;
                }

                if (this.teamCheck.getValue() && entity instanceof EntityPlayer) {
                    try {
                        if (mc.thePlayer.isOnSameTeam((EntityLivingBase) entity) || mc.thePlayer.getDisplayName().getUnformattedText()
                                .startsWith(entity.getDisplayName().getUnformattedText().substring(0, 2)))
                            continue;
                    } catch (Exception ignore) {
                    }
                }

                double distance = entityLivingBase.getDistanceToEntity(mc.thePlayer);
                if (distance > this.range.getValue() + this.preRange.getValue()) continue;

                entityLivingBases.add(entityLivingBase);
            }

            if (this.autoBlockMode.getValue() == AutoBlockMode.FAKE)
                mc.thePlayer.setRenderBlocking(!entityLivingBases.isEmpty());

            if (!this.canBlock() && this.blocking) {
                mc.thePlayer.setRenderBlocking(false);
                this.blocking = false;
                if (this.autoBlockMode.getValue() == AutoBlockMode.REAL)
                    mc.gameSettings.keyBindUseItem.setKeyDown(false);
            }

            if (entityLivingBases.isEmpty()) {
                if (this.blocking) {
                    if (this.autoBlockMode.getValue() != AutoBlockMode.REAL)
                        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    mc.thePlayer.setRenderBlocking(false);
                    this.blocking = false;
                    mc.gameSettings.keyBindUseItem.setKeyDown(false);
                }
                return;
            }

            switch (autoBlockMode.getValue()) {
                case NCP:
                    if (this.blocking && this.canBlock()) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        mc.thePlayer.setRenderBlocking(false);
                        this.blocking = false;
                    }
                    break;
                case HYPIXEL:
                    if (this.canBlock()) {
                        int slot = mc.thePlayer.inventory.currentItem;
                        while (slot == mc.thePlayer.inventory.currentItem || slot == mc.thePlayer.inventory.currentItem + 1) {
                            slot = ThreadLocalRandom.current().nextInt(9);
                        }
                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                        this.blocking = true;
                        mc.thePlayer.setRenderBlocking(true);
                    }
                    break;
            }
            
            entityLivingBases.sort(this.sortMode.getValue().getComparator());
            if(sortMode.getValue() == SortingMode.HURTTIME){
                Comparator<EntityLivingBase> secondaryComparator = this.secondarySortMode.getValue().getComparator();

                entityLivingBases.sort((entity1, entity2) -> {
                    int primaryCompare = sortMode.getValue().comparator.compare(entity1, entity2);
                    if (primaryCompare != 0) {
                        return primaryCompare;
                    } else {
                        return secondaryComparator.compare(entity1, entity2);
                    }
                });
            }



            // op two-target switch aurora
            switch (targetMode.getValue()){
                case SINGLE:
                    this.target = entityLivingBases.get(0);
                    break;
                case SWITCH:
                    if (this.switchTargets.getValue()) {
                        if (lastTarget != null && entityLivingBases.get(0) == lastTarget)
                                entityLivingBases.add(entityLivingBases.remove(0));
                    }
                    this.target = entityLivingBases.get(0);
                    break;
            }

            Vec3 eyesPos = RotationUtil.getEyePosition();
            Vec3 targetPos = RotationUtil.getBestVector(this.target.getEntityBoundingBox(), eyesPos);

//            if (this.rotationMode.getValue() == RotationMode.NORMAL && !nukerModule.rotating) {
//                float[] rotations = RotationUtil.getRotationsToVector(eyesPos, targetPos);
//                RotationUtil.fixGCD(rotations, new float[]{event.getLastYaw(), event.getLastPitch()});
//                event.setYaw(rotations[0]);
//                event.setPitch(rotations[1]);
//            }

            double distance = eyesPos.distanceTo(targetPos);

            // ping compensation 😱😱😱😫😫😫😯😯😯
            int ping = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID()).getResponseTime();
            int ticks = (int) (Math.ceil(ping / 50D) + 1);

            if (distance <= this.range.getValue() && ((System.currentTimeMillis() > this.nextAttack) || (this.target.hurtTime <= ticks && this.hvh.getValue()))) {
                RotationHandler handler = Swift.INSTANCE.getRotationHandler();
                Entity entity = this.raycast.getValue() ? RotationUtil.raycast(this.range.getValue(), handler.getYaw(), handler.getPitch()) : this.target;

                if (entity != null) {
                    mc.thePlayer.swingItem();
                    switch (this.keepSprint.getValue()) {
                        case VANILLA:
                            mc.getNetHandler().getNetworkManager().sendPacket(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
                            break;
                        case OFF:
                            mc.playerController.attackEntity(mc.thePlayer, entity);
                            break;
                    }
                    this.nextAttack = System.currentTimeMillis() + (long) (1000 / cps.getValue());
                    if (entityLivingBases.size() > 2) {
                        lastTarget = this.target;
                    }
                }
            }

            switch (autoBlockMode.getValue()) {
                case VANILLA:
                    if (!this.blocking) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                        mc.thePlayer.setRenderBlocking(true);
                        this.blocking = true;
                    }
                    break;
                case REAL:
                    mc.gameSettings.keyBindUseItem.setKeyDown(true);
                    this.blocking = true;
                    break;
            }
        } else {
            if (this.autoBlockMode.getValue() == AutoBlockMode.NCP) {
                if (!this.blocking && this.target != null && this.canBlock()) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                    mc.thePlayer.setRenderBlocking(true);
                    this.blocking = true;
                }
            }
        }
        if (this.blocking && this.autoBlockMode.getValue() != AutoBlockMode.REAL) {
            mc.gameSettings.keyBindUseItem.setKeyDown(false);
        }
    };

    @Handler
    public final Listener<PacketEvent> packetEventListener = event -> {
        if (event.getDirection() != EventState.SEND || event.getState() != EventState.PRE) return;

        if (event.getPacket() instanceof C09PacketHeldItemChange) {
            if (this.blocking) {
                this.blocking = false;
                mc.thePlayer.setRenderBlocking(false);
            }
        }
    };

    @Override
    public void onDisable() {
        if (this.blocking && this.canBlock()) {
            switch (this.autoBlockMode.getValue()) {
                case REAL:
                    mc.gameSettings.keyBindUseItem.setKeyDown(false);
                    break;
                default:
                    mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    break;
            }
        }
        if (this.blocking || this.autoBlockMode.getValue() == AutoBlockMode.FAKE)
            mc.thePlayer.setRenderBlocking(false);
        this.blocking = false;

        this.target = null;
    }

    @Override
    public void onEnable() {
        // Swift.INSTANCE.getNotificationManager().addNotification(new Notification(Notification.Type.INFO, "Hmmmmm", "This is a test message", 2000));
    }

    public boolean canBlock() {
        return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    enum TargetMode{
        SINGLE, SWITCH
    }

    enum AutoBlockMode {
        OFF, HYPIXEL, NCP, VANILLA, REAL, FAKE
    }

    enum RotationMode {
        OFF, NORMAL
    }

    enum KeepSprintMode {
        OFF, VANILLA
    }

    @AllArgsConstructor
    @Getter
    enum SortingMode {
        DISTANCE(Comparator.comparingDouble(o -> o.getDistanceToEntity(mc.thePlayer))),
        HEALTH(Comparator.comparingDouble(EntityLivingBase::getHealth)),
        HURTTIME(Comparator.comparingDouble(o -> o.hurtTime)),
        FOV(Comparator.comparingDouble(o -> Math.abs(MathHelper.wrapAngleTo180_float(RotationUtil.getRotationsToVector(RotationUtil.getEyePosition(), RotationUtil.getBestVector(o.getEntityBoundingBox(), RotationUtil.getEyePosition()))[0] - mc.thePlayer.rotationYaw))));

        private final Comparator<EntityLivingBase> comparator;
    }

}
