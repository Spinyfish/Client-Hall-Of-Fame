package markgg.modules.render;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import markgg.modules.Module;
import markgg.settings.BooleanSetting;
import markgg.settings.NumberSetting;
import markgg.util.esp.RenderUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityChest;

public class ESP extends Module {

	public BooleanSetting playerBool = new BooleanSetting("Players", this, true);
	public BooleanSetting hostileBool = new BooleanSetting("Mobs", this, false);
	public BooleanSetting friendlyBool = new BooleanSetting("Animals", this, false);
	public NumberSetting plrRed = new NumberSetting("Player Red", this, 0, 0, 1, 0.1);
	public NumberSetting plrGreen = new NumberSetting("Player Green", this, 0.5, 0, 1, 0.1);
	public NumberSetting plrBlue = new NumberSetting("Player Blue", this, 1, 0, 1, 0.1);

	public ESP() {
		super("ESP", "Renders hitboxes", 0, Category.RENDER);
		addSettings(playerBool, hostileBool, friendlyBool, plrRed,plrGreen,plrBlue);
	}

	public void onRender() {
		if(this.isEnabled()) {
			if (mc.theWorld != null) {
				for (final Object theObject : mc.theWorld.loadedEntityList) {
					if (!(theObject instanceof EntityLivingBase)) {
						continue;
					}
					final EntityLivingBase entity = (EntityLivingBase)theObject;
					if(playerBool.isEnabled()) {
						if (entity instanceof EntityPlayer) {
							if (entity == this.mc.thePlayer) {
								continue;
							}
							this.player(entity);
						}
					}
					if(hostileBool.isEnabled()) {
						if (entity instanceof EntityMob || entity instanceof EntityMagmaCube || entity instanceof EntityGhast || entity instanceof EntitySlime) {
							this.mob(entity);
						}
					}
					if(friendlyBool.isEnabled()) {
						if (entity instanceof EntityAnimal || entity instanceof EntityVillager || entity instanceof EntitySquid || entity instanceof EntityBat) {
							this.animal(entity);
						}
					}
				}
			}
		}
	}

	public void player(final EntityLivingBase entity) {
		final float red = (float) plrRed.getValue();
		final float green = (float) plrGreen.getValue();
		final float blue = (float) plrBlue.getValue();
		final double xPos = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * this.mc.timer.renderPartialTicks - this.mc.getRenderManager().renderPosX;
		final double yPos = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * this.mc.timer.renderPartialTicks - this.mc.getRenderManager().renderPosY;
		final double zPos = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * this.mc.timer.renderPartialTicks - this.mc.getRenderManager().renderPosZ;
		this.render(red, green, blue, xPos, yPos, zPos, entity.width, entity.height);
	}

	public void mob(final EntityLivingBase entity) {
		final float red = 1f;
		final float green = 0f;
		final float blue = 0.2f;
		final double xPos = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * this.mc.timer.renderPartialTicks - this.mc.getRenderManager().renderPosX;
		final double yPos = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * this.mc.timer.renderPartialTicks - this.mc.getRenderManager().renderPosY;
		final double zPos = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * this.mc.timer.renderPartialTicks - this.mc.getRenderManager().renderPosZ;
		this.render(red, green, blue, xPos, yPos, zPos, entity.width, entity.height);
	}

	public void animal(final EntityLivingBase entity) {
		final float red = 0f;
		final float green = 1f;
		final float blue = 0.3f;
		final double xPos = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * this.mc.timer.renderPartialTicks - this.mc.getRenderManager().renderPosX;
		final double yPos = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * this.mc.timer.renderPartialTicks - this.mc.getRenderManager().renderPosY;
		final double zPos = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * this.mc.timer.renderPartialTicks - this.mc.getRenderManager().renderPosZ;
		this.render(red, green, blue, xPos, yPos, zPos, entity.width, entity.height);
	}

	public void render(final float red, final float green, final float blue, final double x, final double y, final double z, final float width, final float height) {
		RenderUtil.drawOutlinedEntityESP(x, y, z, width, height, red, green, blue, 0.45f);
	}


}
