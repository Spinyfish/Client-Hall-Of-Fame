package de.fanta.module.impl.visual;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventKey;
import de.fanta.events.listeners.EventRender2D;
import de.fanta.events.listeners.EventTick;
import de.fanta.events.listeners.EventKey.PressType;
import de.fanta.module.Module;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.CheckBox;
import de.fanta.setting.settings.ColorValue;
import de.fanta.setting.settings.DropdownBox;
import de.fanta.utils.BlurHelper;
import de.fanta.utils.ChatUtil;
import de.fanta.utils.RenderUtil;
import de.fanta.utils.TimeUtil;
import de.hero.example.GUI;
import fr.lavache.anime.Animate;
import fr.lavache.anime.Easing;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

public class Tabgui extends Module {

	private int index, indexModules;
	private boolean opened;
	private final Animate animate = new Animate();
	private final Animate animate2 = new Animate();
	private int last = 0;
	private boolean reversed = false;
	private boolean reversed2 = false;

	public Tabgui() {
		super("Tabgui", 0, Type.Visual, Color.red);
		this.settings.add(new Setting("HoloPicture", new CheckBox(false)));
		this.settings.add(
				new Setting("Mode", new DropdownBox("Flux", new String[] { "Flux", "Rounded", "Violence", "Hero", "Holo","Jello","ZeroDay" })));
		this.settings.add(new Setting("Color", new ColorValue(Color.RED.getRGB())));
	}

	private TimeUtil moveDelayTimer = new TimeUtil();
	private int min = -15, max = 115;

	private int maxModules;

