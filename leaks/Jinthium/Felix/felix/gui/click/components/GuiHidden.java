package felix.gui.click.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import felix.gui.click.Panel;
import felix.gui.click.util.ClickUtil;
import felix.util.visual.RenderUtil;

/**
 * @author sendQueue <Vinii>
 *
 *         Further info at Vinii.de or github@vinii.de, file created at
 *         11.11.2020. Use is only authorized if given credit!
 * 
 */
public class GuiHidden implements GuiComponent {
	private String text;
	
	private boolean hidden;

	private int posX, posY;

	private ArrayList<ActionListener> clickListeners = new ArrayList<ActionListener>();

	public GuiHidden(String text) {
		this.text = text;
	}

	@Override
	public void render(int posX, int posY, int width, int mouseX, int mouseY, int wheelY) {
		Panel.fR.drawStringWithShadow(text, posX + width / 2 - Panel.fR.getWidth(text) / 2, posY + 2, Panel.fontColor);
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		final int width = (int) (Panel.fR.getWidth(text) + 10);
		if (ClickUtil.isHovered(posX, posY + 3, width, getHeight(), mouseX, mouseY)) {
			hidden = !hidden;
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
		return 101;
	}
	
	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}


	@Override
	public int getHeight() {
		return Panel.fR.FONT_HEIGHT + 2;
	}
	
	@Override
	public boolean allowScroll() {
			return false;
	}

	public void addClickListener(ActionListener actionlistener) {
		clickListeners.add(actionlistener);
	}
}
