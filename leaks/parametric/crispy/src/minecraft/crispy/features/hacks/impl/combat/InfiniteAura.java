package crispy.features.hacks.impl.combat;

import crispy.Crispy;
import crispy.features.event.Event;
import crispy.features.event.impl.player.EventPacket;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.util.path.MyGayPathfinder;
import crispy.util.path.Vec3;
import crispy.util.time.TimeHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.superblaubeere27.valuesystem.BooleanValue;
import net.superblaubeere27.valuesystem.NumberValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@HackInfo(name = "InfiniteAura", category = Category.COMBAT)
public class InfiniteAura extends Hack {

    private EntityLivingBase target;
    private long current, last;
    private int delay = 8;
    public static boolean canReach;
    private float yaw, pitch;
    double animation;
    boolean Attacked;
    private ArrayList<Vec3> path = new ArrayList<>();
    private boolean others;
    private List<EntityLivingBase> targets = new CopyOnWriteArrayList<>();
    public static float sYaw, sPitch;
    public static TimeHelper timer = new TimeHelper();
    private List<Vec3>[] test = new ArrayList[50];


    NumberValue<Integer> Delay = new NumberValue<Integer>("Delay", 10000, 1000, 10000);

    NumberValue<Double> Reach = new NumberValue<Double>("Reach", 30D, 0D, 999D);

    BooleanValue NoSwing = new BooleanValue("NoSwing", false);

    NumberValue<Integer> MaxTargets = new NumberValue<Integer>("MaxTargets", 1, 1, 50);

    BooleanValue Invisibles = new BooleanValue("Invisibles", false);

    BooleanValue Players = new BooleanValue("Players", true);

    BooleanValue Animals = new BooleanValue("Animals", true);

    BooleanValue Monsters = new BooleanValue("Monsters", true);


    BooleanValue Villagers = new BooleanValue("Villagers", true);

    BooleanValue Teams = new BooleanValue("Teams", true);


    @Override
    public void onEnable() {

        timer.reset();
        animation = 0;
        super.onEnable();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventPacket) {

        } else if (e instanceof EventUpdate) {
            targets = getTargets();

            updateTime();

            if (current - last > Delay.getObject() / delay) {
                if (targets.size() > 0) {
                    for (int i = 0; i < (targets.size() > MaxTargets.getObject() ? MaxTargets.getObject() : targets.size()); i++) {

                        EntityLivingBase T = targets.get(i);
                        Vec3 topFrom = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                        Vec3 to = new Vec3(T.posX, T.posY, T.posZ);
                        path = computePath(topFrom, to);
                        test[i] = path;
                        for (Vec3 pathElm : path) {

                            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));
                        }
                        if (!NoSwing.getObject()) {
                            mc.thePlayer.swingItem();
                        }
                        mc.playerController.attackEntity(mc.thePlayer, T);
                        Collections.reverse(path);
                        for (Vec3 pathElm : path) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));
                        }

                    }
                }
                resetTime();


            }
        }
    }

    private void updateTime() {
        current = (System.nanoTime() / 1000000L);
    }

    private void resetTime() {
        last = (System.nanoTime() / 1000000L);
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
        if (AntiBot.getInvalid().contains(player) || player.isPlayerSleeping())
            return false;
        if (Crispy.INSTANCE.getFriendManager().isFriend(player.getCommandSenderName()))
            return false;


        return player != mc.thePlayer && mc.thePlayer.getDistanceToEntity(player) <= Reach.getObject();
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

    private ArrayList<Vec3> computePath(Vec3 topFrom, Vec3 to) {
        if (!canPassThrow(new BlockPos(topFrom.mc()))) {
            topFrom = topFrom.addVector(0, 1, 0);
        }
        MyGayPathfinder pathfinder = new MyGayPathfinder(topFrom, to);
        pathfinder.compute();

        int i = 0;
        Vec3 lastLoc = null;
        Vec3 lastDashLoc = null;
        ArrayList<Vec3> path = new ArrayList<Vec3>();
        ArrayList<Vec3> pathFinderPath = pathfinder.getPath();
        for (Vec3 pathElm : pathFinderPath) {
            if (i == 0 || i == pathFinderPath.size() - 1) {
                if (lastLoc != null) {
                    path.add(lastLoc.addVector(0.5, 0, 0.5));
                }
                path.add(pathElm.addVector(0.5, 0, 0.5));
                lastDashLoc = pathElm;
            } else {
                boolean canContinue = true;
                if (pathElm.squareDistanceTo(lastDashLoc) > 5 * 5) {
                    canContinue = false;
                } else {
                    double smallX = Math.min(lastDashLoc.getX(), pathElm.getX());
                    double smallY = Math.min(lastDashLoc.getY(), pathElm.getY());
                    double smallZ = Math.min(lastDashLoc.getZ(), pathElm.getZ());
                    double bigX = Math.max(lastDashLoc.getX(), pathElm.getX());
                    double bigY = Math.max(lastDashLoc.getY(), pathElm.getY());
                    double bigZ = Math.max(lastDashLoc.getZ(), pathElm.getZ());
                    cordsLoop:
                    for (int x = (int) smallX; x <= bigX; x++) {
                        for (int y = (int) smallY; y <= bigY; y++) {
                            for (int z = (int) smallZ; z <= bigZ; z++) {
                                if (!MyGayPathfinder.checkPositionValidity(x, y, z, false)) {
                                    canContinue = false;
                                    break cordsLoop;
                                }
                            }
                        }
                    }

                }
                if (!canContinue) {
                    path.add(lastLoc.addVector(0.5, 0, 0.5));
                    lastDashLoc = lastLoc;
                }
            }
            lastLoc = pathElm;
            i++;
        }
        return path;
    }

    private boolean canPassThrow(BlockPos pos) {
        Block block = Minecraft.getMinecraft().theWorld.getBlockState(new net.minecraft.util.BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock();
        return block.getMaterial() == Material.air || block.getMaterial() == Material.plants || block.getMaterial() == Material.vine || block == Blocks.ladder || block == Blocks.water || block == Blocks.flowing_water || block == Blocks.wall_sign || block == Blocks.standing_sign;
    }

    private List<EntityLivingBase> getTargets() {
        List<EntityLivingBase> targets = new ArrayList<>();

        for (Object o : mc.theWorld.getLoadedEntityList()) {
            if (o instanceof EntityLivingBase) {
                EntityLivingBase entity = (EntityLivingBase) o;
                if (canAttack(entity)) {
                    targets.add(entity);
                }
            }
        }
        targets.sort((o1, o2) -> (int) (o1.getDistanceToEntity(mc.thePlayer) * 1000 - o2.getDistanceToEntity(mc.thePlayer) * 1000));
        return targets;
    }


}
