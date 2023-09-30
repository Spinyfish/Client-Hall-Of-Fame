/**
 * 
 */
package felix.gui.click.components;

import java.awt.Color;
import java.util.ArrayList;

import felix.gui.click.Panel;
import felix.gui.click.components.listeners.ComboListener;
import felix.gui.click.util.ClickUtil;
import felix.value.impl.EnumValue;

public class GuiComboBox implements GuiComponent {
	private EnumValue setting;
	private boolean extended;
	private int height, posX, posY, width;

	private ArrayList<ComboListener> comboListeners = new ArrayList<ComboListener>();

	public GuiComboBox(EnumValue setting) {
		this.setting = setting;
	}

	public void addComboListener(ComboListener comboListener) {
		comboListeners.add(comboListener);
	}

	@Override
	public void render(int posX, int posY, int width, int mouseX, int mouseY, int wheelY) {
		this.posX = posX;
		this.posY = posY;
		this.width = width;
		renderGUI();
	}

	public void renderGUI() {
		if (extended) {
			Panel.fR.drawStringWithShadow(setting.getLabel() + "  -",
					posX + width / 2 - 47, posY + 2, Panel.fontColor);
			height = Panel.fR.FONT_HEIGHT + 4;

			int innerHeight = (Panel.fR.FONT_HEIGHT + 5);
			for (final Enum combo : setting.getConstants()) {
				if (setting.getValueAsString().equalsIgnoreCase(combo.name())) {
					Panel.fR.drawStringWithShadow(combo.name().toUpperCase(), posX + 4, posY + innerHeight, Panel.color);
				} else {
					Panel.fR.drawStringWithShadow(combo.name().toUpperCase(), posX + 4, posY + innerHeight, Panel.fontColor);
				}
				innerHeight += (Panel.fR.FONT_HEIGHT + 2);
			}
			height = innerHeight + 2;
		} else {
			Panel.fR.drawStringWithShadow(setting.getLabel() + "  +",
					posX + width / 2 - 47, posY + 2, Panel.fontColor);
			height = Panel.fR.FONT_HEIGHT + 4;
		}
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (ClickUtil.isHovered(posX, posY, width, Panel.fR.FONT_HEIGHT +2, mouseX, mouseY)) {
			extended = !extended;
		}
		if (extended) {
			if (ClickUtil.isHovered(posX, posY + Panel.fR.FONT_HEIGHT + 2, width,
					(Panel.fR.FONT_HEIGHT + 2) * setting.getConstants().length, mouseX, mouseY) && mouseButton == 0) {
				int h = Panel.fR.FONT_HEIGHT + 2;
				for (int i = 1; i <= setting.getConstants().length + 1; i++) {
					if (ClickUtil.isHovered(posX, posY + h * i, width, h * i, mouseX, mouseY)) {
						setting.setValue(setting.getConstants()[i - 1]);
					}
				}
				for (ComboListener comboListener : comboListeners) {
					comboListener.comboChanged(setting.getValueAsString());
				}
			}
		}
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
		return height;
	}
	
	@Override
	public boolean allowScroll() {
		return false;
	}

	public EnumValue getSetting() {
		return setting;
	}

	public void setSetting(EnumValue setting) {
		this.setting = setting;
	}

}
