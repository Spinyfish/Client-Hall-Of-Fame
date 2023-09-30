package felix.gui.click.components;

import java.awt.Color;
import java.util.ArrayList;

import org.lwjgl.input.Mouse;

import java.awt.Font;

import felix.Client;
import felix.gui.click.Panel;
import felix.gui.click.components.listeners.ValueListener;
import felix.gui.click.util.ClickUtil;
import felix.gui.click.util.MathUtil;
import felix.management.FontManager;
import felix.util.font.FontRenderer;
import felix.util.visual.RenderUtil;
import net.minecraft.client.gui.VanillaFontRenderer;
import net.minecraft.util.MathHelper;

/**
 * @author sendQueue <Vinii>
 *
 *         Further info at Vinii.de or github@vinii.de, file created at
 *         11.11.2020. Use is only authorized if given credit!
 * 
 */
public class GuiSlider implements GuiComponent {
	private static int dragId = -1;
	private float round;
	private int id, width, posX, posY;
	private float min, max, current;
	private double c;

	private boolean wasSliding, hovered;

	private String text;
	
	private double animated;

	private ArrayList<ValueListener> valueListeners = new ArrayList<ValueListener>();

	public GuiSlider(String text, float d, float e, float f, float g) {
		this.text = text;
		this.min = d;
		this.max = e;
		this.current = f;
		c = f / e;
		this.round = g;
		this.id = Panel.compID += 1;
	}

	public void addValueListener(ValueListener vallistener) {
		valueListeners.add(vallistener);
	}

	@Override
	public void render(int posX, int posY, int width, int mouseX, int mouseY, int wheelY) {
		this.posX = posX;
		this.posY = posY;
		this.width = width;
		hovered = ClickUtil.isHovered(posX, posY, width, getHeight(), mouseX, mouseY);
		if (hovered && wheelY != 0) {
			double diff = min < 0 ? Math.abs(min - max) : max - min;
			double w = wheelY / 15;
			// 100 => per scroll 1/100 of the diff is being added/subtracted
			w *= diff / 100;
			if (round == 0) {
				current = (float) MathHelper.clamp_double(current + w, min, max);
			} else {
				current = (float) MathHelper.clamp_double(current + w, min, max);
			}
		}
		if (Mouse.isButtonDown(0) && (dragId == id || dragId == -1) && hovered) {
			if (mouseX < posX + 2) {
				current = min;
			} else if (mouseX > posX + width) {
				current = max;
			} else {
				double diff = max - min;
				double val = min + (MathHelper.clamp_double((mouseX - posX + 3) / (double) width, 0, 1)) * diff;
				if (round == 0) {
					current = (int) val;
				} else {
					current = (float) val;
				}
			}
			dragId = id;
			for (ValueListener listener : valueListeners) {
				listener.valueUpdated(current);
			}
			wasSliding = true;

		} else if (!Mouse.isButtonDown(0) && wasSliding) {
			for (ValueListener listener : valueListeners) {
				listener.valueChanged(current);
			}
			dragId = -1;
			wasSliding = false;
		}
		double percent = (current - min) / (max - min);
		renderGUI(percent);
	}

	private void renderGUI(double percent) {
		String value;
		if (round == 0) {
			value = "" + Math.round(current);
		} else {
			value = "" + MathUtil.round(current, (int) round);
		}

		final Color color = new Color(Panel.color);

		Panel.fR.drawStringWithShadow(text + ":", posX + 3, posY, Panel.fontColor);
		
		final FontRenderer fr = Client.INSTANCE.getFontManager().getFont("Display 16", true);
		int d = value.length() > 1 ? 15 : 15;
		fr.drawStringWithShadow(value, posX + width - Panel.fR.getWidth(value), posY + 2, Panel.fontColor);
		
		RenderUtil.drawRect(posX + 1, posY + Panel.fR.FONT_HEIGHT + 1, width - 2, 2, Panel.black195);
		
		animated = Mouse.isButtonDown(0) && hovered ? RenderUtil.animate((float) (percent * width - 2), animated, 0.1) : (float) (percent * width - 2);
		
		RenderUtil.drawRect(posX + 1, posY + Panel.fR.FONT_HEIGHT + 1, animated, 2, Panel.color);
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
	}

	@Override
	public void keyTyped(int keyCode, char typedChar) {
	}

	@Override
	public int getWidth() {
		return 101;
	}

	@Override
	public int getHeight() {
		return Panel.fR.FONT_HEIGHT + 6;
	}

	@Override
	public boolean allowScroll() {
		return false;
	}

}
