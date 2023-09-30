package intentions.modules.combat;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import intentions.Client;
import intentions.modules.Module;
import intentions.modules.player.AutoSoup;
import intentions.settings.ModeSetting;
import intentions.settings.NumberSetting;
import intentions.util.PlayerUtil;
import intentions.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class AimAssist extends Module {

	public ModeSetting move = new ModeSetting("Turn", "Legit", new String[] {"Instant", "Legit"}),
			
			priority = new ModeSetting("Priority", "Passive", new String[] { "Passive", "None", "Mobs", "Players" });

	public NumberSetting turnSpeed = new NumberSetting("TurnSpeed", 5, 1, 20, 1),
			time = new NumberSetting("Time", 5, 0, 20, 1);  
	  

	
	
	public AimAssist() {
		super("AimAssist", 0, Category.COMBAT, "Automatically aims you towards enemys", true);
		this.addSettings(move, priority, turnSpeed, time);
	}
	
	private final Timer timer = new Timer();

	public void onUpdate() {
		
	  		  
	      
      if(mc.thePlayer == null || !this.toggled) return;
      
      if(time.getValue() != 0 && !timer.hasTimeElapsed((long) (50 * time.getValue()), true))return;
      
      if(mc.thePlayer.getCurrentEquippedItem() != null) {
	      Item held = mc.thePlayer.getCurrentEquippedItem().getItem();
	      if(held == Items.bowl || held == Items.mushroom_stew && AutoSoup.enabled) return;
      }
      
      List<EntityLivingBase> targets = (List<EntityLivingBase>)this.mc.theWorld.loadedEntityList.stream().filter(EntityLivingBase.class::isInstance).collect(Collectors.toList());
      targets = (List<EntityLivingBase>)targets.stream().filter(entity -> (entity.getDistanceToEntity((Entity)this.mc.thePlayer) < 3.5 && entity != this.mc.thePlayer && !entity.isDead && entity.getHealth() > 0.0F)).collect(Collectors.toList());
      if (this.priority.getMode() == "Passive") {
        targets = (List<EntityLivingBase>)targets.stream().filter(EntityAnimal.class::isInstance).collect(Collectors.toList());
      } else if (this.priority.getMode() == "Monsters") {
        targets = (List<EntityLivingBase>)targets.stream().filter(EntityMob.class::isInstance).collect(Collectors.toList());
      } else if (this.priority.getMode() == "Players") {
        targets = (List<EntityLivingBase>)targets.stream().filter(EntityPlayer.class::isInstance).collect(Collectors.toList());
      } 
      targets.sort(Comparator.comparingDouble(entity -> entity.getDistanceToEntity((Entity)this.mc.thePlayer)));
      
      targets = PlayerUtil.removeNotNeeded(targets);
      if (!targets.isEmpty()) {
        EntityLivingBase target = targets.get(0);
        
        
        if(target == null || target.isDead)return;
        
        float[] rotations = getRotations(target);
        float[] cRotations = new float[] {mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
        
        if(move.getMode().equalsIgnoreCase("Instant")) {
        	mc.thePlayer.rotationYaw = rotations[0];
        	mc.thePlayer.rotationPitch = rotations[1];
        	return;
        }
        
        rotations[0] -= 180;
        rotations[1] -= 180;
        cRotations[0] -= 180;
        cRotations[1] -= 180;
        
        if(rotations[0] > cRotations[0] || rotations[0] + 360 > cRotations[0]) 
        	if(cRotations[0] + turnSpeed.getValue() > rotations[0]) 
        		mc.thePlayer.rotationYaw = rotations[0]+180;
        	else 
        		mc.thePlayer.rotationYaw += turnSpeed.getValue();
        	
        else if(rotations[0] + 360 > cRotations[0]) 
        	if(cRotations[0] + turnSpeed.getValue() > rotations[0] + 360) 
        		mc.thePlayer.rotationYaw = rotations[0]+180;
        	else 
        		mc.thePlayer.rotationYaw += turnSpeed.getValue();
        
         else if(rotations[0] < cRotations[0]) 
        	 if(cRotations[0] - turnSpeed.getValue() > rotations[0]) 
         		mc.thePlayer.rotationYaw = rotations[0]+180;
         	 else 
         		mc.thePlayer.rotationYaw -= turnSpeed.getValue();
        
        if(rotations[1] > cRotations[1]) 
        	if(cRotations[1] + turnSpeed.getValue() > rotations[1]) 
        		mc.thePlayer.rotationPitch = rotations[1]+180;
        	else 
        		mc.thePlayer.rotationPitch += turnSpeed.getValue();
        
        else if(rotations[1] + 360 > cRotations[1]) 
        	if(cRotations[1] + turnSpeed.getValue() > rotations[1] + 360) 
        		mc.thePlayer.rotationPitch = rotations[1]+180;
        	else 
        		mc.thePlayer.rotationPitch += turnSpeed.getValue();
        	
         else if(rotations[1] < cRotations[1]) 
        	 if(cRotations[1] - turnSpeed.getValue() > rotations[1]) 
         		mc.thePlayer.rotationPitch = rotations[1]+180;
         	 else 
         		mc.thePlayer.rotationPitch -= turnSpeed.getValue();
        
        
      } 
	}
	
	  public float[] getRotations(Entity e) {
		    double deltaX = e.posX + e.posX - e.lastTickPosX - this.mc.thePlayer.lastTickPosX;
		    double deltaY = e.posY - 3.5D + e.getEyeHeight() - this.mc.thePlayer.posY + this.mc.thePlayer.getEyeHeight();
		    double deltaZ = e.posZ + e.posZ - e.lastTickPosZ - this.mc.thePlayer.lastTickPosZ;
		    double distance = Math.sqrt(Math.pow(deltaX, 2.0D) + Math.pow(deltaZ, 2.0D));
		    float yaw = (float)Math.toDegrees(-Math.atan(deltaX / deltaZ));
		    float pitch = (float)-Math.toDegrees(Math.atan(deltaY / distance));
		    if (deltaX < 0.0D && deltaZ < 0.0D) {
		      yaw = (float)(90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX)));
		    } else if (deltaX > 0.0D && deltaZ < 0.0D) {
		      yaw = (float)(-90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX)));
		    } 
		    return new float[] { yaw, pitch };
		  }
	
}
