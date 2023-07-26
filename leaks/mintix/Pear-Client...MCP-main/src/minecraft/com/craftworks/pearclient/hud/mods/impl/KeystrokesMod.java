package com.craftworks.pearclient.hud.mods.impl;

import java.awt.Color;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.craftworks.pearclient.PearClient;
import com.craftworks.pearclient.hud.mods.HudMod;
import com.craftworks.pearclient.util.animation.SimpleAnimation;
import com.craftworks.pearclient.util.blur.BlurUtils;
import com.craftworks.pearclient.util.draw.DrawUtil;
import com.craftworks.pearclient.util.draw.GLDraw;
import com.craftworks.pearclient.util.setting.impl.ModeSetting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;

public class KeystrokesMod extends HudMod {
	
	ModeSetting keymode = new ModeSetting("Mode", this, "WASD JUMP", "WASD JUMP", "WASD JUMP MOUSE");

	public KeystrokesMod() {
		super("KeyStrokes", "", 100, 100);
	}
	
	public static enum KeystrokesMode{
		
		WASD_JUMP_MOUSE(Key.W, Key.A, Key.S, Key.D, Key.LMB, Key.RMB, Key.Jump2),
		WASD_JUMP(Key.W, Key.A, Key.S, Key.D, Key.Jump1);
		
		private final Key[] keys;
		private int width,height;
		
		private KeystrokesMode(Key... keysIn) {
			
			this.keys = keysIn;
			
			
			for(Key key : keys) {
				this.width = Math.max(this.width, key.getX() + key.getWidth());
				this.height = Math.max(this.height, key.getY() + key.getHeight());
			}
			
		}
		
		public int getHeight() {
			return height;
		}
		
		public int getWidth() {
			return width;
		}
		public Key[] getKeys() {
			return keys;
		}
	}

	public static class Key{

		public static Minecraft mc = Minecraft.getMinecraft();

		private static final Key W = new Key("W", mc.gameSettings.keyBindForward, 21, 0, 18, 18);
		private static final Key A = new Key("A", mc.gameSettings.keyBindLeft, 0, 21, 18, 18);
		private static final Key S = new Key("S", mc.gameSettings.keyBindBack, 21, 21, 18, 18);
		private static final Key D = new Key("D", mc.gameSettings.keyBindRight, 42, 21, 18, 18);


		private static final Key LMB = new Key("LMB", mc.gameSettings.keyBindAttack, 1, 42, 27, 18);
		private static final Key RMB = new Key("RMB", mc.gameSettings.keyBindUseItem, 32, 42, 27, 18);

		private static final Key Jump1 = new Key("", mc.gameSettings.keyBindJump, 1, 43, 58, 18);
		private static final Key Jump2 = new Key("", mc.gameSettings.keyBindJump, 1, 63, 58, 18);


		private final String name;
		private final KeyBinding keyBind;
		private final int x,y,w,h;

		public Key(String name, KeyBinding keyBind, int x, int y, int w, int h) {
			this.name = name;
			this.keyBind = keyBind;
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;

		}

		public boolean isDown() {
			return keyBind.isKeyDown();

		}

		public int getHeight() {
			return h;

		}

		public int getWidth() {
			return w;
		}

		public String getName() {
			return name;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

	}
	
	private KeystrokesMode mode = KeystrokesMode.WASD_JUMP;

	@Override
	public int getWidth() {
		return 60;
	}

	@Override
	public int getHeight() {
		if(keymode.is("WASD JUMP")) {
			return mode.getHeight();
		} else {
			return mode.WASD_JUMP_MOUSE.getHeight();
		}
	}

