package de.lirium.base.clickgui;

import de.lirium.base.clickgui.elements.Element;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.ArrayList;

public abstract class ClickScreen extends GuiScreen {
	protected final ArrayList<Element<?>> elements = new ArrayList<>();

	public ClickScreen() {
		this.onCreation();
	}

	/**
	 * This method will be called on the creation of the click gui
	 */
	protected abstract void onCreation();

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		try {
			super.drawScreen(mouseX, mouseY, partialTicks);
			for (final Element<?> element : elements) {
				if (!element.isVisible()) continue;
				element.render(mouseX, mouseY, partialTicks);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void keyTyped(char typedChar, int keyCode) throws IOException {
		try {
			for (final Element<?> element : elements) {
				if (!element.isVisible()) continue;
				element.keyPressed(typedChar, keyCode);
			}
			super.keyTyped(typedChar, keyCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		try {
			for (final Element<?> element : elements) {
				if (!element.isVisible()) continue;
				element.mousePressed(mouseX, mouseY, mouseButton);
			}
			super.mouseClicked(mouseX, mouseY, mouseButton);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int button) {
		try {
			for (final Element<?> element : elements) {
				if (!element.isVisible()) continue;
				element.mouseReleased(mouseX, mouseY, button);
			}
			super.mouseReleased(mouseX, mouseY, button);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void moveElements(int x, int y) {
		try {
			for (final Element<?> element : elements) element.move(x, y);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setElements(int x, int y) {
		try {
			for (final Element<?> element : elements) element.set(x, y);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Element<?>> getElements() {
		return elements;
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		for (final Element<?> element : elements)
			element.handleMouseInput();
	}
}