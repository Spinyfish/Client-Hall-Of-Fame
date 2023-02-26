package ooo.cpacket.ruby.module.render;

import java.awt.Color;

import net.minecraft.client.gui.ScaledResolution;
import ooo.cpacket.ruby.ClientBase;
import ooo.cpacket.ruby.api.event.EventImpl;
import ooo.cpacket.ruby.api.event.IEvent;
import ooo.cpacket.ruby.api.event.events.render.EventGameRender;
import ooo.cpacket.ruby.module.Module;

public class Hud extends Module implements IEvent, Runnable {
	public Hud(String name, int key, Category category) {
		super(name, key, category);
		if (!this.isEnabled())
			this.toggle();
	}

	@Override
	public void run() {

	}

	@Override
	public void onEnable() {

	}

	@Override
	public void onDisable() {

	}

	/**
	 * Credits to pandora/nivia devs for this
	 * 
	 * @param speed
	 * @param offset
	 * @return
	 */
	public static int getRainbow(int speed, int offset) {
		float hue = (System.currentTimeMillis() + offset) % speed;
		hue /= speed;
		return Color.getHSBColor(hue, 0.7f, 0.7f).getRGB();
	}

	@EventImpl
	public void onUpdate(EventGameRender e) {
		if (mc.theWorld == null)
			return;
		if (mc.gameSettings.showDebugInfo)
			return;
		ScaledResolution var2 = e.sr;
		int y = 0;
		this.mc.fontRendererObj.drawStringWithShadow(
				"\u00A72" + ClientBase.INSTANCE.cinfo.getName() + " " + ClientBase.INSTANCE.cinfo.getBuild(), 2, 0, -1);
		ClientBase.INSTANCE.getModuleManager().sort();
		for (Module m : ClientBase.INSTANCE.getModuleManager().modules) {
			if (!m.isEnabled())
				continue;
			int location = (int) (var2.getScaledWidth() - mc.fontRendererObj.getStringWidth(m.getSuffixedName()) - 2);
			this.mc.fontRendererObj.drawStringWithShadow(m.getSuffixedName(), location, y,
					this.getRainbow(7000, y * -15));
			y += 10;
		}
	}
}
