package de.fanta.module.impl.visual;

import java.awt.Color;
import java.util.AbstractList;
import java.util.List;

import org.apache.logging.log4j.core.pattern.AbstractStyleNameConverter.Red;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.EventRender2D;
import de.fanta.module.Module;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.CheckBox;
import de.fanta.setting.settings.ColorValue;
import de.fanta.setting.settings.Slider;
import de.fanta.utils.ColorUtils;
import de.fanta.utils.Colors;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class InventoryRenderer extends Module {

	public InventoryRenderer() {
		super("InventoryHUD", 0, Type.Visual, ColorUtils.getRandomColor());
		this.settings.add(new Setting("Blur", new CheckBox(false)));
		this.settings.add(new Setting("Lines", new CheckBox(false)));
		this.settings.add(new Setting("Alpha", new Slider(1, 255, 0.1, 50)));
		this.settings.add(new Setting("Alpha2", new Slider(1, 255, 0.1, 50)));
		this.settings.add(new Setting("Color", new ColorValue(Color.RED.getRGB())));
	}

	public static double alpha;
	public static double alpha2;

	@Override
	public void onEvent(Event event) {
		alpha = ((Slider) this.getSetting("Alpha").getSetting()).curValue;
		alpha2 = ((Slider) this.getSetting("Alpha2").getSetting()).curValue;
		if (event instanceof EventRender2D) {
			EventRender2D e = (EventRender2D) event;
			int[] rgb = Colors.getRGB(getColor2());
			try {
				float x = 7.5F;
				float y = 20;
				if (Client.INSTANCE.moduleManager.getModule("Radar").isState()) {
					y = 100;
				}
				if (Client.INSTANCE.moduleManager.getModule("Tabgui").isState()) {
					y = 140;
				}
				if (!Client.INSTANCE.moduleManager.getModule("Tabgui").isState()) {
					x = 15;
				}
				List<List<ItemStack>> itms = Partition.ofSize(Arrays.asList(mc.thePlayer.inventory.mainInventory), 9);
				// InventoryBackground
				if (((CheckBox) this.getSetting("Blur").getSetting()).state) {

					if (!((CheckBox) this.getSetting("Lines").getSetting()).state) {
						Client.blurHelper.blur2(x - 2, y + 17 - 15, x + (9 * 17) + 2, y + 17, (float) 10);
						new Gui().drawRect(x - 2, y + 17 - 15, x + (9 * 17) + 2, y + 15,
								new Color(20, 20, 20, (int) alpha2).getRGB());
					} else {
						Client.blurHelper.blur2(x - 2, y + 17 - 15, x + (9 * 17) + 2, y + 15, (float) 10);
						new Gui().drawRect(x - 2, y + 17 - 15, x + (9 * 17) + 2, y + 15,
								new Color(20, 20, 20, (int) alpha2).getRGB());
					}

					// InventoryLine
					// Client.blurHelper.blur2(x-2, y+17-2.5F, x+(9*17)+2, y+15, (float) 10);
					if (((CheckBox) this.getSetting("Lines").getSetting()).state) {
						new Gui().drawRect(x - 2, y + 17 - 2.5F, x + (9 * 17) + 2, y + 15,
								Colors.getColor(rgb[0], rgb[1], rgb[2], (int) alpha));
					} else {

					}
					// String
					// Background
					Client.blurHelper.blur2(x - 2, y + 17 - 2, x + (9 * 17) + 2, y + (itms.size()) * 17 + 2,
							(float) 10);
					new Gui().drawRect(x - 2, y + 17 - 2, x + (9 * 17) + 2, y + (itms.size()) * 17 + 2,
							new Color(20, 20, 20, (int) alpha2).getRGB());
				} else {
					new Gui().drawRect(x - 2, y + 17 - 15, x + (9 * 17) + 2, y + 15, new Color(20, 20, 20).getRGB());
					// InventoryLine
					new Gui().drawRect(x - 2, y + 17 - 2.5F, x + (9 * 17) + 2, y + 15,
							Colors.getColor(rgb[0], rgb[1], rgb[2], (int) alpha));
					// String
					// new Gui().drawCenteredString(mc.fontRendererObj, "Inventory",
					// (int)x+(9*17)/2, (int)y+3, Color.white.getRGB());
					// Background
					new Gui().drawRect(x - 2, y + 17 - 2, x + (9 * 17) + 2, y + (itms.size()) * 17 + 2,
							new Color(20, 20, 20).getRGB());
				}
				Client.INSTANCE.unicodeBasicFontRenderer.drawCenteredString("Inventory", (int) x + (9 * 17) / 2,
						(int) y + 1, Color.white.getRGB());

				for (int i = 1; i < itms.size(); i++) {
					List<ItemStack> row = itms.get(i);
					if (((CheckBox) this.getSetting("Lines").getSetting()).state) {
						if (i > 1)
							new Gui().drawRect(x - 2, y + (17 * i) - .5F, x + (9 * 17) + 2, y + (17 * i),
									Colors.getColor(rgb[0], rgb[1], rgb[2], (int) alpha));
					} else {
						// if(i > 1) new Gui().drawRect(x-2, y+(17*i)-.5F, x+(9*17)+2, y+(17*i),
						// Colors.getColor(rgb[0], rgb[1], rgb[2], (int) alpha));
					}
					for (int j = 0; j < row.size(); j++) {
						if (row.get(j) == null)
							continue;
						ItemStack itm = row.get(j);
						RenderHelper.enableStandardItemLighting();
						mc.getRenderItem().renderItemAndEffectIntoGUI(itm, (int) x + (j * 17), (int) y + (i * 17));
					}
				}
			} catch (Exception e2) {
			}
		}
	}

	final static class Partition<T> extends AbstractList<List<T>> {

		private final List<T> list;
		private final int chunkSize;

		public Partition(List<T> list, int chunkSize) {
			this.list = (List<T>) new ArrayList();
			this.list.addAll(list);
			this.chunkSize = chunkSize;
		}

		public static <T> Partition<T> ofSize(List<T> list, int chunkSize) {
			return new Partition<T>(list, chunkSize);
		}

		@Override
		public List<T> get(int index) {
			int start = index * chunkSize;
			int end = Math.min(start + chunkSize, list.size());

			if (start > end) {
				throw new IndexOutOfBoundsException(
						"Index " + index + " is out of the list range <0," + (size() - 1) + ">");
			}
			ArrayList ret = new ArrayList();
			ret.addAll(list.subList(start, end));
			return ret;
		}

		@Override
		public int size() {
			return (int) Math.ceil((double) list.size() / (double) chunkSize);
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
