package club.marsh.bloom.impl.utils.combat;

import club.marsh.bloom.Bloom;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class RotationUtils
{
    public static Minecraft mc = Minecraft.getMinecraft();
    
    public static float[] getFixedGCD(float[] rotations)
    {
    	return getFixedGCD(rotations[0], rotations[1]);
    }
    
    public static float[] getFixedGCD(float yaw, float pitch)
    {
        float lastYaw = Bloom.INSTANCE.rotation.getYaw();
        float lastPitch = Bloom.INSTANCE.rotation.getPitch();
        float diffYaw = Math.abs(yaw - lastYaw);
        float diffPitch = Math.abs(pitch - lastPitch);
        float fixedYaw = yaw;
        float fixedPitch = pitch;
        
        if (diffYaw < minMouseFac() / 2)
        {
            fixedYaw = lastYaw;
        }
        
        else
        {
            fixedYaw = getGCD(yaw, lastYaw);
        }
        
        if (diffPitch < minMouseFac() / 2)
        {
            fixedPitch = lastPitch;
        }
        
        else
        {
            fixedPitch = getGCD(pitch, lastPitch);
        }
        
        return new float[] {fixedYaw, fixedPitch};
    }
    
    private static float getGCD(float rotation, float lastRotation)
    {
        float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float f1 = f * f * f * 8.0F;
        return (Math.round((rotation - lastRotation) / 0.15F / f1) * 0.15F * f1) + lastRotation;
    }
    
    private static float minMouseFac()
    {
        float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float f1 = f * f * f * 8.0F;
        return 0.15F * f1;
    }

	public static float[] getRotations(EntityLivingBase target)
	{
		float rotations[] = new float[] {0, 0};
		double x = target.posX - mc.thePlayer.posX;
        double y = target.posY - (mc.thePlayer.posY + (mc.thePlayer.getEyeHeight() - target.getEyeHeight()));
        double z = target.posZ - mc.thePlayer.posZ;
        double distance = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float)(MathHelper.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float)(-(MathHelper.atan2(y, distance) * 180.0D / Math.PI));
		rotations[0] = yaw;
		rotations[1] = pitch;
		rotations = RotationUtils.getFixedGCD(rotations);
		return rotations;
	}
}