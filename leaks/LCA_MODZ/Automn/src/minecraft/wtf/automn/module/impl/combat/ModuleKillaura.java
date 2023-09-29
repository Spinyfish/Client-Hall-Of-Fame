package wtf.automn.module.impl.combat;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import wtf.automn.Automn;
import wtf.automn.events.EventHandler;
import wtf.automn.events.impl.combat.EventClickMouse;
import wtf.automn.events.impl.minecraft.EventPostMouseOver;
import wtf.automn.events.impl.minecraft.EventPreMouseOver;
import wtf.automn.events.impl.player.*;
import wtf.automn.events.impl.player.EventMotion;
import wtf.automn.module.Category;
import wtf.automn.module.Module;
import wtf.automn.module.ModuleInfo;
import wtf.automn.module.settings.Setting;
import wtf.automn.module.settings.SettingBoolean;
import wtf.automn.module.settings.SettingNumber;
import wtf.automn.utils.math.RandomUtil;
import wtf.automn.utils.math.RayCastUtil;
import wtf.automn.utils.math.RotationUtil;
import wtf.automn.utils.math.TimeUtil;
import wtf.automn.utils.minecraft.ChatUtil;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@ModuleInfo(name = "killaura", displayName = "Killaura", category = Category.COMBAT)
public class ModuleKillaura extends Module {
    private final TimeUtil time = new TimeUtil();
    public EntityLivingBase target = null;

    public final SettingNumber preRange = new SettingNumber("preRange", 6D, 3D, 10D, "PreRange", this, "set the Range");
    public final SettingNumber range = new SettingNumber("killauraRange", 3D, 3D, 6D, "Range", this, "set the Range");
    public final SettingNumber minYaw = new SettingNumber("MinYaw", 30D, 1D, 180D, "MinYawSpeed", this, "set the MinYawSpeed");
    public final SettingNumber maxYaw = new SettingNumber("MaxYaw", 50D, 1D, 180D, "MaxYawSpeed", this, "set the MaxYawSpeed");
    public final SettingNumber minPitch = new SettingNumber("MinPitch", 6D, 1D, 180D, "MinPitchSpeed", this, "set the MinPitchSpeed");
    public final SettingNumber maxPitch = new SettingNumber("MaxPitch", 18D, 1D, 180D, "MaxPitchSpeed", this, "set the MaxPitchSpeed");
    public final SettingNumber minCPS = new SettingNumber("minnCPS", 10D, 1D, 20D, "MinCPS", this, "set the MinCPS");
    public final SettingNumber maxCPS = new SettingNumber("maxxCPS", 18D, 2D, 20D, "MaxCPS", this, "set the MaxCPS");
    public final SettingNumber randomStrength = new SettingNumber("randomStrength", 1.15D, 0D, 4D, "RandomStrength", this, "set the RandomStrength");
    public final SettingBoolean rayCast = new SettingBoolean("rayCast", true, "RayCast", this, "attacks entitys between you and target");
    public final SettingBoolean throughWalls = new SettingBoolean("throughWalls", false, "ThroughWalls", this, "attacks target through a wall");
    public final SettingBoolean inInv = new SettingBoolean("inInv", false, "InInv", this, "attacks target in Inv");
    public final SettingBoolean intave = new SettingBoolean("intave", false, "Intave", this, "actiavates Intave Mode");
    public final SettingBoolean moveFix = new SettingBoolean("moveFix", true, "MoveFix", this, "fixes your Movement");
    public final SettingBoolean silentMoveFix = new SettingBoolean("silentMoveFix", true, "SilentMoveFix", this, "walk in direction you look");
    public final SettingBoolean sprintReset = new SettingBoolean("sprintReset", true, "SprintReset", this, "gives target more kb");


