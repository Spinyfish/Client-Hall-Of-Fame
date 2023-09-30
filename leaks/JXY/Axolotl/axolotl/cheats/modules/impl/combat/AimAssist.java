package axolotl.cheats.modules.impl.combat;

import java.util.List;

import axolotl.cheats.events.EventType;
import org.lwjgl.input.Keyboard;

import axolotl.cheats.events.Event;
import axolotl.cheats.events.MoveEvent;
import axolotl.cheats.modules.Module;
import axolotl.cheats.settings.NumberSetting;
import axolotl.util.EntityUtil;
import axolotl.util.RotationUtils;
import axolotl.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class AimAssist extends Module {

	public NumberSetting range = new NumberSetting("Range", 3.5f, 1f, 6f, 0.1f);
	
	public AimAssist() {
		super("AimAssist", Category.COMBAT, true);
		this.addSettings(range);
		this.setSpecialSetting(range);
	}
	
	public EntityLivingBase target;
	public Timer timer = new Timer();
	
	public void onEvent(Event event) {
    	if(mc.thePlayer == null || !(event instanceof MoveEvent) || event.eventType != EventType.PRE) return;
      
      if(target != null && target.getDistanceToEntity(mc.thePlayer) > range.getNumberValue()) {
    	  target = null;
      }
      
      List<EntityLivingBase> targets = EntityUtil.getEntitiesAroundPlayer(range.getNumberValue());
      //targets = (List<EntityLivingBase>)targets.stream().filter(EntityPlayer.class::isInstance).collect(Collectors.toList());
      if (!targets.isEmpty()) {
        EntityLivingBase target = targets.get(0);
        
        if(target.isInvisibleToPlayer(mc.thePlayer))return;
        
        if((this.target != null && this.target.getDistanceToEntity(mc.thePlayer) >= range.getNumberValue()))
        	this.target = target;
        if (this.timer.hasTimeElapsed((long)(1000.0D / 10), true)) {
        
        	
          float rotations[] = getRotations(target);
          float yaw = rotations[0];
          
          if(yaw > mc.thePlayer.rotationYaw) mc.thePlayer.rotationYaw += mc.gameSettings.mouseSensitivity * 0.98532133;
          else if (yaw < mc.thePlayer.rotationYaw) mc.thePlayer.rotationYaw -= mc.gameSettings.mouseSensitivity * 0.98532133;
          
          
        } 
      } 
	}
	
	  public float[] getRotations(Entity e) {
		    return RotationUtils.getRotationsToEntity(e);
	  }
	
}
