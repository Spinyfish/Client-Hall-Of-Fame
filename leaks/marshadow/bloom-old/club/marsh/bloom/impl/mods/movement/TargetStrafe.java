package club.marsh.bloom.impl.mods.movement;

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.BooleanValue;
import club.marsh.bloom.api.value.NumberValue;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.impl.utils.combat.KillAuraUtils;
import club.marsh.bloom.impl.utils.combat.RotationUtils;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import club.marsh.bloom.impl.utils.render.Vector2;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

public class TargetStrafe extends Module {
    private static double direction = -1;
    static NumberValue radius = new NumberValue("Range", 3D, 1D, 6D);
    static BooleanValue playerOnly = new BooleanValue("Player Only", true, () -> true);

    public TargetStrafe() {
        super("Target Strafe",Keyboard.KEY_NONE,Category.MOVEMENT);
    }

    static boolean toggled = false;

    @Override
    public void onEnable() {
        toggled = true;
    }

    @Override
    public void onDisable() {
        toggled = false;
    }
    
    @Subscribe
    public void onUpdate(UpdateEvent e)
    {
    	this.doTargetStrafe(MovementUtils.getSpeed());
    }

    public static boolean doTargetStrafe(double speed) {
        try
        {
        	if(mc.thePlayer.moveStrafing != 0)
            {
                direction = mc.thePlayer.moveStrafing;
            }
            if (mc.thePlayer.isCollidedHorizontally || mc.theWorld.getBlockState(mc.thePlayer.getPosition().add(new BlockPos(mc.thePlayer.motionX*5,0,mc.thePlayer.motionZ*5))).getBlock().isFullBlock())
                direction = -direction;
            
            EntityLivingBase target = KillAuraUtils.getClosest(radius.getValDouble());
            
            if (target == null)
            {
            	return false;
            }
            
            if (playerOnly.isOn() && !(target instanceof EntityPlayer))
            {
            	return false;
            }
            
            if (mc.thePlayer.movementInput.moveStrafe != 0 || mc.thePlayer.movementInput.moveForward != 0)
            {
                float[] rotations = RotationUtils.getRotations(target);
                Vector2 motion = MovementUtils.getMotion(speed, rotations[0], mc.thePlayer.moveStrafing != 0 ? -mc.thePlayer.moveStrafing : -direction, 0);
                
                if (mc.thePlayer.getDistanceToEntity(target) > radius.value.floatValue())
                {
                   motion = MovementUtils.getMotion(speed, rotations[0], mc.thePlayer.moveStrafing != 0 ? -mc.thePlayer.moveStrafing : -direction, 1);
                }
                
                mc.thePlayer.motionX = motion.x;
                mc.thePlayer.motionZ = motion.y;
                return true;
            }
            
            return false;
        }
        
        catch (Exception e)
        {
        	return false;
        }
    }
}
