package intentions.modules.world;

import intentions.Client;
import intentions.modules.Module;
import intentions.util.BlockUtils;
import intentions.util.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class CheatDetector extends Module {

	public CheatDetector() {
		super("CheatDetector", 0, Category.WORLD, "Finds cheaters!", true);
	}

	public static Minecraft mc = Minecraft.getMinecraft();

	public void onEnable() {
	}

	public void onDisable() {
	}

	public void onTick() {
		if (this.toggled && mc.thePlayer != null && mc.theWorld != null) {
			
			for(Object o : mc.theWorld.playerEntities) {
				if(o instanceof EntityOtherPlayerMP) {
					EntityOtherPlayerMP p = (EntityOtherPlayerMP)o;
					
					if(p.hurtTime != p.maxHurtTime - 1 && p.maxHurtTime - 1 > 0) {
					
						
						speedA(p);
					
					
					}
					
					//noSlowdownA(p);
					
					
				}
			}
			
		}
	}
	
	
	
	public void speedA(EntityOtherPlayerMP p) {
		double distX = Math.abs(p.lastTickPosX - p.posX);
		double distZ = Math.abs(p.lastTickPosZ - p.posZ);
		
		double maxXZMove = 0.95D;
		
		for (Block b : BlockUtils.getBlocksBelowEntity(p)) {
			if (b != null && BlockUtils.isIce(b)) {
				maxXZMove += 0.3f;
			}
		}
		
        PotionEffect effect = p.getActivePotionEffect( Potion.moveSpeed);
        if ( effect != null )
        {
            maxXZMove += effect.getAmplifier() / (Math.PI * Math.PI);
        }
		
		if(distZ < distX / 1.1 && Math.abs(distZ - distX) > 0.2f) {
			maxXZMove -= (distX / 2f);
			maxXZMove += 0.15f;
		}else if(distX < distZ / 1.1 && Math.abs(distX - distZ) > 0.2f) {
			maxXZMove -= (distZ / 2f);
			maxXZMove += 0.15f;
		}
		
		if(maxXZMove < 0)return;
		
		double distance = Math.floor((distX + distZ) * 100);
		double maxDistance = Math.floor(maxXZMove * 100);
		
		if(distance > maxDistance && PlayerUtil.isValid(p)) {
			Client.addChatMessage("SPEED (A) - " + p.getName());
		}
	}
	
	public void noSlowdownA(EntityOtherPlayerMP p) {
		double distX = Math.abs(p.lastTickPosX - p.posX);
		double distZ = Math.abs(p.lastTickPosZ - p.posZ);
		
		double dis = distX = distZ;
		
		float tooFast = 0.2153f;
		
		for (Block b : BlockUtils.getBlocksBelowEntity(p)) {
			if (BlockUtils.isIce(b)) {
				tooFast += 0.11f;
			}
		}
		
        PotionEffect effect = p.getActivePotionEffect( Potion.moveSlowdown );
        if ( effect != null )
        {
            tooFast += effect.getAmplifier() / (Math.PI * Math.PI);
        }
		if(dis > tooFast && PlayerUtil.isUsingItem(p) && PlayerUtil.isValid(p) && p.motionY == 0.0D) {
			Client.addChatMessage("NoSlowdown (A) - " + p.getName());
		}
	}
	
}
