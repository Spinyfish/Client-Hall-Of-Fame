package de.fanta.notifications;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import de.fanta.Client;
import de.fanta.utils.TimeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

public class Notification extends Gui {

	private boolean showing = false, started = false;
	private String name, description;

	private long wait = 2;
	private boolean doneWaiting = false;

	private Notification.NotificationType type;

	private float xFade = 0, startFade = 0;

	private int maxWidth = 150;

	public Notification(String name, String description, long wait, Notification.NotificationType type) {
		this.name = name;
		this.description = description;
		this.type = type;

		int nameWidth = Client.INSTANCE.arial2.getStringWidth(name);
		int descWidth = Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(description);
		if (nameWidth > 170 || descWidth > 170) {
			if (nameWidth > descWidth) {
				maxWidth = nameWidth + 20;
			} else {
				maxWidth = descWidth + 20;
			}
		}

		this.xFade = 5 - maxWidth - 10;
		this.startFade = 5 - maxWidth - 10;
	}

	private TimeUtil timer = new TimeUtil();

	public void draw() {
		if (isShowing()) {
			if (!doneWaiting && xFade < 11) {
				xFade++;
				timer.reset();
			} else if (doneWaiting && xFade > startFade) {
				xFade--;
			} else {
				if (doneWaiting && xFade < (startFade + 1)) {
					setShowing(false);
				}
			}
			ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
			int width = maxWidth, height = 50;

			int bgColor = Integer.MIN_VALUE;

			{// rect code
				switch (type) {
				case INFO:
					bgColor = new Color(20, 20, 40, 150).getRGB();
					break;
				case WARNING:
					bgColor = new Color(145, 75, 20, 150).getRGB();
					break;
				case ERROR:
					bgColor = new Color(75, 20, 20, 150).getRGB();
					break;
				default:
					bgColor = new Color(20, 20, 20, 150).getRGB();
					break;
				}
				Client.blurHelper2.blur2(sr.getScaledWidth() - xFade - width, sr.getScaledHeight()  - height,
						sr.getScaledWidth() - xFade, sr.getScaledHeight() - 20, 30);
				drawRect(sr.getScaledWidth() - xFade - width, sr.getScaledHeight()  - height,
						sr.getScaledWidth() - xFade, sr.getScaledHeight() - 20,  new Color(30,30,30,200).getRGB());
				

				//drawRect(sr.getScaledWidth() - xFade - width, sr.getScaledHeight() - height  + 29.2F,
				//	sr.getScaledWidth() - xFade, sr.getScaledHeight() - 20, Color.red.getRGB());
			}
			{// text
				GlStateManager.resetColor();
				GL11.glColor4f(1.0F, 1.0f, 1.0f, 1.0f);
				GlStateManager.pushMatrix();
				Client.INSTANCE.fluxTabGuiFont.drawString(name, sr.getScaledWidth() - xFade - width + 5,
						sr.getScaledHeight()  - height + 2, Color.white.getRGB());
				Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(description, sr.getScaledWidth() - xFade - width + 5,
						sr.getScaledHeight() - 5 - height + 21, Color.white.getRGB());
				GlStateManager.popMatrix();
				GlStateManager.resetColor();
				GL11.glColor4f(1.0F, 1.0f, 1.0f, 1.0f);
			}
			if (timer.hasReached(wait)) {
				doneWaiting = true;
			}
		}
	}

	/*
	 * Getters and Setters
	 */
	public boolean isShowing() {
		return showing;
	}

	public void setShowing(boolean showing) {
		this.showing = showing;
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Notification.NotificationType getType() {
		return type;
	}

	public enum NotificationType {
		INFO, ERROR, WARNING, NONE;
	}

}
