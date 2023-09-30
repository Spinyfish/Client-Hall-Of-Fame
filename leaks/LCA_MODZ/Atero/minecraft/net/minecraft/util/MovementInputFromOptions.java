package net.minecraft.util;

import com.darkmagician6.eventapi.events.callables.EventPreMotionUpdate;
import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.module.modules.combat.Killaura;
import de.verschwiegener.atero.util.TimeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;

import java.util.Random;

public class MovementInputFromOptions extends MovementInput {
    private final GameSettings gameSettings;

    Minecraft mc = Minecraft.getMinecraft();
    private float lastForward, lastStrafe;
    private TimeUtils timer = new TimeUtils();

    public MovementInputFromOptions(GameSettings gameSettingsIn) {
        this.gameSettings = gameSettingsIn;
    }

    public void updatePlayerMoveState() {
        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;

        if (this.gameSettings.keyBindForward.isKeyDown()) {
            ++this.moveForward;
        }

        if (this.gameSettings.keyBindBack.isKeyDown()) {
            --this.moveForward;
        }

        if (this.gameSettings.keyBindLeft.isKeyDown()) {
            ++this.moveStrafe;
        }

        if (this.gameSettings.keyBindRight.isKeyDown()) {
            --this.moveStrafe;
        }
        //System.out.println("Input: ");

        //EventTest updatePlayerMovementState = new EventTest(moveForward, moveStrafe, mc.thePlayer.rotationYawHead, mc.thePlayer.rotationYaw, false, false);
        //EventManager.call(updatePlayerMovementState);

        // final UpdatePlayerMovementState updatePlayerMovementState = new
        // UpdatePlayerMovementState(moveForward, moveStrafe, getYaw(),
        // getPlayer().rotationYaw, false, false).onFire();
        //moveForward = updatePlayerMovementState.getMoveForward();
        //moveStrafe = updatePlayerMovementState.getMoveStrafe();

        //moveForward = updatePlayerMovementState.getMoveForward();
        //moveStrafe = updatePlayerMovementState.getMoveStrafe();

        //System.out.println("MoveStrafe: " + moveStrafe);
        //System.out.println("MoveForward: " + moveForward);
        //System.out.println("Silent: " + updatePlayerMovementState.getYaw());

        //System.out.println("MoveSTrafe1: " + moveStrafe + " Forward1: " + moveForward);

        //if(useCorrectMovement() && (moveForward != 0 || moveStrafe != 0)) {
        //timer.reset();
        //mc.thePlayer.setSprinting(false);
        // boolean last = testlast(moveForward, moveStrafe);
        // System.out.println("Last: " + last);
        //if(!last) {
        //testfix(moveForward, moveStrafe);
        //}else {
        //moveForward = lastForward;
        //moveStrafe = lastStrafe;
        //}
        //}

        //if(useCorrectMovement() && !(moveForward == 0 && moveStrafe == 0)) {
        //System.out.println("MoveForward: " + moveForward + " Strafe: " + moveStrafe);
        //testfix(moveForward, moveStrafe);
        //}
        //System.out.println("MoveSTrafe2: " + moveStrafe + " Forward2: " + moveForward);

        //if (updatePlayerMovementState.isSilentMoveFix() && (moveForward != 0 || moveStrafe != 0)) {
        //    correctMovement2(moveForward, moveStrafe, EventPreMotionUpdate.getInstance.getYaw());
        //getCorrectedMovement(moveForward, moveStrafe, EventPreMotionUpdate.getInstance.getYaw(), 0, false);
        //}

        // if(Killaura.setting.getItemByName("SilentMoveFix").isState()) {
        if (Management.instance.modulemgr.getModuleByName("Killaura").isEnabled() && Management.instance.settingsmgr.getSettingByName("Killaura").getItemByName("SilentMoveFix").isState()) {
            testFix(this.moveForward, this.moveStrafe);
        }
        //  }

        this.jump = this.gameSettings.keyBindJump.isKeyDown();
        this.sneak = this.gameSettings.keyBindSneak.isKeyDown();

        if (this.sneak) {
            final float RandomSneak = (float) MathHelper.getRandomDoubleInRange(new Random(), 0.8, 1);
            final float RandomSneakMinesucht = (float) MathHelper.getRandomDoubleInRange(new Random(), 0.2, 0.3);
            if (Management.instance.modulemgr.getModuleByName("AutoEagle").isEnabled()) {
                if ( Management.instance.modulemgr.getModuleByName("AutoEagle").isEnabled()&& Management.instance.settingsmgr.getSettingByName("AutoEagle").getItemByName("Minesucht").isState() ) {
                    this.moveStrafe = (float) ((double) this.moveStrafe * RandomSneakMinesucht);
                    this.moveForward = (float) ((double) this.moveForward * RandomSneakMinesucht);
                }
            }else {
                if ( Management.instance.modulemgr.getModuleByName("AutoEagle").isEnabled()&& !Management.instance.settingsmgr.getSettingByName("AutoEagle").getItemByName("Minesucht").isState() ) {
                    this.moveStrafe = (float) ((double) this.moveStrafe * RandomSneak);
                    this.moveForward = (float) ((double) this.moveForward * RandomSneak);

                }else {
                    if (!Management.instance.modulemgr.getModuleByName("Scaffold").isEnabled()) {
                        this.moveStrafe = (float) ((double) this.moveStrafe * 0.3);
                        this.moveForward = (float) ((double) this.moveForward * 0.3);
                    }
                }
            }
            //System.out.println("MoveStrafe: " + moveStrafe);
            //System.out.println("MoveForward: " + moveForward);
        }
    }
    private boolean useCorrectMovement() {
        return (Management.instance.modulemgr.getModuleByName("Killaura").isEnabled() && Killaura.instance.hasTarget());
    }

