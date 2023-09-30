package felix.gui.click.components;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import com.mojang.realmsclient.gui.ChatFormatting;

import felix.gui.click.Panel;
import felix.gui.click.util.ClickUtil;
import felix.util.visual.RenderUtil;
import felix.util.visual.Translate;

/**
 * @author sendQueue <Vinii>
 *
 *         Further info at Vinii.de or github@vinii.de, file created at 11.11.2020. 
 *         Use is only authorized if given credit!
 * 
 */
public class GuiToggleButton implements GuiComponent {

	private String text;
	private boolean toggled;

	private int posX, posY;

	private ArrayList<ActionListener> clickListeners = new ArrayList<ActionListener>();

	/**
	 * 
	 */
	public GuiToggleButton(String text) {
		this.text = text;
	}

	@Override
	public void render(int posX, int posY, int width, int mouseX, int mouseY, int wheelY) {
		this.posX = posX;
		this.posY = posY;
		renderGUI(posX, posY);
	}

	/**
	 * Renders toggleButton for theme Caesium
	 */
	private void renderGUI(int posX, int posY) {
		Panel.fR.drawStringWithShadow(text, posX + 25 - 3, posY + 3, Panel.fontColor);
		if (toggled) {
			RenderUtil.drawRoundedRect(posX + 7 - 4, posY + 3, 16, 9, 8, Panel.color);
			
			RenderUtil.drawRoundedRect(posX + 10, posY + 3, 9, 9, 8, Panel.fontColor);
		} else {
			RenderUtil.drawRoundedRect(posX + 7 - 4, posY + 3, 16, 9, 8, Color.gray.getRGB());
			
			RenderUtil.drawRoundedRect(posX + 3, posY + 3, 9, 9, 8, Panel.fontColor);
		}
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		final int width = (int) (Panel.fR.getWidth(text) + 10);
		if (ClickUtil.isHovered(posX, posY + 3, width, getHeight(), mouseX, mouseY)) {
			toggled = !toggled;
			for (ActionListener listener : clickListeners) {
				listener.actionPerformed(new ActionEvent(this, hashCode(), "click", System.currentTimeMillis(), 0));
			}
		}

	}

	@Override
	public void keyTyped(int keyCode, char typedChar) {

	}

	@Override
	public int getWidth() {
		return (int) (Panel.fR.getWidth(text) + 20);
	}

	@Override
	public int getHeight() {
		return Panel.fR.FONT_HEIGHT + 5;
	}
	@Override
	public boolean allowScroll() {
		return false;
	}

	public boolean isToggled() {
		return toggled;
	}

	public void setToggled(boolean toggled) {
		this.toggled = toggled;
	}

	public void addClickListener(ActionListener actionlistener) {
		clickListeners.add(actionlistener);
	}
}
