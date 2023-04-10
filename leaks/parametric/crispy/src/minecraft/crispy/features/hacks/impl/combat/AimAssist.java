package crispy.features.hacks.impl.combat;

import crispy.features.event.Event;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.util.rotation.LookUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.superblaubeere27.valuesystem.BooleanValue;
import net.superblaubeere27.valuesystem.NumberValue;

import static crispy.util.rotation.LookUtils.getPitchChange;
import static crispy.util.rotation.LookUtils.getYawChange;


@HackInfo(name = "AimAssist", category = Category.COMBAT)
public class AimAssist extends Hack {
    public static float sYaw, sPitch;
    private final int delay = 8;

    NumberValue<Integer> FOV = new NumberValue<Integer>("FOV", 360, 1, 360);
    NumberValue<Float> YawJitter = new NumberValue<Float>("Yaw Jitter", 0.2F, 0.0F, 5F);
    NumberValue<Float> PitchJitter = new NumberValue<Float>("Pitch Jitter", 0.2F, 0.0F, 5F);
    NumberValue<Double> SmoothYaw = new NumberValue<Double>("Smooth Yaw", 0.1D, 0.1D, 1D);
    NumberValue<Double> SmoothPitch = new NumberValue<Double>("Smooth Pitch", 3D, 1D, 50D);
    BooleanValue Invisibles = new BooleanValue("Invisibles", false);
    BooleanValue Players = new BooleanValue("Players", true);
    BooleanValue Animals = new BooleanValue("Animals", true);
    BooleanValue Monsters = new BooleanValue("Monsters", true);
    BooleanValue Villagers = new BooleanValue("Villagers", true);
    BooleanValue Teams = new BooleanValue("Teams", true);
    private EntityLivingBase target;
    private long current, last;
    private float yaw, pitch;
    private boolean others;


    @Override
    public void onEvent(Event e) {
        if (e instanceof EventUpdate) {

            target = getClosest(mc.playerController.getBlockReachDistance());
            if (target == null)
                return;

            updateTime();
            yaw = mc.thePlayer.rotationYaw;
            sYaw = mc.thePlayer.rotationYaw;
            sPitch = mc.thePlayer.rotationPitch;
            pitch = mc.thePlayer.rotationPitch;

            if (target.getDistanceToEntity(mc.thePlayer) < 8) {

                if (mc.thePlayer.isSwingInProgress) {
                    if (target != null) {
                        stepAngle();
                    }
                }
            }
        }
    }

    private int randomNumber() {
        return -1 + (int) (Math.random() * ((1 - (-1)) + 1));
    }

    private void smoothAim() {
        double randomYaw = YawJitter.getObject();
        double randomPitch = PitchJitter.getObject();
        float targetYaw = getYawChange(sYaw, target.posX + randomNumber() * randomYaw, target.posZ + randomNumber() * randomYaw);
        float yawFactor = (float) (targetYaw / SmoothYaw.getObject());
        float[] fixedYaw = LookUtils.fixedSensitivity(mc.gameSettings.mouseSensitivity, yawFactor + sYaw, 0);
        mc.thePlayer.rotationYaw = fixedYaw[0];

        sYaw += yawFactor;
        float targetPitch = getPitchChange(sPitch, target, target.posY + randomNumber() * randomPitch);
        float pitchFactor = (float) (targetPitch / SmoothPitch.getObject());

        sPitch += pitchFactor;
    }

    private void stepAngle() {
        float xz = YawJitter.getObject();
        float y = PitchJitter.getObject();
        double pitchFactor = SmoothPitch.getObject();
        double yawFactor = SmoothYaw.getObject();
        float targetYaw = getYawChange(mc.thePlayer.rotationYaw, target.posX + randomNumber() * xz, target.posZ + randomNumber() * xz);

        if (targetYaw > 0 && targetYaw > yawFactor) {
            float[] fixThing = LookUtils.fixedSensitivity(mc.gameSettings.mouseSensitivity, mc.thePlayer.rotationYaw += yawFactor, 0);
            mc.thePlayer.rotationYaw = fixThing[0];
        } else if (targetYaw < 0 && targetYaw < -yawFactor) {
            float[] fixThing = LookUtils.fixedSensitivity(mc.gameSettings.mouseSensitivity, mc.thePlayer.rotationYaw -= yawFactor, 0);
            mc.thePlayer.rotationYaw = fixThing[0];
        } else {
            mc.thePlayer.rotationYaw += targetYaw;
        }
    }


    private void updateTime() {
        current = (System.nanoTime() / 1000000L);
    }

    private void resetTime() {
        last = (System.nanoTime() / 1000000L);
    }

    private EntityLivingBase getClosest(double range) {
        double dist = range;
        EntityLivingBase target = null;
        for (Object object : Minecraft.theWorld.loadedEntityList) {
            Entity entity = (Entity) object;
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase player = (EntityLivingBase) entity;
                if (canAttack(player)) {
                    double currentDist = mc.thePlayer.getDistanceToEntity(player);
                    if (currentDist <= dist) {
                        dist = currentDist;
                        target = player;
                    }
                }
            }
        }
        return target;
    }

    private boolean canAttack(EntityLivingBase player) {
        if (player instanceof EntityPlayer || player instanceof EntityAnimal || player instanceof EntityMob || player instanceof EntityVillager) {
            if (player instanceof EntityPlayer && !Players.getObject())
                return false;
            if (player instanceof EntityAnimal && !Animals.getObject())
                return false;
            if (player instanceof EntityMob && !Monsters.getObject())
                return false;
            if (player instanceof EntityVillager && !Villagers.getObject())
                return false;
        }
        if (player.isOnSameTeam(mc.thePlayer) && Teams.getObject())
            return false;
        if (player.isInvisible() && !Invisibles.getObject())
            return false;
        if (!player.isEntityAlive() && player.getHealth() == 0.0)
            return false;
        if (!isInFOV(player, FOV.getObject()))
            return false;


        return player != mc.thePlayer && mc.thePlayer.getDistanceToEntity(player) <= 8 && player.ticksExisted > 10;
    }


    private boolean isInFOV(EntityLivingBase entity, double angle) {
        angle *= .5D;
        double angleDiff = getAngleDifference(mc.thePlayer.rotationYaw, getRotations(entity.posX, entity.posY, entity.posZ)[0]);
        return (angleDiff > 0 && angleDiff < angle) || (-angle < angleDiff && angleDiff < 0);
    }

    private float getAngleDifference(float dir, float yaw) {
        float f = Math.abs(yaw - dir) % 360F;
        float dist = f > 180F ? 360F - f : f;
        return dist;
    }

    private float[] getRotations(double x, double y, double z) {
        double diffX = x + .5D - mc.thePlayer.posX;
        double diffY = (y + .5D) / 2D - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double diffZ = z + .5D - mc.thePlayer.posZ;

        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180D / Math.PI) - 90F;
        float pitch = (float) -(Math.atan2(diffY, dist) * 180D / Math.PI);

        return new float[]{yaw, pitch};
    }
}
