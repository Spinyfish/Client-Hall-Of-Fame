package felix.gui.click.components;

import felix.util.visual.RenderUtil;

public class ColorIndicator {
	
	private float x, y;
	
	public ColorIndicator(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void draw() {
		RenderUtil.drawCircle(x, y, 30, 30);
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setX(float x) {
		this.x = x;
	}
	
	public void setY(float y) {
		this.y = y;
	}
}
