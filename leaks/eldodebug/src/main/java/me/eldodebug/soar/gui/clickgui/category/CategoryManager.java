package me.eldodebug.soar.gui.clickgui.category;

import java.util.ArrayList;

import me.eldodebug.soar.gui.clickgui.category.impl.ConfigCategory;
import me.eldodebug.soar.gui.clickgui.category.impl.CosmeticCategory;
import me.eldodebug.soar.gui.clickgui.category.impl.EditHUDCategory;
import me.eldodebug.soar.gui.clickgui.category.impl.FeatureCategory;
import me.eldodebug.soar.gui.clickgui.category.impl.MusicPlayerCategory;
import me.eldodebug.soar.gui.clickgui.category.impl.SettingsCategory;

public class CategoryManager {

	private ArrayList<Category> categories = new ArrayList<Category>();

	public CategoryManager() {
		categories.add(new FeatureCategory());
		categories.add(new ConfigCategory());
		categories.add(new CosmeticCategory());
		categories.add(new EditHUDCategory());
		categories.add(new MusicPlayerCategory());
		categories.add(new SettingsCategory());
	}
	
	public ArrayList<Category> getCategories() {
		return categories;
	}
	
	public Category getCategoryByName(String name) {
		return categories.stream().filter(category -> category.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	public Category getCategoryByClass(Class<?> categoryClass) {
		return categories.stream().filter(category -> category.getClass().equals(categoryClass)).findFirst().orElse(null);
	}
}
