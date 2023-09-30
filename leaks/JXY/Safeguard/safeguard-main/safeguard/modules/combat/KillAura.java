package intentions.modules.combat;

import java.awt.Color;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;

import intentions.events.Event;
import intentions.events.listeners.EventMotion;
import intentions.modules.Module;
import intentions.modules.player.AutoSoup;
import intentions.settings.BooleanSetting;
import intentions.settings.ModeSetting;
import intentions.settings.NumberSetting;
import intentions.settings.Setting;
import intentions.util.PlayerUtil;
import intentions.util.RenderUtils;
import intentions.util.Timer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;

public class KillAura extends Module {
  public Timer timer = new Timer();
  
  public static NumberSetting range = new NumberSetting("Range", 4.0D, 1.0D, 6.0D, 0.1D);

  public static NumberSetting cps = new NumberSetting("CPS", 10.0D, 1.0D, 20.0D, 1.0D);
  
  public static BooleanSetting noSwing = new BooleanSetting("No Swing", false), death = new BooleanSetting("Death", true), invisibles = new BooleanSetting("Invisibles", true), esp = new BooleanSetting("ESP", true);
  
  public static ModeSetting sort = new ModeSetting("Sort", "Closest", new String[] { "Furthest", "Health", "Closest" }), rotation = new ModeSetting("Rotation", "Server", new String[] { "Client", "Server" }), priority = new ModeSetting("Priority", "Passive", new String[] { "Passive", "None", "Mobs", "Players" });
  
  public KillAura() {
    super("KillAura", Keyboard.KEY_V, Module.Category.COMBAT, "Attacks entities around you", true);
    addSettings(new Setting[] { (Setting)this.range, (Setting)this.cps, (Setting)this.sort, (Setting)this.noSwing, (Setting)this.rotation, (Setting)this.priority, (Setting)this.invisibles, (Setting)this.death, (Setting)this.esp });
  }

  private EntityLivingBase target;
  private int timeSinceLastAtk;

  
  
