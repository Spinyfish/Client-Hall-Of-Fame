package club.marsh.bloom.impl.mods.combat;


import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.BooleanValue;
import club.marsh.bloom.api.value.NumberValue;
import club.marsh.bloom.impl.events.ClickMouseEvent;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.impl.utils.combat.KillAuraUtils;
import club.marsh.bloom.impl.utils.combat.RotationUtils;
import net.minecraft.entity.EntityLivingBase;

public class AimAssist extends Module {
	public AimAssist() {
		super("Aim Assist",Keyboard.KEY_NONE,Category.COMBAT);
	}
	
	NumberValue range = new NumberValue("Range",3D,0,10D);
	BooleanValue randomization = new BooleanValue("Rotation Randomization",true,() -> true);
	NumberValue<Double> randomizationAmount = new NumberValue<>("Randomize Amount",5D,0D,15D,1,() -> randomization.isOn());
    BooleanValue onClick = new BooleanValue("Only On Click", true);
    private Random random = new Random();
    int lastTick = 0;

    @Subscribe
    public void onClick(ClickMouseEvent e) {
        if (e.clickType == 0)
        {
            lastTick = mc.thePlayer.ticksExisted;
        }
    }

    @Subscribe
    public void onUpdate(UpdateEvent e) {
    	EntityLivingBase target = KillAuraUtils.getClosest(range.getValDouble());
    	
        if (onClick.isOn() && (mc.thePlayer.ticksExisted - lastTick) > 4)
        {
            return;
        }
        
        if (target != null)
        {
        	float[] rotations = RotationUtils.getRotations(target);
        	
        	if (this.randomization.isOn())
			{
				double random1 = random.nextDouble();
				double random2 = random.nextDouble();
				
				if (random1 < 0.5F)
				{
					rotations[0] += random1 * this.randomizationAmount.getValDouble();
				}
				
				else
				{
					rotations[0] -= (random1 - 0.5F) * this.randomizationAmount.getValDouble();
				}
				
				if (random1 < 0.5F)
				{
					rotations[1] += random2 * this.randomizationAmount.getValDouble();
				}
				
				else
				{
					rotations[1] -= (random2 - 0.5F) * this.randomizationAmount.getValDouble();
				}
			}
        	
			float yaw = rotations[0];
			float pitch = rotations[1];
			this.mc.thePlayer.rotationYaw = yaw;
			this.mc.thePlayer.rotationPitch = pitch;
        }
    }
}
