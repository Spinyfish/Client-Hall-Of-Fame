package intentions.util;

import intentions.modules.Module.Category;

public class CategoryObject {

	private Category c;
	private String cname;
	private int x, y;
	
	public CategoryObject(Category c, int x, int y) {
		this.c = c;
		this.cname = c.name;
		this.x = x;
		this.y = y;
	}
	
	public Category getCategory() {
		return c;
	}
	public String getCategoryName() {
		return cname;
	}
	public int[] getCoordinates() {
		return new int[] {x, y};
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	
}
