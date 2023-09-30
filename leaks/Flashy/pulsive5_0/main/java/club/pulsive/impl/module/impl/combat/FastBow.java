package club.pulsive.impl.module.impl.combat;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.impl.event.player.PlayerMotionEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.property.implementations.DoubleProperty;
import club.pulsive.impl.property.implementations.MultiSelectEnumProperty;
import club.pulsive.impl.util.Rotation;
import club.pulsive.impl.util.client.Logger;
import club.pulsive.impl.util.entity.EntityValidator;
import club.pulsive.impl.util.entity.impl.AliveCheck;
import club.pulsive.impl.util.entity.impl.ConstantDistanceCheck;
import club.pulsive.impl.util.entity.impl.EntityCheck;
import club.pulsive.impl.util.entity.impl.TeamsCheck;
import club.pulsive.impl.util.network.PacketUtil;
import club.pulsive.impl.util.player.PlayerUtil;
import club.pulsive.impl.util.player.RotationUtil;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@ModuleInfo(name = "FastBow", renderName = "Yee", category = Category.COMBAT)
public class FastBow extends Module {

    private final DoubleProperty packets = new DoubleProperty("Packets", 20, 5, 100, 1);
    private final DoubleProperty range = new DoubleProperty("Range", 25, 1, 100, 1);
    private final MultiSelectEnumProperty<PlayerUtil.TARGETS> targetsProperty = new MultiSelectEnumProperty<>("Targets", Lists.newArrayList(PlayerUtil.TARGETS.PLAYERS), PlayerUtil.TARGETS.values());

    private EntityValidator entityValidator = new EntityValidator();
    private final List<EntityLivingBase> targets = new ArrayList<>();

    @Override
    public void onEnable() {
        super.onEnable();
        targets.clear();
    }

    @Override
    public void init() {
        super.init();
        targets.clear();
    }

    @EventHandler
    private final Listener<PlayerMotionEvent> playerMotionEventListener = event -> {
        entityValidator = new EntityValidator();
        final AliveCheck aliveCheck = new AliveCheck();
        final EntityCheck entityCheck = new EntityCheck(targetsProperty.isSelected(PlayerUtil.TARGETS.PLAYERS), targetsProperty.isSelected(PlayerUtil.TARGETS.ANIMALS), targetsProperty.isSelected(PlayerUtil.TARGETS.MOBS), targetsProperty.isSelected(PlayerUtil.TARGETS.INVISIBLE));
        final TeamsCheck teamsCheck = new TeamsCheck(targetsProperty.isSelected(PlayerUtil.TARGETS.TEAMS));
        entityValidator.add(aliveCheck);
        entityValidator.add(new ConstantDistanceCheck(range.getValue().floatValue()));
        entityValidator.add(entityCheck);
        entityValidator.add(teamsCheck);
        targets.sort(new DistanceSorter());
        updateTargets();
        if (!event.isPost() || mc.theWorld == null)
            return;
        mc.timer.timerSpeed = 1.0F;
        final ItemStack heldItem = mc.thePlayer.getHeldItem();

        if (!mc.thePlayer.isUsingItem() || heldItem == null || !(heldItem.getItem() instanceof ItemBow))
            return;

        if(targets.isEmpty()) return;
        EntityLivingBase target = targets.get(0);
       // Logger.print("eee");

       // PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(target.posX - 3, target.posY - (1/64), target.posZ - 3, true));
        mc.thePlayer.setPosition(target.posX - 3, mc.thePlayer.posY, target.posZ - 3);

        if(target.hurtTime > 0)
            Logger.print("hurt");

        final boolean checkRotations = target != null;

            if(checkRotations){
                Rotation rotation = RotationUtil.getBowAngles(target);
                RotationUtil.applySmoothing(new float[]{event.getPrevYaw(), event.getPrevPitch()}, 10.5f, new float[]{rotation.getRotationYaw(), rotation.getRotationPitch()});
                RotationUtil.applyGCD(new float[]{rotation.getRotationYaw(), rotation.getRotationPitch()}, new float[]{event.getPrevYaw(), event.getPrevPitch()});
                mc.thePlayer.rotationYaw = rotation.getRotationYaw();
                mc.thePlayer.rotationPitch = rotation.getRotationPitch();
                event.setYaw(rotation.getRotationYaw());
                event.setPitch(rotation.getRotationPitch());
            }
        // Where the hit originates from (your eye-pos)
        final Vec3 origin = RotationUtil.getHitOrigin(this.mc.thePlayer);
        // Calculate where your hit vector will be on the entity
        final MovingObjectPosition intercept = RotationUtil.calculateIntercept(
                RotationUtil.getHittableBoundingBox(target,0.1),
                origin,
               event.getYaw(),
                event.getPitch(),
                range.getValue().floatValue());

        if(intercept != null) {
            mc.thePlayer.getHeldItem().setItemDamage(0);
            PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));
            for (int i = 0; i < packets.getValue(); i++) {
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer(true));
            }
            PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        }
    };

    private void updateTargets() {
        targets.clear();

        final List<Entity> entities = mc.theWorld.loadedEntityList;

        for (int i = 0, entitiesSize = entities.size(); i < entitiesSize; i++) {
            final Entity entity = entities.get(i);
            if (entity instanceof EntityLivingBase) {
                final EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                if (entityValidator.validate(entityLivingBase) && entityLivingBase.posY < 110) {
                    this.targets.add(entityLivingBase);
                }
            }
        }
    }

    private final static class DistanceSorter implements Comparator<EntityLivingBase> {
        public int compare(EntityLivingBase o1, EntityLivingBase o2) {
            return -Double.compare(mc.thePlayer.getDistanceToEntity(o1), mc.thePlayer.getDistanceToEntity(o2));
        }
    }
}
