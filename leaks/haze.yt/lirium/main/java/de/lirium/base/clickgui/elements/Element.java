package de.lirium.base.clickgui.elements;

import de.lirium.base.clickgui.ClickScreen;

import java.util.ArrayList;

public abstract class Element<T> {
	protected final ArrayList<Element<?>> elements = new ArrayList<>();
	protected ClickScreen screen;
	protected int x, y, width, height;
	protected T object;
	protected Element<?> parent;

	public Element(final ClickScreen screen, final Element<?> parent, final T object, final int x, final int y, final int width, final int height) {
		this.screen = screen;
		this.parent = parent;
		this.object = object;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public abstract void render(int mouseX, int mouseY, float partialTicks);

	public abstract void mousePressed(int mouseX, int mouseY, int button);

	public abstract void mouseReleased(int mouseX, int mouseY, int button);

	public abstract void keyPressed(char typedChar, int keyCode);

	public void handleMouseInput() {}

	public boolean isVisible() {
		return true;
	}

	public boolean isHovered(double mouseX, double mouseY) {
		return isHovered(mouseX, mouseY, x, y, width, height);
	}

	public boolean isHovered(double mouseX, double mouseY, int x, int y, int width, int height) {
		return isVisible() && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}

	public T getObject() {
		return object;
	}

	public Element<?> getParent() {
		return parent;
	}

	public ClickScreen getScreen() {
		return screen;
	}

	public ArrayList<Element<?>> getElements() {
		return elements;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void move(int x, int y) {
		this.x += x;
		this.y += y;
	}

	public void set(int x, int y) {
		this.x = x;
		this.y = y;
	}
}