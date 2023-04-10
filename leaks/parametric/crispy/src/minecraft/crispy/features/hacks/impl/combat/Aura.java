package crispy.features.hacks.impl.combat;

import crispy.Crispy;
import crispy.features.commands.impl.ClickPatternCommand;
import crispy.features.event.Event;
import crispy.features.event.impl.movement.EventStrafe;
import crispy.features.event.impl.player.EventPacket;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.event.impl.render.Event3D;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.features.hacks.impl.misc.HackerDetector;
import crispy.features.hacks.impl.movement.Scaffold;
import crispy.features.hacks.impl.render.HUD;
import crispy.util.fbi.target.TargetManager;
import crispy.util.rotation.LookUtils;
import crispy.util.rotation.Rotation;
import crispy.util.rotation.VecRotation;
import crispy.util.time.TimeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.superblaubeere27.valuesystem.BooleanValue;
import net.superblaubeere27.valuesystem.ModeValue;
import net.superblaubeere27.valuesystem.NumberValue;
import org.lwjgl.input.Keyboard;
import viamcp.ViaMCP;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.lwjgl.opengl.GL11.*;


@HackInfo(name = "Aura", category = Category.COMBAT, key = Keyboard.KEY_R)
public class Aura extends Hack {

    public static EntityLivingBase target;
    public static Rotation targetRotation = new Rotation(0, 0);
    public static Rotation serverRotation = new Rotation(0, 0);
    public static ModeValue BlockMode = new ModeValue("Block Mode", "Normal", "Normal", "Matrix", "Hive", "Hypixel", "Verus", "Fake");
    public static BooleanValue KeepSprint = new BooleanValue("KeepSprint", true);
    public static BooleanValue autoBlock = new BooleanValue("AutoBlock", false);
    private final TimeHelper clickDelay = new TimeHelper();
    private final TimeHelper switchTimer = new TimeHelper();
    private final TimeHelper newTarget = new TimeHelper();
    /*
Settings!
 */
    ModeValue killAuraMode = new ModeValue("KillAura Mode ", "Normal", "Normal", "Switch", "Pit");
    ModeValue attackMode = new ModeValue("Attack Mode", "Normal", "Normal", "Pinecone");
    ModeValue RotMode = new ModeValue("Rotation Mode", "Normal", "Normal", "Custom", "LiquidBounce", "HeadShake", "Crazy", "None");
    NumberValue<Float> pitchSmoothing = new NumberValue<Float>("Pitch Smoothing", 5F, 1F, 20F, () -> RotMode.getMode().equalsIgnoreCase("Custom"));
    NumberValue<Float> yawSmoothing = new NumberValue<Float>("Yaw Smoothing", 5F, 1F, 20F, () -> RotMode.getMode().equalsIgnoreCase("Custom"));
    ModeValue clickPattern = new ModeValue("Click Pattern", "Normal", "Normal", "Custom");
    NumberValue<Integer> cps = new NumberValue<Integer>("CPS", 10, 1, 20, () -> clickPattern.getMode().equalsIgnoreCase("Normal"));
    ModeValue raycast = new ModeValue("Raycast Method", "Normal", "Normal", "Raycast", "None");
    ModeValue strafeMode = new ModeValue("Movement Mode", "Default", "Default", "Silent", "None");
    NumberValue<Double> reach = new NumberValue<>("Reach", 4d, 0.5d, 8d);
    NumberValue<Integer> existed = new NumberValue<Integer>("Existed", 30, 0, 500);
    NumberValue<Integer> fov = new NumberValue<Integer>("FOV", 360, 1, 360);
    NumberValue<Long> switchDelay = new NumberValue<Long>("Switch Delay", 0L, 0L, 2000L, () -> killAuraMode.getMode().equalsIgnoreCase("Switch"));
    BooleanValue crack = new BooleanValue("Crack Size", true);
    NumberValue<Integer> crackSize = new NumberValue<>("Crack Size Value", 5, 0, 15, () -> crack.getObject());
    BooleanValue aac = new BooleanValue("AAC", true);
    BooleanValue fixRaycast = new BooleanValue("Fix Raycast", false);
    BooleanValue invis = new BooleanValue("Invisibles", false);
    BooleanValue players = new BooleanValue("Players", true);
    BooleanValue others = new BooleanValue("Others", false);
    BooleanValue entityBeSeen = new BooleanValue("EntityBeSeen", false);
    BooleanValue teams = new BooleanValue("Teams", true);
    BooleanValue circle = new BooleanValue("Draw Circle", false);
    BooleanValue noSnap = new BooleanValue("No Snap", false);
    ModeValue esp = new ModeValue("Target ESP", "None", "None", "Jello");
    double y;
    int[] randoms = {0, 1, 0};
    /*
    Vars
     */
    private double animation;
    public static boolean lol, aura;
    private Entity hittableTarget;
    private float yaw;
    private int clickDelayIndex;
    private float pitch;
    private EntityLivingBase postTarget;

