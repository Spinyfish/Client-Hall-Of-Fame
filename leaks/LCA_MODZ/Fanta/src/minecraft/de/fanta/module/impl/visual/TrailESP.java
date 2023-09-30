package de.fanta.module.impl.visual;

import java.util.ArrayList;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventRender3D;
import de.fanta.module.Module;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.CheckBox;
import de.fanta.setting.settings.ColorValue;
import de.fanta.setting.settings.Slider;
import de.fanta.utils.ChatUtil;
import de.fanta.utils.Colors;
import de.fanta.utils.RenderUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import tv.twitch.chat.ChatUrlImageMessageToken;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;

import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

import static org.lwjgl.opengl.GL11.*;

public class TrailESP extends Module {
	public TrailESP() {
		super("TrailESP", 0, Type.Visual, new Color(108, 2, 139));
		this.settings.add(new Setting("Smooth Ending", new CheckBox(false)));
		this.settings.add(new Setting("Length", new Slider(1, 1000, 0.1, 4)));
		this.settings.add(new Setting("Alpha", new Slider(1, 255, 0.1, 4)));
		this.settings.add(new Setting("Color", new ColorValue(Color.RED.getRGB())));
	}

	// public ArrayList points = new ArrayList<>();
	public ArrayList<Point> points = new ArrayList<Point>();
	public static double lenghtttt;
	public static double alphaaaaa;

	public void onDisable() {
		points.clear();
		mc.gameSettings.keyBindSprint.pressed = false;
		super.onDisable();
	}

	@Override
	public void onEvent(Event event) {
		this.lenghtttt = ((Slider) this.getSetting("Length").getSetting()).curValue;
		this.alphaaaaa = ((Slider) this.getSetting("Alpha").getSetting()).curValue;
		
		if( event instanceof EventRender3D) {
			
			 points.removeIf(p -> p.age >= lenghtttt);
		      if(mc.thePlayer.motionX != 0 || mc.thePlayer.motionZ != 0) {
		            float x = (float) (mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * ((EventRender3D) event).getPartialTicks());
		            float y = (float) (mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * ((EventRender3D) event).getPartialTicks());
		            float z = (float) (mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * ((EventRender3D) event).getPartialTicks());
		            points.add(new Point((float) (x - mc.thePlayer.motionX), y, (float) (z - mc.thePlayer.motionZ)));
		            
		       //     ChatUtil.sendChatMessage(""+ x);
		        }

			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glEnable(GL11.GL_LINE_SMOOTH);
			GlStateManager.disableDepth();
			GlStateManager.disableTexture2D();
			GlStateManager.disableAlpha();
			GlStateManager.disableLighting();
			GL11.glLineWidth(3F);
			GL11.glShadeModel(GL11.GL_SMOOTH);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			for (final Point t : points) {
				if (points.indexOf(t) >= points.size() - 1)
					continue;

				Point temp = points.get(points.indexOf(t) + 1);

				float a =  (float) alphaaaaa;
				if (((CheckBox) this.getSetting("Smooth Ending").getSetting()).isState())
					a = (float) (alphaaaaa * (points.indexOf(t) / (float) points.size()));
				int[] rgb = Colors.getRGB(getColor2());
				glBegin(GL_QUAD_STRIP);
				final double x = t.x - mc.getRenderManager().renderPosX;
				final double y = t.y - mc.getRenderManager().renderPosY;
				final double z = t.z - mc.getRenderManager().renderPosZ;

				final double x1 = temp.x - mc.getRenderManager().renderPosX;
				final double y1 = temp.y - mc.getRenderManager().renderPosY;
				final double z1 = temp.z - mc.getRenderManager().renderPosZ;
		
				RenderUtil.color(Colors.getColor(rgb[0], rgb[1], rgb[2], 0));
				glVertex3d(x, y + mc.thePlayer.height, z);
				RenderUtil.color(getColor2());
				glVertex3d(x, y, z);
				RenderUtil.color(Colors.getColor(rgb[0], rgb[1], rgb[2], 0));
				glVertex3d(x1, y1 + mc.thePlayer.height, z1);
				RenderUtil.color(getColor2());
				glVertex3d(x1, y1, z1);
				glEnd();
				t.age++;
			}
			GlStateManager.enableAlpha();
			GL11.glShadeModel(GL11.GL_FLAT);
			GL11.glDisable(GL11.GL_LINE_SMOOTH);
			GL11.glEnable(GL11.GL_CULL_FACE);
			GlStateManager.enableTexture2D();
			GlStateManager.enableDepth();
			GlStateManager.resetColor();
			GlStateManager.popMatrix();
		}

	}
	

	public int getColor2() {
		try {
			return ((ColorValue) getSetting("Color").getSetting()).color;
		} catch (Exception e) {
			return Color.white.getRGB();
		}
	}

	class Point {
		public final float x, y, z;

		public float age = 0;

		public Point(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

}