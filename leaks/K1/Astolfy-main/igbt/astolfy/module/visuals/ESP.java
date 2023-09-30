package igbt.astolfy.module.visuals;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_LINE_STIPPLE;
import static org.lwjgl.opengl.GL11.GL_POINT_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glPointSize;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glVertex3d;

import java.awt.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import igbt.astolfy.events.Event;
import igbt.astolfy.events.listeners.EventRender3D;
import igbt.astolfy.module.ModuleBase;
import igbt.astolfy.module.combat.AntiBot;
import igbt.astolfy.utils.RenderUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

public class ESP extends ModuleBase {
	
	public ESP() {
		super("ESP",Keyboard.KEY_NONE, Category.VISUALS);
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventRender3D) {
			for(Entity p : AntiBot.getEntities()) {
				if(p instanceof EntityLivingBase) {
					glPushMatrix();
					float partialTicks = ((EventRender3D)e).getPartialTicks();
		            glEnable(GL_BLEND);
		            //glDisable(GL_TEXTURE_2D);
		            double x = RenderUtils.interpolate(p.posX, p.lastTickPosX, partialTicks) - mc.getRenderManager().viewerPosX;
		            double y = RenderUtils.interpolate(p.posY, p.lastTickPosY, partialTicks) - mc.getRenderManager().viewerPosY;
		            double z = RenderUtils.interpolate(p.posZ, p.lastTickPosZ, partialTicks) - mc.getRenderManager().viewerPosZ;
		            glTranslated(x, y, z);
		            glLineWidth(0.75f);
		            glPointSize(1.0f);
		            glDepthMask(false);
		            glDisable(GL_DEPTH_TEST);
		            //mc.entityRenderer.setupCameraTransform(partialTicks, 0);

		            //glBegin(GL11.GL_LINE_LOOP);
		            glRotated(-mc.getRenderManager().playerViewY,0,1,0);

		            Gui.drawRect(0 - 0.7,0 - 0.2,p.width - 0.0,p.height + 0.2, 0x70111111);
		            RenderUtils.drawOutlineRect(0 - 0.7,0 - 0.2,p.width - 0.0,p.height + 0.2,0.05, -1);
		            RenderUtils.drawOutlineRect(p.width + 0.1,0 - 0.2,p.width + 0.1,p.height + 0.2,0.05, -1);
		            double hp = (p.height *( ((EntityLivingBase)p).getHealth()/ ((EntityLivingBase)p).getMaxHealth()));
		            RenderUtils.drawOutlineRect(p.width + 0.1,0 - 0.2,p.width + 0.1,hp + 0.2,0.05, new Color(255,0,0).getRGB());
			          //  glVertex3d(x - 0.5, dist + 1, z + 0.5);
		           // glVertex3d(x - 0.5, dist + 1, z - 0.5);
		           // glVertex3d(x + 0.5, dist + 1, z - 0.5);
		           // glVertex3d(x + 0.5, dist + 1, z + 0.5);
		           // glEnd();
		            glScaled(0.02, 0.02, 0.02);
		            glRotated(180, 0, 0, 1);
		            Gui.drawRect(-mc.customFont.getStringWidth(p.getName()) /2 - 2, -(p.height * 50) - 32,mc.customFont.getStringWidth(p.getName()) /2 + 2,-(p.height * 50) - 18, 0x70111111);
		            mc.customFont.drawString(p.getName(), -mc.customFont.getStringWidth(p.getName()) / 2, -(p.height * 50) - 30, -1);
		            glEnable(GL_DEPTH_TEST);
		            glDepthMask(true);
		            glEnable(GL_TEXTURE_2D);
		            glPopMatrix();
				}
			}
		}
	}
	
}
