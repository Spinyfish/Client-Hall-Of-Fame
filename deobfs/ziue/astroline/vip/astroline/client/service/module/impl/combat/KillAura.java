/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.EnumCreatureAttribute
 *  net.minecraft.entity.item.EntityArmorStand
 *  net.minecraft.entity.monster.EntitySlime
 *  net.minecraft.entity.monster.EntitySpider
 *  net.minecraft.entity.passive.EntityBat
 *  net.minecraft.entity.passive.EntityPig
 *  net.minecraft.entity.passive.EntitySquid
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemSword
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.C02PacketUseEntity
 *  net.minecraft.network.play.client.C02PacketUseEntity$Action
 *  net.minecraft.util.MathHelper
 *  vip.astroline.client.service.event.impl.move.EventPostUpdate
 *  vip.astroline.client.service.event.impl.move.EventPreUpdate
 *  vip.astroline.client.service.event.impl.render.Event2D
 *  vip.astroline.client.service.event.impl.render.EventShader
 *  vip.astroline.client.service.event.types.EventTarget
 *  vip.astroline.client.service.module.Category
 *  vip.astroline.client.service.module.Module
 *  vip.astroline.client.service.module.impl.combat.KillAura$TargetHUD
 *  vip.astroline.client.service.module.value.BooleanValue
 *  vip.astroline.client.service.module.value.FloatValue
 *  vip.astroline.client.service.module.value.ModeValue
 *  vip.astroline.client.storage.utils.angle.Angle
 *  vip.astroline.client.storage.utils.angle.AngleUtility
 *  vip.astroline.client.storage.utils.angle.RotationUtil
 *  vip.astroline.client.storage.utils.other.DelayTimer
 *  vip.astroline.client.storage.utils.vector.impl.Vector3
 */
package vip.astroline.client.service.module.impl.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.MathHelper;
import vip.astroline.client.service.event.impl.move.EventPostUpdate;
import vip.astroline.client.service.event.impl.move.EventPreUpdate;
import vip.astroline.client.service.event.impl.render.Event2D;
import vip.astroline.client.service.event.impl.render.EventShader;
import vip.astroline.client.service.event.types.EventTarget;
import vip.astroline.client.service.module.Category;
import vip.astroline.client.service.module.Module;
import vip.astroline.client.service.module.impl.combat.KillAura;
import vip.astroline.client.service.module.value.BooleanValue;
import vip.astroline.client.service.module.value.FloatValue;
import vip.astroline.client.service.module.value.ModeValue;
import vip.astroline.client.storage.utils.angle.Angle;
import vip.astroline.client.storage.utils.angle.AngleUtility;
import vip.astroline.client.storage.utils.angle.RotationUtil;
import vip.astroline.client.storage.utils.other.DelayTimer;
import vip.astroline.client.storage.utils.vector.impl.Vector3;

