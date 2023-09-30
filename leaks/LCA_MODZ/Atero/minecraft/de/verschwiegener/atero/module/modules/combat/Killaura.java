package de.verschwiegener.atero.module.modules.combat;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.events.callables.EventPreMotionUpdate;
import com.darkmagician6.eventapi.events.callables.EventTest;
import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.friend.FriendManager;
import de.verschwiegener.atero.module.Category;
import de.verschwiegener.atero.module.Module;
import de.verschwiegener.atero.module.modules.movement.Speed;
import de.verschwiegener.atero.settings.Setting;
import de.verschwiegener.atero.settings.SettingsItem;
import de.verschwiegener.atero.util.TimeUtils;
import de.verschwiegener.atero.util.chat.ChatUtil;
import god.buddy.aot.BCompiler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.*;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Killaura extends Module {

    public static Killaura instance;
    public static float yaw;
    public static ArrayList<Entity> bots = new ArrayList<>();
    public static ArrayList<Entity> friends = new ArrayList<>();
    private final Minecraft mc = Minecraft.getMinecraft();
    int FrameClick = 0;
    private EntityLivingBase target, preaimtarget;
    private float pitch;
    private float[] facing;
    private double reach = 0;
    private boolean block;
    public static Setting setting, targetset;
    private TimeUtils timer = new TimeUtils();
    private long slide;

    private boolean down;
    public Killaura() {
        super("KillAura", "KillAura", Keyboard.KEY_NONE, Category.Combat);
        instance = this;
    }
    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)

    public static float[] Intavee(final EntityPlayerSP player, final EntityLivingBase target) {
        final float RotationPitch = (float) MathHelper.getRandomDoubleInRange(new Random(), 90, 92);
        final float RotationYaw = (float) MathHelper.getRandomDoubleInRange(new Random(), RotationPitch, 94);
        final double posX = target.posX - player.posX;
        final float RotationY2 = (float) MathHelper.getRandomDoubleInRange(new Random(), 175, 180);
        final float RotationY4 = (float) MathHelper.getRandomDoubleInRange(new Random(), 0.2, 0.3);
        final float RotationY3 = (float) MathHelper.getRandomDoubleInRange(new Random(), RotationY4, 0.1);
        final double posY = target.posY + target.getEyeHeight()
                - (player.posY + player.getAge() + player.getEyeHeight() + RotationY3);
        final double posZ = target.posZ - player.posZ;
        final double var14 = MathHelper.sqrt_double(posX * posX + posZ * posZ);
        float yaw = (float) (Math.atan2(posZ, posX) * RotationY2 / Math.PI) - RotationYaw;
        float pitch = (float) -(Math.atan2(posY, var14) * RotationY2 / Math.PI);
        final float f2 = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
        final float f3 = f2 * f2 * f2 * 1.2F;
        yaw -= yaw % f3;
        pitch -= pitch % (f3 * f2);
        return new float[]{yaw, MathHelper.clamp_float(pitch, -90, 90)};
    }
    public static float[] Watchdog(final EntityPlayerSP player, final EntityLivingBase target) {
        final float RotationPitch = (float) MathHelper.getRandomDoubleInRange(new Random(), 90, 92);
        final float RotationYaw = (float) MathHelper.getRandomDoubleInRange(new Random(), RotationPitch, 94);
        final double posX = target.posX - player.posX;
        final float RotationY2 = (float) MathHelper.getRandomDoubleInRange(new Random(), 178, 180);
        final float RotationY4 = (float) MathHelper.getRandomDoubleInRange(new Random(), 0.2, 0.3);
        final float RotationY5 = (float) MathHelper.getRandomDoubleInRange(new Random(), 0.02, 0.03);
        final float RotationY3 = (float) MathHelper.getRandomDoubleInRange(new Random(), RotationY4, 0.1);
        final double posY = target.posY + target.getEyeHeight() - (player.posY + player.getAge() + player.getEyeHeight()+RotationY5 );
        final double posZ = target.posZ - player.posZ;
        final double var14 = MathHelper.sqrt_double(posX * posX + posZ * posZ);
        float yaw = (float) (Math.atan2(posZ, posX) * RotationY2 / Math.PI) - RotationPitch;

        float pitch = (float) -(Math.atan2(posY, var14) * 180 / Math.PI);
        final float f2 = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
        final float f3 = f2 * f2 * f2 * 1.2F;
        yaw -= yaw % f3;
        pitch -= pitch % (f3 * f2);
        return new float[]{yaw, MathHelper.clamp_float(pitch, -90, 90)};
    }
    public static float[] NCP(final EntityPlayerSP player, final EntityLivingBase target) {
        final float RotationPitch = (float) MathHelper.getRandomDoubleInRange(new Random(), 90, 92);
        final float RotationYaw = (float) MathHelper.getRandomDoubleInRange(new Random(), RotationPitch, 94);
        final double posX = target.posX - player.posX;
        final float RotationY2 = (float) MathHelper.getRandomDoubleInRange(new Random(), 178, 180);
        final float RotationY4 = (float) MathHelper.getRandomDoubleInRange(new Random(), 0.2, 0.3);
        final float RotationY3 = (float) MathHelper.getRandomDoubleInRange(new Random(), RotationY4, 0.1);
        final double posY = target.posY + target.getEyeHeight() - (player.posY + player.getAge() + player.getEyeHeight());
        final double posZ = target.posZ - player.posZ;
        final double var14 = MathHelper.sqrt_double(posX * posX + posZ * posZ);
        float yaw = (float) (Math.atan2(posZ, posX) * 180 / Math.PI) - 90;

        float pitch = (float) -(Math.atan2(posY, var14) * 180 / Math.PI);
        final float f2 = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
        final float f3 = f2 * f2 * f2 * 1.2F;
        yaw -= yaw % f3;
        pitch -= pitch % (f3 * f2);
        return new float[]{yaw, MathHelper.clamp_float(pitch, -90, 90)};
    }

    public EntityLivingBase getTarget() {
        return (target == null) ? preaimtarget : target;
    }

    public boolean hasTarget() {
        return target != null || preaimtarget != null;
    }

    private boolean canAttack(final EntityLivingBase player) {
        if (player == Minecraft.thePlayer)
            return false;

        if (player instanceof EntityPlayer && !targetset.getItemByName("Player").isState())
            return false;
        if (player instanceof net.minecraft.entity.passive.EntityAnimal
                && !targetset.getItemByName("Animals").isState())
            return false;
        if (player instanceof net.minecraft.entity.monster.EntityMob && !targetset.getItemByName("Mobs").isState())
            return false;
        if (player instanceof net.minecraft.entity.passive.EntityVillager
                && !targetset.getItemByName("Villager").isState())
            return false;
        if (player.isOnSameTeam(Minecraft.thePlayer) && targetset.getItemByName("Teams").isState())
            return false;
        if (player.isInvisible() && !targetset.getItemByName("Invisible").isState())
            return false;
        if (player.isDead && !targetset.getItemByName("Death").isState())
            return false;
        // if (player.deathTime != 0)
        // return false;
        if (bots.contains(player))
            return false;
        return true;
    }

    private boolean canEntityBeSeen(final Entity entityIn) {
        if (!Minecraft.thePlayer.canEntityBeSeen(entityIn))
            return mc.theWorld.rayTraceBlocks(new Vec3(Minecraft.thePlayer.posX,
                            Minecraft.thePlayer.posY + Minecraft.thePlayer.getEyeHeight(), Minecraft.thePlayer.posZ),
                    new Vec3(entityIn.posX, entityIn.posY, entityIn.posZ)) == null;
        else
            return true;
    }

    public EntityLivingBase getClosestPlayer(final double distance) {
        double d0 = distance;
        EntityLivingBase entityplayer = null;
        for (final Entity entity : Minecraft.getMinecraft().theWorld.loadedEntityList) {
            if (entity instanceof EntityLivingBase && canAttack((EntityLivingBase) entity)) {
                final double d1 = Math.sqrt(entity.getDistanceSq(Minecraft.thePlayer.posX, Minecraft.thePlayer.posY,
                        Minecraft.thePlayer.posZ));
                if (d1 < d0) {
                    d0 = d1;
                    entityplayer = (EntityLivingBase) entity;
                }
            }
        }
        return entityplayer;
    }

    private EntityLivingBase getClosest(double range) {
        double dist = range;
        EntityLivingBase target = null;
        for (Object object : Minecraft.theWorld.loadedEntityList) {
            Entity entity = (Entity) object;
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase player = (EntityLivingBase) entity;
                if (canAttack(player) && !Management.instance.friendmgr.isFriend(player.getName())) {
                    double currentDist = Minecraft.thePlayer.getDistanceToEntity((Entity) player);
                    if (currentDist <= dist) {
                        dist = currentDist;
                        target = player;
                    }
                }
            }
        }
        return target;
    }

    /*
     * @BCompiler(aot = BCompiler.AOT.AGGRESSIVE) // TODO Names Reworken private
     * float[] getEntityRotations(final EntityPlayerSP player, final
     * EntityLivingBase target, final Vec3 targetVec) { final double posX =
     * targetVec.xCoord - player.posX; double posY; if (targetVec.yCoord <
     * Minecraft.thePlayer.posY) { posY = targetVec.yCoord - (targetVec.yCoord +
     * player.getEyeHeight()) + 0.1; } else { posY = targetVec.yCoord +
     * target.getEyeHeight() - (targetVec.yCoord + player.getEyeHeight() + 0.5); }
     * final double posZ = targetVec.zCoord - player.posZ; final double var14 =
     * MathHelper.sqrt_double(posX * posX + posZ * posZ); float yaw = (float)
     * (Math.atan2(posZ, posX) * 180.0 / Math.PI) - 90.0f; float pitch = (float)
     * -(Math.atan2(posY, var14) * 180.0 / Math.PI); final float mouseSensitivity =
     * mc.gameSettings.mouseSensitivity * 0.6F + 0.2F; // Wenn kein Schaden macht //
     * float f3 = mouseSensitivity * mouseSensitivity * mouseSensitivity * 1.2F;
     * final float sensitivityOffset = mouseSensitivity * 3 * 1.2F; yaw -= yaw %
     * sensitivityOffset; pitch -= pitch % (sensitivityOffset * mouseSensitivity);
     * return new float[] { yaw, pitch }; }
     */

    public EntityLivingBase getHighestPlayer(final double distance) {
        EntityLivingBase target = null;
        for (final Entity entity : Minecraft.getMinecraft().theWorld.loadedEntityList) {
            if (entity instanceof EntityLivingBase && canAttack((EntityLivingBase) entity)
                    && entity.getDistance(Minecraft.thePlayer.posX, Minecraft.thePlayer.posY,
                    Minecraft.thePlayer.posZ) < distance) {
                if (target != null && ((EntityLivingBase) entity).getHealth() < target.getHealth() || target == null) {
                    target = (EntityLivingBase) entity;
                }
            }
        }
        return target;
    }

    public EntityLivingBase getLowestPlayer(final double distance) {
        EntityLivingBase target = null;
        for (final Entity entity : Minecraft.getMinecraft().theWorld.loadedEntityList) {
            if (entity instanceof EntityLivingBase && canAttack((EntityLivingBase) entity)
                    && entity.getDistance(Minecraft.thePlayer.posX, Minecraft.thePlayer.posY,
                    Minecraft.thePlayer.posZ) < distance) {
                if (target != null && ((EntityLivingBase) entity).getHealth() < target.getHealth() || target == null) {
                    target = (EntityLivingBase) entity;
                }
            }
        }
        return target;
    }

    private MovingObjectPosition getTarget(final float partialTicks, final double distance) {
        Entity pointedEntity;
        MovingObjectPosition omo = mc.renderViewEntity.rayTrace(distance, partialTicks);
        final double d1 = distance;
        final Vec3 vec3 = mc.renderViewEntity.getPositionEyes(partialTicks);
        final Vec3 vec31 = mc.renderViewEntity.getLook(partialTicks);
        final Vec3 vec32 = vec3.addVector(vec31.xCoord * distance, vec31.yCoord * distance, vec31.zCoord * distance);
        pointedEntity = null;
        Vec3 vec33 = null;
        final float f1 = 1.0F;
        final List list = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.renderViewEntity,
                mc.renderViewEntity.boundingBox
                        .addCoord(vec31.xCoord * distance, vec31.yCoord * distance, vec31.zCoord * distance)
                        .expand(f1, f1, f1));
        double d2 = d1;

        for (final Object element : list) {
            final Entity entity = (Entity) element;

            if (entity.canBeCollidedWith()) {
                final float f2 = entity.getCollisionBorderSize();
                final AxisAlignedBB axisalignedbb = entity.boundingBox.expand(f2, f2, f2);
                final MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                if (axisalignedbb.isVecInside(vec3)) {
                    if (0.0D < d2 || d2 == 0.0D) {
                        pointedEntity = entity;
                        vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                        d2 = 0.0D;
                    }
                } else if (movingobjectposition != null) {
                    final double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                    if (d3 < d2 || d2 == 0.0D) {
                        if (entity == mc.renderViewEntity.ridingEntity) {
                            if (d2 == 0.0D) {
                                pointedEntity = entity;
                                vec33 = movingobjectposition.hitVec;
                            }
                        } else {
                            pointedEntity = entity;
                            vec33 = movingobjectposition.hitVec;
                            d2 = d3;
                        }
                    }
                }
            }
        }
        if (pointedEntity != null && (d2 < d1 || omo == null)) {
            omo = new MovingObjectPosition(pointedEntity, vec33);

            if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame) {
                mc.pointedEntity = pointedEntity;
            }
        }
        if (omo != null && omo.typeOfHit == MovingObjectType.ENTITY && omo.entityHit instanceof EntityLivingBase)
            return omo;
        return null;
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    private float interpolateRotation(final float yaw, final float pitch, final float partialTicks) {
        float f = MathHelper.wrapAngleTo180_float(pitch - yaw);
        if (f > partialTicks) {
            f = partialTicks;
        }
        if (f < -partialTicks) {
            f = -partialTicks;
        }
        return yaw + f;
    }

    /*
     * @BCompiler(aot = BCompiler.AOT.AGGRESSIVE) private Vec3 linearpredict(final
     * EntityLivingBase base) { final double x = base.posX + base.motionX; final
     * double y = base.posY + base.motionY; final double z = base.posZ +
     * base.motionZ; return new Vec3(x, y, z); }
     */

    @Override
    public void onEnable() {
        super.onEnable();
        setting = Management.instance.settingsmgr.getSettingByName(getName());
        targetset = Management.instance.settingsmgr.getSettingByName("Target");
    }
    @Override
    public void onDisable() {
        bots.clear();
        super.onDisable();

    }

    @EventTarget

    public void onEvent(final EventTest event) {
        if (setting.getItemByName("CorrectMM").isState() && (target != null || preaimtarget != null)) {
            event.setYaw(EventPreMotionUpdate.getInstance.getYaw());
            event.setSilentMoveFix(true);
        }
    }
    @Override

    public void onUpdate() {
        super.onUpdate();
        final String modes = setting.getItemByName("RotationMode").getCurrent();
        setExtraTag("" + modes);
    }
    @EventTarget

    public void onPre(final EventPreMotionUpdate pre) {



            if (setting.getItemByName("DMGBlockHit").isState()) {
                if(Killaura.instance.hasTarget()){
                if(mc.thePlayer.hurtTime != 0) {
                    mc.gameSettings.keyBindUseItem.pressed = true;
                }else{
                    if(setting.getItemByName("SwingProgress").isState()) {
                        if (target.isSwingInProgress) {
                            mc.gameSettings.keyBindUseItem.pressed = true;
                        }
                    }
                    }
                }
            }

        if(setting.getItemByName("AutoBlock").isState()) {
            if(!Killaura.instance.hasTarget()) {
                if (Minecraft.thePlayer.getHeldItem().getItem() instanceof net.minecraft.item.ItemSword) {
                    mc.gameSettings.keyBindUseItem.pressed = false;
                }
            }
        }
        if (setting.getItemByName("DMGBlockHit").isState()) {
            if(!Killaura.instance.hasTarget()) {
                mc.gameSettings.keyBindUseItem.pressed = false;
            }
        }
        if(setting.getItemByName("Watchdog").isState()) {
            if(!Killaura.instance.hasTarget()) {
                if (Minecraft.thePlayer.getHeldItem().getItem() instanceof net.minecraft.item.ItemSword) {
                    mc.gameSettings.keyBindUseItem.pressed = false;
                }
            }
        }

        if ((target != null)) {
            if(setting.getItemByName("AutoBlock").isState()) {
                //   if(mc.thePlayer.ticksExisted % 10 == 0) {
                if (Minecraft.thePlayer.getHeldItem() != null)
                    if (Minecraft.thePlayer.getHeldItem().getItem() instanceof net.minecraft.item.ItemSword) {
                        //   mc.playerController.sendUseItem((EntityPlayer) Minecraft.thePlayer, (World) Minecraft.theWorld, Minecraft.thePlayer.getHeldItem());
                        if (!setting.getItemByName("Watchdog").isState()) {
                            mc.gameSettings.keyBindUseItem.pressed = true;
                            //   }

                        }
                    }
                if ((target != null)) {
                    if (setting.getItemByName("AutoBlock").isState()) {
                        if (setting.getItemByName("Watchdog").isState()) {
                            if (Minecraft.thePlayer.getHeldItem() != null)
                                if (Minecraft.thePlayer.getHeldItem().getItem() instanceof net.minecraft.item.ItemSword) {
                                    mc.playerController.sendUseItem((EntityPlayer) Minecraft.thePlayer, (World) Minecraft.theWorld, Minecraft.thePlayer.getHeldItem());

                                    //   }
                                }
                        }
                    }
                }

            }

            final String modes = setting.getItemByName("RotationMode").getCurrent();
            if(modes.equalsIgnoreCase("AAC")) {
                facing = Killaura.Intavee(Minecraft.thePlayer, target);
            }else {
                if (modes.equalsIgnoreCase("Watchdog")) {
                    facing = Killaura.Watchdog(Minecraft.thePlayer, target);
                } else {
                    if (modes.equalsIgnoreCase("NCP")) {
                        facing = Killaura.NCP(Minecraft.thePlayer, target);
                    }
                }
            }
            pre.setYaw(yaw);
            pre.setPitch(pitch);
            //Minecraft.thePlayer.rotationPitch = pitch;
            // Minecraft.thePlayer.rotationYaw = yaw ;
            if (setting.getItemByName("RotationSpeed").isState()) {
                yaw = interpolateRotation(yaw, facing[0], setting.getItemByName("Speed").getCurrentValue());
                pitch = interpolateRotation(pitch, facing[1], setting.getItemByName("Speed").getCurrentValue());
            } else {
                yaw = interpolateRotation(yaw, facing[0], 180);
                pitch = interpolateRotation(pitch, facing[1], 180);
            }
        } else if (preaimtarget != null) {
            facing = Killaura.Intavee(Minecraft.thePlayer, preaimtarget);

            pre.setYaw(yaw);
            pre.setPitch(pitch);

            // yaw = facing[0];
            // pitch = facing[1];
            yaw = interpolateRotation(yaw, facing[0], 180);
            pitch = interpolateRotation(pitch, facing[1], 180);
        }
        //ChatUtil.sendMessage("" + FrameClick + " target: " + target);
    }

    @Override

    public void onUpdateClick() {
        super.onUpdateClick();

        int maxCPS = (int) setting.getItemByName("MAXCPS").getCurrentValue();
        int minCPS = (int) setting.getItemByName("MINCPS").getCurrentValue();
        if (minCPS >= maxCPS) {
            minCPS = maxCPS - 1;
        }
        final float CCPS = (float) ((float) 1000 / MathHelper.getRandomDoubleInRange(new Random(), minCPS, maxCPS));
        if (setting.getItemByName("AutoAttack").isState()) {

            if (timer.hasReached(CCPS)) {
                mc.clickMouse();
                timer.reset();
            }

        }

        try {
            if (Minecraft.currentScreen != null)
                return;

            reach = setting.getItemByName("Range").getCurrentValue();
            final String mode = setting.getItemByName("TargetMode").getCurrent();
            if (mode.equalsIgnoreCase("Nearest")) {
                target = getClosest(reach);
            } else if (mode.equalsIgnoreCase("Lowest")) {
                target = getLowestPlayer(reach);
            } else if (mode.equalsIgnoreCase("Highest")) {
                target = getHighestPlayer(reach);
            }

            if (target == null) {

                if (facing != null) {
                    yaw = interpolateRotation(yaw, facing[0], 180);
                    pitch = interpolateRotation(pitch, facing[1], 180);
                }
                return;
            }


            //final MovingObjectPosition t = getTarget(mc.timer.elapsedTicks, reach);

            // final float CCPS = 1000 / random(minCPS, maxCPS);
            if (target != null) {
                if (timer.hasReached(CCPS)) {
                    if (setting.getItemByName("LegitAttack").isState()) {
                        mc.clickMouse();
                    } else {
                        //    if (!(mc.thePlayer.fallDistance > 0.08F && Management.instance.modulemgr.getModuleByName("Speed").isEnabled() && Speed.setting.getItemByName("Cubecraft").isState())) {
                        mc.thePlayer.swingItem();
                        mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
                        //  }
                    }
                    timer.reset();
                }
                // FrameClick++;
                //timer.reset();


            }


            // Sprint
            if (!setting.getItemByName("KeepSprint").isState()) {
                Minecraft.getMinecraft();
                Minecraft.thePlayer.setSprinting(false);
            }
            // FakeBlock
            // if (setting.getItemByName("FakeBlock").isState()) {
            // Minecraft.getMinecraft().thePlayer.setItemInUse(Minecraft.getMinecraft().thePlayer.getHeldItem(),5);
            // }

            // getWinkel(target);
            if (!canEntityBeSeen(target) && !setting.getItemByName("ThroughWalls").isState())
                return;

            // AutoBlock


	    
		/*while (FrameClick > 0) {
		    if ((t != null && t.typeOfHit == MovingObjectType.ENTITY
			    || setting.getItemByName("ThroughWalls").isState())) {
			if (setting.getItemByName("LegitAttack").isState()) {
			    mc.clickMouse();
			} else {
			    System.out.println("Attak");
			    mc.thePlayer.swingItem();
			    mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
			}

		    }
		    FrameClick--;

		}*/


        } catch (final NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public Setting getSetting() {
        return setting;
    }

    private int random(final int min, final int max) {
        final Random random = new Random();
        int zufallZahl = random.nextInt(max);
        while (zufallZahl < min) {
            zufallZahl = random.nextInt(max);
        }
        return zufallZahl;
    }

    @Override

    public void setup() {
        super.setup();

        final ArrayList<SettingsItem> items = new ArrayList<>();
        final ArrayList<String> targetModes = new ArrayList<>();
        final ArrayList<String> rotationMode = new ArrayList<>();
        targetModes.add("Nearest");
        targetModes.add("Lowest");
        targetModes.add("Highest");
        rotationMode.add("AAC");
        rotationMode.add("NCP");
        rotationMode.add("Watchdog");
        items.add(new SettingsItem("TargetMode", targetModes, "Nearest", "", ""));
        items.add(new SettingsItem("RotationMode", rotationMode, "AAC", "", ""));
        items.add(new SettingsItem("Range", 1.0F, 6.0F, 3.0F, ""));
        items.add(new SettingsItem("MAXCPS", 1, 50, 4, ""));
        items.add(new SettingsItem("MINCPS", 1, 50, 3, ""));
        items.add(new SettingsItem("Preaim", false, "PreAimRange"));
        items.add(new SettingsItem("PreAimRange", 1.0F, 6.0F, 3.0F, ""));
        items.add(new SettingsItem("KeepSprint", true, ""));
        items.add(new SettingsItem("LegitAttack", false, ""));
        items.add(new SettingsItem("JumpFix", false, ""));
        items.add(new SettingsItem("AutoAttack", false, ""));
        items.add(new SettingsItem("ThroughWalls", false, ""));
        items.add(new SettingsItem("FakeBlock", true, ""));
        items.add(new SettingsItem("AutoBlock", false, "Watchdog"));
        items.add(new SettingsItem("Watchdog", false, ""));
        items.add(new SettingsItem("DMGBlockHit", false, "SwingProgress"));
        items.add(new SettingsItem("SwingProgress", false, ""));
        items.add(new SettingsItem("HardMoveFix", false, "SilentMoveFix"));
        items.add(new SettingsItem("SilentMoveFix", true, ""));
        items.add(new SettingsItem("TargetESP", true, ""));
        items.add(new SettingsItem("RotationSpeed", false, "Speed"));
        items.add(new SettingsItem("Speed", 10F, 180F, 180F, ""));

        Management.instance.settingsmgr.addSetting(new Setting(this, items));
    }

    public void onRender() {
        try {

            if(setting.getItemByName("TargetESP").isState()) {
                EntityLivingBase entityLivingBase = target;
                mc.getRenderManager();
                double x = (entityLivingBase).prevPosX + ((entityLivingBase).posX - (entityLivingBase).prevPosX) * mc.timer.renderPartialTicks - RenderManager.renderPosX;
                mc.getRenderManager();
                double y = (entityLivingBase).prevPosY + ((entityLivingBase).posY - (entityLivingBase).prevPosY) * mc.timer.renderPartialTicks - RenderManager.renderPosY;
                mc.getRenderManager();
                double z = (entityLivingBase).prevPosZ + ((entityLivingBase).posZ - (entityLivingBase).prevPosZ) * mc.timer.renderPartialTicks - RenderManager.renderPosZ;
                Color color = Management.instance.colorBlue;
                float rad = 0.5F;
                GL11.glPushMatrix();
                GL11.glDisable(3553);
                GL11.glEnable(2848);
                GL11.glDisable(2929);
                GL11.glDepthMask(false);
                GL11.glLineWidth(2.0F);
                GL11.glBegin(3);
                float r = 0.003921569F * color.getRed();
                float g = 0.003921569F * color.getGreen();
                float b = 0.003921569F * color.getBlue();
                double pix2 = 6.283185307179586D;
                if (this.slide < 200L && !this.down) {
                    this.slide++;
                } else {
                    this.down = true;
                    if (this.slide > 0L)
                        this.slide--;
                    if (this.slide == 0L)
                        this.down = false;
                }
                for (int i = 0; i <= 90; i++) {
                    GL11.glColor3f(r, g, b);
                    GL11.glVertex3d(x + rad * Math.cos(i * pix2 / 45.0D), y + this.slide * 0.01D, z + rad * Math.sin(i * pix2 / 45.0D));
                }
                GL11.glEnd();
                GL11.glDepthMask(true);
                GL11.glEnable(2929);
                GL11.glDisable(2848);
                GL11.glEnable(3553);
                GL11.glPopMatrix();
            }
        } catch (NullPointerException e) {
        }
            }



}