	@Override
	public void onRender2D() {
		GL11.glPushMatrix();

		if(keymode.is("WASD JUMP MOUSE")) {
			for(Key key : mode.WASD_JUMP_MOUSE.getKeys()) {
				DrawUtil.drawRoundedOutline(getX() + key.getX() - 1f, getY() + key.getY() - 1, key.getWidth() + 2, key.getHeight() + 2, 6, 0.35f, new Color(0, 0, 0, 0), new Color(0, 0, 0, 50));
			}
			DrawUtil.drawRoundedRectangle(getX() + Key.W.getX(), getY() + Key.W.getY(), Key.W.getWidth(), Key.W.getHeight(), 6, Key.W.isDown() ? new Color(0,0,0, 30) : new Color(255,255,255, 0));
			DrawUtil.drawRoundedRectangle(getX() + Key.A.getX(), getY() + Key.A.getY(), Key.A.getWidth(), Key.A.getHeight(), 6, Key.A.isDown() ? new Color(0,0,0, 30) : new Color(255,255,255, 0));
			DrawUtil.drawRoundedRectangle(getX() + Key.S.getX(), getY() + Key.S.getY(), Key.S.getWidth(), Key.S.getHeight(), 6, Key.S.isDown() ? new Color(0,0,0, 30) : new Color(255,255,255, 0));
			DrawUtil.drawRoundedRectangle(getX() + Key.D.getX(), getY() + Key.D.getY(), Key.D.getWidth(), Key.D.getHeight(), 6, Key.D.isDown() ? new Color(0,0,0, 30) : new Color(255,255,255, 0));
			DrawUtil.drawRoundedRectangle(getX() + Key.LMB.getX(), getY() + Key.LMB.getY(), Key.LMB.getWidth(), Key.LMB.getHeight(), 6, Key.LMB.isDown() ? new Color(0,0,0, 30) : new Color(255,255,255, 0));
			DrawUtil.drawRoundedRectangle(getX() + Key.RMB.getX(), getY() + Key.RMB.getY(), Key.RMB.getWidth(), Key.RMB.getHeight(), 6, Key.RMB.isDown() ? new Color(0,0,0, 30) : new Color(255,255,255, 0));
			DrawUtil.drawRoundedRectangle(getX() + Key.Jump2.getX(), getY() + Key.Jump2.getY(), Key.Jump2.getWidth(), Key.Jump2.getHeight(), 6, Key.Jump2.isDown() ? new Color(0,0,0, 30) : new Color(255,255,255, 0));
			DrawUtil.drawRoundedRectangle(getX() + Key.Jump2.getX() + 8, getY() + Key.Jump2.getY() + 8.5f, Key.Jump2.getWidth() - 16, Key.Jump2.getHeight() - 17, 0, new Color(255, 255, 255));
			for(Key key : mode.WASD_JUMP_MOUSE.getKeys()) {
				int textWidth = (int) fr.getStringWidth(key.getName());
				fr.drawString(key.getName(), getX() + key.getX() + key.getWidth() / 2 - textWidth / 2, getY() + key.getY() + key.getHeight() / 2 - 4, -1);
			}
		}  else if(keymode.is("WASD JUMP")) {
			for(Key key : mode.WASD_JUMP.getKeys()) {
				DrawUtil.drawRoundedOutline(getX() + key.getX() - 1f, getY() + key.getY() - 1, key.getWidth() + 2, key.getHeight() + 2, 6, 0.35f, new Color(0, 0, 0, 0), new Color(0, 0, 0, 50));
			}
			DrawUtil.drawRoundedRectangle(getX() + Key.W.getX(), getY() + Key.W.getY(), Key.W.getWidth(), Key.W.getHeight(), 6, Key.W.isDown() ? new Color(0,0,0, 30) : new Color(255,255,255, 0));
			DrawUtil.drawRoundedRectangle(getX() + Key.A.getX(), getY() + Key.A.getY(), Key.A.getWidth(), Key.A.getHeight(), 6, Key.A.isDown() ? new Color(0,0,0, 30) : new Color(255,255,255, 0));
			DrawUtil.drawRoundedRectangle(getX() + Key.S.getX(), getY() + Key.S.getY(), Key.S.getWidth(), Key.S.getHeight(), 6, Key.S.isDown() ? new Color(0,0,0, 30) : new Color(255,255,255, 0));
			DrawUtil.drawRoundedRectangle(getX() + Key.D.getX(), getY() + Key.D.getY(), Key.D.getWidth(), Key.D.getHeight(), 6, Key.D.isDown() ? new Color(0,0,0, 30) : new Color(255,255,255, 0));
			DrawUtil.drawRoundedRectangle(getX() + Key.Jump1.getX(), getY() + Key.Jump1.getY(), Key.Jump1.getWidth(), Key.Jump1.getHeight(), 6, Key.Jump1.isDown() ? new Color(0,0,0, 30) : new Color(255,255,255, 0));
			DrawUtil.drawRoundedRectangle(getX() + Key.Jump1.getX() + 8, getY() + Key.Jump1.getY() + 8.5f, Key.Jump1.getWidth() - 16, Key.Jump1.getHeight() - 17, 0, new Color(255, 255, 255));
			for(Key key : mode.WASD_JUMP.getKeys()) {
				int textWidth = (int) fr.getStringWidth(key.getName());
				fr.drawString(key.getName(), getX() + key.getX() + key.getWidth() / 2 - textWidth / 2, getY() + key.getY() + key.getHeight() / 2 - 4, -1);
			}
		}
		GL11.glPopMatrix();
		
		super.onRender2D();
	}