    private boolean testlast(float forward, float strafe) {

        if (Float.isNaN(lastForward) || Float.isNaN(lastStrafe)) {
            return false;
        }


        float slipperiness = 0.91F;
        if (mc.thePlayer.onGround) {
            slipperiness = mc.theWorld.getBlockState(new BlockPos(MathHelper.floor_double(mc.thePlayer.posX),
                    MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(mc.thePlayer.posZ)))
                    .getBlock().slipperiness * 0.91F;
        }

        float moveSpeedOffset = 0.16277136F / (slipperiness * slipperiness * slipperiness);
        float friction;

        if (mc.thePlayer.onGround) {
            friction = mc.thePlayer.getAIMoveSpeed() * moveSpeedOffset;
        } else {
            friction = mc.thePlayer.jumpMovementFactor;
        }

        float f = strafe * strafe + forward * forward;

        f = friction / f;


        float clientStrafe = strafe * f;
        float clientForward = forward * f;
        float clientRotationSin = MathHelper.sin(mc.thePlayer.rotationYaw * (float) Math.PI / 180.0F) - 90;
        float clientRotationCos = MathHelper.cos(mc.thePlayer.rotationYaw * (float) Math.PI / 180.0F);
        float clientMotionX = (clientStrafe * clientRotationCos - clientForward * clientRotationSin);
        float clientMotionZ = (clientForward * clientRotationCos + clientStrafe * clientRotationSin);

        //System.out.println("MotionXDif: " + (clientMotionX - mc.thePlayer.motionX));
        //System.out.println("MotionZDif: " + (clientMotionZ - mc.thePlayer.motionZ));

        float serverRotationSin = MathHelper.sin(EventPreMotionUpdate.getInstance.getYaw() * (float) Math.PI / 180.0F) - 90;
        float serverRotationCos = MathHelper.cos(EventPreMotionUpdate.getInstance.getYaw() * (float) Math.PI / 180.0F);

        //System.out.println("F: " + f);

        float calcStrafe = lastStrafe * f;
        float calcForward = lastForward * f;

        //System.err.println("CalsStrafe: " + calcStrafe + " F: " + calcForward);

        float calcMotionX = (calcStrafe * serverRotationCos - calcForward * serverRotationSin);
        float calcMotionZ = (calcForward * serverRotationCos + calcStrafe * serverRotationSin);

        //System.err.println("CalcMotion: " + calcMotionX + " Z: " + calcMotionZ);

        float diffMotionX = calcMotionX - clientMotionX;
        float diffMotionZ = calcMotionZ - clientMotionZ;

        //System.out.println("DifMotion: " + diffMotionX + " Z: " + diffMotionZ);

        float distance = MathHelper.sqrt_float(diffMotionZ * diffMotionZ + diffMotionX * diffMotionX);

        System.out.println("Distance: " + distance);

        if (distance < 0.10) {
            moveForward = lastForward;
            moveStrafe = lastStrafe;
            return true;
        }
        return false;

    }

