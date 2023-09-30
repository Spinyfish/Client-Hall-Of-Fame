package de.fanta.module.impl.visual;

import java.awt.Color;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventRender2D;
import de.fanta.events.listeners.EventUpdate;
import de.fanta.module.Module;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.CheckBox;
import de.fanta.setting.settings.Slider;
import de.fanta.utils.BlurHelper;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

public class MotionGraph extends Module{
	
	private ArrayList<Motion> motions = new ArrayList();
	
	public MotionGraph() {
		super("MotionGraph", 0, Type.Visual, Color.blue);
		this.settings.add(new Setting("Outline", new CheckBox(true)));
		this.settings.add(new Setting("Blur", new CheckBox(true)));
		this.settings.add(new Setting("Background", new CheckBox(true)));
		this.settings.add(new Setting("Time MS", new Slider(2000, 7000, 100, 3000)));
		
	}

	@Override
	public void onDisable() {
		motions.clear();
	}
	
	@Override
	public void onEvent(Event event) {
		if(event instanceof EventRender2D) {
			EventRender2D e = (EventRender2D)event;
			if(!e.isPre()) return;
			ScaledResolution sr = new ScaledResolution(mc);

			GL11.glEnable(GL11.GL_BLEND);
	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	        Gui gui = new Gui();
	        if(((CheckBox)this.getSetting("Blur").getSetting()).state)Client.blurHelper.blur2(sr.getScaledWidth()/2-motions.size(), sr.getScaledHeight()-100, sr.getScaledWidth()/2+motions.size(), sr.getScaledHeight()-50, 100F);
	        if(((CheckBox)this.getSetting("Background").getSetting()).state)gui.drawRect(sr.getScaledWidth()/2-motions.size(), sr.getScaledHeight()-100, sr.getScaledWidth()/2+motions.size(), sr.getScaledHeight()-50, Integer.MIN_VALUE);
	        
	        if(((CheckBox)this.getSetting("Outline").getSetting()).state) gui.drawHollowRect(sr.getScaledWidth()/2-motions.size()-.5f, sr.getScaledHeight()-100.5F, motions.size()*2+1, 51, .5f, Color.white.getRGB());
	        for(int i = 0; i < motions.size(); i++) {
	        	Motion mot = motions.get(i);
	        	if(mot.motion > 58) mot.motion = 58F;
	        	try {
	        		if(motions.get(i+1).motion > 58) motions.get(i+1).motion = 58F;					
				} catch (Exception e2) {
				}
	        //	float yStand = sr.getScaledHeight()-45;
	        	float x = sr.getScaledWidth()/2-motions.size()+(i*2);
	        	float y = sr.getScaledHeight()-60-(mot.motion/2);
	        	float x2;
	        	try {
	        		x2 = sr.getScaledWidth()/2-motions.size()+((i+1)*2);					
				} catch (Exception e2) {
					x2 = x;
				}
	        	float y2;
	        	try {
	        		y2 = sr.getScaledHeight()-60-(motions.get(i+1).motion/2);					
				} catch (Exception e2) {
					y2 = y;
				}
	        	gui.draw2dLine((double)x, (double)y, (double)x2, (double)y2, Color.white);
	        }
//	        gui.draw2dLine(sr.getScaledWidth()/2-motions.size()+.25, sr.getScaledHeight()-60.25, sr.getScaledWidth()/2+motions.size()-.25, sr.getScaledHeight()-60.25, 1, Color.red);
		}
		if(event instanceof EventUpdate) {
			double len = Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionY * mc.thePlayer.motionY + mc.thePlayer.motionZ * mc.thePlayer.motionZ)*50;
			if(len<=3.92) len=0;
			motions.add(new Motion((float) len, System.currentTimeMillis()));
			if(motions == null || motions.isEmpty()) return;
			try {
				for(Motion motion : motions) {
					if(System.currentTimeMillis()-motion.time > (int)((double)((Slider) this.getSetting("Time MS").getSetting()).curValue)) {
						motions.remove(motion);
					}
				}				
			} catch (Exception e) {
			}
		}
	}

	class Motion {
		
		public float motion;
		public long time;
		
		public Motion(float motion, long time) {
			this.motion = motion;
			this.time = time;
		}
		
	}
	
}
