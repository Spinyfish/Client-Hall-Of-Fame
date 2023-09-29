package net.minecraft.util;

import java.util.Random;

import de.fanta.Client;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.module.impl.combat.TestAura;
import de.fanta.module.impl.combat.TriggerBot;
import de.fanta.setting.settings.CheckBox;
import de.fanta.utils.FriendSystem;
import de.fanta.utils.Rotations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
public class MovementInputFromOptions extends MovementInput
{
    private final GameSettings gameSettings;
    private float lastForward, lastStrafe;
    Minecraft mc = Minecraft.getMinecraft();
    public MovementInputFromOptions(GameSettings gameSettingsIn)
    {
        this.gameSettings = gameSettingsIn;
    }

    public void updatePlayerMoveState()
    {
        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;

        if (this.gameSettings.keyBindForward.isKeyDown())
        {
            ++this.moveForward;
        }

        if (this.gameSettings.keyBindBack.isKeyDown())
        {
            --this.moveForward;
        }

        if (this.gameSettings.keyBindLeft.isKeyDown())
        {
            ++this.moveStrafe;
        }

        if (this.gameSettings.keyBindRight.isKeyDown())
        {
            --this.moveStrafe;
        }

        this.jump = this.gameSettings.keyBindJump.isKeyDown();
        this.sneak = this.gameSettings.keyBindSneak.isKeyDown();

       if( TestAura.target != null && Client.INSTANCE.moduleManager.getModule("TestAura").state && Minecraft.getMinecraft().thePlayer.isMoving() &&  ((CheckBox) Client.INSTANCE.moduleManager.getModule("TestAura").getSetting("SilentMoveFix")
				.getSetting()).state && ((CheckBox) Client.INSTANCE.moduleManager.getModule("TestAura").getSetting("MoveFix")
						.getSetting()).state ) {
    	   testFix(this.moveForward, this.moveStrafe);
        }
        
//        if( TriggerBot.Target != null && Client.INSTANCE.moduleManager.getModule("TriggerBot").state && Minecraft.getMinecraft().thePlayer.isMoving() && !FriendSystem.isFriendString(TriggerBot.Target.getName())) {
//            testFix2(this.moveForward, this.moveStrafe);
//             }
        
        if (this.sneak)
        {
            if (!((CheckBox) Client.INSTANCE.moduleManager.getModule("Scaffold").getSetting("Legit").getSetting()).state  && !Minecraft.getMinecraft().gameSettings.keyBindJump.pressed) {
            final float RandomSneak = (float) MathHelper.getRandomDoubleInRange(new Random(), 0.8, 0.88);
            //final float RandomSneak = (float) MathHelper.getRandomDoubleInRange(new Random(), 0.99, 1);
            this.moveStrafe = (float)((double)this.moveStrafe * 0.3);
           this.moveForward = (float)((double)this.moveForward * 0.3);
           // mc.rightClickDelayTimer = (int) 4F;
            }else {
            	  this.moveStrafe = (float)((double)this.moveStrafe * 0.3);
                  this.moveForward = (float)((double)this.moveForward * 0.3);
            }
        }
       if(Minecraft.getMinecraft().gameSettings.keyBindSneak.pressed && !Client.INSTANCE.moduleManager.getModule("Scaffold").isState()) {
    		  this.moveStrafe = (float)((double)this.moveStrafe * 0.3);
              this.moveForward = (float)((double)this.moveForward * 0.3);
       }
    }
    
    private void testFix(float forward, float strafe) {
        if (!Minecraft.getMinecraft().thePlayer.isMoving() && Killaura.kTarget == null) return;
        float slipperiness = 0.91F;
        if (mc.thePlayer.onGround) {
            slipperiness = mc.theWorld.getBlockState(new BlockPos(MathHelper.floor_double(mc.thePlayer.posX), MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(mc.thePlayer.posZ))).getBlock().slipperiness * 0.91F;
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

      

        float serverRotationSin = MathHelper.sin(Rotations.yaw * (float) Math.PI / 180.0F);
        float serverRotationCos = MathHelper.cos(Rotations.yaw * (float) Math.PI / 180.0F);

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
        moveForward = posibleForward;
        moveStrafe = posibleStrafe;
        lastForward = posibleForward;
        lastStrafe = posibleStrafe;
    }
    private void testFix2(float forward, float strafe) {
        if (!Minecraft.getMinecraft().thePlayer.isMoving() && TriggerBot.Target == null) return;
        float slipperiness = 0.91F;
        if (mc.thePlayer.onGround) {
            slipperiness = mc.theWorld.getBlockState(new BlockPos(MathHelper.floor_double(mc.thePlayer.posX), MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(mc.thePlayer.posZ))).getBlock().slipperiness * 0.91F;
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

        float serverRotationSin = MathHelper.sin(Rotations.yaw * (float) Math.PI / 180.0F);
        float serverRotationCos = MathHelper.cos(Rotations.yaw * (float) Math.PI / 180.0F);

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