public class KillAura
extends Module {
    public static EntityLivingBase target;
    public static List<EntityLivingBase> targets;
    public static List<EntityLivingBase> blockTargets;
    public static ModeValue priority;
    public static ModeValue targetHUDMode;
    public static BooleanValue targetHUD;
    public static BooleanValue autoBlock;
    public static FloatValue range;
    public static FloatValue blockRange;
    public static FloatValue rotationSpeed;
    public static FloatValue aps;
    public static FloatValue switchDelay;
    public static FloatValue switchSize;
    public static FloatValue fov;
    public static BooleanValue attackMob;
    public static BooleanValue attackPlayer;
    public static BooleanValue blockThoughWall;
    public static BooleanValue attackInvisible;
    public static ArrayList<EntityLivingBase> attacked;
    public static DelayTimer disableHelper;
    private EventPreUpdate looked;
    public static int killed;
    public boolean canAttack;
    boolean isSwitch;
    AngleUtility angleUtility = new AngleUtility(110.0f, 120.0f, 30.0f, 40.0f);
    private int nextAttackDelay;
    private DelayTimer switchTimer = new DelayTimer();
    private DelayTimer delayTimer = new DelayTimer();
    private int index;
    private float[] lastRotations;
    public static boolean blockedStatusForRender;
    public static boolean blocked;

    public KillAura() {
        super("KillAura", Category.Combat, 19, false);
    }

    /*
     * Unable to fully structure code
     */
    @EventTarget
    public void onPreUpdate(EventPreUpdate event) {
        if (!KillAura.disableHelper.isDelayComplete(50.0)) {
            return;
        }
        this.looked = null;
        KillAura.blockedStatusForRender = KillAura.blocked;
        if (KillAura.blocked) {
            KillAura.blocked = false;
        }
        try {
            String.class.getMethods()[76].getName();
        }
        catch (Throwable throwable) {
            this.updateAttackTargets();
            this.updateBlockTargets();
        }
        if (KillAura.targets.isEmpty()) {
            KillAura.target = null;
            this.lastRotations = null;
            return;
        }
        if (this.index > KillAura.targets.size() - 1) {
            this.index = 0;
        }
        if (KillAura.targets.size() > 1 && this.switchTimer.hasPassed((double)KillAura.switchDelay.getValue().floatValue())) {
            ++this.index;
            this.switchTimer.reset();
        }
        if (this.index > KillAura.targets.size() - 1 || !this.isSwitch) {
            this.index = 0;
        }
        KillAura.target = KillAura.targets.get(this.index);
        if (this.lastRotations == null) {
            this.lastRotations = new float[]{KillAura.mc.thePlayer.rotationYaw, KillAura.mc.thePlayer.rotationPitch};
        }
        rotationSpeed = 180.0f - KillAura.rotationSpeed.getValue().floatValue() / 2.0f;
        target = KillAura.target;
        comparison = Math.abs(target.posY - KillAura.mc.thePlayer.posY) > 1.8 ? Math.abs(target.posY - KillAura.mc.thePlayer.posY) / Math.abs(target.posY - KillAura.mc.thePlayer.posY) / 2.0 : Math.abs(target.posY - KillAura.mc.thePlayer.posY);
        enemyCoords = new Vector3((Number)(target.getEntityBoundingBox().minX + (target.getEntityBoundingBox().maxX - target.getEntityBoundingBox().minX) / 2.0), (Number)((target instanceof EntityPig != false || target instanceof EntitySpider != false ? target.getEntityBoundingBox().minY - (double)target.getEyeHeight() * 1.2 : target.posY) - comparison), (Number)(target.getEntityBoundingBox().minZ + (target.getEntityBoundingBox().maxZ - target.getEntityBoundingBox().minZ) / 2.0));
        myCoords = new Vector3((Number)(KillAura.mc.thePlayer.getEntityBoundingBox().minX + (KillAura.mc.thePlayer.getEntityBoundingBox().maxX - KillAura.mc.thePlayer.getEntityBoundingBox().minX) / 2.0), (Number)KillAura.mc.thePlayer.posY, (Number)(KillAura.mc.thePlayer.getEntityBoundingBox().minZ + (KillAura.mc.thePlayer.getEntityBoundingBox().maxZ - KillAura.mc.thePlayer.getEntityBoundingBox().minZ) / 2.0));
        srcAngle = new Angle(Float.valueOf(this.lastRotations[0]), Float.valueOf(this.lastRotations[1]));
        dstAngle = this.angleUtility.calculateAngle(enemyCoords, myCoords);
        smoothedAngle = this.angleUtility.smoothAngle(dstAngle, srcAngle, rotationSpeed, rotationSpeed);
        this.lastRotations = new float[]{event.yaw, event.pitch};
        event.setYaw(dstAngle.getYaw().floatValue());
        event.setPitch(dstAngle.getPitch().floatValue());
        KillAura.mc.thePlayer.rotationYawHead = dstAngle.getYaw().floatValue();
        KillAura.mc.thePlayer.renderYawOffset = dstAngle.getYaw().floatValue();
        KillAura.mc.thePlayer.rotationPitchHead = dstAngle.getPitch().floatValue();
        this.looked = event;
        if (this.delayTimer.hasPassed((double)this.nextAttackDelay)) ** GOTO lbl-1000
        if (KillAura.aps.getValue().floatValue() == 15.0f) {
            ** if (KillAura.mc.thePlayer.ticksExisted % 3 != 0) goto lbl-1000
        }
        ** GOTO lbl-1000
lbl-1000:
        // 2 sources

        {
            v0 = true;
            ** GOTO lbl51
        }
lbl-1000:
        // 2 sources

        {
            v0 = false;
        }
lbl51:
        // 2 sources

        this.canAttack = v0;
    }

    @EventTarget
    public void onLoop(EventPostUpdate event) {
        if (!disableHelper.isDelayComplete(50.0)) {
            return;
        }
        if (this.looked != null && this.canAttack) {
            this.nextAttackDelay = (int)(1000.0 / ((double)aps.getValue().floatValue() + KillAura.getRandomDoubleInRange(-1.0, 1.0)));
            this.delayTimer.reset();
            EntityLivingBase target = targets.get(this.index);
            if (KillAura.mc.thePlayer.getDistanceToEntity((Entity)targets.get(this.index)) <= range.getValue().floatValue()) {
                this.attackEntity(target);
            }
        }
        if (autoBlock.getValue() == false) return;
        if (blockTargets.size() <= 0) return;
        if (!KillAura.isHeldingSword()) return;
        if (blocked) return;
        if (!(targets.size() == 0 || targets.get(this.index).canEntityBeSeen((Entity)KillAura.mc.thePlayer) && targets.size() <= 1)) {
            if (!blockThoughWall.getValueState()) return;
        }
        blocked = true;
    }

    public static boolean isHeldingSword() {
        return KillAura.mc.thePlayer.getHeldItem() != null && KillAura.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    public static double getRandomDoubleInRange(double minDouble, double maxDouble) {
        return minDouble >= maxDouble ? minDouble : new Random().nextDouble() * (maxDouble - minDouble) + minDouble;
    }

    public void updateAttackTargets() {
        if (!targets.isEmpty() && !targets.stream().anyMatch(e -> !KillAura.isValid(e, range.getValue().floatValue()))) {
            if (targets.size() == this.getTargets(true).size()) return;
        }
        targets = this.getTargets(true);
    }

    public void updateBlockTargets() {
        if (!blockTargets.isEmpty() && !blockTargets.stream().anyMatch(e -> !KillAura.isValid(e, blockRange.getValue().floatValue()))) {
            if (blockTargets.size() == this.getTargets(false).size()) return;
        }
        blockTargets = this.getTargets(false);
    }

    public static boolean isValid(EntityLivingBase entity, double maxRange) {
        if (entity.isDead || entity.getHealth() <= 0.0f || entity == KillAura.mc.thePlayer) {
            if (!attacked.contains(entity)) return false;
            ++killed;
            attacked.remove(entity);
            return false;
        }
        if (entity instanceof EntityArmorStand) {
            return false;
        }
        if (entity.isInvisible() && !attackInvisible.getValue().booleanValue()) {
            return false;
        }
        if (entity instanceof EntityPlayer && !attackPlayer.getValue().booleanValue()) {
            return false;
        }
        if ((entity instanceof EntityCreature || entity instanceof EntityBat || entity instanceof EntitySlime || entity instanceof EntitySquid) && !attackMob.getValue().booleanValue()) {
            return false;
        }
        if ((double)entity.getDistanceToEntity((Entity)KillAura.mc.thePlayer) > maxRange) {
            return false;
        }
        if (RotationUtil.isVisibleFOV((Entity)entity, (float)fov.getValue().floatValue())) return true;
        return false;
    }

    @EventTarget
    public void Render2D(Event2D event) {
        if (!targetHUD.getValueState()) return;
        if (!disableHelper.isDelayComplete(50.0)) return;
        ScaledResolution sr = new ScaledResolution(mc);
        int renderIndex = 0;
        Iterator iterator = KillAura.mc.theWorld.loadedEntityList.iterator();
        while (iterator.hasNext()) {
            Entity base = (Entity)iterator.next();
            if (!(base instanceof EntityPlayer)) continue;
            EntityPlayer player = (EntityPlayer)base;
            if (targets.contains(base)) {
                if (player.targetHUD == null) {
                    player.targetHUD = new TargetHUD(this, player);
                }
                int size = 33;
                if (targetHUDMode.isCurrentMode("Flux (Old)")) {
                    size = 43;
                } else if (targetHUDMode.isCurrentMode("Astolfo")) {
                    size = 60;
                }
                player.targetHUD.render((float)sr.getScaledWidth() / 2.0f + 14.0f, (float)sr.getScaledHeight() / 2.0f - 14.0f + (float)(renderIndex * size));
                ++renderIndex;
                continue;
            }
            if (player.targetHUD == null) continue;
            player.targetHUD.animation = 0.0f;
        }
    }

    @EventTarget
    public void onShader(EventShader event) {
        if (!targetHUD.getValueState()) return;
        if (!disableHelper.isDelayComplete(50.0)) return;
        ScaledResolution sr = new ScaledResolution(mc);
        int renderIndex = 0;
        Iterator iterator = KillAura.mc.theWorld.loadedEntityList.iterator();
        while (iterator.hasNext()) {
            Entity base = (Entity)iterator.next();
            if (!(base instanceof EntityPlayer)) continue;
            EntityPlayer player = (EntityPlayer)base;
            if (targets.contains(base)) {
                if (player.targetHUD == null) {
                    player.targetHUD = new TargetHUD(this, player);
                }
                int size = 33;
                if (targetHUDMode.isCurrentMode("Flux (Old)")) {
                    size = 43;
                } else if (targetHUDMode.isCurrentMode("Astolfo")) {
                    size = 60;
                }
                player.targetHUD.render((float)sr.getScaledWidth() / 2.0f + 14.0f, (float)sr.getScaledHeight() / 2.0f - 14.0f + (float)(renderIndex * size));
                ++renderIndex;
                continue;
            }
            if (player.targetHUD == null) continue;
            player.targetHUD.animation = 0.0f;
        }
    }

    private List<EntityLivingBase> getTargets(boolean isAttack) {
        Stream<EntityLivingBase> stream = KillAura.mc.theWorld.loadedEntityList.stream().filter(entity -> entity instanceof EntityLivingBase).map(entity -> (EntityLivingBase)entity).filter(isAttack ? KillAura::isValidAttack : KillAura::isValidBlock);
        if (priority.isCurrentMode("Armor")) {
            stream = stream.sorted(Comparator.comparingInt(o -> o instanceof EntityPlayer ? ((EntityPlayer)o).inventory.getTotalArmorValue() : (int)o.getHealth()));
        } else if (priority.isCurrentMode("Range")) {
            stream = stream.sorted((o1, o2) -> (int)(o1.getDistanceToEntity((Entity)KillAura.mc.thePlayer) - o2.getDistanceToEntity((Entity)KillAura.mc.thePlayer)));
        } else if (priority.isCurrentMode("Fov")) {
            stream = stream.sorted(Comparator.comparingDouble(o -> KillAura.getDistanceBetweenAngles(KillAura.mc.thePlayer.rotationPitch, this.getLoserRotation((Entity)o)[0])));
        } else if (priority.isCurrentMode("Angle")) {
            stream = stream.sorted((o1, o2) -> {
                float[] rot1 = RotationUtil.getRotation((Entity)o1);
                float[] rot2 = RotationUtil.getRotation((Entity)o2);
                return (int)(KillAura.mc.thePlayer.rotationYaw - rot1[0] - (KillAura.mc.thePlayer.rotationYaw - rot2[0]));
            });
        } else if (priority.isCurrentMode("Health")) {
            stream = stream.sorted((o1, o2) -> (int)(o1.getHealth() - o2.getHealth()));
        }
        List list = stream.collect(Collectors.toList());
        return list.subList(0, Math.min(list.size(), switchSize.getValue().intValue()));
    }

    public static boolean isValidAttack(EntityLivingBase entityLivingBase) {
        return KillAura.isValid(entityLivingBase, range.getValue().floatValue());
    }

    public static boolean isValidBlock(EntityLivingBase entityLivingBase) {
        return KillAura.isValid(entityLivingBase, range.getValue().floatValue() + blockRange.getValue().floatValue());
    }

    public static float getDistanceBetweenAngles(float angle1, float angle2) {
        float angle3 = Math.abs(angle1 - angle2) % 360.0f;
        if (!(angle3 > 180.0f)) return angle3;
        angle3 = 0.0f;
        return angle3;
    }

    public float[] getLoserRotation(Entity target) {
        double xDiff = target.posX - KillAura.mc.thePlayer.posX;
        double yDiff = target.posY - KillAura.mc.thePlayer.posY - 0.4;
        double zDiff = target.posZ - KillAura.mc.thePlayer.posZ;
        double dist = MathHelper.sqrt_double((double)(xDiff * xDiff + zDiff * zDiff));
        float yaw = (float)(Math.atan2(zDiff, xDiff) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(-Math.atan2(yDiff, dist) * 180.0 / Math.PI);
        float[] array = new float[2];
        float rotationYaw = KillAura.mc.thePlayer.rotationYaw;
        array[0] = rotationYaw + MathHelper.wrapAngleTo180_float((float)(yaw - KillAura.mc.thePlayer.rotationYaw));
        float rotationPitch = KillAura.mc.thePlayer.rotationPitch;
        array[1] = rotationPitch + MathHelper.wrapAngleTo180_float((float)(pitch - KillAura.mc.thePlayer.rotationPitch));
        return array;
    }

    public void attackEntity(EntityLivingBase entity) {
        if (entity == null) {
            return;
        }
        if (!attacked.contains(entity) && entity instanceof EntityPlayer) {
            attacked.add(entity);
        }
        KillAura.mc.thePlayer.swingItem();
        mc.getNetHandler().getNetworkManager().sendPacket((Packet)new C02PacketUseEntity((Entity)entity, C02PacketUseEntity.Action.ATTACK));
        float sharpLevel = EnchantmentHelper.func_152377_a((ItemStack)KillAura.mc.thePlayer.inventory.getCurrentItem(), (EnumCreatureAttribute)entity.getCreatureAttribute());
        if (!(sharpLevel > 0.0f)) return;
        KillAura.mc.thePlayer.onEnchantmentCritical((Entity)entity);
    }

    public static boolean isRenderBlocked() {
        return blockedStatusForRender;
    }

    public static void unblock() {
        if (!blocked) return;
        blocked = false;
    }

    public void onEnable() {
        this.index = 0;
        this.lastRotations = null;
        attacked.clear();
        super.onEnable();
    }

    public void onDisable() {
        KillAura.unblock();
        blockedStatusForRender = false;
        target = null;
        super.onDisable();
    }

    static {
        targets = new ArrayList<EntityLivingBase>();
        blockTargets = new ArrayList<EntityLivingBase>();
        priority = new ModeValue("KillAura", "Priority", "Angle", new String[]{"Angle", "Health", "Fov", "Range", "Armor"});
        targetHUDMode = new ModeValue("KillAura", "TargetHUD Mode", "Flux", new String[]{"Flux", "Flux (Old)", "Astolfo", "Innominate"});
        targetHUD = new BooleanValue("KillAura", "Target HUD", Boolean.valueOf(true));
        autoBlock = new BooleanValue("KillAura", "Autoblock", Boolean.valueOf(false));
        range = new FloatValue("KillAura", "Range", 4.2f, 1.0f, 8.0f, 0.1f);
        blockRange = new FloatValue("KillAura", "Block Range", 2.0f, 0.0f, 4.0f, 0.1f);
        rotationSpeed = new FloatValue("KillAura", "Rotation Speed", 120.0f, 30.0f, 180.0f, 1.0f);
        aps = new FloatValue("KillAura", "APS", 10.0f, 1.0f, 20.0f, 1.0f);
        switchDelay = new FloatValue("KillAura", "Switch Delay", 1000.0f, 1.0f, 2000.0f, 100.0f);
        switchSize = new FloatValue("KillAura", "Switch Size", 3.0f, 1.0f, 8.0f, 1.0f);
        fov = new FloatValue("KillAura", "FoV", 360.0f, 10.0f, 360.0f, 10.0f, "\u00b0");
        attackMob = new BooleanValue("KillAura", "Mob", Boolean.valueOf(false));
        attackPlayer = new BooleanValue("KillAura", "Player", Boolean.valueOf(true));
        blockThoughWall = new BooleanValue("KillAura", "Block Through Wall", Boolean.valueOf(false));
        attackInvisible = new BooleanValue("KillAura", "Invisible", Boolean.valueOf(true));
        attacked = new ArrayList();
        disableHelper = new DelayTimer();
    }
}
