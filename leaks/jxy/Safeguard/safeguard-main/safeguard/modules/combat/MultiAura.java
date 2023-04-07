package intentions.modules.combat;

import java.util.List;

import intentions.events.Event;
import intentions.events.listeners.EventMotion;
import intentions.modules.Module;
import intentions.settings.BooleanSetting;
import intentions.settings.ModeSetting;
import intentions.settings.NumberSetting;
import intentions.settings.Setting;
import intentions.util.RenderUtils;
import intentions.util.TeleportUtils;
import intentions.util.Timer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;

public class MultiAura extends Module {
  public Timer timer = new Timer();
  
  public static NumberSetting range = new NumberSetting("Range", 4.0D, 1.0D, 6.0D, 0.1D);
  
  public static NumberSetting cps = new NumberSetting("CPS", 10.0D, 1.0D, 20.0D, 1.0D);
  
  public BooleanSetting noSwing = new BooleanSetting("No Swing", false), death = new BooleanSetting("Death", true), esp = new BooleanSetting("ESP", true);
  
  public static ModeSetting priority = new ModeSetting("Priority", "Passive", new String[] { "Passive", "None", "Mobs", "Players" });
  
  public MultiAura() {
    super("MultiAura", 0, Module.Category.COMBAT, "Attacks ALL entities around you", true);
    addSettings(new Setting[] { (Setting)range, (Setting)cps, (Setting)this.noSwing, (Setting)this.priority, (Setting)this.esp });
  }

  private List<EntityLivingBase> targets;

  private int timeSinceLastAtk;
  
  public void onRender() {
	  if(esp.isEnabled() && timeSinceLastAtk < 10 && targets != null) {
		  for(Entity target : targets) {
			if(!target.isEntityAlive()) {targets.remove(target); return;}
			float red = 0.10588235294f;
			float green = 0.56470588235f;
			float blue = 0.75294117647f;
			
			double xPos = (target.lastTickPosX + (target.posX - target.lastTickPosX) * mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosX;
			double yPos = (target.lastTickPosY + (target.posY - target.lastTickPosY) * mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosY;
			double zPos = (target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosZ;    		
			render(red, green, blue, xPos, yPos, zPos, target.width, target.height);
		  }
      }
  }
  
  
  public void onEvent(Event e) {
    if (e instanceof EventMotion && 
      e.isPre()) {
    	
      if (mc.thePlayer.deathTime > 1 && death.isEnabled()) {
      	  if(this.toggled) {
      		  this.toggle();
      	  }
      	  return;
      }
      timeSinceLastAtk++;
      EventMotion event = (EventMotion)e;
      /*List<EntityLivingBase> targets = (List<EntityLivingBase>)this.mc.theWorld.loadedEntityList.stream().filter(EntityLivingBase.class::isInstance).collect(Collectors.toList());
      targets = (List<EntityLivingBase>)targets.stream().filter(entity -> (entity.getDistanceToEntity((Entity)this.mc.thePlayer) < range.getValue() && entity != this.mc.thePlayer && !entity.isDead && entity.getHealth() > 0.0F)).collect(Collectors.toList());
      if (priority.getMode() == "Passive") {
        targets = (List<EntityLivingBase>)targets.stream().filter(EntityAnimal.class::isInstance).collect(Collectors.toList());
      } else if (priority.getMode() == "Monsters") {
        targets = (List<EntityLivingBase>)targets.stream().filter(EntityMob.class::isInstance).collect(Collectors.toList());
      } else if (priority.getMode() == "Players") {
        targets = (List<EntityLivingBase>)targets.stream().filter(EntityPlayer.class::isInstance).collect(Collectors.toList());
      } 
      targets = PlayerUtil.removeNotNeeded(targets);
      if (!targets.isEmpty()) {
    	  this.targets = targets;
    	  timeSinceLastAtk = 0;
    	  if(this.timer.hasTimeElapsed((long)(1000.0D / cps.getValue()), true)) {
	        if (this.noSwing.isEnabled()) {
	          this.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C0APacketAnimation());
	        } else {
	          this.mc.thePlayer.swingItem();
	        } 
	        int count = 0;
	        for (EntityLivingBase target : targets) {
	          event.setYaw(getRotations((Entity)target)[0]);
	          event.setPitch(getRotations((Entity)target)[1]);
	          
	          if(Criticals.c) {
	        	  
	        	  this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.2958195819595185918, mc.thePlayer.posZ, false));
	        	  this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
	         
	          }
	          
	          this.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C02PacketUseEntity((Entity)target, C02PacketUseEntity.Action.ATTACK));
	          count++;
	        } 
    	  }
      } */
      for(Object j : mc.theWorld.playerEntities) {
    	  
    	  if(j instanceof EntityOtherPlayerMP) {
    		  
    		  TeleportUtils.pathFinderTeleportTo(mc.thePlayer.getPositionVector(), ((EntityOtherPlayerMP) j).getPositionVector());
    		  
    		  this.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C02PacketUseEntity((Entity)j, C02PacketUseEntity.Action.ATTACK));
    		  
    	  }
    	  
      }
    } 
  }
  
  public void render(float red, float green, float blue, double x, double y, double z, float width, float height) {
	  RenderUtils.drawEntityESP(x, y, z, width - (width / 4), height, red, green, blue, 0.2F, 0F, 0F, 0F, 1F, 1F);
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