	@Override
	public void onRenderDummy() {
		GL11.glPushMatrix();

		if(keymode.is("WASD JUMP MOUSE")) {
			for(Key key : mode.WASD_JUMP_MOUSE.getKeys()) {
				DrawUtil.drawRoundedOutline(getX() + key.getX() - 1f, getY() + key.getY() - 1, key.getWidth() + 2, key.getHeight() + 2, 6, 0.35f, new Color(0, 0, 0, 0), new Color(0, 0, 0, 50));
			}
			DrawUtil.drawRoundedRectangle(getX() + Key.W.getX(), getY() + Key.W.getY(), Key.W.getWidth(), Key.W.getHeight(), 6, Key.W.isDown() ? new Color(0,0,0, 30) : new Color(255,255,255, 0));
			DrawUtil.drawRoundedRectangle(getX() + Key.A.getX(), getY() + Key.A.getY(), Key.A.getWidth(), Key.A.getHeight(), 6, Key.A.isDown() ? new Color(0,0,0, 30) : new Color(255,255,255, 0));
			DrawUtil.drawRoundedRectangle(getX() + Key.S.getX(), getY() + Key.S.getY(), Key.S.getWidth(), Key.S.getHeight(), 6, Key.S.isDown() ? new Color(0,0,0, 30) : new Color(255,255,255, 0));
			DrawUtil.drawRoundedRectangle(getX() + Key.D.getX(), getY() + Key.D.getY(), Key.D.getWidth(), Key.D.getHeight(), 6, Key.D.isDown() ? new Color(0,0,0, 30) : new Color(255,255,255, 0));
			DrawUtil.drawRoundedRectangle(getX() + Key.LMB.getX(), getY() + Key.LMB.getY(), Key.LMB.getWidth(), Key.LMB.getHeight(), 6, Key.LMB.isDown() ? new Color(0,0,0, 30) : new Color(255,255,255, 0));
			DrawUtil.drawRoundedRectangle(getX() + Key.RMB.getX(), getY() + Key.RMB.getY(), Key.RMB.getWidth(), Key.RMB.getHeight(), 6, Key.RMB.isDown() ? new Color(0,0,0, 30) : new Color(255,255,255, 0));
			DrawUtil.drawRoundedRectangle(getX() + Key.Jump2.getX(), getY() + Key.Jump2.getY(), Key.Jump2.getWidth(), Key.Jump2.getHeight(), 6, Key.Jump2.isDown() ? new Color(0,0,0, 30) : new Color(255,255,255, 0));
			DrawUtil.drawRoundedRectangle(getX() + Key.Jump2.getX() + 8, getY() + Key.Jump2.getY() + 8.5f, Key.Jump2.getWidth() - 16, Key.Jump2.getHeight() - 17, 0, new Color(255, 255, 255));
			for(Key key : mode.WASD_JUMP_MOUSE.getKeys()) {
				int textWidth = (int) fr.getStringWidth(key.getName());
				fr.drawString(key.getName(), getX() + key.getX() + key.getWidth() / 2 - textWidth / 2, getY() + key.getY() + key.getHeight() / 2 - 4, -1);
			}
		}  else if(keymode.is("WASD JUMP")) {
			for(Key key : mode.WASD_JUMP.getKeys()) {
				DrawUtil.drawRoundedOutline(getX() + key.getX() - 1f, getY() + key.getY() - 1, key.getWidth() + 2, key.getHeight() + 2, 6, 0.35f, new Color(0, 0, 0, 0), new Color(0, 0, 0, 50));
			}
			DrawUtil.drawRoundedRectangle(getX() + Key.W.getX(), getY() + Key.W.getY(), Key.W.getWidth(), Key.W.getHeight(), 6, Key.W.isDown() ? new Color(0,0,0, 30) : new Color(255,255,255, 0));
			DrawUtil.drawRoundedRectangle(getX() + Key.A.getX(), getY() + Key.A.getY(), Key.A.getWidth(), Key.A.getHeight(), 6, Key.A.isDown() ? new Color(0,0,0, 30) : new Color(255,255,255, 0));
			DrawUtil.drawRoundedRectangle(getX() + Key.S.getX(), getY() + Key.S.getY(), Key.S.getWidth(), Key.S.getHeight(), 6, Key.S.isDown() ? new Color(0,0,0, 30) : new Color(255,255,255, 0));
			DrawUtil.drawRoundedRectangle(getX() + Key.D.getX(), getY() + Key.D.getY(), Key.D.getWidth(), Key.D.getHeight(), 6, Key.D.isDown() ? new Color(0,0,0, 30) : new Color(255,255,255, 0));
			DrawUtil.drawRoundedRectangle(getX() + Key.Jump1.getX(), getY() + Key.Jump1.getY(), Key.Jump1.getWidth(), Key.Jump1.getHeight(), 6, Key.Jump1.isDown() ? new Color(0,0,0, 30) : new Color(255,255,255, 0));
			DrawUtil.drawRoundedRectangle(getX() + Key.Jump1.getX() + 8, getY() + Key.Jump1.getY() + 8.5f, Key.Jump1.getWidth() - 16, Key.Jump1.getHeight() - 17, 0, new Color(255, 255, 255));
			for(Key key : mode.WASD_JUMP.getKeys()) {
				int textWidth = (int) fr.getStringWidth(key.getName());
				fr.drawString(key.getName(), getX() + key.getX() + key.getWidth() / 2 - textWidth / 2, getY() + key.getY() + key.getHeight() / 2 - 4, -1);
			}
		}
		GL11.glPopMatrix();
		
		super.onRenderDummy();
	}


}