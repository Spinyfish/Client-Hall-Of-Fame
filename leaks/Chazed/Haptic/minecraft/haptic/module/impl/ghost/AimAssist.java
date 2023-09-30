package haptic.module.impl.ghost;

import org.lwjgl.input.Keyboard;

import haptic.Haptic;
import haptic.event.Event;
import haptic.event.impl.EventRender;
import haptic.module.Category;
import haptic.module.Module;
import haptic.module.impl.combat.Antibot;
import haptic.setting.impl.NumberSetting;
import haptic.util.misc.TimerUtil;
import haptic.util.rotations.RotationsUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class AimAssist extends Module {
	
	public NumberSetting range = new NumberSetting("Range", 4.5, 2, 8, 0.2, this);
	public NumberSetting speed = new NumberSetting("Speed", 10, 0.25, 20, 0.25, this);
	
	public Entity target;
	
	public TimerUtil timer = new TimerUtil();
	
	public AimAssist() {
		super("AimAssist", Category.GHOST, Keyboard.KEY_NONE);
		this.addSettings(range, speed);
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventRender) {
			if(target == null || !canAttack(target)) {
				target = getClosest(range.getValue());
			}
			
			if(target == null) {
				timer.reset();
				return;
			}
			
			if(mc.gameSettings.keyBindAttack.isKeyDown()) {
				this.faceEntity(target, Math.min(2.0 * this.speed.getValue(), this.speed.getValue() * 2.0 * Math.max(0.2, this.getDistanceFromMouse(target) / mc.gameSettings.fovSetting)));
				
				mc.thePlayer.rotationYaw += Math.random() * 0.5 - 0.25;
				mc.thePlayer.rotationYaw = Math.round(mc.thePlayer.rotationYaw * 5000) / 5000.0F;
				
				mc.thePlayer.rotationPitch += Math.random() * 0.2 - 0.1;
				mc.thePlayer.rotationPitch = Math.max(mc.thePlayer.rotationPitch, -90);
				mc.thePlayer.rotationPitch = Math.min(mc.thePlayer.rotationPitch, 90);
			}
			
			timer.reset();
		}
	}
	
	public boolean canAttack(Entity target) {
		if(target instanceof EntityLivingBase) {
			Antibot antibot = (Antibot) Haptic.getModuleManager().getModuleByName("Antibot");
			if(antibot.isEnabled() && !antibot.canAttack((EntityLivingBase) target)) {
				return false;
			}
			
			if(!(target instanceof EntityPlayer)) {
				return false;
			} else if(target.isInvisible() || target.isInvisibleToPlayer(mc.thePlayer)) {
				return false;
			} else if(!mc.thePlayer.canEntityBeSeen(target)) {
				return false;
			} else if(!(target instanceof EntityPlayer) && !(target instanceof EntityAnimal) && !(target instanceof EntitySquid) && !(target instanceof EntityVillager) && !(target instanceof EntityMob)) {
				return false;
			//} else if(Vestige.INSTANCE.antibot.isEnabled() && !Vestige.INSTANCE.antibot.canAttack(target)) {
			//	return false;
			} else {
				return target != mc.thePlayer && mc.thePlayer.isEntityAlive() && mc.thePlayer.getDistanceToEntity(target) <= range.getValue() && mc.thePlayer.ticksExisted > 100;
			}
		} else {
			return false;
		}
	}
	
	public EntityLivingBase getClosest(double range) {
        double dist = range;
        EntityLivingBase target1 = null;
        for (Object object : mc.theWorld.loadedEntityList) {
            Entity entity = (Entity) object;
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase player = (EntityLivingBase) entity;
                if (canAttack(player)) {
                    double currentDist = mc.thePlayer.getDistanceToEntity(player);
                    if (currentDist <= dist) {
                        dist = currentDist;
                        target1 = player;
                    }
                }
            }
        }
        return target1;
    }
	
	public static float wrapAngleTo180_float(float value)
    {
        return MathHelper.wrapAngleTo180_float(value);
    }
	
	private int getDistanceFromMouse(Entity entity) {
        float[] neededRotations = this.getRotationsNeeded(entity);
        if (neededRotations != null) {
            float distanceFromMouse = MathHelper.sqrt_float(mc.thePlayer.rotationYaw - neededRotations[0] * mc.thePlayer.rotationYaw - neededRotations[0] + mc.thePlayer.rotationPitch - neededRotations[1] * mc.thePlayer.rotationPitch - neededRotations[1]);
            return (int) distanceFromMouse;
        }
        return -1;
    }
    
    private float[] getRotationsNeeded(Entity entity) {
        if (entity == null) {
            return null;
        }
        double diffX = entity.posX - mc.thePlayer.posX;
        double diffY;
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
            diffY = entityLivingBase.posY + entityLivingBase.getEyeHeight() * 0.9 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        } else {
            diffY = (entity.boundingBox.minY + entity.boundingBox.maxY) / 2.0 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        }
        double diffZ = entity.posZ - mc.thePlayer.posZ;
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float) (-(Math.atan2(diffY, dist) * 180.0 / 3.141592653589793));
        return new float[] { mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw), mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch) };
    }
    
    private void faceEntity(Entity entity, double speed) {
    	//speed += Math.random() * 4 - 3;
    	//speed = Math.round(speed * 5000) / 5000.0D;
    	
        float[] rotations = this.getRotationsNeeded(entity);
        if (rotations != null) {
            mc.thePlayer.rotationYaw = (float) this.limitAngleChange(mc.thePlayer.prevRotationYaw, rotations[0], speed);
        }
    }
    
    private double limitAngleChange(double current, double intended, double speed) {
        double change = intended - current;
        if (change > speed) {
            change = speed;
        } else if (change < -speed) {
            change = -speed;
        }
        return current + change;
    }

}
