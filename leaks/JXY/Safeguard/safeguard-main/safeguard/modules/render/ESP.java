
package intentions.modules.render;

import org.lwjgl.input.Keyboard;

import intentions.Client;
import intentions.modules.Module;
import intentions.settings.BooleanSetting;
import intentions.util.RenderUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;

public class ESP extends Module {
	
	public static BooleanSetting box = new BooleanSetting("Box", true), tracers = new BooleanSetting("Tracers", true);
	public static BooleanSetting ESPSelf = new BooleanSetting("ESPSelf", false);
	public static BooleanSetting mob = new BooleanSetting("Mobs", true);
	public static BooleanSetting player = new BooleanSetting("Players", true);
	public static BooleanSetting animal = new BooleanSetting("Animals", true);
	public static BooleanSetting other = new BooleanSetting("Other", true);
	
	public ESP() {
		super("ESP", Keyboard.KEY_NONE, Category.RENDER, "Renders lines and/or boxes to entities around you", true);
		this.addSettings(box, tracers, ESPSelf, mob, player, animal, other);
	}

	public void onTick() {
		if(this.toggled)
			mc.gameSettings.viewBobbing = false; // Prevent ESP from being bonkers XD
	}
	
	public void onRender() {
		
		if(!this.toggled) 
			return;
		
		for(Object obj : mc.theWorld.loadedEntityList)
		{
			//if(obj != mc.thePlayer /* && !(obj instanceof EntityLivingBase) */)
			//{
				try {
					EntityLivingBase entity = (EntityLivingBase) obj;
					if(entity.isPotionActive(Potion.invisibility) || entity.isInvisible())continue;
					if(entity instanceof EntityPlayer && player.isEnabled()) {
						if(!ESPSelf.isEnabled() && obj == mc.thePlayer && player.isEnabled()) continue;
						if(obj == mc.thePlayer) {
							if(mc.gameSettings.thirdPersonView == 0) {
								player(entity, false, false);
							} else {
								player(entity, false, true);
							}
						} else player(entity, true, true);
					}
					else if(entity instanceof EntityMob && mob.isEnabled()) {
						mob(entity);
					}
					else if(entity instanceof EntityAnimal && animal.isEnabled()) {
						animal(entity);
					}
					else if(other.isEnabled() && !(entity instanceof EntityAnimal) && !(entity instanceof EntityMob) && !(entity instanceof EntityPlayer)) {
						passive(entity);
					}
				} catch (ClassCastException e)
				{
					continue; // Prevent crashes if trying to ESP an item :/
				}
			//}
		}
	}
	
	public void player(EntityLivingBase entity, boolean t, boolean v)
	{
		float red = 1.0f;
		float green = 0f;
		float blue = 0f;
		
		double xPos = (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosX;
		double yPos = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosY;
		double zPos = (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosZ;
		
		render(red, green, blue, xPos, yPos, zPos, entity.width, entity.height, t, v);
	}
	
	public void mob(EntityLivingBase entity)
	{
		float red = 1.0f;
		float green = 1.0f;
		float blue = 0f;
		
		double xPos = (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosX;
		double yPos = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosY;
		double zPos = (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosZ;
		
		render(red, green, blue, xPos, yPos, zPos, entity.width, entity.height, true, true);
	}
	
	public void animal(EntityLivingBase entity)
	{
		float red = 0f;
		float green = 1.0f;
		float blue = 0f;
		
		double xPos = (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosX;
		double yPos = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosY;
		double zPos = (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosZ;
		
		render(red, green, blue, xPos, yPos, zPos, entity.width, entity.height, true, true);
	}
	
	public void passive(EntityLivingBase entity)
	{
		float red = 0.5f;
		float green = 0.5f;
		float blue = 0.5f;
		
		double xPos = (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosX;
		double yPos = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosY;
		double zPos = (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks) - mc.getRenderManager().renderPosZ;
		
		render(red, green, blue, xPos, yPos, zPos, entity.width, entity.height, true, true);
	}
	
	public void render(float red, float green, float blue, double x, double y, double z, float width, float height, boolean t, boolean v) {
		if(box.isEnabled() && v)
			RenderUtils.drawEntityESP(x, y, z, width - (width / 4), height, red, green, blue, 0.2F, 0F, 0F, 0F, 1F, 1F);
		if(tracers.isEnabled() && t) {
			RenderUtils.drawTracerLine(x, y, z, red, green, blue, 0.45F, 1F);
		}
	}
}
