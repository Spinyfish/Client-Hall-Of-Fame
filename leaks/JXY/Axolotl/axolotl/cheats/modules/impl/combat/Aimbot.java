package axolotl.cheats.modules.impl.combat;

import java.util.List;

import axolotl.cheats.events.EventType;

import axolotl.cheats.events.Event;
import axolotl.cheats.events.EventUpdate;
import axolotl.cheats.modules.Module;
import axolotl.cheats.settings.NumberSetting;
import axolotl.util.EntityUtil;
import axolotl.util.RotationUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class Aimbot extends Module {

	public NumberSetting range = new NumberSetting("Range", 3.5f, 0.5f, 30f, 0.5f);
	
	public Aimbot() {
		super("Aimbot", Category.COMBAT, true);
		this.addSettings(range);
		this.setSpecialSetting(range);
	}
	
	public void onEvent(Event e) {
		
		if(e instanceof EventUpdate && e.eventType == EventType.PRE) {
			
		      List<EntityLivingBase> targets = EntityUtil.getEntitiesAroundPlayer(range.getNumberValue());
		      if (!targets.isEmpty()) {
		    	  
		    	  EntityLivingBase target = targets.get(0);
		    	  
		    	  float[] rotations = getRotations(target);
		    	  
		    	  mc.thePlayer.rotationYaw = rotations[0];
		    	  mc.thePlayer.rotationPitch = rotations[1];
		    	  
		      }
			
		}
		
	}
	
	  public float[] getRotations(Entity e) {
		    return RotationUtils.getRotationsToEntity(e);
	  }
	
}