    public boolean isMoving() {
        return mc.thePlayer != null && (mc.thePlayer.movementInput.moveForward != 0F || mc.thePlayer.movementInput.moveStrafe != 0F);
    }


    private void testFix(float forward, float strafe) {
        if (!Killaura.instance.hasTarget() || !isMoving() ||  !Management.instance.settingsmgr.getSettingByName("Killaura").getItemByName("HardMoveFix").isState() || Management.instance.modulemgr.getModuleByName("AutoEagle").isEnabled()) return;
        float slipperiness = 0.91F;
        if (mc.thePlayer.onGround) {
            slipperiness = mc.theWorld.getBlockState(new BlockPos(MathHelper.floor_double(mc.thePlayer.posX),
                    MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(mc.thePlayer.posZ)))
                    .getBlock().slipperiness * 0.91F;
        }

        float moveSpeedOffset = 0.16277136F / (slipperiness * slipperiness * slipperiness);
        float friction;

        if (mc.thePlayer.onGround) {
            friction = mc.thePlayer.getAIMoveSpeed() * moveSpeedOffset;
        } else {
            friction = mc.thePlayer.jumpMovementFactor;
        }

        float f = strafe * strafe + forward * forward;
        f = MathHelper.sqrt_float(f);

        if (f < 1.0F) f = 1.0F;

        f = friction / f;


        float clientStrafe = strafe * f;
        float clientForward = forward * f;
        float clientRotationSin = MathHelper.sin(mc.thePlayer.rotationYaw * (float) Math.PI / 180.0F);
        float clientRotationCos = MathHelper.cos(mc.thePlayer.rotationYaw * (float) Math.PI / 180.0F);
        float clientMotionX = (clientStrafe * clientRotationCos - clientForward * clientRotationSin);
        float clientMotionZ = (clientForward * clientRotationCos + clientStrafe * clientRotationSin);

        System.out.println("MotionXDif: " + (clientMotionX - mc.thePlayer.motionX));
        System.out.println("MotionZDif: " + (clientMotionZ - mc.thePlayer.motionZ));

        float serverRotationSin = MathHelper.sin(Killaura.yaw * (float) Math.PI / 180.0F);
        float serverRotationCos = MathHelper.cos(Killaura.yaw * (float) Math.PI / 180.0F);

        float smalestDistance = Float.NaN;
        float posibleForward = 0;
        float posibleStrafe = 0;

        for (int strafevalue = -1; strafevalue <= 1; strafevalue++) {
            for (int forwardvalue = -1; forwardvalue <= 1; forwardvalue++) {
                if (!(forwardvalue == 0 && strafevalue == 0)) {
                    float f2 = strafevalue * strafevalue + forwardvalue * forwardvalue;
                    f2 = MathHelper.sqrt_float(f2);
                    float calcStrafe = strafevalue * f;
                    float calcForward = forwardvalue * f;
                    float calcMotionX = (calcStrafe * serverRotationCos - calcForward * serverRotationSin);
                    float calcMotionZ = (calcForward * serverRotationCos + calcStrafe * serverRotationSin);

                    float diffMotionX = calcMotionX - clientMotionX;
                    float diffMotionZ = calcMotionZ - clientMotionZ;
                    float distance = normalize(MathHelper.sqrt_float(diffMotionX * diffMotionX + diffMotionZ * diffMotionZ));
                    System.out.println("Distance: " + distance + " Smalest: " + smalestDistance + " Forward: "
                            + forwardvalue + " Strafe: " + strafevalue);
                    if (Float.isNaN(smalestDistance) || distance < smalestDistance) {
                        posibleForward = forwardvalue;
                        posibleStrafe = strafevalue;
                        smalestDistance = (float) distance;
                    }
                }
            }
        }
        System.out.println("Forward: " + posibleForward + " Strafe: " + posibleStrafe);
        moveForward = posibleForward;
        moveStrafe = posibleStrafe;
        lastForward = posibleForward;
        lastStrafe = posibleStrafe;
    }

    public float normalize(float value) {
        if (value < 0) {
            return value / -1;
        }
        return value;
    }

}