	@Override
	public void onEvent(Event event) {
		if (getMode().equals("Flux")) {
			List<Module> modules = new ArrayList<>();
			for (Module module : Client.INSTANCE.moduleManager.modules) {
				if (module.type == Type.values()[index])
					modules.add(module);
			}
			if (!opened)
				indexModules = 0;

			maxModules = modules.size();

			if (this.reversed) {
				if (this.animate.getValue() == 0) {
					this.opened = false;
					this.animate.reset();
				}
			}

			if (event instanceof EventRender2D && event.isPre()) {
				ScaledResolution sr = new ScaledResolution(mc);

				Gui gui = new Gui();
				float x = 5, y = 20;
				float height = Type.values().length * 15, width = 60;
				
				gui.drawRect(x, y, x + width, y + height - 1, new Color(50, 50, 50, 50).getRGB());
				Client.blurHelper.blur2(x, y, x + width, y + height, 1);
				Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow("Fanta", 3, 1, new Color(60, 50, 255).getRGB());
				for (int yT = 0; yT < Type.values().length; yT++) {
					Client.INSTANCE.fluxTabGuiFont.drawString(Type.values()[yT].name(), x + 5, y + (yT * 15),
							index == yT ? Color.white.getRGB() : Color.lightGray.getRGB());
				}

				animate.setEase(Easing.SINE_IN_OUT).setMin(0).setMax(width).setSpeed(220).setReversed(this.reversed)
						.update();
				animate2.setEase(Easing.SINE_IN_OUT).setMin(0).setMax(width).setSpeed(220).setReversed(this.reversed)
						.update();

				gui.drawRect(x + 1, y + 2 + (index * 15), x + 2.5F, y + 13 + (index * 15), getColor2());
				if (opened) {

					float moduleHeight = modules.size() * 15;

					gui.drawRect(x + width + 5, y + (index * 15), x + 5 + width + animate.getValue(),
							y + (index * 15) + moduleHeight - 1, new Color(50, 50, 50, 50).getRGB());
					Client.blurHelper2.blur2(x + width + 5, y + (index * 15), x + 5 + width + animate.getValue(),
							y + (index * 15) + moduleHeight - 1, 30);

					for (int i = 0; i < modules.size(); i++) {
						GL11.glEnable(GL11.GL_SCISSOR_TEST);
						// final ScaledResolution sr = new ScaledResolution(mc);
						GL11.glScissor((int) ((x + width + 5) * sr.getScaleFactor()),
								(int) (((sr.getScaledHeight() - y - (index * 15) - (i * 15)) - moduleHeight)
										* sr.getScaleFactor()),
								(int) ((animate.getValue()) * sr.getScaleFactor()),
								(int) (moduleHeight * sr.getScaleFactor()));

						Client.INSTANCE.fluxTabGuiFont.drawString(modules.get(i).getName(), x + width + 10,
								y + (index * 15) + (i * 15),
								(indexModules == i || modules.get(i).isState()) ? Color.white.getRGB()
										: Color.lightGray.getRGB());
						GL11.glDisable(GL11.GL_SCISSOR_TEST);
					}
					gui.drawRect(x + width + 5 + 1, y + (index * 15) + 2 + ((indexModules) * 15), x + width + 5 + 2.5F,
							y + (index * 15) + 13 + ((indexModules) * 15), getColor2());
				}
			}
			if (event instanceof EventTick) {
				if (!opened) {
					if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) && index < Type.values().length - 1
							&& moveDelayTimer.hasReached(120)) {
						index++;
						moveDelayTimer.reset();
					}
					if (Keyboard.isKeyDown(Keyboard.KEY_UP) && index > 0 && moveDelayTimer.hasReached(120)) {
						index--;
						moveDelayTimer.reset();
					}
				} else {
					if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) && indexModules < maxModules - 1
							&& moveDelayTimer.hasReached(120)) {
						indexModules++;
						moveDelayTimer.reset();
					}
					if (Keyboard.isKeyDown(Keyboard.KEY_UP) && indexModules > 0 && moveDelayTimer.hasReached(120)) {
						indexModules--;
						moveDelayTimer.reset();
					}
				}

				if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
					if (opened && moveDelayTimer.hasReached(200)) {
						modules.get(indexModules).setState(!modules.get(indexModules).isState());
						moveDelayTimer.reset();
					} else {
						moveDelayTimer.reset();
						this.reversed = false;
						opened = true;
					}
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
					this.reversed = true;
				}
			}
		} else if (getMode().equals("Rounded")) {
			List<Module> modules = new ArrayList<>();
			for (Module module : Client.INSTANCE.moduleManager.modules) {
				if (module.type == Type.values()[index])
					modules.add(module);
			}
			if (!opened)
				indexModules = 0;

			maxModules = modules.size();

			if (this.reversed) {
				if (this.animate.getValue() == 0) {
					this.opened = false;
					this.animate.reset();
				}
			}

			if (event instanceof EventRender2D && event.isPre()) {
				ScaledResolution sr = new ScaledResolution(mc);

				Gui gui = new Gui();
				float x = 5, y = 20;
				float height = Type.values().length * 15, width = 90;
				Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow("Fanta", 3, 1, this.getColor2());

				RenderUtil.drawRoundedRect2(x, y, width - 35, height - 15, 6, new Color(50, 50, 50, 200));

				for (int yT = 0; yT < Type.values().length; yT++) {
					Client.INSTANCE.fluxTabGuiFont.drawString(Type.values()[yT].name(), x + 5, y + (yT * 12),
							index == yT ? getColor2() : Color.lightGray.getRGB());
				}

				animate.setEase(Easing.SINE_IN_OUT).setMin(0).setMax(width).setSpeed(220).setReversed(this.reversed)
						.update();
				animate2.setEase(Easing.SINE_IN_OUT).setMin(0).setMax(width).setSpeed(220).setReversed(this.reversed)
						.update();

				if (opened) {
					float moduleHeight = modules.size() * 15;
					RenderUtil.drawRoundedRect2(x + width - 30, y + (index * 15), x + 35 + modules.size() + 18,
							modules.size() * 15 - 1, 5, new Color(50, 50, 50, 200));
					for (int i = 0; i < modules.size(); i++) {
						GL11.glEnable(GL11.GL_SCISSOR_TEST);
						GL11.glScissor((int) ((x + width - 25) * sr.getScaleFactor()),
								(int) (((sr.getScaledHeight() - y - (index * 15) - (i * 15)) - moduleHeight)
										* sr.getScaleFactor()),
								(int) ((animate.getValue()) * sr.getScaleFactor()),
								(int) (moduleHeight * sr.getScaleFactor()));

						Client.INSTANCE.fluxTabGuiFont.drawString(modules.get(i).getName(), x + width - 25,
								y + (index * 15) + (i * 15),
								(indexModules == i || modules.get(i).isState()) ? Color.white.getRGB()
										: Color.lightGray.getRGB());
						GL11.glDisable(GL11.GL_SCISSOR_TEST);
					}
					Client.INSTANCE.fluxTabGuiFont.drawString(".", x + width - 28 + 1,
							y + (index * 15) - 2 + ((indexModules) * 15), getColor2());
				}
			}
			if (event instanceof EventTick) {
				if (!opened) {
					if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) && index < Type.values().length - 1
							&& moveDelayTimer.hasReached(120)) {
						index++;
						moveDelayTimer.reset();
					}
					if (Keyboard.isKeyDown(Keyboard.KEY_UP) && index > 0 && moveDelayTimer.hasReached(120)) {
						index--;
						moveDelayTimer.reset();
					}
				} else {
					if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) && indexModules < maxModules - 1
							&& moveDelayTimer.hasReached(120)) {
						indexModules++;
						moveDelayTimer.reset();
					}
					if (Keyboard.isKeyDown(Keyboard.KEY_UP) && indexModules > 0 && moveDelayTimer.hasReached(120)) {
						indexModules--;
						moveDelayTimer.reset();
					}
				}

				if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
					if (opened && moveDelayTimer.hasReached(200)) {
						modules.get(indexModules).setState(!modules.get(indexModules).isState());
						moveDelayTimer.reset();
					} else {
						moveDelayTimer.reset();
						this.reversed = false;
						opened = true;
					}
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
					this.reversed = true;
				}
			}
			
			
		} else if (getMode().equals("Holo")) {
			
			List<Module> modules = new ArrayList<>();
			for (Module module : Client.INSTANCE.moduleManager.modules) {
				if (module.type == Type.values()[index])
					modules.add(module);
			}
			if (!opened)
				indexModules = 0;

			maxModules = modules.size();

			if (this.reversed) {
				if (this.animate.getValue() == 0) {
					this.opened = false;
					this.animate.reset();
				}
			}

			if (event instanceof EventRender2D && event.isPre()) {
				
				
				
				
				ScaledResolution sr = new ScaledResolution(mc);

				Gui gui = new Gui();
				
				float x = 5, y = 20;
				float height = Type.values().length * 15, width = 60;
				
				gui.drawRect(x , y +15, x + width, y + height + 15, new Color(255, 255, 255, 30).getRGB());
				Client.blurHelper.blur2(x, y + 14.5F, x + width, y + height + 17, 1);
				gui.drawRect(x + width, y  + (index * 15) + (15), x, y + 15 + (index * 15)+ (15), new Color(20,20,20,70).getRGB());
				//Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow("Fanta", 3, 1, new Color(60, 50, 255).getRGB());
				for (int yT = 0; yT < Type.values().length; yT++) {
				
				
					
					Client.INSTANCE.fluxTabGuiFont.drawString(Type.values()[yT].name(), x + 5, y + (yT * 15) + (17),
							index == yT ? Color.white.getRGB() : Color.lightGray.getRGB());
				}

				animate.setEase(Easing.SINE_IN_OUT).setMin(0).setMax(width).setSpeed(220).setReversed(this.reversed)
						.update();
				animate2.setEase(Easing.SINE_IN_OUT).setMin(0).setMax(width).setSpeed(220).setReversed(this.reversed)
						.update();
				
				int Fade = 0;
				gui.drawRect(x , y +15, x + 1, y + height + 15, Themes.getGradientOffset(Color.blue, Color.cyan, Fade / 100)
						.getRGB());
				Fade++;
				if (opened) {

					float moduleHeight = modules.size() * 15;

					gui.drawRect(x + width + 5, y + (index * 15) + (15), x + 5 + width + animate.getValue(),
							y + (index * 15) + (15) + moduleHeight - 1, new Color(50, 50, 50, 50).getRGB());
					Client.blurHelper2.blur2(x + width + 5, y + (index * 15) + (15), x + 5 + width + animate.getValue(),
							y + (index * 15) + (15)+ moduleHeight - 1, 30);
				
					for (int i = 0; i < modules.size(); i++) {
						GL11.glEnable(GL11.GL_SCISSOR_TEST);
						// final ScaledResolution sr = new ScaledResolution(mc);
						GL11.glScissor((int) ((x + width + 5) * sr.getScaleFactor()),
								(int) (((sr.getScaledHeight() - y - (index * 15) - (i * 15)) - moduleHeight)
										* sr.getScaleFactor()),
								(int) ((animate.getValue()) * sr.getScaleFactor()),
								(int) (moduleHeight * sr.getScaleFactor()));
						gui.drawRect(x + width + 5, y + (index * 15) - (2)+ (15)+ 2 + ((indexModules) * 15), x + width+ width + 5F,
								y + (index * 15) +(2)+ (15)+ 13 + ((indexModules) * 15), new Color(20,20,20,30).getRGB());
						Client.INSTANCE.fluxTabGuiFont.drawString(modules.get(i).getName(), x + width + 10,
								y + (index * 15) + (17) + (i * 15),
								(indexModules == i || modules.get(i).isState()) ? Color.white.getRGB()
										: Color.lightGray.getRGB());
						GL11.glDisable(GL11.GL_SCISSOR_TEST);
					}
					
				}
				if (((CheckBox) this.getSetting("HoloPicture").getSetting()).state) {
				drawImage(ScaledResolution.INSTANCE.getScaledWidth() / 140, 10, 60, 20, new ResourceLocation("Fanta/gui/Holo2.png"));
				}else {
					Client.INSTANCE.fluxTabGuiFont2.drawString("Fanta", 3, 0, -1);
				}

			}
			if (event instanceof EventTick) {
				if (!opened) {
					if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) && index < Type.values().length - 1
							&& moveDelayTimer.hasReached(120)) {
						index++;
						moveDelayTimer.reset();
					}
					if (Keyboard.isKeyDown(Keyboard.KEY_UP) && index > 0 && moveDelayTimer.hasReached(120)) {
						index--;
						moveDelayTimer.reset();
					}
				} else {
					if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) && indexModules < maxModules - 1
							&& moveDelayTimer.hasReached(120)) {
						indexModules++;
						moveDelayTimer.reset();
					}
					if (Keyboard.isKeyDown(Keyboard.KEY_UP) && indexModules > 0 && moveDelayTimer.hasReached(120)) {
						indexModules--;
						moveDelayTimer.reset();
					}
				}

				if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
					if (opened && moveDelayTimer.hasReached(200)) {
						modules.get(indexModules).setState(!modules.get(indexModules).isState());
						moveDelayTimer.reset();
					} else {
						moveDelayTimer.reset();
						this.reversed = false;
						opened = true;
					}
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
					this.reversed = true;
				}
				
				
				
			}
			
			
			
			
			
			
			
			
	} else if (getMode().equals("Jello")) {
			
			List<Module> modules = new ArrayList<>();
			for (Module module : Client.INSTANCE.moduleManager.modules) {
				if (module.type == Type.values()[index])
					modules.add(module);
			}
			if (!opened)
				indexModules = 0;

			maxModules = modules.size();

			if (this.reversed) {
				if (this.animate.getValue() == 0) {
					this.opened = false;
					this.animate.reset();
				}
			}

			if (event instanceof EventRender2D && event.isPre()) {
				
				
				
				
				ScaledResolution sr = new ScaledResolution(mc);

				Gui gui = new Gui();
				
				float x = 5, y = 20;
				float height = Type.values().length * 15, width = 60;
				
				
				Client.blurHelper.blur2(x, y + 17F, x + width, y + height + 3, 1);
				gui.drawRect(x , y +17, x + width, y + height + 1, new Color(255, 255, 255, 27).getRGB());
				gui.drawRect(x + width, y  + (index * 12) + 17, x, y + 15 + (index * 12)+ 17, new Color(20,20,20,50).getRGB());
				Client.INSTANCE.Jello2.drawString("Sigma", 8, 8, new Color(255,255,255,190).getRGB());
				Client.INSTANCE.Jello3.drawString("Jello", 8, 27, new Color(255,255,255,190).getRGB());
				for (int yT = 0; yT < Type.values().length; yT++) {
				
				
					
					Client.INSTANCE.Jello.drawString(Type.values()[yT].name(), x + 5, y + (yT * 12) + (21),
							index == yT ? Color.white.getRGB() : Color.white.getRGB());
				}

				animate.setEase(Easing.SINE_IN_OUT).setMin(0).setMax(width).setSpeed(220).setReversed(this.reversed)
						.update();
				animate2.setEase(Easing.SINE_IN_OUT).setMin(0).setMax(width).setSpeed(220).setReversed(this.reversed)
						.update();
				
				int Fade = 0;
				//gui.drawRect(x , y +15, x + 1, y + height + 15, Themes.getGradientOffset(Color.blue, Color.cyan, Fade / 100)
					//	.getRGB());
				Fade++;
				if (opened) {

					float moduleHeight = modules.size() * 15;

					gui.drawRect(x + width + 5, y + (index * 15) + (15), x + 5 + width + animate.getValue(),
							y + (index * 15) + (15) + moduleHeight - 1, new Color(50, 50, 50, 50).getRGB());
					Client.blurHelper2.blur2(x + width + 5, y + (index * 15) + (15), x + 5 + width + animate.getValue(),
							y + (index * 15) + (15)+ moduleHeight - 1, 30);
				
					for (int i = 0; i < modules.size(); i++) {
						GL11.glEnable(GL11.GL_SCISSOR_TEST);
						// final ScaledResolution sr = new ScaledResolution(mc);
						GL11.glScissor((int) ((x + width + 5) * sr.getScaleFactor()),
								(int) (((sr.getScaledHeight() - y - (index * 15) - (i * 15)) - moduleHeight)
										* sr.getScaleFactor()),
								(int) ((animate.getValue()) * sr.getScaleFactor()),
								(int) (moduleHeight * sr.getScaleFactor()));
						gui.drawRect(x + width + 5, y + (index * 15) - (2)+ (15)+ 2 + ((indexModules) * 15), x + width+ width + 5F,
								y + (index * 15) +(2)+ (15)+ 13 + ((indexModules) * 15), new Color(20,20,20,30).getRGB());
						Client.INSTANCE.Jello.drawString(modules.get(i).getName(), x + width + 10,
								y + (index * 15) + (17) + (i * 15),
								(indexModules == i || modules.get(i).isState()) ? Color.white.getRGB()
										: Color.lightGray.getRGB());
						GL11.glDisable(GL11.GL_SCISSOR_TEST);
					}
					
				}
				

			}
			if (event instanceof EventTick) {
				if (!opened) {
					if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) && index < Type.values().length - 1
							&& moveDelayTimer.hasReached(120)) {
						index++;
						moveDelayTimer.reset();
					}
					if (Keyboard.isKeyDown(Keyboard.KEY_UP) && index > 0 && moveDelayTimer.hasReached(120)) {
						index--;
						moveDelayTimer.reset();
					}
				} else {
					if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) && indexModules < maxModules - 1
							&& moveDelayTimer.hasReached(120)) {
						indexModules++;
						moveDelayTimer.reset();
					}
					if (Keyboard.isKeyDown(Keyboard.KEY_UP) && indexModules > 0 && moveDelayTimer.hasReached(120)) {
						indexModules--;
						moveDelayTimer.reset();
					}
				}

				if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
					if (opened && moveDelayTimer.hasReached(200)) {
						modules.get(indexModules).setState(!modules.get(indexModules).isState());
						moveDelayTimer.reset();
					} else {
						moveDelayTimer.reset();
						this.reversed = false;
						opened = true;
					}
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
					this.reversed = true;
				}
				
				
				
			}
			
			
	} else if (getMode().equals("ZeroDay")) {
		
		List<Module> modules = new ArrayList<>();
		for (Module module : Client.INSTANCE.moduleManager.modules) {
			if (module.type == Type.values()[index])
				modules.add(module);
		}
		if (!opened)
			indexModules = 0;

		maxModules = modules.size();

		if (this.reversed) {
			if (this.animate.getValue() == 0) {
				this.opened = false;
				this.animate.reset();
			}
		}

		if (event instanceof EventRender2D && event.isPre()) {
			
			
			
			
			ScaledResolution sr = new ScaledResolution(mc);

			Gui gui = new Gui();
			
			float x = 5, y = 20;
			float height = Type.values().length * 15, width = 60;
			
			
			//Client.blurHelper.blur2(x, y + 17F, x + width, y + height + 3, 1);
			RenderUtil.drawRoundedRect2(x , y +15, x + width + 5, y + height- 28,3, new Color(0, 0, 0, 150));
			gui.drawRect(x + 1.5F, y  + (index * 12) + 20, x, y + 17 + (index * 12)+ 16, Color.green.getRGB());
		//	Client.INSTANCE.Jello2.drawString("Sigma", 8, 8, new Color(255,255,255,190).getRGB());
			//Client.INSTANCE.Jello3.drawString("Jello", 8, 27, new Color(255,255,255,190).getRGB());
			for (int yT = 0; yT < Type.values().length; yT++) {
			
			
				
				Client.INSTANCE.verdana2.drawStringWithShadow(Type.values()[yT].name(), x + 5, y + (yT * 12) + (21),
						index == yT ? Color.white.getRGB() : Color.white.getRGB());
			}

			animate.setEase(Easing.SINE_IN_OUT).setMin(0).setMax(width).setSpeed(220).setReversed(this.reversed)
					.update();
			animate2.setEase(Easing.SINE_IN_OUT).setMin(0).setMax(width).setSpeed(220).setReversed(this.reversed)
					.update();
			
			int Fade = 0;
			//gui.drawRect(x , y +15, x + 1, y + height + 15, Themes.getGradientOffset(Color.blue, Color.cyan, Fade / 100)
				//	.getRGB());
			Fade++;
			if (opened) {

				float moduleHeight = modules.size() * 15;

				gui.drawRect(x + width + 5, y + (index * 15) + (15), x + 5 + width + animate.getValue(),
						y + (index * 15) + (15) + moduleHeight - 1, new Color(50, 50, 50, 50).getRGB());
				Client.blurHelper2.blur2(x + width + 5, y + (index * 15) + (15), x + 5 + width + animate.getValue(),
						y + (index * 15) + (15)+ moduleHeight - 1, 30);
			
				for (int i = 0; i < modules.size(); i++) {
					GL11.glEnable(GL11.GL_SCISSOR_TEST);
					// final ScaledResolution sr = new ScaledResolution(mc);
					GL11.glScissor((int) ((x + width + 5) * sr.getScaleFactor()),
							(int) (((sr.getScaledHeight() - y - (index * 15) - (i * 15)) - moduleHeight)
									* sr.getScaleFactor()),
							(int) ((animate.getValue()) * sr.getScaleFactor()),
							(int) (moduleHeight * sr.getScaleFactor()));
					gui.drawRect(x + width + 5, y + (index * 15) - (2)+ (15)+ 2 + ((indexModules) * 15), x + width+ width + 5F,
							y + (index * 15) +(2)+ (15)+ 13 + ((indexModules) * 15), new Color(20,20,20,30).getRGB());
					Client.INSTANCE.Jello.drawString(modules.get(i).getName(), x + width + 10,
							y + (index * 15) + (17) + (i * 15),
							(indexModules == i || modules.get(i).isState()) ? Color.white.getRGB()
									: Color.lightGray.getRGB());
					GL11.glDisable(GL11.GL_SCISSOR_TEST);
				}
			}
			
			drawImage(ScaledResolution.INSTANCE.getScaledWidth() / 140 - 12, -3, 150, 47, new ResourceLocation("Fanta/gui/zeroday.png"));

		}
		if (event instanceof EventTick) {
			if (!opened) {
				if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) && index < Type.values().length - 1
						&& moveDelayTimer.hasReached(120)) {
					index++;
					moveDelayTimer.reset();
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_UP) && index > 0 && moveDelayTimer.hasReached(120)) {
					index--;
					moveDelayTimer.reset();
				}
			} else {
				if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) && indexModules < maxModules - 1
						&& moveDelayTimer.hasReached(120)) {
					indexModules++;
					moveDelayTimer.reset();
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_UP) && indexModules > 0 && moveDelayTimer.hasReached(120)) {
					indexModules--;
					moveDelayTimer.reset();
				}
			}

			if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
				if (opened && moveDelayTimer.hasReached(200)) {
					modules.get(indexModules).setState(!modules.get(indexModules).isState());
					moveDelayTimer.reset();
				} else {
					moveDelayTimer.reset();
					this.reversed = false;
					opened = true;
				}
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
				this.reversed = true;
			}
			
			
			
		}
			
			
			
			
			
			
			
			
		} else if (getMode().equals("Hero")) {
			List<Module> modules = new ArrayList<>();
			for (Module module : Client.INSTANCE.moduleManager.modules) {
				if (module.type == Type.values()[index])
					modules.add(module);
			}
			if (!opened)
				indexModules = 0;

			maxModules = modules.size();

			if (this.reversed) {
				if (this.animate.getValue() == 0) {
					this.opened = false;
					this.animate.reset();
				}
			}

			if (event instanceof EventRender2D && event.isPre()) {
				ScaledResolution sr = new ScaledResolution(mc);

				Gui gui = new Gui();
				float x = 5, y = 20;
				float height = Type.values().length * 15, width = 60;
				//Client.INSTANCE.unicodeBasicFontRenderer4.drawString("Hero", x +20, y, new Color(20,255,200,200).getRGB());
				gui.drawRect(x - 5, y-20, x + width- 10, y + height- 10, new Color(20, 20, 20, 180).getRGB());
			//	Client.blurHelper.blur2(x, y, x + width, y + height, 1);
				gui.drawRect(x + 50, y + (index * 13), x - 5, y + 14 + (index * 13),new Color(20,255,100, 200).getRGB());
				gui.drawRect(x + 50, y - 20, x + 51, y + 80,new Color(20,255,100, 200).getRGB());
				gui.drawRect(x -5, y+ 79, x + 51, y + 80,new Color(20,255,100, 200).getRGB());
				//gui.drawRect(x + 60, y + (index * 15), x + 61, y + 90 + (index * 15),new Color(20,255,100, 200).getRGB());
				for (int yT = 0; yT < Type.values().length; yT++) {
					Client.INSTANCE.heroTabGui.drawStringWithShadow("Hero", 1.5F, -3.5F, new Color(100,255,90,255).getRGB());

					Client.INSTANCE.unicodeBasicFontRenderer4.drawString(Type.values()[yT].name(), x - 3, y + (yT * 13),
							index == yT ? Color.white.getRGB() : Color.lightGray.getRGB());
					
				}

				animate.setEase(Easing.SINE_IN_OUT).setMin(0).setMax(width).setSpeed(220).setReversed(this.reversed)
						.update();
				animate2.setEase(Easing.SINE_IN_OUT).setMin(0).setMax(width).setSpeed(220).setReversed(this.reversed)
						.update();

				
				if (opened) {

					float moduleHeight = modules.size() * 15;

					gui.drawRect(x + width -8, y + (index * 15), x + 5 + width + animate.getValue(),
							y + (index * 15) + moduleHeight - 1, new Color(20, 20, 20, 170).getRGB());
//					Client.blurHelper2.blur2(x + width + 5, y + (index * 15), x + 5 + width + animate.getValue(),
//							y + (index * 15) + moduleHeight - 1, 30);

					for (int i = 0; i < modules.size(); i++) {
						GL11.glEnable(GL11.GL_SCISSOR_TEST);
						// final ScaledResolution sr = new ScaledResolution(mc);
						GL11.glScissor((int) ((x + width -8) * sr.getScaleFactor()),
								(int) (((sr.getScaledHeight() - y - (index * 15) - (i * 15)) - moduleHeight )
										* sr.getScaleFactor()),
								(int) ((animate.getValue()) * sr.getScaleFactor()+26),
								(int) (moduleHeight * sr.getScaleFactor()));
						gui.drawRect(x + width -8, y + (index * 15)  + ((indexModules) * 15), x + width + 62.5F + 2.5F,
								y + (index * 15) + 14 + ((indexModules) * 15),new Color(20,255,100, 200).getRGB());
						Client.INSTANCE.unicodeBasicFontRenderer4.drawString(modules.get(i).getName(), x + width -2 ,
								y + (index * 15) + (i * 15),
								(indexModules == i || modules.get(i).isState()) ? Color.white.getRGB()
										: Color.lightGray.getRGB());
						GL11.glDisable(GL11.GL_SCISSOR_TEST);
					}
					
				}
			}
			if (event instanceof EventTick) {
				if (!opened) {
					if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) && index < Type.values().length - 1
							&& moveDelayTimer.hasReached(120)) {
						index++;
						moveDelayTimer.reset();
					}
					if (Keyboard.isKeyDown(Keyboard.KEY_UP) && index > 0 && moveDelayTimer.hasReached(120)) {
						index--;
						moveDelayTimer.reset();
					}
				} else {
					if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) && indexModules < maxModules - 1
							&& moveDelayTimer.hasReached(120)) {
						indexModules++;
						moveDelayTimer.reset();
					}
					if (Keyboard.isKeyDown(Keyboard.KEY_UP) && indexModules > 0 && moveDelayTimer.hasReached(120)) {
						indexModules--;
						moveDelayTimer.reset();
					}
				}

				if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
					if (opened && moveDelayTimer.hasReached(200)) {
						modules.get(indexModules).setState(!modules.get(indexModules).isState());
						moveDelayTimer.reset();
					} else {
						moveDelayTimer.reset();
						this.reversed = false;
						opened = true;
					}
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
					this.reversed = true;
				}
			}
		} else if (getMode().equals("Violence")) {
			List<Module> modules = new ArrayList<>();
			for (Module module : Client.INSTANCE.moduleManager.modules) {
				if (module.type == Type.values()[index])
					modules.add(module);
			}
			if (!opened)
				indexModules = 0;

			maxModules = modules.size();

			if (this.reversed) {
				if (this.animate.getValue() == 0) {
					this.opened = false;
					this.animate.reset();
				}
			}

			if (event instanceof EventRender2D && event.isPre()) {
				ScaledResolution sr = new ScaledResolution(mc);

				Gui gui = new Gui();
				float x = 5, y = 20;
				float height = Type.values().length * 15, width = 60;
				gui.drawRect(x, y, x + width - 7, y + height - 17, Color.black.getRGB());
				// Client.blurHelper.blur2(x, y, x + width, y + height, 1);
				gui.drawRect(x + 52, (y - 1) + (index * 12) + 1, x, y + 13 + (index * 12) - 1,
						new Color(90, 100, 255).getRGB());
				Client.INSTANCE.Violence2.drawStringWithShadow("V", 3, 1, new Color(60, 50, 255).getRGB());
				Client.INSTANCE.Violence.drawString("IOLENCE", 11, 6, -1);
				for (int yT = 0; yT < Type.values().length; yT++) {
				
					Client.INSTANCE.ViolenceTabGUi.drawStringWithShadow(Type.values()[yT].name(),
							x + (width - 8) / 2
									- Client.INSTANCE.ViolenceTabGUi.getStringWidth(Type.values()[yT].name()) / 2,
							y + (yT * 12), index == yT ? Color.white.getRGB() : Color.lightGray.getRGB());
				}

				animate.setEase(Easing.SINE_IN_OUT).setMin(0).setMax(width).setSpeed(220).setReversed(this.reversed)
						.update();
				animate2.setEase(Easing.SINE_IN_OUT).setMin(0).setMax(width).setSpeed(220).setReversed(this.reversed)
						.update();

				if (opened) {

					float moduleHeight = modules.size() * 15;

					gui.drawRect(x + width - 5, y + (index * 15), x + width + animate.getValue(),
							y + (index * 15) + moduleHeight - 1, Color.black.getRGB());
					// Client.blurHelper2.blur2(x + width + 5, y + (index * 15), x + 5 + width +
					// animate.getValue(),
					// y + (index * 15) + moduleHeight - 1, 30);
					gui.drawRect(x + width - 6 + 1, y + (index * 15) + ((indexModules) * 15), x + width + 59,
							y + (index * 15) + 13 + ((indexModules) * 15), new Color(90, 100, 255).getRGB());
					for (int i = 0; i < modules.size(); i++) {
						GL11.glEnable(GL11.GL_SCISSOR_TEST);
						// final ScaledResolution sr = new ScaledResolution(mc);
						GL11.glScissor((int) ((x + width + 5) * sr.getScaleFactor()),
								(int) (((sr.getScaledHeight() - y - (index * 15) - (i * 15)) - moduleHeight)
										* sr.getScaleFactor()),
								(int) ((animate.getValue()) * sr.getScaleFactor()),
								(int) (moduleHeight * sr.getScaleFactor()));

						Client.INSTANCE.ViolenceTabGUi.drawStringWithShadow2(modules.get(i).getName(), x + width + 8,
								y + (index * 15) + (i * 15),
								(indexModules == i || modules.get(i).isState()) ? Color.orange.getRGB()
										: Color.lightGray.getRGB());
						GL11.glDisable(GL11.GL_SCISSOR_TEST);
					}

				}
			}
			if (event instanceof EventTick) {
				if (!opened) {
					if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) && index < Type.values().length - 1
							&& moveDelayTimer.hasReached(120)) {
						index++;
						moveDelayTimer.reset();
					}
					if (Keyboard.isKeyDown(Keyboard.KEY_UP) && index > 0 && moveDelayTimer.hasReached(120)) {
						index--;
						moveDelayTimer.reset();
					}
				} else {
					if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) && indexModules < maxModules - 1
							&& moveDelayTimer.hasReached(120)) {
						indexModules++;
						moveDelayTimer.reset();
					}
					if (Keyboard.isKeyDown(Keyboard.KEY_UP) && indexModules > 0 && moveDelayTimer.hasReached(120)) {
						indexModules--;
						moveDelayTimer.reset();
					}
				}

				if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
					if (opened && moveDelayTimer.hasReached(200)) {
						modules.get(indexModules).setState(!modules.get(indexModules).isState());
						moveDelayTimer.reset();
					} else {
						moveDelayTimer.reset();
						this.reversed = false;
						opened = true;
					}
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
					this.reversed = true;
				}
			}

		}

	}

	public int getColor2() {
		try {
			return ((ColorValue) getSetting("Color").getSetting()).color;
		} catch (Exception e) {
			return Color.white.getRGB();
		}
	}

	public String getMode() {
		try {
			return ((DropdownBox) getSetting("Mode").getSetting()).curOption;
		} catch (Exception e) {
			return "Flux";
		}
	}
	public static void drawImage(int x, int y, int width, int height, ResourceLocation resourceLocation) {
		mc.getTextureManager().bindTexture(resourceLocation);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
