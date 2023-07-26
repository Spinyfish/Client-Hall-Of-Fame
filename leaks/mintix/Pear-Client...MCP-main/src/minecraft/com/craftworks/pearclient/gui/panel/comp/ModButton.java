package com.craftworks.pearclient.gui.panel.comp;

import java.awt.Color;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.craftworks.pearclient.PearClient;
import com.craftworks.pearclient.gui.panel.impl.ModulePanel;
import com.craftworks.pearclient.gui.panel.impl.ModulePanelType;
import com.craftworks.pearclient.hud.mods.HudMod;
import com.craftworks.pearclient.nofication.Notification;
import com.craftworks.pearclient.nofication.NotificationManager;
import com.craftworks.pearclient.nofication.NotificationType;
import com.craftworks.pearclient.util.GLUtils;
import com.craftworks.pearclient.util.animation.SimpleAnimation;
import com.craftworks.pearclient.util.draw.DrawUtil;
import com.craftworks.pearclient.util.font.GlyphPageFontRenderer;

import net.minecraft.client.Minecraft;

public class ModButton {
	
	public float x, y, w, h;
	public HudMod m;
	
	public ModButton(int x, float y, int w, int h, HudMod m) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.m = m;
	}
	
	public void draw() {
		GlyphPageFontRenderer renderer = GlyphPageFontRenderer.create("Roboto", 16, false, false, false);
		DrawUtil.drawRoundedRectangle(this.x - 5, this.y - 25, this.w, this.h + 30, 3.0f, new Color(66, 66, 66));

		DrawUtil.drawRoundedRectangle(this.x + 50 + 5, this.y + 14.5f + 5, this.w - 65, this.h - 19, 6, m.isToggled() ? new Color(60, 232, 118) : new Color(216, 49, 49, 200));
		DrawUtil.drawRoundedRectangle(m.isToggled() ? this.x + 64 + 5 : this.x + 52 + 5, this.y + 15.5f + 5, this.w - 81, this.h - 21, 4, getColor());

		DrawUtil.drawRoundedOutline(this.x + 49 + 5, this.y + 13 + 5, this.w - 63, this.h - 16, 6, 0.15f, new Color(0, 0, 0, 0), new Color(33, 33, 33));
		renderer.drawString(m.getName(), x + 10, y - 10, -1, false);
	}
	
	private Color getColor() {
		if(m.isToggled()) {
			return new Color(255, 255, 255);
		} else {
			return new Color(255, 255, 255);
		}
	}
	
	
	public void onClick(int mouseX, int mouseY, int mouseButton) {
	    		if(mouseX >= x + 50 + 5 && mouseX <= x + w - 9 && mouseY >= y + 14 + 5 && mouseY <= y + h - 2) {
	    			if(mouseButton == 0) {
	    				if(m.isToggled()) {
	    					m.setToggled(false);
	    				} else {
	    					m.setToggled(true);
	    					NotificationManager.show(new Notification(NotificationType.INFO, m.getName(), m.getDescription(), 2));
	    				}
	    			}
	    			System.out.println(m.getName());
	    			}
	    		}
	}