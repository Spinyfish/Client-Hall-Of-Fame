package de.fanta.module.impl.visual;

import java.awt.Color;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventRender2D;
import de.fanta.module.Module;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.CheckBox;
import de.fanta.setting.settings.ColorValue;
import de.fanta.setting.settings.Slider;
import de.fanta.utils.RenderUtil;
import de.fanta.utils.RotationUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class Radar extends Module {
	public Radar() {
		super("Radar", 0, Type.Visual, new Color(108, 2, 139));
		this.settings.add(new Setting("Outline", new CheckBox(true)));
		this.settings.add(new Setting("Blur", new CheckBox(false)));
		this.settings.add(new Setting("BlurAlpha", new Slider(0, 255, 1, 10)));
		this.settings.add(new Setting("Size", new Slider(0, 255, 1, 70)));
		this.settings.add(new Setting("Alpha", new Slider(0, 255, 1, 255)));
		this.settings.add(new Setting("Color", new ColorValue(Color.RED.getRGB())));
	}

	public static int mouseX;
	public static int mouseY;
	public static int lastMouseX;
	public static int lastMouseY;
	public static boolean dragging;
	public static Setting setting;
	public static int posX = 13;
	public static int posY = 34;
	public static double width = 70;
	public static double height = 70;
	public static double size;
	public static double alphaaa;
	public static double blurAlpha;

	@Override
	public void onEvent(Event event) {
		blurAlpha = ((Slider) this.getSetting("BlurAlpha").getSetting()).curValue;
		size = ((Slider) this.getSetting("Size").getSetting()).curValue;
		if (event instanceof EventRender2D && event.isPre()
				&& !Client.INSTANCE.moduleManager.getModule("TabGui").isState()) {
			int index1 = 0;
			mouseX = mouseX - lastMouseX;
			mouseY = mouseY - lastMouseY;
			int posX = Radar.posX + mouseX;
			int posY = Radar.posY + mouseY;
			if (((CheckBox) this.getSetting("Blur").getSetting()).state) {
				Client.blurHelper.blur2(posX, posY - 8, (float) (posX + size), (float) (posY + size - 10),
						(float) blurAlpha);
			}
			alphaaa = ((Slider) this.getSetting("Alpha").getSetting()).curValue;
			Gui.drawRect2(posX, posY - 8, posX + size, posY + size - 10, new Color(30, 30, 30, (int) alphaaa).getRGB());
			if (((CheckBox) this.getSetting("Outline").getSetting()).state) {
				Gui.drawRect2(posX, posY - 8, posX + size + 1, posY + size - 77, getColor2());
				Gui.drawRect2(posX, posY + 60, posX + size + 1, posY + size - 11, getColor2());
				Gui.drawRect2(posX, posY - 8, posX + size - 69, posY + size - 11, getColor2());
				Gui.drawRect2(posX + 70, posY - 8, posX + size + 1, posY + size - 11, getColor2());
			}
			index1++;
			double halfWidth = size / 2 + 0.5;
			double halfHeight = size / 2 - 10.5;
			GuiIngame.drawRect(posX + halfWidth, posY + halfHeight, posX + halfWidth + size / 70,
					posY + halfHeight + size / 70, getColor2());
			for (EntityPlayer player : mc.theWorld.playerEntities) {
				if (player != mc.thePlayer) {
					double playerX = player.posX;
					double playerZ = player.posZ;
					double diffX = playerX - mc.thePlayer.posX;
					double diffZ = playerZ - mc.thePlayer.posZ;
					if (MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ) < 50) {
						double clampedX = MathHelper.clamp_double(diffX, -halfWidth + 3, halfWidth - 3);
						double clampedY = MathHelper.clamp_double(diffZ, -halfHeight + 5, halfHeight - 3);
						GuiIngame.drawRect(posX + halfWidth + clampedX, posY + halfHeight + clampedY,
								posX + halfWidth + clampedX + size / 70, posY + halfHeight + clampedY + size / 70,
								Color.pink.getRGB());
					}
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

	public static float getAngle(Entity entity) {
		double x = RenderUtil.interpolate(entity.posX, entity.lastTickPosX, 1.0)
				- RenderUtil.interpolate(mc.thePlayer.posX, mc.thePlayer.lastTickPosX, 1.0);
		double z = RenderUtil.interpolate(entity.posZ, entity.lastTickPosZ, 1.0)
				- RenderUtil.interpolate(mc.thePlayer.posZ, mc.thePlayer.lastTickPosZ, 1.0);
		float yaw = (float) (-(Math.toDegrees(Math.atan2(x, z))));
		return (float) (yaw - RenderUtil.interpolate((double) mc.thePlayer.rotationYaw,
				(double) mc.thePlayer.prevRotationYaw, 1.0));
	}
}