    private List<EntityLivingBase> loaded = new CopyOnWriteArrayList<>();
    private int index;

    public static float randomNumber(float max, float min) {
        return (float) (min + Math.random() * ((max - min)));
    }

    public static int randomNumber(int max, int min) {
        return Math.round(min + (float) Math.random() * ((max - min)));
    }

    @Override
    public void onEnable() {
        reset();
        aura = false;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        reset();
        AntiBot.getInvalid().clear();
        aura = false;
        super.onDisable();
    }

    public void reset() {
        if (Minecraft.theWorld != null && mc.thePlayer != null) {
            clickDelay.reset();
            newTarget.reset();
            switchTimer.reset();
            target = null;
            yaw = mc.thePlayer.rotationYaw;
            pitch = mc.thePlayer.rotationPitch;
            targetRotation = new Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        }
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventUpdate) {
            HackerDetector fbi = Crispy.INSTANCE.getHackManager().getHack(HackerDetector.class);
            EventUpdate event = (EventUpdate) e;
            ClickPatternCommand pattern = Crispy.INSTANCE.getCommandManager().getCommand(ClickPatternCommand.class);
            Scaffold scaffold = Crispy.INSTANCE.getHackManager().getHack(Scaffold.class);

            if (strafeMode.getObject() == 2) {
                if (target == null) {
                    if (!noSnap.getObject()) {
                        yaw = mc.thePlayer.rotationYaw;
                        pitch = mc.thePlayer.rotationPitch;
                        targetRotation = new Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
                        aura = false;
                    } else {
                        if (aura) {
                            aura = Math.abs(MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw - yaw)) > 1;
                        }
                        if (aura) noSnap();
                    }
                } else {
                    aura = true;
                }
                onUpdate();
            }
            if (scaffold.disableAura.getObject() && Scaffold.sPitch != 999 && Scaffold.sYaw != 999) {
                target = null;
                return;
            }

            if (target == null) {

                return;
            }


            event.setYaw(targetRotation.getYaw());
            event.setPitch(targetRotation.getPitch());

            int minran = cps.getObject();
            int maxran = cps.getObject();
            int rand = randomNumber(minran, maxran);
            int cpsdel = 0 + rand <= 0 ? 1 : 0 + rand;

            float del = 1000 / (cpsdel);
            if (clickPattern.getObject() == 1 && !pattern.delays.isEmpty()) {
                if (clickDelayIndex > pattern.delays.size() - 1) {
                    clickDelayIndex = 0;
                }

                del = pattern.delays.get(clickDelayIndex);

            }
            if (target instanceof EntityPlayer && fbi.cpsIncrease.getObject()) {
                EntityPlayer entityPlayer = (EntityPlayer) target;
                if (TargetManager.INSTANCE.getTargets().contains(entityPlayer)) {
                    del = 10;
                }
            }


            boolean shouldMiss = randomNumber(0, 100) > 100;
            if (ViaMCP.getInstance().getVersion() > 107 && !Crispy.INSTANCE.getHackManager().getHack(OldHitting.class).isEnabled()) {
                del = (1 / getHitDelay(mc.thePlayer.getHeldItem())) * 1000;
            }