  public void onEvent(Event e) {
    if (e instanceof EventMotion && 
      e.isPre()) {
    
      if (mc.thePlayer.deathTime > 1 && death.isEnabled()) {
    	  if(this.toggled) {
    		  this.toggle();
    	  }
    	  return;
      }
      
      if(mc.thePlayer == null) return;
      
      if(mc.thePlayer.getCurrentEquippedItem() != null) {
	      Item held = mc.thePlayer.getCurrentEquippedItem().getItem();
	      if(held == Items.bowl || held == Items.mushroom_stew && AutoSoup.enabled) return;
      }
      
      timeSinceLastAtk++;
      
      EventMotion event = (EventMotion)e;
      List<EntityLivingBase> targets = (List<EntityLivingBase>)this.mc.theWorld.loadedEntityList.stream().filter(EntityLivingBase.class::isInstance).collect(Collectors.toList());
      targets = (List<EntityLivingBase>)targets.stream().filter(entity -> (entity.getDistanceToEntity((Entity)this.mc.thePlayer) < this.range.getValue() && entity != this.mc.thePlayer && !entity.isDead && entity.getHealth() > 0.0F)).collect(Collectors.toList());
      if (this.priority.getMode() == "Passive") {
        targets = (List<EntityLivingBase>)targets.stream().filter(EntityAnimal.class::isInstance).collect(Collectors.toList());
      } else if (this.priority.getMode() == "Monsters") {
        targets = (List<EntityLivingBase>)targets.stream().filter(EntityMob.class::isInstance).collect(Collectors.toList());
      } else if (this.priority.getMode() == "Players") {
        targets = (List<EntityLivingBase>)targets.stream().filter(EntityPlayer.class::isInstance).collect(Collectors.toList());
      } 
      if (this.sort.getMode() == "Closest") {
        targets.sort(Comparator.comparingDouble(entity -> entity.getDistanceToEntity((Entity)this.mc.thePlayer)));
      } else if (this.sort.getMode() == "Furthest") {
        targets.sort(Comparator.<EntityLivingBase>comparingDouble(entity -> ((EntityLivingBase)entity).getDistanceToEntity((Entity)this.mc.thePlayer)).reversed());
      } else if (this.sort.getMode() == "Health") {
          targets.sort(Comparator.<EntityLivingBase>comparingDouble(entity -> ((EntityLivingBase)entity).getHealth()));
      } 
      targets = PlayerUtil.removeNotNeeded(targets);
      if (!targets.isEmpty()) {
        EntityLivingBase target = targets.get(0);
        
        if(target.isInvisibleToPlayer(mc.thePlayer))return;
        
        if (this.rotation.getMode() == "Server") {
        	if(mc.objectMouseOver.entityHit != target && mc.thePlayer.getDistanceToEntity(target) > 0.1) {
            	event.setYaw((float) ((float) getRotations((Entity)target)[0]) + (float)(Math.random()));
            	event.setPitch((float) ((float) getRotations((Entity)target)[1]) + (float)(Math.random()));
        	}
        } else {
            this.mc.thePlayer.rotationYaw = getRotations((Entity)target)[0] + (float)(Math.random());
            this.mc.thePlayer.rotationPitch = getRotations((Entity)target)[1] + (float)(Math.random());
        } 
        
        this.target = target;
        timeSinceLastAtk = 0;
        if (this.timer.hasTimeElapsed((long)(1000.0D / this.cps.getValue()), true)) {
        
          if(!invisibles.isEnabled() && target.isInvisible()) return;
        
          
          
          if (this.noSwing.isEnabled()) {
            this.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C0APacketAnimation());
          } else {
            this.mc.thePlayer.swingItem();
          } 
          
          if(Criticals.c) {
        	  double x = mc.thePlayer.posX, y = mc.thePlayer.posY, z = mc.thePlayer.posZ;
              mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.11, z, false));
              mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.1100013579, z, false));
              mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.0000013579, z, false));
          }
          this.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C02PacketUseEntity((Entity)target, C02PacketUseEntity.Action.ATTACK));
          
        } 
      } 
    } 
  }
  
  
  public void onUpdate() {
      if (target != null && !target.isDead && target != mc.thePlayer && mc.thePlayer.getDistanceToEntity(target) <= range.getValue() && esp.isEnabled()) {
          //Entity And Background
          GuiInventory.drawEntityOnScreen(ScaledResolution.getScaledWidth()/2-30,ScaledResolution.getScaledHeight()/2+80, 22, target.rotationYaw, target.rotationPitch, target);
          RenderUtils.drawRect(ScaledResolution.getScaledWidth()/2-60, ScaledResolution.getScaledHeight()/2+23, ScaledResolution.getScaledWidth()/2+105, ScaledResolution.getScaledHeight()/2+83, new Color(0, 0, 0, 120).getRGB());
          //Text
          mc.fontRendererObj.drawString(target.getName(), ScaledResolution.getScaledWidth()/2+10, ScaledResolution.getScaledHeight()/2+50, new Color(255, 255, 255, 255).getRGB());
          mc.fontRendererObj.drawString("Health: §c" + Math.ceil(target.getHealth()), ScaledResolution.getScaledWidth()/2+10, ScaledResolution.getScaledHeight()/2+60, new Color(255, 255, 255, 255).getRGB());
          //Winning Status
          if (mc.thePlayer.getHealth() - target.getHealth() == 0) mc.fontRendererObj.drawString("Tie", ScaledResolution.getScaledWidth()/2+10, ScaledResolution.getScaledHeight()/2+70, new Color(255, 255, 255, 255).getRGB());
          if (mc.thePlayer.getHealth() - target.getHealth() > 0) mc.fontRendererObj.drawString("Winning", ScaledResolution.getScaledWidth()/2+10, ScaledResolution.getScaledHeight()/2+70, new Color(255, 255, 255, 255).getRGB());
          if (mc.thePlayer.getHealth() - target.getHealth() < 0) mc.fontRendererObj.drawString("Losing", ScaledResolution.getScaledWidth()/2+10, ScaledResolution.getScaledHeight()/2+70, new Color(255, 255, 255, 255).getRGB());
          //Health
          RenderUtils.drawRect(ScaledResolution.getScaledWidth()/2, ScaledResolution.getScaledHeight()/2+30, (int) (ScaledResolution.getScaledWidth()/2+40+target.getHealth()-target.hurtTime), ScaledResolution.getScaledHeight()/2+40, new Color(255,51,204,255).getRGB());
          RenderUtils.drawRect(ScaledResolution.getScaledWidth()/2, ScaledResolution.getScaledHeight()/2+30, (int) (ScaledResolution.getScaledWidth()/2+40+target.getHealth()-target.hurtTime), ScaledResolution.getScaledHeight()/2+40, new Color(255,51,204,255).getRGB());
          //Health Background
          RenderUtils.drawRect(ScaledResolution.getScaledWidth()/2, ScaledResolution.getScaledHeight()/2+30, (int) (ScaledResolution.getScaledWidth()/2+40+target.getMaxHealth()), ScaledResolution.getScaledHeight()/2+40, new Color(128,128,128,150).getRGB());
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
