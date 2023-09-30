package de.verschwiegener.atero.util.files.config;

import java.awt.Color;
import java.util.ArrayList;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.module.Module;
import de.verschwiegener.atero.settings.Setting;
import de.verschwiegener.atero.settings.SettingsItem;
import de.verschwiegener.atero.util.files.config.handler.XMLHelper;

public class Config {

    private String name;
    private String description;
    private String recommendedServerIP;
    private ConfigType type;

    ArrayList<ConfigItem> items = new ArrayList<>();

    /**
     * Used for online Config loading
     * @param name
     * @param description
     * @param recommendedServerIP
     * @param items
     */
    public Config(String name, String description, String recommendedServerIP, ArrayList<ConfigItem> items, ConfigType type) {
	this.name = name;
	this.description = description;
	this.recommendedServerIP = recommendedServerIP;
	this.items = items;
	this.type = type;
    }
    /**
     * Used for config command
     * @param name
     * @param description
     * @param recommendedServerIP
     */
    public Config(String name, String description, String recommendedServerIP, ConfigType type) {
	this.name = name;
	this.description = description;
	this.recommendedServerIP = recommendedServerIP;
	this.type = type;
	createItems();
	XMLHelper.write(this);
    }
    public Config(String name, String description, String recommendedServerIP, ConfigType type, ArrayList<String> selectedModules) {
	this.name = name;
	this.description = description;
	this.recommendedServerIP = recommendedServerIP;
	this.type = type;
	createItems(selectedModules);
	XMLHelper.write(this);
    }
    /**
     * Used for config command
     * @param name
     */
    public Config(String name, ConfigType type) {
	this.name = name;
	this.description = "";
	this.recommendedServerIP = "";
	this.type = type;
	createItems();
	XMLHelper.write(this);
    }

    private void createItems() {
	for (Module m : Management.instance.modulemgr.modules) {
	    items.add(new ConfigItem(new String[] { "toggle", m.getName(), Boolean.toString(m.isEnabled()) }));
	    if (Management.instance.settingsmgr.getSettingByName(m.getName()) != null) {
		for (SettingsItem item : Management.instance.settingsmgr.getSettingByName(m.getName()).getItems()) {
		    switch (item.getCategory()) {
		    case CHECKBOX:
			items.add(new ConfigItem(
				new String[] { "set", m.getName(), item.getName(), Boolean.toString(item.isState()) }));
			break;
		    case COLOR_PICKER:
			Color color = item.getColor();
			items.add(new ConfigItem(new String[] { "set", m.getName(), item.getName(),
				Integer.toString(color.getRed()), Integer.toString(color.getGreen()),
				Integer.toString(color.getBlue()), Integer.toString(color.getAlpha()) }));
			break;
		    case COMBO_BOX:
			items.add(
				new ConfigItem(new String[] { "set", m.getName(), item.getName(), item.getCurrent() }));
			break;
		    case SLIDER:
			items.add(new ConfigItem(new String[] { "set", m.getName(), item.getName(),
				Float.toString(item.getCurrentValue()) }));
			break;
		    case TEXT_FIELD:
			items.add(
				new ConfigItem(new String[] { "set", m.getName(), item.getName(), item.getCurrent() }));
			break;
		    }
		}
	    }
	}
    }

    private void createItems(ArrayList<String> selectedModules) {
	for (Module m : Management.instance.modulemgr.modules) {
	    if (selectedModules.contains(m.getName())) {
		items.add(new ConfigItem(new String[] { "toggle", m.getName(), Boolean.toString(m.isEnabled()) }));
		if (Management.instance.settingsmgr.getSettingByName(m.getName()) != null) {
		    for (SettingsItem item : Management.instance.settingsmgr.getSettingByName(m.getName()).getItems()) {
			switch (item.getCategory()) {
			case CHECKBOX:
			    items.add(new ConfigItem(new String[] { "set", m.getName(), item.getName(),
				    Boolean.toString(item.isState()) }));
			    break;
			case COLOR_PICKER:
			    Color color = item.getColor();
			    items.add(new ConfigItem(new String[] { "set", m.getName(), item.getName(),
				    Integer.toString(color.getRed()), Integer.toString(color.getGreen()),
				    Integer.toString(color.getBlue()), Integer.toString(color.getAlpha()) }));
			    break;
			case COMBO_BOX:
			    items.add(new ConfigItem(
				    new String[] { "set", m.getName(), item.getName(), item.getCurrent() }));
			    break;
			case SLIDER:
			    items.add(new ConfigItem(new String[] { "set", m.getName(), item.getName(),
				    Float.toString(item.getCurrentValue()) }));
			    break;
			case TEXT_FIELD:
			    items.add(new ConfigItem(
				    new String[] { "set", m.getName(), item.getName(), item.getCurrent() }));
			    break;
			}
		    }
		}
	    }
	}
    }
    
    public void loadConfig() {
	for(ConfigItem item : items) {
	    item.execute();
	}
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getRecommendedServerIP() {
	return recommendedServerIP;
    }

    public void setRecommendedServerIP(String recommendedServerIP) {
	this.recommendedServerIP = recommendedServerIP;
    }

    public ArrayList<ConfigItem> getItems() {
	return items;
    }

    public void setItems(ArrayList<ConfigItem> items) {
	this.items = items;
    }
    public ConfigType getType() {
	return type;
    }

}
