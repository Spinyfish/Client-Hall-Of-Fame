package felix.gui.click.components;

import java.awt.Color;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import felix.Client;
import felix.api.annotations.Handler;
import felix.events.player.EventKeyPress;
import felix.gui.click.Panel;
import felix.gui.click.components.listeners.KeyListener;
import felix.gui.click.util.ClickUtil;
import felix.util.visual.RenderUtil;

/**
 * @author sendQueue <Vinii>
 *
 *         Further info at Vinii.de or github@vinii.de, file created at
 *         11.11.2020. Use is only authorized if given credit!
 * 
 */
public class GuiGetKey implements GuiComponent {
	private boolean wasChanged, allowChange;
	private int key, posX, posY, width;
	private String text;

	private ArrayList<KeyListener> keylisteners = new ArrayList<KeyListener>();

	public GuiGetKey(String text, int key) {
		this.text = text;
		this.key = key;
		if (key < 0)
			this.key = 0;
	}

	public void addKeyListener(KeyListener listener) {
		keylisteners.add(listener);
	}

	@Override
	public void render(int posX, int posY, int width, int mouseX, int mouseY, int wheelY) {
		this.posX = posX;
		this.posY = posY;
		this.width = width;
		renderGUI(posX, posY);
	}

	private void renderGUI(int posX, int posY) {
		String keyString = Keyboard.getKeyName(key);
		if (allowChange) {
			wasChanged = false;
		}else {
			wasChanged = true;
		}
		//RenderUtil.drawVerticalGradient(posX, posY, width, 14, RenderUtil.darker(new Color(Panel.color), 0.7f).getRGB(), RenderUtil.brighter(new Color(Panel.color), 0.7f).getRGB());
		Panel.fR.drawStringWithShadow(text + ":", posX + 3, posY + 2, Panel.fontColor);
		if (wasChanged) {
			final String sr = keyString.equalsIgnoreCase("escape") ? "NONE" : keyString;
			Panel.fR.drawStringWithShadow(sr, posX + width - Panel.fR.getWidth(sr) - 3, posY + 1, Panel.fontColor);
		} else {
			Panel.fR.drawString("Press any key...", posX + width / 2 - 3, posY + 1, Panel.fontColor);
		}
		
		RenderUtil.drawRect(posX, posY + 12, width, 0.8f, Panel.black195);
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		final String keyString = Keyboard.getKeyName(key);
		final int w = (int) Panel.fR.getWidth(text);
		if (ClickUtil.isHovered(posX, posY, width, 11, mouseX, mouseY)) {
			allowChange = true;
		} else {
			allowChange = false;
		}

	}

	@Override
	public void keyTyped(int keyCode, char typedChar) {
		if (allowChange) {
			for (KeyListener listener : keylisteners) {
				listener.keyChanged(keyCode);
				if (keyCode == Keyboard.KEY_ESCAPE) {
					listener.keyChanged(Keyboard.KEY_NONE);
				}
			}
			
			key = keyCode == Keyboard.KEY_ESCAPE ? Keyboard.KEY_NONE : keyCode;
			allowChange = false;
		}
	}

	@Override
	public int getWidth() {
		return 101;
	}

	@Override
	public int getHeight() {
		return Panel.fR.FONT_HEIGHT + 4;
	}
	
	@Override
		public boolean allowScroll() {
			// TODO Auto-generated method stub
			return false;
		}

}
