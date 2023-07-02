package me.eldodebug.soar.management.mods.impl;

import java.awt.Color;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventRender3D;
import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;
import me.eldodebug.soar.utils.color.ColorUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class ReachCirclesMod extends Mod{

	public ReachCirclesMod() {
		super("Reach Circles", "Shows the range of the attack", ModCategory.RENDER);
	}

	@Override
	public void setup() {
		this.addBooleanSetting("Accent Color", this, true);
		this.addSliderSetting("Width", this, 1, 1, 10, true);
	}
	
	@EventTarget
	public void onRender3D(EventRender3D event) {
		
        GL11.glPushMatrix();
        mc.entityRenderer.disableLightmap();
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2929);
        GL11.glEnable(2848);
        GL11.glDepthMask(false);
        Iterator<Entity> iterator = mc.theWorld.loadedEntityList.iterator();

        while (iterator.hasNext()) {
            Object o = iterator.next();
            Entity entity = (Entity) o;

            if (entity instanceof EntityLivingBase && !entity.isInvisible() && !entity.isSneaking() && entity != mc.thePlayer && ((EntityLivingBase) entity).canEntityBeSeen(mc.thePlayer) && !entity.isInvisible() && entity instanceof EntityPlayer) {
                double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) event.getPartialTicks() - mc.getRenderManager().viewerPosX;
                double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) event.getPartialTicks() - mc.getRenderManager().viewerPosY;
                double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) event.getPartialTicks() - mc.getRenderManager().viewerPosZ;

                this.circle(posX, posY, posZ, mc.playerController.isInCreativeMode() ? 4.7D : 3.4D);
            }
        }

        GL11.glDepthMask(true);
        GL11.glDisable(2848);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        mc.entityRenderer.enableLightmap();
        GL11.glPopMatrix();
	}
	
    private void circle(double x, double y, double z, double rad) {
        GL11.glPushMatrix();
        Color color = Soar.instance.settingsManager.getSettingByName(this, "Accent Color").getValBoolean() ? ColorUtils.getClientColor(0) : new Color(255, 255, 255);
        
        GL11.glLineWidth((float) Soar.instance.settingsManager.getSettingByName(this, "Width").getValDouble());
        ColorUtils.setColor(color.getRGB());
        GL11.glBegin(1);

        for (int i = 0; i <= 90; ++i) {
            ColorUtils.setColor(color.getRGB(), 40);
            GL11.glVertex3d(x + rad * Math.cos((double) i * 6.283185307179586D / 45.0D), y, z + rad * Math.sin((double) i * 6.283185307179586D / 45.0D));
        }

        GL11.glEnd();
        GL11.glPopMatrix();
    }
}