   @EventHandler
   public void onEventPreMouseOver(EventPreMouseOver eventPreMouseOver) {
       this.target = searchTargets();
       Vec3 targetVec = RotationUtil.getBestHitVec(target);
       float yawSpeed =  RandomUtil.nextSecureFloat(minYaw.getValue(), maxYaw.getValue());
       float pitchSpeed = RandomUtil.nextSecureFloat(minPitch.getValue(), maxPitch.getValue());
       if (mc.thePlayer.getBestDistanceToEntity(this.target) < 0.1) {
           yawSpeed = RandomUtil.nextSecureFloat(0, 4);
           pitchSpeed = RandomUtil.nextSecureFloat(0, 4);
       }
       double x = targetVec.xCoord - mc.thePlayer.posX;
       double y = targetVec.yCoord - (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight());
       double z = targetVec.zCoord - mc.thePlayer.posZ;

       float calcYaw = (float) ((Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F);
       float calcPitch = (float) (-((MathHelper.atan2(y, MathHelper.sqrt_double(x * x + z * z)) * 180.0D / Math.PI)));

       double diffYaw = MathHelper.wrapAngleTo180_float(calcYaw - rm.getLastRotation()[0]);
       double diffPitch = MathHelper.wrapAngleTo180_float(calcPitch - rm.getLastRotation()[1]);

       float yaw = RotationUtil.updateRotation(rm.getLastRotation()[0], calcYaw, yawSpeed);
       float pitch = RotationUtil.updateRotation(rm.getLastRotation()[1], calcPitch, pitchSpeed);

       if (intave.getBoolean()) {
           if ((!(-yawSpeed <= diffYaw) || !(diffYaw <= yawSpeed)) || (!(-pitchSpeed <= diffPitch) || !(diffPitch <= pitchSpeed))) {
               yaw += RandomUtil.nextSecureFloat(-randomStrength.getValue(), randomStrength.getValue());
               pitch += RandomUtil.nextSecureFloat(-randomStrength.getValue(), randomStrength.getValue());
           }
       } else {
           yaw += RandomUtil.nextSecureFloat(-randomStrength.getValue(), randomStrength.getValue());
           pitch += RandomUtil.nextSecureFloat(-randomStrength.getValue(), randomStrength.getValue());
       }

       float[] rots = RotationUtil.mouseSens(yaw, pitch, rm.getLastRotation()[0], rm.getLastRotation()[1]);
       rm.setRotation(rots, 2, this);
       if (this.sprintReset.getBoolean() && this.target != null && this.target.hurtTime == 9) {
           mc.thePlayer.sprintReset = true;
       }
   }

    @EventHandler
    public void onEventPostMouseOver(EventPostMouseOver eventPostMouseOver) {
        if (this.target == null) {
            return;
        }
        RayCastUtil.getMouseOver(1.0F, range.getValue(), rayCast.getBoolean(), target);
    }

    @EventHandler
    public void onEventClickMouse(EventClickMouse eventClickMouse) {
       if (!shouldKillAura()) {
           return;
       }
        eventClickMouse.setCancel(true);
        if (mc.thePlayer.isUsingItem()) {
            if (!mc.gameSettings.keyBindUseItem.isKeyDown())
                mc.playerController.onStoppedUsingItem(mc.thePlayer);

            while (mc.gameSettings.keyBindAttack.isPressed()) {
            }

            while (mc.gameSettings.keyBindUseItem.isPressed()) {
            }

            while (mc.gameSettings.keyBindPickBlock.isPressed()) {
            }
        } else {

            float CPS = RandomUtil.nextSecureFloat(minCPS.getValue(), maxCPS.getValue());
            if (this.time.hasReached((long) (1000 / CPS))) {
                if (this.target != null) {
                    clickMouse();
                    this.time.reset();
                }
            }

            //clickMouse();

            while (mc.gameSettings.keyBindUseItem.isPressed()) mc.rightClickMouse();

            while (mc.gameSettings.keyBindPickBlock.isPressed()) mc.middleClickMouse();
        }

        if (mc.gameSettings.keyBindUseItem.isKeyDown() && mc.rightClickDelayTimer == 0 && !mc.thePlayer.isUsingItem())
            mc.rightClickMouse();

        mc.sendClickBlockToController(false);
    }

    @EventHandler
    public void onEventMoveFlying(EventMoveFlying eventMoveFlying) {
       if (shouldKillAura()) {
           if (!this.moveFix.getBoolean()) {
               eventMoveFlying.setYaw(Automn.instance().yawPitchHelper().realYaw);
           }
       }
    }

    @EventHandler
    public void onEventJump(EventJump eventJump) {
        if (shouldKillAura()) {
            if (!this.moveFix.getBoolean()) {
                eventJump.setYaw(Automn.instance().yawPitchHelper().realYaw);
            }
        }
    }

    @EventHandler
    public void onEvnetSilentMove(EventSilentMove eventSilentMove) {
        if (shouldKillAura()) {
            if (moveFix.getBoolean() && this.silentMoveFix.getBoolean()) {
                eventSilentMove.setSilent(true);
            }
        }
    }

    private boolean shouldKillAura() {
       return (this.inInv.getBoolean() || mc.currentScreen == null) && this.target != null;
    }


    public EntityLivingBase searchTargets() {
       double d = 10000;
        EntityLivingBase entityLivingBase = null;
        for (final Object o : this.MC.theWorld.loadedEntityList) {
            final Entity e = (Entity) o;
            if (!e.getName().equals(this.MC.thePlayer.getName()) && (e instanceof EntityPlayer || e instanceof EntityMob) && (this.throughWalls.getBoolean() || mc.thePlayer.canEntityBeSeen(e)))
                if (this.MC.thePlayer.getDistanceToEntity(e) < preRange.getValue() && this.MC.thePlayer.getDistanceToEntity(e) < d) {
                    entityLivingBase = (EntityLivingBase) e;
                    d = this.MC.thePlayer.getDistanceToEntity(e);
                }
        }
        return entityLivingBase;
    }

    private void clickMouse() {
        if (mc.leftClickCounter <= 0) {
            mc.thePlayer.swingItem();
            if (mc.objectMouseOver == null) {
                if (mc.playerController.isNotCreative()) {
                    mc.leftClickCounter = 10;
                }
            } else {
                switch (mc.objectMouseOver.typeOfHit) {
                    case ENTITY:
                        mc.playerController.attackEntity(mc.thePlayer, mc.objectMouseOver.entityHit);
                        break;
                    case BLOCK:
                        BlockPos blockpos = mc.objectMouseOver.getBlockPos();

                        if (mc.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {
                            mc.playerController.clickBlock(blockpos, mc.objectMouseOver.sideHit);
                            break;
                        }

                    case MISS:
                    default:
                        if (mc.playerController.isNotCreative()) {
                            mc.leftClickCounter = 10;
                        }
                }
            }
        }
    }
}
