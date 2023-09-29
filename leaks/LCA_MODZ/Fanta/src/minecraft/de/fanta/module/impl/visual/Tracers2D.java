package de.fanta.module.impl.visual;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventRender2D;
import de.fanta.events.listeners.EventRender3D;
import de.fanta.events.listeners.PlayerMoveEvent;
import de.fanta.module.Module;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.CheckBox;
import de.fanta.setting.settings.ColorValue;
import de.fanta.utils.Colors;
import de.fanta.utils.RenderUtil;
import de.fanta.utils.RotationUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;


public class Tracers2D extends Module {
	public static Tracers2D INSTANCE;
	public Tracers2D() {
		super("2D-Tracers", 0, Type.Visual, Color.magenta);
		this.settings.add(new Setting("Color", new ColorValue(Color.RED.getRGB())));
		  INSTANCE = this;
	}

	@Override
	public void onEvent(Event event) {
		if (event instanceof EventRender2D) {
			int x = Display.getWidth() / 2 / Math.max(mc.gameSettings.guiScale, 1);
			int y = Display.getHeight() / 2 / Math.max(mc.gameSettings.guiScale, 1);
			
			List<EntityPlayer> playerList = mc.theWorld.playerEntities;
			playerList.removeIf(entity -> entity == mc.thePlayer || entity.isInvisible());
			
			int[] rgb = Colors.getRGB(getColor2());
			for (EntityPlayer player : playerList) {
				int alpha = (int) (255 - Math.min(mc.thePlayer.getDistanceToEntity(player), 255));
				float angle = RotationUtil.getAngle(player) % 360 + 180;
				GlStateManager.pushMatrix();
				GlStateManager.translate(x, y, 0);
				GlStateManager.rotate(angle, 0, 0, 1);
				GlStateManager.translate(-x, -y, 0);
				RenderUtil.drawTriangleFilled(x, y + 50, 5, 9, Colors.getColor(rgb[0], rgb[1], rgb[2], alpha));
				GlStateManager.translate(x, y, 0);
				GlStateManager.rotate(-angle, 0, 0, 1);
				GlStateManager.translate(-x, -y, 0);
				GlStateManager.popMatrix();
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
}