            if (raycast.getObject() == 1) {

                shouldMiss = !LookUtils.isFaced(target, target.getDistanceToEntity(mc.thePlayer));

            }

            if (raycast.getObject() == 0) {

                float neededPitch = new LookUtils().getPitchChange(targetRotation.getPitch(), target, target.posY);
                float neededYaw = LookUtils.getYawChange(targetRotation.getYaw(), target.posX, target.posZ);

                neededPitch = new LookUtils().getPitchChange(targetRotation.getPitch(), target, target.posY);
                neededYaw = getCustomRotsChange(targetRotation.getYaw(), targetRotation.getPitch(), target.posX, target.posY, target.posZ)[0];

                float interval = 60.0f - this.mc.thePlayer.getDistanceToEntity(Aura.target) * 10.0f;

                interval = 50.0f - this.mc.thePlayer.getDistanceToEntity(Aura.target) * 10.0f;

                if (neededYaw > interval || neededYaw < -interval || !newTarget.hasReached(0)) {

                    shouldMiss = true;

                    newTarget.reset();
                }
            } else if (raycast.getObject() == 2) {
                shouldMiss = false;
            }
            if (this.clickDelay.hasReached((long) del)) {
                this.clickDelay.reset();
                if (!shouldMiss) {
                    attack(target, event.isPre());
                } else {

                    mc.thePlayer.swingItem();

                }
            }
        } else if (e instanceof EventStrafe) {
            EventStrafe eventStrafe = (EventStrafe) e;
            HackerDetector fbiAntiCheat = Crispy.INSTANCE.getHackManager().getHack(HackerDetector.class);
            Scaffold scaffold = Crispy.INSTANCE.getHackManager().getHack(Scaffold.class);
            if (scaffold.disableAura.getObject() && Scaffold.sPitch != 999 && Scaffold.sYaw != 999) {
                return;
            }
            onUpdate();
            if (strafeMode.getObject() != 2) {
                if (target == null) {
                    if (!noSnap.getObject()) {
                        yaw = mc.thePlayer.rotationYaw;
                        pitch = mc.thePlayer.rotationPitch;
                        targetRotation = new Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
                        aura = false;
                    } else {
                        if (aura) {
                            aura = Math.abs(MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw - yaw)) > 1;
                        }
                        if (aura) noSnap();
                    }
                    return;
                } else {
                    aura = true;
                }
                onUpdate();

                eventStrafe.setCancelled(true);
                if (strafeMode.getObject() == 0) {
                    float strafe = eventStrafe.getStrafe();
                    float forward = eventStrafe.getFward();
                    float friction = eventStrafe.getFric();
                    float f = strafe * strafe + forward * forward;
                    if (f >= 1.0E-4f) {
                        f = MathHelper.sqrt_float(f);
                        if (f < 1.0)
                            f = 1.0f;
                        f = friction / f;
                        strafe *= f;
                        forward *= f;
                        float yawSin = MathHelper.sin((float) (targetRotation.getYaw() * Math.PI / 180));
                        float yawCos = MathHelper.cos((float) (targetRotation.getYaw() * Math.PI / 180));
                        mc.thePlayer.motionX += strafe * yawCos - forward * yawSin;
                        mc.thePlayer.motionZ += forward * yawCos + strafe * yawSin;
                    }

                } else {
                    LookUtils.applyStrafeToPlayer(eventStrafe, targetRotation.getYaw());
                }
            }


        } else if (e instanceof EventPacket) {
            EventPacket ep = (EventPacket) e;
            Packet packet = ep.getPacket();
            if (packet instanceof C03PacketPlayer) {
                C03PacketPlayer C03 = (C03PacketPlayer) packet;
                if (C03.rotating) {
                    serverRotation = new Rotation(C03.getYaw(), C03.getPitch());
                }
            }
        } else if (e instanceof Event3D) {
            Event3D event3D = (Event3D) e;
            if (circle.getObject()) {
                drawCircle(mc.thePlayer, event3D.getPartialTicks(), reach.getObject());
            }
            if (esp.getMode().equalsIgnoreCase("Jello") && target != null) {
                drawESP(target, ((Event3D) e).getPartialTicks(), .5);
            }
        }
    }

    public float getHitDelay(ItemStack item) {
        if (item == null) {
            return 2;
        }
        if (item.getItem() instanceof ItemSword) {
            return 1.6f;
        }
        if (item.getItem() instanceof ItemAxe) {
            if (item.getItem().getUnlocalizedName().equalsIgnoreCase("hatchetWood") || item.getItem().getUnlocalizedName().equalsIgnoreCase("hatchetStone")) {
                return 0.8f;
            }
            if (item.getItem().getUnlocalizedName().equalsIgnoreCase("hatchetIron")) {
                return 0.9f;
            }
            return 1;

        }
        return 2f;
    }

    private void noSprint(boolean b) {
        KeyBinding sneakBinding = mc.gameSettings.keyBindSprint;
        try {
            Field field = sneakBinding.getClass().getDeclaredField("pressed");
            field.setAccessible(true);
            field.setBoolean(sneakBinding, b);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public void onUpdate() {
        try {

            updateTarget();

            this.setDisplayName("Aura \2477" + killAuraMode.getModes()[killAuraMode.getObject()] + " " + RotMode.getModes()[RotMode.getObject()] + " " + reach.getObject());
            if (target == null)
                return;
            boolean shouldMiss = randomNumber(0, 100) > 100;
            if (BlockMode.getObject() == 0 || BlockMode.getObject() == 1 || BlockMode.getObject() == 4) {
                boolean block = target != null && autoBlock.getObject() && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
                if (block && target.getDistanceToEntity(mc.thePlayer) < reach.getObject())
                    if (BlockMode.getObject() == 4) {
                        mc.thePlayer.setItemInUse(mc.thePlayer.inventory.getCurrentItem(), 10);
                    } else {
                        mc.thePlayer.setItemInUse(mc.thePlayer.inventory.getCurrentItem(), 10);
                    }
            }
            if (entityBeSeen.getObject() && !mc.thePlayer.canEntityBeSeen(target))
                return;
            y = 0;
            if (RotMode.getObject() == 0) {

                float[] rot = LookUtils.getPredictedRotations(target);
                targetRotation = new Rotation(rot[0], rot[1]);
            } else if (RotMode.getObject() == 1) {
                if (RotMode.getObject() == 1) {
                    if (!LookUtils.isFaced3(target, reach.getObject()) && aac.getObject()) {
                        smoothAimFixGcd();
                    } else if (!aac.getObject()) {
                        smoothAimFixGcd();
                    }
                }
            } else if (RotMode.getObject() == 2) {
                if (!LookUtils.isFaced3(target, reach.getObject()) && aac.getObject()) {

                    legitAim2();
                } else if (!aac.getObject()) {

                    legitAim2();
                }
            } else if (RotMode.getObject() == 3) {
                if (!LookUtils.isFaced3(target, reach.getObject()) && aac.getObject()) {
                    headShakeAim();
                } else if (!aac.getObject()) {
                    headShakeAim();
                }
            } else if (RotMode.getMode().equalsIgnoreCase("None")) {
                targetRotation = new Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
            } else if (RotMode.getMode().equalsIgnoreCase("Crazy")) {
                targetRotation = new Rotation(MathHelper.wrapAngleTo180_float(randomNumber(360, -360)), randomNumber(90, -90));
            }
            if (Crispy.INSTANCE.getHackManager().getHack(AutoPot.class).isPotting()) {
                targetRotation = new Rotation(targetRotation.getYaw(), 90);
            }
        } catch (Exception ignored) {

        }
    }

    private void attack(EntityLivingBase entity, boolean isPre) {

        if (BlockMode.getObject() == 0 || BlockMode.getMode().equalsIgnoreCase("Verus") || BlockMode.getObject() == 1) {
            boolean block = target != null && autoBlock.getObject() && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
            if (block) {
                //mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            }
        }
        if (clickPattern.getObject() == 1) {
            clickDelayIndex++;
        }
        //Don't wanna attack null entity !
        if (hittableTarget == null && fixRaycast.getObject()) {
            mc.thePlayer.swingItem();
            return;
        }
        if (crack.getObject()) {
            for (int i = 0; i < crackSize.getObject(); i++)
                mc.thePlayer.onCriticalHit(fixRaycast.getObject() ? hittableTarget : target);
        }
        if (isPre) {
            Criticals criticals = Crispy.INSTANCE.getHackManager().getHack(Criticals.class);
            if (criticals.isEnabled() || TargetManager.INSTANCE.getTargets().contains(target)) criticals.critical();
            mc.thePlayer.swingItem();

            if (attackMode.getMode().equalsIgnoreCase("Syphlex")) {
                mc.thePlayer.attackTargetEntityWithCurrentItem(fixRaycast.getObject() ? hittableTarget : target);

            } else {
                mc.playerController.attackEntity(mc.thePlayer, fixRaycast.getObject() ? hittableTarget : target);
            }
        }

        if (BlockMode.getObject() == 2) {
            boolean block = target != null && autoBlock.getObject() && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
            if (block) {
                mc.playerController.sendUseItem(mc.thePlayer, Minecraft.theWorld, mc.thePlayer.inventory.getCurrentItem());
            }

        }

    }

    public void updateTarget() {
        postTarget = target;
        if (killAuraMode.getObject() == 0) {
            target = getClosest(reach.getObject());

        } else if (killAuraMode.getObject() == 1) {

            if (switchTimer.hasReached(switchDelay.getObject())) {
                loaded = getTargets();
            }
            if (index >= loaded.size()) {
                index = 0;
            }
            if (switchTimer.hasReached(switchDelay.getObject()) && loaded.size() > 0) {
                index += 1;
                if (index >= loaded.size()) {
                    index = 0;
                }
                switchTimer.reset();
            }


            if (loaded.size() > 0)
                target = loaded.get(index);
            else
                target = null;
        } else if (killAuraMode.getObject() == 2) {
            loaded = getTargets();
            if (!loaded.isEmpty())
                target = loaded.get(0);


            if (mc.thePlayer.getDistanceToEntity(target) > reach.getObject())
                target = null;

        }
        if (fixRaycast.getObject()) {
            //Always return true since we want to raycast always!!!!!!!
            hittableTarget = LookUtils.raycastEntity(reach.getObject(), Entity::isEntityAlive);
        }

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

    private List<EntityLivingBase> getTargets() {
        List<EntityLivingBase> targets = new ArrayList<>();
        //List entities = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer, mc.thePlayer.boundingBox.expand(range, range, range));
        //for (Object o : entities) {
        for (Object o : Minecraft.theWorld.loadedEntityList) {
            if (o instanceof EntityLivingBase) {
                EntityLivingBase entity = (EntityLivingBase) o;
                if (canAttack(entity)) {
                    targets.add(entity);
                }
            }
        }
        sortList(targets);
        return targets;
    }

    private void sortList(List<EntityLivingBase> oof) {

        oof.sort((o1, o2) -> (int) (o1.getHealth() - o2.getHealth()));
        oof.sort(Comparator
                .comparingInt(o -> (o instanceof EntityPlayer ? ((EntityPlayer) o).inventory
                        .getTotalArmorValue() : (int) o.getHealth())));

    }

    private void noSnap() {
        float targetYaw = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw - yaw);
        float yawFactor = targetYaw / 3;
        float[] fixedYaw = LookUtils.fixedSensitivity(mc.gameSettings.mouseSensitivity, yaw + yawFactor, 0);
        yaw += yawFactor;
        float targetPitch = mc.thePlayer.rotationPitch - pitch;
        float pitchFactor = targetPitch / 3;

        float[] fixedPitch = LookUtils.fixedSensitivity(mc.gameSettings.mouseSensitivity, 0, pitch + pitchFactor);
        pitch += pitchFactor;

        targetRotation = new Rotation(fixedYaw[0], fixedPitch[1]);
    }

    private void smoothAimFixGcd() {

        float targetYaw = LookUtils.getYawChange(yaw, target.posX, target.posZ);
        float yawFactor = targetYaw / yawSmoothing.getObject();

        float[] fixedYaw = LookUtils.fixedSensitivity(mc.gameSettings.mouseSensitivity, yaw + yawFactor, 0);

        yaw += yawFactor;
        float targetPitch = LookUtils.getPitchChange(pitch, target, target.posY);
        float pitchFactor = targetPitch / pitchSmoothing.getObject();
        float[] fixedPitch = LookUtils.fixedSensitivity(mc.gameSettings.mouseSensitivity, 0, pitch + pitchFactor);
        pitch += pitchFactor;
        //No bad pitch method for u
        if (pitch > 90)
            return;
        targetRotation = new Rotation(fixedYaw[0], fixedPitch[1]);

    }

    private void headShakeAim() {
        Random random = new Random();
        double randomX = random.nextDouble() * 0.1;
        double randomZ = random.nextDouble() * 0.1;
        double randomY = random.nextDouble() * 0.1;
        float targetYaw = LookUtils.getYawChange(yaw, target.posX + randomX, target.posZ + randomZ);
        float yawFactor = targetYaw / 1.1f;

        float[] fixedYaw = LookUtils.fixedSensitivity(mc.gameSettings.mouseSensitivity, yaw + yawFactor, 0);

        yaw += yawFactor;
        float targetPitch = LookUtils.getPitchChange(pitch, target, target.posY + randomY);
        float pitchFactor = targetPitch / 1.1f;
        float[] fixedPitch = LookUtils.fixedSensitivity(mc.gameSettings.mouseSensitivity, 0, pitch + pitchFactor);
        pitch += pitchFactor;
        if (fixedPitch[1] > 90) {
            fixedPitch[1] = 90;
        }

        targetRotation = new Rotation(fixedYaw[0], fixedPitch[1]);

    }

    public void legitAim2() {
        try {
            if (target != null) {
                AxisAlignedBB bb = target.getEntityBoundingBox();
                //jitter fixed??

                VecRotation rotation = LookUtils.searchCenter(bb, false, false, mc.thePlayer.getDistanceToEntity(target));
                Rotation limit = LookUtils.limitAngleChange(targetRotation, rotation.getRotation(), randomNumber(100, 1));
                float[] fixedRotation = LookUtils.fixedSensitivity(mc.gameSettings.mouseSensitivity, limit.getYaw(), limit.getPitch());
                if (fixedRotation[1] > 90)
                    return;

                targetRotation = new Rotation(fixedRotation[0], fixedRotation[1]);
            }
        } catch (Exception e) {
        }
    }

    public float[] getCustomRotsChange(float yaw, float pitch, double x, double y, double z) {

        double xDiff = x - mc.thePlayer.posX;
        double zDiff = z - mc.thePlayer.posZ;
        double yDiff = y - mc.thePlayer.posY;

        double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
        double mult = (1 / (dist + 0.0001)) * 2;
        if (mult > 0.2)
            mult = 0.2;
        if (!Minecraft.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer, mc.thePlayer.boundingBox).contains(target)) {
            x += 0.3 * randoms[0];
            y -= 0.4 + mult * randoms[1];
            z += 0.3 * randoms[2];
        }
        xDiff = x - mc.thePlayer.posX;
        zDiff = z - mc.thePlayer.posZ;
        yDiff = y - mc.thePlayer.posY;
        float yawToEntity = (float) (Math.atan2(zDiff, xDiff) * 180.0D / 3.141592653589793D) - 90.0F;
        float pitchToEntity = (float) -(Math.atan2(yDiff, dist) * 180.0D / 3.141592653589793D);
        return new float[]{MathHelper.wrapAngleTo180_float(-(yaw - yawToEntity)), -MathHelper.wrapAngleTo180_float(pitch - pitchToEntity) - 2.5F};
    }

    private boolean canAttack(EntityLivingBase player) {
        if (player instanceof EntityPlayer || player instanceof EntityAnimal || player instanceof EntityMob || player instanceof EntityVillager) {
            if (player instanceof EntityPlayer && !players.getObject())
                return false;
            if (player instanceof EntityAnimal && !others.getObject())
                return false;
            if (player instanceof EntityMob && !others.getObject())
                return false;
            if (player instanceof EntityVillager && !others.getObject())
                return false;
        }

        if (Crispy.INSTANCE.getFriendManager().isFriend(player.getName()))
            return false;
        if (player.isOnSameTeam(mc.thePlayer) && teams.getObject())
            return false;
        if (player.isInvisible() && !invis.getObject())
            return false;


        if (AntiBot.getInvalid().contains(player) || player.isPlayerSleeping())
            return false;
        //if (Crispy.friendManager.isFriend(player.getCommandSenderName()))
        //    return false;

        if (!isInFOV(player, fov.getObject()))
            return false;


        return player != mc.thePlayer && mc.thePlayer.getDistanceToEntity(player) <= reach.getObject() && player.ticksExisted > existed.getObject();
    }

    private boolean isInFOV(EntityLivingBase entity, double angle) {
        angle *= .5D;
        double angleDiff = getAngleDifference(mc.thePlayer.rotationYaw, getRotations(entity.posX, entity.posY, entity.posZ)[0]);
        return (angleDiff > 0 && angleDiff < angle) || (-angle < angleDiff && angleDiff < 0);

    }


    private void drawCircle(Entity entity, float partialTicks, double rad) {
        glPushMatrix();
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glLineWidth(1.0f);
        glBegin(GL_LINE_STRIP);

        final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - mc.getRenderManager().viewerPosX;
        final double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - mc.getRenderManager().viewerPosY;
        final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - mc.getRenderManager().viewerPosZ;

        final float r = ((float) 1 / 255) * Color.WHITE.getRed();
        final float g = ((float) 1 / 255) * Color.WHITE.getGreen();
        final float b = ((float) 1 / 255) * Color.WHITE.getBlue();

        final double pix2 = Math.PI * 2.0D;

        for (int i = 0; i <= 90; ++i) {
            glColor3f(r, g, b);
            glVertex3d(x + rad * Math.cos(i * pix2 / 45.0), y, z + rad * Math.sin(i * pix2 / 45.0));
        }

        glEnd();
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
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

    private void drawESP(Entity entity, float partialTicks, double rad) {
        glPushMatrix();
        glDisable(GL_TEXTURE_2D);

        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glBegin(GL_LINE_STRIP);

        final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - mc.getRenderManager().viewerPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - mc.getRenderManager().viewerPosY;
        final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - mc.getRenderManager().viewerPosZ;

        float translation = 100.4F / Minecraft.getDebugFPS();

        if (lol) {
            animation += translation / 20;
        } else {
            animation -= translation / 20;
        }
        if (animation > y + 2) {
            lol = false;
        }
        if (animation < y - 0) {
            lol = true;
        }
        y = animation;


        final float r = ((float) 1 / 255) * HUD.effect(-1, (float) 0.5, 500).getRed();
        final float g = ((float) 1 / 255) * HUD.effect(-1, (float) 0.5, 500).getGreen();
        final float b = ((float) 1 / 255) * HUD.effect(-1, (float) 0.5, 500).getBlue();

        final double pix2 = Math.PI * 2.0D;

        for (int i = 0; i <= 90; ++i) {
            glColor3f(r, g, b);
            glVertex3d(x + rad * Math.cos(i * pix2 / 45.0), y, z + rad * Math.sin(i * pix2 / 45.0));
        }

        glEnd();
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);

        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
    }

    public void drawTraceBack() {

    }


}
